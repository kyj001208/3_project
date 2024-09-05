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
public class ChatbotServiceProcess {

	private final KomoranServiceProcess komoranService;
	private final KeywordRepository keywordRepository;
	private final AnswerRepository answerRepository;
	private final ScenarioRepository scenarioRepository;
	private final WeatherServiceProcess weatherService;
	private final LocationServiceProcess locationService;

	// 현재 시나리오와 날씨 조회를 위한 지역 정보를 저장하는 변수
	private ScenarioEntity currentScenario;
	private String pendingWeatherLocation;

	@Transactional
	public AnswerDTO processUserQuestion(QuestionDTO questionDTO) {
		
		// 사용자의 질문 내용을 자연어 처리(NLP)하여 분석
		MessageDTO analysisResult = komoranService.nlpAnalyze(questionDTO.getContent());
		Set<String> nouns = analysisResult.getNouns();
		
		// 질문 내용에 "날씨", "기온", "습도"가 포함되어 있거나, 날씨 관련 대화 단계가 진행 중인 경우
		if (nouns.contains("날씨") || nouns.contains("기온") || nouns.contains("습도") || questionDTO.getWeatherStep() > 0) {
			return handleWeatherQuery(questionDTO);
		}
		
		// 시나리오가 진행 중이거나, 사용자가 소모임 추천을 요청한 경우
		if (questionDTO.isInScenario() || "소모임 추천해주세요!".equals(questionDTO.getContent())) {
			return processScenario(questionDTO.getContent());
		}
		// NLP 분석 결과로 얻은 명사를 바탕으로 의도를 추출
		Set<NNPIntentionEntity> nnpIntentions = findNNPIntention(nouns);
		Optional<AnswerEntity> answerEntityOptional = Optional.empty();
		
		// 의도가 발견되지 않은 경우, 기본 응답을 찾음
		if (nnpIntentions.isEmpty()) {
			answerEntityOptional = answerRepository.findByNnpIntention_NnpNo(0);
		} else {
			// 발견된 의도가 있을 경우, 해당 의도에 맞는 답변을 찾음
			for (NNPIntentionEntity nnpIntention : nnpIntentions) {
				if (answerEntityOptional.isEmpty()) {
					answerEntityOptional = answerRepository.findByNnpIntention_NnpNo(nnpIntention.getNnpNo());
				}
			}
		}
		// 적절한 답변이 발견된 경우, AnswerDTO로 변환하여 반환
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
			return AnswerDTO.builder().answer(weatherInfo.get("error")).nnpNo(0).build();
		}
		// 날씨 정보가 정상적으로 반환된 경우, 기온과 습도를 가져옵니다.
		String locationName = weatherInfo.get("location");
		String temperature = weatherInfo.get("temperature");
		String humidity = weatherInfo.get("humidity");
		// 위치와 함께 기온과 습도를 포함한 답변 문자열을 생성합니다.
		 String answer = String.format("%s의 현재 기온은 %s°C이고, 습도는 %s%%입니다.", locationName, temperature, humidity);
		// 생성된 답변을 포함한 AnswerDTO 객체를 반환합니다.
		return AnswerDTO.builder().answer(answer).nnpNo(0).build();
	}

	// 시나리오 처리를 위한 메소드
	private AnswerDTO processScenario(String userInput) {
		// currentScenario가 null인 경우, 첫 번째 시나리오 단계(root)를 찾음
		if (currentScenario == null) {
			// 부모 시나리오가 없는 (root) 시나리오를 dept 0으로 검색
			Optional<ScenarioEntity> rootScenario = scenarioRepository.findByDeptAndParentIsNull(0);
			// root 시나리오가 존재하면 현재 시나리오로 설정하고 다음 단계로 이동
			if (rootScenario.isPresent()) {
				currentScenario = rootScenario.get();
				return getNextScenarioStep(currentScenario);
			}
		} else {
			// currentScenario가 존재하는 경우, 사용자의 입력을 바탕으로 다음 시나리오를 찾음
			Optional<ScenarioEntity> nextScenario = findNextScenario(currentScenario, userInput);
			if (nextScenario.isPresent()) {
				currentScenario = nextScenario.get();
				return getNextScenarioStep(currentScenario);
			} else {
				// 다음 시나리오를 찾지 못한 경우, 현재 시나리오의 카테고리 URL을 반환
				return getNextScenarioStep(currentScenario);
			}
		}
		// 시나리오를 찾지 못하거나 입력을 이해하지 못한 경우 초기 상태로 돌아가도록 설정
		currentScenario = null;
		return AnswerDTO.builder().answer("죄송합니다. 해당 내용을 이해하지 못했습니다. 처음 질문으로 돌아가겠습니다.").endScenario(true).build();
	}

	// 주어진 현재 시나리오와 사용자 입력을 바탕으로 다음 시나리오를 찾는 메서드
	private Optional<ScenarioEntity> findNextScenario(ScenarioEntity currentScenario, String userInput) {
		return scenarioRepository.findByParentAndContentContaining(currentScenario, userInput);
	}

	// 현재 시나리오 단계의 정보를 바탕으로 다음 단계의 답변을 생성하는 메서드
	private AnswerDTO getNextScenarioStep(ScenarioEntity scenario) {
		// 현재 시나리오의 자식 시나리오들을 검색하고, 내용만 리스트로 추출
		List<ScenarioEntity> children = scenarioRepository.findByParentOrderByDept(scenario);
		List<String> options = children.stream().map(ScenarioEntity::getContent).toList();

		AnswerDTO answerDTO = AnswerDTO.builder().answer(scenario.getContent()).options(options).build();

		// 마지막 단계 (리프 노드)인 경우 카테고리 URL 추가
		if (children.isEmpty() && scenario.getCategory() != null) {
			answerDTO.setCategoryUrl("/group-list?category=" + scenario.getCategory());
			answerDTO.setEndScenario(true);
		}

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
	@Transactional
	public void resetScenario() {
		currentScenario = null;
		pendingWeatherLocation = null;
	}

	// 현재 시나리오가 진행 중인지 확인하는 메소드
	public boolean isInScenario() {
		return currentScenario != null;
	}
}