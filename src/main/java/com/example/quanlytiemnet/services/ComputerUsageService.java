package com.example.quanlytiemnet.services;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quanlytiemnet.models.Computer;
import com.example.quanlytiemnet.models.ComputerUsage;
import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.repositories.ComputerUsageRepository;

@Service
@Transactional
public class ComputerUsageService {
	@Autowired
	private ComputerUsageRepository usageRepository;
	@Autowired
	private ComputerService computerService;

	// Giá tiền mỗi giờ (VNĐ)
	private static final BigDecimal HOURLY_RATE = BigDecimal.valueOf(10000);

	public ComputerUsage startUsage(Computer computer, Member member) {
		ComputerUsage usage = new ComputerUsage();
		usage.setComputer(computer);
		usage.setMember(member);
		usage.setStartTime(LocalDateTime.now());
		computerService.updateComputerStatus(computer.getComputerId(), "InUse");
		return usageRepository.save(usage);
	}

	public ComputerUsage endUsage(Integer usageId) {
		ComputerUsage usage = usageRepository.findById(usageId)
				.orElseThrow(() -> new RuntimeException("Usage not found"));

		// Đặt thời gian kết thúc
		usage.setEndTime(LocalDateTime.now());

		// Tính toán số tiền dựa trên thời gian sử dụng
		Duration duration = Duration.between(usage.getStartTime(), usage.getEndTime());
		double hours = duration.toMinutes() / 60.0;
		BigDecimal amount = HOURLY_RATE.multiply(BigDecimal.valueOf(hours));

		// Làm tròn đến hàng nghìn
		amount = amount.setScale(-3, java.math.RoundingMode.CEILING);
		usage.setTotalAmount(amount);

		// Cập nhật trạng thái máy
		computerService.updateComputerStatus(usage.getComputer().getComputerId(), "Available");

		return usageRepository.save(usage);
	}

	public List<ComputerUsage> getUsagesBetweenDates(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		return usageRepository.findByStartTimeBetweenOrderByStartTimeDesc(startDateTime, endDateTime);
	}
}