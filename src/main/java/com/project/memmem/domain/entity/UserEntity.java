package com.project.memmem.domain.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId; // 사용자ID

	@Column(nullable = false)
	private String name; // 사용자이름
	
	@Column(nullable = false)
	private String birthDate; // 생년월일

	@Column(nullable = false)
	private String address; // 주소

	@Column(nullable = false)
	private String number; // 핸드폰번호

	@Column(nullable = false, unique = true)
	private String email; // 이메일
	
	@Column(nullable = false)
	private String password; // 비밀번호
	
	@Column(nullable = false, unique = true)
	private String nickName; // 닉네임

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "role", joinColumns = @JoinColumn(name = "userId"))
    @Column(name = "role")
    private Role role; // 'Role' Enum 타입을 별도로 정의
    
	@OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BlockListEntity> blocking = new HashSet<>();

	@OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BlockListEntity> blockedBy = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ReviewEntity> posts = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<GroupMemberShipEntity> groupMemberShip = new HashSet<>(); // 그룹 멤버십 관계

}
