package com.project.memmem.service.impl.group;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.project.memmem.domain.dto.group.GroupDTO;
import com.project.memmem.domain.entity.Category;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.repository.group.GroupEntityRepository;
import com.project.memmem.service.GroupListService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupListServiceProcess implements GroupListService {

	private final GroupEntityRepository groupRepository;
    private final String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/jyj.img.host/";

    private GroupDTO convertToDTO(GroupEntity group) {
        return group.toGroupDTO(baseUrl);
    }

    @Override
    public void groupsList(Category category, Model model) {
        List<GroupEntity> groups; // 조회된 그룹 엔티티를 저장할 리스트

        // 카테고리가 null이면 모든 그룹을 조회, 아니면 특정 카테고리에 속한 그룹을 조회
        if (category == null) {
            groups = groupRepository.findAll(); // 모든 그룹 조회
        } else {
            groups = groupRepository.findByCategory(category); // 특정 카테고리의 그룹만 조회
        }

        // 조회된 그룹 엔티티를 DTO로 변환
        List<GroupDTO> groupDTOs = groups.stream()
                .map(this::convertToDTO) // 각 그룹 엔티티를 DTO로 변환
                .collect(Collectors.toList());

        model.addAttribute("groups", groupDTOs); // 변환된 DTO 리스트를 모델에 추가
    }
    
    @Override //그룹 데이터의 페이지를 조회하는 메서드
    public Page<GroupDTO> getGroupsPage(int page, int size, Category category) {
        // 페이지 요청을 위한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);
        
        Page<GroupEntity> groupPage;
        
        if (category == null) {
            groupPage = groupRepository.findAll(pageable);
        } else {
            groupPage = groupRepository.findByCategory(category, pageable);
        }
        
        return groupPage.map(this::convertToDTO);
    }

}	