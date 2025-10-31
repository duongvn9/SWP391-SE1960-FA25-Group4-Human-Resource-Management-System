package group4.hrms.controller;

import group4.hrms.dao.AccountDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.AccountListDto;
import group4.hrms.model.Department;
import group4.hrms.model.Position;
import group4.hrms.model.User;
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
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final AccountDao accountDao = new AccountDao();
    private final UserDao userDao = new UserDao();

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

            // Check authorization - Only ADMIN can access account list
            String positionCode = group4.hrms.util.PermissionUtil.getCurrentUserPositionCode(request);
            boolean canView = group4.hrms.util.PermissionUtil.canViewAccountList(request);
            logger.info("Position code: {}, canViewAccountList: {}", positionCode, canView);

            if (!canView) {
                logger.warn("User does not have permission to view account list");
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
            int pageSize = parseIntOrDefault(pageSizeStr, DEFAULT_PAGE_SIZE);

            // Validate page size
            if (pageSize < 1 || pageSize > 100) {
                pageSize = DEFAULT_PAGE_SIZE;
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

            // Measure query execution time
            long queryStartTime = System.currentTimeMillis();

            List<AccountListDto> accountDtos = accountDao.findWithFilters(
                    search, status, departmentId, positionId,
                    offset, pageSize, sortBy, sortOrder);

            // Get total count for pagination
            int totalRecords = accountDao.countWithFilters(search, status, departmentId, positionId);

            long queryEndTime = System.currentTimeMillis();
            long queryExecutionTime = queryEndTime - queryStartTime;

            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            logger.info("Found {} accounts out of {} total records in {} ms",
                    accountDtos.size(), totalRecords, queryExecutionTime);
            logger.info("Total pages: {}, Main query execution time: {} ms", totalPages, queryExecutionTime);

            // Log performance metrics for monitoring
            logger.info("Performance metrics - Main query: {} ms, Page: {}, PageSize: {}, TotalRecords: {}",
                    queryExecutionTime, page, pageSize, totalRecords);

            // Log performance warning if query is slow
            if (queryExecutionTime > 1000) {
                logger.warn("Slow query detected: {} ms for account list query with filters", queryExecutionTime);
            }

            // Fetch departments and positions for filter dropdowns using DropdownCacheUtil
            // This replaces direct DAO calls with cached data for performance optimization
            long cacheStartTime = System.currentTimeMillis();

            List<Department> departments = DropdownCacheUtil.getDepartments(getServletContext());
            List<Position> positions = DropdownCacheUtil.getPositions(getServletContext());

            long cacheEndTime = System.currentTimeMillis();
            long cacheExecutionTime = cacheEndTime - cacheStartTime;

            // Log cache performance and statistics
            logger.info("Cache lookup completed in {} ms", cacheExecutionTime);

            // Log detailed cache statistics for monitoring cache hit rate
            String departmentsCacheStats = DropdownCacheUtil.getDepartmentsCacheStats(getServletContext());
            String positionsCacheStats = DropdownCacheUtil.getPositionsCacheStats(getServletContext());

            logger.info("Departments cache statistics: {}", departmentsCacheStats);
            logger.info("Positions cache statistics: {}", positionsCacheStats);

            // Log cache validity status
            boolean isDeptCacheValid = DropdownCacheUtil.isDepartmentsCacheValid(getServletContext());
            boolean isPosCacheValid = DropdownCacheUtil.isPositionsCacheValid(getServletContext());
            logger.debug("Cache validity - Departments: {}, Positions: {}", isDeptCacheValid, isPosCacheValid);

            // Performance warning for slow cache operations
            if (cacheExecutionTime > 100) {
                logger.warn("Slow cache operation detected: {} ms for dropdown cache lookup", cacheExecutionTime);
            }

            // Lazy loading for users without account - only load when requested
            List<User> usersWithoutAccount = null;
            int usersWithoutAccountTotal = 0;
            String loadUsers = request.getParameter("loadUsers");
            String limitStr = request.getParameter("limit");

            if ("true".equals(loadUsers)) {
                // Parse limit parameter (default to 10, max 50)
                int limit = parseIntOrDefault(limitStr, 10);
                if (limit > 50)
                    limit = 50;

                long usersQueryStartTime = System.currentTimeMillis();
                usersWithoutAccount = userDao.findUsersWithoutAccountLimited(limit);

                // Get total count for display
                List<User> allUsersWithoutAccount = userDao.findUsersWithoutAccount();
                usersWithoutAccountTotal = allUsersWithoutAccount.size();

                long usersQueryEndTime = System.currentTimeMillis();
                long usersQueryTime = usersQueryEndTime - usersQueryStartTime;

                logger.info("Lazy loaded {} users without account (total: {}, limit: {}) in {} ms",
                        usersWithoutAccount.size(), usersWithoutAccountTotal, limit, usersQueryTime);

                // Log performance warning if users query is slow
                if (usersQueryTime > 500) {
                    logger.warn("Slow query detected: {} ms for users without account query", usersQueryTime);
                }
            } else {
                logger.debug("Users without account not requested - skipping query for performance");
            }

            // Set attributes for JSP
            request.setAttribute("accounts", accountDtos);
            request.setAttribute("departments", departments);
            request.setAttribute("positions", positions);
            request.setAttribute("usersWithoutAccount", usersWithoutAccount);
            request.setAttribute("usersWithoutAccountTotal", usersWithoutAccountTotal);
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

            // Set permissions for UI
            request.setAttribute("canCreateAccount", group4.hrms.util.PermissionUtil.canCreateAccount(request));
            request.setAttribute("canResetPassword", group4.hrms.util.PermissionUtil.canResetPassword(request));

            // Check if user is admin (for edit button visibility)
            boolean isAdmin = group4.hrms.util.PermissionUtil.POSITION_ADMIN.equals(positionCode);
            request.setAttribute("isAdmin", isAdmin);
            logger.info("User position code: {}, isAdmin: {}", positionCode, isAdmin);

            // Log final performance summary
            long totalRequestTime = System.currentTimeMillis() - queryStartTime;
            logger.info(
                    "AccountListServlet request completed - Total time: {} ms, Query time: {} ms, Cache time: {} ms",
                    totalRequestTime, queryExecutionTime, cacheExecutionTime);

            // Performance optimization verification
            if (totalRequestTime < 2000) {
                logger.debug("Performance target met: Request completed in {} ms (< 2000ms target)", totalRequestTime);
            } else {
                logger.warn("Performance target missed: Request took {} ms (> 2000ms target)", totalRequestTime);
            }

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
