package group4.hrms.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Role entity
 * Used for creating and updating roles
 */
public class RoleDto {
    
    private String code;
    private String name;
    private Integer priority;
    private Boolean isSystem;
    
    // Validation errors
    private List<String> errors;
    
    public RoleDto() {
        this.errors = new ArrayList<>();
        this.isSystem = false;
    }
    
    // Getters and Setters
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
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
    
    /**
     * Validate all fields
     */
    public boolean validate() {
        errors.clear();
        
        // Validate code (required, max 50 chars, uppercase letters and underscores only)
        if (code == null || code.trim().isEmpty()) {
            errors.add("Role code is required");
        } else if (code.length() > 50) {
            errors.add("Role code must not exceed 50 characters");
        } else if (!code.matches("^[A-Z_]+$")) {
            errors.add("Role code must contain only uppercase letters and underscores");
        }
        
        // Validate name (required, max 100 chars)
        if (name == null || name.trim().isEmpty()) {
            errors.add("Role name is required");
        } else if (name.length() > 100) {
            errors.add("Role name must not exceed 100 characters");
        }
        
        // Validate priority (optional, must be >= 0)
        if (priority != null && priority < 0) {
            errors.add("Priority must be greater than or equal to 0");
        }
        
        return errors.isEmpty();
    }
    
    @Override
    public String toString() {
        return "RoleDto{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", isSystem=" + isSystem +
                '}';
    }
}
