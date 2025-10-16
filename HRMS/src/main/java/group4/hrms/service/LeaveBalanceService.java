package group4.hrms.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.RequestDao;
import group4.hrms.dao.LeaveTypeDao;
import group4.hrms.dto.LeaveRequestDetail;
import group4.hrms.model.Request;
import group4.hrms.model.LeaveType;

/**
 * LeaveBalanceService - Handles leave balance calculations with half-day support
 *
 * This service provides methods to calculate leave balances, check sufficient balance,
 * and handle both full-day and half-day leave calculations.
 */
public class LeaveBalanceService {
    private static final Logger logger = Logger.getLogger(LeaveBalanceService.class.getName());

    private final RequestDao requestDao;
    private final LeaveTypeDao leaveTypeDao;

    public LeaveBalanceService(RequestDao requestDao, LeaveTypeDao leaveTypeDao) {
        this.requestDao = requestDao;
        this.leaveTypeDao = leaveTypeDao;
    }

    /**
     * Calculate used days for user, leave type and year
     * Supports both full-day (1.0) and half-day (0.5) leave calculations
     *
     * Requirements: 3.3, 3.4
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param year Year to calculate for
     * @return Total used days as double with decimal precision
     */
    public double calculateUsedDays(Long userId, String leaveTypeCode, int year) {
        logger.fine(String.format("Calculating used days: userId=%d, leaveType=%s, year=%d",
                   userId, leaveTypeCode, year));

        try {
            // Get all requests for user
            List<Request> requests = requestDao.findByUserId(userId);

            double totalUsedDays = 0.0;
            int approvedCount = 0;

            for (Request request : requests) {
                // Only count APPROVED requests
                if (!"APPROVED".equals(request.getStatus())) {
                    continue;
                }

                // Check if request has leave detail
                LeaveRequestDetail detail = request.getLeaveDetail();
                if (detail == null || !leaveTypeCode.equals(detail.getLeaveTypeCode())) {
                    continue;
                }

                // Check if request's start date is in the specified year
                String startDateStr = detail.getStartDate();
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    try {
                        LocalDateTime startDate = LocalDateTime.parse(startDateStr);
                        if (startDate.getYear() == year) {
                            // Sum full-day leaves as 1.0 days each
                            // Sum half-day leaves as 0.5 days each
                            if (detail.getIsHalfDay() != null && detail.getIsHalfDay()) {
                                totalUsedDays += 0.5;
                            } else {
                                totalUsedDays += detail.getDayCount();
                            }
                            approvedCount++;
                        }
                    } catch (Exception e) {
                        logger.warning(String.format("Failed to parse startDate for request %d: %s. Falling back to createdAt",
                                      request.getId(), startDateStr));
                        // If parsing fails, fallback to createdAt
                        if (request.getCreatedAt() != null &&
                            request.getCreatedAt().getYear() == year) {
                            if (detail.getIsHalfDay() != null && detail.getIsHalfDay()) {
                                totalUsedDays += 0.5;
                            } else {
                                totalUsedDays += detail.getDayCount();
                            }
                            approvedCount++;
                        }
                    }
                } else if (request.getCreatedAt() != null &&
                           request.getCreatedAt().getYear() == year) {
                    // Fallback to createdAt if startDate is not available
                    if (detail.getIsHalfDay() != null && detail.getIsHalfDay()) {
                        totalUsedDays += 0.5;
                    } else {
                        totalUsedDays += detail.getDayCount();
                    }
                    approvedCount++;
                }
            }

            logger.info(String.format("Calculated used days: userId=%d, leaveType=%s, year=%d, usedDays=%.1f, approvedRequests=%d",
                       userId, leaveTypeCode, year, totalUsedDays, approvedCount));
            return totalUsedDays;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating used days: userId=%d, leaveType=%s, year=%d",
                      userId, leaveTypeCode, year), e);
            return 0.0;
        }
    }

    /**
     * Check if user has sufficient balance for requested leave
     *
     * Requirements: 3.5, 3.6, 3.7
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param requestedDays Number of days requested (0.5 for half-day, 1.0+ for full-day)
     * @param year Year to check balance for
     * @return true if sufficient balance, false otherwise
     */
    public boolean hasSufficientBalance(Long userId, String leaveTypeCode, double requestedDays, int year) {
        logger.fine(String.format("Checking sufficient balance: userId=%d, leaveType=%s, requestedDays=%.1f, year=%d",
                   userId, leaveTypeCode, requestedDays, year));

        try {
            // Get leave type
            LeaveType leaveType = leaveTypeDao.findByCode(leaveTypeCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type: " + leaveTypeCode));

            // Always return true for unpaid leave type (no balance check)
            if (!leaveType.isPaid()) {
                logger.info(String.format("Unpaid leave type, skipping balance check: userId=%d, leaveType=%s",
                           userId, leaveTypeCode));
                return true;
            }

            // Get default days for leave type
            Integer defaultDays = leaveType.getDefaultDays();
            if (defaultDays == null || defaultDays <= 0) {
                // Unlimited leave type
                logger.info(String.format("Unlimited leave type, skipping balance check: userId=%d, leaveType=%s",
                           userId, leaveTypeCode));
                return true;
            }

            // Calculate seniority bonus (simplified - can be enhanced later)
            int seniorityBonus = 0; // TODO: Calculate based on user's join date

            // Calculate total allowed days
            double totalAllowed = defaultDays + seniorityBonus;

            // Calculate used days from APPROVED requests in the year
            double usedDays = calculateUsedDays(userId, leaveTypeCode, year);

            // Calculate remaining days
            double remainingDays = totalAllowed - usedDays;

            logger.fine(String.format("Balance check: userId=%d, leaveType=%s, total=%.1f, used=%.1f, remaining=%.1f, requested=%.1f",
                       userId, leaveTypeCode, totalAllowed, usedDays, remainingDays, requestedDays));

            // Check balance >= 0.5 for half-day requests
            // Check balance >= 1.0 for full-day requests
            boolean hasSufficient = remainingDays >= requestedDays;

            if (!hasSufficient) {
                logger.warning(String.format("Insufficient balance: userId=%d, leaveType=%s, requested=%.1f, remaining=%.1f",
                              userId, leaveTypeCode, requestedDays, remainingDays));
            } else {
                logger.info(String.format("Sufficient balance: userId=%d, leaveType=%s, requested=%.1f, remaining=%.1f",
                           userId, leaveTypeCode, requestedDays, remainingDays));
            }

            return hasSufficient;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, String.format("Error checking sufficient balance: userId=%d, leaveType=%s",
                      userId, leaveTypeCode), e);
            return false;
        }
    }

    /**
     * Get available balance for user and leave type
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param year Year to check balance for
     * @return Available balance as double, or -1 if unlimited/unpaid
     */
    public double getAvailableBalance(Long userId, String leaveTypeCode, int year) {
        logger.fine(String.format("Getting available balance: userId=%d, leaveType=%s, year=%d",
                   userId, leaveTypeCode, year));

        try {
            // Get leave type
            LeaveType leaveType = leaveTypeDao.findByCode(leaveTypeCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type: " + leaveTypeCode));

            // Return -1 for unpaid leave type (no balance tracking)
            if (!leaveType.isPaid()) {
                return -1.0;
            }

            // Get default days for leave type
            Integer defaultDays = leaveType.getDefaultDays();
            if (defaultDays == null || defaultDays <= 0) {
                // Unlimited leave type
                return -1.0;
            }

            // Calculate seniority bonus (simplified - can be enhanced later)
            int seniorityBonus = 0; // TODO: Calculate based on user's join date

            // Calculate total allowed days
            double totalAllowed = defaultDays + seniorityBonus;

            // Calculate used days from APPROVED requests in the year
            double usedDays = calculateUsedDays(userId, leaveTypeCode, year);

            // Calculate remaining days
            double remainingDays = totalAllowed - usedDays;

            logger.info(String.format("Available balance: userId=%d, leaveType=%s, year=%d, available=%.1f",
                       userId, leaveTypeCode, year, remainingDays));

            return remainingDays;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, String.format("Error getting available balance: userId=%d, leaveType=%s",
                      userId, leaveTypeCode), e);
            return 0.0;
        }
    }

    /**
     * Get leave balance display information for UI
     * Handles both paid and unpaid leave types
     *
     * Requirements: 3.4, 6.10, 6.11
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param year Year to get balance for
     * @return LeaveBalanceDisplay object with formatted information
     */
    public LeaveBalanceDisplay getLeaveBalanceDisplay(Long userId, String leaveTypeCode, int year) {
        logger.fine(String.format("Getting leave balance display: userId=%d, leaveType=%s, year=%d",
                   userId, leaveTypeCode, year));

        try {
            // Get leave type
            LeaveType leaveType = leaveTypeDao.findByCode(leaveTypeCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type: " + leaveTypeCode));

            LeaveBalanceDisplay display = new LeaveBalanceDisplay();
            display.leaveTypeCode = leaveTypeCode;
            display.leaveTypeName = leaveType.getName();
            display.isPaid = leaveType.isPaid();

            // Calculate used days (works for both paid and unpaid)
            double usedDays = calculateUsedDays(userId, leaveTypeCode, year);
            display.usedDays = usedDays;

            if (!leaveType.isPaid()) {
                // For unpaid leave: Show "days taken" (no balance)
                display.showBalance = false;
                display.displayText = String.format("%.1f days taken this year", usedDays);
                display.totalDays = 0.0;
                display.remainingDays = 0.0;
            } else {
                // For paid leave: Show decimal values (e.g., "5.5 days")
                Integer defaultDays = leaveType.getDefaultDays();
                if (defaultDays == null || defaultDays <= 0) {
                    // Unlimited leave type
                    display.showBalance = false;
                    display.displayText = String.format("%.1f days used (Unlimited)", usedDays);
                    display.totalDays = 0.0;
                    display.remainingDays = 0.0;
                } else {
                    // Calculate seniority bonus (simplified)
                    int seniorityBonus = 0; // TODO: Calculate based on user's join date
                    double totalAllowed = defaultDays + seniorityBonus;
                    double remainingDays = totalAllowed - usedDays;

                    display.showBalance = true;
                    display.totalDays = totalAllowed;
                    display.remainingDays = remainingDays;
                    display.displayText = String.format("%.1f days remaining", remainingDays);
                }
            }

            logger.info(String.format("Leave balance display: userId=%d, leaveType=%s, showBalance=%b, text=%s",
                       userId, leaveTypeCode, display.showBalance, display.displayText));

            return display;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, String.format("Error getting leave balance display: userId=%d, leaveType=%s",
                      userId, leaveTypeCode), e);
            return null;
        }
    }

    /**
     * Data class for leave balance display information
     * Used by UI to show balance with proper formatting
     */
    public static class LeaveBalanceDisplay {
        public String leaveTypeCode;
        public String leaveTypeName;
        public boolean isPaid;
        public boolean showBalance;      // Hide balance card for unpaid leave type
        public double totalDays;
        public double usedDays;
        public double remainingDays;
        public String displayText;       // Formatted display text

        // Getters for JSP EL
        public String getLeaveTypeCode() { return leaveTypeCode; }
        public String getLeaveTypeName() { return leaveTypeName; }
        public boolean isPaid() { return isPaid; }
        public boolean isShowBalance() { return showBalance; }
        public double getTotalDays() { return totalDays; }
        public double getUsedDays() { return usedDays; }
        public double getRemainingDays() { return remainingDays; }
        public String getDisplayText() { return displayText; }
    }
}
