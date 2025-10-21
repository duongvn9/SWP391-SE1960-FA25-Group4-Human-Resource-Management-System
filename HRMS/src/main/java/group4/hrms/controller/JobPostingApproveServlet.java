package group4.hrms.controller;

import group4.hrms.service.JobPostingService;
import group4.hrms.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/job-posting/approve")
public class JobPostingApproveServlet extends HttpServlet {
    private JobPostingService jobPostingService;

    @Override
    public void init() throws ServletException {
        jobPostingService = (JobPostingService) getServletContext().getAttribute("jobPostingService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Validate Role and Get Approver ID
        String userRole = (String) request.getSession().getAttribute("userRole");
        Long approverId = SecurityUtil.getLoggedInUserId(request.getSession());
        
        if (!"HRM".equals(userRole) || approverId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Must be logged in as HRM.");
            return;
        }

        // 2. Validate CSRF Token
        String csrfToken = request.getParameter("csrfToken");
        if (!SecurityUtil.verifyCsrfToken(request.getSession(), csrfToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        // 3. Get and Validate Job ID
        String idStr = request.getParameter("id");
        Long jobId = null;
        try {
            jobId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Invalid job posting ID.");
            return;
        }

        // 4. Call Service with Exception Handling
        String errorMessage = null;
        try {
            jobPostingService.approve(jobId, approverId);
            
        } catch (IllegalArgumentException e) {
            // Job posting not found
            errorMessage = e.getMessage();
        } catch (IllegalStateException e) {
            // Invalid state transition
            errorMessage = e.getMessage();
        } catch (Exception e) {
            // Database or other errors
            errorMessage = "An error occurred while approving the job posting: " + e.getMessage();
        }

        // 5. Redirect with Result
        if (errorMessage == null) {
            response.sendRedirect(request.getContextPath() + 
                "/job-postings?success=Job posting approved successfully.");
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/job-postings?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        }
    }
}
