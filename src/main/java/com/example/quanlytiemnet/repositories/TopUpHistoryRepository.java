package com.example.quanlytiemnet.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quanlytiemnet.models.TopUpHistory;

@Repository
public interface TopUpHistoryRepository extends JpaRepository<TopUpHistory, Integer> {
    List<TopUpHistory> findByTopUpDateBetweenOrderByTopUpDateDesc(LocalDateTime startDate, LocalDateTime endDate);
}