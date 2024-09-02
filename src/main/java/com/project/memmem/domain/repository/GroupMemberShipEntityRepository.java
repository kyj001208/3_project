package com.project.memmem.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.GroupMemberShipEntity;
import com.project.memmem.domain.entity.UserEntity;

public interface GroupMemberShipEntityRepository extends JpaRepository<GroupMemberShipEntity, Long>{

	boolean existsByUserAndGroup(UserEntity user, GroupEntity group);

	  boolean existsByUserUserIdAndGroup_Id(Long userId, Long groupId);

}
