package group4.hrms.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import group4.hrms.dao.HolidayDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.AccountDto;
import group4.hrms.dto.OTBalance;
import group4.hrms.dto.UserDto;
import group4.hrms.model.Request;
import group4.hrms.model.User;
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
 *   <li>GET /request/ot?action=create - Display OT request creation form</li>
 *   <li>GET /request/ot?action=list - Display list of user's OT requests</li>
 *   <li>POST /request/ot?action=create - Submit new OT request</li>
 * </ul>
 *
 * <p>The controller integrates with OTRequestService for business logic
 * and forwards to JSP views for presentation.
 *
 * @author Group4
 * @version 1.0
 * @see group4.hrms.service.OTRequestService
 */
@WebServlet("/request/ot")
public class OTRequestController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(OTRequestController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("OTRequestController.doGet() called. Request URI: " + request.getRequestURI());
        String action = request.getParameter("action");
        logger.info("Action parameter: " + action);

        if ("create".equals(action)) {
            showCreateForm(request, response);
        } else if ("list".equals(action)) {
            showOTList(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            handleCreateOT(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
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

        logger.info("Loading OT request form (authentication check disabled for development)...");

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

            // TEMPORARY: Use dummy user ID for development
            // TODO: Get real user from session when login is implemented
            /*
            UserDto user = (UserDto) session.getAttribute("user");
            Long userId = user.getId();
            */
            Long userId = 1L; // Mock user ID

            logger.info("Loading OT balance for user " + userId);
            // Load OT balance for the user (Requirement 8)
            OTBalance otBalance = service.getOTBalance(userId);
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

            // TODO: Load department employees if user is manager (Requirement 9)
            // For now, we'll skip this and implement in a future iteration
            /*
            if (user.isManager()) {
                List<User> departmentEmployees = service.getDepartmentEmployees(user.getDepartmentId());
                request.setAttribute("departmentEmployees", departmentEmployees);
            }
            */

            logger.info("Forwarding to ot-form.jsp...");
            // Forward to ot-form.jsp
            request.getRequestDispatcher("/WEB-INF/views/requests/ot-form.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            logger.severe("Error loading OT request form: " + e.getMessage());
            e.printStackTrace();

            // Send detailed error response
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Error Loading OT Request Form</h1>");
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
            response.getWriter().println("<pre>");
            e.printStackTrace(response.getWriter());
            response.getWriter().println("</pre>");
            response.getWriter().println("<p><a href='" + request.getContextPath() + "/dashboard'>Back to Dashboard</a></p>");
            response.getWriter().println("</body></html>");
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

            // Extract form parameters
            String otDate = request.getParameter("otDate");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String reason = request.getParameter("reason");
            String employeeConsentStr = request.getParameter("employeeConsent");

            // Parse employee consent checkbox
            Boolean employeeConsent = "on".equals(employeeConsentStr) || "true".equals(employeeConsentStr);

            logger.info("Creating OT request: date=" + otDate + ", start=" + startTime +
                ", end=" + endTime + ", consent=" + employeeConsent);

            // Initialize service
            OTRequestService service = new OTRequestService(
                new RequestDao(),
                new RequestTypeDao(),
                new HolidayDao(),
                new group4.hrms.dao.HolidayCalendarDao(),
                new UserDao()
            );

            // Call service.createOTRequest() with all parameters
            Long requestId = service.createOTRequest(
                account.getId(),
                user.getId(),
                user.getDepartmentId(),
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
            logger.warning("Validation error: " + e.getMessage());
            request.setAttribute("error", e.getMessage());

        } catch (Exception e) {
            // Handle system errors: catch Exception, log error, set generic error message
            logger.severe("Error creating OT request: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "System error. Please try again later.");
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

            Long userId = 1L; // Mock user ID
            OTBalance otBalance = service.getOTBalance(userId);
            request.setAttribute("otBalance", otBalance);

        } catch (Exception e) {
            logger.severe("Error reloading OT balance: " + e.getMessage());
        }

        // Forward back to ot-form.jsp with success/error message
        request.getRequestDispatcher("/WEB-INF/views/requests/ot-form.jsp")
               .forward(request, response);
    }

    /**
     * Displays the list of OT requests for the current user.
     *
     * <p>Loads all OT requests (PENDING, APPROVED, REJECTED) for the user
     * and sets them as request attribute.
     *
     * <p>Forwards to: /WEB-INF/views/requests/ot-list.jsp
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    private void showOTList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

        logger.info("Loading OT request list (authentication check disabled for development)...");

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

            // TEMPORARY: Use dummy user ID for development
            // TODO: Get real user from session when login is implemented
            /*
            UserDto user = (UserDto) session.getAttribute("user");
            Long userId = user.getId();
            */
            Long userId = 1L; // Mock user ID

            logger.info("Loading OT requests for user " + userId);
            // Load OT requests for the user
            List<Request> otRequests = service.getUserOTRequests(userId);
            logger.info("Loaded " + (otRequests != null ? otRequests.size() : 0) + " OT requests");

            // Set OT requests as request attribute
            request.setAttribute("otRequests", otRequests);

            logger.info("Forwarding to ot-list.jsp...");
            // Forward to ot-list.jsp
            request.getRequestDispatcher("/WEB-INF/views/requests/ot-list.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            logger.severe("Error loading OT request list: " + e.getMessage());
            e.printStackTrace();

            // Send detailed error response
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Error Loading OT Request List</h1>");
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
            response.getWriter().println("<pre>");
            e.printStackTrace(response.getWriter());
            response.getWriter().println("</pre>");
            response.getWriter().println("<p><a href='" + request.getContextPath() + "/dashboard'>Back to Dashboard</a></p>");
            response.getWriter().println("</body></html>");
        }
    }
}
