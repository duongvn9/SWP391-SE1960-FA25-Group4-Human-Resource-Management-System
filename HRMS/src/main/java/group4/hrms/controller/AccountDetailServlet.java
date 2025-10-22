package group4.hrms.controller;

import group4.hrms.dao.AccountDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Account;
import group4.hrms.model.User;
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
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Servlet xử lý Account Detail (AJAX)
 * URL: /employees/accounts/details?id={accountId}
 */
@WebServlet("/employees/accounts/details")
public class AccountDetailServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AccountDetailServlet.class);
    private final AccountDao accountDao = new AccountDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

            // Get account ID from query parameter
            String accountIdStr = request.getParameter("id");
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

            // Fetch account details
            Optional<Account> accountOpt = accountDao.findById(accountId);
            if (accountOpt.isEmpty()) {
                out.write("{\"success\":false,\"message\":\"Account not found\"}");
                return;
            }

            Account account = accountOpt.get();

            // Fetch user details
            Optional<User> userOpt = userDao.findById(account.getUserId());
            User user = userOpt.orElse(null);

            // Build JSON response manually
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,\"account\":{");
            json.append("\"id\":").append(account.getId()).append(",");
            json.append("\"username\":\"").append(escapeJson(account.getUsername())).append("\",");
            json.append("\"emailLogin\":\"").append(escapeJson(account.getEmailLogin())).append("\",");
            json.append("\"status\":\"").append(escapeJson(account.getStatus())).append("\",");
            json.append("\"userId\":").append(account.getUserId());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            if (account.getLastLoginAt() != null) {
                json.append(",\"lastLoginAt\":\"").append(account.getLastLoginAt().format(formatter)).append("\"");
            } else {
                json.append(",\"lastLoginAt\":null");
            }

            // Add created and updated timestamps
            if (account.getCreatedAt() != null) {
                json.append(",\"createdAt\":\"").append(account.getCreatedAt().format(formatter)).append("\"");
            } else {
                json.append(",\"createdAt\":null");
            }

            if (account.getUpdatedAt() != null) {
                json.append(",\"updatedAt\":\"").append(account.getUpdatedAt().format(formatter)).append("\"");
            } else {
                json.append(",\"updatedAt\":null");
            }

            // Get password updated at from auth_local_credentials
            String passwordUpdatedAt = getPasswordUpdatedAt(accountId);
            if (passwordUpdatedAt != null) {
                json.append(",\"passwordUpdatedAt\":\"").append(passwordUpdatedAt).append("\"");
            } else {
                json.append(",\"passwordUpdatedAt\":null");
            }

            // Add user information
            if (user != null) {
                json.append(",\"userFullName\":\"").append(escapeJson(user.getFullName())).append("\"");
                json.append(",\"userEmployeeCode\":\"").append(escapeJson(user.getEmployeeCode())).append("\"");
                json.append(",\"userEmailCompany\":\"").append(escapeJson(user.getEmailCompany())).append("\"");

                if (user.getDepartmentId() != null) {
                    json.append(",\"departmentId\":").append(user.getDepartmentId());
                }
                if (user.getPositionId() != null) {
                    json.append(",\"positionId\":").append(user.getPositionId());
                }

                // Get department and position names from cache
                String deptName = getDepartmentName(user.getDepartmentId());
                String posName = getPositionName(user.getPositionId());

                if (deptName != null) {
                    json.append(",\"departmentName\":\"").append(escapeJson(deptName)).append("\"");
                }
                if (posName != null) {
                    json.append(",\"positionName\":\"").append(escapeJson(posName)).append("\"");
                }
            }

            json.append("}}");
            out.write(json.toString());

            logger.info("Account details fetched successfully for ID: {}", accountId);

        } catch (Exception e) {
            logger.error("Error fetching account details", e);
            out.write("{\"success\":false,\"message\":\"An error occurred: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null)
            return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String getDepartmentName(Long departmentId) {
        if (departmentId == null)
            return null;
        try {
            // Simple query to get department name
            return group4.hrms.util.DropdownCacheUtil.getCachedDepartments(getServletContext())
                    .stream()
                    .filter(d -> d.getId().equals(departmentId))
                    .findFirst()
                    .map(d -> d.getName())
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error getting department name", e);
            return null;
        }
    }

    private String getPositionName(Long positionId) {
        if (positionId == null)
            return null;
        try {
            // Simple query to get position name
            return group4.hrms.util.DropdownCacheUtil.getCachedPositions(getServletContext())
                    .stream()
                    .filter(p -> p.getId().equals(positionId))
                    .findFirst()
                    .map(p -> p.getName())
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error getting position name", e);
            return null;
        }
    }

    private String getPasswordUpdatedAt(Long accountId) {
        if (accountId == null)
            return null;

        String sql = "SELECT alc.password_updated_at " +
                "FROM auth_local_credentials alc " +
                "INNER JOIN auth_identities ai ON alc.identity_id = ai.id " +
                "WHERE ai.account_id = ? " +
                "ORDER BY alc.password_updated_at DESC LIMIT 1";

        try (java.sql.Connection conn = group4.hrms.util.DatabaseUtil.getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);

            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    java.sql.Timestamp timestamp = rs.getTimestamp("password_updated_at");
                    if (timestamp != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        return timestamp.toLocalDateTime().format(formatter);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error getting password updated at for account {}: {}", accountId, e.getMessage());
        }

        return null;
    }
}
