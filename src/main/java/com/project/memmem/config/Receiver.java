package com.project.memmem.config;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.project.memmem.domain.dto.chatbot.MessageDTO;
import com.project.memmem.domain.dto.chatbot.QuestionDTO;
import com.project.memmem.service.impl.chatbot.KomoranServiceProcess;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Receiver {
	// RabbitMQ 사용시 주석해제
		private final SimpMessagingTemplate smt;
		
		private final KomoranServiceProcess komoranService;
		private final TemplateEngine templateEngine; // Inject Thymeleaf template engine
		//RabbitTemplate template 에서 전달한 메세지가 전송된다.
		public void receiveMessage(QuestionDTO dto) {
			System.out.println(">>>>"+dto);
			
			//komoran을 사용해서
			//의도분석->응답메세지 작성
			MessageDTO msg=komoranService.nlpAnalyze(dto.getContent());
		        
			Context thymeleafContext = new Context();
			thymeleafContext.setVariable("msg", msg);
			// Process the Thymeleaf template
			String htmlResponse = templateEngine.process("chatbot/bot-message", thymeleafContext);
			//응답메세지 보내기
			smt.convertAndSend("/topic/bot/"+dto.getKey(), htmlResponse);
		}
		
	}