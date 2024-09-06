package com.project.memmem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

	/*
	 * @GetMapping("/block") public String getBlockedUsers(@AuthenticationPrincipal
	 * MemmemUserDetails user, Model model) { long userId = user.getUserId();
	 * List<BlockDTO> blockedUsers = blockService.getBlockedUsers(userId);
	 * model.addAttribute("blockedUsers", blockedUsers); return "/views/block/list";
	 * }
	 */
	@GetMapping("/block")
    public String getBlockedUsers(@AuthenticationPrincipal MemmemUserDetails user, Model model) {
        long userId = user.getUserId();
        blockService.getBlockedUsers(model, userId);
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

	/*
	 * @GetMapping("/block") public String block() { return "/views/block/list"; }
	 */

	/*
	 * @PostMapping("/blockUser") public String blockUser(@RequestParam("blockerId")
	 * Long blockerId, @RequestParam("blockedId") Long blockedId) {
	 * blockService.blockUser(blockerId, blockedId); return
	 * "redirect:/views/review/review_main"; }
	 */

	/*
	 * @PostMapping
	 * 
	 * @ResponseBody public ResponseEntity<?> blockUser(@RequestBody BlockRequest
	 * blockRequest) { try { blockService.blockUser(blockRequest.getBlockerId(),
	 * blockRequest.getBlockedId()); return ResponseEntity.ok().body(new
	 * SuccessResponse("차단 완료!")); } catch (Exception e) { return
	 * ResponseEntity.status(500).body(new
	 * ErrorResponse("문제가 발생했습니다. 다시 시도해 주세요.")); } }
	 */

	
	/*
	 * @PostMapping("/blockUser") public String blockUser(@RequestParam Long
	 * blockerId, @RequestParam Long blockedId, RedirectAttributes
	 * redirectAttributes) { blockService.blockUser(blockerId, blockedId); return
	 * "redirect:/views/review/review_main"; // 리디렉션할 페이지로 설정 }
	 * 
	 * @GetMapping("/getCurrentUserId") public @ResponseBody Long
	 * getCurrentUserId(Principal principal) { // 현재 사용자 ID를 반환합니다. return
	 * Long.parseLong(principal.getName()); }
	 */

	/*
	 * @PostMapping("/blockUser")
	 * 
	 * @ResponseBody public ResponseEntity<Map<String, Object>>
	 * blockUser(@RequestParam Long blockerId, @RequestParam Long blockedId) { try {
	 * blockService.blockUser(blockerId, blockedId); Map<String, Object> response =
	 * new HashMap<>(); response.put("success", true); response.put("message",
	 * "User blocked successfully"); return ResponseEntity.ok(response); } catch
	 * (Exception e) { Map<String, Object> response = new HashMap<>();
	 * response.put("success", false); response.put("message",
	 * "Failed to block user: " + e.getMessage()); return
	 * ResponseEntity.badRequest().body(response); } }
	 */

}
