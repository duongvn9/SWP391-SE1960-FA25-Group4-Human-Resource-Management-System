package group4.hrms.dao;

import group4.hrms.model.SystemParameters;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng system_parameters
 * Mapping theo database schema mới
 * 
 * @author Group4
 */
public class SystemParametersDao extends BaseDao<SystemParameters, Long> {
    
    @Override
    protected String getTableName() {
        return "system_parameters";
    }
    
    @Override
    protected SystemParameters mapResultSetToEntity(ResultSet rs) throws SQLException {
        SystemParameters param = new SystemParameters();
        param.setId(rs.getLong("id"));
        param.setScopeType(rs.getString("scope_type"));
        param.setScopeId(rs.getLong("scope_id"));
        param.setNamespace(rs.getString("namespace"));
        param.setParamKey(rs.getString("param_key"));
        param.setValueJson(rs.getString("value_json"));
        param.setDescription(rs.getString("description"));
        param.setUpdatedByAccountId(rs.getLong("updated_by_account_id"));
        param.setUpdatedAt(getLocalDateTime(rs, "updated_at"));
        
        return param;
    }
    
    @Override
    protected void setEntityId(SystemParameters param, Long id) {
        param.setId(id);
    }
    
    @Override
    protected Long getEntityId(SystemParameters param) {
        return param.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO system_parameters (scope_type, scope_id, namespace, param_key, " +
               "value_json, description, updated_by_account_id, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE system_parameters SET scope_type = ?, scope_id = ?, namespace = ?, " +
               "param_key = ?, value_json = ?, description = ?, updated_by_account_id = ?, " +
               "updated_at = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, SystemParameters param) throws SQLException {
        stmt.setString(1, param.getScopeType());
        stmt.setObject(2, param.getScopeId(), Types.BIGINT);
        stmt.setString(3, param.getNamespace());
        stmt.setString(4, param.getParamKey());
        stmt.setString(5, param.getValueJson());
        stmt.setString(6, param.getDescription());
        stmt.setObject(7, param.getUpdatedByAccountId(), Types.BIGINT);
        setTimestamp(stmt, 8, param.getUpdatedAt() != null ? param.getUpdatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, SystemParameters param) throws SQLException {
        stmt.setString(1, param.getScopeType());
        stmt.setObject(2, param.getScopeId(), Types.BIGINT);
        stmt.setString(3, param.getNamespace());
        stmt.setString(4, param.getParamKey());
        stmt.setString(5, param.getValueJson());
        stmt.setString(6, param.getDescription());
        stmt.setObject(7, param.getUpdatedByAccountId(), Types.BIGINT);
        setTimestamp(stmt, 8, LocalDateTime.now());
        stmt.setLong(9, param.getId());
    }
    
    // Business methods
    
    /**
     * Tìm parameter theo scope, namespace và key
     */
    public Optional<SystemParameters> findByKey(String scopeType, Long scopeId, String namespace, String paramKey) throws SQLException {
        if (scopeType == null || namespace == null || paramKey == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM system_parameters WHERE scope_type = ? AND " +
                    "(scope_id = ? OR (scope_id IS NULL AND ? IS NULL)) AND " +
                    "namespace = ? AND param_key = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, scopeType);
            stmt.setObject(2, scopeId, Types.BIGINT);
            stmt.setObject(3, scopeId, Types.BIGINT);
            stmt.setString(4, namespace);
            stmt.setString(5, paramKey);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding parameter by key {}:{}:{}:{}: {}", scopeType, scopeId, namespace, paramKey, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm tất cả parameters theo namespace
     */
    public List<SystemParameters> findByNamespace(String namespace) throws SQLException {
        if (namespace == null || namespace.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<SystemParameters> parameters = new ArrayList<>();
        String sql = "SELECT * FROM system_parameters WHERE namespace = ? ORDER BY scope_type, scope_id, param_key";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, namespace);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    parameters.add(mapResultSetToEntity(rs));
                }
            }
            
            return parameters;
            
        } catch (SQLException e) {
            logger.error("Error finding parameters by namespace {}: {}", namespace, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm parameters theo scope
     */
    public List<SystemParameters> findByScope(String scopeType, Long scopeId) throws SQLException {
        if (scopeType == null || scopeType.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<SystemParameters> parameters = new ArrayList<>();
        String sql = "SELECT * FROM system_parameters WHERE scope_type = ? AND " +
                    "(scope_id = ? OR (scope_id IS NULL AND ? IS NULL)) " +
                    "ORDER BY namespace, param_key";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, scopeType);
            stmt.setObject(2, scopeId, Types.BIGINT);
            stmt.setObject(3, scopeId, Types.BIGINT);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    parameters.add(mapResultSetToEntity(rs));
                }
            }
            
            return parameters;
            
        } catch (SQLException e) {
            logger.error("Error finding parameters by scope {}:{}: {}", scopeType, scopeId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm global parameters
     */
    public List<SystemParameters> findGlobalParameters() throws SQLException {
        return findByScope("GLOBAL", null);
    }
    
    /**
     * Tìm parameters theo scope type và namespace
     */
    public List<SystemParameters> findByScopeAndNamespace(String scopeType, Long scopeId, String namespace) throws SQLException {
        if (scopeType == null || namespace == null) {
            return new ArrayList<>();
        }
        
        List<SystemParameters> parameters = new ArrayList<>();
        String sql = "SELECT * FROM system_parameters WHERE scope_type = ? AND " +
                    "(scope_id = ? OR (scope_id IS NULL AND ? IS NULL)) AND " +
                    "namespace = ? ORDER BY param_key";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, scopeType);
            stmt.setObject(2, scopeId, Types.BIGINT);
            stmt.setObject(3, scopeId, Types.BIGINT);
            stmt.setString(4, namespace);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    parameters.add(mapResultSetToEntity(rs));
                }
            }
            
            return parameters;
            
        } catch (SQLException e) {
            logger.error("Error finding parameters by scope and namespace {}:{}:{}: {}", scopeType, scopeId, namespace, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật value của parameter
     */
    public boolean updateValue(String scopeType, Long scopeId, String namespace, String paramKey, String valueJson, Long updatedBy) throws SQLException {
        if (scopeType == null || namespace == null || paramKey == null || valueJson == null) {
            return false;
        }
        
        String sql = "UPDATE system_parameters SET value_json = ?, updated_by_account_id = ?, updated_at = ? " +
                    "WHERE scope_type = ? AND " +
                    "(scope_id = ? OR (scope_id IS NULL AND ? IS NULL)) AND " +
                    "namespace = ? AND param_key = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, valueJson);
            stmt.setObject(2, updatedBy, Types.BIGINT);
            setTimestamp(stmt, 3, LocalDateTime.now());
            stmt.setString(4, scopeType);
            stmt.setObject(5, scopeId, Types.BIGINT);
            stmt.setObject(6, scopeId, Types.BIGINT);
            stmt.setString(7, namespace);
            stmt.setString(8, paramKey);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating parameter value {}:{}:{}:{}: {}", scopeType, scopeId, namespace, paramKey, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Xóa parameter
     */
    public boolean deleteParameter(String scopeType, Long scopeId, String namespace, String paramKey) throws SQLException {
        if (scopeType == null || namespace == null || paramKey == null) {
            return false;
        }
        
        String sql = "DELETE FROM system_parameters WHERE scope_type = ? AND " +
                    "(scope_id = ? OR (scope_id IS NULL AND ? IS NULL)) AND " +
                    "namespace = ? AND param_key = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, scopeType);
            stmt.setObject(2, scopeId, Types.BIGINT);
            stmt.setObject(3, scopeId, Types.BIGINT);
            stmt.setString(4, namespace);
            stmt.setString(5, paramKey);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error deleting parameter {}:{}:{}:{}: {}", scopeType, scopeId, namespace, paramKey, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm số parameters theo namespace
     */
    public long countByNamespace(String namespace) throws SQLException {
        if (namespace == null || namespace.trim().isEmpty()) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM system_parameters WHERE namespace = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, namespace);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting parameters by namespace {}: {}", namespace, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Lấy tất cả namespaces
     */
    public List<String> getAllNamespaces() throws SQLException {
        List<String> namespaces = new ArrayList<>();
        String sql = "SELECT DISTINCT namespace FROM system_parameters ORDER BY namespace";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                namespaces.add(rs.getString("namespace"));
            }
            
            return namespaces;
            
        } catch (SQLException e) {
            logger.error("Error getting all namespaces: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Lấy giá trị parameter dưới dạng String (convenience method)
     */
    public String getParameterValue(String scopeType, Long scopeId, String namespace, String paramKey) throws SQLException {
        Optional<SystemParameters> param = findByKey(scopeType, scopeId, namespace, paramKey);
        return param.map(SystemParameters::getValueJson).orElse(null);
    }
    
    /**
     * Lấy giá trị global parameter (convenience method)
     */
    public String getGlobalParameterValue(String namespace, String paramKey) throws SQLException {
        return getParameterValue("GLOBAL", null, namespace, paramKey);
    }
    
    /**
     * Kiểm tra parameter có tồn tại không
     */
    public boolean existsParameter(String scopeType, Long scopeId, String namespace, String paramKey) throws SQLException {
        return findByKey(scopeType, scopeId, namespace, paramKey).isPresent();
    }
}