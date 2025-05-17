package com.example.quanlytiemnet.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quanlytiemnet.models.Employee;
import com.example.quanlytiemnet.models.EmployeeShift;
import com.example.quanlytiemnet.models.WorkShift;

@Repository
public interface EmployeeShiftRepository extends JpaRepository<EmployeeShift, Integer> {
	// Tìm tất cả ca làm việc đang active của một nhân viên
	@Query("SELECT es FROM EmployeeShift es LEFT JOIN FETCH es.workShift WHERE es.employee = :employee AND es.isActive = true")
	List<EmployeeShift> findByEmployeeAndIsActiveTrue(@Param("employee") Employee employee);

	// Tìm ca làm việc của nhân viên theo ngày
	@Query("SELECT es FROM EmployeeShift es LEFT JOIN FETCH es.workShift WHERE es.employee = :employee AND es.dayOfWeek = :dayOfWeek AND es.isActive = true")
	List<EmployeeShift> findByEmployeeAndDayOfWeekAndIsActiveTrue(@Param("employee") Employee employee,
			@Param("dayOfWeek") Integer dayOfWeek);

	// Custom query để lấy lịch làm việc có format cho một nhân viên
	@Query("SELECT NEW map(" +
			"e.fullName as employeeName, " +
			"w.shiftId as shiftId, " +
			"w.shiftName as shiftName, " +
			"es.dayOfWeek as dayOfWeek) " +
			"FROM EmployeeShift es " +
			"JOIN es.employee e " +
			"JOIN es.workShift w " +
			"WHERE es.isActive = true " +
			"AND e.id = :employeeId " +
			"ORDER BY es.dayOfWeek, w.startTime")
	List<Map<String, Object>> findShiftSchedule(@Param("employeeId") Long employeeId);

	// Tìm ca làm việc cụ thể của nhân viên
	@Query("SELECT es FROM EmployeeShift es LEFT JOIN FETCH es.workShift WHERE es.employee = :employee AND es.workShift = :workShift AND es.dayOfWeek = :dayOfWeek")
	EmployeeShift findByEmployeeAndWorkShiftAndDayOfWeek(
			@Param("employee") Employee employee,
			@Param("workShift") WorkShift workShift,
			@Param("dayOfWeek") Integer dayOfWeek);

	// Lấy tất cả ca làm việc của một nhân viên
	@Query("SELECT es FROM EmployeeShift es LEFT JOIN FETCH es.workShift WHERE es.employee = :employee")
	List<EmployeeShift> findByEmployee(@Param("employee") Employee employee);

	// Custom query để lấy tất cả lịch làm việc của nhân viên có format
	@Query("SELECT NEW map(" +
			"e.fullName as employeeName, " +
			"TRIM(w.shiftName) as shiftName, " +
			"es.dayOfWeek as dayOfWeek) " +
			"FROM EmployeeShift es " +
			"JOIN es.employee e " +
			"JOIN es.workShift w " +
			"WHERE es.isActive = true " +
			"ORDER BY es.dayOfWeek, w.startTime")
	List<Map<String, Object>> findAllEmployeeShifts();
}