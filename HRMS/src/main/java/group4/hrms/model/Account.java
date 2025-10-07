package group4.hrms.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Account entity - Tài khoản đăng nhập
 * Tương ứng với bảng accounts trong database
 */
public class Account {
    private Long id;
    private Long userId;
    private String username;
    private String emailLogin;
    private Long departmentId;
    private Long positionId;
    private String status;
    private Integer failedAttempts;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enums for status
    public enum Status {
        ACTIVE("active"),
        INACTIVE("inactive"),
        LOCKED("locked"),
        SUSPENDED("suspended");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Status fromValue(String value) {
            for (Status status : Status.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return ACTIVE; // default
        }
    }

    // Constructors
    public Account() {}

    public Account(Long userId, String username, String emailLogin) {
        this.userId = userId;
        this.username = username;
        this.emailLogin = emailLogin;
        this.status = Status.ACTIVE.getValue();
        this.failedAttempts = 0;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailLogin() {
        return emailLogin;
    }

    public void setEmailLogin(String emailLogin) {
        this.emailLogin = emailLogin;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
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
    public boolean isActive() {
        return Status.ACTIVE.getValue().equals(this.status);
    }

    public boolean isLocked() {
        return Status.LOCKED.getValue().equals(this.status);
    }

    public boolean isSuspended() {
        return Status.SUSPENDED.getValue().equals(this.status);
    }

    public void activate() {
        this.status = Status.ACTIVE.getValue();
        this.failedAttempts = 0;
    }

    public void lock() {
        this.status = Status.LOCKED.getValue();
    }

    public void suspend() {
        this.status = Status.SUSPENDED.getValue();
    }

    public void incrementFailedAttempts() {
        this.failedAttempts = (this.failedAttempts == null) ? 1 : this.failedAttempts + 1;
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.resetFailedAttempts();
    }

    public boolean shouldLockAfterFailedAttempt(int maxAttempts) {
        return this.failedAttempts != null && this.failedAttempts >= maxAttempts;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(username, account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", emailLogin='" + emailLogin + '\'' +
                ", status='" + status + '\'' +
                ", failedAttempts=" + failedAttempts +
                ", lastLoginAt=" + lastLoginAt +
                '}';
    }
}