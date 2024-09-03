package com.project.memmem.service.impl.review;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.img.ImgUploadDTO;
import com.project.memmem.domain.dto.review.ReviewSaveDTO;
import com.project.memmem.domain.entity.ImageEntity;
import com.project.memmem.domain.entity.ReviewEntity;
import com.project.memmem.domain.repository.ImageEntityRepository;
import com.project.memmem.domain.repository.ReviewRepository;
import com.project.memmem.service.review.ReviewService;
import com.project.memmem.util.FileUploadUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewServiceProcess implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final FileUploadUtils fileUploadUtil;

	@Value("${spring.cloud.aws.s3.host}")
	private String imgHost;

	// 이미지 및 컨텐츠 저장
	@Transactional
	@Override
	public void reviewSaveProcess(ReviewSaveDTO dto, long userId) {
		// 디버깅 로그: DTO 값 출력
		System.out.println("ReviewSaveDTO: " + dto);
		String mainImageBucketKey = fileUploadUtil.s3TempToImage(dto.getMainImageBucketKey());
		// tempKey->uploadKey 변경됨
		dto.setMainImageBucketKey(mainImageBucketKey);

		reviewRepository.save(dto.toReviewEntity(userId));
	}

	// 이미지 업로드 서비스
	@Override
	public Map<String, String> s3TempUpload(MultipartFile file) throws IOException {
		return fileUploadUtil.s3TempUpload(file);
	}

	// 저장된 db값 메인화면에 뿌려주는것
	@Override
	public void reviewListProcess(Model model) {

		model.addAttribute("list", reviewRepository.findAll().stream()
				.map(review -> ReviewEntity.toListDTO(review, imgHost)).collect(Collectors.toList()));
	}

	// 상세 페이지 값 뿌려주기
	@Transactional
	public ReviewEntity getReviewById(long reId) {
		return reviewRepository.findById(reId).orElseThrow(() -> new EntityNotFoundException("Review not found"));
	}

	// 상세 페이지에 필요한 데이터를 가져오는 서비스 메서드
	public void getReviewDetail(long reId, Model model) {
		ReviewEntity review = getReviewById(reId);
		model.addAttribute("review", review);

		// 포맷된 시간 추가
		String formattedTime = formatTime(review.getCreatedAt());
		model.addAttribute("formattedTime", formattedTime);

		// 이미지 URL 추가
	    String imageUrl = review.getImageUrl(imgHost);
	    System.out.println("Image URL: " + imageUrl); // 로그로 출력
	    model.addAttribute("imageUrl", imageUrl);
	}

	// 시간 포맷터
	public String formatTime(LocalDateTime createdAt) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return createdAt.format(formatter);
	}
}
