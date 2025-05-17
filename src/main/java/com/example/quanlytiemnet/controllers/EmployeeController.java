package com.example.quanlytiemnet.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import com.example.quanlytiemnet.models.Employee;
import com.example.quanlytiemnet.models.EmployeeShift;
import com.example.quanlytiemnet.services.EmployeeService;
import com.example.quanlytiemnet.services.ShiftService;
import com.example.quanlytiemnet.services.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;

@Controller
@RequestMapping("/admin/employees")
@Validated
@Slf4j
public class EmployeeController {
	private static final String SUCCESS_MESSAGE = "successMessage";
	private static final String ERROR_MESSAGE = "errorMessage";

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ShiftService shiftService;

	@Autowired
	private MemberService memberService;

	@GetMapping
	public String listEmployees(Model model) {
		try {
			model.addAttribute("employees", employeeService.getAllEmployees());

			// Lấy role hiện tại
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String role = auth.getAuthorities().stream()
					.map(a -> a.getAuthority())
					.filter(r -> r.startsWith("ROLE_"))
					.findFirst()
					.orElse("");
			model.addAttribute("currentRole", role);

			return "admin/employees";
		} catch (Exception e) {
			log.error("Lỗi khi lấy danh sách nhân viên", e);
			model.addAttribute(ERROR_MESSAGE, "Không thể lấy danh sách nhân viên: " + e.getMessage());
			return "error";
		}
	}

	@PostMapping("/add")
	public String addEmployee(@ModelAttribute @Validated Employee employee, RedirectAttributes redirectAttributes) {
		if (!hasAdminRole()) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Bạn không có quyền thêm nhân viên!");
			return "redirect:/admin/employees";
		}
		try {
			employeeService.createEmployee(employee);
			redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Thêm nhân viên thành công");
			log.info("Đã thêm nhân viên mới: {}", employee.getUsername());
		} catch (IllegalArgumentException e) {
			log.error("Lỗi khi thêm nhân viên", e);
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
		}
		return "redirect:/admin/employees";
	}

	@GetMapping("/edit/{id}")
	@ResponseBody
	@Transactional
	public ResponseEntity<Map<String, Object>> getEmployee(@PathVariable Long id) {
		try {
			log.info("Fetching employee with ID: {}", id);
			Employee employee = employeeService.getEmployeeById(id);
			if (employee == null) {
				log.warn("Employee not found with ID: {}", id);
				return ResponseEntity.badRequest().body(Map.of(
						"success", false,
						"message", "Không tìm thấy nhân viên"));
			}

			// Create a DTO with only necessary information
			Map<String, Object> employeeData = new HashMap<>();
			employeeData.put("id", employee.getId());
			employeeData.put("username", employee.getUsername());
			employeeData.put("fullName", employee.getFullName());
			employeeData.put("role", employee.getRole());

			// Return success response
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", employeeData);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error fetching employee {}: {}", id, e.getMessage());
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể tải thông tin nhân viên: " + e.getMessage()));
		}
	}

	@PostMapping("/update")
	public String updateEmployee(@ModelAttribute @Validated Employee employee, RedirectAttributes redirectAttributes) {
		if (!hasAdminRole()) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Bạn không có quyền cập nhật nhân viên!");
			return "redirect:/admin/employees";
		}
		try {
			employeeService.updateEmployee(employee);
			redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Cập nhật nhân viên thành công");
		} catch (Exception e) {
			log.error("Lỗi khi cập nhật nhân viên", e);
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Lỗi khi cập nhật nhân viên: " + e.getMessage());
		}
		return "redirect:/admin/employees";
	}

	@PostMapping("/delete/{id}")
	public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		if (!hasAdminRole()) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Bạn không có quyền xóa nhân viên!");
			return "redirect:/admin/employees";
		}
		try {
			employeeService.deleteEmployee(id);
			redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Xóa nhân viên thành công");
			log.info("Đã xóa nhân viên ID: {}", id);
		} catch (Exception e) {
			log.error("Lỗi khi xóa nhân viên {}", id, e);
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Không thể xóa nhân viên: " + e.getMessage());
		}
		return "redirect:/admin/employees";
	}

	@DeleteMapping("/shifts/{employeeId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteEmployeeShifts(@PathVariable Long employeeId) {
		try {
			shiftService.deleteEmployeeShifts(employeeId);
			log.info("Đã xóa ca làm việc của nhân viên ID: {}", employeeId);
			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "Đã xóa tất cả ca làm việc thành công"));
		} catch (Exception e) {
			log.error("Lỗi khi xóa ca làm việc của nhân viên {}", employeeId, e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể xóa ca làm việc: " + e.getMessage()));
		}
	}

	// API endpoints for shifts
	@GetMapping("/shifts/{employeeId}")
	@ResponseBody
	@Transactional(readOnly = true)
	public ResponseEntity<Map<String, Object>> getEmployeeShifts(@PathVariable Long employeeId) {
		try {
			log.info("Fetching shifts for employee ID: {}", employeeId);
			List<Map<String, Object>> shifts = shiftService.getShiftSchedule(employeeId);
			return ResponseEntity.ok(Map.of(
					"success", true,
					"data", shifts));
		} catch (Exception e) {
			log.error("Error fetching shifts for employee {}: {}", employeeId, e.getMessage());
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể tải ca làm việc: " + e.getMessage()));
		}
	}

	@PostMapping("/shifts/{employeeId}")
	@ResponseBody
	@Transactional
	public ResponseEntity<Map<String, Object>> saveEmployeeShifts(
			@PathVariable Long employeeId,
			@RequestBody List<Map<String, Object>> shifts) {
		try {
			log.info("Saving shifts for employee ID: {}", employeeId);
			log.debug("Received shifts data: {}", shifts);

			// Validate employee exists
			Employee employee = employeeService.getEmployeeById(employeeId);
			if (employee == null) {
				return ResponseEntity.badRequest().body(Map.of(
						"success", false,
						"message", "Không tìm thấy nhân viên với ID: " + employeeId));
			}

			// Save shifts
			shiftService.saveEmployeeShifts(employeeId, shifts);

			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "Đã lưu ca làm việc thành công"));

		} catch (Exception e) {
			log.error("Error saving shifts for employee {}: {}", employeeId, e.getMessage(), e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể lưu ca làm việc: " + e.getMessage()));
		}
	}

	// phuong thuc lay lich lam viec
	@GetMapping("/shift-schedule/{employeeId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEmployeeShiftSchedule(@PathVariable Long employeeId) {
		try {
			List<Map<String, Object>> shifts = shiftService.getShiftSchedule(employeeId);
			return ResponseEntity.ok(Map.of(
					"success", true,
					"data", shifts));
		} catch (Exception e) {
			log.error("Lỗi khi lấy lịch làm việc của nhân viên {}", employeeId, e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể lấy lịch làm việc: " + e.getMessage()));
		}
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		log.info("Accessing employee dashboard");
		return "employee/dashboard";
	}

	@GetMapping("/members")
	public String listMembers(Model model) {
		log.info("Accessing member list from employee view");
		model.addAttribute("members", memberService.getAllMembers());
		return "employee/members";
	}

	@GetMapping("/profile")
	public String profile(Model model) {
		log.info("Accessing employee profile");
		return "employee/profile";
	}

	private boolean hasAdminRole() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	}
}