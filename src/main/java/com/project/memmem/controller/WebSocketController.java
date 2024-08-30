package com.project.memmem.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.project.memmem.domain.dto.bot.AnswerDTO;
import com.project.memmem.domain.dto.bot.QuestionDTO;
import com.project.memmem.service.impl.ChatbotService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    
    private final ChatbotService chatbotService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/bot/question")
    public void handleWebSocketQuestion(QuestionDTO questionDTO) {
        System.out.println("Received question: " + questionDTO.getContent());
        System.out.println("User key: " + questionDTO.getKey());
        
        AnswerDTO answer = chatbotService.processUserQuestion(questionDTO);
        
        System.out.println("Answer: " + answer);
        messagingTemplate.convertAndSend("/topic/bot/" + questionDTO.getKey(), answer);
    }
}