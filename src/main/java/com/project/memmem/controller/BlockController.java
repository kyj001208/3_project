package com.project.memmem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.memmem.controller.BlockController.ErrorResponse;
import com.project.memmem.controller.BlockController.SuccessResponse;
import com.project.memmem.domain.dto.block.BlockDTO;
import com.project.memmem.security.MemmemUserDetails;
import com.project.memmem.service.BlockService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	// 사용자 차단 목록을 표시할 페이지

	@GetMapping("/block")
	public String getBlockedUsers(@AuthenticationPrincipal MemmemUserDetails user, Model model) {
		long userId = user.getUserId();
		List<BlockDTO> blockedUsers = blockService.getBlockedUsers(userId);
		model.addAttribute("blockedUsers", blockedUsers);
		return "/views/block/list";
	}

	@PostMapping("/blockUser")
	@ResponseBody
	public ResponseEntity<?> blockUser(@RequestParam("blockedId") Long blockedId,
			@AuthenticationPrincipal MemmemUserDetails user) {

		Long blockerId = user.getUserId();

		try {
			blockService.blockUser(blockerId, blockedId);
			return ResponseEntity.ok().body(new SuccessResponse("차단 완료!"));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse("문제가 발생했습니다. 다시 시도해 주세요."));
		}
	}

	static class SuccessResponse {
		private String message;

		public SuccessResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	static class ErrorResponse {
		private String message;

		public ErrorResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
	
	@DeleteMapping("/unblockUser/{id}")
	public String unblock(@PathVariable("id") long id) {
		blockService.unblockProcess(id);
		return "redirect:/mypage";
	}

}
