package group4.hrms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import group4.hrms.config.LatePolicyConfig;
import group4.hrms.config.TaxConfig;
import group4.hrms.config.UnderHoursPolicyConfig;

/**
 * DTO to hold snapshots of salary, policy, and timesheet data for payslip calculations
 * This ensures payslip calculations remain consistent even if source data changes
 * Requirements: 3.4, 6.6, 6.7, 6.8, 6.9, 6.10
 */
public class PayslipSnapshots {

    @JsonProperty("salary")
    private SalarySnapshot salary;

    @JsonProperty("policy")
    private PolicySnapshot policy;

    @JsonProperty("timesheet")
    private TimesheetSnapshot timesheet;

    // Default constructor for JSON deserialization
    public PayslipSnapshots() {
    }

    // Constructor
    public PayslipSnapshots(SalarySnapshot salary, PolicySnapshot policy, TimesheetSnapshot timesheet) {
        this.salary = salary;
        this.policy = policy;
        this.timesheet = timesheet;
    }

    // Getters and setters
    public SalarySnapshot getSalary() {
        return salary;
    }

    public void setSalary(SalarySnapshot salary) {
        this.salary = salary;
    }

    public PolicySnapshot getPolicy() {
        return policy;
    }

    public void setPolicy(PolicySnapshot policy) {
        this.policy = policy;
    }

    public TimesheetSnapshot getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(TimesheetSnapshot timesheet) {
        this.timesheet = timesheet;
    }

    /**
     * Salary snapshot containing salary information at calculation time
     */
    public static class SalarySnapshot {
        @JsonProperty("base_salary")
        private BigDecimal baseSalary;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("effective_date")
        private String effectiveDate;

        // Default constructor
        public SalarySnapshot() {
        }

        // Constructor
        public SalarySnapshot(BigDecimal baseSalary, String currency, LocalDate effectiveDate) {
            this.baseSalary = baseSalary;
            this.currency = currency;
            this.effectiveDate = effectiveDate != null ? effectiveDate.toString() : null;
        }

        // Getters and setters
        public BigDecimal getBaseSalary() {
            return baseSalary;
        }

        public void setBaseSalary(BigDecimal baseSalary) {
            this.baseSalary = baseSalary;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getEffectiveDate() {
            return effectiveDate;
        }

        public void setEffectiveDate(String effectiveDate) {
            this.effectiveDate = effectiveDate;
        }

        public void setEffectiveDate(LocalDate effectiveDate) {
            this.effectiveDate = effectiveDate != null ? effectiveDate.toString() : null;
        }
    }

    /**
     * Policy snapshot containing all policy configurations at calculation time
     */
    public static class PolicySnapshot {
        @JsonProperty("working_days_per_month")
        private int workingDaysPerMonth;

        @JsonProperty("working_hours_per_day")
        private int workingHoursPerDay;

        @JsonProperty("ot_multipliers")
        private Map<String, Double> otMultipliers;

        @JsonProperty("late_policy")
        private LatePolicyConfig latePolicy;

        @JsonProperty("under_hours_policy")
        private UnderHoursPolicyConfig underHoursPolicy;

        @JsonProperty("tax_config")
        private TaxConfig taxConfig;

        // Default constructor
        public PolicySnapshot() {
        }

        // Getters and setters
        public int getWorkingDaysPerMonth() {
            return workingDaysPerMonth;
        }

        public void setWorkingDaysPerMonth(int workingDaysPerMonth) {
            this.workingDaysPerMonth = workingDaysPerMonth;
        }

        public int getWorkingHoursPerDay() {
            return workingHoursPerDay;
        }

        public void setWorkingHoursPerDay(int workingHoursPerDay) {
            this.workingHoursPerDay = workingHoursPerDay;
        }

        public Map<String, Double> getOtMultipliers() {
            return otMultipliers;
        }

        public void setOtMultipliers(Map<String, Double> otMultipliers) {
            this.otMultipliers = otMultipliers;
        }

        public LatePolicyConfig getLatePolicy() {
            return latePolicy;
        }

        public void setLatePolicy(LatePolicyConfig latePolicy) {
            this.latePolicy = latePolicy;
        }

        public UnderHoursPolicyConfig getUnderHoursPolicy() {
            return underHoursPolicy;
        }

        public void setUnderHoursPolicy(UnderHoursPolicyConfig underHoursPolicy) {
            this.underHoursPolicy = underHoursPolicy;
        }

        public TaxConfig getTaxConfig() {
            return taxConfig;
        }

        public void setTaxConfig(TaxConfig taxConfig) {
            this.taxConfig = taxConfig;
        }
    }

    /**
     * Timesheet snapshot containing attendance, leave, and overtime data for the period
     */
    public static class TimesheetSnapshot {
        @JsonProperty("worked_days")
        private int workedDays;

        @JsonProperty("paid_leave_days")
        private int paidLeaveDays;

        @JsonProperty("unpaid_leave_days")
        private int unpaidLeaveDays;

        @JsonProperty("worked_hours")
        private double workedHours;

        @JsonProperty("paid_leave_hours")
        private double paidLeaveHours;

        @JsonProperty("late_minutes_total")
        private int lateMinutesTotal;

        @JsonProperty("ot_hours")
        private Map<String, Double> otHours;

        // Default constructor
        public TimesheetSnapshot() {
        }

        // Getters and setters
        public int getWorkedDays() {
            return workedDays;
        }

        public void setWorkedDays(int workedDays) {
            this.workedDays = workedDays;
        }

        public int getPaidLeaveDays() {
            return paidLeaveDays;
        }

        public void setPaidLeaveDays(int paidLeaveDays) {
            this.paidLeaveDays = paidLeaveDays;
        }

        public int getUnpaidLeaveDays() {
            return unpaidLeaveDays;
        }

        public void setUnpaidLeaveDays(int unpaidLeaveDays) {
            this.unpaidLeaveDays = unpaidLeaveDays;
        }

        public double getWorkedHours() {
            return workedHours;
        }

        public void setWorkedHours(double workedHours) {
            this.workedHours = workedHours;
        }

        public double getPaidLeaveHours() {
            return paidLeaveHours;
        }

        public void setPaidLeaveHours(double paidLeaveHours) {
            this.paidLeaveHours = paidLeaveHours;
        }

        public int getLateMinutesTotal() {
            return lateMinutesTotal;
        }

        public void setLateMinutesTotal(int lateMinutesTotal) {
            this.lateMinutesTotal = lateMinutesTotal;
        }

        public Map<String, Double> getOtHours() {
            return otHours;
        }

        public void setOtHours(Map<String, Double> otHours) {
            this.otHours = otHours;
        }
    }

    @Override
    public String toString() {
        return String.format("PayslipSnapshots{salary=%s, policy=%s, timesheet=%s}",
                           salary != null ? salary.getBaseSalary() : "null",
                           policy != null ? policy.getWorkingDaysPerMonth() + " days" : "null",
                           timesheet != null ? timesheet.getWorkedDays() + " worked days" : "null");
    }
}