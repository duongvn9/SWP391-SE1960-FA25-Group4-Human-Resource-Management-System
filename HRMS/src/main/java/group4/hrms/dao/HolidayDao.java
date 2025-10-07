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
        return "INSERT INTO holidays (calendar_id, date_holiday, name, created_at) VALUES (?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE holidays SET calendar_id = ?, date_holiday = ?, name = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Holiday holiday) throws SQLException {
        stmt.setLong(1, holiday.getCalendarId());
        setDate(stmt, 2, holiday.getDateHoliday());
        stmt.setString(3, holiday.getName());
        setTimestamp(stmt, 4, holiday.getCreatedAt() != null ? holiday.getCreatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Holiday holiday) throws SQLException {
        stmt.setLong(1, holiday.getCalendarId());
        setDate(stmt, 2, holiday.getDateHoliday());
        stmt.setString(3, holiday.getName());
        stmt.setLong(4, holiday.getId());
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
}