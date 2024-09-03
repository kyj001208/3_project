package com.project.memmem.service.impl.review;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.img.ImgUploadDTO;
import com.project.memmem.domain.dto.review.ReviewSaveDTO;
import com.project.memmem.domain.entity.ImageEntity;
import com.project.memmem.domain.entity.ReviewEntity;
import com.project.memmem.domain.repository.ImageEntityRepository;
import com.project.memmem.domain.repository.ReviewRepository;
import com.project.memmem.service.review.ReviewService;
import com.project.memmem.util.FileUploadUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewServiceProcess implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final FileUploadUtils fileUploadUtil;

	@Value("${spring.cloud.aws.s3.host}")
	private String imgHost;

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

	@Override
	public Map<String, String> s3TempUpload(MultipartFile file) throws IOException {
		return fileUploadUtil.s3TempUpload(file);
	}
}
