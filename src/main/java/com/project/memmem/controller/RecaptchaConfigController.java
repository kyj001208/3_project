package com.project.memmem.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<Map<String, Boolean>> verifyRecaptcha(@RequestParam("recaptchaToken") String token) {
		System.out.println("Received token: " + token);
		boolean isValid = reCaptchaService.verifyRecaptcha(token);
		System.out.println("Validation result: " + isValid);
		Map<String, Boolean> response = new HashMap<>();
		response.put("success", isValid);
		System.out.println("Sending response: " + response);
		return ResponseEntity.ok(response);
    }
}
