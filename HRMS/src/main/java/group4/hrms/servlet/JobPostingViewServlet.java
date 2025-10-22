package group4.hrms.servlet;

import group4.hrms.model.JobPosting;
import group4.hrms.service.JobPostingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebServlet({"/job-posting/view", "/job-postings/view"})
public class JobPostingViewServlet extends HttpServlet {
    private JobPostingService jobPostingService;

    @Override
    public void init() throws ServletException {
        super.init();
        // obtain service initialized in ContextListener
        this.jobPostingService = (JobPostingService) getServletContext().getAttribute("jobPostingService");
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
            // forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/job-postings/view.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Invalid job posting ID");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Error viewing job posting");
        }
    }
}