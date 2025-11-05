package group4.hrms.dao;

import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO for Dashboard KPI queries
 */
public class DashboardDao {
    private static final Logger logger = LoggerFactory.getLogger(DashboardDao.class);

    // Employee metrics
    public int getTotalEmployees() {
        String sql = "SELECT COUNT(*) FROM users";
        return executeCountQuery(sql);
    }

    public int getActiveEmployees() {
        String sql = "SELECT COUNT(*) FROM users WHERE status = 'active'";
        return executeCountQuery(sql);
    }
    
    public int getTotalDepartments() {
        String sql = "SELECT COUNT(*) FROM departments";
        return executeCountQuery(sql);
    }
    
    // Account metrics
    public int getTotalAccounts() {
        String sql = "SELECT COUNT(*) FROM accounts";
        return executeCountQuery(sql);
    }
    
    public int getActiveAccounts() {
        String sql = "SELECT COUNT(*) FROM accounts WHERE status = 'active'";
        return executeCountQuery(sql);
    }
    
    public int getInactiveAccounts() {
        String sql = "SELECT COUNT(*) FROM accounts WHERE status = 'inactive'";
        return executeCountQuery(sql);
    }
    
    public int getLockedAccounts() {
        String sql = "SELECT COUNT(*) FROM accounts WHERE status = 'locked'";
        return executeCountQuery(sql);
    }

    public int getNewEmployeesThisMonth() {
        String sql = "SELECT COUNT(*) FROM users WHERE YEAR(created_at) = YEAR(CURDATE()) AND MONTH(created_at) = MONTH(CURDATE())";
        return executeCountQuery(sql);
    }

    // Leave metrics
    public int getPendingLeaveRequests() {
        String sql = "SELECT COUNT(*) FROM requests WHERE request_type_id = 6 AND status = 'PENDING'";
        return executeCountQuery(sql);
    }

    public int getApprovedLeavesToday() {
        String sql = """
            SELECT COUNT(*) FROM requests r
            WHERE r.request_type_id = 6 
            AND r.status = 'APPROVED'
            AND JSON_UNQUOTE(JSON_EXTRACT(r.detail, '$.startDate')) = CURDATE()
            """;
        return executeCountQuery(sql);
    }

