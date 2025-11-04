package group4.hrms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base class for payslip issues
 * Requirements: 4.4, 4.5, 4.6, 4.7
 */
public abstract class PayslipIssue {

    protected Long userId;
    protected String employeeCode;
    protected String employeeName;
    protected String departmentName;
    protected LocalDate periodStart;
    protected LocalDate periodEnd;
    protected String issueType;
    protected String description;
    protected LocalDateTime detectedAt;

    // Constructors
    public PayslipIssue() {
        this.detectedAt = LocalDateTime.now();
    }

    public PayslipIssue(Long userId, String employeeCode, String employeeName,
                       String departmentName, LocalDate periodStart, LocalDate periodEnd) {
        this();
        this.userId = userId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.departmentName = departmentName;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    // Abstract methods
    public abstract String getActionLabel();
    public abstract String getActionUrl();
    public abstract String getSeverity(); // "high", "medium", "low"

    // Business methods
    public String getPeriodDisplay() {
        if (periodStart != null && periodEnd != null) {
            return periodStart.toString() + " - " + periodEnd.toString();
        }
        return "";
    }

    public boolean isHighPriority() {
        return "high".equals(getSeverity());
    }

    public boolean isMediumPriority() {
        return "medium".equals(getSeverity());
    }

    public boolean isLowPriority() {
        return "low".equals(getSeverity());
    }

    @Override
    public String toString() {
        return String.format("%s{userId=%d, employeeName='%s', period=%s, type='%s'}",
                           getClass().getSimpleName(), userId, employeeName, getPeriodDisplay(), issueType);
    }
}