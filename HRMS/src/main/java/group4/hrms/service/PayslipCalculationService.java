package group4.hrms.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import group4.hrms.config.LatePolicyConfig;
import group4.hrms.config.PayrollConfig;
import group4.hrms.config.TaxConfig;
import group4.hrms.config.UnderHoursPolicyConfig;
import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.EmploymentContractDao;
import group4.hrms.dao.HolidayDao;
import group4.hrms.dao.LeaveTypeDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.util.LunarCalendarUtil;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.dto.PayslipCalculationResult;
import group4.hrms.dto.PayslipSnapshots;
import group4.hrms.model.EmploymentContract;
import group4.hrms.model.Holiday;
import group4.hrms.model.LeaveType;
import group4.hrms.model.Request;
import group4.hrms.service.OTCalculationService.OTType;

/**
 * Service for calculating payslip amounts using standardized formulas and real-time data
 * Implements requirements 6.1-6.10, 7.1-7.4
 */
public class PayslipCalculationService {

    private static final Logger logger = Logger.getLogger(PayslipCalculationService.class.getName());

    // Rounding mode for all monetary calculations
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int DECIMAL_PLACES = 2;

    // Dependencies
    private final EmploymentContractDao employmentContractDao;
    private final AttendanceService attendanceService;
    private final OTCalculationService otCalculationService;
    private final LeaveBalanceService leaveBalanceService;
    private final RequestDao requestDao;
    private final LeaveTypeDao leaveTypeDao;
    private final HolidayDao holidayDao;
    private final ObjectMapper objectMapper;

