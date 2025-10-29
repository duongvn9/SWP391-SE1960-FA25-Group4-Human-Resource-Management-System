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
    private Long createdByAccountId;        // Account ID of creator (for manager-created requests)
    private String userName;                // Tên người tạo request
    private String userFullName;            // Họ tên đầy đủ
    private Long requestTypeId;
    private String requestTypeName;         // Tên loại request
    private String requestTypeCode;         // Mã loại request
    private String title;
    private String description;
    private String detailJson;              // JSON detail from Request

    // Parsed detail objects (transient - not serialized)
    private transient LeaveRequestDetail leaveDetail;
    private transient OTRequestDetail otDetail;
    private transient AppealRequestDetail appealDetail;
    private transient RecruitmentDetailsDto recruitmentDetail;

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

    // UI helper fields for request list page
    private String employeeCode;            // Employee code
    private String departmentName;          // Department name
    private String statusBadgeClass;        // CSS class for status badge
    private boolean canUpdate;              // Can user update this request
    private boolean canDelete;              // Can user delete this request
    private boolean canApprove;             // Can user approve/reject this request
    private String updateUrl;               // URL to update page
    private String detailUrl;               // URL to detail page
    private int attachmentCount;            // Number of attachments for this request

    // Constructors
    public RequestDto() {
    }

    public RequestDto(Request request) {
        this.id = request.getId();
        this.userId = request.getUserId();
        this.createdByAccountId = request.getCreatedByAccountId();
        this.requestTypeId = request.getRequestTypeId();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.detailJson = request.getDetailJson();

        // Parse detail JSON based on request_type_id
        if (this.detailJson != null && !this.detailJson.trim().isEmpty() && this.requestTypeId != null) {
            try {
                if (this.requestTypeId == 6L) {
                    // LEAVE_REQUEST
                    this.leaveDetail = request.getLeaveDetail();
                } else if (this.requestTypeId == 7L) {
                    // OVERTIME_REQUEST
                    this.otDetail = request.getOtDetail();
                } else if (this.requestTypeId == 8L) {
                    // ADJUSTMENT_REQUEST (Appeal)
                    this.appealDetail = request.getAppealDetail();
                } else if (this.requestTypeId == 9L) {
                    // RECRUITMENT_REQUEST
                    this.recruitmentDetail = request.getRecruitmentDetail();
                }
            } catch (Exception e) {
                // Ignore parsing errors - detail will be null
                System.err.println("[ERROR RequestDto Constructor] Failed to parse detail: " + e.getMessage());
                e.printStackTrace();
            }
        }

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
        request.setCreatedByAccountId(this.createdByAccountId);
        request.setCreatedByUserId(this.userId); // Also set createdByUserId for consistency
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

    public Long getCreatedByAccountId() {
        return createdByAccountId;
    }

    public void setCreatedByAccountId(Long createdByAccountId) {
        this.createdByAccountId = createdByAccountId;
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

    public String getDetailJson() {
        return detailJson;
    }

    public void setDetailJson(String detailJson) {
        this.detailJson = detailJson;
        // Clear cached parsed objects when JSON changes
        this.leaveDetail = null;
        this.otDetail = null;
        this.appealDetail = null;
        this.recruitmentDetail = null;
    }

    /**
     * Get parsed LeaveRequestDetail from JSON Lazy-loads and caches the result
     */
    public LeaveRequestDetail getLeaveDetail() {
        if (leaveDetail == null && detailJson != null && !detailJson.trim().isEmpty()) {
            try {
                leaveDetail = LeaveRequestDetail.fromJson(detailJson);
            } catch (Exception e) {
                // Return null if parsing fails
                return null;
            }
        }
        return leaveDetail;
    }

    /**
     * Get parsed OTRequestDetail from JSON Lazy-loads and caches the result
     */
    public OTRequestDetail getOtDetail() {
        if (otDetail == null && detailJson != null && !detailJson.trim().isEmpty()) {
            try {
                otDetail = OTRequestDetail.fromJson(detailJson);
            } catch (Exception e) {
                // Return null if parsing fails
                return null;
            }
        }
        return otDetail;
    }

    /**
     * Get parsed AppealRequestDetail from JSON
     * Lazy-loads from detailJson if needed
     *
     * @return AppealRequestDetail object or null if detailJson is null
     */
    public AppealRequestDetail getAppealDetail() {
        if (appealDetail == null && detailJson != null && !detailJson.trim().isEmpty()) {
            try {
                appealDetail = AppealRequestDetail.fromJson(detailJson);
            } catch (Exception e) {
                // Return null if parsing fails
                return null;
            }
        }
        return appealDetail;
    }

    /**
     * Set AppealRequestDetail
     *
     * @param appealDetail AppealRequestDetail object to set
     */
    public void setAppealDetail(AppealRequestDetail appealDetail) {
        this.appealDetail = appealDetail;
    }

    /**
     * Get parsed RecruitmentDetailsDto from JSON
     * Lazy-loads from detailJson if needed
     *
     * @return RecruitmentDetailsDto object or null if detailJson is null
     */
    public RecruitmentDetailsDto getRecruitmentDetail() {
        if (recruitmentDetail == null && detailJson != null && !detailJson.trim().isEmpty()) {
            try {
                recruitmentDetail = RecruitmentDetailsDto.fromJson(detailJson);
            } catch (Exception e) {
                // Return null if parsing fails
                return null;
            }
        }
        return recruitmentDetail;
    }

    /**
     * Set RecruitmentDetailsDto
     *
     * @param recruitmentDetail RecruitmentDetailsDto object to set
     */
    public void setRecruitmentDetail(RecruitmentDetailsDto recruitmentDetail) {
        this.recruitmentDetail = recruitmentDetail;
    }

    /**
     * Check if this is a leave request
     */
    public boolean isLeaveRequest() {
        return requestTypeCode != null && requestTypeCode.startsWith("LEAVE_");
    }

    /**
     * Check if this is an OT request
     */
    public boolean isOTRequest() {
        return requestTypeCode != null && requestTypeCode.startsWith("OT_");
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

    /**
     * Get approvedAt as java.util.Date for JSP fmt:formatDate compatibility
     * @return Date object or null if approvedAt is null
     */
    public java.util.Date getApprovedAtAsDate() {
        if (approvedAt == null) {
            return null;
        }
        return java.util.Date.from(approvedAt.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get createdAt as java.util.Date for JSP fmt:formatDate compatibility
     * @return Date object or null if createdAt is null
     */
    public java.util.Date getCreatedAtAsDate() {
        if (createdAt == null) {
            return null;
        }
        return java.util.Date.from(createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Get updatedAt as java.util.Date for JSP fmt:formatDate compatibility
     * @return Date object or null if updatedAt is null
     */
    public java.util.Date getUpdatedAtAsDate() {
        if (updatedAt == null) {
            return null;
        }
        return java.util.Date.from(updatedAt.atZone(java.time.ZoneId.systemDefault()).toInstant());
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

    // Getters and Setters for UI helper fields
    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getStatusBadgeClass() {
        return statusBadgeClass;
    }

    public void setStatusBadgeClass(String statusBadgeClass) {
        this.statusBadgeClass = statusBadgeClass;
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanApprove() {
        return canApprove;
    }

    public void setCanApprove(boolean canApprove) {
        this.canApprove = canApprove;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    // Helper method to determine status badge CSS class
    public void calculateStatusBadgeClass() {
        if (status == null) {
            this.statusBadgeClass = "bg-secondary text-white";
            return;
        }
        switch (status) {
            case "PENDING":
                this.statusBadgeClass = "bg-warning text-dark";
                break;
            case "APPROVED":
                this.statusBadgeClass = "bg-success text-white";
                break;
            case "REJECTED":
                this.statusBadgeClass = "bg-danger text-white";
                break;
            default:
                this.statusBadgeClass = "bg-secondary text-white";
        }
    }

    // Helper method to build update URL based on request type
    public void buildUpdateUrl(String contextPath) {
        if (requestTypeCode == null || id == null) {
            this.updateUrl = null;
            return;
        }

        String typeSegment;
        if (requestTypeCode.startsWith("LEAVE_")) {
            typeSegment = "leave";
        } else if (requestTypeCode.startsWith("OT_")) {
            typeSegment = "ot";
        } else if (requestTypeCode.startsWith("ATTENDANCE_")) {
            typeSegment = "attendance-appeal";
        } else if (requestTypeCode.startsWith("RECRUITMENT_")) {
            typeSegment = "recruitment";
        } else {
            typeSegment = "request";
        }

        this.updateUrl = contextPath + "/requests/" + typeSegment + "/" + id + "/edit";
    }

    // Helper method to build detail URL based on request type
    public void buildDetailUrl(String contextPath) {
        if (requestTypeCode == null || id == null) {
            this.detailUrl = null;
            return;
        }

        String typeSegment;
        if (requestTypeCode.startsWith("LEAVE_")) {
            typeSegment = "leave";
        } else if (requestTypeCode.startsWith("OT_")) {
            typeSegment = "ot";
        } else if (requestTypeCode.startsWith("ATTENDANCE_")) {
            typeSegment = "attendance-appeal";
        } else if (requestTypeCode.startsWith("RECRUITMENT_")) {
            typeSegment = "recruitment";
        } else {
            typeSegment = "request";
        }

        this.detailUrl = contextPath + "/requests/" + typeSegment + "/" + id;
    }

    public String getReasonFromDetail() {
        // Try leave detail first
        LeaveRequestDetail leave = getLeaveDetail();
        if (leave != null && leave.getReason() != null && !leave.getReason().trim().isEmpty()) {
            return leave.getReason();
        }

        // Try OT detail
        OTRequestDetail ot = getOtDetail();
        if (ot != null && ot.getReason() != null && !ot.getReason().trim().isEmpty()) {
            return ot.getReason();
        }

        // Fallback to description field
        if (description != null && !description.trim().isEmpty()) {
            return description;
        }

        return "";
    }
}
