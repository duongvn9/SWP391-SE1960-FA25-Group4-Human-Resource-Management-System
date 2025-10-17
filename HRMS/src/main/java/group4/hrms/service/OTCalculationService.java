package group4.hrms.service;

import group4.hrms.dao.HolidayDao;
import group4.hrms.model.Holiday;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * Service for OT (Overtime) calculation and type determination
 * Implements BR-OT-01, BR-OT-02, BR-OT-03
 *
 * Business Rules:
 * - BR-OT-01: OT multipliers: 150% (weekday), 200% (weekly rest day), 300% (public holiday)
 * - BR-OT-02: Holiday overlaps rest day → substitute day paid as weekly rest day (200%)
 * - BR-OT-03: Weekend/holiday work obeys caps (≤10h/day, ≤48h/week)
 */
public class OTCalculationService {

    private static final Logger logger = Logger.getLogger(OTCalculationService.class.getName());

    private final HolidayDao holidayDao;

    public OTCalculationService(HolidayDao holidayDao) {
        this.holidayDao = holidayDao;
    }

    /**
     * OT Type enum
     */
    public enum OTType {
        WEEKDAY("WEEKDAY", 1.5),           // 150%
        WEEKEND("WEEKEND", 2.0),           // 200%
        HOLIDAY("HOLIDAY", 3.0),           // 300% - Original holiday only
        COMPENSATORY("COMPENSATORY", 2.0); // 200% - Substitute day

        private final String code;
        private final double multiplier;

        OTType(String code, double multiplier) {
            this.code = code;
            this.multiplier = multiplier;
        }

        public String getCode() {
            return code;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }

