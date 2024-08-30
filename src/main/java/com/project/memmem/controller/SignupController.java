package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.memmem.service.UserService;

import lombok.RequiredArgsConstructor;

import com.project.memmem.domain.dto.SaveUserDTO;

@RequiredArgsConstructor
@Controller
public class SignupController {
	
	private final UserService userService;
	
	@GetMapping("/signup")
	public String signup() {
		return "views/login/signup";
	}

	@PostMapping("/signup")
	public String signup(@ModelAttribute SaveUserDTO dto, RedirectAttributes redirectAttributes) {
		if (userService.isEmailDuplicate(dto.getEmail())) {
			redirectAttributes.addFlashAttribute("errorMessage", "이미 사용 중인 이메일입니다.");
			return "redirect:/signup";
		}

		try {
			userService.signupProcess(dto);
			return "redirect:/login"; // 회원가입 성공 시 리다이렉트할 경로
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
			return "redirect:/signup";
		}
	}
}
