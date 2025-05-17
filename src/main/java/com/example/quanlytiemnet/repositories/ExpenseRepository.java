package com.example.quanlytiemnet.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quanlytiemnet.models.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
	List<Expense> findByExpenseDateBetween(LocalDateTime start, LocalDateTime end);
}