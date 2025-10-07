package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity LeaveType - Loại nghỉ phép
 * Ví dụ: Nghỉ phép năm, nghỉ bệnh, nghỉ thai sản, v.v.
 */
public class LeaveType {
    
    private Long id;
    private String name;                    // Tên loại nghỉ phép
    private String code;                    // Mã loại nghỉ phép (ANNUAL, SICK, MATERNITY)
    private String description;             // Mô tả chi tiết
    private Integer defaultDays;            // Số ngày được phép nghỉ mặc định/năm
    private Integer maxDays;                // Số ngày tối đa được phép nghỉ
    private boolean isPaid;                 // Có được trả lương không
    private boolean requiresApproval;       // Có cần duyệt không
    private boolean requiresCertificate;    // Có cần giấy tờ chứng minh không
    private Integer minAdvanceNotice;       // Số ngày báo trước tối thiểu
    private boolean canCarryForward;        // Có thể chuyển sang năm sau không
    private Integer maxCarryForward;        // Số ngày tối đa được chuyển
    private String gender;                  // Giới tính áp dụng (ALL, MALE, FEMALE)
    private boolean isActive;               // Trạng thái hoạt động
    private LocalDateTime createdAt;        // Thời gian tạo
    private LocalDateTime updatedAt;        // Thời gian cập nhật cuối
    
    // Constructors
    public LeaveType() {}
    
    public LeaveType(String name, String code, Integer defaultDays) {
        this.name = name;
        this.code = code;
        this.defaultDays = defaultDays;
        this.maxDays = defaultDays;
        this.isPaid = true;
        this.requiresApproval = true;
        this.requiresCertificate = false;
        this.minAdvanceNotice = 1;
        this.canCarryForward = false;
        this.gender = "ALL";
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
    
    public Integer getDefaultDays() {
        return defaultDays;
    }
    
    public void setDefaultDays(Integer defaultDays) {
        this.defaultDays = defaultDays;
    }
    
    public Integer getMaxDays() {
        return maxDays;
    }
    
    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }
    
    public boolean isPaid() {
        return isPaid;
    }
    
    public void setPaid(boolean paid) {
        isPaid = paid;
    }
    
    public boolean isRequiresApproval() {
        return requiresApproval;
    }
    
    public void setRequiresApproval(boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }
    
    public boolean isRequiresCertificate() {
        return requiresCertificate;
    }
    
    public void setRequiresCertificate(boolean requiresCertificate) {
        this.requiresCertificate = requiresCertificate;
    }
    
    public Integer getMinAdvanceNotice() {
        return minAdvanceNotice;
    }
    
    public void setMinAdvanceNotice(Integer minAdvanceNotice) {
        this.minAdvanceNotice = minAdvanceNotice;
    }
    
    public boolean isCanCarryForward() {
        return canCarryForward;
    }
    
    public void setCanCarryForward(boolean canCarryForward) {
        this.canCarryForward = canCarryForward;
    }
    
    public Integer getMaxCarryForward() {
        return maxCarryForward;
    }
    
    public void setMaxCarryForward(Integer maxCarryForward) {
        this.maxCarryForward = maxCarryForward;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
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
    public boolean isAnnualLeave() {
        return "ANNUAL".equals(this.code);
    }
    
    public boolean isSickLeave() {
        return "SICK".equals(this.code);
    }
    
    public boolean isMaternityLeave() {
        return "MATERNITY".equals(this.code);
    }
    
    public boolean isForAllGenders() {
        return "ALL".equals(this.gender);
    }
    
    public boolean isForMaleOnly() {
        return "MALE".equals(this.gender);
    }
    
    public boolean isForFemaleOnly() {
        return "FEMALE".equals(this.gender);
    }
    
    /**
     * Kiểm tra xem loại nghỉ phép này có áp dụng cho giới tính không
     */
    public boolean isApplicableForGender(String userGender) {
        return isForAllGenders() || this.gender.equals(userGender);
    }
    
    @Override
    public String toString() {
        return "LeaveType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", defaultDays=" + defaultDays +
                ", isPaid=" + isPaid +
                ", requiresApproval=" + requiresApproval +
                ", isActive=" + isActive +
                '}';
    }
}