package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MypageController {

    @GetMapping("/mypage")
    public String myPage(Model model) {
        model.addAttribute("activeSection", "profile");
        return "views/mypage/mypage";
    }

    @GetMapping("/mypage/{section}")
    public String loadSection(@PathVariable("section") String section, Model model) {
        model.addAttribute("activeSection", section);
        return "views/mypage/" + section + " :: content";
    }
    
    @GetMapping("hidden")
    public String heddin() {
    	return "/views/mypage/hidden";
    }
    

}
