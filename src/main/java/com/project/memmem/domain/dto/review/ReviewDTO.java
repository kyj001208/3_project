package com.project.memmem.domain.dto.review;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
	
	private Long reId;
	private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt; // 포맷된 날짜를 저장
}
