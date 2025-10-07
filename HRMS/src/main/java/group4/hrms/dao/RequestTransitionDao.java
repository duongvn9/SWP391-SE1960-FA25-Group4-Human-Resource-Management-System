package group4.hrms.dao;

import group4.hrms.model.RequestTransition;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng RequestTransition
 * 
 * @author Group4
 */
public class RequestTransitionDao extends BaseDao<RequestTransition, Long> {
    
    @Override
    protected String getTableName() {
        return "request_transitions";
    }
    
    @Override
    protected RequestTransition mapResultSetToEntity(ResultSet rs) throws SQLException {
        RequestTransition transition = new RequestTransition();
        transition.setId(rs.getLong("id"));
        transition.setRequestId(rs.getLong("request_id"));
        transition.setFromStatus(rs.getString("from_status"));
        transition.setToStatus(rs.getString("to_status"));
        transition.setActionDate(getLocalDateTime(rs, "action_date"));
        transition.setActionBy(rs.getLong("action_by"));
        transition.setActionType(rs.getString("action_type"));
        transition.setComments(rs.getString("comments"));
        transition.setAttachmentPath(rs.getString("attachment_path"));
        transition.setIpAddress(rs.getString("ip_address"));
        transition.setCreatedAt(getLocalDateTime(rs, "created_at"));
        
        return transition;
    }
    
    @Override
    protected void setEntityId(RequestTransition transition, Long id) {
        transition.setId(id);
    }
    
