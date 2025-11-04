package group4.hrms.controller;

import group4.hrms.dao.ApplicationDao;
import group4.hrms.dao.JobPostingDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.model.Application;
import group4.hrms.model.JobPosting;
import group4.hrms.model.Department;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for handling application viewing and approval for HR/HRM
 * 
 * @author Group4
 */
@WebServlet("/applications")
public class ApplicationManagementServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationManagementServlet.class);
    
    private ApplicationDao applicationDao;
    private JobPostingDao jobPostingDao;
    private DepartmentDao departmentDao;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.applicationDao = new ApplicationDao();
        this.jobPostingDao = new JobPostingDao();
        this.departmentDao = new DepartmentDao();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        

        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        // Check permission to access applications
        if (currentUser == null || !ApplicationPermissionHelper.canViewApplications(currentUser.getPositionId())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Get parameters
            int page = getIntParameter(request, "page", 1);
            int size = getIntParameter(request, "size", 10);
            String statusFilter = request.getParameter("status");
            String searchTerm = request.getParameter("search");
            
            // Get applications list with pagination
            List<Application> applications = applicationDao.findAllWithPagination(page, size, statusFilter, searchTerm);
            
            // Format dates for JSP display
            Map<Long, String> formattedDates = new HashMap<>();
            for (Application app : applications) {
                if (app.getCreatedAt() != null) {
                    formattedDates.put(app.getId(), app.getCreatedAt().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    ));
                }
            }
            long totalApplications = applicationDao.countWithFilter(statusFilter, searchTerm);
            int totalPages = (int) Math.ceil((double) totalApplications / size);
            
            // Get job posting information for each application
            Map<Long, JobPosting> jobMap = new HashMap<>();
            Map<Long, Department> departmentMap = new HashMap<>();
            
            for (Application app : applications) {
                if (!jobMap.containsKey(app.getJobId())) {
                    JobPosting job = jobPostingDao.findById(app.getJobId()).orElse(null);
                    if (job != null) {
                        jobMap.put(app.getJobId(), job);
                        
                        // Get department information
                        if (job.getDepartmentId() != null && !departmentMap.containsKey(job.getDepartmentId())) {
                            Department dept = departmentDao.findById(job.getDepartmentId()).orElse(null);
                            if (dept != null) {
                                departmentMap.put(job.getDepartmentId(), dept);
                            }
                        }
                    }
                }
            }
            
            // Set attributes
            request.setAttribute("applications", applications);
            request.setAttribute("jobMap", jobMap);
            request.setAttribute("departmentMap", departmentMap);
            request.setAttribute("formattedDates", formattedDates);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalApplications", totalApplications);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("currentUser", currentUser);
            
            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/applications/application-list.jsp")
                   .forward(request, response);
                   
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while loading applications list: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/applications/application-list.jsp")
                   .forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        // Check access permission
        if (currentUser == null || !ApplicationPermissionHelper.canManageApplications(currentUser.getPositionId())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("approve".equals(action)) {
            handleApproval(request, response, currentUser);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
    
    private void handleApproval(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        
        try {
            Long applicationId = Long.parseLong(request.getParameter("applicationId"));
            String status = request.getParameter("status"); // approved, rejected, reviewing
            String note = request.getParameter("note");
            
            // Get application information
            Application application = applicationDao.findById(applicationId).orElse(null);
            if (application == null) {
                request.setAttribute("error", "Application not found");
                doGet(request, response);
                return;
            }
            
            // Determine current user's role
            String approverRole = ApplicationPermissionHelper.getApprovalRole(currentUser.getPositionId());
            
            if (approverRole == null) {
                request.setAttribute("error", "You do not have permission to approve applications");
                doGet(request, response);
                return;
            }
            
            // Approval logic:
            // - HR approves first (status = reviewing)
            // - HRM makes final decision (status = approved/rejected)
            
            // Check permission to approve/reject this application
            boolean canApprove = ApplicationPermissionHelper.canApproveApplication(currentUser.getPositionId(), application.getStatus());
            boolean canReject = ApplicationPermissionHelper.canRejectApplication(currentUser.getPositionId(), application.getStatus());
            
            if ("approved".equals(status) && !canApprove) {
                request.setAttribute("error", "You do not have permission to approve this application in its current status");
                doGet(request, response);
                return;
            }
            
            if ("rejected".equals(status) && !canReject) {
                request.setAttribute("error", "You do not have permission to reject this application in its current status");
                doGet(request, response);
                return;
            }
            
            String newApplicationStatus = application.getStatus();
            
            if ("HR".equals(approverRole)) {
                // HR can only move from "new" to "reviewing"
                if ("new".equals(application.getStatus()) && "approved".equals(status)) {
                    newApplicationStatus = "reviewing";
                } else if ("rejected".equals(status)) {
                    newApplicationStatus = "rejected";
                }
            } else if ("HRM".equals(approverRole)) {
                // HRM can make final approval
                if ("reviewing".equals(application.getStatus()) || "new".equals(application.getStatus())) {
                    newApplicationStatus = status; // approved or rejected
                }
            }
            
            // Update status and approval in a transaction
            boolean updated = applicationDao.updateStatusWithApproval(
                applicationId, 
                newApplicationStatus, 
                note,
                currentUser.getId(),
                currentUser.getFullName(),
                approverRole,
                status
            );
            
            if (updated) {
                String successMessage = "HR".equals(approverRole) ? 
                    "Application moved to review status" : 
                    "Application approved successfully";
                    
                if ("rejected".equals(status)) {
                    successMessage = "Application rejected";
                }
                
                request.setAttribute("success", successMessage);
            } else {
                request.setAttribute("error", "Unable to update application status");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid application ID");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during update: " + e.getMessage());
        }
        
        // Redirect to applications list
        response.sendRedirect(request.getContextPath() + "/applications");
    }
    
    private int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String paramValue = request.getParameter(paramName);
        if (paramValue != null && !paramValue.trim().isEmpty()) {
            try {
                return Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                // Return default value if parsing fails
            }
        }
        return defaultValue;
    }
}