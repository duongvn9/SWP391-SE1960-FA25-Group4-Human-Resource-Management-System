package group4.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity mapping bảng leave_ledger
 * Ghi sổ các thay đổi về số ngày phép của nhân viên
 * 
 * @author Group4
 */
public class LeaveLedger {
    private Long id;
    private Long userId;               // nhân viên
    private Long leaveTypeId;          // loại phép  
    private Long requestId;            // request gây ra thay đổi (nullable)
    private BigDecimal deltaDays;      // số ngày thay đổi (+/-)
    private String note;               // ghi chú
    private LocalDateTime createdAt;
    
    // Constructors
    public LeaveLedger() {
        this.createdAt = LocalDateTime.now();
    }
    
    public LeaveLedger(Long userId, Long leaveTypeId, BigDecimal deltaDays, String note) {
        this();
        this.userId = userId;
        this.leaveTypeId = leaveTypeId;
        this.deltaDays = deltaDays;
        this.note = note;
    }
    
    // Getters and Setters
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
    
    public Long getLeaveTypeId() {
        return leaveTypeId;
    }
    
    public void setLeaveTypeId(Long leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }
    
    public Long getRequestId() {
        return requestId;
    }
    
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
    
    public BigDecimal getDeltaDays() {
        return deltaDays;
    }
    
    public void setDeltaDays(BigDecimal deltaDays) {
        this.deltaDays = deltaDays;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    public boolean isDeduction() {
        return deltaDays != null && deltaDays.compareTo(BigDecimal.ZERO) < 0;
    }
    
    public boolean isAddition() {
        return deltaDays != null && deltaDays.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isFromRequest() {
        return requestId != null;
    }
    
    public BigDecimal getAbsoluteDays() {
        return deltaDays != null ? deltaDays.abs() : BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "LeaveLedger{" +
                "id=" + id +
                ", userId=" + userId +
                ", leaveTypeId=" + leaveTypeId +
                ", requestId=" + requestId +
                ", deltaDays=" + deltaDays +
                ", note='" + note + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}