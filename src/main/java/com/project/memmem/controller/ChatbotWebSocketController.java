/*
 * package com.project.memmem.controller;
 * 
 * import org.springframework.messaging.handler.annotation.MessageMapping;
 * import org.springframework.messaging.handler.annotation.SendTo; import
 * org.springframework.stereotype.Controller;
 * 
 * import com.project.memmem.domain.dto.bot.AnswerDTO; import
 * com.project.memmem.domain.dto.bot.QuestionDTO; import
 * com.project.memmem.service.impl.ChatbotService;
 * 
 * import lombok.RequiredArgsConstructor;
 * 
 * @Controller
 * 
 * @RequiredArgsConstructor public class ChatbotWebSocketController { private
 * final ChatbotService chatbotService;
 * 
 * @MessageMapping("/question")
 * 
 * @SendTo("/topic/answers") public AnswerDTO handleQuestion(QuestionDTO
 * question) { return chatbotService.processUserQuestion(question); } }
 */