package group4.hrms.dto;

import group4.hrms.model.EmploymentContract;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * DTO cho EmploymentContract entity
 * 
 * @author Group4
 */
public class EmploymentContractDto {
    private Long id;
    private Long userId;
    private String username;           // Username from session or account
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
    
    // Formatted fields for display
    private String formattedStartDate;
    private String formattedEndDate;
    private String formattedSalary;
    private String contractTypeDisplay;
    private String statusDisplay;
    private String statusColor;
    private String formattedCreatedAt;
    private String formattedUpdatedAt;
    
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
            
            // Format fields for display
            this.formattedStartDate = formatDate(contract.getStartDate());
            this.formattedEndDate = formatDate(contract.getEndDate());
            this.formattedSalary = formatCurrency(contract.getBaseSalary(), contract.getCurrency());
            this.contractTypeDisplay = formatContractType(contract.getContractType());
            this.formattedCreatedAt = formatDateTime(contract.getCreatedAt());
            this.formattedUpdatedAt = formatDateTime(contract.getUpdatedAt());
            
            // Format status with color
            formatStatus(contract.getStatus());
        }
    }
    
    /**
     * Format LocalDate to dd/MM/yyyy
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
    
    /**
     * Format LocalDateTime to dd/MM/yyyy HH:mm:ss
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }
    
    /**
     * Format currency with thousand separator (without currency code)
     */
    private String formatCurrency(BigDecimal amount, String currency) {
        if (amount == null) {
            return "N/A";
        }
        
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        
        return formatter.format(amount);
    }
    
    /**
     * Format contract type to English display text
     */
    private String formatContractType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return "N/A";
        }
        
        switch (type.toLowerCase()) {
            case "indefinite":
                return "Indefinite Contract";
            case "fixed_term":
                return "Fixed-term Contract";
            case "probation":
                return "Probation Contract";
            default:
                return type;
        }
    }
    
    /**
     * Format status with display text and color
     */
    private void formatStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            this.statusDisplay = "N/A";
            this.statusColor = "secondary";
            return;
        }
        
        switch (status.toLowerCase()) {
            case "active":
                this.statusDisplay = "Active";
                this.statusColor = "success";
                break;
            case "expired":
                this.statusDisplay = "Expired";
                this.statusColor = "secondary";
                break;
            case "terminated":
                this.statusDisplay = "Terminated";
                this.statusColor = "danger";
                break;
            default:
                this.statusDisplay = status;
                this.statusColor = "secondary";
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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
    
    // Getters and Setters for formatted fields
    public String getFormattedStartDate() {
        return formattedStartDate;
    }
    
    public void setFormattedStartDate(String formattedStartDate) {
        this.formattedStartDate = formattedStartDate;
    }
    
    public String getFormattedEndDate() {
        return formattedEndDate;
    }
    
    public void setFormattedEndDate(String formattedEndDate) {
        this.formattedEndDate = formattedEndDate;
    }
    
    public String getFormattedSalary() {
        return formattedSalary;
    }
    
    public void setFormattedSalary(String formattedSalary) {
        this.formattedSalary = formattedSalary;
    }
    
    public String getContractTypeDisplay() {
        return contractTypeDisplay;
    }
    
    public void setContractTypeDisplay(String contractTypeDisplay) {
        this.contractTypeDisplay = contractTypeDisplay;
    }
    
    public String getStatusDisplay() {
        return statusDisplay;
    }
    
    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }
    
    public String getStatusColor() {
        return statusColor;
    }
    
    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }
    
    public String getFormattedCreatedAt() {
        return formattedCreatedAt;
    }
    
    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }
    
    public String getFormattedUpdatedAt() {
        return formattedUpdatedAt;
    }
    
    public void setFormattedUpdatedAt(String formattedUpdatedAt) {
        this.formattedUpdatedAt = formattedUpdatedAt;
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