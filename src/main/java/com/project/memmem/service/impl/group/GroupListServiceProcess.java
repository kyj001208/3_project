package com.project.memmem.service.impl.group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.memmem.domain.dto.group.GroupDTO;
import com.project.memmem.domain.entity.Category;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.repository.GroupMemberShipEntityRepository;
import com.project.memmem.domain.repository.group.GroupEntityRepository;
import com.project.memmem.service.GroupListService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupListServiceProcess implements GroupListService {

	private final GroupEntityRepository groupRepository;
	private final GroupMemberShipEntityRepository memberShipRep;
    private final String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/jyj.img.host/";

 // GroupEntity를 GroupDTO로 변환하는 메서드
    private GroupDTO convertToDTO(GroupEntity group) {
        // 각 그룹의 멤버 수 계산
        int memberCount = memberShipRep.countByGroup(group);
        
        // 멤버 수를 포함하여 DTO 생성
        return group.toGroupDTO(baseUrl, memberCount);
    }
    
    @Override
    public Page<GroupDTO> getGroupsPage(int page, int size, Category category) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<GroupEntity> groupPage;
        
        if (category == null) {
            // 멤버 수가 많은 순으로, 멤버 수가 같을 경우 최신 생성일 기준으로 정렬된 그룹 가져오기
            groupPage = groupRepository.findAllByOrderByMemberCountDescAndCreatedAtDesc(pageable);
        } else {
            // 멤버 수가 많은 순으로, 멤버 수가 같을 경우 최신 생성일 기준으로 정렬된 특정 카테고리의 그룹 가져오기
            groupPage = groupRepository.findByCategoryOrderByMemberCountDescAndCreatedAtDesc(category, pageable);
        }
        
        // 각 그룹을 DTO로 변환하고 멤버 수 포함
        return groupPage.map(this::convertToDTO);
    }

}	