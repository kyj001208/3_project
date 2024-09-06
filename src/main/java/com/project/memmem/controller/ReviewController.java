package com.project.memmem.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.img.ImageSaveDTO;
import com.project.memmem.domain.dto.review.ReviewListDTO;
import com.project.memmem.domain.dto.review.ReviewSaveDTO;
import com.project.memmem.domain.dto.review.ReviewUpDateDTO;
import com.project.memmem.domain.entity.ReviewEntity;
import com.project.memmem.security.MemmemUserDetails;
import com.project.memmem.service.review.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	// 이미지 업로드
	@PostMapping("/upload-temp")
	@ResponseBody
	public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		return service.s3TempUpload(file);
	}

	// 메인페이지 저장된 값 뿌려주기, 언니와 나의 합작
	@GetMapping("/mem/review")
	public String review(@AuthenticationPrincipal MemmemUserDetails user, Model model) {
		Long userId = user.getUserId();
		service.reviewListProcess(model, userId);
		return "views/review/review_main";
	}
	
	// 상세페이지 저장된 값 뿌려주기
	@GetMapping("/mem/detail/{reId}")
	public String reviewDetail(@PathVariable("reId") long reId, Model model, @AuthenticationPrincipal MemmemUserDetails user) {
		service.getReviewDetail(reId, model, user);
		return "views/review/review_detail";
	}

	
	//삭제하는 컨트롤러.
	@DeleteMapping("/mem/detail/{reId}")
	public String reviewDelete(@PathVariable("reId") long reId, @AuthenticationPrincipal MemmemUserDetails user) {
		
		service.reviewDelete(reId, user.getUserId());
		return "views/review/review_main";
	}

	
	///////////////////////////////////////////////////스탑
	

	//업데이트 하는 컨트롤러
	@PutMapping("/mem/detail/{reId}")
	public String reviewUpdate(@PathVariable("reId") long reId, @AuthenticationPrincipal MemmemUserDetails user,
	            ReviewUpDateDTO dto, @RequestParam(value = "image", required = false) MultipartFile image) {

	    // 로그인한 사용자 ID
	    long userId = user.getUserId();

	    // 서비스 호출을 통해 리뷰 수정
	    service.reviewUpdateProcess(reId, dto, userId, image);

	    return "redirect:/mem/detail/" + reId;
	}


}