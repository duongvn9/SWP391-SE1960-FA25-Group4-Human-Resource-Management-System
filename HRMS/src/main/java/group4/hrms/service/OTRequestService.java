package group4.hrms.service;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import group4.hrms.dao.HolidayDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.OTBalance;
import group4.hrms.dto.OTRequestDetail;
import group4.hrms.model.Holiday;
import group4.hrms.model.Request;
import group4.hrms.model.RequestType;
import group4.hrms.model.User;

public class OTRequestService {
    private static final Logger logger = Logger.getLogger(OTRequestService.class.getName());

    private final RequestDao requestDao;
    private final RequestTypeDao requestTypeDao;
    private final HolidayDao holidayDao;
    private final group4.hrms.dao.HolidayCalendarDao holidayCalendarDao;
    private final UserDao userDao;

    // Constants for OT limits
    private static final int DAILY_LIMIT = 10;  // Total hours per day (regular + OT)
    private static final int WEEKLY_LIMIT = 48; // Total hours per week (regular + OT)
    private static final int MONTHLY_LIMIT = 40; // OT hours per month
    private static final int ANNUAL_LIMIT = 300; // OT hours per year

    private static final int REGULAR_DAILY_HOURS = 8;
    private static final int REGULAR_WEEKLY_HOURS = 40; // 5 days x 8 hours

    // Time range constants
    private static final LocalTime DAY_SHIFT_START = LocalTime.of(6, 0);
    private static final LocalTime DAY_SHIFT_END = LocalTime.of(22, 0);

    // OT Type constants
    private static final String OT_TYPE_WEEKDAY = "WEEKDAY";
    private static final String OT_TYPE_WEEKEND = "WEEKEND";
    private static final String OT_TYPE_HOLIDAY = "HOLIDAY";
    private static final String OT_TYPE_COMPENSATORY = "COMPENSATORY";

    // Pay multipliers
    private static final double PAY_MULTIPLIER_WEEKDAY = 1.5;
    private static final double PAY_MULTIPLIER_WEEKEND = 2.0;
    private static final double PAY_MULTIPLIER_HOLIDAY = 3.0;
    private static final double PAY_MULTIPLIER_COMPENSATORY = 2.0;

    // Request type code
    private static final String OT_REQUEST_TYPE_CODE = "OVERTIME_REQUEST";

    public OTRequestService(RequestDao requestDao, RequestTypeDao requestTypeDao,
                           HolidayDao holidayDao, group4.hrms.dao.HolidayCalendarDao holidayCalendarDao,
                           UserDao userDao) {
        this.requestDao = requestDao;
        this.requestTypeDao = requestTypeDao;
        this.holidayDao = holidayDao;
        this.holidayCalendarDao = holidayCalendarDao;
        this.userDao = userDao;
    }


