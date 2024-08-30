package com.project.memmem.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;

        // InternalAuthenticationServiceException을 처리
        if (exception instanceof InternalAuthenticationServiceException) {
            Throwable cause = exception.getCause();
            if (cause instanceof DisabledException) {
                exception = (DisabledException) cause;
            }
        }

        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
        } else if (exception instanceof DisabledException) {
            String exceptionMessage = exception.getMessage();
            if ("delete".equals(exceptionMessage)) {
                errorMessage = "탈퇴한 회원입니다.";
            } else {
                errorMessage = "계정이 비활성화 되었습니다. 관리자에게 문의하세요.";
            }
        } else {
            errorMessage = "로그인 중 오류가 발생했습니다. 다시 시도해 주세요.";
        }

        // 에러 메시지 URL 인코딩
        errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // 실패 URL 설정
        String failureUrl = "/login?error=true&exception=" + errorMessage;

        setDefaultFailureUrl(failureUrl);
        super.onAuthenticationFailure(request, response, exception);
    }
}
