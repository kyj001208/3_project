package com.project.memmem.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.memmem.domain.dto.SignupDTO;
import com.project.memmem.service.impl.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignupController {
    
    private final UserService userService;
    private final PasswordEncoder pe;
    
    @GetMapping
    public String signupPage(Model model) {
        model.addAttribute("signupDTO", new SignupDTO());
        return "views/login/signup";
    }

    @PostMapping
    public @ResponseBody String signup(
            SignupDTO dto, 
            BindingResult bindingResult, 
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        if (bindingResult.hasErrors()) {
            return "{\"status\":\"error\",\"message\":\"Validation errors\"}";
        }

        try {
            userService.saveUser(dto, pe);
            if ("XMLHttpRequest".equals(requestedWith)) {
                return "{\"status\":\"success\"}";
            } else {
                return "redirect:/";
            }
        } catch (IllegalStateException e) {
            // 로그를 남기거나 처리 필요
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }
}
