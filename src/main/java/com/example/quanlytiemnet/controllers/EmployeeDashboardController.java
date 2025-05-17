package com.example.quanlytiemnet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/admin/employees";
    }
}