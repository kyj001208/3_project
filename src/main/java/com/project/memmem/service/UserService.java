package com.project.memmem.service;

import com.project.memmem.domain.dto.SaveUserDTO;

public interface UserService {

	boolean isEmailDuplicate(String email);

	void signupProcess(SaveUserDTO dto);

}
