package group4.hrms.dto;

import group4.hrms.model.AttendanceLog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho AttendanceLog với thông tin bổ sung
 */
public class AttendanceLogDto {

    private Long id;
    private Long userId;
    private String userName;                // Username nhân viên
    private String userFullName;            // Họ tên đầy đủ
    private String departmentName;          // Tên phòng ban
    private LocalDate workDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String checkInType;
    private String checkInTypeDisplay;      // Hiển thị tiếng Việt
    private String checkOutType;
    private String checkOutTypeDisplay;     // Hiển thị tiếng Việt
    private Double workingHours;
    private Double overtimeHours;
    private String status;
    private String statusDisplay;           // Hiển thị trạng thái tiếng Việt
    private String notes;
    private String checkInIp;
    private String checkOutIp;
    private String checkInLocation;
    private String checkOutLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Half-day leave information
    private String halfDayLeaveInfo;        // Display text for half-day leave
    private Boolean hasHalfDayLeave;        // Flag indicating half-day leave exists

    // Constructors
    public AttendanceLogDto() {}

    public AttendanceLogDto(AttendanceLog attendanceLog) {
        this.id = attendanceLog.getId();
        this.userId = attendanceLog.getUserId();
        this.workDate = attendanceLog.getWorkDate();
        this.checkInTime = attendanceLog.getCheckInTime();
        this.checkOutTime = attendanceLog.getCheckOutTime();
        this.checkInType = attendanceLog.getCheckInType();
        this.checkOutType = attendanceLog.getCheckOutType();
        this.workingHours = attendanceLog.getWorkingHours();
        this.overtimeHours = attendanceLog.getOvertimeHours();
        this.status = attendanceLog.getStatus();
        this.notes = attendanceLog.getNotes();
        this.checkInIp = attendanceLog.getCheckInIp();
        this.checkOutIp = attendanceLog.getCheckOutIp();
        this.checkInLocation = attendanceLog.getCheckInLocation();
        this.checkOutLocation = attendanceLog.getCheckOutLocation();
        this.createdAt = attendanceLog.getCreatedAt();
        this.updatedAt = attendanceLog.getUpdatedAt();

        // Set display values
        this.statusDisplay = getStatusDisplayText(this.status);
        this.checkInTypeDisplay = getCheckTypeDisplayText(this.checkInType);
        this.checkOutTypeDisplay = getCheckTypeDisplayText(this.checkOutType);
    }

    // Factory methods
    public static AttendanceLogDto fromEntity(AttendanceLog attendanceLog) {
        return new AttendanceLogDto(attendanceLog);
    }

    public AttendanceLog toEntity() {
        AttendanceLog attendanceLog = new AttendanceLog();
        attendanceLog.setId(this.id);
        attendanceLog.setUserId(this.userId);
        attendanceLog.setWorkDate(this.workDate);
        attendanceLog.setCheckInTime(this.checkInTime);
        attendanceLog.setCheckOutTime(this.checkOutTime);
        attendanceLog.setCheckInType(this.checkInType);
        attendanceLog.setCheckOutType(this.checkOutType);
        attendanceLog.setWorkingHours(this.workingHours);
        attendanceLog.setOvertimeHours(this.overtimeHours);
        attendanceLog.setStatus(this.status);
        attendanceLog.setNotes(this.notes);
        attendanceLog.setCheckInIp(this.checkInIp);
        attendanceLog.setCheckOutIp(this.checkOutIp);
        attendanceLog.setCheckInLocation(this.checkInLocation);
        attendanceLog.setCheckOutLocation(this.checkOutLocation);
        attendanceLog.setCreatedAt(this.createdAt);
        attendanceLog.setUpdatedAt(this.updatedAt);
        return attendanceLog;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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
        this.checkInTypeDisplay = getCheckTypeDisplayText(checkInType);
    }

    public String getCheckInTypeDisplay() {
        return checkInTypeDisplay;
    }

    public void setCheckInTypeDisplay(String checkInTypeDisplay) {
        this.checkInTypeDisplay = checkInTypeDisplay;
    }

    public String getCheckOutType() {
        return checkOutType;
    }

    public void setCheckOutType(String checkOutType) {
        this.checkOutType = checkOutType;
        this.checkOutTypeDisplay = getCheckTypeDisplayText(checkOutType);
    }

    public String getCheckOutTypeDisplay() {
        return checkOutTypeDisplay;
    }

    public void setCheckOutTypeDisplay(String checkOutTypeDisplay) {
        this.checkOutTypeDisplay = checkOutTypeDisplay;
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
        this.statusDisplay = getStatusDisplayText(status);
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
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

    public String getHalfDayLeaveInfo() {
        return halfDayLeaveInfo;
    }

    public void setHalfDayLeaveInfo(String halfDayLeaveInfo) {
        this.halfDayLeaveInfo = halfDayLeaveInfo;
    }

    public Boolean getHasHalfDayLeave() {
        return hasHalfDayLeave;
    }

    public void setHasHalfDayLeave(Boolean hasHalfDayLeave) {
        this.hasHalfDayLeave = hasHalfDayLeave;
    }

    // Business methods
    public boolean isPresent() {
        return "PRESENT".equals(this.status);
    }

    public boolean isAbsent() {
        return "ABSENT".equals(this.status);
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

    // Formatted display methods
    public String getWorkDateFormatted() {
        if (workDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return workDate.format(formatter);
        }
        return "";
    }

    public String getCheckInTimeFormatted() {
        if (checkInTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return checkInTime.format(formatter);
        }
        return "";
    }

    public String getCheckOutTimeFormatted() {
        if (checkOutTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return checkOutTime.format(formatter);
        }
        return "";
    }

    public String getWorkingHoursFormatted() {
        if (workingHours != null) {
            return String.format("%.2f giờ", workingHours);
        }
        return "0 giờ";
    }

    public String getOvertimeHoursFormatted() {
        if (overtimeHours != null && overtimeHours > 0) {
            return String.format("%.2f giờ", overtimeHours);
        }
        return "";
    }

    // Helper methods
    private String getStatusDisplayText(String status) {
        if (status == null) return "";

        switch (status) {
            case "PRESENT":
                return "Có mặt";
            case "ABSENT":
                return "Vắng mặt";
            case "LATE":
                return "Đi muộn";
            case "EARLY_LEAVE":
                return "Về sớm";
            default:
                return status;
        }
    }

    private String getCheckTypeDisplayText(String checkType) {
        if (checkType == null) return "";

        switch (checkType) {
            case "NORMAL":
                return "Bình thường";
            case "LATE":
                return "Muộn";
            case "EARLY":
                return "Sớm";
            case "OVERTIME":
                return "Tăng ca";
            default:
                return checkType;
        }
    }
}