package com.project.memmem.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import lombok.RequiredArgsConstructor;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    //private final CustomUserDetailsService customUserDetailsService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    
    
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
	        .authorizeHttpRequests(authorize -> authorize
	        		.requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
	        		.requestMatchers("/","/groupSave","/login","/logout","/signup", "/login?error=true").permitAll()  // 로그인 페이지와 오류 페이지에 대한 접근 허용               
	                .requestMatchers("/mypage/").hasRole("USER")
	                .anyRequest().authenticated()
	            )
	        .formLogin(login -> login
	            .loginPage("/login")
	            .loginProcessingUrl("/login")
	            .usernameParameter("email")
	            .passwordParameter("password")
	            .successHandler(customLoginSuccessHandler)
	            .failureUrl("/login?error=true") // 로그인 실패 시 리다이렉트할 URL
	            .permitAll()
	        )
	        .logout(logout -> logout
	            .logoutUrl("/logout")
	            .logoutSuccessUrl("/")
	            .invalidateHttpSession(true)
	            .deleteCookies("JSESSIONID")
	            .permitAll()
	        )
	        /*
	        //GET 요청을 통해 로그아웃을 처리하도록 허용
	        .logout(logout -> logout.logoutRequestMatcher(
	                 new OrRequestMatcher(
	                        new AntPathRequestMatcher("/logout", "GET"),
	                        new AntPathRequestMatcher("/logout", "POST")
	                    )
	        ))
	        */
	           ;

        return http.build();
    }
}