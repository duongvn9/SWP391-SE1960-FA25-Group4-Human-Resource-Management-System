package group4.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity mapping bảng employment_contracts
 * Hợp đồng lao động của nhân viên
 * 
 * @author Group4
 */
public class EmploymentContract {
    private Long id;
    private Long userId;               // nhân viên
    private String contractNo;         // số hợp đồng
    private String contractType;       // loại hợp đồng: "indefinite", "fixed_term", "probation"
    private LocalDate startDate;       // ngày bắt đầu
    private LocalDate endDate;         // ngày kết thúc (nullable for indefinite)
    private BigDecimal baseSalary;     // lương cơ bản
    private String currency;           // VND, USD, etc.
    private String status;             // active, expired, terminated
    private String filePath;           // file hợp đồng đã ký
    private String note;               // ghi chú
    private Long createdByAccountId;   // ai tạo hợp đồng
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public EmploymentContract() {
        this.status = "active";
        this.currency = "VND";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public EmploymentContract(Long userId, String contractType, LocalDate startDate, BigDecimal baseSalary) {
        this();
        this.userId = userId;
        this.contractType = contractType;
        this.startDate = startDate;
        this.baseSalary = baseSalary;
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
        this.updatedAt = LocalDateTime.now();
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
    
    // Business methods
    public boolean isActive() {
        return "active".equalsIgnoreCase(this.status);
    }
    
    public boolean isExpired() {
        return "expired".equalsIgnoreCase(this.status) || 
               (endDate != null && endDate.isBefore(LocalDate.now()));
    }
    
    public boolean isIndefinite() {
        return "indefinite".equalsIgnoreCase(this.contractType);
    }
    
    public boolean isFixedTerm() {
        return "fixed_term".equalsIgnoreCase(this.contractType);
    }
    
    public boolean isProbation() {
        return "probation".equalsIgnoreCase(this.contractType);
    }
    
    public boolean hasSignedFile() {
        return filePath != null && !filePath.trim().isEmpty();
    }
    
    public void terminate() {
        this.status = "terminated";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void expire() {
        this.status = "expired";
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Tự động cập nhật status sang expired nếu hợp đồng đã hết hạn
     * và status hiện tại không phải là terminated
     */
    public void updateStatusIfExpired() {
        if (endDate != null && 
            !endDate.isAfter(LocalDate.now()) && 
            !"terminated".equalsIgnoreCase(this.status) &&
            !"expired".equalsIgnoreCase(this.status)) {
            this.expire();
        }
    }
    
    @Override
    public String toString() {
        return "EmploymentContract{" +
                "id=" + id +
                ", userId=" + userId +
                ", contractNo='" + contractNo + '\'' +
                ", contractType='" + contractType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", baseSalary=" + baseSalary +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}