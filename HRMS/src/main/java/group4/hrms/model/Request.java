package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity Request - Yêu cầu/Đơn từ của nhân viên Bao gồm: nghỉ phép, tăng ca,
 * thay đổi thông tin, v.v.
 */
public class Request {

    private Long id;                           // PK
    private Long requestTypeId;                // FK -> request_types.id
    private String title;                      // Tiêu đề request
    private String detail;                     // Dữ liệu JSON chi tiết
    private Long createdByAccountId;           // FK -> accounts.id (người tạo, theo account)
    private Long createdByUserId;              // FK -> users.id (người tạo, theo user)
    private Long departmentId;                 // FK -> departments.id (bộ phận người tạo)
    private String status;                     // Trạng thái: DRAFT, PENDING, APPROVED, REJECTED, ...
    private Long currentApproverAccountId;     // Người duyệt hiện tại
    private LocalDateTime createdAt;           // Thời gian tạo
    private LocalDateTime updatedAt;           // Thời gian cập nhật

    public Request() {
    }

    public Request(Long id, Long requestTypeId, String title, String detail,
            Long createdByAccountId, Long createdByUserId, Long departmentId,
            String status, Long currentApproverAccountId,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.requestTypeId = requestTypeId;
        this.title = title;
        this.detail = detail;
        this.createdByAccountId = createdByAccountId;
        this.createdByUserId = createdByUserId;
        this.departmentId = departmentId;
        this.status = status;
        this.currentApproverAccountId = currentApproverAccountId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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
}
