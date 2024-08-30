package com.project.memmem.service.impl.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.project.memmem.domain.dto.group.GroupSaveDTO;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.ImageEntity;
import com.project.memmem.domain.repository.group.GroupEntityRepository;
import com.project.memmem.domain.repository.image.ImageEntityRepository;
import com.project.memmem.service.group.GroupService;
import com.project.memmem.utils.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServiceProcess implements GroupService{

	private final GroupEntityRepository groupRepository;
	private final ImageEntityRepository imageRepository;
	private final FileUploadUtil fileUploadUtil;
	
	@Override
	public void groupSaveProcess(GroupSaveDTO dto) {
		// 메인 이미지 처리
        if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
            List<String> mainImageKeys = new ArrayList<>();
            mainImageKeys.add(dto.getMainImageBucketKey());
            List<String> mainImageUrls = fileUploadUtil.s3TempToImages(mainImageKeys);
            if (!mainImageUrls.isEmpty()) {
                dto.setMainImageBucketKey(mainImageUrls.get(0));
            }
        }
        
        GroupEntity groupEntity=groupRepository.save(dto.toGroupEntity());
        groupEntity = groupRepository.save(groupEntity);
        
        //이미지 정보 저장
        saveImages(groupEntity, dto);
	}

	private void saveImages(GroupEntity groups, GroupSaveDTO dto) {
		// 메인 이미지 저장
        if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
            ImageEntity mainImage = ImageEntity.builder()
            	.groups(groups)
                .imageUrl(dto.getMainImageBucketKey()) // 전체 URL 저장
                .imageType(ImageEntity.ImageType.GROUP)
                .build();
            imageRepository.save(mainImage);
        }
	}

	@Override
	public Map<String, String> s3TempUpload(MultipartFile file) throws IOException {
		return fileUploadUtil.s3TempUpload(file);
	}

}
