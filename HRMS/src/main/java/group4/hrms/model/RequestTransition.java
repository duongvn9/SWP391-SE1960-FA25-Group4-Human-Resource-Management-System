package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity RequestTransition - Lịch sử chuyển trạng thái của Request
 * Theo dõi workflow của các yêu cầu từ tạo đến hoàn thành
 */
public class RequestTransition {
    
    private Long id;
    private Long requestId;                 // ID của request
    private String fromStatus;              // Trạng thái trước
    private String toStatus;                // Trạng thái sau
    private Long actionBy;                  // Người thực hiện action
    private String actionType;              // SUBMIT, APPROVE, REJECT, CANCEL, REOPEN
    private String comments;                // Ghi chú/lý do
    private String attachmentPath;          // Đường dẫn file đính kèm (nếu có)
    private LocalDateTime actionDate;       // Thời gian thực hiện action
    private String ipAddress;               // IP địa chỉ thực hiện action
    private LocalDateTime createdAt;        // Thời gian tạo
    
    // Constructors
    public RequestTransition() {}
    
    public RequestTransition(Long requestId, String fromStatus, String toStatus, 
                           Long actionBy, String actionType) {
        this.requestId = requestId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actionBy = actionBy;
        this.actionType = actionType;
        this.actionDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters và Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRequestId() {
        return requestId;
    }
    
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
    
    public String getFromStatus() {
        return fromStatus;
    }
    
    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }
    
    public String getToStatus() {
        return toStatus;
    }
    
    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }
    
    public Long getActionBy() {
        return actionBy;
    }
    
    public void setActionBy(Long actionBy) {
        this.actionBy = actionBy;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public String getAttachmentPath() {
        return attachmentPath;
    }
    
    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
    
    public LocalDateTime getActionDate() {
        return actionDate;
    }
    
    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    public boolean isSubmitAction() {
        return "SUBMIT".equals(this.actionType);
    }
    
    public boolean isApproveAction() {
        return "APPROVE".equals(this.actionType);
    }
    
    public boolean isRejectAction() {
        return "REJECT".equals(this.actionType);
    }
    
    public boolean isCancelAction() {
        return "CANCEL".equals(this.actionType);
    }
    
    public boolean isReopenAction() {
        return "REOPEN".equals(this.actionType);
    }
    
    public boolean hasComments() {
        return comments != null && !comments.trim().isEmpty();
    }
    
    public boolean hasAttachment() {
        return attachmentPath != null && !attachmentPath.trim().isEmpty();
    }
    
    /**
     * Tạo transition cho việc submit request
     */
    public static RequestTransition createSubmitTransition(Long requestId, Long userId, String ipAddress) {
        RequestTransition transition = new RequestTransition(requestId, null, "PENDING", userId, "SUBMIT");
        transition.setIpAddress(ipAddress);
        return transition;
    }
    
    /**
     * Tạo transition cho việc approve request
     */
    public static RequestTransition createApproveTransition(Long requestId, Long approverId, 
                                                          String comments, String ipAddress) {
        RequestTransition transition = new RequestTransition(requestId, "PENDING", "APPROVED", approverId, "APPROVE");
        transition.setComments(comments);
        transition.setIpAddress(ipAddress);
        return transition;
    }
    
    /**
     * Tạo transition cho việc reject request
     */
    public static RequestTransition createRejectTransition(Long requestId, Long approverId, 
                                                         String reason, String ipAddress) {
        RequestTransition transition = new RequestTransition(requestId, "PENDING", "REJECTED", approverId, "REJECT");
        transition.setComments(reason);
        transition.setIpAddress(ipAddress);
        return transition;
    }
    
    /**
     * Tạo transition cho việc cancel request
     */
    public static RequestTransition createCancelTransition(Long requestId, Long userId, 
                                                         String reason, String ipAddress) {
        RequestTransition transition = new RequestTransition(requestId, "PENDING", "CANCELLED", userId, "CANCEL");
        transition.setComments(reason);
        transition.setIpAddress(ipAddress);
        return transition;
    }
    
    @Override
    public String toString() {
        return "RequestTransition{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", fromStatus='" + fromStatus + '\'' +
                ", toStatus='" + toStatus + '\'' +
                ", actionBy=" + actionBy +
                ", actionType='" + actionType + '\'' +
                ", actionDate=" + actionDate +
                '}';
    }
}