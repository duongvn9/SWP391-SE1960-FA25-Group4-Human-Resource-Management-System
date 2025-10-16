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
    private Boolean isSubstitute;      // TRUE nếu là ngày nghỉ bù (compensatory day)
    private LocalDate originalHolidayDate;  // Ngày lễ gốc (nếu là nghỉ bù)
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

    public Boolean getIsSubstitute() {
        return isSubstitute;
    }

    public void setIsSubstitute(Boolean isSubstitute) {
        this.isSubstitute = isSubstitute;
    }

    public LocalDate getOriginalHolidayDate() {
        return originalHolidayDate;
    }

    public void setOriginalHolidayDate(LocalDate originalHolidayDate) {
        this.originalHolidayDate = originalHolidayDate;
    }

    // Business methods

    /**
     * Check if this is a substitute/compensatory day
     * Substitute days have OT multiplier of 200%, not 300%
     */
    public boolean isSubstituteDay() {
        return Boolean.TRUE.equals(isSubstitute);
    }

    /**
     * Check if this is an original public holiday
     * Original holidays have OT multiplier of 300%
     */
    public boolean isOriginalHoliday() {
        return !isSubstituteDay();
    }

    /**
     * Get OT multiplier for this holiday
     * - Original holiday: 3.0 (300%)
     * - Substitute day: 2.0 (200%)
     */
    public double getOTMultiplier() {
        return isSubstituteDay() ? 2.0 : 3.0;
    }

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