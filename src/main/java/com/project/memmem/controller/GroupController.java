package com.project.memmem.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.group.GroupSaveDTO;
import com.project.memmem.domain.entity.Category;
import com.project.memmem.service.group.GroupService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GroupController {

	private final GroupService groupservice;

	@GetMapping("/group-detail")
	public String groupDetail() {
		return "views/group/group";
	}

	// 그룹생성하기
	@PostMapping("/groupSave")
	public String groupSave(GroupSaveDTO dto) {
		groupservice.groupSaveProcess(dto);
		return "redirect:/";
	}
	//이미지 저장
	@PostMapping("/uploadImage")
	@ResponseBody
	public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		return groupservice.s3TempUpload(file);
	}
	
	

	@GetMapping("/create-group")
	public String createGroup(Model model) {
		// ENUM의 모든 값 가져오기
		List<Category> categories = Arrays.asList(Category.values());
		model.addAttribute("categories", categories);

		return "views/group/create-group";
	}

}
