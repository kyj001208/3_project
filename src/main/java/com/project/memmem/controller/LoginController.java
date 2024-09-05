package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	
	@GetMapping("/login")
	public String login() {
		return "views/login/login";
	}
	@GetMapping("/login/error")
	public String loginFail(Model model) {
		model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호는 확인해주세요.");
		return "views/login/login";
	}
	
}
