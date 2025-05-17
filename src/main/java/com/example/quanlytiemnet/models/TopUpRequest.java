package com.example.quanlytiemnet.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TopUpRequest")
@Data
public class TopUpRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "MemberID", nullable = false)
    private Member member;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED

    @Column
    private String note;

    @Column
    private LocalDateTime processedDate;

    @ManyToOne
    @JoinColumn(name = "ProcessedByEmployeeID")
    private Employee processedBy;
}