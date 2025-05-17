package com.example.quanlytiemnet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.repositories.MemberRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/init")
public class InitController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create-admin")
    public String createAdmin() {
        if (memberRepository.findByUsername("admin").isPresent()) {
            return "Admin đã tồn tại!";
        }

        Member admin = new Member();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setFullName("Administrator");
        admin.setPhone("0123456789");
        admin.setBalance(BigDecimal.ZERO);
        admin.setPoints(0);
        admin.setCreatedDate(LocalDateTime.now());

        memberRepository.save(admin);
        return "Đã tạo tài khoản admin thành công!";
    }
}