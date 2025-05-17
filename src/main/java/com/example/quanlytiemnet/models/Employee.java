package com.example.quanlytiemnet.models;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.Data;

@Data
@Entity
@Table(name = "Employee")
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EmployeeID")
	private Long id;

	@Column(name = "Username")
	private String username;

	@Column(name = "Password")
	private String password;

	@Column(name = "FullName")
	private String fullName;

	@Column(name = "Role")
	private String role;

	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private Set<EmployeeShift> shifts = new HashSet<>();

	public Set<EmployeeShift> getShifts() {
		if (shifts == null) {
			shifts = new HashSet<>();
		}
		return shifts;
	}

	public void setShifts(Set<EmployeeShift> shifts) {
		this.shifts = shifts != null ? shifts : new HashSet<>();
	}

	public void addShift(EmployeeShift shift) {
		if (shifts == null) {
			shifts = new HashSet<>();
		}
		shifts.add(shift);
		shift.setEmployee(this);
	}

	public void removeShift(EmployeeShift shift) {
		if (shifts != null) {
			shifts.remove(shift);
			shift.setEmployee(null);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}