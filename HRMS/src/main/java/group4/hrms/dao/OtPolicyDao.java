package group4.hrms.dao;

import group4.hrms.model.OtPolicy;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng ot_policies
 * 
 * @author Group4
 */
public class OtPolicyDao extends BaseDao<OtPolicy, Long> {
    
    @Override
    protected String getTableName() {
        return "ot_policies";
    }
    
    @Override
    protected OtPolicy mapResultSetToEntity(ResultSet rs) throws SQLException {
        OtPolicy policy = new OtPolicy();
        policy.setId(rs.getLong("id"));
        policy.setCode(rs.getString("code"));
        policy.setName(rs.getString("name"));
        policy.setDescription(rs.getString("description"));
        policy.setRulesJson(rs.getString("rules_json"));
        policy.setAssignmentsJson(rs.getString("assignments_json"));
        
        Long updatedBy = rs.getLong("updated_by_account_id");
        if (!rs.wasNull()) {
            policy.setUpdatedByAccountId(updatedBy);
        }
        
        policy.setCreatedAt(getLocalDateTime(rs, "created_at"));
        policy.setUpdatedAt(getLocalDateTime(rs, "updated_at"));
        
        return policy;
    }
    
    @Override
    protected void setEntityId(OtPolicy policy, Long id) {
        policy.setId(id);
    }
    
    @Override
    protected Long getEntityId(OtPolicy policy) {
        return policy.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO ot_policies (code, name, description, rules_json, assignments_json, " +
               "updated_by_account_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE ot_policies SET code = ?, name = ?, description = ?, rules_json = ?, " +
               "assignments_json = ?, updated_by_account_id = ?, updated_at = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, OtPolicy policy) throws SQLException {
        stmt.setString(1, policy.getCode());
        stmt.setString(2, policy.getName());
        stmt.setString(3, policy.getDescription());
        stmt.setString(4, policy.getRulesJson());
        stmt.setString(5, policy.getAssignmentsJson());
        if (policy.getUpdatedByAccountId() != null) {
            stmt.setLong(6, policy.getUpdatedByAccountId());
        } else {
            stmt.setNull(6, Types.BIGINT);
        }
        setTimestamp(stmt, 7, policy.getCreatedAt() != null ? policy.getCreatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 8, policy.getUpdatedAt() != null ? policy.getUpdatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, OtPolicy policy) throws SQLException {
        stmt.setString(1, policy.getCode());
        stmt.setString(2, policy.getName());
        stmt.setString(3, policy.getDescription());
        stmt.setString(4, policy.getRulesJson());
        stmt.setString(5, policy.getAssignmentsJson());
        if (policy.getUpdatedByAccountId() != null) {
            stmt.setLong(6, policy.getUpdatedByAccountId());
        } else {
            stmt.setNull(6, Types.BIGINT);
        }
        setTimestamp(stmt, 7, LocalDateTime.now());
        stmt.setLong(8, policy.getId());
    }
    
    // Business methods
    
    /**
     * Tìm policy theo code
     */
    public Optional<OtPolicy> findByCode(String code) throws SQLException {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM ot_policies WHERE code = ?";
        
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
            logger.error("Error finding OT policy by code {}: {}", code, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm tất cả policies
     */
    public List<OtPolicy> findAllOrderByName() throws SQLException {
        List<OtPolicy> policies = new ArrayList<>();
        String sql = "SELECT * FROM ot_policies ORDER BY name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                policies.add(mapResultSetToEntity(rs));
            }
            
            return policies;
            
        } catch (SQLException e) {
            logger.error("Error finding all OT policies: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật rules JSON
     */
    public boolean updateRules(Long policyId, String rulesJson, Long updatedBy) throws SQLException {
        if (policyId == null) {
            return false;
        }
        
        String sql = "UPDATE ot_policies SET rules_json = ?, updated_by_account_id = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rulesJson);
            if (updatedBy != null) {
                stmt.setLong(2, updatedBy);
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            setTimestamp(stmt, 3, LocalDateTime.now());
            stmt.setLong(4, policyId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating OT policy rules {}: {}", policyId, e.getMessage(), e);
            throw e;
        }
    }
}