package com.project.memmem.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long>{

}
