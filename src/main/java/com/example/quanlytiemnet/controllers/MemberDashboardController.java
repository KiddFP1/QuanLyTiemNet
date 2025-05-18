package com.example.quanlytiemnet.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.example.quanlytiemnet.dto.ActivityDTO;

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

        // Combine activities
        List<ActivityDTO> allActivities = new ArrayList<>();

        // Add service orders
        serviceOrders.forEach(order -> {
            ActivityDTO activity = new ActivityDTO();
            activity.setTimestamp(order.getOrderDate());
            activity.setType("ORDER");
            activity.setTitle(order.getService().getServiceName());
            activity.setAmount(order.getTotalPrice());
            activity.setStatus(order.getStatus());
            activity.setNote("Số lượng: " + order.getQuantity());
            allActivities.add(activity);
        });

        // Add top-up requests
        topUpRequests.forEach(request -> {
            ActivityDTO activity = new ActivityDTO();
            activity.setTimestamp(request.getRequestDate());
            activity.setType("TOPUP");
            activity.setTitle("Nạp tiền");
            activity.setAmount(request.getAmount());
            activity.setStatus(request.getStatus());
            activity.setNote(request.getStatus().equals("APPROVED") ? "Đã nạp tiền thành công"
                    : request.getStatus().equals("REJECTED") ? "Yêu cầu bị từ chối" : "Đang chờ xử lý");
            allActivities.add(activity);
        });

        // Sort by timestamp (newest first) and get top 4
        List<ActivityDTO> recentActivities = allActivities.stream()
                .sorted()
                .limit(4)
                .collect(Collectors.toList());

        model.addAttribute("member", member);
        model.addAttribute("services", services);
        model.addAttribute("recentActivities", recentActivities);
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

    @GetMapping("/activities")
    public String activities(Model model, Authentication authentication) {
        Member member = memberService.getMemberByUsername(authentication.getName());
        List<ServiceOrder> serviceOrders = serviceOrderService.getMemberOrders(member);
        List<TopUpRequest> topUpRequests = topUpRequestService.getMemberRequests(member);

        model.addAttribute("serviceOrders", serviceOrders);
        model.addAttribute("topUpRequests", topUpRequests);
        return "member/activities";
    }

    @GetMapping("/topup-history")
    public String topupHistory(Model model, Authentication authentication) {
        Member member = memberService.getMemberByUsername(authentication.getName());
        List<TopUpRequest> topUpRequests = topUpRequestService.getMemberRequests(member);
        model.addAttribute("topUpRequests", topUpRequests);
        return "member/topup-history";
    }
}