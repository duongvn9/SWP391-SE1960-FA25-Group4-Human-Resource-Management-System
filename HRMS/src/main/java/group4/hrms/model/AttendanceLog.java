package group4.hrms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity AttendanceLog - Bảng chấm công
 * Ghi lại thời gian vào/ra của nhân viên
 */
public class AttendanceLog {
    
    private Long id;
    private Long userId;                    // ID nhân viên
    private LocalDate workDate;             // Ngày làm việc
    private LocalDateTime checkInTime;      // Thời gian vào
    private LocalDateTime checkOutTime;     // Thời gian ra
    private String checkInType;             // NORMAL, LATE, EARLY
    private String checkOutType;            // NORMAL, EARLY, OVERTIME
    private Double workingHours;            // Số giờ làm việc thực tế
    private Double overtimeHours;           // Số giờ tăng ca
    private String status;                  // PRESENT, ABSENT, LATE, EARLY_LEAVE
    private String notes;                   // Ghi chú
    private String checkInIp;               // IP địa chỉ check in
    private String checkOutIp;              // IP địa chỉ check out
    private String checkInLocation;         // Vị trí check in (GPS)
    private String checkOutLocation;        // Vị trí check out (GPS)
    private LocalDateTime createdAt;        // Thời gian tạo
    private LocalDateTime updatedAt;        // Thời gian cập nhật cuối
    
    // Constructors
    public AttendanceLog() {}
    
    public AttendanceLog(Long userId, LocalDate workDate) {
        this.userId = userId;
        this.workDate = workDate;
        this.status = "ABSENT";
        this.workingHours = 0.0;
        this.overtimeHours = 0.0;
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
    
    public LocalDate getWorkDate() {
        return workDate;
    }
    
    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }
    
    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
    
    public String getCheckInType() {
        return checkInType;
    }
    
    public void setCheckInType(String checkInType) {
        this.checkInType = checkInType;
    }
    
    public String getCheckOutType() {
        return checkOutType;
    }
    
    public void setCheckOutType(String checkOutType) {
        this.checkOutType = checkOutType;
    }
    
    public Double getWorkingHours() {
        return workingHours;
    }
    
    public void setWorkingHours(Double workingHours) {
        this.workingHours = workingHours;
    }
    
    public Double getOvertimeHours() {
        return overtimeHours;
    }
    
    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getCheckInIp() {
        return checkInIp;
    }
    
    public void setCheckInIp(String checkInIp) {
        this.checkInIp = checkInIp;
    }
    
    public String getCheckOutIp() {
        return checkOutIp;
    }
    
    public void setCheckOutIp(String checkOutIp) {
        this.checkOutIp = checkOutIp;
    }
    
    public String getCheckInLocation() {
        return checkInLocation;
    }
    
    public void setCheckInLocation(String checkInLocation) {
        this.checkInLocation = checkInLocation;
    }
    
    public String getCheckOutLocation() {
        return checkOutLocation;
    }
    
    public void setCheckOutLocation(String checkOutLocation) {
        this.checkOutLocation = checkOutLocation;
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
    public boolean isPresent() {
        return "PRESENT".equals(this.status);
    }
    
    public boolean isAbsent() {
        return "ABSENT".equals(this.status);
    }
    
    public boolean isLate() {
        return "LATE".equals(this.status) || "LATE".equals(this.checkInType);
    }
    
    public boolean hasOvertime() {
        return overtimeHours != null && overtimeHours > 0;
    }
    
    public boolean isCheckedIn() {
        return checkInTime != null;
    }
    
    public boolean isCheckedOut() {
        return checkOutTime != null;
    }
    
    public boolean isWorkingDay() {
        return isCheckedIn() && isCheckedOut();
    }
    
    /**
     * Tính toán số giờ làm việc thực tế
     */
    public void calculateWorkingHours() {
        if (checkInTime != null && checkOutTime != null) {
            long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
            this.workingHours = minutes / 60.0;
            
            // Trừ giờ nghỉ trưa (1 tiếng)
            if (this.workingHours > 4) {
                this.workingHours -= 1.0;
            }
        } else {
            this.workingHours = 0.0;
        }
    }
    
    /**
     * Tính toán giờ tăng ca (trên 8 tiếng/ngày)
     */
    public void calculateOvertimeHours() {
        if (workingHours != null && workingHours > 8.0) {
            this.overtimeHours = workingHours - 8.0;
        } else {
            this.overtimeHours = 0.0;
        }
    }
    
    @Override
    public String toString() {
        return "AttendanceLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", workDate=" + workDate +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", status='" + status + '\'' +
                ", workingHours=" + workingHours +
                '}';
    }
}