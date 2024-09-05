package com.project.memmem.controller;

import java.util.HashMap;
import java.util.Map;

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
    public @ResponseBody Map<String, String> signup(
            SignupDTO dto,
            BindingResult bindingResult,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        Map<String, String> response = new HashMap<>();
        
        if (bindingResult.hasErrors()) {
            response.put("status", "error");
            response.put("message", "Validation errors");
            return response;
        }

        try {
            userService.saveUser(dto, pe);
            response.put("status", "success");
            return response;
        } catch (IllegalStateException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return response;
        }
    }
}
