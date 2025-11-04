package group4.hrms.email.model;

import java.time.LocalDateTime;

/**
 * Entity mapping bảng email_queue
 * Hàng đợi email chờ gửi
 * 
 * @author Group4
 */
public class EmailQueue {
    private Long id;
    private String recipientEmail;
    private String subject;
    private String content;
    private EmailStatus status;
    private int retryCount;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private String errorMessage;
    private String referenceId; // Link to contact_request or application
    private LocalDateTime createdAt;

    // Constructors
    public EmailQueue() {
        this.status = EmailStatus.PENDING;
        this.retryCount = 0;
        this.scheduledAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public EmailQueue(String recipientEmail, String subject, String content) {
        this();
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    // Alias methods for compatibility
    public int getAttempts() {
        return retryCount;
    }

    public void setAttempts(int attempts) {
        this.retryCount = attempts;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Business methods
    public boolean isPending() {
        return status == EmailStatus.PENDING;
    }

    public boolean isSent() {
        return status == EmailStatus.SENT;
    }

    public boolean isFailed() {
        return status == EmailStatus.FAILED;
    }

    public boolean canRetry() {
        return retryCount < 3 && (status == EmailStatus.PENDING || status == EmailStatus.RETRY);
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean isReadyToSend() {
        return (status == EmailStatus.PENDING || status == EmailStatus.RETRY)
                && scheduledAt.isBefore(LocalDateTime.now());
    }

    public void markAsSent() {
        this.status = EmailStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    public void markAsFailed(String error) {
        if (canRetry()) {
            this.status = EmailStatus.RETRY;
            this.errorMessage = error;
            // Schedule retry with exponential backoff: 5, 10, 15 minutes
            this.scheduledAt = LocalDateTime.now().plusMinutes((retryCount + 1) * 5L);
        } else {
            this.status = EmailStatus.FAILED;
            this.errorMessage = error;
        }
    }

    @Override
    public String toString() {
        return "EmailQueue{" +
                "id=" + id +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", subject='" + subject + '\'' +
                ", status=" + status +
                ", retryCount=" + retryCount +
                ", scheduledAt=" + scheduledAt +
                '}';
    }
}
