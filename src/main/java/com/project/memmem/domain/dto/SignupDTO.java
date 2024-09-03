package com.project.memmem.domain.dto;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.memmem.domain.entity.Role;
import com.project.memmem.domain.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupDTO {

	private long userId;
	private String name;
	private String birthDate; // 생년월일
	private String address; // 주소
	private String number; // 핸드폰번호
	private String email; // 이메일
	private String password; // 비밀번호
    private String nickName; // 닉네임
    
    public UserEntity toEntity(PasswordEncoder pe) {
		return UserEntity.builder()
				.userId(userId)
				.name(name)
				.birthDate(birthDate)
				.address(address)
				.number(number)
				.email(email)
				.password(pe.encode(password))
				.nickName(nickName)
				.role(Role.USER)
				.build();
    }
}
