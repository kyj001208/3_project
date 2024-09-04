package com.project.memmem.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.memmem.domain.entity.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);

	String findByNickName(String nickName);
}
