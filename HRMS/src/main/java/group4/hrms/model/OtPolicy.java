package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity mapping bảng ot_policies
 * Chính sách làm thêm giờ với rules và assignments dạng JSON
 * 
 * @author Group4
 */
public class OtPolicy {
    private Long id;
    private String code;               // unique code
    private String name;               // tên chính sách
    private String description;        // mô tả
    private String rulesJson;          // JSON rules for overtime calculation
    private String assignmentsJson;   // JSON assignments (departments, positions, users)
    private Long updatedByAccountId;   // ai cập nhật cuối
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public OtPolicy() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public OtPolicy(String code, String name) {
        this();
        this.code = code;
        this.name = name;
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
    
    public String getRulesJson() {
        return rulesJson;
    }
    
    public void setRulesJson(String rulesJson) {
        this.rulesJson = rulesJson;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAssignmentsJson() {
        return assignmentsJson;
    }
    
    public void setAssignmentsJson(String assignmentsJson) {
        this.assignmentsJson = assignmentsJson;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getUpdatedByAccountId() {
        return updatedByAccountId;
    }
    
    public void setUpdatedByAccountId(Long updatedByAccountId) {
        this.updatedByAccountId = updatedByAccountId;
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
    public boolean hasRules() {
        return rulesJson != null && !rulesJson.trim().isEmpty();
    }
    
    public boolean hasAssignments() {
        return assignmentsJson != null && !assignmentsJson.trim().isEmpty();
    }
    
    public void updateRules(String newRules, Long updatedBy) {
        this.rulesJson = newRules;
        this.updatedByAccountId = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateAssignments(String newAssignments, Long updatedBy) {
        this.assignmentsJson = newAssignments;
        this.updatedByAccountId = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "OtPolicy{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", hasRules=" + hasRules() +
                ", hasAssignments=" + hasAssignments() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}