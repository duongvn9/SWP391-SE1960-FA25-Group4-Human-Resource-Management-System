package group4.hrms.controller;

import group4.hrms.dao.PayslipDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.PositionDao;
import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dto.PayslipCalculationResult;
import group4.hrms.model.Payslip;
import group4.hrms.model.User;
import group4.hrms.model.Department;
import group4.hrms.model.Position;
import group4.hrms.service.PayslipCalculationService;

import group4.hrms.util.SessionUtil;
import group4.hrms.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.List;

@WebServlet("/payslips/detail")
public class PayslipDetailServlet extends HttpServlet {

    private PayslipDao payslipDao;
    private UserDao userDao;
    private DepartmentDao departmentDao;
    private PositionDao positionDao;
    private AttendanceLogDao attendanceLogDao;
    private PayslipCalculationService calculationService;


    @Override
    public void init() throws ServletException {
        super.init();
        this.payslipDao = new PayslipDao();
        this.userDao = new UserDao();
        this.departmentDao = new DepartmentDao();
        this.positionDao = new PositionDao();
        this.attendanceLogDao = new AttendanceLogDao();
        this.calculationService = new PayslipCalculationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {       

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || !SessionUtil.isUserLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Get parameters
            String userIdParam = request.getParameter("userId");
            String payslipIdParam = request.getParameter("payslipId");
            String periodStartParam = request.getParameter("periodStart");
            String periodEndParam = request.getParameter("periodEnd");



            // Determine target user - if userId is null, use current user (view own payslip)
            Long targetUserId;
            if (userIdParam != null && !userIdParam.trim().isEmpty()) {
                try {
                    targetUserId = Long.valueOf(userIdParam);
                    
                    // Check permission - only HRM can view other users' payslips
                    if (!targetUserId.equals(currentUser.getId()) && !PermissionUtil.canViewAllUsers(request)) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                        return;
                    }
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
                    return;
                }
            } else {
                // No userId parameter - view own payslip
                targetUserId = currentUser.getId();
            }

            // Get target user info
            Optional<User> targetUserOpt = userDao.findById(targetUserId);
            if (!targetUserOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            User targetUser = targetUserOpt.get();

            // Get additional user info
            Optional<Department> department = Optional.empty();
            Optional<Position> position = Optional.empty();
            
            if (targetUser.getDepartmentId() != null) {
                department = departmentDao.findById(targetUser.getDepartmentId());
            }
            if (targetUser.getPositionId() != null) {
                position = positionDao.findById(targetUser.getPositionId());
            }

            Payslip payslip = null;
            PayslipCalculationResult calculationResult = null;

            // Case 1: View existing payslip by ID
            if (payslipIdParam != null && !payslipIdParam.trim().isEmpty()) {
                try {
                    Long payslipId = Long.valueOf(payslipIdParam);
                    Optional<Payslip> payslipOpt = payslipDao.findById(payslipId);
                    
                    if (!payslipOpt.isPresent()) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Payslip not found");
                        return;
                    }
                    
                    payslip = payslipOpt.get();
                    
                    // Verify payslip belongs to target user
                    if (!payslip.getUserId().equals(targetUserId)) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                        return;
                    }
                    
                    // Calculate current values for comparison (if needed)
                    calculationResult = calculationService.calculatePayslip(
                        targetUserId, payslip.getPeriodStart(), payslip.getPeriodEnd());
                        
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid payslip ID");
                    return;
                }
            }
            // Case 2: View/calculate payslip for specific period
            else if (periodStartParam != null && periodEndParam != null) {
                try {
                    LocalDate periodStart = LocalDate.parse(periodStartParam);
                    LocalDate periodEnd = LocalDate.parse(periodEndParam);
                    
                    // Try to find existing payslip first
                    Optional<Payslip> existingPayslip = payslipDao.findByUserAndPeriod(
                        targetUserId, periodStart, periodEnd);
                    
                    if (existingPayslip.isPresent()) {
                        payslip = existingPayslip.get();
                    }
                    
                    // Always calculate current values
                    calculationResult = calculationService.calculatePayslip(
                        targetUserId, periodStart, periodEnd);
                        
                } catch (DateTimeParseException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format");
                    return;
                }
            }
            // Case 3: No specific payslip or period - show latest payslip
            else {
                try {
                    List<Payslip> userPayslips = payslipDao.findByUserId(targetUserId);
                    if (!userPayslips.isEmpty()) {
                        payslip = userPayslips.get(0); // Latest payslip (ordered by created_at DESC)
                        
                        // Calculate current values
                        calculationResult = calculationService.calculatePayslip(
                            targetUserId, payslip.getPeriodStart(), payslip.getPeriodEnd());
                    } else {
                        // No payslips found - show message
                        request.setAttribute("noPayslipsFound", true);
                    }
                } catch (SQLException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Error loading payslip data");
                    return;
                }
            }

