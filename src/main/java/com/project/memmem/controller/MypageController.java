package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    @GetMapping
    public String myPage(Model model) {
        model.addAttribute("activeSection", "profile");
        return "views/mypage/mypage";
    }

    @GetMapping("/{section}")
    public String loadSection(@PathVariable("section") String section, Model model) {
        model.addAttribute("activeSection", section);
        return "views/mypage/" + section + " :: content";
    }

}
