package group4.hrms.dao;

import group4.hrms.model.EmploymentContract;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
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
        // Now all fields are available in EmploymentContract model
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
        // Now all fields are available in EmploymentContract model for INSERT
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
        // Now all fields are available in EmploymentContract model for UPDATE
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

        // Dựa vào account status thay vì user status
        String sql = """
            SELECT ec.* FROM employment_contracts ec
            INNER JOIN accounts a ON ec.user_id = a.user_id
            WHERE ec.user_id = ? AND ec.status = 'active' AND a.status = 'active'
            ORDER BY ec.start_date DESC LIMIT 1
            """;

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
     * Tìm contract có hiệu lực tại một ngày cụ thể (tái sử dụng logic findActiveContractByUser)
     */
    public Optional<EmploymentContract> findContractForDate(Long userId, LocalDate date) throws SQLException {
        if (userId == null || date == null) {
            return Optional.empty();
        }

        // Tái sử dụng pattern từ findActiveContractByUser nhưng thêm điều kiện date
        String sql = """
            SELECT ec.* FROM employment_contracts ec
            INNER JOIN accounts a ON ec.user_id = a.user_id
            WHERE ec.user_id = ? AND ec.status = 'active' AND a.status = 'active'
            AND ec.start_date <= ? AND (ec.end_date IS NULL OR ec.end_date >= ?)
            ORDER BY ec.start_date DESC LIMIT 1
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            setDate(stmt, 2, date);
            setDate(stmt, 3, date);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding contract for user {} on date {}: {}", userId, date, e.getMessage(), e);
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
     * Get full name from users table by account ID
     * The field created_by_account_id stores account.id, so we need to JOIN accounts -> users
     * to get the user's full_name
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

    // ==================== PERFORMANCE OPTIMIZED METHODS ====================

    /**
     * Batch update expired contracts
     * Updates all contracts where end_date < today and status = 'active' to status = 'expired'
     * This is much faster than updating contracts one by one in a loop
     */
    public int batchUpdateExpiredContracts() throws SQLException {
        String sql = "UPDATE employment_contracts " +
                    "SET status = 'expired', updated_at = ? " +
                    "WHERE status = 'active' " +
                    "AND end_date IS NOT NULL " +
                    "AND end_date < CURDATE()";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setTimestamp(stmt, 1, LocalDateTime.now());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Batch updated {} expired contracts", rowsAffected);
            }

            return rowsAffected;

        } catch (SQLException e) {
            logger.error("Error batch updating expired contracts: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Find contracts with filters and pagination (OPTIMIZED)
     * Uses JOIN to get all data in ONE query instead of N+1 queries
     * Applies filters at database level instead of in Java
     * Returns only the requested page of results
     */
    public List<group4.hrms.dto.EmploymentContractDto> findWithFilters(
            String searchQuery,
            String statusFilter,
            String approvalStatusFilter,
            String typeFilter,
            int offset,
            int limit,
            boolean prioritizePending) throws SQLException {

        List<group4.hrms.dto.EmploymentContractDto> dtos = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    ec.*, " +
            "    u.full_name as user_full_name, " +
            "    u.employee_code as user_employee_code, " +
            "    creator_user.full_name as created_by_name, " +
            "    approver_user.full_name as approved_by_name " +
            "FROM employment_contracts ec " +
            "INNER JOIN users u ON ec.user_id = u.id " +
            "LEFT JOIN users creator_user ON ec.created_by_account_id = creator_user.id " +
            "LEFT JOIN users approver_user ON ec.approved_by_account_id = approver_user.id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // Add search filter
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (ec.contract_no LIKE ? OR u.full_name LIKE ? OR u.employee_code LIKE ?) ");
            String searchPattern = "%" + searchQuery.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Add status filter
        if (statusFilter != null && !statusFilter.isEmpty() && !"all".equals(statusFilter)) {
            sql.append("AND ec.status = ? ");
            params.add(statusFilter);
        }

        // Add approval status filter
        if (approvalStatusFilter != null && !approvalStatusFilter.isEmpty() && !"all".equals(approvalStatusFilter)) {
            sql.append("AND ec.approval_status = ? ");
            params.add(approvalStatusFilter);
        }

        // Add contract type filter
        if (typeFilter != null && !typeFilter.isEmpty() && !"all".equals(typeFilter)) {
            sql.append("AND ec.contract_type = ? ");
            params.add(typeFilter);
        }

        // Add ordering and pagination
        // If prioritizePending is true (for HRM), pending contracts come first
        if (prioritizePending) {
            sql.append("ORDER BY CASE WHEN ec.approval_status = 'pending' THEN 0 ELSE 1 END, ec.created_at DESC LIMIT ? OFFSET ?");
        } else {
            sql.append("ORDER BY ec.created_at DESC LIMIT ? OFFSET ?");
        }
        params.add(limit);
        params.add(offset);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EmploymentContract contract = mapResultSetToEntity(rs);
                    group4.hrms.dto.EmploymentContractDto dto = new group4.hrms.dto.EmploymentContractDto(contract);

                    // Set user info from JOIN results
                    dto.setUserFullName(rs.getString("user_full_name"));
                    dto.setUsername(rs.getString("user_employee_code"));
                    dto.setCreatedByName(rs.getString("created_by_name"));
                    dto.setApprovedByName(rs.getString("approved_by_name"));

                    dtos.add(dto);
                }
            }

            return dtos;

        } catch (SQLException e) {
            logger.error("Error finding contracts with filters: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Count contracts with filters (for pagination)
     * Uses the same filters as findWithFilters to get accurate count
     */
    public int countWithFilters(
            String searchQuery,
            String statusFilter,
            String approvalStatusFilter,
            String typeFilter) throws SQLException {

        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) " +
            "FROM employment_contracts ec " +
            "INNER JOIN users u ON ec.user_id = u.id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // Add same filters as findWithFilters
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (ec.contract_no LIKE ? OR u.full_name LIKE ? OR u.employee_code LIKE ?) ");
            String searchPattern = "%" + searchQuery.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (statusFilter != null && !statusFilter.isEmpty() && !"all".equals(statusFilter)) {
            sql.append("AND ec.status = ? ");
            params.add(statusFilter);
        }

        if (approvalStatusFilter != null && !approvalStatusFilter.isEmpty() && !"all".equals(approvalStatusFilter)) {
            sql.append("AND ec.approval_status = ? ");
            params.add(approvalStatusFilter);
        }

        if (typeFilter != null && !typeFilter.isEmpty() && !"all".equals(typeFilter)) {
            sql.append("AND ec.contract_type = ? ");
            params.add(typeFilter);
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 0;

        } catch (SQLException e) {
            logger.error("Error counting contracts with filters: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Find all contracts with user info using JOIN (OPTIMIZED)
     * Returns DTOs with all necessary information in ONE query
     * Use this instead of findAll() + multiple findById() calls
     * 
     * NOTE: created_by_account_id and approved_by_account_id store user.id directly
     */
    public List<group4.hrms.dto.EmploymentContractDto> findAllWithUserInfo() throws SQLException {
        List<group4.hrms.dto.EmploymentContractDto> dtos = new ArrayList<>();

        String sql = "SELECT " +
                    "    ec.*, " +
                    "    u.full_name as user_full_name, " +
                    "    u.employee_code as user_employee_code, " +
                    "    creator.full_name as created_by_name, " +
                    "    approver.full_name as approved_by_name " +
                    "FROM employment_contracts ec " +
                    "INNER JOIN users u ON ec.user_id = u.id " +
                    "LEFT JOIN users creator ON ec.created_by_account_id = creator.id " +
                    "LEFT JOIN users approver ON ec.approved_by_account_id = approver.id " +
                    "ORDER BY ec.created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                EmploymentContract contract = mapResultSetToEntity(rs);
                group4.hrms.dto.EmploymentContractDto dto = new group4.hrms.dto.EmploymentContractDto(contract);

                // Set user info from JOIN results
                dto.setUserFullName(rs.getString("user_full_name"));
                dto.setUsername(rs.getString("user_employee_code"));
                dto.setCreatedByName(rs.getString("created_by_name"));
                dto.setApprovedByName(rs.getString("approved_by_name"));

                dtos.add(dto);
            }

            return dtos;

        } catch (SQLException e) {
            logger.error("Error finding all contracts with user info: {}", e.getMessage(), e);
            throw e;
        }
    }
}