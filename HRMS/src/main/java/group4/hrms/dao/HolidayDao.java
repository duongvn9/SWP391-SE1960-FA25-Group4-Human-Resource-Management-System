package group4.hrms.dao;

import group4.hrms.model.Holiday;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class để xử lý các thao tác với bảng holidays
 *
 * @author Group4
 */
public class HolidayDao extends BaseDao<Holiday, Long> {

    @Override
    protected String getTableName() {
        return "holidays";
    }

    @Override
    protected Holiday mapResultSetToEntity(ResultSet rs) throws SQLException {
        Holiday holiday = new Holiday();
        holiday.setId(rs.getLong("id"));
        holiday.setCalendarId(rs.getLong("calendar_id"));
        holiday.setDateHoliday(getLocalDate(rs, "date_holiday"));
        holiday.setName(rs.getString("name"));

        // Handle is_substitute field (may not exist in old schema)
        try {
            Boolean isSubstitute = (Boolean) rs.getObject("is_substitute");
            holiday.setIsSubstitute(isSubstitute != null ? isSubstitute : false);
        } catch (SQLException e) {
            holiday.setIsSubstitute(false);  // Default to false if column doesn't exist
        }

        // Handle original_holiday_date field (may not exist in old schema)
        try {
            holiday.setOriginalHolidayDate(getLocalDate(rs, "original_holiday_date"));
        } catch (SQLException e) {
            holiday.setOriginalHolidayDate(null);  // Default to null if column doesn't exist
        }

        holiday.setCreatedAt(getLocalDateTime(rs, "created_at"));

        return holiday;
    }

    @Override
    protected void setEntityId(Holiday holiday, Long id) {
        holiday.setId(id);
    }

    @Override
    protected Long getEntityId(Holiday holiday) {
        return holiday.getId();
    }

    @Override
    protected String createInsertSql() {
        return "INSERT INTO holidays (calendar_id, date_holiday, name, is_substitute, original_holiday_date, created_at) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE holidays SET calendar_id = ?, date_holiday = ?, name = ?, is_substitute = ?, original_holiday_date = ? WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Holiday holiday) throws SQLException {
        stmt.setLong(1, holiday.getCalendarId());
        setDate(stmt, 2, holiday.getDateHoliday());
        stmt.setString(3, holiday.getName());
        stmt.setBoolean(4, holiday.getIsSubstitute() != null ? holiday.getIsSubstitute() : false);
        setDate(stmt, 5, holiday.getOriginalHolidayDate());
        setTimestamp(stmt, 6, holiday.getCreatedAt() != null ? holiday.getCreatedAt() : LocalDateTime.now());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Holiday holiday) throws SQLException {
        stmt.setLong(1, holiday.getCalendarId());
        setDate(stmt, 2, holiday.getDateHoliday());
        stmt.setString(3, holiday.getName());
        stmt.setBoolean(4, holiday.getIsSubstitute() != null ? holiday.getIsSubstitute() : false);
        setDate(stmt, 5, holiday.getOriginalHolidayDate());
        stmt.setLong(6, holiday.getId());
    }

    // Business methods

