
package group4.hrms.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import group4.hrms.dao.LeaveTypeDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.model.Account;
import group4.hrms.model.User;
import group4.hrms.service.LeaveRequestService;
import group4.hrms.service.LeaveRequestService.LeaveTypeRules;
import group4.hrms.exception.LeaveValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for handling leave request operations.
 * Manages leave request creation.
 * Integrates with LeaveRequestService for business logic and validation.
 *
 * Supported URLs:
 * - GET /requests/leave/create - Display leave request creation form
 * - POST /requests/leave/create - Process leave request submission
 *
 * @author HRMS Development Team
 * @version 1.0
 */
@WebServlet("/requests/leave/create")
public class LeaveRequestController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LeaveRequestController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("LeaveRequestController.doGet() called. Request URI: " + request.getRequestURI());
        logger.info("=== LEAVE REQUEST CREATE ACTION START ===");

        // Check authentication
        HttpSession session = request.getSession(false);
            logger.info("Session exists: " + (session != null));

            if (session == null) {
                logger.warning("Session is null. Redirecting to login.");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            Object accountObj = session.getAttribute("account");
            Object userObj = session.getAttribute("user");
            logger.info("Account in session: " + (accountObj != null));
            logger.info("User in session: " + (userObj != null));

        if (accountObj == null) {
            logger.warning("Account not found in session. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Account account = (Account) accountObj;
        User user = (User) userObj;

        if (user == null) {
            logger.warning("User not found in session. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        logger.info("Loading leave request form for user: " + user.getId() + " (account: " + account.getId() + ")");

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

            // Load leave balances from database
            int currentYear = java.time.LocalDateTime.now().getYear();
            logger.info("Loading leave balances for user " + user.getId() + " in year " + currentYear);
            List<group4.hrms.dto.LeaveBalance> leaveBalances = service.getAllLeaveBalances(user.getId(), currentYear);
            logger.info("Loaded " + (leaveBalances != null ? leaveBalances.size() : 0) + " leave balances");

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
            logger.severe(String.format("Unexpected error loading leave request form: userId=%d, error=%s",
                         user.getId(), e.getMessage()));
            e.printStackTrace();

            // Set error message and forward to error page or dashboard
            request.setAttribute("error", "System error occurred while loading the form. Please try again later.");
            request.setAttribute("errorDetails", e.getMessage());

            // Try to forward to dashboard instead of sending HTML directly
            try {
                request.getRequestDispatcher("/dashboard").forward(request, response);
            } catch (Exception forwardError) {
                // If forward fails, send simple error response
                if (!response.isCommitted()) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "System error occurred. Please contact support.");
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            // Check authentication
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("account") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            group4.hrms.model.Account account = (group4.hrms.model.Account) session.getAttribute("account");
            group4.hrms.model.User user = (group4.hrms.model.User) session.getAttribute("user");

            if (account == null || user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            try {

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
                logger.warning(String.format("Leave validation error: userId=%d, accountId=%d, errorType=%s, message=%s",
                              user.getId(), account.getId(), e.getErrorType(), e.getShortMessage()));

                // Set error attributes for JSP display
                request.setAttribute("error", e.getMessage());
                request.setAttribute("errorType", e.getErrorType());
                request.setAttribute("errorTitle", e.getShortMessage());
                request.setAttribute("errorDetails", e.getDetailedMessage());

            } catch (IllegalArgumentException e) {
                // Handle generic validation errors
                logger.warning(String.format("Validation error creating leave request: userId=%d, accountId=%d, error=%s",
                              user.getId(), account.getId(), e.getMessage()));
                request.setAttribute("error", e.getMessage());
                request.setAttribute("errorType", "VALIDATION_ERROR");

            } catch (java.sql.SQLException e) {
                // Handle database errors
                logger.severe(String.format("Database error creating leave request: userId=%d, accountId=%d, error=%s",
                             user.getId(), account.getId(), e.getMessage()));
                e.printStackTrace();
                request.setAttribute("error", "Database error occurred. Please try again later. If the problem persists, contact IT support.");
                request.setAttribute("errorType", "DATABASE_ERROR");

            } catch (Exception e) {
                // Handle system errors: catch Exception, log error, set generic error message
                logger.severe(String.format("Unexpected error creating leave request: userId=%d, accountId=%d, error=%s",
                             user.getId(), account.getId(), e.getMessage()));
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
    }

}
