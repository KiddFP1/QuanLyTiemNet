package com.example.quanlytiemnet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quanlytiemnet.models.WorkShift;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Integer> {
	// Các phương thức mặc định từ JpaRepository đã đủ dùng
}