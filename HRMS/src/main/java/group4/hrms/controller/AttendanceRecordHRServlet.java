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

            // --- Lấy các tham số filter từ request ---
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String status = req.getParameter("status");
            String source = req.getParameter("source");
            String periodIdStr = req.getParameter("periodSelect");
            String department = req.getParameter("department"); 

            LocalDate startDate;
            LocalDate endDate;
            Long periodId = null;

            // --- Nếu không chọn ngày, lấy từ ngày đầu đến ngày cuối của tháng hiện tại ---
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

            // --- Lấy danh sách attendance theo filter và phân trang ---
            List<AttendanceLogDto> attendanceList = attendanceLogDao.findByFilter(
                    null, // userId null = lấy tất cả nhân viên
                    department, // filter theo department nếu cần
                    null, // employee keyword, null nếu không filter
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId,
                    recordsPerPage,
                    offset,
                    true // active only
            );

            // --- Tổng số bản ghi để tính phân trang ---
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

            // --- Set attributes cho JSP ---
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
            Long userId = null; // HR xem tất cả nhân viên
            int recordsPerPage = 10;
            int currentPage = PaginationUtil.getCurrentPage(req);
            int offset = (currentPage - 1) * recordsPerPage;

            String action = req.getParameter("action");
            String exportType = req.getParameter("exportType");

            // --- Lấy các tham số filter từ request ---
            String employeeKeyword = getParam(req, "employeeKeyword");
            String department = getParam(req, "department");
            String startDateStr = getParam(req, "startDate");
            String endDateStr = getParam(req, "endDate");
            String status = getParam(req, "status");
            String source = getParam(req, "source");
            System.out.println(source);
            String periodIdStr = getParam(req, "periodSelect");

            LocalDate startDate = parseDate(startDateStr);
            LocalDate endDate = parseDate(endDateStr);
            Long periodId = parseLongSafe(periodIdStr);

            if ("reset".equalsIgnoreCase(action)) {
                employeeKeyword = "";
                department = "";
                status = "";
                source = "";
                periodId = null;

                LocalDate now = LocalDate.now();
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
            } else {
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    startDate = parseDate(startDateStr);
                }
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    endDate = parseDate(endDateStr);
                }
            }

            // --- Xử lý export ---
            if (exportType != null && !exportType.isEmpty()) {
                List<AttendanceLogDto> filteredRecords = attendanceLogDao.findByFilter(
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

            // --- Lấy dữ liệu theo filter & phân trang ---
            List<AttendanceLogDto> attendanceList = attendanceLogDao.findByFilter(
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

            int totalRecords = attendanceLogDao.countByFilter(
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

            // --- Set attributes cho JSP ---
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
