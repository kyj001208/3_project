
package com.project.memmem.domain.entity;

import java.time.LocalDateTime;

import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "image")
@Entity
public class ImageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ID; // 이미지 번호

	@Column(nullable = false)
	private String imageUrl; // 제목

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ImageType imageType;

	@ManyToOne
	@JoinColumn(name = "group_id") // 기존 'id'를 'group_id'로 변경
	private GroupEntity groups;

	@ManyToOne
	@JoinColumn(name = "review_id") // 기존 'reId'를 'review_id'로 변경
	private ReviewEntity review;

	public enum ImageType {
		REVIEW, GROUP
	}
}