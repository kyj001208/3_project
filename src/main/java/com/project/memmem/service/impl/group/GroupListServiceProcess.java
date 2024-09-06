package com.project.memmem.service.impl.group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public Page<GroupDTO> getGroupsPage(int page, int size, Category category) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<GroupEntity> groupPage;
        
        if (category == null) {
            // 최신순으로 모든 그룹 가져오기
            groupPage = groupRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            // 최신순으로 특정 카테고리의 그룹 가져오기
            groupPage = groupRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
        }
        
        return groupPage.map(this::convertToDTO);
    }

}	