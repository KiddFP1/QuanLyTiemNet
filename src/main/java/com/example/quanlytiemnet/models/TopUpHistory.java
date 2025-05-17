package com.example.quanlytiemnet.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "TopUpHistory")
public class TopUpHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TopUpID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "MemberID", nullable = false)
    private Member member;

    @Column(name = "Amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "TopUpDate", nullable = false)
    private LocalDateTime topUpDate;

    @Column(name = "Note")
    private String note;
}