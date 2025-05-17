package com.example.quanlytiemnet.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class DailyRevenue {
	private LocalDate date;
	private BigDecimal computerRevenue;
	private BigDecimal serviceRevenue;
	private BigDecimal totalRevenue;
}