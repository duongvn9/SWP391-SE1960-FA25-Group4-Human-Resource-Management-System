package group4.hrms.dto;

import group4.hrms.model.TimesheetPeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho TimesheetPeriod với thông tin bổ sung
 */
public class TimesheetPeriodDto {
    
    private Long id;
    private String name;
    private String code;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String statusDisplay;           // Hiển thị trạng thái tiếng Việt
    private String periodType;
    private String periodTypeDisplay;       // Hiển thị loại kỳ tiếng Việt
    private Integer workingDays;
    private Double standardHours;
    private String description;
    private Long createdBy;
    private String createdByName;           // Tên người tạo
    private LocalDateTime lockedAt;
    private Long lockedBy;
    private String lockedByName;            // Tên người khóa
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int attendanceRecordCount;      // Số bản ghi chấm công trong kỳ
    private int employeeCount;              // Số nhân viên trong kỳ
    
    // Constructors
    public TimesheetPeriodDto() {}
//    
//    public TimesheetPeriodDto(TimesheetPeriod timesheetPeriod) {
//        this.id = timesheetPeriod.getId();
//        this.name = timesheetPeriod.getName();
//        this.code = timesheetPeriod.getCode();
//        this.startDate = timesheetPeriod.getStartDate();
//        this.endDate = timesheetPeriod.getEndDate();
//        this.status = timesheetPeriod.getStatus();
//        this.periodType = timesheetPeriod.getPeriodType();
//        this.workingDays = timesheetPeriod.getWorkingDays();
//        this.standardHours = timesheetPeriod.getStandardHours();
//        this.description = timesheetPeriod.getDescription();
//        this.createdBy = timesheetPeriod.getCreatedBy();
//        this.lockedAt = timesheetPeriod.getLockedAt();
//        this.lockedBy = timesheetPeriod.getLockedBy();
//        this.createdAt = timesheetPeriod.getCreatedAt();
//        this.updatedAt = timesheetPeriod.getUpdatedAt();
//        
//        // Set display values
//        this.statusDisplay = getStatusDisplayText(this.status);
//        this.periodTypeDisplay = getPeriodTypeDisplayText(this.periodType);
//    }
//    
//    // Factory methods
//    public static TimesheetPeriodDto fromEntity(TimesheetPeriod timesheetPeriod) {
//        return new TimesheetPeriodDto(timesheetPeriod);
//    }
//    
//    public TimesheetPeriod toEntity() {
//        TimesheetPeriod timesheetPeriod = new TimesheetPeriod();
//        timesheetPeriod.setId(this.id);
//        timesheetPeriod.setName(this.name);
//        timesheetPeriod.setCode(this.code);
//        timesheetPeriod.setStartDate(this.startDate);
//        timesheetPeriod.setEndDate(this.endDate);
//        timesheetPeriod.setStatus(this.status);
//        timesheetPeriod.setPeriodType(this.periodType);
//        timesheetPeriod.setWorkingDays(this.workingDays);
//        timesheetPeriod.setStandardHours(this.standardHours);
//        timesheetPeriod.setDescription(this.description);
//        timesheetPeriod.setCreatedBy(this.createdBy);
//        timesheetPeriod.setLockedAt(this.lockedAt);
//        timesheetPeriod.setLockedBy(this.lockedBy);
//        timesheetPeriod.setCreatedAt(this.createdAt);
//        timesheetPeriod.setUpdatedAt(this.updatedAt);
//        return timesheetPeriod;
//    }
    
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
        this.statusDisplay = getStatusDisplayText(status);
    }
    
    public String getStatusDisplay() {
        return statusDisplay;
    }
    
    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }
    
    public String getPeriodType() {
        return periodType;
    }
    
    public void setPeriodType(String periodType) {
        this.periodType = periodType;
        this.periodTypeDisplay = getPeriodTypeDisplayText(periodType);
    }
    
    public String getPeriodTypeDisplay() {
        return periodTypeDisplay;
    }
    
    public void setPeriodTypeDisplay(String periodTypeDisplay) {
        this.periodTypeDisplay = periodTypeDisplay;
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
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
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
    
    public String getLockedByName() {
        return lockedByName;
    }
    
    public void setLockedByName(String lockedByName) {
        this.lockedByName = lockedByName;
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
    
    public int getAttendanceRecordCount() {
        return attendanceRecordCount;
    }
    
    public void setAttendanceRecordCount(int attendanceRecordCount) {
        this.attendanceRecordCount = attendanceRecordCount;
    }
    
    public int getEmployeeCount() {
        return employeeCount;
    }
    
    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
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
    
    public boolean canBeEdited() {
        return isOpen();
    }
    
    public boolean containsDate(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    // Formatted display methods
    public String getStartDateFormatted() {
        if (startDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return startDate.format(formatter);
        }
        return "";
    }
    
    public String getEndDateFormatted() {
        if (endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return endDate.format(formatter);
        }
        return "";
    }
    
    public String getDateRangeFormatted() {
        return getStartDateFormatted() + " - " + getEndDateFormatted();
    }
    
    public String getLockedAtFormatted() {
        if (lockedAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return lockedAt.format(formatter);
        }
        return "";
    }
    
    public String getWorkingDaysText() {
        if (workingDays != null) {
            return workingDays + " ngày";
        }
        return "0 ngày";
    }
    
    public String getStandardHoursText() {
        if (standardHours != null) {
            return String.format("%.1f giờ", standardHours);
        }
        return "0 giờ";
    }
    
    public long getTotalDays() {
        if (startDate != null && endDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        return 0;
    }
    
    public String getTotalDaysText() {
        return getTotalDays() + " ngày";
    }
    
    // Helper methods
    private String getStatusDisplayText(String status) {
        if (status == null) return "";
        
        return switch (status) {
            case "OPEN" -> "Mở";
            case "CLOSED" -> "Đóng";
            case "LOCKED" -> "Khóa";
            default -> status;
        };
    }
    
    private String getPeriodTypeDisplayText(String periodType) {
        if (periodType == null) return "";
        
        return switch (periodType) {
            case "MONTHLY" -> "Hàng tháng";
            case "QUARTERLY" -> "Hàng quý";
            case "CUSTOM" -> "Tùy chỉnh";
            default -> periodType;
        };
    }
    
    public String getStatusColor() {
        return switch (status) {
            case "OPEN" -> "success";
            case "CLOSED" -> "warning";
            case "LOCKED" -> "danger";
            default -> "secondary";
        };
    }
}