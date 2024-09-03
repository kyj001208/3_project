package com.project.memmem.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.img.ImageSaveDTO;
import com.project.memmem.domain.dto.review.ReviewSaveDTO;
import com.project.memmem.security.CustomUserDetails;
import com.project.memmem.security.MemmemUserDetails;
import com.project.memmem.service.review.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class ReviewController {

	private final ReviewService service;

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

	// 글 상세페이지
	@GetMapping("/mem/detail")
	public String reviewDetail() {
		return "views/review/review_detail";
	}

	// 글 내용 저장하기
	@PostMapping("/reviews")
	public String groupSave(ReviewSaveDTO dto, @AuthenticationPrincipal MemmemUserDetails user) {
		
		service.reviewSaveProcess(dto, user.getUserId());
		return "redirect:/";
	}

	@PostMapping("/upload-temp")
	@ResponseBody
	public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		return service.s3TempUpload(file);
	}

}