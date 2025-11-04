package group4.hrms.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.PayslipDao;

/**
 * Service for managing payslip dirty flag operations
 * Handles automatic dirty marking when source data changes
 * Requirements: 8.1, 8.2, 8.3, 8.4, 8.6, 8.7
 */
public class PayslipDirtyFlagService {

    private static final Logger logger = Logger.getLogger(PayslipDirtyFlagService.class.getName());

    private final PayslipDao payslipDao;

    public PayslipDirtyFlagService() {
        this.payslipDao = new PayslipDao();
    }

    /**
     * Mark payslip as dirty when attendance data changes
     * Requirements: 8.1
     *
     * @param userId User ID whose attendance changed
     * @param changeDate Date of the attendance change
     * @param reason Specific reason for the change
     */
    public void markDirtyForAttendanceChange(Long userId, LocalDate changeDate, String reason) {
        logger.info(String.format("Processing attendance change for user %d on %s: %s",
                                userId, changeDate, reason));

        try {
            // Find all payslip periods that include this change date
            List<LocalDate[]> affectedPeriods = findAffectedPayslipPeriods(changeDate);

            for (LocalDate[] period : affectedPeriods) {
                LocalDate periodStart = period[0];
                LocalDate periodEnd = period[1];

                String dirtyReason = String.format("Attendance changed on %s: %s", changeDate, reason);
                markPayslipDirty(userId, periodStart, periodEnd, dirtyReason);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                     String.format("Error marking payslip dirty for attendance change: userId=%d, date=%s",
                                 userId, changeDate), e);
            // Don't throw exception to avoid breaking the main attendance update operation
        }
    }

    /**
     * Mark payslip as dirty when overtime data changes
     * Requirements: 8.2
     *
     * @param userId User ID whose overtime changed
     * @param otDate Date of the overtime
     * @param reason Specific reason for the change
     */
    public void markDirtyForOvertimeChange(Long userId, LocalDate otDate, String reason) {
        logger.info(String.format("Processing overtime change for user %d on %s: %s",
                                userId, otDate, reason));

        try {
            // Find all payslip periods that include this OT date
            List<LocalDate[]> affectedPeriods = findAffectedPayslipPeriods(otDate);

            for (LocalDate[] period : affectedPeriods) {
                LocalDate periodStart = period[0];
                LocalDate periodEnd = period[1];

                String dirtyReason = String.format("Overtime changed on %s: %s", otDate, reason);
                markPayslipDirty(userId, periodStart, periodEnd, dirtyReason);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                     String.format("Error marking payslip dirty for overtime change: userId=%d, date=%s",
                                 userId, otDate), e);
        }
    }

    /**
     * Mark payslip as dirty when leave data changes
     * Requirements: 8.2
     *
     * @param userId User ID whose leave changed
     * @param leaveStartDate Start date of the leave
     * @param leaveEndDate End date of the leave
     * @param reason Specific reason for the change
     */
    public void markDirtyForLeaveChange(Long userId, LocalDate leaveStartDate, LocalDate leaveEndDate, String reason) {
        logger.info(String.format("Processing leave change for user %d from %s to %s: %s",
                                userId, leaveStartDate, leaveEndDate, reason));

        try {
            // Find all payslip periods that overlap with the leave period
            List<LocalDate[]> affectedPeriods = findAffectedPayslipPeriodsForRange(leaveStartDate, leaveEndDate);

            for (LocalDate[] period : affectedPeriods) {
                LocalDate periodStart = period[0];
                LocalDate periodEnd = period[1];

                String dirtyReason = String.format("Leave changed from %s to %ty %s",
                                                 leaveStartDate, leaveEndDate, reason);
                markPayslipDirty(userId, periodStart, periodEnd, dirtyReason);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                     String.format("Error marking payslip dirty for leave change: userId=%d, dates=%s to %s",
                                 userId, leaveStartDate, leaveEndDate), e);
        }
    }

    /**
     * Mark payslip as dirty when salary or contract data changes
     * Requirements: 8.3
     *
     * @param userId User ID whose salary changed
     * @param effectiveDate Effective date of the salary change
     * @param reason Specific reason for the change
     */
    public void markDirtyForSalaryChange(Long userId, LocalDate effectiveDate, String reason) {
        logger.info(String.format("Processing salary change for user %d effective %s: %s",
                                userId, effectiveDate, reason));

        try {
            // Find all payslip periods that are affected by this salary change
            // This includes all periods from the effective date onwards
            List<LocalDate[]> affectedPeriods = findAffectedPayslipPeriodsFromDate(effectiveDate);

            for (LocalDate[] period : affectedPeriods) {
                LocalDate periodStart = period[0];
                LocalDate periodEnd = period[1];

                String dirtyReason = String.format("Salary changed effective %s: %s", effectiveDate, reason);
                markPayslipDirty(userId, periodStart, periodEnd, dirtyReason);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                     String.format("Error marking payslip dirty for salary change: userId=%d, effectiveDate=%s",
                                 userId, effectiveDate), e);
        }
    }

    /**
     * Bulk mark multiple users' payslips as dirty
     * Requirements: 8.1, 8.2, 8.3
     *
     * @param userIds List of user IDs
     * @param periodStart Period start date
     * @param periodEnd Period end date
     * @param reason Reason for marking dirty
     */
    public void bulkMarkDirty(List<Long> userIds, LocalDate periodStart, LocalDate periodEnd, String reason) {
        logger.info(String.format("Bulk marking %d users' payslips as dirty for period %s to %s: %s",
                                userIds.size(), periodStart, periodEnd, reason));

        try {
            int markedCount = payslipDao.bulkMarkDirtyByUserIds(userIds, periodStart, periodEnd, reason);
            logger.info(String.format("Successfully marked %d payslips as dirty", markedCount));

        } catch (SQLException e) {
            logger.log(Level.SEVERE,
                     String.format("Error bulk marking payslips dirty for %d users", userIds.size()), e);
            throw new RuntimeException("Failed to bulk mark payslips dirty", e);
        }
    }

