package group4.hrms.controller;

import group4.hrms.dao.AccountDao;
import group4.hrms.model.Account;
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
import java.util.Optional;

/**
 * Servlet to handle account status toggle (active/inactive)
 * URL: /employees/accounts/{id}/toggle-status
 * Method: POST
 * Returns: JSON response with success/error message
 */
@WebServlet("/employees/accounts/*/toggle-status")
public class AccountToggleStatusServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AccountToggleStatusServlet.class);

    private final AccountDao accountDao = new AccountDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("AccountToggleStatusServlet.doPost() called");

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                logger.warn("User not logged in");
                sendJsonResponse(response, false, "User not authenticated", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Check authorization - Only ADMIN can toggle account status
            if (!SessionUtil.isAdmin(request)) {
                logger.warn("User is not admin, cannot toggle account status");
                sendJsonResponse(response, false, "You don't have permission to perform this action",
                        HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Extract account ID from URL path
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.isEmpty()) {
                // Try to extract from request URI
                String requestURI = request.getRequestURI();
                String contextPath = request.getContextPath();
                String servletPath = requestURI.substring(contextPath.length());

                // Extract ID from pattern: /employees/accounts/{id}/toggle-status
                String[] pathParts = servletPath.split("/");
                if (pathParts.length >= 4) {
                    String accountIdStr = pathParts[3];
                    Long accountId = parseLongOrNull(accountIdStr);

                    if (accountId == null) {
                        logger.warn("Invalid account ID: {}", accountIdStr);
                        sendJsonResponse(response, false, "Invalid account ID", HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }

                    // Toggle account status
                    toggleAccountStatus(accountId, response);
                } else {
                    logger.warn("Invalid URL pattern");
                    sendJsonResponse(response, false, "Invalid URL pattern", HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                logger.warn("Invalid URL pattern");
                sendJsonResponse(response, false, "Invalid URL pattern", HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.error("Error toggling account status: {}", e.getMessage(), e);
            sendJsonResponse(response, false, "An error occurred while toggling account status",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Toggle account status between active and inactive
     */
    private void toggleAccountStatus(Long accountId, HttpServletResponse response) throws IOException {
        logger.info("Toggling status for account ID: {}", accountId);

        // Find account by ID
        Optional<Account> accountOpt = accountDao.findById(accountId);

        if (!accountOpt.isPresent()) {
            logger.warn("Account not found with ID: {}", accountId);
            sendJsonResponse(response, false, "Account not found", HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Account account = accountOpt.get();
        String currentStatus = account.getStatus();
        String newStatus;

        // Toggle status
        if ("active".equalsIgnoreCase(currentStatus)) {
            newStatus = "inactive";

            // Check if this is an admin account being deactivated
            boolean isAdmin = accountDao.isAdminAccount(accountId);
            if (isAdmin) {
                // Count active admins BEFORE deactivation
                int activeAdminCount = accountDao.countActiveAdmins();
                logger.info("Attempting to deactivate admin account. Current active admin count: {}", activeAdminCount);

                // Calculate how many admins will remain active AFTER this deactivation
                int remainingAdmins = activeAdminCount - 1;

                // Prevent deactivating if it would leave zero active admins
                if (remainingAdmins < 1) {
                    logger.warn("Cannot deactivate the last active admin account. Would leave {} active admins",
                            remainingAdmins);
                    sendJsonResponse(response, false,
                            "Cannot deactivate the last active admin account. At least one admin must remain active to manage the system.",
                            HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                logger.info("Deactivation allowed. Will have {} active admin(s) remaining", remainingAdmins);
            }
        } else {
            newStatus = "active";
        }

        // Update account status
        account.setStatus(newStatus);
        Account updatedAccount = accountDao.update(account);

        if (updatedAccount != null) {
            logger.info("Successfully toggled account status from {} to {} for account ID: {}",
                    currentStatus, newStatus, accountId);
            sendJsonResponse(response, true,
                    "Account status changed to " + newStatus + " successfully",
                    HttpServletResponse.SC_OK);
        } else {
            logger.error("Failed to update account status for account ID: {}", accountId);
            sendJsonResponse(response, false, "Failed to update account status",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send JSON response
     */
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int statusCode)
            throws IOException {
        response.setStatus(statusCode);

        PrintWriter out = response.getWriter();
        out.print("{");
        out.print("\"success\": " + success + ",");
        out.print("\"message\": \"" + escapeJson(message) + "\"");
        out.print("}");
        out.flush();
    }

    /**
     * Escape special characters in JSON string
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Parse Long from String, return null if invalid
     */
    private Long parseLongOrNull(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
