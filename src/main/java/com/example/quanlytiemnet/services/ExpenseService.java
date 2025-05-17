package com.example.quanlytiemnet.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlytiemnet.models.Expense;
import com.example.quanlytiemnet.repositories.ExpenseRepository;

@Service
public class ExpenseService {
	@Autowired
	private ExpenseRepository expenseRepository;

	public List<Expense> getExpensesByDateRange(LocalDateTime start, LocalDateTime end) {
		return expenseRepository.findByExpenseDateBetween(start, end);
	}
}