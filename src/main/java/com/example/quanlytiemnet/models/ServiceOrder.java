package com.example.quanlytiemnet.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ServiceOrder")
@Data
public class ServiceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "MemberID", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "ServiceID", nullable = false)
    private Service service;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, CANCELLED

    @Column
    private String note;

    @ManyToOne
    @JoinColumn(name = "ProcessedByEmployeeID")
    private Employee processedBy;
}