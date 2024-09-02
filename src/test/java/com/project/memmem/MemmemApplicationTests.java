package com.project.memmem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.project.memmem.domain.dto.SignupDTO;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.service.impl.UserService;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application.properties")
class MemmemApplicationTests {

	@Autowired
	UserService userService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public UserEntity toEntity() {
		SignupDTO dto = new SignupDTO();
		dto.setName("전용준");
		dto.setBirthDate("19960224");
		dto.setRRN("960224-1111111");
		dto.setAddress("덕릉로");
		dto.setNumber("010-6412-7777");
		dto.setEmail("qwer1234@test.com");
		dto.setPassword("qwer1234");
		dto.setNickName("웨엥");
		return dto.toEntity(passwordEncoder);
	}
	/*
	@Test
	@DisplayName("회원가입 테스트")
	public void saveUserTest() {
		UserEntity user = toEntity();
		UserEntity saveUser = userService.saveUser(user);
		
		assertEquals(user.getName(), saveUser.getName());
		assertEquals(user.getBirthDate(), saveUser.getBirthDate());
		assertEquals(user.getRRN(), saveUser.getRRN());
		assertEquals(user.getAddress(), saveUser.getAddress());
		assertEquals(user.getNumber(), saveUser.getNumber());
		assertEquals(user.getEmail(), saveUser.getEmail());
		assertEquals(user.getPassword(), saveUser.getPassword());
		assertEquals(user.getNickName(), saveUser.getNickName());
		assertEquals(user.getRole(), saveUser.getRole());
	}
	
	@Test
	@DisplayName("중복 회원 가입 테스트")
	public void checkUserTest() {
		UserEntity user1 = toEntity();
		UserEntity user2 = toEntity();
		userService.saveUser(user1);
		
		Throwable e = assertThrows(IllegalStateException.class, ()->{
			userService.saveUser(user2);});
		assertEquals("이미 가입된 이메일입니다.", e.getMessage());
	}
	*/
}
