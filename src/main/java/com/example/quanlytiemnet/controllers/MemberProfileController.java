package com.example.quanlytiemnet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.services.MemberService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/member")
@Slf4j
public class MemberProfileController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        Member member = memberService.getMemberByUsername(authentication.getName());
        model.addAttribute("member", member);
        return "member/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Member member, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Member currentMember = memberService.getMemberByUsername(authentication.getName());

            // Chỉ cập nhật họ tên và số điện thoại
            currentMember.setFullName(member.getFullName());
            currentMember.setPhone(member.getPhone());

            memberService.updateMember(currentMember);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");

        } catch (Exception e) {
            log.error("Error updating member profile", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra khi cập nhật thông tin: " + e.getMessage());
        }

        return "redirect:/member/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Member member = memberService.getMemberByUsername(authentication.getName());

            // Kiểm tra mật khẩu hiện tại
            if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không đúng!");
                return "redirect:/member/profile";
            }

            // Kiểm tra mật khẩu mới và xác nhận
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp!");
                return "redirect:/member/profile";
            }

            // Cập nhật mật khẩu mới
            member.setPassword(passwordEncoder.encode(newPassword));
            memberService.updateMember(member);

            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");

        } catch (Exception e) {
            log.error("Error changing member password", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi đổi mật khẩu: " + e.getMessage());
        }

        return "redirect:/member/profile";
    }
}