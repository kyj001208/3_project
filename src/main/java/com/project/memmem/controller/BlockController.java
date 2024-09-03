package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.memmem.service.BlockService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BlockController {


	@GetMapping("/block")
	public String block() {
		return "/views/block/list";
	}

}
