package com.example.quanlytiemnet.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.quanlytiemnet.models.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
	Optional<AppUser> findByUsername(String username);

	boolean existsByUsername(String username);
}