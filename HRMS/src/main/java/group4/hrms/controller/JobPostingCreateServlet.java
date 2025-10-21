package group4.hrms.controller;

import group4.hrms.dto.JobPostingFormDto;
import group4.hrms.model.JobPosting;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.JobPostingService;
import group4.hrms.service.PositionService;
import group4.hrms.util.SecurityUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

/**
 * Controller for HR to create new job posting
 */
@WebServlet("/job-posting/create")
public class JobPostingCreateServlet extends HttpServlet {
    
    @Inject
    private JobPostingService jobPostingService;

    @Inject
    private DepartmentService departmentService;

    @Inject
    private PositionService positionService;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Only HR can create job postings
        if (!SecurityUtil.hasRole(request, "HR")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Add departments and positions for dropdowns if needed
        request.setAttribute("departments", departmentService.getAllDepartments());
        request.setAttribute("positions", positionService.getAllPositions());
        
        // Forward to create form
        request.getRequestDispatcher("/WEB-INF/views/job-postings/create.jsp")
               .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Only HR can create job postings
        if (!SecurityUtil.hasRole(request, "HR")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Validate CSRF token
        if (!SecurityUtil.isValidCsrfToken(request)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid CSRF token");
            return;
        }

        try {
            // Parse and validate form data
            JobPostingFormDto formDto = parseFormData(request);
            Map<String, String> errors = formDto.validate();
            
            if (!errors.isEmpty()) {
                // Re-populate form with error messages
                request.setAttribute("errors", errors);
                request.setAttribute("departments", departmentService.getAllDepartments());
                request.setAttribute("positions", positionService.getAllPositions());
                request.getRequestDispatcher("/WEB-INF/views/job-postings/create.jsp")
                       .forward(request, response);
                return;
            }
            
            // Create job posting
            JobPosting jobPosting = new JobPosting();
            populateJobPosting(jobPosting, formDto, request);
            
            // Save and get ID
            long id = jobPostingService.create(jobPosting);
            
            // Redirect to success page
            response.sendRedirect(request.getContextPath() + 
                "/job-postings?success=Job posting created successfully. Pending HRM approval.");
            
        } catch (Exception e) {
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
        } catch (Exception e) {
            // Will be caught in validation
        }
        
        // Contact information
        dto.setContactEmail(request.getParameter("contactEmail"));
        dto.setContactPhone(request.getParameter("contactPhone"));
        
        return dto;
    }
    
    private void populateJobPosting(JobPosting jobPosting, JobPostingFormDto formDto, 
            HttpServletRequest request) {
        // Basic information
        jobPosting.setCode(formDto.getPositionCode());
        jobPosting.setTitle(formDto.getPositionName());
        jobPosting.setLevel(formDto.getJobLevel());
        jobPosting.setJobType(formDto.getJobType());
        jobPosting.setNumberOfPositions(formDto.getNumberOfPositions());
        
        // Salary information
        jobPosting.setSalaryType(formDto.getSalaryType());
        jobPosting.setMinSalary(formDto.getMinSalary());
        jobPosting.setMaxSalary(formDto.getMaxSalary());
        
        // Job details
        jobPosting.setDescription(formDto.getDescription());
        jobPosting.setRequirements(formDto.getRequirements());
        jobPosting.setBenefits(formDto.getBenefits());
        jobPosting.setLocation(formDto.getLocation());
        jobPosting.setApplicationDeadline(formDto.getApplicationDeadline());
        
        // Contact information
        jobPosting.setContactEmail(formDto.getContactEmail());
        jobPosting.setContactPhone(formDto.getContactPhone());
        
        // Status and audit
        jobPosting.setStatus("PENDING");
        jobPosting.setCreatedByAccountId(SecurityUtil.getAccountId(request));
        jobPosting.setCreatedAt(java.time.LocalDateTime.now());
        jobPosting.setUpdatedAt(java.time.LocalDateTime.now());
    }
}