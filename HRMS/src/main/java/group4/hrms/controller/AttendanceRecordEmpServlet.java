package group4.hrms.controller;

import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.service.ExportService;
import group4.hrms.util.PaginationUtil;
import java.io.IOException;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/attendance/record/emp")
public class AttendanceRecordEmpServlet extends HttpServlet {

    private final AttendanceLogDao dao = new AttendanceLogDao();
    private final TimesheetPeriodDao tDAO = new TimesheetPeriodDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long userId = (Long) req.getSession().getAttribute(SessionUtil.USER_ID_KEY);
            int recordsPerPage = 10;
            int currentPage = PaginationUtil.getCurrentPage(req);
            int offset = (currentPage - 1) * recordsPerPage;

            String action = req.getParameter("action");
            String exportType = req.getParameter("exportType");

            String employeeKeyword = req.getParameter("employeeKeyword");
            String department = req.getParameter("department");
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String status = req.getParameter("status");
            String source = req.getParameter("source");
            String periodIdStr = req.getParameter("periodSelect");

            LocalDate startDate;
            LocalDate endDate;
            Long periodId = null;

            if ("reset".equalsIgnoreCase(action)) {
                LocalDate now = LocalDate.now();
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
                employeeKeyword = "";
                department = "";
                status = "";
                source = "";
                periodIdStr = "";
            } else {
                startDate = (startDateStr != null && !startDateStr.isEmpty()) ? LocalDate.parse(startDateStr) : null;
                endDate = (endDateStr != null && !endDateStr.isEmpty()) ? LocalDate.parse(endDateStr) : null;
            }

            if (periodIdStr != null && !periodIdStr.isEmpty()) {
                try {
                    periodId = Long.valueOf(periodIdStr);
                } catch (NumberFormatException e) {
                    periodId = null;
                }
            }

            if (exportType != null) {
                List<AttendanceLogDto> filteredRecords = dao.findByFilter(
                        userId,
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

            List<AttendanceLogDto> attendanceList = dao.findByFilter(
                    userId,
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

            int totalRecords = dao.countByFilter(
                    userId,
                    employeeKeyword,
                    department,
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId
            );
            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("employeeKeyword", employeeKeyword);
            req.setAttribute("department", department);
            req.setAttribute("startDate", (startDate != null) ? startDate.toString() : "");
            req.setAttribute("endDate", (endDate != null) ? endDate.toString() : "");
            req.setAttribute("status", status);
            req.setAttribute("source", source);
            req.setAttribute("selectedPeriod", periodId);
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long userId = (Long) req.getSession().getAttribute(SessionUtil.USER_ID_KEY);
            int recordsPerPage = 10;
            int currentPage = PaginationUtil.getCurrentPage(req);
            int offset = (currentPage - 1) * recordsPerPage;

            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String status = req.getParameter("status");
            String source = req.getParameter("source");
            String periodIdStr = req.getParameter("periodSelect");

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

            List<AttendanceLogDto> attendanceList = dao.findByFilter(
                    userId, null, null, startDate, endDate, status, source, periodId,
                    recordsPerPage, offset, true
            );

            int totalRecords = dao.countByFilter(
                    userId, null, null, startDate, endDate, status, source, periodId
            );
            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("startDate", startDate.toString());
            req.setAttribute("endDate", endDate.toString());
            req.setAttribute("status", status);
            req.setAttribute("source", source);
            req.setAttribute("selectedPeriod", periodId);
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }
}