    /**
     * Reset dirty flag after successful regeneration
     * Requirements: 8.6
     *
     * @param payslipIds List of payslip IDs to reset
     */
    public void resetDirtyFlags(List<Long> payslipIds) {
        logger.info(String.format("Resetting dirty flags for %d payslips", payslipIds.size()));

        try {
            int resetCount = payslipDao.bulkResetDirtyFlags(payslipIds);
            logger.info(String.format("Successfully reset dirty flags for %d payslips", resetCount));

        } catch (SQLException e) {
            logger.log(Level.SEVERE,
                     String.format("Error resetting dirty flags for %d payslips", payslipIds.size()), e);
            throw new RuntimeException("Failed to reset dirty flags", e);
        }
    }

    /**
     * Update generated timestamp and reset dirty flag for successful generation
     * Requirements: 8.6, 8.7
     *
     * @param payslipIds List of payslip IDs
     * @param generatedAt Generation timestamp
     */
    public void markAsGenerated(List<Long> payslipIds, LocalDateTime generatedAt) {
        logger.info(String.format("Marking %d payslips as generated at %s", payslipIds.size(), generatedAt));

        try {
            int updatedCount = payslipDao.bulkUpdateGeneratedTimestamp(payslipIds, generatedAt);
            logger.info(String.format("Successfully marked %d payslips as generated", updatedCount));

        } catch (SQLException e) {
            logger.log(Level.SEVERE,
                     String.format("Error marking %d payslips as generated", payslipIds.size()), e);
            throw new RuntimeException("Failed to mark payslips as generated", e);
        }
    }

    /**
     * Check if payslip needs regeneration based on dirty flag and other criteria
     * Requirements: 8.4, 8.5
     *
     * @param userId User ID
     * @param periodStart Period start date
     * @param periodEnd Period end date
     * @return true if regeneration is needed
     */
    public boolean needsRegeneration(Long userId, LocalDate periodStart, LocalDate periodEnd) {
        try {
            return payslipDao.findByUserAndPeriod(userId, periodStart, periodEnd)
                    .map(payslip -> Boolean.TRUE.equals(payslip.getIsDirty()) || payslip.getGeneratedAt() == null)
                    .orElse(false); // No payslip exists, so no regeneration needed (generation needed instead)

        } catch (SQLException e) {
            logger.log(Level.WARNING,
                     String.format("Error checking regeneration need for user %d", userId), e);
            return false;
        }
    }

    // Private helper methods

    private void markPayslipDirty(Long userId, LocalDate periodStart, LocalDate periodEnd, String reason) {
        try {
            boolean marked = payslipDao.markDirty(userId, periodStart, periodEnd, reason);
            if (!marked) {
                logger.fine(String.format("No payslip found to mark dirty for user %d in period %s to %s",
                                        userId, periodStart, periodEnd));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,
                     String.format("Error marking payslip dirty: userId=%d, period=%s to %s",
                                 userId, periodStart, periodEnd), e);
            throw new RuntimeException("Failed to mark payslip dirty", e);
        }
    }

    /**
     * Find payslip periods that include a specific date
     * This is a simplified implementation - in reality would query existing payslip periods
     */
    private List<LocalDate[]> findAffectedPayslipPeriods(LocalDate changeDate) {
        // Simplified: assume monthly periods
        // In reality, this would query the database for existing payslip periods that include this date
        LocalDate periodStart = changeDate.withDayOfMonth(1);
        LocalDate periodEnd = changeDate.withDayOfMonth(changeDate.lengthOfMonth());

        List<LocalDate[]> periods = new ArrayList<>();
        periods.add(new LocalDate[]{periodStart, periodEnd});
        return periods;
    }

    /**
     * Find payslip periods that overlap with a date range
     */
    private List<LocalDate[]> findAffectedPayslipPeriodsForRange(LocalDate startDate, LocalDate endDate) {
        // Simplified: find all monthly periods that overlap with the range
        List<LocalDate[]> periods = new java.util.ArrayList<>();

        LocalDate current = startDate.withDayOfMonth(1);
        LocalDate rangeEnd = endDate.withDayOfMonth(endDate.lengthOfMonth());

        while (!current.isAfter(rangeEnd)) {
            LocalDate periodEnd = current.withDayOfMonth(current.lengthOfMonth());
            periods.add(new LocalDate[]{current, periodEnd});
            current = current.plusMonths(1);
        }

        return periods;
    }

    /**
     * Find payslip periods from a specific date onwards
     */
    private List<LocalDate[]> findAffectedPayslipPeriodsFromDate(LocalDate fromDate) {
        // Simplified: find periods from the given date up to current date
        List<LocalDate[]> periods = new java.util.ArrayList<>();

        LocalDate current = fromDate.withDayOfMonth(1);
        LocalDate today = LocalDate.now();
        LocalDate currentMonth = today.withDayOfMonth(1);

        while (!current.isAfter(currentMonth)) {
            LocalDate periodEnd = current.withDayOfMonth(current.lengthOfMonth());
            periods.add(new LocalDate[]{current, periodEnd});
            current = current.plusMonths(1);
        }

        return periods;
    }
}