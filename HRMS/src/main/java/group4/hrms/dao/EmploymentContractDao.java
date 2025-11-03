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
        contract.setApprovalStatus(rs.getString("approval_status"));
        contract.setFilePath(rs.getString("file_path"));
        contract.setNote(rs.getString("note"));
        
        Long createdBy = rs.getLong("created_by_account_id");
        if (!rs.wasNull()) {
            contract.setCreatedByAccountId(createdBy);
        }
        
        Long approvedBy = rs.getLong("approved_by_account_id");
        if (!rs.wasNull()) {
            contract.setApprovedByAccountId(approvedBy);
        }
        
        contract.setApprovedAt(getLocalDateTime(rs, "approved_at"));
        contract.setRejectedReason(rs.getString("rejected_reason"));
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
               "base_salary, currency, status, approval_status, file_path, note, created_by_account_id, approved_by_account_id, " +
               "approved_at, rejected_reason, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE employment_contracts SET user_id = ?, contract_no = ?, contract_type = ?, " +
               "start_date = ?, end_date = ?, base_salary = ?, currency = ?, status = ?, approval_status = ?, " +
               "file_path = ?, note = ?, created_by_account_id = ?, approved_by_account_id = ?, " +
               "approved_at = ?, rejected_reason = ?, updated_at = ? WHERE id = ?";
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
        stmt.setString(9, contract.getApprovalStatus());
        stmt.setString(10, contract.getFilePath());
        stmt.setString(11, contract.getNote());
        if (contract.getCreatedByAccountId() != null) {
            stmt.setLong(12, contract.getCreatedByAccountId());
        } else {
            stmt.setNull(12, Types.BIGINT);
        }
        if (contract.getApprovedByAccountId() != null) {
            stmt.setLong(13, contract.getApprovedByAccountId());
        } else {
            stmt.setNull(13, Types.BIGINT);
        }
        setTimestamp(stmt, 14, contract.getApprovedAt());
        stmt.setString(15, contract.getRejectedReason());
        setTimestamp(stmt, 16, contract.getCreatedAt() != null ? contract.getCreatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 17, contract.getUpdatedAt() != null ? contract.getUpdatedAt() : LocalDateTime.now());
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
        stmt.setString(9, contract.getApprovalStatus());
        stmt.setString(10, contract.getFilePath());
        stmt.setString(11, contract.getNote());
        if (contract.getCreatedByAccountId() != null) {
            stmt.setLong(12, contract.getCreatedByAccountId());
        } else {
            stmt.setNull(12, Types.BIGINT);
        }
        if (contract.getApprovedByAccountId() != null) {
            stmt.setLong(13, contract.getApprovedByAccountId());
        } else {
            stmt.setNull(13, Types.BIGINT);
        }
        setTimestamp(stmt, 14, contract.getApprovedAt());
        stmt.setString(15, contract.getRejectedReason());
        setTimestamp(stmt, 16, LocalDateTime.now());
        stmt.setLong(17, contract.getId());
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
    
    /**
     * Generate contract number theo format CONTRACT-YYYY-XXX
     */
    public String generateContractNo() throws SQLException {
        int currentYear = java.time.Year.now().getValue();
        String yearPrefix = "CONTRACT-" + currentYear + "-";
        
        String sql = "SELECT contract_no FROM employment_contracts " +
                    "WHERE contract_no LIKE ? " +
                    "ORDER BY contract_no DESC LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, yearPrefix + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String lastContractNo = rs.getString("contract_no");
                    // Extract number from CONTRACT-2025-001
                    String[] parts = lastContractNo.split("-");
                    if (parts.length == 3) {
                        int lastNumber = Integer.parseInt(parts[2]);
                        int nextNumber = lastNumber + 1;
                        return String.format("%s%03d", yearPrefix, nextNumber);
                    }
                }
            }
            
            // First contract of the year
            return yearPrefix + "001";
            
        } catch (SQLException e) {
            logger.error("Error generating contract number: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Lấy danh sách user IDs có active contract
     */
    public List<Long> findUserIdsWithActiveContract() throws SQLException {
        List<Long> userIds = new ArrayList<>();
        String sql = "SELECT DISTINCT user_id FROM employment_contracts WHERE status = 'active'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                userIds.add(rs.getLong("user_id"));
            }
            
            return userIds;
            
        } catch (SQLException e) {
            logger.error("Error finding user IDs with active contract: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Lấy danh sách user IDs có BẤT KỲ contract nào (bất kể status: draft, active, expired)
     * Dùng để lọc "Users Without Contract" - chỉ hiển thị users chưa từng có contract
     */
    public List<Long> findUserIdsWithAnyContract() throws SQLException {
        List<Long> userIds = new ArrayList<>();
        String sql = "SELECT DISTINCT user_id FROM employment_contracts";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                userIds.add(rs.getLong("user_id"));
            }
            
            return userIds;
            
        } catch (SQLException e) {
            logger.error("Error finding user IDs with any contract: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Approve contract (HRM only)
     * When approved, set approval_status = 'approved' and status = 'active'
     */
    public boolean approve(Long contractId, Long approverAccountId) throws SQLException {
        if (contractId == null || approverAccountId == null) {
            return false;
        }
        
        String sql = "UPDATE employment_contracts SET approval_status = 'approved', status = 'active', " +
                    "approved_by_account_id = ?, approved_at = ?, updated_at = ? WHERE id = ? AND approval_status = 'pending'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setLong(1, approverAccountId);
            setTimestamp(stmt, 2, now);
            setTimestamp(stmt, 3, now);
            stmt.setLong(4, contractId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Approved contract {} by account {}", contractId, approverAccountId);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.error("Error approving contract {}: {}", contractId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Reject contract with reason (HRM only)
     */
    public boolean reject(Long contractId, Long approverAccountId, String reason) throws SQLException {
        if (contractId == null || approverAccountId == null || reason == null) {
            return false;
        }
        
        String sql = "UPDATE employment_contracts SET approval_status = 'rejected', " +
                    "approved_by_account_id = ?, rejected_reason = ?, updated_at = ? WHERE id = ? AND approval_status = 'pending'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, approverAccountId);
            stmt.setString(2, reason);
            setTimestamp(stmt, 3, LocalDateTime.now());
            stmt.setLong(4, contractId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Rejected contract {} by account {} with reason: {}", contractId, approverAccountId, reason);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.error("Error rejecting contract {}: {}", contractId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Get full name from users table by account ID (JOIN accounts -> users)
     */
    public String getFullNameByAccountId(Long accountId) throws SQLException {
        if (accountId == null) {
            return null;
        }
        
        String sql = "SELECT u.full_name FROM accounts a " +
                    "JOIN users u ON a.user_id = u.id " +
                    "WHERE a.id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            logger.error("Error getting full name by account ID {}: {}", accountId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm số lượng contracts theo approval status
     */
    public long countByApprovalStatus(String approvalStatus) throws SQLException {
        if (approvalStatus == null || approvalStatus.trim().isEmpty()) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM employment_contracts WHERE approval_status = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, approvalStatus);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting contracts by approval status {}: {}", approvalStatus, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Xóa contract theo ID
     */
    public boolean deleteById(Long contractId) throws SQLException {
        if (contractId == null) {
            return false;
        }
        
        String sql = "DELETE FROM employment_contracts WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, contractId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Deleted contract with ID: {}", contractId);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.error("Error deleting contract {}: {}", contractId, e.getMessage(), e);
            throw e;
        }
    }
}