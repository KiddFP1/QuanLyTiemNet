package com.example.quanlytiemnet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.quanlytiemnet.models.Computer;
import com.example.quanlytiemnet.models.ComputerUsage;
import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.services.ComputerService;
import com.example.quanlytiemnet.services.ComputerUsageService;
import com.example.quanlytiemnet.services.MemberService;

@Controller
@RequestMapping("/usage")
public class ComputerUsageController {
	@Autowired
	private ComputerUsageService usageService;

	@Autowired
	private ComputerService computerService;

	@Autowired
	private MemberService memberService;

	@PostMapping("/start")
	@ResponseBody
	public ComputerUsage startUsage(@RequestParam Integer computerId, @RequestParam Integer memberId) {
		Computer computer = computerService.getComputerById(computerId);
		Member member = memberService.getMemberById(memberId);
		return usageService.startUsage(computer, member);
	}

	@PostMapping("/end/{id}")
	@ResponseBody
	public ComputerUsage endUsage(@PathVariable Integer id) {
		return usageService.endUsage(id);
	}
}