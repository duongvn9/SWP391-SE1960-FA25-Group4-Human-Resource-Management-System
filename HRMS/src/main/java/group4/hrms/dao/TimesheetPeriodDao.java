package group4.hrms.dao;

import group4.hrms.model.TimesheetPeriod;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng TimesheetPeriod
 *
 * @author Group4
 */
public class TimesheetPeriodDao extends BaseDao<TimesheetPeriod, Long> {

    @Override
    protected String getTableName() {
        return "timesheet_periods";
    }

    @Override
    protected TimesheetPeriod mapResultSetToEntity(ResultSet rs) throws SQLException {
        TimesheetPeriod period = new TimesheetPeriod();

        period.setId(rs.getLong("id"));
        period.setName(rs.getString("name"));

        Date startDate = rs.getDate("date_start");
        if (startDate != null) {
            period.setStartDate(startDate.toLocalDate());
        }

        Date endDate = rs.getDate("date_end");
        if (endDate != null) {
            period.setEndDate(endDate.toLocalDate());
        }

        // is_locked là BOOLEAN trong DB → lấy kiểu boolean
        period.setIsLocked(rs.getBoolean("is_locked"));

        Long lockedBy = rs.getLong("locked_by");
        if (!rs.wasNull()) {
            period.setLockedBy(lockedBy);
        }

        Timestamp lockedAt = rs.getTimestamp("locked_at");
        if (lockedAt != null) {
            period.setLockedAt(lockedAt.toLocalDateTime());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            period.setCreatedAt(createdAt.toLocalDateTime());
        }

        return period;
    }

    @Override
    protected String createInsertSql() {
        return "INSERT INTO timesheet_periods (name, date_start, date_end, is_locked, locked_by, locked_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE timesheet_periods "
                + "SET name = ?, date_start = ?, date_end = ?, is_locked = ?, locked_by = ?, locked_at = ? "
                + "WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, TimesheetPeriod period) throws SQLException {
        stmt.setString(1, period.getName());

        if (period.getStartDate() != null) {
            stmt.setDate(2, Date.valueOf(period.getStartDate()));
        } else {
            stmt.setNull(2, Types.DATE);
        }

        if (period.getEndDate() != null) {
            stmt.setDate(3, Date.valueOf(period.getEndDate()));
        } else {
            stmt.setNull(3, Types.DATE);
        }

        if (period.getIsLocked() != null) {
            stmt.setBoolean(4, period.getIsLocked());
        } else {
            stmt.setBoolean(4, false); // mặc định "open"
        }

        if (period.getLockedBy() != null) {
            stmt.setLong(5, period.getLockedBy());
        } else {
            stmt.setNull(5, Types.BIGINT);
        }

        if (period.getLockedAt() != null) {
            stmt.setTimestamp(6, Timestamp.valueOf(period.getLockedAt()));
        } else {
            stmt.setNull(6, Types.TIMESTAMP);
        }
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, TimesheetPeriod period) throws SQLException {
        stmt.setString(1, period.getName());

        if (period.getStartDate() != null) {
            stmt.setDate(2, Date.valueOf(period.getStartDate()));
        } else {
            stmt.setNull(2, Types.DATE);
        }

        if (period.getEndDate() != null) {
            stmt.setDate(3, Date.valueOf(period.getEndDate()));
        } else {
            stmt.setNull(3, Types.DATE);
        }

        if (period.getIsLocked() != null) {
            stmt.setBoolean(4, period.getIsLocked());
        } else {
            stmt.setBoolean(4, false); // mặc định "open"
        }

        if (period.getLockedBy() != null) {
            stmt.setLong(5, period.getLockedBy());
        } else {
            stmt.setNull(5, Types.BIGINT);
        }

        if (period.getLockedAt() != null) {
            stmt.setTimestamp(6, Timestamp.valueOf(period.getLockedAt()));
        } else {
            stmt.setNull(6, Types.TIMESTAMP);
        }

        stmt.setLong(7, period.getId());
    }

    @Override
    protected void setEntityId(TimesheetPeriod period, Long id) {
        period.setId(id);
    }

    @Override
    protected Long getEntityId(TimesheetPeriod period) {
        return period.getId();
    }

