package com.project.memmem.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.memmem.domain.dto.bot.MessageDTO;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KomoranServiceProcess {
	private final Komoran komoran; // Komoran 형태소 분석기

	public MessageDTO nlpAnalyze(String message) {
		// Komoran을 사용하여 메시지 분석
		KomoranResult result = komoran.analyze(message);
		result.getTokenList().forEach(token -> {
			System.out.format("(%2d, %2d) %s/%s\n", token.getBeginIndex(), token.getEndIndex(), token.getMorph(),
					token.getPos());
		});

		// 메시지에서 고유 명사(NNP)들을 추출한 후 중복 제거해서 Set에 저장
		Set<String> properNouns = result.getTokenList().stream().filter(token -> "NNP".equals(token.getPos()))
				.map(token -> token.getMorph()).collect(Collectors.toSet());
		properNouns.forEach(noun -> {
			System.out.println("고유 명사: " + noun);
		});

		// 명사 집합을 기반으로 응답 메시지 생성
		return analyzeToken(properNouns);
	}

	private MessageDTO analyzeToken(Set<String> properNouns) {
		// 분석된 고유 명사를 기반으로 응답 메시지를 생성하는 로직
		String analyzedContent = "다음과 같은 고유 명사를 분석했습니다: " + String.join(", ", properNouns);

		// 메시지와 고유 명사를 반환하는 메시지 DTO 생성
		MessageDTO messageDTO = MessageDTO.builder().content(analyzedContent).nouns(properNouns).build();

		// 추가적인 로직이 필요하다면 여기에 추가
		return messageDTO;
	}
}