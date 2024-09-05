package com.project.memmem.service;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import com.project.memmem.domain.dto.group.GroupDTO;
import com.project.memmem.domain.entity.Category;

public interface GroupListService {

	void groupsList(Category category, Model model);

	Page<GroupDTO> getGroupsPage(int page, int size, Category category);
}
