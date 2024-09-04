package com.project.memmem.service;

import java.util.List;

import com.project.memmem.domain.dto.block.BlockDTO;
import com.project.memmem.domain.entity.UserEntity;

public interface BlockService {

	void blockUser(Long blockerId, Long blockedId);

	List<UserEntity> getBlockedUsers(Long userId);
	 
	 

}
