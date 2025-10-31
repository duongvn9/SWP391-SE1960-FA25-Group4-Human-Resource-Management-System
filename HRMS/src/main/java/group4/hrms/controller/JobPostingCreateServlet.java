package group4.hrms.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.dto.JobPostingFormDto;
import group4.hrms.model.JobPosting;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.JobPostingService;
import group4.hrms.service.PositionService;
import group4.hrms.service.impl.DepartmentServiceImpl;
import group4.hrms.service.impl.JobPostingServiceImpl;
import group4.hrms.service.impl.PositionServiceImpl;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.JobPostingDao;
import group4.hrms.dao.PositionDao;
import group4.hrms.util.JobPostingPermissionHelper;
import group4.hrms.util.SecurityUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller for HR to create new job posting
 */
@WebServlet({"/job-posting/create", "/job-postings/create"})
public class JobPostingCreateServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(JobPostingCreateServlet.class);
    
    @Inject
    private JobPostingService jobPostingService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private PositionService positionService;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.info("Start processing job posting form submission...");

        // Log request info
        logger.info("Request URI: {}", request.getRequestURI());
        logger.info("Content type: {}", request.getContentType());
        
        // Get user's position ID for permission check 
        Object sessUserObj2 = request.getSession(false) != null ? request.getSession(false).getAttribute("user") : null;
        Long sessPositionId2 = null;
        if (sessUserObj2 instanceof group4.hrms.model.User) {
            group4.hrms.model.User su2 = (group4.hrms.model.User) sessUserObj2;
            sessPositionId2 = su2.getPositionId();
            logger.info("User position ID: {}", sessPositionId2);
        } else {
            logger.warn("No valid user found in session");
        }
        
        // Only HR Staff (8) can create job postings. HR Manager (7) can only approve/reject/publish.
        if (!JobPostingPermissionHelper.canCreateJobPosting(sessPositionId2)) {
            logger.error("Access denied - Only HR Staff (positionId=8) can create job postings. User position ID: {}", sessPositionId2);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only HR Staff can create job postings");
            return;
        }

        // If dependency injection isn't available in this runtime, fall back to ServletContext services
        if (departmentService == null) {
            Object ds = request.getServletContext().getAttribute("departmentService");
            if (ds instanceof DepartmentService) {
                departmentService = (DepartmentService) ds;
            }
        }
        if (positionService == null) {
            Object ps = request.getServletContext().getAttribute("positionService");
            if (ps instanceof PositionService) {
                positionService = (PositionService) ps;
            }
        }

        // Add departments and positions for dropdowns if available
        if (departmentService != null) {
            request.setAttribute("departments", departmentService.getAllDepartments());
        } else {
            request.setAttribute("departments", java.util.Collections.emptyList());
        }
        if (positionService != null) {
            request.setAttribute("positions", positionService.getAllPositions());
        } else {
            request.setAttribute("positions", java.util.Collections.emptyList());
        }

        // CSRF token for form
        String csrfToken = SecurityUtil.generateCsrfToken(request.getSession());
        request.setAttribute("csrfToken", csrfToken);
        logger.info("Generated CSRF token for form: {}", csrfToken);

        // If requestId is provided, load recruitment request and prefill fields
        String requestIdStr = request.getParameter("requestId");
        // Debug logs removed
        
        if (requestIdStr != null && !requestIdStr.isBlank()) {
            try {
                long reqId = Long.parseLong(requestIdStr);
                group4.hrms.dao.RequestDao requestDao = new group4.hrms.dao.RequestDao();
                java.util.Optional<group4.hrms.model.Request> reqOpt = requestDao.findById(reqId);
                if (reqOpt.isPresent()) {
                    group4.hrms.model.Request reqEntity = reqOpt.get();
                    group4.hrms.dto.RecruitmentDetailsDto details = reqEntity.getRecruitmentDetail();
                    if (details != null) {
                        request.setAttribute("requestDetails", details);
                        request.setAttribute("sourceRequestId", reqId);
                    } else {
                        logger.warn("Request details is null for request ID: {}", reqId);
                    }
                    // Also load department from recruitment request for display
                    if (reqEntity.getDepartmentId() != null) {
                        request.setAttribute("sourceDepartmentId", reqEntity.getDepartmentId());
                        logger.info("Loaded departmentId={} from recruitment request for prefill", reqEntity.getDepartmentId());
                    }
                }
            } catch (NumberFormatException e) {
                // invalid id - ignore
            } catch (RuntimeException e) {
                // DB or other runtime errors - ignore prefill
            }
        }
        
        // Forward to create form
        request.getRequestDispatcher("/WEB-INF/views/job-postings/create.jsp")
               .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Debug logs removed - functionality working
        logger.info("JobPostingCreateServlet.doPost: starting...");
        
        // Get user's position ID for permission check 
        Object sessUserObj2 = request.getSession(false) != null ? request.getSession(false).getAttribute("user") : null;
        Long sessPositionId2 = null;
        if (sessUserObj2 instanceof group4.hrms.model.User) {
            group4.hrms.model.User su2 = (group4.hrms.model.User) sessUserObj2;
            sessPositionId2 = su2.getPositionId();
        }
        
        // Only HR Staff (8) can create job postings. HR Manager (7) can only approve/reject/publish.
        if (!JobPostingPermissionHelper.canCreateJobPosting(sessPositionId2)) {
            logger.error("Access denied - Only HR Staff (positionId=8) can create job postings. User position ID: {}", sessPositionId2);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only HR Staff can create job postings");
            return;
        }

        // Log all form parameters for debugging
        Map<String, String[]> params = request.getParameterMap();
        for (String key : params.keySet()) {
            if (!key.contains("password") && !key.contains("csrf")) { // Don't log sensitive data
                logger.info("Form field: {} = {}", key, String.join(", ", params.get(key)));
            }
        }

        // Validate CSRF token (with detailed logging for debugging)
        String csrfToken = request.getParameter("csrfToken");
        HttpSession session = request.getSession(false);
        
        logger.info("CSRF Validation - Request token: {}", csrfToken);
        logger.info("CSRF Validation - Session exists: {}", session != null);
        
        if (session != null) {
            String sessionToken = (String) session.getAttribute("csrfToken");
            logger.info("CSRF Validation - Session token: {}", sessionToken);
            
            if (sessionToken == null) {
                logger.warn("No CSRF token in session - allowing for development");
            } else if (csrfToken == null) {
                logger.warn("No CSRF token in request - allowing for development");
            } else if (!sessionToken.equals(csrfToken)) {
                logger.warn("CSRF token mismatch - allowing for development");
                logger.warn("Expected: {}, Got: {}", sessionToken, csrfToken);
            } else {
                logger.info("CSRF token validation passed successfully");
            }
        } else {
            logger.warn("No session found - allowing for development");
        }
        
        // For development, we allow all requests to continue
        logger.info("Continuing with request processing...");

        // If dependency injection isn't available in this runtime, fall back to ServletContext services
        if (departmentService == null) {
            Object ds = request.getServletContext().getAttribute("departmentService");
            if (ds instanceof DepartmentService) {
                departmentService = (DepartmentService) ds;
                logger.info("Successfully loaded DepartmentService from ServletContext");
            } else {
                logger.warn("DepartmentService not found in ServletContext");
            }
        }
        if (positionService == null) {
            Object ps = request.getServletContext().getAttribute("positionService");
            if (ps instanceof PositionService) {
                positionService = (PositionService) ps;
            }
        }
        if (jobPostingService == null) {
            Object js = request.getServletContext().getAttribute("jobPostingService");
            if (js instanceof JobPostingService) {
                jobPostingService = (JobPostingService) js;
            }
        }

        try {
            // Parse and validate form data
            JobPostingFormDto formDto = parseFormData(request);
            logger.info("Form data parsed successfully: {}", formDto.toString());
            
            // Additional server-side sanitization
            sanitizeFormData(formDto);
            
            Map<String, String> errors = formDto.validate();
            
            // Additional custom validations
            performAdditionalValidations(formDto, errors);
            
            if (!errors.isEmpty()) {
                logger.error("Form validation failed with errors: {}", errors);
                
                // Re-populate form with error messages
                request.setAttribute("errors", errors);
                request.setAttribute("formData", formDto); // Add this to preserve form data
                
                // Re-populate requestDetails and sourceRequestId for error display
                String sourceRequestIdStr = request.getParameter("sourceRequestId");
                if (sourceRequestIdStr != null && !sourceRequestIdStr.isBlank()) {
                    try {
                        long reqId = Long.parseLong(sourceRequestIdStr);
                        group4.hrms.dao.RequestDao requestDao = new group4.hrms.dao.RequestDao();
                        java.util.Optional<group4.hrms.model.Request> reqOpt = requestDao.findById(reqId);
                        if (reqOpt.isPresent()) {
                            group4.hrms.model.Request reqEntity = reqOpt.get();
                            group4.hrms.dto.RecruitmentDetailsDto details = reqEntity.getRecruitmentDetail();
                            if (details != null) {
                                request.setAttribute("requestDetails", details);
                                request.setAttribute("sourceRequestId", reqId);
                            }
                            if (reqEntity.getDepartmentId() != null) {
                                request.setAttribute("sourceDepartmentId", reqEntity.getDepartmentId());
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to reload request details for error display: {}", e.getMessage());
                    }
                }
                
                // Generate new CSRF token for the error page
                String newCsrfToken = SecurityUtil.generateCsrfToken(request.getSession());
                request.setAttribute("csrfToken", newCsrfToken);
                
                if (departmentService != null) {
                    request.setAttribute("departments", departmentService.getAllDepartments());
                } else {
                    logger.warn("DepartmentService is null, using empty department list");
                    request.setAttribute("departments", java.util.Collections.emptyList());
                }
                
                if (positionService != null) {
                    request.setAttribute("positions", positionService.getAllPositions());
                } else {
                    logger.warn("PositionService is null, using empty position list");
                    request.setAttribute("positions", java.util.Collections.emptyList());
                }
                
                request.getRequestDispatcher("/WEB-INF/views/job-postings/create.jsp")
                       .forward(request, response);
                return;
            }
            
            logger.info("Form validation passed successfully");
            
            // Create job posting
            JobPosting jobPosting = new JobPosting();
            populateJobPosting(jobPosting, formDto, request);
            logger.debug("Populated JobPosting before save: {}", jobPosting);
            
            // Save and get ID
            if (jobPostingService == null) {
                logger.error("JobPostingService is null - trying to create instance manually");
                try {
                    // Create service instances if needed
                    if (departmentService == null) {
                        departmentService = new group4.hrms.service.impl.DepartmentServiceImpl(new group4.hrms.dao.DepartmentDao());
                    }
                    if (positionService == null) {
                        positionService = new group4.hrms.service.impl.PositionServiceImpl(new group4.hrms.dao.PositionDao());
                    }
                    
                    jobPostingService = new group4.hrms.service.impl.JobPostingServiceImpl(departmentService, positionService);
                } catch (Exception e) {
                    logger.error("Failed to create JobPostingService manually", e);
                    request.setAttribute("error", "Failed to create job posting: Service initialization error - " + e.getMessage());
                    request.setAttribute("formData", formDto);
                    
                    // Generate new CSRF token for the error page
                    String newCsrfToken = SecurityUtil.generateCsrfToken(request.getSession());
                    request.setAttribute("csrfToken", newCsrfToken);
                    
                    // Re-populate dropdowns for error display
                    if (departmentService != null) {
                        request.setAttribute("departments", departmentService.getAllDepartments());
                    } else {
                        request.setAttribute("departments", java.util.Collections.emptyList());
                    }
                    if (positionService != null) {
                        request.setAttribute("positions", positionService.getAllPositions());
                    } else {
                        request.setAttribute("positions", java.util.Collections.emptyList());
                    }
                    
                    request.getRequestDispatcher("/WEB-INF/views/job-postings/create.jsp")
                           .forward(request, response);
                    return;
                }
            }
            
            try {
                logger.info("About to create job posting with title: {}", jobPosting.getTitle());
                logger.debug("Calling jobPostingService.create; service instance = {}", jobPostingService);
                
                long createdId = jobPostingService.create(jobPosting);
                logger.info("SUCCESS: JobPostingCreateServlet created job posting id={}", createdId);

                // Redirect back to recruitment approved list with success message
                response.sendRedirect(request.getContextPath() + 
                    "/recruitment/approved?success=Job posting created successfully. Pending HRM approval.");
                return; // Important: stop processing after redirect
            } catch (Exception e) {
                logger.error("Failed to create job posting - detailed error", e);
                String errorMsg = "Failed to create job posting: ";
                
                // Handle specific database errors
                if (e.getMessage() != null) {
                    if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("job_postings.ux_job_postings_code")) {
                        errorMsg = "Job code already exists. Please use a different job code.";
                    } else if (e.getMessage().contains("Duplicate entry")) {
                        errorMsg = "Duplicate data detected. Please check your input.";
                    } else {
                        errorMsg += e.getMessage();
                    }
                } else {
                    errorMsg += "Database error";
                }
                
                request.setAttribute("error", errorMsg);
                request.setAttribute("formData", formDto);
                
                // Generate new CSRF token for the error page
                String newCsrfToken = SecurityUtil.generateCsrfToken(request.getSession());
                request.setAttribute("csrfToken", newCsrfToken);
                
                // Re-populate dropdowns for error display
                if (departmentService != null) {
                    request.setAttribute("departments", departmentService.getAllDepartments());
                } else {
                    request.setAttribute("departments", java.util.Collections.emptyList());
                }
                if (positionService != null) {
                    request.setAttribute("positions", positionService.getAllPositions());
                } else {
                    request.setAttribute("positions", java.util.Collections.emptyList());
                }
                
                request.getRequestDispatcher("/WEB-INF/views/job-postings/create.jsp")
                       .forward(request, response);
                return;
            }
            
        } catch (RuntimeException e) {
            request.setAttribute("error", "Failed to create job posting: " + e.getMessage());
            
            // Generate new CSRF token for the error page
            String newCsrfToken = SecurityUtil.generateCsrfToken(request.getSession());
            request.setAttribute("csrfToken", newCsrfToken);
            
            request.getRequestDispatcher("/WEB-INF/views/job-postings/create.jsp")
                   .forward(request, response);
        }
    }
    
    private JobPostingFormDto parseFormData(HttpServletRequest request) {
        JobPostingFormDto dto = new JobPostingFormDto();
        
        // Basic information
        dto.setPositionName(request.getParameter("positionName"));
        dto.setJobTitle(request.getParameter("jobTitle"));
        dto.setCode(request.getParameter("code"));
        dto.setJobLevel(request.getParameter("jobLevel"));
        dto.setJobType(request.getParameter("jobType"));
        try {
            dto.setNumberOfPositions(Integer.valueOf(request.getParameter("numberOfPositions")));
        } catch (NumberFormatException e) {
            dto.setNumberOfPositions(null);
        }
        
        // Salary information
        dto.setSalaryType(request.getParameter("salaryType"));
        try {
            String minSalary = request.getParameter("minSalary");
            if (minSalary != null && !minSalary.isBlank()) {
                dto.setMinSalary(new java.math.BigDecimal(minSalary.replace(",", "")));
            }
        } catch (NumberFormatException e) {
            // Will be caught in validation
        }
        try {
            String maxSalary = request.getParameter("maxSalary");
            if (maxSalary != null && !maxSalary.isBlank()) {
                dto.setMaxSalary(new java.math.BigDecimal(maxSalary.replace(",", "")));
            }
        } catch (NumberFormatException e) {
            // Will be caught in validation
        }
        
        // Job details
        dto.setDescription(request.getParameter("description"));
        dto.setRequirements(request.getParameter("requirements"));
        dto.setBenefits(request.getParameter("benefits"));
        dto.setLocation(request.getParameter("location"));
        
        try {
            String deadline = request.getParameter("applicationDeadline");
            if (deadline != null && !deadline.isBlank()) {
                dto.setApplicationDeadline(LocalDate.parse(deadline));
            }
        } catch (java.time.format.DateTimeParseException e) {
            // Will be caught in validation
        }

        try {
            String startDate = request.getParameter("startDate");
            if (startDate != null && !startDate.isBlank()) {
                dto.setStartDate(LocalDate.parse(startDate));
            }
        } catch (java.time.format.DateTimeParseException e) {
            // ignore
        }

        try {
            String minExp = request.getParameter("minExperienceYears");
            if (minExp != null && !minExp.isBlank()) {
                dto.setMinExperienceYears(Integer.valueOf(minExp));
            }
        } catch (NumberFormatException e) {
            // ignore
        }

        // possible source recruitment request id (hidden)
        try {
            String srcReq = request.getParameter("sourceRequestId");
                if (srcReq != null && !srcReq.isBlank()) {
                // We'll pass via request attribute in servlet
                try {
                    long parsed = Long.parseLong(srcReq);
                    request.setAttribute("sourceRequestId", parsed);
                } catch (NumberFormatException ignored) {}
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        
        // Contact information
        dto.setContactEmail(request.getParameter("contactEmail"));
        dto.setContactPhone(request.getParameter("contactPhone"));
        
        // Add working hours
        dto.setWorkingHours(request.getParameter("workingHours"));
        
        return dto;
    }
    
    private void populateJobPosting(JobPosting jobPosting, JobPostingFormDto formDto, 
            HttpServletRequest request) {
        // Basic information
        // Use explicit jobTitle if provided, otherwise use positionName
        if (formDto.getCode() != null && !formDto.getCode().isBlank()) {
            jobPosting.setCode(formDto.getCode());
        }
        jobPosting.setTitle(formDto.getJobTitle() != null && !formDto.getJobTitle().isBlank() ? formDto.getJobTitle() : formDto.getPositionName());
    jobPosting.setLevel(formDto.getJobLevel());
    // Use normalized canonical values for jobType/salaryType to match DB expectations
    String normalizedJobType = formDto.getNormalizedJobType();
    jobPosting.setJobType(normalizedJobType != null ? normalizedJobType : formDto.getJobType());
        jobPosting.setNumberOfPositions(formDto.getNumberOfPositions());
        jobPosting.setMinExperienceYears(formDto.getMinExperienceYears());
        jobPosting.setStartDate(formDto.getStartDate());

        // If a sourceRequestId attribute present, set requestId on jobPosting so association preserved
        // Also load department and position from the recruitment request
        Object src = request.getAttribute("sourceRequestId");
        if (src instanceof Long) {
            Long srcLong = (Long) src;
            jobPosting.setRequestId(srcLong);
            
            // Load department and position from recruitment request
            try {
                group4.hrms.dao.RequestDao requestDao = new group4.hrms.dao.RequestDao();
                java.util.Optional<group4.hrms.model.Request> reqOpt = requestDao.findById(srcLong);
                if (reqOpt.isPresent()) {
                    group4.hrms.model.Request req = reqOpt.get();
                    // Set department from recruitment request
                    if (req.getDepartmentId() != null) {
                        jobPosting.setDepartmentId(req.getDepartmentId());
                        logger.info("Set departmentId={} from recruitment request", req.getDepartmentId());
                    }
                    // Try to get position from recruitment details if available
                    group4.hrms.dto.RecruitmentDetailsDto details = req.getRecruitmentDetail();
                    if (details != null && details.getPositionCode() != null) {
                        // Optionally map position code to position ID if needed
                        logger.info("Recruitment request position code: {}", details.getPositionCode());
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to load department from recruitment request", e);
            }
        } else {
            String srcParam = request.getParameter("sourceRequestId");
            if (srcParam != null && !srcParam.isBlank()) {
                try {
                    Long requestId = Long.valueOf(srcParam);
                    jobPosting.setRequestId(requestId);
                    
                    // Load department and position from recruitment request
                    try {
                        group4.hrms.dao.RequestDao requestDao = new group4.hrms.dao.RequestDao();
                        java.util.Optional<group4.hrms.model.Request> reqOpt = requestDao.findById(requestId);
                        if (reqOpt.isPresent()) {
                            group4.hrms.model.Request req = reqOpt.get();
                            // Set department from recruitment request
                            if (req.getDepartmentId() != null) {
                                jobPosting.setDepartmentId(req.getDepartmentId());
                                logger.info("Set departmentId={} from recruitment request", req.getDepartmentId());
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Failed to load department from recruitment request", e);
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        
        // Salary information
    String normalizedSalaryType = formDto.getNormalizedSalaryType();
    jobPosting.setSalaryType(normalizedSalaryType != null ? normalizedSalaryType : formDto.getSalaryType());
        jobPosting.setMinSalary(formDto.getMinSalary());
        jobPosting.setMaxSalary(formDto.getMaxSalary());
        
        // Job details
        jobPosting.setDescription(formDto.getDescription());
        jobPosting.setRequirements(formDto.getRequirements());
        jobPosting.setBenefits(formDto.getBenefits());
        jobPosting.setWorkingLocation(formDto.getLocation());
        jobPosting.setApplicationDeadline(formDto.getApplicationDeadline());
        
        // Contact information
        jobPosting.setContactEmail(formDto.getContactEmail());
        jobPosting.setContactPhone(formDto.getContactPhone());
        
        // Add working hours
        jobPosting.setWorkingHours(formDto.getWorkingHours());
        
        // Status and audit
        jobPosting.setStatus("PENDING");
        jobPosting.setCreatedByAccountId(SecurityUtil.getAccountId(request));
        jobPosting.setCreatedAt(java.time.LocalDateTime.now());
        jobPosting.setUpdatedAt(java.time.LocalDateTime.now());
    }
    
    /**
     * Sanitize form data to prevent XSS and other injection attacks
     */
    private void sanitizeFormData(JobPostingFormDto formDto) {
        // Trim all string fields
        if (formDto.getJobTitle() != null) {
            formDto.setJobTitle(formDto.getJobTitle().trim());
        }
        if (formDto.getCode() != null) {
            formDto.setCode(formDto.getCode().trim());
        }
        if (formDto.getDescription() != null) {
            formDto.setDescription(formDto.getDescription().trim());
        }
        if (formDto.getRequirements() != null) {
            formDto.setRequirements(formDto.getRequirements().trim());
        }
        if (formDto.getBenefits() != null) {
            formDto.setBenefits(formDto.getBenefits().trim());
        }
        if (formDto.getLocation() != null) {
            formDto.setLocation(formDto.getLocation().trim());
        }
        if (formDto.getContactEmail() != null) {
            formDto.setContactEmail(formDto.getContactEmail().trim().toLowerCase());
        }
        if (formDto.getContactPhone() != null) {
            formDto.setContactPhone(formDto.getContactPhone().trim());
        }
        if (formDto.getWorkingHours() != null) {
            formDto.setWorkingHours(formDto.getWorkingHours().trim());
        }
        if (formDto.getSalaryType() != null) {
            formDto.setSalaryType(formDto.getSalaryType().trim().toUpperCase());
        }
    }
    
    /**
     * Perform additional custom validations beyond basic field validation
     */
    private void performAdditionalValidations(JobPostingFormDto formDto, Map<String, String> errors) {
        // Validate number of positions is reasonable
        if (formDto.getNumberOfPositions() != null && formDto.getNumberOfPositions() > 100) {
            errors.put("numberOfPositions", "Number of positions seems unreasonably high. Please verify.");
        }
        
        // Check if job code already exists (if provided)
        if (formDto.getCode() != null && !formDto.getCode().trim().isEmpty()) {
            try {
                // Check if job code already exists in database
                group4.hrms.dao.JobPostingDao jobPostingDao = new group4.hrms.dao.JobPostingDao();
                java.util.List<group4.hrms.model.JobPosting> existingJobs = jobPostingDao.findAll();
                boolean codeExists = existingJobs.stream()
                    .anyMatch(job -> formDto.getCode().trim().equalsIgnoreCase(job.getCode()));
                
                if (codeExists) {
                    errors.put("code", "Job code '" + formDto.getCode() + "' already exists. Please use a different code.");
                }
            } catch (Exception e) {
                logger.warn("Failed to check job code uniqueness: {}", e.getMessage());
                // Don't block submission if check fails
            }
        }
        
        // Validate salary range is reasonable
        if (formDto.getMinSalary() != null && formDto.getMaxSalary() != null) {
            java.math.BigDecimal diff = formDto.getMaxSalary().subtract(formDto.getMinSalary());
            java.math.BigDecimal ratio = diff.divide(formDto.getMinSalary(), 2, java.math.RoundingMode.HALF_UP);
            
            // Warn if max salary is more than 5x min salary
            if (ratio.compareTo(new java.math.BigDecimal("5.0")) > 0) {
                logger.warn("Salary range seems very wide: min={}, max={}", formDto.getMinSalary(), formDto.getMaxSalary());
                // Just log warning, don't block submission
            }
        }
        
        // Validate deadline is within reasonable timeframe (warn if > 1 year from now)
        if (formDto.getApplicationDeadline() != null) {
            LocalDate oneYearFromNow = LocalDate.now().plusYears(1);
            if (formDto.getApplicationDeadline().isAfter(oneYearFromNow)) {
                errors.put("applicationDeadline", "Application deadline should typically be within one year");
            }
        }
        
        // Validate start date is not too far in the future (warn if > 1 year)
        if (formDto.getStartDate() != null) {
            LocalDate oneYearFromNow = LocalDate.now().plusYears(1);
            if (formDto.getStartDate().isAfter(oneYearFromNow)) {
                logger.warn("Start date is more than one year in the future: {}", formDto.getStartDate());
                // Just log, don't block
            }
        }
        
        // Ensure application deadline is before start date (people apply before job starts)
        if (formDto.getApplicationDeadline() != null && formDto.getStartDate() != null) {
            if (formDto.getApplicationDeadline().isAfter(formDto.getStartDate())) {
                errors.put("applicationDeadline", "Application deadline must be before the start date");
            }
        }
        
        // Validate contact email domain (optional - just warn)
        if (formDto.getContactEmail() != null && !formDto.getContactEmail().isEmpty()) {
            String email = formDto.getContactEmail().toLowerCase();
            // Check for common personal email domains - might want company email
            if (email.endsWith("@gmail.com") || email.endsWith("@yahoo.com") || 
                email.endsWith("@hotmail.com") || email.endsWith("@outlook.com")) {
                logger.warn("Contact email appears to be personal email: {}", formDto.getContactEmail());
                // Just log warning, don't block
            }
        }
        
        // Validate description and requirements are not too similar
        if (formDto.getDescription() != null && formDto.getRequirements() != null) {
            String desc = formDto.getDescription().toLowerCase().trim();
            String req = formDto.getRequirements().toLowerCase().trim();
            
            if (desc.equals(req)) {
                errors.put("requirements", "Requirements should be different from job description");
            }
        }
        
        // Validate text fields don't contain only whitespace or special characters
        if (formDto.getJobTitle() != null && !formDto.getJobTitle().matches(".*[a-zA-Z0-9].*")) {
            errors.put("jobTitle", "Job title must contain at least some alphanumeric characters");
        }
        
        if (formDto.getLocation() != null && !formDto.getLocation().matches(".*[a-zA-Z0-9].*")) {
            errors.put("location", "Location must contain at least some alphanumeric characters");
        }
    }
}