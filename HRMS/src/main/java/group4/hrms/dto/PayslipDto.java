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

    // New fields for enhanced payslip management
    private BigDecimal baseSalary;          // base_salary
    private BigDecimal otAmount;            // ot_amount
    private BigDecimal latenessDeduction;   // lateness_deduction
    private BigDecimal underHoursDeduction; // under_hours_deduction
    private BigDecimal taxAmount;           // tax_amount
    private Boolean isDirty;                // is_dirty
    private String dirtyReason;             // dirty_reason
    private String dirtyReasonDisplay;      // Hiển thị lý do dirty tiếng Việt
    private LocalDateTime updatedAt;        // updated_at
    private LocalDateTime generatedAt;      // generated_at

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

            // Map new fields
            this.baseSalary = payslip.getBaseSalary();
            this.otAmount = payslip.getOtAmount();
            this.latenessDeduction = payslip.getLatenessDeduction();
            this.underHoursDeduction = payslip.getUnderHoursDeduction();
            this.taxAmount = payslip.getTaxAmount();
            this.isDirty = payslip.getIsDirty();
            this.dirtyReason = payslip.getDirtyReason();
            this.updatedAt = payslip.getUpdatedAt();
            this.generatedAt = payslip.getGeneratedAt();

            // Set display texts
            this.statusDisplay = getStatusDisplayText(this.status);
            this.dirtyReasonDisplay = getDirtyReasonDisplayText(this.dirtyReason);
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

        // Set new fields
        payslip.setBaseSalary(this.baseSalary);
        payslip.setOtAmount(this.otAmount);
        payslip.setLatenessDeduction(this.latenessDeduction);
        payslip.setUnderHoursDeduction(this.underHoursDeduction);
        payslip.setTaxAmount(this.taxAmount);
        payslip.setIsDirty(this.isDirty);
        payslip.setDirtyReason(this.dirtyReason);
        payslip.setUpdatedAt(this.updatedAt);
        payslip.setGeneratedAt(this.generatedAt);

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
        this.dirtyReasonDisplay = getDirtyReasonDisplayText(dirtyReason);
    }

    public String getDirtyReasonDisplay() {
        return dirtyReasonDisplay;
    }

    public void setDirtyReasonDisplay(String dirtyReasonDisplay) {
        this.dirtyReasonDisplay = dirtyReasonDisplay;
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
     * Lấy text hiển thị cho dirty reason
     */
    public String getDirtyReasonDisplayText(String dirtyReason) {
        if (dirtyReason == null || dirtyReason.trim().isEmpty()) return "";

        switch (dirtyReason.toLowerCase()) {
            case "attendance_changed":
                return "Dữ liệu chấm công thay đổi";
            case "overtime_changed":
                return "Dữ liệu làm thêm giờ thay đổi";
            case "leave_changed":
                return "Dữ liệu nghỉ phép thay đổi";
            case "salary_changed":
                return "Dữ liệu lương thay đổi";
            case "contract_changed":
                return "Hợp đồng thay đổi";
            case "policy_changed":
                return "Chính sách tính lương thay đổi";
            default:
                return dirtyReason;
        }
    }

    /**
     * Format số tiền theo locale Việt Nam
     */
    public String getFormattedBaseSalary() {
        if (baseSalary == null) return "0 ₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(baseSalary);
    }

    public String getFormattedOtAmount() {
        if (otAmount == null) return "0 ₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(otAmount);
    }

    public String getFormattedLatenessDeduction() {
        if (latenessDeduction == null) return "0 ₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(latenessDeduction);
    }

    public String getFormattedUnderHoursDeduction() {
        if (underHoursDeduction == null) return "0 ₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(underHoursDeduction);
    }

    public String getFormattedTaxAmount() {
        if (taxAmount == null) return "0 ₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(taxAmount);
    }

    /**
     * Format ngày theo định dạng Việt Nam
     */
    public String getFormattedUpdatedAt() {
        if (updatedAt == null) return "";
        return updatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getFormattedGeneratedAt() {
        if (generatedAt == null) return "Chưa tạo";
        return generatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
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