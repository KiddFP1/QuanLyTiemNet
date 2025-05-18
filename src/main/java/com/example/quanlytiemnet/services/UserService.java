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
import java.util.Collections;
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

        // First try to find an employee
        try {
            return loadEmployee(username);
        } catch (UsernameNotFoundException e) {
            // If employee not found, try to find a member
            try {
                return loadMember(username);
            } catch (UsernameNotFoundException ex) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        }
    }

    private UserDetails loadEmployee(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

        String role = employee.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        log.info("Employee {} loaded with role: {}", username, role);

        return User.builder()
                .username(employee.getUsername())
                .password(employee.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private UserDetails loadMember(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        log.info("Member {} loaded with role: ROLE_MEMBER", username);

        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}