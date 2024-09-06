package com.project.memmem.service.impl.group;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.group.GroupListDTO;
import com.project.memmem.domain.dto.group.GroupSaveDTO;
import com.project.memmem.domain.dto.group.GroupUpdateDTO;
import com.project.memmem.domain.dto.group.NoticeSaveDTO;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.domain.entity.GroupMemberShipEntity;
import com.project.memmem.domain.entity.ImageEntity;
import com.project.memmem.domain.entity.NoticeEntity;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.GroupMemberShipEntityRepository;
import com.project.memmem.domain.repository.ImageEntityRepository;
import com.project.memmem.domain.repository.UserEntityRepository;
import com.project.memmem.domain.repository.group.GroupEntityRepository;
import com.project.memmem.domain.repository.group.NoticeEntityRepository;
import com.project.memmem.service.group.GroupService;
import com.project.memmem.utils.FileUploadUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServiceProcess implements GroupService {

	// 의존성 주입
	private final GroupEntityRepository groupRepository;
	private final ImageEntityRepository imageRepository;
	private final FileUploadUtil fileUploadUtil;
	private final UserEntityRepository userRepository;
	private final GroupMemberShipEntityRepository groupMemberShipRepository;
	private final String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/jyj.img.host/";
	private final NoticeEntityRepository noticeRepository;

	// 1. 그룹 생성 및 저장 관련 메서드
	@Override
	public void groupSaveProcess(long userId, GroupSaveDTO dto) {
		UserEntity user = userRepository.findById(userId).orElseThrow();

		if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
			List<String> mainImageKeys = new ArrayList<>();
			mainImageKeys.add(dto.getMainImageBucketKey());
			List<String> mainImageUrls = fileUploadUtil.s3TempToImages(mainImageKeys);
			if (!mainImageUrls.isEmpty()) {
				dto.setMainImageBucketKey(mainImageUrls.get(0));
			}
		}

		GroupEntity groupEntity = dto.toGroupEntity(user);
		groupEntity = groupRepository.save(groupEntity);

		saveImages(groupEntity, dto);
		addCreatorToGroup(groupEntity, user);
	}

	private void saveImages(GroupEntity groups, GroupSaveDTO dto) {
		if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
			ImageEntity mainImage = ImageEntity.builder()
				.groups(groups)
				.imageUrl(dto.getMainImageBucketKey())
				.imageType(ImageEntity.ImageType.GROUP)
				.build();
			imageRepository.save(mainImage);
		}
	}

	private void addCreatorToGroup(GroupEntity group, UserEntity creator) {
		GroupMemberShipEntity creatorMembership = GroupMemberShipEntity.builder()
			.user(creator)
			.group(group)
			.role(GroupMemberShipEntity.Role.ROLE_CREATOR)
			.joinedAt(LocalDateTime.now())
			.build();

		groupMemberShipRepository.save(creatorMembership);
	}

	// 2. 그룹 조회 관련 메서드
	@Override
	public List<GroupListDTO> getGroupsByGroupId(Long groupId) {
		return groupRepository.findById(groupId)
			.stream()
			.map(group -> group.toGroupListDTO(baseUrl))
			.collect(Collectors.toList());
	}

	@Override
	public GroupEntity findGroupById(Long id) {
		return groupRepository.findById(id).orElseThrow();
	}

	@Override
	public GroupUpdateDTO getGroupUpdateDTOById(Long id) {
		GroupEntity groupEntity = groupRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + id));

		GroupUpdateDTO groupUpdateDTO = new GroupUpdateDTO();
		groupUpdateDTO.setId(groupEntity.getId());
		groupUpdateDTO.setGroupName(groupEntity.getGroupName());
		groupUpdateDTO.setGreeting(groupEntity.getGreeting());
		groupUpdateDTO.setDescription(groupEntity.getDescription());
		groupUpdateDTO.setCategory(groupEntity.getCategory());

		if (!groupEntity.getImages().isEmpty()) {
			String mainImageUrl = groupEntity.getImages().get(0).getImageUrl();
			groupUpdateDTO.setMainImageUrl(baseUrl + mainImageUrl);
		}

		return groupUpdateDTO;
	}

	@Override
	public Map<String, Object> getInitialsForUserAndCreator(Long groupId, long userId) {
		Map<String, Object> initials = new HashMap<>();
		GroupEntity group = groupRepository.findById(groupId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupId));

		UserEntity creator = group.getCreator();
		if (creator != null && creator.getNickName() != null && !creator.getNickName().isEmpty()) {
			initials.put("creatorInitial", creator.getNickName().substring(0, 1));
		} else {
			initials.put("creatorInitial", "");
		}

		List<String> memberInitials = new ArrayList<>();
		List<GroupMemberShipEntity> memberships = groupMemberShipRepository.findByGroupId(groupId);

		for (GroupMemberShipEntity membership : memberships) {
			UserEntity user = membership.getUser();
			if (user != null && user.getNickName() != null && !user.getNickName().isEmpty()) {
				if (!(user.getUserId() == (creator.getUserId()))) {
					memberInitials.add(user.getNickName().substring(0, 1));
				}
			}
		}

		initials.put("userInitial", memberInitials);

		return initials;
	}

	// 3. 그룹 수정 관련 메서드
	@Transactional
	@Override
	public void updateProcess(Long id, GroupUpdateDTO dto, MultipartFile groupImage) {
		GroupEntity group = groupRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

		group.update(dto);

		if (groupImage != null && !groupImage.isEmpty()) {
			try {
				Map<String, String> uploadResult = fileUploadUtil.s3TempUpload(groupImage);
				String bucketKey = uploadResult.get("bucketKey");

				List<String> finalImageUrls = fileUploadUtil.s3TempToImages(List.of(bucketKey));

				if (!finalImageUrls.isEmpty()) {
					List<ImageEntity> currentImages = group.getImages();
					imageRepository.deleteAll(currentImages);

					ImageEntity newImage = ImageEntity.builder()
						.groups(group)
						.imageUrl(finalImageUrls.get(0))
						.imageType(ImageEntity.ImageType.GROUP)
						.build();
					imageRepository.save(newImage);

					currentImages.clear();
					currentImages.add(newImage);
				}
			} catch (IOException e) {
				System.err.println("Image upload failed: " + e.getMessage());
			}
		}

		groupRepository.save(group);
	}

	// 4. 그룹 가입 및 탈퇴 관련 메서드
	@Transactional
	@Override
	public void joinGroup(long userId, Long groupId) {
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
		GroupEntity group = groupRepository.findById(groupId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

		boolean isAlreadyMember = groupMemberShipRepository.existsByUserAndGroup(user, group);
		if (isAlreadyMember) {
			throw new IllegalStateException("User is already a member of this group.");
		}

		GroupMemberShipEntity newMember = GroupMemberShipEntity.builder()
			.user(user)
			.group(group)
			.role(GroupMemberShipEntity.Role.ROLE_MEMBER)
			.joinedAt(LocalDateTime.now())
			.build();

		groupMemberShipRepository.save(newMember);
	}

	@Transactional
	@Override
	public void leaveGroup(long userId, Long groupId) {
		GroupMemberShipEntity membership = groupMemberShipRepository.findByUserUserIdAndGroup_Id(userId, groupId)
			.orElseThrow(() -> new IllegalArgumentException("User is not a member of the group."));

		if (membership.getRole() == GroupMemberShipEntity.Role.ROLE_CREATOR) {
			throw new IllegalStateException("The group creator cannot leave the group.");
		}

		groupMemberShipRepository.delete(membership);
	}

	// 5. 그룹 권한 확인 메서드
	@Override
	public boolean isUserMemberOfGroup(long userId, Long groupId) {
		return groupMemberShipRepository.existsByUserUserIdAndGroup_Id(userId, groupId);
	}

	@Override
	public boolean isUserCreatorOfGroup(long userId, Long groupId) {
		GroupEntity group = groupRepository.findById(groupId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

		Optional<GroupMemberShipEntity> membership = groupMemberShipRepository.findByUserUserIdAndGroup_Id(userId, groupId);

		return membership.isPresent() && membership.get().getRole() == GroupMemberShipEntity.Role.ROLE_CREATOR;
	}

	// 6. 공지사항 관련 메서드
	@Transactional
	@Override
	public NoticeEntity addNoticeProcess(Long groupId, long userId, NoticeSaveDTO noticeDTO) {
		GroupEntity group = groupRepository.findById(groupId)
			.orElseThrow(() -> new IllegalArgumentException("Group not found"));

		GroupMemberShipEntity membership = groupMemberShipRepository.findByUserUserIdAndGroup_Id(userId, groupId)
			.orElseThrow(() -> new IllegalArgumentException("User is not a member of the group"));

		NoticeEntity notice = noticeDTO.toEntity(group);
		group.addNotice(notice);

		return noticeRepository.save(notice);
	}

	@Override
	public List<NoticeEntity> getNoticesByGroupId(Long groupId) {
		return noticeRepository.findByGroupId(groupId);
	}

	@Transactional
	public void deleteNotice(Long groupId, Long noticeId) {
		NoticeEntity notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new IllegalArgumentException("Notice not found"));

		if (!notice.getGroup().getId().equals(groupId)) {
			throw new IllegalStateException("This notice does not belong to the group");
		}

		noticeRepository.delete(notice);
	}

	// 7. 이미지 업로드 관련 메서드
	@Override
	public Map<String, String> s3TempUpload(MultipartFile file) throws IOException {
		return fileUploadUtil.s3TempUpload(file);
	}

	// 8. 그룹 삭제 메서드
	@Transactional
	@Override
	public void deleteGroup(Long id) {
		GroupEntity group = groupRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

		imageRepository.deleteAll(group.getImages());
		groupRepository.delete(group);
	}
}

