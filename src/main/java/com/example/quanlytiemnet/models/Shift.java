package com.example.quanlytiemnet.models;

import java.time.LocalDateTime;

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
@Table(name = "Shift")
public class Shift {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer shiftId;

	@ManyToOne
	@JoinColumn(name = "EmployeeID")
	private Employee employee;

	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String status; // STARTED, COMPLETED
}