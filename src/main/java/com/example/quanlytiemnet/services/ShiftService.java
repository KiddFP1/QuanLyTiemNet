package com.example.quanlytiemnet.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quanlytiemnet.models.Employee;
import com.example.quanlytiemnet.models.EmployeeShift;
import com.example.quanlytiemnet.models.Shift;
import com.example.quanlytiemnet.models.WorkShift;
import com.example.quanlytiemnet.repositories.EmployeeRepository;
import com.example.quanlytiemnet.repositories.EmployeeShiftRepository;
import com.example.quanlytiemnet.repositories.ShiftRepository;
import com.example.quanlytiemnet.repositories.WorkShiftRepository;

@Service
public class ShiftService {
	private static final Logger logger = LoggerFactory.getLogger(ShiftService.class);
	private static final String EMPLOYEE_NOT_FOUND = "Không tìm thấy nhân viên";
	private static final String SHIFT_NOT_FOUND = "Không tìm thấy ca làm việc";
	private static final String ACTIVE_SHIFT_EXISTS = "Bạn đã có ca làm việc đang hoạt động";
	private static final String UNAUTHORIZED_SHIFT_END = "Bạn không được phép kết thúc ca làm việc này";
	private static final String INVALID_SHIFT_STATUS = "Ca làm việc này không trong trạng thái hoạt động";

	@Autowired
	private EmployeeShiftRepository employeeShiftRepository;

	@Autowired
	private WorkShiftRepository workShiftRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ShiftRepository shiftRepository;

	// Lấy tất cả các ca làm việc
	public List<WorkShift> getAllWorkShifts() {
		return workShiftRepository.findAll();
	}

