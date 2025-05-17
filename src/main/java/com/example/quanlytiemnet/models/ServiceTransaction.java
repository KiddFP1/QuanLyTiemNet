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
@Table(name = "ServiceTransaction")
public class ServiceTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TransactionID")
	private Integer MemberIDTransactionID;

	@ManyToOne
	@JoinColumn(name = "MemberID")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "ServiceID")
	private Service service;

	private Integer quantity;
	private BigDecimal totalAmount;

	@Column(name = "TransactionDate")
	private LocalDateTime transactionDate;
}