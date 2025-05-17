package com.example.quanlytiemnet.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays; // Thêm dòng này vào các import khác ở đầu file
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlytiemnet.models.Computer;
import com.example.quanlytiemnet.models.ComputerUsage;
import com.example.quanlytiemnet.repositories.ComputerRepository;
import com.example.quanlytiemnet.repositories.ComputerUsageRepository;

@Service
public class ComputerService {
	@Autowired
	private ComputerRepository computerRepository;

	@Autowired
	private ComputerUsageRepository usageRepository;

	public List<Computer> getAllComputers() {
		return computerRepository.findAll();
	}

	public List<Computer> getAvailableComputers() {
		return computerRepository.findByStatus("Available");
	}

	public void updateComputerStatus(Integer id, String status) {
		Computer computer = computerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Computer not found"));
		computer.setStatus(status);
		computerRepository.save(computer);
	}

	public Computer getComputerById(Integer id) {
		return computerRepository.findById(id).orElseThrow(() -> new RuntimeException("Computer not found"));
	}

	public Computer saveComputer(Computer computer) {
		return computerRepository.save(computer);
	}

	public void deleteComputer(Integer id) {
		computerRepository.deleteById(id);
	}

	public BigDecimal calculateTodayRevenue() {
		LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
		LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

		List<ComputerUsage> todayUsages = usageRepository.findByStartTimeBetween(startOfDay, endOfDay);

		return todayUsages.stream().map(ComputerUsage::getTotalAmount).filter(amount -> amount != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public List<Map<String, Object>> getRevenueLastSevenDays() {
		LocalDateTime endDate = LocalDateTime.now();
		LocalDateTime startDate = endDate.minusDays(6);

		List<Map<String, Object>> result = new ArrayList<>();
		LocalDateTime currentDate = startDate;

		System.out.println("===== Getting Revenue Last Seven Days =====");
		System.out.println("Start Date: " + startDate);
		System.out.println("End Date: " + endDate);

		while (!currentDate.isAfter(endDate)) {
			LocalDateTime dayStart = currentDate.withHour(0).withMinute(0);
			LocalDateTime dayEnd = currentDate.withHour(23).withMinute(59);

			List<ComputerUsage> dayUsages = usageRepository.findByStartTimeBetween(dayStart, dayEnd);

			BigDecimal revenue = dayUsages.stream().map(ComputerUsage::getTotalAmount).filter(amount -> amount != null)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			Map<String, Object> dayData = new HashMap<>();
			dayData.put("date", currentDate.format(DateTimeFormatter.ofPattern("dd/MM")));
			dayData.put("revenue", revenue);
			result.add(dayData);

			System.out.println("Date: " + currentDate.format(DateTimeFormatter.ofPattern("dd/MM")) + ", Revenue: "
					+ revenue + ", Usage Count: " + dayUsages.size());

			currentDate = currentDate.plusDays(1);
		}

		// Nếu không có dữ liệu, trả về dữ liệu mẫu
		if (result.isEmpty()) {
			result = Arrays.asList(createRevenueEntry("31/01", 1000), createRevenueEntry("01/02", 1500),
					createRevenueEntry("02/02", 1200), createRevenueEntry("03/02", 1800),
					createRevenueEntry("04/02", 2000), createRevenueEntry("05/02", 1600),
					createRevenueEntry("06/02", 2200));
		}

		System.out.println("Total Revenue Entries: " + result.size());
		return result;
	}

	private Map<String, Object> createRevenueEntry(String date, long revenue) {
		Map<String, Object> entry = new HashMap<>();
		entry.put("date", date);
		entry.put("revenue", BigDecimal.valueOf(revenue));
		return entry;
	}

	public Map<String, Long> getComputerUsageStats() {
		List<Computer> computers = getAllComputers();
		Map<String, Long> stats = new HashMap<>();

		System.out.println("===== Computer Usage Stats =====");
		System.out.println("Total Computers: " + computers.size());

		long inUse = computers.stream().filter(c -> "InUse".equals(c.getStatus())).count();
		long available = computers.stream().filter(c -> "Available".equals(c.getStatus())).count();
		long maintenance = computers.stream().filter(c -> "Maintenance".equals(c.getStatus())).count();

		stats.put("Đang sử dụng", inUse);
		stats.put("Trống", available);
		stats.put("Bảo trì", maintenance);

		System.out.println("In Use: " + inUse);
		System.out.println("Available: " + available);
		System.out.println("Maintenance: " + maintenance);

		return stats;
	}

}