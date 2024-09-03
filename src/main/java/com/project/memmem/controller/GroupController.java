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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.memmem.domain.dto.group.GroupListDTO;
import com.project.memmem.domain.dto.group.GroupSaveDTO;
import com.project.memmem.domain.entity.Category;
import com.project.memmem.domain.entity.GroupEntity;
import com.project.memmem.security.MemmemUserDetails;
import com.project.memmem.service.group.GroupService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GroupController {

	private final GroupService groupService;


    @GetMapping("/group-detail/{id}")
    public String groupDetail(@PathVariable("id") Long groupId, Model model,
                              @AuthenticationPrincipal MemmemUserDetails userDetails) {
        List<GroupListDTO> groups = groupService.getGroupsByGroupId(groupId);
        boolean isMember = groupService.isUserMemberOfGroup(userDetails.getUserId(), groupId);
        boolean isCreator = groupService.isUserCreatorOfGroup(userDetails.getUserId(), groupId);
        model.addAttribute("groups", groups);
        model.addAttribute("isCreator", isCreator);
        model.addAttribute("isMember", isMember);

        return "views/group/group";
    }

    @PostMapping("/groupSave")
    public String groupSave(@AuthenticationPrincipal MemmemUserDetails userDetails, GroupSaveDTO dto) {
        groupService.groupSaveProcess(userDetails.getUserId(), dto);
        return "redirect:/";
    }

    @PostMapping("/uploadImage")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return groupService.s3TempUpload(file);
    }

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

    @GetMapping("/create-group")
    public String createGroup(Model model) {
        List<Category> categories = Arrays.asList(Category.values());
        model.addAttribute("categories", categories);
        return "views/group/create-group";
    }

    // 그룹 수정 페이지에 대한 기존 GET 요청은 제거합니다.
    
    @PostMapping("/edit-group/{id}")
    @ResponseBody
    public ResponseEntity<?> editGroup(@PathVariable("id") Long id, @RequestBody GroupSaveDTO groupSaveDTO,
                                       @AuthenticationPrincipal MemmemUserDetails userDetails) {
        try {
            groupService.updateGroup(id, groupSaveDTO);
            return ResponseEntity.ok("그룹이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("그룹 수정에 실패했습니다.");
        }
    }

    // 새로운 비동기 그룹 정보 요청
    @GetMapping("/group/{id}/info")
    @ResponseBody
    public ResponseEntity<GroupEntity> getGroupInfo(@PathVariable("id") Long id) {
        GroupEntity group = groupService.findGroupById(id);
        return ResponseEntity.ok(group);
    }
}