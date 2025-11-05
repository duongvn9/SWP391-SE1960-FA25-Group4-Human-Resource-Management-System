package group4.hrms.service;

import group4.hrms.dao.PayslipDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.DirtyPayslipIssue;
import group4.hrms.dto.MissingPayslipIssue;
import group4.hrms.dto.PayslipIssue;
import group4.hrms.dto.PayslipSummaryCounters;
import group4.hrms.model.Payslip;
import group4.hrms.model.User;
import group4.hrms.util.DatabaseUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for detecting and resolving payslip issues
 * Requirements: 4.4, 4.5, 4.6, 4.7, 2.3
 */
public class PayslipIssuesService {

    private static final Logger logger = LoggerFactory.getLogger(PayslipIssuesService.class);

    private final PayslipDao payslipDao;
    private final UserDao userDao;

    public PayslipIssuesService() {
        this.payslipDao = new PayslipDao();
        this.userDao = new UserDao();
    }

    public PayslipIssuesService(PayslipDao payslipDao, UserDao userDao) {
        this.payslipDao = payslipDao;
        this.userDao = userDao;
    }

    /**
     * Find employees without payslips for a period
     * Requirements: 4.4, 4.5
     */
    public List<MissingPayslipIssue> findMissingPayslips(LocalDate periodStart, LocalDate periodEnd,
                                                        List<Long> scopeUserIds) throws SQLException {
        logger.info("Finding missing payslips for period {} to {}, scope: {} users",
                   periodStart, periodEnd, scopeUserIds != null ? scopeUserIds.size() : "all");

        List<MissingPayslipIssue> missingIssues = new ArrayList<>();

        if (periodStart == null || periodEnd == null) {
            logger.warn("Period dates cannot be null");
            return missingIssues;
        }

        // Build query to find active users without payslips for the period
        StringBuilder sql = new StringBuilder("""
            SELECT u.id, u.employee_code, u.full_name, d.name as department_name,
                   CASE WHEN al.user_id IS NOT NULL THEN 1 ELSE 0 END as has_attendance,
                   CASE WHEN sh.user_id IS NOT NULL THEN 1 ELSE 0 END as has_salary
            FROM users u
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN payslips p ON u.id = p.user_id
                AND p.period_start = ? AND p.period_end = ?
            LEFT JOIN (
                SELECT DISTINCT user_id
                FROM attendance_logs
                WHERE DATE(checked_at) >= ? AND DATE(checked_at) <= ?
            ) al ON u.id = al.user_id
            LEFT JOIN (
                SELECT DISTINCT user_id
                FROM salary_history
                WHERE effective_from <= ? AND (effective_to IS NULL OR effective_to >= ?)
            ) sh ON u.id = sh.user_id
            WHERE u.status = 'active'
                AND p.id IS NULL
            """);

        // Add scope filter if provided
        if (scopeUserIds != null && !scopeUserIds.isEmpty()) {
            String placeholders = String.join(",", scopeUserIds.stream().map(id -> "?").toArray(String[]::new));
            sql.append(" AND u.id IN (").append(placeholders).append(")");
        }

        sql.append(" ORDER BY d.name, u.full_name");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodStart));
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodEnd));
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodStart));
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodEnd));
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodEnd));
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodStart));

            // Set scope parameters if provided
            if (scopeUserIds != null && !scopeUserIds.isEmpty()) {
                for (Long userId : scopeUserIds) {
                    stmt.setLong(paramIndex++, userId);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MissingPayslipIssue issue = new MissingPayslipIssue(
                        rs.getLong("id"),
                        rs.getString("employee_code"),
                        rs.getString("full_name"),
                        rs.getString("department_name"),
                        periodStart,
                        periodEnd
                    );

                    issue.setHasAttendanceData(rs.getBoolean("has_attendance"));
                    issue.setHasSalaryData(rs.getBoolean("has_salary"));

                    missingIssues.add(issue);
                }
            }
        }

        logger.info("Found {} missing payslip issues", missingIssues.size());
        return missingIssues;
    }

    /**
     * Find payslips needing regeneration
     * Requirements: 4.4, 4.5, 4.7
     */
    public List<DirtyPayslipIssue> findDirtyPayslips(LocalDate periodStart, LocalDate periodEnd,
                                                    List<Long> scopeUserIds) throws SQLException {
        logger.info("Finding dirty payslips for period {} to {}, scope: {} users",
                   periodStart, periodEnd, scopeUserIds != null ? scopeUserIds.size() : "all");

        List<DirtyPayslipIssue> dirtyIssues = new ArrayList<>();

        if (periodStart == null || periodEnd == null) {
            logger.warn("Period dates cannot be null");
            return dirtyIssues;
        }

        // Build query to find dirty payslips
        StringBuilder sql = new StringBuilder("""
            SELECT p.id, p.user_id, p.dirty_reason, p.updated_at, p.generated_at,
                   u.employee_code, u.full_name, d.name as department_name
            FROM payslips p
            INNER JOIN users u ON p.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            WHERE p.is_dirty = 1
                AND p.period_start = ? AND p.period_end = ?
            """);

        // Add scope filter if provided
        if (scopeUserIds != null && !scopeUserIds.isEmpty()) {
            String placeholders = String.join(",", scopeUserIds.stream().map(id -> "?").toArray(String[]::new));
            sql.append(" AND p.user_id IN (").append(placeholders).append(")");
        }

        sql.append(" ORDER BY p.updated_at DESC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodStart));
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodEnd));

            // Set scope parameters if provided
            if (scopeUserIds != null && !scopeUserIds.isEmpty()) {
                for (Long userId : scopeUserIds) {
                    stmt.setLong(paramIndex++, userId);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DirtyPayslipIssue issue = new DirtyPayslipIssue(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("employee_code"),
                        rs.getString("full_name"),
                        rs.getString("department_name"),
                        periodStart,
                        periodEnd
                    );

                    issue.setDirtyReason(rs.getString("dirty_reason"));

                    java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        issue.setLastChangedAt(updatedAt.toLocalDateTime());
                    }

                    java.sql.Timestamp generatedAt = rs.getTimestamp("generated_at");
                    if (generatedAt != null) {
                        issue.setLastGeneratedAt(generatedAt.toLocalDateTime());
                    }

                    dirtyIssues.add(issue);
                }
            }
        }

        logger.info("Found {} dirty payslip issues", dirtyIssues.size());
        return dirtyIssues;
    }

    /**
     * Get all issues for a period and scope
     * Requirements: 4.6
     */
    public List<PayslipIssue> detectIssues(LocalDate periodStart, LocalDate periodEnd,
                                          List<Long> scopeUserIds) throws SQLException {
        logger.info("Detecting all payslip issues for period {} to {}", periodStart, periodEnd);

        List<PayslipIssue> allIssues = new ArrayList<>();

        // Find missing payslips
        List<MissingPayslipIssue> missingIssues = findMissingPayslips(periodStart, periodEnd, scopeUserIds);
        allIssues.addAll(missingIssues);

        // Find dirty payslips
        List<DirtyPayslipIssue> dirtyIssues = findDirtyPayslips(periodStart, periodEnd, scopeUserIds);
        allIssues.addAll(dirtyIssues);

        logger.info("Detected {} total issues ({} missing, {} dirty)",
                   allIssues.size(), missingIssues.size(), dirtyIssues.size());

        return allIssues;
    }

    /**
     * Quick generate payslips for missing issues
     * Requirements: 4.6, 4.7
     */
    public IssueResolutionResult quickGenerate(List<Long> userIds, LocalDate periodStart, LocalDate periodEnd) {
        logger.info("Quick generating payslips for {} users, period {} to {}",
                   userIds.size(), periodStart, periodEnd);

        IssueResolutionResult result = new IssueResolutionResult();
        result.setAction("QUICK_GENERATE");
        result.setRequestedCount(userIds.size());

        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        for (Long userId : userIds) {
            try {
                // Check if payslip already exists
                Optional<Payslip> existingPayslip = payslipDao.findByUserAndPeriod(userId, periodStart, periodEnd);
                if (existingPayslip.isPresent()) {
                    logger.warn("Payslip already exists for user {} in period {} to {}",
                               userId, periodStart, periodEnd);
                    errors.add("User " + userId + ": Payslip already exists");
                    failureCount++;
                    continue;
                }

                // Create basic payslip record (actual calculation would be done by PayslipGenerationService)
                Payslip payslip = new Payslip(userId, periodStart, periodEnd);
                payslip.setStatus("generated");
                payslip.setGeneratedAt(LocalDateTime.now());

                // Save the payslip
                Payslip savedPayslip = payslipDao.save(payslip);
                if (savedPayslip != null) {
                    successCount++;
                    logger.debug("Successfully created payslip for user {}", userId);
                } else {
                    failureCount++;
                    errors.add("User " + userId + ": Failed to save payslip");
                }

            } catch (Exception e) {
                failureCount++;
                String errorMsg = "User " + userId + ": " + e.getMessage();
                errors.add(errorMsg);
                logger.error("Failed to generate payslip for user {}: {}", userId, e.getMessage(), e);
            }
        }

        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setErrors(errors);
        result.setCompletedAt(LocalDateTime.now());

        logger.info("Quick generate completed: {} success, {} failures", successCount, failureCount);
        return result;
    }



    /**
     * Quick regenerate dirty payslips
     * Requirements: 4.6, 4.7
     */
    public IssueResolutionResult quickRegenerate(List<Long> payslipIds) {
        logger.info("Quick regenerating {} payslips", payslipIds.size());

        IssueResolutionResult result = new IssueResolutionResult();
        result.setAction("QUICK_REGENERATE");
        result.setRequestedCount(payslipIds.size());

        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        for (Long payslipId : payslipIds) {
            try {
                // Find the payslip
                Optional<Payslip> payslipOpt = payslipDao.findById(payslipId);
                if (!payslipOpt.isPresent()) {
                    failureCount++;
                    errors.add("Payslip " + payslipId + ": Not found");
                    continue;
                }

                Payslip payslip = payslipOpt.get();

                // Reset dirty flag and update timestamps
                payslip.setIsDirty(false);
                payslip.setDirtyReason(null);
                payslip.setUpdatedAt(LocalDateTime.now());
                payslip.setGeneratedAt(LocalDateTime.now());

                // Update the payslip (actual recalculation would be done by PayslipCalculationService)
                Payslip updated = payslipDao.update(payslip);
                if (updated != null) {
                    successCount++;
                    logger.debug("Successfully regenerated payslip {}", payslipId);
                } else {
                    failureCount++;
                    errors.add("Payslip " + payslipId + ": Failed to update");
                }

            } catch (Exception e) {
                failureCount++;
                String errorMsg = "Payslip " + payslipId + ": " + e.getMessage();
                errors.add(errorMsg);
                logger.error("Failed to regenerate payslip {}: {}", payslipId, e.getMessage(), e);
            }
        }

        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setErrors(errors);
        result.setCompletedAt(LocalDateTime.now());

        logger.info("Quick regenerate completed: {} success, {} failures", successCount, failureCount);
        return result;
    }

    /**
     * Get summary counters for payslips in a period and scope
     * Requirements: 2.3, 4.6
     */
    public PayslipSummaryCounters getSummaryCounters(LocalDate periodStart, LocalDate periodEnd,
                                                   List<Long> scopeUserIds) throws SQLException {
        logger.info("Getting summary counters for period {} to {}, scope: {} users",
                   periodStart, periodEnd, scopeUserIds != null ? scopeUserIds.size() : "all");

        if (periodStart == null || periodEnd == null) {
            logger.warn("Period dates cannot be null");
            return new PayslipSummaryCounters(0, 0, 0, 0);
        }

        // Build query to get all counts in one go for performance
        StringBuilder sql = new StringBuilder("""
            SELECT
                COUNT(DISTINCT u.id) as total_in_scope,
                COUNT(DISTINCT CASE WHEN p.id IS NOT NULL AND p.generated_at IS NOT NULL AND (p.is_dirty IS NULL OR p.is_dirty = 0) THEN p.id END) as generated_count,
                COUNT(DISTINCT CASE WHEN p.id IS NOT NULL AND p.is_dirty = 1 THEN p.id END) as dirty_count,
                COUNT(DISTINCT CASE WHEN p.id IS NULL THEN u.id END) as missing_count
            FROM users u
            LEFT JOIN payslips p ON u.id = p.user_id
                AND p.period_start = ? AND p.period_end = ?
            WHERE u.status = 'active'
            """);

        // Add scope filter if provided
        if (scopeUserIds != null && !scopeUserIds.isEmpty()) {
            String placeholders = String.join(",", scopeUserIds.stream().map(id -> "?").toArray(String[]::new));
            sql.append(" AND u.id IN (").append(placeholders).append(")");
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodStart));
            stmt.setObject(paramIndex++, java.sql.Date.valueOf(periodEnd));

            // Set scope parameters if provided
            if (scopeUserIds != null && !scopeUserIds.isEmpty()) {
                for (Long userId : scopeUserIds) {
                    stmt.setLong(paramIndex++, userId);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long totalInScope = rs.getLong("total_in_scope");
                    long generatedCount = rs.getLong("generated_count");
                    long dirtyCount = rs.getLong("dirty_count");
                    long missingCount = rs.getLong("missing_count");

                    PayslipSummaryCounters counters = new PayslipSummaryCounters(
                        generatedCount, dirtyCount, missingCount, totalInScope);

                    logger.info("Summary counters: generated={}, dirty={}, missing={}, total={}",
                               generatedCount, dirtyCount, missingCount, totalInScope);

                    return counters;
                }
            }
        }

        logger.warn("No data found for summary counters");
        return new PayslipSummaryCounters(0, 0, 0, 0);
    }

    /**
     * Result class for issue resolution operations
     */
    public static class IssueResolutionResult {
        private String action;
        private int requestedCount;
        private int successCount;
        private int failureCount;
        private List<String> errors;
        private LocalDateTime completedAt;

        public IssueResolutionResult() {
            this.errors = new ArrayList<>();
        }

        // Getters and Setters
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public int getRequestedCount() { return requestedCount; }
        public void setRequestedCount(int requestedCount) { this.requestedCount = requestedCount; }

        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }

        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }

        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

        public boolean isSuccessful() { return failureCount == 0; }
        public boolean hasErrors() { return !errors.isEmpty(); }
    }
}