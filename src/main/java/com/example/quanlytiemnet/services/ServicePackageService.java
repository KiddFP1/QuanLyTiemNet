package com.example.quanlytiemnet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlytiemnet.models.ServicePackage;
import com.example.quanlytiemnet.repositories.ServicePackageRepository;

@Service
public class ServicePackageService {
	@Autowired
	private ServicePackageRepository packageRepository;

	public List<ServicePackage> getAllPackages() {
		return packageRepository.findAll();
	}

	public ServicePackage createPackage(ServicePackage pkg) {
		return packageRepository.save(pkg);
	}
}