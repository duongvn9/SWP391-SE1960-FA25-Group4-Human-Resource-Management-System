package group4.hrms.dao;

import group4.hrms.model.AttendanceLog;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng AttendanceLog
 * 
 * @author Group4
 */
public class AttendanceLogDao extends BaseDao<AttendanceLog, Long> {
    

    
    @Override
    protected String getTableName() {
        return "attendance_logs";
    }
    
    @Override
    protected AttendanceLog mapResultSetToEntity(ResultSet rs) throws SQLException {
        AttendanceLog log = new AttendanceLog();
        log.setId(rs.getLong("id"));
        log.setUserId(rs.getLong("user_id"));
        
        Date workDate = rs.getDate("work_date");
        if (workDate != null) {
            log.setWorkDate(workDate.toLocalDate());
        }
        
        log.setCheckInTime(getLocalDateTime(rs, "check_in_time"));
        log.setCheckOutTime(getLocalDateTime(rs, "check_out_time"));
        log.setCheckInType(rs.getString("check_in_type"));
        log.setCheckOutType(rs.getString("check_out_type"));
        
        Double workingHours = rs.getDouble("working_hours");
        if (!rs.wasNull()) {
            log.setWorkingHours(workingHours);
        }
        
        Double overtimeHours = rs.getDouble("overtime_hours");
        if (!rs.wasNull()) {
            log.setOvertimeHours(overtimeHours);
        }
        
        log.setStatus(rs.getString("status"));
        log.setNotes(rs.getString("notes"));
        log.setCheckInIp(rs.getString("check_in_ip"));
        log.setCheckOutIp(rs.getString("check_out_ip"));
        log.setCheckInLocation(rs.getString("check_in_location"));
        log.setCheckOutLocation(rs.getString("check_out_location"));
        log.setCreatedAt(getLocalDateTime(rs, "created_at"));
        log.setUpdatedAt(getLocalDateTime(rs, "updated_at"));
        
        return log;
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO attendance_logs (user_id, work_date, check_in_time, check_out_time, " +
               "check_in_type, check_out_type, working_hours, overtime_hours, status, notes, " +
               "check_in_ip, check_out_ip, check_in_location, check_out_location, " +
               "created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE attendance_logs SET user_id = ?, work_date = ?, check_in_time = ?, " +
               "check_out_time = ?, check_in_type = ?, check_out_type = ?, working_hours = ?, " +
               "overtime_hours = ?, status = ?, notes = ?, check_in_ip = ?, check_out_ip = ?, " +
               "check_in_location = ?, check_out_location = ?, updated_at = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, AttendanceLog log) throws SQLException {
        stmt.setLong(1, log.getUserId());
        
        if (log.getWorkDate() != null) {
            stmt.setDate(2, Date.valueOf(log.getWorkDate()));
        } else {
            stmt.setNull(2, Types.DATE);
        }
        
        setTimestamp(stmt, 3, log.getCheckInTime());
        setTimestamp(stmt, 4, log.getCheckOutTime());
        stmt.setString(5, log.getCheckInType());
        stmt.setString(6, log.getCheckOutType());
        
        if (log.getWorkingHours() != null) {
            stmt.setDouble(7, log.getWorkingHours());
        } else {
            stmt.setNull(7, Types.DOUBLE);
        }
        
        if (log.getOvertimeHours() != null) {
            stmt.setDouble(8, log.getOvertimeHours());
        } else {
            stmt.setNull(8, Types.DOUBLE);
        }
        
        stmt.setString(9, log.getStatus());
        stmt.setString(10, log.getNotes());
        stmt.setString(11, log.getCheckInIp());
        stmt.setString(12, log.getCheckOutIp());
        stmt.setString(13, log.getCheckInLocation());
        stmt.setString(14, log.getCheckOutLocation());
        
        LocalDateTime now = LocalDateTime.now();
        setTimestamp(stmt, 15, now);
        setTimestamp(stmt, 16, now);
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, AttendanceLog log) throws SQLException {
        stmt.setLong(1, log.getUserId());
        
        if (log.getWorkDate() != null) {
            stmt.setDate(2, Date.valueOf(log.getWorkDate()));
        } else {
            stmt.setNull(2, Types.DATE);
        }
        
        setTimestamp(stmt, 3, log.getCheckInTime());
        setTimestamp(stmt, 4, log.getCheckOutTime());
        stmt.setString(5, log.getCheckInType());
        stmt.setString(6, log.getCheckOutType());
        
        if (log.getWorkingHours() != null) {
            stmt.setDouble(7, log.getWorkingHours());
        } else {
            stmt.setNull(7, Types.DOUBLE);
        }
        
        if (log.getOvertimeHours() != null) {
            stmt.setDouble(8, log.getOvertimeHours());
        } else {
            stmt.setNull(8, Types.DOUBLE);
        }
        
        stmt.setString(9, log.getStatus());
        stmt.setString(10, log.getNotes());
        stmt.setString(11, log.getCheckInIp());
        stmt.setString(12, log.getCheckOutIp());
        stmt.setString(13, log.getCheckInLocation());
        stmt.setString(14, log.getCheckOutLocation());
        setTimestamp(stmt, 15, LocalDateTime.now());
        stmt.setLong(16, log.getId());
    }
    
    @Override
    protected void setEntityId(AttendanceLog log, Long id) {
        log.setId(id);
    }
    
    @Override
    protected Long getEntityId(AttendanceLog log) {
        return log.getId();
    }
    
    /**
     * Override save method để xử lý insert/update
     */
    @Override
    public AttendanceLog save(AttendanceLog entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        
        if (getEntityId(entity) != null) {
            return update(entity);
        } else {
            String sql = createInsertSql();
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                setInsertParameters(stmt, entity);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating attendance log failed, no rows affected");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long generatedId = generatedKeys.getLong(1);
                        setEntityId(entity, generatedId);
                    } else {
                        throw new SQLException("Creating attendance log failed, no ID obtained");
                    }
                }
                
                logger.info("Created new attendance log for user {} on {}", entity.getUserId(), entity.getWorkDate());
                return entity;
                
            } catch (SQLException e) {
                logger.error("Error saving attendance log: {}", e.getMessage(), e);
                throw e;
            }
        }
    }
    
    /**
     * Tìm attendance log theo user và work date
     */
    public Optional<AttendanceLog> findByUserIdAndWorkDate(Long userId, LocalDate workDate) throws SQLException {
        if (userId == null || workDate == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM attendance_logs WHERE user_id = ? AND work_date = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(workDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding attendance log by user {} and work date {}: {}", userId, workDate, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attendance logs theo user ID
     */
    public List<AttendanceLog> findByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        List<AttendanceLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM attendance_logs WHERE user_id = ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToEntity(rs));
                }
            }
            
            logger.debug("Found {} attendance logs for user {}", logs.size(), userId);
            return logs;
            
        } catch (SQLException e) {
            logger.error("Error finding attendance logs by user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attendance logs theo user ID và khoảng thời gian
     */
    public List<AttendanceLog> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        if (userId == null || startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        
        List<AttendanceLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM attendance_logs WHERE user_id = ? AND work_date BETWEEN ? AND ? ORDER BY work_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToEntity(rs));
                }
            }
            
            logger.debug("Found {} attendance logs for user {} between {} and {}", 
                        logs.size(), userId, startDate, endDate);
            return logs;
            
        } catch (SQLException e) {
            logger.error("Error finding attendance logs by user {} and date range {}-{}: {}", 
                        userId, startDate, endDate, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attendance logs theo work date
     */
    public List<AttendanceLog> findByWorkDate(LocalDate workDate) throws SQLException {
        if (workDate == null) {
            return new ArrayList<>();
        }
        
        List<AttendanceLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM attendance_logs WHERE work_date = ? ORDER BY user_id";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(workDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToEntity(rs));
                }
            }
            
            logger.debug("Found {} attendance logs for work date {}", logs.size(), workDate);
            return logs;
            
        } catch (SQLException e) {
            logger.error("Error finding attendance logs by work date {}: {}", workDate, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attendance logs theo status
     */
    public List<AttendanceLog> findByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<AttendanceLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM attendance_logs WHERE status = ? ORDER BY work_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToEntity(rs));
                }
            }
            
            logger.debug("Found {} attendance logs with status {}", logs.size(), status);
            return logs;
            
        } catch (SQLException e) {
            logger.error("Error finding attendance logs by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật check-in time
     */
    public boolean updateCheckIn(Long logId, LocalDateTime checkInTime) throws SQLException {
        if (logId == null || checkInTime == null) {
            return false;
        }
        
        String sql = "UPDATE attendance_logs SET check_in_time = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setTimestamp(stmt, 1, checkInTime);
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, logId);
            
            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;
            
            if (updated) {
                logger.info("Updated check-in time for attendance log {}", logId);
            }
            
            return updated;
            
        } catch (SQLException e) {
            logger.error("Error updating check-in time for log {}: {}", logId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Cập nhật check-out time
     */
    public boolean updateCheckOut(Long logId, LocalDateTime checkOutTime) throws SQLException {
        if (logId == null || checkOutTime == null) {
            return false;
        }
        
        String sql = "UPDATE attendance_logs SET check_out_time = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setTimestamp(stmt, 1, checkOutTime);
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, logId);
            
            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;
            
            if (updated) {
                logger.info("Updated check-out time for attendance log {}", logId);
            }
            
            return updated;
            
        } catch (SQLException e) {
            logger.error("Error updating check-out time for log {}: {}", logId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tính tổng working hours theo user và khoảng thời gian
     */
    public double getTotalWorkingHours(Long userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        if (userId == null || startDate == null || endDate == null) {
            return 0.0;
        }
        
        String sql = "SELECT SUM(working_hours) FROM attendance_logs " +
                    "WHERE user_id = ? AND work_date BETWEEN ? AND ? AND working_hours IS NOT NULL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
            
            return 0.0;
            
        } catch (SQLException e) {
            logger.error("Error calculating total working hours for user {} between {}-{}: {}", 
                        userId, startDate, endDate, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tính tổng overtime hours theo user và khoảng thời gian
     */
    public double getTotalOvertimeHours(Long userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        if (userId == null || startDate == null || endDate == null) {
            return 0.0;
        }
        
        String sql = "SELECT SUM(overtime_hours) FROM attendance_logs " +
                    "WHERE user_id = ? AND work_date BETWEEN ? AND ? AND overtime_hours IS NOT NULL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
            
            return 0.0;
            
        } catch (SQLException e) {
            logger.error("Error calculating total overtime hours for user {} between {}-{}: {}", 
                        userId, startDate, endDate, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm số ngày làm việc theo user và khoảng thời gian
     */
    public long countWorkingDays(Long userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        if (userId == null || startDate == null || endDate == null) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM attendance_logs " +
                    "WHERE user_id = ? AND work_date BETWEEN ? AND ? AND status != 'ABSENT'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting working days for user {} between {}-{}: {}", 
                        userId, startDate, endDate, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attendance logs chưa hoàn thành (chưa check-out)
     */
    public List<AttendanceLog> findIncompleteAttendance() throws SQLException {
        List<AttendanceLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM attendance_logs WHERE check_in_time IS NOT NULL AND check_out_time IS NULL ORDER BY work_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                logs.add(mapResultSetToEntity(rs));
            }
            
            logger.debug("Found {} incomplete attendance logs", logs.size());
            return logs;
            
        } catch (SQLException e) {
            logger.error("Error finding incomplete attendance logs: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Xóa attendance logs cũ (trước một ngày cụ thể)
     */
    public int deleteOldLogs(LocalDate beforeDate) throws SQLException {
        if (beforeDate == null) {
            return 0;
        }
        
        String sql = "DELETE FROM attendance_logs WHERE work_date < ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(beforeDate));
            
            int deletedRows = stmt.executeUpdate();
            
            if (deletedRows > 0) {
                logger.info("Deleted {} old attendance logs before {}", deletedRows, beforeDate);
            }
            
            return deletedRows;
            
        } catch (SQLException e) {
            logger.error("Error deleting old attendance logs before {}: {}", beforeDate, e.getMessage(), e);
            throw e;
        }
    }
}