    /**
     * Tìm holidays theo calendar ID
     */
    public List<Holiday> findByCalendarId(Long calendarId) throws SQLException {
        if (calendarId == null) {
            return new ArrayList<>();
        }

        List<Holiday> holidays = new ArrayList<>();
        String sql = "SELECT * FROM holidays WHERE calendar_id = ? ORDER BY date_holiday";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, calendarId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    holidays.add(mapResultSetToEntity(rs));
                }
            }

            return holidays;

        } catch (SQLException e) {
            logger.error("Error finding holidays by calendar ID {}: {}", calendarId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm holidays trong khoảng thời gian
     */
    public List<Holiday> findByDateRange(Long calendarId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        if (calendarId == null || fromDate == null || toDate == null) {
            return new ArrayList<>();
        }

        List<Holiday> holidays = new ArrayList<>();
        String sql = "SELECT * FROM holidays WHERE calendar_id = ? AND date_holiday >= ? AND date_holiday <= ? ORDER BY date_holiday";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, calendarId);
            setDate(stmt, 2, fromDate);
            setDate(stmt, 3, toDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    holidays.add(mapResultSetToEntity(rs));
                }
            }

            return holidays;

        } catch (SQLException e) {
            logger.error("Error finding holidays by date range for calendar {}: {}", calendarId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Kiểm tra ngày có phải holiday không
     */
    public boolean isHoliday(Long calendarId, LocalDate date) throws SQLException {
        if (calendarId == null || date == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM holidays WHERE calendar_id = ? AND date_holiday = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, calendarId);
            setDate(stmt, 2, date);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking holiday for calendar {} and date {}: {}", calendarId, date, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Kiểm tra holiday đã tồn tại chưa
     */
    public boolean exists(Long calendarId, LocalDate date) throws SQLException {
        return isHoliday(calendarId, date);
    }

    /**
     * Kiểm tra ngày có phải ngày lễ không (không cần calendarId)
     * Tự động lấy calendar của năm hiện tại
     */
    public boolean isHoliday(LocalDate date) throws SQLException {
        if (date == null) {
            return false;
        }

        String sql = """
            SELECT COUNT(*) FROM holidays h
            INNER JOIN holiday_calendar hc ON h.calendar_id = hc.id
            WHERE hc.year = ? AND h.date_holiday = ?
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, date.getYear());
            setDate(stmt, 2, date);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking holiday for date {}: {}", date, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Kiểm tra ngày có phải ngày nghỉ bù không
     * Ngày nghỉ bù là ngày có tên chứa "Nghỉ bù" hoặc "nghi bu"
     */
    public boolean isCompensatoryDay(LocalDate date) throws SQLException {
        if (date == null) {
            return false;
        }

        String sql = """
            SELECT COUNT(*) FROM holidays h
            INNER JOIN holiday_calendar hc ON h.calendar_id = hc.id
            WHERE hc.year = ? AND h.date_holiday = ?
            AND (LOWER(h.name) LIKE '%nghỉ bù%' OR LOWER(h.name) LIKE '%nghi bu%')
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, date.getYear());
            setDate(stmt, 2, date);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking compensatory day for date {}: {}", date, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy danh sách holidays trong khoảng thời gian (không cần calendarId)
     * Tự động lấy calendar theo năm của từng ngày
     */
    public List<Holiday> getHolidaysInRange(LocalDate start, LocalDate end) throws SQLException {
        if (start == null || end == null) {
            return new ArrayList<>();
        }

        List<Holiday> holidays = new ArrayList<>();
        String sql = """
            SELECT h.* FROM holidays h
            INNER JOIN holiday_calendar hc ON h.calendar_id = hc.id
            WHERE h.date_holiday >= ? AND h.date_holiday <= ?
            ORDER BY h.date_holiday
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setDate(stmt, 1, start);
            setDate(stmt, 2, end);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    holidays.add(mapResultSetToEntity(rs));
                }
            }

            return holidays;

        } catch (SQLException e) {
            logger.error("Error getting holidays in range {} to {}: {}", start, end, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm holiday theo ngày cụ thể (không cần calendarId)
     * Tự động lấy calendar của năm tương ứng
     *
     * @param date Ngày cần tìm
     * @return Holiday object nếu tìm thấy, null nếu không
     */
    public Holiday findByDate(LocalDate date) throws SQLException {
        if (date == null) {
            return null;
        }

        String sql = """
            SELECT h.* FROM holidays h
            INNER JOIN holiday_calendar hc ON h.calendar_id = hc.id
            WHERE hc.year = ? AND h.date_holiday = ?
            LIMIT 1
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, date.getYear());
            setDate(stmt, 2, date);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            logger.error("Error finding holiday by date {}: {}", date, e.getMessage(), e);
            throw e;
        }
    }
}