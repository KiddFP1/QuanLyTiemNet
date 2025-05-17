package com.example.quanlytiemnet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.quanlytiemnet.models.User;
import com.example.quanlytiemnet.services.UserService;

import java.security.Principal;
import java.util.Collections;

@Controller
@RequestMapping("/member")
public class MemberHomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        // Tạo user mẫu
        User user = new User();
        user.setAccountBalance(0.0);
        user.setRemainingTime(0);
        model.addAttribute("user", user);

        // Dữ liệu mẫu để tránh lỗi Thymeleaf
        model.addAttribute("featuredProducts", Collections.emptyList());
        model.addAttribute("popularGameCards", Collections.emptyList());
        model.addAttribute("currentComputer", null);

        return "member/home";
    }
}
