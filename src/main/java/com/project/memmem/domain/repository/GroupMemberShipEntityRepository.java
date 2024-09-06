package com.project.memmem.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.GroupMemberShipEntity;
import com.project.memmem.domain.entity.UserEntity;

public interface GroupMemberShipEntityRepository extends JpaRepository<GroupMemberShipEntity, Long> {

	boolean existsByUserAndGroup(UserEntity user, GroupEntity group);

	boolean existsByUserUserIdAndGroup_Id(Long userId, Long groupId);

	List<GroupMemberShipEntity> findByUser(UserEntity user);

	Optional<GroupMemberShipEntity> findByUserUserIdAndGroup_Id(Long userId, Long groupId);

	List<GroupMemberShipEntity> findByUserAndRole(UserEntity user, GroupMemberShipEntity.Role role);
	
	@Query("SELECT COUNT(gm) FROM GroupMemberShipEntity gm WHERE gm.group = :group")
	int countByGroup(@Param("group")GroupEntity group);

	List<GroupMemberShipEntity> findByGroupId(Long groupId);

}
