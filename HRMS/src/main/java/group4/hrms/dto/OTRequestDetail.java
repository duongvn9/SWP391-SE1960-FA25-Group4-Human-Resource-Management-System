package group4.hrms.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Data Transfer Object for OT (Overtime) Request details.
 * This DTO is serialized to JSON and stored in the 'detail' column of the requests table.
 *
 * <p>Contains all information about an overtime request including:
 * <ul>
 *   <li>Date and time information (otDate, startTime, endTime, otHours)</li>
 *   <li>OT type and pay multiplier (otType, payMultiplier)</li>
 *   <li>Employee consent information (employeeConsent, consentTimestamp)</li>
 *   <li>Manager creation metadata (createdByManager, managerAccountId, managerNotes)</li>
 * </ul>
 *
 * @author Group4
 * @version 1.0
 * @see group4.hrms.service.OTRequestService
 */
public class OTRequestDetail {

    // Core fields
    private String otDate;           // "2025-01-15"
    private String startTime;        // "18:00"
    private String endTime;          // "20:00"
    private Double otHours;          // 2.0
    private String reason;           // Lý do làm OT

    // OT Type & Pay
    private String otType;           // "WEEKDAY" | "WEEKEND" | "HOLIDAY" | "COMPENSATORY"
    private Double payMultiplier;    // 1.5 | 2.0 | 3.0

    // Employee Consent
    private Boolean employeeConsent; // true
    private String consentTimestamp; // "2025-01-10T14:30:00"

    // Manager creation
    private Boolean createdByManager; // false
    private Long managerAccountId;    // null or manager ID
    private String managerNotes;      // Ghi chú của manager

    // Gson instance for JSON serialization/deserialization
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Default constructor for OTRequestDetail.
     */
    public OTRequestDetail() {}

    /**
     * Constructor with core OT request fields.
     *
     * @param otDate the date of overtime work (format: "yyyy-MM-dd")
     * @param startTime the start time of overtime (format: "HH:mm")
     * @param endTime the end time of overtime (format: "HH:mm")
     * @param otHours the total overtime hours
     * @param reason the reason for overtime request
     * @param otType the type of overtime (WEEKDAY, WEEKEND, HOLIDAY, COMPENSATORY)
     * @param payMultiplier the pay multiplier (1.5, 2.0, or 3.0)
     * @param employeeConsent whether employee consents to overtime
     */
    public OTRequestDetail(String otDate, String startTime, String endTime,
                          Double otHours, String reason, String otType,
                          Double payMultiplier, Boolean employeeConsent) {
        this.otDate = otDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.otHours = otHours;
        this.reason = reason;
        this.otType = otType;
        this.payMultiplier = payMultiplier;
        this.employeeConsent = employeeConsent;
    }

    // JSON Serialization Methods

    /**
     * Serializes this object to a JSON string.
     *
     * @return JSON string representation of this OTRequestDetail object
     */
    public String toJson() {
        return gson.toJson(this);
    }

    /**
     * Deserializes a JSON string to an OTRequestDetail object.
     *
     * @param json the JSON string to deserialize
     * @return the deserialized OTRequestDetail object
     * @throws IllegalArgumentException if JSON is null, empty, or has invalid format
     */
    public static OTRequestDetail fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            return gson.fromJson(json, OTRequestDetail.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }
    }

    // Validation Methods

    /**
     * Validates all required fields of this OT request detail.
     *
     * <p>Validation rules:
     * <ul>
     *   <li>otDate, startTime, endTime, reason must not be null or empty</li>
     *   <li>otHours must be greater than 0</li>
     *   <li>reason must not exceed 1000 characters</li>
     *   <li>otType must be one of: WEEKDAY, WEEKEND, HOLIDAY, COMPENSATORY</li>
     *   <li>payMultiplier must be 1.5, 2.0, or 3.0</li>
     *   <li>employeeConsent must be true</li>
     *   <li>consentTimestamp must not be null or empty</li>
     * </ul>
     *
     * @throws IllegalArgumentException if any validation rule fails
     */
    public void validate() {
        if (otDate == null || otDate.trim().isEmpty()) {
            throw new IllegalArgumentException("OT date is required");
        }

        if (startTime == null || startTime.trim().isEmpty()) {
            throw new IllegalArgumentException("Start time is required");
        }

        if (endTime == null || endTime.trim().isEmpty()) {
            throw new IllegalArgumentException("End time is required");
        }

        if (otHours == null || otHours <= 0) {
            throw new IllegalArgumentException("OT hours must be greater than 0");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required");
        }

        if (reason.length() > 1000) {
            throw new IllegalArgumentException("Reason cannot exceed 1000 characters");
        }

        if (otType == null || otType.trim().isEmpty()) {
            throw new IllegalArgumentException("OT type is required");
        }

        if (!otType.matches("WEEKDAY|WEEKEND|HOLIDAY|COMPENSATORY")) {
            throw new IllegalArgumentException("Invalid OT type. Must be WEEKDAY, WEEKEND, HOLIDAY, or COMPENSATORY");
        }

        if (payMultiplier == null) {
            throw new IllegalArgumentException("Pay multiplier is required");
        }

        if (payMultiplier != 1.5 && payMultiplier != 2.0 && payMultiplier != 3.0) {
            throw new IllegalArgumentException("Pay multiplier must be 1.5, 2.0, or 3.0");
        }

        if (employeeConsent == null || !employeeConsent) {
            throw new IllegalArgumentException("Employee consent is required");
        }

        if (consentTimestamp == null || consentTimestamp.trim().isEmpty()) {
            throw new IllegalArgumentException("Consent timestamp is required");
        }
    }

    // Getters and Setters

    public String getOtDate() {
        return otDate;
    }

    public void setOtDate(String otDate) {
        this.otDate = otDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getOtHours() {
        return otHours;
    }

    public void setOtHours(Double otHours) {
        this.otHours = otHours;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOtType() {
        return otType;
    }

    public void setOtType(String otType) {
        this.otType = otType;
    }

    public Double getPayMultiplier() {
        return payMultiplier;
    }

    public void setPayMultiplier(Double payMultiplier) {
        this.payMultiplier = payMultiplier;
    }

    public Boolean getEmployeeConsent() {
        return employeeConsent;
    }

    public void setEmployeeConsent(Boolean employeeConsent) {
        this.employeeConsent = employeeConsent;
    }

    public String getConsentTimestamp() {
        return consentTimestamp;
    }

    public void setConsentTimestamp(String consentTimestamp) {
        this.consentTimestamp = consentTimestamp;
    }

    public Boolean getCreatedByManager() {
        return createdByManager;
    }

    public void setCreatedByManager(Boolean createdByManager) {
        this.createdByManager = createdByManager;
    }

    public Long getManagerAccountId() {
        return managerAccountId;
    }

    public void setManagerAccountId(Long managerAccountId) {
        this.managerAccountId = managerAccountId;
    }

    public String getManagerNotes() {
        return managerNotes;
    }

    public void setManagerNotes(String managerNotes) {
        this.managerNotes = managerNotes;
    }

    @Override
    public String toString() {
        return "OTRequestDetail{" +
                "otDate='" + otDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", otHours=" + otHours +
                ", reason='" + reason + '\'' +
                ", otType='" + otType + '\'' +
                ", payMultiplier=" + payMultiplier +
                ", employeeConsent=" + employeeConsent +
                ", consentTimestamp='" + consentTimestamp + '\'' +
                ", createdByManager=" + createdByManager +
                ", managerAccountId=" + managerAccountId +
                ", managerNotes='" + managerNotes + '\'' +
                '}';
    }
}
