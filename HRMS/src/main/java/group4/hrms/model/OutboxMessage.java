package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity mapping bảng outbox_messages
 * Message queue cho event publishing pattern
 * 
 * @author Group4
 */
public class OutboxMessage {
    private Long id;
    private String topic;              // topic/channel name
    private String payloadJson;        // message payload dạng JSON
    private String headersJson;        // headers dạng JSON (nullable)
    private String status;             // NEW, SENT, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;      // khi nào đã gửi (nullable)
    
    // Constructors
    public OutboxMessage() {
        this.status = "NEW";
        this.createdAt = LocalDateTime.now();
    }
    
    public OutboxMessage(String topic, String payloadJson) {
        this();
        this.topic = topic;
        this.payloadJson = payloadJson;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public String getPayloadJson() {
        return payloadJson;
    }
    
    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }
    
    public String getHeadersJson() {
        return headersJson;
    }
    
    public void setHeadersJson(String headersJson) {
        this.headersJson = headersJson;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    // Business methods
    public boolean isNew() {
        return "NEW".equalsIgnoreCase(this.status);
    }
    
    public boolean isSent() {
        return "SENT".equalsIgnoreCase(this.status);
    }
    
    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(this.status);
    }
    
    public boolean hasHeaders() {
        return headersJson != null && !headersJson.trim().isEmpty();
    }
    
    public void markAsSent() {
        this.status = "SENT";
        this.sentAt = LocalDateTime.now();
    }
    
    public void markAsFailed() {
        this.status = "FAILED";
    }
    
    public void retry() {
        this.status = "NEW";
        this.sentAt = null;
    }
    
    @Override
    public String toString() {
        return "OutboxMessage{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", status='" + status + '\'' +
                ", hasHeaders=" + hasHeaders() +
                ", createdAt=" + createdAt +
                ", sentAt=" + sentAt +
                '}';
    }
}