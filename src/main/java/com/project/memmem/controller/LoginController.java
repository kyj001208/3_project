package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	
	@GetMapping("/login")
	public String login() {
		return "views/login/login";
	}
}