    public Long createOTRequest(Long accountId, Long userId, Long departmentId,
                               String requestTitle, String otDate, String startTime, String endTime,
                               String reason, Boolean employeeConsent) throws SQLException {

        logger.info(String.format("Creating OT request: userId=%d, date=%s, time=%s-%s, hours=%.1f",
                   userId, otDate, startTime, endTime, calculateOTHours(startTime, endTime)));

        try {
            // Validate OT date - must be at least 1 day in advance
            LocalDate requestedDate = LocalDate.parse(otDate);
            LocalDate today = LocalDate.now();
            if (!requestedDate.isAfter(today)) {
                throw new IllegalArgumentException("OT request must be submitted at least 1 day in advance. " +
                    "Requested date: " + otDate + ", Today: " + today);
            }

            // Validate employee consent
            validateEmployeeConsent(employeeConsent);

            // Validate time increment (must be 00, 15, 30, or 45 minutes)
            validateTimeIncrement(startTime, endTime);

            // Calculate OT hours
            Double otHours = calculateOTHours(startTime, endTime);

            // Validate time range
            validateTimeRange(startTime, endTime);

            // Determine OT type and pay multiplier
            String otType = determineOTType(otDate);
            Double payMultiplier = getPayMultiplier(otType);

            // Validate weekday OT time restrictions (19:00-22:00, max 2h)
            validateWeekdayOTTime(otDate, startTime, endTime, otHours);

            // NEW VALIDATIONS - Check for conflicts and limits
            // Check for OT overlap (same day, overlapping time)
            checkOTOverlap(userId, otDate, startTime, endTime);

            // Check for pending OT requests (warning only)
            checkPendingOTRequests(userId, otDate);

            // Validate OT balance (weekly/monthly/annual limits)
            validateOTBalance(userId, otDate, otHours);

            // Check conflict with leave requests (including half-day leave)
            // Check both PENDING and APPROVED to prevent conflicts early
            checkConflictWithLeave(userId, otDate, startTime, endTime);

            // EXISTING VALIDATIONS - Daily and weekly limits
            validateDailyLimit(userId, otDate, otHours);
            validateWeeklyLimit(userId, otDate, otHours);

            // Get OT request type
            RequestType otRequestType = requestTypeDao.findByCode(OT_REQUEST_TYPE_CODE);
            if (otRequestType == null) {
                throw new IllegalArgumentException("OT_REQUEST type not configured in system");
            }

            // Create OTRequestDetail object
            OTRequestDetail detail = new OTRequestDetail();
            detail.setOtDate(otDate);
            detail.setStartTime(startTime);
            detail.setEndTime(endTime);
            detail.setOtHours(otHours);
            detail.setReason(reason);
            detail.setOtType(otType);
            detail.setPayMultiplier(payMultiplier);
            detail.setEmployeeConsent(employeeConsent);
            detail.setConsentTimestamp(LocalDateTime.now().toString());
            detail.setCreatedByManager(false);
            detail.setManagerAccountId(null);

            // Validate detail
            detail.validate();

            // Create request object
            Request otRequest = new Request();
            otRequest.setRequestTypeId(otRequestType.getId());
            otRequest.setTitle(requestTitle != null && !requestTitle.trim().isEmpty()
                ? requestTitle.trim()
                : "OT Request - " + otDate);
            otRequest.setOtDetail(detail);
            otRequest.setCreatedByAccountId(accountId);
            otRequest.setCreatedByUserId(userId);
            otRequest.setDepartmentId(departmentId);
            otRequest.setStatus("PENDING");
            otRequest.setCreatedAt(LocalDateTime.now());
            otRequest.setUpdatedAt(LocalDateTime.now());

            // Save to database
            Request savedRequest = requestDao.save(otRequest);

            // Determine approver and auto-approve if top-level
            try {
                ApproverService approverService = new ApproverService(userDao, new group4.hrms.dao.AccountDao());

                Long approverId = approverService.findApprover(userId);

                if (approverId != null) {
                    // Found approver - set and keep status PENDING
                    savedRequest.setCurrentApproverAccountId(approverId);
                    requestDao.update(savedRequest);
                    logger.info(String.format("Approver set for OT request: requestId=%d, approverId=%d",
                               savedRequest.getId(), approverId));
                } else {
                    // No approver found - user is top-level (HRM/Department Head)
                    // Auto-approve immediately
                    savedRequest.setStatus("APPROVED");
                    savedRequest.setCurrentApproverAccountId(accountId);
                    savedRequest.setApproveReason("Auto-approved (top-level manager)");
                    requestDao.update(savedRequest);

                    logger.info(String.format("Auto-approved OT request for top-level user: requestId=%d, userId=%d",
                               savedRequest.getId(), userId));
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, String.format("Error setting approver for OT request: requestId=%d",
                          savedRequest.getId()), e);
                // Continue anyway - request is created, approver can be set manually
            }

            logger.info(String.format("Successfully created OT request: id=%d, userId=%d, date=%s, hours=%.1f, type=%s, status=%s",
                       savedRequest.getId(), userId, otDate, otHours, otType, savedRequest.getStatus()));
            return savedRequest.getId();

        } catch (IllegalArgumentException e) {
            logger.warning(String.format("OT validation failed: userId=%d, date=%s, error=%s",
                          userId, otDate, e.getMessage()));
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Unexpected error creating OT request: userId=%d, date=%s",
                      userId, otDate), e);
            throw e;
        }
    }


    public Long createOTRequestForEmployee(Long managerAccountId, Long employeeUserId,
                                          String requestTitle, String otDate, String startTime, String endTime,
                                          String reason) throws SQLException {

        logger.info("Manager " + managerAccountId + " creating OT request for employee " + employeeUserId);

        try {
            // Validate OT date - must be at least 1 day in advance
            LocalDate requestedDate = LocalDate.parse(otDate);
            LocalDate today = LocalDate.now();
            if (!requestedDate.isAfter(today)) {
                throw new IllegalArgumentException("OT request must be submitted at least 1 day in advance. " +
                    "Requested date: " + otDate + ", Today: " + today);
            }

            // Get employee information
            Optional<User> employeeOpt = userDao.findById(employeeUserId);
            if (!employeeOpt.isPresent()) {
                throw new IllegalArgumentException("Employee not found: " + employeeUserId);
            }
            User employee = employeeOpt.get();

            // Validate time increment (must be 00, 15, 30, or 45 minutes)
            validateTimeIncrement(startTime, endTime);

            // Calculate OT hours
            Double otHours = calculateOTHours(startTime, endTime);

            // Validate time range
            validateTimeRange(startTime, endTime);

            // Determine OT type and pay multiplier
            String otType = determineOTType(otDate);
            Double payMultiplier = getPayMultiplier(otType);

            // Validate weekday OT time restrictions (19:00-22:00, max 2h)
            validateWeekdayOTTime(otDate, startTime, endTime, otHours);

            // NEW VALIDATIONS - Check for conflicts and limits
            // Check for OT overlap (same day, overlapping time)
            checkOTOverlap(employeeUserId, otDate, startTime, endTime);

            // Check for pending OT requests (warning only)
            checkPendingOTRequests(employeeUserId, otDate);

            // Validate OT balance (weekly/monthly/annual limits)
            validateOTBalance(employeeUserId, otDate, otHours);

            // Check conflict with approved leave requests (including half-day leave)
            checkConflictWithLeave(employeeUserId, otDate, startTime, endTime);

            // EXISTING VALIDATIONS - Daily and weekly limits
            validateDailyLimit(employeeUserId, otDate, otHours);
            validateWeeklyLimit(employeeUserId, otDate, otHours);

            // Get OT request type
            RequestType otRequestType = requestTypeDao.findByCode(OT_REQUEST_TYPE_CODE);
            if (otRequestType == null) {
                throw new IllegalArgumentException("OT_REQUEST type not configured in system");
            }

            // Create OTRequestDetail object
            OTRequestDetail detail = new OTRequestDetail();
            detail.setOtDate(otDate);
            detail.setStartTime(startTime);
            detail.setEndTime(endTime);
            detail.setOtHours(otHours);
            detail.setReason(reason);
            detail.setOtType(otType);
            detail.setPayMultiplier(payMultiplier);
            detail.setEmployeeConsent(true); // Manager creates on behalf
            detail.setConsentTimestamp(LocalDateTime.now().toString());
            detail.setCreatedByManager(true);
            detail.setManagerAccountId(managerAccountId);

            // Validate detail
            detail.validate();

            // Create request object
            Request otRequest = new Request();
            otRequest.setRequestTypeId(otRequestType.getId());
            otRequest.setTitle(requestTitle != null && !requestTitle.trim().isEmpty()
                ? requestTitle.trim()
                : "OT Request - " + otDate + " (Created by Manager)");
            otRequest.setOtDetail(detail);
            otRequest.setCreatedByAccountId(managerAccountId);
            otRequest.setCreatedByUserId(employeeUserId);
            otRequest.setDepartmentId(employee.getDepartmentId());
            otRequest.setStatus("PENDING");
            otRequest.setCreatedAt(LocalDateTime.now());
            otRequest.setUpdatedAt(LocalDateTime.now());

            // Save to database
            Request savedRequest = requestDao.save(otRequest);

            logger.info("Manager created OT request with ID " + savedRequest.getId());
            return savedRequest.getId();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating OT request for employee", e);
            throw e;
        }
    }


    public List<Request> getUserOTRequests(Long userId) {
        try {
            // Get all requests for user
            List<Request> allRequests = requestDao.findByUserId(userId);

            // Get OT request type
            RequestType otRequestType = requestTypeDao.findByCode(OT_REQUEST_TYPE_CODE);
            if (otRequestType == null) {
                logger.warning("OT_REQUEST type not found in system");
                return List.of();
            }

            // Filter only OT requests
            return allRequests.stream()
                    .filter(req -> req.getRequestTypeId().equals(otRequestType.getId()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding OT requests for user: " + userId, e);
            throw new RuntimeException("Error finding OT requests", e);
        }
    }


    public OTBalance getOTBalance(Long userId) {
        logger.fine(String.format("Getting OT balance: userId=%d", userId));

        try {
            LocalDate now = LocalDate.now();

            // Calculate current week hours (Monday to Sunday)
            double currentWeekHours = calculateOTHoursInWeek(userId, now);

            // Also compute how many approved OT requests are in the current week
            java.time.LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            java.time.LocalDate weekEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            int approvedCountInWeek = countApprovedOTRequestsInRange(userId, weekStart, weekEnd);

            // Calculate current month hours
            double monthlyHours = calculateOTHoursInMonth(userId, now.getYear(), now.getMonthValue());

            // Calculate current year hours
            double annualHours = calculateOTHoursInYear(userId, now.getYear());

            // Count approved requests for month and year
            java.time.LocalDate monthStart = now.with(TemporalAdjusters.firstDayOfMonth());
            java.time.LocalDate monthEnd = now.with(TemporalAdjusters.lastDayOfMonth());
            int approvedCountInMonth = countApprovedOTRequestsInRange(userId, monthStart, monthEnd);

            java.time.LocalDate yearStart = java.time.LocalDate.of(now.getYear(), 1, 1);
            java.time.LocalDate yearEnd = java.time.LocalDate.of(now.getYear(), 12, 31);
            int approvedCountInYear = countApprovedOTRequestsInRange(userId, yearStart, yearEnd);

            logger.info(String.format("OT balance retrieved: userId=%d, weekly=%.2f/%.0f, monthly=%.2f/%.0f, annual=%.2f/%.0f",
                       userId, currentWeekHours, (double)WEEKLY_LIMIT, monthlyHours, (double)MONTHLY_LIMIT,
                       annualHours, (double)ANNUAL_LIMIT));

            OTBalance balance = new OTBalance();
            balance.setCurrentWeekHours(currentWeekHours);
            // compute regular hours this week: default REGULAR_WEEKLY_HOURS if there is any weekday OT in week
            double regularHoursThisWeek = computeRegularHoursForWeek(userId, now);
            balance.setRegularHoursThisWeek(regularHoursThisWeek);
            balance.setWeeklyLimit((double) WEEKLY_LIMIT);

            // Set week date range (Monday to Sunday)
            java.time.format.DateTimeFormatter weekFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
            balance.setWeekStartDate(weekStart.format(weekFormatter));
            balance.setWeekEndDate(weekEnd.format(weekFormatter));

            // Set month name
            java.time.format.DateTimeFormatter monthFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.ENGLISH);
            balance.setMonthName(now.format(monthFormatter));

            balance.setMonthlyHours(monthlyHours);
            balance.setMonthlyLimit((double) MONTHLY_LIMIT);
            balance.setMonthlyApprovedCount(approvedCountInMonth);
            balance.setAnnualHours(annualHours);
            balance.setAnnualLimit((double) ANNUAL_LIMIT);
            balance.setAnnualApprovedCount(approvedCountInYear);
            balance.setWeeklyApprovedCount(approvedCountInWeek);
            return balance;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error getting OT balance: userId=%d", userId), e);
            throw new RuntimeException("Error getting OT balance", e);
        }
    }

    /**
     * Get OT balance for a specific week/month offset from current date.
     *
     * @param userId the user ID
     * @param weekOffset weeks from current week (0 = current, -1 = last week, +1 = next week)
     * @param monthOffset months from current month (0 = current, -1 = last month, +1 = next month)
     * @return OTBalance with calculated hours for the specified period
     */
    public OTBalance getOTBalanceWithOffset(Long userId, int weekOffset, int monthOffset, int yearOffset) {
        logger.fine(String.format("Getting OT balance with offset: userId=%d, weekOffset=%d, monthOffset=%d, yearOffset=%d",
                                  userId, weekOffset, monthOffset, yearOffset));

        try {
            LocalDate now = LocalDate.now();

            // Calculate target week (apply weekOffset)
            LocalDate targetWeekDate = now.plusWeeks(weekOffset);
            java.time.LocalDate weekStart = targetWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            java.time.LocalDate weekEnd = targetWeekDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            // Calculate target month (apply monthOffset)
            LocalDate targetMonthDate = now.plusMonths(monthOffset);
            java.time.LocalDate monthStart = targetMonthDate.with(TemporalAdjusters.firstDayOfMonth());
            java.time.LocalDate monthEnd = targetMonthDate.with(TemporalAdjusters.lastDayOfMonth());

            // Calculate target year (apply yearOffset)
            int targetYear = now.getYear() + yearOffset;
            java.time.LocalDate yearStart = java.time.LocalDate.of(targetYear, 1, 1);
            java.time.LocalDate yearEnd = java.time.LocalDate.of(targetYear, 12, 31);

            // Calculate week hours for target week
            double currentWeekHours = calculateOTHoursInWeek(userId, targetWeekDate);
            int approvedCountInWeek = countApprovedOTRequestsInRange(userId, weekStart, weekEnd);

            // Calculate month hours for target month
            double monthlyHours = calculateOTHoursInMonth(userId, targetMonthDate.getYear(), targetMonthDate.getMonthValue());
            int approvedCountInMonth = countApprovedOTRequestsInRange(userId, monthStart, monthEnd);

            // Calculate annual hours for target year (with offset)
            double annualHours = calculateOTHoursInYear(userId, targetYear);
            int approvedCountInYear = countApprovedOTRequestsInRange(userId, yearStart, yearEnd);

            logger.info(String.format("OT balance with offset retrieved: userId=%d, week(offset=%d)=%.2f, month(offset=%d)=%.2f, year(offset=%d)=%.2f",
                       userId, weekOffset, currentWeekHours, monthOffset, monthlyHours, yearOffset, annualHours));

            OTBalance balance = new OTBalance();
            balance.setCurrentWeekHours(currentWeekHours);
            double regularHoursThisWeek = computeRegularHoursForWeek(userId, targetWeekDate);
            balance.setRegularHoursThisWeek(regularHoursThisWeek);
            balance.setWeeklyLimit((double) WEEKLY_LIMIT);

            // Set week date range
            java.time.format.DateTimeFormatter weekFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
            balance.setWeekStartDate(weekStart.format(weekFormatter));
            balance.setWeekEndDate(weekEnd.format(weekFormatter));

            // Set month name
            java.time.format.DateTimeFormatter monthFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.ENGLISH);
            balance.setMonthName(targetMonthDate.format(monthFormatter));

            balance.setMonthlyHours(monthlyHours);
            balance.setMonthlyLimit((double) MONTHLY_LIMIT);
            balance.setMonthlyApprovedCount(approvedCountInMonth);
            balance.setAnnualHours(annualHours);
            balance.setAnnualLimit((double) ANNUAL_LIMIT);
            balance.setAnnualApprovedCount(approvedCountInYear);
            balance.setWeeklyApprovedCount(approvedCountInWeek);

            return balance;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error getting OT balance with offset: userId=%d, weekOffset=%d, monthOffset=%d",
                                                    userId, weekOffset, monthOffset), e);
            throw new RuntimeException("Error getting OT balance with offset", e);
        }
    }


    /**
     * Validate that time uses valid increments (00, 15, 30, 45 minutes)
     * This prevents odd times like 10:07 or 14:23
     */
    private void validateTimeIncrement(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        int startMinute = start.getMinute();
        int endMinute = end.getMinute();

        // Valid minutes: 0, 15, 30, 45
        if (startMinute % 15 != 0) {
            throw new IllegalArgumentException(
                String.format("Start time must use 15-minute increments (00, 15, 30, 45). Invalid time: %s", startTime)
            );
        }

        if (endMinute % 15 != 0) {
            throw new IllegalArgumentException(
                String.format("End time must use 15-minute increments (00, 15, 30, 45). Invalid time: %s", endTime)
            );
        }
    }

    private void validateTimeRange(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        // Check if end time is after start time
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                "End time must be after start time"
            );
        }

        // General time range: 06:00-22:00
        if (start.isBefore(DAY_SHIFT_START) || end.isAfter(DAY_SHIFT_END)) {
            throw new IllegalArgumentException(
                "OT hours must be within 06:00-22:00 time range"
            );
        }
    }

    /**
     * Validate weekday OT time restrictions
     * Weekday OT: 19:00-22:00, max 2 hours
     */
    private void validateWeekdayOTTime(String otDate, String startTime, String endTime, Double otHours) {
        // Determine OT type for the date - only enforce these rules for WEEKDAY type
        String otType = determineOTType(otDate);
        if (!OT_TYPE_WEEKDAY.equals(otType)) {
            // Compensatory/Holiday/Weekend are exempt from the weekday 19:00-22:00 restriction
            return;
        }

        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        LocalTime weekdayOTStart = LocalTime.of(19, 0); // 19:00
        LocalTime weekdayOTEnd = LocalTime.of(22, 0);   // 22:00

        // Weekday OT must be between 19:00-22:00
        if (start.isBefore(weekdayOTStart)) {
            throw new IllegalArgumentException(
                "Weekday OT can only start from 19:00 onwards. Your start time: " + startTime
            );
        }

        if (end.isAfter(weekdayOTEnd)) {
            throw new IllegalArgumentException(
                "Weekday OT must end by 22:00. Your end time: " + endTime
            );
        }

        // Weekday OT maximum 2 hours
        if (otHours > 2.0) {
            throw new IllegalArgumentException(
                String.format("Weekday OT is limited to 2 hours maximum. You requested: %.1f hours", otHours)
            );
        }
    }


    private void validateDailyLimit(Long userId, String otDate, Double otHours) throws SQLException {
        logger.fine(String.format("Validating daily limit: userId=%d, date=%s, requestedHours=%.1f",
                   userId, otDate, otHours));

        // Determine OT type to apply different limits
        String otType = determineOTType(otDate);
        boolean isWeekdayOT = OT_TYPE_WEEKDAY.equals(otType);
        boolean isWeekendOT = OT_TYPE_WEEKEND.equals(otType);
        boolean isHolidayOT = OT_TYPE_HOLIDAY.equals(otType) || OT_TYPE_COMPENSATORY.equals(otType);

        // Get approved OT hours for the same date
        List<Request> otRequests = getUserOTRequests(userId);

        double approvedOTHours = 0.0;
        for (Request request : otRequests) {
            if (!"APPROVED".equals(request.getStatus())) {
                continue;
            }

            OTRequestDetail detail = request.getOtDetail();
            if (detail != null && otDate.equals(detail.getOtDate())) {
                approvedOTHours += detail.getOtHours();
            }
        }

        if (isWeekdayOT) {
            // WEEKDAY: Regular work (8h) + OT max 2h = 10h total
            double totalDailyHours = REGULAR_DAILY_HOURS + approvedOTHours + otHours;
            double maxOTHours = 2.0; // Maximum 2 hours OT on weekdays

            logger.fine(String.format("Weekday OT validation: userId=%d, date=%s, regular=%d, approved=%.1f, requested=%.1f, total=%.1f, limit=%d",
                       userId, otDate, REGULAR_DAILY_HOURS, approvedOTHours, otHours, totalDailyHours, DAILY_LIMIT));

            // Check total daily hours (regular + OT)
            if (totalDailyHours > DAILY_LIMIT) {
                logger.warning(String.format("Daily limit exceeded: userId=%d, date=%s, total=%.1f, limit=%d",
                              userId, otDate, totalDailyHours, DAILY_LIMIT));
                throw new IllegalArgumentException(
                    String.format("Total daily hours cannot exceed %d hours (current: %.1fh). " +
                        "Regular hours: %dh, Approved OT: %.1fh, Requested OT: %.1fh",
                        DAILY_LIMIT, totalDailyHours, REGULAR_DAILY_HOURS, approvedOTHours, otHours)
                );
            }

            // Check OT hours limit (max 2h on weekdays)
            double totalOTHours = approvedOTHours + otHours;
            if (totalOTHours > maxOTHours) {
                logger.warning(String.format("Weekday OT limit exceeded: userId=%d, date=%s, totalOT=%.1f, maxOT=%.1f",
                              userId, otDate, totalOTHours, maxOTHours));
                throw new IllegalArgumentException(
                    String.format("Weekday OT is limited to %.0f hours maximum (approved: %.1fh, requested: %.1fh, total: %.1fh)",
                        maxOTHours, approvedOTHours, otHours, totalOTHours)
                );
            }

        } else if (isWeekendOT || isHolidayOT) {
            // WEEKEND/HOLIDAY: No regular work, can OT more hours
            double maxOTHours = 10.0; // Maximum 10 hours OT on weekends/holidays
            double totalOTHours = approvedOTHours + otHours;

            logger.fine(String.format("Weekend/Holiday OT validation: userId=%d, date=%s, type=%s, approved=%.1f, requested=%.1f, total=%.1f, limit=%.1f",
                       userId, otDate, otType, approvedOTHours, otHours, totalOTHours, maxOTHours));

            // Check OT hours limit (no regular work hours on weekends/holidays)
            if (totalOTHours > maxOTHours) {
                logger.warning(String.format("Weekend/Holiday OT limit exceeded: userId=%d, date=%s, totalOT=%.1f, maxOT=%.1f",
                              userId, otDate, totalOTHours, maxOTHours));
                throw new IllegalArgumentException(
                    String.format("Weekend/Holiday OT is limited to %.0f hours maximum (approved: %.1fh, requested: %.1fh, total: %.1fh)",
                        maxOTHours, approvedOTHours, otHours, totalOTHours)
                );
            }
        }

        logger.fine(String.format("Daily limit validation passed: userId=%d, date=%s, type=%s", userId, otDate, otType));
    }


    private void validateWeeklyLimit(Long userId, String otDate, Double otHours) throws SQLException {
        logger.fine(String.format("Validating weekly limit: userId=%d, date=%s, requestedHours=%.1f",
                   userId, otDate, otHours));

        LocalDate date = LocalDate.parse(otDate);

        // Get week start (Monday) and end (Sunday)
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Get approved OT hours in the week
        List<Request> otRequests = getUserOTRequests(userId);

        double approvedWeeklyOTHours = 0.0;
        int weekdayOTCount = 0;
        for (Request request : otRequests) {
            if (!"APPROVED".equals(request.getStatus())) {
                continue;
            }

            OTRequestDetail detail = request.getOtDetail();
            if (detail != null) {
                LocalDate requestDate = LocalDate.parse(detail.getOtDate());
                if (!requestDate.isBefore(weekStart) && !requestDate.isAfter(weekEnd)) {
                    approvedWeeklyOTHours += detail.getOtHours();
                    // Count weekday OT hours only
                    if (OT_TYPE_WEEKDAY.equals(detail.getOtType())) {
                        weekdayOTCount++;
                    }
                }
            }
        }

        // Determine if current request is weekday OT
        String currentOTType = determineOTType(otDate);
        boolean isCurrentWeekdayOT = OT_TYPE_WEEKDAY.equals(currentOTType);

        // Calculate total weekly hours
        // Only add regular hours if there are weekday OT requests
        double regularHours = (weekdayOTCount > 0 || isCurrentWeekdayOT) ? REGULAR_WEEKLY_HOURS : 0;
        double totalWeeklyHours = regularHours + approvedWeeklyOTHours + otHours;

        logger.fine(String.format("Weekly hours calculation: userId=%d, week=%s to %s, regular=%d, approved=%.1f, requested=%.1f, total=%.1f, limit=%d, currentType=%s",
                   userId, weekStart, weekEnd, (int)regularHours, approvedWeeklyOTHours, otHours, totalWeeklyHours, WEEKLY_LIMIT, currentOTType));

        if (totalWeeklyHours > WEEKLY_LIMIT) {
            logger.warning(String.format("Weekly limit exceeded: userId=%d, week=%s to %s, total=%.1f, limit=%d",
                          userId, weekStart, weekEnd, totalWeeklyHours, WEEKLY_LIMIT));
            throw new IllegalArgumentException(
                String.format("Total weekly hours cannot exceed %d hours (current: %.1fh). " +
                    "Regular hours: %.0fh, Approved OT: %.1fh, Requested OT: %.1fh",
                    WEEKLY_LIMIT, totalWeeklyHours, regularHours, approvedWeeklyOTHours, otHours)
            );
        }

        logger.fine(String.format("Weekly limit validation passed: userId=%d, week=%s to %s",
                   userId, weekStart, weekEnd));
    }


    private void validateEmployeeConsent(Boolean employeeConsent) {
        if (employeeConsent == null || !employeeConsent) {
            throw new IllegalArgumentException(
                "Employee consent is required for all OT work"
            );
        }
    }


    private String determineOTType(String otDate) {
        try {
            LocalDate date = LocalDate.parse(otDate);

            // Check if it's a holiday first
            if (isHoliday(otDate)) {
                // Check if it's a compensatory day
                if (isCompensatoryDay(otDate)) {
                    return OT_TYPE_COMPENSATORY;
                }
                return OT_TYPE_HOLIDAY;
            }

            // Check if it's a weekend
            if (isWeekend(otDate)) {
                return OT_TYPE_WEEKEND;
            }

            // Default to weekday
            return OT_TYPE_WEEKDAY;

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error determining OT type for date: " + otDate, e);
            return OT_TYPE_WEEKDAY; // Default to weekday on error
        }
    }


    private Double getPayMultiplier(String otType) {
        switch (otType) {
            case OT_TYPE_WEEKDAY:
                return PAY_MULTIPLIER_WEEKDAY;
            case OT_TYPE_WEEKEND:
                return PAY_MULTIPLIER_WEEKEND;
            case OT_TYPE_HOLIDAY:
                return PAY_MULTIPLIER_HOLIDAY;
            case OT_TYPE_COMPENSATORY:
                return PAY_MULTIPLIER_COMPENSATORY;
            default:
                logger.warning("Unknown OT type: " + otType + ", defaulting to weekday multiplier");
                return PAY_MULTIPLIER_WEEKDAY;
        }
    }


    private Double calculateOTHours(String startTime, String endTime) {
        try {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            // Calculate duration in hours
            long minutes = java.time.Duration.between(start, end).toMinutes();
            double hours = minutes / 60.0;

            // Round to 2 decimal places
            return Math.round(hours * 100.0) / 100.0;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating OT hours", e);
            throw new IllegalArgumentException("Invalid time format: " + e.getMessage());
        }
    }


    private boolean isHoliday(String date) throws SQLException {
        try {
            LocalDate localDate = LocalDate.parse(date);
            int year = localDate.getYear();

            // Get calendar for the year
            Optional<group4.hrms.model.HolidayCalendar> calendarOpt = holidayCalendarDao.findByYear(year);

            if (!calendarOpt.isPresent()) {
                logger.warning("No holiday calendar found for year: " + year);
                return false;
            }

            Long calendarId = calendarOpt.get().getId();

            // Check if date is a holiday
            return holidayDao.isHoliday(calendarId, localDate);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking if date is holiday: " + date, e);
            return false;
        }
    }


    private boolean isWeekend(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();

            return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking if date is weekend: " + date, e);
            return false;
        }
    }


    private boolean isCompensatoryDay(String date) throws SQLException {
        try {
            LocalDate localDate = LocalDate.parse(date);
            int year = localDate.getYear();

            // Get calendar for the year
            Optional<group4.hrms.model.HolidayCalendar> calendarOpt = holidayCalendarDao.findByYear(year);

            if (!calendarOpt.isPresent()) {
                return false;
            }

            Long calendarId = calendarOpt.get().getId();

            // Get holidays for the date
            List<Holiday> holidays = holidayDao.findByDateRange(calendarId, localDate, localDate);

            // Check if any holiday name contains "Nghỉ bù" or "nghi bu"
            for (Holiday holiday : holidays) {
                String name = holiday.getName();
                if (name != null &&
                    (name.toLowerCase().contains("nghỉ bù") ||
                     name.toLowerCase().contains("nghi bu"))) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking if date is compensatory day: " + date, e);
            return false;
        }
    }


    private double calculateOTHoursInWeek(Long userId, LocalDate date) {
        // Get week start (Monday) and end (Sunday)
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return calculateApprovedOTHours(userId, weekStart, weekEnd);
    }


    private double calculateOTHoursInMonth(Long userId, int year, int month) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());
        return calculateApprovedOTHours(userId, monthStart, monthEnd);
    }


    private double calculateOTHoursInYear(Long userId, int year) {
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);
        return calculateApprovedOTHours(userId, yearStart, yearEnd);
    }


    private double calculateApprovedOTHours(Long userId, LocalDate startDate, LocalDate endDate) {
        logger.fine(String.format("Calculating approved and pending OT hours: userId=%d, startDate=%s, endDate=%s",
                   userId, startDate, endDate));

        try {
            List<Request> otRequests = getUserOTRequests(userId);

            double totalHours = 0.0;
            int approvedCount = 0;

            for (Request request : otRequests) {
                // Count both APPROVED and PENDING requests (PENDING requests are awaiting approval and should count toward limits)
                String status = request.getStatus();
                if (!"APPROVED".equals(status) && !"PENDING".equals(status)) {
                    continue;
                }

                // Get OT detail
                OTRequestDetail detail = request.getOtDetail();
                if (detail == null) {
                    continue;
                }

                // Parse OT date
                LocalDate otDate = LocalDate.parse(detail.getOtDate());

                // Check if OT date is in range
                if (!otDate.isBefore(startDate) && !otDate.isAfter(endDate)) {
                    totalHours += detail.getOtHours();
                    approvedCount++;
                }
            }

            logger.info(String.format("Calculated approved OT hours (double): userId=%d, dateRange=%s to %s, hours=%.2f, requests=%d",
                       userId, startDate, endDate, totalHours, approvedCount));
            return totalHours;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating approved OT hours: userId=%d, startDate=%s, endDate=%s",
                      userId, startDate, endDate), e);
            return 0;
        }
    }

    /**
     * Count approved OT requests (number of requests) in a date range for user.
     */
    private int countApprovedOTRequestsInRange(Long userId, LocalDate startDate, LocalDate endDate) {
        try {
            List<Request> otRequests = getUserOTRequests(userId);
            int approvedCount = 0;
            for (Request request : otRequests) {
                if (!"APPROVED".equals(request.getStatus())) continue;
                OTRequestDetail detail = request.getOtDetail();
                if (detail == null) continue;
                LocalDate otDate = LocalDate.parse(detail.getOtDate());
                if (!otDate.isBefore(startDate) && !otDate.isAfter(endDate)) {
                    approvedCount++;
                }
            }
            return approvedCount;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error counting approved OT requests", e);
            return 0;
        }
    }


    /**
     * Check if OT request overlaps with existing OT requests on the same date
     * Time ranges overlap if: start1 < end2 AND start2 < end1
     *
     * @param userId User ID
     * @param otDate OT date (yyyy-MM-dd format)
     * @param startTime Start time (HH:mm format)
     * @param endTime End time (HH:mm format)
     * @throws IllegalArgumentException if overlap detected
     */
    private void checkOTOverlap(Long userId, String otDate, String startTime, String endTime) {
        logger.fine(String.format("Checking OT overlap: userId=%d, date=%s, time=%s-%s",
                   userId, otDate, startTime, endTime));

        try {
            // Get all OT requests for the same date
            List<Request> otRequests = getUserOTRequests(userId);

            LocalTime newStart = LocalTime.parse(startTime);
            LocalTime newEnd = LocalTime.parse(endTime);

            for (Request request : otRequests) {
                // Only check APPROVED and PENDING requests
                if (!"APPROVED".equals(request.getStatus()) && !"PENDING".equals(request.getStatus())) {
                    continue;
                }

                OTRequestDetail detail = request.getOtDetail();
                if (detail == null || !otDate.equals(detail.getOtDate())) {
                    continue;
                }

                // Parse existing OT time range
                LocalTime existingStart = LocalTime.parse(detail.getStartTime());
                LocalTime existingEnd = LocalTime.parse(detail.getEndTime());

                // Check if time ranges overlap: start1 < end2 AND start2 < end1
                if (newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd)) {
                    logger.warning(String.format("OT overlap detected: userId=%d, date=%s, existingTime=%s-%s, requestedTime=%s-%s, status=%s",
                                  userId, otDate, detail.getStartTime(), detail.getEndTime(),
                                  startTime, endTime, request.getStatus()));
                    throw new IllegalArgumentException(
                        String.format("OT schedule conflict! You already have OT from %s to %s on %s (Status: %s). " +
                            "Please choose a different time slot.",
                            detail.getStartTime(), detail.getEndTime(), otDate, request.getStatus())
                    );
                }
            }

            logger.fine(String.format("No OT overlap found: userId=%d, date=%s", userId, otDate));

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error checking OT overlap: userId=%d, date=%s",
                      userId, otDate), e);
            throw new RuntimeException("Error checking OT overlap", e);
        }
    }


    /**
     * Check for pending OT requests on the same date
     * This is a warning only - does not block request creation
     *
     * @param userId User ID
     * @param otDate OT date (yyyy-MM-dd format)
     */
    private void checkPendingOTRequests(Long userId, String otDate) {
        try {
            // Get all OT requests for the same date
            List<Request> otRequests = getUserOTRequests(userId);

            for (Request request : otRequests) {
                // Only check PENDING requests
                if (!"PENDING".equals(request.getStatus())) {
                    continue;
                }

                OTRequestDetail detail = request.getOtDetail();
                if (detail == null || !otDate.equals(detail.getOtDate())) {
                    continue;
                }

                // Log warning if pending request found
                logger.log(Level.WARNING,
                    String.format("User %d has pending OT request on %s (ID: %d, Time: %s-%s). " +
                        "Creating another OT request for the same date.",
                        userId, otDate, request.getId(), detail.getStartTime(), detail.getEndTime())
                );
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error checking pending OT requests for user: " + userId, e);
            // Don't throw exception - this is warning only
        }
    }


    /**
     * Validate OT balance against weekly, monthly, and annual limits
     * Checks if requested OT would exceed any limit
     *
     * @param userId User ID
     * @param otDate OT date (yyyy-MM-dd format)
     * @param otHours Requested OT hours
     * @throws IllegalArgumentException if any limit would be exceeded
     */
    public void validateOTBalance(Long userId, String otDate, Double otHours) {
        logger.fine(String.format("Validating OT balance: userId=%d, date=%s, requestedHours=%.1f",
                   userId, otDate, otHours));

        try {
            LocalDate date = LocalDate.parse(otDate);

            // Calculate current weekly OT hours (double precision)
            double currentWeekHours = calculateOTHoursInWeek(userId, date);

            // IMPORTANT: Calculate regular work hours for the week to enforce 48h total limit
            double regularHoursThisWeek = computeRegularHoursForWeek(userId, date);
            double totalWorkHours = regularHoursThisWeek + currentWeekHours + otHours;

            logger.fine(String.format("Weekly balance: userId=%d, regular=%.2f, currentOT=%.2f, requestedOT=%.1f, total=%.2f, limit=%d",
                       userId, regularHoursThisWeek, currentWeekHours, otHours, totalWorkHours, WEEKLY_LIMIT));

            // Check if total work hours (regular + OT) exceeds 48h/week limit
            if (totalWorkHours > WEEKLY_LIMIT) {
                double remainingHours = WEEKLY_LIMIT - regularHoursThisWeek - currentWeekHours;
                logger.warning(String.format("Weekly work limit exceeded: userId=%d, regular=%.2f, currentOT=%.2f, requestedOT=%.1f, total=%.2f, limit=%d",
                              userId, regularHoursThisWeek, currentWeekHours, otHours, totalWorkHours, WEEKLY_LIMIT));
                throw new IllegalArgumentException(
                    String.format("Weekly work limit (48h) exceeded! Regular work: %.1fh, Current OT: %.1fh, Requested OT: %.1fh, " +
                        "Total: %.1fh. You can only add %.1fh more OT this week.",
                        regularHoursThisWeek, currentWeekHours, otHours, totalWorkHours, Math.max(0, remainingHours))
                );
            }

            // Calculate current monthly OT hours
            double monthlyHours = calculateOTHoursInMonth(userId, date.getYear(), date.getMonthValue());
            logger.fine(String.format("Monthly balance: userId=%d, current=%.2f, requested=%.1f, limit=%d",
                       userId, monthlyHours, otHours, MONTHLY_LIMIT));

            if (monthlyHours + otHours > MONTHLY_LIMIT) {
                logger.warning(String.format("Monthly OT limit exceeded: userId=%d, current=%.2f, requested=%.1f, limit=%d",
                              userId, monthlyHours, otHours, MONTHLY_LIMIT));
                throw new IllegalArgumentException(
                    String.format("Monthly OT limit exceeded! Current: %.1fh, Requested: %.1fh, " +
                        "Limit: %dh. Remaining: %.1fh.",
                        monthlyHours, otHours, MONTHLY_LIMIT, MONTHLY_LIMIT - monthlyHours)
                );
            }

            // Calculate current annual OT hours
            double annualHours = calculateOTHoursInYear(userId, date.getYear());
            logger.fine(String.format("Annual balance: userId=%d, current=%.2f, requested=%.1f, limit=%d",
                       userId, annualHours, otHours, ANNUAL_LIMIT));

            if (annualHours + otHours > ANNUAL_LIMIT) {
                logger.warning(String.format("Annual OT limit exceeded: userId=%d, current=%.2f, requested=%.1f, limit=%d",
                              userId, annualHours, otHours, ANNUAL_LIMIT));
                throw new IllegalArgumentException(
                    String.format("Annual OT limit exceeded! Current: %.1fh, Requested: %.1fh, " +
                        "Limit: %dh. Remaining: %.1fh.",
                        annualHours, otHours, ANNUAL_LIMIT, ANNUAL_LIMIT - annualHours)
                );
            }

            logger.info(String.format("OT balance validation passed: userId=%d, date=%s, regular=%.2f, weekly=%.2f/%.0f, monthly=%.2f/%.0f, annual=%.2f/%.0f",
                       userId, otDate, regularHoursThisWeek, currentWeekHours, (double)WEEKLY_LIMIT, monthlyHours, (double)MONTHLY_LIMIT,
                       annualHours, (double)ANNUAL_LIMIT));

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error validating OT balance: userId=%d, date=%s",
                      userId, otDate), e);
            throw new RuntimeException("Error validating OT balance", e);
        }
    }


    /**
     * Check if OT request conflicts with leave requests (PENDING + APPROVED)
     * This checks both PENDING and APPROVED leaves to prevent conflicts early
     * Used in both creation and approval flows
     *
     * Half-day leave rules (Requirements: 5.8, 5.9, 5.10, 4.6, BR-22, BR-LV-11):
     * - Morning half-day (8:00-12:00): Allow OT in afternoon/evening (after 12:00)
     * - Afternoon half-day (13:00-17:00): Allow OT after 17:00
     * - Two half-days (AM+PM) on same date: Treat as full-day, block all OT
     * - Full-day leave: Block all OT
     *
     * @param userId User ID
     * @param otDate OT date (yyyy-MM-dd format)
     * @param startTime OT start time (HH:mm format)
     * @param endTime OT end time (HH:mm format)
     * @throws IllegalArgumentException if conflict detected
     */
    public void checkConflictWithLeave(Long userId, String otDate, String startTime, String endTime) {
        try {
            LocalDate date = LocalDate.parse(otDate);
            LocalTime otStart = LocalTime.parse(startTime);
            LocalTime otEnd = LocalTime.parse(endTime);

            // Get all requests for user
            List<Request> allRequests = requestDao.findByUserId(userId);

            // FIRST PASS: Check if there are 2 half-days (AM+PM) on the same date
            // If yes, treat as full-day leave → block all OT
            // Check both PENDING and APPROVED leaves
            boolean hasAMHalfDay = false;
            boolean hasPMHalfDay = false;
            String amStatus = null;
            String pmStatus = null;

            for (Request request : allRequests) {
                // Check both PENDING and APPROVED leaves
                if (!"APPROVED".equals(request.getStatus()) && !"PENDING".equals(request.getStatus())) {
                    continue;
                }

                group4.hrms.dto.LeaveRequestDetail leaveDetail = request.getLeaveDetail();
                if (leaveDetail == null) {
                    continue;
                }

                // Check if this leave falls on OT date
                String startDate = leaveDetail.getStartDate();
                String endDate = leaveDetail.getEndDate();
                if (startDate == null || endDate == null) {
                    continue;
                }

                LocalDate leaveStart = LocalDate.parse(startDate.substring(0, 10));
                LocalDate leaveEnd = LocalDate.parse(endDate.substring(0, 10));

                if (date.isBefore(leaveStart) || date.isAfter(leaveEnd)) {
                    continue; // Not on OT date
                }

                // Check if it's a half-day leave
                Boolean isHalfDay = leaveDetail.getIsHalfDay();
                String halfDayPeriod = leaveDetail.getHalfDayPeriod();

                if (isHalfDay != null && isHalfDay && halfDayPeriod != null) {
                    if ("AM".equals(halfDayPeriod)) {
                        hasAMHalfDay = true;
                        amStatus = request.getStatus();
                    } else if ("PM".equals(halfDayPeriod)) {
                        hasPMHalfDay = true;
                        pmStatus = request.getStatus();
                    }
                }
            }

            // If both AM and PM half-days exist on the same date → treat as full-day leave
            if (hasAMHalfDay && hasPMHalfDay) {
                logger.warning(String.format("OT conflicts with 2 half-day leaves (AM+PM = full-day): userId=%d, date=%s, AM status=%s, PM status=%s",
                              userId, otDate, amStatus, pmStatus));
                throw new IllegalArgumentException(
                    String.format("Cannot create OT on %s! You have half-day leave requests " +
                        "for both morning (AM - %s) and afternoon (PM - %s) on this date, " +
                        "which is equivalent to a full-day leave. No OT is allowed.",
                        otDate, amStatus, pmStatus)
                );
            }

            // SECOND PASS: Check individual leave conflicts
            for (Request request : allRequests) {
                // Check both PENDING and APPROVED leave requests
                if (!"APPROVED".equals(request.getStatus()) && !"PENDING".equals(request.getStatus())) {
                    continue;
                }

                // Get leave detail
                group4.hrms.dto.LeaveRequestDetail leaveDetail = request.getLeaveDetail();
                if (leaveDetail == null) {
                    continue; // Not a leave request
                }

                // Validate leave dates
                String startDate = leaveDetail.getStartDate();
                String endDate = leaveDetail.getEndDate();
                if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
                    logger.warning(String.format("Leave request has null/empty dates: requestId=%d, userId=%d",
                                  request.getId(), userId));
                    continue; // Skip invalid leave request
                }

                // Parse leave date range
                LocalDate leaveStart = LocalDate.parse(startDate.substring(0, 10));
                LocalDate leaveEnd = LocalDate.parse(endDate.substring(0, 10));

                // Check if OT date falls within leave period
                if (date.isBefore(leaveStart) || date.isAfter(leaveEnd)) {
                    continue; // OT date is outside leave period
                }

                // Check if it's a half-day leave
                Boolean isHalfDay = leaveDetail.getIsHalfDay();
                String halfDayPeriod = leaveDetail.getHalfDayPeriod();

                if (isHalfDay != null && isHalfDay && halfDayPeriod != null) {
                    // Half-day leave conflict checking
                    if ("AM".equals(halfDayPeriod)) {
                        // Morning half-day (8:00-12:00)
                        // Block OT if it overlaps with morning period
                        LocalTime morningStart = LocalTime.of(8, 0);
                        LocalTime morningEnd = LocalTime.of(12, 0);

                        // Check if OT overlaps with morning period: start1 < end2 AND start2 < end1
                        if (otStart.isBefore(morningEnd) && morningStart.isBefore(otEnd)) {
                            logger.warning(String.format("OT conflicts with morning half-day leave: userId=%d, date=%s, otTime=%s-%s, leaveType=%s, status=%s",
                                          userId, otDate, startTime, endTime, leaveDetail.getLeaveTypeName(), request.getStatus()));
                            throw new IllegalArgumentException(
                                String.format("Cannot create OT during %s-%s on %s! " +
                                    "You have a %s morning half-day leave (8:00-12:00) (%s). " +
                                    "You can only create OT after 12:00.",
                                    startTime, endTime, otDate, request.getStatus().toLowerCase(), leaveDetail.getLeaveTypeName())
                            );
                        }
                        // OT is allowed if it's after 12:00 (afternoon/evening)
                        logger.fine(String.format("OT allowed after morning half-day leave: userId=%d, date=%s, otTime=%s-%s",
                                   userId, otDate, startTime, endTime));

                    } else if ("PM".equals(halfDayPeriod)) {
                        // Afternoon half-day (13:00-17:00)
                        // Block OT if it overlaps with afternoon period
                        LocalTime afternoonStart = LocalTime.of(13, 0);
                        LocalTime afternoonEnd = LocalTime.of(17, 0);

                        // Check if OT overlaps with afternoon period: start1 < end2 AND start2 < end1
                        if (otStart.isBefore(afternoonEnd) && afternoonStart.isBefore(otEnd)) {
                            logger.warning(String.format("OT conflicts with afternoon half-day leave: userId=%d, date=%s, otTime=%s-%s, leaveType=%s, status=%s",
                                          userId, otDate, startTime, endTime, leaveDetail.getLeaveTypeName(), request.getStatus()));
                            throw new IllegalArgumentException(
                                String.format("Cannot create OT during %s-%s on %s! " +
                                    "You have a %s afternoon half-day leave (13:00-17:00) (%s). " +
                                    "You can only create OT after 17:00.",
                                    startTime, endTime, otDate, request.getStatus().toLowerCase(), leaveDetail.getLeaveTypeName())
                            );
                        }
                        // OT is allowed if it's after 17:00
                        logger.fine(String.format("OT allowed after afternoon half-day leave: userId=%d, date=%s, otTime=%s-%s",
                                   userId, otDate, startTime, endTime));
                    }
                } else {
                    // Full-day leave - block all OT
                    logger.warning(String.format("OT conflicts with full-day leave: userId=%d, date=%s, leaveType=%s, leaveDates=%s to %s, status=%s",
                                  userId, otDate, leaveDetail.getLeaveTypeName(), leaveDetail.getStartDate(), leaveDetail.getEndDate(), request.getStatus()));
                    throw new IllegalArgumentException(
                        String.format("Cannot create OT on %s! You have a %s leave request " +
                            "(%s) from %s to %s.",
                            otDate, request.getStatus().toLowerCase(), leaveDetail.getLeaveTypeName(),
                            leaveDetail.getStartDate(), leaveDetail.getEndDate())
                    );
                }
            }

            logger.fine(String.format("No leave conflict found for OT: userId=%d, date=%s, time=%s-%s",
                       userId, otDate, startTime, endTime));

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error checking conflict with leave: userId=%d, date=%s",
                      userId, otDate), e);
            throw new RuntimeException("Error checking conflict with leave", e);
        }
    }



    /**
     * Gets list of holiday dates for a specific year.
     * Automatically generates holidays if not exists for the year.
     *
     * @param year the year to get holidays for
     * @return list of holiday dates in "yyyy-MM-dd" format
     */
    public List<String> getHolidaysForYear(int year) {
        try {
            // Check if calendar exists and is generated
            Optional<group4.hrms.model.HolidayCalendar> calendarOpt = holidayCalendarDao.findByYear(year);

            // Auto-generate if calendar doesn't exist or not generated yet
            if (!calendarOpt.isPresent() || !calendarOpt.get().isGenerated()) {
                logger.info("Holiday calendar not found or not generated for year " + year
                    + ". Auto-generating holidays...");

                try {
                    // Auto-generate holidays using HolidayGenerator
                    group4.hrms.util.HolidayGenerator generator =
                        new group4.hrms.util.HolidayGenerator(holidayCalendarDao, holidayDao);
                    int count = generator.generateHolidaysForYear(year);
                    logger.info("Auto-generated " + count + " holidays for year " + year);

                    // Reload calendar after generation
                    calendarOpt = holidayCalendarDao.findByYear(year);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to auto-generate holidays for year " + year, e);
                    return java.util.Collections.emptyList();
                }
            }

            if (!calendarOpt.isPresent()) {
                logger.warning("No holiday calendar found for year " + year + " after generation attempt");
                return java.util.Collections.emptyList();
            }

            Long calendarId = calendarOpt.get().getId();
            List<Holiday> holidays = holidayDao.findByCalendarId(calendarId);

            // Filter out compensatory days and return only regular holidays
            return holidays.stream()
                .filter(h -> !h.getName().toLowerCase().contains("nghỉ bù")
                          && !h.getName().toLowerCase().contains("nghi bu")
                          && !h.getName().toLowerCase().contains("compensatory"))
                .map(h -> h.getDateHoliday().toString())
                .collect(Collectors.toList());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting holidays for year " + year, e);
            return java.util.Collections.emptyList();
        }
    }


    /**
     * Gets list of compensatory day dates for a specific year.
     * Automatically generates holidays if not exists for the year.
     *
     * @param year the year to get compensatory days for
     * @return list of compensatory day dates in "yyyy-MM-dd" format
     */
    public List<String> getCompensatoryDaysForYear(int year) {
        try {
            // Check if calendar exists and is generated
            Optional<group4.hrms.model.HolidayCalendar> calendarOpt = holidayCalendarDao.findByYear(year);

            // Auto-generate if calendar doesn't exist or not generated yet
            if (!calendarOpt.isPresent() || !calendarOpt.get().isGenerated()) {
                logger.info("Holiday calendar not found or not generated for year " + year
                    + ". Auto-generating holidays...");

                try {
                    // Auto-generate holidays using HolidayGenerator
                    group4.hrms.util.HolidayGenerator generator =
                        new group4.hrms.util.HolidayGenerator(holidayCalendarDao, holidayDao);
                    int count = generator.generateHolidaysForYear(year);
                    logger.info("Auto-generated " + count + " holidays for year " + year);

                    // Reload calendar after generation
                    calendarOpt = holidayCalendarDao.findByYear(year);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to auto-generate holidays for year " + year, e);
                    return java.util.Collections.emptyList();
                }
            }

            if (!calendarOpt.isPresent()) {
                logger.warning("No holiday calendar found for year " + year + " after generation attempt");
                return java.util.Collections.emptyList();
            }

            Long calendarId = calendarOpt.get().getId();
            List<Holiday> holidays = holidayDao.findByCalendarId(calendarId);

            // Filter only compensatory days
            return holidays.stream()
                .filter(h -> h.getName().toLowerCase().contains("nghỉ bù")
                          || h.getName().toLowerCase().contains("nghi bu")
                          || h.getName().toLowerCase().contains("compensatory"))
                .map(h -> h.getDateHoliday().toString())
                .collect(Collectors.toList());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting compensatory days for year " + year, e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Compute regular scheduled work hours for the week containing the given date.
     * This accounts for holidays and compensatory days: regular hours are only
     * counted for working weekdays that are not holidays/compensatory.
     */
    private double computeRegularHoursForWeek(Long userId, LocalDate dateInWeek) {
        try {
            LocalDate weekStart = dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate weekEnd = dateInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            int workingDays = 0;
            LocalDate cursor = weekStart;
            while (!cursor.isAfter(weekEnd)) {
                DayOfWeek dow = cursor.getDayOfWeek();
                // skip weekends
                if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                    boolean isHoliday = isHoliday(cursor.toString());
                    boolean isComp = isCompensatoryDay(cursor.toString());
                    // If it's a regular weekday and not holiday/compensatory, count as working day
                    if (!isHoliday && !isComp) {
                        workingDays++;
                    }
                }
                cursor = cursor.plusDays(1);
            }

            return workingDays * (double) REGULAR_DAILY_HOURS;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error computing regular hours for week: " + dateInWeek, e);
            // Fall back to default regular weekly hours if error
            return (double) REGULAR_WEEKLY_HOURS;
        }
    }

}
