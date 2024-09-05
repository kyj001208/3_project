package com.project.memmem.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaV3Service {

    @Value("${google.recaptcha.key.secret}")
    private String secretKey;

    @Value("${google.recaptcha.threshold:0.5}")
    private float threshold;

    private final RestTemplate restTemplate;

    public RecaptchaV3Service(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean verifyRecaptcha(String token) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        String params = "?secret=" + secretKey + "&response=" + token;

        ResponseEntity<Map> response = restTemplate.postForEntity(url + params, null, Map.class);
        Map<String, Object> body = response.getBody();

        if (body == null || !Boolean.TRUE.equals(body.get("success"))) {
            return false;
        }

        float score = ((Number) body.get("score")).floatValue();
        return score >= threshold;
    }
}
