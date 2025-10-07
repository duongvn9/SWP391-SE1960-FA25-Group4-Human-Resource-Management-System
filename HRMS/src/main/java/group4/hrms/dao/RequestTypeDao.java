package group4.hrms.dao;

import group4.hrms.model.RequestType;
import group4.hrms.dto.RequestTypeDto;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO cho RequestType - Loại yêu cầu/đơn từ
 */
public class RequestTypeDao extends BaseDao<RequestType, Long> {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestTypeDao.class);
    
    private static final String TABLE_NAME = "request_types";
    
    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }
    
    @Override
    protected void setEntityId(RequestType requestType, Long id) {
        requestType.setId(id);
    }
    
    @Override
    protected Long getEntityId(RequestType requestType) {
        return requestType.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO request_types (name, code, description, category, requires_approval, " +
               "requires_attachment, max_days, approval_workflow, is_active, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE request_types SET name = ?, code = ?, description = ?, category = ?, " +
               "requires_approval = ?, requires_attachment = ?, max_days = ?, approval_workflow = ?, " +
               "is_active = ?, updated_at = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, RequestType requestType) throws SQLException {
        stmt.setString(1, requestType.getName());
        stmt.setString(2, requestType.getCode());
        stmt.setString(3, requestType.getDescription());
        stmt.setString(4, requestType.getCategory());
        stmt.setBoolean(5, requestType.isRequiresApproval());
        stmt.setBoolean(6, requestType.isRequiresAttachment());
        
        if (requestType.getMaxDays() != null) {
            stmt.setInt(7, requestType.getMaxDays());
        } else {
            stmt.setNull(7, Types.INTEGER);
        }
        
        stmt.setString(8, requestType.getApprovalWorkflow());
        stmt.setBoolean(9, requestType.isActive());
        
        LocalDateTime now = LocalDateTime.now();
        setTimestamp(stmt, 10, now);
        setTimestamp(stmt, 11, now);
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, RequestType requestType) throws SQLException {
        stmt.setString(1, requestType.getName());
        stmt.setString(2, requestType.getCode());
        stmt.setString(3, requestType.getDescription());
        stmt.setString(4, requestType.getCategory());
        stmt.setBoolean(5, requestType.isRequiresApproval());
        stmt.setBoolean(6, requestType.isRequiresAttachment());
        
        if (requestType.getMaxDays() != null) {
            stmt.setInt(7, requestType.getMaxDays());
        } else {
            stmt.setNull(7, Types.INTEGER);
        }
        
        stmt.setString(8, requestType.getApprovalWorkflow());
        stmt.setBoolean(9, requestType.isActive());
        setTimestamp(stmt, 10, LocalDateTime.now());
        stmt.setLong(11, requestType.getId());
    }
    
    private static final String SELECT_ALL = 
        "SELECT id, name, code, description, category, requires_approval, requires_attachment, " +
        "max_days, approval_workflow, is_active, created_at, updated_at FROM " + TABLE_NAME;
    
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";
    
    private static final String SELECT_WITH_REQUEST_COUNT = 
        "SELECT rt.id, rt.name, rt.code, rt.description, rt.category, rt.requires_approval, " +
        "rt.requires_attachment, rt.max_days, rt.approval_workflow, rt.is_active, " +
        "rt.created_at, rt.updated_at, COUNT(r.id) as request_count " +
        "FROM " + TABLE_NAME + " rt " +
        "LEFT JOIN requests r ON rt.id = r.request_type_id " +
        "GROUP BY rt.id, rt.name, rt.code, rt.description, rt.category, rt.requires_approval, " +
        "rt.requires_attachment, rt.max_days, rt.approval_workflow, rt.is_active, " +
        "rt.created_at, rt.updated_at ";
    
    private static final String INSERT = 
        "INSERT INTO " + TABLE_NAME + " (name, code, description, category, requires_approval, " +
        "requires_attachment, max_days, approval_workflow, is_active, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE = 
        "UPDATE " + TABLE_NAME + " SET name = ?, code = ?, description = ?, category = ?, " +
        "requires_approval = ?, requires_attachment = ?, max_days = ?, approval_workflow = ?, " +
        "is_active = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
    
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM " + TABLE_NAME;
    
    @Override
    public Optional<RequestType> findById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding request type by id: {}", id, e);
            throw new RuntimeException("Error finding request type by id: " + id, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<RequestType> findAll() {
        return findAll(0, Integer.MAX_VALUE);
    }
    
    public List<RequestType> findAll(int offset, int limit) {
        List<RequestType> requestTypes = new ArrayList<>();
        String sql = SELECT_ALL + " ORDER BY name ASC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requestTypes.add(mapResultSetToEntity(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding all request types", e);
            throw new RuntimeException("Error finding all request types", e);
        }
        
        return requestTypes;
    }
    
    @Override
    public RequestType save(RequestType requestType) {
        if (requestType.getId() != null) {
            return update(requestType);
        } else {
            return insert(requestType);
        }
    }
    
    private RequestType insert(RequestType requestType) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            requestType.setCreatedAt(now);
            requestType.setUpdatedAt(now);
            
            stmt.setString(1, requestType.getName());
            stmt.setString(2, requestType.getCode());
            stmt.setString(3, requestType.getDescription());
            stmt.setString(4, requestType.getCategory());
            stmt.setBoolean(5, requestType.isRequiresApproval());
            stmt.setBoolean(6, requestType.isRequiresAttachment());
            stmt.setObject(7, requestType.getMaxDays());
            stmt.setString(8, requestType.getApprovalWorkflow());
            stmt.setBoolean(9, requestType.isActive());
            stmt.setTimestamp(10, Timestamp.valueOf(requestType.getCreatedAt()));
            stmt.setTimestamp(11, Timestamp.valueOf(requestType.getUpdatedAt()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        requestType.setId(generatedKeys.getLong(1));
                        logger.info("Request type created successfully with id: {}", requestType.getId());
                        return requestType;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error creating request type", e);
            throw new RuntimeException("Error creating request type", e);
        }
        
        throw new RuntimeException("Failed to create request type");
    }
    
    public RequestType update(RequestType requestType) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            
            requestType.setUpdatedAt(LocalDateTime.now());
            
            stmt.setString(1, requestType.getName());
            stmt.setString(2, requestType.getCode());
            stmt.setString(3, requestType.getDescription());
            stmt.setString(4, requestType.getCategory());
            stmt.setBoolean(5, requestType.isRequiresApproval());
            stmt.setBoolean(6, requestType.isRequiresAttachment());
            stmt.setObject(7, requestType.getMaxDays());
            stmt.setString(8, requestType.getApprovalWorkflow());
            stmt.setBoolean(9, requestType.isActive());
            stmt.setTimestamp(10, Timestamp.valueOf(requestType.getUpdatedAt()));
            stmt.setLong(11, requestType.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Request type updated successfully: {}", requestType.getId());
                return requestType;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating request type: {}", requestType.getId(), e);
            throw new RuntimeException("Error updating request type: " + requestType.getId(), e);
        }
        
        throw new RuntimeException("Failed to update request type: " + requestType.getId());
    }
    
    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {
            
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Request type deleted successfully: {}", id);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error deleting request type: {}", id, e);
            throw new RuntimeException("Error deleting request type: " + id, e);
        }
        
        return false;
    }
    
    @Override
    public long count() {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
        } catch (SQLException e) {
            logger.error("Error counting request types", e);
            throw new RuntimeException("Error counting request types", e);
        }
        
        return 0;
    }
    
    /**
     * Tìm request types theo category
     */
    public List<RequestType> findByCategory(String category) {
        List<RequestType> requestTypes = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE category = ? AND is_active = true ORDER BY name ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requestTypes.add(mapResultSetToEntity(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding request types by category: {}", category, e);
            throw new RuntimeException("Error finding request types by category: " + category, e);
        }
        
        return requestTypes;
    }
    
    /**
     * Tìm request types đang hoạt động
     */
    public List<RequestType> findActiveTypes() {
        List<RequestType> requestTypes = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE is_active = true ORDER BY name ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requestTypes.add(mapResultSetToEntity(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding active request types", e);
            throw new RuntimeException("Error finding active request types", e);
        }
        
        return requestTypes;
    }
    
    /**
     * Tìm request type theo code
     */
    public RequestType findByCode(String code) {
        String sql = SELECT_ALL + " WHERE code = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding request type by code: {}", code, e);
            throw new RuntimeException("Error finding request type by code: " + code, e);
        }
        
        return null;
    }
    
    /**
     * Tìm request types với số lượng request
     */
    public List<RequestTypeDto> findAllWithRequestCount() {
        List<RequestTypeDto> requestTypes = new ArrayList<>();
        String sql = SELECT_WITH_REQUEST_COUNT + " ORDER BY rt.name ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RequestTypeDto dto = new RequestTypeDto(mapResultSetToEntity(rs));
                    dto.setRequestCount(rs.getInt("request_count"));
                    requestTypes.add(dto);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding request types with request count", e);
            throw new RuntimeException("Error finding request types with request count", e);
        }
        
        return requestTypes;
    }
    
    @Override
    protected RequestType mapResultSetToEntity(ResultSet rs) throws SQLException {
        RequestType requestType = new RequestType();
        
        requestType.setId(rs.getLong("id"));
        requestType.setName(rs.getString("name"));
        requestType.setCode(rs.getString("code"));
        requestType.setDescription(rs.getString("description"));
        requestType.setCategory(rs.getString("category"));
        requestType.setRequiresApproval(rs.getBoolean("requires_approval"));
        requestType.setRequiresAttachment(rs.getBoolean("requires_attachment"));
        requestType.setMaxDays((Integer) rs.getObject("max_days"));
        requestType.setApprovalWorkflow(rs.getString("approval_workflow"));
        requestType.setActive(rs.getBoolean("is_active"));
        requestType.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        requestType.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        return requestType;
    }
}