package group4.hrms.dto;

import java.time.LocalDate;

/**
 * Issue representing missing payslip for an employee in a period
 * Requirements: 4.4, 4.5, 4.6
 */
public class MissingPayslipIssue extends PayslipIssue {

    private String reason;
    private boolean hasAttendanceData;
    private boolean hasSalaryData;

    // Constructors
    public MissingPayslipIssue() {
        super();
        this.issueType = "MISSING_PAYSLIP";
    }

    public MissingPayslipIssue(Long userId, String employeeCode, String employeeName,
                              String departmentName, LocalDate periodStart, LocalDate periodEnd) {
        super(userId, employeeCode, employeeName, departmentName, periodStart, periodEnd);
        this.issueType = "MISSING_PAYSLIP";
        this.description = "Employee does not have a payslip for this period";
    }

    // Getters and Setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isHasAttendanceData() {
        return hasAttendanceData;
    }

    public void setHasAttendanceData(boolean hasAttendanceData) {
        this.hasAttendanceData = hasAttendanceData;
    }

    public boolean isHasSalaryData() {
        return hasSalaryData;
    }

    public void setHasSalaryData(boolean hasSalaryData) {
        this.hasSalaryData = hasSalaryData;
    }

    // Override abstract methods
    @Override
    public String getActionLabel() {
        return "Quick Generate";
    }

    @Override
    public String getActionUrl() {
        return "/payslips/quick-generate?userId=" + userId +
               "&periodStart=" + periodStart + "&periodEnd=" + periodEnd;
    }

    @Override
    public String getSeverity() {
        // High priority if employee has both attendance and salary data
        if (hasAttendanceData && hasSalaryData) {
            return "high";
        }
        // Medium priority if missing some data but employee is active
        return "medium";
    }

    // Business methods
    public boolean canGenerate() {
        return hasAttendanceData && hasSalaryData;
    }

    public String getReasonDisplay() {
        if (reason != null && !reason.trim().isEmpty()) {
            return reason;
        }

        if (!hasAttendanceData && !hasSalaryData) {
            return "Missing attendance and salary data";
        } else if (!hasAttendanceData) {
            return "Missing attendance data";
        } else if (!hasSalaryData) {
            return "Missing salary data";
        } else {
            return "Payslip not generated yet";
        }
    }

    public String getStatusIcon() {
        if (canGenerate()) {
            return "fa-exclamation-triangle text-warning";
        } else {
            return "fa-times-circle text-danger";
        }
    }

    public String getStatusText() {
        if (canGenerate()) {
            return "Ready to Generate";
        } else {
            return "Missing Data";
        }
    }

    @Override
    public String getDescription() {
        if (description != null && !description.trim().isEmpty()) {
            return description;
        }
        return getReasonDisplay();
    }
}