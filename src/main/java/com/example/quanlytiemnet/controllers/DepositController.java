package com.example.quanlytiemnet.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.models.TopUpHistory;
import com.example.quanlytiemnet.repositories.TopUpHistoryRepository;
import com.example.quanlytiemnet.services.MemberService;

@Controller
@RequestMapping("/member")
public class DepositController {
    private static final Logger log = LoggerFactory.getLogger(DepositController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private TopUpHistoryRepository topUpHistoryRepository;

    @GetMapping("/deposit")
    public String showDepositForm(Model model) {
        return "member/deposit";
    }

    @PostMapping("/deposit")
    public String processDeposit(@RequestParam Integer memberId, @RequestParam BigDecimal amount,
            RedirectAttributes redirectAttributes) {
        try {
            log.info("Processing deposit for member {} with amount {}", memberId, amount);

            // Kiểm tra số tiền nạp
            if (amount.compareTo(BigDecimal.valueOf(10000)) < 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Số tiền nạp tối thiểu là 10,000 VNĐ");
                return "redirect:/member/deposit";
            }

            // Cập nhật số dư
            memberService.updateBalance(memberId, amount);

            // Lưu lịch sử nạp tiền
            Member member = memberService.getMemberById(memberId);
            TopUpHistory topUpHistory = new TopUpHistory();
            topUpHistory.setMember(member);
            topUpHistory.setAmount(amount);
            topUpHistory.setTopUpDate(LocalDateTime.now());
            topUpHistory.setNote("Nạp tiền vào tài khoản");
            topUpHistoryRepository.save(topUpHistory);

            redirectAttributes.addFlashAttribute("successMessage", "Nạp tiền thành công");
            return "redirect:/member/home";
        } catch (Exception e) {
            log.error("Error processing deposit for member {}: {}", memberId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi nạp tiền: " + e.getMessage());
            return "redirect:/member/deposit";
        }
    }
}