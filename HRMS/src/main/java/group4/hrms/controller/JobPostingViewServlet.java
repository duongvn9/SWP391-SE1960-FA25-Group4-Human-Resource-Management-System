package group4.hrms.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.dao.AccountDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Account;
import group4.hrms.model.JobPosting;
import group4.hrms.model.User;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.JobPostingService;
import group4.hrms.service.PositionService;
import group4.hrms.util.JobPostingPermissionHelper;
import group4.hrms.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet({"/job-posting/view", "/job-postings/view"})
public class JobPostingViewServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(JobPostingViewServlet.class);
    private JobPostingService jobPostingService;
    private DepartmentService departmentService;
    private PositionService positionService;
    private AccountDao accountDao;
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        super.init();
        // obtain service initialized in ContextListener
        this.jobPostingService = (JobPostingService) getServletContext().getAttribute("jobPostingService");
        Object ds = getServletContext().getAttribute("departmentService");
        if (ds instanceof DepartmentService) this.departmentService = (DepartmentService) ds;
        Object ps = getServletContext().getAttribute("positionService");
        if (ps instanceof PositionService) this.positionService = (PositionService) ps;
        
        // Initialize AccountDao and UserDao directly
        this.accountDao = new AccountDao();
        this.userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check authentication and authorization
        Object userObj = request.getSession(false) != null ? request.getSession(false).getAttribute("user") : null;
        Long positionId = null;
        
        if (userObj instanceof User) {
            User user = (User) userObj;
            positionId = user.getPositionId();
            logger.info("User position ID: {}", positionId);
        } else {
            logger.warn("No valid user found in session - redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Check if user has permission to view job postings
        if (!JobPostingPermissionHelper.canViewJobPosting(positionId)) {
            logger.error("Access denied - User position ID {} cannot view job postings", positionId);
            response.sendRedirect(request.getContextPath() + "/login?error=You don't have permission to view job postings");
            return;
        }
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/job-postings");
                return;
            }

            long id = Long.parseLong(idParam);
            JobPosting jobPosting = jobPostingService.findById(id).orElse(null);

            if (jobPosting == null) {
                response.sendRedirect(request.getContextPath() + "/job-postings?error=Job posting not found");
                return;
            }

            request.setAttribute("jobPosting", jobPosting);
            
            // Debug: Log rejection info
            logger.info("📋 Job Posting Debug Info:");
            logger.info("  - ID: {}", jobPosting.getId());
            logger.info("  - Status: {}", jobPosting.getStatus());
            logger.info("  - Rejected Reason: {}", jobPosting.getRejectedReason());
            logger.info("  - Approved By Account ID: {}", jobPosting.getApprovedByAccountId());
            logger.info("  - Approved At: {}", jobPosting.getApprovedAt());

            // Add reference data used by the view (department/position lookup)
            if (departmentService != null) {
                try {
                    request.setAttribute("departments", departmentService.getAllDepartments());
                } catch (Exception ex) {
                    logger.warn("Failed to load departments for view: {}", ex.getMessage());
                }
            }
            if (positionService != null) {
                try {
                    request.setAttribute("positions", positionService.getAllPositions());
                } catch (Exception ex) {
                    logger.warn("Failed to load positions for view: {}", ex.getMessage());
                }
            }
            
            // Load approver/rejector information
            if (accountDao != null && userDao != null && jobPosting.getApprovedByAccountId() != null) {
                try {
                    Account approver = accountDao.findById(jobPosting.getApprovedByAccountId()).orElse(null);
                    if (approver != null) {
                        request.setAttribute("approverAccount", approver);
                        logger.info("✅ Loaded approver account: {}", approver.getUsername());
                        
                        // Load user information to get full name
                        if (approver.getUserId() != null) {
                            User approverUser = userDao.findById(approver.getUserId()).orElse(null);
                            if (approverUser != null) {
                                request.setAttribute("approverUser", approverUser);
                                logger.info("✅ Loaded approver user: {}", approverUser.getFullName());
                            } else {
                                logger.warn("⚠️ Approver user not found for ID: {}", approver.getUserId());
                            }
                        }
                    } else {
                        logger.warn("⚠️ Approver account not found for ID: {}", jobPosting.getApprovedByAccountId());
                    }
                } catch (Exception ex) {
                    logger.error("❌ Failed to load approver account: {}", ex.getMessage(), ex);
                }
            }
            
            // Format rejection date for display
            if (jobPosting.getApprovedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String formattedDate = jobPosting.getApprovedAt().format(formatter);
                request.setAttribute("rejectionDateFormatted", formattedDate);
                logger.info("✅ Formatted rejection date: {}", formattedDate);
            }

            // Set CSRF token for forms
            request.setAttribute("csrfToken", SecurityUtil.getCsrfToken(request.getSession()));

            // forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/job-postings/view.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Invalid job posting ID");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Error viewing job posting");
        }
    }
}