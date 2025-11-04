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

    // New fields for enhanced payslip management
    private BigDecimal baseSalary;          // base_salary (DECIMAL(18,2))
    private BigDecimal otAmount;            // ot_amount (DECIMAL(18,2))
    private BigDecimal latenessDeduction;   // lateness_deduction (DECIMAL(18,2))
    private BigDecimal underHoursDeduction; // under_hours_deduction (DECIMAL(18,2))
    private BigDecimal taxAmount;           // tax_amount (DECIMAL(18,2))
    private Boolean isDirty;                // is_dirty (TINYINT(1))
    private String dirtyReason;             // dirty_reason (VARCHAR(255))
    private LocalDateTime updatedAt;        // updated_at
    private LocalDateTime generatedAt;      // generated_at

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
        this.baseSalary = BigDecimal.ZERO;
        this.otAmount = BigDecimal.ZERO;
        this.latenessDeduction = BigDecimal.ZERO;
        this.underHoursDeduction = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.isDirty = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getOtAmount() {
        return otAmount;
    }

    public void setOtAmount(BigDecimal otAmount) {
        this.otAmount = otAmount;
    }

    public BigDecimal getLatenessDeduction() {
        return latenessDeduction;
    }

    public void setLatenessDeduction(BigDecimal latenessDeduction) {
        this.latenessDeduction = latenessDeduction;
    }

    public BigDecimal getUnderHoursDeduction() {
        return underHoursDeduction;
    }

    public void setUnderHoursDeduction(BigDecimal underHoursDeduction) {
        this.underHoursDeduction = underHoursDeduction;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(Boolean isDirty) {
        this.isDirty = isDirty;
    }

    public String getDirtyReason() {
        return dirtyReason;
    }

    public void setDirtyReason(String dirtyReason) {
        this.dirtyReason = dirtyReason;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
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

    public boolean isDirty() {
        return Boolean.TRUE.equals(this.isDirty);
    }

    public boolean isGenerated() {
        return this.generatedAt != null;
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