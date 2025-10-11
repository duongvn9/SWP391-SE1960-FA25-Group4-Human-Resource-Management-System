package group4.hrms.dao;

import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
        log.setCheckType(rs.getString("check_type"));

        // checked_at
        Timestamp checkedAtTs = rs.getTimestamp("checked_at");
        if (checkedAtTs != null) {
            log.setCheckedAt(checkedAtTs.toLocalDateTime());
        }

        log.setSource(rs.getString("source"));
        log.setNote(rs.getString("note"));

        // period_id có thể null
        long periodId = rs.getLong("period_id");
        if (!rs.wasNull()) {
            log.setPeriodId(periodId);
        }

        // created_at
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            log.setCreatedAt(createdAtTs.toLocalDateTime());
        }

        return log;
    }

    @Override
    protected String createInsertSql() {
        return "INSERT INTO attendance_logs ("
                + "user_id, check_type, checked_at, source, note, period_id"
                + ") VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE attendance_logs SET "
                + "user_id = ?, "
                + "check_type = ?, "
                + "checked_at = ?, "
                + "source = ?, "
                + "note = ?, "
                + "period_id = ? "
                + "WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, AttendanceLog log) throws SQLException {
        stmt.setLong(1, log.getUserId());
        stmt.setString(2, log.getCheckType());

        // checked_at
        if (log.getCheckedAt() != null) {
            stmt.setTimestamp(3, Timestamp.valueOf(log.getCheckedAt()));
        } else {
            stmt.setNull(3, Types.TIMESTAMP);
        }

        // source (có thể null)
        if (log.getSource() != null) {
            stmt.setString(4, log.getSource());
        } else {
            stmt.setNull(4, Types.VARCHAR);
        }

        // note (có thể null)
        if (log.getNote() != null) {
            stmt.setString(5, log.getNote());
        } else {
            stmt.setNull(5, Types.VARCHAR);
        }

        // period_id (có thể null)
        if (log.getPeriodId() != null) {
            stmt.setLong(6, log.getPeriodId());
        } else {
            stmt.setNull(6, Types.BIGINT);
        }
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, AttendanceLog log) throws SQLException {
        stmt.setLong(1, log.getUserId());
        stmt.setString(2, log.getCheckType());

        // checked_at
        if (log.getCheckedAt() != null) {
            stmt.setTimestamp(3, Timestamp.valueOf(log.getCheckedAt()));
        } else {
            stmt.setNull(3, Types.TIMESTAMP);
        }

        // source (có thể null)
        if (log.getSource() != null) {
            stmt.setString(4, log.getSource());
        } else {
            stmt.setNull(4, Types.VARCHAR);
        }

        // note (có thể null)
        if (log.getNote() != null) {
            stmt.setString(5, log.getNote());
        } else {
            stmt.setNull(5, Types.VARCHAR);
        }

        // period_id (có thể null)
        if (log.getPeriodId() != null) {
            stmt.setLong(6, log.getPeriodId());
        } else {
            stmt.setNull(6, Types.BIGINT);
        }

        // id ở cuối để WHERE
        stmt.setLong(7, log.getId());
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

            try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

                // Thay vì log WorkDate, dùng checkedAt
                logger.info("Created new attendance log for user {} on {}",
                        entity.getUserId(), entity.getCheckedAt());
                return entity;

            } catch (SQLException e) {
                logger.error("Error saving attendance log: {}", e.getMessage(), e);
                throw e;
            }
        }
    }

    public List<AttendanceLogDto> findAllForOverview() throws SQLException {
        String sql = """
            SELECT
              u.id AS employee_id,
              u.employee_code AS employee_code,
              u.full_name AS employee_name,
              d.name AS department_name,
              DATE(al.checked_at) AS work_date,
              MIN(CASE WHEN al.check_type = 'IN'  THEN al.checked_at END)  AS check_in,
              MAX(CASE WHEN al.check_type = 'OUT' THEN al.checked_at END)  AS check_out,
              COALESCE(
                MIN(CASE WHEN al.check_type = 'IN'  THEN al.note END),
                MAX(CASE WHEN al.check_type = 'OUT' THEN al.note END),
                'No Records'
              ) AS status,
              GROUP_CONCAT(DISTINCT al.source SEPARATOR ', ') AS source,
              tp.name AS period_name
            FROM attendance_logs al
            JOIN users u ON al.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
            GROUP BY
              u.id,
              u.employee_code,
              u.full_name,
              d.name,
              tp.name,
              DATE(al.checked_at)
            ORDER BY DATE(al.checked_at) DESC, u.full_name;
        """;

        List<AttendanceLogDto> results = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AttendanceLogDto dto = new AttendanceLogDto();
                dto.setEmployeeId(rs.getLong("employee_id"));
                dto.setEmployeeName(rs.getString("employee_name"));
                dto.setDepartment(rs.getString("department_name"));

                // Lấy LocalDate từ sql Date
                Date sqlDate = rs.getDate("work_date");
                dto.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);

                // Lấy LocalTime từ Timestamp
                Timestamp inTs = rs.getTimestamp("check_in");
                Timestamp outTs = rs.getTimestamp("check_out");
                dto.setCheckIn(inTs != null ? inTs.toLocalDateTime().toLocalTime() : null);
                dto.setCheckOut(outTs != null ? outTs.toLocalDateTime().toLocalTime() : null);

                dto.setStatus(rs.getString("status"));
                dto.setSource(rs.getString("source"));
                dto.setPeriod(rs.getString("period_name"));

                results.add(dto);
            }

        } catch (SQLException e) {
        }

        return results;
    }

    /**
     * Tìm attendance log theo user và work date
     */
    public Optional<AttendanceLog> findByUserIdAndWorkDate(Long userId, LocalDate workDate) throws SQLException {
        if (userId == null || workDate == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM attendance_logs WHERE user_id = ? AND work_date = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
    public List<AttendanceLogDto> findByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return Collections.emptyList();
        }

        String sql = """
        SELECT
          DATE(al.checked_at) AS work_date,
          MIN(CASE WHEN al.check_type = 'IN'  THEN al.checked_at END) AS check_in,
          MAX(CASE WHEN al.check_type = 'OUT' THEN al.checked_at END) AS check_out,
          COALESCE(
            MIN(CASE WHEN al.check_type = 'IN'  THEN al.note END),
            MAX(CASE WHEN al.check_type = 'OUT' THEN al.note END),
            'No Records'
          ) AS status,
          GROUP_CONCAT(DISTINCT al.source SEPARATOR ', ') AS source,
          tp.name AS period_name
        FROM attendance_logs al
        LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
        WHERE al.user_id = ?
        GROUP BY DATE(al.checked_at), tp.name
        ORDER BY DATE(al.checked_at) DESC;
    """;

        List<AttendanceLogDto> results = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceLogDto dto = new AttendanceLogDto();

                    Date sqlDate = rs.getDate("work_date");
                    dto.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);

                    Timestamp inTs = rs.getTimestamp("check_in");
                    Timestamp outTs = rs.getTimestamp("check_out");
                    dto.setCheckIn(inTs != null ? inTs.toLocalDateTime().toLocalTime() : null);
                    dto.setCheckOut(outTs != null ? outTs.toLocalDateTime().toLocalTime() : null);

                    dto.setStatus(rs.getString("status"));
                    dto.setSource(rs.getString("source"));
                    dto.setPeriod(rs.getString("period_name"));

                    results.add(dto);
                }
            }
        } catch (SQLException e) {
            throw e;
        }

        return results;
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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        String sql = "SELECT SUM(working_hours) FROM attendance_logs "
                + "WHERE user_id = ? AND work_date BETWEEN ? AND ? AND working_hours IS NOT NULL";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        String sql = "SELECT SUM(overtime_hours) FROM attendance_logs "
                + "WHERE user_id = ? AND work_date BETWEEN ? AND ? AND overtime_hours IS NOT NULL";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        String sql = "SELECT COUNT(*) FROM attendance_logs "
                + "WHERE user_id = ? AND work_date BETWEEN ? AND ? AND status != 'ABSENT'";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
