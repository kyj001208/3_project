package com.project.memmem.domain.dto.review;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewListDTO {
	
	private long reId;

	private String imageUrl; // 메인 이미지의 S3 버킷 키

	private String title;

	private String content;

	private LocalDateTime createdAt;

	private  String nickName;
}
