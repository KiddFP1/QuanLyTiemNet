package com.example.quanlytiemnet.models;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class RevenueReport {
	private List<DailyRevenue> dailyRevenues;
	private BigDecimal totalRevenue;
}