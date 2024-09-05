package com.project.memmem.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.memmem.domain.entity.BlockListEntity;
import com.project.memmem.domain.entity.UserEntity;

public interface BlockRepository extends JpaRepository<BlockListEntity, Long> {

	// 블로커와 블록된 사용자에 대한 블록 여부를 확인하는 메서드 (자동 생성)
	boolean existsByBlockerAndBlocked(UserEntity blocker, UserEntity blocked);

	// 블로커에 의해 차단된 사용자 목록을 가져오는 메서드
	@Query("SELECT b.blocked FROM BlockListEntity b WHERE b.blocker = :user")
	List<UserEntity> findBlockedUsersByBlocker(@Param("user") UserEntity user);

	List<BlockListEntity> findByBlocker(UserEntity blocker);

}