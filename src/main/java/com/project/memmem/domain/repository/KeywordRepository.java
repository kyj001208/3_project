package com.project.memmem.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.chatbot.KeywordEntity;

public interface KeywordRepository extends JpaRepository<KeywordEntity, String> {
	Optional<KeywordEntity> findByKeyword(String keyword);
}
