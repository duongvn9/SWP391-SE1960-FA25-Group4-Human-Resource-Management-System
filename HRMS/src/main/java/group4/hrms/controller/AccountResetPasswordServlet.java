package group4.hrms.controller;

import group4.hrms.dao.AuthIdentityDao;
import group4.hrms.dao.AuthLocalCredentialsDao;
import group4.hrms.dao.AccountDao;
import group4.hrms.model.AuthIdentity;
import group4.hrms.util.PasswordUtil;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Servlet to handle account password reset
 * URL: /employees/accounts/reset-password
 */
@WebServlet("/employees/accounts/reset-password")
public class AccountResetPasswordServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AccountResetPasswordServlet.class);
    private final AccountDao accountDao = new AccountDao();
    private final AuthIdentityDao authIdentityDao = new AuthIdentityDao();
    private final AuthLocalCredentialsDao authLocalCredentialsDao = new AuthLocalCredentialsDao();

    // Password validation patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*(),.?\":{}|<>].*");

    /**
     * Validate password strength according to requirements
     * 
     * @param password The password to validate
     * @return List of specific error messages, empty if password is valid
     */
    private List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.length() < 6) {
            errors.add("Password must be at least 6 characters long");
        }

        if (password != null && !UPPERCASE_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (password != null && !SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one special character");
        }

        return errors;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                out.write("{\"success\":false,\"message\":\"User not logged in\"}");
                return;
            }

            // Check authorization - Only ADMIN can reset passwords
            if (!group4.hrms.util.PermissionUtil.canResetPassword(request)) {
                out.write("{\"success\":false,\"message\":\"You don't have permission to reset passwords\"}");
                return;
            }

            // Get form parameters
            String accountIdStr = request.getParameter("accountId");
            String newPassword = request.getParameter("newPassword");

            // Validate required fields
            if (accountIdStr == null || accountIdStr.trim().isEmpty()) {
                out.write("{\"success\":false,\"message\":\"Account ID is required\"}");
                return;
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                out.write("{\"success\":false,\"message\":\"New password is required\"}");
                return;
            }

            // Validate password strength
            List<String> passwordErrors = validatePassword(newPassword);
            if (!passwordErrors.isEmpty()) {
                // Combine all password validation errors into a single message
                String errorMessage = "Password validation failed: " + String.join("; ", passwordErrors);
                out.write("{\"success\":false,\"message\":\"" + errorMessage.replace("\"", "\\\"") + "\"}");
                return;
            }

            // Parse account ID
            Long accountId;
            try {
                accountId = Long.parseLong(accountIdStr);
            } catch (NumberFormatException e) {
                out.write("{\"success\":false,\"message\":\"Invalid account ID format\"}");
                return;
            }

            // Verify account exists
            if (accountDao.findById(accountId).isEmpty()) {
                out.write("{\"success\":false,\"message\":\"Account not found\"}");
                return;
            }

            // Find local auth identity for this account
            Optional<AuthIdentity> identityOpt = authIdentityDao.findByAccountAndProvider(accountId, "local");

            if (identityOpt.isEmpty()) {
                out.write("{\"success\":false,\"message\":\"No local authentication found for this account\"}");
                return;
            }

            AuthIdentity identity = identityOpt.get();
            Long identityId = identity.getId();

            // Hash the new password
            String passwordHash = PasswordUtil.hashPassword(newPassword);

            // Update password in auth_local_credentials
            boolean updated = authLocalCredentialsDao.updatePassword(identityId, passwordHash);

            if (updated) {
                out.write("{\"success\":true,\"message\":\"Password reset successfully\"}");
                logger.info("Password reset successfully for account ID {} by user: {}",
                        accountId, SessionUtil.getCurrentUsername(request));
            } else {
                out.write("{\"success\":false,\"message\":\"Failed to reset password\"}");
                logger.warn("Failed to reset password for account ID {}", accountId);
            }

        } catch (SQLException e) {
            logger.error("Database error resetting password", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "Database error";
            out.write("{\"success\":false,\"message\":\"Database error: " + errorMsg + "\"}");
        } catch (Exception e) {
            logger.error("Error resetting password", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "Unknown error";
            out.write("{\"success\":false,\"message\":\"An error occurred: " + errorMsg + "\"}");
        }
    }
}