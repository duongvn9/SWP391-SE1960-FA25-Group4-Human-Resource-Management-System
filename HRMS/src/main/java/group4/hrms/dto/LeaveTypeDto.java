package group4.hrms.dto;

import group4.hrms.model.LeaveType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho LeaveType với thông tin bổ sung
 */
public class LeaveTypeDto {
    
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer defaultDays;
    private Integer maxDays;
    private boolean isPaid;
    private boolean requiresApproval;
    private boolean requiresCertificate;
    private Integer minAdvanceNotice;
    private boolean canCarryForward;
    private Integer maxCarryForward;
    private String gender;
    private String genderDisplay;           // Hiển thị giới tính tiếng Việt
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int employeeCount;              // Số nhân viên được áp dụng loại này
    private int totalUsedDays;              // Tổng số ngày đã sử dụng
    
    // Constructors
    public LeaveTypeDto() {}
    
    public LeaveTypeDto(LeaveType leaveType) {
        this.id = leaveType.getId();
        this.name = leaveType.getName();
        this.code = leaveType.getCode();
        this.description = leaveType.getDescription();
        this.defaultDays = leaveType.getDefaultDays();
        this.maxDays = leaveType.getMaxDays();
        this.isPaid = leaveType.isPaid();
        this.requiresApproval = leaveType.isRequiresApproval();
        this.requiresCertificate = leaveType.isRequiresCertificate();
        this.minAdvanceNotice = leaveType.getMinAdvanceNotice();
        this.canCarryForward = leaveType.isCanCarryForward();
        this.maxCarryForward = leaveType.getMaxCarryForward();
        this.gender = leaveType.getGender();
        this.isActive = leaveType.isActive();
        this.createdAt = leaveType.getCreatedAt();
        this.updatedAt = leaveType.getUpdatedAt();
        
        // Set display values
        this.genderDisplay = getGenderDisplayText(this.gender);
    }
    
    // Factory methods
    public static LeaveTypeDto fromEntity(LeaveType leaveType) {
        return new LeaveTypeDto(leaveType);
    }
    
    public LeaveType toEntity() {
        LeaveType leaveType = new LeaveType();
        leaveType.setId(this.id);
        leaveType.setName(this.name);
        leaveType.setCode(this.code);
        leaveType.setDescription(this.description);
        leaveType.setDefaultDays(this.defaultDays);
        leaveType.setMaxDays(this.maxDays);
        leaveType.setPaid(this.isPaid);
        leaveType.setRequiresApproval(this.requiresApproval);
        leaveType.setRequiresCertificate(this.requiresCertificate);
        leaveType.setMinAdvanceNotice(this.minAdvanceNotice);
        leaveType.setCanCarryForward(this.canCarryForward);
        leaveType.setMaxCarryForward(this.maxCarryForward);
        leaveType.setGender(this.gender);
        leaveType.setActive(this.isActive);
        leaveType.setCreatedAt(this.createdAt);
        leaveType.setUpdatedAt(this.updatedAt);
        return leaveType;
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
        this.genderDisplay = getGenderDisplayText(gender);
    }
    
    public String getGenderDisplay() {
        return genderDisplay;
    }
    
    public void setGenderDisplay(String genderDisplay) {
        this.genderDisplay = genderDisplay;
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
    
    public int getEmployeeCount() {
        return employeeCount;
    }
    
    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }
    
    public int getTotalUsedDays() {
        return totalUsedDays;
    }
    
    public void setTotalUsedDays(int totalUsedDays) {
        this.totalUsedDays = totalUsedDays;
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
    
    public boolean isApplicableForGender(String userGender) {
        return isForAllGenders() || this.gender.equals(userGender);
    }
    
    // Formatted display methods
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
    
    public String getPaidStatusText() {
        return isPaid ? "Có lương" : "Không lương";
    }
    
    public String getRequiresApprovalText() {
        return requiresApproval ? "Cần duyệt" : "Không cần duyệt";
    }
    
    public String getRequiresCertificateText() {
        return requiresCertificate ? "Cần giấy tờ" : "Không cần giấy tờ";
    }
    
    public String getCanCarryForwardText() {
        return canCarryForward ? "Được chuyển" : "Không chuyển";
    }
    
    public String getDefaultDaysText() {
        if (defaultDays != null) {
            return defaultDays + " ngày/năm";
        }
        return "Không giới hạn";
    }
    
    public String getMaxDaysText() {
        if (maxDays != null) {
            return "Tối đa " + maxDays + " ngày";
        }
        return "Không giới hạn";
    }
    
    public String getMinAdvanceNoticeText() {
        if (minAdvanceNotice != null) {
            return "Báo trước " + minAdvanceNotice + " ngày";
        }
        return "Không yêu cầu";
    }
    
    public String getMaxCarryForwardText() {
        if (canCarryForward && maxCarryForward != null) {
            return "Tối đa " + maxCarryForward + " ngày";
        }
        return "";
    }
    
    // Helper methods
    private String getGenderDisplayText(String gender) {
        if (gender == null) return "";
        
        return switch (gender) {
            case "ALL" -> "Tất cả";
            case "MALE" -> "Nam";
            case "FEMALE" -> "Nữ";
            default -> gender;
        };
    }
    
    public String getStatusColor() {
        return isActive ? "success" : "secondary";
    }
    
    public String getPaidColor() {
        return isPaid ? "success" : "warning";
    }
}