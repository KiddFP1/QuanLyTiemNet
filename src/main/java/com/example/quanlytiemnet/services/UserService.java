package com.example.quanlytiemnet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.models.Employee;
import com.example.quanlytiemnet.repositories.MemberRepository;
import com.example.quanlytiemnet.repositories.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;
import java.util.Set;
import java.util.HashSet;
import org.springframework.security.core.GrantedAuthority;

@Service
@Primary
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = employee.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(role));

        log.info("User {} loaded with role: {}", username, role);

        return User.builder()
                .username(employee.getUsername())
                .password(employee.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}