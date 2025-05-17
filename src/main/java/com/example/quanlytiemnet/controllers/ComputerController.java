package com.example.quanlytiemnet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.quanlytiemnet.models.Computer;
import com.example.quanlytiemnet.services.ComputerService;

@Controller
@RequestMapping("/admin/computers")
public class ComputerController {
	@Autowired
	private ComputerService computerService;

	@GetMapping
	public String listComputers(Model model) {
		model.addAttribute("computers", computerService.getAllComputers());
		return "admin/computers";
	}

	@PostMapping("/add")
	public String addComputer(@ModelAttribute Computer computer) {
		computerService.saveComputer(computer);
		return "redirect:/admin/computers";
	}

	@GetMapping("/edit/{id}")
	@ResponseBody
	public Computer getComputer(@PathVariable Integer id) {
		return computerService.getComputerById(id);
	}

	@PostMapping("/update/{id}")
	public String updateComputer(@PathVariable Integer id, @ModelAttribute Computer computer) {
		computer.setComputerId(id);
		computerService.saveComputer(computer);
		return "redirect:/admin/computers";
	}

	@PostMapping("/delete/{id}")
	public String deleteComputer(@PathVariable Integer id) {
		computerService.deleteComputer(id);
		return "redirect:/admin/computers";
	}
}