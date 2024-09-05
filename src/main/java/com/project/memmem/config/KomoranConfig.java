package com.project.memmem.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class KomoranConfig {

	// 사용자 정의 사전 파일 이름
	private static final String DICTIONARY_FILE = "user.dic";

	@Bean // Komoran 객체를 Spring Bean으로 정의
	Komoran komoran() throws IOException {
		// 사용자 정의 사전 파일을 생성
		createDictionaryDIC(); // 명사 사전 생성

		// Komoran 인스턴스를 생성하고, 기본 모델을 FULL로 설정
		Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);

		// 사용자 정의 사전 파일 경로를 Komoran에 설정
		komoran.setUserDic(DICTIONARY_FILE); // 통합된 사전 파일 설정

		return komoran; // Komoran 객체를 반환
	}

	// 사용자 정의 사전 파일을 생성하거나 업데이트하는 메서드
	private void createDictionaryDIC() throws IOException {
		// 사용자 정의 사전 파일 경로를 지정
		File file = new File(DICTIONARY_FILE);

		// 파일이 존재하지 않으면 새로 생성
		if (!file.exists())
			file.createNewFile();

		// 명사 집합을 저장할 Set
		Set<String> nounSet = new HashSet<>();

		// 기존 명사 데이터를 읽어와 Set에 추가
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String data;
			while ((data = br.readLine()) != null) {
				if (data.startsWith("#"))
					continue; // 주석 라인은 무시

				String[] parts = data.split("\\t");
				if (parts.length >= 2) {
					String word = parts[0];
					String pos = parts[1];

					if ("NNP".equals(pos)) {
						nounSet.add(word);
					}
				}
			}
		}

		// 사용자 정의 사전에 명사 추가
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			nounSet.forEach(noun -> {
				try {
					bw.write(noun + "\tNNP\n"); // NNP는 명사 고유명사
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
}