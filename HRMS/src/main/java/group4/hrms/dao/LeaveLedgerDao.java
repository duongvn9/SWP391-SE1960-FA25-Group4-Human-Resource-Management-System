package group4.hrms.dao;

import group4.hrms.model.LeaveLedger;
import group4.hrms.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class để xử lý các thao tác với bảng leave_ledger
 * 
 * @author Group4
 */
public class LeaveLedgerDao extends BaseDao<LeaveLedger, Long> {
    
    @Override
    protected String getTableName() {
        return "leave_ledger";
    }
    
    @Override
    protected LeaveLedger mapResultSetToEntity(ResultSet rs) throws SQLException {
        LeaveLedger ledger = new LeaveLedger();
        ledger.setId(rs.getLong("id"));
        ledger.setUserId(rs.getLong("user_id"));
        ledger.setLeaveTypeId(rs.getLong("leave_type_id"));
        
        Long requestId = rs.getLong("request_id");
        if (!rs.wasNull()) {
            ledger.setRequestId(requestId);
        }
        
        ledger.setDeltaDays(rs.getBigDecimal("delta_days"));
        ledger.setNote(rs.getString("note"));
        ledger.setCreatedAt(getLocalDateTime(rs, "created_at"));
        
        return ledger;
    }
    
    @Override
    protected void setEntityId(LeaveLedger ledger, Long id) {
        ledger.setId(id);
    }
    
    @Override
    protected Long getEntityId(LeaveLedger ledger) {
        return ledger.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO leave_ledger (user_id, leave_type_id, request_id, delta_days, note, created_at) " +
               "VALUES (?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE leave_ledger SET user_id = ?, leave_type_id = ?, request_id = ?, " +
               "delta_days = ?, note = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, LeaveLedger ledger) throws SQLException {
        stmt.setLong(1, ledger.getUserId());
        stmt.setLong(2, ledger.getLeaveTypeId());
        if (ledger.getRequestId() != null) {
            stmt.setLong(3, ledger.getRequestId());
        } else {
            stmt.setNull(3, Types.BIGINT);
        }
        stmt.setBigDecimal(4, ledger.getDeltaDays());
        stmt.setString(5, ledger.getNote());
        setTimestamp(stmt, 6, ledger.getCreatedAt() != null ? ledger.getCreatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, LeaveLedger ledger) throws SQLException {
        stmt.setLong(1, ledger.getUserId());
        stmt.setLong(2, ledger.getLeaveTypeId());
        if (ledger.getRequestId() != null) {
            stmt.setLong(3, ledger.getRequestId());
        } else {
            stmt.setNull(3, Types.BIGINT);
        }
        stmt.setBigDecimal(4, ledger.getDeltaDays());
        stmt.setString(5, ledger.getNote());
        stmt.setLong(6, ledger.getId());
    }
    
    // Business methods
    
    /**
     * Tìm ledger entries theo user ID
     */
    public List<LeaveLedger> findByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        List<LeaveLedger> entries = new ArrayList<>();
        String sql = "SELECT * FROM leave_ledger WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToEntity(rs));
                }
            }
            
            return entries;
            
        } catch (SQLException e) {
            logger.error("Error finding ledger by user ID {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm ledger entries theo user và leave type
     */
    public List<LeaveLedger> findByUserAndLeaveType(Long userId, Long leaveTypeId) throws SQLException {
        if (userId == null || leaveTypeId == null) {
            return new ArrayList<>();
        }
        
        List<LeaveLedger> entries = new ArrayList<>();
        String sql = "SELECT * FROM leave_ledger WHERE user_id = ? AND leave_type_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, leaveTypeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToEntity(rs));
                }
            }
            
            return entries;
            
        } catch (SQLException e) {
            logger.error("Error finding ledger by user {} and leave type {}: {}", userId, leaveTypeId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tính tổng delta days của user cho một leave type
     */
    public BigDecimal calculateTotalDeltaForUser(Long userId, Long leaveTypeId) throws SQLException {
        if (userId == null || leaveTypeId == null) {
            return BigDecimal.ZERO;
        }
        
        String sql = "SELECT SUM(delta_days) FROM leave_ledger WHERE user_id = ? AND leave_type_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, leaveTypeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
            
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            logger.error("Error calculating total delta for user {} and leave type {}: {}", userId, leaveTypeId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm ledger entries theo request ID
     */
    public List<LeaveLedger> findByRequestId(Long requestId) throws SQLException {
        if (requestId == null) {
            return new ArrayList<>();
        }
        
        List<LeaveLedger> entries = new ArrayList<>();
        String sql = "SELECT * FROM leave_ledger WHERE request_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, requestId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToEntity(rs));
                }
            }
            
            return entries;
            
        } catch (SQLException e) {
            logger.error("Error finding ledger by request ID {}: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }
}