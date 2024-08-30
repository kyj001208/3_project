package com.project.memmem.security;

import java.io.IOException;
import java.util.Collection; // 컬렉션 인터페이스를 임포트
import java.util.HashMap; // 해시맵 클래스를 임포트
import java.util.Map; // 맵 인터페이스를 임포트

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority; // 스프링 시큐리티의 권한 객체를 임포트
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler; // 인증 성공 핸들러를 임포트
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper; // JSON 처리를 위한 Jackson 라이브러리의 ObjectMapper를 임포트

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // 이 클래스를 스프링 빈으로 등록
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override // 부모 클래스의 메서드를 오버라이드
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        handle(request, response, authentication); // 인증 성공 시 처리 로직 호출
        clearAuthenticationAttributes(request); // 인증 관련 속성들을 제거
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String targetUrl = determineTargetUrl(authentication); // 인증된 사용자의 목표 URL을 결정

        if (response.isCommitted()) { // 응답이 이미 커밋되었는지 확인
            return; // 커밋되었으면 더 이상의 작업을 중단
        }

        // 추가로 role_name을 응답에 추가
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities(); // 사용자의 권한을 가져옴
        String role = authorities.iterator().next().getAuthority(); // 첫 번째 권한을 가져옴

        Map<String, String> userInfo = new HashMap<>(); // 사용자 정보를 저장할 맵 생성
        userInfo.put("role_name", role); // 권한 이름을 맵에 추가

        ObjectMapper mapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper 객체 생성
        String userInfoJson = mapper.writeValueAsString(userInfo); // 사용자 정보를 JSON 문자열로 변환

        response.setContentType("application/json"); // 응답의 콘텐츠 타입을 JSON으로 설정
        response.setCharacterEncoding("UTF-8"); // 응답의 문자 인코딩을 UTF-8로 설정
        response.getWriter().write(userInfoJson); // 응답 바디에 JSON 문자열을 작성

        clearAuthenticationAttributes(request); // 인증 관련 속성들을 제거
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities(); // 사용자의 권한을 가져옴

        for (GrantedAuthority authority : authorities) { // 각 권한을 순회
            String authorityName = authority.getAuthority(); // 권한 이름을 가져옴

            if ("ROLE_USER".equals(authorityName)) { // 권한이 "ROLE_USER"이면
                return "/"; // 학생용 페이지로 리다이렉트
            }
        }
        return "/"; // 기본 페이지로 이동하거나 다른 처리 추가
    }
}