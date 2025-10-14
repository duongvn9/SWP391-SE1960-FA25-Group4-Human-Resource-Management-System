
package group4.hrms.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import group4.hrms.dao.LeaveTypeDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dto.AccountDto;
import group4.hrms.dto.UserDto;
import group4.hrms.service.LeaveRequestService;
import group4.hrms.service.LeaveRequestService.LeaveTypeRules;
import group4.hrms.exception.LeaveValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/request/leave")
public class LeaveRequestController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LeaveRequestController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("LeaveRequestController.doGet() called. Request URI: " + request.getRequestURI());
        String action = request.getParameter("action");
        logger.info("Action parameter: " + action);

        if ("create".equals(action)) {
            // TEMPORARY: Skip authentication check for development
            // TODO: Uncomment this when login is implemented
            /*
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("account") == null) {
                logger.warning("User not authenticated. Redirecting to login.");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            */

            logger.info("Loading leave request form (authentication check disabled for development)...");

            try {
                logger.info("Initializing LeaveRequestService...");

                // Initialize LeaveRequestService with required DAOs
                LeaveRequestService service = new LeaveRequestService(
                    new RequestDao(),
                    new RequestTypeDao(),
                    new LeaveTypeDao()
                );

                logger.info("Loading available leave types...");
                // Load available leave types using service.getAvailableLeaveTypes()
                Map<String, String> leaveTypes = service.getAvailableLeaveTypes();
                logger.info("Loaded " + (leaveTypes != null ? leaveTypes.size() : 0) + " leave types");

                logger.info("Loading leave type rules...");
                // Load leave type rules using service.getAllLeaveTypeRules()
                List<LeaveTypeRules> leaveTypeRules = service.getAllLeaveTypeRules();
                logger.info("Loaded " + (leaveTypeRules != null ? leaveTypeRules.size() : 0) + " leave type rules");

                // ========== TEMPORARY: Mock data for leave balances (no login yet) ==========
                // TODO: Uncomment this when login is implemented
                /*
                Long userId = 1L; // Get from session
                int currentYear = java.time.LocalDateTime.now().getYear();
                logger.info("Loading leave balances for user " + userId + " in year " + currentYear);
                List<group4.hrms.dto.LeaveBalance> leaveBalances = service.getAllLeaveBalances(userId, currentYear);
                logger.info("Loaded " + (leaveBalances != null ? leaveBalances.size() : 0) + " leave balances");
                */

                // Mock data for UI preview
                int currentYear = java.time.LocalDateTime.now().getYear();
                List<group4.hrms.dto.LeaveBalance> leaveBalances = new java.util.ArrayList<>();

                // Mock: Annual Leave (12 default + 2 seniority = 14 total, used 5, remaining 9)
                leaveBalances.add(new group4.hrms.dto.LeaveBalance(
                    "ANNUAL", "Annual Leave", 12, 2, 5, currentYear
                ));

                // Mock: Sick Leave (7 default + 0 seniority = 7 total, used 2, remaining 5)
                leaveBalances.add(new group4.hrms.dto.LeaveBalance(
                    "SICK", "Sick Leave", 7, 0, 2, currentYear
                ));

                // Mock: Personal Leave (5 default + 1 seniority = 6 total, used 3, remaining 3)
                leaveBalances.add(new group4.hrms.dto.LeaveBalance(
                    "PERSONAL", "Personal Leave", 5, 1, 3, currentYear
                ));

                logger.info("Using MOCK DATA: " + leaveBalances.size() + " leave balances for UI preview");
                // ========== END TEMPORARY MOCK DATA ==========

                // Set leaveTypes, leaveTypeRules and leaveBalances as request attributes
                request.setAttribute("leaveTypes", leaveTypes);
                request.setAttribute("leaveTypeRules", leaveTypeRules);
                request.setAttribute("leaveBalances", leaveBalances);
                request.setAttribute("currentYear", currentYear);

                logger.info("Forwarding to leave-form.jsp...");
                // Forward to leave-form.jsp
                request.getRequestDispatcher("/WEB-INF/views/requests/leave-form.jsp")
                       .forward(request, response);

            } catch (Exception e) {
                logger.severe("Error loading leave request form: " + e.getMessage());
                e.printStackTrace();

                // Send detailed error response
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<html><body>");
                response.getWriter().println("<h1>Error Loading Leave Request Form</h1>");
                response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
                response.getWriter().println("<pre>");
                e.printStackTrace(response.getWriter());
                response.getWriter().println("</pre>");
                response.getWriter().println("<p><a href='" + request.getContextPath() + "/dashboard'>Back to Dashboard</a></p>");
                response.getWriter().println("</body></html>");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            // TEMPORARY: Skip authentication check for development
            // TODO: Uncomment this when login is implemented
            /*
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("account") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            */

            try {
                // TEMPORARY: Use dummy data for development
                // TODO: Get real account and user from session when login is implemented
                /*
                AccountDto account = (AccountDto) session.getAttribute("account");
                UserDto user = (UserDto) session.getAttribute("user");
                */

                // Dummy data for testing
                AccountDto account = new AccountDto();
                account.setId(1L);

                UserDto user = new UserDto();
                user.setId(1L);
                user.setDepartmentId(1L);

                // Extract form parameters: leaveTypeCode, startDate, endDate, reason
                String leaveTypeCode = request.getParameter("leaveTypeCode");
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                String reason = request.getParameter("reason");

                // Parse date strings to LocalDateTime
                java.time.LocalDateTime startDate = java.time.LocalDateTime.parse(startDateStr);
                java.time.LocalDateTime endDate = java.time.LocalDateTime.parse(endDateStr);

                // Initialize service
                LeaveRequestService service = new LeaveRequestService(
                    new RequestDao(),
                    new RequestTypeDao(),
                    new LeaveTypeDao()
                );

                // Call service.createLeaveRequest() with all parameters
                Long requestId = service.createLeaveRequest(
                    account.getId(),
                    user.getId(),
                    user.getDepartmentId(),
                    leaveTypeCode,
                    startDate,
                    endDate,
                    reason
                );

                // Handle success: set success message attribute
                request.setAttribute("success", "Leave request submitted successfully! Request ID: " + requestId);

            } catch (LeaveValidationException e) {
                // Handle structured validation errors with detailed formatting
                logger.warning("Validation error (" + e.getErrorType() + "): " + e.getShortMessage());

                // Set error attributes for JSP display
                request.setAttribute("error", e.getMessage());
                request.setAttribute("errorType", e.getErrorType());
                request.setAttribute("errorTitle", e.getShortMessage());
                request.setAttribute("errorDetails", e.getDetailedMessage());

            } catch (IllegalArgumentException e) {
                // Handle generic validation errors
                logger.warning("Validation error: " + e.getMessage());
                request.setAttribute("error", e.getMessage());
                request.setAttribute("errorType", "VALIDATION_ERROR");

            } catch (Exception e) {
                // Handle system errors: catch Exception, log error, set generic error message
                logger.severe("Error creating leave request: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "System error. Please try again later.");
                request.setAttribute("errorType", "SYSTEM_ERROR");
            }

            try {
                // Reload form data (leave types and rules) before forwarding
                LeaveRequestService service = new LeaveRequestService(
                    new RequestDao(),
                    new RequestTypeDao(),
                    new LeaveTypeDao()
                );

                Map<String, String> leaveTypes = service.getAvailableLeaveTypes();
                List<LeaveTypeRules> leaveTypeRules = service.getAllLeaveTypeRules();

                request.setAttribute("leaveTypes", leaveTypes);
                request.setAttribute("leaveTypeRules", leaveTypeRules);

            } catch (Exception e) {
                logger.severe("Error reloading form data: " + e.getMessage());
            }

            // Forward back to leave-form.jsp with success/error message
            request.getRequestDispatcher("/WEB-INF/views/requests/leave-form.jsp")
                   .forward(request, response);

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
