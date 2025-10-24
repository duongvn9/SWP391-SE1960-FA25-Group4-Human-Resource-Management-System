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
            
            // Verify job posting can be edited
            if (!jobPosting.canBeEdited()) {
                response.sendRedirect(request.getContextPath() + 
                    "/job-postings?error=Job posting cannot be edited in its current state");
                return;
            }
            
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
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Missing job posting ID");
            return;
        }

        try {
            // 4. Load existing job posting
            long id = Long.parseLong(idStr);
            JobPosting jobPosting = jobPostingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found: " + id));
            
            // 5. Verify job posting can be edited
            if (!jobPosting.canBeEdited()) {
                response.sendRedirect(request.getContextPath() + 
                    "/job-postings?error=Job posting cannot be edited in its current state");
                return;
            }

            // 6. Parse and validate form data
            JobPostingFormDto formDto = parseFormData(request);
            sanitizeFormData(formDto);
            Map<String, String> errors = formDto.validate();
            
            if (!errors.isEmpty()) {
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
            
            // 7. Update job posting
            populateJobPosting(jobPosting, formDto, request);
            jobPostingService.update(jobPosting);
            
            // 8. Redirect to success page
            response.sendRedirect(request.getContextPath() + 
                "/job-postings?success=Job posting updated successfully");
                
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
        dto.setPriority(request.getParameter("priority"));
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
        jobPosting.setCode(formDto.getCode() != null && !formDto.getCode().isBlank() 
            ? formDto.getCode() : formDto.getPositionCode());
        jobPosting.setTitle(formDto.getJobTitle() != null && !formDto.getJobTitle().isBlank() 
            ? formDto.getJobTitle() : formDto.getPositionName());
        jobPosting.setLevel(formDto.getJobLevel());
        
        // Use normalized canonical values for jobType/salaryType to match DB expectations
        String normalizedJobType = formDto.getNormalizedJobType();
        jobPosting.setJobType(normalizedJobType != null ? normalizedJobType : formDto.getJobType());
        jobPosting.setNumberOfPositions(formDto.getNumberOfPositions());
        
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
        
        // Add priority and working hours
        jobPosting.setPriority(formDto.getPriority() != null ? formDto.getPriority() : "MEDIUM");
        jobPosting.setWorkingHours(formDto.getWorkingHours());
        
        // Update audit fields
        jobPosting.setUpdatedAt(java.time.LocalDateTime.now());
    }
}