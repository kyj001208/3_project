package com.project.memmem.security;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.UserEntityRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemmemLoginSuccessHandler implements AuthenticationSuccessHandler {
	
    //private final UserEntityRepository userRepository;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
    	
    	// 인증된 사용자가 CustomUserDetails의 인스턴스인지 확인합니다.
        if (authentication.getPrincipal() instanceof MemmemUserDetails) {
            // 인증된 사용자의 세부 정보를 가져옵니다.
            MemmemUserDetails userDetails = (MemmemUserDetails) authentication.getPrincipal();
            // UserEntity 객체를 가져옵니다. 이 객체에는 사용자의 데이터베이스 정보가 포함되어 있습니다.
            //UserEntity user = userDetails.getUserEntity();
            // 모든 사용자를 메인 페이지("/")로 리디렉션합니다.
            response.sendRedirect("/"); // 메인 페이지로 리디렉션
        } else {
            // 인증된 사용자가 CustomUserDetails의 인스턴스가 아닌 경우에도 메인 페이지로 리디렉션합니다.
            response.sendRedirect("/"); // 로그인 성공이 확인되지 않은 경우에도 메인 페이지로 리디렉션
        }
    }
}