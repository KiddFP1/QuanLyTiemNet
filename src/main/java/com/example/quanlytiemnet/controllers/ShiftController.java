package com.example.quanlytiemnet.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlytiemnet.models.Shift;
import com.example.quanlytiemnet.services.ShiftService;

@RestController
@RequestMapping("/api/shifts")
@Validated
public class ShiftController {
	private static final Logger logger = LoggerFactory.getLogger(ShiftController.class);

	@Autowired
	private ShiftService shiftService;

	@GetMapping("/all")
	public ResponseEntity<Map<String, Object>> getAllEmployeeShifts() {
		try {
			List<Map<String, Object>> shifts = shiftService.getAllEmployeeShifts();
			return ResponseEntity.ok(Map.of(
					"success", true,
					"data", shifts));
		} catch (Exception e) {
			logger.error("Lỗi khi lấy tất cả ca làm việc", e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể lấy danh sách ca làm việc: " + e.getMessage()));
		}
	}

	@GetMapping("/current")
	public ResponseEntity<Map<String, Object>> getCurrentEmployeeShifts() {
		try {
			List<Shift> shifts = shiftService.getCurrentEmployeeShifts();
			return ResponseEntity.ok(Map.of(
					"success", true,
					"data", shifts));
		} catch (Exception e) {
			logger.error("Lỗi khi lấy ca làm việc hiện tại", e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể lấy ca làm việc hiện tại: " + e.getMessage()));
		}
	}

	@PostMapping("/start")
	public ResponseEntity<Map<String, Object>> startShift() {
		try {
			Shift shift = shiftService.startShift();
			return ResponseEntity.ok(Map.of(
					"success", true,
					"data", shift));
		} catch (Exception e) {
			logger.error("Lỗi khi bắt đầu ca làm việc", e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể bắt đầu ca làm việc: " + e.getMessage()));
		}
	}

	@PostMapping("/end/{shiftId}")
	public ResponseEntity<Map<String, Object>> endShift(@PathVariable Integer shiftId) {
		try {
			if (shiftId == null) {
				return ResponseEntity.badRequest().body(Map.of(
						"success", false,
						"message", "ID ca làm việc không được để trống"));
			}

			Shift shift = shiftService.endShift(shiftId);
			return ResponseEntity.ok(Map.of(
					"success", true,
					"data", shift));
		} catch (Exception e) {
			logger.error("Lỗi khi kết thúc ca làm việc {}", shiftId, e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể kết thúc ca làm việc: " + e.getMessage()));
		}
	}

	@PostMapping("/cancel-shift")
	public ResponseEntity<Map<String, Object>> cancelEmployeeShift(@RequestBody Map<String, Object> payload) {
		try {
			// Validate và chuyển đổi kiểu dữ liệu
			if (!payload.containsKey("employeeId") || !payload.containsKey("shiftId")
					|| !payload.containsKey("dayOfWeek")) {
				return ResponseEntity.badRequest().body(Map.of(
						"success", false,
						"message", "Thiếu thông tin bắt buộc (employeeId, shiftId, dayOfWeek)"));
			}

			Long employeeId = ((Number) payload.get("employeeId")).longValue();
			Integer shiftId = ((Number) payload.get("shiftId")).intValue();
			Integer dayOfWeek = ((Number) payload.get("dayOfWeek")).intValue();

			// Validate giá trị
			if (dayOfWeek < 1 || dayOfWeek > 7) {
				return ResponseEntity.badRequest().body(Map.of(
						"success", false,
						"message", "Ngày trong tuần phải từ 1 đến 7"));
			}

			boolean result = shiftService.cancelEmployeeShift(employeeId, shiftId, dayOfWeek);
			return ResponseEntity.ok(Map.of(
					"success", result,
					"message", result ? "Đã hủy ca làm việc thành công" : "Không tìm thấy ca làm việc để hủy"));
		} catch (Exception e) {
			logger.error("Lỗi khi hủy ca làm việc", e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể hủy ca làm việc: " + e.getMessage()));
		}
	}

	@PostMapping("/cancel-all-shifts/{employeeId}")
	public ResponseEntity<Map<String, Object>> cancelAllEmployeeShifts(@PathVariable Long employeeId) {
		try {
			if (employeeId == null) {
				return ResponseEntity.badRequest().body(Map.of(
						"success", false,
						"message", "ID nhân viên không được để trống"));
			}

			boolean result = shiftService.cancelAllEmployeeShifts(employeeId);
			return ResponseEntity.ok(Map.of(
					"success", result,
					"message", result ? "Đã hủy tất cả ca làm việc thành công" : "Không có ca làm việc nào để hủy"));
		} catch (Exception e) {
			logger.error("Lỗi khi hủy tất cả ca làm việc của nhân viên {}", employeeId, e);
			return ResponseEntity.badRequest().body(Map.of(
					"success", false,
					"message", "Không thể hủy ca làm việc: " + e.getMessage()));
		}
	}
}