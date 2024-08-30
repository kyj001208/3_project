package com.project.memmem.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.memmem.service.GroupListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GroupListController {
	
	private final GroupListService service;
	
	@GetMapping("/group-list")
	public String groupList() {
		return "views/group/list";
	}
	
	@GetMapping("/api/group-list")
    @ResponseBody
    public List<Map<String, Object>> getGroups(@RequestParam("page") int page, @RequestParam("size") int size) {
        // 서비스에서 페이지 번호와 크기를 기반으로 데이터를 가져옵니다.
        return service.getGroups(page, size);
    }
}
