package com.project.memmem.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.AnswerEntity;



public interface AnswerRepository extends JpaRepository<AnswerEntity, Integer> {
	Optional<AnswerEntity> findByNnpIntention_NnpNo(int nnpNo);
}
