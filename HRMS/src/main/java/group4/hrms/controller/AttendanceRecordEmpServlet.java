package group4.hrms.controller;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dto.AttendanceLogDto;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

@WebServlet("/attendance/record/emp")
public class AttendanceRecordEmpServlet extends HttpServlet {

    private final AttendanceLogDao dao = new AttendanceLogDao();
    private final TimesheetPeriodDao tDAO = new TimesheetPeriodDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
//            // 🔹 Lấy session hiện tại, không tạo mới nếu chưa tồn tại
//            HttpSession session = req.getSession(false);
//
//            Long userId = null; // mặc định chưa có
//            if (session != null) {
//                Object userIdObj = session.getAttribute("userId"); // hoặc "loggedInUserId"
//                if (userIdObj instanceof Long) {
//                    userId = (Long) userIdObj;
//                } else if (userIdObj != null) {
//                    try {
//                        userId = Long.valueOf(userIdObj.toString());
//                    } catch (NumberFormatException e) {
//                        userId = null; // nếu parse lỗi
//                    }
//                }
//            }
//
//            // Nếu userId vẫn null => chưa đăng nhập, có thể redirect về login
//            if (userId == null) {
//                resp.sendRedirect(req.getContextPath() + "/login");
//                return; // dừng xử lý tiếp
//            }

            // 🔹 Lấy thông tin người dùng hiện tại (tạm thời hardcode, sau này lấy từ session)
            Long userId = 45L; 

            String employeeKeyword = req.getParameter("employeeKeyword"); // có thể null đối với employee view
            String department = req.getParameter("department");
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String status = req.getParameter("status");
            String source = req.getParameter("source");
            String periodIdStr = req.getParameter("periodId");

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

            List<AttendanceLogDto> attendanceList = dao.findByFilter(
                    userId,
                    employeeKeyword,
                    department,
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId
            );

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll()); 
            req.setAttribute("employeeKeyword", employeeKeyword);
            req.setAttribute("department", department);
            req.setAttribute("startDate", startDateStr);
            req.setAttribute("endDate", endDateStr);
            req.setAttribute("status", status);
            req.setAttribute("source", source);
            req.setAttribute("periodId", periodIdStr);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
//            Long userId = (Long) req.getSession().getAttribute("userId");    
            Long userId = 45l;
            List<AttendanceLogDto> attendanceList = dao.findByUserId(userId);
            System.out.println(tDAO.findAll());
            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<String> attendanceList = new ArrayList<>();
        req.setAttribute("attendanceList", attendanceList);
        req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);

    }
}
