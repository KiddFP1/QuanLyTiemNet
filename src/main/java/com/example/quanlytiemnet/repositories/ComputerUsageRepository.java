package com.example.quanlytiemnet.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quanlytiemnet.models.ComputerUsage;
import com.example.quanlytiemnet.models.Member;

@Repository
public interface ComputerUsageRepository extends JpaRepository<ComputerUsage, Integer> {

	List<ComputerUsage> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

	List<ComputerUsage> findByMemberAndStartTimeBetween(Member member, LocalDateTime start, LocalDateTime end);

	List<ComputerUsage> findByStartTimeBetweenOrderByStartTimeDesc(LocalDateTime startTime, LocalDateTime endTime);
}