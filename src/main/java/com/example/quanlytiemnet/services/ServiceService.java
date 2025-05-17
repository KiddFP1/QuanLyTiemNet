package com.example.quanlytiemnet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.quanlytiemnet.repositories.ServiceRepository;

@org.springframework.stereotype.Service // Use full path for @Service annotation
public class ServiceService {
	@Autowired
	private ServiceRepository serviceRepository;

	public List<com.example.quanlytiemnet.models.Service> getAllServices() {
		return serviceRepository.findAll();
	}

	public com.example.quanlytiemnet.models.Service saveService(com.example.quanlytiemnet.models.Service service) {
		return serviceRepository.save(service);
	}

	public com.example.quanlytiemnet.models.Service getServiceById(Integer id) {
		return serviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found"));
	}

	public void deleteService(Integer id) {
		serviceRepository.deleteById(id);
	}

	public void updateStock(Integer serviceId, Integer quantity) {
		com.example.quanlytiemnet.models.Service service = getServiceById(serviceId);
		service.setStockQuantity(service.getStockQuantity() + quantity);
		serviceRepository.save(service);
	}
}