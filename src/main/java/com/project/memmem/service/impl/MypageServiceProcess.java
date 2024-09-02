package com.project.memmem.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.memmem.domain.dto.user.UserUpdateDTO;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.UserEntityRepository;
import com.project.memmem.service.MypageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageServiceProcess implements MypageService{

	private final UserEntityRepository userRepository;

    @Override
    public UserEntity getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Override
    @Transactional
    public void updateUser(long userId, UserUpdateDTO userUpdateDTO) {
        UserEntity user = getUserById(userId);
        
        user.setName(userUpdateDTO.getName());
        user.setNickName(userUpdateDTO.getNickName());
        user.setBirthDate(userUpdateDTO.getBirthDate());
        user.setAddress(userUpdateDTO.getAddress());
        user.setNumber(userUpdateDTO.getNumber());
        
        // 비밀번호 변경 로직 (옵션)
        if (userUpdateDTO.getNewPassword() != null && !userUpdateDTO.getNewPassword().isEmpty()) {
            // 비밀번호 유효성 검사 및 암호화 로직 추가
            user.setPassword(encodePassword(userUpdateDTO.getNewPassword()));
        }
        
        userRepository.save(user);
    }

    private String encodePassword(String password) {
        // 비밀번호 암호화 로직 구현
        // 예: return passwordEncoder.encode(password);
        return password; // 임시 구현, 실제로는 반드시 암호화해야 함
    }
}