	// Lấy ca làm việc hiện tại của nhân viên đang đăng nhập
	public List<Shift> getCurrentEmployeeShifts() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		Employee currentEmployee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(EMPLOYEE_NOT_FOUND));

		return shiftRepository.findByEmployee(currentEmployee);
	}

	// Bắt đầu ca làm việc
	@Transactional
	public Shift startShift() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(EMPLOYEE_NOT_FOUND));

		// Check if employee already has an active shift
		List<Shift> activeShifts = shiftRepository.findByEmployeeAndStatus(employee, "STARTED");
		if (!activeShifts.isEmpty()) {
			throw new RuntimeException(ACTIVE_SHIFT_EXISTS);
		}

		Shift shift = new Shift();
		shift.setEmployee(employee);
		shift.setStartTime(LocalDateTime.now());
		shift.setStatus("STARTED");

		return shiftRepository.save(shift);
	}

	// Kết thúc ca làm việc
	@Transactional
	public Shift endShift(Integer shiftId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException(EMPLOYEE_NOT_FOUND));

		Shift shift = shiftRepository.findById(shiftId)
				.orElseThrow(() -> new RuntimeException(SHIFT_NOT_FOUND));

		if (!shift.getEmployee().equals(employee)) {
			throw new RuntimeException(UNAUTHORIZED_SHIFT_END);
		}

		if (!"STARTED".equals(shift.getStatus())) {
			throw new RuntimeException(INVALID_SHIFT_STATUS);
		}

		shift.setEndTime(LocalDateTime.now());
		shift.setStatus("COMPLETED");

		return shiftRepository.save(shift);
	}

	// Lấy lịch làm việc của nhân viên
	public List<EmployeeShift> getEmployeeShifts(Long employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new RuntimeException(EMPLOYEE_NOT_FOUND));
		return employeeShiftRepository.findByEmployee(employee);
	}

	// Lấy lịch làm việc có format
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getShiftSchedule(Long employeeId) {
		try {
			Employee employee = employeeRepository.findById(employeeId)
					.orElseThrow(() -> new RuntimeException(EMPLOYEE_NOT_FOUND));

			List<Map<String, Object>> shiftData = new ArrayList<>();
			List<EmployeeShift> employeeShifts = employeeShiftRepository.findByEmployeeAndIsActiveTrue(employee);

			logger.debug("Found {} active shifts for employee {}", employeeShifts.size(), employeeId);

			for (EmployeeShift shift : employeeShifts) {
				if (shift.getWorkShift() != null) {
					Map<String, Object> shiftInfo = new HashMap<>();
					WorkShift workShift = shift.getWorkShift();
					shiftInfo.put("shiftId", workShift.getShiftId());
					shiftInfo.put("dayOfWeek", shift.getDayOfWeek());
					shiftInfo.put("shiftName", workShift.getShiftName());
					shiftInfo.put("isActive", true);
					logger.debug("Adding shift: {} for day {}", workShift.getShiftName(), shift.getDayOfWeek());
					shiftData.add(shiftInfo);
				}
			}

			return shiftData;
		} catch (Exception e) {
			logger.error("Error getting shift schedule for employee {}: {}", employeeId, e.getMessage(), e);
			throw new RuntimeException("Không thể lấy lịch làm việc: " + e.getMessage());
		}
	}

	// Lưu ca làm việc cho nhân viên
	@Transactional
	public void saveEmployeeShifts(Long employeeId, List<Map<String, Object>> shifts) {
		logger.info("Saving shifts for employee: {}", employeeId);
		logger.debug("Input shifts data: {}", shifts);

		try {
			Employee employee = employeeRepository.findById(employeeId)
					.orElseThrow(() -> new RuntimeException(EMPLOYEE_NOT_FOUND));

			// Delete all existing shifts first
			employeeShiftRepository.deleteAll(employeeShiftRepository.findByEmployee(employee));
			employeeRepository.flush();

			// Add new shifts if any
			if (shifts != null && !shifts.isEmpty()) {
				for (Map<String, Object> shiftData : shifts) {
					Integer shiftId = ((Number) shiftData.get("shiftId")).intValue();
					Integer dayOfWeek = ((Number) shiftData.get("dayOfWeek")).intValue();

					logger.debug("Processing shift: {} for day {}", shiftId, dayOfWeek);

					WorkShift workShift = workShiftRepository.findById(shiftId)
							.orElseThrow(() -> new RuntimeException(SHIFT_NOT_FOUND));

					EmployeeShift employeeShift = new EmployeeShift();
					employeeShift.setEmployee(employee);
					employeeShift.setWorkShift(workShift);
					employeeShift.setDayOfWeek(dayOfWeek);
					employeeShift.setIsActive(true);

					employeeShiftRepository.save(employeeShift);
				}
			}

			employeeRepository.flush();

		} catch (Exception e) {
			logger.error("Error saving shifts: {}", e.getMessage(), e);
			throw new RuntimeException("Lỗi khi lưu ca làm việc: " + e.getMessage());
		}
	}

	@Transactional
	public boolean cancelEmployeeShift(Long employeeId, Integer shiftId, Integer dayOfWeek) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new RuntimeException(EMPLOYEE_NOT_FOUND));

		WorkShift workShift = workShiftRepository.findById(shiftId)
				.orElseThrow(() -> new RuntimeException(SHIFT_NOT_FOUND));

		EmployeeShift employeeShift = employeeShiftRepository.findByEmployeeAndWorkShiftAndDayOfWeek(employee,
				workShift, dayOfWeek);

		if (employeeShift != null) {
			employeeShiftRepository.delete(employeeShift);
			return true;
		}

		return false;
	}

	@Transactional
	public boolean cancelAllEmployeeShifts(Long employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new RuntimeException(EMPLOYEE_NOT_FOUND));

		List<EmployeeShift> shifts = employeeShiftRepository.findByEmployee(employee);
		if (!shifts.isEmpty()) {
			employeeShiftRepository.deleteAll(shifts);
			return true;
		}
		return false;
	}

	@Transactional
	public void deleteEmployeeShifts(Long employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new RuntimeException(EMPLOYEE_NOT_FOUND));

		List<EmployeeShift> employeeShifts = employeeShiftRepository.findByEmployee(employee);
		employeeShiftRepository.deleteAll(employeeShifts);
	}

	public List<Map<String, Object>> getAllEmployeeShifts() {
		return employeeShiftRepository.findAllEmployeeShifts();
	}
}