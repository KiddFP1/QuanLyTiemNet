package com.example.quanlytiemnet.controllers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.services.MemberService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/members")
@Slf4j
public class MemberController {
	@Autowired
	private MemberService memberService;

	@GetMapping("")
	public String listMembers(Model model) {
		model.addAttribute("members", memberService.getAllMembers());
		return "admin/members";
	}

	@PostMapping("/add")
	public String addMember(@ModelAttribute Member member) {
		memberService.createMember(member);
		return "redirect:/admin/members";
	}

	@GetMapping("/edit/{id}")
	@ResponseBody
	public Member getMember(@PathVariable Integer id) {
		return memberService.getMemberById(id);
	}

	@PostMapping("/update/{id}")
	public String updateMember(@PathVariable Integer id, @ModelAttribute Member member) {
		member.setId(id);
		memberService.updateMember(member);
		return "redirect:/admin/members";
	}

	@PostMapping("/delete/{id}")
	public String deleteMember(@PathVariable Integer id) {
		memberService.deleteMember(id);
		return "redirect:/admin/members";
	}

	@PostMapping("/topup/{memberId}")
	@ResponseBody
	public ResponseEntity<?> topUpMember(@PathVariable Integer memberId, @RequestBody Map<String, Object> payload) {
		try {
			log.info("Attempting to top up member {}", memberId);
			BigDecimal amount = new BigDecimal(payload.get("amount").toString());
			memberService.updateBalance(memberId, amount);
			log.info("Successfully topped up member {} with amount {}", memberId, amount);
			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "Nạp tiền thành công"));
		} catch (Exception e) {
			log.error("Error topping up member {}: {}", memberId, e.getMessage());
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Có lỗi xảy ra khi nạp tiền"));
		}
	}

	@GetMapping("/home")
	public String home(Model model) {
		model.addAttribute("featuredProducts", Collections.emptyList());
		model.addAttribute("popularGameCards", Collections.emptyList());
		model.addAttribute("currentComputer", null);
		return "member/home";
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		log.info("Accessing member dashboard");
		return "member/dashboard";
	}

	@GetMapping("/profile")
	public String profile(Model model) {
		log.info("Accessing member profile");
		return "member/profile";
	}
}