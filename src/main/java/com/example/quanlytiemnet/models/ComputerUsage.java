package com.example.quanlytiemnet.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
@Table(name = "computer_usage") // Sửa tên bảng thành lowercase
public class ComputerUsage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usage_id") // Thêm annotation Column
	private Integer usageId;

	@ManyToOne
	@JoinColumn(name = "computer_id") // Sửa tên cột thành lowercase
	private Computer computer;

	@ManyToOne
	@JoinColumn(name = "member_id") // Sửa tên cột thành lowercase
	private Member member;

	@Column(name = "start_time") // Thêm annotation Column
	private LocalDateTime startTime;

	@Column(name = "end_time") // Thêm annotation Column
	private LocalDateTime endTime;

	@Column(name = "total_amount") // Thêm annotation Column
	private BigDecimal totalAmount;
}