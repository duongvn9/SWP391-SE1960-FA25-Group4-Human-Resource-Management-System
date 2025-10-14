package group4.hrms.model;

import group4.hrms.dto.LeaveRequestDetail;
import group4.hrms.dto.OTRequestDetail;
import java.time.LocalDateTime;

/**
 * Entity Request - Yêu cầu/Đơn từ của nhân viên
 * Bao gồm: nghỉ phép, tăng ca, thay đổi thông tin, v.v.
 */
public class Request {

    private Long id;
    private Long requestTypeId; // Loại request (LEAVE_REQUEST, OT_REQUEST, etc.)
    private String title; // Tiêu đề request
    private String detailJson; // Raw JSON string from database (stored in 'detail' column)
    private Long createdByAccountId; // Account ID của người tạo
    private Long createdByUserId; // User ID của người tạo
    private Long departmentId; // Department ID
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    private Long currentApproverAccountId; // Account ID của người duyệt hiện tại
    private LocalDateTime createdAt; // Thời gian tạo
    private LocalDateTime updatedAt; // Thời gian cập nhật cuối

    // Transient fields for parsed details (not stored in database)
    private transient LeaveRequestDetail leaveDetail;
    private transient OTRequestDetail otDetail;

    // Deprecated fields - kept for backward compatibility but should not be used
    @Deprecated
    private Long userId; // Use createdByUserId instead
    @Deprecated
    private Long leaveTypeId; // Stored in JSON detail
    @Deprecated
    private String description; // Use detailJson instead
    @Deprecated
    private String priority; // Not in current schema
    @Deprecated
    private LocalDateTime startDate; // Stored in JSON detail
    @Deprecated
    private LocalDateTime endDate; // Stored in JSON detail
    @Deprecated
    private Integer dayCount; // Stored in JSON detail
    @Deprecated
    private String attachmentPath; // Stored in JSON detail
    @Deprecated
    private String rejectReason; // Stored in JSON detail
    @Deprecated
    private Long approvedBy; // Use currentApproverAccountId instead
    @Deprecated
    private LocalDateTime approvedAt; // Not in current schema

    // Constructors
    public Request() {
    }

    public Request(Long createdByAccountId, Long createdByUserId, Long requestTypeId, String title) {
        this.createdByAccountId = createdByAccountId;
        this.createdByUserId = createdByUserId;
        this.requestTypeId = requestTypeId;
        this.title = title;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @Deprecated
    public Request(Long userId, Long requestTypeId, String title, String description) {
        this.userId = userId;
        this.createdByUserId = userId;
        this.requestTypeId = requestTypeId;
        this.title = title;
        this.description = description;
        this.status = "PENDING";
        this.priority = "MEDIUM";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters và Setters for active fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDetailJson() {
        return detailJson;
    }

    public void setDetailJson(String detailJson) {
        this.detailJson = detailJson;
        // Clear cached details when JSON changes
        this.leaveDetail = null;
        this.otDetail = null;
    }

    public Long getCreatedByAccountId() {
        return createdByAccountId;
    }

    public void setCreatedByAccountId(Long createdByAccountId) {
        this.createdByAccountId = createdByAccountId;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCurrentApproverAccountId() {
        return currentApproverAccountId;
    }

    public void setCurrentApproverAccountId(Long currentApproverAccountId) {
        this.currentApproverAccountId = currentApproverAccountId;
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

    // JSON Helper Methods

    /**
     * Get parsed LeaveRequestDetail from JSON
     * Lazy-loads from detailJson if needed
     * @return LeaveRequestDetail object or null if detailJson is null
     */
    public LeaveRequestDetail getLeaveDetail() {
        if (leaveDetail == null && detailJson != null && !detailJson.trim().isEmpty()) {
            leaveDetail = LeaveRequestDetail.fromJson(detailJson);
        }
        return leaveDetail;
    }

    /**
     * Set LeaveRequestDetail and automatically serialize to JSON
     * Sets both leaveDetail and detailJson fields
     * @param detail LeaveRequestDetail object to set
     */
    public void setLeaveDetail(LeaveRequestDetail detail) {
        this.leaveDetail = detail;
        this.detailJson = (detail != null) ? detail.toJson() : null;
    }

    /**
     * Get parsed OTRequestDetail from JSON
     * Lazy-loads from detailJson if needed
     * @return OTRequestDetail object or null if detailJson is null
     */
    public OTRequestDetail getOtDetail() {
        if (otDetail == null && detailJson != null && !detailJson.trim().isEmpty()) {
            try {
                otDetail = OTRequestDetail.fromJson(detailJson);
            } catch (Exception e) {
                // If parsing as OT detail fails, it might be a leave detail
                return null;
            }
        }
        return otDetail;
    }

    /**
     * Set OTRequestDetail and automatically serialize to JSON
     * Sets both otDetail and detailJson fields
     * @param detail OTRequestDetail object to set
     */
    public void setOtDetail(OTRequestDetail detail) {
        this.otDetail = detail;
        this.detailJson = (detail != null) ? detail.toJson() : null;
    }

    // Deprecated getters/setters - kept for backward compatibility
    @Deprecated
    public Long getUserId() {
        return userId != null ? userId : createdByUserId;
    }

    @Deprecated
    public void setUserId(Long userId) {
        this.userId = userId;
        this.createdByUserId = userId;
    }

    @Deprecated
    public Long getLeaveTypeId() {
        return leaveTypeId;
    }

    @Deprecated
    public void setLeaveTypeId(Long leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    @Deprecated
    public String getDescription() {
        return description;
    }

    @Deprecated
    public void setDescription(String description) {
        this.description = description;
    }

    @Deprecated
    public String getPriority() {
        return priority;
    }

    @Deprecated
    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Deprecated
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @Deprecated
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @Deprecated
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @Deprecated
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Deprecated
    public Integer getDayCount() {
        return dayCount;
    }

    @Deprecated
    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }

    @Deprecated
    public String getAttachmentPath() {
        return attachmentPath;
    }

    @Deprecated
    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    @Deprecated
    public String getRejectReason() {
        return rejectReason;
    }

    @Deprecated
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    @Deprecated
    public Long getApprovedBy() {
        return approvedBy != null ? approvedBy : currentApproverAccountId;
    }

    @Deprecated
    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
        this.currentApproverAccountId = approvedBy;
    }

    @Deprecated
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    @Deprecated
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
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
                ", requestTypeId=" + requestTypeId +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", createdByAccountId=" + createdByAccountId +
                ", createdByUserId=" + createdByUserId +
                ", departmentId=" + departmentId +
                ", createdAt=" + createdAt +
                '}';
    }
}