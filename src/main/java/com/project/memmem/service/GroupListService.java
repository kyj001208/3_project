package com.project.memmem.service;

import org.springframework.data.domain.Page;

import com.project.memmem.domain.dto.group.GroupDTO;
import com.project.memmem.domain.entity.Category;

public interface GroupListService {

	Page<GroupDTO> getGroupsPage(int page, int size, Category category);
}
