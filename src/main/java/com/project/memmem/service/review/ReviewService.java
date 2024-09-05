package com.project.memmem.service.review;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.img.ImageSaveDTO;
import com.project.memmem.domain.dto.review.ReviewSaveDTO;
import com.project.memmem.domain.dto.review.ReviewUpDateDTO;
import com.project.memmem.domain.entity.ReviewEntity;

public interface ReviewService {

	void reviewSaveProcess(ReviewSaveDTO dto, long userId);

	Map<String, String> s3TempUpload(MultipartFile file) throws IOException;

	void reviewListProcess(Model model, Long userId);

	void getReviewDetail(long reId, Model model);

	void reviewDelete(long reId, long userId);
  
	void reviewUpdateProcess(long reId, ReviewUpDateDTO dto, long userId, MultipartFile image);

	List<ReviewEntity> getReviewsExcludingBlockedUsers(Long userId);

}
