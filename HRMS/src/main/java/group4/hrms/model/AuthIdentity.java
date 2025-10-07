package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity mapping bảng auth_identities
 * Quản lý các identity xác thực của account (local, google, etc.)
 * 
 * @author Group4
 */
public class AuthIdentity {
    private Long id;
    private Long accountId;
    private String provider;           // "local", "google", "microsoft", etc.
    private String providerUserId;     // user ID từ provider
    private String email;              // email từ provider
    private Boolean emailVerified;     // email đã verify chưa
    private LocalDateTime createdAt;
    
    // Constructors
    public AuthIdentity() {}
    
    public AuthIdentity(Long accountId, String provider, String providerUserId) {
        this.accountId = accountId;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.emailVerified = false;
        this.createdAt = LocalDateTime.now();
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
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getProviderUserId() {
        return providerUserId;
    }
    
    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Boolean getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    public boolean isLocalProvider() {
        return "local".equalsIgnoreCase(this.provider);
    }
    
    public boolean isEmailVerified() {
        return this.emailVerified != null && this.emailVerified;
    }
    
    @Override
    public String toString() {
        return "AuthIdentity{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", provider='" + provider + '\'' +
                ", providerUserId='" + providerUserId + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", createdAt=" + createdAt +
                '}';
    }
}