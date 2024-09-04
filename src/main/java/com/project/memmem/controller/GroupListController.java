package com.project.memmem.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.memmem.domain.dto.group.GroupDTO;
import com.project.memmem.domain.entity.Category;
import com.project.memmem.service.GroupListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GroupListController {

	private final GroupListService service;
	
	@GetMapping("/group-list")
	public String groupList(@RequestParam(value = "category", required = false) Category category, Model model) {
	    service.groupsList(category, model);
	    return "views/group/list";
	}
	
	@GetMapping("/api/groups")
	public ResponseEntity<List<GroupDTO>> getGroups(
	        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
	        @RequestParam(name = "size", required = false, defaultValue = "10") int size,
	        @RequestParam(name = "category", required = false) Category category) {
	    Page<GroupDTO> groupPage = service.getGroupsPage(page, size, category);
	    return ResponseEntity.ok(groupPage.getContent());
	}

	
}
