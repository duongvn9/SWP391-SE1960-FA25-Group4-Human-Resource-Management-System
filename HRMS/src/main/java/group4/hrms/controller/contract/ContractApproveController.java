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
 * Controller for HRM to approve employment contracts
 */
@WebServlet("/contracts/approve")
public class ContractApproveController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ContractApproveController.class);
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
        
        // 3. Get and validate contract ID
        String idStr = request.getParameter("id");
        Long contractId = null;
        try {
            contractId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/contracts?error=Invalid contract ID");
            return;
        }
        
        // 4. Approve contract
        String errorMessage = null;
        try {
            // Verify contract exists and is in PENDING status
            Optional<EmploymentContract> contractOpt = contractDao.findById(contractId);
            if (!contractOpt.isPresent()) {
                errorMessage = "Contract not found";
            } else {
                EmploymentContract contract = contractOpt.get();
                if (!contract.canBeApproved()) {
                    errorMessage = "Contract cannot be approved. Current status: " + contract.getApprovalStatus();
                } else {
                    // Approve the contract with user_id
                    boolean success = contractDao.approve(contractId, currentUser.getId());
                    if (!success) {
                        errorMessage = "Failed to approve contract";
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error approving contract", e);
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
                "success=" + java.net.URLEncoder.encode("Contract approved successfully", "UTF-8"));
        } else {
            response.sendRedirect(redirectUrl + 
                (redirectUrl.contains("?") ? "&" : "?") + 
                "error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        }
    }
}
