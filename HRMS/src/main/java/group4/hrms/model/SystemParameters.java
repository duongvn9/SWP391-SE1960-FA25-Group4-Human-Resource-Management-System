package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity SystemParameters - Tham số hệ thống
 * Mapping từ bảng system_parameters trong database
 */
public class SystemParameters {
    
    // Các field khớp với database schema
    private Long id;
    private String scopeType;               // scope_type (NVARCHAR(16)) - DEFAULT 'GLOBAL'
    private Long scopeId;                   // scope_id - có thể null
    private String namespace;               // namespace (NVARCHAR(64))
    private String paramKey;                // param_key (NVARCHAR(64))
    private String valueJson;               // value_json (NVARCHAR(MAX))
    private String description;             // description (NVARCHAR(255))
    private Long updatedByAccountId;        // updated_by_account_id
    private LocalDateTime updatedAt;        // updated_at
    
    // Constructors
    public SystemParameters() {}
    
    public SystemParameters(String scopeType, Long scopeId, String namespace, 
                           String paramKey, String valueJson) {
        this.scopeType = scopeType != null ? scopeType : "GLOBAL";
        this.scopeId = scopeId;
        this.namespace = namespace;
        this.paramKey = paramKey;
        this.valueJson = valueJson;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters và Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getScopeType() {
        return scopeType;
    }
    
    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }
    
    public Long getScopeId() {
        return scopeId;
    }
    
    public void setScopeId(Long scopeId) {
        this.scopeId = scopeId;
    }
    
    public String getNamespace() {
        return namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public String getParamKey() {
        return paramKey;
    }
    
    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }
    
    public String getValueJson() {
        return valueJson;
    }
    
    public void setValueJson(String valueJson) {
        this.valueJson = valueJson;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getUpdatedByAccountId() {
        return updatedByAccountId;
    }
    
    public void setUpdatedByAccountId(Long updatedByAccountId) {
        this.updatedByAccountId = updatedByAccountId;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business methods
    public boolean isGlobalScope() {
        return "GLOBAL".equals(this.scopeType);
    }
    
    public boolean isDepartmentScope() {
        return "DEPARTMENT".equals(this.scopeType);
    }
    
    public boolean isUserScope() {
        return "USER".equals(this.scopeType);
    }
    
    public boolean isAttendanceNamespace() {
        return "attendance".equals(this.namespace);
    }
    
    public boolean isFilesNamespace() {
        return "files".equals(this.namespace);
    }
    
    public boolean isAuthNamespace() {
        return "auth".equals(this.namespace);
    }
    
    public boolean isUiNamespace() {
        return "ui".equals(this.namespace);
    }
    
    /**
     * Tạo unique key cho parameter
     */
    public String getUniqueKey() {
        return String.format("%s:%s:%s:%s", 
                scopeType, 
                scopeId != null ? scopeId.toString() : "null", 
                namespace, 
                paramKey);
    }
    
    @Override
    public String toString() {
        return "SystemParameters{" +
                "id=" + id +
                ", scopeType='" + scopeType + '\'' +
                ", scopeId=" + scopeId +
                ", namespace='" + namespace + '\'' +
                ", paramKey='" + paramKey + '\'' +
                ", valueJson='" + valueJson + '\'' +
                '}';
    }
}