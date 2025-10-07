package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity RequestType - Loại yêu cầu/đơn từ
 * Ví dụ: Nghỉ phép, Tăng ca, Thay đổi thông tin cá nhân, v.v.
 */
public class RequestType {
    
    private Long id;
    private String name;                    // Tên loại request (VD: "Nghỉ phép", "Tăng ca")
    private String code;                    // Mã code (VD: "LEAVE", "OVERTIME")
    private String description;             // Mô tả chi tiết
    private String category;                // Danh mục (LEAVE, OVERTIME, PERSONAL, SYSTEM)
    private boolean requiresApproval;       // Có cần duyệt không
    private boolean requiresAttachment;     // Có cần đính kèm file không
    private Integer maxDays;                // Số ngày tối đa (với leave request)
    private String approvalWorkflow;        // Quy trình duyệt (SINGLE, MULTI_LEVEL)
    private boolean isActive;               // Trạng thái hoạt động
    private LocalDateTime createdAt;        // Thời gian tạo
    private LocalDateTime updatedAt;        // Thời gian cập nhật cuối
    
    // Constructors
    public RequestType() {}
    
    public RequestType(String name, String code, String category) {
        this.name = name;
        this.code = code;
        this.category = category;
        this.requiresApproval = true;
        this.requiresAttachment = false;
        this.approvalWorkflow = "SINGLE";
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters và Setters
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
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isRequiresApproval() {
        return requiresApproval;
    }
    
    public void setRequiresApproval(boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }
    
    public boolean isRequiresAttachment() {
        return requiresAttachment;
    }
    
    public void setRequiresAttachment(boolean requiresAttachment) {
        this.requiresAttachment = requiresAttachment;
    }
    
    public Integer getMaxDays() {
        return maxDays;
    }
    
    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }
    
    public String getApprovalWorkflow() {
        return approvalWorkflow;
    }
    
    public void setApprovalWorkflow(String approvalWorkflow) {
        this.approvalWorkflow = approvalWorkflow;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
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
    public boolean isLeaveType() {
        return "LEAVE".equals(this.category);
    }
    
    public boolean isOvertimeType() {
        return "OVERTIME".equals(this.category);
    }
    
    public boolean isPersonalType() {
        return "PERSONAL".equals(this.category);
    }
    
    public boolean hasMultiLevelApproval() {
        return "MULTI_LEVEL".equals(this.approvalWorkflow);
    }
    
    @Override
    public String toString() {
        return "RequestType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", category='" + category + '\'' +
                ", requiresApproval=" + requiresApproval +
                ", isActive=" + isActive +
                '}';
    }
}