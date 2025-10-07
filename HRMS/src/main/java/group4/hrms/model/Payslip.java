package group4.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity Payslip - Phiếu lương của nhân viên
 * Mapping từ bảng payslips trong database
 */
public class Payslip {
    
    // Các field khớp với database schema
    private Long id;
    private Long userId;                    // user_id
    private LocalDate periodStart;          // period_start
    private LocalDate periodEnd;            // period_end
    private String currency;                // currency (NVARCHAR(8))
    private BigDecimal grossAmount;         // gross_amount (DECIMAL(18,2))
    private BigDecimal netAmount;           // net_amount (DECIMAL(18,2))
    private String detailsJson;             // details_json (NVARCHAR(MAX))
    private String filePath;                // file_path (NVARCHAR(1024))
    private String status;                  // status (approved, paid, etc.)
    private LocalDateTime createdAt;        // created_at
    
    // Constructors
    public Payslip() {}
    
    public Payslip(Long userId, LocalDate periodStart, LocalDate periodEnd) {
        this.userId = userId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.status = "approved";
        this.currency = "VND";
        this.grossAmount = BigDecimal.ZERO;
        this.netAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters và Setters
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
    
    public LocalDate getPeriodStart() {
        return periodStart;
    }
    
    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }
    
    public LocalDate getPeriodEnd() {
        return periodEnd;
    }
    
    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BigDecimal getGrossAmount() {
        return grossAmount;
    }
    
    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }
    
    public BigDecimal getNetAmount() {
        return netAmount;
    }
    
    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }
    
    public String getDetailsJson() {
        return detailsJson;
    }
    
    public void setDetailsJson(String detailsJson) {
        this.detailsJson = detailsJson;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    public boolean isApproved() {
        return "approved".equals(this.status);
    }
    
    public boolean isPaid() {
        return "paid".equals(this.status);
    }
    
    public boolean canBeModified() {
        return !"paid".equals(this.status);
    }
    
    @Override
    public String toString() {
        return "Payslip{" +
                "id=" + id +
                ", userId=" + userId +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", status='" + status + '\'' +
                ", grossAmount=" + grossAmount +
                ", netAmount=" + netAmount +
                '}';
    }
}