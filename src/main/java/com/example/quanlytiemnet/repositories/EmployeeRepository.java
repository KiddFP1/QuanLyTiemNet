package com.example.quanlytiemnet.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quanlytiemnet.models.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	@Query("SELECT e FROM Employee e WHERE e.username = :username")
	Optional<Employee> findByUsername(String username);

	@Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.shifts")
	List<Employee> findAllWithShifts();
}
