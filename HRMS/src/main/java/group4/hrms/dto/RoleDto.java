package group4.hrms.dto;

import java.time.LocalDateTime;

/**
 * DTO cho Role entity
 * Dùng để truyền dữ liệu giữa các tầng
 */
public class RoleDto {
    private Long id;
    private String code;
    private String name;
    private Integer priority;
    private Boolean isSystem;
    private LocalDateTime createdAt;

    // Constructors
    public RoleDto() {}

    public RoleDto(String code, String name, Integer priority) {
        this.code = code;
        this.name = name;
        this.priority = priority;
    }

    // Static factory methods
    public static RoleDto createNew(String code, String name, Integer priority) {
        return new RoleDto(code, name, priority);
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

    @Override
    public String toString() {
        return "RoleDto{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", isSystem=" + isSystem +
                '}';
    }
}