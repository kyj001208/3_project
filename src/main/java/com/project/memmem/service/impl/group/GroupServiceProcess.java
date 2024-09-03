package com.project.memmem.service.impl.group;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.group.GroupListDTO;
import com.project.memmem.domain.dto.group.GroupSaveDTO;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.GroupMemberShipEntity;
import com.project.memmem.domain.entity.ImageEntity;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.GroupMemberShipEntityRepository;
import com.project.memmem.domain.repository.ImageEntityRepository;
import com.project.memmem.domain.repository.UserEntityRepository;
import com.project.memmem.domain.repository.group.GroupEntityRepository;

import com.project.memmem.service.group.GroupService;
import com.project.memmem.utils.FileUploadUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServiceProcess implements GroupService {

	private final GroupEntityRepository groupRepository;
	private final ImageEntityRepository imageRepository;
	private final FileUploadUtil fileUploadUtil;
	private final UserEntityRepository userRepository;
	private final GroupMemberShipEntityRepository groupMemberShipRepository;

	@Override
	public void groupSaveProcess(long userId, GroupSaveDTO dto) {
		UserEntity user = userRepository.findById(userId).orElseThrow();

		// 메인 이미지 처리
		if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
			List<String> mainImageKeys = new ArrayList<>();
			mainImageKeys.add(dto.getMainImageBucketKey());
			List<String> mainImageUrls = fileUploadUtil.s3TempToImages(mainImageKeys);
			if (!mainImageUrls.isEmpty()) {
				dto.setMainImageBucketKey(mainImageUrls.get(0));
			}
		}

		// GroupEntity 생성 및 저장
		GroupEntity groupEntity = dto.toGroupEntity(user); // 수정: UserEntity user를 전달하여 creator 필드 설정
		groupEntity = groupRepository.save(groupEntity);

		// 이미지 정보 저장
		saveImages(groupEntity, dto);

		// 그룹 생성자를 멤버로 추가
		addCreatorToGroup(groupEntity, user);
	}

	private void saveImages(GroupEntity groups, GroupSaveDTO dto) {
		// 메인 이미지 저장
		if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
			ImageEntity mainImage = ImageEntity.builder().groups(groups).imageUrl(dto.getMainImageBucketKey()) // 전체 URL
																												// 저장
					.imageType(ImageEntity.ImageType.GROUP).build();
			imageRepository.save(mainImage);
		}
	}

	private void addCreatorToGroup(GroupEntity group, UserEntity creator) {
		GroupMemberShipEntity creatorMembership = GroupMemberShipEntity.builder().user(creator).group(group)
				.role(GroupMemberShipEntity.Role.ROLE_CREATOR).joinedAt(LocalDateTime.now()).build();

		groupMemberShipRepository.save(creatorMembership);
	}

	@Override
	public Map<String, String> s3TempUpload(MultipartFile file) throws IOException {
		return fileUploadUtil.s3TempUpload(file);
	}

	@Override
	// 그룹 목록을 가져오는 메서드
	public List<GroupListDTO> getGroupsByGroupId(Long groupId) {
		// S3 또는 다른 이미지 저장소의 기본 URL
		String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/jyj.img.host/";
		return groupRepository.findById(groupId)
				.stream()
				.map(group -> group.toGroupListDTO(baseUrl))
				.collect(Collectors.toList());
	}

	@Transactional
	@Override
	public void joinGroup(long userId, Long groupId) {
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
		GroupEntity group = groupRepository.findById(groupId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

		// 사용자가 이미 그룹의 멤버인지 확인
		boolean isAlreadyMember = groupMemberShipRepository.existsByUserAndGroup(user, group);
		if (isAlreadyMember) {
			throw new IllegalStateException("User is already a member of this group.");
		}

		// 그룹에 사용자 추가
		GroupMemberShipEntity newMember = GroupMemberShipEntity.builder().user(user).group(group)
				.role(GroupMemberShipEntity.Role.ROLE_MEMBER).joinedAt(LocalDateTime.now()).build();

		groupMemberShipRepository.save(newMember);
	}

	@Override
	public boolean isUserMemberOfGroup(long userId, Long groupId) {
		return groupMemberShipRepository.existsByUserUserIdAndGroup_Id(userId, groupId);
	}
	
	 // 사용자가 그룹의 생성자인지 확인하는 메서드 추가
    @Override
    public boolean isUserCreatorOfGroup(long userId, Long groupId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        GroupMemberShipEntity membership = groupMemberShipRepository.findByUserUserIdAndGroup_Id(userId, groupId);
        return membership != null && membership.getRole() == GroupMemberShipEntity.Role.ROLE_CREATOR;
    }

	@Override
	public void updateGroup(Long id, GroupSaveDTO groupSaveDTO) {
		groupRepository.findById(id).orElseThrow().update(groupSaveDTO);
		
	}

	@Override
	public GroupEntity findGroupById(Long id) {
	    return groupRepository.findById(id).orElseThrow();
	}
}
