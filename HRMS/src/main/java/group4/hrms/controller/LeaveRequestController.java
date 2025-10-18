
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

        // Check if this is an API call (JSON response)
        String action = request.getParameter("action");
        if ("checkConflict".equals(action)) {
            handleCheckConflict(request, response);
            return;
        }

        // Normal page load
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

            // Load user profile to get gender
            group4.hrms.dao.UserProfileDao userProfileDao = new group4.hrms.dao.UserProfileDao();
            group4.hrms.model.UserProfile userProfile = userProfileDao.findByUserId(user.getId());

            // Debug logging
            logger.info("UserProfile found: " + (userProfile != null));
            if (userProfile != null) {
                logger.info("Raw gender value from DB: '" + userProfile.getGender() + "'");
            }

            // Normalize gender: handle MALE/Male/M and FEMALE/Female/F
            String userGender = "MALE"; // default
            if (userProfile != null && userProfile.getGender() != null) {
                String rawGender = userProfile.getGender().toUpperCase().trim();
                if (rawGender.startsWith("F") || rawGender.equals("FEMALE")) {
                    userGender = "FEMALE";
                } else if (rawGender.startsWith("M") || rawGender.equals("MALE")) {
                    userGender = "MALE";
                }
            }
            logger.info("User gender (normalized): " + userGender);

            logger.info("Loading available leave types...");
            // Load available leave types using service.getAvailableLeaveTypes()
            Map<String, String> allLeaveTypes = service.getAvailableLeaveTypes();

            // Filter leave types based on gender (Maternity only for FEMALE)
            Map<String, String> leaveTypes = new java.util.LinkedHashMap<>();
            for (Map.Entry<String, String> entry : allLeaveTypes.entrySet()) {
                String code = entry.getKey();
                String name = entry.getValue();

                // Filter MATERNITY/MATERNITY_LEAVE for females only
                if ("MATERNITY".equals(code) || "MATERNITY_LEAVE".equals(code)) {
                    if ("FEMALE".equalsIgnoreCase(userGender)) {
                        leaveTypes.put(code, name);
                    }
                }
                // Filter PATERNITY/PATERNITY_LEAVE for males only
                else if ("PATERNITY".equals(code) || "PATERNITY_LEAVE".equals(code)) {
                    if ("MALE".equalsIgnoreCase(userGender)) {
                        leaveTypes.put(code, name);
                    }
                }
                // All other leave types available for everyone
                else {
                    leaveTypes.put(code, name);
                }
            }
            logger.info("Loaded " + (leaveTypes != null ? leaveTypes.size() : 0) + " leave types (filtered by gender)");

            logger.info("Loading leave type rules...");
            // Load leave type rules using service.getAllLeaveTypeRules()
            List<LeaveTypeRules> allLeaveTypeRules = service.getAllLeaveTypeRules();

            // Filter rules based on gender
            List<LeaveTypeRules> leaveTypeRules = new java.util.ArrayList<>();
            for (LeaveTypeRules rules : allLeaveTypeRules) {
                String ruleCode = rules.getCode();
                if ("MATERNITY".equals(ruleCode) || "MATERNITY_LEAVE".equals(ruleCode)) {
                    if ("FEMALE".equalsIgnoreCase(userGender)) {
                        leaveTypeRules.add(rules);
                    }
                } else if ("PATERNITY".equals(ruleCode) || "PATERNITY_LEAVE".equals(ruleCode)) {
                    if ("MALE".equalsIgnoreCase(userGender)) {
                        leaveTypeRules.add(rules);
                    }
                } else {
                    leaveTypeRules.add(rules);
                }
            }
            logger.info("Loaded " + (leaveTypeRules != null ? leaveTypeRules.size() : 0) + " leave type rules (filtered by gender)");

            // Load leave balances from database
            int currentYear = java.time.LocalDateTime.now().getYear();
            logger.info("Loading leave balances for user " + user.getId() + " in year " + currentYear);
            List<group4.hrms.dto.LeaveBalance> allLeaveBalances = service.getAllLeaveBalances(user.getId(), currentYear);
            logger.info("Loaded " + (allLeaveBalances != null ? allLeaveBalances.size() : 0) + " leave balances");

            // Filter leave balances based on gender
            List<group4.hrms.dto.LeaveBalance> leaveBalances = new java.util.ArrayList<>();
            for (group4.hrms.dto.LeaveBalance balance : allLeaveBalances) {
                String balanceCode = balance.getLeaveTypeCode();

                // Filter MATERNITY/MATERNITY_LEAVE for females only
                if ("MATERNITY".equals(balanceCode) || "MATERNITY_LEAVE".equals(balanceCode)) {
                    if ("FEMALE".equalsIgnoreCase(userGender)) {
                        leaveBalances.add(balance);
                    }
                }
                // Filter PATERNITY/PATERNITY_LEAVE for males only
                else if ("PATERNITY".equals(balanceCode) || "PATERNITY_LEAVE".equals(balanceCode)) {
                    if ("MALE".equalsIgnoreCase(userGender)) {
                        leaveBalances.add(balance);
                    }
                }
                // All other leave types available for everyone
                else {
                    leaveBalances.add(balance);
                }
            }
            logger.info("Filtered to " + leaveBalances.size() + " leave balances based on gender: " + userGender);

            // Set leaveTypes, leaveTypeRules and leaveBalances as request attributes
            request.setAttribute("leaveTypes", leaveTypes);
            request.setAttribute("leaveTypeRules", leaveTypeRules);
            request.setAttribute("leaveBalances", leaveBalances);
            request.setAttribute("currentYear", currentYear);
            request.setAttribute("userGender", userGender);

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

            // Extract form parameters (declare outside try for catch block access)
            String leaveTypeCode = request.getParameter("leaveTypeCode");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String reason = request.getParameter("reason");
            String isHalfDayParam = request.getParameter("isHalfDay");
            Boolean isHalfDay = (isHalfDayParam != null && "true".equalsIgnoreCase(isHalfDayParam));
            String halfDayPeriod = request.getParameter("halfDayPeriod");

            try {
                // Debug: Log received parameters
                logger.info("Received parameters: startDate='" + startDateStr + "', endDate='" + endDateStr + "'");
                logger.info("Leave type: " + leaveTypeCode + ", isHalfDay: " + isHalfDay + ", halfDayPeriod: " + halfDayPeriod);

                // Validate required date parameters
                if (startDateStr == null || startDateStr.trim().isEmpty()) {
                    logger.severe("Start date is missing or empty");
                    request.setAttribute("error", "Start date is required");
                    request.getRequestDispatcher("/WEB-INF/views/requests/leave-form.jsp").forward(request, response);
                    return;
                }

                // For half-day leave, endDate should equal startDate (auto-filled by frontend)
                // For full-day leave, endDate is required
                if (!isHalfDay && (endDateStr == null || endDateStr.trim().isEmpty())) {
                    logger.severe("End date is missing or empty for full-day leave");
                    request.setAttribute("error", "End date is required for full-day leave");
                    request.getRequestDispatcher("/WEB-INF/views/requests/leave-form.jsp").forward(request, response);
                    return;
                }

                // If half-day and endDate is missing, set it equal to startDate
                if (isHalfDay && (endDateStr == null || endDateStr.trim().isEmpty())) {
                    endDateStr = startDateStr;
                    logger.info("Half-day leave: auto-set endDate = startDate = " + endDateStr);
                }

                // Validate half-day specific rules
                if (isHalfDay) {
                    if (halfDayPeriod == null || halfDayPeriod.trim().isEmpty()) {
                        group4.hrms.exception.ValidationErrorMessage errorMsg =
                            group4.hrms.exception.ValidationErrorMessage.invalidHalfDayPeriodError(null);
                        throw new LeaveValidationException(errorMsg);
                    }
                    if (!"AM".equals(halfDayPeriod) && !"PM".equals(halfDayPeriod)) {
                        group4.hrms.exception.ValidationErrorMessage errorMsg =
                            group4.hrms.exception.ValidationErrorMessage.invalidHalfDayPeriodError(halfDayPeriod);
                        throw new LeaveValidationException(errorMsg);
                    }
                }

                // Parse date strings to LocalDateTime
                // Form sends date in format "yyyy-MM-dd", need to convert to LocalDateTime
                logger.info("Parsing dates: startDate='" + startDateStr.trim() + "', endDate='" + endDateStr.trim() + "'");
                java.time.LocalDate startLocalDate = java.time.LocalDate.parse(startDateStr.trim());
                java.time.LocalDate endLocalDate = java.time.LocalDate.parse(endDateStr.trim());
                logger.info("Dates parsed successfully: " + startLocalDate + " to " + endLocalDate);

                // Convert to LocalDateTime (start of day)
                java.time.LocalDateTime startDate = startLocalDate.atStartOfDay();
                java.time.LocalDateTime endDate = endLocalDate.atTime(23, 59, 59);

                // Initialize service
                LeaveRequestService service = new LeaveRequestService(
                    new RequestDao(),
                    new RequestTypeDao(),
                    new LeaveTypeDao()
                );

                // Call service.createLeaveRequest() with all parameters including half-day fields
                Long requestId = service.createLeaveRequest(
                    account.getId(),
                    user.getId(),
                    user.getDepartmentId(),
                    leaveTypeCode,
                    startDate,
                    endDate,
                    reason,
                    isHalfDay,
                    halfDayPeriod
                );

                // Handle success: set success message attribute
                request.setAttribute("success", "Leave request submitted successfully! Request ID: " + requestId);

            } catch (LeaveValidationException e) {
                // Handle structured validation errors with detailed formatting
                logger.warning(String.format("Leave validation error: userId=%d, accountId=%d, errorType=%s, message=%s",
                              user.getId(), account.getId(), e.getErrorType(), e.getShortMessage()));

                // Save form data to session to preserve user input
                saveFormDataToSession(session, leaveTypeCode, startDateStr, endDateStr, reason,
                                     isHalfDay, halfDayPeriod);

                // Set error attributes for JSP display
                request.setAttribute("error", e.getMessage());
                request.setAttribute("errorType", e.getErrorType());
                request.setAttribute("errorTitle", e.getShortMessage());
                request.setAttribute("errorDetails", e.getDetailedMessage());

                // Return HTTP 409 for conflict errors (Requirements: 5.2, 5.3, 5.4)
                if ("HALF_DAY_FULL_DAY_CONFLICT".equals(e.getErrorType()) ||
                    "HALF_DAY_SAME_PERIOD_CONFLICT".equals(e.getErrorType())) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT); // HTTP 409
                }
                // Return HTTP 400 for insufficient balance errors (Requirements: 3.5, 3.6, 14.3)
                else if ("INSUFFICIENT_BALANCE".equals(e.getErrorType())) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400
                }
                // Return HTTP 400 for other validation errors (Requirements: 5.7, 2.1, 2.2, 2.3, 14.1, 14.4)
                else if ("HALF_DAY_NON_WORKING_DAY".equals(e.getErrorType())) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400
                }
                // Return HTTP 400 for invalid period errors (Requirements: 2.1, 2.2, 2.3, 14.4)
                else if ("INVALID_HALF_DAY_PERIOD".equals(e.getErrorType())) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400
                }

            } catch (IllegalArgumentException e) {
                // Handle generic validation errors
                logger.warning(String.format("Validation error creating leave request: userId=%d, accountId=%d, error=%s",
                              user.getId(), account.getId(), e.getMessage()));
                saveFormDataToSession(session, leaveTypeCode, startDateStr, endDateStr, reason,
                                     isHalfDay, halfDayPeriod);
                request.setAttribute("error", e.getMessage());
                request.setAttribute("errorType", "VALIDATION_ERROR");

            } catch (java.sql.SQLException e) {
                // Handle database errors
                logger.severe(String.format("Database error creating leave request: userId=%d, accountId=%d, error=%s",
                             user.getId(), account.getId(), e.getMessage()));
                e.printStackTrace();
                saveFormDataToSession(session, leaveTypeCode, startDateStr, endDateStr, reason,
                                     isHalfDay, halfDayPeriod);
                request.setAttribute("error", "Database error occurred. Please try again later. If the problem persists, contact IT support.");
                request.setAttribute("errorType", "DATABASE_ERROR");

            } catch (Exception e) {
                // Handle system errors: catch Exception, log error, set generic error message
                logger.severe(String.format("Unexpected error creating leave request: userId=%d, accountId=%d, error=%s",
                             user.getId(), account.getId(), e.getMessage()));
                saveFormDataToSession(session, leaveTypeCode, startDateStr, endDateStr, reason,
                                     isHalfDay, halfDayPeriod);
                e.printStackTrace();
                request.setAttribute("error", "System error. Please try again later.");
                request.setAttribute("errorType", "SYSTEM_ERROR");
            }

            try {
                // Reload form data (leave types, rules, and balances) before forwarding
                LeaveRequestService service = new LeaveRequestService(
                    new RequestDao(),
                    new RequestTypeDao(),
                    new LeaveTypeDao()
                );

                // Load user profile to get gender for filtering
                group4.hrms.dao.UserProfileDao userProfileDao = new group4.hrms.dao.UserProfileDao();
                group4.hrms.model.UserProfile userProfile = userProfileDao.findByUserId(user.getId());
                String userGender = (userProfile != null && userProfile.getGender() != null)
                    ? userProfile.getGender()
                    : "UNKNOWN";

                // Get all leave types and filter by gender
                Map<String, String> allLeaveTypes = service.getAvailableLeaveTypes();
                Map<String, String> leaveTypes = new java.util.LinkedHashMap<>();
                for (Map.Entry<String, String> entry : allLeaveTypes.entrySet()) {
                    String code = entry.getKey();
                    if ("MATERNITY".equals(code) || "MATERNITY_LEAVE".equals(code)) {
                        if ("FEMALE".equalsIgnoreCase(userGender)) {
                            leaveTypes.put(code, entry.getValue());
                        }
                    } else if ("PATERNITY".equals(code) || "PATERNITY_LEAVE".equals(code)) {
                        if ("MALE".equalsIgnoreCase(userGender)) {
                            leaveTypes.put(code, entry.getValue());
                        }
                    } else {
                        leaveTypes.put(code, entry.getValue());
                    }
                }

                // Get all leave type rules and filter by gender
                List<LeaveTypeRules> allLeaveTypeRules = service.getAllLeaveTypeRules();
                List<LeaveTypeRules> leaveTypeRules = new java.util.ArrayList<>();
                for (LeaveTypeRules rules : allLeaveTypeRules) {
                    String ruleCode = rules.getCode();
                    if ("MATERNITY".equals(ruleCode) || "MATERNITY_LEAVE".equals(ruleCode)) {
                        if ("FEMALE".equalsIgnoreCase(userGender)) {
                            leaveTypeRules.add(rules);
                        }
                    } else if ("PATERNITY".equals(ruleCode) || "PATERNITY_LEAVE".equals(ruleCode)) {
                        if ("MALE".equalsIgnoreCase(userGender)) {
                            leaveTypeRules.add(rules);
                        }
                    } else {
                        leaveTypeRules.add(rules);
                    }
                }

                // Reload leave balances for the current year and filter by gender
                int currentYear = java.time.LocalDate.now().getYear();
                List<group4.hrms.dto.LeaveBalance> allLeaveBalances = service.getAllLeaveBalances(user.getId(), currentYear);
                List<group4.hrms.dto.LeaveBalance> leaveBalances = new java.util.ArrayList<>();
                for (group4.hrms.dto.LeaveBalance balance : allLeaveBalances) {
                    String balanceCode = balance.getLeaveTypeCode();
                    if ("MATERNITY".equals(balanceCode) || "MATERNITY_LEAVE".equals(balanceCode)) {
                        if ("FEMALE".equalsIgnoreCase(userGender)) {
                            leaveBalances.add(balance);
                        }
                    } else if ("PATERNITY".equals(balanceCode) || "PATERNITY_LEAVE".equals(balanceCode)) {
                        if ("MALE".equalsIgnoreCase(userGender)) {
                            leaveBalances.add(balance);
                        }
                    } else {
                        leaveBalances.add(balance);
                    }
                }

                request.setAttribute("leaveTypes", leaveTypes);
                request.setAttribute("leaveTypeRules", leaveTypeRules);
                request.setAttribute("leaveBalances", leaveBalances);
                request.setAttribute("currentYear", currentYear);
                request.setAttribute("userGender", userGender);

            } catch (Exception e) {
                logger.severe("Error reloading form data: " + e.getMessage());
            }

        // Forward back to leave-form.jsp with success/error message
        request.getRequestDispatcher("/WEB-INF/views/requests/leave-form.jsp")
               .forward(request, response);
    }

    /**
     * Handle AJAX request to check half-day conflict
     * Returns JSON response
     *
     * URL: /requests/leave/create?action=checkConflict&date=YYYY-MM-DD&period=AM/PM
     */
    private void handleCheckConflict(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        java.io.PrintWriter out = response.getWriter();
        Map<String, Object> result = new java.util.HashMap<>();

        try {
            // Check authentication
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.put("error", "Not authenticated");
                out.print(new com.google.gson.Gson().toJson(result));
                return;
            }

            User user = (User) session.getAttribute("user");

            // Get parameters
            String dateStr = request.getParameter("date");
            String period = request.getParameter("period");

            // Validate parameters
            if (dateStr == null || dateStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("error", "Date parameter is required");
                out.print(new com.google.gson.Gson().toJson(result));
                return;
            }

            if (period == null || (!"AM".equals(period) && !"PM".equals(period))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("error", "Period must be AM or PM");
                out.print(new com.google.gson.Gson().toJson(result));
                return;
            }

            // Parse date
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);

            // Check conflict
            LeaveRequestService service = new LeaveRequestService(
                new RequestDao(),
                new RequestTypeDao(),
                new LeaveTypeDao()
            );

            group4.hrms.dto.HalfDayConflict conflict = service.checkHalfDayConflict(
                user.getId(), date, period
            );

            // Build response
            result.put("hasConflict", conflict.hasConflict());
            result.put("conflictType", conflict.getConflictType());
            result.put("message", conflict.getErrorMessage());

            if (conflict.hasConflict()) {
                result.put("existingLeaveType", conflict.getConflictLeaveType());
                result.put("existingPeriod", conflict.getConflictPeriod());
                result.put("existingStatus", conflict.getConflictStatus());
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
            }

            logger.info(String.format("Half-day conflict check: userId=%d, hasConflict=%b",
                user.getId(), conflict.hasConflict()));

        } catch (Exception e) {
            logger.severe("Error checking conflict: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("error", "System error occurred");
        }

        out.print(new com.google.gson.Gson().toJson(result));
        out.flush();
    }

    /**
     * Save form data to session to preserve user input when there's an error
     */
    private void saveFormDataToSession(HttpSession session, String leaveTypeCode,
                                      String startDate, String endDate, String reason,
                                      Boolean isHalfDay, String halfDayPeriod) {
        session.setAttribute("formData_leaveTypeCode", leaveTypeCode);
        session.setAttribute("formData_startDate", startDate);
        session.setAttribute("formData_endDate", endDate);
        session.setAttribute("formData_reason", reason);
        session.setAttribute("formData_isHalfDay", isHalfDay);
        session.setAttribute("formData_halfDayPeriod", halfDayPeriod);
    }

}
