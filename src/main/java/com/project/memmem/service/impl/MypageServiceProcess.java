package com.project.memmem.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.project.memmem.domain.dto.user.MyGroupListDTO;
import com.project.memmem.domain.dto.user.UserUpdateDTO;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.GroupMemberShipEntity;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.GroupMemberShipEntityRepository;
import com.project.memmem.domain.repository.UserEntityRepository;
import com.project.memmem.domain.repository.group.GroupEntityRepository;
import com.project.memmem.service.MypageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageServiceProcess implements MypageService{

	private final UserEntityRepository userRepository;
	private final GroupMemberShipEntityRepository groupMemberShipEntityRepository;
	private final GroupEntityRepository groupEntityRepository;

    @Override
    public UserEntity getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Override
    @Transactional
    public void updateUser(long userId, UserUpdateDTO userUpdateDTO) {
        UserEntity user = getUserById(userId);
        
        user.setName(userUpdateDTO.getName());
        user.setNickName(userUpdateDTO.getNickName());
        user.setBirthDate(userUpdateDTO.getBirthDate());
        user.setAddress(userUpdateDTO.getAddress());
        user.setNumber(userUpdateDTO.getNumber());
        
        // 비밀번호 변경 로직 (옵션)
        if (userUpdateDTO.getNewPassword() != null && !userUpdateDTO.getNewPassword().isEmpty()) {
            // 비밀번호 유효성 검사 및 암호화 로직 추가
            user.setPassword(encodePassword(userUpdateDTO.getNewPassword()));
        }
        
        userRepository.save(user);
    }

    private String encodePassword(String password) {
        // 비밀번호 암호화 로직 구현
        // 예: return passwordEncoder.encode(password);
        return password; // 임시 구현, 실제로는 반드시 암호화해야 함
    }

    @Override
    @Transactional(readOnly = true)
    public void listProcess(Long userId, Model model) {
        try {
            UserEntity user = userRepository.findById(userId).orElseThrow();

            // 내가 만든 모임 조회 (내가 가입한 모임 제외)
            List<GroupMemberShipEntity> memberships = groupMemberShipEntityRepository.findByUserAndRole(user, GroupMemberShipEntity.Role.ROLE_MEMBER);           
            List<MyGroupListDTO> joinedGroups = memberships.stream()
                .map(membership -> MyGroupListDTO.fromMembership(membership, "baseUrl"))
                .collect(Collectors.toList());

            //내가 가입한 모임 조회
            List<GroupEntity> createdGroups = groupEntityRepository.findByCreator(user);

            List<MyGroupListDTO> createdGroupsDTO = createdGroups.stream()
                .map(group -> MyGroupListDTO.fromGroup(group, "baseUrl"))
                .collect(Collectors.toList());

            model.addAttribute("joinedGroups", joinedGroups);
            model.addAttribute("createdGroups", createdGroupsDTO);
        } catch (Exception e) {
            System.err.println("Error in listProcess: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "모임 목록을 불러오는 중 오류가 발생했습니다.");
        }
    }
}
