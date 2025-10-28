package group4.hrms.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.model.Department;
import group4.hrms.model.JobPosting;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.JobPostingService;
import group4.hrms.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/job-postings")
public class JobPostingListServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(JobPostingListServlet.class);
    private JobPostingService jobPostingService;
    private DepartmentService departmentService;

    @Override
    public void init() throws ServletException {
        Object js = getServletContext().getAttribute("jobPostingService");
        if (js instanceof JobPostingService) {
            jobPostingService = (JobPostingService) js;
        } else {
            throw new ServletException("JobPostingService not found in ServletContext");
        }

        Object ds = getServletContext().getAttribute("departmentService");
        if (ds instanceof DepartmentService) {
            departmentService = (DepartmentService) ds;
        } else {
            throw new ServletException("DepartmentService not found in ServletContext");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Collect all filter parameters
        String status = request.getParameter("status");
        String departmentIdStr = request.getParameter("departmentId");
        String jobType = request.getParameter("jobType");
        String jobLevel = request.getParameter("jobLevel");
        String priority = request.getParameter("priority");
        String searchQuery = request.getParameter("q"); // For title/code search
        String sortBy = request.getParameter("sortBy"); // For sorting
        String pageStr = request.getParameter("page");
        
        logger.info("Job Postings List - Filters: status={}, dept={}, type={}, level={}, priority={}", 
                    status, departmentIdStr, jobType, jobLevel, priority);
        
        // Parse pagination parameters
        int page = 1;
        int pageSize = 6; // 6 items per page for better readability
        if (pageStr != null) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }
        
        // Parse department ID
        Long departmentId = null;
        if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
            try {
                departmentId = Long.parseLong(departmentIdStr);
            } catch (NumberFormatException ignored) {}
        }

        // Build search criteria map
        Map<String, Object> searchCriteria = new HashMap<>();
        // Don't default to PENDING - show all if not specified
        if (status != null && !status.isEmpty()) {
            searchCriteria.put("status", status);
        }
        if (departmentId != null) searchCriteria.put("departmentId", departmentId);
        if (jobType != null && !jobType.isEmpty()) searchCriteria.put("jobType", jobType);
        if (jobLevel != null && !jobLevel.isEmpty()) searchCriteria.put("jobLevel", jobLevel);
        if (priority != null && !priority.isEmpty()) searchCriteria.put("priority", priority);
        if (searchQuery != null && !searchQuery.trim().isEmpty()) searchCriteria.put("searchQuery", searchQuery.trim());
        if (sortBy != null && !sortBy.isEmpty()) searchCriteria.put("sortBy", sortBy);
        
        // Get paginated results using service
        List<JobPosting> jobPostings;
        int totalCount;
        try {
            if (jobPostingService == null) {
                logger.error("JobPostingService is null");
                request.setAttribute("error", "Service temporarily unavailable");
                jobPostings = new ArrayList<>();
                totalCount = 0;
            } else {
                jobPostings = jobPostingService.findJobPostings(searchCriteria, page, pageSize);
                totalCount = jobPostingService.countJobPostings(searchCriteria);
                logger.info("Found {} job postings matching criteria", jobPostings.size());
            }
        } catch (Exception e) {
            logger.error("Error getting job postings: {}", e.getMessage(), e);
            request.setAttribute("error", "Error loading job postings: " + e.getMessage());
            jobPostings = new ArrayList<>();
            totalCount = 0;
        }
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        // Get reference data for filters
        List<Department> departments = departmentService.getAllDepartments();
        List<String> jobTypes = jobPostingService.getAllJobTypes(); // Get from service
        List<String> jobLevels = jobPostingService.getAllJobLevels(); // Get from service

        // Set all attributes for view
        request.setAttribute("jobPostings", jobPostings);
        request.setAttribute("departments", departments);
        request.setAttribute("jobTypes", jobTypes);
        request.setAttribute("jobLevels", jobLevels);
        
        // Set filter values for re-population
        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedDepartmentId", departmentId);
        request.setAttribute("selectedJobType", jobType);
        request.setAttribute("selectedJobLevel", jobLevel);
        request.setAttribute("selectedPriority", priority);
        request.setAttribute("searchQuery", searchQuery);
        request.setAttribute("sortBy", sortBy);
        
        // Set pagination data
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalItems", totalCount);
        request.setAttribute("totalPages", totalPages);
        
        // Set CSRF token
        request.setAttribute("csrfToken", SecurityUtil.generateCsrfToken(request.getSession()));
        
        // Forward to view
        request.getRequestDispatcher("/WEB-INF/views/job-postings/list.jsp")
               .forward(request, response);
    }
}
