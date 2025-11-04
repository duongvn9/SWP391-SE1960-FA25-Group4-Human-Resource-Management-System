package group4.hrms.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Filter parameters for payslip queries
 * Requirements: 1.2, 2.2, 2.4
 */
public class PayslipFilter {

    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long departmentId;
    private Long userId;
    private String employeeSearch; // Search by employee code or name
    private Boolean onlyDirty;
    private Boolean onlyNotGenerated;
    private String status;
    private List<Long> userIds; // For scope filtering

    // Constructors
    public PayslipFilter() {}

    public PayslipFilter(LocalDate periodStart, LocalDate periodEnd) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    // Getters and Setters
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmployeeSearch() {
        return employeeSearch;
    }

    public void setEmployeeSearch(String employeeSearch) {
        this.employeeSearch = employeeSearch;
    }

    public Boolean getOnlyDirty() {
        return onlyDirty;
    }

    public void setOnlyDirty(Boolean onlyDirty) {
        this.onlyDirty = onlyDirty;
    }

    public Boolean getOnlyNotGenerated() {
        return onlyNotGenerated;
    }

    public void setOnlyNotGenerated(Boolean onlyNotGenerated) {
        this.onlyNotGenerated = onlyNotGenerated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    // Helper methods
    public boolean hasPeriodFilter() {
        return periodStart != null && periodEnd != null;
    }

    public boolean hasDepartmentFilter() {
        return departmentId != null;
    }

    public boolean hasUserFilter() {
        return userId != null;
    }

    public boolean hasStatusFilter() {
        return status != null && !status.trim().isEmpty();
    }

    public boolean hasDirtyFilter() {
        return Boolean.TRUE.equals(onlyDirty);
    }

    public boolean hasNotGeneratedFilter() {
        return Boolean.TRUE.equals(onlyNotGenerated);
    }

    public boolean hasUserScopeFilter() {
        return userIds != null && !userIds.isEmpty();
    }

    public boolean isEmpty() {
        return !hasPeriodFilter() && !hasDepartmentFilter() && !hasUserFilter()
               && !hasStatusFilter() && !hasDirtyFilter() && !hasNotGeneratedFilter()
               && !hasUserScopeFilter();
    }

    @Override
    public String toString() {
        return "PayslipFilter{" +
                "periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", departmentId=" + departmentId +
                ", userId=" + userId +
                ", onlyDirty=" + onlyDirty +
                ", onlyNotGenerated=" + onlyNotGenerated +
                ", status='" + status + '\'' +
                ", userIds=" + (userIds != null ? userIds.size() + " users" : "null") +
                '}';
    }
}