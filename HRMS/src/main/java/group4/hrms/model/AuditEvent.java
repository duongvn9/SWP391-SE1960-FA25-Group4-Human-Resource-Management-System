package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity mapping bảng audit_events
 * Ghi log các hoạt động của user trong hệ thống
 * 
 * @author Group4
 */
public class AuditEvent {
    private Long id;
    private Long accountId;            // account thực hiện action (nullable)
    private String eventType;          // loại sự kiện: LOGIN, LOGOUT, CREATE, UPDATE, DELETE, etc.
    private String entityType;         // loại entity: USER, PAYSLIP, REQUEST, etc. (nullable)
    private Long entityId;             // ID của entity (nullable)
    private String ip;                 // IP address
    private String userAgent;          // User-Agent header
    private LocalDateTime createdAt;
    
    // Constructors
    public AuditEvent() {
        this.createdAt = LocalDateTime.now();
    }
    
    public AuditEvent(String eventType) {
        this();
        this.eventType = eventType;
    }
    
    public AuditEvent(Long accountId, String eventType, String entityType, Long entityId) {
        this();
        this.accountId = accountId;
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAccountId() {
        return accountId;
    }
    
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    public boolean isLoginEvent() {
        return "LOGIN".equalsIgnoreCase(this.eventType);
    }
    
    public boolean isLogoutEvent() {
        return "LOGOUT".equalsIgnoreCase(this.eventType);
    }
    
    public boolean isEntityRelated() {
        return entityType != null && entityId != null;
    }
    
    public boolean isSystemEvent() {
        return accountId == null;
    }
    
    public boolean isUserEvent() {
        return accountId != null;
    }
    
    @Override
    public String toString() {
        return "AuditEvent{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", eventType='" + eventType + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", ip='" + ip + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}