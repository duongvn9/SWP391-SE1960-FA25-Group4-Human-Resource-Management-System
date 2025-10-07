package group4.hrms.dto;

import group4.hrms.model.Account;
import java.time.LocalDateTime;

/**
 * DTO cho Account entity
 * Dùng để truyền dữ liệu tài khoản giữa các tầng
 */
public class AccountDto {
    private Long id;
    private String username;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private Long roleId;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User info (từ join với User table)
    private Long userId;
    private String fullName;
    private String emailCompany;

    // Constructors
    public AccountDto() {}

    public AccountDto(String username, Long roleId) {
        this.username = username;
        this.roleId = roleId;
        this.isActive = true;
    }

    // Static factory methods
    public static AccountDto createNew(String username, Long roleId) {
        return new AccountDto(username, roleId);
    }
    
    public static AccountDto fromAccount(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setUsername(account.getUsername());
        dto.setIsActive(account.isActive());
        dto.setLastLogin(account.getLastLoginAt());
        dto.setRoleId(account.getUserId()); // Account không có roleId, dùng userId
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailCompany() {
        return emailCompany;
    }

    public void setEmailCompany(String emailCompany) {
        this.emailCompany = emailCompany;
    }

    // Business methods
    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }

    public String getDisplayName() {
        if (this.fullName != null && !this.fullName.trim().isEmpty()) {
            return this.fullName + " (" + this.username + ")";
        }
        return this.username;
    }

    public String getStatusText() {
        return isActive() ? "Hoạt động" : "Tạm dừng";
    }

    public String getStatusBadgeClass() {
        return isActive() ? "badge bg-success" : "badge bg-secondary";
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                ", roleName='" + roleName + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}