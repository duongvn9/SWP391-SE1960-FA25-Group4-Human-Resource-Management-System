package group4.hrms.dao;

import group4.hrms.dto.PayslipDto;
import group4.hrms.dto.PayslipFilter;
import group4.hrms.dto.PayslipSummaryCounters;
import group4.hrms.dto.PaginationMetadata;
import group4.hrms.model.Payslip;
import group4.hrms.util.DatabaseUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng payslips
 * Mapping theo database schema mới
 *
 * @author Group4
 */
public class PayslipDao extends BaseDao<Payslip, Long> {

    private static final Logger logger = LoggerFactory.getLogger(PayslipDao.class);

    @Override
    protected String getTableName() {
        return "payslips";
    }

    @Override
    protected Payslip mapResultSetToEntity(ResultSet rs) throws SQLException {
        Payslip payslip = new Payslip();
        payslip.setId(rs.getLong("id"));
        payslip.setUserId(rs.getLong("user_id"));
        payslip.setPeriodStart(rs.getDate("period_start") != null ? rs.getDate("period_start").toLocalDate() : null);
        payslip.setPeriodEnd(rs.getDate("period_end") != null ? rs.getDate("period_end").toLocalDate() : null);
        payslip.setCurrency(rs.getString("currency"));
        payslip.setGrossAmount(rs.getBigDecimal("gross_amount"));
        payslip.setNetAmount(rs.getBigDecimal("net_amount"));
        payslip.setDetailsJson(rs.getString("details_json"));
        payslip.setFilePath(rs.getString("file_path"));
        payslip.setStatus(rs.getString("status"));
        payslip.setCreatedAt(getLocalDateTime(rs, "created_at"));

        // Map new fields
        payslip.setBaseSalary(rs.getBigDecimal("base_salary"));
        payslip.setOtAmount(rs.getBigDecimal("ot_amount"));
        payslip.setLatenessDeduction(rs.getBigDecimal("lateness_deduction"));
        payslip.setUnderHoursDeduction(rs.getBigDecimal("under_hours_deduction"));
        payslip.setTaxAmount(rs.getBigDecimal("tax_amount"));
        payslip.setIsDirty(rs.getBoolean("is_dirty"));
        payslip.setDirtyReason(rs.getString("dirty_reason"));
        payslip.setUpdatedAt(getLocalDateTime(rs, "updated_at"));
        payslip.setGeneratedAt(getLocalDateTime(rs, "generated_at"));

        return payslip;
    }

    @Override
    protected void setEntityId(Payslip payslip, Long id) {
        payslip.setId(id);
    }

    @Override
    protected Long getEntityId(Payslip payslip) {
        return payslip.getId();
    }

    @Override
    protected String createInsertSql() {
        return "INSERT INTO payslips (user_id, period_start, period_end, currency, " +
               "gross_amount, net_amount, details_json, file_path, status, created_at, " +
               "base_salary, ot_amount, lateness_deduction, under_hours_deduction, tax_amount, " +
               "is_dirty, dirty_reason, updated_at, generated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE payslips SET user_id = ?, period_start = ?, period_end = ?, currency = ?, " +
               "gross_amount = ?, net_amount = ?, details_json = ?, file_path = ?, status = ?, " +
               "base_salary = ?, ot_amount = ?, lateness_deduction = ?, under_hours_deduction = ?, " +
               "tax_amount = ?, is_dirty = ?, dirty_reason = ?, updated_at = ?, generated_at = ? " +
               "WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Payslip payslip) throws SQLException {
        stmt.setLong(1, payslip.getUserId());
        stmt.setObject(2, payslip.getPeriodStart() != null ? Date.valueOf(payslip.getPeriodStart()) : null, Types.DATE);
        stmt.setObject(3, payslip.getPeriodEnd() != null ? Date.valueOf(payslip.getPeriodEnd()) : null, Types.DATE);
        stmt.setString(4, payslip.getCurrency());
        stmt.setBigDecimal(5, payslip.getGrossAmount());
        stmt.setBigDecimal(6, payslip.getNetAmount());
        stmt.setString(7, payslip.getDetailsJson());
        stmt.setString(8, payslip.getFilePath());
        stmt.setString(9, payslip.getStatus());
        setTimestamp(stmt, 10, payslip.getCreatedAt() != null ? payslip.getCreatedAt() : LocalDateTime.now());

        // Set new fields
        stmt.setBigDecimal(11, payslip.getBaseSalary() != null ? payslip.getBaseSalary() : new BigDecimal("0"));
        stmt.setBigDecimal(12, payslip.getOtAmount() != null ? payslip.getOtAmount() : new BigDecimal("0"));
        stmt.setBigDecimal(13, payslip.getLatenessDeduction() != null ? payslip.getLatenessDeduction() : new BigDecimal("0"));
        stmt.setBigDecimal(14, payslip.getUnderHoursDeduction() != null ? payslip.getUnderHoursDeduction() : new BigDecimal("0"));
        stmt.setBigDecimal(15, payslip.getTaxAmount() != null ? payslip.getTaxAmount() : new BigDecimal("0"));
        stmt.setBoolean(16, payslip.getIsDirty() != null ? payslip.getIsDirty() : false);
        stmt.setString(17, payslip.getDirtyReason());
        setTimestamp(stmt, 18, payslip.getUpdatedAt() != null ? payslip.getUpdatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 19, payslip.getGeneratedAt());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Payslip payslip) throws SQLException {
        stmt.setLong(1, payslip.getUserId());
        stmt.setObject(2, payslip.getPeriodStart() != null ? Date.valueOf(payslip.getPeriodStart()) : null, Types.DATE);
        stmt.setObject(3, payslip.getPeriodEnd() != null ? Date.valueOf(payslip.getPeriodEnd()) : null, Types.DATE);
        stmt.setString(4, payslip.getCurrency());
        stmt.setBigDecimal(5, payslip.getGrossAmount());
        stmt.setBigDecimal(6, payslip.getNetAmount());
        stmt.setString(7, payslip.getDetailsJson());
        stmt.setString(8, payslip.getFilePath());
        stmt.setString(9, payslip.getStatus());

        // Set new fields
        stmt.setBigDecimal(10, payslip.getBaseSalary() != null ? payslip.getBaseSalary() : new BigDecimal("0"));
        stmt.setBigDecimal(11, payslip.getOtAmount() != null ? payslip.getOtAmount() : new BigDecimal("0"));
        stmt.setBigDecimal(12, payslip.getLatenessDeduction() != null ? payslip.getLatenessDeduction() : new BigDecimal("0"));
        stmt.setBigDecimal(13, payslip.getUnderHoursDeduction() != null ? payslip.getUnderHoursDeduction() : new BigDecimal("0"));
        stmt.setBigDecimal(14, payslip.getTaxAmount() != null ? payslip.getTaxAmount() : new BigDecimal("0"));
        stmt.setBoolean(15, payslip.getIsDirty() != null ? payslip.getIsDirty() : false);
        stmt.setString(16, payslip.getDirtyReason());
        setTimestamp(stmt, 17, payslip.getUpdatedAt() != null ? payslip.getUpdatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 18, payslip.getGeneratedAt());

        stmt.setLong(19, payslip.getId());
    }

