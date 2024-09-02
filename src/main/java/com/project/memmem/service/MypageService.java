package com.project.memmem.service;

import com.project.memmem.domain.dto.user.UserUpdateDTO;
import com.project.memmem.domain.entity.UserEntity;

public interface MypageService {

	UserEntity getUserById(long userId);

	void updateUser(long userId, UserUpdateDTO userUpdateDTO);

}
