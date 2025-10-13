package group4.hrms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity TimesheetPeriod - Kỳ chấm công Quản lý các kỳ tính lương (tháng, quý)
 */
public class TimesheetPeriod {

    private Long id;
    private String name;                  
    private LocalDate startDate;         
    private LocalDate endDate;           
    private Boolean isLocked;          
    private Long lockedBy;              
    private LocalDateTime lockedAt;      
    private LocalDateTime createdAt;    
    
    public TimesheetPeriod() {
    }

    public TimesheetPeriod(Long id, String name, LocalDate startDate, LocalDate endDate, Boolean isLocked, Long lockedBy, LocalDateTime lockedAt, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isLocked = isLocked;
        this.lockedBy = lockedBy;
        this.lockedAt = lockedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Long getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(Long lockedBy) {
        this.lockedBy = lockedBy;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
