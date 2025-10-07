package group4.hrms.dao;

import group4.hrms.model.EmploymentContract;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng employment_contracts
 * 
 * @author Group4
 */
public class EmploymentContractDao extends BaseDao<EmploymentContract, Long> {
    
    @Override
    protected String getTableName() {
        return "employment_contracts";
    }
    
    @Override
    protected EmploymentContract mapResultSetToEntity(ResultSet rs) throws SQLException {
        EmploymentContract contract = new EmploymentContract();
        contract.setId(rs.getLong("id"));
        contract.setUserId(rs.getLong("user_id"));
        contract.setContractNo(rs.getString("contract_no"));
        contract.setContractType(rs.getString("contract_type"));
        contract.setStartDate(getLocalDate(rs, "start_date"));
        contract.setEndDate(getLocalDate(rs, "end_date"));
        contract.setBaseSalary(rs.getBigDecimal("base_salary"));
        contract.setCurrency(rs.getString("currency"));
        contract.setStatus(rs.getString("status"));
        contract.setFilePath(rs.getString("file_path"));
        contract.setNote(rs.getString("note"));
        
        Long createdBy = rs.getLong("created_by_account_id");
        if (!rs.wasNull()) {
            contract.setCreatedByAccountId(createdBy);
        }
        
        contract.setCreatedAt(getLocalDateTime(rs, "created_at"));
        contract.setUpdatedAt(getLocalDateTime(rs, "updated_at"));
        
        return contract;
    }
    
    @Override
    protected void setEntityId(EmploymentContract contract, Long id) {
        contract.setId(id);
    }
    
    @Override
    protected Long getEntityId(EmploymentContract contract) {
        return contract.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO employment_contracts (user_id, contract_no, contract_type, start_date, end_date, " +
               "base_salary, currency, status, file_path, note, created_by_account_id, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE employment_contracts SET user_id = ?, contract_no = ?, contract_type = ?, " +
               "start_date = ?, end_date = ?, base_salary = ?, currency = ?, status = ?, " +
               "file_path = ?, note = ?, created_by_account_id = ?, updated_at = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, EmploymentContract contract) throws SQLException {
        stmt.setLong(1, contract.getUserId());
        stmt.setString(2, contract.getContractNo());
        stmt.setString(3, contract.getContractType());
        setDate(stmt, 4, contract.getStartDate());
        setDate(stmt, 5, contract.getEndDate());
        stmt.setBigDecimal(6, contract.getBaseSalary());
        stmt.setString(7, contract.getCurrency());
        stmt.setString(8, contract.getStatus());
        stmt.setString(9, contract.getFilePath());
        stmt.setString(10, contract.getNote());
        if (contract.getCreatedByAccountId() != null) {
            stmt.setLong(11, contract.getCreatedByAccountId());
        } else {
            stmt.setNull(11, Types.BIGINT);
        }
        setTimestamp(stmt, 12, contract.getCreatedAt() != null ? contract.getCreatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 13, contract.getUpdatedAt() != null ? contract.getUpdatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, EmploymentContract contract) throws SQLException {
        stmt.setLong(1, contract.getUserId());
        stmt.setString(2, contract.getContractNo());
        stmt.setString(3, contract.getContractType());
        setDate(stmt, 4, contract.getStartDate());
        setDate(stmt, 5, contract.getEndDate());
        stmt.setBigDecimal(6, contract.getBaseSalary());
        stmt.setString(7, contract.getCurrency());
        stmt.setString(8, contract.getStatus());
        stmt.setString(9, contract.getFilePath());
        stmt.setString(10, contract.getNote());
        if (contract.getCreatedByAccountId() != null) {
            stmt.setLong(11, contract.getCreatedByAccountId());
        } else {
            stmt.setNull(11, Types.BIGINT);
        }
        setTimestamp(stmt, 12, LocalDateTime.now());
        stmt.setLong(13, contract.getId());
    }
    
    // Business methods
    
    /**
     * Tìm contracts theo user ID
     */
    public List<EmploymentContract> findByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        List<EmploymentContract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM employment_contracts WHERE user_id = ? ORDER BY start_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapResultSetToEntity(rs));
                }
            }
            
            return contracts;
            
        } catch (SQLException e) {
            logger.error("Error finding contracts by user ID {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm active contract của user
     */
    public Optional<EmploymentContract> findActiveContractByUser(Long userId) throws SQLException {
        if (userId == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM employment_contracts WHERE user_id = ? AND status = 'active' " +
                    "ORDER BY start_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding active contract by user ID {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm contracts theo status
     */
    public List<EmploymentContract> findByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<EmploymentContract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM employment_contracts WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapResultSetToEntity(rs));
                }
            }
            
            return contracts;
            
        } catch (SQLException e) {
            logger.error("Error finding contracts by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm contract theo contract number
     */
    public Optional<EmploymentContract> findByContractNo(String contractNo) throws SQLException {
        if (contractNo == null || contractNo.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM employment_contracts WHERE contract_no = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, contractNo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding contract by contract number {}: {}", contractNo, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật status của contract
     */
    public boolean updateStatus(Long contractId, String newStatus) throws SQLException {
        if (contractId == null || newStatus == null) {
            return false;
        }
        
        String sql = "UPDATE employment_contracts SET status = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, contractId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating contract status {}: {}", contractId, e.getMessage(), e);
            throw e;
        }
    }
}