package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class GroupController {
	
	@GetMapping("/group-list")
	public String groupList() {
		return "views/group/list";
	}
	
	@GetMapping("/group-detail")
	public String groupDetail() {
		return "views/group/group";
	}
	
	@GetMapping("/create-group")
	public String createGroup() {
		return "views/group/create-group";
	}
	
}
