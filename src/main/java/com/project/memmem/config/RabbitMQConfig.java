package com.project.memmem.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableRabbit
public class RabbitMQConfig {
	
		// 메시지 리스너 어댑터를 정의,MessageMQ
		@Bean
		MessageListenerAdapter messageListenerAdapter(Receiver receiver) {
			//receiver 객체와 receiveMessage 메소드를 사용하여 메시지 리스너 어댑터를 설정
			MessageListenerAdapter messageListenerAdapter=new MessageListenerAdapter(receiver, "receiveMessage");
			messageListenerAdapter.setMessageConverter(messageConverter());
			return messageListenerAdapter;
		}
		
		
		//RabbitMQ 메시지 변환기를 정의
		@Bean
		MessageConverter messageConverter() {
			return new Jackson2JsonMessageConverter();
		}
		
	}