            // Calculate working days data if we have period information
            if (payslip != null || calculationResult != null) {
                LocalDate periodStart = payslip != null ? payslip.getPeriodStart() : calculationResult.getPeriodStart();
                LocalDate periodEnd = payslip != null ? payslip.getPeriodEnd() : calculationResult.getPeriodEnd();
                
                // Use existing methods from PayslipCalculationService
                int standardWorkingDays = calculationService.getWorkingDaysInMonth(periodStart.getYear(), periodStart.getMonthValue());
                
                // Get actual working days and hours from calculationResult if available
                int actualWorkingDays = 0;
                double actualWorkingHours = 0.0;
                double requiredHours = 0.0;
                
                if (calculationResult != null) {
                    actualWorkingDays = calculationResult.getWorkedDays();
                    actualWorkingHours = calculationResult.getTotalActualHours();
                    requiredHours = calculationResult.getRequiredHours();
                }
                
                // Calculate ratios
                double workingDaysRatio = standardWorkingDays > 0 ? (actualWorkingDays * 100.0 / standardWorkingDays) : 0.0;
                double workingHoursRatio = requiredHours > 0 ? (actualWorkingHours * 100.0 / requiredHours) : 0.0;
                
                // Set working days attributes
                request.setAttribute("standardWorkingDays", standardWorkingDays);
                request.setAttribute("actualWorkingDays", actualWorkingDays);
                request.setAttribute("workingDaysRatio", String.format("%.1f%%", workingDaysRatio));
                request.setAttribute("workingHoursRatio", String.format("%.1f%%", workingHoursRatio));
                request.setAttribute("avgHoursPerDay", actualWorkingDays > 0 ? 
                    String.format("%.1f", actualWorkingHours / actualWorkingDays) : "0.0");
            }

            // Set attributes for JSP
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("targetUser", targetUser);
            request.setAttribute("department", department.orElse(null));
            request.setAttribute("position", position.orElse(null));
            request.setAttribute("payslip", payslip);
            request.setAttribute("calculationResult", calculationResult);
            request.setAttribute("isOwnPayslip", targetUserId.equals(currentUser.getId()));
            request.setAttribute("canViewAllUsers", PermissionUtil.canViewAllUsers(request));

            // Format dates for JSP display (JSP can't handle LocalDate directly)
            if (payslip != null) {
                String periodDisplay = payslip.getPeriodStart().format(DateTimeFormatter.ofPattern("MM/yyyy"));
                request.setAttribute("periodDisplay", periodDisplay);
                
                // Format individual dates for JSP
                request.setAttribute("periodStartFormatted", payslip.getPeriodStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                request.setAttribute("periodEndFormatted", payslip.getPeriodEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                
                if (payslip.getGeneratedAt() != null) {
                    request.setAttribute("generatedAtFormatted", payslip.getGeneratedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            } 
            
            if (calculationResult != null) {
                if (payslip == null) {
                    String periodDisplay = calculationResult.getPeriodStart().format(DateTimeFormatter.ofPattern("MM/yyyy"));
                    request.setAttribute("periodDisplay", periodDisplay);
                }
                
                // Format calculation result dates
                request.setAttribute("calcPeriodStartFormatted", calculationResult.getPeriodStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                request.setAttribute("calcPeriodEndFormatted", calculationResult.getPeriodEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/payslips/detail-payslip.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Database error occurred");
        } catch (ServletException | IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
}
