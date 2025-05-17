package com.example.quanlytiemnet.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quanlytiemnet.models.Service;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
	List<Service> findAll();

	List<Service> findByCategory(String category);
}