    /**
     * Determine OT type for a specific date
     *
     * Logic:
     * 1. Check if date is a public holiday (original) → HOLIDAY (300%)
     * 2. Check if date is a substitute day → COMPENSATORY (200%)
     * 3. Check if date is weekend (Sat/Sun) → WEEKEND (200%)
     * 4. Otherwise → WEEKDAY (150%)
     *
     * @param date The date to check
     * @return OT type for the date
     */
    public OTType determineOTType(LocalDate date) {
        logger.fine("Determining OT type for date: " + date);

        try {
            // 1. Check if it's a holiday
            Holiday holiday = holidayDao.findByDate(date);

            if (holiday != null) {
                // Check if it's a substitute day (BR-OT-02)
                if (holiday.isSubstituteDay()) {
                    logger.info("Date " + date + " is a COMPENSATORY day (substitute) - 200% OT");
                    return OTType.COMPENSATORY;
                } else {
                    // Original public holiday
                    logger.info("Date " + date + " is a HOLIDAY (original) - 300% OT");
                    return OTType.HOLIDAY;
                }
            }

            // 2. Check if it's weekend
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                logger.info("Date " + date + " is a WEEKEND - 200% OT");
                return OTType.WEEKEND;
            }

            // 3. Otherwise it's a weekday
            logger.info("Date " + date + " is a WEEKDAY - 150% OT");
            return OTType.WEEKDAY;

        } catch (SQLException e) {
            logger.warning("Error checking holiday for date " + date + ": " + e.getMessage());
            // Fallback to day of week check
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                return OTType.WEEKEND;
            }
            return OTType.WEEKDAY;
        }
    }

    /**
     * Get OT multiplier for a specific date
     *
     * @param date The date to check
     * @return OT multiplier (1.5, 2.0, or 3.0)
     */
    public double getOTMultiplier(LocalDate date) {
        OTType otType = determineOTType(date);
        return otType.getMultiplier();
    }

    /**
     * Calculate OT pay
     *
     * @param date The date of OT work
     * @param hours Number of OT hours
     * @param hourlyRate Hourly rate
     * @return OT pay amount
     */
    public double calculateOTPay(LocalDate date, double hours, double hourlyRate) {
        double multiplier = getOTMultiplier(date);
        double otPay = hourlyRate * hours * multiplier;

        logger.info(String.format("OT Pay calculation: date=%s, hours=%.1f, rate=%.0f, multiplier=%.1f, pay=%.0f",
                   date, hours, hourlyRate, multiplier, otPay));

        return otPay;
    }

    /**
     * Validate OT hours against caps (BR-OT-03, BR-AD-02, BR-AD-03, BR-AD-04)
     *
     * @param dailyHours Daily OT hours
     * @param weeklyHours Weekly OT hours
     * @param monthlyHours Monthly OT hours
     * @param annualHours Annual OT hours
     * @return Validation result with capped values
     */
    public OTValidationResult validateOTHours(double dailyHours, double weeklyHours,
                                              double monthlyHours, double annualHours) {
        OTValidationResult result = new OTValidationResult();

        // BR-AD-02: Daily total (regular + OT) ≤ 10h
        // Assuming 8h regular work, max OT = 2h
        double maxDailyOT = 2.0;
        if (dailyHours > maxDailyOT) {
            result.dailyCapped = true;
            result.dailyAllowed = maxDailyOT;
            result.dailyExcess = dailyHours - maxDailyOT;
        } else {
            result.dailyAllowed = dailyHours;
        }

        // BR-AD-02: Weekly total ≤ 48h
        // Assuming 40h regular work, max OT = 8h
        double maxWeeklyOT = 8.0;
        if (weeklyHours > maxWeeklyOT) {
            result.weeklyCapped = true;
            result.weeklyAllowed = maxWeeklyOT;
            result.weeklyExcess = weeklyHours - maxWeeklyOT;
        } else {
            result.weeklyAllowed = weeklyHours;
        }

        // BR-AD-03: Monthly OT cap ≤ 40h
        double maxMonthlyOT = 40.0;
        if (monthlyHours > maxMonthlyOT) {
            result.monthlyCapped = true;
            result.monthlyAllowed = maxMonthlyOT;
            result.monthlyExcess = monthlyHours - maxMonthlyOT;
        } else {
            result.monthlyAllowed = monthlyHours;
        }

        // BR-AD-04: Annual OT cap ≤ 300h (or ≤ 200h)
        double maxAnnualOT = 300.0;  // Can be configured
        if (annualHours > maxAnnualOT) {
            result.annualCapped = true;
            result.annualAllowed = maxAnnualOT;
            result.annualExcess = annualHours - maxAnnualOT;
        } else {
            result.annualAllowed = annualHours;
        }

        result.isValid = !result.dailyCapped && !result.weeklyCapped &&
                        !result.monthlyCapped && !result.annualCapped;

        return result;
    }

    /**
     * OT Validation Result
     */
    public static class OTValidationResult {
        public boolean isValid = true;

        public boolean dailyCapped = false;
        public double dailyAllowed = 0;
        public double dailyExcess = 0;

        public boolean weeklyCapped = false;
        public double weeklyAllowed = 0;
        public double weeklyExcess = 0;

        public boolean monthlyCapped = false;
        public double monthlyAllowed = 0;
        public double monthlyExcess = 0;

        public boolean annualCapped = false;
        public double annualAllowed = 0;
        public double annualExcess = 0;

        public String getMessage() {
            if (isValid) {
                return "OT hours within all caps";
            }

            StringBuilder msg = new StringBuilder("OT hours exceed caps: ");
            if (dailyCapped) {
                msg.append(String.format("Daily %.1fh (max 2h), ", dailyExcess));
            }
            if (weeklyCapped) {
                msg.append(String.format("Weekly %.1fh (max 8h), ", weeklyExcess));
            }
            if (monthlyCapped) {
                msg.append(String.format("Monthly %.1fh (max 40h), ", monthlyExcess));
            }
            if (annualCapped) {
                msg.append(String.format("Annual %.1fh (max 300h), ", annualExcess));
            }

            return msg.toString().replaceAll(", $", "");
        }
    }
}
