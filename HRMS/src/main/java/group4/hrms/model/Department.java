package group4.hrms.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Department entity - Phòng ban trong công ty
 * Tương ứng với bảng departments trong database
 */
public class Department {
    private Long id;
    private String name;
    private String description;
    private Long headAccountId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Department() {}

    public Department(String name) {
        this.name = name;
    }

    public Department(String name, Long headAccountId) {
        this.name = name;
        this.headAccountId = headAccountId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getHeadAccountId() {
        return headAccountId;
    }

    public void setHeadAccountId(Long headAccountId) {
        this.headAccountId = headAccountId;
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
    public boolean hasHead() {
        return this.headAccountId != null;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", headAccountId=" + headAccountId +
                '}';
    }
}