package com.project.memmem.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MypageController {
	
	private final MypageService mypageService; // 사용자 추가 정보를 가져오는 서비스
	private final GroupService groupService;
	private final UserDetailsService userDetailsService;

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
                                @ModelAttribute("user") UserUpdateDTO userUpdateDTO,
                                HttpSession session) {
        if (userDetails != null) {
            // 사용자 정보 업데이트
            mypageService.updateUser(userDetails.getUserId(), userUpdateDTO);

            // 업데이트된 사용자 정보로 새로운 UserDetails 객체 생성
            UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(userDetails.getUsername());

            //새로운 Authentication 객체 생성 및 SecurityContext에 설정
            Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            // 세션 갱신
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }
        return "redirect:/mypage";
    }

	//목록 조회
	@GetMapping("/mypage/myGroup")
	private String mypageGroupList(@AuthenticationPrincipal MemmemUserDetails userDetails, Model model) {
            mypageService.listProcess(userDetails.getUserId(), model);
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
