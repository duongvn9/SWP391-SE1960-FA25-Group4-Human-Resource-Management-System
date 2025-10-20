package group4.hrms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Account;
import group4.hrms.model.Request;
import group4.hrms.model.User;
import group4.hrms.util.RequestListPermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for approving/rejecting requests
 * Can be used from request list page or request detail page
 */
@WebServlet("/requests/approve")
public class ApproveRequestController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ApproveRequestController.class.getName());
    private RequestDao requestDao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.requestDao = new RequestDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Get session and user
            HttpSession session = request.getSession(false);
            if (session == null) {
                out.print("{\"success\": false, \"message\": \"Session expired\"}");
                return;
            }

            User currentUser = (User) session.getAttribute("user");
            Account currentAccount = (Account) session.getAttribute("account");
            if (currentUser == null || currentAccount == null) {
                out.print("{\"success\": false, \"message\": \"User not logged in\"}");
                return;
            }

            // Get parameters
            String action = request.getParameter("action");
            String requestIdStr = request.getParameter("requestId");

            if (action == null || requestIdStr == null) {
                out.print("{\"success\": false, \"message\": \"Missing parameters\"}");
                return;
            }

            Long requestId;
            try {
                requestId = Long.parseLong(requestIdStr);
            } catch (NumberFormatException e) {
                out.print("{\"success\": false, \"message\": \"Invalid request ID\"}");
                return;
            }

            // Fetch the request
            Optional<Request> requestOpt = requestDao.findById(requestId);
            if (!requestOpt.isPresent()) {
                out.print("{\"success\": false, \"message\": \"Request not found\"}");
                return;
            }

            Request req = requestOpt.get();

            // Parse request detail based on request_type_id (needed for permission check)
            if (req.getDetailJson() != null && !req.getDetailJson().trim().isEmpty()) {
                try {
                    Long requestTypeId = req.getRequestTypeId();
                    if (requestTypeId != null) {
                        if (requestTypeId == 7L) {
                            req.getOtDetail(); // Parse OT detail for OVERTIME_REQUEST
                        } else if (requestTypeId == 6L) {
                            req.getLeaveDetail(); // Parse Leave detail for LEAVE_REQUEST
                        } else if (requestTypeId == 8L) {
                            req.getAppealDetail(); // Parse Appeal detail for ADJUSTMENT_REQUEST
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Error parsing request detail for request " + requestId + ": " + e.getMessage());
                }
            }

            // Get user's position for permission check
            group4.hrms.dao.PositionDao positionDao = new group4.hrms.dao.PositionDao();
            group4.hrms.model.Position position = null;
            if (currentUser.getPositionId() != null) {
                java.util.Optional<group4.hrms.model.Position> positionOpt = positionDao.findById(currentUser.getPositionId());
                if (positionOpt.isPresent()) {
                    position = positionOpt.get();
                }
            }

            // Check permission
            if (!RequestListPermissionHelper.canApproveRequest(currentUser, req, position, currentAccount.getId())) {
                out.print("{\"success\": false, \"message\": \"You do not have permission to approve/reject this request\"}");
                return;
            }

            // Perform action
            boolean success;
            String message;
            String reason = request.getParameter("reason");

            if ("approve".equals(action)) {
                // Only allow approving PENDING requests
                if (!"PENDING".equals(req.getStatus())) {
                    out.print("{\"success\": false, \"message\": \"Can only approve PENDING requests\"}");
                    return;
                }

                req.setStatus("APPROVED");
                req.setCurrentApproverAccountId(currentAccount.getId());
                req.setUpdatedAt(LocalDateTime.now());
                // Store approval reason if provided
                if (reason != null && !reason.trim().isEmpty()) {
                    req.setApproveReason(reason);
                } else {
                    req.setApproveReason(null);
                }
                Request updated = requestDao.update(req);
                success = (updated != null);
                message = success ? "Request approved successfully" : "Failed to approve request";

            } else if ("reject".equals(action)) {
                // Rejection reason is always required
                if (reason == null || reason.trim().isEmpty()) {
                    out.print("{\"success\": false, \"message\": \"Rejection reason is required\"}");
                    return;
                }

                // Can reject PENDING or APPROVED requests (for manager override)
                if (!"PENDING".equals(req.getStatus()) && !"APPROVED".equals(req.getStatus())) {
                    out.print("{\"success\": false, \"message\": \"Can only reject PENDING or APPROVED requests\"}");
                    return;
                }

                req.setStatus("REJECTED");
                req.setCurrentApproverAccountId(currentAccount.getId());
                req.setUpdatedAt(LocalDateTime.now());
                req.setApproveReason(reason);
                Request updated = requestDao.update(req);
                success = (updated != null);
                message = success ? "Request rejected successfully" : "Failed to reject request";

            } else {
                out.print("{\"success\": false, \"message\": \"Invalid action\"}");
                return;
            }

            // Return response
            if (success) {
                logger.info(String.format("User %d %s request %d", currentUser.getId(), action, requestId));
                out.print("{\"success\": true, \"message\": \"" + message + "\"}");
            } else {
                logger.warning(String.format("Failed to %s request %d by user %d", action, requestId, currentUser.getId()));
                out.print("{\"success\": false, \"message\": \"" + message + "\"}");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing approval", e);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
}
