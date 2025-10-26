package group4.hrms.dto;

/**
 * Data Transfer Object for OT (Overtime) Balance information.
 *
 * <p>Tracks employee's overtime hours usage across different time periods:
 * <ul>
 *   <li>Weekly: Current week hours vs 48-hour limit</li>
 *   <li>Monthly: Current month hours vs 40-hour limit</li>
 *   <li>Annual: Current year hours vs 300-hour limit (or 200 if not eligible)</li>
 * </ul>
 *
 * <p>Provides calculated methods for remaining hours and usage percentages
 * to support UI display with progress bars and warning indicators.
 *
 * @author Group4
 * @version 1.0
 * @see group4.hrms.service.OTRequestService#getOTBalance(Long)
 */
public class OTBalance {
    private Double currentWeekHours;    // Giờ OT tuần này (may be fractional)
    private Integer weeklyApprovedCount;  // Number of approved OT requests in current week
    private Integer monthlyApprovedCount; // Number of approved OT requests in current month
    private Integer annualApprovedCount;  // Number of approved OT requests in current year
    private Double regularHoursThisWeek; // Regular scheduled work hours in the week (used to compute remaining OT)
    private Double weeklyLimit;         // 48h
    private Double monthlyHours;        // Giờ OT tháng này
    private Double monthlyLimit;        // 40h
    private Double annualHours;         // Giờ OT năm này
    private Double annualLimit;         // 300h hoặc 200h

    // Date ranges for display
    private String weekStartDate;       // Monday of current week (format: dd/MM)
    private String weekEndDate;         // Sunday of current week (format: dd/MM)
    private String monthName;           // Current month name (e.g., "October 2025")

    /**
     * Default constructor with standard limits and zero hours.
     */
    public OTBalance() {
        this.weeklyLimit = 48.0;
        this.monthlyLimit = 40.0;
        this.annualLimit = 300.0;
        this.currentWeekHours = 0.0;
        this.weeklyApprovedCount = 0;
        this.monthlyApprovedCount = 0;
        this.annualApprovedCount = 0;
        this.regularHoursThisWeek = 0.0;
        this.monthlyHours = 0.0;
        this.annualHours = 0.0;
    }

    /**
     * Constructor with current hours and standard limits.
     *
     * @param currentWeekHours hours used in current week
     * @param monthlyHours hours used in current month
     * @param annualHours hours used in current year
     */
    public OTBalance(Integer currentWeekHours, Integer monthlyHours, Integer annualHours) {
        this.currentWeekHours = currentWeekHours == null ? 0.0 : currentWeekHours.doubleValue();
        this.weeklyLimit = 48.0;
        this.weeklyApprovedCount = 0;
        this.monthlyApprovedCount = 0;
        this.annualApprovedCount = 0;
        this.regularHoursThisWeek = 0.0;
        this.monthlyHours = monthlyHours == null ? 0.0 : monthlyHours.doubleValue();
        this.monthlyLimit = 40.0;
        this.annualHours = annualHours == null ? 0.0 : annualHours.doubleValue();
        this.annualLimit = 300.0;
    }

    /**
     * Constructor with all fields including custom limits.
     *
     * @param currentWeekHours hours used in current week
     * @param weeklyLimit maximum hours allowed per week
     * @param monthlyHours hours used in current month
     * @param monthlyLimit maximum hours allowed per month
     * @param annualHours hours used in current year
     * @param annualLimit maximum hours allowed per year
     */
    public OTBalance(Integer currentWeekHours, Integer weeklyLimit,
                    Integer monthlyHours, Integer monthlyLimit,
                    Integer annualHours, Integer annualLimit) {
        this.currentWeekHours = currentWeekHours == null ? 0.0 : currentWeekHours.doubleValue();
        this.weeklyLimit = weeklyLimit == null ? 48.0 : weeklyLimit.doubleValue();
        this.regularHoursThisWeek = 0.0;
        this.monthlyHours = monthlyHours == null ? 0.0 : monthlyHours.doubleValue();
        this.monthlyLimit = monthlyLimit == null ? 40.0 : monthlyLimit.doubleValue();
        this.annualHours = annualHours == null ? 0.0 : annualHours.doubleValue();
        this.annualLimit = annualLimit == null ? 300.0 : annualLimit.doubleValue();
    }

    // Calculated Methods

    /**
     * Calculates remaining weekly OT hours as a fractional value.
     *
     * @return remaining hours in the week (minimum 0.0), rounded to 1 decimal
     */
    public Double getWeeklyRemaining() {
        // Remaining allowed OT this week = weeklyLimit - regularHoursThisWeek - currentWeekHours
        double remaining = (weeklyLimit != null ? weeklyLimit : 0.0) -
                           (regularHoursThisWeek != null ? regularHoursThisWeek : 0.0) -
                           (currentWeekHours != null ? currentWeekHours : 0.0);
        double result = Math.max(0.0, remaining);
        // round to 1 decimal for display consistency
        return Math.round(result * 10.0) / 10.0;
    }

    /**
     * Calculates remaining monthly OT hours as a fractional value.
     *
     * @return remaining hours in the month (minimum 0.0), rounded to 1 decimal
     */
    public Double getMonthlyRemaining() {
        double remaining = (monthlyLimit != null ? monthlyLimit : 0.0) -
                           (monthlyHours != null ? monthlyHours : 0.0);
        double result = Math.max(0.0, remaining);
        return Math.round(result * 10.0) / 10.0;
    }

