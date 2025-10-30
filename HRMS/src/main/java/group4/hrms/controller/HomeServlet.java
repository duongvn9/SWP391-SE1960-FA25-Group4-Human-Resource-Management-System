package group4.hrms.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.model.Department;
import group4.hrms.model.JobPosting;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.JobPostingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HomeServlet: Điều hướng root (guest) tới landing page.
 */
@WebServlet(name = "HomeServlet", urlPatterns = { "/", "/home" })
public class HomeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);
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
        logger.info("HomeServlet forwarding to landing page");

        // Handle logout message
        String logoutMessage = req.getParameter("logoutMessage");
        if (logoutMessage != null && !logoutMessage.trim().isEmpty()) {
            req.setAttribute("logoutMessage", logoutMessage);
        }
        // Load latest published job postings for public landing (max 6)
        try {
            if (jobPostingService != null) {
                Map<String, Object> criteria = new HashMap<>();
                criteria.put("status", "PUBLISHED");
                List<JobPosting> publishedJobs = jobPostingService.findJobPostings(criteria, 1, 6);
                req.setAttribute("publishedJobs", publishedJobs);
            }
            if (departmentService != null) {
                List<Department> departments = departmentService.getAllDepartments();
                req.setAttribute("departments", departments);
            }
        } catch (Exception ex) {
            logger.warn("Failed to load published jobs for landing: {}", ex.getMessage());
            req.setAttribute("publishedJobs", java.util.Collections.emptyList());
        }

        req.getRequestDispatcher("/WEB-INF/views/home/landing.jsp").forward(req, resp);
    }
}
