package group4.hrms.service;

import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * Integration service for triggering payslip dirty flag updates from other services
 * This service provides a simple interface for other services to notify when data changes
 * that affect payslip calculations occur.
 *

 */
public class PayslipIntegrationService {

    private static final Logger logger = Logger.getLogger(PayslipIntegrationService.class.getName());

    // Singleton instance for easy access from other services
    private static PayslipIntegrationService instance;

    private final PayslipGenerationService payslipGenerationService;

    // Private constructor for singleton
    private PayslipIntegrationService() {
        this.payslipGenerationService = new PayslipGenerationService();
    }

    // Get singleton instance
    public static PayslipIntegrationService getInstance() {
        if (instance == null) {
            synchronized (PayslipIntegrationService.class) {
                if (instance == null) {
                    instance = new PayslipIntegrationService();
                }
            }
        }
        return instance;
    }

    /**
     * Notify that attendance data has changed for a user
     * This should be called from AttendanceService when attendance records are updated
     *
     * @param userId User ID whose attendance changed
     * @param attendanceDate Date of attendance change
     * @param changeDescription Description of what changed
     */
    public void notifyAttendanceChange(Long userId, LocalDate attendanceDate, String changeDescription) {
        logger.info(String.format("Attendance change notification: userId=%d, date=%s, change=%s",
                                userId, attendanceDate, changeDescription));

        try {
            // TODO: Implement markDirtyForAttendanceChange in PayslipGenerationService
            // payslipGenerationService.markDirtyForAttendanceChange(userId, attendanceDate, changeDescription);
            logger.info(String.format("Attendance change notification: userId=%d, date=%s, description=%s",
                                     userId, attendanceDate, changeDescription));
        } catch (Exception e) {
            logger.severe(String.format("Error processing attendance change notification: userId=%d, date=%s, error=%s",
                                       userId, attendanceDate, e.getMessage()));
        }
    }

    /**
     * Notify that an overtime request has been approved or modified
     * This should be called from OTRequestService when OT requests are approved/modified
     *
     * @param userId User ID whose OT request changed
     * @param otDate Date of overtime
     * @param changeDescription Description of what changed
     */
    public void notifyOvertimeChange(Long userId, LocalDate otDate, String changeDescription) {
        logger.info(String.format("Overtime change notification: userId=%d, date=%s, change=%s",
                                userId, otDate, changeDescription));

        try {
            // TODO: Implement markDirtyForOvertimeChange in PayslipGenerationService
            // payslipGenerationService.markDirtyForOvertimeChange(userId, otDate, changeDescription);
            logger.info(String.format("Overtime change processed: userId=%d, date=%s, description=%s",
                                     userId, otDate, changeDescription));
        } catch (Exception e) {
            logger.severe(String.format("Error processing overtime change notification: userId=%d, date=%s, error=%s",
                                       userId, otDate, e.getMessage()));
        }
    }

    /**
     * Notify that a leave request has been approved or modified
     * This should be called from LeaveRequestService when leave requests are approved/modified
     *
     * @param userId User ID whose leave request changed
     * @param leaveStartDate Start date of leave
     * @param leaveEndDate End date of leave
     * @param changeDescription Description of what changed
     */
    public void notifyLeaveChange(Long userId, LocalDate leaveStartDate, LocalDate leaveEndDate, String changeDescription) {
        logger.info(String.format("Leave change notification: userId=%d, dates=%s to %s, change=%s",
                                userId, leaveStartDate, leaveEndDate, changeDescription));

        try {
            // TODO: Implement markDirtyForLeaveChange in PayslipGenerationService
            // payslipGenerationService.markDirtyForLeaveChange(userId, leaveStartDate, leaveEndDate, changeDescription);
            logger.info(String.format("Leave change processed: userId=%d, dates=%s to %s, description=%s",
                                     userId, leaveStartDate, leaveEndDate, changeDescription));
        } catch (Exception e) {
            logger.severe(String.format("Error processing leave change notification: userId=%d, dates=%s to %s, error=%s",
                                       userId, leaveStartDate, leaveEndDate, e.getMessage()));
        }
    }

    /**
     * Notify that salary or contract data has changed for a user
     * This should be called from SalaryHistoryService when salary changes are made
     *
     * @param userId User ID whose salary changed
     * @param effectiveDate Effective date of salary change
     * @param changeDescription Description of what changed
     */
    public void notifySalaryChange(Long userId, LocalDate effectiveDate, String changeDescription) {
        logger.info(String.format("Salary change notification: userId=%d, effectiveDate=%s, change=%s",
                                userId, effectiveDate, changeDescription));

        try {
            // TODO: Implement markDirtyForSalaryChange in PayslipGenerationService
            // payslipGenerationService.markDirtyForSalaryChange(userId, effectiveDate, changeDescription);
            logger.info(String.format("Salary change processed: userId=%d, effectiveDate=%s, description=%s",
                                     userId, effectiveDate, changeDescription));
        } catch (Exception e) {
            logger.severe(String.format("Error processing salary change notification: userId=%d, effectiveDate=%s, error=%s",
                                       userId, effectiveDate, e.getMessage()));
        }
    }

    /**
     * Convenience method for request approval notifications
     * This can be called from request approval workflows
     *
     * @param requestType Type of request (OT, LEAVE, etc.)
     * @param userId User ID
     * @param requestStartDate Start date of request
     * @param requestEndDate End date of request (can be same as start for single-day requests)
     * @param approvalAction Action taken (APPROVED, REJECTED, MODIFIED)
     */
    public void notifyRequestApproval(String requestType, Long userId, LocalDate requestStartDate,
                                    LocalDate requestEndDate, String approvalAction) {

        if (!"APPROVED".equals(approvalAction)) {
            // Only process approved requests that affect payslips
            return;
        }

        String changeDescription = String.format("%s request %s", requestType, approvalAction.toLowerCase());

        switch (requestType.toUpperCase()) {
            case "OT":
            case "OVERTIME":
                notifyOvertimeChange(userId, requestStartDate, changeDescription);
                break;
            case "LEAVE":
            case "ANNUAL_LEAVE":
            case "SICK_LEAVE":
            case "PERSONAL_LEAVE":
                notifyLeaveChange(userId, requestStartDate, requestEndDate, changeDescription);
                break;
            default:
                logger.info(String.format("Unknown request type for payslip notification: %s", requestType));
        }
    }

    /**
     * Batch notification for multiple attendance changes (e.g., from Excel import)
     *
     * @param userId User ID
     * @param attendanceDates List of dates with attendance changes
     * @param changeDescription Description of the batch change
     */
    public void notifyBatchAttendanceChange(Long userId, java.util.List<LocalDate> attendanceDates, String changeDescription) {
        logger.info(String.format("Batch attendance change notification: userId=%d, dates=%d, change=%s",
                                userId, attendanceDates.size(), changeDescription));

        for (LocalDate date : attendanceDates) {
            notifyAttendanceChange(userId, date, changeDescription);
        }
    }
}