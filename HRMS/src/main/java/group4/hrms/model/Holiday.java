package group4.hrms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity mapping bảng holidays
 * Các ngày nghỉ lễ cụ thể trong lịch
 * 
 * @author Group4
 */
public class Holiday {
    private Long id;
    private Long calendarId;           // FK tới holiday_calendar
    private LocalDate dateHoliday;     // ngày nghỉ lễ
    private String name;               // tên ngày lễ
    private LocalDateTime createdAt;
    
    // Constructors
    public Holiday() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Holiday(Long calendarId, LocalDate dateHoliday, String name) {
        this();
        this.calendarId = calendarId;
        this.dateHoliday = dateHoliday;
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCalendarId() {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }
    
    public LocalDate getDateHoliday() {
        return dateHoliday;
    }
    
    public void setDateHoliday(LocalDate dateHoliday) {
        this.dateHoliday = dateHoliday;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    public boolean isToday() {
        return dateHoliday != null && dateHoliday.equals(LocalDate.now());
    }
    
    public boolean isPast() {
        return dateHoliday != null && dateHoliday.isBefore(LocalDate.now());
    }
    
    public boolean isUpcoming() {
        return dateHoliday != null && dateHoliday.isAfter(LocalDate.now());
    }
    
    public int getDaysFromNow() {
        if (dateHoliday == null) return 0;
        return (int) LocalDate.now().datesUntil(dateHoliday).count();
    }
    
    @Override
    public String toString() {
        return "Holiday{" +
                "id=" + id +
                ", calendarId=" + calendarId +
                ", dateHoliday=" + dateHoliday +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}