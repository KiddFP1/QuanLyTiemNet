package com.example.quanlytiemnet.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Computer")
public class Computer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer computerId;

	private String computerName;
	private String status;
	private String location;
	private LocalDateTime lastMaintenanceDate;

	@OneToMany(mappedBy = "computer")
	private List<ComputerUsage> usages;
}