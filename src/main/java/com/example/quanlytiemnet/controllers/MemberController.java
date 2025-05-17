package com.example.quanlytiemnet.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.models.TopUpHistory;
import com.example.quanlytiemnet.repositories.TopUpHistoryRepository;
import com.example.quanlytiemnet.services.MemberService;

@Controller
@RequestMapping("/admin/members")
public class MemberController {
	private static final Logger log = LoggerFactory.getLogger(MemberController.class);

	@Autowired
	private MemberService memberService;

	@Autowired
	private TopUpHistoryRepository topUpHistoryRepository;

	@GetMapping("")
	public String listMembers(Model model) {
		model.addAttribute("members", memberService.getAllMembers());
		return "admin/members";
	}

	@PostMapping("/add")
	public String addMember(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {
		try {
			memberService.createMember(member);
			redirectAttributes.addFlashAttribute("successMessage", "Thêm thành viên thành công");
		} catch (Exception e) {
			log.error("Error creating member", e);
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm thành viên: " + e.getMessage());
		}
		return "redirect:/admin/members";
	}

	@GetMapping("/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> getMember(@PathVariable Integer id) {
		try {
			log.info("Fetching member with ID: {}", id);
			Member member = memberService.getMemberById(id);
			return ResponseEntity.ok(member);
		} catch (Exception e) {
			log.error("Error fetching member {}", id, e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Lỗi khi lấy thông tin thành viên: " + e.getMessage()));
		}
	}

	@PostMapping("/update")
	public String updateMember(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {
		try {
			log.info("Updating member: {}", member);
			memberService.updateMember(member);
			redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành viên thành công");
		} catch (Exception e) {
			log.error("Error updating member", e);
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật thành viên: " + e.getMessage());
		}
		return "redirect:/admin/members";
	}

	@PostMapping("/delete/{id}")
	public String deleteMember(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			memberService.deleteMember(id);
			redirectAttributes.addFlashAttribute("successMessage", "Xóa thành viên thành công");
		} catch (Exception e) {
			log.error("Error deleting member {}", id, e);
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa thành viên: " + e.getMessage());
		}
		return "redirect:/admin/members";
	}

	@PostMapping("/topup/{memberId}")
	@ResponseBody
	public ResponseEntity<?> topUpMember(@PathVariable Integer memberId, @RequestBody Map<String, Object> payload) {
		try {
			log.info("Attempting to top up member {}", memberId);
			BigDecimal amount = new BigDecimal(payload.get("amount").toString());

			// Cập nhật số dư và lưu lịch sử nạp tiền
			memberService.updateBalance(memberId, amount);

			// Lưu lịch sử nạp tiền
			Member member = memberService.getMemberById(memberId);
			TopUpHistory topUpHistory = new TopUpHistory();
			topUpHistory.setMember(member);
			topUpHistory.setAmount(amount);
			topUpHistory.setTopUpDate(LocalDateTime.now());
			topUpHistory.setNote("Nạp tiền vào tài khoản (Admin)");
			topUpHistoryRepository.save(topUpHistory);

			log.info("Successfully topped up member {} with amount {}", memberId, amount);
			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "Nạp tiền thành công"));
		} catch (Exception e) {
			log.error("Error topping up member {}: {}", memberId, e.getMessage());
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Có lỗi xảy ra khi nạp tiền: " + e.getMessage()));
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