package com.project.memmem.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.memmem.service.BlockService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	/*
	 * @GetMapping("/block") public String block() { return "/views/block/list"; }
	 */

	@PostMapping("/blockUser") public String blockUser(@RequestParam Long
	  blockerId, @RequestParam Long blockedId, RedirectAttributes
	  redirectAttributes) { blockService.blockUser(blockerId, blockedId); return
	  "redirect:/views/review/review_main"; // 리디렉션할 페이지로 설정 
	  }
	  
	  // 사용자 차단 목록을 표시할 페이지

	@GetMapping("/block")
	public String getBlockedUsers(@RequestParam("userId") Long userId, Model model) {
		model.addAttribute("currentUserId", userId);
		model.addAttribute("blockedUsers", blockService.getBlockedUsers(userId));
		return "/views/block/list";
	}

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

}