    // Business methods

    /**
     * Tìm payslip theo userId và kỳ lương
     */
    public Optional<Payslip> findByUserAndPeriod(Long userId, java.time.LocalDate periodStart, java.time.LocalDate periodEnd) throws SQLException {
        if (userId == null || periodStart == null || periodEnd == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM payslips WHERE user_id = ? AND period_start = ? AND period_end = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(periodStart));
            stmt.setDate(3, Date.valueOf(periodEnd));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Tìm tất cả payslip của một user
     */
    public List<Payslip> findByUserId(Long userId) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();

        if (userId == null) {
            return payslips;
        }

        String sql = "SELECT * FROM payslips WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }

        return payslips;
    }

    /**
     * Tìm payslip theo status
     */
    public List<Payslip> findByStatus(String status) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();

        if (status == null || status.trim().isEmpty()) {
            return payslips;
        }

        String sql = "SELECT * FROM payslips WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }

        return payslips;
    }

    /**
     * Tìm payslip theo khoảng thời gian
     */
    public List<Payslip> findByPeriodRange(java.time.LocalDate startDate, java.time.LocalDate endDate) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();

        if (startDate == null || endDate == null) {
            return payslips;
        }

        String sql = "SELECT * FROM payslips WHERE period_start >= ? AND period_end <= ? ORDER BY period_start DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }

        return payslips;
    }

    /**
     * Find employees without payslip for a given period (optimized single query)
     * Returns list of maps with user info: id, employeeCode, fullName, departmentId
     * BỎ QUA status filtering để lấy tất cả users chưa có payslip
     */
    public List<java.util.Map<String, Object>> findEmployeesWithoutPayslip(
            java.time.LocalDate periodStart, java.time.LocalDate periodEnd) throws SQLException {

        List<java.util.Map<String, Object>> employees = new ArrayList<>();

        String sql = """
            SELECT u.id, u.employee_code, u.full_name, u.department_id
            FROM users u
            WHERE NOT EXISTS (
                SELECT 1 FROM payslips p
                WHERE p.user_id = u.id
                AND p.period_start = ?
                AND p.period_end = ?
            )
            ORDER BY u.full_name
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(periodStart));
            stmt.setDate(2, Date.valueOf(periodEnd));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> empData = new java.util.HashMap<>();
                    empData.put("id", rs.getLong("id"));
                    empData.put("employeeCode", rs.getString("employee_code"));
                    empData.put("fullName", rs.getString("full_name"));
                    empData.put("departmentId", rs.getObject("department_id"));
                    employees.add(empData);
                }
            }
        }

        return employees;
    }

    /**
     * Cập nhật status của payslip
     */
    public boolean updateStatus(Long payslipId, String newStatus) throws SQLException {
        if (payslipId == null || newStatus == null || newStatus.trim().isEmpty()) {
            return false;
        }

        String sql = "UPDATE payslips SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setLong(2, payslipId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Đếm số payslip theo status
     */
    public long countByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM payslips WHERE status = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    /**
     * Đếm số payslip của một user
     */
    public long countByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM payslips WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    /**
     * Kiểm tra xem payslip đã tồn tại cho user và kỳ lương chưa
     */
    public boolean existsByUserAndPeriod(Long userId, java.time.LocalDate periodStart, java.time.LocalDate periodEnd) throws SQLException {
        return findByUserAndPeriod(userId, periodStart, periodEnd).isPresent();
    }

    /**
     * Lấy payslip với thông tin user (JOIN query)
     */
    public List<Payslip> findWithUserInfo() throws SQLException {
        List<Payslip> payslips = new ArrayList<>();

        String sql = "SELECT p.*, u.employee_code, u.full_name, u.employee_code as employee_id " +
                    "FROM payslips p " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "ORDER BY p.created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Payslip payslip = mapResultSetToEntity(rs);
                // Note: User info có thể được map vào PayslipDto thay vì entity
                payslips.add(payslip);
            }
        }

        return payslips;
    }

    /**
     * Tìm payslip theo dirty status
     */
    public List<Payslip> findDirtyPayslips(LocalDate periodStart, LocalDate periodEnd) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();

        String sql = "SELECT * FROM payslips WHERE is_dirty = 1";
        if (periodStart != null && periodEnd != null) {
            sql += " AND period_start >= ? AND period_end <= ?";
        }
        sql += " ORDER BY updated_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (periodStart != null && periodEnd != null) {
                stmt.setDate(paramIndex++, Date.valueOf(periodStart));
                stmt.setDate(paramIndex++, Date.valueOf(periodEnd));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }

        return payslips;
    }

    /**
     * Mark individual payslip as dirty with enhanced logging
     * Requirements: 9.1, 9.4, 9.7, 9.8
     */
    public boolean markDirty(Long userId, LocalDate periodStart, LocalDate periodEnd, String reason) throws SQLException {
        if (userId == null || periodStart == null || periodEnd == null) {
            logger.warn("Cannot mark payslip dirty: missing required parameters (userId={}, periodStart={}, periodEnd={})",
                       userId, periodStart, periodEnd);
            return false;
        }

        String sql = "UPDATE payslips SET is_dirty = 1, dirty_reason = ?, updated_at = ? " +
                    "WHERE user_id = ? AND period_start = ? AND period_end = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reason);
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, userId);
            stmt.setDate(4, Date.valueOf(periodStart));
            stmt.setDate(5, Date.valueOf(periodEnd));

            int rowsUpdated = stmt.executeUpdate();

            // Enhanced logging for audit purposes
            if (rowsUpdated > 0) {
                logger.info("Marked payslip as dirty for user {} in period {}-{}: {}",
                           userId, periodStart, periodEnd, reason);
            } else {
                logger.warn("No payslip found to mark dirty for user {} in period {}-{}",
                           userId, periodStart, periodEnd);
            }

            return rowsUpdated > 0;
        }
    }

    /**
     * Bulk update status for multiple payslips with optimized batching
     * Requirements: 9.1, 9.4, 9.7, 9.8
     */
    public int bulkUpdateStatus(List<Long> payslipIds, String status) throws SQLException {
        if (payslipIds == null || payslipIds.isEmpty() || status == null) {
            return 0;
        }

        // Process in batches to optimize connection pooling and avoid query size limits
        final int BATCH_SIZE = 1000;
        int totalUpdated = 0;

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction for better performance

            try {
                for (int i = 0; i < payslipIds.size(); i += BATCH_SIZE) {
                    int endIndex = Math.min(i + BATCH_SIZE, payslipIds.size());
                    List<Long> batch = payslipIds.subList(i, endIndex);

                    String placeholders = String.join(",", batch.stream().map(id -> "?").toArray(String[]::new));
                    String sql = "UPDATE payslips SET status = ?, updated_at = ? WHERE id IN (" + placeholders + ")";

                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, status);
                        setTimestamp(stmt, 2, LocalDateTime.now());

                        for (int j = 0; j < batch.size(); j++) {
                            stmt.setLong(3 + j, batch.get(j));
                        }

                        int batchUpdated = stmt.executeUpdate();
                        totalUpdated += batchUpdated;

                        logger.debug("Updated {} payslips in batch {}/{}", batchUpdated,
                                   (i / BATCH_SIZE) + 1, (payslipIds.size() + BATCH_SIZE - 1) / BATCH_SIZE);
                    }
                }

                conn.commit(); // Commit transaction
                logger.info("Successfully bulk updated status to '{}' for {} payslips", status, totalUpdated);
                return totalUpdated;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                logger.error("Failed to bulk update status, transaction rolled back", e);
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restore auto-commit
            }
        }
    }

    /**
     * Đếm số payslip dirty
     */
    public long countDirtyPayslips() throws SQLException {
        String sql = "SELECT COUNT(*) FROM payslips WHERE is_dirty = 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        return 0;
    }

    /**
     * Đếm số payslip đã được generate
     */
    public long countGeneratedPayslips(LocalDate periodStart, LocalDate periodEnd) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payslips WHERE generated_at IS NOT NULL";
        if (periodStart != null && periodEnd != null) {
            sql += " AND period_start >= ? AND period_end <= ?";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (periodStart != null && periodEnd != null) {
                stmt.setDate(paramIndex++, Date.valueOf(periodStart));
                stmt.setDate(paramIndex++, Date.valueOf(periodEnd));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    /**
     * Bulk mark multiple payslips as dirty
     * Requirements: 8.1, 8.2, 8.3
     */
    public int bulkMarkDirty(List<Long> userIds, LocalDate periodStart, LocalDate periodEnd, String reason) throws SQLException {
        if (userIds == null || userIds.isEmpty() || periodStart == null || periodEnd == null) {
            return 0;
        }

        String placeholders = String.join(",", userIds.stream().map(id -> "?").toArray(String[]::new));
        String sql = "UPDATE payslips SET is_dirty = 1, dirty_reason = ?, updated_at = ? " +
                    "WHERE user_id IN (" + placeholders + ") AND period_start = ? AND period_end = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reason);
            setTimestamp(stmt, 2, LocalDateTime.now());

            int paramIndex = 3;
            for (Long userId : userIds) {
                stmt.setLong(paramIndex++, userId);
            }
            stmt.setDate(paramIndex++, Date.valueOf(periodStart));
            stmt.setDate(paramIndex++, Date.valueOf(periodEnd));

            return stmt.executeUpdate();
        }
    }

    /**
     * Reset dirty flags for multiple payslips with optimized batching
     * Requirements: 8.6, 9.1, 9.4, 9.7, 9.8
     */
    public int bulkResetDirtyFlags(List<Long> payslipIds) throws SQLException {
        if (payslipIds == null || payslipIds.isEmpty()) {
            return 0;
        }

        // Process in batches for better performance
        final int BATCH_SIZE = 1000;
        int totalUpdated = 0;

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                for (int i = 0; i < payslipIds.size(); i += BATCH_SIZE) {
                    int endIndex = Math.min(i + BATCH_SIZE, payslipIds.size());
                    List<Long> batch = payslipIds.subList(i, endIndex);

                    String placeholders = String.join(",", batch.stream().map(id -> "?").toArray(String[]::new));
                    String sql = "UPDATE payslips SET is_dirty = 0, dirty_reason = NULL, updated_at = ? " +
                                "WHERE id IN (" + placeholders + ")";

                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        setTimestamp(stmt, 1, LocalDateTime.now());

                        for (int j = 0; j < batch.size(); j++) {
                            stmt.setLong(2 + j, batch.get(j));
                        }

                        int batchUpdated = stmt.executeUpdate();
                        totalUpdated += batchUpdated;
                    }
                }

                conn.commit(); // Commit transaction
                logger.info("Successfully reset dirty flags for {} payslips", totalUpdated);
                return totalUpdated;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                logger.error("Failed to reset dirty flags, transaction rolled back", e);
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restore auto-commit
            }
        }
    }

    /**
     * Find payslips that need regeneration (dirty or missing generated_at)
     * Requirements: 8.4
     */
    public List<Payslip> findPayslipsNeedingRegeneration(LocalDate periodStart, LocalDate periodEnd) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();

        String sql = "SELECT * FROM payslips WHERE (is_dirty = 1 OR generated_at IS NULL)";
        if (periodStart != null && periodEnd != null) {
            sql += " AND period_start >= ? AND period_end <= ?";
        }
        sql += " ORDER BY updated_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (periodStart != null && periodEnd != null) {
                stmt.setDate(paramIndex++, Date.valueOf(periodStart));
                stmt.setDate(paramIndex++, Date.valueOf(periodEnd));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }

        return payslips;
    }

    /**
     * Get dirty payslips with user information for reporting
     * Requirements: 8.4, 8.7
     */
    public List<Payslip> findDirtyPayslipsWithUserInfo(LocalDate periodStart, LocalDate periodEnd) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();

        String sql = "SELECT p.*, u.employee_code, u.full_name, u.employee_code as employee_id " +
                    "FROM payslips p " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "WHERE p.is_dirty = 1";

        if (periodStart != null && periodEnd != null) {
            sql += " AND p.period_start >= ? AND p.period_end <= ?";
        }
        sql += " ORDER BY p.updated_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (periodStart != null && periodEnd != null) {
                stmt.setDate(paramIndex++, Date.valueOf(periodStart));
                stmt.setDate(paramIndex++, Date.valueOf(periodEnd));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payslip payslip = mapResultSetToEntity(rs);
                    // Note: User info could be mapped to a PayslipDto instead
                    payslips.add(payslip);
                }
            }
        }

        return payslips;
    }

    /**
     * Get summary counters for payslips in a period with optional scope filtering
     * Optimized single query for performance with large datasets
     * Requirements: 2.3, 4.6
     */
    public PayslipSummaryCounters getSummaryCounters(LocalDate periodStart, LocalDate periodEnd,
                                                   List<Long> scopeUserIds) throws SQLException {
        if (periodStart == null || periodEnd == null) {
            return new PayslipSummaryCounters(0, 0, 0, 0);
        }

        // Build optimized query to get all counts in one go
        StringBuilder sql = new StringBuilder("""
            SELECT
                COUNT(DISTINCT u.id) as total_in_scope,
                COUNT(DISTINCT CASE WHEN p.id IS NOT NULL AND p.generated_at IS NOT NULL AND (p.is_dirty IS NULL OR p.is_dirty = 0) THEN p.id END) as generated_count,
                COUNT(DISTINCT CASE WHEN p.id IS NOT NULL AND p.is_dirty = 1 THEN p.id END) as dirty_count,
                COUNT(DISTINCT CASE WHEN p.id IS NULL THEN u.id END) as missing_count
            FROM users u
            LEFT JOIN payslips p ON u.id = p.user_id
                AND p.period_start = ? AND p.period_end = ?
            WHERE 1=1  -- Removed status filtering as per business requirement
            """);

        // Add scope filter if provided
        if (scopeUserIds != null && !scopeUserIds.isEmpty()) {
            String placeholders = String.join(",", scopeUserIds.stream().map(id -> "?").toArray(String[]::new));
            sql.append(" AND u.id IN (").append(placeholders).append(")");
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setDate(paramIndex++, Date.valueOf(periodStart));
            stmt.setDate(paramIndex++, Date.valueOf(periodEnd));

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

                    return new PayslipSummaryCounters(generatedCount, dirtyCount, missingCount, totalInScope);
                }
            }
        }

        return new PayslipSummaryCounters(0, 0, 0, 0);
    }

    /**
     * Bulk update multiple payslips with different statuses
     * Requirements: 9.1, 9.4, 9.7, 9.8
     */
    public int bulkUpdatePayslipStatuses(List<Long> payslipIds, List<String> statuses) throws SQLException {
        if (payslipIds == null || statuses == null || payslipIds.size() != statuses.size() || payslipIds.isEmpty()) {
            return 0;
        }

        int totalUpdated = 0;

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                String sql = "UPDATE payslips SET status = ?, updated_at = ? WHERE id = ?";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (int i = 0; i < payslipIds.size(); i++) {
                        stmt.setString(1, statuses.get(i));
                        setTimestamp(stmt, 2, LocalDateTime.now());
                        stmt.setLong(3, payslipIds.get(i));
                        stmt.addBatch();

                        // Execute batch every 1000 records
                        if ((i + 1) % 1000 == 0) {
                            int[] results = stmt.executeBatch();
                            totalUpdated += results.length;
                            stmt.clearBatch();
                        }
                    }

                    // Execute remaining batch
                    int[] results = stmt.executeBatch();
                    totalUpdated += results.length;
                }

                conn.commit(); // Commit transaction
                logger.info("Successfully bulk updated {} payslips with individual statuses", totalUpdated);
                return totalUpdated;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                logger.error("Failed to bulk update payslip statuses, transaction rolled back", e);
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restore auto-commit
            }
        }
    }

    /**
     * Find users missing payslips for a period with optional scope filtering
     * Optimized query for missing payslip detection with connection pooling optimization
     * Requirements: 4.4, 4.5, 9.1, 9.4, 9.7, 9.8
     */
    public List<Long> findMissingPayslipUsers(LocalDate periodStart, LocalDate periodEnd,
                                            List<Long> scopeUserIds) throws SQLException {
        List<Long> missingUserIds = new ArrayList<>();

        if (periodStart == null || periodEnd == null) {
            logger.warn("Cannot find missing payslip users: period dates are null");
            return missingUserIds;
        }

        StringBuilder sql = new StringBuilder("""
            SELECT u.id
            FROM users u
            LEFT JOIN payslips p ON u.id = p.user_id
                AND p.period_start = ? AND p.period_end = ?
            WHERE 1=1  -- Removed status filtering as per business requirement
                AND p.id IS NULL
            """);

        // Add scope filter if provided - process in batches for large scope lists
        if (scopeUserIds != null && !scopeUserIds.isEmpty()) {
            if (scopeUserIds.size() <= 1000) {
                // Small scope - use IN clause
                String placeholders = String.join(",", scopeUserIds.stream().map(id -> "?").toArray(String[]::new));
                sql.append(" AND u.id IN (").append(placeholders).append(")");
            } else {
                // Large scope - process in batches
                return findMissingPayslipUsersInBatches(periodStart, periodEnd, scopeUserIds);
            }
        }

        sql.append(" ORDER BY u.id");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setDate(paramIndex++, Date.valueOf(periodStart));
            stmt.setDate(paramIndex++, Date.valueOf(periodEnd));

            // Set scope parameters if provided
            if (scopeUserIds != null && !scopeUserIds.isEmpty() && scopeUserIds.size() <= 1000) {
                for (Long userId : scopeUserIds) {
                    stmt.setLong(paramIndex++, userId);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    missingUserIds.add(rs.getLong("id"));
                }
            }
        }

        logger.debug("Found {} users missing payslips for period {}-{}", missingUserIds.size(), periodStart, periodEnd);
        return missingUserIds;
    }

    /**
     * Helper method to find missing payslip users in batches for large scope lists
     * Requirements: 9.1, 9.4, 9.7, 9.8
     */
    private List<Long> findMissingPayslipUsersInBatches(LocalDate periodStart, LocalDate periodEnd,
                                                       List<Long> scopeUserIds) throws SQLException {
        List<Long> allMissingUserIds = new ArrayList<>();
        final int BATCH_SIZE = 1000;

        for (int i = 0; i < scopeUserIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, scopeUserIds.size());
            List<Long> batch = scopeUserIds.subList(i, endIndex);

            String placeholders = String.join(",", batch.stream().map(id -> "?").toArray(String[]::new));
            String sql = """
                SELECT u.id
                FROM users u
                LEFT JOIN payslips p ON u.id = p.user_id
                    AND p.period_start = ? AND p.period_end = ?
                WHERE 1=1  -- Removed status filtering as per business requirement
                    AND p.id IS NULL
                    AND u.id IN (""" + placeholders + """
                )
                ORDER BY u.id
                """;

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                int paramIndex = 1;
                stmt.setDate(paramIndex++, Date.valueOf(periodStart));
                stmt.setDate(paramIndex++, Date.valueOf(periodEnd));

                for (Long userId : batch) {
                    stmt.setLong(paramIndex++, userId);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        allMissingUserIds.add(rs.getLong("id"));
                    }
                }
            }
        }

        return allMissingUserIds;
    }

    /**
     * Count payslips by status for a specific period
     * Requirements: 2.3
     */
    public long countByStatusAndPeriod(String status, LocalDate periodStart, LocalDate periodEnd) throws SQLException {
        if (status == null || periodStart == null || periodEnd == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM payslips WHERE status = ? AND period_start = ? AND period_end = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setDate(2, Date.valueOf(periodStart));
            stmt.setDate(3, Date.valueOf(periodEnd));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    /**
     * Count dirty payslips for a specific period
     * Requirements: 2.3, 4.6
     */
    public long countDirtyPayslipsByPeriod(LocalDate periodStart, LocalDate periodEnd) throws SQLException {
        if (periodStart == null || periodEnd == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM payslips WHERE is_dirty = 1 AND period_start = ? AND period_end = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(periodStart));
            stmt.setDate(2, Date.valueOf(periodEnd));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    /**
     * Find payslips with filters and pagination support
     * Requirements: 1.2, 2.2, 2.4
     */
    public List<PayslipDto> findWithFilters(PayslipFilter filter, PaginationMetadata pagination) throws SQLException {
        List<PayslipDto> payslips = new ArrayList<>();

        if (filter == null) {
            logger.debug("PayslipDao.findWithFilters: filter is null, returning empty list");
            return payslips;
        }

        logger.debug("PayslipDao.findWithFilters called with filter: {}", filter);

        // Debug: Check total payslips in database
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement debugStmt = conn.prepareStatement("SELECT COUNT(*) FROM payslips");
             ResultSet debugRs = debugStmt.executeQuery()) {
            if (debugRs.next()) {
                logger.debug("Total payslips in database: {}", debugRs.getInt(1));
            }
        }

        // Build dynamic query with joins for user information
        StringBuilder sql = new StringBuilder("""
            SELECT p.*, u.employee_code, u.full_name, u.employee_code as employee_id,
                   d.name as department_name, pos.name as position_name
            FROM payslips p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN positions pos ON u.position_id = pos.id
            WHERE 1=1
            """);

        List<Object> parameters = new ArrayList<>();

        // Apply filters - Use overlap logic instead of exact match
        if (filter.hasPeriodFilter()) {
            sql.append(" AND p.period_start <= ? AND p.period_end >= ?");
            parameters.add(Date.valueOf(filter.getPeriodEnd()));
            parameters.add(Date.valueOf(filter.getPeriodStart()));
        }

        if (filter.hasUserFilter()) {
            sql.append(" AND p.user_id = ?");
            parameters.add(filter.getUserId());
        }

        // Employee search filter - search by code or name
        if (filter.getEmployeeSearch() != null && !filter.getEmployeeSearch().trim().isEmpty()) {
            sql.append(" AND (u.employee_code LIKE ? OR u.full_name LIKE ?)");
            String searchPattern = "%" + filter.getEmployeeSearch().trim() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        if (filter.hasUserScopeFilter()) {
            String placeholders = String.join(",", filter.getUserIds().stream().map(id -> "?").toArray(String[]::new));
            sql.append(" AND p.user_id IN (").append(placeholders).append(")");
            parameters.addAll(filter.getUserIds());
        }

        if (filter.hasDepartmentFilter()) {
            sql.append(" AND u.department_id = ?");
            parameters.add(filter.getDepartmentId());
        }

        if (filter.hasStatusFilter()) {
            sql.append(" AND p.status = ?");
            parameters.add(filter.getStatus());
        }

        if (filter.hasDirtyFilter()) {
            sql.append(" AND p.is_dirty = 1");
        }

        if (filter.hasNotGeneratedFilter()) {
            sql.append(" AND p.generated_at IS NULL");
        }

        // Add ordering
        sql.append(" ORDER BY p.period_start DESC, p.updated_at DESC");

        // Add pagination
        if (pagination != null && pagination.getPageSize() > 0) {
            int offset = (pagination.getCurrentPage() - 1) * pagination.getPageSize();
            sql.append(" LIMIT ? OFFSET ?");
            parameters.add(pagination.getPageSize());
            parameters.add(offset);
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Debug logging
            logger.debug("PayslipDao.findWithFilters SQL: {}", sql.toString());
            logger.debug("PayslipDao.findWithFilters Parameters: {}", parameters);

            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                } else if (param instanceof Long) {
                    stmt.setLong(i + 1, (Long) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else {
                    stmt.setString(i + 1, param.toString());
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PayslipDto dto = mapResultSetToDto(rs);
                    payslips.add(dto);
                }
            }

            logger.debug("PayslipDao.findWithFilters found {} payslips", payslips.size());
        }

        return payslips;
    }

    /**
     * Count payslips with filters for pagination
     * Requirements: 1.2, 2.2, 2.4
     */
    public long countWithFilters(PayslipFilter filter) throws SQLException {
        if (filter == null) {
            return 0;
        }

        // Build dynamic count query
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT p.id)
            FROM payslips p
            LEFT JOIN users u ON p.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            WHERE 1=1
            """);

        List<Object> parameters = new ArrayList<>();

        // Apply same filters as findWithFilters
        if (filter.hasPeriodFilter()) {
            sql.append(" AND p.period_start = ? AND p.period_end = ?");
            parameters.add(Date.valueOf(filter.getPeriodStart()));
            parameters.add(Date.valueOf(filter.getPeriodEnd()));
        }

        if (filter.hasUserFilter()) {
            sql.append(" AND p.user_id = ?");
            parameters.add(filter.getUserId());
        }

        // Employee search filter - search by code or name (for countWithFilters)
        if (filter.getEmployeeSearch() != null && !filter.getEmployeeSearch().trim().isEmpty()) {
            sql.append(" AND (u.employee_code LIKE ? OR u.full_name LIKE ?)");
            String searchPattern = "%" + filter.getEmployeeSearch().trim() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        if (filter.hasUserScopeFilter()) {
            String placeholders = String.join(",", filter.getUserIds().stream().map(id -> "?").toArray(String[]::new));
            sql.append(" AND p.user_id IN (").append(placeholders).append(")");
            parameters.addAll(filter.getUserIds());
        }

        if (filter.hasDepartmentFilter()) {
            sql.append(" AND u.department_id = ?");
            parameters.add(filter.getDepartmentId());
        }

        if (filter.hasStatusFilter()) {
            sql.append(" AND p.status = ?");
            parameters.add(filter.getStatus());
        }

        if (filter.hasDirtyFilter()) {
            sql.append(" AND p.is_dirty = 1");
        }

        if (filter.hasNotGeneratedFilter()) {
            sql.append(" AND p.generated_at IS NULL");
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                } else if (param instanceof Long) {
                    stmt.setLong(i + 1, (Long) param);
                } else {
                    stmt.setString(i + 1, param.toString());
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    /**
     * Map ResultSet to PayslipDto with user information
     * Requirements: 1.2, 2.2, 2.4
     */
    private PayslipDto mapResultSetToDto(ResultSet rs) throws SQLException {
        PayslipDto dto = new PayslipDto();

        // Map payslip fields
        dto.setId(rs.getLong("id"));
        dto.setUserId(rs.getLong("user_id"));
        dto.setPeriodStart(rs.getDate("period_start") != null ? rs.getDate("period_start").toLocalDate() : null);
        dto.setPeriodEnd(rs.getDate("period_end") != null ? rs.getDate("period_end").toLocalDate() : null);
        dto.setCurrency(rs.getString("currency"));
        dto.setGrossAmount(rs.getBigDecimal("gross_amount"));
        dto.setNetAmount(rs.getBigDecimal("net_amount"));
        dto.setDetailsJson(rs.getString("details_json"));
        dto.setFilePath(rs.getString("file_path"));
        dto.setStatus(rs.getString("status"));
        dto.setCreatedAt(getLocalDateTime(rs, "created_at"));

        // Map new fields
        dto.setBaseSalary(rs.getBigDecimal("base_salary"));
        dto.setOtAmount(rs.getBigDecimal("ot_amount"));
        dto.setLatenessDeduction(rs.getBigDecimal("lateness_deduction"));
        dto.setUnderHoursDeduction(rs.getBigDecimal("under_hours_deduction"));
        dto.setTaxAmount(rs.getBigDecimal("tax_amount"));
        dto.setIsDirty(rs.getBoolean("is_dirty"));
        dto.setDirtyReason(rs.getString("dirty_reason"));
        dto.setUpdatedAt(getLocalDateTime(rs, "updated_at"));
        dto.setGeneratedAt(getLocalDateTime(rs, "generated_at"));

        // Map user information
        dto.setUserName(rs.getString("employee_code"));
        dto.setUserFullName(rs.getString("full_name"));
        dto.setUserEmployeeId(rs.getString("employee_id"));
        dto.setDepartmentName(rs.getString("department_name"));
        dto.setPositionName(rs.getString("position_name"));

        return dto;
    }

    /**
     * Bulk mark multiple payslips as dirty by user IDs
     * Requirements: 9.1, 9.4, 9.7, 9.8
     */
    public int bulkMarkDirtyByUserIds(List<Long> userIds, LocalDate periodStart, LocalDate periodEnd, String reason) throws SQLException {
        if (userIds == null || userIds.isEmpty() || periodStart == null || periodEnd == null) {
            return 0;
        }

        // Process in batches to optimize connection pooling
        final int BATCH_SIZE = 1000;
        int totalUpdated = 0;

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
                    int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
                    List<Long> batch = userIds.subList(i, endIndex);

                    String placeholders = String.join(",", batch.stream().map(id -> "?").toArray(String[]::new));
                    String sql = "UPDATE payslips SET is_dirty = 1, dirty_reason = ?, updated_at = ? " +
                                "WHERE user_id IN (" + placeholders + ") AND period_start = ? AND period_end = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, reason);
                        setTimestamp(stmt, 2, LocalDateTime.now());

                        int paramIndex = 3;
                        for (Long userId : batch) {
                            stmt.setLong(paramIndex++, userId);
                        }
                        stmt.setDate(paramIndex++, Date.valueOf(periodStart));
                        stmt.setDate(paramIndex++, Date.valueOf(periodEnd));

                        int batchUpdated = stmt.executeUpdate();
                        totalUpdated += batchUpdated;
                    }
                }

                conn.commit(); // Commit transaction
                logger.info("Successfully bulk marked {} payslips as dirty for period {}-{}: {}",
                           totalUpdated, periodStart, periodEnd, reason);
                return totalUpdated;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                logger.error("Failed to bulk mark payslips dirty, transaction rolled back", e);
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restore auto-commit
            }
        }
    }

    /**
     * Enhanced findDirtyPayslips method with filtering support
     * Requirements: 9.1, 9.4, 9.7, 9.8
     */
    public List<Payslip> findDirtyPayslips(LocalDate periodStart, LocalDate periodEnd, PayslipFilter filter) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT p.* FROM payslips p WHERE p.is_dirty = 1");
        List<Object> parameters = new ArrayList<>();

        // Add period filter
        if (periodStart != null && periodEnd != null) {
            sql.append(" AND p.period_start = ? AND p.period_end = ?");
            parameters.add(Date.valueOf(periodStart));
            parameters.add(Date.valueOf(periodEnd));
        }

        // Add additional filters if provided
        if (filter != null) {
            if (filter.hasUserFilter()) {
                sql.append(" AND p.user_id = ?");
                parameters.add(filter.getUserId());
            }

            if (filter.hasUserScopeFilter()) {
                String placeholders = String.join(",", filter.getUserIds().stream().map(id -> "?").toArray(String[]::new));
                sql.append(" AND p.user_id IN (").append(placeholders).append(")");
                parameters.addAll(filter.getUserIds());
            }

            if (filter.hasStatusFilter()) {
                sql.append(" AND p.status = ?");
                parameters.add(filter.getStatus());
            }
        }

        sql.append(" ORDER BY p.updated_at DESC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                } else if (param instanceof Long) {
                    stmt.setLong(i + 1, (Long) param);
                } else {
                    stmt.setString(i + 1, param.toString());
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }

        return payslips;
    }







    /**
     * Get payslips by IDs for bulk operations
     * Requirements: 9.1, 9.4, 9.7, 9.8
     */
    public List<Payslip> findByIds(List<Long> payslipIds) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();

        if (payslipIds == null || payslipIds.isEmpty()) {
            return payslips;
        }

        String placeholders = String.join(",", payslipIds.stream().map(id -> "?").toArray(String[]::new));
        String sql = "SELECT * FROM payslips WHERE id IN (" + placeholders + ") ORDER BY id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < payslipIds.size(); i++) {
                stmt.setLong(i + 1, payslipIds.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }

        return payslips;
    }

    /**
     * Bulk update generated timestamp for successful payslip generation
     * Requirements: 9.1, 9.4, 9.7, 9.8
     */
    public int bulkUpdateGeneratedTimestamp(List<Long> payslipIds, LocalDateTime generatedAt) throws SQLException {
        if (payslipIds == null || payslipIds.isEmpty()) {
            return 0;
        }

        // Process in batches for better performance
        final int BATCH_SIZE = 1000;
        int totalUpdated = 0;

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                for (int i = 0; i < payslipIds.size(); i += BATCH_SIZE) {
                    int endIndex = Math.min(i + BATCH_SIZE, payslipIds.size());
                    List<Long> batch = payslipIds.subList(i, endIndex);

                    String placeholders = String.join(",", batch.stream().map(id -> "?").toArray(String[]::new));
                    String sql = "UPDATE payslips SET generated_at = ?, is_dirty = 0, dirty_reason = NULL, updated_at = ? " +
                                "WHERE id IN (" + placeholders + ")";

                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        setTimestamp(stmt, 1, generatedAt != null ? generatedAt : LocalDateTime.now());
                        setTimestamp(stmt, 2, LocalDateTime.now());

                        for (int j = 0; j < batch.size(); j++) {
                            stmt.setLong(3 + j, batch.get(j));
                        }

                        int batchUpdated = stmt.executeUpdate();
                        totalUpdated += batchUpdated;
                    }
                }

                conn.commit(); // Commit transaction
                logger.info("Successfully updated generated timestamp for {} payslips", totalUpdated);
                return totalUpdated;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                logger.error("Failed to update generated timestamps, transaction rolled back", e);
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restore auto-commit
            }
        }
    }



    /**
     * Bulk delete payslips by IDs with transaction support
     * Requirements: 9.1, 9.4, 9.7, 9.8
     */
    public int bulkDeletePayslips(List<Long> payslipIds) throws SQLException {
        if (payslipIds == null || payslipIds.isEmpty()) {
            return 0;
        }

        // Process in batches for better performance
        final int BATCH_SIZE = 1000;
        int totalDeleted = 0;

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                for (int i = 0; i < payslipIds.size(); i += BATCH_SIZE) {
                    int endIndex = Math.min(i + BATCH_SIZE, payslipIds.size());
                    List<Long> batch = payslipIds.subList(i, endIndex);

                    String placeholders = String.join(",", batch.stream().map(id -> "?").toArray(String[]::new));
                    String sql = "DELETE FROM payslips WHERE id IN (" + placeholders + ")";

                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        for (int j = 0; j < batch.size(); j++) {
                            stmt.setLong(j + 1, batch.get(j));
                        }

                        int batchDeleted = stmt.executeUpdate();
                        totalDeleted += batchDeleted;
                    }
                }

                conn.commit(); // Commit transaction
                logger.info("Successfully deleted {} payslips", totalDeleted);
                return totalDeleted;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                logger.error("Failed to bulk delete payslips, transaction rolled back", e);
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restore auto-commit
            }
        }
    }
}