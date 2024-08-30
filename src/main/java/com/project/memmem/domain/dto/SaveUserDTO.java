package com.project.memmem.domain.dto;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.memmem.domain.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaveUserDTO {

	private long userId;
	private String name;
    private String nickName; // 닉네임
    private String password; // 비밀번호
    private String RRN; // 주민등록번호
    private String address; // 주소
    private String number; // 핸드폰번호
    private String email; // 이메일
    private String birthDate; // 생년월일
    
	public UserEntity toEntity(PasswordEncoder pe) {
		// TODO Auto-generated method stub
		return null;
	}

    
	
}
