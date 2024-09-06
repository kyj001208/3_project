package com.project.memmem.domain.repository.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.NoticeEntity;

public interface NoticeEntityRepository extends JpaRepository<NoticeEntity, Long>{

	List<NoticeEntity> findByGroupId(Long groupId);
}
