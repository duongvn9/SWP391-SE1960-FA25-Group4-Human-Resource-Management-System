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
    private Integer currentWeekHours;    // Giờ OT tuần này
    private Integer weeklyLimit;         // 48h
    private Integer monthlyHours;        // Giờ OT tháng này
    private Integer monthlyLimit;        // 40h
    private Integer annualHours;         // Giờ OT năm này
    private Integer annualLimit;         // 300h hoặc 200h

    /**
     * Default constructor with standard limits and zero hours.
     */
    public OTBalance() {
        this.weeklyLimit = 48;
        this.monthlyLimit = 40;
        this.annualLimit = 300;
        this.currentWeekHours = 0;
        this.monthlyHours = 0;
        this.annualHours = 0;
    }

    /**
     * Constructor with current hours and standard limits.
     *
     * @param currentWeekHours hours used in current week
     * @param monthlyHours hours used in current month
     * @param annualHours hours used in current year
     */
    public OTBalance(Integer currentWeekHours, Integer monthlyHours, Integer annualHours) {
        this.currentWeekHours = currentWeekHours;
        this.weeklyLimit = 48;
        this.monthlyHours = monthlyHours;
        this.monthlyLimit = 40;
        this.annualHours = annualHours;
        this.annualLimit = 300;
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
        this.currentWeekHours = currentWeekHours;
        this.weeklyLimit = weeklyLimit;
        this.monthlyHours = monthlyHours;
        this.monthlyLimit = monthlyLimit;
        this.annualHours = annualHours;
        this.annualLimit = annualLimit;
    }

    // Calculated Methods

    /**
     * Calculates remaining weekly OT hours.
     *
     * @return remaining hours in the week (minimum 0)
     */
    public Integer getWeeklyRemaining() {
        return Math.max(0, weeklyLimit - currentWeekHours);
    }

    /**
     * Calculates remaining monthly OT hours.
     *
     * @return remaining hours in the month (minimum 0)
     */
    public Integer getMonthlyRemaining() {
        return Math.max(0, monthlyLimit - monthlyHours);
    }

    /**
     * Calculates remaining annual OT hours.
     *
     * @return remaining hours in the year (minimum 0)
     */
    public Integer getAnnualRemaining() {
        return Math.max(0, annualLimit - annualHours);
    }

    /**
     * Calculates percentage of weekly OT hours used.
     * Used for progress bar display in UI.
     *
     * @return percentage (0-100)
     */
    public Integer getWeeklyPercentage() {
        if (weeklyLimit <= 0) {
            return 0;
        }
        return Math.min(100, (int) ((currentWeekHours * 100.0) / weeklyLimit));
    }

    /**
     * Calculates percentage of monthly OT hours used.
     * Used for progress bar display in UI.
     *
     * @return percentage (0-100)
     */
    public Integer getMonthlyPercentage() {
        if (monthlyLimit <= 0) {
            return 0;
        }
        return Math.min(100, (int) ((monthlyHours * 100.0) / monthlyLimit));
    }

    /**
     * Calculates percentage of annual OT hours used.
     * Used for progress bar display in UI.
     *
     * @return percentage (0-100)
     */
    public Integer getAnnualPercentage() {
        if (annualLimit <= 0) {
            return 0;
        }
        return Math.min(100, (int) ((annualHours * 100.0) / annualLimit));
    }

    // Getters and Setters

    public Integer getCurrentWeekHours() {
        return currentWeekHours;
    }

    public void setCurrentWeekHours(Integer currentWeekHours) {
        this.currentWeekHours = currentWeekHours;
    }

    public Integer getWeeklyLimit() {
        return weeklyLimit;
    }

    public void setWeeklyLimit(Integer weeklyLimit) {
        this.weeklyLimit = weeklyLimit;
    }

    public Integer getMonthlyHours() {
        return monthlyHours;
    }

    public void setMonthlyHours(Integer monthlyHours) {
        this.monthlyHours = monthlyHours;
    }

    public Integer getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(Integer monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public Integer getAnnualHours() {
        return annualHours;
    }

    public void setAnnualHours(Integer annualHours) {
        this.annualHours = annualHours;
    }

    public Integer getAnnualLimit() {
        return annualLimit;
    }

    public void setAnnualLimit(Integer annualLimit) {
        this.annualLimit = annualLimit;
    }

    @Override
    public String toString() {
        return "OTBalance{" +
                "currentWeekHours=" + currentWeekHours +
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
