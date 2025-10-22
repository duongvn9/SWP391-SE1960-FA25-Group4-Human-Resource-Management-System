package group4.hrms.servlet;

import group4.hrms.service.JobPostingService;
import group4.hrms.util.SecurityUtil;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JobPostingApproveServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(JobPostingApproveServlet.class);
    private JobPostingService jobPostingService;

    @Override
    public void init() throws ServletException {
        // reuse service registered in ContextListener
        this.jobPostingService = (JobPostingService) getServletContext().getAttribute("jobPostingService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userRole = (String) request.getSession().getAttribute("userRole");
        Long approverId = SecurityUtil.getLoggedInUserId(request.getSession());

        if (!"HRM".equals(userRole) || approverId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Must be logged in as HRM.");
            return;
        }

        String csrfToken = request.getParameter("csrfToken");
        if (!SecurityUtil.verifyCsrfToken(request.getSession(), csrfToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        String idStr = request.getParameter("id");
        Long jobId = null;
        try {
            jobId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Invalid job posting ID.");
            return;
        }

        String errorMessage = null;
        try {
            jobPostingService.approve(jobId, approverId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            errorMessage = e.getMessage();
        } catch (Exception e) {
            errorMessage = "An error occurred while approving the job posting: " + e.getMessage();
        }

        if (errorMessage == null) {
            response.sendRedirect(request.getContextPath() + "/job-postings?success=Job posting approved successfully.");
        } else {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        }
    }
}