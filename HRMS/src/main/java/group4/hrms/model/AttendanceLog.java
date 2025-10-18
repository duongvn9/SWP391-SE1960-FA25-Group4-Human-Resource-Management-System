package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity AttendanceLog - Bảng chấm công Ghi lại thời gian vào/ra của nhân viên
 */
public class AttendanceLog {

    private Long id;
    private Long userId;
    private String checkType; 
    private LocalDateTime checkedAt;
    private String source;
    private String note;
    private Long periodId;
    private LocalDateTime createdAt;

    public AttendanceLog() {
    }

    // Constructor không có id (thường dùng khi insert mới)
    public AttendanceLog(Long userId, String checkType, LocalDateTime checkedAt,
            String source, String note, Long periodId) {
        this.userId = userId;
        this.checkType = checkType;
        this.checkedAt = checkedAt;
        this.source = source;
        this.note = note;
        this.periodId = periodId;
    }

    // Constructor đầy đủ (thường dùng khi đọc dữ liệu từ DB)
    public AttendanceLog(Long id, Long userId, String checkType, LocalDateTime checkedAt,
            String source, String note, Long periodId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.checkType = checkType;
        this.checkedAt = checkedAt;
        this.source = source;
        this.note = note;
        this.periodId = periodId;
        this.createdAt = createdAt;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AttendanceLog{"
                + "id=" + id
                + ", userId=" + userId
                + ", checkType='" + checkType + '\''
                + ", checkedAt=" + checkedAt
                + ", source='" + source + '\''
                + ", note='" + note + '\''
                + ", periodId=" + periodId
                + ", createdAt=" + createdAt
                + '}';
    }
}
