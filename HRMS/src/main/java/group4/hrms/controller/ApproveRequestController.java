package group4.hrms.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dao.LeaveTypeDao;
import group4.hrms.model.Account;
import group4.hrms.model.User;
import group4.hrms.service.LeaveRequestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for approving leave requests.
 * Handles leave request approval and balance deduction.
 *
 * Supported URLs:
 * - POST /requests/approve - Approve a leave request
 *
 * @author HRMS Development Team
 * @version 1.0
 */
@WebServlet("/requests/approve")
public class ApproveRequestController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ApproveRequestController.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("ApproveRequestController.doPost() called");

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
            String requestIdStr = request.getParameter("requestId");
            if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
                request.setAttribute("error", "Request ID is required");
                response.sendRedirect(request.getContextPath() + "/requests/list");
                return;
            }

            Long requestId = Long.parseLong(requestIdStr);

            // Initialize service
            LeaveRequestService service = new LeaveRequestService(
                new RequestDao(),
                new RequestTypeDao(),
                new LeaveTypeDao()
            );

            // Process leave approval (handles balance deduction)
            service.processLeaveApproval(requestId);

            logger.info(String.format("Leave request approved successfully: requestId=%d, approvedBy=%d",
                       requestId, account.getId()));

            // Set success message
            request.setAttribute("success", "Leave request approved successfully!");

            // Redirect to request list or detail page
            response.sendRedirect(request.getContextPath() + "/requests/list?success=approved");

        } catch (NumberFormatException e) {
            logger.warning("Invalid request ID format: " + e.getMessage());
            request.setAttribute("error", "Invalid request ID");
            response.sendRedirect(request.getContextPath() + "/requests/list?error=invalid_id");

        } catch (IllegalArgumentException e) {
            logger.warning(String.format("Validation error approving request: error=%s", e.getMessage()));
            request.setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/requests/list?error=" +
                                java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));

        } catch (SQLException e) {
            logger.severe(String.format("Database error approving request: error=%s", e.getMessage()));
            e.printStackTrace();
            request.setAttribute("error", "Database error occurred. Please try again later.");
            response.sendRedirect(request.getContextPath() + "/requests/list?error=database");

        } catch (Exception e) {
            logger.severe(String.format("Unexpected error approving request: error=%s", e.getMessage()));
            e.printStackTrace();
            request.setAttribute("error", "System error. Please try again later.");
            response.sendRedirect(request.getContextPath() + "/requests/list?error=system");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to POST (or show error)
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                          "GET method not supported. Use POST to approve requests.");
    }
}
