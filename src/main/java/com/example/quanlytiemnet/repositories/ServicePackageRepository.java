package com.example.quanlytiemnet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quanlytiemnet.models.ServicePackage;

public interface ServicePackageRepository extends JpaRepository<ServicePackage, Integer> {
}