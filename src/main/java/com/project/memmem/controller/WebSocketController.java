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
    	
        // 사용자의 질문을 Komoran을 통해 분석하고 결과를 반환
        AnswerDTO answer = chatbotService.processUserQuestion(questionDTO);
        
        System.out.println("User key: " + questionDTO.getKey());
        
        // 분석 결과를 콘솔에 출력
        System.out.println("Received question: " + questionDTO.getContent());
        
		long key = questionDTO.getKey(); //QuestionDTO에서 key 값을 추출
		String responseMessage = answer.getAnswer();  //AnswerDTO answer에서 답변추출
		
		System.out.println("Analysis result: " + responseMessage);
		messagingTemplate.convertAndSend("/topic/bot/"+key, responseMessage);;
    }
}