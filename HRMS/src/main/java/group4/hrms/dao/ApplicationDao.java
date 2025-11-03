package group4.hrms.dao;

import group4.hrms.model.Application;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng applications
 * 
 * @author Group4
 */
public class ApplicationDao extends BaseDao<Application, Long> {
    
    @Override
    protected String getTableName() {
        return "applications";
    }
    
    @Override
    protected Application mapResultSetToEntity(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setId(rs.getLong("id"));
        app.setJobId(rs.getLong("job_id"));
        app.setStatus(rs.getString("status"));
        app.setNote(rs.getString("note"));
        
        // Personal info
        app.setFullName(rs.getString("full_name"));
        app.setEmail(rs.getString("email"));
        app.setPhone(rs.getString("phone"));
        app.setDob(getLocalDate(rs, "dob"));
        app.setGender(rs.getString("gender"));
        app.setHometown(rs.getString("hometown"));
        
        // Address
        app.setAddressLine1(rs.getString("address_line1"));
        app.setAddressLine2(rs.getString("address_line2"));
        app.setCity(rs.getString("city"));
        app.setState(rs.getString("state"));
        app.setPostalCode(rs.getString("postal_code"));
        app.setCountry(rs.getString("country"));
        
        app.setResumePath(rs.getString("resume_path"));
        
        // CCCD
        app.setCccd(rs.getString("cccd"));
        app.setCccdIssuedDate(getLocalDate(rs, "cccd_issued_date"));
        app.setCccdIssuedPlace(rs.getString("cccd_issued_place"));
        app.setCccdFrontPath(rs.getString("cccd_front_path"));
        app.setCccdBackPath(rs.getString("cccd_back_path"));
        
        app.setCreatedAt(getLocalDateTime(rs, "created_at"));
        
        // HR Approval fields
        app.setHrApproverId(getNullableLong(rs, "hr_approver_id"));
        app.setHrApproverName(rs.getString("hr_approver_name"));
        app.setHrApprovalStatus(rs.getString("hr_approval_status"));
        app.setHrApprovalNote(rs.getString("hr_approval_note"));
        app.setHrApprovalDate(getLocalDateTime(rs, "hr_approval_date"));
        
        // HRM Approval fields
        app.setHrmApproverId(getNullableLong(rs, "hrm_approver_id"));
        app.setHrmApproverName(rs.getString("hrm_approver_name"));
        app.setHrmApprovalStatus(rs.getString("hrm_approval_status"));
        app.setHrmApprovalNote(rs.getString("hrm_approval_note"));
        app.setHrmApprovalDate(getLocalDateTime(rs, "hrm_approval_date"));
        
        return app;
    }
    
    @Override
    protected void setEntityId(Application app, Long id) {
        app.setId(id);
    }
    
