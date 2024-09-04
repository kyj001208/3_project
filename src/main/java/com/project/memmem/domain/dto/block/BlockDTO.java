package com.project.memmem.domain.dto.block;

import java.time.LocalDateTime;

import com.project.memmem.domain.entity.UserEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BlockDTO {

	private Long id;
	private String blockerNickName; // 차단한 사용자 닉네임
    private String blockedNickName; // 차단 당한 사용자 닉네임
	private LocalDateTime blockTime; //차단 날짜
	
	public BlockDTO(Long id, String blockerNickName, String blockedNickName, LocalDateTime blockTime) {
		BlockDTO blockDTO = BlockDTO.builder()
		        .id(id)
		        .blockerNickName(blockerNickName)
		        .blockedNickName(blockedNickName)
		        .blockTime(blockTime)
		        .build();
	}
	
}
