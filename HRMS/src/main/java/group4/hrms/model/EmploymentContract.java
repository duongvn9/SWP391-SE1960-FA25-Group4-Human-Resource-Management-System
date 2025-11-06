package group4.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Employment Contract model
 * Represents employment contracts with salary information
 */
public class EmploymentContract {
    private Long id;
    private Long userId;
    private String contractNo;
    private String contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal baseSalary;
    private String currency;
    private String status;
    private String approvalStatus;
    private String note;
    private LocalDateTime createdAt;

    // Additional fields for contract management (safe to add)
    private String filePath;
    private Long createdByAccountId;
    private Long approvedByAccountId;
    private LocalDateTime approvedAt;
    private String rejectedReason;
    private LocalDateTime updatedAt;

    // Constructors
    public EmploymentContract() {
    }

    public EmploymentContract(Long userId, BigDecimal baseSalary, String currency) {
        this.userId = userId;
        this.baseSalary = baseSalary;
        this.currency = currency;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter/Setter for additional fields (safe to add)
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getCreatedByAccountId() {
        return createdByAccountId;
    }

    public void setCreatedByAccountId(Long createdByAccountId) {
        this.createdByAccountId = createdByAccountId;
    }

    public Long getApprovedByAccountId() {
        return approvedByAccountId;
    }

    public void setApprovedByAccountId(Long approvedByAccountId) {
        this.approvedByAccountId = approvedByAccountId;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Additional business methods (safe to add)
    public boolean canBeEditedBy(Long userId) {
        // Only PENDING and REJECTED contracts can be edited, and only by the creator
        if ("pending".equals(approvalStatus) || "rejected".equals(approvalStatus)) {
            return createdByAccountId != null && createdByAccountId.equals(userId);
        }
        // Other statuses cannot be edited
        return false;
    }

    public boolean canBeApproved() {
        return "pending".equals(approvalStatus);
    }

    public boolean canBeRejected() {
        return "pending".equals(approvalStatus);
    }

    public void updateStatusIfExpired() {
        if (endDate != null && endDate.isBefore(LocalDate.now()) && "active".equals(status)) {
            this.status = "expired";
        }
    }

    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }

    public boolean hasSignedFile() {
        return filePath != null && !filePath.trim().isEmpty();
    }

    // Business methods
    public boolean isActive() {
        return "active".equals(status) && "approved".equals(approvalStatus);
    }

    public boolean isValidForDate(LocalDate date) {
        if (date == null) return false;

        boolean afterStart = startDate == null || !date.isBefore(startDate);
        boolean beforeEnd = endDate == null || !date.isAfter(endDate);

        return afterStart && beforeEnd;
    }

    public boolean isIndefinite() {
        return endDate == null;
    }

    @Override
    public String toString() {
        return String.format("EmploymentContract{id=%d, userId=%d, contractNo='%s', baseSalary=%s %s, status='%s'}",
                           id, userId, contractNo, baseSalary, currency, status);
    }
}