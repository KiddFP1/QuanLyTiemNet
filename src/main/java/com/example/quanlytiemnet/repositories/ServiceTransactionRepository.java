package com.example.quanlytiemnet.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.models.ServiceTransaction;

public interface ServiceTransactionRepository extends JpaRepository<ServiceTransaction, Integer> {
	List<ServiceTransaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);

	List<ServiceTransaction> findByMemberAndTransactionDateBetween(Member member, LocalDateTime start,
			LocalDateTime end);
}