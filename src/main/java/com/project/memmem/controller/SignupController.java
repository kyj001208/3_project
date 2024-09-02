package com.project.memmem.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.project.memmem.domain.dto.SignupDTO;
import com.project.memmem.service.impl.UserService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class SignupController {
	
	private final UserService userService;
	private final PasswordEncoder pe;
	
	@GetMapping("/signup")
	public String signupPage(Model model) {
		model.addAttribute("signupDTO", new SignupDTO());
		return "views/login/signup";
	}
	@PostMapping("/signup")
	public String postMethodName(SignupDTO dto, 
								BindingResult bindingResult, Model model) {
		if(bindingResult.hasErrors()) {
			return "views/login/signup";
		}
		try {
			userService.saveUser(dto, pe);
		}catch (IllegalStateException e) {
			/*model.addAttribute("errorMessage",e.getMessage());*/
			return "views/login/signup";
		}
		return "redirect:/";
	}
}
