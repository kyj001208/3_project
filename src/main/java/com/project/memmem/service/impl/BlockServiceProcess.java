package com.project.memmem.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.memmem.domain.dto.block.BlockDTO;
import com.project.memmem.domain.entity.BlockListEntity;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.BlockRepository;
import com.project.memmem.domain.repository.UserEntityRepository;
import com.project.memmem.service.BlockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockServiceProcess implements BlockService {

	private final BlockRepository blockRepository;
	private final UserEntityRepository userRepository;

	@Override
	@Transactional
	public void blockUser(Long blockerId, Long blockedId) {
		UserEntity blocker = userRepository.findById(blockerId)
				.orElseThrow(() -> new RuntimeException("Blocker not found"));
		UserEntity blocked = userRepository.findById(blockedId)
				.orElseThrow(() -> new RuntimeException("Blocked user not found"));

		if (!blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
			BlockListEntity blockListEntity = BlockListEntity.builder().blocker(blocker).blocked(blocked)
					.blockTime(LocalDateTime.now()).build();

			blockRepository.save(blockListEntity);
		}
	}

	@Override
	public List<UserEntity> getBlockedUsers(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return blockRepository.findBlockedUsersByBlocker(user);
	}
	
	
}