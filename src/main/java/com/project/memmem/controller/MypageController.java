package com.project.memmem.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.project.memmem.domain.dto.user.UserUpdateDTO;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.security.MemmemUserDetails;
import com.project.memmem.service.MypageService;
import com.project.memmem.service.group.GroupService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MypageController {
	
	private final MypageService mypageService; // 사용자 추가 정보를 가져오는 서비스
	private final GroupService groupService;

	@GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal MemmemUserDetails userDetails, Model model) {
        if (userDetails != null) {
        	addUserDetailsToModel(userDetails.getUserId(), model);
        }
        model.addAttribute("activeSection", "profile");
        return "views/mypage/mypage";
    }

    @GetMapping("/mypage/{section}")
    public String loadSection(@AuthenticationPrincipal MemmemUserDetails userDetails,
                              @PathVariable("section") String section, 
                              Model model) {
        if (userDetails != null) {
        	addUserDetailsToModel(userDetails.getUserId(), model);
        }
        model.addAttribute("activeSection", section);
        return "views/mypage/" + section + " :: content";
    }

    private void addUserDetailsToModel(long userId, Model model) {
        UserEntity userEntity = mypageService.getUserById(userId);
        model.addAttribute("email", userEntity.getEmail());
        model.addAttribute("name", userEntity.getName());
        model.addAttribute("nickName", userEntity.getNickName());
        model.addAttribute("userId", userEntity.getUserId());
        model.addAttribute("number", userEntity.getNumber());
        model.addAttribute("address", userEntity.getAddress());
        model.addAttribute("birthDate", userEntity.getBirthDate());
    }

	
	@GetMapping("/mypage/edit")
    public String showEditForm(@AuthenticationPrincipal MemmemUserDetails userDetails, Model model) {
        if (userDetails != null) {
            UserEntity userEntity = mypageService.getUserById(userDetails.getUserId());
            model.addAttribute("user", userEntity);
        }
        return "views/mypage/editProfile";
    }

	@PostMapping("/mypage/update")
	public String updateProfile(@AuthenticationPrincipal MemmemUserDetails userDetails,
	                            @ModelAttribute("user") UserUpdateDTO userUpdateDTO) {
	    if (userDetails != null) {
	        mypageService.updateUser(userDetails.getUserId(), userUpdateDTO);
	    }
	    return "redirect:/mypage";
	}

	//목록 조회
	@GetMapping("/mypage/myGroup")
	private String mypageGroupList(@AuthenticationPrincipal MemmemUserDetails userDetails, Model model) {
        System.out.println("Processing group list for user ID: " + userDetails.getUserId());
        try {
            mypageService.listProcess(userDetails.getUserId(), model);
            List<?> joinedGroups = (List<?>) model.getAttribute("joinedGroups");
            List<?> createdGroups = (List<?>) model.getAttribute("createdGroups");
            System.out.println("Joined Groups Size: " + (joinedGroups != null ? joinedGroups.size() : "null"));
            System.out.println("Created Groups Size: " + (createdGroups != null ? createdGroups.size() : "null"));
        } catch (Exception e) {
            System.err.println("Error in mypageGroupList: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "모임 목록을 불러오는 중 오류가 발생했습니다.");
        }
        return "views/mypage/mygroup :: content";
    }
	
	@GetMapping("/mypage/group-detail/{id}")
    public String getGroupDetail(@PathVariable("id") Long groupId, Model model) {
        GroupEntity group = groupService.findGroupById(groupId);
        model.addAttribute("group", group);
        return "views/group-detail";
    }

	@GetMapping("hidden")
	public String heddin() {
		return "/views/mypage/hidden";
	}

}
