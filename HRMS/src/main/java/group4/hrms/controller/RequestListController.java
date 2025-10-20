package group4.hrms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.RequestListFilter;
import group4.hrms.dto.RequestListResult;
import group4.hrms.model.Account;
import group4.hrms.model.Position;
import group4.hrms.model.RequestType;
import group4.hrms.model.User;
import group4.hrms.service.RequestListService;
import group4.hrms.util.RequestListPermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for Request List page
 * Handles displaying and filtering requests with pagination
 * Supports different scopes: my requests, subordinate requests, all requests
 *
 * Mapped URLs:
 * - GET /requests - Display request list page
 * - GET /requests/list - Alias for /requests
 * - POST /requests/delete - Soft delete a request (AJAX)
 *
 * Requirements: All requirements from request-list-page spec
 *
 * @author HRMS Development Team
 * @version 1.0
 */
@WebServlet({"/requests", "/requests/list"})
public class RequestListController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RequestListController.class.getName());

    private static final int DEFAULT_PAGE_SIZE = 8;
    private static final int DEFAULT_PAGE = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("RequestListController.doGet() called. Request URI: " + request.getRequestURI());
        logger.info("=== REQUEST LIST PAGE ACTION START ===");

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

        if (accountObj == null || userObj == null) {
            logger.warning("Account or User not found in session. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Account account = (Account) accountObj;
        User user = (User) userObj;

        logger.info("Loading request list for user: " + user.getId() + " (account: " + account.getId() + ")");

        try {
            // 1. Parse filter parameters from request
            RequestListFilter filter = parseFilterParameters(request);
            logger.info("Parsed filter: " + filter);

            // 2. Get user's position for permission checks
            Position position = getUserPosition(user);
            if (position == null) {
                logger.warning("Position not found for user: " + user.getId());
                request.setAttribute("error", "User position not found. Please contact administrator.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }

            // 3. Check user permissions and get available scopes
            Set<String> availableScopes = RequestListPermissionHelper.getAvailableScopes(position);
            logger.info("Available scopes for user " + user.getId() + ": " + availableScopes);

            // If no scope specified, use default
            if (filter.getScope() == null || filter.getScope().trim().isEmpty()) {
                String defaultScope = RequestListPermissionHelper.getDefaultScope(position);
                filter.setScope(defaultScope);
                logger.info("No scope specified, using default: " + defaultScope);
            }

            // 4. Initialize service and DAOs
            RequestListService service = new RequestListService(
                new RequestDao(),
                new UserDao(),
                new DepartmentDao()
            );

            // 5. Call service to get filtered requests
            String contextPath = request.getContextPath();
            RequestListResult result = service.getRequestList(filter, user, position, account.getId(), contextPath);
            logger.info("Retrieved request list: " + result);

            // 5.1. Get request type statistics (for statistics cards)
            Map<Long, Integer> typeStatistics = service.getRequestTypeStatistics(filter, user, position);
            logger.info("Retrieved type statistics: " + typeStatistics);

            // 6. Load request types for filter dropdown
            RequestTypeDao requestTypeDao = new RequestTypeDao();
            List<RequestType> requestTypes = requestTypeDao.findAll();
            logger.info("Loaded " + requestTypes.size() + " request types");

            // 7. Load employees for employee filter (if user has permission)
            if (availableScopes.contains("subordinate") || availableScopes.contains("all")) {
                List<User> employees = loadEmployeesForFilter(user, position);
                request.setAttribute("employees", employees);
                logger.info("Loaded " + employees.size() + " employees for filter");
            }

            // 8. Set attributes for JSP
            request.setAttribute("result", result);
            request.setAttribute("filter", filter);
            request.setAttribute("availableScopes", availableScopes);
            request.setAttribute("requestTypes", requestTypes);
            request.setAttribute("canExport", RequestListPermissionHelper.canExport(position));
            request.setAttribute("typeStatistics", typeStatistics);

            logger.info("Forwarding to request-list.jsp...");
            // 9. Forward to JSP view
            request.getRequestDispatcher("/WEB-INF/views/requests/request-list.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error loading request list: userId=%d, error=%s",
                       user.getId(), e.getMessage()), e);

            // Set error message and forward to error page
            request.setAttribute("error", "System error occurred while loading request list. Please try again later.");
            request.setAttribute("errorDetails", e.getMessage());

            try {
                request.getRequestDispatcher("/dashboard").forward(request, response);
            } catch (Exception forwardError) {
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
        logger.info("RequestListController.doPost() called");

        // Check if this is a delete action
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            handleDeleteRequest(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
        }
    }

    /**
     * Handle AJAX delete request
     * Performs soft delete by changing status to CANCELLED
     *
     * Requirement: 10
     */
    private void handleDeleteRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            // Check authentication
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.put("success", false);
                result.put("message", "Not authenticated");
                out.print(new Gson().toJson(result));
                return;
            }

            User user = (User) session.getAttribute("user");

            // Get request ID parameter
            String requestIdStr = request.getParameter("requestId");
            if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Request ID is required");
                out.print(new Gson().toJson(result));
                return;
            }

            Long requestId;
            try {
                requestId = Long.parseLong(requestIdStr);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Invalid request ID format");
                out.print(new Gson().toJson(result));
                return;
            }

            // Initialize service
            RequestListService service = new RequestListService(
                new RequestDao(),
                new UserDao(),
                new DepartmentDao()
            );

            // Perform soft delete
            boolean success = service.softDeleteRequest(requestId, user);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                result.put("success", true);
                result.put("message", "Request deleted successfully");
                logger.info(String.format("Request deleted successfully: requestId=%d, userId=%d",
                           requestId, user.getId()));
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                result.put("success", false);
                result.put("message", "You are not authorized to delete this request or the request cannot be deleted");
                logger.warning(String.format("Failed to delete request: requestId=%d, userId=%d",
                              requestId, user.getId()));
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "System error occurred while deleting request");
        }

        out.print(new Gson().toJson(result));
        out.flush();
    }

    /**
     * Parse filter parameters from HTTP request
     *
     * Requirements: 4, 5, 6, 6.1, 7, 8, 9
     */
    private RequestListFilter parseFilterParameters(HttpServletRequest request) {
        RequestListFilter filter = new RequestListFilter();

        // Scope filter (Requirement 4)
        String scope = request.getParameter("scope");
        if (scope != null && !scope.trim().isEmpty()) {
            filter.setScope(scope.trim());
        }

        // Request type filter (Requirement 5)
        String typeStr = request.getParameter("type");
        if (typeStr != null && !typeStr.trim().isEmpty() && !"all".equalsIgnoreCase(typeStr)) {
            try {
                filter.setRequestTypeId(Long.parseLong(typeStr));
            } catch (NumberFormatException e) {
                logger.warning("Invalid request type ID: " + typeStr);
            }
        }

        // Status filter (Requirement 6)
        String status = request.getParameter("status");
        if (status != null && !status.trim().isEmpty()) {
            filter.setStatus(status.trim());
        }

        // Show cancelled toggle (Requirement 6.1)
        String showCancelledStr = request.getParameter("showCancelled");
        filter.setShowCancelled("true".equalsIgnoreCase(showCancelledStr) || "on".equalsIgnoreCase(showCancelledStr));

        // Date range filter (Requirement 7)
        String fromDateStr = request.getParameter("fromDate");
        if (fromDateStr != null && !fromDateStr.trim().isEmpty()) {
            try {
                filter.setFromDate(LocalDate.parse(fromDateStr.trim()));
            } catch (DateTimeParseException e) {
                logger.warning("Invalid from date format: " + fromDateStr);
            }
        }

        String toDateStr = request.getParameter("toDate");
        if (toDateStr != null && !toDateStr.trim().isEmpty()) {
            try {
                filter.setToDate(LocalDate.parse(toDateStr.trim()));
            } catch (DateTimeParseException e) {
                logger.warning("Invalid to date format: " + toDateStr);
            }
        }

        // Employee filter (Requirement 8)
        String employeeIdStr = request.getParameter("employeeId");
        if (employeeIdStr != null && !employeeIdStr.trim().isEmpty()) {
            try {
                filter.setEmployeeId(Long.parseLong(employeeIdStr));
            } catch (NumberFormatException e) {
                logger.warning("Invalid employee ID: " + employeeIdStr);
            }
        }

        // Search filter (Requirement 9)
        String search = request.getParameter("search");
        if (search != null && !search.trim().isEmpty()) {
            filter.setSearchKeyword(search.trim());
        }

        // Pagination
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                filter.setPage(Integer.parseInt(pageStr));
            } catch (NumberFormatException e) {
                logger.warning("Invalid page number: " + pageStr);
                filter.setPage(DEFAULT_PAGE);
            }
        } else {
            filter.setPage(DEFAULT_PAGE);
        }

        String pageSizeStr = request.getParameter("pageSize");
        if (pageSizeStr != null && !pageSizeStr.trim().isEmpty()) {
            try {
                filter.setPageSize(Integer.parseInt(pageSizeStr));
            } catch (NumberFormatException e) {
                logger.warning("Invalid page size: " + pageSizeStr);
                filter.setPageSize(DEFAULT_PAGE_SIZE);
            }
        } else {
            filter.setPageSize(DEFAULT_PAGE_SIZE);
        }

        return filter;
    }

    /**
     * Get user's position from database
     *
     * Requirement: 14
     */
    private Position getUserPosition(User user) {
        if (user.getPositionId() == null) {
            return null;
        }

        try {
            group4.hrms.dao.PositionDao positionDao = new group4.hrms.dao.PositionDao();
            Optional<Position> positionOpt = positionDao.findById(user.getPositionId());
            return positionOpt.orElse(null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading position for user: " + user.getId(), e);
            return null;
        }
    }

    /**
     * Load employees for employee filter dropdown
     * Based on user's permissions:
     * - Managers see their subordinates
     * - HR staff see all employees
     *
     * Requirement: 8
     */
    private List<User> loadEmployeesForFilter(User currentUser, Position position) {
        List<User> employees = new ArrayList<>();

        try {
            UserDao userDao = new UserDao();

            // If user can see all requests, load all employees
            if (RequestListPermissionHelper.getAvailableScopes(position).contains("all")) {
                employees = userDao.findAll();
                logger.info("Loaded all employees for HR user: " + currentUser.getId());
            }
            // Otherwise, load subordinates
            else {
                List<Long> subordinateIds = userDao.findSubordinateUserIds(currentUser.getId());
                for (Long subordinateId : subordinateIds) {
                    Optional<User> subordinateOpt = userDao.findById(subordinateId);
                    subordinateOpt.ifPresent(employees::add);
                }
                logger.info("Loaded " + employees.size() + " subordinates for manager: " + currentUser.getId());
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading employees for filter", e);
        }

        return employees;
    }
}
