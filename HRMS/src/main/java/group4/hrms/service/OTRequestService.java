package group4.hrms.service;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
                               String otDate, String startTime, String endTime,
      String reason, Boolean employeeConsent) throws SQLException {

        logger.info(String.format("Creating OT request: userId=%d, date=%s, time=%s-%s, hours=%.1f",
                   userId, otDate, startTime, endTime, calculateOTHours(startTime, endTime)));

        try {
            // Validate employee consent
            validateEmployeeConsent(employeeConsent);

            // Calculate OT hours
            Double otHours = calculateOTHours(startTime, endTime);

            // Validate time range
            validateTimeRange(startTime, endTime);

            // Determine OT type and pay multiplier
            String otType = determineOTType(otDate);
            Double payMultiplier = getPayMultiplier(otType);

            // NEW VALIDATIONS - Check for conflicts and limits
            // Check for OT overlap (same day, overlapping time)
            checkOTOverlap(userId, otDate, startTime, endTime);

            // Check for pending OT requests (warning only)
            checkPendingOTRequests(userId, otDate);

            // Validate OT balance (weekly/monthly/annual limits)
            validateOTBalance(userId, otDate, otHours);

            // Check conflict with approved leave requests
            checkConflictWithLeave(userId, otDate);

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
            otRequest.setTitle("OT Request - " + otDate);
            otRequest.setOtDetail(detail);
            otRequest.setCreatedByAccountId(accountId);
            otRequest.setCreatedByUserId(userId);
            otRequest.setDepartmentId(departmentId);
            otRequest.setStatus("PENDING");
            otRequest.setCreatedAt(LocalDateTime.now());
            otRequest.setUpdatedAt(LocalDateTime.now());

            // Save to database
            Request savedRequest = requestDao.save(otRequest);

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
                                          String otDate, String startTime, String endTime,
                                          String reason) throws SQLException {

        logger.info("Manager " + managerAccountId + " creating OT request for employee " + employeeUserId);

        try {
            // Get employee information
            Optional<User> employeeOpt = userDao.findById(employeeUserId);
            if (!employeeOpt.isPresent()) {
                throw new IllegalArgumentException("Employee not found: " + employeeUserId);
            }
            User employee = employeeOpt.get();

            // Calculate OT hours
            Double otHours = calculateOTHours(startTime, endTime);

            // Validate time range
            validateTimeRange(startTime, endTime);

            // Determine OT type and pay multiplier
            String otType = determineOTType(otDate);
            Double payMultiplier = getPayMultiplier(otType);

            // NEW VALIDATIONS - Check for conflicts and limits
            // Check for OT overlap (same day, overlapping time)
            checkOTOverlap(employeeUserId, otDate, startTime, endTime);

            // Check for pending OT requests (warning only)
            checkPendingOTRequests(employeeUserId, otDate);

            // Validate OT balance (weekly/monthly/annual limits)
            validateOTBalance(employeeUserId, otDate, otHours);

            // Check conflict with approved leave requests
            checkConflictWithLeave(employeeUserId, otDate);

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
            otRequest.setTitle("OT Request - " + otDate + " (Created by Manager)");
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
            int currentWeekHours = calculateOTHoursInWeek(userId, now);

            // Calculate current month hours
            int monthlyHours = calculateOTHoursInMonth(userId, now.getYear(), now.getMonthValue());

            // Calculate current year hours
            int annualHours = calculateOTHoursInYear(userId, now.getYear());

            logger.info(String.format("OT balance retrieved: userId=%d, weekly=%d/%d, monthly=%d/%d, annual=%d/%d",
                       userId, currentWeekHours, WEEKLY_LIMIT, monthlyHours, MONTHLY_LIMIT,
                       annualHours, ANNUAL_LIMIT));

            return new OTBalance(currentWeekHours, WEEKLY_LIMIT,
                               monthlyHours, MONTHLY_LIMIT,
                               annualHours, ANNUAL_LIMIT);

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error getting OT balance: userId=%d", userId), e);
            throw new RuntimeException("Error getting OT balance", e);
        }
    }


    private void validateTimeRange(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        // Check if start time is before 06:00 or end time is after 22:00
        if (start.isBefore(DAY_SHIFT_START) || end.isAfter(DAY_SHIFT_END)) {
            throw new IllegalArgumentException(
                "Chỉ được làm OT trong khung giờ 06:00-22:00 (ca ngày)"
            );
        }

        // Check if end time is after start time
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                "Giờ kết thúc phải sau giờ bắt đầu"
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
                    String.format("Tổng giờ trong ngày không được vượt quá %d giờ (hiện tại: %.1f giờ). " +
                        "Giờ làm thường: %dh, OT đã duyệt: %.1fh, OT yêu cầu: %.1fh",
                        DAILY_LIMIT, totalDailyHours, REGULAR_DAILY_HOURS, approvedOTHours, otHours)
                );
            }

            // Check OT hours limit (max 2h on weekdays)
            double totalOTHours = approvedOTHours + otHours;
            if (totalOTHours > maxOTHours) {
                logger.warning(String.format("Weekday OT limit exceeded: userId=%d, date=%s, totalOT=%.1f, maxOT=%.1f",
                              userId, otDate, totalOTHours, maxOTHours));
                throw new IllegalArgumentException(
                    String.format("Ngày thường chỉ được OT tối đa %.0f giờ (đã duyệt: %.1fh, yêu cầu: %.1fh, tổng: %.1fh)",
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
                    String.format("Cuối tuần/Ngày lễ chỉ được OT tối đa %.0f giờ (đã duyệt: %.1fh, yêu cầu: %.1fh, tổng: %.1fh)",
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
                String.format("Tổng giờ trong tuần không được vượt quá %d giờ (hiện tại: %.1f giờ). " +
                    "Giờ thường: %.0fh, OT đã duyệt: %.1fh, OT yêu cầu: %.1fh",
                    WEEKLY_LIMIT, totalWeeklyHours, regularHours, approvedWeeklyOTHours, otHours)
            );
        }

        logger.fine(String.format("Weekly limit validation passed: userId=%d, week=%s to %s",
                   userId, weekStart, weekEnd));
    }


    private void validateEmployeeConsent(Boolean employeeConsent) {
        if (employeeConsent == null || !employeeConsent) {
            throw new IllegalArgumentException(
                "Cần có sự đồng ý của nhân viên cho mọi công việc OT"
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


    private int calculateOTHoursInWeek(Long userId, LocalDate date) {
        // Get week start (Monday) and end (Sunday)
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return calculateApprovedOTHours(userId, weekStart, weekEnd);
    }


    private int calculateOTHoursInMonth(Long userId, int year, int month) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());

        return calculateApprovedOTHours(userId, monthStart, monthEnd);
    }


    private int calculateOTHoursInYear(Long userId, int year) {
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        return calculateApprovedOTHours(userId, yearStart, yearEnd);
    }


    private int calculateApprovedOTHours(Long userId, LocalDate startDate, LocalDate endDate) {
        logger.fine(String.format("Calculating approved OT hours: userId=%d, startDate=%s, endDate=%s",
                   userId, startDate, endDate));

        try {
            List<Request> otRequests = getUserOTRequests(userId);

            double totalHours = 0.0;
            int approvedCount = 0;

            for (Request request : otRequests) {
                // Only count approved requests
                if (!"APPROVED".equals(request.getStatus())) {
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

            int result = (int) Math.ceil(totalHours);
            logger.info(String.format("Calculated approved OT hours: userId=%d, dateRange=%s to %s, hours=%d, requests=%d",
                       userId, startDate, endDate, result, approvedCount));
            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating approved OT hours: userId=%d, startDate=%s, endDate=%s",
                      userId, startDate, endDate), e);
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
                        String.format("Trùng lịch OT! Bạn đã có OT từ %s đến %s vào ngày %s (Trạng thái: %s). " +
                            "Vui lòng chọn khung giờ khác.",
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
    private void validateOTBalance(Long userId, String otDate, Double otHours) {
        logger.fine(String.format("Validating OT balance: userId=%d, date=%s, requestedHours=%.1f",
                   userId, otDate, otHours));

        try {
            LocalDate date = LocalDate.parse(otDate);

            // Calculate current weekly OT hours
            int currentWeekHours = calculateOTHoursInWeek(userId, date);
            logger.fine(String.format("Weekly balance: userId=%d, current=%d, requested=%.1f, limit=%d",
                       userId, currentWeekHours, otHours, WEEKLY_LIMIT));

            if (currentWeekHours + otHours > WEEKLY_LIMIT) {
                logger.warning(String.format("Weekly OT limit exceeded: userId=%d, current=%d, requested=%.1f, limit=%d",
                              userId, currentWeekHours, otHours, WEEKLY_LIMIT));
                throw new IllegalArgumentException(
                    String.format("Vượt quá giới hạn OT tuần! Hiện tại: %d giờ, Yêu cầu: %.1f giờ, " +
                        "Giới hạn: %d giờ. Còn lại: %d giờ.",
                        currentWeekHours, otHours, WEEKLY_LIMIT, WEEKLY_LIMIT - currentWeekHours)
                );
            }

            // Calculate current monthly OT hours
            int monthlyHours = calculateOTHoursInMonth(userId, date.getYear(), date.getMonthValue());
            logger.fine(String.format("Monthly balance: userId=%d, current=%d, requested=%.1f, limit=%d",
                       userId, monthlyHours, otHours, MONTHLY_LIMIT));

            if (monthlyHours + otHours > MONTHLY_LIMIT) {
                logger.warning(String.format("Monthly OT limit exceeded: userId=%d, current=%d, requested=%.1f, limit=%d",
                              userId, monthlyHours, otHours, MONTHLY_LIMIT));
                throw new IllegalArgumentException(
                    String.format("Vượt quá giới hạn OT tháng! Hiện tại: %d giờ, Yêu cầu: %.1f giờ, " +
                        "Giới hạn: %d giờ. Còn lại: %d giờ.",
                        monthlyHours, otHours, MONTHLY_LIMIT, MONTHLY_LIMIT - monthlyHours)
                );
            }

            // Calculate current annual OT hours
            int annualHours = calculateOTHoursInYear(userId, date.getYear());
            logger.fine(String.format("Annual balance: userId=%d, current=%d, requested=%.1f, limit=%d",
                       userId, annualHours, otHours, ANNUAL_LIMIT));

            if (annualHours + otHours > ANNUAL_LIMIT) {
                logger.warning(String.format("Annual OT limit exceeded: userId=%d, current=%d, requested=%.1f, limit=%d",
                              userId, annualHours, otHours, ANNUAL_LIMIT));
                throw new IllegalArgumentException(
                    String.format("Vượt quá giới hạn OT năm! Hiện tại: %d giờ, Yêu cầu: %.1f giờ, " +
                        "Giới hạn: %d giờ. Còn lại: %d giờ.",
                        annualHours, otHours, ANNUAL_LIMIT, ANNUAL_LIMIT - annualHours)
                );
            }

            logger.info(String.format("OT balance validation passed: userId=%d, date=%s, weekly=%d/%d, monthly=%d/%d, annual=%d/%d",
                       userId, otDate, currentWeekHours, WEEKLY_LIMIT, monthlyHours, MONTHLY_LIMIT,
                       annualHours, ANNUAL_LIMIT));

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error validating OT balance: userId=%d, date=%s",
                      userId, otDate), e);
            throw new RuntimeException("Error validating OT balance", e);
        }
    }


    /**
     * Check if OT request conflicts with approved leave requests
     * OT cannot be created on dates when employee has approved leave
     *
     * @param userId User ID
     * @param otDate OT date (yyyy-MM-dd format)
     * @throws IllegalArgumentException if conflict detected
     */
    private void checkConflictWithLeave(Long userId, String otDate) {
        try {
            LocalDate date = LocalDate.parse(otDate);
            LocalDateTime startDateTime = date.atStartOfDay();
            LocalDateTime endDateTime = date.atTime(23, 59, 59);

            // Query APPROVED leave requests that overlap with OT date
            List<Request> leaveRequests = requestDao.findOTRequestsByUserIdAndDateRange(
                userId, startDateTime, endDateTime
            );

            // Check if any leave requests found
            // Note: findOTRequestsByUserIdAndDateRange is misnamed - it actually finds leave requests
            // We need to filter for leave requests, not OT requests
            List<Request> allRequests = requestDao.findByUserId(userId);

            for (Request request : allRequests) {
                // Only check APPROVED leave requests
                if (!"APPROVED".equals(request.getStatus())) {
                    continue;
                }

                // Get leave detail
                group4.hrms.dto.LeaveRequestDetail leaveDetail = request.getLeaveDetail();
                if (leaveDetail == null) {
                    continue; // Not a leave request
                }

                // Parse leave date range
                LocalDate leaveStart = LocalDate.parse(leaveDetail.getStartDate());
                LocalDate leaveEnd = LocalDate.parse(leaveDetail.getEndDate());

                // Check if OT date falls within leave period
                if (!date.isBefore(leaveStart) && !date.isAfter(leaveEnd)) {
                    throw new IllegalArgumentException(
                        String.format("Không thể tạo OT vào ngày %s! Bạn đã có đơn nghỉ phép được duyệt " +
                            "(%s) từ %s đến %s.",
                            otDate, leaveDetail.getLeaveTypeName(),
                            leaveDetail.getStartDate(), leaveDetail.getEndDate())
                    );
                }
            }

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking conflict with leave for user: " + userId, e);
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

}
