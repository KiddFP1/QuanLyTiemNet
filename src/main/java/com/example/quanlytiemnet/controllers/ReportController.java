package com.example.quanlytiemnet.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quanlytiemnet.models.ComputerUsage;
import com.example.quanlytiemnet.models.TopUpHistory;
import com.example.quanlytiemnet.services.ComputerUsageService;
import com.example.quanlytiemnet.services.MemberService;

@Controller
@RequestMapping("/admin/reports")
public class ReportController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private ComputerUsageService computerUsageService;

	@GetMapping("")
	public String showReports(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@RequestParam(required = false, defaultValue = "day") String period,
			Model model) {

		// Nếu không có ngày được chọn, mặc định là 7 ngày gần nhất
		if (startDate == null) {
			startDate = LocalDate.now().minusDays(6);
		}
		if (endDate == null) {
			endDate = LocalDate.now();
		}

		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

		// Lấy danh sách giao dịch sử dụng máy và nạp tiền
		List<ComputerUsage> usages = computerUsageService.getUsagesBetweenDates(startDate, endDate);
		List<TopUpHistory> topUps = memberService.getTopUpHistoryBetweenDates(startDateTime, endDateTime);

		// Tính tổng số tiền sử dụng máy (chỉ để theo dõi)
		BigDecimal usageAmount = usages.stream()
				.map(ComputerUsage::getTotalAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// Tính tổng doanh thu từ nạp tiền (doanh thu thực tế)
		BigDecimal topUpRevenue = topUps.stream()
				.map(TopUpHistory::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// Tổng doanh thu = doanh thu từ nạp tiền
		BigDecimal totalRevenue = topUpRevenue;

		// Thống kê doanh thu theo thời gian (dựa trên nạp tiền)
		Map<LocalDate, BigDecimal> revenueByPeriod;
		switch (period) {
			case "week":
				// Nhóm theo tuần
				revenueByPeriod = topUps.stream()
						.collect(Collectors.groupingBy(
								topUp -> topUp.getTopUpDate().toLocalDate()
										.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1),
								Collectors.mapping(
										TopUpHistory::getAmount,
										Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
				break;
			case "month":
				// Nhóm theo tháng
				revenueByPeriod = topUps.stream()
						.collect(Collectors.groupingBy(
								topUp -> topUp.getTopUpDate().toLocalDate().withDayOfMonth(1),
								Collectors.mapping(
										TopUpHistory::getAmount,
										Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
				break;
			default:
				// Nhóm theo ngày
				revenueByPeriod = topUps.stream()
						.collect(Collectors.groupingBy(
								topUp -> topUp.getTopUpDate().toLocalDate(),
								Collectors.mapping(
										TopUpHistory::getAmount,
										Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
				break;
		}

		// Tính số lượng giao dịch
		int totalTransactions = usages.size() + topUps.size();

		// Thêm dữ liệu vào model
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("period", period);
		model.addAttribute("revenueByPeriod", revenueByPeriod);
		model.addAttribute("totalRevenue", totalRevenue);
		model.addAttribute("usageAmount", usageAmount);
		model.addAttribute("topUpRevenue", topUpRevenue);
		model.addAttribute("totalTransactions", totalTransactions);
		model.addAttribute("usageCount", usages.size());
		model.addAttribute("topUpCount", topUps.size());

		return "admin/reports";
	}
}