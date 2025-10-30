package group4.hrms.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.LeaveTypeDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.LeaveRequestDetail;
import group4.hrms.model.LeaveType;
import group4.hrms.model.Request;

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
    private final UserDao userDao;

    public LeaveBalanceService(RequestDao requestDao, LeaveTypeDao leaveTypeDao) {
        this.requestDao = requestDao;
        this.leaveTypeDao = leaveTypeDao;
        this.userDao = new UserDao();
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

            // Track half-day leaves per date to detect AM+PM combinations
            // Key: LocalDate, Value: Set of periods ("AM", "PM")
            java.util.Map<java.time.LocalDate, java.util.Set<String>> halfDaysByDate = new java.util.HashMap<>();

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
                            // Full-day leave: count as dayCount
                            if (detail.getIsHalfDay() == null || !detail.getIsHalfDay()) {
                                totalUsedDays += detail.getDayCount();
                                approvedCount++;
                            } else {
                                // Half-day leave: track by date and period
                                java.time.LocalDate leaveDate = startDate.toLocalDate();
                                String period = detail.getHalfDayPeriod();

                                if (period != null) {
                                    halfDaysByDate.computeIfAbsent(leaveDate, k -> new java.util.HashSet<>()).add(period);
                                    approvedCount++;
                                } else {
                                    // Period is null, count as 0.5 (fallback)
                                    totalUsedDays += 0.5;
                                    approvedCount++;
                                }
                            }
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

            // Process half-days: if date has both AM and PM → count as 1.0, otherwise 0.5 each
            for (java.util.Map.Entry<java.time.LocalDate, java.util.Set<String>> entry : halfDaysByDate.entrySet()) {
                java.time.LocalDate date = entry.getKey();
                java.util.Set<String> periods = entry.getValue();

                if (periods.contains("AM") && periods.contains("PM")) {
                    // Both AM and PM on same date → count as 1 full day
                    totalUsedDays += 1.0;
                    logger.fine(String.format("Date %s has both AM+PM half-days, counted as 1.0 day", date));
                } else {
                    // Only AM or only PM → count 0.5 for each
                    totalUsedDays += periods.size() * 0.5;
                    logger.fine(String.format("Date %s has %d half-day(s), counted as %.1f day(s)",
                               date, periods.size(), periods.size() * 0.5));
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

            // Calculate seniority bonus based on user's join date
            int seniorityBonus = calculateSeniorityBonus(userId, leaveTypeCode);

            // Calculate total allowed days
            double totalAllowed = defaultDays + seniorityBonus;

            // Calculate used days from APPROVED requests in the year
            double usedDays = calculateUsedDays(userId, leaveTypeCode, year);

            // Calculate pending days from PENDING requests in the year
            double pendingDays = calculatePendingDays(userId, leaveTypeCode, year);

            // Calculate remaining days and available days
            double remainingDays = totalAllowed - usedDays;
            double availableDays = remainingDays - pendingDays;

            logger.fine(String.format("Balance check: userId=%d, leaveType=%s, total=%.1f, used=%.1f, pending=%.1f, remaining=%.1f, available=%.1f, requested=%.1f",
                       userId, leaveTypeCode, totalAllowed, usedDays, pendingDays, remainingDays, availableDays, requestedDays));

            // Check available balance (remaining - pending) >= requested days
            // This prevents creating requests that would exceed balance when pending requests are approved
            boolean hasSufficient = availableDays >= requestedDays;

            if (!hasSufficient) {
                logger.warning(String.format("Insufficient available balance: userId=%d, leaveType=%s, requested=%.1f, available=%.1f (remaining=%.1f, pending=%.1f)",
                              userId, leaveTypeCode, requestedDays, availableDays, remainingDays, pendingDays));
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

            // Calculate seniority bonus based on user's join date
            int seniorityBonus = calculateSeniorityBonus(userId, leaveTypeCode);

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
                    // Calculate seniority bonus based on user's join date
                    int seniorityBonus = calculateSeniorityBonus(userId, leaveTypeCode);
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

    /**
     * Calculate seniority bonus based on user's join date and company policy
     *
     * Company Policy for Annual Leave:
     * - 0-4 years: 0 extra days
     * - 5+ years: +1 day for every 5 years of service
     *
     * Examples:
     * - 4 years: 0 bonus days
     * - 5 years: 1 bonus day
     * - 9 years: 1 bonus day
     * - 10 years: 2 bonus days
     * - 15 years: 3 bonus days
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code (only applies to ANNUAL)
     * @return Seniority bonus days
     */
    private int calculateSeniorityBonus(Long userId, String leaveTypeCode) {
        logger.fine(String.format("Calculating seniority bonus: userId=%d, leaveType=%s", userId, leaveTypeCode));

        try {
            // Only apply seniority bonus to Annual Leave
            if (!"ANNUAL".equals(leaveTypeCode)) {
                logger.fine(String.format("Seniority bonus not applicable for leave type: %s", leaveTypeCode));
                return 0;
            }

            // Get user information
            java.util.Optional<group4.hrms.model.User> userOpt = userDao.findById(userId);
            if (!userOpt.isPresent()) {
                logger.warning(String.format("User not found for seniority calculation: userId=%d", userId));
                return 0;
            }

            group4.hrms.model.User user = userOpt.get();
            java.time.LocalDate joinDate = user.getDateJoined();

            logger.info(String.format("DEBUG: User %d dateJoined=%s", userId, joinDate));

            // Fallback to startWorkDate if dateJoined is null
            if (joinDate == null) {
                joinDate = user.getStartWorkDate();
                logger.info(String.format("DEBUG: User %d using startWorkDate=%s", userId, joinDate));
            }

            if (joinDate == null) {
                logger.warning(String.format("No join date found for user: userId=%d", userId));
                return 0;
            }

            // Calculate years of service
            java.time.LocalDate currentDate = java.time.LocalDate.now();
            long yearsOfService = java.time.temporal.ChronoUnit.YEARS.between(joinDate, currentDate);

            logger.fine(String.format("User service calculation: userId=%d, joinDate=%s, yearsOfService=%d",
                       userId, joinDate, yearsOfService));

            // Apply company policy: 1 bonus day for every 5 years of service
            int seniorityBonus = 0;
            if (yearsOfService >= 5) {
                seniorityBonus = (int) (yearsOfService / 5); // Integer division: 5-9 years = 1 day, 10-14 years = 2 days, etc.
            }

            logger.info(String.format("Seniority bonus calculated: userId=%d, yearsOfService=%d, bonus=%d days",
                       userId, yearsOfService, seniorityBonus));

            return seniorityBonus;

        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Error calculating seniority bonus: userId=%d, leaveType=%s",
                      userId, leaveTypeCode), e);
            return 0; // Return 0 on error to be safe
        }
    }

    /**
     * Calculate pending days for user, leave type and year
     * Only counts PENDING requests to check available balance
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param year Year
     * @return Number of pending leave days (supports half-day: 0.5)
     */
    private double calculatePendingDays(Long userId, String leaveTypeCode, int year) {
        logger.fine(String.format("Calculating pending days: userId=%d, leaveType=%s, year=%d",
                   userId, leaveTypeCode, year));

        try {
            List<Request> allRequests = requestDao.findByUserId(userId);
            double totalPendingDays = 0.0;

            for (Request request : allRequests) {
                // Only count PENDING requests
                if (!"PENDING".equals(request.getStatus())) {
                    continue;
                }

                LeaveRequestDetail detail = request.getLeaveDetail();
                if (detail == null) {
                    continue;
                }

                // Only count matching leave type
                if (!leaveTypeCode.equals(detail.getLeaveTypeCode())) {
                    continue;
                }

                // Check if request is in the target year
                String startDateStr = detail.getStartDate();
                if (startDateStr == null || startDateStr.length() < 4) {
                    continue;
                }

                try {
                    java.time.LocalDateTime requestStartDate = java.time.LocalDateTime.parse(startDateStr);
                    if (requestStartDate.getYear() != year) {
                        continue;
                    }

                    // Add days to total (supports half-day)
                    if (detail.getIsHalfDay() != null && detail.getIsHalfDay()) {
                        totalPendingDays += 0.5;
                    } else {
                        int dayCount = detail.getDayCount() != null ? detail.getDayCount() : 1;
                        totalPendingDays += dayCount;
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, String.format("Error parsing date for request %d: %s",
                              request.getId(), startDateStr), e);
                    // Continue with other requests
                }
            }

            logger.info(String.format("Calculated pending days: userId=%d, leaveType=%s, year=%d, pendingDays=%.1f",
                       userId, leaveTypeCode, year, totalPendingDays));

            return totalPendingDays;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating pending days: userId=%d, leaveType=%s, year=%d",
                      userId, leaveTypeCode, year), e);
            return 0.0; // Return 0 on error to be safe
        }
    }
}
