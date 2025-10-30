package group4.hrms.controller;

import java.io.IOException;
import java.util.Optional;

import group4.hrms.model.Department;
import group4.hrms.model.JobPosting;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.JobPostingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/job-view-publish")
public class JobViewPublishServlet extends HttpServlet {
    private JobPostingService jobPostingService;
    private DepartmentService departmentService;

    @Override
    public void init() throws ServletException {
        Object js = getServletContext().getAttribute("jobPostingService");
        if (js instanceof JobPostingService) jobPostingService = (JobPostingService) js;
        Object ds = getServletContext().getAttribute("departmentService");
        if (ds instanceof DepartmentService) departmentService = (DepartmentService) ds;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/jobs?error=Missing job id");
            return;
        }
        try {
            long id = Long.parseLong(idStr);
            Optional<JobPosting> jobOpt = (jobPostingService != null) ? jobPostingService.findById(id) : Optional.empty();
            if (jobOpt.isEmpty() || jobOpt.get() == null || !"PUBLISHED".equalsIgnoreCase(jobOpt.get().getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/jobs?error=Not found or not published");
                return;
            }
            JobPosting job = jobOpt.get();
            Department department = null;
            if (departmentService != null && job.getDepartmentId() != null)
                department = departmentService.getDepartmentById(job.getDepartmentId());

            req.setAttribute("job", job);
            req.setAttribute("department", department);
            req.getRequestDispatcher("/WEB-INF/views/job-view-publish.jsp").forward(req, resp);
            return;
        } catch (Exception ex) {
            resp.sendRedirect(req.getContextPath() + "/jobs?error=Not found");
        }
    }
}
