package com.project.memmem.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.memmem.service.impl.RecaptchaV3Service;

@RestController
public class RecaptchaConfigController {
    
	@Autowired
    private RecaptchaV3Service reCaptchaService;

    @Value("${google.recaptcha.key.site}")
    private String recaptchaSiteKey;

    @GetMapping("/api/recaptcha-config")
    public Map<String, String> getRecaptchaConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("siteKey", recaptchaSiteKey);
        return config;
    }

    @PostMapping("/verify-recaptcha")
    public ResponseEntity<Map<String, Boolean>> verifyRecaptcha(@RequestBody Map<String, String> request) {
        String token = request.get("recaptchaToken");
        boolean isValid = reCaptchaService.verifyRecaptcha(token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", isValid);
        return ResponseEntity.ok(response);
    }
}
