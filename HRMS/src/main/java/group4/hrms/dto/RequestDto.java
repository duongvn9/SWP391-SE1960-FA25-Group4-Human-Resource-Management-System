package group4.hrms.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import group4.hrms.model.Request;

/**
 * DTO cho Request với thông tin bổ sung Bao gồm tên người tạo, loại request,
 * người duyệt
 */
public class RequestDto {

    private Long id;
    private Long userId;
    private String userName;                // Tên người tạo request
    private String userFullName;            // Họ tên đầy đủ
    private Long requestTypeId;
    private String requestTypeName;         // Tên loại request
    private String requestTypeCode;         // Mã loại request
    private String title;
    private String description;
    private String status;
    private String statusDisplay;           // Hiển thị trạng thái tiếng Việt
    private String priority;
    private String priorityDisplay;         // Hiển thị mức độ ưu tiên tiếng Việt
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer dayCount;
    private String attachmentPath;
    private String rejectReason;
    private Long approvedBy;
    private String approverName;            // Tên người duyệt
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private RecruitmentDetailsDto recruitmentDetails;

    // Constructors
    public RequestDto() {
    }

    public RequestDto(Request request) {
        this.id = request.getId();
        this.userId = request.getUserId();
        this.requestTypeId = request.getRequestTypeId();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.status = request.getStatus();
        this.priority = request.getPriority();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.dayCount = request.getDayCount();
        this.attachmentPath = request.getAttachmentPath();
        this.rejectReason = request.getRejectReason();
        this.approvedBy = request.getApprovedBy();
        this.approvedAt = request.getApprovedAt();
        this.createdAt = request.getCreatedAt();
        this.updatedAt = request.getUpdatedAt();

        if (request.getRequestTypeId() != null && request.getRequestTypeId() == 2L) {
            // Gọi hàm getRecruitmentDetail() trong Model Request.java
            this.recruitmentDetails = request.getRecruitmentDetail();
        }

        // Set display values
        this.statusDisplay = getStatusDisplayText(this.status);
        this.priorityDisplay = getPriorityDisplayText(this.priority);
    }

    // Factory methods
    public static RequestDto fromEntity(Request request) {
        return new RequestDto(request);
    }

    public Request toEntity() {
        Request request = new Request();
        request.setId(this.id);
        request.setUserId(this.userId);
        request.setRequestTypeId(this.requestTypeId);
        request.setTitle(this.title);
        request.setDescription(this.description);
        request.setStatus(this.status);
        request.setPriority(this.priority);
        request.setStartDate(this.startDate);
        request.setEndDate(this.endDate);
        request.setDayCount(this.dayCount);
        request.setAttachmentPath(this.attachmentPath);
        request.setRejectReason(this.rejectReason);
        request.setApprovedBy(this.approvedBy);
        request.setApprovedAt(this.approvedAt);
        request.setCreatedAt(this.createdAt);
        request.setUpdatedAt(this.updatedAt);
        return request;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Long getRequestTypeId() {
        return requestTypeId;
    }

    public void setRequestTypeId(Long requestTypeId) {
        this.requestTypeId = requestTypeId;
    }

    public String getRequestTypeName() {
        return requestTypeName;
    }

    public void setRequestTypeName(String requestTypeName) {
        this.requestTypeName = requestTypeName;
    }

    public String getRequestTypeCode() {
        return requestTypeCode;
    }

    public void setRequestTypeCode(String requestTypeCode) {
        this.requestTypeCode = requestTypeCode;
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
        this.statusDisplay = getStatusDisplayText(status);
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
        this.priorityDisplay = getPriorityDisplayText(priority);
    }

    public String getPriorityDisplay() {
        return priorityDisplay;
    }

    public void setPriorityDisplay(String priorityDisplay) {
        this.priorityDisplay = priorityDisplay;
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

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
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

    public String getCreatedAtFormatted() {
        if (createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return createdAt.format(formatter);
        }
        return "";
    }

    public String getStartDateFormatted() {
        if (startDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return startDate.format(formatter);
        }
        return "";
    }

    public String getEndDateFormatted() {
        if (endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return endDate.format(formatter);
        }
        return "";
    }

    public RecruitmentDetailsDto getRecruitmentDetails() {
        return recruitmentDetails;
    }

    // Helper methods
    private String getStatusDisplayText(String status) {
        if (status == null) {
            return "";
        }

        switch (status) {
            case "PENDING":
                return "Chờ duyệt";
            case "APPROVED":
                return "Đã duyệt";
            case "REJECTED":
                return "Từ chối";
            case "CANCELLED":
                return "Đã hủy";
            default:
                return status;
        }
    }

    private String getPriorityDisplayText(String priority) {
        if (priority == null) {
            return "";
        }

        switch (priority) {
            case "LOW":
                return "Thấp";
            case "MEDIUM":
                return "Trung bình";
            case "HIGH":
                return "Cao";
            case "URGENT":
                return "Khẩn cấp";
            default:
                return priority;
        }
    }
}
