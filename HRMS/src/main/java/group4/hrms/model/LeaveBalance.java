package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity LeaveBalance - Số dư nghỉ phép của nhân viên
 * Theo dõi số ngày nghỉ phép còn lại của từng nhân viên theo từng loại
 */
public class LeaveBalance {
    
    private Long id;
    private Long userId;                    // ID nhân viên
    private Long leaveTypeId;               // ID loại nghỉ phép
    private Integer year;                   // Năm áp dụng
    private Integer totalDays;              // Tổng số ngày được phép nghỉ trong năm
    private Integer usedDays;               // Số ngày đã sử dụng
    private Integer remainingDays;          // Số ngày còn lại
    private Integer carriedForwardDays;     // Số ngày chuyển từ năm trước
    private Integer accrualDays;            // Số ngày tích lũy được (theo tháng)
    private LocalDateTime lastUpdated;      // Lần cập nhật cuối
    private String notes;                   // Ghi chú
    private LocalDateTime createdAt;        // Thời gian tạo
    private LocalDateTime updatedAt;        // Thời gian cập nhật cuối
    
    // Constructors
    public LeaveBalance() {}
    
    public LeaveBalance(Long userId, Long leaveTypeId, Integer year, Integer totalDays) {
        this.userId = userId;
        this.leaveTypeId = leaveTypeId;
        this.year = year;
        this.totalDays = totalDays;
        this.usedDays = 0;
        this.remainingDays = totalDays;
        this.carriedForwardDays = 0;
        this.accrualDays = 0;
        this.lastUpdated = LocalDateTime.now();
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
    
    public Long getLeaveTypeId() {
        return leaveTypeId;
    }
    
    public void setLeaveTypeId(Long leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
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
    
    /**
     * Sử dụng số ngày nghỉ phép
     */
    public boolean useLeave(int days) {
        if (canTakeLeave(days)) {
            this.usedDays += days;
            this.remainingDays -= days;
            this.lastUpdated = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    /**
     * Hoàn trả số ngày nghỉ phép (khi cancel request)
     */
    public void returnLeave(int days) {
        this.usedDays = Math.max(0, this.usedDays - days);
        this.remainingDays += days;
        this.lastUpdated = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Tính toán lại số ngày còn lại
     */
    public void recalculateBalance() {
        this.remainingDays = this.totalDays - this.usedDays;
        this.lastUpdated = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Thêm ngày tích lũy theo tháng
     */
    public void addAccrualDays(int days) {
        this.accrualDays += days;
        this.totalDays += days;
        this.remainingDays += days;
        this.lastUpdated = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Thêm ngày chuyển từ năm trước
     */
    public void addCarriedForwardDays(int days) {
        this.carriedForwardDays += days;
        this.totalDays += days;
        this.remainingDays += days;
        this.lastUpdated = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Tính phần trăm đã sử dụng
     */
    public double getUsagePercentage() {
        if (totalDays == null || totalDays == 0) {
            return 0.0;
        }
        return (usedDays * 100.0) / totalDays;
    }
    
    @Override
    public String toString() {
        return "LeaveBalance{" +
                "id=" + id +
                ", userId=" + userId +
                ", leaveTypeId=" + leaveTypeId +
                ", year=" + year +
                ", totalDays=" + totalDays +
                ", usedDays=" + usedDays +
                ", remainingDays=" + remainingDays +
                '}';
    }
}