package group4.hrms.controller;

import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.service.ExportService;
import group4.hrms.util.PaginationUtil;
import java.io.IOException;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.TimesheetPeriod;
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
            Long selectedPeriodId = null;
            TimesheetPeriod selectedPeriod = null;

            if ("reset".equalsIgnoreCase(action)) {
                // Lấy kỳ công hiện tại
                selectedPeriod = tDAO.findCurrentPeriod();
                if (selectedPeriod != null) {
                    selectedPeriodId = selectedPeriod.getId();
                    startDate = selectedPeriod.getStartDate();
                    endDate = selectedPeriod.getEndDate();
                } else {
                    LocalDate now = LocalDate.now();
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                }

                // Reset các filter khác
                employeeKeyword = "";
                department = "";
                status = "";
                source = "";
            } else {
                // Nhánh dùng filter từ request, giữ null nếu param rỗng
                startDate = (startDateStr != null && !startDateStr.isEmpty()) ? LocalDate.parse(startDateStr) : null;
                endDate = (endDateStr != null && !endDateStr.isEmpty()) ? LocalDate.parse(endDateStr) : null;

                if (periodIdStr != null && !periodIdStr.isEmpty()) {
                    try {
                        selectedPeriodId = Long.valueOf(periodIdStr);
                        selectedPeriod = tDAO.findById(selectedPeriodId).orElse(null);
                    } catch (NumberFormatException e) {
                        selectedPeriodId = null;
                        selectedPeriod = null;
                    }
                } else {
                    selectedPeriodId = null;
                    selectedPeriod = null;
                }
            }

            // Xử lý export
            if (exportType != null && !exportType.isEmpty()) {
                List<AttendanceLogDto> filteredRecords = dao.findByFilter(
                        userId,
                        employeeKeyword,
                        department,
                        startDate,
                        endDate,
                        status,
                        source,
                        selectedPeriodId,
                        Integer.MAX_VALUE,
                        0,
                        false
                );
                ExportService.AttendanceRecordExport(resp, exportType, filteredRecords);
                return;
            }

            // Lấy danh sách attendance
            List<AttendanceLogDto> attendanceList = dao.findByFilter(
                    userId,
                    employeeKeyword,
                    department,
                    startDate,
                    endDate,
                    status,
                    source,
                    selectedPeriodId,
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
                    selectedPeriodId
            );
            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

            // Set attribute cho JSP
            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("employeeKeyword", employeeKeyword);
            req.setAttribute("department", department);
            req.setAttribute("startDate", (startDate != null) ? startDate.toString() : "");
            req.setAttribute("endDate", (endDate != null) ? endDate.toString() : "");
            req.setAttribute("status", status != null ? status : "");
            req.setAttribute("source", source != null ? source : "");
            req.setAttribute("selectedPeriodId", selectedPeriodId); // dùng cho <select>
            req.setAttribute("selectedPeriod", selectedPeriod);     // dùng cho toggle nếu cần
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

            if (userId == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

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
            Long selectedPeriodId = null;
            TimesheetPeriod selectedPeriod = null;

            // ===== Xác định kỳ công =====
            if (periodIdStr != null && !periodIdStr.isEmpty()) {
                try {
                    selectedPeriodId = Long.valueOf(periodIdStr);
                    selectedPeriod = tDAO.findById(selectedPeriodId).orElse(null);
                } catch (NumberFormatException e) {
                    selectedPeriodId = null;
                }
            }

            if (selectedPeriodId == null) {
                // Nếu chưa có kỳ công filter, lấy kỳ công hiện tại
                selectedPeriod = tDAO.findCurrentPeriod();
                if (selectedPeriod != null) {
                    selectedPeriodId = selectedPeriod.getId();
                    startDate = selectedPeriod.getStartDate();
                    endDate = selectedPeriod.getEndDate();
                } else {
                    // Fallback nếu không có kỳ công hiện tại
                    LocalDate now = LocalDate.now();
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                }
            } else {
                // Nếu đã có periodId từ filter, ưu tiên start/end từ request nếu có
                if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty()) {
                    startDate = LocalDate.parse(startDateStr);
                    endDate = LocalDate.parse(endDateStr);
                } else if (selectedPeriod != null) {
                    startDate = selectedPeriod.getStartDate();
                    endDate = selectedPeriod.getEndDate();
                } else {
                    LocalDate now = LocalDate.now();
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                }
            }

            List<AttendanceLogDto> attendanceList = dao.findByFilter(
                    userId, null, null, startDate, endDate, status, source, selectedPeriodId,
                    recordsPerPage, offset, true
            );

            int totalRecords = dao.countByFilter(
                    userId, null, null, startDate, endDate, status, source, selectedPeriodId
            );
            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("startDate", startDate.toString());
            req.setAttribute("endDate", endDate.toString());
            req.setAttribute("status", status != null ? status : "");
            req.setAttribute("source", source != null ? source : "");
            req.setAttribute("selectedPeriodId", selectedPeriodId); // dùng cho <select>
            req.setAttribute("selectedPeriod", selectedPeriod);     // dùng cho toggle lock/unlock
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);

        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }
}
