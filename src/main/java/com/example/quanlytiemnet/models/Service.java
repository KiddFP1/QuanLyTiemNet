package com.example.quanlytiemnet.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;

@Entity
@Data
@Table(name = "Service")
public class Service {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer serviceId;
	private String serviceName;
	private BigDecimal price;

	@Column(columnDefinition = "NVARCHAR(50) COLLATE Vietnamese_CI_AS")
	private String category;

	private Integer stockQuantity;

	@OneToMany(mappedBy = "service")
	@JsonIgnore
	private List<ServiceTransaction> transactions;
}