    // Constructor
    public PayslipCalculationService() {
        this.employmentContractDao = new EmploymentContractDao();
        this.attendanceService = new AttendanceService();
        this.holidayDao = new HolidayDao();
        this.otCalculationService = new OTCalculationService(holidayDao);
        this.requestDao = new RequestDao();
        this.leaveTypeDao = new LeaveTypeDao();
        this.leaveBalanceService = new LeaveBalanceService(requestDao, leaveTypeDao);
        this.objectMapper = new ObjectMapper();

        // Configure ObjectMapper for LocalDate serialization
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * Calculate complete payslip for a user and period
     *
     * @param userId User ID
     * @param periodStart Period start date
     * @param periodEnd Period end date
     * @return PayslipCalculationResult with all calculated amounts
     */
    public PayslipCalculationResult calculatePayslip(Long userId, LocalDate periodStart, LocalDate periodEnd) {
        logger.info(String.format("Starting payslip calculation: userId=%d, period=%s to %s",
                                userId, periodStart, periodEnd));

        try {
            // Create result object
            PayslipCalculationResult result = new PayslipCalculationResult(userId, periodStart, periodEnd);

            // Step 1: Create snapshots of all data
            PayslipSnapshots snapshots = createSnapshots(userId, periodStart, periodEnd);
            result.setSnapshots(snapshots);

            // Step 2: Get salary information from employment contract
            Optional<EmploymentContract> currentContract = employmentContractDao.findContractForDate(userId, periodStart);
            BigDecimal baseSalary;
            String currency;

            if (!currentContract.isPresent()) {
                logger.warning("No active employment contract found for user: " + userId + ". Using default salary.");
                baseSalary = new BigDecimal("15000000.00"); // Default base salary
                currency = "VND";
            } else {
                baseSalary = currentContract.get().getBaseSalary();
                currency = currentContract.get().getCurrency();
            }
            result.setBaseSalary(baseSalary);
            result.setCurrency(currency); // Set currency from contract

            // Step 3: Calculate daily and hourly rates
            // NEW FORMULA: Daily rate = Base Salary / 30 (calendar days)
            BigDecimal dailyRate = calculateDailyRate(baseSalary, 30); // Fixed 30 days
            BigDecimal hourlyRate = calculateHourlyRate(dailyRate, PayrollConfig.getWorkingHoursPerDay());
            result.setDailyRate(dailyRate);
            result.setHourlyRate(hourlyRate);

            // Step 4: Get timesheet data from snapshots
            PayslipSnapshots.TimesheetSnapshot timesheet = snapshots.getTimesheet();
            result.setWorkedDays(timesheet.getWorkedDays());
            result.setPaidLeaveDays(timesheet.getPaidLeaveDays());
            result.setWorkedHours(timesheet.getWorkedHours());
            result.setPaidLeaveHours(timesheet.getPaidLeaveHours());
            result.setTotalLateMinutes(timesheet.getLateMinutesTotal());

            // Step 5: Calculate paid days and hours (NEW LOGIC)
            // Total Paid Days = Worked Days + Paid Leave Days
            // UNPAID Leave is already excluded (not in Worked Days, not in Paid Leave Days)
            double workedDays = timesheet.getWorkedDays();
            double paidLeaveDays = timesheet.getPaidLeaveDays();
            double totalPaidDays = workedDays + paidLeaveDays;

            double workedHours = timesheet.getWorkedHours();
            double paidLeaveHours = timesheet.getPaidLeaveHours();
            double totalActualHours = workedHours + paidLeaveHours;

            result.setTotalPaidDays((int) Math.round(totalPaidDays));
            result.setTotalActualHours(totalActualHours);

            logger.info(String.format("Paid days calculation: workedDays=%.1f, paidLeaveDays=%.1f, totalPaidDays=%.1f",
                                    workedDays, paidLeaveDays, totalPaidDays));

            // Step 6: Calculate base prorated amount
            // CORRECT FORMULA: Base Salary × (Total Paid Days / Working Days in Period)
            // Daily Rate is ONLY used for OT and deduction calculations
            int workingDaysInPeriod = calculateWorkingDaysInPeriod(periodStart, periodEnd);
            BigDecimal baseProrated = baseSalary
                .multiply(BigDecimal.valueOf(totalPaidDays))
                .divide(BigDecimal.valueOf(workingDaysInPeriod), DECIMAL_PLACES, ROUNDING_MODE);

            result.setBaseProrated(baseProrated);
            result.setWorkingDaysInPeriod(workingDaysInPeriod); // Store for reference

            logger.info(String.format("Base prorated calculation: baseSalary=%s, totalPaidDays=%.1f, workingDays=%d, result=%s",
                                    baseSalary, totalPaidDays, workingDaysInPeriod, baseProrated));

            // Step 7: Calculate overtime amount (NEW LOGIC)
            // Calculate OT amount directly from approved OT requests (using individual multipliers)
            BigDecimal otAmount = calculateOvertimeAmountFromRequests(userId, periodStart, periodEnd, hourlyRate);
            result.setOtAmount(otAmount);

            // Extract OT hours by type for result (from snapshot)
            Map<String, Double> otHours = timesheet.getOtHours();
            result.setWeekdayOTHours(otHours.getOrDefault("WEEKDAY", 0.0));
            result.setWeekendOTHours(otHours.getOrDefault("WEEKEND", 0.0));
            result.setHolidayOTHours(otHours.getOrDefault("HOLIDAY", 0.0));
            result.setCompensatoryOTHours(otHours.getOrDefault("COMPENSATORY", 0.0));

            // Step 8: Calculate lateness deduction (Fair-pay mode: NO deduction)
            // Lateness only affects KPI/discipline, not salary
            BigDecimal latenessDeduction = BigDecimal.ZERO;
            result.setLatenessDeduction(latenessDeduction);
            result.setTotalLateMinutes(timesheet.getLateMinutesTotal());

            logger.info(String.format("Lateness: %d minutes (Fair-pay mode: no deduction)",
                                    timesheet.getLateMinutesTotal()));

            // Step 9: Calculate under-hours deduction (NEW LOGIC)
            // Only calculate on Paid Hours (Worked + Paid Leave)
            // UNPAID Leave does NOT affect Under Hours
            double paidBaseHours = totalPaidDays * PayrollConfig.getWorkingHoursPerDay();
            double actualPaidHours = workedHours + paidLeaveHours;
            double underHours = Math.max(0, paidBaseHours - actualPaidHours);

            // Apply threshold: Ignore under hours less than 5 minutes (0.083 hours)
            // This prevents deduction for minor rounding differences
            final double UNDER_HOURS_THRESHOLD = 0.083; // 5 minutes
            if (underHours < UNDER_HOURS_THRESHOLD) {
                underHours = 0;
                logger.info(String.format("Under hours %.4f is below threshold (%.3f hours / 5 minutes), setting to 0",
                                        underHours, UNDER_HOURS_THRESHOLD));
            }

            result.setRequiredHours(paidBaseHours);
            result.setUnderHours(underHours);

            BigDecimal underHoursDeduction = calculateUnderHoursDeduction(underHours, hourlyRate);
            result.setUnderHoursDeduction(underHoursDeduction);
            result.setUnderHoursUnpaid(underHours);

            logger.info(String.format("Under hours: paidBaseHours=%.2f, actualPaidHours=%.2f, underHours=%.2f, deduction=%s",
                                    paidBaseHours, actualPaidHours, underHours, underHoursDeduction));

            // Step 10: Calculate gross amount
            BigDecimal grossAmount = baseProrated.add(otAmount);
            result.setGrossAmount(grossAmount);

            // Step 11: Calculate total deductions
            BigDecimal totalDeductions = underHoursDeduction; // Only Under Hours (Lateness = 0)
            result.setWorktimeUnpaid(totalDeductions);

            // Step 12: Calculate taxable income (AFTER deductions)
            BigDecimal taxableIncome = grossAmount.subtract(totalDeductions);

            // Step 13: Calculate tax amount (on taxable income, not gross)
            BigDecimal taxAmount = calculateTaxAmount(taxableIncome, PayrollConfig.getTaxConfig());
            result.setTaxAmount(taxAmount);

            // Step 14: Calculate net amount
            BigDecimal netAmount = taxableIncome.subtract(taxAmount);
            result.setNetAmount(netAmount);

            logger.info(String.format("Final calculation: gross=%s, deductions=%s, taxable=%s, tax=%s, net=%s",
                                    grossAmount, totalDeductions, taxableIncome, taxAmount, netAmount));

            logger.info(String.format("Payslip calculation completed: userId=%d, gross=%s, net=%s",
                                    userId, grossAmount, netAmount));

            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating payslip: userId=%d, period=%s to %s",
                                                 userId, periodStart, periodEnd), e);
            throw new RuntimeException("Payslip calculation failed", e);
        }
    }

    /**
     * Calculate daily rate from base salary
     * NEW FORMULA: Daily Rate = Base Salary / 30 (calendar days)
     * Requirements: 6.1
     *
     * @param baseSalary Base monthly salary
     * @param daysPerMonth Days per month (30 for calendar days)
     * @return Daily rate
     */
    public BigDecimal calculateDailyRate(BigDecimal baseSalary, int daysPerMonth) {
        if (baseSalary == null || daysPerMonth <= 0) {
            return BigDecimal.ZERO;
        }

        return baseSalary.divide(BigDecimal.valueOf(daysPerMonth), DECIMAL_PLACES, ROUNDING_MODE);
    }

    /**
     * Calculate hourly rate from daily rate
     * Requirements: 6.2
     *
     * @param dailyRate Daily rate
     * @param workingHoursPerDay Working hours per day from configuration
     * @return Hourly rate
     */
    public BigDecimal calculateHourlyRate(BigDecimal dailyRate, int workingHoursPerDay) {
        if (dailyRate == null || workingHoursPerDay <= 0) {
            return BigDecimal.ZERO;
        }

        return dailyRate.divide(BigDecimal.valueOf(workingHoursPerDay), DECIMAL_PLACES, ROUNDING_MODE);
    }

    /**
     * Calculate overtime amount directly from approved OT requests
     * Uses individual multipliers from each request for accurate calculation
     * Requirements: 6.5
     *
     * @param userId User ID
     * @param periodStart Period start date
     * @param periodEnd Period end date
     * @param hourlyRate Hourly rate
     * @return Total overtime amount
     */
    public BigDecimal calculateOvertimeAmountFromRequests(Long userId, LocalDate periodStart, LocalDate periodEnd, BigDecimal hourlyRate) {
        if (hourlyRate == null) {
            return BigDecimal.ZERO;
        }

        try {
            // Get approved OT requests for the period
            List<Request> approvedOTRequests = requestDao.findOTRequestsByUserIdAndDateRange(
                userId,
                periodStart.atStartOfDay(),
                periodEnd.atTime(23, 59, 59)
            );

            BigDecimal totalOTAmount = BigDecimal.ZERO;

            // Calculate OT amount for each request using its individual multiplier
            for (Request otRequest : approvedOTRequests) {
                if (otRequest.getOtDetail() != null) {
                    try {
                        double hours = otRequest.getOtDetail().getOtHours();
                        Double payMultiplier = otRequest.getOtDetail().getPayMultiplier();
                        String otType = otRequest.getOtDetail().getOtType();

                        // Use multiplier from request (default to 1.5 if not set)
                        double multiplier = payMultiplier != null ? payMultiplier : 1.5;

                        BigDecimal otAmount = hourlyRate
                            .multiply(BigDecimal.valueOf(hours))
                            .multiply(BigDecimal.valueOf(multiplier))
                            .setScale(DECIMAL_PLACES, ROUNDING_MODE);

                        totalOTAmount = totalOTAmount.add(otAmount);

                        logger.fine(String.format("OT calculation: type=%s, hours=%.2f, multiplier=%.1f, amount=%s",
                                                otType, hours, multiplier, otAmount));

                    } catch (Exception e) {
                        logger.warning(String.format("Error calculating OT for request %d: %s",
                                                   otRequest.getId(), e.getMessage()));
                    }
                }
            }

            logger.info(String.format("Total OT amount calculated: userId=%d, totalAmount=%s",
                                    userId, totalOTAmount));

            return totalOTAmount;

        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Error calculating OT amount for user %d: %s",
                                                   userId, e.getMessage()), e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculate overtime amount using OTCalculationService multipliers
     * @deprecated Use calculateOvertimeAmountFromRequests instead for accurate calculation with individual multipliers
     * Requirements: 6.5
     *
     * @param otHours Map of OT hours by type (WEEKDAY, WEEKEND, HOLIDAY, COMPENSATORY)
     * @param hourlyRate Hourly rate
     * @return Total overtime amount
     */
    @Deprecated
    public BigDecimal calculateOvertimeAmount(Map<String, Double> otHours, BigDecimal hourlyRate) {
        if (otHours == null || hourlyRate == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalOTAmount = BigDecimal.ZERO;

        for (Map.Entry<String, Double> entry : otHours.entrySet()) {
            String otTypeStr = entry.getKey();
            Double hours = entry.getValue();

            if (hours == null || hours <= 0) {
                continue;
            }

            try {
                OTType otType = OTType.valueOf(otTypeStr);
                double multiplier = otType.getMultiplier();

                BigDecimal otAmount = hourlyRate
                    .multiply(BigDecimal.valueOf(hours))
                    .multiply(BigDecimal.valueOf(multiplier))
                    .setScale(DECIMAL_PLACES, ROUNDING_MODE);

                totalOTAmount = totalOTAmount.add(otAmount);

                logger.fine(String.format("OT calculation: type=%s, hours=%.2f, multiplier=%.1f, amount=%s",
                                        otTypeStr, hours, multiplier, otAmount));

            } catch (IllegalArgumentException e) {
                logger.warning(String.format("Unknown OT type: %s, skipping", otTypeStr));
            }
        }

        return totalOTAmount;
    }

    /**
     * Calculate lateness deduction using late policy configuration
     * Requirements: 6.4
     *
     * @param totalLateMinutes Total late minutes in the period
     * @param hourlyRate Hourly rate
     * @param latePolicy Late policy configuration
     * @return Lateness deduction amount
     */
    public BigDecimal calculateLatenessDeduction(int totalLateMinutes, BigDecimal hourlyRate,
                                               LatePolicyConfig latePolicy) {
        if (totalLateMinutes <= 0 || hourlyRate == null || latePolicy == null) {
            return BigDecimal.ZERO;
        }

        // Apply grace period
        int effectiveLateMinutes = Math.max(0, totalLateMinutes - latePolicy.getGraceMinutesPerEvent());

        if (effectiveLateMinutes <= 0) {
            return BigDecimal.ZERO;
        }

        double unpaidHours = 0.0;

        if ("ladder".equals(latePolicy.getMode()) && latePolicy.getLadderBands() != null) {
            // Use ladder policy
            for (LatePolicyConfig.LadderBand band : latePolicy.getLadderBands()) {
                if (band.applies(effectiveLateMinutes)) {
                    unpaidHours = band.calculateUnpaidHours(effectiveLateMinutes);
                    break;
                }
            }
        } else if ("rounding".equals(latePolicy.getMode()) && latePolicy.getRoundingUnitMinutes() != null) {
            // Use rounding policy
            int roundingUnit = latePolicy.getRoundingUnitMinutes();
            unpaidHours = Math.ceil((double) effectiveLateMinutes / roundingUnit) * (roundingUnit / 60.0);
        }

        BigDecimal deduction = hourlyRate.multiply(BigDecimal.valueOf(unpaidHours))
                                       .setScale(DECIMAL_PLACES, ROUNDING_MODE);

        logger.fine(String.format("Lateness deduction: lateMinutes=%d, effectiveMinutes=%d, unpaidHours=%.2f, deduction=%s",
                                totalLateMinutes, effectiveLateMinutes, unpaidHours, deduction));

        return deduction;
    }

    /**
     * Calculate under-hours deduction
     * Requirements: 6.3
     *
     * @param underHours Number of under hours
     * @param hourlyRate Hourly rate
     * @return Under-hours deduction amount
     */
    public BigDecimal calculateUnderHoursDeduction(double underHours, BigDecimal hourlyRate) {
        if (underHours <= 0 || hourlyRate == null) {
            return BigDecimal.ZERO;
        }

        return hourlyRate.multiply(BigDecimal.valueOf(underHours))
                        .setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }

    /**
     * Calculate tax amount using tax configuration
     * Requirements: 7.2
     *
     * @param grossAmount Gross amount (base + OT)
     * @param taxConfig Tax configuration
     * @return Tax amount
     */
    public BigDecimal calculateTaxAmount(BigDecimal grossAmount, TaxConfig taxConfig) {
        if (grossAmount == null || taxConfig == null || !taxConfig.isEnableTax()) {
            return BigDecimal.ZERO;
        }

        if ("flat".equals(taxConfig.getMode())) {
            BigDecimal taxAmount = grossAmount.multiply(BigDecimal.valueOf(taxConfig.getFlatRate()))
                                            .setScale(DECIMAL_PLACES, ROUNDING_MODE);

            logger.fine(String.format("Tax calculation: gross=%s, rate=%.4f, tax=%s",
                                    grossAmount, taxConfig.getFlatRate(), taxAmount));

            return taxAmount;
        }

        return BigDecimal.ZERO;
    }

    /**
     * Create snapshots of salary, policy, and timesheet data
     * Requirements: 3.4, 6.6, 6.7, 6.8, 6.9, 6.10
     *
     * @param userId User ID
     * @param periodStart Period start date
     * @param periodEnd Period end date
     * @return PayslipSnapshots containing all snapshot data
     */
    public PayslipSnapshots createSnapshots(Long userId, LocalDate periodStart, LocalDate periodEnd) {
        logger.fine(String.format("Creating snapshots: userId=%d, period=%s to %s",
                                userId, periodStart, periodEnd));

        try {
            // Create salary snapshot
            PayslipSnapshots.SalarySnapshot salarySnapshot = createSalarySnapshot(userId, periodStart);

            // Create policy snapshot
            PayslipSnapshots.PolicySnapshot policySnapshot = createPolicySnapshot();

            // Create timesheet snapshot
            PayslipSnapshots.TimesheetSnapshot timesheetSnapshot = createTimesheetSnapshot(userId, periodStart, periodEnd);

            return new PayslipSnapshots(salarySnapshot, policySnapshot, timesheetSnapshot);

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error creating snapshots: userId=%d, period=%s to %s",
                                                 userId, periodStart, periodEnd), e);
            throw new RuntimeException("Failed to create snapshots", e);
        }
    }

    // Private helper methods

    private PayslipSnapshots.SalarySnapshot createSalarySnapshot(Long userId, LocalDate periodStart) throws SQLException {
        Optional<EmploymentContract> currentContract = employmentContractDao.findContractForDate(userId, periodStart);
        if (!currentContract.isPresent()) {
            logger.warning("No active employment contract found for user: " + userId + ". Using default salary.");
            // Create a default salary snapshot for users without contracts
            return new PayslipSnapshots.SalarySnapshot(
                new BigDecimal("15000000.00"), // Default base salary
                "VND",
                periodStart
            );
        }

        EmploymentContract contract = currentContract.get();
        return new PayslipSnapshots.SalarySnapshot(
            contract.getBaseSalary(),
            contract.getCurrency(),
            contract.getStartDate()
        );
    }

    private PayslipSnapshots.PolicySnapshot createPolicySnapshot() {
        PayslipSnapshots.PolicySnapshot snapshot = new PayslipSnapshots.PolicySnapshot();

        snapshot.setWorkingDaysPerMonth(PayrollConfig.getWorkingDaysPerMonth());
        snapshot.setWorkingHoursPerDay(PayrollConfig.getWorkingHoursPerDay());

        // Create OT multipliers map
        Map<String, Double> otMultipliers = new HashMap<>();
        otMultipliers.put("WEEKDAY", OTType.WEEKDAY.getMultiplier());
        otMultipliers.put("WEEKEND", OTType.WEEKEND.getMultiplier());
        otMultipliers.put("HOLIDAY", OTType.HOLIDAY.getMultiplier());
        otMultipliers.put("COMPENSATORY", OTType.COMPENSATORY.getMultiplier());
        snapshot.setOtMultipliers(otMultipliers);

        // Store policy objects (will be serialized to JSON)
        snapshot.setLatePolicy(PayrollConfig.getLatePolicyConfig());
        snapshot.setUnderHoursPolicy(PayrollConfig.getUnderHoursPolicyConfig());
        snapshot.setTaxConfig(PayrollConfig.getTaxConfig());

        return snapshot;
    }

    private PayslipSnapshots.TimesheetSnapshot createTimesheetSnapshot(Long userId, LocalDate periodStart, LocalDate periodEnd) throws SQLException {
        return createEnhancedTimesheetSnapshot(userId, periodStart, periodEnd);
    }

    /**
     * Calculate actual working days in a period, excluding weekends and holidays
     * OPTIMIZED: Uses cached holidays to avoid N+1 query problem
     * Requirements: 6.2, 6.3
     *
     * @param periodStart Start date of the period (inclusive)
     * @param periodEnd End date of the period (inclusive)
     * @return Number of actual working days
     */
    private int calculateWorkingDaysInPeriod(LocalDate periodStart, LocalDate periodEnd) {
        if (periodStart == null || periodEnd == null) {
            logger.warning("Period dates cannot be null");
            return 0;
        }

        if (periodStart.isAfter(periodEnd)) {
            logger.warning("Period start date cannot be after end date");
            return 0;
        }

        // OPTIMIZATION: Load all holidays in period ONCE (instead of querying for each day)
        java.util.Set<LocalDate> cachedHolidays = loadHolidaysInPeriod(periodStart, periodEnd);
        java.util.Set<LocalDate> cachedCompensatoryDays = loadCompensatoryDaysInPeriod(periodStart, periodEnd);

        int workingDays = 0;
        LocalDate current = periodStart;

        while (!current.isAfter(periodEnd)) {
            // Skip weekends (Saturday and Sunday)
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY &&
                current.getDayOfWeek() != DayOfWeek.SUNDAY) {

                // Skip holidays (using cached set - NO DB QUERY)
                if (!isHoliday(current, cachedHolidays)) {
                    // Skip compensatory days (using cached set - NO DB QUERY)
                    if (!isCompensatoryDay(current, cachedCompensatoryDays)) {
                        workingDays++;
                    }
                }
            }
            current = current.plusDays(1);
        }

        logger.info(String.format("Calculated working days for period %s to %s: %d days (using cached holidays)",
                   periodStart, periodEnd, workingDays));

        return workingDays;
    }

    /**
     * Load all holidays in a period ONCE to avoid N+1 query problem
     * OPTIMIZATION: Single DB query instead of multiple queries
     *
     * @param periodStart Start date
     * @param periodEnd End date
     * @return Set of holiday dates
     */
    private java.util.Set<LocalDate> loadHolidaysInPeriod(LocalDate periodStart, LocalDate periodEnd) {
        java.util.Set<LocalDate> holidays = new java.util.HashSet<>();

        try {
            List<Holiday> holidayList = holidayDao.getHolidaysInRange(periodStart, periodEnd);
            for (Holiday holiday : holidayList) {
                if (holiday.getDateHoliday() != null) {
                    holidays.add(holiday.getDateHoliday());
                }
            }
            logger.fine(String.format("Loaded %d holidays for period %s to %s in single query",
                                    holidays.size(), periodStart, periodEnd));
        } catch (SQLException e) {
            logger.warning("Error loading holidays in period: " + e.getMessage());
            // Fallback to empty set - will use hardcoded holidays if needed
        }

        return holidays;
    }

    /**
     * Load all compensatory days in a period ONCE to avoid N+1 query problem
     * OPTIMIZATION: Single DB query instead of multiple queries
     *
     * @param periodStart Start date
     * @param periodEnd End date
     * @return Set of compensatory day dates
     */
    private java.util.Set<LocalDate> loadCompensatoryDaysInPeriod(LocalDate periodStart, LocalDate periodEnd) {
        java.util.Set<LocalDate> compensatoryDays = new java.util.HashSet<>();

        try {
            List<Holiday> holidayList = holidayDao.getHolidaysInRange(periodStart, periodEnd);
            for (Holiday holiday : holidayList) {
                if (holiday.getDateHoliday() != null && holiday.getName() != null) {
                    String name = holiday.getName().toLowerCase();
                    if (name.contains("nghỉ bù") || name.contains("nghi bu")) {
                        compensatoryDays.add(holiday.getDateHoliday());
                    }
                }
            }
            logger.fine(String.format("Loaded %d compensatory days for period %s to %s in single query",
                                    compensatoryDays.size(), periodStart, periodEnd));
        } catch (SQLException e) {
            logger.warning("Error loading compensatory days in period: " + e.getMessage());
            // Fallback to empty set
        }

        return compensatoryDays;
    }

    /**
     * Check if a date is a holiday using database lookup
     * Reuses the existing holiday system from OT calculations
     *
     * @param date Date to check
     * @return true if the date is a holiday
     * @deprecated Use isHoliday(LocalDate, Set<LocalDate>) with cached holidays instead to avoid N+1 queries
     */
    @Deprecated
    private boolean isHoliday(LocalDate date) {
        try {
            return holidayDao.isHoliday(date);
        } catch (SQLException e) {
            logger.warning("Error checking holiday for date " + date + ": " + e.getMessage());
            // Fallback to hardcoded holidays if database fails
            return isHardcodedHoliday(date);
        }
    }

    /**
     * Check if a date is a holiday using cached holiday set (OPTIMIZED - NO DB QUERY)
     * This method prevents N+1 query problem by using pre-loaded holidays
     *
     * @param date Date to check
     * @param cachedHolidays Set of holiday dates loaded once from database
     * @return true if the date is a holiday
     */
    private boolean isHoliday(LocalDate date, java.util.Set<LocalDate> cachedHolidays) {
        return cachedHolidays.contains(date);
    }

    /**
     * Fallback method for hardcoded holidays when database is unavailable
     * Uses LunarCalendarUtil for accurate lunar calendar calculations
     *
     * @param date Date to check
     * @return true if the date is a hardcoded holiday
     */
    private boolean isHardcodedHoliday(LocalDate date) {
        // Check solar calendar holidays (fixed dates)
        if (isSolarHoliday(date)) {
            return true;
        }

        // Check lunar calendar holidays using LunarCalendarUtil
        try {
            return LunarCalendarUtil.isLunarHoliday(date);
        } catch (Exception e) {
            logger.warning("Error checking lunar holiday for date " + date + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a date is a solar calendar holiday (fixed dates)
     *
     * @param date Date to check
     * @return true if the date is a solar holiday
     */
    private boolean isSolarHoliday(LocalDate date) {
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // Common Vietnamese holidays (fixed solar dates)
        return (month == 1 && day == 1) ||    // New Year's Day
               (month == 4 && day == 30) ||   // Liberation Day
               (month == 5 && day == 1) ||    // Labor Day
               (month == 9 && day == 2) ||    // National Day
               (month == 10 && day == 20);    // Vietnamese Women's Day
    }

    /**
     * Check if a date is a compensatory day (nghỉ bù)
     * Uses HolidayDao to check for substitute days in the database
     *
     * @param date Date to check
     * @return true if the date is a compensatory day
     * @deprecated Use isCompensatoryDay(LocalDate, Set<LocalDate>) with cached compensatory days instead to avoid N+1 queries
     */
    @Deprecated
    private boolean isCompensatoryDay(LocalDate date) {
        try {
            return holidayDao.isCompensatoryDay(date);
        } catch (SQLException e) {
            logger.warning("Error checking compensatory day for date " + date + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a date is a compensatory day using cached set (OPTIMIZED - NO DB QUERY)
     * This method prevents N+1 query problem by using pre-loaded compensatory days
     *
     * @param date Date to check
     * @param cachedCompensatoryDays Set of compensatory day dates loaded once from database
     * @return true if the date is a compensatory day
     */
    private boolean isCompensatoryDay(LocalDate date, java.util.Set<LocalDate> cachedCompensatoryDays) {
        return cachedCompensatoryDays.contains(date);
    }

    /**
     * Get working days count for a specific month and year
     * This is a convenience method that can be used for validation
     *
     * @param year Year
     * @param month Month (1-12)
     * @return Number of working days in the month
     */
    public int getWorkingDaysInMonth(int year, int month) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        return calculateWorkingDaysInPeriod(monthStart, monthEnd);
    }

    /**
     * Get all holidays in a date range for debugging/logging purposes
     *
     * @param periodStart Start date
     * @param periodEnd End date
     * @return List of holiday dates in the period
     */
    public List<LocalDate> getHolidaysInPeriod(LocalDate periodStart, LocalDate periodEnd) {
        List<LocalDate> holidays = new ArrayList<>();
        LocalDate current = periodStart;

        while (!current.isAfter(periodEnd)) {
            if (isHoliday(current)) {
                holidays.add(current);
            }
            current = current.plusDays(1);
        }

        return holidays;
    }

    /**
     * Serialize snapshots to JSON string for storage
     *
     * @param snapshots PayslipSnapshots object
     * @return JSON string representation
     */
    public String serializeSnapshots(PayslipSnapshots snapshots) {
        try {
            return objectMapper.writeValueAsString(snapshots);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing snapshots to JSON", e);
            throw new RuntimeException("Failed to serialize snapshots", e);
        }
    }

    /**
     * Deserialize snapshots from JSON string
     *
     * @param snapshotsJson JSON string representation
     * @return PayslipSnapshots object
     */
    public PayslipSnapshots deserializeSnapshots(String snapshotsJson) {
        try {
            return objectMapper.readValue(snapshotsJson, PayslipSnapshots.class);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error deserializing snapshots from JSON: " + snapshotsJson, e);
            throw new RuntimeException("Failed to deserialize snapshots", e);
        }
    }

    /**
     * Enhanced timesheet snapshot creation with detailed attendance data
     * NEW LOGIC: Only count weekday attendance (Mon-Fri, not holiday/compensatory)
     * OPTIMIZED: Uses cached holidays to avoid N+1 query problem
     */
    private PayslipSnapshots.TimesheetSnapshot createEnhancedTimesheetSnapshot(Long userId, LocalDate periodStart, LocalDate periodEnd) throws SQLException {
        PayslipSnapshots.TimesheetSnapshot snapshot = new PayslipSnapshots.TimesheetSnapshot();

        // OPTIMIZATION: Load all holidays and compensatory days ONCE for the entire period
        // This prevents N+1 query problem (was causing 100+ DB queries for 50 attendance records)
        java.util.Set<LocalDate> cachedHolidays = loadHolidaysInPeriod(periodStart, periodEnd);
        java.util.Set<LocalDate> cachedCompensatoryDays = loadCompensatoryDaysInPeriod(periodStart, periodEnd);

        logger.fine(String.format("Cached %d holidays and %d compensatory days for period %s to %s",
                                cachedHolidays.size(), cachedCompensatoryDays.size(), periodStart, periodEnd));

        // Get attendance records for the period
        AttendanceLogDao attendanceDao = new AttendanceLogDao();
        List<AttendanceLogDto> attendanceList = attendanceDao.findByFilter(
            userId, null, null, periodStart, periodEnd, null, null, null,
            Integer.MAX_VALUE, 0, false
        );

        // Calculate worked days and hours (ONLY WEEKDAYS, not weekend/holiday/compensatory)
        // Worked Hours: MAX 8h per day (OT hours NOT included)
        // Worked Days: Count UNIQUE dates (avoid duplicate counting if multiple records per day)
        java.util.Set<LocalDate> workedDaysSet = new java.util.HashSet<>();
        double workedHours = 0.0;
        int lateMinutes = 0;
        LocalTime standardStartTime = LocalTime.of(8, 0);
        int graceMinutes = 10;
        int workingHoursPerDay = PayrollConfig.getWorkingHoursPerDay(); // 8

        // Create a map to store worked hours per day
        java.util.Map<LocalDate, Double> workedHoursPerDay = new java.util.HashMap<>();

        for (AttendanceLogDto record : attendanceList) {
            LocalDate attendanceDate = record.getDate();

            // Check if this is a weekday (Mon-Fri) and not holiday/compensatory
            // OPTIMIZED: Using cached sets instead of DB queries (NO MORE N+1 PROBLEM!)
            if (attendanceDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                attendanceDate.getDayOfWeek() != DayOfWeek.SUNDAY &&
                !isHoliday(attendanceDate, cachedHolidays) &&
                !isCompensatoryDay(attendanceDate, cachedCompensatoryDays)) {

                // Add to worked days set (automatically handles duplicates)
                workedDaysSet.add(attendanceDate);

                // Calculate hours worked (MAX 8h, no OT counted)
                if (record.getCheckIn() != null && record.getCheckOut() != null) {
                    long minutesWorked = java.time.Duration.between(record.getCheckIn(), record.getCheckOut()).toMinutes();

                    // Subtract lunch break (1 hour) only if worked >= 6 hours
                    double hoursWorkedRaw = minutesWorked / 60.0;
                    if (hoursWorkedRaw >= 6.0) {
                        minutesWorked = Math.max(0, minutesWorked - 60);
                    }

                    double hoursWorked = minutesWorked / 60.0;

                    // Cap at 8 hours (OT hours NOT included in Worked Hours)
                    hoursWorked = Math.min(hoursWorked, workingHoursPerDay);

                    // Accumulate hours for this day (if multiple records, sum them up to max 8h)
                    double currentDayHours = workedHoursPerDay.getOrDefault(attendanceDate, 0.0);
                    double totalDayHours = Math.min(currentDayHours + hoursWorked, workingHoursPerDay);
                    workedHoursPerDay.put(attendanceDate, totalDayHours);
                }

                // Calculate late minutes (only count once per day)
                if (record.getCheckIn() != null &&
                    ("Late".equals(record.getStatus()) || "Late & Early Leave".equals(record.getStatus()))) {
                    LocalTime checkIn = record.getCheckIn();
                    LocalTime graceTime = standardStartTime.plusMinutes(graceMinutes);
                    if (checkIn.isAfter(graceTime)) {
                        long lateMinutesThisDay = java.time.Duration.between(standardStartTime, checkIn).toMinutes();
                        lateMinutes += Math.max(0, lateMinutesThisDay);
                    }
                }
            }
            // Weekend/holiday/compensatory attendance will be counted as OT, not worked days
        }

        // Calculate total worked hours from the map
        workedHours = workedHoursPerDay.values().stream().mapToDouble(Double::doubleValue).sum();
        double workedDays = workedDaysSet.size();

        snapshot.setWorkedDays((int) Math.round(workedDays));
        snapshot.setWorkedHours(workedHours);
        snapshot.setLateMinutesTotal(lateMinutes);

        logger.info(String.format("Weekday attendance calculated: userId=%d, workedDays=%.1f, workedHours=%.2f, lateMinutes=%d",
                                userId, workedDays, workedHours, lateMinutes));

        // Get approved leave requests for the period
        List<Request> approvedLeaveRequests = requestDao.findByUserIdAndDateRange(
            userId,
            periodStart.atStartOfDay(),
            periodEnd.atTime(23, 59, 59),
            List.of("APPROVED"),
            null
        );

        // Create a set of dates that have attendance records (to check overlap)
        // OPTIMIZED: Reuse cached holidays instead of querying DB again
        java.util.Set<LocalDate> attendanceDates = new java.util.HashSet<>();
        for (AttendanceLogDto record : attendanceList) {
            LocalDate attendanceDate = record.getDate();
            // Only count weekday attendance
            // OPTIMIZED: Using cached sets (NO MORE DUPLICATE QUERIES!)
            if (attendanceDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                attendanceDate.getDayOfWeek() != DayOfWeek.SUNDAY &&
                !isHoliday(attendanceDate, cachedHolidays) &&
                !isCompensatoryDay(attendanceDate, cachedCompensatoryDays)) {
                attendanceDates.add(attendanceDate);
            }
        }

        double paidLeaveDays = 0.0;
        int unpaidLeaveDays = 0;
        double paidLeaveHours = 0.0;
        int overlappedLeaveDays = 0; // Days with both leave request and attendance

        // Calculate leave days and hours with overlap checking (NEW LOGIC)
        for (Request request : approvedLeaveRequests) {
            if (request.getLeaveDetail() != null) {
                try {
                    String leaveTypeCode = request.getLeaveDetail().getLeaveTypeCode();
                    Optional<LeaveType> leaveTypeOpt = leaveTypeDao.findByCode(leaveTypeCode);

                    if (leaveTypeOpt.isPresent()) {
                        LeaveType leaveType = leaveTypeOpt.get();
                        boolean isPaid = leaveType.isPaid();

                        // Parse leave dates and day count from request (USE CORRECT FIELDS)
                        LocalDate leaveStartDate = LocalDate.parse(request.getLeaveDetail().getStartDate());
                        LocalDate leaveEndDate = LocalDate.parse(request.getLeaveDetail().getEndDate());

                        // Use durationDays if available, otherwise fallback to dayCount
                        Double durationDays = request.getLeaveDetail().getDurationDays();
                        double dayCount = durationDays != null ? durationDays : request.getLeaveDetail().getDayCount();

                        // Check if this is half-day leave
                        Boolean isHalfDay = request.getLeaveDetail().getIsHalfDay();
                        String halfDayPeriod = request.getLeaveDetail().getHalfDayPeriod(); // "AM" or "PM"
                        boolean isFullDay = (isHalfDay == null || !isHalfDay);

                        // Process each day in the leave period
                        LocalDate currentDate = leaveStartDate;
                        while (!currentDate.isAfter(leaveEndDate)) {
                            // Check if this leave day overlaps with attendance
                            if (attendanceDates.contains(currentDate)) {
                                // Employee came to work despite having leave request

                                if (isFullDay) {
                                    // Full-day leave + came to work → Only count leave, subtract worked day
                                    workedDays -= 1.0;
                                    workedHours -= workedHoursPerDay.getOrDefault(currentDate, 0.0);

                                    if (isPaid) {
                                        paidLeaveDays += 1.0;
                                        paidLeaveHours += workingHoursPerDay;
                                    } else {
                                        unpaidLeaveDays += 1;
                                    }

                                    overlappedLeaveDays++;
                                    logger.info(String.format("Full-day leave overlap: userId=%d, date=%s (only count leave)",
                                                            userId, currentDate));
                                } else {
                                    // Half-day leave + came to work
                                    // Subtract 0.5 from worked days (regardless of which session)
                                    // The leave balance is still deducted
                                    workedDays -= 0.5;
                                    workedHours -= (workingHoursPerDay / 2.0);

                                    if (isPaid) {
                                        paidLeaveDays += 0.5;
                                        paidLeaveHours += (workingHoursPerDay / 2.0);
                                    } else {
                                        unpaidLeaveDays += 1; // Count as 1 for unpaid (simplified)
                                    }

                                    logger.info(String.format("Half-day leave overlap: userId=%d, date=%s, period=%s (only count leave)",
                                                            userId, currentDate, halfDayPeriod));
                                }
                            } else {
                                // No attendance on this leave day → Count as leave
                                if (isPaid) {
                                    paidLeaveDays += dayCount;
                                    paidLeaveHours += (dayCount * workingHoursPerDay);
                                } else {
                                    unpaidLeaveDays += (int) Math.ceil(dayCount);
                                }
                            }
                            currentDate = currentDate.plusDays(1);
                        }
                    }
                } catch (Exception e) {
                    logger.warning(String.format("Error processing leave request %d: %s",
                                               request.getId(), e.getMessage()));
                }
            }
        }

        snapshot.setWorkedDays((int) Math.round(workedDays));
        snapshot.setWorkedHours(workedHours);
        snapshot.setPaidLeaveDays((int) Math.round(paidLeaveDays));
        snapshot.setUnpaidLeaveDays(unpaidLeaveDays);
        snapshot.setPaidLeaveHours(paidLeaveHours);

        logger.info(String.format("Leave calculation: userId=%d, paidLeaveDays=%.1f, unpaidLeaveDays=%d, overlappedDays=%d",
                                userId, paidLeaveDays, unpaidLeaveDays, overlappedLeaveDays));

        // Get approved OT requests for the period (NEW LOGIC)
        // OT hours and multiplier are taken directly from approved OT requests
        List<Request> approvedOTRequests = requestDao.findOTRequestsByUserIdAndDateRange(
            userId,
            periodStart.atStartOfDay(),
            periodEnd.atTime(23, 59, 59)
        );

        Map<String, Double> otHours = new HashMap<>();
        otHours.put("WEEKDAY", 0.0);
        otHours.put("WEEKEND", 0.0);
        otHours.put("HOLIDAY", 0.0);
        otHours.put("COMPENSATORY", 0.0);

        // Process approved OT requests
        // Take OT hours and multiplier directly from the request (already calculated and approved)
        for (Request otRequest : approvedOTRequests) {
            if (otRequest.getOtDetail() != null) {
                try {
                    LocalDate otDate = LocalDate.parse(otRequest.getOtDetail().getOtDate());
                    double hours = otRequest.getOtDetail().getOtHours(); // Use approved hours from request
                    Double payMultiplier = otRequest.getOtDetail().getPayMultiplier(); // Use multiplier from request
                    String otTypeFromRequest = otRequest.getOtDetail().getOtType(); // Get OT type from request

                    // Use OT type from request (already determined when creating the request)
                    String otTypeKey = otTypeFromRequest != null ? otTypeFromRequest : "WEEKDAY";

                    // Add to total OT hours for this type
                    otHours.put(otTypeKey, otHours.get(otTypeKey) + hours);

                    logger.info(String.format("OT approved: userId=%d, date=%s, type=%s, hours=%.2f, multiplier=%.1f",
                                            userId, otDate, otTypeKey, hours,
                                            payMultiplier != null ? payMultiplier : 1.5));

                } catch (Exception e) {
                    logger.warning(String.format("Error processing OT request %d: %s",
                                               otRequest.getId(), e.getMessage()));
                }
            }
        }

        snapshot.setOtHours(otHours);

        logger.info(String.format("Timesheet snapshot created: userId=%d, workedDays=%d, paidLeaveDays=%d, " +
                                "unpaidLeaveDays=%d, totalOTHours=%.2f",
                                userId, snapshot.getWorkedDays(), snapshot.getPaidLeaveDays(),
                                snapshot.getUnpaidLeaveDays(),
                                otHours.values().stream().mapToDouble(Double::doubleValue).sum()));

        return snapshot;
    }

    /**
     * Calculate total late minutes for a user in a given period
     * This method analyzes attendance records to determine lateness
     */
    private int calculateTotalLateMinutes(Long userId, LocalDate periodStart, LocalDate periodEnd) {
        try {
            // Get attendance records for the period
            AttendanceLogDao attendanceDao = new AttendanceLogDao();
            List<AttendanceLogDto> attendanceList = attendanceDao.findByFilter(
                userId, null, null, periodStart, periodEnd, null, null, null,
                Integer.MAX_VALUE, 0, false
            );

            int totalLateMinutes = 0;
            LocalTime standardStartTime = LocalTime.of(8, 0); // 8:00 AM standard start time
            int graceMinutes = 10; // 10 minutes grace period

            for (AttendanceLogDto record : attendanceList) {
                if (record.getCheckIn() != null &&
                    ("Late".equals(record.getStatus()) || "Late & Early Leave".equals(record.getStatus()))) {

                    // Calculate late minutes for this day
                    LocalTime checkIn = record.getCheckIn();
                    LocalTime graceTime = standardStartTime.plusMinutes(graceMinutes);

                    if (checkIn.isAfter(graceTime)) {
                        long lateMinutesThisDay = java.time.Duration.between(standardStartTime, checkIn).toMinutes();
                        totalLateMinutes += Math.max(0, lateMinutesThisDay);
                    }
                }
            }

            logger.fine(String.format("Calculated total late minutes: userId=%d, period=%s to %s, lateMinutes=%d",
                                    userId, periodStart, periodEnd, totalLateMinutes));

            return totalLateMinutes;

        } catch (Exception e) {
            logger.warning(String.format("Error calculating late minutes for user %d: %s", userId, e.getMessage()));
            return 0; // Return 0 if calculation fails
        }
    }
}