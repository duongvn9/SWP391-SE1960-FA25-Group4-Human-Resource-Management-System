package group4.hrms.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import group4.hrms.dao.HolidayDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Account;
import group4.hrms.model.User;
import group4.hrms.dto.OTBalance;
import group4.hrms.service.OTRequestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for handling OT (Overtime) Request operations.
 *
 * <p>Handles HTTP requests for:
 * <ul>
 *   <li>GET /requests/ot/create - Display OT request creation form</li>
 *   <li>POST /requests/ot/create - Submit new OT request</li>
 * </ul>
 *
 * <p>The controller integrates with OTRequestService for business logic
 * and forwards to JSP views for presentation.
 *
 * @author Group4
 * @version 1.0
 * @see group4.hrms.service.OTRequestService
 */
@WebServlet("/requests/ot/create")
public class OTRequestController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(OTRequestController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("OTRequestController.doGet() called. Request URI: " + request.getRequestURI());
        logger.info("=== OT REQUEST CREATE ACTION START ===");
        showCreateForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleCreateOT(request, response);
    }

    /**
     * Displays the OT request creation form.
     *
     * <p>Loads and sets the following request attributes:
     * <ul>
     *   <li>otBalance - Current OT balance (weekly, monthly, annual)</li>
     *   <li>departmentEmployees - List of employees (if user is manager)</li>
     * </ul>
     *
     * <p>Forwards to: /WEB-INF/views/requests/ot-form.jsp
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

        logger.info("Loading OT request form for user: " + user.getId() + " (account: " + account.getId() + ")");

        try {
            logger.info("Initializing OTRequestService...");

            // Initialize OTRequestService with required DAOs
            OTRequestService service = new OTRequestService(
                new RequestDao(),
                new RequestTypeDao(),
                new HolidayDao(),
                new group4.hrms.dao.HolidayCalendarDao(),
                new UserDao()
            );

            logger.info("Loading OT balance for user " + user.getId());
            // Load OT balance for the user (Requirement 8)
            OTBalance otBalance = service.getOTBalance(user.getId());
            logger.info("Loaded OT balance: Week=" + otBalance.getCurrentWeekHours() + "h, Month="
                + otBalance.getMonthlyHours() + "h, Annual=" + otBalance.getAnnualHours() + "h");

            // Set OT balance as request attribute
            request.setAttribute("otBalance", otBalance);

            // Load holidays for current year and next 2 years to pass to JavaScript
            logger.info("Loading holidays for current and future years...");
            int currentYear = java.time.Year.now().getValue();

            java.util.List<String> allHolidays = new java.util.ArrayList<>();
            java.util.List<String> allCompensatoryDays = new java.util.ArrayList<>();

            // Load holidays for current year, next year, and year after
            // This ensures users can create OT requests for future dates
            for (int year = currentYear; year <= currentYear + 2; year++) {
                allHolidays.addAll(service.getHolidaysForYear(year));
                allCompensatoryDays.addAll(service.getCompensatoryDaysForYear(year));
            }

            logger.info("Loaded " + allHolidays.size() + " holidays and "
                + allCompensatoryDays.size() + " compensatory days for years "
                + currentYear + "-" + (currentYear + 2));
            request.setAttribute("holidays", allHolidays);
            request.setAttribute("compensatoryDays", allCompensatoryDays);

            // Load subordinates if user has management privileges
            // Based on job_level: lower number = higher authority
            // ADMIN (1) > HR_MANAGER (2) > HR_STAFF (3) > DEPT_MANAGER (4) > STAFF (5)
            logger.info("Loading subordinates for user " + user.getId());
            try {
                UserDao userDao = new UserDao();
                List<User> subordinates = userDao.getSubordinates(user.getId());
                if (subordinates != null && !subordinates.isEmpty()) {
                    request.setAttribute("departmentEmployees", subordinates);
                    logger.info("Loaded " + subordinates.size() + " subordinates for user " + user.getId());
                } else {
                    logger.info("No subordinates found for user " + user.getId());
                }
            } catch (Exception e) {
                logger.warning("Error loading subordinates: " + e.getMessage());
                // Continue without subordinates - user can still create for themselves
            }

            logger.info("Forwarding to ot-form.jsp...");
            // Forward to ot-form.jsp
            request.getRequestDispatcher("/WEB-INF/views/requests/ot-form.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            logger.severe(String.format("Unexpected error loading OT request form: userId=%d, error=%s",
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

    /**
     * Handles OT request creation form submission.
     *
     * <p>Extracts form parameters and calls OTRequestService to create the request.
     * Sets success or error message as request attribute and forwards back to form.
     *
     * <p>Form parameters:
     * <ul>
     *   <li>otDate - Date of overtime (yyyy-MM-dd)</li>
     *   <li>startTime - Start time (HH:mm)</li>
     *   <li>endTime - End time (HH:mm)</li>
     *   <li>reason - Reason for overtime</li>
     *   <li>employeeConsent - Consent checkbox ("on" or "true")</li>
     * </ul>
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    private void handleCreateOT(HttpServletRequest request, HttpServletResponse response)
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
        String requestFor = request.getParameter("requestFor");
        String selectedEmployeeIdStr = request.getParameter("selectedEmployeeId");
        String otDate = request.getParameter("otDate");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String reason = request.getParameter("reason");
        String employeeConsentStr = request.getParameter("employeeConsent");
        Boolean employeeConsent = "on".equals(employeeConsentStr) || "true".equals(employeeConsentStr);

        try {
            // Determine target user (self or subordinate)
            Long targetUserId = user.getId();
            Long targetDepartmentId = user.getDepartmentId();

            if ("employee".equals(requestFor) && selectedEmployeeIdStr != null && !selectedEmployeeIdStr.trim().isEmpty()) {
                // Creating for subordinate
                final Long selectedUserId = Long.parseLong(selectedEmployeeIdStr);
                targetUserId = selectedUserId;

                // Verify the selected employee is actually a subordinate
                final UserDao userDaoForValidation = new UserDao();
                java.util.List<User> subordinates = userDaoForValidation.getSubordinates(user.getId());
                boolean isValidSubordinate = subordinates.stream()
                    .anyMatch(sub -> sub.getId().equals(selectedUserId));

                if (!isValidSubordinate) {
                    throw new IllegalArgumentException("You can only create OT requests for your subordinates");
                }

                // Get target employee's department
                java.util.Optional<User> targetUserOpt = userDaoForValidation.findById(selectedUserId);
                if (targetUserOpt.isPresent()) {
                    targetDepartmentId = targetUserOpt.get().getDepartmentId();
                } else {
                    throw new IllegalArgumentException("Selected employee not found");
                }

                logger.info("Manager " + user.getId() + " creating OT request for employee " + targetUserId);
            } else {
                logger.info("User " + user.getId() + " creating OT request for themselves");
            }

            logger.info("Creating OT request: targetUser=" + targetUserId + ", date=" + otDate +
                ", start=" + startTime + ", end=" + endTime + ", consent=" + employeeConsent);

            // Initialize service
            OTRequestService service = new OTRequestService(
                new RequestDao(),
                new RequestTypeDao(),
                new HolidayDao(),
                new group4.hrms.dao.HolidayCalendarDao(),
                new UserDao()
            );

            // Call service.createOTRequest() with target user parameters
            Long requestId = service.createOTRequest(
                account.getId(),
                targetUserId,
                targetDepartmentId,
                otDate,
                startTime,
                endTime,
                reason,
                employeeConsent
            );

            // Handle success: set success message attribute
            logger.info("OT request created successfully with ID: " + requestId);
            request.setAttribute("success", "OT request submitted successfully! Request ID: " + requestId);

        } catch (IllegalArgumentException e) {
            // Handle validation errors: catch IllegalArgumentException and set error message
            logger.warning(String.format("OT validation error: userId=%d, accountId=%d, error=%s",
                          user.getId(), account.getId(), e.getMessage()));
            saveFormDataToSession(session, otDate, startTime, endTime, reason);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("errorType", "VALIDATION_ERROR");

        } catch (java.sql.SQLException e) {
            // Handle database errors
            logger.severe(String.format("Database error creating OT request: userId=%d, accountId=%d, error=%s",
                         user.getId(), account.getId(), e.getMessage()));
            e.printStackTrace();
            saveFormDataToSession(session, otDate, startTime, endTime, reason);
            request.setAttribute("error", "Database error occurred. Please try again later. If the problem persists, contact IT support.");
            request.setAttribute("errorType", "DATABASE_ERROR");

        } catch (Exception e) {
            // Handle system errors: catch Exception, log error, set generic error message
            logger.severe(String.format("Unexpected error creating OT request: userId=%d, accountId=%d, error=%s",
                         user.getId(), account.getId(), e.getMessage()));
            e.printStackTrace();
            saveFormDataToSession(session, otDate, startTime, endTime, reason);
            request.setAttribute("error", "System error. Please try again later.");
            request.setAttribute("errorType", "SYSTEM_ERROR");
        }

        try {
            // Reload OT balance before forwarding
            OTRequestService service = new OTRequestService(
                new RequestDao(),
                new RequestTypeDao(),
                new HolidayDao(),
                new group4.hrms.dao.HolidayCalendarDao(),
                new UserDao()
            );

            OTBalance otBalance = service.getOTBalance(user.getId());
            request.setAttribute("otBalance", otBalance);

        } catch (Exception e) {
            logger.severe("Error reloading OT balance: " + e.getMessage());
        }

        // Forward back to ot-form.jsp with success/error message
        request.getRequestDispatcher("/WEB-INF/views/requests/ot-form.jsp")
               .forward(request, response);
    }

    /**
     * Save form data to session to preserve user input when there's an error
     */
    private void saveFormDataToSession(HttpSession session, String otDate,
                                      String startTime, String endTime, String reason) {
        session.setAttribute("formData_otDate", otDate);
        session.setAttribute("formData_startTime", startTime);
        session.setAttribute("formData_endTime", endTime);
        session.setAttribute("formData_reason", reason);
    }
}
