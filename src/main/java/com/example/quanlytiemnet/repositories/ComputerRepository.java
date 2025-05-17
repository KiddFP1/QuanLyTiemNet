package com.example.quanlytiemnet.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quanlytiemnet.models.Computer;

public interface ComputerRepository extends JpaRepository<Computer, Integer> {
	List<Computer> findByStatus(String status);
}