    public double getAverageLeaveBalance() {
        String sql = "SELECT AVG(balance_days) FROM leave_balances WHERE year = YEAR(CURDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting average leave balance", e);
        }
        return 0.0;
    }

    // OT metrics
    public int getPendingOtRequests() {
        String sql = "SELECT COUNT(*) FROM requests WHERE request_type_id = 7 AND status = 'PENDING'";
        return executeCountQuery(sql);
    }

    public double getTotalOtHoursThisMonth() {
        String sql = """
            SELECT COALESCE(SUM(CAST(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.totalHours')) AS DECIMAL(10,2))), 0)
            FROM requests
            WHERE request_type_id = 7 
            AND status = 'APPROVED'
            AND YEAR(created_at) = YEAR(CURDATE())
            AND MONTH(created_at) = MONTH(CURDATE())
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting total OT hours", e);
        }
        return 0.0;
    }

    public double getTotalOtHours() {
        String sql = """
            SELECT COALESCE(SUM(CAST(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.totalHours')) AS DECIMAL(10,2))), 0)
            FROM requests
            WHERE request_type_id = 7 
            AND status = 'APPROVED'
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting total OT hours (all time)", e);
        }
        return 0.0;
    }

    public int getTotalOtRequests() {
        String sql = "SELECT COUNT(*) FROM requests WHERE request_type_id = 7 AND status = 'APPROVED'";
        return executeCountQuery(sql);
    }

    // Attendance metrics
    public int getPresentToday() {
        String sql = """
            SELECT COUNT(DISTINCT user_id) FROM attendance_logs
            WHERE DATE(checked_at) = CURDATE() AND check_type = 'IN'
            """;
        return executeCountQuery(sql);
    }

    public int getAbsentToday() {
        int activeEmployees = getActiveEmployees();
        int presentToday = getPresentToday();
        return Math.max(0, activeEmployees - presentToday);
    }

    public int getLateCheckinsThisWeek() {
        String sql = """
            SELECT COUNT(*) FROM attendance_logs
            WHERE check_type = 'IN'
            AND YEARWEEK(checked_at, 1) = YEARWEEK(CURDATE(), 1)
            AND TIME(checked_at) > '09:00:00'
            """;
        return executeCountQuery(sql);
    }

    public double getAttendanceRate() {
        String sql = """
            SELECT 
                (COUNT(DISTINCT al.user_id) * 100.0 / NULLIF((SELECT COUNT(*) FROM users WHERE status = 'active'), 0)) as rate
            FROM attendance_logs al
            WHERE DATE(al.checked_at) = CURDATE() AND al.check_type = 'IN'
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("rate");
            }
        } catch (SQLException e) {
            logger.error("Error getting attendance rate", e);
        }
        return 0.0;
    }

    // Payroll metrics - Get latest month with payslips, separated by currency
    public BigDecimal getTotalPayrollThisMonth(String currency) {
        String sql = """
            SELECT COALESCE(SUM(net_amount), 0) FROM payslips
            WHERE DATE_FORMAT(period_start, '%Y-%m') = (
                SELECT DATE_FORMAT(MAX(period_start), '%Y-%m') FROM payslips
            )
            AND currency = ?
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currency);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting total payroll for currency: " + currency, e);
        }
        return BigDecimal.ZERO;
    }

    public int getPayslipsGeneratedThisMonth() {
        String sql = """
            SELECT COUNT(*) FROM payslips
            WHERE DATE_FORMAT(period_start, '%Y-%m') = (
                SELECT DATE_FORMAT(MAX(period_start), '%Y-%m') FROM payslips
            )
            """;
        return executeCountQuery(sql);
    }

    public BigDecimal getAverageSalary(String currency) {
        String sql = """
            SELECT COALESCE(AVG(net_amount), 0) FROM payslips
            WHERE DATE_FORMAT(period_start, '%Y-%m') = (
                SELECT DATE_FORMAT(MAX(period_start), '%Y-%m') FROM payslips
            )
            AND currency = ?
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currency);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting average salary for currency: " + currency, e);
        }
        return BigDecimal.ZERO;
    }

    // Department breakdown
    public Map<String, Integer> getEmployeesByDepartment() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
            SELECT COALESCE(d.name, 'Unassigned') as dept_name, COUNT(*) as count
            FROM users u
            LEFT JOIN departments d ON u.department_id = d.id
            WHERE u.status = 'active'
            GROUP BY d.name
            ORDER BY count DESC
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("dept_name"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            logger.error("Error getting employees by department", e);
        }
        return result;
    }

    public Map<String, Integer> getEmployeesByPosition() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
            SELECT COALESCE(p.name, 'Unassigned') as pos_name, COUNT(*) as count
            FROM users u
            LEFT JOIN positions p ON u.position_id = p.id
            WHERE u.status = 'active'
            GROUP BY p.name
            ORDER BY count DESC
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("pos_name"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            logger.error("Error getting employees by position", e);
        }
        return result;
    }

    // Request breakdown
    public Map<String, Integer> getRequestsByStatus() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
            SELECT status, COUNT(*) as count
            FROM requests
            WHERE YEAR(created_at) = YEAR(CURDATE())
            AND MONTH(created_at) = MONTH(CURDATE())
            GROUP BY status
            ORDER BY count DESC
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            logger.error("Error getting requests by status", e);
        }
        return result;
    }

    public Map<String, Integer> getOtRequestsByStatus() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
            SELECT status, COUNT(*) as count
            FROM requests
            WHERE request_type_id = 7
            GROUP BY status
            ORDER BY count DESC
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            logger.error("Error getting OT requests by status", e);
        }
        return result;
    }

    public Map<String, Integer> getLeaveRequestsByStatus() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
            SELECT status, COUNT(*) as count
            FROM requests
            WHERE request_type_id = 6
            GROUP BY status
            ORDER BY count DESC
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            logger.error("Error getting Leave requests by status", e);
        }
        return result;
    }

    public Map<String, Integer> getRequestsByType() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
            SELECT rt.name, COUNT(*) as count
            FROM requests r
            JOIN request_types rt ON r.request_type_id = rt.id
            WHERE YEAR(r.created_at) = YEAR(CURDATE())
            AND MONTH(r.created_at) = MONTH(CURDATE())
            GROUP BY rt.name
            ORDER BY count DESC
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("name"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            logger.error("Error getting requests by type", e);
        }
        return result;
    }

    // Monthly trends (last 6 months)
    public Map<String, Integer> getEmployeeTrend() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
            SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(*) as count
            FROM users
            WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
            GROUP BY DATE_FORMAT(created_at, '%Y-%m')
            ORDER BY month
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("month"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            logger.error("Error getting employee trend", e);
        }
        return result;
    }

    public Map<String, Double> getOtTrend() {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = """
            SELECT 
                DATE_FORMAT(created_at, '%Y-%m') as month,
                COALESCE(SUM(CAST(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.totalHours')) AS DECIMAL(10,2))), 0) as total_hours
            FROM requests
            WHERE request_type_id = 7 
            AND status = 'APPROVED'
            AND created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
            GROUP BY DATE_FORMAT(created_at, '%Y-%m')
            ORDER BY month
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("month"), rs.getDouble("total_hours"));
            }
        } catch (SQLException e) {
            logger.error("Error getting OT trend", e);
        }
        return result;
    }

    public Map<String, Double> getAttendanceTrend() {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = """
            SELECT 
                DATE_FORMAT(checked_at, '%Y-%m') as month,
                (COUNT(DISTINCT user_id) * 100.0 / NULLIF((SELECT COUNT(*) FROM users WHERE status = 'active'), 0)) as rate
            FROM attendance_logs
            WHERE check_type = 'IN'
            AND checked_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
            GROUP BY DATE_FORMAT(checked_at, '%Y-%m')
            ORDER BY month
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("month"), rs.getDouble("rate"));
            }
        } catch (SQLException e) {
            logger.error("Error getting attendance trend", e);
        }
        return result;
    }

    // Recruitment Request metrics
    public int getPendingRecruitmentRequests() {
        String sql = """
            SELECT COUNT(*) FROM requests r
            JOIN request_types rt ON r.request_type_id = rt.id
            WHERE rt.code = 'RECRUITMENT_REQUEST' AND r.status = 'PENDING'
            """;
        return executeCountQuery(sql);
    }

    public int getTotalRecruitmentRequestsThisMonth() {
        String sql = """
            SELECT COUNT(*) FROM requests r
            JOIN request_types rt ON r.request_type_id = rt.id
            WHERE rt.code = 'RECRUITMENT_REQUEST'
            AND YEAR(r.created_at) = YEAR(CURDATE())
            AND MONTH(r.created_at) = MONTH(CURDATE())
            """;
        return executeCountQuery(sql);
    }

    public Map<String, Integer> getRecruitmentRequestsByStatus() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = """
            SELECT r.status, COUNT(*) as count
            FROM requests r
            JOIN request_types rt ON r.request_type_id = rt.id
            WHERE rt.code = 'RECRUITMENT_REQUEST'
            GROUP BY r.status
            ORDER BY FIELD(r.status, 'PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            logger.error("Error getting recruitment requests by status", e);
        }
        return result;
    }

    // Helper method
    private int executeCountQuery(String sql) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error executing count query: " + sql, e);
        }
        return 0;
    }
}
