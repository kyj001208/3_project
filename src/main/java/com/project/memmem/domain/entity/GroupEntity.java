package com.project.memmem.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import com.project.memmem.domain.dto.group.GroupListDTO;

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
}