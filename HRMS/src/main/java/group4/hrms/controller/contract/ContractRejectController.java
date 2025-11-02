package group4.hrms.controller.contract;

import group4.hrms.dao.EmploymentContractDao;
import group4.hrms.model.EmploymentContract;
import group4.hrms.model.User;
import group4.hrms.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Controller for HRM to reject employment contracts
 */
@WebServlet("/contracts/reject")
public class ContractRejectController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ContractRejectController.class);
    private EmploymentContractDao contractDao;
    
    @Override
    public void init() throws ServletException {
        contractDao = new EmploymentContractDao();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Check permission (HRM only - position_id = 7)
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Long positionId = currentUser.getPositionId();
        if (positionId == null || positionId != 7L) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Must be logged in as HRM.");
            return;
        }
        
        // 2. Validate CSRF Token
        String csrfToken = request.getParameter("csrfToken");
        if (!SecurityUtil.verifyCsrfToken(request.getSession(), csrfToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }
        
        // 3. Get and validate parameters
        String idStr = request.getParameter("id");
        String reason = request.getParameter("reason");
        
        Long contractId = null;
        try {
            contractId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/contracts?error=Invalid contract ID");
            return;
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + 
                "/contracts?error=" + java.net.URLEncoder.encode("Rejection reason is required", "UTF-8"));
            return;
        }
        
        // 4. Reject contract
        String errorMessage = null;
        try {
            // Verify contract exists and is in PENDING status
            Optional<EmploymentContract> contractOpt = contractDao.findById(contractId);
            if (!contractOpt.isPresent()) {
                errorMessage = "Contract not found";
            } else {
                EmploymentContract contract = contractOpt.get();
                if (!contract.canBeRejected()) {
                    errorMessage = "Contract cannot be rejected. Current status: " + contract.getStatus();
                } else {
                    // Reject the contract
                    boolean success = contractDao.reject(contractId, currentUser.getId(), reason.trim());
                    if (!success) {
                        errorMessage = "Failed to reject contract";
                    } else {
                        logger.info("Contract {} rejected by HRM {} with reason: {}", 
                            contractId, currentUser.getId(), reason.trim());
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error rejecting contract", e);
            errorMessage = "Database error: " + e.getMessage();
        }
        
        // 5. Redirect with result
        // Check if there are still pending contracts
        String redirectUrl;
        try {
            long pendingCount = contractDao.countByApprovalStatus("pending");
            if (pendingCount > 0) {
                // Still have pending contracts, stay on pending filter
                redirectUrl = request.getContextPath() + "/contracts?approvalStatus=pending";
            } else {
                // No more pending contracts, go to all
                redirectUrl = request.getContextPath() + "/contracts";
            }
        } catch (SQLException e) {
            logger.error("Error checking pending contracts count", e);
            redirectUrl = request.getContextPath() + "/contracts";
        }
        
        if (errorMessage == null) {
            response.sendRedirect(redirectUrl + 
                (redirectUrl.contains("?") ? "&" : "?") + 
                "success=" + java.net.URLEncoder.encode("Contract rejected successfully", "UTF-8"));
        } else {
            response.sendRedirect(redirectUrl + 
                (redirectUrl.contains("?") ? "&" : "?") + 
                "error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        }
    }
}
