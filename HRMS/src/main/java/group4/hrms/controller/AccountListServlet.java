package group4.hrms.controller;

import group4.hrms.dao.AccountDao;
import group4.hrms.dto.AccountListDto;
import group4.hrms.model.Department;
import group4.hrms.model.Position;
import group4.hrms.util.DropdownCacheUtil;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý Account List page
 * URL: /employees/accounts
 */
@WebServlet("/employees/accounts")
public class AccountListServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AccountListServlet.class);

    private final AccountDao accountDao = new AccountDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("AccountListServlet.doGet() called");

        try {
            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                logger.warn("User not logged in, redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            logger.info("User is logged in, proceeding with account list");

            // Check authorization - Only ADMIN, HRM, HR can access
            String userRoles = SessionUtil.getUserRoles(request);
            // TODO: Implement proper role checking after roles are set in session
            // Temporarily allow all logged-in users for testing
            if (userRoles != null && !hasRequiredRole(userRoles)) {
                request.setAttribute("errorMessage", "You don't have permission to access this page");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }

            // Parse query parameters
            String search = request.getParameter("search");
            String status = request.getParameter("status");
            String departmentIdStr = request.getParameter("department");
            String positionIdStr = request.getParameter("position");
            String pageStr = request.getParameter("page");
            String pageSizeStr = request.getParameter("pageSize");
            String sortBy = request.getParameter("sortBy");
            String sortOrder = request.getParameter("sortOrder");

            // Parse pagination parameters
            int page = parseIntOrDefault(pageStr, 1);
            int pageSize = parseIntOrDefault(pageSizeStr, 20);

            // Validate page size
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 20;
            }

            // Parse filter parameters
            Long departmentId = parseLongOrNull(departmentIdStr);
            Long positionId = parseLongOrNull(positionIdStr);

            // Default sort
            if (sortBy == null || sortBy.trim().isEmpty()) {
                sortBy = "created_at";
            }
            if (sortOrder == null || sortOrder.trim().isEmpty()) {
                sortOrder = "desc";
            }

            // Calculate offset
            int offset = (page - 1) * pageSize;

            // Fetch accounts with filters
            logger.info(
                    "Fetching accounts with filters - search: {}, status: {}, dept: {}, pos: {}, offset: {}, pageSize: {}",
                    search, status, departmentId, positionId, offset, pageSize);

            List<AccountListDto> accountDtos = accountDao.findWithFilters(
                    search, status, departmentId, positionId,
                    offset, pageSize, sortBy, sortOrder);

            logger.info("Found {} accounts", accountDtos.size());

            // Get total count for pagination
            int totalRecords = accountDao.countWithFilters(search, status, departmentId, positionId);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            logger.info("Total records: {}, Total pages: {}", totalRecords, totalPages);

            // Fetch departments and positions for filter dropdowns (using cache)
            List<Department> departments = DropdownCacheUtil.getCachedDepartments(getServletContext());
            List<Position> positions = DropdownCacheUtil.getCachedPositions(getServletContext());

            // Set attributes for JSP
            request.setAttribute("accounts", accountDtos);
            request.setAttribute("departments", departments);
            request.setAttribute("positions", positions);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("search", search);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("selectedDepartment", departmentId);
            request.setAttribute("selectedPosition", positionId);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("sortOrder", sortOrder);

            // Check if user is ADMIN for showing action buttons
            boolean isAdmin = SessionUtil.hasRole(request, "ADMIN");
            request.setAttribute("isAdmin", isAdmin);

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/employees/account-list.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading account list", e);
            request.setAttribute("errorMessage", "An error occurred while loading data. Please try again later.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    /**
     * Check if user has required role (ADMIN, HRM, or HR)
     */
    private boolean hasRequiredRole(String userRoles) {
        if (userRoles == null) {
            return false;
        }
        return userRoles.contains("ADMIN") ||
                userRoles.contains("HRM") ||
                userRoles.contains("HR");
    }

    /**
     * Parse integer parameter with default value
     */
    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse Long parameter or return null
     */
    private Long parseLongOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
