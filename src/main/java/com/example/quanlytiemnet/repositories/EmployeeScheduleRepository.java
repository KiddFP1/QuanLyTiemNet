package com.example.quanlytiemnet.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quanlytiemnet.models.EmployeeSchedule;

@Repository
public interface EmployeeScheduleRepository extends JpaRepository<EmployeeSchedule, Integer> {
	@Query("SELECT s FROM EmployeeSchedule s ORDER BY s.dayOfWeek, s.shiftNumber")
	List<EmployeeSchedule> getFullSchedule();
}