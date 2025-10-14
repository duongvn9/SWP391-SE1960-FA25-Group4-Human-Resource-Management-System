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

    /**
     * Create overlap error message
     *
     * @param existingLeaveType Leave type of overlapping request
     * @param status Status of overlapping request (PENDING/APPROVED)
     * @param startDate Start date of overlapping request
     * @param endDate End date of overlapping request
     * @return ValidationErrorMessage
     */
    public static ValidationErrorMessage overlapError(
            String existingLeaveType,
            String status,
            String startDate,
            String endDate) {

        String message = "Đơn nghỉ phép trùng với đơn khác đã tồn tại";
        String details = String.format(
            "Bạn đã có đơn %s (%s) từ %s đến %s. " +
            "Vui lòng chọn khoảng thời gian khác hoặc hủy đơn cũ trước khi tạo đơn mới.",
            existingLeaveType,
            status.equals("PENDING") ? "đang chờ duyệt" : "đã được duyệt",
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

        String message = "Không đủ số ngày nghỉ phép";
        String details = String.format(
            "Loại nghỉ phép: %s\n" +
            "• Tổng số ngày được phép: %d ngày\n" +
            "• Đã sử dụng: %d ngày\n" +
            "• Còn lại: %d ngày\n" +
            "• Bạn đang xin: %d ngày\n\n" +
            "Vui lòng giảm số ngày xin nghỉ hoặc chọn loại nghỉ phép khác.",
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

        String message = "Xung đột với đơn làm thêm giờ đã được duyệt";
        String details = String.format(
            "Bạn đã có đơn OT được duyệt vào ngày %s:\n" +
            "• Thời gian: %s - %s\n" +
            "• Số giờ: %.1f giờ\n\n" +
            "Không thể xin nghỉ phép trong ngày đã có đơn OT được duyệt. " +
            "Vui lòng chọn ngày khác hoặc hủy đơn OT trước.",
            otDate,
            startTime,
            endTime,
            otHours
        );

        return new ValidationErrorMessage("OT_CONFLICT", message, details);
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
