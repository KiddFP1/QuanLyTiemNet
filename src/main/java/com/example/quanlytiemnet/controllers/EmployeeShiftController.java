package com.example.quanlytiemnet.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.quanlytiemnet.repositories.EmployeeShiftRepository;

@Controller
@RequestMapping("/admin/employees")
public class EmployeeShiftController {

	@Autowired
	private EmployeeShiftRepository employeeShiftRepository;

	@GetMapping("/shifts/all")
	@ResponseBody
	public List<Map<String, Object>> getAllEmployeeShifts() {
		// Lấy dữ liệu từ EmployeeShiftRepository, đã JOIN sang WorkShift
		return employeeShiftRepository.findAllEmployeeShifts();
	}
}