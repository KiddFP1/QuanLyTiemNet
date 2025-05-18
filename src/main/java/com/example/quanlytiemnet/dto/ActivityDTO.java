package com.example.quanlytiemnet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ActivityDTO implements Comparable<ActivityDTO> {
    private LocalDateTime timestamp;
    private String type; // "ORDER" or "TOPUP"
    private String title;
    private BigDecimal amount;
    private String status;
    private String note;

    @Override
    public int compareTo(ActivityDTO other) {
        return other.timestamp.compareTo(this.timestamp); // Sort by newest first
    }
}