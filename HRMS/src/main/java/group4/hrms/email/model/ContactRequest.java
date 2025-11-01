package group4.hrms.email.model;

import java.time.LocalDateTime;

/**
 * Entity mapping bảng contact_requests
 * Lưu trữ các yêu cầu liên hệ từ website visitors
 * 
 * @author Group4
 */
public class ContactRequest {
    private String id; // UUID
    private String fullName;
    private String email;
    private String phone;
    private ContactType contactType;
    private String subject;
    private String message;
    private ContactStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ContactRequest() {
        this.status = ContactStatus.NEW;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ContactRequest(String fullName, String email, ContactType contactType, String subject, String message) {
        this();
        this.fullName = fullName;
        this.email = email;
        this.contactType = contactType;
        this.subject = subject;
        this.message = message;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ContactStatus getStatus() {
        return status;
    }

    public void setStatus(ContactStatus status) {
        this.status = status;
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
    public boolean isNew() {
        return this.status == ContactStatus.NEW;
    }

    public boolean isInProgress() {
        return this.status == ContactStatus.IN_PROGRESS;
    }

    public boolean isResolved() {
        return this.status == ContactStatus.RESOLVED;
    }

    public boolean hasPhone() {
        return phone != null && !phone.trim().isEmpty();
    }

    public boolean isUrgent() {
        return contactType == ContactType.COMPLAINT;
    }

    @Override
    public String toString() {
        return "ContactRequest{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", contactType=" + contactType +
                ", subject='" + subject + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
