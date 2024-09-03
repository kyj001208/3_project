package com.project.memmem.service.impl;

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
    private final String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/jyj.img.host/";

    @Override
    public void groupsList(Model model) {
        List<GroupEntity> groups = groupRepository.findAll();
        List<GroupDTO> groupDTOs = groups.stream()
            .map(group -> group.toGroupDTO(baseUrl))
            .collect(Collectors.toList());
        model.addAttribute("groups", groupDTOs);
    }
}
