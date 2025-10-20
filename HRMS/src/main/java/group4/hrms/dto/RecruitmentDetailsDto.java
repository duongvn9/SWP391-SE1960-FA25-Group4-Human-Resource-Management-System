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

public class RecruitmentDetailsDto implements Serializable {

    // Sử dụng Gson instance để thực hiện Serialization/Deserialization
    private static final Gson GSON = new Gson();

    // Thuộc tính chi tiết được lưu trong JSON (match với DB structure)
    private String positionCode;
    private String positionName;
    private String jobLevel; // SENIOR, JUNIOR, MID_LEVEL, etc. (String in DB, not Integer)
    private Integer quantity;
    private String jobType; // FULL_TIME, PART_TIME, CONTRACT, etc. (stored as "jobType" in DB)
    private String recruitmentReason;
    private Double minSalary; // Minimum salary
    private Double maxSalary; // Maximum salary
    private String salaryType; // GROSS, NET
    private String jobSummary; // Job description
    private String workingLocation; // Working location (separate field)
    private String attachmentPath; // File attachment path

    // Computed fields for display (not stored in DB)
    private transient String budgetSalaryRange; // Formatted salary range for display

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

    public String getRecruitmentReason() {
        return recruitmentReason;
    }

    public void setRecruitmentReason(String recruitmentReason) {
        this.recruitmentReason = recruitmentReason;
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

    public String getJobSummary() {
        return jobSummary;
    }

    public void setJobSummary(String jobSummary) {
        this.jobSummary = jobSummary;
    }

    public String getWorkingLocation() {
        return workingLocation;
    }

    public void setWorkingLocation(String workingLocation) {
        this.workingLocation = workingLocation;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    /**
     * Get formatted salary range for display.
     * Computes from minSalary, maxSalary, and salaryType if not already set.
     */
    public String getBudgetSalaryRange() {
        if (budgetSalaryRange == null && minSalary != null && maxSalary != null) {
            String formattedMin = String.format("%,.0f", minSalary);
            String formattedMax = String.format("%,.0f", maxSalary);
            String type = (salaryType != null) ? salaryType : "";
            budgetSalaryRange = formattedMin + " - " + formattedMax + " VND (" + type + ")";
        }
        return budgetSalaryRange;
    }

    public void setBudgetSalaryRange(String budgetSalaryRange) {
        this.budgetSalaryRange = budgetSalaryRange;
    }
}
