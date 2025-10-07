package group4.hrms.dao;

import group4.hrms.model.Application;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
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
               "cccd, cccd_issued_date, cccd_issued_place, cccd_front_path, cccd_back_path, created_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE applications SET job_id = ?, status = ?, note = ?, full_name = ?, email = ?, " +
               "phone = ?, dob = ?, gender = ?, hometown = ?, address_line1 = ?, address_line2 = ?, " +
               "city = ?, state = ?, postal_code = ?, country = ?, resume_path = ?, cccd = ?, " +
               "cccd_issued_date = ?, cccd_issued_place = ?, cccd_front_path = ?, cccd_back_path = ? " +
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
        stmt.setLong(22, app.getId());
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
}