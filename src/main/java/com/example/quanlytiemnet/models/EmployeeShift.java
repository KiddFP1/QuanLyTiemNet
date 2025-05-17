package com.example.quanlytiemnet.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "EmployeeShift")
public class EmployeeShift {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer employeeShiftId;

	@ManyToOne
	@JoinColumn(name = "EmployeeID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "ShiftID")
	private WorkShift workShift;

	private Integer dayOfWeek; // 1 = Thứ 2, 2 = Thứ 3, ..., 7 = Chủ nhật
	private Boolean isActive = true;

	// Constructor
	public EmployeeShift() {
	}

	public EmployeeShift(Employee employee, WorkShift workShift, Integer dayOfWeek) {
		this.employee = employee;
		this.workShift = workShift;
		this.dayOfWeek = dayOfWeek;
		this.isActive = true;
	}
}