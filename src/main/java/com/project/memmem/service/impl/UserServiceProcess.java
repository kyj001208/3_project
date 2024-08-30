package com.project.memmem.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.memmem.domain.dto.SaveUserDTO;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.UserEntityRepository;
import com.project.memmem.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceProcess implements UserService{
	////////////////////////////////////////////////////////////////////////////////
	private final UserEntityRepository repository;
	private final PasswordEncoder pe;
	
	@Override
	public void signupProcess(SaveUserDTO dto) {
		UserEntity userEntity = dto.toEntity(pe);
        repository.save(userEntity);
    }

	@Override
    public boolean isEmailDuplicate(String email) {
        return repository.existsByEmail(email);
    }/*
	@Override
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email); // 이메일이 존재하는지 확인하는 메서드
    }*/
}