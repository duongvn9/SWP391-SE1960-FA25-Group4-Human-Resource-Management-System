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
import group4.hrms.util.JobPostingPermissionHelper;
import group4.hrms.util.SecurityUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
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
        
        // Only HR (8) or HR Manager (7) can create job postings
        if (!JobPostingPermissionHelper.canManageJobPosting(sessPositionId2)) {
            logger.error("Access denied - Invalid position ID: {}", sessPositionId2);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
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
        request.setAttribute("csrfToken", SecurityUtil.generateCsrfToken(request.getSession()));

        // If requestId is provided, load recruitment request and prefill fields
        String requestIdStr = request.getParameter("requestId");
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
        logger.info("JobPostingCreateServlet.doPost: starting...");
        
        // Get user's position ID for permission check 
        Object sessUserObj2 = request.getSession(false) != null ? request.getSession(false).getAttribute("user") : null;
        Long sessPositionId2 = null;
        if (sessUserObj2 instanceof group4.hrms.model.User) {
            group4.hrms.model.User su2 = (group4.hrms.model.User) sessUserObj2;
            sessPositionId2 = su2.getPositionId();
        }
        
        // Only HR (8) or HR Manager (7) can create job postings
        if (!JobPostingPermissionHelper.canManageJobPosting(sessPositionId2)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Log all form parameters for debugging
        Map<String, String[]> params = request.getParameterMap();
        for (String key : params.keySet()) {
            if (!key.contains("password") && !key.contains("csrf")) { // Don't log sensitive data
                logger.info("Form field: {} = {}", key, String.join(", ", params.get(key)));
            }
        }

        // Validate CSRF token
        String csrfToken = request.getParameter("csrfToken");
        if (!SecurityUtil.isValidCsrfToken(request)) {
            logger.error("Invalid CSRF token provided: {}", csrfToken);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid CSRF token");
            return;
        }
        logger.info("CSRF token validation passed");

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
            
            Map<String, String> errors = formDto.validate();
            if (!errors.isEmpty()) {
                logger.error("Form validation failed with errors: {}", errors);
                
                // Re-populate form with error messages
                request.setAttribute("errors", errors);
                request.setAttribute("formData", formDto); // Add this to preserve form data
                
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
            
            logger.info("Form validation passed successfully");            // Create job posting
            JobPosting jobPosting = new JobPosting();
            populateJobPosting(jobPosting, formDto, request);
            logger.debug("Populated JobPosting before save: {}", jobPosting);
            
            // Save and get ID
            if (jobPostingService == null) {
                throw new RuntimeException("JobPostingService is not available (dependency injection failed)");
            }
            
            try {
                logger.debug("Calling jobPostingService.create; service instance = {}", jobPostingService);
                long createdId = jobPostingService.create(jobPosting);
                logger.info("JobPostingCreateServlet: created job posting id={}", createdId);

                // Redirect back to recruitment approved list with success message
                response.sendRedirect(request.getContextPath() + 
                    "/recruitment/approved?success=Job posting created successfully. Pending HRM approval.");
                return; // Important: stop processing after redirect
            } catch (Exception e) {
                logger.error("Failed to create job posting", e);
                request.setAttribute("error", "Failed to create job posting: Database error");
                request.setAttribute("formData", formDto);
                request.getRequestDispatcher("/WEB-INF/views/job-postings/create.jsp")
                       .forward(request, response);
                return;
            }
            
        } catch (RuntimeException e) {
            request.setAttribute("error", "Failed to create job posting: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/job-postings/create.jsp")
                   .forward(request, response);
        }
    }
    
    private JobPostingFormDto parseFormData(HttpServletRequest request) {
        JobPostingFormDto dto = new JobPostingFormDto();
        
        // Basic information
    dto.setPositionCode(request.getParameter("positionCode"));
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
        
        // Add priority and working hours
        dto.setPriority(request.getParameter("priority"));
        dto.setWorkingHours(request.getParameter("workingHours"));
        
        return dto;
    }
    
    private void populateJobPosting(JobPosting jobPosting, JobPostingFormDto formDto, 
            HttpServletRequest request) {
        // Basic information
        // Use explicit jobTitle if provided, otherwise use positionName
        jobPosting.setCode(formDto.getCode() != null && !formDto.getCode().isBlank() ? formDto.getCode() : formDto.getPositionCode());
        jobPosting.setTitle(formDto.getJobTitle() != null && !formDto.getJobTitle().isBlank() ? formDto.getJobTitle() : formDto.getPositionName());
    jobPosting.setLevel(formDto.getJobLevel());
    // Use normalized canonical values for jobType/salaryType to match DB expectations
    String normalizedJobType = formDto.getNormalizedJobType();
    jobPosting.setJobType(normalizedJobType != null ? normalizedJobType : formDto.getJobType());
        jobPosting.setNumberOfPositions(formDto.getNumberOfPositions());
        jobPosting.setMinExperienceYears(formDto.getMinExperienceYears());
        jobPosting.setStartDate(formDto.getStartDate());

        // If a sourceRequestId attribute present, set requestId on jobPosting so association preserved
        Object src = request.getAttribute("sourceRequestId");
        if (src instanceof Long) {
            Long srcLong = (Long) src;
            jobPosting.setRequestId(srcLong);
        } else {
            String srcParam = request.getParameter("sourceRequestId");
            if (srcParam != null && !srcParam.isBlank()) {
                try {
                    jobPosting.setRequestId(Long.valueOf(srcParam));
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
        
        // Add priority and working hours
        jobPosting.setPriority(formDto.getPriority() != null ? formDto.getPriority() : "MEDIUM");
        jobPosting.setWorkingHours(formDto.getWorkingHours());
        
        // Status and audit
        jobPosting.setStatus("PENDING");
        jobPosting.setCreatedByAccountId(SecurityUtil.getAccountId(request));
        jobPosting.setCreatedAt(java.time.LocalDateTime.now());
        jobPosting.setUpdatedAt(java.time.LocalDateTime.now());
    }
}