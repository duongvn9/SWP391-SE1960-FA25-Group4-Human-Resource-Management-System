package group4.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity JobPosting - Tin tuyển dụng
 * Quản lý các vị trí tuyển dụng và yêu cầu công việc
 */
public class JobPosting {
    
    private Long id;
    private Long requestId;                 // ID request tuyển dụng
    private String title;                   // Tiêu đề công việc
    private String code;                    // Mã công việc
    private Long departmentId;              // Phòng ban tuyển dụng
    private Long positionId;                // Vị trí tuyển dụng
    private String jobType;                 // Loại công việc (FULL_TIME, PART_TIME, CONTRACT, INTERN)
    private String level;                   // Cấp độ (ENTRY, JUNIOR, SENIOR, MANAGER, DIRECTOR)
    private Integer numberOfPositions;      // Số lượng cần tuyển
    private String description;             // Mô tả công việc
    private String requirements;            // Yêu cầu ứng viên
    private String benefits;                // Quyền lợi
    private String location;                // Địa điểm làm việc
    private Integer minExperienceYears;     // Số năm kinh nghiệm tối thiểu
    private BigDecimal minSalary;           // Mức lương tối thiểu
    private BigDecimal maxSalary;           // Mức lương tối đa
    private String salaryType;              // Loại lương (GROSS, NET, NEGOTIABLE)
    private LocalDate applicationDeadline;  // Hạn nộp hồ sơ
    private LocalDate startDate;            // Ngày bắt đầu làm việc dự kiến
    private String status;                  // DRAFT, PUBLISHED, CLOSED, CANCELLED
    private String priority;                // Mức độ ưu tiên (LOW, MEDIUM, HIGH, URGENT)
    private String workingHours;            // Giờ làm việc
    private String contactEmail;            // Email liên hệ
    private String contactPhone;            // Số điện thoại liên hệ
    private Long createdBy;                 // Người tạo
    private Long createdByAccountId;        // ID account người tạo (from DB)
    private Long approvedBy;                // Người duyệt
    private Long approvedByAccountId;       // account id của người duyệt
    private Long publishedByAccountId;      // account id của người publish
    private String rejectedReason;          // Lý do bị từ chối
    private LocalDateTime approvedAt;       // Thời gian duyệt
    private LocalDateTime publishedAt;      // Thời gian đăng tuyển
    private LocalDateTime createdAt;        // Thời gian tạo
    private LocalDateTime updatedAt;        // Thời gian cập nhật cuối
    
    // Constructors
    public JobPosting() {}
    
    public JobPosting(String title, Long departmentId, Long positionId, Integer numberOfPositions) {
        this.title = title;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.numberOfPositions = numberOfPositions;
        this.jobType = "FULL_TIME";
        this.level = "JUNIOR";
        this.status = "DRAFT";
        this.priority = "MEDIUM";
        this.salaryType = "GROSS";
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
    
    public Long getRequestId() {
        return requestId;
    }
    
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public Long getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
    
    public Long getPositionId() {
        return positionId;
    }
    
    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }
    
    public String getJobType() {
        return jobType;
    }
    
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public Integer getNumberOfPositions() {
        return numberOfPositions;
    }
    
    public void setNumberOfPositions(Integer numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getRequirements() {
        return requirements;
    }
    
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
    
    public String getBenefits() {
        return benefits;
    }
    
    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMinExperienceYears() {
        return minExperienceYears;
    }

    public void setMinExperienceYears(Integer minExperienceYears) {
        this.minExperienceYears = minExperienceYears;
    }
    
    public BigDecimal getMinSalary() {
        return minSalary;
    }
    
    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }
    
    public BigDecimal getMaxSalary() {
        return maxSalary;
    }
    
    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }
    
    public String getSalaryType() {
        return salaryType;
    }
    
    public void setSalaryType(String salaryType) {
        this.salaryType = salaryType;
    }
    
    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }
    
    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
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
    
    public String getWorkingHours() {
        return workingHours;
    }
    
    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public Long getCreatedByAccountId() {
        return createdByAccountId;
    }
    
    public void setCreatedByAccountId(Long createdByAccountId) {
        this.createdByAccountId = createdByAccountId;
    }
    
    public Long getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Long getApprovedByAccountId() {
        return approvedByAccountId;
    }

    public void setApprovedByAccountId(Long approvedByAccountId) {
        this.approvedByAccountId = approvedByAccountId;
    }

    public Long getPublishedByAccountId() {
        return publishedByAccountId;
    }

    public void setPublishedByAccountId(Long publishedByAccountId) {
        this.publishedByAccountId = publishedByAccountId;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
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
    // Status helpers for new approval flow
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(this.status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(this.status);
    }

    public boolean isPublished() {
        return "PUBLISHED".equals(this.status);
    }

    public boolean isActive() {
        return isPublished() && !isExpired();
    }

    public boolean isExpired() {
        return applicationDeadline != null && LocalDate.now().isAfter(applicationDeadline);
    }

    /**
     * Only allow edit when status is PENDING (HR can edit before HRM approval)
     */
    public boolean canBeEdited() {
        return isPending();
    }

    /**
     * Only allow publish when status is APPROVED (HRM can publish)
     */
    public boolean canBePublished() {
        return isApproved();
    }

    public boolean canBeClosed() {
        return isPublished();
    }
    
    /**
     * Days left to apply
     */
    public long getDaysUntilDeadline() {
        if (applicationDeadline != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), applicationDeadline);
        }
        return -1;
    }
    
    /**
     * Get salary range for display (English)
     */
    public String getSalaryRange() {
        if (minSalary != null && maxSalary != null) {
            return String.format("%,.0f - %,.0f VND", minSalary, maxSalary);
        } else if (minSalary != null) {
            return String.format("From %,.0f VND", minSalary);
        } else if (maxSalary != null) {
            return String.format("Up to %,.0f VND", maxSalary);
        } else {
            return "Negotiable";
        }
    }
    
    @Override
    public String toString() {
        return "JobPosting{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", code='" + code + '\'' +
                ", departmentId=" + departmentId +
                ", positionId=" + positionId +
                ", jobType='" + jobType + '\'' +
                ", level='" + level + '\'' +
                ", numberOfPositions=" + numberOfPositions +
                ", status='" + status + '\'' +
                ", applicationDeadline=" + applicationDeadline +
                '}';
    }
}