package com.project.memmem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		// "/bookBot" 을 엔드포인트로 등록
		registry.addEndpoint("/bookBot").withSockJS();
		
		// 클라이언트 쪽에서 자바스크립트를 통해 웹소켓을 연결할 때
		// var socket=new SockJS("/bookBot") 과 같이 사용한다
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		
		//사용자->서버 메세지를 전송할때
		// 클라이언트가 서버로 메시지를 보낼 때 "/message/경로" 형식으로 보낸다
		registry.setApplicationDestinationPrefixes("/message");
	}
}