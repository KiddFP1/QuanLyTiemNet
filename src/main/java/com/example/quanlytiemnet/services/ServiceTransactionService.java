package com.example.quanlytiemnet.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlytiemnet.models.ServiceTransaction;
import com.example.quanlytiemnet.repositories.ServiceTransactionRepository;

@Service
public class ServiceTransactionService {
	@Autowired
	private ServiceTransactionRepository transactionRepository;

	public ServiceTransaction createTransaction(ServiceTransaction transaction) {
		transaction.setTransactionDate(LocalDateTime.now());
		return transactionRepository.save(transaction);
	}
}