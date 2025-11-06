package group4.hrms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO to hold payslip calculation results
 * Contains all calculated amounts and intermediate values
 */
public class PayslipCalculationResult {

    // Basic information
    private Long userId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String currency;

    // Salary components
    private BigDecimal baseSalary;
    private BigDecimal dailyRate;
    private BigDecimal hourlyRate;

    // Time calculations
    private int workedDays;
    private int paidLeaveDays;
    private int totalPaidDays;
    private int workingDaysInPeriod; // Total working days in the period (for base prorated calculation)
    private double workedHours;
    private double paidLeaveHours;
    private double totalActualHours;
    private double requiredHours;
    private double underHours;

    // Overtime calculations
    private double weekdayOTHours;
    private double weekendOTHours;
    private double holidayOTHours;
    private double compensatoryOTHours;
    private BigDecimal otAmount;

    // Deductions
    private int totalLateMinutes;
    private double latenessUnpaidHours;
    private BigDecimal latenessDeduction;
    private double underHoursUnpaid;
    private BigDecimal underHoursDeduction;

    // Tax calculations
    private BigDecimal grossAmount;
    private BigDecimal taxAmount;

    // Final amounts
    private BigDecimal baseProrated;
    private BigDecimal worktimeUnpaid;
    private BigDecimal netAmount;

    // Snapshots reference
    private PayslipSnapshots snapshots;

    // Default constructor
    public PayslipCalculationResult() {
        this.currency = "VND";
    }

    // Constructor with basic info
    public PayslipCalculationResult(Long userId, LocalDate periodStart, LocalDate periodEnd) {
        this();
        this.userId = userId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

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

    public int getTotalPaidDays() {
        return totalPaidDays;
    }

    public void setTotalPaidDays(int totalPaidDays) {
        this.totalPaidDays = totalPaidDays;
    }

    public int getWorkingDaysInPeriod() {
        return workingDaysInPeriod;
    }

    public void setWorkingDaysInPeriod(int workingDaysInPeriod) {
        this.workingDaysInPeriod = workingDaysInPeriod;
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

    public double getTotalActualHours() {
        return totalActualHours;
    }

    public void setTotalActualHours(double totalActualHours) {
        this.totalActualHours = totalActualHours;
    }

    public double getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(double requiredHours) {
        this.requiredHours = requiredHours;
    }

    public double getUnderHours() {
        return underHours;
    }

    public void setUnderHours(double underHours) {
        this.underHours = underHours;
    }

    public double getWeekdayOTHours() {
        return weekdayOTHours;
    }

    public void setWeekdayOTHours(double weekdayOTHours) {
        this.weekdayOTHours = weekdayOTHours;
    }

    public double getWeekendOTHours() {
        return weekendOTHours;
    }

    public void setWeekendOTHours(double weekendOTHours) {
        this.weekendOTHours = weekendOTHours;
    }

    public double getHolidayOTHours() {
        return holidayOTHours;
    }

    public void setHolidayOTHours(double holidayOTHours) {
        this.holidayOTHours = holidayOTHours;
    }

    public double getCompensatoryOTHours() {
        return compensatoryOTHours;
    }

    public void setCompensatoryOTHours(double compensatoryOTHours) {
        this.compensatoryOTHours = compensatoryOTHours;
    }

    public BigDecimal getOtAmount() {
        return otAmount;
    }

    public void setOtAmount(BigDecimal otAmount) {
        this.otAmount = otAmount;
    }

    public int getTotalLateMinutes() {
        return totalLateMinutes;
    }

    public void setTotalLateMinutes(int totalLateMinutes) {
        this.totalLateMinutes = totalLateMinutes;
    }

    public double getLatenessUnpaidHours() {
        return latenessUnpaidHours;
    }

    public void setLatenessUnpaidHours(double latenessUnpaidHours) {
        this.latenessUnpaidHours = latenessUnpaidHours;
    }

    public BigDecimal getLatenessDeduction() {
        return latenessDeduction;
    }

    public void setLatenessDeduction(BigDecimal latenessDeduction) {
        this.latenessDeduction = latenessDeduction;
    }

    public double getUnderHoursUnpaid() {
        return underHoursUnpaid;
    }

    public void setUnderHoursUnpaid(double underHoursUnpaid) {
        this.underHoursUnpaid = underHoursUnpaid;
    }

    public BigDecimal getUnderHoursDeduction() {
        return underHoursDeduction;
    }

    public void setUnderHoursDeduction(BigDecimal underHoursDeduction) {
        this.underHoursDeduction = underHoursDeduction;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getBaseProrated() {
        return baseProrated;
    }

    public void setBaseProrated(BigDecimal baseProrated) {
        this.baseProrated = baseProrated;
    }

    public BigDecimal getWorktimeUnpaid() {
        return worktimeUnpaid;
    }

    public void setWorktimeUnpaid(BigDecimal worktimeUnpaid) {
        this.worktimeUnpaid = worktimeUnpaid;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public PayslipSnapshots getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(PayslipSnapshots snapshots) {
        this.snapshots = snapshots;
    }

    @Override
    public String toString() {
        return String.format("PayslipCalculationResult{userId=%d, period=%s to %s, baseSalary=%s, " +
                           "grossAmount=%s, netAmount=%s, currency='%s'}",
                           userId, periodStart, periodEnd, baseSalary, grossAmount, netAmount, currency);
    }
}