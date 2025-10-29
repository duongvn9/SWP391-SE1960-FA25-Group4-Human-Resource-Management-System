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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.model.JobPosting;
import group4.hrms.util.DatabaseUtil;

/**
 * DAO class để xử lý các thao tác với bảng job_postings
 * 
 * @author Group4
 */
public class JobPostingDao extends BaseDao<JobPosting, Long> {
    private static final Logger logger = LoggerFactory.getLogger(JobPostingDao.class);
    
    @Override
    protected String getTableName() {
        return "job_postings";
    }
    
    /**
     * Get list of request IDs that already have job postings created
     * Used to filter out requests that already have job postings
     */
    public List<Long> findRequestIdsWithJobPostings() throws SQLException {
        String sql = "SELECT DISTINCT request_id FROM job_postings WHERE request_id IS NOT NULL";
        List<Long> requestIds = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requestIds.add(rs.getLong("request_id"));
                }
            }
        }
        return requestIds;
    }
    
    @Override
    public JobPosting update(JobPosting entity) throws SQLException {
        // Reuse save() method which has full field mapping
        return save(entity);
    }
    
    @Override 
    public JobPosting save(JobPosting entity) throws SQLException {
        logger.debug("Starting save operation for JobPosting: {}", entity);
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql;
            if (entity.getId() == null) {
            sql = "INSERT INTO job_postings (request_id, title, department_id, description, status, " +
                "created_by_account_id, created_at, updated_at, code, job_level, job_type, " +
                "quantity, min_salary, max_salary, min_experience_years, requirements, benefits, " +
                "working_location, application_deadline, contact_email, contact_phone, " +
                "salary_type, priority, working_hours, start_date, expiry_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?)";
                logger.debug("Using INSERT SQL: {}", sql);
            } else {
            sql = "UPDATE job_postings SET request_id=?, title=?, department_id=?, description=?, status=?, " +
                "created_by_account_id=?, updated_at=?, code=?, job_level=?, job_type=?, " +
                "quantity=?, min_salary=?, max_salary=?, min_experience_years=?, requirements=?, " +
                "benefits=?, working_location=?, application_deadline=?, contact_email=?, contact_phone=?, " +
                "salary_type=?, priority=?, working_hours=?, start_date=?, expiry_date=?, " +
                "rejected_reason=?, approved_by_account_id=?, approved_at=?, published_by_account_id=?, published_at=? " +
                "WHERE id=?";
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                int paramIndex = 1;
                setNullableLong(stmt, paramIndex++, entity.getRequestId());
                stmt.setString(paramIndex++, entity.getTitle());
                setNullableLong(stmt, paramIndex++, entity.getDepartmentId());
                stmt.setString(paramIndex++, entity.getDescription());
                stmt.setString(paramIndex++, entity.getStatus());
                setNullableLong(stmt, paramIndex++, entity.getCreatedByAccountId());
                
                if (entity.getId() == null) {
                    // For INSERT
                    stmt.setObject(paramIndex++, LocalDateTime.now());
                    stmt.setObject(paramIndex++, LocalDateTime.now());
                } else {
                    // For UPDATE
                    stmt.setObject(paramIndex++, LocalDateTime.now());
                }
                
                stmt.setString(paramIndex++, entity.getCode());
                // JobPosting uses 'level' as the field name and getter getLevel()
                stmt.setString(paramIndex++, entity.getLevel());
                stmt.setString(paramIndex++, entity.getJobType());
                // DB column is 'quantity'
                setNullableInt(stmt, paramIndex++, entity.getNumberOfPositions());
                setNullableBigDecimal(stmt, paramIndex++, entity.getMinSalary());
                setNullableBigDecimal(stmt, paramIndex++, entity.getMaxSalary());
                setNullableInt(stmt, paramIndex++, entity.getMinExperienceYears());
                stmt.setString(paramIndex++, entity.getRequirements());
                stmt.setString(paramIndex++, entity.getBenefits());
                stmt.setString(paramIndex++, entity.getWorkingLocation());
                stmt.setObject(paramIndex++, entity.getApplicationDeadline());
                stmt.setString(paramIndex++, entity.getContactEmail());
                stmt.setString(paramIndex++, entity.getContactPhone());
                
                // Thêm các trường mới
                stmt.setString(paramIndex++, entity.getSalaryType());
                stmt.setString(paramIndex++, entity.getPriority());
                stmt.setString(paramIndex++, entity.getWorkingHours());
                stmt.setObject(paramIndex++, entity.getStartDate());
                stmt.setObject(paramIndex++, entity.getApplicationDeadline()); // expiry_date = application_deadline
                
                // Add approval/rejection fields (only for UPDATE)
                if (entity.getId() != null) {
                    logger.debug("💾 Setting rejection fields in UPDATE:");
                    logger.debug("  [{}] rejectedReason: {}", paramIndex, entity.getRejectedReason());
                    stmt.setString(paramIndex++, entity.getRejectedReason());
                    
                    logger.debug("  [{}] approvedByAccountId: {}", paramIndex, entity.getApprovedByAccountId());
                    setNullableLong(stmt, paramIndex++, entity.getApprovedByAccountId());
                    
                    logger.debug("  [{}] approvedAt: {}", paramIndex, entity.getApprovedAt());
                    stmt.setObject(paramIndex++, entity.getApprovedAt());
                    
                    logger.debug("  [{}] publishedByAccountId: {}", paramIndex, entity.getPublishedByAccountId());
                    setNullableLong(stmt, paramIndex++, entity.getPublishedByAccountId());
                    
                    logger.debug("  [{}] publishedAt: {}", paramIndex, entity.getPublishedAt());
                    stmt.setObject(paramIndex++, entity.getPublishedAt());
                    
                    logger.debug("  [{}] id (WHERE): {}", paramIndex, entity.getId());
                    stmt.setLong(paramIndex++, entity.getId());
                }

                logger.debug("🚀 Executing UPDATE SQL...");
                int affectedRows = stmt.executeUpdate();
                logger.info("✅ UPDATE affected {} rows", affectedRows);
                if (affectedRows == 0) {
                    throw new SQLException("Creating/Updating job posting failed, no rows affected.");
                }

                if (entity.getId() == null) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            entity.setId(generatedKeys.getLong(1));
                        } else {
                            throw new SQLException("Creating job posting failed, no ID obtained.");
                        }
                    }
                }
            }
            return entity;
        }
    }
    
    private void setNullableLong(PreparedStatement stmt, int paramIndex, Long value) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, Types.BIGINT);
        } else {
            stmt.setLong(paramIndex, value);
        }
    }
    
    private void setNullableInt(PreparedStatement stmt, int paramIndex, Integer value) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, Types.INTEGER);
        } else {
            stmt.setInt(paramIndex, value);
        }
    }
    
    private void setNullableBigDecimal(PreparedStatement stmt, int paramIndex, java.math.BigDecimal value) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, Types.DECIMAL);
        } else {
            stmt.setBigDecimal(paramIndex, value);
        }
    }
    
    @Override
    protected JobPosting mapResultSetToEntity(ResultSet rs) throws SQLException {
        JobPosting job = new JobPosting();
        job.setId(rs.getLong("id"));
        
        // Request ID
        Long requestId = rs.getLong("request_id");
        if (!rs.wasNull()) {
            job.setRequestId(requestId);
        }
        
        // Basic info
        job.setTitle(rs.getString("title"));
        job.setCode(rs.getString("code"));
        
        // Department & Position
        Long departmentId = rs.getLong("department_id");
        if (!rs.wasNull()) {
            job.setDepartmentId(departmentId);
        }
        
        Long positionId = rs.getLong("position_id");
        if (!rs.wasNull()) {
            job.setPositionId(positionId);
        }
        
        // Job details
        job.setJobType(rs.getString("job_type"));
        job.setLevel(rs.getString("job_level"));
        job.setPriority(rs.getString("priority"));
        
        // Quantity (number of positions) column in DB is named 'quantity'
        int qty = rs.getInt("quantity");
        if (!rs.wasNull()) {
            job.setNumberOfPositions(qty);
        }
        
        // Experience and dates
        Integer minExp = (Integer) rs.getObject("min_experience_years");
        if (minExp != null) {
            job.setMinExperienceYears(minExp);
        }
        
        job.setStartDate(getLocalDate(rs, "start_date"));
        job.setApplicationDeadline(getLocalDate(rs, "application_deadline"));
        
        // Salary fields
        java.math.BigDecimal minSalary = rs.getBigDecimal("min_salary");
        if (minSalary != null) job.setMinSalary(minSalary);
        java.math.BigDecimal maxSalary = rs.getBigDecimal("max_salary");
        if (maxSalary != null) job.setMaxSalary(maxSalary);
        String salaryType = rs.getString("salary_type");
        if (salaryType != null) job.setSalaryType(salaryType);
        
        // Text content
        job.setDescription(rs.getString("description"));
        job.setRequirements(rs.getString("requirements"));
        job.setBenefits(rs.getString("benefits"));
        
        // Location and hours
        String workingLocation = rs.getString("working_location");
        if (workingLocation != null) job.setWorkingLocation(workingLocation);
        job.setWorkingHours(rs.getString("working_hours"));
        
        // Contact info
        job.setContactEmail(rs.getString("contact_email"));
        job.setContactPhone(rs.getString("contact_phone"));
        
        // Status and workflow
        job.setStatus(rs.getString("status"));
        job.setRejectedReason(rs.getString("rejected_reason"));
        
        // Approval tracking
        Long approvedByAccountId = rs.getLong("approved_by_account_id");
        if (!rs.wasNull()) {
            job.setApprovedByAccountId(approvedByAccountId);
        }
        job.setApprovedAt(getLocalDateTime(rs, "approved_at"));
        
        // Publishing tracking
        Long publishedByAccountId = rs.getLong("published_by_account_id");
        if (!rs.wasNull()) {
            job.setPublishedByAccountId(publishedByAccountId);
        }
        job.setPublishedAt(getLocalDateTime(rs, "published_at"));
        
        // Audit fields
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
         "min_salary, max_salary, salary_type, published_at, created_by_account_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE job_postings SET request_id=?, title=?, department_id=?, description=?, status=?, " +
            "created_by_account_id=?, updated_at=?, code=?, job_level=?, job_type=?, " +
            "quantity=?, min_salary=?, max_salary=?, min_experience_years=?, requirements=?, " +
            "benefits=?, working_location=?, application_deadline=?, contact_email=?, contact_phone=?, " +
            "salary_type=?, priority=?, working_hours=?, start_date=?, expiry_date=? " +
            "WHERE id=?";
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

        // salary fields
        if (job.getMinSalary() != null) {
            stmt.setBigDecimal(6, job.getMinSalary());
        } else {
            stmt.setNull(6, Types.DECIMAL);
        }
        if (job.getMaxSalary() != null) {
            stmt.setBigDecimal(7, job.getMaxSalary());
        } else {
            stmt.setNull(7, Types.DECIMAL);
        }
        if (job.getSalaryType() != null) {
            stmt.setString(8, job.getSalaryType());
        } else {
            stmt.setNull(8, Types.VARCHAR);
        }

        setTimestamp(stmt, 9, job.getPublishedAt());
        if (job.getCreatedByAccountId() != null) {
            stmt.setLong(10, job.getCreatedByAccountId());
        } else {
            stmt.setNull(10, Types.BIGINT);
        }
        setTimestamp(stmt, 11, job.getCreatedAt() != null ? job.getCreatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 12, job.getUpdatedAt() != null ? job.getUpdatedAt() : LocalDateTime.now());
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
        
        if (job.getCreatedByAccountId() != null) {
            stmt.setLong(6, job.getCreatedByAccountId());
        } else {
            stmt.setNull(6, Types.BIGINT);
        }
        
        setTimestamp(stmt, 7, LocalDateTime.now()); // updated_at
        
        // Additional fields
        if (job.getCode() != null) {
            stmt.setString(8, job.getCode());
        } else {
            stmt.setNull(8, Types.VARCHAR);
        }
        if (job.getLevel() != null) {
            stmt.setString(9, job.getLevel());
        } else {
            stmt.setNull(9, Types.VARCHAR);
        }
        if (job.getJobType() != null) {
            stmt.setString(10, job.getJobType());
        } else {
            stmt.setNull(10, Types.VARCHAR);
        }
        
        if (job.getNumberOfPositions() != null) {
            stmt.setInt(11, job.getNumberOfPositions());
        } else {
            stmt.setNull(11, Types.INTEGER);
        }
        
        if (job.getMinSalary() != null) {
            stmt.setBigDecimal(12, job.getMinSalary());
        } else {
            stmt.setNull(12, Types.DECIMAL);
        }
        if (job.getMaxSalary() != null) {
            stmt.setBigDecimal(13, job.getMaxSalary());
        } else {
            stmt.setNull(13, Types.DECIMAL);
        }
        
        if (job.getMinExperienceYears() != null) {
            stmt.setInt(14, job.getMinExperienceYears());
        } else {
            stmt.setNull(14, Types.INTEGER);
        }
        
        stmt.setString(15, job.getRequirements());
        stmt.setString(16, job.getBenefits());
        stmt.setString(17, job.getWorkingLocation());
        
        if (job.getApplicationDeadline() != null) {
            stmt.setObject(18, job.getApplicationDeadline());
        } else {
            stmt.setNull(18, Types.DATE);
        }
        
        stmt.setString(19, job.getContactEmail());
        stmt.setString(20, job.getContactPhone());
        
        if (job.getSalaryType() != null) {
            stmt.setString(21, job.getSalaryType());
        } else {
            stmt.setNull(21, Types.VARCHAR);
        }
        
        if (job.getPriority() != null) {
            stmt.setString(22, job.getPriority());
        } else {
            stmt.setNull(22, Types.VARCHAR);
        }
        
        if (job.getWorkingHours() != null) {
            stmt.setString(23, job.getWorkingHours());
        } else {
            stmt.setNull(23, Types.VARCHAR);
        }
        
        if (job.getStartDate() != null) {
            stmt.setObject(24, job.getStartDate());
        } else {
            stmt.setNull(24, Types.DATE);
        }
        
        if (job.getApplicationDeadline() != null) {
            stmt.setObject(25, job.getApplicationDeadline());
        } else {
            stmt.setNull(25, Types.DATE);
        }
        
        stmt.setLong(26, job.getId());
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