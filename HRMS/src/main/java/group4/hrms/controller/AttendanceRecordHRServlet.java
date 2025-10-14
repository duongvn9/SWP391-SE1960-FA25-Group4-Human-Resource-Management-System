package group4.hrms.controller;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.Department;
import group4.hrms.model.TimesheetPeriod;
import group4.hrms.service.ExportService;
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
            List<AttendanceLogDto> attendanceList = attendanceLogDao.findAllForOverview();
            req.setAttribute("attendanceList", attendanceList);

            List<TimesheetPeriod> periodList = tDAO.findAll();
            req.setAttribute("periodList", periodList);

            List<Department> departmentList = dDAO.findAll();
            req.setAttribute("departmentList", departmentList);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordHRServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = req.getParameter("action");
            if ("reset".equals(action)) {
                req.setAttribute("attendanceList", attendanceLogDao.findAllForOverview());
                req.setAttribute("periodList", tDAO.findAll());
                req.setAttribute("departmentList", dDAO.findAll());
                req.setAttribute("employeeKeyword", "");
                req.setAttribute("department", "");
                req.setAttribute("startDate", "");
                req.setAttribute("endDate", "");
                req.setAttribute("status", "");
                req.setAttribute("source", "");
                req.setAttribute("periodId", "");

                req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp").forward(req, resp);
                return;
            }

            Long userId = null;
            String exportType = req.getParameter("exportType");
            String employeeKeyword = req.getParameter("employee");
            String departmentIdStr = req.getParameter("department");
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String status = req.getParameter("status");
            String source = req.getParameter("source");
            String periodIdStr = req.getParameter("periodSelect");

            LocalDate startDate = null;
            LocalDate endDate = null;
            Long periodId = null;
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr);
            }
            if (periodIdStr != null && !periodIdStr.isEmpty()) {
                try {
                    periodId = Long.valueOf(periodIdStr);
                } catch (NumberFormatException e) {
                    periodId = null;
                }
            }

            if (exportType != null) {
                ExportService.handleExport(resp, exportType, userId);
                return;
            }

            List<AttendanceLogDto> attendanceList = attendanceLogDao.findByFilter(
                    userId,
                    employeeKeyword,
                    departmentIdStr,
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId
            );

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

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-HR.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordHRServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
