package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReviewController {

	// 후기 모임 들어가는 단순 페이지 이동
	@GetMapping("/mem/review")
	public String review() {
		return "views/review/review_main";
	}

	// 글 등록하는 페이지
	@GetMapping("/mem/review-write")
	public String reviewWrite() {
		return "views/review/review_write";
	}

	// 글 등록하는 페이지
	@GetMapping("/mem/detail")
	public String reviewDetail() {
		return "views/review/review_detail";
	}

}
