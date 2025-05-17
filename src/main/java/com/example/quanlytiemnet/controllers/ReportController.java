package com.example.quanlytiemnet.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quanlytiemnet.models.RevenueReport;
import com.example.quanlytiemnet.services.ReportService;

@Controller
@RequestMapping("/admin/reports")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@GetMapping("")
	public String showReports(Model model) {
		model.addAttribute("inventory", reportService.getInventoryReport());
		return "admin/reports";
	}

	@GetMapping("/revenue")
	public String getRevenueReport(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate, Model model) {

		if (startDate == null) {
			startDate = LocalDate.now().minusDays(30);
		}

		if (endDate == null) {
			endDate = LocalDate.now();
		}

		if (endDate.isBefore(startDate)) {
			model.addAttribute("dateError", "Ngày kết thúc không được trước ngày bắt đầu");
		} else {
			RevenueReport report = reportService.getRevenueReport(startDate, endDate);
			model.addAttribute("revenueReport", report);
		}

		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("inventory", reportService.getInventoryReport());

		return "admin/reports";
	}
}