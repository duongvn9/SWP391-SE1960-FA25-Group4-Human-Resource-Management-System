package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity mapping bảng auth_local_credentials  
 * Lưu password hash cho local authentication
 * 
 * @author Group4
 */
public class AuthLocalCredentials {
    private Long identityId;           // PK và FK tới auth_identities
    private String passwordHash;       // BCrypt hash
    private LocalDateTime passwordUpdatedAt;
    
    // Constructors
    public AuthLocalCredentials() {}
    
    public AuthLocalCredentials(Long identityId, String passwordHash) {
        this.identityId = identityId;
        this.passwordHash = passwordHash;
        this.passwordUpdatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getIdentityId() {
        return identityId;
    }
    
    public void setIdentityId(Long identityId) {
        this.identityId = identityId;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.passwordUpdatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }
    
    public void setPasswordUpdatedAt(LocalDateTime passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }
    
    // Security methods
    public boolean isPasswordExpired(int maxDaysValid) {
        if (passwordUpdatedAt == null || maxDaysValid <= 0) {
            return false;
        }
        return passwordUpdatedAt.plusDays(maxDaysValid).isBefore(LocalDateTime.now());
    }
    
    @Override
    public String toString() {
        return "AuthLocalCredentials{" +
                "identityId=" + identityId +
                ", passwordHash='[PROTECTED]'" +
                ", passwordUpdatedAt=" + passwordUpdatedAt +
                '}';
    }
}