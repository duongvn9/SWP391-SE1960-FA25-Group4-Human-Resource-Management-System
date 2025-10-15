/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group4.hrms.dto;

/**
 *
 * @author HieuTrung
 */
import com.google.gson.Gson;
import java.io.Serializable;

public class RecruitmentDetailsDto implements Serializable {

    // Sử dụng Gson instance để thực hiện Serialization/Deserialization
    private static final Gson GSON = new Gson();

    // Thuộc tính chi tiết được lưu trong JSON
    private String positionCode;
    private String positionName;
    private Integer jobLevel;
    private Integer quantity;
    private String type; // Full-time, Part-time, etc.
    private String recruitmentReason;
    private String budgetSalaryRange;
    private String jobSummary; // Tóm tắt công việc (dùng cho mô tả)
    private String attachmentPath; // Đường dẫn file đính kèm

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

    public Integer getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(Integer jobLevel) {
        this.jobLevel = jobLevel;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecruitmentReason() {
        return recruitmentReason;
    }

    public void setRecruitmentReason(String recruitmentReason) {
        this.recruitmentReason = recruitmentReason;
    }

    public String getBudgetSalaryRange() {
        return budgetSalaryRange;
    }

    public void setBudgetSalaryRange(String budgetSalaryRange) {
        this.budgetSalaryRange = budgetSalaryRange;
    }

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
}
