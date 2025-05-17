package com.example.quanlytiemnet.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quanlytiemnet.models.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	Optional<Member> findByUsername(String username);

	List<Member> findAll();
}