package com.project.memmem.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.project.memmem.domain.dto.chatbot.AnswerDTO;
import com.project.memmem.domain.dto.chatbot.QuestionDTO;
import com.project.memmem.service.impl.chatbot.ChatbotServiceProcess;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

	private final ChatbotServiceProcess chatbotService;
	private final SimpMessagingTemplate messagingTemplate;

	// 클라이언트에서 "/bot/question" 경로로 메시지가 들어올 때 처리하는 메서드
	@MessageMapping("/bot/question")
	public void handleWebSocketQuestion(QuestionDTO questionDTO) {
		System.out.println("Received question: " + questionDTO.getContent());
		System.out.println("User key: " + questionDTO.getKey());

		// ChatbotService를 사용하여 사용자의 질문을 처리하고 답변을 생성
		AnswerDTO answer = chatbotService.processUserQuestion(questionDTO);

		System.out.println("Answer: " + answer);
		messagingTemplate.convertAndSend("/topic/bot/" + questionDTO.getKey(), answer);
	}

	// 클라이언트에서 "/bot/reset" 경로로 메시지가 들어올 때 처리하는 메서드
	@MessageMapping("/bot/reset")
	public void handleResetMessage(SimpMessageHeaderAccessor headerAccessor) {
		// ChatbotService를 사용하여 시나리오를 초기화
		chatbotService.resetScenario();
	}
}