    @Override
    protected Long getEntityId(RequestTransition transition) {
        return transition.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO request_transitions (request_id, from_status, to_status, " +
               "action_by, action_type, comments, attachment_path, action_date, ip_address, created_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE request_transitions SET request_id = ?, from_status = ?, to_status = ?, " +
               "action_by = ?, action_type = ?, comments = ?, attachment_path = ?, " +
               "action_date = ?, ip_address = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, RequestTransition transition) throws SQLException {
        stmt.setLong(1, transition.getRequestId());
        stmt.setString(2, transition.getFromStatus());
        stmt.setString(3, transition.getToStatus());
        stmt.setLong(4, transition.getActionBy());
        stmt.setString(5, transition.getActionType());
        stmt.setString(6, transition.getComments());
        stmt.setString(7, transition.getAttachmentPath());
        setTimestamp(stmt, 8, transition.getActionDate());
        stmt.setString(9, transition.getIpAddress());
        setTimestamp(stmt, 10, LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, RequestTransition transition) throws SQLException {
        stmt.setLong(1, transition.getRequestId());
        stmt.setString(2, transition.getFromStatus());
        stmt.setString(3, transition.getToStatus());
        stmt.setLong(4, transition.getActionBy());
        stmt.setString(5, transition.getActionType());
        stmt.setString(6, transition.getComments());
        stmt.setString(7, transition.getAttachmentPath());
        setTimestamp(stmt, 8, transition.getActionDate());
        stmt.setString(9, transition.getIpAddress());
        stmt.setLong(10, transition.getId());
    }
    
    // Business methods
    
    /**
     * Tìm tất cả transitions của một request
     */
    public List<RequestTransition> findByRequestId(Long requestId) throws SQLException {
        if (requestId == null) {
            return new ArrayList<>();
        }
        
        List<RequestTransition> transitions = new ArrayList<>();
        String sql = "SELECT * FROM request_transitions WHERE request_id = ? ORDER BY action_date ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, requestId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transitions.add(mapResultSetToEntity(rs));
                }
            }
            
            return transitions;
            
        } catch (SQLException e) {
            logger.error("Error finding transitions by request {}: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm transition cuối cùng của request
     */
    public Optional<RequestTransition> findLatestByRequestId(Long requestId) throws SQLException {
        if (requestId == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM request_transitions WHERE request_id = ? " +
                    "ORDER BY action_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, requestId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding latest transition by request {}: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm tất cả transitions của user trong khoảng thời gian
     */
    public List<RequestTransition> findByUserIdAndDateRange(Long userId, LocalDateTime fromDate, 
                                                            LocalDateTime toDate) throws SQLException {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        List<RequestTransition> transitions = new ArrayList<>();
        String sql = "SELECT * FROM request_transitions WHERE action_by = ? " +
                    "AND action_date BETWEEN ? AND ? ORDER BY action_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            setTimestamp(stmt, 2, fromDate);
            setTimestamp(stmt, 3, toDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transitions.add(mapResultSetToEntity(rs));
                }
            }
            
            return transitions;
            
        } catch (SQLException e) {
            logger.error("Error finding transitions by user {} in date range: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm tất cả transitions theo status
     */
    public List<RequestTransition> findByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<RequestTransition> transitions = new ArrayList<>();
        String sql = "SELECT * FROM request_transitions WHERE to_status = ? ORDER BY action_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transitions.add(mapResultSetToEntity(rs));
                }
            }
            
            return transitions;
            
        } catch (SQLException e) {
            logger.error("Error finding transitions by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm tất cả transitions trong khoảng thời gian
     */
    public List<RequestTransition> findByDateRange(LocalDateTime fromDate, LocalDateTime toDate) throws SQLException {
        List<RequestTransition> transitions = new ArrayList<>();
        String sql = "SELECT * FROM request_transitions WHERE action_date BETWEEN ? AND ? " +
                    "ORDER BY action_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setTimestamp(stmt, 1, fromDate);
            setTimestamp(stmt, 2, toDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transitions.add(mapResultSetToEntity(rs));
                }
            }
            
            return transitions;
            
        } catch (SQLException e) {
            logger.error("Error finding transitions by date range: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tạo transition mới
     */
    public RequestTransition createTransition(Long requestId, String fromStatus, String toStatus, 
                                            Long userId, String comments) throws SQLException {
        if (requestId == null || toStatus == null || userId == null) {
            throw new IllegalArgumentException("RequestId, toStatus and userId cannot be null");
        }
        
        RequestTransition transition = new RequestTransition();
        transition.setRequestId(requestId);
        transition.setFromStatus(fromStatus);
        transition.setToStatus(toStatus);
        transition.setActionDate(LocalDateTime.now());
        transition.setActionBy(userId);
        transition.setComments(comments);
        
        return save(transition);
    }
    
    /**
     * Lấy danh sách status changes của request
     */
    public List<String> getStatusHistory(Long requestId) throws SQLException {
        if (requestId == null) {
            return new ArrayList<>();
        }
        
        List<String> statusHistory = new ArrayList<>();
        String sql = "SELECT to_status FROM request_transitions WHERE request_id = ? " +
                    "ORDER BY action_date ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, requestId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statusHistory.add(rs.getString("to_status"));
                }
            }
            
            return statusHistory;
            
        } catch (SQLException e) {
            logger.error("Error getting status history for request {}: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm số transitions theo status trong khoảng thời gian
     */
    public long countByStatusAndDateRange(String status, LocalDateTime fromDate, LocalDateTime toDate) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM request_transitions WHERE to_status = ? " +
                    "AND action_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            setTimestamp(stmt, 2, fromDate);
            setTimestamp(stmt, 3, toDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting transitions by status {} and date range: {}", status, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm số transitions của user trong khoảng thời gian
     */
    public long countByUserIdAndDateRange(Long userId, LocalDateTime fromDate, LocalDateTime toDate) throws SQLException {
        if (userId == null) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM request_transitions WHERE action_by = ? " +
                    "AND action_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            setTimestamp(stmt, 2, fromDate);
            setTimestamp(stmt, 3, toDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting transitions by user {} and date range: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Kiểm tra xem request đã có transition nào chưa
     */
    public boolean hasTransitions(Long requestId) throws SQLException {
        if (requestId == null) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM request_transitions WHERE request_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, requestId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.error("Error checking transitions for request {}: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Xoá tất cả transitions của request
     */
    public boolean deleteByRequestId(Long requestId) throws SQLException {
        if (requestId == null) {
            return false;
        }
        
        String sql = "DELETE FROM request_transitions WHERE request_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, requestId);
            
            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;
            
            if (deleted) {
                logger.info("Deleted {} transitions for request {}", affectedRows, requestId);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Error deleting transitions for request {}: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Xoá tất cả transitions của user
     */
    public boolean deleteByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return false;
        }
        
        String sql = "DELETE FROM request_transitions WHERE action_by = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;
            
            if (deleted) {
                logger.info("Deleted {} transitions for user {}", affectedRows, userId);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Error deleting transitions for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
}