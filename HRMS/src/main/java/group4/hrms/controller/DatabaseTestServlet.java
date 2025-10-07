package group4.hrms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Test Servlet để kiểm tra kết nối database
 * URL: /test-db
 *
 * @author Group4
 * @since 1.0
 */
@WebServlet("/test-db")
public class DatabaseTestServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTestServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>HRMS Database Test</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println(".success { color: green; }");
            out.println(".error { color: red; }");
            out.println(".info { color: blue; }");
            out.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>HRMS Database Connection Test</h1>");

            // Test basic connection
            testBasicConnection(out);

            // Test pool info
            testPoolInfo(out);

            // Test database queries
            testDatabaseQueries(out);

            out.println("</body>");
            out.println("</html>");
        }
    }

    private void testBasicConnection(PrintWriter out) {
        out.println("<h2>1. Test Basic Connection</h2>");

        try {
            if (DatabaseUtil.testConnection()) {
                out.println("<p class='success'>✓ Kết nối database thành công!</p>");
                logger.info("Test connection thành công");
            } else {
                out.println("<p class='error'>✗ Kết nối database thất bại!</p>");
                logger.error("Test connection thất bại");
            }
        } catch (Exception e) {
            out.println("<p class='error'>✗ Lỗi khi test connection: " + e.getMessage() + "</p>");
            logger.error("Lỗi khi test connection", e);
        }
    }

    private void testPoolInfo(PrintWriter out) {
        out.println("<h2>2. Connection Pool Information</h2>");

        try {
            String poolInfo = DatabaseUtil.getPoolInfo();
            out.println("<p class='info'>" + poolInfo + "</p>");
            logger.info("Pool info: {}", poolInfo);
        } catch (Exception e) {
            out.println("<p class='error'>✗ Lỗi khi lấy pool info: " + e.getMessage() + "</p>");
            logger.error("Lỗi khi lấy pool info", e);
        }
    }

    private void testDatabaseQueries(PrintWriter out) {
        out.println("<h2>3. Test Database Queries</h2>");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            // Test 1: Check MySQL version
            out.println("<h3>3.1 MySQL Version</h3>");
            stmt = conn.prepareStatement("SELECT VERSION() as version");
            rs = stmt.executeQuery();

            if (rs.next()) {
                String version = rs.getString("version");
                out.println("<p class='success'>✓ MySQL Version: " + version + "</p>");
                logger.info("MySQL version: {}", version);
            }
            rs.close();
            stmt.close();

            // Test 2: Check current database
            out.println("<h3>3.2 Current Database</h3>");
            stmt = conn.prepareStatement("SELECT DATABASE() as db_name");
            rs = stmt.executeQuery();

            if (rs.next()) {
                String dbName = rs.getString("db_name");
                out.println("<p class='success'>✓ Current Database: " + dbName + "</p>");
                logger.info("Current database: {}", dbName);
            }
            rs.close();
            stmt.close();

            // Test 3: Check current time with timezone
            out.println("<h3>3.3 Current Time (Asia/Ho_Chi_Minh)</h3>");
            stmt = conn.prepareStatement("SELECT NOW() as current_datetime, @@time_zone as server_timezone");
            rs = stmt.executeQuery();

            if (rs.next()) {
                String currentTime = rs.getString("current_datetime");
                String timezone = rs.getString("server_timezone");
                out.println("<p class='success'>✓ Current Time: " + currentTime + "</p>");
                out.println("<p class='info'>Timezone: " + timezone + "</p>");
                logger.info("Current time: {}, timezone: {}", currentTime, timezone);
            }
            rs.close();
            stmt.close();

            // Test 4: List existing tables
            out.println("<h3>3.4 Existing Tables</h3>");
            stmt = conn.prepareStatement("SHOW TABLES");
            rs = stmt.executeQuery();

            out.println("<table>");
            out.println("<tr><th>Table Name</th><th>Engine</th></tr>");

            boolean hasTables = false;
            int tableCount = 0;
            while (rs.next()) {
                String tableName = rs.getString(1);
                out.println("<tr><td>" + tableName + "</td><td>InnoDB</td></tr>");
                hasTables = true;
                tableCount++;
            }

            if (!hasTables) {
                out.println("<tr><td colspan='2'><em>Chưa có bảng nào trong database</em></td></tr>");
            } else {
                out.println("<tr><td colspan='2'><strong>Total: " + tableCount + " tables</strong></td></tr>");
            }

            out.println("</table>");

            // Test 5: Database character set and collation
            rs.close();
            stmt.close();
            out.println("<h3>3.5 Database Character Set</h3>");
            stmt = conn.prepareStatement(
                    "SELECT SCHEMA_NAME, DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?");
            stmt.setString(1, "hrmsv2");
            rs = stmt.executeQuery();

            if (rs.next()) {
                String charset = rs.getString("DEFAULT_CHARACTER_SET_NAME");
                String collation = rs.getString("DEFAULT_COLLATION_NAME");
                out.println("<p class='info'>Character Set: " + charset + "</p>");
                out.println("<p class='info'>Collation: " + collation + "</p>");

                if ("utf8mb4".equals(charset)) {
                    out.println(
                            "<p class='success'>✓ UTF8MB4 support enabled - có thể lưu emoji và ký tự đặc biệt</p>");
                } else {
                    out.println(
                            "<p class='warning'>⚠ Character set không phải UTF8MB4 - có thể không support emoji</p>");
                }
            }

        } catch (SQLException e) {
            out.println("<p class='error'>✗ Lỗi khi thực hiện queries: " + e.getMessage() + "</p>");
            logger.error("Lỗi khi thực hiện test queries", e);
        } finally {
            // Đóng resources
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.warn("Lỗi khi đóng ResultSet", e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.warn("Lỗi khi đóng PreparedStatement", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.warn("Lỗi khi đóng Connection", e);
                }
            }
        }
    }
}