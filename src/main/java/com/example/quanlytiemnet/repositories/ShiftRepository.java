package com.example.quanlytiemnet.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quanlytiemnet.models.Employee;
import com.example.quanlytiemnet.models.Shift;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
	List<Shift> findByEmployee(Employee employee);

	List<Shift> findByEmployeeAndStatus(Employee employee, String status);
}