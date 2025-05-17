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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quanlytiemnet.models.Service;
import com.example.quanlytiemnet.services.ServiceService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/services")
@Slf4j
public class ServiceController {
	@Autowired
	private ServiceService serviceService;

	@GetMapping("")
	public String listServices(Model model) {
		model.addAttribute("services", serviceService.getAllServices());
		return "admin/services";
	}

	@PostMapping("/add")
	public String addService(@ModelAttribute Service service, RedirectAttributes redirectAttributes) {
		try {
			serviceService.saveService(service);
			redirectAttributes.addFlashAttribute("successMessage", "Thêm dịch vụ thành công");
		} catch (Exception e) {
			log.error("Lỗi khi thêm dịch vụ", e);
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm dịch vụ: " + e.getMessage());
		}
		return "redirect:/admin/services";
	}

	@GetMapping("/edit/{id}")
	@ResponseBody
	public Service getService(@PathVariable Integer id) {
		try {
			Service service = serviceService.getServiceById(id);
			log.info("Found service: {}", service); // Debug log
			return service;
		} catch (Exception e) {
			log.error("Error fetching service with id {}: {}", id, e.getMessage());
			throw e;
		}
	}

	@PostMapping("/update")
	public String updateService(@ModelAttribute Service service, RedirectAttributes redirectAttributes) {
		try {
			serviceService.saveService(service);
			redirectAttributes.addFlashAttribute("successMessage", "Cập nhật dịch vụ thành công");
		} catch (Exception e) {
			log.error("Lỗi khi cập nhật dịch vụ", e);
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật dịch vụ: " + e.getMessage());
		}
		return "redirect:/admin/services";
	}

	@PostMapping("/delete/{id}")
	public String deleteService(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			serviceService.deleteService(id);
			redirectAttributes.addFlashAttribute("successMessage", "Xóa dịch vụ thành công");
		} catch (Exception e) {
			log.error("Lỗi khi xóa dịch vụ", e);
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa dịch vụ: " + e.getMessage());
		}
		return "redirect:/admin/services";
	}
}
