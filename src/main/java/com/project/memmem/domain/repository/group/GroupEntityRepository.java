package com.project.memmem.domain.repository.group;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.memmem.domain.entity.Category;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.UserEntity;

public interface GroupEntityRepository extends JpaRepository<GroupEntity, Long>{

	Page<GroupEntity> findByCategory(Category category, Pageable pageable);

	List<GroupEntity> findByCategory(Category category);
	
	List<GroupEntity> findByCreator(UserEntity creator);

	List<GroupEntity> findAllByOrderByCreatedAtDesc();

	// 멤버 수가 많은 순으로 정렬하고, 멤버 수가 같을 경우 최신 생성일 기준으로 정렬
    @Query("SELECT g FROM GroupEntity g LEFT JOIN GroupMemberShipEntity m ON g = m.group GROUP BY g ORDER BY COUNT(m) DESC, g.createdAt DESC")
    Page<GroupEntity> findAllByOrderByMemberCountDescAndCreatedAtDesc(Pageable pageable);

    // 카테고리별로 멤버 수가 많은 순으로 정렬하고, 멤버 수가 같을 경우 최신 생성일 기준으로 정렬
    @Query("SELECT g FROM GroupEntity g LEFT JOIN GroupMemberShipEntity m ON g = m.group WHERE g.category = :category GROUP BY g ORDER BY COUNT(m) DESC, g.createdAt DESC")
    Page<GroupEntity> findByCategoryOrderByMemberCountDescAndCreatedAtDesc(@Param("category") Category category, Pageable pageable);

  

}
