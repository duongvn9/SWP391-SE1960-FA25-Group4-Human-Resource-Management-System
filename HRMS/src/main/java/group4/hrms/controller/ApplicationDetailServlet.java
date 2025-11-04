package group4.hrms.controller;

import group4.hrms.dao.ApplicationDao;
import group4.hrms.dao.JobPostingDao;
import group4.hrms.model.Application;
import group4.hrms.model.JobPosting;
import group4.hrms.model.User;
import group4.hrms.util.SecurityUtil;
import group4.hrms.util.ApplicationPermissionHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for handling application detail page
 * 
 * @author Group4
 */
@WebServlet("/applications/detail")
public class ApplicationDetailServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationDetailServlet.class);
    
    private ApplicationDao applicationDao;
    private JobPostingDao jobPostingDao;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.applicationDao = new ApplicationDao();
        this.jobPostingDao = new JobPostingDao();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        // Check permission to access application details
        if (currentUser == null || !ApplicationPermissionHelper.canViewApplicationDetails(currentUser.getPositionId())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String applicationIdStr = request.getParameter("id");
        
        if (applicationIdStr == null || applicationIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing application ID");
            return;
        }
        
        try {
            Long applicationId = Long.parseLong(applicationIdStr);
            
            // Get application information
            Application application = applicationDao.findById(applicationId).orElse(null);
            if (application == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Application not found");
                return;
            }
            
            // Get job posting information
            JobPosting jobPosting = null;
            if (application.getJobId() != null) {
                jobPosting = jobPostingDao.findById(application.getJobId()).orElse(null);
            }
            
            // Set attributes
            request.setAttribute("application", application);
            request.setAttribute("jobPosting", jobPosting);
            request.setAttribute("currentUser", currentUser);
            
            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/applications/application-detail.jsp")
                   .forward(request, response);
                   
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid application ID");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while loading application information: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/applications/application-detail.jsp")
                   .forward(request, response);
        }
    }
}