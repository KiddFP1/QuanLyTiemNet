package com.example.quanlytiemnet.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating; // 1-5 sao

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_issue_report")
    private Boolean isIssueReport = false;

    @Column(name = "issue_type")
    @Enumerated(EnumType.STRING)
    private IssueType issueType;

    @Column(name = "issue_status")
    @Enumerated(EnumType.STRING)
    private IssueStatus issueStatus;

    public enum IssueType {
        COMPUTER, // Sự cố máy tính
        NETWORK, // Sự cố mạng
        SERVICE, // Sự cố dịch vụ
        OTHER // Khác
    }

    public enum IssueStatus {
        PENDING, // Đang chờ xử lý
        IN_PROGRESS, // Đang xử lý
        RESOLVED, // Đã giải quyết
        CLOSED // Đã đóng
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isIssueReport) {
            issueStatus = IssueStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsIssueReport() {
        return isIssueReport;
    }

    public void setIsIssueReport(Boolean isIssueReport) {
        this.isIssueReport = isIssueReport;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public IssueStatus getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(IssueStatus issueStatus) {
        this.issueStatus = issueStatus;
    }
}