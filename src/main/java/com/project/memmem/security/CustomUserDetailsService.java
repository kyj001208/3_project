package com.project.memmem.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final UserEntityRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 사용자 정보를 조회합니다. 
        // userRepository는 데이터베이스에서 사용자 정보를 검색하는 역할을 하는 JPA 레포지토리입니다.
        UserEntity user = userRepository.findByEmail(email);

        // 사용자가 존재하지 않는 경우, UsernameNotFoundException 예외를 발생시킵니다.
        // 이는 Spring Security가 로그인 시도 시 사용자 정보를 찾을 수 없다는 것을 의미합니다.
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // 사용자가 존재하는 경우, CustomUserDetails 객체를 생성하여 반환합니다.
        // CustomUserDetails는 Spring Security의 UserDetails 인터페이스를 구현한 클래스입니다.
        // UserEntity 객체를 인자로 받아 사용자 정보를 CustomUserDetails 객체에 설정합니다.
        return new CustomUserDetails(user);
    }

}