    /**
     * Calculates remaining annual OT hours as a fractional value.
     *
     * @return remaining hours in the year (minimum 0.0), rounded to 1 decimal
     */
    public Double getAnnualRemaining() {
        double remaining = (annualLimit != null ? annualLimit : 0.0) -
                           (annualHours != null ? annualHours : 0.0);
        double result = Math.max(0.0, remaining);
        return Math.round(result * 10.0) / 10.0;
    }

    /**
     * Calculates percentage of weekly OT hours used.
     * Used for progress bar display in UI.
     *
     * Percentage is calculated based on OT hours used vs allowed OT hours in the week.
     * Allowed OT = weeklyLimit (48h) - regularHoursThisWeek
     *
     * @return percentage (0-100)
     */
    public Integer getWeeklyPercentage() {
        if (weeklyLimit == null || weeklyLimit <= 0.0) {
            return 0;
        }

        // Calculate allowed OT hours in the week
        double regularHours = (regularHoursThisWeek != null ? regularHoursThisWeek : 0.0);
        double allowedOT = weeklyLimit - regularHours;

        // If no OT allowed, return 100% if any OT used, otherwise 0%
        if (allowedOT <= 0.0) {
            double used = (currentWeekHours != null ? currentWeekHours : 0.0);
            return used > 0.0 ? 100 : 0;
        }

        // Calculate percentage based on OT hours used vs allowed OT
        double used = (currentWeekHours != null ? currentWeekHours : 0.0);
        return Math.min(100, (int) Math.round((used * 100.0) / allowedOT));
    }

    /**
     * Calculates percentage of monthly OT hours used.
     * Used for progress bar display in UI.
     *
     * @return percentage (0-100)
     */
    public Integer getMonthlyPercentage() {
        if (monthlyLimit == null || monthlyLimit <= 0.0) {
            return 0;
        }
        double used = (monthlyHours != null ? monthlyHours : 0.0);
        return Math.min(100, (int) Math.round((used * 100.0) / monthlyLimit));
    }

    /**
     * Calculates percentage of annual OT hours used.
     * Used for progress bar display in UI.
     *
     * @return percentage (0-100)
     */
    public Integer getAnnualPercentage() {
        if (annualLimit == null || annualLimit <= 0.0) {
            return 0;
        }
        double used = (annualHours != null ? annualHours : 0.0);
        return Math.min(100, (int) Math.round((used * 100.0) / annualLimit));
    }

    // Getters and Setters

    public Double getCurrentWeekHours() {
        return currentWeekHours;
    }

    public void setCurrentWeekHours(Double currentWeekHours) {
        this.currentWeekHours = currentWeekHours;
    }

    public Integer getWeeklyApprovedCount() {
        return weeklyApprovedCount;
    }

    public void setWeeklyApprovedCount(Integer weeklyApprovedCount) {
        this.weeklyApprovedCount = weeklyApprovedCount;
    }

    public Double getWeeklyLimit() {
        return weeklyLimit;
    }

    public void setWeeklyLimit(Double weeklyLimit) {
        this.weeklyLimit = weeklyLimit;
    }

    public Double getMonthlyHours() {
        return monthlyHours;
    }

    public void setMonthlyHours(Double monthlyHours) {
        this.monthlyHours = monthlyHours;
    }

    public Double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(Double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public Double getAnnualHours() {
        return annualHours;
    }

    public void setAnnualHours(Double annualHours) {
        this.annualHours = annualHours;
    }

    public Double getAnnualLimit() {
        return annualLimit;
    }

    public void setAnnualLimit(Double annualLimit) {
        this.annualLimit = annualLimit;
    }

    public Double getRegularHoursThisWeek() {
        return regularHoursThisWeek;
    }

    public void setRegularHoursThisWeek(Double regularHoursThisWeek) {
        this.regularHoursThisWeek = regularHoursThisWeek;
    }

    public Integer getMonthlyApprovedCount() {
        return monthlyApprovedCount;
    }

    public void setMonthlyApprovedCount(Integer monthlyApprovedCount) {
        this.monthlyApprovedCount = monthlyApprovedCount;
    }

    public Integer getAnnualApprovedCount() {
        return annualApprovedCount;
    }

    public void setAnnualApprovedCount(Integer annualApprovedCount) {
        this.annualApprovedCount = annualApprovedCount;
    }

    public String getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(String weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public String getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(String weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    @Override
    public String toString() {
    return "OTBalance{" +
        "currentWeekHours=" + currentWeekHours +
        ", regularHoursThisWeek=" + regularHoursThisWeek +
        ", weeklyLimit=" + weeklyLimit +
        ", monthlyHours=" + monthlyHours +
        ", monthlyLimit=" + monthlyLimit +
        ", annualHours=" + annualHours +
        ", annualLimit=" + annualLimit +
        ", weeklyRemaining=" + getWeeklyRemaining() +
        ", monthlyRemaining=" + getMonthlyRemaining() +
        ", annualRemaining=" + getAnnualRemaining() +
        '}';
    }
}
