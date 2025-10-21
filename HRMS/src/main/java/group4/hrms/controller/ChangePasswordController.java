package group4.hrms.controller;

import group4.hrms.dao.AccountDao;
import group4.hrms.model.Account;
import group4.hrms.util.PasswordUtil;
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
    private final AccountDao accountDao;
    
    public ChangePasswordController() {
        this.accountDao = new AccountDao();
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
            
            // Validate input - check empty fields
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                logger.warn("Current password is empty");
                resp.sendRedirect(req.getContextPath() + "/change-password?error=Current password is required");
                return;
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                logger.warn("New password is empty");
                resp.sendRedirect(req.getContextPath() + "/change-password?error=New password is required");
                return;
            }
            
            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                logger.warn("Confirm password is empty");
                resp.sendRedirect(req.getContextPath() + "/change-password?error=Confirm password is required");
                return;
            }
            
            // Check if new password matches confirm password
            if (!newPassword.equals(confirmPassword)) {
                logger.warn("New password and confirm password do not match");
                resp.sendRedirect(req.getContextPath() + "/change-password?error=New password and confirm password do not match");
                return;
            }
            
            // Get password hash from database
            String currentPasswordHash = accountDao.getPasswordHash(accountId);
            if (currentPasswordHash == null) {
                logger.error("Password hash not found for account_id: {}", accountId);
                resp.sendRedirect(req.getContextPath() + "/change-password?error=Account not found");
                return;
            }
            
            // Verify current password
            if (!PasswordUtil.verifyPassword(currentPassword, currentPasswordHash)) {
                logger.warn("Current password is incorrect for account_id: {}", accountId);
                resp.sendRedirect(req.getContextPath() + "/change-password?error=Current password is incorrect");
                return;
            }
            
            // Hash new password using bcrypt
            String newPasswordHash = PasswordUtil.hashPassword(newPassword);
            
            // Update password in database
            boolean success = accountDao.updatePassword(accountId, newPasswordHash);
            
            if (success) {
                logger.info("Password changed successfully for account_id: {}", accountId);
                resp.sendRedirect(req.getContextPath() + "/change-password?success=Password changed successfully");
            } else {
                logger.error("Failed to change password for account_id: {}", accountId);
                resp.sendRedirect(req.getContextPath() + "/change-password?error=Failed to change password");
            }
            
        } catch (Exception e) {
            logger.error("Error changing password", e);
            resp.sendRedirect(req.getContextPath() + "/change-password?error=An error occurred while changing password");
        }
    }
}
