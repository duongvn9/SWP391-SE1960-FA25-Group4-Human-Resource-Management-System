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
import group4.hrms.util.PaginationUtil;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/attendance/record/HR")
public class AttendanceRecordHRServlet extends HttpServlet {

    private final AttendanceLogDao attendanceLogDao = new AttendanceLogDao();
    private final TimesheetPeriodDao tDAO = new TimesheetPeriodDao();
    private final DepartmentDao dDAO = new DepartmentDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute("accountId");

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int recordsPerPage = 10;
            int currentPage = PaginationUtil.getCurrentPage(req);
            int offset = (currentPage - 1) * recordsPerPage;

            String status = req.getParameter("status");
            String source = req.getParameter("source");
            String periodIdStr = req.getParameter("periodSelect");
            String department = req.getParameter("department");

            LocalDate startDate;
            LocalDate endDate;
            Long periodId = null;

            TimesheetPeriod selectedPeriod = null;

            if (periodIdStr != null && !periodIdStr.isEmpty()) {
                try {
                    periodId = Long.valueOf(periodIdStr);
                    selectedPeriod = tDAO.findById(periodId).orElse(null);
                } catch (NumberFormatException e) {
                    periodId = null;
                }
            }

            if (selectedPeriod == null) {
                selectedPeriod = tDAO.findCurrentPeriod();
                if (selectedPeriod != null) {
                    periodId = selectedPeriod.getId();
                }
            }

            if (selectedPeriod != null) {
                startDate = selectedPeriod.getStartDate();
                endDate = selectedPeriod.getEndDate();
            } else {
                LocalDate now = LocalDate.now();
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
            }

            List<AttendanceLogDto> attendanceList = attendanceLogDao.findByFilter(
                    null,
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
                    null,
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
            req.setAttribute("startDate", startDate.toString());
            req.setAttribute("endDate", endDate.toString());
            req.setAttribute("status", status != null ? status : "");
            req.setAttribute("source", source != null ? source : "");
            req.setAttribute("selectedPeriod", selectedPeriod);
            req.setAttribute("department", department != null ? department : "");
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp").forward(req, resp);

        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordHRServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int recordsPerPage = 10;
            int currentPage = PaginationUtil.getCurrentPage(req);

            String action = req.getParameter("action");
            String exportType = req.getParameter("exportType");
            String employeeIdStr = req.getParameter("employeeId");
            Long employeeId = parseLongSafe(employeeIdStr);
            String employeeKeyword = getParam(req, "employeeKeyword");
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
                employeeKeyword = "";
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

                employeeKeyword = getParam(req, "employeeId");
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
                        employeeKeyword,
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
                    employeeKeyword,
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
                    employeeKeyword,
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
            tDAO.updateLockStatus(periodIdLock, isLocked, userId);
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
            String status = req.getParameter("statusUpdate");
            String source = req.getParameter("sourceUpdate");
            String period = req.getParameter("periodUpdate");

            LocalTime oldCheckIn = LocalTime.parse(req.getParameter("checkInOld"));
            LocalTime oldCheckOut = LocalTime.parse(req.getParameter("checkOutOld"));

            AttendanceLogDto record = new AttendanceLogDto();
            record.setUserId(userId);
            record.setEmployeeName(employeeName);
            record.setDepartment(department);
            record.setDate(LocalDate.parse(dateStr));
            record.setCheckIn(LocalTime.parse(checkInStr));
            record.setCheckOut(LocalTime.parse(checkOutStr));
            // Tự động tính status thay vì dùng input từ form
            String calculatedStatus = AttendanceService.calculateAttendanceStatus(record);
            record.setStatus(calculatedStatus);
            record.setSource(source);
            record.setPeriod(period);

            List<AttendanceLog> logs = AttendanceMapper.convertDtoToEntity(record, oldCheckIn, oldCheckOut);
            System.out.println(logs);
            boolean success = attendanceLogDao.updateAttendanceLogs(logs);

            if (success) {
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
}
