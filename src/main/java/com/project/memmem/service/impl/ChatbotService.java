package com.project.memmem.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.memmem.domain.dto.bot.AnswerDTO;
import com.project.memmem.domain.dto.bot.MessageDTO;
import com.project.memmem.domain.dto.bot.QuestionDTO;
import com.project.memmem.domain.entity.AnswerEntity;
import com.project.memmem.domain.entity.KeywordEntity;
import com.project.memmem.domain.entity.NNPIntentionEntity;
import com.project.memmem.domain.entity.ScenarioEntity;
import com.project.memmem.domain.repository.AnswerRepository;
import com.project.memmem.domain.repository.KeywordRepository;
import com.project.memmem.domain.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatbotService {

	private final KomoranService komoranService;
	private final KeywordRepository keywordRepository;
	private final AnswerRepository answerRepository;
	private final ScenarioRepository scenarioRepository;
	private final WeatherService weatherService;
	private final LocationService locationService;

	// 현재 시나리오와 날씨 조회를 위한 지역 정보를 저장하는 변수
	private ScenarioEntity currentScenario;
	private String pendingWeatherLocation;

	@Transactional
	public AnswerDTO processUserQuestion(QuestionDTO questionDTO) {
		// 질문 내용 분석
		MessageDTO analysisResult = komoranService.nlpAnalyze(questionDTO.getContent());
		Set<String> nouns = analysisResult.getNouns();

		// 날씨 관련 키워드 체크
		if (nouns.contains("날씨") || nouns.contains("기온") || nouns.contains("습도") || questionDTO.getWeatherStep() > 0) {
			return handleWeatherQuery(questionDTO);
		}

		// 시나리오 처리가 필요한 경우
		if (questionDTO.isInScenario() || "소모임 추천해주세요!".equals(questionDTO.getContent())) {
			return processScenario(questionDTO.getContent());
		}
		// 명사에서 의도 찾기
		Set<NNPIntentionEntity> nnpIntentions = findNNPIntention(nouns);
		Optional<AnswerEntity> answerEntityOptional = Optional.empty();
		// NNP 의도가 없으면 기본 답변을 찾음
		if (nnpIntentions.isEmpty()) {
			answerEntityOptional = answerRepository.findByNnpIntention_NnpNo(0);
		} else {
			// NNP 의도에 해당하는 답변 찾기
			for (NNPIntentionEntity nnpIntention : nnpIntentions) {
				if (answerEntityOptional.isEmpty()) {
					answerEntityOptional = answerRepository.findByNnpIntention_NnpNo(nnpIntention.getNnpNo());
				}
			}
		}
		// 답변 생성
		if (answerEntityOptional.isPresent()) {
			AnswerEntity answerEntity = answerEntityOptional.get();
			return AnswerDTO.builder().answer(answerEntity.getAnswer()).nnpNo(answerEntity.getNnpIntention().getNnpNo())
					.build();
		} else {
			return AnswerDTO.builder().answer("죄송합니다. 답변을 찾을 수 없습니다.").nnpNo(0).build();
		}
	}

	// 날씨 쿼리 처리 메소드
	private AnswerDTO handleWeatherQuery(QuestionDTO questionDTO) {
		// 날씨 단계에 따라 적절한 응답 생성
		if (questionDTO.getWeatherStep() == 0) {
			return AnswerDTO.builder().answer("어느 지역의 날씨를 알려드릴까요?").nnpNo(0).build();
		} else if (questionDTO.getWeatherStep() == 2) {
			return processWeatherQuery(questionDTO.getSelectedLocation());
		} else {
			String location = extractLocation(new HashSet<>(Arrays.asList(questionDTO.getContent().split("\\s+"))));
			return processWeatherQuery(location);
		}
	}

	// 명사에서 위치를 추출하는 메소드
	private String extractLocation(Set<String> nouns) {
		Optional<String> location = nouns.stream().filter(locationService::isValidLocation).findFirst();

		if (location.isPresent()) {
			return location.get();
		}

		for (String noun : nouns) {
			Optional<String> partialMatch = locationService.findMatchingLocation(noun);
			if (partialMatch.isPresent()) {
				return partialMatch.get();
			}
		}

		return null; // 유효한 위치를 찾지 못한 경우 null 반환
	}

	// 위치를 기반으로 날씨 정보를 처리하는 메소드
	private AnswerDTO processWeatherQuery(String location) {
		// 날씨 정보를 요청한 위치에 대한 날씨 데이터를 가져옵니다.
	    Map<String, String> weatherInfo = weatherService.getCurrentWeather(location);
	    
	    if (weatherInfo.containsKey("error")) {
	        return AnswerDTO.builder()
	                .answer(weatherInfo.get("error"))
	                .nnpNo(0)
	                .build();
	    }
	    // 날씨 정보가 정상적으로 반환된 경우, 기온과 습도를 가져옵니다.
	    String temperature = weatherInfo.get("temperature");
	    String humidity = weatherInfo.get("humidity");
	    // 위치와 함께 기온과 습도를 포함한 답변 문자열을 생성합니다.
	    String answer = String.format("%s의 현재 기온은 %s°C이고, 습도는 %s%%입니다.", 
	            location, temperature, humidity);
	    // 생성된 답변을 포함한 AnswerDTO 객체를 반환합니다.
	    return AnswerDTO.builder()
	            .answer(answer)
	            .nnpNo(0)
	            .build();
	}

	// 시나리오 처리를 위한 메소드
	private AnswerDTO processScenario(String userInput) {
		if (currentScenario == null) {
			// 시나리오 시작
			Optional<ScenarioEntity> rootScenario = scenarioRepository.findByDeptAndParentIsNull(0);
			if (rootScenario.isPresent()) {
				currentScenario = rootScenario.get();
				return getNextScenarioStep(currentScenario);
			}
		} else {
			// 다음 시나리오 찾기
			Optional<ScenarioEntity> nextScenario = findNextScenario(currentScenario, userInput);
			if (nextScenario.isPresent()) {
				currentScenario = nextScenario.get();
				return getNextScenarioStep(currentScenario);
			}
		}
		// 시나리오를 찾지 못한 경우
		currentScenario = null; // 시나리오 초기화
		return AnswerDTO.builder().answer("죄송합니다. 해당 내용을 이해하지 못했습니다. 처음 질문으로 돌아가겠습니다.").endScenario(true).build();
	}

	// 현재 시나리오에서 다음 시나리오를 찾는 메소드
	private Optional<ScenarioEntity> findNextScenario(ScenarioEntity currentScenario, String userInput) {
		return scenarioRepository.findByParentAndContentContaining(currentScenario, userInput);
	}

	// 주어진 시나리오 단계의 다음 단계를 가져오는 메소드
	private AnswerDTO getNextScenarioStep(ScenarioEntity scenario) {
		List<ScenarioEntity> children = scenarioRepository.findByParentOrderByDept(scenario);
		List<String> options = children.stream().map(ScenarioEntity::getContent).toList();

		AnswerDTO answerDTO = new AnswerDTO();
		answerDTO.setAnswer(scenario.getContent());
		answerDTO.setOptions(options);
		return answerDTO;
	}

	// 명사에서 NNP 의도를 찾는 메소드
	private Set<NNPIntentionEntity> findNNPIntention(Set<String> nouns) {
		Set<NNPIntentionEntity> nnpi = new HashSet<>();
		for (String noun : nouns) {
			Optional<KeywordEntity> keyword = keywordRepository.findByKeyword(noun);
			if (keyword.isPresent()) {
				nnpi.add(keyword.get().getNnpIntention());
			}
		}
		return nnpi;
	}

	// 현재 시나리오를 리셋하는 메소드
	public void resetScenario() {
		currentScenario = null;
	}

	// 현재 시나리오가 진행 중인지 확인하는 메소드
	public boolean isInScenario() {
		return currentScenario != null;
	}
}