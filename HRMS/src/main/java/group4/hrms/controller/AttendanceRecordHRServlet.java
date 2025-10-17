package group4.hrms.controller;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dto.AttendanceLogDto;
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

            List<AttendanceLogDto> attendanceList = attendanceLogDao.findAllForOverview(offset, recordsPerPage, true);
            int totalRecords = attendanceLogDao.countAllForOverview();
            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("departmentList", dDAO.findAll());

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordHRServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long userId = null; // TODO: lấy từ session sau
            int recordsPerPage = 10;
            int currentPage = PaginationUtil.getCurrentPage(req);
            int offset = (currentPage - 1) * recordsPerPage;

            String action = req.getParameter("action");
            String exportType = req.getParameter("exportType");

            // ===== Export =====
            if (exportType != null && !exportType.isEmpty()) {
                ExportService.AttendanceRecordExport(resp, exportType, userId);
                return;
            }

            // ===== Reset =====
            if ("reset".equalsIgnoreCase(action)) {
                resetFilters(req, currentPage, recordsPerPage, userId);
                req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp").forward(req, resp);
                return;
            }

            // ===== Filter =====
            String employeeKeyword = getParam(req, "employee");
            String departmentIdStr = getParam(req, "department");
            String startDateStr = getParam(req, "startDate");
            String endDateStr = getParam(req, "endDate");
            String status = getParam(req, "status");
            String source = getParam(req, "source");
            String periodIdStr = getParam(req, "periodId");

            LocalDate startDate = parseDate(startDateStr);
            LocalDate endDate = parseDate(endDateStr);
            Long periodId = parseLongSafe(periodIdStr);

            List<AttendanceLogDto> attendanceList = getAttendanceListByFilter(
                    userId, employeeKeyword, departmentIdStr, startDate, endDate,
                    status, source, periodId, recordsPerPage, offset
            );
            int totalRecords = countAttendanceByFilter(
                    userId, employeeKeyword, departmentIdStr, startDate, endDate,
                    status, source, periodId
            );
            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

            setRequestAttributesAfterFilter(req, attendanceList, employeeKeyword, departmentIdStr,
                    startDateStr, endDateStr, status, source, periodIdStr, currentPage, totalPages);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp").forward(req, resp);

        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordHRServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }

    private void resetFilters(HttpServletRequest req, int currentPage, int recordsPerPage, Long userId) throws SQLException {
        List<AttendanceLogDto> attendanceList = getAttendanceListByFilter(
                userId, "", "", null, null, "", "", null, recordsPerPage, 0
        );
        int totalRecords = countAttendanceByFilter(userId, "", "", null, null, "", "", null);
        int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

        setRequestAttributesAfterFilter(req, attendanceList, "", "", "", "",
                "", "", "", currentPage, totalPages);
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

    private List<AttendanceLogDto> getAttendanceListByFilter(
            Long userId, String employeeKeyword, String departmentIdStr,
            LocalDate startDate, LocalDate endDate, String status, String source,
            Long periodId, int recordsPerPage, int offset
    ) throws SQLException {
        return attendanceLogDao.findByFilter(
                userId, employeeKeyword, departmentIdStr, startDate, endDate,
                status, source, periodId, recordsPerPage, offset, true
        );
    }

    private int countAttendanceByFilter(
            Long userId, String employeeKeyword, String departmentIdStr,
            LocalDate startDate, LocalDate endDate, String status, String source, Long periodId
    ) throws SQLException {
        return attendanceLogDao.countByFilter(
                userId, employeeKeyword, departmentIdStr, startDate, endDate, status, source, periodId
        );
    }

    private void setRequestAttributesAfterFilter(HttpServletRequest req, List<AttendanceLogDto> attendanceList,
            String employeeKeyword, String departmentIdStr,
            String startDateStr, String endDateStr, String status,
            String source, String periodIdStr,
            int currentPage, int totalPages) throws SQLException {

        req.setAttribute("attendanceList", attendanceList);
        req.setAttribute("periodList", tDAO.findAll());
        req.setAttribute("departmentList", dDAO.findAll());
        req.setAttribute("employeeKeyword", employeeKeyword);
        req.setAttribute("department", departmentIdStr);
        req.setAttribute("startDate", startDateStr);
        req.setAttribute("endDate", endDateStr);
        req.setAttribute("status", status);
        req.setAttribute("source", source);
        req.setAttribute("periodId", periodIdStr);
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);
    }
}
