package com.project.memmem.security;

import lombok.Getter;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.project.memmem.domain.entity.UserEntity;

@Getter
public class CustomUserDetails extends User {

    private static final long serialVersionUID = 1L;

    private final String email; // 사용자의 이메일
    private final String name; // 사용자의 이름
    private final UserEntity userEntity; // 사용자와 연관된 UserEntity를 참조합니다.
    private final long userId; // 사용자의 고유 ID
    private final String number; // 사용자의 전화번호

    /**
     * UserEntity를 기반으로 CustomUserDetails 객체를 생성합니다.
     * 이 생성자는 일반 로그인 사용자를 위한 것입니다.
     *
     * @param entity UserEntity 객체
     */
    public CustomUserDetails(UserEntity entity) {
        // 부모 클래스인 User의 생성자를 호출하여 사용자명과 비밀번호를 설정합니다.
        super(entity.getEmail(), entity.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // UserEntity로부터 사용자 세부 정보를 초기화합니다.
        this.email = entity.getEmail();
        this.name = entity.getName();
        this.userEntity = entity; // UserEntity 객체를 저장합니다.
        this.userId = entity.getUserId();
        this.number = entity.getNumber();
    }

    /**
     * UserEntity 객체를 반환하는 메서드입니다.
     * 이 메서드를 통해 UserEntity에 직접 접근할 수 있습니다.
     *
     * @return UserEntity 객체
     */
    public UserEntity getUser() {
        return this.userEntity;
    }
}
