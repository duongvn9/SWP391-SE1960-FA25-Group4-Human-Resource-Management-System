/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group4.hrms.dto;

/**
 *
 * @author HieuTrung
 */
import java.io.Serializable;

import com.google.gson.Gson;
import java.util.List;

public class RecruitmentDetailsDto implements Serializable {

    // Sử dụng Gson instance để thực hiện Serialization/Deserialization
    private static final Gson GSON = new Gson();

    // Thuộc tính chi tiết được lưu trong JSON
    private String positionCode;
    private String positionName;
    private String jobLevel; // JUNIOR, MIDDLE, SENIOR
    private Integer quantity;
    private String jobType; // Full-time, Part-time, etc.
    private String recruitmentReason;
    private Double minSalary;
    private Double maxSalary;
    private String salaryType;
    private String jobSummary; // Tóm tắt công việc (dùng cho mô tả)
    private String attachmentPath; // Đường dẫn file đính kèm hoặc link
    private List<String> attachments; // Danh sách file đính kèm (nếu có)
    public List<String> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }
    private String workingLocation; // e.g. Ho Chi Minh, Hanoi; required

    // --- Phương thức tiện ích JSON (GSON HELPERS) ---
    public String toJson() {
        return GSON.toJson(this);
    }

    public static RecruitmentDetailsDto fromJson(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        try {
            return GSON.fromJson(jsonString, RecruitmentDetailsDto.class);
        } catch (Exception e) {
            System.err.println("Error parsing RecruitmentDetailsDto JSON: " + e.getMessage());
            return null;
        }
    }

    // GETTERS VÀ SETTERS (Cần triển khai đầy đủ)

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getJobType() {
        return jobType;
    }
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Double getMinSalary() {
        return minSalary;
    }
    public void setMinSalary(Double minSalary) {
        this.minSalary = minSalary;
    }

    public Double getMaxSalary() {
        return maxSalary;
    }
    public void setMaxSalary(Double maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getSalaryType() {
        return salaryType;
    }
    public void setSalaryType(String salaryType) {
        this.salaryType = salaryType;
    }

    public String getRecruitmentReason() {
        return recruitmentReason;
    }

    public void setRecruitmentReason(String recruitmentReason) {
        this.recruitmentReason = recruitmentReason;
    }

    // budgetSalaryRange đã bỏ, không dùng nữa

    public String getJobSummary() {
        return jobSummary;
    }

    public void setJobSummary(String jobSummary) {
        this.jobSummary = jobSummary;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getWorkingLocation() {
        return workingLocation;
    }

    public void setWorkingLocation(String workingLocation) {
        this.workingLocation = workingLocation;
    }

        /**
         * Validate required fields for recruitment request detail
         * @throws IllegalArgumentException if validation fails
         */
        public void validate() {
            // Position code is optional for recruitment request
            if (positionName == null || positionName.trim().isEmpty()) {
                throw new IllegalArgumentException("Position name is required");
            }
            if (jobLevel == null || jobLevel.trim().isEmpty()) {
                throw new IllegalArgumentException("Job level is required");
            }
            if (!jobLevel.matches("JUNIOR|MIDDLE|SENIOR")) {
                throw new IllegalArgumentException("Job level must be JUNIOR, MIDDLE, or SENIOR");
            }
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            if (jobType == null || jobType.trim().isEmpty()) {
                throw new IllegalArgumentException("Job type is required");
            }
            if (recruitmentReason == null || recruitmentReason.trim().isEmpty()) {
                throw new IllegalArgumentException("Recruitment reason is required");
            }
            if (recruitmentReason.length() > 1000) {
                throw new IllegalArgumentException("Recruitment reason cannot exceed 1000 characters");
            }
            // minSalary, maxSalary, salaryType: optional, không kiểm tra
            if (jobSummary == null || jobSummary.trim().isEmpty()) {
                throw new IllegalArgumentException("Job summary is required");
            }
            if (jobSummary.length() > 2000) {
                throw new IllegalArgumentException("Job summary cannot exceed 2000 characters");
            }
            if (workingLocation == null || workingLocation.trim().isEmpty()) {
                throw new IllegalArgumentException("Working location is required");
            }
            // Validate salary values if provided
            if (minSalary != null && minSalary < 0d) {
                throw new IllegalArgumentException("Minimum salary must be a non-negative number");
            }
            if (maxSalary != null && maxSalary < 0d) {
                throw new IllegalArgumentException("Maximum salary must be a non-negative number");
            }
            if (minSalary != null && maxSalary != null && minSalary > maxSalary) {
                throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
            }
            // attachmentPath: optional, không kiểm tra
        }
}