package com.project.memmem.domain.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateDTO {
	
	private String name;
    private String nickName;
    private String birthDate;
    private String address;
    private String number;
    private String newPassword; // 옵션: 비밀번호 변경을 위한 필드

}
