package com.project.memmem.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.project.memmem.service.HiddenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HiddenServiceProcess implements HiddenService{

	private final RestTemplate restTemplate;

    private final String GPT_API_URL = "https://api.openai.com/v1/chat/completions";  // GPT API URL

    @Value("${openai.api.key}")  // application.properties에서 API 키 주입
    private String apiKey;

    @Override
    public String getGptResponse(String question) {
        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 요청 본문 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        // "messages" 필드 설정
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", question);

        // messages 리스트 생성
        requestBody.put("messages", new Object[]{message});

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // GPT API로 요청 보내기
            ResponseEntity<String> response = restTemplate.exchange(GPT_API_URL, HttpMethod.POST, entity, String.class);
            
            // 응답 반환
            return response.getBody();
        } catch (RestClientException e) {
            e.printStackTrace();
            return "Error occurred while calling GPT API: " + e.getMessage();
        }
    }

}
