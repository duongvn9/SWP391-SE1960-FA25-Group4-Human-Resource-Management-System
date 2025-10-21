package group4.hrms.controller;

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

/**
 * Servlet to get accounts for a specific user
 * URL: /employees/users/accounts?userId={userId}
 */
@WebServlet("/employees/users/accounts")
public class UserAccountsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserAccountsServlet.class);

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

            // Get user ID from query parameter
            String userIdStr = request.getParameter("userId");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                out.write("{\"success\":false,\"message\":\"User ID is required\"}");
                return;
            }

            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                out.write("{\"success\":false,\"message\":\"Invalid user ID format\"}");
                return;
            }

            // Fetch accounts for this user
            // Simple query without account_roles for now
            String sql = "SELECT a.id, a.username, a.email_login, a.status " +
                    "FROM accounts a " +
                    "WHERE a.user_id = ? " +
                    "ORDER BY a.created_at DESC";

            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,\"accounts\":[");

            try (java.sql.Connection conn = group4.hrms.util.DatabaseUtil.getConnection();
                    java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setLong(1, userId);

                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first)
                            json.append(",");
                        first = false;

                        json.append("{");
                        long accountId = rs.getLong("id");

                        json.append("\"id\":").append(accountId).append(",");
                        json.append("\"username\":\"").append(escapeJson(rs.getString("username"))).append("\",");
                        json.append("\"emailLogin\":\"").append(escapeJson(rs.getString("email_login"))).append("\",");
                        json.append("\"status\":\"").append(escapeJson(rs.getString("status"))).append("\",");

                        // Get role name from account_roles if table exists
                        String roleName = getRoleName(accountId, conn);
                        if (roleName != null) {
                            json.append("\"roleName\":\"").append(escapeJson(roleName)).append("\"");
                        } else {
                            json.append("\"roleName\":null");
                        }

                        json.append("}");
                    }
                }
            }

            json.append("]}");
            out.write(json.toString());

            logger.info("Fetched accounts for user ID: {}", userId);

        } catch (Exception e) {
            logger.error("Error fetching user accounts", e);
            out.write("{\"success\":false,\"message\":\"An error occurred: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String getRoleName(long accountId, java.sql.Connection conn) {
        // Try to get role name from account_roles table
        // If table doesn't exist, return null
        String sql = "SELECT r.name FROM account_roles ar " +
                "INNER JOIN roles r ON ar.role_id = r.id " +
                "WHERE ar.account_id = ? " +
                "ORDER BY r.priority DESC LIMIT 1";

        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setLong(1, accountId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (Exception e) {
            // Table might not exist yet, return null
            logger.debug("Could not fetch role for account {}: {}", accountId, e.getMessage());
        }

        return null;
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
}
