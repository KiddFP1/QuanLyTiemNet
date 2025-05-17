package com.example.quanlytiemnet.services;

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
		usage.setEndTime(LocalDateTime.now());
		computerService.updateComputerStatus(usage.getComputer().getComputerId(), "Available");
		return usageRepository.save(usage);
	}

	public List<ComputerUsage> getUsagesBetweenDates(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		return usageRepository.findByStartTimeBetweenOrderByStartTimeDesc(startDateTime, endDateTime);
	}
}