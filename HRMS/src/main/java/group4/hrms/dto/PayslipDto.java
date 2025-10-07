package group4.hrms.dto;

import group4.hrms.model.Payslip;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * DTO cho Payslip với thông tin bổ sung
 * Mapping theo database schema mới
 */
public class PayslipDto {
    
    private Long id;
    private Long userId;
    private String userName;                // Username nhân viên
    private String userFullName;            // Họ tên đầy đủ
    private String userEmployeeId;          // Mã nhân viên
    private String departmentName;          // Tên phòng ban
    private String positionName;            // Tên chức vụ
    
    // Các field khớp với DB schema mới
    private LocalDate periodStart;          // period_start
    private LocalDate periodEnd;            // period_end
    private String currency;                // currency
    private BigDecimal grossAmount;         // gross_amount
    private BigDecimal netAmount;           // net_amount
    private String detailsJson;             // details_json
    private String filePath;                // file_path
    private String status;                  // status
    private String statusDisplay;           // Hiển thị trạng thái tiếng Việt
    private LocalDateTime createdAt;        // created_at
    
    // Constructors
    public PayslipDto() {}
    
    public PayslipDto(Payslip payslip) {
        if (payslip != null) {
            this.id = payslip.getId();
            this.userId = payslip.getUserId();
            this.periodStart = payslip.getPeriodStart();
            this.periodEnd = payslip.getPeriodEnd();
            this.currency = payslip.getCurrency();
            this.grossAmount = payslip.getGrossAmount();
            this.netAmount = payslip.getNetAmount();
            this.detailsJson = payslip.getDetailsJson();
            this.filePath = payslip.getFilePath();
            this.status = payslip.getStatus();
            this.createdAt = payslip.getCreatedAt();
            
            // Set status display
            this.statusDisplay = getStatusDisplayText(this.status);
        }
    }
    
    // Convert to entity
    public Payslip toEntity() {
        Payslip payslip = new Payslip();
        payslip.setId(this.id);
        payslip.setUserId(this.userId);
        payslip.setPeriodStart(this.periodStart);
        payslip.setPeriodEnd(this.periodEnd);
        payslip.setCurrency(this.currency);
        payslip.setGrossAmount(this.grossAmount);
        payslip.setNetAmount(this.netAmount);
        payslip.setDetailsJson(this.detailsJson);
        payslip.setFilePath(this.filePath);
        payslip.setStatus(this.status);
        payslip.setCreatedAt(this.createdAt);
        
        return payslip;
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
    
    public String getUserEmployeeId() {
        return userEmployeeId;
    }
    
    public void setUserEmployeeId(String userEmployeeId) {
        this.userEmployeeId = userEmployeeId;
    }
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public String getPositionName() {
        return positionName;
    }
    
    public void setPositionName(String positionName) {
        this.positionName = positionName;
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
        this.statusDisplay = getStatusDisplayText(status);
    }
    
    public String getStatusDisplay() {
        return statusDisplay;
    }
    
    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
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
    
    /**
     * Lấy text hiển thị cho status
     */
    public String getStatusDisplayText(String status) {
        if (status == null) return "";
        
        switch (status.toLowerCase()) {
            case "approved":
                return "Đã duyệt";
            case "paid":
                return "Đã thanh toán";
            case "draft":
                return "Nháp";
            case "cancelled":
                return "Đã hủy";
            default:
                return status;
        }
    }
    
    /**
     * Format số tiền theo locale Việt Nam
     */
    public String getFormattedGrossAmount() {
        if (grossAmount == null) return "0 ₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(grossAmount);
    }
    
    public String getFormattedNetAmount() {
        if (netAmount == null) return "0 ₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(netAmount);
    }
    
    /**
     * Format ngày theo định dạng Việt Nam
     */
    public String getFormattedPeriodStart() {
        if (periodStart == null) return "";
        return periodStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    public String getFormattedPeriodEnd() {
        if (periodEnd == null) return "";
        return periodEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    /**
     * Lấy tên kỳ lương (tháng/năm)
     */
    public String getPeriodName() {
        if (periodStart == null || periodEnd == null) return "";
        
        // Nếu cùng tháng thì hiển thị "Tháng MM/yyyy"
        if (periodStart.getMonthValue() == periodEnd.getMonthValue() && 
            periodStart.getYear() == periodEnd.getYear()) {
            return String.format("Tháng %02d/%d", periodStart.getMonthValue(), periodStart.getYear());
        }
        
        // Nếu khác tháng thì hiển thị khoảng thời gian
        return String.format("%s - %s", getFormattedPeriodStart(), getFormattedPeriodEnd());
    }
    
    @Override
    public String toString() {
        return "PayslipDto{" +
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