package group4.hrms.dao;

import group4.hrms.model.Features;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng features
 * 
 * @author Group4
 */
public class FeaturesDao extends BaseDao<Features, Long> {
    
    @Override
    protected String getTableName() {
        return "features";
    }
    
    @Override
    protected Features mapResultSetToEntity(ResultSet rs) throws SQLException {
        Features feature = new Features();
        feature.setId(rs.getLong("id"));
        feature.setCode(rs.getString("code"));
        feature.setName(rs.getString("name"));
        feature.setDescription(rs.getString("description"));
        feature.setRoute(rs.getString("route"));
        feature.setSortOrder(rs.getInt("sort_order"));
        feature.setIsActive(rs.getBoolean("is_active"));
        feature.setCreatedAt(getLocalDateTime(rs, "created_at"));
        feature.setUpdatedAt(getLocalDateTime(rs, "updated_at"));
        
        return feature;
    }
    
    @Override
    protected void setEntityId(Features feature, Long id) {
        feature.setId(id);
    }
    
    @Override
    protected Long getEntityId(Features feature) {
        return feature.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO features (code, name, description, route, sort_order, is_active, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE features SET code = ?, name = ?, description = ?, route = ?, sort_order = ?, " +
               "is_active = ?, updated_at = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Features feature) throws SQLException {
        stmt.setString(1, feature.getCode());
        stmt.setString(2, feature.getName());
        stmt.setString(3, feature.getDescription());
        stmt.setString(4, feature.getRoute());
        Integer sortOrder = feature.getSortOrder();
        stmt.setInt(5, sortOrder != null ? sortOrder : 0);
        Boolean isActive = feature.getIsActive();
        stmt.setBoolean(6, isActive != null && isActive);
        setTimestamp(stmt, 7, feature.getCreatedAt() != null ? feature.getCreatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 8, feature.getUpdatedAt() != null ? feature.getUpdatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Features feature) throws SQLException {
        stmt.setString(1, feature.getCode());
        stmt.setString(2, feature.getName());
        stmt.setString(3, feature.getDescription());
        stmt.setString(4, feature.getRoute());
        Integer sortOrder = feature.getSortOrder();
        stmt.setInt(5, sortOrder != null ? sortOrder : 0);
        Boolean isActive = feature.getIsActive();
        stmt.setBoolean(6, isActive != null && isActive);
        setTimestamp(stmt, 7, LocalDateTime.now());
        stmt.setLong(8, feature.getId());
    }
    
    // Business methods
    
    /**
     * Tìm feature theo code
     */
    public Optional<Features> findByCode(String code) throws SQLException {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM features WHERE code = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding feature by code {}: {}", code, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm active features
     */
    public List<Features> findActiveFeatures() throws SQLException {
        List<Features> features = new ArrayList<>();
        String sql = "SELECT * FROM features WHERE is_active = true ORDER BY sort_order, name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                features.add(mapResultSetToEntity(rs));
            }
            
            return features;
            
        } catch (SQLException e) {
            logger.error("Error finding active features: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật trạng thái active
     */
    public boolean updateActiveStatus(Long featureId, boolean isActive) throws SQLException {
        if (featureId == null) {
            return false;
        }
        
        String sql = "UPDATE features SET is_active = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, isActive);
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, featureId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating feature active status {}: {}", featureId, e.getMessage(), e);
            throw e;
        }
    }
}