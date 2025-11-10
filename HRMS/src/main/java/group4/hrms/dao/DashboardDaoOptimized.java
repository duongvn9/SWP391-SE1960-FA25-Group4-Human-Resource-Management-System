package group4.hrms.dao;

import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OPTIMIZED DAO for Dashboard KPI queries
 * Reduces 30+ queries down to ~5 queries using UNION ALL
 */
public class DashboardDaoOptimized {
    private static final Logger logger = LoggerFactory.getLogger(DashboardDaoOptimized.class);

    /**
     * Get all basic counts in ONE query using UNION ALL
     */
    public Map<String, Integer> getAllBasicCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        
        String sql = """
            SELECT 'total_employees' as metric, COUNT(*) as value FROM users
            UNION ALL
            SELECT 'active_employees', COUNT(*) FROM users WHERE status = 'active'
            UNION ALL
            SELECT 'new_employees_this_month', COUNT(*) FROM users 
            WHERE YEAR(created_at) = YEAR(CURDATE()) AND MONTH(created_at) = MONTH(CURDATE())
            UNION ALL
            SELECT 'total_departments', COUNT(*) FROM departments
            UNION ALL
            SELECT 'total_accounts', COUNT(*) FROM accounts
            UNION ALL
            SELECT 'active_accounts', COUNT(*) FROM accounts WHERE status = 'active'
            UNION ALL
            SELECT 'inactive_accounts', COUNT(*) FROM accounts WHERE status = 'inactive'
            UNION ALL
            SELECT 'locked_accounts', COUNT(*) FROM accounts WHERE status = 'locked'
            UNION ALL
            SELECT 'pending_leave_requests', COUNT(*) FROM requests 
            WHERE request_type_id = 6 AND status = 'PENDING'
            UNION ALL
            SELECT 'approved_leaves_today', COUNT(*) FROM requests r
            WHERE r.request_type_id = 6 AND r.status = 'APPROVED'
            AND JSON_UNQUOTE(JSON_EXTRACT(r.detail, '$.startDate')) = CURDATE()
            UNION ALL
            SELECT 'pending_ot_requests', COUNT(*) FROM requests 
            WHERE request_type_id = 7 AND status = 'PENDING'
            UNION ALL
            SELECT 'present_today', COUNT(DISTINCT user_id) FROM attendance_logs
            WHERE DATE(checked_at) = CURDATE() AND check_type = 'IN'
            UNION ALL
            SELECT 'late_checkins_this_week', COUNT(*) FROM attendance_logs
            WHERE check_type = 'IN' AND YEARWEEK(checked_at, 1) = YEARWEEK(CURDATE(), 1)
            AND TIME(checked_at) > '09:00:00'
            UNION ALL
            SELECT 'payslips_generated', COUNT(*) FROM payslips
            WHERE DATE_FORMAT(period_start, '%Y-%m') = (
                SELECT DATE_FORMAT(MAX(period_start), '%Y-%m') FROM payslips
            )
            UNION ALL
            SELECT 'pending_recruitment_requests', COUNT(*) FROM requests r
            JOIN request_types rt ON r.request_type_id = rt.id
            WHERE rt.code = 'RECRUITMENT_REQUEST' AND r.status = 'PENDING'
            UNION ALL
            SELECT 'total_recruitment_requests_this_month', COUNT(*) FROM requests r
            JOIN request_types rt ON r.request_type_id = rt.id
            WHERE rt.code = 'RECRUITMENT_REQUEST'
            AND YEAR(r.created_at) = YEAR(CURDATE()) AND MONTH(r.created_at) = MONTH(CURDATE())
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("metric"), rs.getInt("value"));
            }
            logger.info("Fetched {} basic counts in 1 query", counts.size());
        } catch (SQLException e) {
            logger.error("Error getting all basic counts", e);
        }
        
        return counts;
    }

    /**
     * Get all decimal metrics in ONE query
     */
    public Map<String, Double> getAllDecimalMetrics() {
        Map<String, Double> metrics = new LinkedHashMap<>();
        
        String sql = """
            SELECT 'avg_leave_balance' as metric, 
                   COALESCE(AVG(balance_days), 0) as value 
            FROM leave_balances WHERE year = YEAR(CURDATE())
            UNION ALL
            SELECT 'total_ot_hours',
                   COALESCE(SUM(CAST(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.totalHours')) AS DECIMAL(10,2))), 0)
            FROM requests WHERE request_type_id = 7 AND status = 'APPROVED'
            UNION ALL
            SELECT 'total_ot_hours_this_month',
                   COALESCE(SUM(CAST(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.totalHours')) AS DECIMAL(10,2))), 0)
            FROM requests
            WHERE request_type_id = 7 AND status = 'APPROVED'
            AND YEAR(created_at) = YEAR(CURDATE()) AND MONTH(created_at) = MONTH(CURDATE())
            UNION ALL
            SELECT 'attendance_rate',
                   (COUNT(DISTINCT al.user_id) * 100.0 / NULLIF((SELECT COUNT(*) FROM users WHERE status = 'active'), 0))
            FROM attendance_logs al
            WHERE DATE(al.checked_at) = CURDATE() AND al.check_type = 'IN'
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                metrics.put(rs.getString("metric"), rs.getDouble("value"));
            }
            logger.info("Fetched {} decimal metrics in 1 query", metrics.size());
        } catch (SQLException e) {
            logger.error("Error getting all decimal metrics", e);
        }
        
        return metrics;
    }

    /**
     * Get payroll by department and currency (latest month)
     * Returns map with keys like "department_VND" and "department_USD"
     */
    public Map<String, BigDecimal> getPayrollByDepartment() {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        
        String sql = """
            SELECT 
                COALESCE(d.name, 'Unassigned') as department_name,
                p.currency,
                SUM(p.net_amount) as total_payroll
            FROM payslips p
            JOIN users u ON p.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            WHERE DATE_FORMAT(p.period_start, '%Y-%m') = (
                SELECT DATE_FORMAT(MAX(period_start), '%Y-%m') FROM payslips
            )
            GROUP BY d.name, p.currency
            ORDER BY p.currency, total_payroll DESC
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String key = rs.getString("department_name") + "_" + rs.getString("currency");
                result.put(key, rs.getBigDecimal("total_payroll"));
            }
            logger.info("Fetched payroll for {} department-currency combinations in 1 query", result.size());
        } catch (SQLException e) {
            logger.error("Error getting payroll by department", e);
        }
        
        return result;
    }
    
    /**
     * Get payroll by department for specific currency
     */
    public Map<String, BigDecimal> getPayrollByDepartmentByCurrency(String currency) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        
        String sql = """
            SELECT 
                COALESCE(d.name, 'Unassigned') as department_name,
                SUM(p.net_amount) as total_payroll
            FROM payslips p
            JOIN users u ON p.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            WHERE DATE_FORMAT(p.period_start, '%Y-%m') = (
                SELECT DATE_FORMAT(MAX(period_start), '%Y-%m') FROM payslips
            )
            AND p.currency = ?
            GROUP BY d.name
            ORDER BY total_payroll DESC
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currency);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("department_name"), rs.getBigDecimal("total_payroll"));
                }
                logger.info("Fetched payroll for {} departments in {} currency", result.size(), currency);
            }
        } catch (SQLException e) {
            logger.error("Error getting payroll by department for currency: " + currency, e);
        }
        
        return result;
    }

    /**
     * Get payroll metrics by currency in ONE query
     */
    public Map<String, BigDecimal> getPayrollMetrics() {
        Map<String, BigDecimal> metrics = new LinkedHashMap<>();
        
        String sql = """
            SELECT 
                currency,
                SUM(net_amount) as total_payroll,
                AVG(net_amount) as avg_salary
            FROM payslips
            WHERE DATE_FORMAT(period_start, '%Y-%m') = (
                SELECT DATE_FORMAT(MAX(period_start), '%Y-%m') FROM payslips
            )
            GROUP BY currency
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String currency = rs.getString("currency");
                metrics.put("total_payroll_" + currency, rs.getBigDecimal("total_payroll"));
                metrics.put("avg_salary_" + currency, rs.getBigDecimal("avg_salary"));
            }
            logger.info("Fetched payroll metrics for {} currencies in 1 query", metrics.size() / 2);
        } catch (SQLException e) {
            logger.error("Error getting payroll metrics", e);
        }
        
        return metrics;
    }

    /**
     * Get all breakdowns in ONE query using UNION ALL
     */
    public Map<String, Map<String, Integer>> getAllBreakdowns() {
        Map<String, Map<String, Integer>> allBreakdowns = new LinkedHashMap<>();
        allBreakdowns.put("employees_by_department", new LinkedHashMap<>());
        allBreakdowns.put("employees_by_position", new LinkedHashMap<>());
        allBreakdowns.put("requests_by_status", new LinkedHashMap<>());
        allBreakdowns.put("requests_by_type", new LinkedHashMap<>());
        allBreakdowns.put("ot_requests_by_status", new LinkedHashMap<>());
        allBreakdowns.put("leave_requests_by_status", new LinkedHashMap<>());
        allBreakdowns.put("recruitment_requests_by_status", new LinkedHashMap<>());
        
        String sql = """
            SELECT 'employees_by_department' as breakdown_type, 
                   COALESCE(d.name, 'Unassigned') as category, 
                   COUNT(*) as count
            FROM users u
            LEFT JOIN departments d ON u.department_id = d.id
            WHERE u.status = 'active'
            GROUP BY d.name
            
            UNION ALL
            
            SELECT 'employees_by_position',
                   COALESCE(p.name, 'Unassigned'),
                   COUNT(*)
            FROM users u
            LEFT JOIN positions p ON u.position_id = p.id
            WHERE u.status = 'active'
            GROUP BY p.name
            
            UNION ALL
            
            SELECT 'requests_by_status',
                   status,
                   COUNT(*)
            FROM requests
            WHERE YEAR(created_at) = YEAR(CURDATE()) AND MONTH(created_at) = MONTH(CURDATE())
            GROUP BY status
            
            UNION ALL
            
            SELECT 'requests_by_type',
                   rt.name,
                   COUNT(*)
            FROM requests r
            JOIN request_types rt ON r.request_type_id = rt.id
            WHERE YEAR(r.created_at) = YEAR(CURDATE()) AND MONTH(r.created_at) = MONTH(CURDATE())
            GROUP BY rt.name
            
            UNION ALL
            
            SELECT 'ot_requests_by_status',
                   status,
                   COUNT(*)
            FROM requests
            WHERE request_type_id = 7
            GROUP BY status
            
            UNION ALL
            
            SELECT 'leave_requests_by_status',
                   status,
                   COUNT(*)
            FROM requests
            WHERE request_type_id = 6
            GROUP BY status
            
            UNION ALL
            
            SELECT 'recruitment_requests_by_status',
                   r.status,
                   COUNT(*)
            FROM requests r
            JOIN request_types rt ON r.request_type_id = rt.id
            WHERE rt.code = 'RECRUITMENT_REQUEST'
            GROUP BY r.status
            
            ORDER BY breakdown_type, count DESC
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String breakdownType = rs.getString("breakdown_type");
                String category = rs.getString("category");
                int count = rs.getInt("count");
                
                allBreakdowns.get(breakdownType).put(category, count);
            }
            logger.info("Fetched {} breakdowns in 1 query", allBreakdowns.size());
        } catch (SQLException e) {
            logger.error("Error getting all breakdowns", e);
        }
        
        return allBreakdowns;
    }

    /**
     * Get all trends (last 6 months) in ONE query
     */
    public Map<String, Map<String, Double>> getAllTrends() {
        Map<String, Map<String, Double>> allTrends = new LinkedHashMap<>();
        allTrends.put("employee_trend", new LinkedHashMap<>());
        allTrends.put("ot_trend", new LinkedHashMap<>());
        allTrends.put("attendance_trend", new LinkedHashMap<>());
        
        String sql = """
            SELECT 'employee_trend' as trend_type,
                   DATE_FORMAT(created_at, '%Y-%m') as month,
                   COUNT(*) as value
            FROM users
            WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
            GROUP BY DATE_FORMAT(created_at, '%Y-%m')
            
            UNION ALL
            
            SELECT 'ot_trend',
                   DATE_FORMAT(created_at, '%Y-%m'),
                   COALESCE(SUM(CAST(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.totalHours')) AS DECIMAL(10,2))), 0)
            FROM requests
            WHERE request_type_id = 7 AND status = 'APPROVED'
            AND created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
            GROUP BY DATE_FORMAT(created_at, '%Y-%m')
            
            UNION ALL
            
            SELECT 'attendance_trend',
                   DATE_FORMAT(checked_at, '%Y-%m'),
                   (COUNT(DISTINCT user_id) * 100.0 / NULLIF((SELECT COUNT(*) FROM users WHERE status = 'active'), 0))
            FROM attendance_logs
            WHERE check_type = 'IN'
            AND checked_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
            GROUP BY DATE_FORMAT(checked_at, '%Y-%m')
            
            ORDER BY trend_type, month
            """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String trendType = rs.getString("trend_type");
                String month = rs.getString("month");
                double value = rs.getDouble("value");
                
                allTrends.get(trendType).put(month, value);
            }
            logger.info("Fetched {} trends in 1 query", allTrends.size());
        } catch (SQLException e) {
            logger.error("Error getting all trends", e);
        }
        
        return allTrends;
    }
}
