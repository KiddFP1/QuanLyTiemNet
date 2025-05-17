package com.example.quanlytiemnet.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.models.Service;
import com.example.quanlytiemnet.models.ServiceOrder;
import com.example.quanlytiemnet.models.TopUpRequest;
import com.example.quanlytiemnet.services.MemberService;
import com.example.quanlytiemnet.services.ServiceService;
import com.example.quanlytiemnet.services.ServiceOrderService;
import com.example.quanlytiemnet.services.TopUpRequestService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/member")
@Slf4j
public class MemberDashboardController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private TopUpRequestService topUpRequestService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        log.info("Member accessing dashboard: {}", authentication.getName());

        Member member = memberService.getMemberByUsername(authentication.getName());
        List<Service> services = serviceService.getAllServices();
        List<ServiceOrder> serviceOrders = serviceOrderService.getMemberOrders(member);
        List<TopUpRequest> topUpRequests = topUpRequestService.getMemberRequests(member);

        model.addAttribute("member", member);
        model.addAttribute("services", services);
        model.addAttribute("serviceOrders", serviceOrders);
        model.addAttribute("topUpRequests", topUpRequests);

        return "member/dashboard";
    }

    @PostMapping("/service/order")
    public String orderService(
            @RequestParam Integer serviceId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String note,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Member member = memberService.getMemberByUsername(authentication.getName());
            Service service = serviceService.getServiceById(serviceId);

            // Tính tổng tiền
            BigDecimal totalPrice = service.getPrice().multiply(BigDecimal.valueOf(quantity));

            // Kiểm tra số dư
            if (member.getBalance().compareTo(totalPrice) < 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Số dư không đủ để thực hiện giao dịch. Vui lòng nạp thêm tiền.");
                return "redirect:/member/dashboard";
            }

            // Tạo đơn hàng mới
            ServiceOrder order = new ServiceOrder();
            order.setMember(member);
            order.setService(service);
            order.setQuantity(quantity);
            order.setTotalPrice(totalPrice);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");
            order.setNote(note);

            // Trừ tiền trực tiếp
            member.setBalance(member.getBalance().subtract(totalPrice));
            memberService.updateMember(member);

            // Lưu đơn hàng
            serviceOrderService.createOrder(order);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt món thành công! Vui lòng đợi nhân viên xử lý.");

        } catch (Exception e) {
            log.error("Error creating service order", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra khi gọi món: " + e.getMessage());
        }

        return "redirect:/member/dashboard";
    }

    @PostMapping("/topup/request")
    public String requestTopUp(
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String note,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            if (amount.compareTo(BigDecimal.valueOf(10000)) < 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Số tiền nạp tối thiểu là 10,000 VNĐ");
                return "redirect:/member/dashboard";
            }

            Member member = memberService.getMemberByUsername(authentication.getName());

            TopUpRequest request = new TopUpRequest();
            request.setMember(member);
            request.setAmount(amount);
            request.setRequestDate(LocalDateTime.now());
            request.setStatus("PENDING");
            request.setNote(note);

            topUpRequestService.createRequest(request);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã gửi yêu cầu nạp tiền. Vui lòng đợi nhân viên xử lý.");

        } catch (Exception e) {
            log.error("Error creating top-up request", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra khi yêu cầu nạp tiền: " + e.getMessage());
        }

        return "redirect:/member/dashboard";
    }
}