package com.project.memmem.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

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
    private String description; // 소모임 설명, TEXT

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일, DATETIME

    @ManyToOne
    @JoinColumn(nullable = false)
    private UserEntity creator; // 소모임 생성자 (UserEntity와 다대일 관계 설정)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; // 카테고리, ENUM
}