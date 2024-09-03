package com.project.memmem.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.img.ImageSaveDTO;
import com.project.memmem.domain.dto.review.ReviewSaveDTO;
import com.project.memmem.domain.entity.ReviewEntity;
import com.project.memmem.security.MemmemUserDetails;
import com.project.memmem.service.review.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class ReviewController {

	private final ReviewService service;

	// 글 등록하는 페이지
	@GetMapping("/mem/review-write")
	public String reviewWrite() {
		return "views/review/review_write";
	}

	

	// 글 내용 저장 및 이미지 저장
	@PostMapping("/reviews")
	public String groupSave(ReviewSaveDTO dto, @AuthenticationPrincipal MemmemUserDetails user) {

		service.reviewSaveProcess(dto, user.getUserId());
		return "redirect:/mem/review";
	}
	
	//이미지 업로드
	@PostMapping("/upload-temp")
	@ResponseBody
	public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		return service.s3TempUpload(file);
	}

	//메인페이지 저장된 값 뿌려주기
	@GetMapping("/mem/review")
	public String review(Model model){
		
		service.reviewListProcess(model);
		return "views/review/review_main";
	}
	
	
	@GetMapping("/mem/detail/{reId}")
	public String reviewDetail(@PathVariable("reId") long reId, Model model) {
	    service.getReviewDetail(reId, model);
	    return "views/review/review_detail";
	}
	

}