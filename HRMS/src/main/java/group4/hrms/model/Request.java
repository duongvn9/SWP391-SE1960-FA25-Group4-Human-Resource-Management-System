package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity Request - Yêu cầu/Đơn từ của nhân viên
 * Bao gồm: nghỉ phép, tăng ca, thay đổi thông tin, v.v.
 */
public class Request {
    
    private Long id;
    private Long userId;                    // Người tạo request
    private Long requestTypeId;             // Loại request
    private String title;                   // Tiêu đề request
    private String description;             // Mô tả chi tiết
    private String status;                  // PENDING, APPROVED, REJECTED, CANCELLED
    private String priority;                // LOW, MEDIUM, HIGH, URGENT
    private LocalDateTime startDate;        // Ngày bắt đầu (với leave request)
    private LocalDateTime endDate;          // Ngày kết thúc (với leave request)
    private Integer dayCount;               // Số ngày nghỉ
    private String attachmentPath;          // Đường dẫn file đính kèm
    private String rejectReason;            // Lý do từ chối
    private Long approvedBy;                // Người duyệt
    private LocalDateTime approvedAt;       // Thời gian duyệt
    private LocalDateTime createdAt;        // Thời gian tạo
    private LocalDateTime updatedAt;        // Thời gian cập nhật cuối
    
    // Constructors
    public Request() {}
    
    public Request(Long userId, Long requestTypeId, String title, String description) {
        this.userId = userId;
        this.requestTypeId = requestTypeId;
        this.title = title;
        this.description = description;
        this.status = "PENDING";
        this.priority = "MEDIUM";
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getRequestTypeId() {
        return requestTypeId;
    }
    
    public void setRequestTypeId(Long requestTypeId) {
        this.requestTypeId = requestTypeId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public Integer getDayCount() {
        return dayCount;
    }
    
    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }
    
    public String getAttachmentPath() {
        return attachmentPath;
    }
    
    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
    
    public String getRejectReason() {
        return rejectReason;
    }
    
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    
    public Long getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
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
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
    
    public boolean isApproved() {
        return "APPROVED".equals(this.status);
    }
    
    public boolean isRejected() {
        return "REJECTED".equals(this.status);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equals(this.status);
    }
    
    public boolean canBeApproved() {
        return isPending();
    }
    
    public boolean canBeRejected() {
        return isPending();
    }
    
    public boolean canBeCancelled() {
        return isPending();
    }
    
    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", userId=" + userId +
                ", requestTypeId=" + requestTypeId +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}