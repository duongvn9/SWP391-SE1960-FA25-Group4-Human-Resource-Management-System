package group4.hrms.controller;

import group4.hrms.dao.PayslipDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.User;
import group4.hrms.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * API Controller for payslip-related data endpoints
 * Provides JSON data for pending employees and attendance changes
 */
@WebServlet(urlPatterns = {
    "/payslips/api/employees-without-payslip",
    "/payslips/api/employees-with-attendance-changes"
})
public class PayslipApiController extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PayslipApiController.class);
    private static final Gson gson = new Gson();

    private PayslipDao payslipDao;
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        this.payslipDao = new PayslipDao();
        this.userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check authorization - only HRM can access
        User currentUser = SessionUtil.getCurrentUser(request);
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }

        // Position 7 = HR Manager, 8 = HR Staff
        if (currentUser.getPositionId() != 7 && currentUser.getPositionId() != 8) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        String path = request.getServletPath();
        
        try {
            if (path.endsWith("/employees-without-payslip")) {
                handleEmployeesWithoutPayslip(request, response);
            } else if (path.endsWith("/employees-with-attendance-changes")) {
                handleEmployeesWithAttendanceChanges(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling API request", e);
            sendErrorResponse(response, "Internal server error: " + e.getMessage());
        }
    }

    /**
     * Get employees without payslip for previous month
     */
    private void handleEmployeesWithoutPayslip(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Get period from request or default to previous month
        String periodParam = request.getParameter("period");
        LocalDate periodStart;
        
        if (periodParam != null && !periodParam.isEmpty()) {
            periodStart = LocalDate.parse(periodParam);
        } else {
            // Default to previous month
            periodStart = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        }
        
        LocalDate periodEnd = periodStart.with(TemporalAdjusters.lastDayOfMonth());

        logger.info("Fetching employees without payslip for period: {} to {}", periodStart, periodEnd);

        try {
            // Use optimized single query to get employees without payslips
            List<Map<String, Object>> employeesWithoutPayslip = 
                payslipDao.findEmployeesWithoutPayslip(periodStart, periodEnd);

            logger.info("Found {} employees without payslip for period {} to {}", 
                employeesWithoutPayslip.size(), periodStart, periodEnd);

            // Build response
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("total", employeesWithoutPayslip.size());
            responseMap.put("period", periodStart.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            responseMap.put("employees", employeesWithoutPayslip);

            sendJsonResponse(response, responseMap);
            
        } catch (Exception e) {
            logger.error("Error fetching employees without payslip", e);
            sendErrorResponse(response, "Failed to fetch employees: " + e.getMessage());
        }
    }

    /**
     * Get employees with attendance changes in current payroll period
     */
    private void handleEmployeesWithAttendanceChanges(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Get period from request or default to current month
        String periodParam = request.getParameter("period");
        LocalDate periodStart;
        
        if (periodParam != null && !periodParam.isEmpty()) {
            periodStart = LocalDate.parse(periodParam);
        } else {
            // Default to current month
            periodStart = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        }
        
        LocalDate periodEnd = periodStart.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate today = LocalDate.now();

        logger.info("Fetching employees with attendance changes for period: {} to {}", periodStart, periodEnd);

        // Check if we're still in the grace period (first 7 days of month)
        boolean inGracePeriod = today.getDayOfMonth() <= 7;

        try {
            // Get dirty payslips (those with attendance changes)
            List<group4.hrms.model.Payslip> dirtyPayslips = 
                payslipDao.findDirtyPayslipsWithUserInfo(periodStart, periodEnd);
            
            List<Map<String, Object>> employeesWithChanges = new ArrayList<>();
            
            for (group4.hrms.model.Payslip payslip : dirtyPayslips) {
                // Get user info
                Optional<User> userOpt = userDao.findById(payslip.getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    Map<String, Object> empData = new HashMap<>();
                    empData.put("id", user.getId());
                    empData.put("employeeCode", user.getEmployeeCode());
                    empData.put("fullName", user.getFullName());
                    empData.put("departmentId", user.getDepartmentId());
                    empData.put("payslipId", payslip.getId());
                    empData.put("changeReason", payslip.getDirtyReason());
                    empData.put("updatedAt", payslip.getUpdatedAt() != null ? 
                        payslip.getUpdatedAt().toString() : null);
                    employeesWithChanges.add(empData);
                }
            }

            // Build response
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("total", employeesWithChanges.size());
            responseMap.put("period", periodStart.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            responseMap.put("inGracePeriod", inGracePeriod);
            responseMap.put("employees", employeesWithChanges);

            sendJsonResponse(response, responseMap);
            
        } catch (Exception e) {
            logger.error("Error fetching employees with attendance changes", e);
            sendErrorResponse(response, "Failed to fetch employees: " + e.getMessage());
        }
    }

    /**
     * Send JSON response
     */
    private void sendJsonResponse(HttpServletResponse response, Map<String, Object> data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    /**
     * Send error response
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("error", message);
        
        sendJsonResponse(response, errorMap);
    }
}
