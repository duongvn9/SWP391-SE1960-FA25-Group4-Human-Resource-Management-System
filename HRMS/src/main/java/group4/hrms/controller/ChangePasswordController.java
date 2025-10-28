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
            
<<<<<<< HEAD
            // Validate all password change requirements using service
            String validationError = changePasswordService.validatePasswordChange(
                currentPassword, newPassword, confirmPassword, accountId
            );
            
=======
            // Validate all password inputs
            String validationError = validatePasswordInputs(currentPassword, newPassword, confirmPassword);
>>>>>>> e77eabc81a0f624e022587923c8a8ff7e76b3d15
            if (validationError != null) {
                logger.warn("Password validation failed: {}", validationError);
                req.setAttribute("error", validationError);
                req.getRequestDispatcher("/WEB-INF/views/account/change-password.jsp").forward(req, resp);
                return;
            }
            
<<<<<<< HEAD
            // Change password using service
            boolean success = changePasswordService.changePassword(accountId, newPassword);
=======
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
            
            // Check if new password is same as current password
            if (PasswordUtil.verifyPassword(newPassword, currentPasswordHash)) {
                logger.warn("New password is same as current password for account_id: {}", accountId);
                req.setAttribute("error", "New password must be different from current password");
                req.getRequestDispatcher("/WEB-INF/views/account/change-password.jsp").forward(req, resp);
                return;
            }
            
            // Hash new password using bcrypt
            String newPasswordHash = PasswordUtil.hashPassword(newPassword);
            
            // Update password in database
            boolean success = accountDao.updatePassword(accountId, newPasswordHash);
>>>>>>> e77eabc81a0f624e022587923c8a8ff7e76b3d15
            
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
<<<<<<< HEAD
=======
    
  
    private String validatePasswordInputs(String currentPassword, String newPassword, String confirmPassword) {
        // Check current password
        if (currentPassword == null || currentPassword.isEmpty()) {
            return "Current password is required";
        }
        
        // Check new password
        if (newPassword == null || newPassword.isEmpty()) {
            return "New password is required";
        }
        
        // Check confirm password
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return "Confirm password is required";
        }
        
        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            return "New password and confirm password do not match";
        }
        
        // Validate password strength - length
        if (newPassword.length() <= 6) {
            return "Password must be longer than 6 characters";
        }
        
        // Validate password strength - uppercase letter
        if (!newPassword.matches(".*[A-Z].*")) {
            return "Password must contain at least 1 uppercase letter";
        }
        
        // Validate password strength - number
        if (!newPassword.matches(".*[0-9].*")) {
            return "Password must contain at least 1 number";
        }
        
        // Validate password strength - special character
        if (!newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return "Password must contain at least 1 special character";
        }
        
        return null; // All validations passed
    }
>>>>>>> e77eabc81a0f624e022587923c8a8ff7e76b3d15
}
