package com.project.memmem.service.review;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.img.ImageSaveDTO;
import com.project.memmem.domain.dto.review.ReviewSaveDTO;

public interface ReviewService {

	

	void reviewSaveProcess(ReviewSaveDTO dto);

	Map<String, String> s3TempUpload(MultipartFile file) throws IOException;

}
