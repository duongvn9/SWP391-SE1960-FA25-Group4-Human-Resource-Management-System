package group4.hrms.controller;

import group4.hrms.model.Request;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.model.RequestType;
import group4.hrms.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import group4.hrms.model.User;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/recruitment-requests/approved")
public class ApprovedRecruitmentRequestsServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ApprovedRecruitmentRequestsServlet.class);
    private RequestDao requestDao;
    private RequestTypeDao requestTypeDao;
    
        @Override
    public void init() throws ServletException {
        requestDao = new RequestDao();
        requestTypeDao = new RequestTypeDao();
    }
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is HR (8) or HRM (7)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getPositionId() != 7 && currentUser.getPositionId() != 8) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            // Resolve request type id for recruitment requests
            RequestType recruitmentType = requestTypeDao.findByCode("RECRUITMENT_REQUEST");
            if (recruitmentType == null) {
                logger.error("Request type RECRUITMENT_REQUEST not found");
                throw new ServletException("Request type RECRUITMENT_REQUEST not configured in the system");
            }

            // Fetch approved recruitment requests by type id
            List<Request> approvedRequests = requestDao.findByTypeAndStatus(recruitmentType.getId(), "APPROVED");

            // Ensure detail JSON is parsed for each request (lazy parsing inside model)
            for (Request r : approvedRequests) {
                try {
                    r.getRecruitmentDetail();
                } catch (Exception ex) {
                    // Log and continue — we don't want one bad JSON to break the page
                    logger.warn("Failed to parse recruitment detail JSON for request id={}", r.getId(), ex);
                }
            }

            request.setAttribute("requests", approvedRequests);
            request.setAttribute("csrfToken", SecurityUtil.generateCsrfToken(request.getSession()));

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/job-postings/approved-recruitment-requests.jsp")
                    .forward(request, response);

        } catch (ServletException | IOException e) {
            // Re-throw servlet and IO exceptions directly
            throw e;
        } catch (RuntimeException e) {
            // Database / mapping runtime errors
            logger.error("Runtime error fetching approved recruitment requests", e);
            throw new ServletException("Error fetching approved recruitment requests", e);
        }
    }
}