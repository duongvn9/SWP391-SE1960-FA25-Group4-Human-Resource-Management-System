package group4.hrms.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.model.User;
import group4.hrms.service.JobPostingService;
import group4.hrms.util.JobPostingPermissionHelper;
import group4.hrms.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/job-posting/unpublish")
public class JobPostingUnpublishServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(JobPostingUnpublishServlet.class);
    private JobPostingService jobPostingService;

    @Override
    public void init() throws ServletException {
        jobPostingService = (JobPostingService) getServletContext().getAttribute("jobPostingService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Validate Role (by position) and Get Unpublisher ID
        Long unpublisherId = SecurityUtil.getLoggedInUserId(request.getSession());
        User logged = (User) request.getSession().getAttribute("user");
        Long positionId = logged != null ? logged.getPositionId() : null;

        logger.info("Unpublish request - unpublisherId: {}, positionId: {}", unpublisherId, positionId);

        // Only HR Manager (position_id = 7) can unpublish
        if (unpublisherId == null || !JobPostingPermissionHelper.canApproveJobPosting(positionId)) {
            response.sendRedirect(request.getContextPath() + "/login?error=You don't have permission to unpublish job postings");
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
            logger.info("Calling service.unpublish(jobId={}, unpublisherId={})", jobId, unpublisherId);
            jobPostingService.unpublish(jobId, unpublisherId);
            logger.info("Unpublish successful!");
            
        } catch (IllegalArgumentException e) {
            // Job posting not found
            errorMessage = e.getMessage();
        } catch (IllegalStateException e) {
            // Invalid state transition (e.g. not published)
            errorMessage = e.getMessage();
        } catch (Exception e) {
            // Database or other errors
            logger.error("Error unpublishing job posting: {}", e.getMessage(), e);
            errorMessage = "An error occurred while unpublishing the job posting: " + e.getMessage();
        }

        // 5. Redirect with Result
        if (errorMessage == null) {
            response.sendRedirect(request.getContextPath() + 
                "/job-postings?success=Job posting unpublished successfully. Status changed back to Approved.");
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/job-postings?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        }
    }
}
