package com.example.quanlytiemnet.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.quanlytiemnet.models.Computer;
import com.example.quanlytiemnet.services.ComputerService;
import com.google.gson.Gson;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private ComputerService computerService;

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

		model.addAttribute("computers", computers);
		model.addAttribute("computerInUse", inUse);
		model.addAttribute("availableComputers", available);
		model.addAttribute("maintenanceComputers", maintenance);
		model.addAttribute("todayRevenue", formattedRevenue);

		// Dữ liệu cho biểu đồ doanh thu
		List<Map<String, Object>> revenueData = computerService.getRevenueLastSevenDays();
		Gson gson = new Gson();
		model.addAttribute("revenueDataJson", gson.toJson(revenueData));

		// Dữ liệu cho biểu đồ sử dụng máy
		Map<String, Long> usageStats = computerService.getComputerUsageStats();
		model.addAttribute("usageDataJson", gson.toJson(usageStats));

		return "admin/dashboard";
	}
}