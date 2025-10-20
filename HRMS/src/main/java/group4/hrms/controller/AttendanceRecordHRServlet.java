package group4.hrms.controller;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import group4.hrms.service.AttendanceMapper;
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
        try {
            int recordsPerPage = 10;
            int currentPage = PaginationUtil.getCurrentPage(req);
            int offset = (currentPage - 1) * recordsPerPage;

            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String status = req.getParameter("status");
            String source = req.getParameter("source");
            String periodIdStr = req.getParameter("periodSelect");
            String department = req.getParameter("department");

            LocalDate startDate;
            LocalDate endDate;
            Long periodId = null;

            if (startDateStr == null || startDateStr.isEmpty() || endDateStr == null || endDateStr.isEmpty()) {
                LocalDate now = LocalDate.now();
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
            } else {
                startDate = LocalDate.parse(startDateStr);
                endDate = LocalDate.parse(endDateStr);
            }

            if (periodIdStr != null && !periodIdStr.isEmpty()) {
                try {
                    periodId = Long.valueOf(periodIdStr);
                } catch (NumberFormatException e) {
                    periodId = null;
                }
            }

            List<AttendanceLogDto> attendanceList = attendanceLogDao.findByFilter(
                    null,
                    department,
                    null,
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
                    department,
                    null,
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId
            );
            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("departmentList", dDAO.findAll());
            req.setAttribute("startDate", startDate.toString());
            req.setAttribute("endDate", endDate.toString());
            req.setAttribute("status", status);
            req.setAttribute("source", source);
            req.setAttribute("selectedPeriod", periodId);
            req.setAttribute("department", department);
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

            if ("delete".equalsIgnoreCase(action)) {
                String userIdStr = req.getParameter("userId");
                String dateStr = req.getParameter("date");
                String checkInStr = req.getParameter("checkIn");
                String checkOutStr = req.getParameter("checkOut");

                if (userIdStr != null && !userIdStr.isEmpty() && dateStr != null && !dateStr.isEmpty()) {
                    try {
                        Long userIdD = Long.valueOf(userIdStr);
                        LocalDate date = LocalDate.parse(dateStr);
                        LocalTime checkIn = (checkInStr != null && !checkInStr.isEmpty()) ? LocalTime.parse(checkInStr) : null;
                        LocalTime checkOut = (checkOutStr != null && !checkOutStr.isEmpty()) ? LocalTime.parse(checkOutStr) : null;

                        boolean deleted = attendanceLogDao.deleteAttendance(userIdD, date, checkIn, checkOut);

                        if (deleted) {
                            req.setAttribute("message", "Deleted attendance record successfully.");
                        } else {
                            req.setAttribute("error", "Attendance record not found or could not be deleted.");
                        }

                    } catch (NumberFormatException | DateTimeParseException e) {
                        req.setAttribute("error", "Invalid input format.");
                    }
                } else {
                    req.setAttribute("error", "User ID or date is missing.");
                }
            }

            if ("update".equalsIgnoreCase(action)) {
                Long userIdStr = Long.valueOf(req.getParameter("userIdUpdate"));
                String employeeNameStr = req.getParameter("employeeNameUpdate");
                String departmentStr = req.getParameter("departmentUpdate");
                String dateStr = req.getParameter("dateUpdate");
                String checkInStr = req.getParameter("checkInUpdate");
                String checkOutStr = req.getParameter("checkOutUpdate");
                String statusStr = req.getParameter("statusUpdate");
                String sourceStr = req.getParameter("sourceUpdate");
                String periodStr = req.getParameter("periodUpdate");

                AttendanceLogDto record = new AttendanceLogDto();
                record.setUserId(userIdStr);
                record.setEmployeeName(employeeNameStr);
                record.setDepartment(departmentStr);
                record.setDate(LocalDate.parse(dateStr));
                record.setCheckIn(LocalTime.parse(checkInStr));
                record.setCheckOut(LocalTime.parse(checkOutStr));
                record.setStatus(statusStr);
                record.setSource(sourceStr);
                record.setPeriod(periodStr);

                List<AttendanceLog> logs = AttendanceMapper.convertDtoToEntity(record);
                boolean success = attendanceLogDao.saveAttendanceLogs(logs);

                if (success) {
                    req.setAttribute("message", "Record updated successfully!");
                } else {
                    req.setAttribute("error", "Failed to update record.");
                }
            }

            if ("reset".equalsIgnoreCase(action)) {
                employeeKeyword = "";
                department = "";
                status = "";
                source = "";
                periodId = null;

                LocalDate now = LocalDate.now();
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());

                currentPage = 1;
            }

            if (exportType != null && !exportType.isEmpty()) {
                List<AttendanceLogDto> filteredRecords = attendanceLogDao.findByFilter(
                        null,
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
                    null,
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
                    null,
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

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("departmentList", dDAO.findAll());
            req.setAttribute("employeeKeyword", employeeKeyword);
            req.setAttribute("department", department);
            req.setAttribute("startDate", (startDate != null) ? startDate.toString() : "");
            req.setAttribute("endDate", (endDate != null) ? endDate.toString() : "");
            req.setAttribute("status", status);
            req.setAttribute("source", source);
            req.setAttribute("selectedPeriod", periodId);
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp").forward(req, resp);

        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordHRServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
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
