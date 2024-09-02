package com.project.memmem.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.memmem.service.HiddenService;

@RestController  // Removed @Controller
@RequiredArgsConstructor
public class MypageHController {

    private final HiddenService hiddenService;

    // 질문을 받아 GPT API에 요청을 보내고 응답을 반환하는 엔드포인트
    @PostMapping("/ask")
    @ResponseBody
    public String askGpt(@RequestBody String question) {
        return hiddenService.getGptResponse(question);
    }
}