    @Override
    protected Long getEntityId(Application app) {
        return app.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO applications (job_id, status, note, full_name, email, phone, dob, gender, " +
               "hometown, address_line1, address_line2, city, state, postal_code, country, resume_path, " +
               "cccd, cccd_issued_date, cccd_issued_place, cccd_front_path, cccd_back_path, created_at, " +
               "hr_approver_id, hr_approver_name, hr_approval_status, hr_approval_note, hr_approval_date, " +
               "hrm_approver_id, hrm_approver_name, hrm_approval_status, hrm_approval_note, hrm_approval_date) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE applications SET job_id = ?, status = ?, note = ?, full_name = ?, email = ?, " +
               "phone = ?, dob = ?, gender = ?, hometown = ?, address_line1 = ?, address_line2 = ?, " +
               "city = ?, state = ?, postal_code = ?, country = ?, resume_path = ?, cccd = ?, " +
               "cccd_issued_date = ?, cccd_issued_place = ?, cccd_front_path = ?, cccd_back_path = ?, " +
               "hr_approver_id = ?, hr_approver_name = ?, hr_approval_status = ?, hr_approval_note = ?, hr_approval_date = ?, " +
               "hrm_approver_id = ?, hrm_approver_name = ?, hrm_approval_status = ?, hrm_approval_note = ?, hrm_approval_date = ? " +
               "WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Application app) throws SQLException {
        stmt.setLong(1, app.getJobId());
        stmt.setString(2, app.getStatus());
        stmt.setString(3, app.getNote());
        stmt.setString(4, app.getFullName());
        stmt.setString(5, app.getEmail());
        stmt.setString(6, app.getPhone());
        setDate(stmt, 7, app.getDob());
        stmt.setString(8, app.getGender());
        stmt.setString(9, app.getHometown());
        stmt.setString(10, app.getAddressLine1());
        stmt.setString(11, app.getAddressLine2());
        stmt.setString(12, app.getCity());
        stmt.setString(13, app.getState());
        stmt.setString(14, app.getPostalCode());
        stmt.setString(15, app.getCountry());
        stmt.setString(16, app.getResumePath());
        stmt.setString(17, app.getCccd());
        setDate(stmt, 18, app.getCccdIssuedDate());
        stmt.setString(19, app.getCccdIssuedPlace());
        stmt.setString(20, app.getCccdFrontPath());
        stmt.setString(21, app.getCccdBackPath());
        setTimestamp(stmt, 22, app.getCreatedAt() != null ? app.getCreatedAt() : LocalDateTime.now());
        
        // HR Approval fields
        setNullableLong(stmt, 23, app.getHrApproverId());
        stmt.setString(24, app.getHrApproverName());
        stmt.setString(25, app.getHrApprovalStatus());
        stmt.setString(26, app.getHrApprovalNote());
        setTimestamp(stmt, 27, app.getHrApprovalDate());
        
        // HRM Approval fields
        setNullableLong(stmt, 28, app.getHrmApproverId());
        stmt.setString(29, app.getHrmApproverName());
        stmt.setString(30, app.getHrmApprovalStatus());
        stmt.setString(31, app.getHrmApprovalNote());
        setTimestamp(stmt, 32, app.getHrmApprovalDate());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Application app) throws SQLException {
        stmt.setLong(1, app.getJobId());
        stmt.setString(2, app.getStatus());
        stmt.setString(3, app.getNote());
        stmt.setString(4, app.getFullName());
        stmt.setString(5, app.getEmail());
        stmt.setString(6, app.getPhone());
        setDate(stmt, 7, app.getDob());
        stmt.setString(8, app.getGender());
        stmt.setString(9, app.getHometown());
        stmt.setString(10, app.getAddressLine1());
        stmt.setString(11, app.getAddressLine2());
        stmt.setString(12, app.getCity());
        stmt.setString(13, app.getState());
        stmt.setString(14, app.getPostalCode());
        stmt.setString(15, app.getCountry());
        stmt.setString(16, app.getResumePath());
        stmt.setString(17, app.getCccd());
        setDate(stmt, 18, app.getCccdIssuedDate());
        stmt.setString(19, app.getCccdIssuedPlace());
        stmt.setString(20, app.getCccdFrontPath());
        stmt.setString(21, app.getCccdBackPath());
        
        // HR Approval fields
        setNullableLong(stmt, 22, app.getHrApproverId());
        stmt.setString(23, app.getHrApproverName());
        stmt.setString(24, app.getHrApprovalStatus());
        stmt.setString(25, app.getHrApprovalNote());
        setTimestamp(stmt, 26, app.getHrApprovalDate());
        
        // HRM Approval fields
        setNullableLong(stmt, 27, app.getHrmApproverId());
        stmt.setString(28, app.getHrmApproverName());
        stmt.setString(29, app.getHrmApprovalStatus());
        stmt.setString(30, app.getHrmApprovalNote());
        setTimestamp(stmt, 31, app.getHrmApprovalDate());
        
        stmt.setLong(32, app.getId());
    }
    
    // Business methods
    
    /**
     * Tìm applications theo job ID
     */
    public List<Application> findByJobId(Long jobId) throws SQLException {
        if (jobId == null) {
            return new ArrayList<>();
        }
        
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE job_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, jobId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSetToEntity(rs));
                }
            }
            
            return applications;
            
        } catch (SQLException e) {
            logger.error("Error finding applications by job ID {}: {}", jobId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm applications theo status
     */
    public List<Application> findByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSetToEntity(rs));
                }
            }
            
            return applications;
            
        } catch (SQLException e) {
            logger.error("Error finding applications by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm application theo email và job
     */
    public Optional<Application> findByJobAndEmail(Long jobId, String email) throws SQLException {
        if (jobId == null || email == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM applications WHERE job_id = ? AND email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, jobId);
            stmt.setString(2, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding application by job {} and email {}: {}", jobId, email, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật status của application
     */
    public boolean updateStatus(Long applicationId, String newStatus, String note) throws SQLException {
        if (applicationId == null || newStatus == null) {
            return false;
        }
        
        String sql = "UPDATE applications SET status = ?, note = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setString(2, note);
            stmt.setLong(3, applicationId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating application status {}: {}", applicationId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật HR approval cho application
     */
    public boolean updateHrApproval(Long applicationId, Long hrApproverId, String hrApproverName, 
                                   String approvalStatus, String note) throws SQLException {
        if (applicationId == null || hrApproverId == null || approvalStatus == null) {
            return false;
        }
        
        String sql = "UPDATE applications SET hr_approver_id = ?, hr_approver_name = ?, " +
                    "hr_approval_status = ?, hr_approval_note = ?, hr_approval_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hrApproverId);
            stmt.setString(2, hrApproverName);
            stmt.setString(3, approvalStatus);
            stmt.setString(4, note);
            setTimestamp(stmt, 5, LocalDateTime.now());
            stmt.setLong(6, applicationId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating HR approval for application {}: {}", applicationId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật HRM approval cho application
     */
    public boolean updateHrmApproval(Long applicationId, Long hrmApproverId, String hrmApproverName, 
                                    String approvalStatus, String note) throws SQLException {
        if (applicationId == null || hrmApproverId == null || approvalStatus == null) {
            return false;
        }
        
        String sql = "UPDATE applications SET hrm_approver_id = ?, hrm_approver_name = ?, " +
                    "hrm_approval_status = ?, hrm_approval_note = ?, hrm_approval_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hrmApproverId);
            stmt.setString(2, hrmApproverName);
            stmt.setString(3, approvalStatus);
            stmt.setString(4, note);
            setTimestamp(stmt, 5, LocalDateTime.now());
            stmt.setLong(6, applicationId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating HRM approval for application {}: {}", applicationId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật cả status và approval trong một transaction
     */
    public boolean updateStatusWithApproval(Long applicationId, String newStatus, String note,
                                          Long approverId, String approverName, String approverRole,
                                          String approvalStatus) throws SQLException {
        if (applicationId == null || newStatus == null || approverId == null || approverRole == null) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Update main status
            String statusSql = "UPDATE applications SET status = ?, note = ? WHERE id = ?";
            try (PreparedStatement statusStmt = conn.prepareStatement(statusSql)) {
                statusStmt.setString(1, newStatus);
                statusStmt.setString(2, note);
                statusStmt.setLong(3, applicationId);
                statusStmt.executeUpdate();
            }
            
            // Update approval based on role
            String approvalSql;
            if ("HR".equals(approverRole)) {
                approvalSql = "UPDATE applications SET hr_approver_id = ?, hr_approver_name = ?, " +
                             "hr_approval_status = ?, hr_approval_note = ?, hr_approval_date = ? WHERE id = ?";
            } else if ("HRM".equals(approverRole)) {
                approvalSql = "UPDATE applications SET hrm_approver_id = ?, hrm_approver_name = ?, " +
                             "hrm_approval_status = ?, hrm_approval_note = ?, hrm_approval_date = ? WHERE id = ?";
            } else {
                throw new IllegalArgumentException("Invalid approver role: " + approverRole);
            }
            
            try (PreparedStatement approvalStmt = conn.prepareStatement(approvalSql)) {
                approvalStmt.setLong(1, approverId);
                approvalStmt.setString(2, approverName);
                approvalStmt.setString(3, approvalStatus);
                approvalStmt.setString(4, note);
                setTimestamp(approvalStmt, 5, LocalDateTime.now());
                approvalStmt.setLong(6, applicationId);
                approvalStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.error("Error rolling back transaction: {}", rollbackEx.getMessage(), rollbackEx);
                }
            }
            logger.error("Error updating status with approval for application {}: {}", applicationId, e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    logger.error("Error closing connection: {}", closeEx.getMessage(), closeEx);
                }
            }
        }
    }
    
    /**
     * Đếm applications theo job ID
     */
    public long countByJobId(Long jobId) throws SQLException {
        if (jobId == null) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM applications WHERE job_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, jobId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting applications by job ID {}: {}", jobId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm applications theo status
     */
    public long countByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM applications WHERE status = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting applications by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm applications đã được hire
     */
    public List<Application> findHiredApplications() throws SQLException {
        return findByStatus("hired");
    }
    
    /**
     * Tìm applications mới chưa review
     */
    public List<Application> findNewApplications() throws SQLException {
        return findByStatus("new");
    }
    
    /**
     * Tìm applications đang chờ HR duyệt
     */
    public List<Application> findPendingHrApproval() throws SQLException {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE status = 'new' AND hr_approval_status IS NULL ORDER BY created_at ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                applications.add(mapResultSetToEntity(rs));
            }
            
            return applications;
            
        } catch (SQLException e) {
            logger.error("Error finding applications pending HR approval: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm applications đang chờ HRM duyệt cuối cùng
     */
    public List<Application> findPendingHrmApproval() throws SQLException {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE status = 'reviewing' AND hrm_approval_status IS NULL " +
                    "AND hr_approval_status = 'approved' ORDER BY hr_approval_date ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                applications.add(mapResultSetToEntity(rs));
            }
            
            return applications;
            
        } catch (SQLException e) {
            logger.error("Error finding applications pending HRM approval: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm applications theo HR approver
     */
    public List<Application> findByHrApprover(Long hrApproverId) throws SQLException {
        if (hrApproverId == null) {
            return new ArrayList<>();
        }
        
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE hr_approver_id = ? ORDER BY hr_approval_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hrApproverId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSetToEntity(rs));
                }
            }
            
            return applications;
            
        } catch (SQLException e) {
            logger.error("Error finding applications by HR approver {}: {}", hrApproverId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm applications theo HRM approver
     */
    public List<Application> findByHrmApprover(Long hrmApproverId) throws SQLException {
        if (hrmApproverId == null) {
            return new ArrayList<>();
        }
        
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE hrm_approver_id = ? ORDER BY hrm_approval_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hrmApproverId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSetToEntity(rs));
                }
            }
            
            return applications;
            
        } catch (SQLException e) {
            logger.error("Error finding applications by HRM approver {}: {}", hrmApproverId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm applications theo approval status
     */
    public long countByApprovalStatus(String role, String status) throws SQLException {
        if (role == null || status == null) {
            return 0;
        }
        
        String sql;
        if ("HR".equals(role)) {
            sql = "SELECT COUNT(*) FROM applications WHERE hr_approval_status = ?";
        } else if ("HRM".equals(role)) {
            sql = "SELECT COUNT(*) FROM applications WHERE hrm_approval_status = ?";
        } else {
            return 0;
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting applications by {} approval status {}: {}", role, status, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Lấy tất cả applications với pagination và filter
     */
    public List<Application> findAllWithPagination(int page, int size, String statusFilter, String searchTerm) throws SQLException {
        List<Application> applications = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT a.*, j.title as job_title FROM applications a ");
        sql.append("LEFT JOIN job_postings j ON a.job_id = j.id WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        // Status filter
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"all".equals(statusFilter)) {
            sql.append("AND a.status = ? ");
            params.add(statusFilter);
        }
        
        // Search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (a.full_name LIKE ? OR a.email LIKE ? OR j.title LIKE ?) ");
            String searchPattern = "%" + searchTerm + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        sql.append("ORDER BY a.created_at DESC ");
        sql.append("LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Application app = mapResultSetToEntity(rs);
                    // Set job title if available
                    try {
                        String jobTitle = rs.getString("job_title");
                        // We'll add this to a custom field or handle it in the service layer
                    } catch (SQLException e) {
                        // Ignore if column doesn't exist
                    }
                    applications.add(app);
                }
            }
            
            return applications;
            
        } catch (SQLException e) {
            logger.error("Error finding applications with pagination: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm tổng số applications với filter
     */
    public long countWithFilter(String statusFilter, String searchTerm) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM applications a ");
        sql.append("LEFT JOIN job_postings j ON a.job_id = j.id WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        // Status filter
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"all".equals(statusFilter)) {
            sql.append("AND a.status = ? ");
            params.add(statusFilter);
        }
        
        // Search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (a.full_name LIKE ? OR a.email LIKE ? OR j.title LIKE ?) ");
            String searchPattern = "%" + searchTerm + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting applications with filter: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Helper method để get nullable Long từ ResultSet
     */
    private Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Helper method để set nullable Long vào PreparedStatement
     */
    private void setNullableLong(PreparedStatement stmt, int parameterIndex, Long value) throws SQLException {
        if (value != null) {
            stmt.setLong(parameterIndex, value);
        } else {
            stmt.setNull(parameterIndex, java.sql.Types.BIGINT);
        }
    }
}