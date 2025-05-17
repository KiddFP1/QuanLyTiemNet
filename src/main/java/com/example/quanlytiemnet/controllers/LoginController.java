// LoginController.java
package com.example.quanlytiemnet.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			Model model) {
		if (error != null) {
			model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
		}
		if (logout != null) {
			model.addAttribute("message", "Đăng xuất thành công!");
		}
		return "auth/login";
	}

	// @GetMapping("/default")
	public String defaultAfterLogin(Authentication authentication) {
		// Lấy role và redirect về trang phù hợp
		if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			return "redirect:/admin/dashboard";
		} else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"))) {
			return "redirect:/employee/home";
		} else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
			return "redirect:/member/home";
		}
		return "redirect:/login?error";
	}
}
