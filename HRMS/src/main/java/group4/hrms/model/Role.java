package group4.hrms.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Role entity - Vai trò trong hệ thống
 * Tương ứng với bảng roles trong database
 */
public class Role {
    private Long id;
    private String code;
    private String name;
    private Integer priority;
    private Boolean isSystem;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Role() {}

    public Role(String code, String name, Integer priority) {
        this.code = code;
        this.name = name;
        this.priority = priority;
        this.isSystem = false;
    }

    public Role(String code, String name, Integer priority, Boolean isSystem) {
        this.code = code;
        this.name = name;
        this.priority = priority;
        this.isSystem = isSystem;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
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
    public boolean isSystemRole() {
        return this.isSystem != null && this.isSystem;
    }

    public boolean hasHigherPriorityThan(Role other) {
        if (other == null || other.getPriority() == null) return true;
        if (this.priority == null) return false;
        return this.priority > other.getPriority();
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(code, role.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", isSystem=" + isSystem +
                '}';
    }
}