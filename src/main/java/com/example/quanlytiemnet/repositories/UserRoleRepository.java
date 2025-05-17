package com.example.quanlytiemnet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quanlytiemnet.models.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
	UserRole findByRoleName(String roleName);
}