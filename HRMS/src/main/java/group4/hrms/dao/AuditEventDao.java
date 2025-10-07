package group4.hrms.dao;

import group4.hrms.model.AuditEvent;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class để xử lý các thao tác với bảng audit_events
 * 
 * @author Group4
 */
public class AuditEventDao extends BaseDao<AuditEvent, Long> {
    
    @Override
    protected String getTableName() {
        return "audit_events";
    }
    
    @Override
    protected AuditEvent mapResultSetToEntity(ResultSet rs) throws SQLException {
        AuditEvent event = new AuditEvent();
        event.setId(rs.getLong("id"));
        
        Long accountId = rs.getLong("account_id");
        if (!rs.wasNull()) {
            event.setAccountId(accountId);
        }
        
        event.setEventType(rs.getString("event_type"));
        event.setEntityType(rs.getString("entity_type"));
        
        Long entityId = rs.getLong("entity_id");
        if (!rs.wasNull()) {
            event.setEntityId(entityId);
        }
        
        event.setIp(rs.getString("ip"));
        event.setUserAgent(rs.getString("user_agent"));
        event.setCreatedAt(getLocalDateTime(rs, "created_at"));
        
        return event;
    }
    
    @Override
    protected void setEntityId(AuditEvent event, Long id) {
        event.setId(id);
    }
    
    @Override
    protected Long getEntityId(AuditEvent event) {
        return event.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO audit_events (account_id, event_type, entity_type, entity_id, ip, user_agent, created_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE audit_events SET account_id = ?, event_type = ?, entity_type = ?, entity_id = ?, " +
               "ip = ?, user_agent = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, AuditEvent event) throws SQLException {
        if (event.getAccountId() != null) {
            stmt.setLong(1, event.getAccountId());
        } else {
            stmt.setNull(1, Types.BIGINT);
        }
        stmt.setString(2, event.getEventType());
        stmt.setString(3, event.getEntityType());
        if (event.getEntityId() != null) {
            stmt.setLong(4, event.getEntityId());
        } else {
            stmt.setNull(4, Types.BIGINT);
        }
        stmt.setString(5, event.getIp());
        stmt.setString(6, event.getUserAgent());
        setTimestamp(stmt, 7, event.getCreatedAt() != null ? event.getCreatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, AuditEvent event) throws SQLException {
        if (event.getAccountId() != null) {
            stmt.setLong(1, event.getAccountId());
        } else {
            stmt.setNull(1, Types.BIGINT);
        }
        stmt.setString(2, event.getEventType());
        stmt.setString(3, event.getEntityType());
        if (event.getEntityId() != null) {
            stmt.setLong(4, event.getEntityId());
        } else {
            stmt.setNull(4, Types.BIGINT);
        }
        stmt.setString(5, event.getIp());
        stmt.setString(6, event.getUserAgent());
        stmt.setLong(7, event.getId());
    }
    
    // Business methods
    
    /**
     * Tìm audit events theo account ID
     */
    public List<AuditEvent> findByAccountId(Long accountId) throws SQLException {
        if (accountId == null) {
            return new ArrayList<>();
        }
        
        List<AuditEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM audit_events WHERE account_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEntity(rs));
                }
            }
            
            return events;
            
        } catch (SQLException e) {
            logger.error("Error finding audit events by account ID {}: {}", accountId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm audit events theo event type
     */
    public List<AuditEvent> findByEventType(String eventType) throws SQLException {
        if (eventType == null || eventType.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<AuditEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM audit_events WHERE event_type = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, eventType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEntity(rs));
                }
            }
            
            return events;
            
        } catch (SQLException e) {
            logger.error("Error finding audit events by event type {}: {}", eventType, e.getMessage(), e);
            throw e;
        }
    }
}