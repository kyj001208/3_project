package com.project.memmem.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import com.project.memmem.domain.dto.review.ReviewDTO;

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
}