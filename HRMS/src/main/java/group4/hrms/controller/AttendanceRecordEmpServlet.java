package group4.hrms.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import group4.hrms.model.User;
import group4.hrms.service.AttendanceService;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/attendance/record/emp")
public class AttendanceRecordEmpServlet extends HttpServlet{

    private static final Logger logger = LoggerFactory.getLogger(AttendanceRecordEmpServlet.class);
    private final AttendanceLogDao attendanceLogDao = new AttendanceLogDao();
    private final AttendanceService attendanceService = new AttendanceService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = SessionUtil.getCurrentUserId(req);

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<AttendanceLogDto> attendanceList = new ArrayList<>();

        try {
            // Get date range from request parameters or default to current month
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");

            LocalDate startDate;
            LocalDate endDate;

            if (startDateStr != null && endDateStr != null) {
                startDate = LocalDate.parse(startDateStr);
                endDate = LocalDate.parse(endDateStr);
            } else {
                // Default to current month
                LocalDate now = LocalDate.now();
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
            }

            // Fetch attendance logs for the user
            List<AttendanceLog> logs = attendanceLogDao.findByUserIdAndDateRange(
                userId, startDate, endDate
            );

            // Convert to DTOs and add half-day leave information
            for (AttendanceLog log : logs) {
                AttendanceLogDto dto = new AttendanceLogDto(log);

                // Add half-day leave information
                String halfDayInfo = attendanceService.getHalfDayLeaveDisplayInfo(
                    userId, log.getWorkDate()
                );
                dto.setHalfDayLeaveInfo(halfDayInfo);
                dto.setHasHalfDayLeave(halfDayInfo != null);

                attendanceList.add(dto);
            }

        } catch (SQLException e) {
            logger.error("Error fetching attendance records for user {}: {}", userId, e.getMessage(), e);
            req.setAttribute("errorMessage", "Error loading attendance records");
        }

        req.setAttribute("attendanceList", attendanceList);
        req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);
    }
}
