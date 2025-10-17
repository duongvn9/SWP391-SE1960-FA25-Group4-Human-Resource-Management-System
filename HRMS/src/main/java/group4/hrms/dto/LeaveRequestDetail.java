package group4.hrms.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * DTO cho chi tiết leave request được lưu dưới dạng JSON trong cột detail
 */
public class LeaveRequestDetail {

    private String leaveTypeCode;
    private String leaveTypeName;
    private String startDate;
    private String endDate;
    private Integer dayCount;
    private String reason;
    private String attachmentPath;
    private Boolean certificateRequired;
    private String managerNotes;

    // Half-day leave support fields
    private Boolean isHalfDay;           // true if half-day, false/null if full-day
    private String halfDayPeriod;        // "AM" or "PM", null if full-day
    private Double durationDays;         // 0.5 for half-day, 1.0+ for full-day

    // Gson instance for JSON serialization/deserialization
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Constructors
    public LeaveRequestDetail() {}

    public LeaveRequestDetail(String leaveTypeCode, String leaveTypeName, String startDate,
                             String endDate, Integer dayCount, String reason) {
        this.leaveTypeCode = leaveTypeCode;
        this.leaveTypeName = leaveTypeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayCount = dayCount;
        this.reason = reason;
    }

    // JSON Serialization Methods

    /**
     * Serialize object to JSON string
     * @return JSON string representation of this object
     */
    public String toJson() {
        return gson.toJson(this);
    }

    /**
     * Deserialize JSON string to LeaveRequestDetail object
     * @param json JSON string to deserialize
     * @return LeaveRequestDetail object
     * @throws IllegalArgumentException if JSON is invalid
     */
    public static LeaveRequestDetail fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            return gson.fromJson(json, LeaveRequestDetail.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }
    }

    // Validation Methods

    /**
     * Validate required fields
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (leaveTypeCode == null || leaveTypeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type code is required");
        }

        if (leaveTypeName == null || leaveTypeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type name is required");
        }

        if (startDate == null || startDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (endDate == null || endDate.trim().isEmpty()) {
            throw new IllegalArgumentException("End date is required");
        }

        if (dayCount == null || dayCount <= 0) {
            throw new IllegalArgumentException("Day count must be greater than 0");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required");
        }

        if (reason.length() > 1000) {
            throw new IllegalArgumentException("Reason cannot exceed 1000 characters");
        }

        // Half-day specific validation
        if (isHalfDay != null && isHalfDay) {
            // Half-day period is required
            if (halfDayPeriod == null || halfDayPeriod.trim().isEmpty()) {
                throw new IllegalArgumentException("Half-day period (AM/PM) is required for half-day leave");
            }

            // Period must be AM or PM
            if (!halfDayPeriod.equals("AM") && !halfDayPeriod.equals("PM")) {
                throw new IllegalArgumentException("Half-day period must be 'AM' or 'PM'");
            }

            // Start date must equal end date for half-day
            if (!startDate.equals(endDate)) {
                throw new IllegalArgumentException("Half-day leave can only be requested for a single day");
            }

            // Duration must be 0.5 for half-day
            if (durationDays != null && durationDays != 0.5) {
                throw new IllegalArgumentException("Half-day leave duration must be 0.5 days");
            }
        }

        // Validate durationDays if provided
        if (durationDays != null && durationDays <= 0) {
            throw new IllegalArgumentException("Duration days must be greater than 0");
        }
    }

    // Getters and Setters

    public String getLeaveTypeCode() {
        return leaveTypeCode;
    }

    public void setLeaveTypeCode(String leaveTypeCode) {
        this.leaveTypeCode = leaveTypeCode;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getDayCount() {
        return dayCount;
    }

    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public Boolean getCertificateRequired() {
        return certificateRequired;
    }

    public void setCertificateRequired(Boolean certificateRequired) {
        this.certificateRequired = certificateRequired;
    }

    public String getManagerNotes() {
        return managerNotes;
    }

    public void setManagerNotes(String managerNotes) {
        this.managerNotes = managerNotes;
    }

    public Boolean getIsHalfDay() {
        return isHalfDay;
    }

    public void setIsHalfDay(Boolean isHalfDay) {
        this.isHalfDay = isHalfDay;
    }

    public String getHalfDayPeriod() {
        return halfDayPeriod;
    }

    public void setHalfDayPeriod(String halfDayPeriod) {
        this.halfDayPeriod = halfDayPeriod;
    }

    public Double getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Double durationDays) {
        this.durationDays = durationDays;
    }

    /**
     * Helper method to check if this is a half-day leave request
     * Provides backward compatibility for existing requests without isHalfDay field
     * @return true if half-day, false otherwise
     */
    public boolean isHalfDay() {
        return isHalfDay != null && isHalfDay;
    }

    @Override
    public String toString() {
        return "LeaveRequestDetail{" +
                "leaveTypeCode='" + leaveTypeCode + '\'' +
                ", leaveTypeName='" + leaveTypeName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", dayCount=" + dayCount +
                ", reason='" + reason + '\'' +
                ", attachmentPath='" + attachmentPath + '\'' +
                ", certificateRequired=" + certificateRequired +
                ", managerNotes='" + managerNotes + '\'' +
                ", isHalfDay=" + isHalfDay +
                ", halfDayPeriod='" + halfDayPeriod + '\'' +
                ", durationDays=" + durationDays +
                '}';
    }
}
