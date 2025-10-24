package group4.hrms.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Setting entity - Cấu hình hệ thống (Department, Position, Role)
 * Tương ứng với bảng settings trong database
 */
public class Setting {
    private Long id;
    private String name;
    private String type; // "Department", "Position"
    private String value; // Code cho Position, null cho Department
    private Integer priority; // Job level cho Position
    private String description; // Description cho Department và Position
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Setting() {}

    public Setting(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Setting(String name, String type, String value, Integer priority) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.priority = priority;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    public boolean isDepartment() {
        return "Department".equalsIgnoreCase(this.type);
    }

    public boolean isPosition() {
        return "Position".equalsIgnoreCase(this.type);
    }

    public boolean isRole() {
        return "Role".equalsIgnoreCase(this.type);
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setting setting = (Setting) o;
        return Objects.equals(id, setting.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Setting{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", priority=" + priority +
                '}';
    }
}
