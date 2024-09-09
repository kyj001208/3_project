package com.project.memmem.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.chatbot.ScenarioEntity;

public interface ScenarioRepository extends JpaRepository<ScenarioEntity, Long> {
    Optional<ScenarioEntity> findByDeptAndParentIsNull(int dept);
    Optional<ScenarioEntity> findByParentAndContentContaining(ScenarioEntity parent, String content);
    List<ScenarioEntity> findByParentOrderByDept(ScenarioEntity parent);
}