package group4.hrms.dto;

import group4.hrms.model.LeaveBalance;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho LeaveBalance với thông tin bổ sung
 */
public class LeaveBalanceDto {
    
    private Long id;
    private Long userId;
    private String userName;                // Username nhân viên
    private String userFullName;            // Họ tên đầy đủ
    private Long leaveTypeId;
    private String leaveTypeName;           // Tên loại nghỉ phép
    private String leaveTypeCode;           // Mã loại nghỉ phép
    private Integer year;
    private Integer totalDays;
    private Integer usedDays;
    private Integer remainingDays;
    private Integer carriedForwardDays;
    private Integer accrualDays;
    private LocalDateTime lastUpdated;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public LeaveBalanceDto() {}
    
    public LeaveBalanceDto(LeaveBalance leaveBalance) {
        this.id = leaveBalance.getId();
        this.userId = leaveBalance.getUserId();
        this.leaveTypeId = leaveBalance.getLeaveTypeId();
        this.year = leaveBalance.getYear();
        this.totalDays = leaveBalance.getTotalDays();
        this.usedDays = leaveBalance.getUsedDays();
        this.remainingDays = leaveBalance.getRemainingDays();
        this.carriedForwardDays = leaveBalance.getCarriedForwardDays();
        this.accrualDays = leaveBalance.getAccrualDays();
        this.lastUpdated = leaveBalance.getLastUpdated();
        this.notes = leaveBalance.getNotes();
        this.createdAt = leaveBalance.getCreatedAt();
        this.updatedAt = leaveBalance.getUpdatedAt();
    }
    
    // Factory methods
    public static LeaveBalanceDto fromEntity(LeaveBalance leaveBalance) {
        return new LeaveBalanceDto(leaveBalance);
    }
    
    public LeaveBalance toEntity() {
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setId(this.id);
        leaveBalance.setUserId(this.userId);
        leaveBalance.setLeaveTypeId(this.leaveTypeId);
        leaveBalance.setYear(this.year);
        leaveBalance.setTotalDays(this.totalDays);
        leaveBalance.setUsedDays(this.usedDays);
        leaveBalance.setRemainingDays(this.remainingDays);
        leaveBalance.setCarriedForwardDays(this.carriedForwardDays);
        leaveBalance.setAccrualDays(this.accrualDays);
        leaveBalance.setLastUpdated(this.lastUpdated);
        leaveBalance.setNotes(this.notes);
        leaveBalance.setCreatedAt(this.createdAt);
        leaveBalance.setUpdatedAt(this.updatedAt);
        return leaveBalance;
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
    
    public Long getLeaveTypeId() {
        return leaveTypeId;
    }
    
    public void setLeaveTypeId(Long leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }
    
    public String getLeaveTypeName() {
        return leaveTypeName;
    }
    
    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }
    
    public String getLeaveTypeCode() {
        return leaveTypeCode;
    }
    
    public void setLeaveTypeCode(String leaveTypeCode) {
        this.leaveTypeCode = leaveTypeCode;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public Integer getTotalDays() {
        return totalDays;
    }
    
    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }
    
    public Integer getUsedDays() {
        return usedDays;
    }
    
    public void setUsedDays(Integer usedDays) {
        this.usedDays = usedDays;
    }
    
    public Integer getRemainingDays() {
        return remainingDays;
    }
    
    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }
    
    public Integer getCarriedForwardDays() {
        return carriedForwardDays;
    }
    
    public void setCarriedForwardDays(Integer carriedForwardDays) {
        this.carriedForwardDays = carriedForwardDays;
    }
    
    public Integer getAccrualDays() {
        return accrualDays;
    }
    
    public void setAccrualDays(Integer accrualDays) {
        this.accrualDays = accrualDays;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    public boolean hasRemainingDays() {
        return remainingDays != null && remainingDays > 0;
    }
    
    public boolean hasUsedAllDays() {
        return remainingDays != null && remainingDays <= 0;
    }
    
    public boolean canTakeLeave(int requestedDays) {
        return hasRemainingDays() && remainingDays >= requestedDays;
    }
    
    public double getUsagePercentage() {
        if (totalDays == null || totalDays == 0) {
            return 0.0;
        }
        return (usedDays * 100.0) / totalDays;
    }
    
    // Formatted display methods
    public String getLastUpdatedFormatted() {
        if (lastUpdated != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return lastUpdated.format(formatter);
        }
        return "";
    }
    
    public String getUsagePercentageFormatted() {
        return String.format("%.1f%%", getUsagePercentage());
    }
    
    public String getTotalDaysText() {
        if (totalDays != null) {
            return totalDays + " ngày";
        }
        return "0 ngày";
    }
    
    public String getUsedDaysText() {
        if (usedDays != null) {
            return usedDays + " ngày";
        }
        return "0 ngày";
    }
    
    public String getRemainingDaysText() {
        if (remainingDays != null) {
            return remainingDays + " ngày";
        }
        return "0 ngày";
    }
    
    public String getCarriedForwardDaysText() {
        if (carriedForwardDays != null && carriedForwardDays > 0) {
            return carriedForwardDays + " ngày (chuyển từ năm trước)";
        }
        return "";
    }
    
    public String getAccrualDaysText() {
        if (accrualDays != null && accrualDays > 0) {
            return accrualDays + " ngày (tích lũy)";
        }
        return "";
    }
    
    /**
     * Màu sắc cho progress bar dựa trên tỷ lệ sử dụng
     */
    public String getUsageProgressColor() {
        double percentage = getUsagePercentage();
        if (percentage <= 50) {
            return "success"; // Xanh lá
        } else if (percentage <= 80) {
            return "warning"; // Vàng
        } else {
            return "danger"; // Đỏ
        }
    }
    
    /**
     * Trạng thái dựa trên số ngày còn lại
     */
    public String getBalanceStatus() {
        if (remainingDays == null || remainingDays <= 0) {
            return "Hết phép";
        } else if (remainingDays <= 2) {
            return "Sắp hết";
        } else if (remainingDays <= 5) {
            return "Ít phép";
        } else {
            return "Đầy đủ";
        }
    }
    
    public String getBalanceStatusColor() {
        String status = getBalanceStatus();
        switch (status) {
            case "Hết phép":
                return "danger";
            case "Sắp hết":
                return "warning";
            case "Ít phép":
                return "info";
            case "Đầy đủ":
                return "success";
            default:
                return "secondary";
        }
    }
}