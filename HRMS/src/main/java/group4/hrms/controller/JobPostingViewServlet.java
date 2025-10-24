package group4.hrms.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public void init() throws ServletException {
        super.init();
        // obtain service initialized in ContextListener
        this.jobPostingService = (JobPostingService) getServletContext().getAttribute("jobPostingService");
        Object ds = getServletContext().getAttribute("departmentService");
        if (ds instanceof DepartmentService) this.departmentService = (DepartmentService) ds;
        Object ps = getServletContext().getAttribute("positionService");
        if (ps instanceof PositionService) this.positionService = (PositionService) ps;
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

            // Add debug attributes to help trace missing fields in the JSP
            request.setAttribute("debug_priority", jobPosting.getPriority());
            request.setAttribute("debug_workingHours", jobPosting.getWorkingHours());

            // forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/job-postings/view.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Invalid job posting ID");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Error viewing job posting");
        }
    }
}