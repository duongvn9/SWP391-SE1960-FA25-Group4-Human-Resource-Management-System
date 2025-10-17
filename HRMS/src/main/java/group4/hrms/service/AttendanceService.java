package group4.hrms.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.SystemParametersDao;
import group4.hrms.dto.LeaveRequestDetail;
import group4.hrms.model.AttendanceLog;
import group4.hrms.model.Request;
import group4.hrms.model.SystemParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for handling attendance tracking logic including half-day leave support
 */
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

    private final AttendanceLogDao attendanceLogDao;
    private final RequestDao requestDao;
    private final SystemParametersDao systemParametersDao;
    private final Gson gson;

    // Default configuration values
    private static final int DEFAULT_SHIFT_HOURS = 8;
    private static final int DEFAULT_HALF_DAY_HOURS = 4;
    private static final int DEFAULT_TOLERANCE_MINUTES = 5;
    private static final LocalTime DEFAULT_SHIFT_START = LocalTime.of(8, 0);
    private static final LocalTime DEFAULT_SHIFT_END = LocalTime.of(17, 0);
    private static final LocalTime DEFAULT_LUNCH_START = LocalTime.of(12, 0);
    private static final LocalTime DEFAULT_LUNCH_END = LocalTime.of(13, 0);

    public AttendanceService() {
        this.attendanceLogDao = new AttendanceLogDao();
        this.requestDao = new RequestDao();
        this.systemParametersDao = new SystemParametersDao();
        this.gson = new Gson();
    }

    /**
     * Calculate and set check-in type considering half-day leave
     * Skip late flag for morning half-day leave (8:00-12:00)
     */
    public String calculateCheckInType(Long userId, LocalDate workDate, LocalDateTime checkInTime) {
        try {
            // Check if user has morning half-day leave
            Optional<LeaveRequestDetail> morningLeave = getApprovedHalfDayLeave(userId, workDate, "AM");

            if (morningLeave.isPresent()) {
                // Morning half-day leave exists - skip late flag for morning check-in
                logger.info("User {} has morning half-day leave on {}, skipping late flag", userId, workDate);
                return "NORMAL";
            }

            // No morning leave - apply normal late detection logic
            LocalTime checkIn = checkInTime.toLocalTime();
            LocalTime shiftStart = getShiftStartTime();
            int toleranceMinutes = getToleranceMinutes();

            LocalTime lateThreshold = shiftStart.plusMinutes(toleranceMinutes);

            if (checkIn.isAfter(lateThreshold)) {
                logger.debug("User {} checked in late at {} (threshold: {})", userId, checkIn, lateThreshold);
                return "LATE";
            }

            return "NORMAL";

        } catch (SQLException e) {
            logger.error("Error calculating check-in type for user {} on {}: {}", userId, workDate, e.getMessage(), e);
            return "NORMAL"; // Default to normal on error
        }
    }

    /**
     * Calculate and set check-out type considering half-day leave
     * Skip early flag for afternoon half-day leave (13:00-17:00)
     */
    public String calculateCheckOutType(Long userId, LocalDate workDate, LocalDateTime checkOutTime) {
        try {
            // Check if user has afternoon half-day leave
            Optional<LeaveRequestDetail> afternoonLeave = getApprovedHalfDayLeave(userId, workDate, "PM");

            if (afternoonLeave.isPresent()) {
                // Afternoon half-day leave exists - skip early flag for afternoon check-out
                logger.info("User {} has afternoon half-day leave on {}, skipping early flag", userId, workDate);
                return "NORMAL";
            }

            // No afternoon leave - apply normal early detection logic
            LocalTime checkOut = checkOutTime.toLocalTime();
            LocalTime shiftEnd = getShiftEndTime();
            int toleranceMinutes = getToleranceMinutes();

            LocalTime earlyThreshold = shiftEnd.minusMinutes(toleranceMinutes);

            if (checkOut.isBefore(earlyThreshold)) {
                logger.debug("User {} checked out early at {} (threshold: {})", userId, checkOut, earlyThreshold);
                return "EARLY";
            }

            // Check if overtime
            LocalTime overtimeThreshold = shiftEnd.plusMinutes(toleranceMinutes);
            if (checkOut.isAfter(overtimeThreshold)) {
                return "OVERTIME";
            }

            return "NORMAL";

        } catch (SQLException e) {
            logger.error("Error calculating check-out type for user {} on {}: {}", userId, workDate, e.getMessage(), e);
            return "NORMAL"; // Default to normal on error
        }
    }

    /**
     * Get approved half-day leave for a specific date and period
     */
    private Optional<LeaveRequestDetail> getApprovedHalfDayLeave(Long userId, LocalDate date, String period) throws SQLException {
        List<Request> requests = findByUserAndDateAndStatus(userId, date, "APPROVED");

        for (Request request : requests) {
            if (request.getDetailJson() != null && !request.getDetailJson().trim().isEmpty()) {
                try {
                    LeaveRequestDetail detail = gson.fromJson(request.getDetailJson(), LeaveRequestDetail.class);

                    // Check if it's a half-day leave with matching period
                    if (detail.getIsHalfDay() != null && detail.getIsHalfDay()
                            && period.equals(detail.getHalfDayPeriod())) {
                        return Optional.of(detail);
                    }
                } catch (JsonSyntaxException e) {
                    logger.warn("Failed to parse request detail JSON for request {}: {}", request.getId(), e.getMessage());
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Calculate working hours considering half-day leave
     * Deduct 4 hours for half-day leave (HalfDayHours config)
     */
    public double calculateWorkingHours(Long userId, LocalDate workDate, LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        try {
            if (checkInTime == null || checkOutTime == null) {
                return 0.0;
            }

            // Calculate raw working hours
            long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
            double rawHours = minutes / 60.0;

            // Deduct lunch break (1 hour) if working more than 4 hours
            if (rawHours > 4) {
                rawHours -= 1.0;
            }

            // Check for half-day leave and deduct accordingly
            double halfDayHours = getHalfDayHours();

            Optional<LeaveRequestDetail> morningLeave = getApprovedHalfDayLeave(userId, workDate, "AM");
            if (morningLeave.isPresent()) {
                rawHours -= halfDayHours;
                logger.info("Deducted {} hours for morning half-day leave for user {} on {}", halfDayHours, userId, workDate);
            }

            Optional<LeaveRequestDetail> afternoonLeave = getApprovedHalfDayLeave(userId, workDate, "PM");
            if (afternoonLeave.isPresent()) {
                rawHours -= halfDayHours;
                logger.info("Deducted {} hours for afternoon half-day leave for user {} on {}", halfDayHours, userId, workDate);
            }

            // Ensure non-negative
            return Math.max(0.0, rawHours);

        } catch (SQLException e) {
            logger.error("Error calculating working hours for user {} on {}: {}", userId, workDate, e.getMessage(), e);
            return 0.0;
        }
    }

    /**
     * Check if employee worked during their half-day leave period
     * Returns warning message if violation detected
     */
    public Optional<String> checkWorkDuringLeave(Long userId, LocalDate workDate, LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        try {
            if (checkInTime == null || checkOutTime == null) {
                return Optional.empty();
            }

            LocalTime checkIn = checkInTime.toLocalTime();
            LocalTime checkOut = checkOutTime.toLocalTime();

            // Check morning half-day leave (8:00-12:00)
            Optional<LeaveRequestDetail> morningLeave = getApprovedHalfDayLeave(userId, workDate, "AM");
            if (morningLeave.isPresent()) {
                LocalTime morningStart = LocalTime.of(8, 0);
                LocalTime morningEnd = LocalTime.of(12, 0);

                if (checkIn.isBefore(morningEnd) && checkOut.isAfter(morningStart)) {
                    String warning = String.format("Warning: Employee worked during morning half-day leave period (8:00-12:00) on %s", workDate);
                    logger.warn(warning);
                    return Optional.of(warning);
                }
            }

            // Check afternoon half-day leave (13:00-17:00)
            Optional<LeaveRequestDetail> afternoonLeave = getApprovedHalfDayLeave(userId, workDate, "PM");
            if (afternoonLeave.isPresent()) {
                LocalTime afternoonStart = LocalTime.of(13, 0);
                LocalTime afternoonEnd = LocalTime.of(17, 0);

                if (checkIn.isBefore(afternoonEnd) && checkOut.isAfter(afternoonStart)) {
                    String warning = String.format("Warning: Employee worked during afternoon half-day leave period (13:00-17:00) on %s", workDate);
                    logger.warn(warning);
                    return Optional.of(warning);
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error checking work during leave for user {} on {}: {}", userId, workDate, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get half-day leave display information for a specific date
     * Returns formatted string like "Half Day Leave (Morning 8:00-12:00)" or null if no leave
     */
    public String getHalfDayLeaveDisplayInfo(Long userId, LocalDate workDate) {
        try {
            StringBuilder leaveInfo = new StringBuilder();

            Optional<LeaveRequestDetail> morningLeave = getApprovedHalfDayLeave(userId, workDate, "AM");
            if (morningLeave.isPresent()) {
                if (leaveInfo.length() > 0) {
                    leaveInfo.append("; ");
                }
                leaveInfo.append("Half Day Leave (Morning 8:00-12:00)");
            }

            Optional<LeaveRequestDetail> afternoonLeave = getApprovedHalfDayLeave(userId, workDate, "PM");
            if (afternoonLeave.isPresent()) {
                if (leaveInfo.length() > 0) {
                    leaveInfo.append("; ");
                }
                leaveInfo.append("Half Day Leave (Afternoon 13:00-17:00)");
            }

            return leaveInfo.length() > 0 ? leaveInfo.toString() : null;

        } catch (SQLException e) {
            logger.error("Error getting half-day leave display info for user {} on {}: {}", userId, workDate, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Check if user has any half-day leave on the specified date
     */
    public boolean hasHalfDayLeave(Long userId, LocalDate workDate) {
        try {
            Optional<LeaveRequestDetail> morningLeave = getApprovedHalfDayLeave(userId, workDate, "AM");
            Optional<LeaveRequestDetail> afternoonLeave = getApprovedHalfDayLeave(userId, workDate, "PM");

            return morningLeave.isPresent() || afternoonLeave.isPresent();

        } catch (SQLException e) {
            logger.error("Error checking half-day leave for user {} on {}: {}", userId, workDate, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Process attendance log with half-day leave consideration
     */
    public AttendanceLog processAttendanceLog(AttendanceLog log) {
        if (log == null) {
            return null;
        }

        Long userId = log.getUserId();
        LocalDate workDate = log.getWorkDate();

        // Calculate check-in type
        if (log.getCheckInTime() != null) {
            String checkInType = calculateCheckInType(userId, workDate, log.getCheckInTime());
            log.setCheckInType(checkInType);
        }

        // Calculate check-out type
        if (log.getCheckOutTime() != null) {
            String checkOutType = calculateCheckOutType(userId, workDate, log.getCheckOutTime());
            log.setCheckOutType(checkOutType);
        }

        // Calculate working hours
        if (log.getCheckInTime() != null && log.getCheckOutTime() != null) {
            double workingHours = calculateWorkingHours(userId, workDate, log.getCheckInTime(), log.getCheckOutTime());
            log.setWorkingHours(workingHours);

            // Check for work during leave period
            Optional<String> warning = checkWorkDuringLeave(userId, workDate, log.getCheckInTime(), log.getCheckOutTime());
            if (warning.isPresent()) {
                // Append warning to notes
                String currentNotes = log.getNotes() != null ? log.getNotes() : "";
                log.setNotes(currentNotes.isEmpty() ? warning.get() : currentNotes + "; " + warning.get());
            }
        }

        return log;
    }

    // Configuration getters with fallback to defaults

    private int getShiftHours() {
        try {
            Optional<SystemParameters> param = systemParametersDao.findByKey("GLOBAL", null, "attendance", "ShiftHours");
            if (param.isPresent()) {
                return Integer.parseInt(param.get().getValueJson());
            }
        } catch (Exception e) {
            logger.debug("Using default shift hours: {}", DEFAULT_SHIFT_HOURS);
        }
        return DEFAULT_SHIFT_HOURS;
    }

    private double getHalfDayHours() {
        try {
            Optional<SystemParameters> param = systemParametersDao.findByKey("GLOBAL", null, "attendance", "HalfDayHours");
            if (param.isPresent()) {
                return Double.parseDouble(param.get().getValueJson());
            }
        } catch (Exception e) {
            logger.debug("Using default half-day hours: {}", DEFAULT_HALF_DAY_HOURS);
        }
        return DEFAULT_HALF_DAY_HOURS;
    }

    private int getToleranceMinutes() {
        try {
            Optional<SystemParameters> param = systemParametersDao.findByKey("GLOBAL", null, "attendance", "ToleranceMinutes");
            if (param.isPresent()) {
                return Integer.parseInt(param.get().getValueJson());
            }
        } catch (Exception e) {
            logger.debug("Using default tolerance minutes: {}", DEFAULT_TOLERANCE_MINUTES);
        }
        return DEFAULT_TOLERANCE_MINUTES;
    }

    private LocalTime getShiftStartTime() {
        try {
            Optional<SystemParameters> param = systemParametersDao.findByKey("GLOBAL", null, "attendance", "ShiftStartTime");
            if (param.isPresent()) {
                return LocalTime.parse(param.get().getValueJson().replace("\"", ""));
            }
        } catch (Exception e) {
            logger.debug("Using default shift start time: {}", DEFAULT_SHIFT_START);
        }
        return DEFAULT_SHIFT_START;
    }

    private LocalTime getShiftEndTime() {
        try {
            Optional<SystemParameters> param = systemParametersDao.findByKey("GLOBAL", null, "attendance", "ShiftEndTime");
            if (param.isPresent()) {
                return LocalTime.parse(param.get().getValueJson().replace("\"", ""));
            }
        } catch (Exception e) {
            logger.debug("Using default shift end time: {}", DEFAULT_SHIFT_END);
        }
        return DEFAULT_SHIFT_END;
    }

    /**
     * Helper method to find requests by user, date and status
     */
    private List<Request> findByUserAndDateAndStatus(Long userId, LocalDate date, String status) throws SQLException {
        List<String> statuses = new java.util.ArrayList<>();
        statuses.add(status);
        LocalDateTime dateTime = date.atStartOfDay();
        return requestDao.findByUserIdAndDateRange(userId, dateTime, dateTime, statuses, null);
    }
}
