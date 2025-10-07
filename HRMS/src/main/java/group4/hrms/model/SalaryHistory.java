package group4.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity mapping bảng salary_history
 * Lịch sử thay đổi lương của nhân viên
 * 
 * @author Group4
 */
public class SalaryHistory {
    private Long id;
    private Long userId;               // nhân viên
    private BigDecimal amount;         // mức lương mới
    private String currency;           // VND, USD, etc.
    private LocalDate effectiveFrom;   // có hiệu lực từ ngày
    private LocalDate effectiveTo;     // có hiệu lực đến ngày (nullable)
    private String reason;             // lý do thay đổi
    private Long createdByAccountId;   // ai tạo record này
    private LocalDateTime createdAt;
    
    // Constructors
    public SalaryHistory() {
        this.currency = "VND";
        this.createdAt = LocalDateTime.now();
    }
    
    public SalaryHistory(Long userId, BigDecimal amount, LocalDate effectiveFrom, String reason) {
        this();
        this.userId = userId;
        this.amount = amount;
        this.effectiveFrom = effectiveFrom;
        this.reason = reason;
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
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }
    
    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
    
    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }
    
    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
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
    
    // Business methods
    public boolean isCurrentlyActive() {
        LocalDate now = LocalDate.now();
        return (effectiveFrom == null || !effectiveFrom.isAfter(now)) &&
               (effectiveTo == null || !effectiveTo.isBefore(now));
    }
    
    public boolean isExpired() {
        return effectiveTo != null && effectiveTo.isBefore(LocalDate.now());
    }
    
    public boolean isFuture() {
        return effectiveFrom != null && effectiveFrom.isAfter(LocalDate.now());
    }
    
    public boolean isIndefinite() {
        return effectiveTo == null;
    }
    
    public void endPeriod(LocalDate endDate) {
        this.effectiveTo = endDate;
    }
    
    @Override
    public String toString() {
        return "SalaryHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", effectiveFrom=" + effectiveFrom +
                ", effectiveTo=" + effectiveTo +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}