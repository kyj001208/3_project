package com.project.memmem.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.memmem.domain.entity.ReviewEntity;
import com.project.memmem.domain.entity.UserEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

	@Query("SELECT r FROM ReviewEntity r WHERE r.user NOT IN :blockedUsers")
	List<ReviewEntity> findAllExcludingBlockedUsers(@Param("blockedUsers") List<UserEntity> blockedUsers);
}
