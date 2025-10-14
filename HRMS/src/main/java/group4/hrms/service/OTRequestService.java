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

        logger.info("Creating OT request for user " + userId + " on date " + otDate);

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

            // Validate daily and weekly limits
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

            logger.info("Created OT request with ID " + savedRequest.getId());
            return savedRequest.getId();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating OT request", e);
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

            // Validate daily and weekly limits for the employee
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
        try {
            LocalDate now = LocalDate.now();

            // Calculate current week hours (Monday to Sunday)
            int currentWeekHours = calculateOTHoursInWeek(userId, now);

            // Calculate current month hours
            int monthlyHours = calculateOTHoursInMonth(userId, now.getYear(), now.getMonthValue());

            // Calculate current year hours
            int annualHours = calculateOTHoursInYear(userId, now.getYear());

            return new OTBalance(currentWeekHours, WEEKLY_LIMIT,
                               monthlyHours, MONTHLY_LIMIT,
                               annualHours, ANNUAL_LIMIT);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting OT balance for user: " + userId, e);
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

        // Calculate total hours: regular (8h) + approved OT + requested OT
        double totalDailyHours = REGULAR_DAILY_HOURS + approvedOTHours + otHours;

        if (totalDailyHours > DAILY_LIMIT) {
            throw new IllegalArgumentException(
                String.format("Tổng giờ trong ngày không được vượt quá %d giờ (hiện tại: %.1f giờ)",
                    DAILY_LIMIT, totalDailyHours)
            );
        }
    }


    private void validateWeeklyLimit(Long userId, String otDate, Double otHours) throws SQLException {
        LocalDate date = LocalDate.parse(otDate);

        // Get week start (Monday) and end (Sunday)
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Get approved OT hours in the week
        List<Request> otRequests = getUserOTRequests(userId);

        double approvedWeeklyOTHours = 0.0;
        for (Request request : otRequests) {
            if (!"APPROVED".equals(request.getStatus())) {
                continue;
            }

            OTRequestDetail detail = request.getOtDetail();
            if (detail != null) {
                LocalDate requestDate = LocalDate.parse(detail.getOtDate());
                if (!requestDate.isBefore(weekStart) && !requestDate.isAfter(weekEnd)) {
                    approvedWeeklyOTHours += detail.getOtHours();
                }
            }
        }

        // Calculate total weekly hours: regular (40h) + approved OT + requested OT
        double totalWeeklyHours = REGULAR_WEEKLY_HOURS + approvedWeeklyOTHours + otHours;

        if (totalWeeklyHours > WEEKLY_LIMIT) {
            throw new IllegalArgumentException(
                String.format("Tổng giờ trong tuần không được vượt quá %d giờ (hiện tại: %.1f giờ). " +
                    "Giờ thường: %dh, OT đã duyệt: %.1fh, OT yêu cầu: %.1fh",
                    WEEKLY_LIMIT, totalWeeklyHours, REGULAR_WEEKLY_HOURS, approvedWeeklyOTHours, otHours)
            );
        }
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
        try {
            List<Request> otRequests = getUserOTRequests(userId);

            double totalHours = 0.0;
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
                }
            }

            return (int) Math.ceil(totalHours);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error calculating approved OT hours", e);
            return 0;
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
