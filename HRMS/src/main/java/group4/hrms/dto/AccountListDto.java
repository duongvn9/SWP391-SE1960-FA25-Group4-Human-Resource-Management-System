package group4.hrms.dto;

import java.time.LocalDateTime;

/**
 * DTO for Account List display
 * Contains account data with joined user, department, and position information
 */
public class AccountListDto {
    private Long id;
    private String username;
    private String emailLogin;
    private Long userId;
    private String userFullName;
    private String departmentName;
    private String positionName;
    private String status;
    private LocalDateTime lastLoginAt;

    // Constructors
    public AccountListDto() {
    }

    public AccountListDto(Long id, String username, String emailLogin, Long userId,
            String userFullName, String departmentName, String positionName,
            String status, LocalDateTime lastLoginAt) {
        this.id = id;
        this.username = username;
        this.emailLogin = emailLogin;
        this.userId = userId;
        this.userFullName = userFullName;
        this.departmentName = departmentName;
        this.positionName = positionName;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
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

    public String getEmailLogin() {
        return emailLogin;
    }

    public void setEmailLogin(String emailLogin) {
        this.emailLogin = emailLogin;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    @Override
    public String toString() {
        return "AccountListDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", emailLogin='" + emailLogin + '\'' +
                ", userId=" + userId +
                ", userFullName='" + userFullName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", positionName='" + positionName + '\'' +
                ", status='" + status + '\'' +
                ", lastLoginAt=" + lastLoginAt +
                '}';
    }
}
