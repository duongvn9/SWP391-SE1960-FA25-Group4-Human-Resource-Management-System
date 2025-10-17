package group4.hrms.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Account;
import group4.hrms.model.Request;
import group4.hrms.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for displaying request details.
 * Shows detailed information about a specific request.
 *
 * @author HRMS Development Team
 * @version 1.0
 */
@WebServlet("/requests/detail")
public class RequestDetailController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RequestDetailController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("RequestDetailController.doGet() called");

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Account account = (Account) session.getAttribute("account");
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Get request ID from parameter
            String requestIdStr = request.getParameter("id");
            if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
                request.setAttribute("error", "Request ID is required");
                request.getRequestDispatcher("/requests/list").forward(request, response);
                return;
            }

            Long requestId = Long.parseLong(requestIdStr);
            RequestDao requestDao = new RequestDao();

            // Find the request
            Optional<Request> requestOpt = requestDao.findById(requestId);

            if (!requestOpt.isPresent()) {
                request.setAttribute("error", "Request not found");
                request.getRequestDispatcher("/requests/list").forward(request, response);
                return;
            }

            Request req = requestOpt.get();

            // Verify the request belongs to the current user
            if (!req.getCreatedByUserId().equals(user.getId())) {
                request.setAttribute("error", "You don't have permission to view this request");
                request.getRequestDispatcher("/requests/list").forward(request, response);
                return;
            }

            // Set request as attribute
            request.setAttribute("request", req);

            // Forward to detail view
            request.getRequestDispatcher("/WEB-INF/views/requests/request-detail.jsp")
                   .forward(request, response);

        } catch (NumberFormatException e) {
            logger.warning("Invalid request ID format: " + e.getMessage());
            request.setAttribute("error", "Invalid request ID");
            request.getRequestDispatcher("/requests/list").forward(request, response);
        } catch (Exception e) {
            logger.severe("Error loading request detail: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading request details. Please try again later.");
            request.getRequestDispatcher("/requests/list").forward(request, response);
        }
    }
}
