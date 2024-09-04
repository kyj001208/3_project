package com.project.memmem.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class ReCaptchaService {

    @Value("${google.recaptcha.key.secret}")
    private String secretKey;

    public boolean verifyReCaptcha(String gRecaptchaResponse) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", secretKey);
        requestMap.add("response", gRecaptchaResponse);

        ReCaptchaResponse response = restTemplate.postForObject(url, requestMap, ReCaptchaResponse.class);
        
        return response != null && response.isSuccess();
    }
}

class ReCaptchaResponse {
    private boolean success;
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }// 추가적인 필드도 필요하면 getter와 setter를 추가
}
