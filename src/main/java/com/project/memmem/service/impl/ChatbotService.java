package com.project.memmem.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.memmem.domain.dto.bot.AnswerDTO;
import com.project.memmem.domain.dto.bot.MessageDTO;
import com.project.memmem.domain.dto.bot.QuestionDTO;
import com.project.memmem.domain.entity.AnswerEntity;
import com.project.memmem.domain.entity.KeywordEntity;
import com.project.memmem.domain.entity.NNPIntentionEntity;
import com.project.memmem.domain.repository.AnswerRepository;
import com.project.memmem.domain.repository.KeywordRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatbotService {
	
	private final KomoranService komoranService;
    private final KeywordRepository keywordRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public AnswerDTO processUserQuestion(QuestionDTO questionDTO) {
        // 코모란 서비스로 메시지 분석
        MessageDTO analysisResult = komoranService.nlpAnalyze(questionDTO.getContent());
        System.out.println("분석 결과: " + analysisResult);
        
        // 명사 집합 추출
        Set<String> nouns = analysisResult.getNouns();

        // 명사로부터 nnp_no 찾기
        Set<NNPIntentionEntity> nnpIntentions = findNNPIntention(nouns);

        Optional<AnswerEntity> answerEntityOptional = Optional.empty();

        if (nnpIntentions == null || nnpIntentions.isEmpty()) {
            int nnpNo = 0;  // nnpNo를 0으로 설정
            System.out.println("명사: " + nnpNo);

            // Optional로 감싸 답변 저장
            answerEntityOptional = answerRepository.findByNnpIntention_NnpNo(nnpNo);
        } else {
            for (NNPIntentionEntity nnpIntention : nnpIntentions) {
                if (answerEntityOptional.isEmpty()) { // 답변이 설정되지 않았다면
                    int nnpNo = nnpIntention.getNnpNo();  // nnpIntention에서 nnpNo를 가져옴
                    System.out.println("명사: " + nnpNo);

                    // Optional로 감싸 답변 저장
                    answerEntityOptional = answerRepository.findByNnpIntention_NnpNo(nnpNo);
                }
            }
        }
        
        // 최종적으로 찾은 답변을 DTO로 변환하여 반환
        if (answerEntityOptional.isPresent()) { // isPresent(): 값이 있는지 확인
            AnswerEntity answerEntity = answerEntityOptional.get();
            return AnswerDTO.builder()
                    .answer(answerEntity.getAnswer())
                    .nnpNo(answerEntity.getNnpIntention().getNnpNo())
                    .build();
        } else {
            // answerEntityOptional이 비어 있는 경우, 기본 메시지로 응답
            System.out.println("답변을 찾을 수 없습니다.");
            return AnswerDTO.builder()
                    .answer("죄송합니다. 답변을 찾을 수 없습니다.")
                    .nnpNo(0)
                    .build();
        }
    }

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
}