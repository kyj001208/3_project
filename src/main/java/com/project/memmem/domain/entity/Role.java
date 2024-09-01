package com.project.memmem.domain.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
	
    USER("사용자"), ADMIN("관리자");
	
	private final String roleName;

	public final String roleName() { //getter 대신에 쓰는 메서드
		return roleName;
	}
}