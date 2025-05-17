package com.example.quanlytiemnet.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlytiemnet.models.DailyRevenue;
import com.example.quanlytiemnet.models.RevenueReport;
import com.example.quanlytiemnet.repositories.ComputerUsageRepository;
import com.example.quanlytiemnet.repositories.ServiceRepository;
import com.example.quanlytiemnet.repositories.ServiceTransactionRepository;

@Service
public class ReportService {
	@Autowired
	private ComputerUsageRepository usageRepository;

	@Autowired
	private ServiceTransactionRepository transactionRepository;

	@Autowired
	private ServiceRepository serviceRepository;

	public RevenueReport getRevenueReport(LocalDate startDate, LocalDate endDate) {
		LocalDateTime start = startDate.atStartOfDay();
		LocalDateTime end = endDate.atTime(23, 59, 59);

		RevenueReport report = new RevenueReport();
		List<DailyRevenue> dailyRevenues = new ArrayList<>();
		BigDecimal totalRevenue = BigDecimal.ZERO;

		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			LocalDateTime dayStart = date.atStartOfDay();
			LocalDateTime dayEnd = date.atTime(23, 59, 59);

			DailyRevenue daily = new DailyRevenue();
			daily.setDate(date);

			// Tính doanh thu máy
			BigDecimal computerRevenue = calculateComputerRevenue(dayStart, dayEnd);
			daily.setComputerRevenue(computerRevenue);

			// Tính doanh thu dịch vụ
			BigDecimal serviceRevenue = calculateServiceRevenue(dayStart, dayEnd);
			daily.setServiceRevenue(serviceRevenue);

			// Tính tổng doanh thu ngày
			BigDecimal dailyTotal = computerRevenue.add(serviceRevenue);
			daily.setTotalRevenue(dailyTotal);

			dailyRevenues.add(daily);
			totalRevenue = totalRevenue.add(dailyTotal);
		}

		report.setDailyRevenues(dailyRevenues);
		report.setTotalRevenue(totalRevenue);
		return report;
	}

	private BigDecimal calculateComputerRevenue(LocalDateTime start, LocalDateTime end) {
		return usageRepository.findByStartTimeBetween(start, end).stream().map(usage -> usage.getTotalAmount())
				.filter(amount -> amount != null).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private BigDecimal calculateServiceRevenue(LocalDateTime start, LocalDateTime end) {
		return transactionRepository.findByTransactionDateBetween(start, end).stream()
				.map(transaction -> transaction.getTotalAmount()).filter(amount -> amount != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public List<com.example.quanlytiemnet.models.Service> getInventoryReport() {
		return serviceRepository.findAll();
	}
}