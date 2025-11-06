package group4.hrms.controller;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import group4.hrms.model.TimesheetPeriod;
import group4.hrms.model.User;
import group4.hrms.service.AttendanceMapper;
import group4.hrms.service.AttendanceService;
import group4.hrms.service.ExportService;
import group4.hrms.service.PayslipDirtyFlagService;
import group4.hrms.util.PaginationUtil;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import group4.hrms.util.DatabaseUtil;

@WebServlet("/attendance/record/HR")
public class AttendanceRecordHRServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AttendanceRecordHRServlet.class.getName());
    private final AttendanceLogDao attendanceLogDao = new AttendanceLogDao();
    private final TimesheetPeriodDao tDAO = new TimesheetPeriodDao();
    private final DepartmentDao dDAO = new DepartmentDao();
    private final PayslipDirtyFlagService dirtyFlagService = new PayslipDirtyFlagService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("accountId");

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            tDAO.autoLockExpiredPeriods();
            int recordsPerPage = 10;
            int currentPage = PaginationUtil.getCurrentPage(req);
            int offset = (currentPage - 1) * recordsPerPage;

            String employeeIdStr = req.getParameter("employeeId");
            Long employeeId = parseLongSafe(employeeIdStr);
            String department = getParam(req, "department");
            String startDateStr = getParam(req, "startDate");
            String endDateStr = getParam(req, "endDate");
            String status = getParam(req, "status");
            String source = getParam(req, "source");
            String periodIdStr = getParam(req, "periodSelect");

            LocalDate startDate = parseDate(startDateStr);
            LocalDate endDate = parseDate(endDateStr);
            Long periodId = parseLongSafe(periodIdStr);

            TimesheetPeriod selectedPeriod = null;

            boolean hasAnyParameter = req.getParameter("employeeId") != null
                    || req.getParameter("department") != null
                    || req.getParameter("startDate") != null
                    || req.getParameter("endDate") != null
                    || req.getParameter("status") != null
                    || req.getParameter("source") != null
                    || req.getParameter("periodSelect") != null;
            if (!hasAnyParameter) {
                selectedPeriod = tDAO.findCurrentPeriod();
                if (selectedPeriod != null) {
                    periodId = selectedPeriod.getId();
                    startDate = selectedPeriod.getStartDate();
                    endDate = selectedPeriod.getEndDate();
                } else {
                    LocalDate now = LocalDate.now();
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                }
            } else {
                if (periodId != null) {
                    selectedPeriod = tDAO.findById(periodId).orElse(null);
                }

                if (startDate == null && endDate == null && selectedPeriod != null) {
                    startDate = selectedPeriod.getStartDate();
                    endDate = selectedPeriod.getEndDate();
                }
            }

            List<AttendanceLogDto> attendanceList = attendanceLogDao.findByFilter(
                    employeeId,
                    null,
                    department,
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId,
                    recordsPerPage,
                    offset,
                    true
            );

            int totalRecords = attendanceLogDao.countByFilter(
                    employeeId,
                    null,
                    department,
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId
            );

            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

            UserDao uDao = new UserDao();
            List<User> uList = uDao.findAll();
            req.setAttribute("uList", uList);
            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("departmentList", dDAO.findAll());
            req.setAttribute("employeeId", employeeId);
            req.setAttribute("startDate", (startDate != null) ? startDate.toString() : "");
            req.setAttribute("endDate", (endDate != null) ? endDate.toString() : "");
            req.setAttribute("status", status);
            req.setAttribute("source", source);
            req.setAttribute("selectedPeriod", selectedPeriod);
            req.setAttribute("department", department);
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);

            if (selectedPeriod != null) {
                boolean canToggle = tDAO.canToggleLockStatus(selectedPeriod.getId());
                boolean isPermanentlyLocked = tDAO.isPermanentlyLocked(selectedPeriod.getId());
                req.setAttribute("canToggleLock", canToggle);
                req.setAttribute("isPermanentlyLocked", isPermanentlyLocked);
            }

            if (selectedPeriod != null) {
                boolean canToggle = tDAO.canToggleLockStatus(selectedPeriod.getId());
                boolean isPermanentlyLocked = tDAO.isPermanentlyLocked(selectedPeriod.getId());
                req.setAttribute("canToggleLock", canToggle);
                req.setAttribute("isPermanentlyLocked", isPermanentlyLocked);
            }

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp").forward(req, resp);

        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordHRServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            tDAO.autoLockExpiredPeriods();
            int recordsPerPage = 10;
            int currentPage = PaginationUtil.getCurrentPage(req);

            String action = req.getParameter("action");
            String exportType = req.getParameter("exportType");
            String employeeIdStr = req.getParameter("employeeId");
            Long employeeId = parseLongSafe(employeeIdStr);
            String department = getParam(req, "department");
            String startDateStr = getParam(req, "startDate");
            String endDateStr = getParam(req, "endDate");
            String status = getParam(req, "status");
            String source = getParam(req, "source");
            String periodIdStr = getParam(req, "periodSelect");

            LocalDate startDate = parseDate(startDateStr);
            LocalDate endDate = parseDate(endDateStr);
            Long periodId = parseLongSafe(periodIdStr);

            if ("reset".equalsIgnoreCase(action)) {
                employeeId = null;
                department = "";
                status = "";
                source = "";

                TimesheetPeriod currentPeriod = tDAO.findCurrentPeriod();
                if (currentPeriod != null) {
                    periodId = currentPeriod.getId();
                    startDate = currentPeriod.getStartDate();
                    endDate = currentPeriod.getEndDate();
                } else {
                    periodId = null;
                    LocalDate now = LocalDate.now();
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                }

                currentPage = 1;
            } else if ("filter".equalsIgnoreCase(action)) {
                currentPage = 1;
            } else if ("delete".equalsIgnoreCase(action)) {
                handleDelete(req);
            } else if ("update".equalsIgnoreCase(action)) {
                handleUpdate(req);
            } else if ("toggleLock".equalsIgnoreCase(action)) {
                handleLockPeriod(req);
                employeeIdStr = req.getParameter("employeeId");
                employeeId = parseLongSafe(employeeIdStr);
                department = getParam(req, "department");
                startDateStr = getParam(req, "startDate");
                endDateStr = getParam(req, "endDate");
                status = getParam(req, "status");
                source = getParam(req, "source");
                periodIdStr = getParam(req, "periodSelect");

                startDate = parseDate(startDateStr);
                endDate = parseDate(endDateStr);
                periodId = parseLongSafe(periodIdStr);
            }

            if (exportType != null && !exportType.isEmpty()) {
                List<AttendanceLogDto> filteredRecords = attendanceLogDao.findByFilter(
                        employeeId,
                        null,
                        department,
                        startDate,
                        endDate,
                        status,
                        source,
                        periodId,
                        Integer.MAX_VALUE,
                        0,
                        false
                );
                ExportService.AttendanceRecordExport(resp, exportType, filteredRecords);
                return;
            }

            int totalRecords = attendanceLogDao.countByFilter(
                    employeeId,
                    null,
                    department,
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId
            );

            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);
            if (currentPage > totalPages) {
                currentPage = totalPages > 0 ? totalPages : 1;
            }
            int offset = (currentPage - 1) * recordsPerPage;

            List<AttendanceLogDto> attendanceList = attendanceLogDao.findByFilter(
                    employeeId,
                    null,
                    department,
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId,
                    recordsPerPage,
                    offset,
                    true
            );

            UserDao uDao = new UserDao();
            List<User> uList = uDao.findAll();
            req.setAttribute("uList", uList);
            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("departmentList", dDAO.findAll());
            req.setAttribute("employeeId", employeeId);
            req.setAttribute("department", department);
            req.setAttribute("startDate", (startDate != null) ? startDate.toString() : "");
            req.setAttribute("endDate", (endDate != null) ? endDate.toString() : "");
            req.setAttribute("status", status);
            req.setAttribute("source", source);

            TimesheetPeriod selectedPeriod = (periodId != null)
                    ? tDAO.findById(periodId).orElse(null)
                    : null;
            req.setAttribute("selectedPeriod", selectedPeriod);

            if (selectedPeriod != null) {
                boolean canToggle = tDAO.canToggleLockStatus(selectedPeriod.getId());
                boolean isPermanentlyLocked = tDAO.isPermanentlyLocked(selectedPeriod.getId());
                req.setAttribute("canToggleLock", canToggle);
                req.setAttribute("isPermanentlyLocked", isPermanentlyLocked);
            }

            if (selectedPeriod != null) {
                boolean canToggle = tDAO.canToggleLockStatus(selectedPeriod.getId());
                boolean isPermanentlyLocked = tDAO.isPermanentlyLocked(selectedPeriod.getId());
                req.setAttribute("canToggleLock", canToggle);
                req.setAttribute("isPermanentlyLocked", isPermanentlyLocked);
            }

            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp")
                    .forward(req, resp);

        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordHRServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }

    private void handleLockPeriod(HttpServletRequest req) throws SQLException {
        String periodIdLockStr = getParam(req, "periodId");
        String lockedStr = getParam(req, "locked");

        Long periodIdLock = parseLongSafe(periodIdLockStr);
        boolean isLocked = Boolean.parseBoolean(lockedStr);

        Long userId = (Long) req.getSession().getAttribute("accountId");

        if (periodIdLock != null && userId != null) {
            try {
                if (!tDAO.canToggleLockStatus(periodIdLock)) {
                    req.setAttribute("error", "Cannot change lock status: This period has been permanently locked. Periods are automatically locked after 7 days of the following month.");
                    return;
                }

                tDAO.updateLockStatus(periodIdLock, isLocked, userId);

                String action = isLocked ? "locked" : "unlocked";
                req.setAttribute("message", "Period has been " + action + " successfully.");
            } catch (IllegalStateException e) {
                req.setAttribute("error", e.getMessage());
            }
        }
    }

    private void handleDelete(HttpServletRequest req) throws SQLException {
        String userIdStr = req.getParameter("userIdEdit");
        String dateStr = req.getParameter("dateEdit");
        String checkInStr = req.getParameter("checkInEdit");
        String checkOutStr = req.getParameter("checkOutEdit");

        if (userIdStr == null || dateStr == null || userIdStr.isEmpty() || dateStr.isEmpty()) {
            req.setAttribute("error", "User ID or date is missing.");
            return;
        }

        try {
            Long userId = Long.valueOf(userIdStr);
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime checkIn = (checkInStr != null && !checkInStr.isEmpty()) ? LocalTime.parse(checkInStr) : null;
            LocalTime checkOut = (checkOutStr != null && !checkOutStr.isEmpty()) ? LocalTime.parse(checkOutStr) : null;

            boolean deleted = attendanceLogDao.deleteAttendance(userId, date, checkIn, checkOut);

            if (deleted) {
                // --- ✅ Mark affected payslip as dirty ---
                try {
                    String reason = String.format("Attendance deleted via HR attendance record (date: %s)", dateStr);
                    dirtyFlagService.markDirtyForAttendanceChange(userId, date, reason);
                    logger.info(String.format("Marked payslip dirty for user %d after attendance deletion", userId));
                } catch (Exception e) {
                    logger.log(Level.WARNING,
                            String.format("Failed to mark payslip dirty for user %d: %s", userId, e.getMessage()), e);
                    // Don't fail the delete operation if marking dirty fails
                }

                req.setAttribute("message", "Deleted attendance record successfully.");
            } else {
                req.setAttribute("error", "Attendance record not found or could not be deleted.");
            }

        } catch (NumberFormatException | DateTimeParseException e) {
            req.setAttribute("error", "Invalid input format.");
        }
    }

    private void handleUpdate(HttpServletRequest req) throws SQLException {
        try {
            Long userId = Long.valueOf(req.getParameter("userIdUpdate"));
            String employeeName = req.getParameter("employeeNameUpdate");
            String department = req.getParameter("departmentUpdate");
            String dateStr = req.getParameter("dateUpdate");
            String checkInStr = req.getParameter("checkInUpdate");
            String checkOutStr = req.getParameter("checkOutUpdate");
            String source = req.getParameter("sourceUpdate");
            String period = req.getParameter("periodUpdate");

            String oldCheckInStr = req.getParameter("checkInOld");
            String oldCheckOutStr = req.getParameter("checkOutOld");
            LocalTime oldCheckIn = (oldCheckInStr != null && !oldCheckInStr.isEmpty()) ? LocalTime.parse(oldCheckInStr) : null;
            LocalTime oldCheckOut = (oldCheckOutStr != null && !oldCheckOutStr.isEmpty()) ? LocalTime.parse(oldCheckOutStr) : null;

            AttendanceLogDto record = new AttendanceLogDto();
            record.setUserId(userId);
            record.setEmployeeName(employeeName);
            record.setDepartment(department);
            record.setDate(LocalDate.parse(dateStr));
            record.setCheckIn((checkInStr != null && !checkInStr.isEmpty()) ? LocalTime.parse(checkInStr) : null);
            record.setCheckOut((checkOutStr != null && !checkOutStr.isEmpty()) ? LocalTime.parse(checkOutStr) : null);
            String calculatedStatus = AttendanceService.calculateAttendanceStatus(record);
            record.setStatus(calculatedStatus);
            record.setSource(source);
            record.setPeriod(period);

            // Validate trước khi update
            logger.info(String.format("Validating update: userId=%d, date=%s, oldIn=%s, oldOut=%s, newIn=%s, newOut=%s", 
                    userId, record.getDate(), oldCheckIn, oldCheckOut, record.getCheckIn(), record.getCheckOut()));
            
            String validationError = validateAttendanceUpdate(userId, record.getDate(), 
                    oldCheckIn, oldCheckOut, 
                    record.getCheckIn(), record.getCheckOut());
            
            if (validationError != null) {
                logger.warning("Validation failed: " + validationError);
                req.setAttribute("error", validationError);
                return;
            }
            
            boolean success = updateAttendanceRecord(userId, record.getDate(), 
                    oldCheckIn, oldCheckOut, 
                    record.getCheckIn(), record.getCheckOut(), 
                    record.getSource(), record.getStatus(), record.getPeriod());

            if (success) {
                // --- ✅ Mark affected payslip as dirty ---
                try {
                    String reason = String.format("Attendance updated via HR attendance record (date: %s)", dateStr);
                    dirtyFlagService.markDirtyForAttendanceChange(userId, record.getDate(), reason);
                    logger.info(String.format("Marked payslip dirty for user %d after attendance update", userId));
                } catch (Exception e) {
                    logger.log(Level.WARNING,
                            String.format("Failed to mark payslip dirty for user %d: %s", userId, e.getMessage()), e);
                    // Don't fail the update operation if marking dirty fails
                }

                req.setAttribute("message", "Record updated successfully!");
            } else {
                req.setAttribute("error", "Failed to update record.");
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid update input.");
        }
    }

    private String getParam(HttpServletRequest req, String name) {
        return req.getParameter(name) != null ? req.getParameter(name) : "";
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return LocalDate.parse(s);
    }

    private Long parseLongSafe(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean updateAttendanceRecord(Long userId, LocalDate date, 
            LocalTime oldCheckIn, LocalTime oldCheckOut,
            LocalTime newCheckIn, LocalTime newCheckOut,
            String source, String status, String period) throws SQLException {
        
        TimesheetPeriodDao periodDao = new TimesheetPeriodDao();
        Long periodId = periodDao.findIdByName(period).orElse(null);
        
        boolean success = true;
        
        // Xử lý Check-In
        if (oldCheckIn != null && newCheckIn != null) {
            // Update existing check-in
            AttendanceLog logIn = new AttendanceLog();
            logIn.setUserId(userId);
            logIn.setCheckType("IN");
            logIn.setCheckedAt(LocalDateTime.of(date, oldCheckIn));
            logIn.setCheckedAtNew(LocalDateTime.of(date, newCheckIn));
            logIn.setSource(source);
            logIn.setNote(status);
            logIn.setPeriodId(periodId);
            
            try {
                attendanceLogDao.update(logIn);
            } catch (SQLException e) {
                success = false;
                logger.log(Level.SEVERE, "Failed to update check-in", e);
            }
        } else if (oldCheckIn == null && newCheckIn != null) {
            // Insert new check-in
            AttendanceLog logIn = new AttendanceLog();
            logIn.setUserId(userId);
            logIn.setCheckType("IN");
            logIn.setCheckedAt(LocalDateTime.of(date, newCheckIn));
            logIn.setSource(source);
            logIn.setNote(status);
            logIn.setPeriodId(periodId);
            
            try {
                attendanceLogDao.save(logIn);
            } catch (SQLException e) {
                success = false;
                logger.log(Level.SEVERE, "Failed to insert check-in", e);
            }
        } else if (oldCheckIn != null && newCheckIn == null) {
            // Delete existing check-in
            try {
                attendanceLogDao.deleteAttendance(userId, date, oldCheckIn, null);
            } catch (SQLException e) {
                success = false;
                logger.log(Level.SEVERE, "Failed to delete check-in", e);
            }
        }
        
        // Xử lý Check-Out
        if (oldCheckOut != null && newCheckOut != null) {
            // Update existing check-out
            AttendanceLog logOut = new AttendanceLog();
            logOut.setUserId(userId);
            logOut.setCheckType("OUT");
            logOut.setCheckedAt(LocalDateTime.of(date, oldCheckOut));
            logOut.setCheckedAtNew(LocalDateTime.of(date, newCheckOut));
            logOut.setSource(source);
            logOut.setNote(status);
            logOut.setPeriodId(periodId);
            
            try {
                attendanceLogDao.update(logOut);
            } catch (SQLException e) {
                success = false;
                logger.log(Level.SEVERE, "Failed to update check-out", e);
            }
        } else if (oldCheckOut == null && newCheckOut != null) {
            // Insert new check-out
            AttendanceLog logOut = new AttendanceLog();
            logOut.setUserId(userId);
            logOut.setCheckType("OUT");
            logOut.setCheckedAt(LocalDateTime.of(date, newCheckOut));
            logOut.setSource(source);
            logOut.setNote(status);
            logOut.setPeriodId(periodId);
            
            try {
                attendanceLogDao.save(logOut);
            } catch (SQLException e) {
                success = false;
                logger.log(Level.SEVERE, "Failed to insert check-out", e);
            }
        } else if (oldCheckOut != null && newCheckOut == null) {
            // Delete existing check-out
            try {
                attendanceLogDao.deleteAttendance(userId, date, null, oldCheckOut);
            } catch (SQLException e) {
                success = false;
                logger.log(Level.SEVERE, "Failed to delete check-out", e);
            }
        }
        
        return success;
    }

    private String validateAttendanceUpdate(Long userId, LocalDate date, 
            LocalTime oldCheckIn, LocalTime oldCheckOut,
            LocalTime newCheckIn, LocalTime newCheckOut) throws SQLException {
        
        // Basic validation
        if (userId == null || date == null) {
            return "User ID and date are required";
        }
        
        if (newCheckIn == null && newCheckOut == null) {
            return "At least one of check-in or check-out time is required";
        }
        
        // Check future date
        if (date.isAfter(LocalDate.now())) {
            return "Date cannot be in the future";
        }
        
        // Time range validation
        LocalTime MIN_TIME = LocalTime.of(6, 0);
        LocalTime MAX_TIME = LocalTime.of(23, 59);
        
        if (newCheckIn != null) {
            if (newCheckIn.isBefore(MIN_TIME) || newCheckIn.isAfter(MAX_TIME)) {
                return "Check-in time must be between 06:00 and 23:59";
            }
        }
        
        if (newCheckOut != null) {
            if (newCheckOut.isBefore(MIN_TIME) || newCheckOut.isAfter(MAX_TIME)) {
                return "Check-out time must be between 06:00 and 23:59";
            }
        }
        
        // Logical order: check-in < check-out
        if (newCheckIn != null && newCheckOut != null) {
            if (!newCheckIn.isBefore(newCheckOut)) {
                return "Check-in time must be earlier than check-out time";
            }
        }
        
        // Không cho phép edit từ record đầy đủ thành record thiếu
        boolean hadBothOldTimes = (oldCheckIn != null && oldCheckOut != null);
        boolean hasBothNewTimes = (newCheckIn != null && newCheckOut != null);
        
        logger.info(String.format("Completeness check: hadBoth=%s, hasBoth=%s", hadBothOldTimes, hasBothNewTimes));
        
        if (hadBothOldTimes && !hasBothNewTimes) {
            if (newCheckIn == null && newCheckOut == null) {
                return "Cannot remove both check-in and check-out times from a complete attendance record";
            } else if (newCheckIn == null) {
                return "Cannot remove check-in time from a complete attendance record";
            } else if (newCheckOut == null) {
                return "Cannot remove check-out time from a complete attendance record";
            }
        }
        
        // Check if user exists
        try (Connection conn = DatabaseUtil.getConnection()) {
            String checkUserSql = "SELECT COUNT(1) FROM users WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkUserSql)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        return "Employee does not exist in the system";
                    }
                }
            }
            
            // Check if period is locked
            String checkPeriodSql = """
                SELECT COALESCE(tp.is_locked, FALSE)
                FROM timesheet_periods tp
                WHERE ? BETWEEN tp.date_start AND tp.date_end
                """;
            try (PreparedStatement stmt = conn.prepareStatement(checkPeriodSql)) {
                stmt.setDate(1, java.sql.Date.valueOf(date));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getBoolean(1)) {
                        return "Timesheet period is locked for this date";
                    }
                }
            }
            
            // Check conflicts with existing records (excluding old records being updated)
            return validateTimeConflicts(conn, userId, date, oldCheckIn, oldCheckOut, newCheckIn, newCheckOut);
        }
    }
    
    private String validateTimeConflicts(Connection conn, Long userId, LocalDate date,
            LocalTime oldCheckIn, LocalTime oldCheckOut,
            LocalTime newCheckIn, LocalTime newCheckOut) throws SQLException {
        
        LocalDateTime newCheckInDT = newCheckIn != null ? date.atTime(newCheckIn) : null;
        LocalDateTime newCheckOutDT = null;
        if (newCheckOut != null) {
            if (newCheckIn != null && newCheckOut.isBefore(newCheckIn)) {
                newCheckOutDT = date.plusDays(1).atTime(newCheckOut);
            } else {
                newCheckOutDT = date.atTime(newCheckOut);
            }
        }
        
        // Lấy tất cả log của user trong cùng ngày
        String sql = "SELECT checked_at, check_type FROM attendance_logs WHERE user_id = ? AND DATE(checked_at) = ?";
        
        // Nếu đang update existing records, loại trừ chúng khỏi conflict check
        List<LocalDateTime> excludeTimestamps = new ArrayList<>();
        if (oldCheckIn != null) {
            excludeTimestamps.add(date.atTime(oldCheckIn));
        }
        if (oldCheckOut != null) {
            excludeTimestamps.add(date.atTime(oldCheckOut));
        }
        
        if (!excludeTimestamps.isEmpty()) {
            sql += " AND checked_at NOT IN (" + 
                   excludeTimestamps.stream().map(dt -> "?").collect(java.util.stream.Collectors.joining(",")) + 
                   ")";
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            
            // Set exclude timestamps
            int paramIndex = 3;
            for (LocalDateTime excludeTs : excludeTimestamps) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(excludeTs));
            }
            
            List<LocalDateTime> ins = new ArrayList<>();
            List<LocalDateTime> outs = new ArrayList<>();
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("check_type");
                    Timestamp ts = rs.getTimestamp("checked_at");
                    if (ts == null || type == null) {
                        continue;
                    }
                    
                    if ("IN".equalsIgnoreCase(type)) {
                        ins.add(ts.toLocalDateTime());
                    } else if ("OUT".equalsIgnoreCase(type)) {
                        outs.add(ts.toLocalDateTime());
                    }
                }
            }
            
            logger.info(String.format("Found existing records - INs: %s, OUTs: %s", ins, outs));
            
            // Kiểm tra trực tiếp với từng existing record thay vì ghép cặp phức tạp
            
            // 1. Kiểm tra duplicate exact times
            if (newCheckInDT != null) {
                for (LocalDateTime existingIn : ins) {
                    if (newCheckInDT.equals(existingIn)) {
                        return String.format("Check-in time %s already exists", newCheckIn.toString());
                    }
                }
            }
            
            if (newCheckOutDT != null) {
                for (LocalDateTime existingOut : outs) {
                    if (newCheckOutDT.equals(existingOut)) {
                        return String.format("Check-out time %s already exists", newCheckOut.toString());
                    }
                }
            }
            
            // 2. Nếu đang thêm cả check-in và check-out, kiểm tra overlap với existing records
            if (newCheckInDT != null && newCheckOutDT != null) {
                // Sắp xếp và ghép cặp existing records
                ins.sort(Comparator.naturalOrder());
                outs.sort(Comparator.naturalOrder());
                
                List<Pair<LocalDateTime, LocalDateTime>> existingPairs = new ArrayList<>();
                List<LocalDateTime> remainingIns = new ArrayList<>(ins);
                List<LocalDateTime> remainingOuts = new ArrayList<>(outs);
                
                while (!remainingIns.isEmpty()) {
                    LocalDateTime in = remainingIns.remove(0);
                    LocalDateTime out = null;
                    
                    for (Iterator<LocalDateTime> it = remainingOuts.iterator(); it.hasNext();) {
                        LocalDateTime candidateOut = it.next();
                        if (!candidateOut.isBefore(in)) {
                            out = candidateOut;
                            it.remove();
                            break;
                        }
                    }
                    
                    if (out == null) {
                        out = in.plusHours(8);
                    }
                    
                    existingPairs.add(Pair.of(in, out));
                }
                
                // Kiểm tra overlap với existing pairs
                for (Pair<LocalDateTime, LocalDateTime> pair : existingPairs) {
                    LocalDateTime existIn = pair.getLeft();
                    LocalDateTime existOut = pair.getRight();
                    
                    // Overlap nếu: newStart < existEnd && newEnd > existStart
                    if (newCheckInDT.isBefore(existOut) && newCheckOutDT.isAfter(existIn)) {
                        return String.format("Attendance period (%s - %s) overlaps with existing record (%s - %s)", 
                            newCheckIn.toString(), newCheckOut.toString(),
                            existIn.toLocalTime().toString(), existOut.toLocalTime().toString());
                    }
                }
                
                // Kiểm tra với unpaired records
                for (LocalDateTime extraIn : remainingIns) {
                    if (newCheckInDT.isBefore(extraIn) && newCheckOutDT.isAfter(extraIn)) {
                        return String.format("Attendance period (%s - %s) conflicts with existing check-in at %s", 
                            newCheckIn.toString(), newCheckOut.toString(), extraIn.toLocalTime().toString());
                    }
                }
                
                for (LocalDateTime extraOut : remainingOuts) {
                    if (newCheckInDT.isBefore(extraOut) && newCheckOutDT.isAfter(extraOut)) {
                        return String.format("Attendance period (%s - %s) conflicts with existing check-out at %s", 
                            newCheckIn.toString(), newCheckOut.toString(), extraOut.toLocalTime().toString());
                    }
                }
            }
            
            // 3. Nếu chỉ thêm check-in hoặc check-out, kiểm tra logic conflicts
            else if (newCheckInDT != null) {
                // Thêm check-in: không được có check-out nào trước nó (unpaired)
                for (LocalDateTime existingOut : outs) {
                    if (existingOut.isBefore(newCheckInDT)) {
                        return String.format("Cannot add check-in at %s: there is an unpaired check-out at %s before it", 
                            newCheckIn.toString(), existingOut.toLocalTime().toString());
                    }
                }
            }
            else if (newCheckOutDT != null) {
                // Thêm check-out: phải có ít nhất một check-in trước nó
                boolean hasCheckInBefore = false;
                for (LocalDateTime existingIn : ins) {
                    if (existingIn.isBefore(newCheckOutDT)) {
                        hasCheckInBefore = true;
                        break;
                    }
                }
                if (!hasCheckInBefore) {
                    return String.format("Cannot add check-out at %s: no check-in found before it", 
                        newCheckOut.toString());
                }
            }
        }
        
        return null; // No conflicts
    }
}
