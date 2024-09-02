package com.project.memmem.service.impl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.memmem.domain.dto.SignupDTO;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    /*
    //참고서적 코드
    public UserEntity saveUser(UserEntity user) {
        checkUser(user);
        return userEntityRepository.save(user);
    }
     */
    public void saveUser(SignupDTO dto,PasswordEncoder pe) {
        checkUser(dto.toEntity(pe));
        userEntityRepository.save(dto.toEntity(pe));
    }
    private void checkUser(UserEntity user) {
        UserEntity findUser = userEntityRepository.findByEmail(user.getEmail());
        if(findUser != null) {
            throw new IllegalStateException("이미 있는 이메일입니다.");
        }
    }
}