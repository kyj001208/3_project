package com.project.memmem.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
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

	@Override
	public void groupsList(Category category, Model model) {
	    List<GroupEntity> groups;
	    if (category == null) {
	        groups = groupRepository.findAll();
	    } else {
	        groups = groupRepository.findByCategory(category);
	    }

	    List<GroupDTO> groupDTOs = groups.stream()
	        .map(group -> {
	            GroupDTO dto = new GroupDTO();
	            dto.setId(group.getId());
	            dto.setGroupName(group.getGroupName());
	            dto.setGreeting(group.getGreeting());
	            dto.setCreatedAt(group.getCreatedAt().format(DateTimeFormatter.ofPattern("YY/MM/dd")));
	            dto.setCategory(group.getCategory());
	            return dto;
	        })
	        .collect(Collectors.toList());

	    model.addAttribute("groups", groupDTOs);
	}

	@Override
	public Page<GroupDTO> Scroll(Category category, Pageable pageable) {
	    Page<GroupEntity> groupEntities;

	    if (category != null) {
	        groupEntities = groupRepository.findByCategory(category, pageable);
	    } else {
	        groupEntities = groupRepository.findAll(pageable);
	    }

	    List<GroupDTO> groupDTOs = groupEntities.getContent().stream()
	        .map(group -> {
	            GroupDTO dto = new GroupDTO();
	            dto.setId(group.getId());
	            dto.setGroupName(group.getGroupName());
	            dto.setGreeting(group.getGreeting());
	            dto.setCreatedAt(group.getCreatedAt().format(DateTimeFormatter.ofPattern("YY/MM/dd")));
	            dto.setCategory(group.getCategory());
	            return dto;
	        })
	        .collect(Collectors.toList());

	    return new PageImpl<>(groupDTOs, pageable, groupEntities.getTotalElements());
	}

}
