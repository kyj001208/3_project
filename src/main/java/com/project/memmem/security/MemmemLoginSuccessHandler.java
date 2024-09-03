package com.project.memmem.security;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemmemLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // AJAX 요청인지 확인
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            // JSON 응답 작성
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"success\"}");
        } else {
            // 일반 리디렉션 처리
            response.sendRedirect("/");
        }
    }
}
