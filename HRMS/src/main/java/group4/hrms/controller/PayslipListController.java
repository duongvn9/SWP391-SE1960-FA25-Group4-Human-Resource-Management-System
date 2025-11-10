package group4.hrms.controller;

import group4.hrms.dao.PayslipDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dto.*;
import group4.hrms.model.User;
import group4.hrms.model.Department;
import group4.hrms.service.PayslipGenerationService;
import group4.hrms.service.PayslipIssuesService;
import group4.hrms.service.PayslipExportService;
import group4.hrms.util.SessionUtil;
import group4.hrms.util.PermissionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Role-adaptive controller for payslip list management
 * Provides different views based on user role (Employee vs HRM Administrator)
 *
 * Requirements: 1.1, 1.4, 2.1, 10.4, 10.5
 */
@WebServlet("/payslips")
public class PayslipListController extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PayslipListController.class);

    // Services and DAOs
    private PayslipDao payslipDao;
    private UserDao userDao;
    private DepartmentDao departmentDao;
    private PayslipGenerationService generationService;
    private PayslipIssuesService issuesService;
    private PayslipExportService exportService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.payslipDao = new PayslipDao();
        this.userDao = new UserDao();
        this.departmentDao = new DepartmentDao();
        this.generationService = new PayslipGenerationService();
        this.issuesService = new PayslipIssuesService();
        this.exportService = new PayslipExportService();
        logger.info("PayslipListController initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("PayslipListController.doGet() called");

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
            // Check for view mode parameter (for HRM users switching to personal view)
            String viewMode = request.getParameter("viewMode");

            // Determine user role and route accordingly
            String userRole = determineUserRole(request, currentUser);
            logger.debug("User {} has role: {}, viewMode: {}", currentUser.getId(), userRole, viewMode);

            // If HRM user wants personal view, treat as employee
            if ("HRM".equals(userRole) && "personal".equals(viewMode)) {
                logger.debug("HRM user {} switching to personal view", currentUser.getId());
                handleEmployeeView(request, response, currentUser);
            } else if ("EMPLOYEE".equals(userRole)) {
                handleEmployeeView(request, response, currentUser);
            } else if ("HRM".equals(userRole)) {
                handleHRMView(request, response, currentUser);
            } else {
                // Default to employee view for unknown roles
                logger.warn("Unknown user role: {}, defaulting to employee view", userRole);
                handleEmployeeView(request, response, currentUser);
            }

        } catch (Exception e) {
            logger.error("Error in PayslipListController.doGet()", e);
            session.setAttribute("error", "An error occurred while loading payslips. Please try again.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    /**
     * Handle employee view - simple interface with restricted access
     * Requirements: 1.1, 1.4, 10.5
     */
    private void handleEmployeeView(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        logger.debug("Handling employee view for user: {}", user.getId());

        // Parse filters - employees can only filter by period
        PayslipFilter filter = buildEmployeeFilter(request, user);

        // If no period filter provided, show all payslips (no validation required)

        // Setup pagination
        PaginationMetadata pagination = buildPagination(request);

        // Get payslips for this employee only
        List<PayslipDto> payslips;
        long totalCount;

        try {
            payslips = payslipDao.findWithFilters(filter, pagination);
            totalCount = payslipDao.countWithFilters(filter);
        } catch (SQLException e) {
            logger.error("Error loading payslips for employee view", e);
            request.setAttribute("error", "An error occurred while loading payslips. Please try again.");
            request.setAttribute("userRole", "EMPLOYEE");
            request.setAttribute("pagination", new PaginationMetadata(1, 20, 0));
            request.setAttribute("payslips", new ArrayList<PayslipDto>());
            request.setAttribute("filter", filter);
            request.setAttribute("currentUser", user);
            request.getRequestDispatcher("/WEB-INF/views/payslips/payslip-list.jsp")
                   .forward(request, response);
            return;
        }

        // Update pagination with total count
        pagination.setTotalItems(totalCount);

        // Set attributes for JSP
        request.setAttribute("payslips", payslips);
        request.setAttribute("filter", filter);
        request.setAttribute("pagination", pagination);
        request.setAttribute("userRole", "EMPLOYEE");
        request.setAttribute("currentUser", user);

        // Set currentPage for sidebar highlighting
        request.setAttribute("currentPage", "my-payslip");

        logger.debug("Employee view: found {} payslips for user {}", payslips.size(), user.getId());

        // Forward to JSP
        request.getRequestDispatcher("/WEB-INF/views/payslips/payslip-list.jsp")
               .forward(request, response);
    }

    /**
     * Handle HRM administrator view - full administrative interface
     * Requirements: 2.1, 10.4
     */
    private void handleHRMView(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        logger.debug("Handling HRM view for user: {}", user.getId());

        // Check if HRM wants to view personal payslips
        String viewMode = request.getParameter("viewMode");
        if ("personal".equals(viewMode)) {
            logger.debug("HRM user {} switching to personal payslip view", user.getId());
            handleEmployeeView(request, response, user);
            return;
        }

        // Parse filters - HRM can use all filter options
        PayslipFilter filter = buildHRMFilter(request);

        // Setup pagination
        PaginationMetadata pagination = buildPagination(request);

        // Get payslips with filters
        List<PayslipDto> payslips;
        long totalCount;
        PayslipSummaryCounters counters = null;

        try {
            payslips = payslipDao.findWithFilters(filter, pagination);
            totalCount = payslipDao.countWithFilters(filter);

            // Get summary counters if period is specified
            if (filter.hasPeriodFilter()) {
                counters = payslipDao.getSummaryCounters(
                    filter.getPeriodStart(),
                    filter.getPeriodEnd(),
                    filter.getUserIds()
                );
            }
        } catch (SQLException e) {
            logger.error("Error loading payslips for HRM view", e);
            request.setAttribute("error", "An error occurred while loading payslips. Please try again.");
            request.setAttribute("userRole", "HRM");
            request.setAttribute("pagination", new PaginationMetadata(1, 20, 0));
            request.setAttribute("payslips", new ArrayList<PayslipDto>());
            request.setAttribute("filter", filter);
            request.setAttribute("currentUser", user);
            request.getRequestDispatcher("/WEB-INF/views/payslips/payslip-list.jsp")
                   .forward(request, response);
            return;
        }

        // Update pagination with total count
        pagination.setTotalItems(totalCount);

        // Get issues for CURRENT PERIOD (the period user is viewing)
        List<PayslipIssue> issues = null;
        if (filter.hasPeriodFilter()) {
            try {
                issues = issuesService.detectIssues(filter.getPeriodStart(), filter.getPeriodEnd(), filter.getUserIds());
                logger.debug("Detected issues for current period ({} to {})", filter.getPeriodStart(), filter.getPeriodEnd());
            } catch (Exception e) {
                logger.warn("Error detecting issues: {}", e.getMessage());
            }
        }

        // Load departments for filter dropdown
        List<Department> departments = null;
        try {
            departments = departmentDao.findAll();
            logger.debug("Loaded {} departments for HRM view", departments != null ? departments.size() : 0);
        } catch (Exception e) {
            logger.warn("Error loading departments: {}", e.getMessage());
            departments = new ArrayList<>();
        }

        // Load employees for filter dropdown (optional)
        List<User> employees = null;
        try {
            employees = userDao.findActiveEmployees(); // Use active employees only
            logger.debug("Loaded {} active employees for HRM view", employees != null ? employees.size() : 0);
        } catch (Exception e) {
            logger.warn("Error loading employees: {}", e.getMessage());
            employees = new ArrayList<>();
        }

        // Get employees without payslip and with attendance changes (for quick actions)
        List<java.util.Map<String, Object>> employeesWithoutPayslip = new ArrayList<>();
        List<java.util.Map<String, Object>> employeesWithAttendanceChanges = new ArrayList<>();

        if (filter.hasPeriodFilter()) {
            try {
                // Get employees without payslip for CURRENT PERIOD (the period user is viewing)
                employeesWithoutPayslip = payslipDao.findEmployeesWithoutPayslip(
                    filter.getPeriodStart(), filter.getPeriodEnd());
                logger.debug("Found {} employees without payslip for current period ({} to {})",
                    employeesWithoutPayslip.size(), filter.getPeriodStart(), filter.getPeriodEnd());

                // Get employees with attendance changes (dirty payslips)
                List<group4.hrms.model.Payslip> dirtyPayslips =
                    payslipDao.findDirtyPayslipsWithUserInfo(filter.getPeriodStart(), filter.getPeriodEnd());

                for (group4.hrms.model.Payslip payslip : dirtyPayslips) {
                    java.util.Optional<User> userOpt = userDao.findById(payslip.getUserId());
                    if (userOpt.isPresent()) {
                        User emp = userOpt.get();
                        java.util.Map<String, Object> empData = new java.util.HashMap<>();
                        empData.put("id", emp.getId());
                        empData.put("employeeCode", emp.getEmployeeCode());
                        empData.put("fullName", emp.getFullName());
                        empData.put("departmentId", emp.getDepartmentId());
                        empData.put("payslipId", payslip.getId());
                        empData.put("changeReason", payslip.getDirtyReason());
                        employeesWithAttendanceChanges.add(empData);
                    }
                }
                logger.debug("Found {} employees with attendance changes", employeesWithAttendanceChanges.size());
            } catch (Exception e) {
                logger.warn("Error loading employee action lists: {}", e.getMessage());
            }
        }

        // Set attributes for JSP
        request.setAttribute("payslips", payslips);
        request.setAttribute("filter", filter);
        request.setAttribute("pagination", pagination);
        request.setAttribute("summaryCounters", counters);
        request.setAttribute("issues", issues);
        request.setAttribute("departments", departments);
        request.setAttribute("employees", employees);
        request.setAttribute("employeesWithoutPayslip", employeesWithoutPayslip);
        request.setAttribute("employeesWithAttendanceChanges", employeesWithAttendanceChanges);
        request.setAttribute("userRole", "HRM");
        request.setAttribute("currentUser", user);

        // Set currentPage for sidebar highlighting
        request.setAttribute("currentPage", "payslip-list");

        logger.debug("HRM view: found {} payslips, counters: {}", payslips.size(), counters);

        // Forward to JSP
        request.getRequestDispatcher("/WEB-INF/views/payslips/payslip-list.jsp")
               .forward(request, response);
    }



    /**
     * Build filter for employee view - restricted to current user only
     * Requirements: 1.1, 1.4, 10.5
     */
    private PayslipFilter buildEmployeeFilter(HttpServletRequest request, User user) {
        PayslipFilter filter = new PayslipFilter();

        // Always restrict to current user
        filter.setUserId(user.getId());

        // Parse period parameters - support both direct dates and month/year
        String periodStartStr = request.getParameter("periodStart");
        String periodEndStr = request.getParameter("periodEnd");
        String filterMonth = request.getParameter("filterMonth");
        String filterYear = request.getParameter("filterYear");

        // If month/year are provided, calculate period dates
        if (filterMonth != null && !filterMonth.trim().isEmpty() &&
            filterYear != null && !filterYear.trim().isEmpty()) {
            try {
                int month = Integer.parseInt(filterMonth);
                int year = Integer.parseInt(filterYear);

                // Calculate first and last day of the month
                LocalDate periodStart = LocalDate.of(year, month, 1);
                LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

                filter.setPeriodStart(periodStart);
                filter.setPeriodEnd(periodEnd);

                logger.debug("Employee filter - calculated period from month/year: {} to {}", periodStart, periodEnd);
            } catch (Exception e) {
                logger.warn("Invalid month/year format in employee filter: month={}, year={}", filterMonth, filterYear);
            }
        }
        // Fallback to direct date parameters
        else if (periodStartStr != null && periodEndStr != null) {
            try {
                LocalDate periodStart = LocalDate.parse(periodStartStr);
                LocalDate periodEnd = LocalDate.parse(periodEndStr);
                filter.setPeriodStart(periodStart);
                filter.setPeriodEnd(periodEnd);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format in employee filter: start={}, end={}",
                           periodStartStr, periodEndStr);
            }
        }

        return filter;
    }

    /**
     * Build filter for HRM view - supports all filter options
     * Requirements: 2.1, 2.2, 2.4
     */
    private PayslipFilter buildHRMFilter(HttpServletRequest request) {
        PayslipFilter filter = new PayslipFilter();

        // Parse period parameters - support both direct dates and month/year
        String periodStartStr = request.getParameter("periodStart");
        String periodEndStr = request.getParameter("periodEnd");
        String filterMonth = request.getParameter("filterMonth");
        String filterYear = request.getParameter("filterYear");

        // If month/year are provided, calculate period dates
        if (filterMonth != null && !filterMonth.trim().isEmpty() &&
            filterYear != null && !filterYear.trim().isEmpty()) {
            try {
                int month = Integer.parseInt(filterMonth);
                int year = Integer.parseInt(filterYear);

                // Calculate first and last day of the month
                LocalDate periodStart = LocalDate.of(year, month, 1);
                LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

                filter.setPeriodStart(periodStart);
                filter.setPeriodEnd(periodEnd);

                logger.debug("Calculated period from month/year: {} to {}", periodStart, periodEnd);
            } catch (Exception e) {
                logger.warn("Invalid month/year format: month={}, year={}", filterMonth, filterYear);
            }
        }
        // Fallback to direct date parameters
        else if (periodStartStr != null && periodEndStr != null) {
            try {
                LocalDate periodStart = LocalDate.parse(periodStartStr);
                LocalDate periodEnd = LocalDate.parse(periodEndStr);
                filter.setPeriodStart(periodStart);
                filter.setPeriodEnd(periodEnd);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format in HRM filter: start={}, end={}",
                           periodStartStr, periodEndStr);
            }
        }
        // DEFAULT: If no period specified, use PREVIOUS MONTH for viewing
        // Business rule: Only work with complete months (previous months)
        else {
            LocalDate now = LocalDate.now();
            LocalDate previousMonth = now.minusMonths(1);
            LocalDate periodStart = previousMonth.withDayOfMonth(1);
            LocalDate periodEnd = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());

            filter.setPeriodStart(periodStart);
            filter.setPeriodEnd(periodEnd);

            logger.debug("No period filter provided, defaulting to previous month: {} to {}",
                        periodStart, periodEnd);
        }

        // Parse department filter
        String departmentIdStr = request.getParameter("departmentId");
        if (departmentIdStr != null && !departmentIdStr.trim().isEmpty()) {
            try {
                filter.setDepartmentId(Long.parseLong(departmentIdStr));
            } catch (NumberFormatException e) {
                logger.warn("Invalid department ID format: {}", departmentIdStr);
            }
        }

        // Parse employee filter - support both userId (dropdown) and employeeSearch (text input)
        String userIdStr = request.getParameter("userId");
        String employeeSearch = request.getParameter("employeeSearch");

        // Priority: userId (dropdown) takes precedence if both are provided
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                filter.setUserId(Long.parseLong(userIdStr));
                logger.debug("Filtering by userId: {}", userIdStr);
            } catch (NumberFormatException e) {
                logger.warn("Invalid user ID format: {}", userIdStr);
            }
        } else if (employeeSearch != null && !employeeSearch.trim().isEmpty()) {
            // Search by employee code or name
            filter.setEmployeeSearch(employeeSearch.trim());
            logger.debug("Filtering by employee search: {}", employeeSearch);
        }

        // Parse status filter
        String status = request.getParameter("status");
        if (status != null && !status.trim().isEmpty()) {
            filter.setStatus(status);
        }

        // Parse boolean filters
        String onlyDirtyStr = request.getParameter("onlyDirty");
        if ("true".equals(onlyDirtyStr) || "1".equals(onlyDirtyStr)) {
            filter.setOnlyDirty(true);
        }

        String onlyNotGeneratedStr = request.getParameter("onlyNotGenerated");
        if ("true".equals(onlyNotGeneratedStr) || "1".equals(onlyNotGeneratedStr)) {
            filter.setOnlyNotGenerated(true);
        }

        return filter;
    }

    /**
     * Build pagination metadata from request parameters
     */
    private PaginationMetadata buildPagination(HttpServletRequest request) {
        int page = 1;
        int pageSize = 20; // Default page size

        String pageStr = request.getParameter("page");
        if (pageStr != null) {
            try {
                page = Math.max(1, Integer.parseInt(pageStr));
            } catch (NumberFormatException e) {
                logger.warn("Invalid page number: {}", pageStr);
            }
        }

        String pageSizeStr = request.getParameter("pageSize");
        if (pageSizeStr != null) {
            try {
                pageSize = Math.min(100, Math.max(10, Integer.parseInt(pageSizeStr)));
            } catch (NumberFormatException e) {
                logger.warn("Invalid page size: {}", pageSizeStr);
            }
        }

        return new PaginationMetadata(page, pageSize, 0);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("PayslipListController.doPost() called");

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || !SessionUtil.isUserLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User session invalid");
            return;
        }

        // Check if user has HRM permissions for POST operations
        String userRole = determineUserRole(request, currentUser);
        if (!"HRM".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions");
            return;
        }

        try {
            // Debug: Log all parameters
            logger.debug("POST request parameters:");
            request.getParameterMap().forEach((key, values) -> {
                logger.debug("  {} = {}", key, String.join(", ", values));
            });

            // Also log to console for immediate debugging
            System.out.println("=== PayslipListController POST Debug ===");
            System.out.println("Content-Type: " + request.getContentType());
            System.out.println("Parameters:");
            request.getParameterMap().forEach((key, values) -> {
                System.out.println("  " + key + " = " + String.join(", ", values));
            });
            System.out.println("========================================");

            // Get action parameter
            String action = request.getParameter("action");
            logger.debug("POST action: {}", action);

            if (action == null || action.trim().isEmpty()) {
                logger.warn("Action parameter missing in POST request");
                logger.warn("Content-Type: {}", request.getContentType());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action parameter required");
                return;
            }

            // Route to appropriate handler
            logger.debug("Routing to action handler: {}", action);
            switch (action.toLowerCase()) {
                case "generate":
                    handleGenerateAction(request, response, currentUser);
                    break;
                case "regenerate":
                    handleRegenerateAction(request, response, currentUser);
                    break;
                case "export":
                    handleExportAction(request, response, currentUser);
                    break;
                case "quickgenerate":
                    handleQuickGenerateAction(request, response, currentUser);
                    break;
                case "quickregenerate":
                    handleQuickRegenerateAction(request, response, currentUser);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
            }

        } catch (Exception e) {
            logger.error("Error in PayslipListController.doPost(): " + e.getMessage(), e);
            e.printStackTrace(); // Print full stack trace to console
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    /**
     * Handle bulk payslip generation
     * Requirements: 3.1, 3.2, 9.1
     */
    private void handleGenerateAction(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        logger.debug("Handling generate action for user: {}", user.getId());

        try {
            // Parse generation parameters
            String scope = request.getParameter("scope"); // ALL, DEPARTMENT, EMPLOYEE
            String scopeIdStr = request.getParameter("scopeId");

            // Debug scopeId parameter values
            String[] scopeIdValues = request.getParameterValues("scopeId");
            logger.debug("scopeId parameter values: {}", scopeIdValues != null ? java.util.Arrays.toString(scopeIdValues) : "null");
            String periodStartStr = request.getParameter("periodStart");
            String periodEndStr = request.getParameter("periodEnd");

            // Also check for payrollMonth/payrollYear parameters (from form)
            String payrollMonthStr = request.getParameter("payrollMonth");
            String payrollYearStr = request.getParameter("payrollYear");

            boolean onlyDirty = "true".equals(request.getParameter("onlyDirty"));
            boolean force = "true".equals(request.getParameter("force"));

            // Debug logging
            logger.debug("Parsed parameters: scope={}, scopeId={}, periodStart={}, periodEnd={}, payrollMonth={}, payrollYear={}, onlyDirty={}, force={}",
                        scope, scopeIdStr, periodStartStr, periodEndStr, payrollMonthStr, payrollYearStr, onlyDirty, force);

            // Always calculate period from payrollMonth/payrollYear (JavaScript calculation removed)
            if (payrollMonthStr != null && payrollYearStr != null) {
                try {
                    int month = Integer.parseInt(payrollMonthStr);
                    int year = Integer.parseInt(payrollYearStr);

                    LocalDate firstDay = LocalDate.of(year, month, 1);
                    LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

                    periodStartStr = firstDay.toString();
                    periodEndStr = lastDay.toString();

                    logger.debug("Calculated period from payroll month/year: {} to {}", periodStartStr, periodEndStr);
                } catch (Exception e) {
                    logger.error("Error calculating period from payroll month/year", e);
                    sendJsonError(response, "Invalid payroll month/year format");
                    return;
                }
            }

            // Validate required parameters
            if (scope == null || periodStartStr == null || periodEndStr == null) {
                logger.warn("Missing required parameters: scope={}, periodStart={}, periodEnd={}", scope, periodStartStr, periodEndStr);
                sendJsonError(response, "Missing required parameters: scope, periodStart, periodEnd");
                return;
            }

            // Parse dates
            LocalDate periodStart;
            LocalDate periodEnd;
            try {
                periodStart = LocalDate.parse(periodStartStr);
                periodEnd = LocalDate.parse(periodEndStr);
            } catch (Exception e) {
                sendJsonError(response, "Invalid date format. Use YYYY-MM-DD");
                return;
            }

            // Parse scope
            GenerationRequest.GenerationScope generationScope;
            try {
                generationScope = GenerationRequest.GenerationScope.valueOf(scope.toUpperCase());
            } catch (IllegalArgumentException e) {
                sendJsonError(response, "Invalid scope. Use ALL, DEPARTMENT, or EMPLOYEE");
                return;
            }

            // Parse scope ID if needed
            Long scopeId = null;
            if (generationScope != GenerationRequest.GenerationScope.ALL) {
                logger.debug("Parsing scopeId from: '{}'", scopeIdStr);

                // Clean up scopeId first - handle multiple parameter values or comma-separated format
                String cleanScopeId = null;

                // First try to get from multiple parameter values
                String[] allScopeIdValues = request.getParameterValues("scopeId");
                if (allScopeIdValues != null && allScopeIdValues.length > 0) {
                    // Take the last non-empty value
                    for (int i = allScopeIdValues.length - 1; i >= 0; i--) {
                        String value = allScopeIdValues[i];
                        if (value != null && !value.trim().isEmpty()) {
                            cleanScopeId = value.trim();
                            break;
                        }
                    }
                }

                // If still null, try the single parameter approach
                if (cleanScopeId == null && scopeIdStr != null) {
                    cleanScopeId = scopeIdStr.trim();
                    if (cleanScopeId.contains(",")) {
                        // Take the last non-empty part after comma (in case of ", 52" format)
                        String[] parts = cleanScopeId.split(",");
                        for (int i = parts.length - 1; i >= 0; i--) {
                            String part = parts[i].trim();
                            if (!part.isEmpty()) {
                                cleanScopeId = part;
                                break;
                            }
                        }
                    }
                }

                logger.debug("Cleaned scopeId: '{}'", cleanScopeId);

                if (cleanScopeId == null || cleanScopeId.trim().isEmpty()) {
                    logger.warn("Scope ID required for {} scope but got original: '{}', cleaned: '{}'", scope, scopeIdStr, cleanScopeId);
                    sendJsonError(response, "Scope ID required for " + scope + " scope");
                    return;
                }

                try {
                    scopeId = Long.parseLong(cleanScopeId);
                    logger.debug("Parsed scopeId: {}", scopeId);
                } catch (NumberFormatException e) {
                    logger.error("Invalid scope ID format: original='{}', cleaned='{}', error: {}", scopeIdStr, cleanScopeId, e.getMessage());
                    sendJsonError(response, "Invalid scope ID format: " + scopeIdStr);
                    return;
                }
            }

            // Build generation request
            GenerationRequest generationRequest = new GenerationRequest(periodStart, periodEnd, generationScope, scopeId);
            generationRequest.setOnlyDirty(onlyDirty);
            generationRequest.setForce(force);
            generationRequest.setRequestedByUserId(user.getId());
            generationRequest.setRequestReason("Bulk generation from web interface");

            logger.info("Generation request: {}", generationRequest);

            // Call generation service
            GenerationResult result = generationService.generatePayslips(generationRequest);

            // Build JSON response
            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("{");
            jsonResponse.append("\"success\": ").append(result.isSuccess()).append(",");
            jsonResponse.append("\"message\": \"").append(escapeJson(result.getMessage())).append("\",");
            jsonResponse.append("\"totalRequested\": ").append(result.getTotalRequested()).append(",");
            jsonResponse.append("\"createdCount\": ").append(result.getCreatedCount()).append(",");
            jsonResponse.append("\"updatedCount\": ").append(result.getUpdatedCount()).append(",");
            jsonResponse.append("\"skippedCount\": ").append(result.getSkippedCount()).append(",");
            jsonResponse.append("\"errorCount\": ").append(result.getErrorCount()).append(",");
            jsonResponse.append("\"durationMs\": ").append(result.getDurationMillis());

            if (result.hasErrors()) {
                jsonResponse.append(",\"errors\": [");
                for (int i = 0; i < result.getErrors().size(); i++) {
                    if (i > 0) jsonResponse.append(",");
                    jsonResponse.append("\"").append(escapeJson(result.getErrors().get(i).getError())).append("\"");
                }
                jsonResponse.append("]");
            }

            jsonResponse.append("}");

            sendJsonResponse(response, jsonResponse.toString());

        } catch (Exception e) {
            logger.error("Error in generate action", e);
            sendJsonError(response, "Generation failed: " + e.getMessage());
        }
    }

    /**
     * Handle individual payslip regeneration
     * Requirements: 3.2, 9.1
     */
    private void handleRegenerateAction(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        logger.debug("Handling regenerate action for user: {}", user.getId());

        try {
            String payslipIdStr = request.getParameter("payslipId");
            if (payslipIdStr == null || payslipIdStr.trim().isEmpty()) {
                sendJsonError(response, "Payslip ID required");
                return;
            }

            Long payslipId;
            try {
                payslipId = Long.parseLong(payslipIdStr);
            } catch (NumberFormatException e) {
                sendJsonError(response, "Invalid payslip ID format");
                return;
            }

            boolean force = "true".equals(request.getParameter("force"));

            logger.info("Regeneration request: payslipId={}, force={}", payslipId, force);

            // Call regeneration service
            GenerationResult result = generationService.regeneratePayslip(payslipId, force);

            // Build JSON response
            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("{");
            jsonResponse.append("\"success\": ").append(result.isSuccess()).append(",");
            jsonResponse.append("\"message\": \"").append(escapeJson(result.getMessage())).append("\",");
            jsonResponse.append("\"durationMs\": ").append(result.getDurationMillis());

            if (result.hasErrors()) {
                jsonResponse.append(",\"errors\": [");
                for (int i = 0; i < result.getErrors().size(); i++) {
                    if (i > 0) jsonResponse.append(",");
                    jsonResponse.append("\"").append(escapeJson(result.getErrors().get(i).getError())).append("\"");
                }
                jsonResponse.append("]");
            }

            jsonResponse.append("}");

            sendJsonResponse(response, jsonResponse.toString());

        } catch (Exception e) {
            logger.error("Error in regenerate action", e);
            sendJsonError(response, "Regeneration failed: " + e.getMessage());
        }
    }

    /**
     * Handle payslip data export
     * Requirements: 9.2, 9.3
     */
    private void handleExportAction(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        logger.debug("Handling export action for user: {}", user.getId());

        try {
            String format = request.getParameter("format"); // excel, csv
            if (format == null || format.trim().isEmpty()) {
                format = "excel"; // Default format
            }

            // Validate format
            if (!format.equalsIgnoreCase("excel") && !format.equalsIgnoreCase("csv")) {
                sendJsonError(response, "Invalid format. Use 'excel' or 'csv'");
                return;
            }

            // Build filter from request parameters
            PayslipFilter filter = buildHRMFilter(request);

            logger.info("Export request: format={}, filter={}", format, filter);

            // Call export service
            ExportResult exportResult;
            if (format.equalsIgnoreCase("excel")) {
                exportResult = exportService.exportToExcel(filter);
            } else {
                exportResult = exportService.exportToCSV(filter);
            }

            // Set response headers for file download
            response.setContentType(exportResult.getContentType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + exportResult.getFilename() + "\"");
            response.setContentLength(exportResult.getData().length);

            // Write file data to response
            response.getOutputStream().write(exportResult.getData());
            response.getOutputStream().flush();

            logger.info("Export completed: {} records exported to {} format",
                       exportResult.getRecordCount(), format);

        } catch (Exception e) {
            logger.error("Error in export action", e);

            // Reset response if not committed
            if (!response.isCommitted()) {
                response.reset();
                sendJsonError(response, "Export failed: " + e.getMessage());
            }
        }
    }

    /**
     * Handle quick generation from issues panel
     * Requirements: 4.4, 4.5
     */
    private void handleQuickGenerateAction(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        logger.debug("Handling quick generate action for user: {}", user.getId());

        try {
            String[] userIdStrs = request.getParameterValues("userIds");
            String periodStartStr = request.getParameter("periodStart");
            String periodEndStr = request.getParameter("periodEnd");

            if (userIdStrs == null || userIdStrs.length == 0 ||
                periodStartStr == null || periodEndStr == null) {
                sendJsonError(response, "Missing required parameters: userIds, periodStart, periodEnd");
                return;
            }

            // Parse user IDs
            List<Long> userIds = new ArrayList<>();
            try {
                for (String userIdStr : userIdStrs) {
                    userIds.add(Long.parseLong(userIdStr.trim()));
                }
            } catch (NumberFormatException e) {
                sendJsonError(response, "Invalid user ID format");
                return;
            }

            // Parse dates
            LocalDate periodStart;
            LocalDate periodEnd;
            try {
                periodStart = LocalDate.parse(periodStartStr);
                periodEnd = LocalDate.parse(periodEndStr);
            } catch (Exception e) {
                sendJsonError(response, "Invalid date format. Use YYYY-MM-DD");
                return;
            }

            logger.info("Quick generation request: userIds={}, period={} to {}",
                       userIds.size(), periodStart, periodEnd);

            // Call quick generation service
            PayslipIssuesService.IssueResolutionResult result =
                issuesService.quickGenerate(userIds, periodStart, periodEnd);

            // Build JSON response
            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("{");
            jsonResponse.append("\"success\": ").append(result.isSuccessful()).append(",");
            jsonResponse.append("\"message\": \"Quick generation completed\",");
            jsonResponse.append("\"requestedCount\": ").append(result.getRequestedCount()).append(",");
            jsonResponse.append("\"successCount\": ").append(result.getSuccessCount()).append(",");
            jsonResponse.append("\"failureCount\": ").append(result.getFailureCount());

            if (result.hasErrors()) {
                jsonResponse.append(",\"errors\": [");
                for (int i = 0; i < result.getErrors().size(); i++) {
                    if (i > 0) jsonResponse.append(",");
                    jsonResponse.append("\"").append(escapeJson(result.getErrors().get(i))).append("\"");
                }
                jsonResponse.append("]");
            }

            jsonResponse.append("}");

            sendJsonResponse(response, jsonResponse.toString());

        } catch (Exception e) {
            logger.error("Error in quick generate action", e);
            sendJsonError(response, "Quick generation failed: " + e.getMessage());
        }
    }

    /**
     * Handle quick regeneration from issues panel
     * Requirements: 4.6, 4.7
     */
    private void handleQuickRegenerateAction(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        logger.debug("Handling quick regenerate action for user: {}", user.getId());

        try {
            String[] payslipIdStrs = request.getParameterValues("payslipIds");
            if (payslipIdStrs == null || payslipIdStrs.length == 0) {
                sendJsonError(response, "Payslip IDs required");
                return;
            }

            // Parse payslip IDs
            List<Long> payslipIds = new ArrayList<>();
            try {
                for (String payslipIdStr : payslipIdStrs) {
                    payslipIds.add(Long.parseLong(payslipIdStr.trim()));
                }
            } catch (NumberFormatException e) {
                sendJsonError(response, "Invalid payslip ID format");
                return;
            }

            logger.info("Quick regeneration request: payslipIds={}", payslipIds.size());

            // Call quick regeneration service
            PayslipIssuesService.IssueResolutionResult result =
                issuesService.quickRegenerate(payslipIds);

            // Build JSON response
            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("{");
            jsonResponse.append("\"success\": ").append(result.isSuccessful()).append(",");
            jsonResponse.append("\"message\": \"Quick regeneration completed\",");
            jsonResponse.append("\"requestedCount\": ").append(result.getRequestedCount()).append(",");
            jsonResponse.append("\"successCount\": ").append(result.getSuccessCount()).append(",");
            jsonResponse.append("\"failureCount\": ").append(result.getFailureCount());

            if (result.hasErrors()) {
                jsonResponse.append(",\"errors\": [");
                for (int i = 0; i < result.getErrors().size(); i++) {
                    if (i > 0) jsonResponse.append(",");
                    jsonResponse.append("\"").append(escapeJson(result.getErrors().get(i))).append("\"");
                }
                jsonResponse.append("]");
            }

            jsonResponse.append("}");

            sendJsonResponse(response, jsonResponse.toString());

        } catch (Exception e) {
            logger.error("Error in quick regenerate action", e);
            sendJsonError(response, "Quick regeneration failed: " + e.getMessage());
        }
    }

    /**
     * Send JSON response
     */
    private void sendJsonResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    /**
     * Send JSON error response
     */
    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        sendJsonResponse(response, "{\"success\": false, \"error\": \"" + message + "\"}");
    }

    /**
     * Determine user role based on permissions
     * Requirements: 10.4, 10.5
     */
    private String determineUserRole(HttpServletRequest request, User user) {
        // Check by positionId first (most accurate)
        Long positionId = user.getPositionId();

        // ADMIN (positionId = 6) only sees their own payslips
        if (positionId != null && positionId == 6) {
            return "EMPLOYEE";
        }

        // HRM (positionId = 7) can manage all payslips
        if (positionId != null && positionId == 7) {
            return "HRM";
        }

        // HR (positionId = 8) only sees their own payslips
        if (positionId != null && positionId == 8) {
            return "EMPLOYEE";
        }

        // Fallback: Check position code
        String positionCode = PermissionUtil.getCurrentUserPositionCode(request);
        if ("HRM".equals(positionCode)) {
            return "HRM";
        }

        // Fallback: Check if user has HRM permissions
        if (PermissionUtil.canViewAllUsers(request)) {
            return "HRM";
        }

        // Default to employee
        return "EMPLOYEE";
    }

    /**
     * Escape JSON string values to prevent injection
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
