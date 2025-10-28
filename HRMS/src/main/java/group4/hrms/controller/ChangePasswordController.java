package group4.hrms.controller;

import group4.hrms.service.ChangePasswordService;
import group4.hrms.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordController.class);
    private final ChangePasswordService changePasswordService;
    
    public ChangePasswordController() {
        this.changePasswordService = new ChangePasswordService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        logger.debug("GET /change-password - Displaying change password form");
        
        // Check authentication
        if (!SessionUtil.isUserLoggedIn(req)) {
            logger.warn("User not logged in, redirecting to login page");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // Forward to change password page
        req.getRequestDispatcher("/WEB-INF/views/account/change-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        logger.debug("POST /change-password - Changing password");
        
        // Check authentication
        if (!SessionUtil.isUserLoggedIn(req)) {
            logger.warn("User not logged in, redirecting to login page");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            // Get account ID from session
            Long accountId = SessionUtil.getCurrentAccountId(req);
            if (accountId == null) {
                logger.error("Account ID not found in session");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            // Get form parameters
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");
            
            // Validate all password change requirements using service
            String validationError = changePasswordService.validatePasswordChange(
                currentPassword, newPassword, confirmPassword, accountId
            );
            
            if (validationError != null) {
                logger.warn("Password validation failed: {}", validationError);
                req.setAttribute("error", validationError);
                req.getRequestDispatcher("/WEB-INF/views/account/change-password.jsp").forward(req, resp);
                return;
            }
            
            // Change password using service
            boolean success = changePasswordService.changePassword(accountId, newPassword);
            
            if (success) {
                resp.sendRedirect(req.getContextPath() + "/change-password?success=Password changed successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/change-password?error=Failed to change password");
            }
            
        } catch (Exception e) {
            logger.error("Error changing password", e);
            resp.sendRedirect(req.getContextPath() + "/change-password?error=An error occurred while changing password");
        }
    }
}
