package group4.hrms.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.dto.JobPostingFormDto;
import group4.hrms.model.JobPosting;
import group4.hrms.model.User;
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
 * Controller for HR to edit a job posting
 */
@WebServlet("/job-posting/edit")
public class JobPostingEditServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(JobPostingEditServlet.class);
    
    @Inject
    private JobPostingService jobPostingService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private PositionService positionService;

    @Override
    public void init() throws ServletException {
        super.init();

        // If CDI injection didn't run in this environment, fall back to ServletContext-provided services
        if (jobPostingService == null) {
            Object js = getServletContext().getAttribute("jobPostingService");
            if (js instanceof JobPostingService) {
                jobPostingService = (JobPostingService) js;
            } else {
                throw new ServletException("JobPostingService not found in ServletContext");
            }
        }

        if (departmentService == null) {
            Object ds = getServletContext().getAttribute("departmentService");
            if (ds instanceof DepartmentService) {
                departmentService = (DepartmentService) ds;
            } else {
                logger.warn("DepartmentService not found in ServletContext; some features may be limited");
            }
        }

        if (positionService == null) {
            Object ps = getServletContext().getAttribute("positionService");
            if (ps instanceof PositionService) {
                positionService = (PositionService) ps;
            } else {
                logger.warn("PositionService not found in ServletContext; some features may be limited");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Get user's position ID for permission check 
        User user = (User) request.getSession().getAttribute("user");
        Long positionId = user != null ? user.getPositionId() : null;
        
        // Only HR (8) can edit job postings
        if (!JobPostingPermissionHelper.canManageJobPosting(positionId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Must be logged in as HR.");
            return;
        }

        // Get job posting ID
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Missing job posting ID");
            return;
        }

        try {
            // Load job posting
            long id = Long.parseLong(idStr);
            JobPosting jobPosting = jobPostingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found: " + id));
            
            // Verify job posting can be edited by current user
            Long currentAccountId = SecurityUtil.getAccountId(request);
            if (!jobPosting.canBeEditedBy(currentAccountId)) {
                logger.warn("User {} cannot access edit page for job posting {} (status: {}, creator: {})", 
                    currentAccountId, id, jobPosting.getStatus(), jobPosting.getCreatedByAccountId());
                response.sendRedirect(request.getContextPath() + 
                    "/job-postings?error=You do not have permission to edit this job posting");
                return;
            }
            logger.info("✅ User {} accessing edit page for job posting {} (status: {})", 
                currentAccountId, id, jobPosting.getStatus());
            
            // Load department and position lists
            request.setAttribute("departments", departmentService.getAllDepartments());
            request.setAttribute("positions", positionService.getAllPositions());
            
            // Pass job posting to view
            request.setAttribute("jobPosting", jobPosting);
            
            // Set CSRF token
            request.setAttribute("csrfToken", SecurityUtil.generateCsrfToken(request.getSession()));
            
            // Forward to edit form
            request.getRequestDispatcher("/WEB-INF/views/job-postings/edit.jsp")
                   .forward(request, response);
                   
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Invalid job posting ID");
        } catch (Exception e) {
            logger.error("Error loading job posting for edit", e);
            response.sendRedirect(request.getContextPath() + 
                "/job-postings?error=" + java.net.URLEncoder.encode("Error loading job posting: " + e.getMessage(), "UTF-8"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 1. Get user's position ID for permission check 
        User user = (User) request.getSession().getAttribute("user");
        Long positionId = user != null ? user.getPositionId() : null;
        
        // Only HR (8) can edit job postings
        if (!JobPostingPermissionHelper.canManageJobPosting(positionId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Must be logged in as HR.");
            return;
        }

        // 2. Validate CSRF Token
        String csrfToken = request.getParameter("csrfToken");
        if (!SecurityUtil.verifyCsrfToken(request.getSession(), csrfToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        // 3. Get job posting ID
        String idStr = request.getParameter("id");
        logger.info("📝 POST request received - ID: {}", idStr);
        
        // Debug: Log all parameters
        logger.debug("All request parameters:");
        request.getParameterMap().forEach((key, values) -> {
            logger.debug("  {} = {}", key, values.length > 0 ? values[0] : "");
        });
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Missing job posting ID");
            return;
        }

        try {
            // 4. Load existing job posting
            long id = Long.parseLong(idStr);
            JobPosting jobPosting = jobPostingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found: " + id));
            
            // 5. Verify job posting can be edited by current user
            Long currentAccountId = SecurityUtil.getAccountId(request);
            if (!jobPosting.canBeEditedBy(currentAccountId)) {
                logger.warn("User {} cannot edit job posting {} (status: {}, creator: {})", 
                    currentAccountId, id, jobPosting.getStatus(), jobPosting.getCreatedByAccountId());
                response.sendRedirect(request.getContextPath() + 
                    "/job-postings?error=You do not have permission to edit this job posting");
                return;
            }
            logger.info("✅ User {} can edit job posting {} (status: {})", 
                currentAccountId, id, jobPosting.getStatus());

            // 6. Parse and validate form data
            JobPostingFormDto formDto = parseFormData(request);
            sanitizeFormData(formDto);
            Map<String, String> errors = formDto.validate();
            
            logger.info("Validation result: {} errors", errors.size());
            if (!errors.isEmpty()) {
                logger.error("❌ Validation FAILED:");
                errors.forEach((field, message) -> {
                    logger.error("  - {}: {}", field, message);
                });
                
                // Re-populate form with error messages
                request.setAttribute("errors", errors);
                request.setAttribute("formData", formDto);
                request.setAttribute("jobPosting", jobPosting);
                
                // Re-populate department and position lists
                if (departmentService != null) {
                    request.setAttribute("departments", departmentService.getAllDepartments());
                }
                if (positionService != null) {
                    request.setAttribute("positions", positionService.getAllPositions());
                }
                
                request.getRequestDispatcher("/WEB-INF/views/job-postings/edit.jsp")
                       .forward(request, response);
                return;
            }
            
            logger.info("✅ Validation PASSED - Proceeding with update");
            
            // 7. Update job posting
            logger.info("Updating job posting ID: {}", id);
            String originalStatus = jobPosting.getStatus();
            logger.debug("Form data before populate: title={}, description={}", formDto.getJobTitle(), formDto.getDescription());
            populateJobPosting(jobPosting, formDto, request);
            
            // 7a. If job posting was REJECTED, set back to PENDING for re-approval
            if ("REJECTED".equals(originalStatus)) {
                logger.info("🔄 Job posting was REJECTED, resetting to PENDING for re-approval");
                jobPosting.setStatus("PENDING");
                jobPosting.setRejectedReason(null);
                jobPosting.setApprovedByAccountId(null);
                jobPosting.setApprovedAt(null);
                logger.info("✅ Status reset: REJECTED → PENDING");
            }
            
            logger.debug("Job posting after populate: {}", jobPosting);
            jobPostingService.update(jobPosting);
            logger.info("Job posting updated successfully: {}", id);
            
            // 8. Redirect to success page with appropriate message
            String successMessage = "REJECTED".equals(originalStatus) 
                ? "Job posting updated and resubmitted for approval" 
                : "Job posting updated successfully";
            response.sendRedirect(request.getContextPath() + 
                "/job-postings?success=" + java.net.URLEncoder.encode(successMessage, "UTF-8"));
                
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Invalid job posting ID");
        } catch (Exception e) {
            logger.error("Error updating job posting", e);
            response.sendRedirect(request.getContextPath() + 
                "/job-postings?error=" + java.net.URLEncoder.encode("Error updating job posting: " + e.getMessage(), "UTF-8"));
        }
    }

    private JobPostingFormDto parseFormData(HttpServletRequest request) {
        JobPostingFormDto dto = new JobPostingFormDto();
        
        // Basic information
        dto.setJobTitle(request.getParameter("jobTitle"));
        dto.setCode(request.getParameter("code"));
        dto.setJobLevel(request.getParameter("jobLevel"));
        dto.setJobType(request.getParameter("jobType"));
        
        try {
            dto.setNumberOfPositions(Integer.valueOf(request.getParameter("numberOfPositions")));
        } catch (NumberFormatException e) {
            dto.setNumberOfPositions(null);
        }
        
        // Experience
        try {
            dto.setMinExperienceYears(Integer.valueOf(request.getParameter("minExperienceYears")));
        } catch (NumberFormatException e) {
            dto.setMinExperienceYears(null);
        }
        
        // Salary information
        dto.setSalaryType(request.getParameter("salaryType"));
        try {
            String minSalary = request.getParameter("minSalary");
            if (minSalary != null && !minSalary.isBlank()) {
                dto.setMinSalary(new java.math.BigDecimal(minSalary));
            }
        } catch (NumberFormatException e) {
            // Invalid number format - will be caught by validation
        }
        
        try {
            String maxSalary = request.getParameter("maxSalary");
            if (maxSalary != null && !maxSalary.isBlank()) {
                dto.setMaxSalary(new java.math.BigDecimal(maxSalary));
            }
        } catch (NumberFormatException e) {
            // Invalid number format - will be caught by validation
        }
        
        // Job details
        dto.setDescription(request.getParameter("description"));
        dto.setRequirements(request.getParameter("requirements"));
        dto.setBenefits(request.getParameter("benefits"));
        dto.setLocation(request.getParameter("location"));
        
        // Dates
        try {
            String startDate = request.getParameter("startDate");
            if (startDate != null && !startDate.isBlank()) {
                dto.setStartDate(java.time.LocalDate.parse(startDate));
            }
        } catch (java.time.format.DateTimeParseException e) {
            // Invalid date format - will be caught by validation
        }
        
        try {
            String deadline = request.getParameter("applicationDeadline");
            if (deadline != null && !deadline.isBlank()) {
                dto.setApplicationDeadline(java.time.LocalDate.parse(deadline));
            }
        } catch (java.time.format.DateTimeParseException e) {
            // Invalid date format - will be caught by validation
        }
        
        // Contact information
        dto.setContactEmail(request.getParameter("contactEmail"));
        dto.setContactPhone(request.getParameter("contactPhone"));
        
        // Additional fields
        dto.setWorkingHours(request.getParameter("workingHours"));
        
        return dto;
    }

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
        if (formDto.getJobType() != null) {
            formDto.setJobType(formDto.getJobType().trim().toUpperCase());
        }
        if (formDto.getJobLevel() != null) {
            formDto.setJobLevel(formDto.getJobLevel().trim().toUpperCase());
        }
    }

    private void populateJobPosting(JobPosting jobPosting, JobPostingFormDto formDto, 
            HttpServletRequest request) {
        // Basic information
        if (formDto.getCode() != null && !formDto.getCode().isBlank()) {
            jobPosting.setCode(formDto.getCode());
        }
        if (formDto.getJobTitle() != null && !formDto.getJobTitle().isBlank()) {
            jobPosting.setTitle(formDto.getJobTitle());
        }
        
        // Keep readonly fields as is (they come from form as hidden inputs)
        if (formDto.getJobLevel() != null && !formDto.getJobLevel().isBlank()) {
            jobPosting.setLevel(formDto.getJobLevel());
        }
        
        // Use normalized canonical values for jobType/salaryType to match DB expectations
        String normalizedJobType = formDto.getNormalizedJobType();
        jobPosting.setJobType(normalizedJobType != null ? normalizedJobType : formDto.getJobType());
        
        if (formDto.getNumberOfPositions() != null) {
            jobPosting.setNumberOfPositions(formDto.getNumberOfPositions());
        }
        
        // Experience and dates
        jobPosting.setMinExperienceYears(formDto.getMinExperienceYears());
        jobPosting.setStartDate(formDto.getStartDate());
        jobPosting.setApplicationDeadline(formDto.getApplicationDeadline());
        
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
        
        // Contact information
        jobPosting.setContactEmail(formDto.getContactEmail());
        jobPosting.setContactPhone(formDto.getContactPhone());
        
        // Working hours
        jobPosting.setWorkingHours(formDto.getWorkingHours());
        
        // Update audit fields
        jobPosting.setUpdatedAt(java.time.LocalDateTime.now());
    }
}