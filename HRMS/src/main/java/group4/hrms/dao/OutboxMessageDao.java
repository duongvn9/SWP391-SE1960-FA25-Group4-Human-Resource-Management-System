package group4.hrms.dao;

import group4.hrms.model.OutboxMessage;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class để xử lý các thao tác với bảng outbox_messages
 * 
 * @author Group4
 */
public class OutboxMessageDao extends BaseDao<OutboxMessage, Long> {
    
    @Override
    protected String getTableName() {
        return "outbox_messages";
    }
    
    @Override
    protected OutboxMessage mapResultSetToEntity(ResultSet rs) throws SQLException {
        OutboxMessage message = new OutboxMessage();
        message.setId(rs.getLong("id"));
        message.setTopic(rs.getString("topic"));
        message.setPayloadJson(rs.getString("payload_json"));
        message.setHeadersJson(rs.getString("headers_json"));
        message.setStatus(rs.getString("status"));
        message.setCreatedAt(getLocalDateTime(rs, "created_at"));
        message.setSentAt(getLocalDateTime(rs, "sent_at"));
        
        return message;
    }
    
    @Override
    protected void setEntityId(OutboxMessage message, Long id) {
        message.setId(id);
    }
    
    @Override
    protected Long getEntityId(OutboxMessage message) {
        return message.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO outbox_messages (topic, payload_json, headers_json, status, created_at, sent_at) " +
               "VALUES (?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE outbox_messages SET topic = ?, payload_json = ?, headers_json = ?, status = ?, sent_at = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, OutboxMessage message) throws SQLException {
        stmt.setString(1, message.getTopic());
        stmt.setString(2, message.getPayloadJson());
        stmt.setString(3, message.getHeadersJson());
        stmt.setString(4, message.getStatus());
        setTimestamp(stmt, 5, message.getCreatedAt() != null ? message.getCreatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 6, message.getSentAt());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, OutboxMessage message) throws SQLException {
        stmt.setString(1, message.getTopic());
        stmt.setString(2, message.getPayloadJson());
        stmt.setString(3, message.getHeadersJson());
        stmt.setString(4, message.getStatus());
        setTimestamp(stmt, 5, message.getSentAt());
        stmt.setLong(6, message.getId());
    }
    
    // Business methods
    
    /**
     * Tìm messages theo status
     */
    public List<OutboxMessage> findByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<OutboxMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM outbox_messages WHERE status = ? ORDER BY created_at";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToEntity(rs));
                }
            }
            
            return messages;
            
        } catch (SQLException e) {
            logger.error("Error finding outbox messages by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm new messages cần gửi
     */
    public List<OutboxMessage> findNewMessages() throws SQLException {
        return findByStatus("NEW");
    }
    
    /**
     * Mark message as sent
     */
    public boolean markAsSent(Long messageId) throws SQLException {
        if (messageId == null) {
            return false;
        }
        
        String sql = "UPDATE outbox_messages SET status = 'SENT', sent_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setTimestamp(stmt, 1, LocalDateTime.now());
            stmt.setLong(2, messageId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error marking message as sent {}: {}", messageId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Mark message as failed
     */
    public boolean markAsFailed(Long messageId) throws SQLException {
        if (messageId == null) {
            return false;
        }
        
        String sql = "UPDATE outbox_messages SET status = 'FAILED' WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, messageId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error marking message as failed {}: {}", messageId, e.getMessage(), e);
            throw e;
        }
    }
}