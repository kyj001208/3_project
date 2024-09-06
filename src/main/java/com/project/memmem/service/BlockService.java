package com.project.memmem.service;

import java.util.List;

import org.springframework.ui.Model;

import com.project.memmem.domain.dto.block.BlockDTO;
import com.project.memmem.domain.entity.UserEntity;

public interface BlockService {

	void blockUser(Long blockerId, Long blockedId);

	List<BlockDTO> getBlockedUsers(Long userId);

	void unblockProcess(long id);
	
	
	 
	 

}
