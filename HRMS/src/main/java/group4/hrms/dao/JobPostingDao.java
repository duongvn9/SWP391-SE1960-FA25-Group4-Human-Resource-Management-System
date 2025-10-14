package group4.hrms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import group4.hrms.model.JobPosting;
import group4.hrms.util.DatabaseUtil;

/**
 * DAO class để xử lý các thao tác với bảng job_postings
 * 
 * @author Group4
 */
public class JobPostingDao extends BaseDao<JobPosting, Long> {
    
    @Override
    protected String getTableName() {
        return "job_postings";
    }
    
    @Override
    protected JobPosting mapResultSetToEntity(ResultSet rs) throws SQLException {
        JobPosting job = new JobPosting();
        job.setId(rs.getLong("id"));
        
        Long requestId = rs.getLong("request_id");
        if (!rs.wasNull()) {
            job.setRequestId(requestId);
        }
        
        job.setTitle(rs.getString("title"));
        
        Long departmentId = rs.getLong("department_id");
        if (!rs.wasNull()) {
            job.setDepartmentId(departmentId);
        }
        
        job.setDescription(rs.getString("description"));
        job.setStatus(rs.getString("status"));
        job.setPublishedAt(getLocalDateTime(rs, "published_at"));
        
        Long createdByAccountId = rs.getLong("created_by_account_id");
        if (!rs.wasNull()) {
            job.setCreatedByAccountId(createdByAccountId);
        }
        
        job.setCreatedAt(getLocalDateTime(rs, "created_at"));
        job.setUpdatedAt(getLocalDateTime(rs, "updated_at"));
        
        return job;
    }
    
    @Override
    protected void setEntityId(JobPosting job, Long id) {
        job.setId(id);
    }
    
    @Override
    protected Long getEntityId(JobPosting job) {
        return job.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO job_postings (request_id, title, department_id, description, status, " +
               "published_at, created_by_account_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE job_postings SET request_id = ?, title = ?, department_id = ?, description = ?, " +
               "status = ?, published_at = ?, created_by_account_id = ?, updated_at = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, JobPosting job) throws SQLException {
        if (job.getRequestId() != null) {
            stmt.setLong(1, job.getRequestId());
        } else {
            stmt.setNull(1, Types.BIGINT);
        }
        stmt.setString(2, job.getTitle());
        if (job.getDepartmentId() != null) {
            stmt.setLong(3, job.getDepartmentId());
        } else {
            stmt.setNull(3, Types.BIGINT);
        }
        stmt.setString(4, job.getDescription());
        stmt.setString(5, job.getStatus());
        setTimestamp(stmt, 6, job.getPublishedAt());
        if (job.getCreatedByAccountId() != null) {
            stmt.setLong(7, job.getCreatedByAccountId());
        } else {
            stmt.setNull(7, Types.BIGINT);
        }
        setTimestamp(stmt, 8, job.getCreatedAt() != null ? job.getCreatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 9, job.getUpdatedAt() != null ? job.getUpdatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, JobPosting job) throws SQLException {
        if (job.getRequestId() != null) {
            stmt.setLong(1, job.getRequestId());
        } else {
            stmt.setNull(1, Types.BIGINT);
        }
        stmt.setString(2, job.getTitle());
        if (job.getDepartmentId() != null) {
            stmt.setLong(3, job.getDepartmentId());
        } else {
            stmt.setNull(3, Types.BIGINT);
        }
        stmt.setString(4, job.getDescription());
        stmt.setString(5, job.getStatus());
        setTimestamp(stmt, 6, job.getPublishedAt());
        if (job.getCreatedByAccountId() != null) {
            stmt.setLong(7, job.getCreatedByAccountId());
        } else {
            stmt.setNull(7, Types.BIGINT);
        }
        setTimestamp(stmt, 8, LocalDateTime.now());
        stmt.setLong(9, job.getId());
    }
    
    // Business methods
    
    /**
     * Tìm job postings theo department ID
     */
    public List<JobPosting> findByDepartmentId(Long departmentId) throws SQLException {
        if (departmentId == null) {
            return new ArrayList<>();
        }
        
        List<JobPosting> jobs = new ArrayList<>();
        String sql = "SELECT * FROM job_postings WHERE department_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, departmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToEntity(rs));
                }
            }
            
            return jobs;
            
        } catch (SQLException e) {
            logger.error("Error finding job postings by department ID {}: {}", departmentId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm job postings theo status
     */
    public List<JobPosting> findByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<JobPosting> jobs = new ArrayList<>();
        String sql = "SELECT * FROM job_postings WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToEntity(rs));
                }
            }
            
            return jobs;
            
        } catch (SQLException e) {
            logger.error("Error finding job postings by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm published job postings
     */
    public List<JobPosting> findPublishedJobs() throws SQLException {
        List<JobPosting> jobs = new ArrayList<>();
        String sql = "SELECT * FROM job_postings WHERE status = 'published' AND published_at IS NOT NULL ORDER BY published_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                jobs.add(mapResultSetToEntity(rs));
            }
            
            return jobs;
            
        } catch (SQLException e) {
            logger.error("Error finding published job postings: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm job posting theo request ID
     */
    public Optional<JobPosting> findByRequestId(Long requestId) throws SQLException {
        if (requestId == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM job_postings WHERE request_id = ?";
        
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
            logger.error("Error finding job posting by request ID {}: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Publish job posting
     */
    public boolean publishJob(Long jobId, Long publishedBy) throws SQLException {
        if (jobId == null) {
            return false;
        }
        
        String sql = "UPDATE job_postings SET status = 'published', published_at = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            setTimestamp(stmt, 1, now);
            setTimestamp(stmt, 2, now);
            stmt.setLong(3, jobId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error publishing job {}: {}", jobId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật status
     */
    public boolean updateStatus(Long jobId, String newStatus) throws SQLException {
        if (jobId == null || newStatus == null) {
            return false;
        }
        
        String sql = "UPDATE job_postings SET status = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, jobId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating job status {}: {}", jobId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm applications cho job posting
     */
    public long countApplications(Long jobId) throws SQLException {
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
            logger.error("Error counting applications for job {}: {}", jobId, e.getMessage(), e);
            throw e;
        }
    }
}