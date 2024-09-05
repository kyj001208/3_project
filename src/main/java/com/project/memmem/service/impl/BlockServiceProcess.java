package com.project.memmem.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.project.memmem.domain.dto.block.BlockDTO;
import com.project.memmem.domain.entity.BlockListEntity;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.BlockRepository;
import com.project.memmem.domain.repository.UserEntityRepository;
import com.project.memmem.service.BlockService;

import jakarta.persistence.EntityNotFoundException;
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

	/*
	 * @Override public List<BlockDTO> getBlockedUsers(Long userId) { UserEntity
	 * user = userRepository.findById(userId).orElseThrow(() -> new
	 * RuntimeException("유저를 찾을수 없습니다.")); List<BlockListEntity> blockedEntities =
	 * blockRepository.findByBlocker(user);
	 * 
	 * return blockedEntities.stream() .map(block -> BlockDTO.builder()
	 * .id(block.getId()) .blockerNickName(block.getBlocker().getNickName())
	 * .blockedNickName(block.getBlocked().getNickName())
	 * .blockTime(block.getBlockTime()) .build()) .collect(Collectors.toList());
	 * 
	 * 
	 * }
	 */
	
	@Transactional
	public BlockListEntity getBlockedUsers(long id) {
		return blockRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
	}
	
	public void getBlockedUsers(Model model, long id) {
		BlockListEntity user = getBlockedUsers(id);
		model.addAttribute("user",user);
		
		String dateFormatter = formatTime(user.getBlockTime());
		model.addAttribute("formattedTime", dateFormatter);
		
	}


	public String formatTime(LocalDateTime blockTime) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return blockTime.format(dateFormatter);

	}

	

}