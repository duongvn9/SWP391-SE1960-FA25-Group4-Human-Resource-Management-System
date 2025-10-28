package group4.hrms.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.dao.AccountDao;
import group4.hrms.model.Account;
import group4.hrms.model.JobPosting;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.JobPostingService;
import group4.hrms.service.PositionService;
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

    @Override
    public void init() throws ServletException {
        super.init();
        // obtain service initialized in ContextListener
        this.jobPostingService = (JobPostingService) getServletContext().getAttribute("jobPostingService");
        Object ds = getServletContext().getAttribute("departmentService");
        if (ds instanceof DepartmentService) this.departmentService = (DepartmentService) ds;
        Object ps = getServletContext().getAttribute("positionService");
        if (ps instanceof PositionService) this.positionService = (PositionService) ps;
        
        // Initialize AccountDao directly
        this.accountDao = new AccountDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            if (accountDao != null && jobPosting.getApprovedByAccountId() != null) {
                try {
                    Account approver = accountDao.findById(jobPosting.getApprovedByAccountId()).orElse(null);
                    if (approver != null) {
                        request.setAttribute("approverAccount", approver);
                        logger.info("✅ Loaded approver account: {}", approver.getUsername());
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

            // forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/job-postings/view.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Invalid job posting ID");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Error viewing job posting");
        }
    }
}