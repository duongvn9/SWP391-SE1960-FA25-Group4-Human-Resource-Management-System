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
    private Integer tetDuration;       // số ngày Tết (default 7)
    private Boolean autoCompensatory;  // tự động tạo ngày nghỉ bù (default true)
    private Boolean isGenerated;       // đã generate holidays chưa (default false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public HolidayCalendar() {
        this.createdAt = LocalDateTime.now();
        this.tetDuration = 7;
        this.autoCompensatory = true;
        this.isGenerated = false;
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

    public Integer getTetDuration() {
        return tetDuration;
    }

    public void setTetDuration(Integer tetDuration) {
        this.tetDuration = tetDuration;
    }

    public Boolean getAutoCompensatory() {
        return autoCompensatory;
    }

    public Boolean isAutoCompensatory() {
        return autoCompensatory != null && autoCompensatory;
    }

    public void setAutoCompensatory(Boolean autoCompensatory) {
        this.autoCompensatory = autoCompensatory;
    }

    public Boolean getGenerated() {
        return isGenerated;
    }

    public Boolean isGenerated() {
        return isGenerated != null && isGenerated;
    }

    public void setGenerated(Boolean generated) {
        isGenerated = generated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
                ", tetDuration=" + tetDuration +
                ", autoCompensatory=" + autoCompensatory +
                ", isGenerated=" + isGenerated +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}