package group4.hrms.dto;

import group4.hrms.model.RequestType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho RequestType với thông tin bổ sung
 */
public class RequestTypeDto {
    
    private Long id;
    private String name;
    private String code;
    private String description;
    private String category;
    private String categoryDisplay;         // Hiển thị danh mục tiếng Việt
    private boolean requiresApproval;
    private boolean requiresAttachment;
    private Integer maxDays;
    private String approvalWorkflow;
    private String approvalWorkflowDisplay; // Hiển thị quy trình duyệt tiếng Việt
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int requestCount;               // Số lượng request thuộc loại này
    
    // Constructors
    public RequestTypeDto() {}
    
    public RequestTypeDto(RequestType requestType) {
        this.id = requestType.getId();
        this.name = requestType.getName();
        this.code = requestType.getCode();
        this.description = requestType.getDescription();
        this.category = requestType.getCategory();
        this.requiresApproval = requestType.isRequiresApproval();
        this.requiresAttachment = requestType.isRequiresAttachment();
        this.maxDays = requestType.getMaxDays();
        this.approvalWorkflow = requestType.getApprovalWorkflow();
        this.isActive = requestType.isActive();
        this.createdAt = requestType.getCreatedAt();
        this.updatedAt = requestType.getUpdatedAt();
        
        // Set display values
        this.categoryDisplay = getCategoryDisplayText(this.category);
        this.approvalWorkflowDisplay = getApprovalWorkflowDisplayText(this.approvalWorkflow);
    }
    
    // Factory methods
    public static RequestTypeDto fromEntity(RequestType requestType) {
        return new RequestTypeDto(requestType);
    }
    
    public RequestType toEntity() {
        RequestType requestType = new RequestType();
        requestType.setId(this.id);
        requestType.setName(this.name);
        requestType.setCode(this.code);
        requestType.setDescription(this.description);
        requestType.setCategory(this.category);
        requestType.setRequiresApproval(this.requiresApproval);
        requestType.setRequiresAttachment(this.requiresAttachment);
        requestType.setMaxDays(this.maxDays);
        requestType.setApprovalWorkflow(this.approvalWorkflow);
        requestType.setActive(this.isActive);
        requestType.setCreatedAt(this.createdAt);
        requestType.setUpdatedAt(this.updatedAt);
        return requestType;
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
        this.categoryDisplay = getCategoryDisplayText(category);
    }
    
    public String getCategoryDisplay() {
        return categoryDisplay;
    }
    
    public void setCategoryDisplay(String categoryDisplay) {
        this.categoryDisplay = categoryDisplay;
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
        this.approvalWorkflowDisplay = getApprovalWorkflowDisplayText(approvalWorkflow);
    }
    
    public String getApprovalWorkflowDisplay() {
        return approvalWorkflowDisplay;
    }
    
    public void setApprovalWorkflowDisplay(String approvalWorkflowDisplay) {
        this.approvalWorkflowDisplay = approvalWorkflowDisplay;
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
    
    public int getRequestCount() {
        return requestCount;
    }
    
    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }
    
    // Business methods
    public boolean isLeaveType() {
        return "LEAVE".equals(this.category);
    }
    
    public boolean isOvertimeType() {
        return "OVERTIME".equals(this.category);
    }
    
    public boolean hasMultiLevelApproval() {
        return "MULTI_LEVEL".equals(this.approvalWorkflow);
    }
    
    public String getCreatedAtFormatted() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return createdAt.format(formatter);
        }
        return "";
    }
    
    public String getActiveStatusText() {
        return isActive ? "Hoạt động" : "Không hoạt động";
    }
    
    public String getRequiresApprovalText() {
        return requiresApproval ? "Có" : "Không";
    }
    
    public String getRequiresAttachmentText() {
        return requiresAttachment ? "Có" : "Không";
    }
    
    // Helper methods
    private String getCategoryDisplayText(String category) {
        if (category == null) return "";
        
        switch (category) {
            case "LEAVE":
                return "Nghỉ phép";
            case "OVERTIME":
                return "Tăng ca";
            case "PERSONAL":
                return "Cá nhân";
            case "SYSTEM":
                return "Hệ thống";
            default:
                return category;
        }
    }
    
    private String getApprovalWorkflowDisplayText(String workflow) {
        if (workflow == null) return "";
        
        switch (workflow) {
            case "SINGLE":
                return "Duyệt đơn cấp";
            case "MULTI_LEVEL":
                return "Duyệt đa cấp";
            default:
                return workflow;
        }
    }
}