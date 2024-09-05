package com.project.memmem.domain.dto.review;

import java.time.LocalDateTime;

import com.project.memmem.domain.entity.ReviewEntity;

import lombok.Data;

@Data
public class ReviewUpDateDTO {
	
	private String mainImageBucketKey; // 메인 이미지의 S3 버킷 키
	
	private String title;
	
	private String content;
	
	private LocalDateTime createdAt;
	
	private String imageUrl;

	public ReviewEntity toReviewEntity() {
		
		return ReviewEntity.builder()
				.mainImageBucketKey(mainImageBucketKey)
				.title(title)
				.content(content)
				.createdAt(createdAt)
				.build();
	}
	

}