    /**
     * Override save method để xử lý insert/update
     */
    @Override
    public TimesheetPeriod save(TimesheetPeriod entity) throws SQLException {
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
                    throw new SQLException("Creating timesheet period failed, no rows affected");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long generatedId = generatedKeys.getLong(1);
                        setEntityId(entity, generatedId);
                    } else {
                        throw new SQLException("Creating timesheet period failed, no ID obtained");
                    }
                }

                logger.info("Created new timesheet period: {}", entity.getName());
                return entity;

            } catch (SQLException e) {
                logger.error("Error saving timesheet period: {}", e.getMessage(), e);
                throw e;
            }
        }
    }

    public Optional<Long> findIdByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT id FROM timesheet_periods WHERE name = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("id"));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Tìm timesheet period theo tên
     */
    public Optional<TimesheetPeriod> findByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM timesheet_periods WHERE name = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding timesheet period by name {}: {}", name, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm timesheet periods theo status
     */
    public List<TimesheetPeriod> findByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<TimesheetPeriod> periods = new ArrayList<>();
        String sql = "SELECT * FROM timesheet_periods WHERE status = ? ORDER BY start_date DESC";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    periods.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} timesheet periods with status {}", periods.size(), status);
            return periods;

        } catch (SQLException e) {
            logger.error("Error finding timesheet periods by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm timesheet periods theo khoảng thời gian
     */
    public List<TimesheetPeriod> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }

        List<TimesheetPeriod> periods = new ArrayList<>();
        String sql = "SELECT * FROM timesheet_periods WHERE start_date >= ? AND end_date <= ? ORDER BY start_date";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    periods.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} timesheet periods between {} and {}", periods.size(), startDate, endDate);
            return periods;

        } catch (SQLException e) {
            logger.error("Error finding timesheet periods by date range {}-{}: {}", startDate, endDate, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm timesheet period hiện tại (chứa ngày hôm nay)
     */
    public Optional<TimesheetPeriod> findCurrentPeriod() throws SQLException {
        LocalDate today = LocalDate.now();
        String sql = "SELECT * FROM timesheet_periods WHERE start_date <= ? AND end_date >= ? AND status = 'OPEN'";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(today));
            stmt.setDate(2, Date.valueOf(today));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding current timesheet period: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm timesheet period theo ngày cụ thể
     */
    public Optional<TimesheetPeriod> findPeriodByDate(LocalDate date) throws SQLException {
        if (date == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM timesheet_periods WHERE start_date <= ? AND end_date >= ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding timesheet period by date {}: {}", date, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm timesheet periods sắp kết thúc
     */
    public List<TimesheetPeriod> findUpcomingEndPeriods(int days) throws SQLException {
        List<TimesheetPeriod> periods = new ArrayList<>();
        LocalDate targetDate = LocalDate.now().plusDays(days);

        String sql = "SELECT * FROM timesheet_periods WHERE end_date <= ? AND status = 'OPEN' ORDER BY end_date";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(targetDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    periods.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} upcoming end periods within {} days", periods.size(), days);
            return periods;

        } catch (SQLException e) {
            logger.error("Error finding upcoming end periods: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Kiểm tra name đã tồn tại
     */
    public boolean existsByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM timesheet_periods WHERE name = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking exists by name {}: {}", name, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Kiểm tra khoảng thời gian có bị overlap không
     */
    public boolean existsOverlappingPeriod(LocalDate startDate, LocalDate endDate, Long excludeId) throws SQLException {
        if (startDate == null || endDate == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM timesheet_periods WHERE "
                + "((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?)) "
                + "AND id != ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            stmt.setDate(4, Date.valueOf(endDate));
            stmt.setLong(5, excludeId != null ? excludeId : -1);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking overlapping period {}-{}: {}", startDate, endDate, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật status của timesheet period
     */
    public boolean updateStatus(Long periodId, String status) throws SQLException {
        if (periodId == null || status == null || status.trim().isEmpty()) {
            return false;
        }

        String sql = "UPDATE timesheet_periods SET status = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.trim());
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, periodId);

            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;

            if (updated) {
                logger.info("Updated status for timesheet period {} to {}", periodId, status);
            }

            return updated;

        } catch (SQLException e) {
            logger.error("Error updating status for timesheet period {}: {}", periodId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Đóng timesheet period (chuyển status thành CLOSED)
     */
    public boolean closePeriod(Long periodId) throws SQLException {
        return updateStatus(periodId, "CLOSED");
    }

    /**
     * Khóa timesheet period (chuyển status thành LOCKED)
     */
    public boolean lockPeriod(Long periodId, Long lockedBy) throws SQLException {
        if (periodId == null) {
            return false;
        }

        String sql = "UPDATE timesheet_periods SET status = 'LOCKED', locked_at = ?, locked_by = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            setTimestamp(stmt, 1, now);

            if (lockedBy != null) {
                stmt.setLong(2, lockedBy);
            } else {
                stmt.setNull(2, Types.BIGINT);
            }

            setTimestamp(stmt, 3, now);
            stmt.setLong(4, periodId);

            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;

            if (updated) {
                logger.info("Locked timesheet period {} by user {}", periodId, lockedBy);
            }

            return updated;

        } catch (SQLException e) {
            logger.error("Error locking timesheet period {}: {}", periodId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mở lại timesheet period (chuyển status thành OPEN)
     */
    public boolean reopenPeriod(Long periodId) throws SQLException {
        return updateStatus(periodId, "OPEN");
    }

    /**
     * Đếm timesheet periods theo status
     */
    public long countByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM timesheet_periods WHERE status = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

            return 0;

        } catch (SQLException e) {
            logger.error("Error counting timesheet periods by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm kiếm timesheet periods theo từ khóa
     */
    public List<TimesheetPeriod> searchPeriods(String keyword, int offset, int limit) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        List<TimesheetPeriod> periods = new ArrayList<>();
        String sql = "SELECT * FROM timesheet_periods WHERE "
                + "name LIKE ? OR description LIKE ? "
                + "ORDER BY start_date DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword.trim() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setInt(3, limit);
            stmt.setInt(4, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    periods.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} timesheet periods matching keyword '{}'", periods.size(), keyword);
            return periods;

        } catch (SQLException e) {
            logger.error("Error searching timesheet periods with keyword {}: {}", keyword, e.getMessage(), e);
            throw e;
        }
    }
}
