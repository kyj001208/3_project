package com.project.memmem.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.BlockListEntity;

public interface BlockRepository extends JpaRepository<BlockListEntity, Long>{

	
}
