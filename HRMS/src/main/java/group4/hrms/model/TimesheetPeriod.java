package group4.hrms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity TimesheetPeriod - Kỳ chấm công
 * Quản lý các kỳ tính lương (tháng, quý)
 */
public class TimesheetPeriod {
    
    private Long id;
    private String name;                    // Tên kỳ (VD: "Tháng 10/2024")
    private String code;                    // Mã kỳ (VD: "202410")
    private LocalDate startDate;            // Ngày bắt đầu kỳ
    private LocalDate endDate;              // Ngày kết thúc kỳ
    private String status;                  // OPEN, CLOSED, LOCKED
    private String periodType;              // MONTHLY, QUARTERLY, CUSTOM
    private Integer workingDays;            // Số ngày làm việc trong kỳ
    private Double standardHours;           // Số giờ chuẩn trong kỳ
    private String description;             // Mô tả
    private Long createdBy;                 // Người tạo
    private LocalDateTime lockedAt;         // Thời gian khóa kỳ
    private Long lockedBy;                  // Người khóa kỳ
    private LocalDateTime createdAt;        // Thời gian tạo
    private LocalDateTime updatedAt;        // Thời gian cập nhật cuối
    
    // Constructors
    public TimesheetPeriod() {}
    
    public TimesheetPeriod(String name, String code, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "OPEN";
        this.periodType = "MONTHLY";
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPeriodType() {
        return periodType;
    }
    
    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }
    
    public Integer getWorkingDays() {
        return workingDays;
    }
    
    public void setWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
    }
    
    public Double getStandardHours() {
        return standardHours;
    }
    
    public void setStandardHours(Double standardHours) {
        this.standardHours = standardHours;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getLockedAt() {
        return lockedAt;
    }
    
    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }
    
    public Long getLockedBy() {
        return lockedBy;
    }
    
    public void setLockedBy(Long lockedBy) {
        this.lockedBy = lockedBy;
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
    public boolean isOpen() {
        return "OPEN".equals(this.status);
    }
    
    public boolean isClosed() {
        return "CLOSED".equals(this.status);
    }
    
    public boolean isLocked() {
        return "LOCKED".equals(this.status);
    }
    
    public boolean isMonthly() {
        return "MONTHLY".equals(this.periodType);
    }
    
    public boolean isQuarterly() {
        return "QUARTERLY".equals(this.periodType);
    }
    
    public boolean canBeEdited() {
        return isOpen();
    }
    
    public boolean canBeClosed() {
        return isOpen();
    }
    
    public boolean canBeLocked() {
        return isClosed();
    }
    
    /**
     * Kiểm tra ngày có thuộc kỳ này không
     */
    public boolean containsDate(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    /**
     * Tính số ngày trong kỳ
     */
    public long getTotalDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    /**
     * Tính số giờ chuẩn (8 giờ/ngày * số ngày làm việc)
     */
    public void calculateStandardHours() {
        if (workingDays != null) {
            this.standardHours = workingDays * 8.0;
        }
    }
    
    @Override
    public String toString() {
        return "TimesheetPeriod{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", periodType='" + periodType + '\'' +
                '}';
    }
}