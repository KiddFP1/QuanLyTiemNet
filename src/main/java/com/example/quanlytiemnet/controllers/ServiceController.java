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

import com.example.quanlytiemnet.services.ServiceService;

@Controller
@RequestMapping("/admin/services")
public class ServiceController {
	@Autowired
	private ServiceService serviceService;

	@GetMapping("")
	public String listServices(Model model) {
		model.addAttribute("services", serviceService.getAllServices());
		return "admin/services";
	}

	@PostMapping("/add")
	public String addService(@ModelAttribute("service") com.example.quanlytiemnet.models.Service service) {
		serviceService.saveService(service);
		return "redirect:/admin/services";
	}

	@GetMapping("/edit/{id}")
	@ResponseBody
	public com.example.quanlytiemnet.models.Service getService(@PathVariable Integer id) {
		return serviceService.getServiceById(id);
	}

	@PostMapping("/update/{id}")
	public String updateService(@PathVariable Integer id,
			@ModelAttribute("service") com.example.quanlytiemnet.models.Service service) {
		service.setServiceId(id);
		serviceService.saveService(service);
		return "redirect:/admin/services";
	}

	@PostMapping("/delete/{id}")
	public String deleteService(@PathVariable Integer id) {
		serviceService.deleteService(id);
		return "redirect:/admin/services";
	}
}
