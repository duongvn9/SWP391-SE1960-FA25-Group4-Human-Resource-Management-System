package group4.hrms.dto;

import group4.hrms.model.EmploymentContract;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO cho EmploymentContract entity
 * 
 * @author Group4
 */
public class EmploymentContractDto {
    private Long id;
    private Long userId;
    private String userFullName;       // Join từ users
    private String contractNo;
    private String contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal baseSalary;
    private String currency;
    private String status;
    private String filePath;
    private String note;
    private Long createdByAccountId;
    private String createdByName;      // Join từ accounts/users
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    private boolean isActive;
    private boolean isExpired;
    private boolean isIndefinite;
    private boolean hasSignedFile;
    
    // Constructors
    public EmploymentContractDto() {}
    
    public EmploymentContractDto(EmploymentContract contract) {
        if (contract != null) {
            this.id = contract.getId();
            this.userId = contract.getUserId();
            this.contractNo = contract.getContractNo();
            this.contractType = contract.getContractType();
            this.startDate = contract.getStartDate();
            this.endDate = contract.getEndDate();
            this.baseSalary = contract.getBaseSalary();
            this.currency = contract.getCurrency();
            this.status = contract.getStatus();
            this.filePath = contract.getFilePath();
            this.note = contract.getNote();
            this.createdByAccountId = contract.getCreatedByAccountId();
            this.createdAt = contract.getCreatedAt();
            this.updatedAt = contract.getUpdatedAt();
            
            // Computed fields
            this.isActive = contract.isActive();
            this.isExpired = contract.isExpired();
            this.isIndefinite = contract.isIndefinite();
            this.hasSignedFile = contract.hasSignedFile();
        }
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
    
    public String getUserFullName() {
        return userFullName;
    }
    
    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
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
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public Long getCreatedByAccountId() {
        return createdByAccountId;
    }
    
    public void setCreatedByAccountId(Long createdByAccountId) {
        this.createdByAccountId = createdByAccountId;
    }
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
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
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean isExpired() {
        return isExpired;
    }
    
    public void setExpired(boolean expired) {
        isExpired = expired;
    }
    
    public boolean isIndefinite() {
        return isIndefinite;
    }
    
    public void setIndefinite(boolean indefinite) {
        isIndefinite = indefinite;
    }
    
    public boolean isHasSignedFile() {
        return hasSignedFile;
    }
    
    public void setHasSignedFile(boolean hasSignedFile) {
        this.hasSignedFile = hasSignedFile;
    }
    
    // Convenience methods
    public boolean isFixedTerm() {
        return "fixed_term".equalsIgnoreCase(this.contractType);
    }
    
    public boolean isProbation() {
        return "probation".equalsIgnoreCase(this.contractType);
    }
    
    /**
     * Convert DTO back to entity
     */
    public EmploymentContract toEntity() {
        EmploymentContract contract = new EmploymentContract();
        contract.setId(this.id);
        contract.setUserId(this.userId);
        contract.setContractNo(this.contractNo);
        contract.setContractType(this.contractType);
        contract.setStartDate(this.startDate);
        contract.setEndDate(this.endDate);
        contract.setBaseSalary(this.baseSalary);
        contract.setCurrency(this.currency);
        contract.setStatus(this.status);
        contract.setFilePath(this.filePath);
        contract.setNote(this.note);
        contract.setCreatedByAccountId(this.createdByAccountId);
        contract.setCreatedAt(this.createdAt);
        contract.setUpdatedAt(this.updatedAt);
        
        return contract;
    }
}