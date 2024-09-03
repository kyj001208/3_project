package com.project.memmem.service;

import org.springframework.ui.Model;

import com.project.memmem.domain.dto.user.UserUpdateDTO;
import com.project.memmem.domain.entity.UserEntity;

public interface MypageService {

	UserEntity getUserById(long userId);

	void updateUser(long userId, UserUpdateDTO userUpdateDTO);

	void listProcess(Long userId, Model model);

}
