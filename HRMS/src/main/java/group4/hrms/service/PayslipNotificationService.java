package group4.hrms.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Service for sending notifications related to payslip changes
 * Notifies employees when their payslips are being recalculated
 */
public class PayslipNotificationService {

    private static final Logger logger = Logger.getLogger(PayslipNotificationService.class.getName());
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    /**
     * Notify employee that their payslip is being recalculated
     *
     * @param userId User ID
     * @param periodStart Period start date
     * @param periodEnd Period end date
     * @param reason Reason for recalculation
     */
    public void notifyPayslipRecalculation(Long userId, LocalDate periodStart, LocalDate periodEnd, String reason) {
        String periodName = periodStart.format(MONTH_FORMATTER);

        String message = String.format(
            "Your payslip for %s is being recalculated due to %s. " +
            "The updated payslip will be available shortly.",
            periodName, reason
        );

        logger.info(String.format("📧 NOTIFICATION: userId=%d | %s", userId, message));

        // TODO: Implement actual notification delivery
        // Options:
        // 1. In-app notification (save to notifications table)
        // 2. Email notification
        // 3. SMS notification

        // Example for future implementation:
        // notificationDao.create(new Notification(userId, "PAYSLIP_RECALCULATION", message));
    }

    /**
     * Notify multiple employees about payslip recalculation (bulk)
     *
     * @param userIds Set of user IDs
     * @param periodStart Period start date
     * @param periodEnd Period end date
     * @param reason Reason for recalculation
     */
    public void notifyBulkPayslipRecalculation(Set<Long> userIds, LocalDate periodStart, LocalDate periodEnd, String reason) {
        String periodName = periodStart.format(MONTH_FORMATTER);

        logger.info(String.format(
            "📧 BULK NOTIFICATION: Notifying %d employees about payslip recalculation for %s",
            userIds.size(), periodName
        ));

        int successCount = 0;
        int failCount = 0;

        for (Long userId : userIds) {
            try {
                notifyPayslipRecalculation(userId, periodStart, periodEnd, reason);
                successCount++;
            } catch (Exception e) {
                failCount++;
                logger.warning(String.format(
                    "❌ Failed to notify user %d: %s", userId, e.getMessage()
                ));
            }
        }

        logger.info(String.format(
            "✅ Bulk notification completed: %d sent, %d failed (total: %d)",
            successCount, failCount, userIds.size()
        ));
    }
}
