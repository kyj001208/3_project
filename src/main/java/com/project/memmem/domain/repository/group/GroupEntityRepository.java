package com.project.memmem.domain.repository.group;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.Category;
import com.project.memmem.domain.entity.GroupEntity;

public interface GroupEntityRepository extends JpaRepository<GroupEntity, Long>{

	Page<GroupEntity> findByCategory(Category category, Pageable pageable);

	List<GroupEntity> findByCategory(Category category);

}
