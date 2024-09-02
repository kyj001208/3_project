package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.project.memmem.service.IndexService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class IndexController {
	
	private final IndexService indexservice;
	
	@GetMapping("/")
    public String index(Model model) {
		indexservice.groupsList(model);
        return "index";
    }
	
	
}