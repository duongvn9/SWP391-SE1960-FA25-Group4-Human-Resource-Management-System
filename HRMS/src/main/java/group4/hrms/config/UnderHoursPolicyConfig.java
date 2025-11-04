package group4.hrms.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration class for under-hours policy settings
 * Controls how under-hours deductions are calculated
 */
public class UnderHoursPolicyConfig {

    @JsonProperty("include_paid_leave_as_worked")
    private boolean includePaidLeaveAsWorked;

    @JsonProperty("allow_offset_by_ot")
    private boolean allowOffsetByOt;

    // Default constructor
    public UnderHoursPolicyConfig() {}

    // Constructor
    public UnderHoursPolicyConfig(boolean includePaidLeaveAsWorked, boolean allowOffsetByOt) {
        this.includePaidLeaveAsWorked = includePaidLeaveAsWorked;
        this.allowOffsetByOt = allowOffsetByOt;
    }

    // Getters and setters
    public boolean isIncludePaidLeaveAsWorked() {
        return includePaidLeaveAsWorked;
    }

    public void setIncludePaidLeaveAsWorked(boolean includePaidLeaveAsWorked) {
        this.includePaidLeaveAsWorked = includePaidLeaveAsWorked;
    }

    public boolean isAllowOffsetByOt() {
        return allowOffsetByOt;
    }

    public void setAllowOffsetByOt(boolean allowOffsetByOt) {
        this.allowOffsetByOt = allowOffsetByOt;
    }

    /**
     * Validate configuration integrity
     *
     * @return true if configuration is valid (always true for this simple config)
     */
    public boolean isValid() {
        // Both boolean fields are always valid
        return true;
    }

    @Override
    public String toString() {
        return String.format("UnderHoursPolicyConfig{includePaidLeaveAsWorked=%s, allowOffsetByOt=%s}",
                           includePaidLeaveAsWorked, allowOffsetByOt);
    }
}