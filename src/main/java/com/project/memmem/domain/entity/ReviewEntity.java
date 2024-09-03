package com.project.memmem.domain.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;


import com.project.memmem.domain.dto.review.ReviewDTO;
import com.project.memmem.domain.dto.review.ReviewListDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicUpdate
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "review")
@Entity
public class ReviewEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reId; // 글번호

	@Column(nullable = false)
	private String title; // 제목

	private String content; // 내용

	@CreationTimestamp
	@Column(columnDefinition = "timestamp")
	protected LocalDateTime createdAt; // 엔티티의 생성 일시

	private String mainImageBucketKey; // 메인 이미지의 S3 버킷 키

	@ManyToOne
	@JoinColumn(name = "userId")
	private UserEntity user;
	
	public String getImageUrl(String imgHost) {
        return imgHost + this.mainImageBucketKey;
    }

    public static ReviewDTO toReviewDTO(ReviewEntity reviewEntity, String imgHost) {
        return ReviewDTO.builder()
                .reId(reviewEntity.getReId())
                .imageUrl(reviewEntity.getImageUrl(imgHost))
                .title(reviewEntity.getTitle())
                .content(reviewEntity.getContent())
                .build();
    }

	public String getImageUrl(String imgHost) {
		return "http:" + imgHost + this.mainImageBucketKey;
	}

	public static ReviewListDTO toListDTO(ReviewEntity reviewEntity, String imgHost) {
		return ReviewListDTO.builder()
				.reId(reviewEntity.getReId())
				.imageUrl(reviewEntity.getImageUrl(imgHost))
				.title(reviewEntity.getTitle())
				.content(reviewEntity.getContent())
				.createdAt(reviewEntity.getCreatedAt())
				.nickName(reviewEntity.getUser() != null ? reviewEntity.getUser().getNickName() : "Anonymous").build();
	}
}