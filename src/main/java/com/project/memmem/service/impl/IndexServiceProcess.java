package com.project.memmem.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.project.memmem.domain.dto.group.GroupDTO;
import com.project.memmem.domain.dto.review.ReviewDTO;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.ReviewEntity;
import com.project.memmem.domain.repository.GroupMemberShipEntityRepository;
import com.project.memmem.domain.repository.ReviewRepository;
import com.project.memmem.domain.repository.group.GroupEntityRepository;
import com.project.memmem.service.IndexService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndexServiceProcess implements IndexService {
	
	private final GroupEntityRepository groupRepository;
	private final ReviewRepository reviewRepository;
	private final GroupMemberShipEntityRepository memberShipRep;
    private final String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/jyj.img.host/";
    
    @Value("${spring.cloud.aws.s3.host}") 
    private String imgHost;
    
    @Override
    public void groupsList(Model model) {
    	List<GroupEntity> groups = groupRepository.findAllByOrderByCreatedAtDesc();
        List<GroupDTO> groupDTOs = groups.stream()
            .map(group -> {
                GroupDTO groupDTO = group.toGroupDTO(baseUrl);
                int memberCount = memberShipRep.countByGroup(group);
                groupDTO.setMemberCount(memberCount);
                return groupDTO;
            })
            .collect(Collectors.toList());
        model.addAttribute("groups", groupDTOs);
    }
    
    @Override
    public void reviewList(Model model) {
    	List<ReviewDTO> reviewDTOs = reviewRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(review -> ReviewEntity.toReviewDTO(review, imgHost)) // 'toReviewDTO' -> 'toListDTO'로 변경
            .collect(Collectors.toList());
        
        model.addAttribute("review", reviewDTOs); // 모델에 변환된 DTO 리스트 추가
    }
    
    @Override
    public void premiumPicks(Model model) {
        // 모든 그룹을 생성일 기준으로 가져오기
        List<GroupEntity> groups = groupRepository.findAllByOrderByCreatedAtDesc();
        
        // GroupEntity를 GroupDTO로 변환하고 멤버 수를 계산
        List<GroupDTO> groupDTOs = groups.stream()
            .map(group -> {
                GroupDTO groupDTO = group.toGroupDTO(baseUrl); // GroupDTO로 변환
                int memberCount = memberShipRep.countByGroup(group); // 멤버 수 계산
                groupDTO.setMemberCount(memberCount); // DTO에 멤버 수 설정
                return groupDTO;
            })
            .sorted((g1, g2) -> {
                // 멤버 수가 많은 순서로 정렬
                int memberComparison = Integer.compare(g2.getMemberCount(), g1.getMemberCount());
                if (memberComparison == 0) {
                    // 멤버 수가 같으면 생성일 기준으로 정렬
                    return g2.getCreatedAt().compareTo(g1.getCreatedAt());
                }
                return memberComparison;
            })
            .limit(3) // 상위 3개의 그룹만 가져오기
            .collect(Collectors.toList());
        
        // 모델에 'premiumPicks' 이름으로 DTO 리스트 추가
        model.addAttribute("premiumPicks", groupDTOs);
    }

    
}
