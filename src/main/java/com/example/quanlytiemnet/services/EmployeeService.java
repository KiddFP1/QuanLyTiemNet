package com.example.quanlytiemnet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quanlytiemnet.models.Employee;
import com.example.quanlytiemnet.repositories.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Employee findByUsername(String username) {
		return employeeRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
	}

	public List<Employee> getAllEmployees() {
		return employeeRepository.findAllWithShifts();
	}

	private void validatePassword(String password) {
		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Mật khẩu không được trống");
		}

		if (password.contains(" ")) {
			throw new IllegalArgumentException("Mật khẩu không được chứa khoảng trắng");
		}
	}

	private String formatRole(String role) {
		if (role == null || role.trim().isEmpty()) {
			return "ROLE_EMPLOYEE"; // Default role
		}

		// Remove ROLE_ prefix if it exists
		String cleanRole = role.startsWith("ROLE_") ? role.substring(5) : role;

		// Convert to uppercase and add ROLE_ prefix
		return "ROLE_" + cleanRole.toUpperCase();
	}

	public Employee createEmployee(Employee employee) {
		// Validate username
		if (employee.getUsername() == null || employee.getUsername().trim().isEmpty()) {
			throw new IllegalArgumentException("Tên đăng nhập không được trống");
		}

		// Check if username already exists
		if (employeeRepository.findByUsername(employee.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
		}

		// Validate password
		validatePassword(employee.getPassword());

		// Format role
		employee.setRole(formatRole(employee.getRole()));

		// Encode password before saving
		employee.setPassword(passwordEncoder.encode(employee.getPassword()));

		return employeeRepository.save(employee);
	}

	public Employee getEmployeeById(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID nhân viên không được null");
		}
		return employeeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + id));
	}

	public Employee updateEmployee(Employee employee) {
		// Find existing employee
		Employee existingEmployee = getEmployeeById(employee.getId());

		// Check if username is being changed and if it's already taken
		if (!existingEmployee.getUsername().equals(employee.getUsername())) {
			if (employeeRepository.findByUsername(employee.getUsername()).isPresent()) {
				throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
			}
		}

		// Update fields
		existingEmployee.setUsername(employee.getUsername());
		existingEmployee.setFullName(employee.getFullName());

		// Format and set role
		existingEmployee.setRole(formatRole(employee.getRole()));

		// Update password if provided
		if (employee.getPassword() != null && !employee.getPassword().trim().isEmpty()) {
			validatePassword(employee.getPassword());
			existingEmployee.setPassword(passwordEncoder.encode(employee.getPassword()));
		}

		return employeeRepository.save(existingEmployee);
	}

	@Transactional
	public void deleteEmployee(Long id) {
		// Verify employee exists before deleting
		getEmployeeById(id);
		employeeRepository.deleteById(id);
	}
}