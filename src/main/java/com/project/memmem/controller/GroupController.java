package com.project.memmem.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.memmem.domain.dto.group.NoticeSaveDTO;
import com.project.memmem.domain.dto.group.GroupListDTO;
import com.project.memmem.domain.dto.group.GroupSaveDTO;
import com.project.memmem.domain.dto.group.GroupUpdateDTO;
import com.project.memmem.domain.entity.Category;
import com.project.memmem.domain.entity.NoticeEntity;
import com.project.memmem.security.MemmemUserDetails;
import com.project.memmem.service.group.GroupService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GroupController {

	private final GroupService groupService;

	// 1. 그룹 조회 및 생성 관련 기능
	@GetMapping("/group-detail/{id}")
	public String groupDetail(@PathVariable("id") Long groupId, Model model,
	        @AuthenticationPrincipal MemmemUserDetails userDetails) {

	    // 그룹 목록과 회원 정보를 가져옴
	    List<GroupListDTO> groups = groupService.getGroupsByGroupId(groupId);
	    boolean isMember = groupService.isUserMemberOfGroup(userDetails.getUserId(), groupId);
	    boolean isCreator = groupService.isUserCreatorOfGroup(userDetails.getUserId(), groupId);

	    List<NoticeEntity> notices = groupService.getNoticesByGroupId(groupId);
	    Map<String, Object> initials = groupService.getInitialsForUserAndCreator(groupId, userDetails.getUserId());
	
	    // 모델에 데이터를 추가
	    model.addAttribute("groups", groups);
	    model.addAttribute("isCreator", isCreator);
	    model.addAttribute("isMember", isMember);
	    model.addAttribute("userInitials", initials.get("userInitial"));
	    model.addAttribute("creatorInitial", initials.get("creatorInitial"));
	    model.addAttribute("notices", notices);
	   
	    return "views/group/group";
	}

	@GetMapping("/create-group")
	public String createGroup(Model model) {
		List<Category> categories = Arrays.asList(Category.values());
		model.addAttribute("categories", categories);
		return "views/group/create-group";
	}

	@PostMapping("/groupSave")
	public String groupSave(@AuthenticationPrincipal MemmemUserDetails userDetails, GroupSaveDTO dto) {
		groupService.groupSaveProcess(userDetails.getUserId(), dto);
		return "redirect:/";
	}

	// 2. 그룹 수정 및 삭제 관련 기능
	@GetMapping("/edit-group/{id}")
	public String showEditGroupForm(@PathVariable("id") Long id, Model model) {
		// 그룹 정보를 가져와서 DTO에 저장
		GroupUpdateDTO groupUpdateDTO = groupService.getGroupUpdateDTOById(id);
		List<Category> categories = Arrays.asList(Category.values());
		model.addAttribute("categories", categories);
		model.addAttribute("group", groupUpdateDTO);
		// 그룹 수정 폼을 렌더링할 뷰 이름을 반환
		return "views/group/edit-group-form";
	}

	@PutMapping("/update-group/{id}")
	public String updateGroup(@PathVariable("id") Long id, GroupUpdateDTO dto, BindingResult result,
			@RequestParam(value = "groupImage", required = false) MultipartFile groupImage) {

		// 서비스 호출을 통해 그룹 정보 업데이트
		groupService.updateProcess(id, dto, groupImage);

		return "redirect:/group-detail/" + id;
	}

	@DeleteMapping("/delete/{id}")
	public String deleteGroup(@PathVariable("id") Long id) {
		groupService.deleteGroup(id);
		return "views/group/group"; // 삭제 후 홈으로 리디렉션
	}

	// 3. 그룹 가입 및 탈퇴 관련 기능
	@PostMapping("/join-group/{id}")
	public String joinGroup(@PathVariable("id") Long groupId, @AuthenticationPrincipal MemmemUserDetails userDetails,
			RedirectAttributes redirectAttributes) {
		try {
			groupService.joinGroup(userDetails.getUserId(), groupId);
			redirectAttributes.addFlashAttribute("message", "그룹에 성공적으로 가입되었습니다!");
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "이미 이 그룹의 멤버입니다.");
		}
		return "redirect:/group-detail/" + groupId;
	}

	@PostMapping("/leave-group/{id}")
	public String leaveGroup(@PathVariable("id") Long groupId, @AuthenticationPrincipal MemmemUserDetails userDetails,
			RedirectAttributes redirectAttributes) {
		try {
			groupService.leaveGroup(userDetails.getUserId(), groupId);
			redirectAttributes.addFlashAttribute("message", "그룹에서 성공적으로 탈퇴하였습니다!");
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "그룹 탈퇴 중 문제가 발생했습니다.");
		}
		return "redirect:/group-detail/" + groupId;
	}

	// 4. 공지사항 관련 기능
	@PostMapping("/group/{groupId}/notice")
	public ResponseEntity<Map<String, Object>> createAnnouncement(@PathVariable("groupId") Long groupId,
	        @RequestBody NoticeSaveDTO NoticeDTO, @AuthenticationPrincipal MemmemUserDetails userDetails) {

	    boolean isCreator = groupService.isUserCreatorOfGroup(userDetails.getUserId(), groupId);

	    if (!isCreator) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "공지사항을 등록할 권한이 없습니다."));
	    }

	    try {
	        // 공지사항 생성 후, 생성된 공지사항 엔티티를 반환받음
	        NoticeEntity newNotice = groupService.addNoticeProcess(groupId, userDetails.getUserId(), NoticeDTO);
	        // 공지사항 ID를 포함한 성공 응답을 JSON 형식으로 반환
	        return ResponseEntity.ok(Map.of("message", "공지 사항이 성공적으로 추가되었습니다.", "noticeId", newNotice.getId()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "공지 사항 추가 실패"));
	    }
	}

	@DeleteMapping("/group/{groupId}/notice/{noticeId}")
	public ResponseEntity<Void> deleteNotice(@PathVariable("groupId") Long groupId, @PathVariable("noticeId") Long noticeId, 
	                                         @AuthenticationPrincipal MemmemUserDetails userDetails) {
	    boolean isCreator = groupService.isUserCreatorOfGroup(userDetails.getUserId(), groupId);

	    if (!isCreator) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	    }

	    try {
	        groupService.deleteNotice(groupId, noticeId);
	        return ResponseEntity.ok().build();
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	// 5. 파일 업로드 기능
	@PostMapping("/uploadImage")
	@ResponseBody
	public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		return groupService.s3TempUpload(file);
	}
}
