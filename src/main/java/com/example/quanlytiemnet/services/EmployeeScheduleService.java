package com.example.quanlytiemnet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlytiemnet.models.EmployeeSchedule;
import com.example.quanlytiemnet.repositories.EmployeeScheduleRepository;

@Service
public class EmployeeScheduleService {
	@Autowired
	private EmployeeScheduleRepository employeeScheduleRepository;

	public List<EmployeeSchedule> getFullSchedule() {
		return employeeScheduleRepository.getFullSchedule();
	}
}