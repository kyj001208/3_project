package com.project.memmem.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.memmem.domain.dto.group.GroupListDTO;
import com.project.memmem.domain.dto.group.GroupSaveDTO;
import com.project.memmem.domain.entity.Category;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.security.CustomUserDetails;
import com.project.memmem.service.group.GroupService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GroupController {

	private final GroupService groupservice;


	@GetMapping("/group-detail/{id}")
	public String groupDetail(@PathVariable("id") Long groupId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		List<GroupListDTO> groups = groupservice.getGroupsByGroupId(groupId);
		 boolean isMember = groupservice.isUserMemberOfGroup(userDetails.getUserId(), groupId); // 사용자 가입 여부 확인
		model.addAttribute("groups", groups);
		model.addAttribute("isMember", isMember); // 플래그 추가

		return "views/group/group";
	}

	// 그룹생성하기
	@PostMapping("/groupSave")
	public String groupSave(@AuthenticationPrincipal CustomUserDetails userDetails,GroupSaveDTO dto) {
		groupservice.groupSaveProcess(userDetails.getUserId(), dto);
		return "redirect:/";
	}
	//이미지 저장
	@PostMapping("/uploadImage")
	@ResponseBody
	public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		return groupservice.s3TempUpload(file);
	}
	
	
	// 그룹 가입하기
    @PostMapping("/join-group/{id}")
    public String joinGroup(@PathVariable("id") Long groupId, @AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
    	 try {
    	        groupservice.joinGroup(userDetails.getUserId(), groupId);
    	        redirectAttributes.addFlashAttribute("message", "그룹에 성공적으로 가입되었습니다!");
    	    } catch (IllegalStateException e) {
    	        redirectAttributes.addFlashAttribute("errorMessage", "이미 이 그룹의 멤버입니다.");
    	    }
    	    return "redirect:/group-detail/" + groupId;
    }
	

	@GetMapping("/create-group")
	public String createGroup(Model model) {
		// ENUM의 모든 값 가져오기
		List<Category> categories = Arrays.asList(Category.values());
		model.addAttribute("categories", categories);

		return "views/group/create-group";
	}

}
