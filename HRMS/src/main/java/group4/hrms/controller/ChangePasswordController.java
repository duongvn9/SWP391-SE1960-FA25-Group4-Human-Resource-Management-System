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
                req.setAttribute("error", "Current password is required");
                req.getRequestDispatcher("/WEB-INF/views/account/change-password.jsp").forward(req, resp);
                return;
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                logger.warn("New password is empty");
                req.setAttribute("error", "New password is required");
                req.getRequestDispatcher("/WEB-INF/views/account/change-password.jsp").forward(req, resp);
                return;
            }
            
            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                logger.warn("Confirm password is empty");
                req.setAttribute("error", "Confirm password is required");
                req.getRequestDispatcher("/WEB-INF/views/account/change-password.jsp").forward(req, resp);
                return;
            }
            
            // Check if new password matches confirm password
            if (!newPassword.equals(confirmPassword)) {
                logger.warn("New password and confirm password do not match");
                req.setAttribute("error", "New password and confirm password do not match");
                req.getRequestDispatcher("/WEB-INF/views/account/change-password.jsp").forward(req, resp);
                return;
            }
            
            // Validate password strength
            String passwordError = validatePasswordStrength(newPassword);
            if (passwordError != null) {
                logger.warn("Password validation failed: {}", passwordError);
                req.setAttribute("error", passwordError);
                req.getRequestDispatcher("/WEB-INF/views/account/change-password.jsp").forward(req, resp);
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
    
    /**
     * Validate password strength
     * Password must:
     * - Be longer than 6 characters (minimum 7)
     * - Contain at least 1 uppercase letter
     * - Contain at least 1 number
     * - Contain at least 1 special character
     * 
     * @param password Password to validate
     * @return Error message if invalid, null if valid
     */
    private String validatePasswordStrength(String password) {
        if (password == null || password.length() <= 6) {
            return "Password must be longer than 6 characters";
        }
        
        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least 1 uppercase letter";
        }
        
        // Check for at least one number
        if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least 1 number";
        }
        
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return "Password must contain at least 1 special character";
        }
        
        return null; // Password is valid
    }
}
