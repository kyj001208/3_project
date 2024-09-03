package com.project.memmem.domain.dto.review;

import java.time.LocalDateTime;

import com.project.memmem.domain.entity.ReviewEntity;
import com.project.memmem.domain.entity.UserEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
public class ReviewSaveDTO {

	private String mainImageBucketKey; // 메인 이미지의 S3 버킷 키
	
	private String title;
	
	private String content;
	
	private LocalDateTime createdAt;
	
	//private Long userId; // 작성자의 사용자 ID

	public ReviewEntity  toReviewEntity(long userId) {
		
		return ReviewEntity.builder()
				.mainImageBucketKey(mainImageBucketKey)  // 메인 이미지 키 포함
				.title(title)
				.content(content)
				.createdAt(createdAt)
				.user(UserEntity.builder().userId(userId).build())
				.build();
	}
 
}

