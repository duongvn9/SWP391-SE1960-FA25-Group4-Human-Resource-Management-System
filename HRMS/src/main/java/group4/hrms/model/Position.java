package group4.hrms.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Position entity - Chức vụ trong công ty
 * Tương ứng với bảng positions trong database
 */
public class Position {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer jobLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Position() {}

    public Position(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Position(String code, String name, Integer jobLevel) {
        this.code = code;
        this.name = name;
        this.jobLevel = jobLevel;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(Integer jobLevel) {
        this.jobLevel = jobLevel;
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
    public boolean hasHigherLevelThan(Position other) {
        if (other == null || other.getJobLevel() == null) return true;
        if (this.jobLevel == null) return false;
        return this.jobLevel > other.getJobLevel();
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(id, position.id) && Objects.equals(code, position.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", jobLevel=" + jobLevel +
                '}';
    }
}