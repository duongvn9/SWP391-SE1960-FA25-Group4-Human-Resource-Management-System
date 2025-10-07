package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity mapping bảng holiday_calendar
 * Lịch nghỉ lễ theo năm
 * 
 * @author Group4
 */
public class HolidayCalendar {
    private Long id;
    private Integer year;              // năm lịch
    private String name;               // tên lịch (VD: "Lịch nghỉ lễ 2025")
    private LocalDateTime createdAt;
    
    // Constructors
    public HolidayCalendar() {
        this.createdAt = LocalDateTime.now();
    }
    
    public HolidayCalendar(Integer year, String name) {
        this();
        this.year = year;
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
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
    public boolean isCurrentYear() {
        return year != null && year == LocalDateTime.now().getYear();
    }
    
    public boolean isPastYear() {
        return year != null && year < LocalDateTime.now().getYear();
    }
    
    public boolean isFutureYear() {
        return year != null && year > LocalDateTime.now().getYear();
    }
    
    @Override
    public String toString() {
        return "HolidayCalendar{" +
                "id=" + id +
                ", year=" + year +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}