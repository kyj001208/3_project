package com.project.memmem.domain.dto.block;

import java.time.LocalDateTime;

import com.project.memmem.domain.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BlockDTO {

	private Long id;
	private UserEntity blocker; //차단한 사용자
	private UserEntity blocked; //차단 당한 사용자
	private LocalDateTime blockTime; //차단 날짜
	
}
