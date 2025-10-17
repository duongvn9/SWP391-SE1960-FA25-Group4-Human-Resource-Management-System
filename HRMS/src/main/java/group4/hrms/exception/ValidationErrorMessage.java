package group4.hrms.exception;

/**
 * ValidationErrorMessage - Error message templates for leave request validation
 * Requirements: 6
 *
 * Provides structured error messages with detailed information for:
 * - Overlap detection errors
 * - Balance validation errors
 * - OT conflict errors
 */
public class ValidationErrorMessage {

    private final String type;
    private final String message;
    private final String details;

    private ValidationErrorMessage(String type, String message, String details) {
        this.type = type;
        this.message = message;
        this.details = details;
    }


    public static ValidationErrorMessage overlapError(
            String existingLeaveType,
            String status,
            String startDate,
            String endDate) {

        String message = "Leave request overlaps with existing request";
        String details = String.format(
            "You already have a %s leave request (%s) from %s to %s.\n\n" +
            "Please choose a different date range or cancel the existing request before creating a new one.",
            existingLeaveType,
            status.equals("PENDING") ? "Pending Approval" : "Approved",
            startDate,
            endDate
        );

        return new ValidationErrorMessage("OVERLAP", message, details);
    }

    /**
     * Create balance exceeded error message
     *
     * @param leaveTypeName Name of leave type
     * @param remainingDays Number of days remaining
     * @param usedDays Number of days already used
     * @param requestedDays Number of days requested
     * @param totalAllowed Total days allowed
     * @return ValidationErrorMessage
     */
    public static ValidationErrorMessage balanceExceededError(
            String leaveTypeName,
            int remainingDays,
            int usedDays,
            int requestedDays,
            int totalAllowed) {

        String message = "Insufficient leave balance";
        String details = String.format(
            "Leave Type: %s\n" +
            "• Total Allowed: %d days\n" +
            "• Already Used: %d days\n" +
            "• Remaining: %d days\n" +
            "• You are requesting: %d days\n\n" +
            "Please reduce the number of days or choose a different leave type.",
            leaveTypeName,
            totalAllowed,
            usedDays,
            remainingDays,
            requestedDays
        );

        return new ValidationErrorMessage("BALANCE_EXCEEDED", message, details);
    }

    /**
     * Create OT conflict error message
     *
     * @param otDate Date of approved OT request
     * @param otHours Number of OT hours
     * @param startTime OT start time
     * @param endTime OT end time
     * @return ValidationErrorMessage
     */
    public static ValidationErrorMessage otConflictError(
            String otDate,
            Double otHours,
            String startTime,
            String endTime) {

        String message = "Conflict with approved overtime request";
        String details = String.format(
            "You already have an approved OT request on %s:\n" +
            "• Time: %s - %s\n" +
            "• Hours: %.1f hours\n\n" +
            "Cannot request leave on a day with approved OT.\n" +
            "Please choose a different date or cancel the OT request first.",
            otDate,
            startTime,
            endTime,
            otHours
        );

        return new ValidationErrorMessage("OT_CONFLICT", message, details);
    }

    /**
     * Create half-day weekend/holiday error message
     * Requirements: 5.7
     *
     * @param date Date that is weekend or holiday
     * @param dayType Type of day ("weekend" or "holiday")
     * @return ValidationErrorMessage
     */
    public static ValidationErrorMessage halfDayWeekendHolidayError(String date, String dayType) {
        String message = "Half-day leave cannot be requested on " + dayType;
        String details = String.format(
            "The date %s is a %s.\n\n" +
            "Half-day leave can only be requested for working days (Monday-Friday, excluding holidays).\n" +
            "Please select a different date or request full-day leave if needed.",
            date,
            dayType
        );

        return new ValidationErrorMessage("HALF_DAY_NON_WORKING_DAY", message, details);
    }

    /**
     * Create half-day full-day conflict error message
     * Requirements: 5.2
     *
     * @param date Date of conflict
     * @param leaveType Leave type of conflicting full-day request
     * @param status Status of conflicting request
     * @return ValidationErrorMessage
     */
    public static ValidationErrorMessage halfDayFullDayConflictError(
            String date,
            String leaveType,
            String status) {

        String message = "Cannot request half-day: Full-day leave already exists";
        String details = String.format(
            "You already have a full-day %s leave on %s (Status: %s).\n\n" +
            "You cannot request half-day leave on a date that already has a full-day leave request.\n" +
            "Please choose a different date or cancel the existing full-day request first.",
            leaveType,
            date,
            status.equals("PENDING") ? "Pending Approval" : "Approved"
        );

        return new ValidationErrorMessage("HALF_DAY_FULL_DAY_CONFLICT", message, details);
    }

