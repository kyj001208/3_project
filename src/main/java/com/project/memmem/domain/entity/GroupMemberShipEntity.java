package com.project.memmem.domain.entity;

import java.time.LocalDateTime;

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
@Table(name = "groupMemberShip")
@Getter
@Setter
@Entity
public class GroupMemberShipEntity {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "user_id", nullable = false)
	    private UserEntity user; // 사용자

	    @ManyToOne
	    @JoinColumn(name = "group_id", nullable = false)
	    private GroupEntity group; // 그룹

	    @Enumerated(EnumType.STRING)
	    private Role role; // 사용자의 역할 (생성자 또는 참여자)

	    private LocalDateTime joinedAt; // 사용자가 그룹에 참여한 날짜

	    // Enum 정의
	    public enum Role {
	        ROLE_CREATOR, // 그룹 생성자
	        ROLE_MEMBER   // 그룹 참여자
	    }


}
