package group4.hrms.controller;

import group4.hrms.dao.UserDao;
import group4.hrms.dto.UserListDto;
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
 * Servlet xử lý User List page
 * URL: /employees/users
 */
@WebServlet("/employees/users")
public class UserListServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserListServlet.class);
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("UserListServlet.doGet() called");

        try {
            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                logger.warn("User not logged in, redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            logger.info("User is logged in, proceeding with user list");

            // Check authorization - Only ADMIN, HRM, HR, Dept Manager can access
            if (!group4.hrms.util.PermissionUtil.canViewUserList(request)) {
                logger.warn("User does not have permission to view user list");
                request.setAttribute("errorMessage", "You don't have permission to access this page");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }

            // Parse query parameters
            String search = request.getParameter("search");
            String departmentParam = request.getParameter("department");
            String positionIdStr = request.getParameter("position");
            String status = request.getParameter("status");
            String gender = request.getParameter("gender");
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
            Long departmentId = null;
            if (departmentParam != null && !departmentParam.trim().isEmpty()) {
                // Try to parse as Long first (for backward compatibility)
                departmentId = parseLongOrNull(departmentParam);

                // If not a number, try to find department by name
                if (departmentId == null) {
                    try {
                        group4.hrms.dao.DepartmentDao departmentDao = new group4.hrms.dao.DepartmentDao();
                        java.util.Optional<group4.hrms.model.Department> dept = departmentDao
                                .findByName(departmentParam.trim());
                        if (dept.isPresent()) {
                            departmentId = dept.get().getId();
                            logger.info("Found department '{}' with ID: {}", departmentParam, departmentId);
                        } else {
                            logger.warn("Department not found: {}", departmentParam);
                        }
                    } catch (Exception e) {
                        logger.error("Error finding department by name: {}", departmentParam, e);
                    }
                }
            }
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

            // Fetch users with filters as DTOs
            // Dept Manager chỉ xem được users trong phòng mình
            if (!group4.hrms.util.PermissionUtil.canViewAllUsers(request)) {
                Long userDeptId = group4.hrms.util.PermissionUtil.getCurrentUserDepartmentId(request);
                if (userDeptId != null) {
                    departmentId = userDeptId; // Force filter by user's department
                    logger.info("Department Manager viewing only their department: {}", departmentId);
                }
            }

            logger.info(
                    "Fetching users with filters - search: {}, dept: {}, pos: {}, status: {}, gender: {}, offset: {}, pageSize: {}",
                    search, departmentId, positionId, status, gender, offset, pageSize);

            // Measure query execution time
            long queryStartTime = System.currentTimeMillis();

            List<UserListDto> userDtos = userDao.findWithFiltersAsDto(
                    search, departmentId, positionId, status, gender,
                    offset, pageSize, sortBy, sortOrder);

            // Get total count for pagination
            int totalRecords = userDao.countWithFilters(search, departmentId, positionId, status, gender);

            long queryEndTime = System.currentTimeMillis();
            long queryExecutionTime = queryEndTime - queryStartTime;

            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            logger.info("Found {} users out of {} total records in {} ms",
                    userDtos.size(), totalRecords, queryExecutionTime);
            logger.info("Total pages: {}, Query execution time: {} ms", totalPages, queryExecutionTime);

            // Log performance warning if query is slow
            if (queryExecutionTime > 1000) {
                logger.warn("Slow query detected: {} ms for user list query with filters", queryExecutionTime);
            }

            // Fetch departments and positions for filter dropdowns (using optimized cache)
            long cacheStartTime = System.currentTimeMillis();
            List<Department> departments = DropdownCacheUtil.getDepartments(getServletContext());
            List<Position> positions = DropdownCacheUtil.getPositions(getServletContext());
            long cacheEndTime = System.currentTimeMillis();

            // Log cache performance
            logger.info("Cache lookup completed in {} ms", (cacheEndTime - cacheStartTime));
            logger.debug("Cache statistics - {}", DropdownCacheUtil.getDepartmentsCacheStats(getServletContext()));
            logger.debug("Cache statistics - {}", DropdownCacheUtil.getPositionsCacheStats(getServletContext()));

            // Set attributes for JSP
            request.setAttribute("users", userDtos);
            request.setAttribute("departments", departments);
            request.setAttribute("positions", positions);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("search", search);
            request.setAttribute("selectedDepartment", departmentId);
            request.setAttribute("selectedDepartmentName", departmentParam); // For dropdown selection
            request.setAttribute("selectedPosition", positionId);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("selectedGender", gender);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("sortOrder", sortOrder);

            // Set permissions for UI
            request.setAttribute("canCreateUser", group4.hrms.util.PermissionUtil.canCreateUser(request));
            request.setAttribute("canViewAllUsers", group4.hrms.util.PermissionUtil.canViewAllUsers(request));

            // Check if user is admin (for edit button visibility)
            String positionCode = group4.hrms.util.PermissionUtil.getCurrentUserPositionCode(request);
            boolean isAdmin = group4.hrms.util.PermissionUtil.POSITION_ADMIN.equals(positionCode);
            request.setAttribute("isAdmin", isAdmin);
            logger.info("User position code: {}, isAdmin: {}", positionCode, isAdmin);

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/employees/user-list.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading user list", e);
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
