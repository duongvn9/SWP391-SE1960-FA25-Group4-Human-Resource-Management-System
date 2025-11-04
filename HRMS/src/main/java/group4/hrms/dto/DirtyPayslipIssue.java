package group4.hrms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Issue representing payslip that needs regeneration due to data changes
 * Requirements: 4.4, 4.5, 4.7
 */
public class DirtyPayslipIssue extends PayslipIssue {

    private Long payslipId;
    private String dirtyReason;
    private LocalDateTime lastChangedAt;
    private LocalDateTime lastGeneratedAt;

    // Constructors
    public DirtyPayslipIssue() {
        super();
        this.issueType = "DIRTY_PAYSLIP";
    }

    public DirtyPayslipIssue(Long payslipId, Long userId, String employeeCode, String employeeName,
                            String departmentName, LocalDate periodStart, LocalDate periodEnd) {
        super(userId, employeeCode, employeeName, departmentName, periodStart, periodEnd);
        this.payslipId = payslipId;
        this.issueType = "DIRTY_PAYSLIP";
        this.description = "Payslip needs regeneration due to data changes";
    }

    // Getters and Setters
    public Long getPayslipId() {
        return payslipId;
    }

    public void setPayslipId(Long payslipId) {
        this.payslipId = payslipId;
    }

    public String getDirtyReason() {
        return dirtyReason;
    }

    public void setDirtyReason(String dirtyReason) {
        this.dirtyReason = dirtyReason;
    }

    public LocalDateTime getLastChangedAt() {
        return lastChangedAt;
    }

    public void setLastChangedAt(LocalDateTime lastChangedAt) {
        this.lastChangedAt = lastChangedAt;
    }

    public LocalDateTime getLastGeneratedAt() {
        return lastGeneratedAt;
    }

    public void setLastGeneratedAt(LocalDateTime lastGeneratedAt) {
        this.lastGeneratedAt = lastGeneratedAt;
    }

    // Override abstract methods
    @Override
    public String getActionLabel() {
        return "Quick Regenerate";
    }

    @Override
    public String getActionUrl() {
        return "/payslips/quick-regenerate?payslipId=" + payslipId;
    }

    @Override
    public String getSeverity() {
        if (lastChangedAt != null && lastGeneratedAt != null) {
            // High priority if changed more than 7 days ago
            long daysSinceChange = java.time.temporal.ChronoUnit.DAYS.between(lastChangedAt, LocalDateTime.now());
            if (daysSinceChange > 7) {
                return "high";
            } else if (daysSinceChange > 3) {
                return "medium";
            }
        }
        return "low";
    }

    // Business methods
    public String getDirtyReasonDisplay() {
        if (dirtyReason != null && !dirtyReason.trim().isEmpty()) {
            return dirtyReason;
        }
        return "Data changed - regeneration required";
    }

    public String getLastChangedAtFormatted() {
        if (lastChangedAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return lastChangedAt.format(formatter);
        }
        return "";
    }

    public String getLastGeneratedAtFormatted() {
        if (lastGeneratedAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return lastGeneratedAt.format(formatter);
        }
        return "";
    }

    public long getDaysSinceChange() {
        if (lastChangedAt != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(lastChangedAt, LocalDateTime.now());
        }
        return 0;
    }

    public String getStatusIcon() {
        String severity = getSeverity();
        switch (severity) {
            case "high":
                return "fa-exclamation-circle text-danger";
            case "medium":
                return "fa-exclamation-triangle text-warning";
            default:
                return "fa-info-circle text-info";
        }
    }

    public String getStatusText() {
        String severity = getSeverity();
        switch (severity) {
            case "high":
                return "Urgent Regeneration";
            case "medium":
                return "Needs Regeneration";
            default:
                return "Recently Changed";
        }
    }

    public String getChangeTypeFromReason() {
        if (dirtyReason == null) {
            return "Unknown";
        }

        String reason = dirtyReason.toLowerCase();
        if (reason.contains("attendance")) {
            return "Attendance";
        } else if (reason.contains("overtime") || reason.contains("ot")) {
            return "Overtime";
        } else if (reason.contains("leave")) {
            return "Leave";
        } else if (reason.contains("salary")) {
            return "Salary";
        } else {
            return "Data Change";
        }
    }

    @Override
    public String getDescription() {
        if (description != null && !description.trim().isEmpty()) {
            return description;
        }
        return getDirtyReasonDisplay();
    }
}