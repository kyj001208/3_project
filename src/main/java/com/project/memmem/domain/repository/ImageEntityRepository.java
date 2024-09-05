package com.project.memmem.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.ImageEntity;
import com.project.memmem.domain.entity.ImageEntity.ImageType;
import com.project.memmem.domain.entity.UserEntity;

public interface ImageEntityRepository extends JpaRepository<ImageEntity, Long> {

	List<ImageEntity> findByGroupsId(long groupId);  // 특정 그룹에 속한 이미지들을 찾기 위한 메서드
	
	List<ImageEntity> findByGroupsIdAndImageType(Long id, ImageType group);

}
