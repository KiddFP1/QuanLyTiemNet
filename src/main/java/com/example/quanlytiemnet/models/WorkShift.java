package com.example.quanlytiemnet.models;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "WorkShift")
public class WorkShift {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer shiftId;

	private String shiftName; // Tên ca (Ca 1, Ca 2, Ca 3)
	private LocalTime startTime; // Giờ bắt đầu (6:00, 14:00, 22:00)
	private LocalTime endTime; // Giờ kết thúc (14:00, 22:00, 6:00)
	private String description; // Mô tả (Ca sáng, Ca chiều, Ca đêm)

	@JsonIgnore
	@OneToMany(mappedBy = "workShift")
	private List<EmployeeShift> employeeShifts;

	// Constructor
	public WorkShift() {
	}

	public WorkShift(String shiftName, LocalTime startTime, LocalTime endTime, String description) {
		this.shiftName = shiftName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
	}

	// Tạo chuỗi hiển thị thời gian làm việc
	public String getTimeRange() {
		return String.format("%s-%s", startTime.toString(), endTime.toString());
	}
}