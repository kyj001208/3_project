package com.project.memmem.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String signup(SignupDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Validation errors");
            return "redirect:/signup"; // Validation errors 발생 시 회원가입 페이지로 리디렉션
        }

        try {
            userService.saveUser(dto, pe);
            redirectAttributes.addFlashAttribute("status", "success");
            return "redirect:/login"; // 회원가입 성공 시 로그인 페이지로 리디렉션
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/signup"; // 오류 발생 시 회원가입 페이지로 리디렉션
        }
    }
}
