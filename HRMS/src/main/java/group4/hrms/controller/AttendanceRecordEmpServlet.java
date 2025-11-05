package group4.hrms.controller;

import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.service.AttendanceService;
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
import java.util.Map;
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

                employeeKeyword = "";
                department = "";
                status = "";
                source = "";
            } else {
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

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("employeeKeyword", employeeKeyword);
            req.setAttribute("department", department);
            req.setAttribute("startDate", (startDate != null) ? startDate.toString() : "");
            req.setAttribute("endDate", (endDate != null) ? endDate.toString() : "");
            req.setAttribute("status", status != null ? status : "");
            req.setAttribute("source", source != null ? source : "");
            req.setAttribute("selectedPeriodId", selectedPeriodId);
            req.setAttribute("selectedPeriod", selectedPeriod);
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);
            
            // Tính toán attendance summary chỉ khi có period cụ thể
            if (selectedPeriodId != null && selectedPeriod != null) {
                try {
                    LocalDate summaryStartDate = selectedPeriod.getStartDate();
                    LocalDate summaryEndDate = selectedPeriod.getEndDate();
                    
                    System.out.println(summaryStartDate);
                    System.out.println(summaryEndDate);
                    // Fallback nếu period không có startDate/endDate
                    if (summaryStartDate == null || summaryEndDate == null) {
                        LocalDate now = LocalDate.now();
                        if (summaryStartDate == null) {
                            summaryStartDate = now.withDayOfMonth(1);
                        }
                        if (summaryEndDate == null) {
                            summaryEndDate = now.withDayOfMonth(now.lengthOfMonth());
                        }
                    }
                    
                    Map<String, Object> summary = AttendanceService.calculateAttendanceSummary(
                        userId, summaryStartDate, summaryEndDate
                    );
                    req.setAttribute("attendanceSummary", summary);
                    req.setAttribute("showSummaryButton", true);
                } catch (SQLException e) {
                    Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.WARNING, "Error calculating attendance summary in doPost", e);
                    req.setAttribute("showSummaryButton", false);
                }
            } else {
                Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.WARNING, 
                    "Cannot calculate summary - missing period: periodId={0}, period={1}", 
                    new Object[]{selectedPeriodId, selectedPeriod});
                req.setAttribute("showSummaryButton", false);
            }

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

            // Xử lý period trước
            if (periodIdStr != null && !periodIdStr.isEmpty()) {
                try {
                    selectedPeriodId = Long.valueOf(periodIdStr);
                    selectedPeriod = tDAO.findById(selectedPeriodId).orElse(null);
                } catch (NumberFormatException e) {
                    selectedPeriodId = null;
                    selectedPeriod = null;
                }
            }

            // Nếu không có period được chọn, lấy current period làm mặc định
            if (selectedPeriodId == null) {
                selectedPeriod = tDAO.findCurrentPeriod();
                if (selectedPeriod != null) {
                    selectedPeriodId = selectedPeriod.getId();
                }
            }

            // Xử lý startDate và endDate - ưu tiên giá trị user nhập, nếu không có thì lấy từ period
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr);
            } else if (selectedPeriod != null) {
                // Nếu user không nhập startDate, lấy từ period làm mặc định
                startDate = selectedPeriod.getStartDate();
            } else {
                // Fallback nếu không có period
                startDate = LocalDate.now().withDayOfMonth(1);
            }

            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr);
            } else if (selectedPeriod != null) {
                // Nếu user không nhập endDate, lấy từ period làm mặc định
                endDate = selectedPeriod.getEndDate();
            } else {
                // Fallback nếu không có period
                LocalDate now = LocalDate.now();
                endDate = now.withDayOfMonth(now.lengthOfMonth());
            }
            


            List<AttendanceLogDto> attendanceList = dao.findByFilter(
                    userId, employeeKeyword, department, startDate, endDate, status, source, selectedPeriodId,
                    recordsPerPage, offset, true
            );

            int totalRecords = dao.countByFilter(
                    userId, employeeKeyword, department, startDate, endDate, status, source, selectedPeriodId
            );
            int totalPages = PaginationUtil.calculateTotalPages(totalRecords, recordsPerPage);

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("employeeKeyword", employeeKeyword != null ? employeeKeyword : "");
            req.setAttribute("department", department != null ? department : "");
            req.setAttribute("startDate", startDate.toString());
            req.setAttribute("endDate", endDate.toString());
            req.setAttribute("status", status != null ? status : "");
            req.setAttribute("source", source != null ? source : "");
            req.setAttribute("selectedPeriodId", selectedPeriodId);
            req.setAttribute("selectedPeriod", selectedPeriod);
            req.setAttribute("currentPage", currentPage);
            req.setAttribute("totalPages", totalPages);

            // Debug logging (temporary - can be removed later)
            System.out.println("DEBUG doGet - selectedPeriodId: " + selectedPeriodId + 
                ", selectedPeriod: " + (selectedPeriod != null ? selectedPeriod.getName() : "null") + 
                ", startDate: " + startDate + ", endDate: " + endDate + 
                ", status: '" + status + "', source: '" + source + "'");
            
            // Tính toán attendance summary chỉ khi có period cụ thể
            if (selectedPeriodId != null && selectedPeriod != null) {
                try {
                    // Luôn sử dụng startDate và endDate từ TimesheetPeriod để tính summary
                    // Không sử dụng filter dates
                    LocalDate summaryStartDate = selectedPeriod.getStartDate();
                    LocalDate summaryEndDate = selectedPeriod.getEndDate();                 
                    
                    // Fallback nếu period không có startDate/endDate
                    if (summaryStartDate == null || summaryEndDate == null) {
                        LocalDate now = LocalDate.now();
                        if (summaryStartDate == null) {
                            summaryStartDate = now.withDayOfMonth(1);
                        }
                        if (summaryEndDate == null) {
                            summaryEndDate = now.withDayOfMonth(now.lengthOfMonth());
                        }
                    }
                    
                    Map<String, Object> summary = AttendanceService.calculateAttendanceSummary(
                        userId, summaryStartDate, summaryEndDate
                    );
                    req.setAttribute("attendanceSummary", summary);
                    req.setAttribute("showSummaryButton", true);
                } catch (SQLException e) {
                    Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.WARNING, "Error calculating attendance summary in doGet", e);
                    req.setAttribute("showSummaryButton", false);
                }
            } else {
                Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.WARNING, 
                    "Cannot calculate summary - missing period: periodId={0}, period={1}", 
                    new Object[]{selectedPeriodId, selectedPeriod});
                req.setAttribute("showSummaryButton", false);
            }

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }
}
