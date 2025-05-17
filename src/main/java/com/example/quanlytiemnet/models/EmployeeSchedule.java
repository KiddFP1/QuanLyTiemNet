package com.example.quanlytiemnet.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "EmployeeSchedule")
public class EmployeeSchedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer scheduleId;
	private Integer employeeId;
	private String employeeName;
	private Integer dayOfWeek;
	private Integer shiftNumber;
	private String timeRange;
}