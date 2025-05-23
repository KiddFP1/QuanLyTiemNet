package com.example.quanlytiemnet.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DefaultController {

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        log.info("Handling default routing for user: {}", authentication.getName());
        log.info("User authorities: {}", authentication.getAuthorities());

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))) {
            log.info("Routing admin/employee user to /admin/dashboard");
            return "redirect:/admin/dashboard";
        } else {
            log.info("Routing member user to /member/dashboard");
            return "redirect:/member/dashboard";
        }
    }

    // XÓA hoặc COMMENT ĐOẠN NÀY!
    // @GetMapping({ "/login", "/login/" })
    // public String login(@RequestParam(value = "error", required = false) String
    // error,
    // @RequestParam(value = "logout", required = false) String logout,
    // Model model) {
    // if (error != null) {
    // model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
    // }
    // if (logout != null) {
    // model.addAttribute("message", "Đăng xuất thành công!");
    // }
    // return "auth/login";
    // }
}
