package com.project.memmem.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.project.memmem.domain.dto.group.GroupDTO;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.repository.group.GroupEntityRepository;
import com.project.memmem.service.IndexService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndexServiceProcess implements IndexService {
	
	private final GroupEntityRepository groupRepository;
	
	
	@Override
	public void groupsList(Model model) {
	    List<GroupEntity> groups = groupRepository.findAll();

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YY/MM/dd"); // 원하는 포맷

	    List<GroupDTO> groupDTOs = groups.stream()
	        .map(group -> {
	            GroupDTO dto = new GroupDTO();
	            dto.setId(group.getId());
	            dto.setGroupName(group.getGroupName());
	            dto.setGreeting(group.getGreeting());
	            dto.setCreatedAt(group.getCreatedAt().format(formatter)); // 포맷된 날짜
	            dto.setCategory(group.getCategory());
	            return dto;
	        })
	        .collect(Collectors.toList());

	    model.addAttribute("groups", groupDTOs);
	}



}