    /**
     * Create half-day same period conflict error message
     * Requirements: 5.3, 5.4
     *
     * @param date Date of conflict
     * @param period Period of conflict ("AM" or "PM")
     * @param leaveType Leave type of conflicting request
     * @param status Status of conflicting request
     * @return ValidationErrorMessage
     */
    public static ValidationErrorMessage halfDaySamePeriodConflictError(
            String date,
            String period,
            String leaveType,
            String status) {

        String periodName = period.equals("AM") ? "Morning (8:00-12:00)" : "Afternoon (13:00-17:00)";
        String otherPeriod = period.equals("AM") ? "Afternoon (13:00-17:00)" : "Morning (8:00-12:00)";

        String message = "Cannot request half-day: Same period already requested";
        String details = String.format(
            "You already have a %s half-day %s leave on %s (Status: %s).\n\n" +
            "You cannot request another half-day leave for the same period.\n" +
            "However, you can request a half-day leave for the %s on the same date.",
            periodName,
            leaveType,
            date,
            status.equals("PENDING") ? "Pending Approval" : "Approved",
            otherPeriod
        );

        return new ValidationErrorMessage("HALF_DAY_SAME_PERIOD_CONFLICT", message, details);
    }

    /**
     * Create insufficient balance error for half-day leave
     * Requirements: 3.5, 3.6
     *
     * @param leaveTypeName Name of leave type
     * @param availableDays Available balance
     * @param requestedDays Requested days (0.5 for half-day)
     * @param usedDays Days already used
     * @param totalAllowed Total days allowed
     * @return ValidationErrorMessage
     */
    public static ValidationErrorMessage insufficientBalanceHalfDayError(
            String leaveTypeName,
            double availableDays,
            double requestedDays,
            double usedDays,
            double totalAllowed) {

        String message = "Insufficient leave balance";
        String details;

        if (availableDays == 0) {
            details = String.format(
                "Leave Type: %s\n" +
                "• Total Allowed: %.1f days\n" +
                "• Already Used: %.1f days\n" +
                "• Available: %.1f days\n" +
                "• You are requesting: %.1f days\n\n" +
                "You have no remaining balance for this leave type.\n" +
                "Please choose a different leave type or wait for the next allocation period.",
                leaveTypeName,
                totalAllowed,
                usedDays,
                availableDays,
                requestedDays
            );
        } else if (availableDays < requestedDays) {
            // Suggest half-day if only 0.5 days available and requesting full day
            if (availableDays == 0.5 && requestedDays == 1.0) {
                details = String.format(
                    "Leave Type: %s\n" +
                    "• Total Allowed: %.1f days\n" +
                    "• Already Used: %.1f days\n" +
                    "• Available: %.1f days\n" +
                    "• You are requesting: %.1f days\n\n" +
                    "💡 Suggestion: You only have 0.5 days remaining. Consider requesting a half-day leave instead of a full day.",
                    leaveTypeName,
                    totalAllowed,
                    usedDays,
                    availableDays,
                    requestedDays
                );
            } else {
                details = String.format(
                    "Leave Type: %s\n" +
                    "• Total Allowed: %.1f days\n" +
                    "• Already Used: %.1f days\n" +
                    "• Available: %.1f days\n" +
                    "• You are requesting: %.1f days\n\n" +
                    "Please reduce the number of days or choose a different leave type.",
                    leaveTypeName,
                    totalAllowed,
                    usedDays,
                    availableDays,
                    requestedDays
                );
            }
        } else {
            details = String.format(
                "Leave Type: %s\n" +
                "• Total Allowed: %.1f days\n" +
                "• Already Used: %.1f days\n" +
                "• Available: %.1f days\n" +
                "• You are requesting: %.1f days",
                leaveTypeName,
                totalAllowed,
                usedDays,
                availableDays,
                requestedDays
            );
        }

        return new ValidationErrorMessage("INSUFFICIENT_BALANCE", message, details);
    }

    /**
     * Create invalid half-day period error message
     * Requirements: 2.1, 2.2, 2.3
     *
     * @param providedPeriod The invalid period value provided
     * @return ValidationErrorMessage
     */
    public static ValidationErrorMessage invalidHalfDayPeriodError(String providedPeriod) {
        String message = "Invalid half-day period";
        String details;

        if (providedPeriod == null || providedPeriod.trim().isEmpty()) {
            details = "Half-day period must be specified when requesting half-day leave.\n\n" +
                     "Valid options:\n" +
                     "• AM - Morning (8:00-12:00)\n" +
                     "• PM - Afternoon (13:00-17:00)\n\n" +
                     "Please select a period for your half-day leave.";
        } else {
            details = String.format(
                "The period '%s' is not valid.\n\n" +
                "Valid options:\n" +
                "• AM - Morning (8:00-12:00)\n" +
                "• PM - Afternoon (13:00-17:00)\n\n" +
                "Please select either AM or PM.",
                providedPeriod
            );
        }

        return new ValidationErrorMessage("INVALID_HALF_DAY_PERIOD", message, details);
    }

    /**
     * Create generic validation error message
     *
     * @param message Error message
     * @return ValidationErrorMessage
     */
    public static ValidationErrorMessage genericError(String message) {
        return new ValidationErrorMessage("VALIDATION_ERROR", message, message);
    }

    /**
     * Get error type
     */
    public String getType() {
        return type;
    }

    /**
     * Get error message (short summary)
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get error details (full explanation)
     */
    public String getDetails() {
        return details;
    }

    /**
     * Get formatted error for display
     */
    public String getFormattedError() {
        return message + "\n\n" + details;
    }

    /**
     * Get HTML formatted error for JSP display
     */
    public String getHtmlFormattedError() {
        return "<strong>" + escapeHtml(message) + "</strong><br><br>" +
               escapeHtml(details).replace("\n", "<br>");
    }

    /**
     * Simple HTML escape utility
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
