package com.example.quanlytiemnet.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.quanlytiemnet.models.Computer;
import com.example.quanlytiemnet.models.ServiceOrder;
import com.example.quanlytiemnet.models.TopUpRequest;
import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.services.ComputerService;
import com.example.quanlytiemnet.services.ServiceOrderService;
import com.example.quanlytiemnet.services.TopUpRequestService;
import com.example.quanlytiemnet.services.MemberService;
import com.example.quanlytiemnet.repositories.ServiceOrderRepository;
import com.example.quanlytiemnet.repositories.TopUpRequestRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {
	@Autowired
	private ComputerService computerService;

	@Autowired
	private ServiceOrderService serviceOrderService;

	@Autowired
	private TopUpRequestService topUpRequestService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private ServiceOrderRepository serviceOrderRepository;

	@Autowired
	private TopUpRequestRepository topUpRequestRepository;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		List<Computer> computers = computerService.getAllComputers();

		// Thống kê trạng thái máy
		long inUse = computers.stream().filter(c -> "InUse".equals(c.getStatus())).count();
		long available = computers.stream().filter(c -> "Available".equals(c.getStatus())).count();
		long maintenance = computers.stream().filter(c -> "Maintenance".equals(c.getStatus())).count();

		// Tính doanh thu hôm nay
		BigDecimal todayRevenue = computerService.calculateTodayRevenue();
		String formattedRevenue = String.format("%,d đ", todayRevenue.longValue());

		// Lấy danh sách yêu cầu nạp tiền đang chờ
		List<TopUpRequest> pendingTopUps = topUpRequestService.getPendingRequests();

		// Lấy danh sách đơn hàng dịch vụ đang chờ
		List<ServiceOrder> pendingOrders = serviceOrderService.getPendingOrders();

		model.addAttribute("computers", computers);
		model.addAttribute("computerInUse", inUse);
		model.addAttribute("availableComputers", available);
		model.addAttribute("maintenanceComputers", maintenance);
		model.addAttribute("todayRevenue", formattedRevenue);
		model.addAttribute("pendingTopUps", pendingTopUps);
		model.addAttribute("pendingOrders", pendingOrders);

		// Dữ liệu cho biểu đồ doanh thu
		List<Map<String, Object>> revenueData = computerService.getRevenueLastSevenDays();
		Gson gson = new Gson();
		model.addAttribute("revenueDataJson", gson.toJson(revenueData));

		// Dữ liệu cho biểu đồ sử dụng máy
		Map<String, Long> usageStats = computerService.getComputerUsageStats();
		model.addAttribute("usageDataJson", gson.toJson(usageStats));

		return "admin/dashboard";
	}

	@PostMapping("/topup/approve/{id}")
	@ResponseBody
	public ResponseEntity<?> approveTopUp(@PathVariable Integer id) {
		try {
			TopUpRequest request = topUpRequestRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu nạp tiền"));
			topUpRequestService.approveRequest(request, "Đã duyệt yêu cầu nạp tiền");
			return ResponseEntity.ok(Map.of("success", true));
		} catch (Exception e) {
			log.error("Error approving top-up request {}", id, e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Lỗi khi duyệt yêu cầu: " + e.getMessage()));
		}
	}

	@PostMapping("/topup/reject/{id}")
	@ResponseBody
	public ResponseEntity<?> rejectTopUp(@PathVariable Integer id) {
		try {
			TopUpRequest request = topUpRequestRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu nạp tiền"));
			topUpRequestService.rejectRequest(request, "Đã từ chối yêu cầu nạp tiền");
			return ResponseEntity.ok(Map.of("success", true));
		} catch (Exception e) {
			log.error("Error rejecting top-up request {}", id, e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Lỗi khi từ chối yêu cầu: " + e.getMessage()));
		}
	}

	@PostMapping("/orders/complete/{id}")
	@ResponseBody
	public ResponseEntity<?> completeOrder(@PathVariable Integer id) {
		try {
			ServiceOrder order = serviceOrderRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
			order.setStatus("COMPLETED");
			serviceOrderService.updateOrder(order);
			return ResponseEntity.ok(Map.of("success", true));
		} catch (Exception e) {
			log.error("Error completing order {}", id, e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Lỗi khi hoàn thành đơn hàng: " + e.getMessage()));
		}
	}

	@PostMapping("/orders/cancel/{id}")
	@ResponseBody
	public ResponseEntity<?> cancelOrder(@PathVariable Integer id) {
		try {
			ServiceOrder order = serviceOrderRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

			// Only refund if the order is not already canceled or completed
			if (!order.getStatus().equals("CANCELLED") && !order.getStatus().equals("COMPLETED")) {
				// Get the member associated with the order
				Member member = order.getMember();

				// Refund the money to the member's balance
				BigDecimal refundAmount = order.getTotalPrice();
				member.setBalance(member.getBalance().add(refundAmount));

				// Update the member
				memberService.updateMember(member);

				log.info("Refunded {} to member ID {} for canceled order {}",
						refundAmount, member.getId(), order.getId());
			}

			// Update order status
			order.setStatus("CANCELLED");
			serviceOrderService.updateOrder(order);

			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "Đơn hàng đã bị hủy và tiền đã được hoàn lại"));
		} catch (Exception e) {
			log.error("Error canceling order {}", id, e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Lỗi khi hủy đơn hàng: " + e.getMessage()));
		}
	}
}