package group4.hrms.dao;

import group4.hrms.model.TimesheetPeriod;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            stmt.setBoolean(4, false); 
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
            stmt.setBoolean(4, false);
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

    @Override
    public Optional<TimesheetPeriod> findById(Long id) throws SQLException {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM timesheet_periods WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding timesheet period by id {}: {}", id, e.getMessage(), e);
            throw e;
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

    public TimesheetPeriod findCurrentPeriod() throws SQLException {
        String sql = """
        SELECT *
        FROM timesheet_periods
        WHERE date_start <= ? AND date_end >= ?
        ORDER BY date_start ASC
        LIMIT 1""";

        LocalDate today = LocalDate.now();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(today));
            stmt.setDate(2, Date.valueOf(today));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TimesheetPeriod p = new TimesheetPeriod();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setStartDate(rs.getDate("date_start").toLocalDate());
                    p.setEndDate(rs.getDate("date_end").toLocalDate());
                    p.setIsLocked(rs.getBoolean("is_locked"));
                    p.setLockedBy(rs.getLong("locked_by"));
                    Timestamp lockedAtTs = rs.getTimestamp("locked_at");
                    if (lockedAtTs != null) {
                        p.setLockedAt(lockedAtTs.toLocalDateTime());
                    }
                    Timestamp createdAtTs = rs.getTimestamp("created_at");
                    if (createdAtTs != null) {
                        p.setCreatedAt(createdAtTs.toLocalDateTime());
                    }
                    return p;
                }
            }
        }

        return null;
    }

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

    public TimesheetPeriod findPeriodByDate(LocalDate date) throws SQLException {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        String sql = """
        SELECT *
        FROM timesheet_periods
        WHERE date_start <= ? AND date_end >= ?
        ORDER BY date_start ASC
        LIMIT 1
    """;

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setDate(2, java.sql.Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TimesheetPeriod p = new TimesheetPeriod();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));

                    Date startDate = rs.getDate("date_start");
                    if (startDate != null) {
                        p.setStartDate(startDate.toLocalDate());
                    }

                    Date endDate = rs.getDate("date_end");
                    if (endDate != null) {
                        p.setEndDate(endDate.toLocalDate());
                    }

                    p.setIsLocked(rs.getObject("is_locked") != null ? rs.getBoolean("is_locked") : null);

                    long lockedBy = rs.getLong("locked_by");
                    if (!rs.wasNull()) {
                        p.setLockedBy(lockedBy);
                    }

                    Timestamp lockedAtTs = rs.getTimestamp("locked_at");
                    if (lockedAtTs != null) {
                        p.setLockedAt(lockedAtTs.toLocalDateTime());
                    }

                    Timestamp createdAtTs = rs.getTimestamp("created_at");
                    if (createdAtTs != null) {
                        p.setCreatedAt(createdAtTs.toLocalDateTime());
                    }

                    return p;
                }
            }
        }

        return null;
    }

    public boolean updateLockStatus(Long periodId, boolean isLocked, Long userId) throws SQLException {
        if (periodId == null) {
            throw new IllegalArgumentException("periodId cannot be null");
        }

        String sql = "UPDATE timesheet_periods "
                + "SET is_locked = ?, "
                + "locked_by = ?, "
                + "locked_at = ? "
                + "WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isLocked);

            if (isLocked) {
                stmt.setLong(2, userId != null ? userId : Types.NULL);
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                stmt.setNull(2, Types.BIGINT);
                stmt.setNull(3, Types.TIMESTAMP);
            }

            stmt.setLong(4, periodId);
            return stmt.executeUpdate() > 0;
        }
    }
}
