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
import com.project.memmem.domain.repository.ReviewRepository;
import com.project.memmem.domain.repository.group.GroupEntityRepository;
import com.project.memmem.service.IndexService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndexServiceProcess implements IndexService {
	
	private final GroupEntityRepository groupRepository;
	private final ReviewRepository reviewRepository;
    private final String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/jyj.img.host/";
    
    @Value("${spring.cloud.aws.s3.host}") 
    private String imgHost;
    
    @Override
    public void groupsList(Model model) {
    	List<GroupEntity> groups = groupRepository.findAllByOrderByCreatedAtDesc();
        List<GroupDTO> groupDTOs = groups.stream()
            .map(group -> group.toGroupDTO(baseUrl))
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

}
