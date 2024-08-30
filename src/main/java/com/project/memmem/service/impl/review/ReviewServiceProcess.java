package com.project.memmem.service.impl.review;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final ImageEntityRepository imageRepository;
    private final FileUploadUtils fileUploadUtil;

    @Transactional
    @Override
    public void reviewSaveProcess(ReviewSaveDTO dto) {
        // 디버깅 로그: DTO 값 출력
        System.out.println("ReviewSaveDTO: " + dto);

        // 메인 이미지 처리
        if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
            List<String> mainImageKeys = new ArrayList<>();
            mainImageKeys.add(dto.getMainImageBucketKey());

            ImgUploadDTO uploadDTO = fileUploadUtil.s3TempToImages(mainImageKeys, ImageEntity.ImageType.REVIEW);
            System.out.println("ImgUploadDTO: " + uploadDTO);
            List<String> mainImageUrls = uploadDTO.getUploadUrls();

            System.out.println("Main image URLs: " + mainImageUrls);

            if (!mainImageUrls.isEmpty()) {
                dto.setMainImageBucketKey(mainImageUrls.get(0));
                System.out.println("Updated MainImageBucketKey: " + dto.getMainImageBucketKey());
            } else {
                System.out.println("No image URLs returned from s3TempToImages");
            }
        } else {
            System.out.println("Initial MainImageBucketKey: " + dto.getMainImageBucketKey());
        }

        dto.setCreatedAt(LocalDateTime.now());
        ReviewEntity reviewEntity = reviewRepository.save(dto.toReviewEntity());

        // 디버깅용 로그
        System.out.println("Review saved: " + reviewEntity);

        // 이미지 정보 저장
        saveImages(reviewEntity, dto);
    }

    @Transactional
    private void saveImages(ReviewEntity reviewEntity, ReviewSaveDTO dto) {
        try {
            if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
                ImageEntity mainImage = ImageEntity.builder()
                    .review(reviewEntity)
                    .imageUrl(dto.getMainImageBucketKey())
                    .imageType(ImageEntity.ImageType.REVIEW)
                    .build();

                System.out.println("Saving ImageEntity: " + mainImage);
                ImageEntity savedImage = imageRepository.save(mainImage);
                System.out.println("Saved ImageEntity: " + savedImage);
            } else {
                System.out.println("MainImageBucketKey is null or empty. Skipping image save.");
            }
        } catch (Exception e) {
            System.err.println("Error saving image: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save image", e);
        }
    }

    @Override
    public Map<String, String> s3TempUpload(MultipartFile file) throws IOException {
        return fileUploadUtil.s3TempUpload(file);
    }
}
