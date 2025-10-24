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
 * Servlet xử lý cập nhật Account (AJAX)
 * URL: /employees/accounts/update
 */
@WebServlet("/employees/accounts/update")
public class AccountUpdateServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AccountUpdateServlet.class);
    private final AccountDao accountDao = new AccountDao();

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

            // Check authorization - Only ADMIN can update accounts
            if (!SessionUtil.hasRole(request, "ADMIN")) {
                out.write("{\"success\":false,\"message\":\"You don't have permission to update accounts\"}");
                return;
            }

            // Get form parameters
            String accountIdStr = request.getParameter("accountId");
            String emailLogin = request.getParameter("emailLogin");
            String status = request.getParameter("status");

            // Validate required fields
            if (accountIdStr == null || accountIdStr.trim().isEmpty()) {
                out.write("{\"success\":false,\"message\":\"Account ID is required\"}");
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

            // Fetch existing account
            Optional<Account> accountOpt = accountDao.findById(accountId);
            if (accountOpt.isEmpty()) {
                out.write("{\"success\":false,\"message\":\"Account not found\"}");
                return;
            }

            Account account = accountOpt.get();

            // Store original values for comparison
            String originalEmailLogin = account.getEmailLogin();
            String originalStatus = account.getStatus();

            // Update fields
            if (emailLogin != null && !emailLogin.trim().isEmpty()) {
                // Check if email login already exists for another account
                Optional<Account> existingAccount = accountDao.findByEmailLogin(emailLogin.trim());
                if (existingAccount.isPresent() && !existingAccount.get().getId().equals(accountId)) {
                    out.write("{\"success\":false,\"message\":\"Email login already exists for another account\"}");
                    return;
                }
                account.setEmailLogin(emailLogin.trim());
            }

            if (status != null && !status.trim().isEmpty()) {
                account.setStatus(status.trim());
            }

            // Check if any changes were made
            boolean hasChanges = !account.getEmailLogin().equals(originalEmailLogin)
                    || !account.getStatus().equals(originalStatus);

            if (!hasChanges) {
                out.write(
                        "{\"success\":true,\"message\":\"No changes detected. Account information is already up to date.\"}");
                logger.info("No changes detected for account ID: {}", accountId);
                return;
            }

            // Update account
            // Note: Permissions are determined by the user's position, not by role
            Account updatedAccount = accountDao.update(account);

            if (updatedAccount != null) {
                out.write("{\"success\":true,\"message\":\"Account updated successfully\"}");
                logger.info("Account updated successfully: {} by user: {}",
                        account.getUsername(),
                        SessionUtil.getCurrentUsername(request));
            } else {
                out.write("{\"success\":false,\"message\":\"Failed to update account\"}");
            }

        } catch (Exception e) {
            logger.error("Error updating account", e);
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "Unknown error";
            out.write("{\"success\":false,\"message\":\"An error occurred: " + errorMsg + "\"}");
        }
    }
}
