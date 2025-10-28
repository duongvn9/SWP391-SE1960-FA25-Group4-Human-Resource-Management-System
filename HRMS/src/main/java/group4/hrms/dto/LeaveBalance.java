package group4.hrms.dto;

/**
 * Leave Balance DTO - Thông tin số ngày nghỉ phép của nhân viên
 */
public class LeaveBalance {
    private String leaveTypeCode;
    private String leaveTypeName;
    private int defaultDays;        // Số ngày được nghỉ cơ bản
    private int seniorityBonus;     // Số ngày thêm từ thâm niên
    private int totalAllowed;       // Tổng số ngày được phép nghỉ
    private double usedDays;        // Số ngày đã nghỉ (supports half-day: 0.5, 1.5, etc.)
    private double remainingDays;   // Số ngày còn lại (supports decimal)
    private double pendingDays;     // Số ngày đang chờ duyệt (supports half-day)
    private double availableDays;   // Số ngày có thể xin (remaining - pending)
    private int year;               // Năm áp dụng

    public LeaveBalance() {
    }

    public LeaveBalance(String leaveTypeCode, String leaveTypeName, int defaultDays,
                       int seniorityBonus, double usedDays, int year) {
        this.leaveTypeCode = leaveTypeCode;
        this.leaveTypeName = leaveTypeName;
        this.defaultDays = defaultDays;
        this.seniorityBonus = seniorityBonus;
        this.totalAllowed = defaultDays + seniorityBonus;
        this.usedDays = usedDays;
        this.remainingDays = totalAllowed - usedDays;
        this.pendingDays = 0.0;
        this.availableDays = remainingDays;
        this.year = year;
    }

    public LeaveBalance(String leaveTypeCode, String leaveTypeName, int defaultDays,
                       int seniorityBonus, double usedDays, double pendingDays, int year) {
        this.leaveTypeCode = leaveTypeCode;
        this.leaveTypeName = leaveTypeName;
        this.defaultDays = defaultDays;
        this.seniorityBonus = seniorityBonus;
        this.totalAllowed = defaultDays + seniorityBonus;
        this.usedDays = usedDays;
        this.remainingDays = totalAllowed - usedDays;
        this.pendingDays = pendingDays;
        this.availableDays = remainingDays - pendingDays;
        this.year = year;
    }

    // Getters and Setters
    public String getLeaveTypeCode() {
        return leaveTypeCode;
    }

    public void setLeaveTypeCode(String leaveTypeCode) {
        this.leaveTypeCode = leaveTypeCode;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public int getDefaultDays() {
        return defaultDays;
    }

    public void setDefaultDays(int defaultDays) {
        this.defaultDays = defaultDays;
        this.totalAllowed = this.defaultDays + this.seniorityBonus;
        this.remainingDays = this.totalAllowed - this.usedDays;
    }

    public int getSeniorityBonus() {
        return seniorityBonus;
    }

    public void setSeniorityBonus(int seniorityBonus) {
        this.seniorityBonus = seniorityBonus;
        this.totalAllowed = this.defaultDays + this.seniorityBonus;
        this.remainingDays = this.totalAllowed - this.usedDays;
    }

    public int getTotalAllowed() {
        return totalAllowed;
    }

    public double getUsedDays() {
        return usedDays;
    }

    public void setUsedDays(double usedDays) {
        this.usedDays = usedDays;
        this.remainingDays = this.totalAllowed - this.usedDays;
        this.availableDays = this.remainingDays - this.pendingDays;
    }

    public double getRemainingDays() {
        return remainingDays;
    }

    public double getPendingDays() {
        return pendingDays;
    }

    public void setPendingDays(double pendingDays) {
        this.pendingDays = pendingDays;
        this.availableDays = this.remainingDays - this.pendingDays;
    }

    public double getAvailableDays() {
        return availableDays;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Get percentage of remaining days (for progress bar)
     */
    public int getRemainingPercentage() {
        if (totalAllowed <= 0) {
            return 0;
        }
        return (int) ((remainingDays * 100.0) / totalAllowed);
    }

    @Override
    public String toString() {
        return "LeaveBalance{" +
                "leaveTypeCode='" + leaveTypeCode + '\'' +
                ", leaveTypeName='" + leaveTypeName + '\'' +
                ", defaultDays=" + defaultDays +
                ", seniorityBonus=" + seniorityBonus +
                ", totalAllowed=" + totalAllowed +
                ", usedDays=" + usedDays +
                ", remainingDays=" + remainingDays +
                ", pendingDays=" + pendingDays +
                ", availableDays=" + availableDays +
                ", year=" + year +
                '}';
    }
}
