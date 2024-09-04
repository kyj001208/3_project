package com.project.memmem.domain.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import com.project.memmem.domain.dto.group.GroupDTO;
import com.project.memmem.domain.dto.group.GroupListDTO;
import com.project.memmem.domain.dto.group.GroupSaveDTO;
import com.project.memmem.domain.dto.group.GroupUpdateDTO;
import com.project.memmem.domain.repository.ImageEntityRepository;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DynamicUpdate
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "groups")
@Getter
@Setter
@Entity
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = false, length = 255)
    private String groupName; // 그룹 이름, VARCHAR(255)

    @Column(columnDefinition = "TEXT")
    private String greeting; // 그룹 인삿말, TEXT
    
    @Column(columnDefinition = "TEXT")
    private String description; // 소모임 설명, TEXT

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일, DATETIME

    @ManyToOne
    @JoinColumn(nullable = false)
    private UserEntity creator; // 소모임 생성자 (UserEntity와 다대일 관계 설정)

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMemberShipEntity> groupMemberShip; // 그룹 멤버십 관계
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; // 카테고리, ENUM
    
    @OneToMany(mappedBy = "groups", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEntity> images; // 그룹과 연결된 이미지 리스트
    
    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }
    
    // GroupListDTO 변환 메서드
    public GroupListDTO toGroupListDTO(String baseUrl) {
        String mainImageUrl = null;

        // 이미지 목록 중 메인 이미지를 가져오는 로직
        for (ImageEntity image : images) {
            if (image.getImageType() == ImageEntity.ImageType.GROUP && image.getImageUrl() != null) {
                mainImageUrl = baseUrl + image.getImageUrl();
                break; // 첫 번째 이미지를 메인 이미지로 사용
            }
        }

        return GroupListDTO.builder()
        		.id(id)
                .groupName(this.groupName)
                .greeting(this.greeting)
                .description(this.description)
                .category(this.category)
                .creatorNickname(creator.getNickName())
                .mainImageUrl(mainImageUrl) // 메인 이미지 URL 설정
                .build();
    }
    
    public GroupDTO toGroupDTO(String baseUrl) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YY/MM/dd");
        String formattedDate = this.createdAt.format(formatter); // GroupEntity의 생성 날짜를 지정된 형식으로 포맷

        return GroupDTO.builder()
        		.id(this.id)
                .groupName(this.groupName)
                .greeting(this.greeting)
                .categoryKoName(this.category.getKoName()) // Category의 한국어 이름 설정
                .creatorUserId(this.creator.getUserId())
                .mainImageUrl(getMainImageUrl(baseUrl)) // 메인 이미지 URL 설정 (getMainImageUrl 메서드 사용)
                .createdAt(formattedDate)
                .build();
    }

    private String getMainImageUrl(String baseUrl) {
        // 이미지 목록 중에서 첫 번째로 찾은 그룹 이미지의 URL을 반환 (없으면 null 반환)
        return images.stream()
                .filter(image -> image.getImageType() == ImageEntity.ImageType.GROUP && image.getImageUrl() != null) // 그룹 이미지 타입이면서 URL이 존재하는 이미지 필터링
                .findFirst() // 첫 번째 일치하는 이미지 찾기
                .map(image -> baseUrl + image.getImageUrl()) // 이미지 URL에 기본 URL 추가하여 전체 URL 생성
                .orElse(null); // 이미지가 없는 경우 null 반환
    }


	public void update(GroupUpdateDTO dto) {
		this.groupName = dto.getGroupName();
		this.category = dto.getCategory();
		this.greeting = dto.getGreeting();
        this.description = dto.getDescription();
		
	}
	
	// 이미지 업데이트 메서드
    public void updateImages(List<String> imageUrls, ImageEntityRepository imageRepository) {
        // 기존 이미지 삭제
        if (!this.images.isEmpty()) {
            imageRepository.deleteAll(this.images);
            this.images.clear();
        }

        // 새 이미지 추가
        for (String imageUrl : imageUrls) {
            ImageEntity newImage = ImageEntity.builder()
                    .groups(this)
                    .imageUrl(imageUrl)
                    .imageType(ImageEntity.ImageType.GROUP)
                    .build();
            this.images.add(newImage);
        }
    }
}