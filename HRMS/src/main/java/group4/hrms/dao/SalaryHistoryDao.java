package group4.hrms.dao;

import group4.hrms.model.SalaryHistory;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng salary_history
 * 
 * @author Group4
 */
public class SalaryHistoryDao extends BaseDao<SalaryHistory, Long> {
    
    @Override
    protected String getTableName() {
        return "salary_history";
    }
    
    @Override
    protected SalaryHistory mapResultSetToEntity(ResultSet rs) throws SQLException {
        SalaryHistory history = new SalaryHistory();
        history.setId(rs.getLong("id"));
        history.setUserId(rs.getLong("user_id"));
        history.setAmount(rs.getBigDecimal("amount"));
        history.setCurrency(rs.getString("currency"));
        history.setEffectiveFrom(getLocalDate(rs, "effective_from"));
        history.setEffectiveTo(getLocalDate(rs, "effective_to"));
        history.setReason(rs.getString("reason"));
        
        Long createdBy = rs.getLong("created_by_account_id");
        if (!rs.wasNull()) {
            history.setCreatedByAccountId(createdBy);
        }
        
        history.setCreatedAt(getLocalDateTime(rs, "created_at"));
        
        return history;
    }
    
    @Override
    protected void setEntityId(SalaryHistory history, Long id) {
        history.setId(id);
    }
    
    @Override
    protected Long getEntityId(SalaryHistory history) {
        return history.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO salary_history (user_id, amount, currency, effective_from, effective_to, " +
               "reason, created_by_account_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE salary_history SET user_id = ?, amount = ?, currency = ?, effective_from = ?, " +
               "effective_to = ?, reason = ?, created_by_account_id = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, SalaryHistory history) throws SQLException {
        stmt.setLong(1, history.getUserId());
        stmt.setBigDecimal(2, history.getAmount());
        stmt.setString(3, history.getCurrency());
        setDate(stmt, 4, history.getEffectiveFrom());
        setDate(stmt, 5, history.getEffectiveTo());
        stmt.setString(6, history.getReason());
        if (history.getCreatedByAccountId() != null) {
            stmt.setLong(7, history.getCreatedByAccountId());
        } else {
            stmt.setNull(7, Types.BIGINT);
        }
        setTimestamp(stmt, 8, history.getCreatedAt() != null ? history.getCreatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, SalaryHistory history) throws SQLException {
        stmt.setLong(1, history.getUserId());
        stmt.setBigDecimal(2, history.getAmount());
        stmt.setString(3, history.getCurrency());
        setDate(stmt, 4, history.getEffectiveFrom());
        setDate(stmt, 5, history.getEffectiveTo());
        stmt.setString(6, history.getReason());
        if (history.getCreatedByAccountId() != null) {
            stmt.setLong(7, history.getCreatedByAccountId());
        } else {
            stmt.setNull(7, Types.BIGINT);
        }
        stmt.setLong(8, history.getId());
    }
    
    // Business methods
    
    /**
     * Tìm salary history theo user ID
     */
    public List<SalaryHistory> findByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        List<SalaryHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM salary_history WHERE user_id = ? ORDER BY effective_from DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapResultSetToEntity(rs));
                }
            }
            
            return histories;
            
        } catch (SQLException e) {
            logger.error("Error finding salary history by user ID {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm current salary của user
     */
    public Optional<SalaryHistory> findCurrentSalary(Long userId) throws SQLException {
        if (userId == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM salary_history WHERE user_id = ? AND effective_from <= ? AND " +
                    "(effective_to IS NULL OR effective_to >= ?) ORDER BY effective_from DESC LIMIT 1";
        
        LocalDate today = LocalDate.now();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            setDate(stmt, 2, today);
            setDate(stmt, 3, today);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding current salary for user ID {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm salary history theo date range
     */
    public List<SalaryHistory> findByDateRange(Long userId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        if (userId == null || fromDate == null || toDate == null) {
            return new ArrayList<>();
        }
        
        List<SalaryHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM salary_history WHERE user_id = ? AND " +
                    "((effective_from <= ? AND (effective_to IS NULL OR effective_to >= ?)) OR " +
                    "(effective_from >= ? AND effective_from <= ?)) " +
                    "ORDER BY effective_from DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            setDate(stmt, 2, toDate);
            setDate(stmt, 3, fromDate);
            setDate(stmt, 4, fromDate);
            setDate(stmt, 5, toDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapResultSetToEntity(rs));
                }
            }
            
            return histories;
            
        } catch (SQLException e) {
            logger.error("Error finding salary history by date range for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * End current salary period và create new one
     */
    public boolean endCurrentSalaryAndCreateNew(Long userId, SalaryHistory newSalary) throws SQLException {
        if (userId == null || newSalary == null) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // End current salary period
            String endCurrentSql = "UPDATE salary_history SET effective_to = ? WHERE user_id = ? AND effective_to IS NULL";
            try (PreparedStatement stmt = conn.prepareStatement(endCurrentSql)) {
                setDate(stmt, 1, newSalary.getEffectiveFrom().minusDays(1));
                stmt.setLong(2, userId);
                stmt.executeUpdate();
            }
            
            // Create new salary
            newSalary.setUserId(userId);
            try (PreparedStatement stmt = conn.prepareStatement(createInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
                setInsertParameters(stmt, newSalary);
                stmt.executeUpdate();
                
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        newSalary.setId(keys.getLong(1));
                    }
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction: {}", ex.getMessage(), ex);
                }
            }
            logger.error("Error ending current salary and creating new: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection: {}", e.getMessage(), e);
                }
            }
        }
    }
}