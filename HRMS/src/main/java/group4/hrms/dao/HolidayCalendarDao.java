package group4.hrms.dao;

import group4.hrms.model.HolidayCalendar;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng holiday_calendar
 * 
 * @author Group4
 */
public class HolidayCalendarDao extends BaseDao<HolidayCalendar, Long> {
    
    @Override
    protected String getTableName() {
        return "holiday_calendar";
    }
    
    @Override
    protected HolidayCalendar mapResultSetToEntity(ResultSet rs) throws SQLException {
        HolidayCalendar calendar = new HolidayCalendar();
        calendar.setId(rs.getLong("id"));
        calendar.setYear(rs.getInt("year"));
        calendar.setName(rs.getString("name"));
        calendar.setCreatedAt(getLocalDateTime(rs, "created_at"));
        
        return calendar;
    }
    
    @Override
    protected void setEntityId(HolidayCalendar calendar, Long id) {
        calendar.setId(id);
    }
    
    @Override
    protected Long getEntityId(HolidayCalendar calendar) {
        return calendar.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO holiday_calendar (year, name, created_at) VALUES (?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE holiday_calendar SET year = ?, name = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, HolidayCalendar calendar) throws SQLException {
        stmt.setInt(1, calendar.getYear());
        stmt.setString(2, calendar.getName());
        setTimestamp(stmt, 3, calendar.getCreatedAt() != null ? calendar.getCreatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, HolidayCalendar calendar) throws SQLException {
        stmt.setInt(1, calendar.getYear());
        stmt.setString(2, calendar.getName());
        stmt.setLong(3, calendar.getId());
    }
    
    // Business methods
    
    /**
     * Tìm calendar theo năm
     */
    public Optional<HolidayCalendar> findByYear(Integer year) throws SQLException {
        if (year == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM holiday_calendar WHERE year = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding calendar by year {}: {}", year, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm current year calendar
     */
    public Optional<HolidayCalendar> findCurrentYearCalendar() throws SQLException {
        return findByYear(LocalDateTime.now().getYear());
    }
    
    /**
     * Tìm tất cả calendars
     */
    public List<HolidayCalendar> findAllOrderByYear() throws SQLException {
        List<HolidayCalendar> calendars = new ArrayList<>();
        String sql = "SELECT * FROM holiday_calendar ORDER BY year DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                calendars.add(mapResultSetToEntity(rs));
            }
            
            return calendars;
            
        } catch (SQLException e) {
            logger.error("Error finding all calendars: {}", e.getMessage(), e);
            throw e;
        }
    }
}