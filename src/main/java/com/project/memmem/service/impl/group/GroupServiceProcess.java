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
import com.project.memmem.domain.dto.group.GroupUpdateDTO;
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

    // 의존성 주입
    private final GroupEntityRepository groupRepository;
    private final ImageEntityRepository imageRepository;
    private final FileUploadUtil fileUploadUtil;
    private final UserEntityRepository userRepository;
    private final GroupMemberShipEntityRepository groupMemberShipRepository;
    private final String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/jyj.img.host/";

    /**
     * 그룹을 생성하는 메서드
     * @param userId - 생성자 ID
     * @param dto - 그룹 생성 데이터
     */
    @Override
    public void groupSaveProcess(long userId, GroupSaveDTO dto) {
        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userId).orElseThrow();

        // 메인 이미지 처리 로직
        if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
            List<String> mainImageKeys = new ArrayList<>();
            mainImageKeys.add(dto.getMainImageBucketKey());
            List<String> mainImageUrls = fileUploadUtil.s3TempToImages(mainImageKeys);
            if (!mainImageUrls.isEmpty()) {
                dto.setMainImageBucketKey(mainImageUrls.get(0)); // 업로드된 이미지 URL 설정
            }
        }

        // 그룹 엔티티 생성 및 저장
        GroupEntity groupEntity = dto.toGroupEntity(user); // 사용자 정보를 포함하여 그룹 엔티티 생성
        groupEntity = groupRepository.save(groupEntity);

        // 이미지 정보 저장
        saveImages(groupEntity, dto);

        // 그룹 생성자를 멤버로 추가
        addCreatorToGroup(groupEntity, user);
    }

    /**
     * 이미지 정보를 저장하는 메서드
     * @param groups - 그룹 엔티티
     * @param dto - 그룹 생성 데이터
     */
    private void saveImages(GroupEntity groups, GroupSaveDTO dto) {
        // 메인 이미지가 있는 경우 저장
        if (dto.getMainImageBucketKey() != null && !dto.getMainImageBucketKey().isEmpty()) {
            ImageEntity mainImage = ImageEntity.builder()
                .groups(groups)
                .imageUrl(dto.getMainImageBucketKey()) // 이미지 URL 저장
                .imageType(ImageEntity.ImageType.GROUP)
                .build();
            imageRepository.save(mainImage);
        }
    }

    /**
     * 그룹 생성자를 멤버로 추가하는 메서드
     * @param group - 그룹 엔티티
     * @param creator - 그룹 생성자(사용자 엔티티)
     */
    private void addCreatorToGroup(GroupEntity group, UserEntity creator) {
        // 생성자를 그룹 멤버십 엔티티에 추가
        GroupMemberShipEntity creatorMembership = GroupMemberShipEntity.builder()
            .user(creator)
            .group(group)
            .role(GroupMemberShipEntity.Role.ROLE_CREATOR)
            .joinedAt(LocalDateTime.now())
            .build();

        groupMemberShipRepository.save(creatorMembership);
    }

    /**
     * S3 버킷에 파일을 임시 업로드하는 메서드
     * @param file - 업로드할 파일
     * @return 업로드 결과 Map
     * @throws IOException
     */
    @Override
    public Map<String, String> s3TempUpload(MultipartFile file) throws IOException {
        return fileUploadUtil.s3TempUpload(file);
    }

    /**
     * 그룹 ID로 그룹 목록을 조회하는 메서드
     * @param groupId - 조회할 그룹 ID
     * @return 그룹 리스트 DTO
     */
    @Override
    public List<GroupListDTO> getGroupsByGroupId(Long groupId) {
        return groupRepository.findById(groupId).stream()
            .map(group -> group.toGroupListDTO(baseUrl)) // DTO로 변환
            .collect(Collectors.toList());
    }

    /**
     * 사용자를 그룹에 가입시키는 메서드
     * @param userId - 사용자 ID
     * @param groupId - 그룹 ID
     */
    @Transactional
    @Override
    public void joinGroup(long userId, Long groupId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        // 이미 그룹의 멤버인지 확인
        boolean isAlreadyMember = groupMemberShipRepository.existsByUserAndGroup(user, group);
        if (isAlreadyMember) {
            throw new IllegalStateException("User is already a member of this group.");
        }

        // 새로운 멤버로 추가
        GroupMemberShipEntity newMember = GroupMemberShipEntity.builder()
            .user(user)
            .group(group)
            .role(GroupMemberShipEntity.Role.ROLE_MEMBER)
            .joinedAt(LocalDateTime.now())
            .build();

        groupMemberShipRepository.save(newMember);
    }

    /**
     * 사용자가 그룹의 멤버인지 확인하는 메서드
     * @param userId - 사용자 ID
     * @param groupId - 그룹 ID
     * @return 멤버 여부
     */
    @Override
    public boolean isUserMemberOfGroup(long userId, Long groupId) {
        return groupMemberShipRepository.existsByUserUserIdAndGroup_Id(userId, groupId);
    }

    /**
     * 사용자가 그룹의 생성자인지 확인하는 메서드
     * @param userId - 사용자 ID
     * @param groupId - 그룹 ID
     * @return 생성자 여부
     */
    @Override
    public boolean isUserCreatorOfGroup(long userId, Long groupId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        GroupMemberShipEntity membership = groupMemberShipRepository.findByUserUserIdAndGroup_Id(userId, groupId);
        return membership != null && membership.getRole() == GroupMemberShipEntity.Role.ROLE_CREATOR;
    }

    /**
     * 그룹 ID로 그룹 엔티티를 조회하는 메서드
     * @param id - 그룹 ID
     * @return 그룹 엔티티
     */
    @Override
    public GroupEntity findGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow();
    }

    /**
     * 그룹 ID로 그룹 업데이트 DTO를 가져오는 메서드
     * @param id - 그룹 ID
     * @return 그룹 업데이트 DTO
     */
    @Override
    public GroupUpdateDTO getGroupUpdateDTOById(Long id) {
        GroupEntity groupEntity = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + id));

        // 그룹 엔티티 정보를 DTO로 변환
        GroupUpdateDTO groupUpdateDTO = new GroupUpdateDTO();
        groupUpdateDTO.setId(groupEntity.getId());
        groupUpdateDTO.setGroupName(groupEntity.getGroupName());
        groupUpdateDTO.setGreeting(groupEntity.getGreeting());
        groupUpdateDTO.setDescription(groupEntity.getDescription());
        groupUpdateDTO.setCategory(groupEntity.getCategory());

        if (!groupEntity.getImages().isEmpty()) {
            String mainImageUrl = groupEntity.getImages().get(0).getImageUrl();
            groupUpdateDTO.setMainImageUrl(baseUrl + mainImageUrl); // 절대 URL 설정
        }

        return groupUpdateDTO;
    }

    /**
     * 그룹을 업데이트하는 메서드
     * @param id - 그룹 ID
     * @param dto - 업데이트할 데이터
     * @param groupImage - 업로드된 그룹 이미지
     */
    @Transactional
    @Override
    public void updateProcess(Long id, GroupUpdateDTO dto, MultipartFile groupImage) {
        // 그룹 조회
        GroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        System.out.println("Updating group with ID: " + id);
        System.out.println("New group name: " + dto.getGroupName());
        System.out.println("New greeting: " + dto.getGreeting());

        // 그룹 정보를 업데이트
        group.update(dto);
        System.out.println("Group updated in memory");

        // 새로운 메인 이미지가 업로드된 경우
        if (groupImage != null && !groupImage.isEmpty()) {
            try {
                Map<String, String> uploadResult = fileUploadUtil.s3TempUpload(groupImage);
                String bucketKey = uploadResult.get("bucketKey");

                // S3 버킷에서 최종 이미지 URL을 가져옴
                List<String> finalImageUrls = fileUploadUtil.s3TempToImages(List.of(bucketKey));

                if (!finalImageUrls.isEmpty()) {
                    // 기존 이미지 삭제 후 새로운 이미지 추가
                    List<ImageEntity> currentImages = group.getImages();
                    imageRepository.deleteAll(currentImages);

                    // 새 이미지 엔티티 생성 및 저장
                    ImageEntity newImage = ImageEntity.builder()
                            .groups(group)
                            .imageUrl(finalImageUrls.get(0))
                            .imageType(ImageEntity.ImageType.GROUP)
                            .build();
                    imageRepository.save(newImage);

                    // 그룹 엔티티에 새로운 이미지 추가
                    currentImages.clear();
                    currentImages.add(newImage);

                    System.out.println("Images updated successfully");
                }
            } catch (IOException e) {
                System.err.println("Image upload failed: " + e.getMessage());
            }
        }

        // 업데이트된 그룹 정보를 저장
        groupRepository.save(group);
        System.out.println("Group saved to database");
    }

    /**
     * 그룹을 삭제하는 메서드
     * @param id - 삭제할 그룹 ID
     */
    @Transactional
    @Override
    public void deleteGroup(Long id) {
        GroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        // 그룹과 연관된 이미지 삭제
        imageRepository.deleteAll(group.getImages());

        // 그룹 삭제
        groupRepository.delete(group);
    }

}
