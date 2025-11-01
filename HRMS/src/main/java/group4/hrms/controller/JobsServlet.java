package group4.hrms.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group4.hrms.model.Department;
import group4.hrms.model.JobPosting;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.JobPostingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/jobs")
public class JobsServlet extends HttpServlet {
    private JobPostingService jobPostingService;
    private DepartmentService departmentService;

    @Override
    public void init() throws ServletException {
        Object js = getServletContext().getAttribute("jobPostingService");
        if (js instanceof JobPostingService) {
            jobPostingService = (JobPostingService) js;
        }
        Object ds = getServletContext().getAttribute("departmentService");
        if (ds instanceof DepartmentService) {
            departmentService = (DepartmentService) ds;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1;
        int pageSize = 6; // 6 cards per page
        String pageStr = req.getParameter("page");
        if (pageStr != null) {
            try { page = Integer.parseInt(pageStr); if (page < 1) page = 1; } catch (NumberFormatException ignored) {}
        }

        List<JobPosting> jobs = java.util.Collections.emptyList();
        int totalItems = 0;
        if (jobPostingService != null) {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("status", "PUBLISHED");
            // Filtering params from UI
            String title = req.getParameter("title");
            String deptIdStr = req.getParameter("departmentId");
            String jobType = req.getParameter("jobType");
            if (title != null && !title.trim().isEmpty()) {
                criteria.put("searchQuery", title.trim());
            }
            if (deptIdStr != null && !deptIdStr.trim().isEmpty()) {
                try {
                    Long deptId = Long.parseLong(deptIdStr);
                    criteria.put("departmentId", deptId);
                } catch (NumberFormatException e) {
                    // ignore invalid id
                }
            }
            if (jobType != null && !jobType.trim().isEmpty()) {
                criteria.put("jobType", jobType.trim());
            }
            jobs = jobPostingService.findJobPostings(criteria, page, pageSize);
            totalItems = jobPostingService.countJobPostings(criteria);
        }
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages < 1) totalPages = 1;

        if (departmentService != null) {
            List<Department> departments = departmentService.getAllDepartments();
            req.setAttribute("departments", departments);
        }

        // Build query string to preserve filters in pagination links
        StringBuilder qs = new StringBuilder();
        String titleParam = req.getParameter("title");
        String departmentParam = req.getParameter("departmentId");
        String jobTypeParam = req.getParameter("jobType");
        try {
            if (titleParam != null && !titleParam.isEmpty()) {
                qs.append("&title=").append(java.net.URLEncoder.encode(titleParam, java.nio.charset.StandardCharsets.UTF_8.toString()));
            }
            if (departmentParam != null && !departmentParam.isEmpty()) {
                qs.append("&departmentId=").append(java.net.URLEncoder.encode(departmentParam, java.nio.charset.StandardCharsets.UTF_8.toString()));
            }
            if (jobTypeParam != null && !jobTypeParam.isEmpty()) {
                qs.append("&jobType=").append(java.net.URLEncoder.encode(jobTypeParam, java.nio.charset.StandardCharsets.UTF_8.toString()));
            }
        } catch (Exception e) {
            // ignore encoding issues
        }
        req.setAttribute("queryString", qs.toString());

        req.setAttribute("jobs", jobs);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("totalItems", totalItems);

        req.getRequestDispatcher("/WEB-INF/views/job.jsp").forward(req, resp);
    }
}


