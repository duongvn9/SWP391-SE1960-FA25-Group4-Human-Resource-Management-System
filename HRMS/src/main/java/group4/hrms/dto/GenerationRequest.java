package group4.hrms.dto;

import java.time.LocalDate;

/**
 * DTO for payslip generation requests
 * Requirements: 3.1, 3.2, 3.3
 */
public class GenerationRequest {

    public enum GenerationScope {
        ALL,        // Generate for all employees
        DEPARTMENT, // Generate for specific department
        EMPLOYEE    // Generate for specific employee
    }

    private LocalDate periodStart;
    private LocalDate periodEnd;
    private GenerationScope scope;
    private Long scopeId; // departmentId or userId depending on scope
    private Boolean onlyDirty; // Only generate dirty payslips
    private Boolean force; // Force regeneration of existing payslips
    private Long requestedByUserId; // User who requested the generation
    private String requestReason; // Reason for the generation request

    // Default constructor
    public GenerationRequest() {
        this.onlyDirty = false;
        this.force = false;
    }

    // Constructor with basic parameters
    public GenerationRequest(LocalDate periodStart, LocalDate periodEnd, GenerationScope scope) {
        this();
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.scope = scope;
    }

    // Constructor with scope ID (for PayslipListController)
    public GenerationRequest(LocalDate periodStart, LocalDate periodEnd, GenerationScope scope, Long scopeId) {
        this(periodStart, periodEnd, scope);
        this.scopeId = scopeId;
    }

    // Getters and setters
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

    public GenerationScope getScope() {
        return scope;
    }

    public void setScope(GenerationScope scope) {
        this.scope = scope;
    }

    public Long getScopeId() {
        return scopeId;
    }

    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }

    public Boolean getOnlyDirty() {
        return onlyDirty;
    }

    public void setOnlyDirty(Boolean onlyDirty) {
        this.onlyDirty = onlyDirty;
    }

    public Boolean getForce() {
        return force;
    }

    public void setForce(Boolean force) {
        this.force = force;
    }

    public Long getRequestedByUserId() {
        return requestedByUserId;
    }

    public void setRequestedByUserId(Long requestedByUserId) {
        this.requestedByUserId = requestedByUserId;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    // Validation methods
    public boolean isValid() {
        if (periodStart == null || periodEnd == null || scope == null) {
            return false;
        }

        if (periodStart.isAfter(periodEnd)) {
            return false;
        }

        // Validate scope-specific requirements
        if ((scope == GenerationScope.DEPARTMENT || scope == GenerationScope.EMPLOYEE) && scopeId == null) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return String.format("GenerationRequest{period=%s to %s, scope=%s, scopeId=%s, onlyDirty=%s, force=%s}",
                           periodStart, periodEnd, scope, scopeId, onlyDirty, force);
    }
}