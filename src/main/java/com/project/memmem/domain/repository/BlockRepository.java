package com.project.memmem.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.memmem.domain.entity.BlockListEntity;
import com.project.memmem.domain.entity.UserEntity;

public interface BlockRepository extends JpaRepository<BlockListEntity, Long>{

	@Query("SELECT COUNT(b) > 0 FROM BlockListEntity b WHERE b.blocker = :blocker AND b.blocked = :blocked")
	boolean existsByBlockerAndBlocked(@Param("blocker") UserEntity blocker, @Param("blocked") UserEntity blocked);
	
	@Query("SELECT b.blocked FROM BlockListEntity b WHERE b.blocker = :blocker")
    List<UserEntity> findBlockedUsersByBlocker(@Param("blocker") UserEntity blocker);

	
}
