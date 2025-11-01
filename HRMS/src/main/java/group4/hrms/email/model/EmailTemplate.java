package group4.hrms.email.model;

import java.time.LocalDateTime;

/**
 * Entity mapping bảng email_templates
 * Lưu trữ các mẫu email cho các loại sự kiện khác nhau
 * 
 * @author Group4
 */
public class EmailTemplate {
    private Long id;
    private EmailEventType eventType;
    private String subject;
    private String htmlContent;
    private String textContent;
    private String placeholders; // JSON string
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public EmailTemplate() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public EmailTemplate(EmailEventType eventType, String subject, String htmlContent) {
        this();
        this.eventType = eventType;
        this.subject = subject;
        this.htmlContent = htmlContent;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmailEventType getEventType() {
        return eventType;
    }

    public void setEventType(EmailEventType eventType) {
        this.eventType = eventType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(String placeholders) {
        this.placeholders = placeholders;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
    public boolean hasTextContent() {
        return textContent != null && !textContent.trim().isEmpty();
    }

    public boolean hasPlaceholders() {
        return placeholders != null && !placeholders.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "EmailTemplate{" +
                "id=" + id +
                ", eventType=" + eventType +
                ", subject='" + subject + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
