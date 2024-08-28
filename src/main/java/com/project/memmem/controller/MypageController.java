package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageController {
	@GetMapping("/mypage")
	public String listPlaces() {
		return "views/commons/mypage/mypage";
	}

}
