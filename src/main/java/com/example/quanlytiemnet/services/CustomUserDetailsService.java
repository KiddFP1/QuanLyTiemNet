package com.example.quanlytiemnet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.quanlytiemnet.models.Employee;
import com.example.quanlytiemnet.repositories.EmployeeRepository;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        log.info("Found user: {}, role: {}", username, employee.getRole());

        return User.builder()
                .username(employee.getUsername())
                .password(employee.getPassword())
                .authorities(new SimpleGrantedAuthority(employee.getRole()))
                .build();
    }
}