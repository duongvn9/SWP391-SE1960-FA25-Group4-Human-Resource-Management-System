package group4.hrms.dto;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

/**
 * Data Transfer Object for Attendance Appeal Request details.
 * This DTO is serialized to JSON and stored in the 'detail' column of the requests table.
 *
 * <p>Contains all information about an attendance appeal request including:
 * <ul>
 *   <li>List of attendance dates being disputed</li>
 *   <li>Dispute reason and detailed explanation</li>
 *   <li>Supporting attachment information</li>
 *   <li>HR/HRM approval notes</li>
 * </ul>
 *
 * <p><strong>Authorization:</strong> Only HR and HRM roles can approve/reject this type of request.
 *
 * @author Group4 HRMS Team
 * @version 1.0
 * @see group4.hrms.service.AppealRequestService
 */
public class AppealRequestDetail {

    // Core fields
    @SerializedName(value = "attendanceDates", alternate = {"attendance_dates"})
    private List<String> attendanceDates;  // List of dates being disputed (format: "yyyy-MM-dd")

    @SerializedName(value = "reason", alternate = {"detail_text"})
    private String reason;                 // Reason/explanation of the dispute (maps to detail_text in DB)

    @SerializedName(value = "attachmentPath", alternate = {"attachment_path"})
    private String attachmentPath;         // Path to supporting document (optional)

    // HR/HRM review fields
    @SerializedName(value = "hrNotes", alternate = {"hr_notes"})
    private String hrNotes;                // Notes from HR during review

    @SerializedName(value = "hrmNotes", alternate = {"hrm_notes"})
    private String hrmNotes;               // Notes from HRM during review

    @SerializedName(value = "resolutionAction", alternate = {"resolution_action"})
    private String resolutionAction;       // Action taken to resolve the dispute

    // Metadata
    @SerializedName(value = "submittedDate", alternate = {"submitted_date"})
    private String submittedDate;          // Date when appeal was submitted (format: "yyyy-MM-dd")

    @SerializedName(value = "appealStatus", alternate = {"appeal_status"})
    private String appealStatus;           // Status of appeal processing (PENDING, APPROVED, REJECTED)

    // Gson instance for JSON serialization/deserialization
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Default constructor for AppealRequestDetail.
     */
    public AppealRequestDetail() {}

    /**
     * Constructor with core appeal request fields.
     *
     * @param attendanceDates list of attendance dates being disputed
     * @param reason reason/explanation of the dispute
     */
    public AppealRequestDetail(List<String> attendanceDates, String reason) {
        this.attendanceDates = attendanceDates;
        this.reason = reason;
    }

    // JSON Serialization Methods

    /**
     * Serializes this object to a JSON string.
     *
     * @return JSON string representation of this AppealRequestDetail object
     */
    public String toJson() {
        return gson.toJson(this);
    }

    /**
     * Deserializes a JSON string to an AppealRequestDetail object.
     * Handles multiple JSON formats:
     * 1. Modern format with attendanceDates array
     * 2. Legacy format with attendance_dates string
     * 3. Records format with dates extracted from records array
     *
     * @param json the JSON string to deserialize
     * @return the deserialized AppealRequestDetail object
     * @throws IllegalArgumentException if JSON is null, empty, or has invalid format
     */
    public static AppealRequestDetail fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            // Parse JSON to check format
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            // Extract attendance dates from different possible sources
            List<String> datesList = new java.util.ArrayList<>();

            // Method 1: Check for attendanceDates array (modern format)
            if (jsonObject.has("attendanceDates") && jsonObject.get("attendanceDates").isJsonArray()) {
                jsonObject.getAsJsonArray("attendanceDates").forEach(element -> {
                    if (element.isJsonPrimitive()) {
                        datesList.add(element.getAsString());
                    }
                });
            }
            // Method 2: Check for attendance_dates string (legacy format)
            else if (jsonObject.has("attendance_dates") && jsonObject.get("attendance_dates").isJsonPrimitive()) {
                String datesString = jsonObject.get("attendance_dates").getAsString();
                if (datesString != null && !datesString.trim().isEmpty()) {
                    // Support both comma and dot as separators
                    String[] datesArray = datesString.split("[,.]");
                    for (String date : datesArray) {
                        String trimmedDate = date.trim();
                        if (!trimmedDate.isEmpty()) {
                            datesList.add(trimmedDate);
                        }
                    }
                }
            }
            // Method 3: Extract dates from records array (current format)
            else if (jsonObject.has("records") && jsonObject.get("records").isJsonArray()) {
                jsonObject.getAsJsonArray("records").forEach(element -> {
                    if (element.isJsonObject()) {
                        JsonObject recordObj = element.getAsJsonObject();
                        // Try to get date from newRecord first, then oldRecord
                        String date = null;
                        if (recordObj.has("newRecord") && recordObj.getAsJsonObject("newRecord").has("date")) {
                            date = recordObj.getAsJsonObject("newRecord").get("date").getAsString();
                        } else if (recordObj.has("oldRecord") && recordObj.getAsJsonObject("oldRecord").has("date")) {
                            date = recordObj.getAsJsonObject("oldRecord").get("date").getAsString();
                        }
                        if (date != null && !datesList.contains(date)) {
                            datesList.add(date);
                        }
                    }
                });
            }

            // Remove problematic fields before Gson parsing
            jsonObject.remove("attendance_dates");

            // Parse object with Gson
            AppealRequestDetail detail = gson.fromJson(jsonObject.toString(), AppealRequestDetail.class);

            // Set extracted dates
            detail.setAttendanceDates(datesList);

            // Set default submittedDate if not present (use current date)
            if (detail.getSubmittedDate() == null || detail.getSubmittedDate().trim().isEmpty()) {
                detail.setSubmittedDate(java.time.LocalDate.now().toString());
            }

            return detail;
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }
    }

    // Validation Methods

    /**
     * Validates all required fields of this appeal request detail.
     *
     * <p>Validation rules:
     * <ul>
     *   <li>attendanceDates must not be null or empty</li>
     *   <li>Each date must be in valid format (yyyy-MM-dd)</li>
     *   <li>reason must not be null or empty</li>
     *   <li>reason must not exceed 1000 characters</li>
     * </ul>
     *
     * @throws IllegalArgumentException if any validation rule fails
     */
    public void validate() {
        if (attendanceDates == null || attendanceDates.isEmpty()) {
            throw new IllegalArgumentException("At least one attendance date is required");
        }

        // Validate date format (basic check)
        for (String date : attendanceDates) {
            if (date == null || date.trim().isEmpty()) {
                throw new IllegalArgumentException("Attendance date cannot be null or empty");
            }
            // Basic format check: yyyy-MM-dd (10 characters)
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
            }
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required");
        }

        if (reason.length() > 1000) {
            throw new IllegalArgumentException("Reason cannot exceed 1000 characters");
        }
    }

    // Getters and Setters

    public List<String> getAttendanceDates() {
        return attendanceDates;
    }

    public void setAttendanceDates(List<String> attendanceDates) {
        this.attendanceDates = attendanceDates;
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

    public String getHrNotes() {
        return hrNotes;
    }

    public void setHrNotes(String hrNotes) {
        this.hrNotes = hrNotes;
    }

    public String getHrmNotes() {
        return hrmNotes;
    }

    public void setHrmNotes(String hrmNotes) {
        this.hrmNotes = hrmNotes;
    }

    public String getResolutionAction() {
        return resolutionAction;
    }

    public void setResolutionAction(String resolutionAction) {
        this.resolutionAction = resolutionAction;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(String appealStatus) {
        this.appealStatus = appealStatus;
    }

    /**
     * Get attendance records from the original JSON for display purposes
     * This method parses both edited records and new records arrays
     *
     * @param originalJson The original JSON string containing records
     * @return List of AttendanceRecordInfo for display
     */
    public static java.util.List<AttendanceRecordInfo> getAttendanceRecords(String originalJson) {
        java.util.List<AttendanceRecordInfo> records = new java.util.ArrayList<>();

        if (originalJson == null || originalJson.trim().isEmpty()) {
            return records;
        }

        try {
            JsonObject jsonObject = JsonParser.parseString(originalJson).getAsJsonObject();

            // Parse "Edit records" array (edited existing records)
            if (jsonObject.has("Edit records") && jsonObject.get("Edit records").isJsonArray()) {
                jsonObject.getAsJsonArray("Edit records").forEach(element -> {
                    if (element.isJsonObject()) {
                        JsonObject recordObj = element.getAsJsonObject();

                        AttendanceRecordInfo info = new AttendanceRecordInfo();
                        info.recordType = "EDIT"; // Mark as edited record

                        // Parse newRecord
                        if (recordObj.has("newRecord") && recordObj.get("newRecord").isJsonObject()) {
                            JsonObject newRecord = recordObj.getAsJsonObject("newRecord");
                            info.date = newRecord.has("date") ? newRecord.get("date").getAsString() : "";
                            info.newCheckIn = newRecord.has("checkIn") ? newRecord.get("checkIn").getAsString() : "";
                            info.newCheckOut = newRecord.has("checkOut") ? newRecord.get("checkOut").getAsString() : "";
                            info.newStatus = newRecord.has("status") ? newRecord.get("status").getAsString() : "";
                        }

                        // Parse oldRecord
                        if (recordObj.has("oldRecord") && recordObj.get("oldRecord").isJsonObject()) {
                            JsonObject oldRecord = recordObj.getAsJsonObject("oldRecord");
                            info.oldCheckIn = oldRecord.has("checkIn") ? oldRecord.get("checkIn").getAsString() : "";
                            info.oldCheckOut = oldRecord.has("checkOut") ? oldRecord.get("checkOut").getAsString() : "";
                            info.oldStatus = oldRecord.has("status") ? oldRecord.get("status").getAsString() : "";
                            info.source = oldRecord.has("source") ? oldRecord.get("source").getAsString() : "";
                            info.period = oldRecord.has("period") ? oldRecord.get("period").getAsString() : "";
                        }

                        records.add(info);
                    }
                });
            }

            // Parse "newRecords" array (completely new records)
            if (jsonObject.has("newRecords") && jsonObject.get("newRecords").isJsonArray()) {
                jsonObject.getAsJsonArray("newRecords").forEach(element -> {
                    if (element.isJsonObject()) {
                        JsonObject newRecordObj = element.getAsJsonObject();

                        AttendanceRecordInfo info = new AttendanceRecordInfo();
                        info.recordType = "NEW"; // Mark as new record
                        
                        // For new records, there's no old record - only new data
                        info.date = newRecordObj.has("date") ? newRecordObj.get("date").getAsString() : "";
                        info.newCheckIn = newRecordObj.has("checkIn") ? newRecordObj.get("checkIn").getAsString() : "";
                        info.newCheckOut = newRecordObj.has("checkOut") ? newRecordObj.get("checkOut").getAsString() : "";
                        info.newStatus = newRecordObj.has("status") ? newRecordObj.get("status").getAsString() : "";
                        
                        // No old record data for new records
                        info.oldCheckIn = "";
                        info.oldCheckOut = "";
                        info.oldStatus = "";
                        info.source = "appeal"; // New records are from appeal
                        info.period = ""; // Will be determined by system

                        records.add(info);
                    }
                });
            }

            // Fallback: Parse legacy "records" array format for backward compatibility
            if (records.isEmpty() && jsonObject.has("records") && jsonObject.get("records").isJsonArray()) {
                jsonObject.getAsJsonArray("records").forEach(element -> {
                    if (element.isJsonObject()) {
                        JsonObject recordObj = element.getAsJsonObject();

                        AttendanceRecordInfo info = new AttendanceRecordInfo();
                        info.recordType = "EDIT"; // Assume edited for legacy format

                        // Parse newRecord
                        if (recordObj.has("newRecord") && recordObj.get("newRecord").isJsonObject()) {
                            JsonObject newRecord = recordObj.getAsJsonObject("newRecord");
                            info.date = newRecord.has("date") ? newRecord.get("date").getAsString() : "";
                            info.newCheckIn = newRecord.has("checkIn") ? newRecord.get("checkIn").getAsString() : "";
                            info.newCheckOut = newRecord.has("checkOut") ? newRecord.get("checkOut").getAsString() : "";
                            info.newStatus = newRecord.has("status") ? newRecord.get("status").getAsString() : "";
                        }

                        // Parse oldRecord
                        if (recordObj.has("oldRecord") && recordObj.get("oldRecord").isJsonObject()) {
                            JsonObject oldRecord = recordObj.getAsJsonObject("oldRecord");
                            info.oldCheckIn = oldRecord.has("checkIn") ? oldRecord.get("checkIn").getAsString() : "";
                            info.oldCheckOut = oldRecord.has("checkOut") ? oldRecord.get("checkOut").getAsString() : "";
                            info.oldStatus = oldRecord.has("status") ? oldRecord.get("status").getAsString() : "";
                            info.source = oldRecord.has("source") ? oldRecord.get("source").getAsString() : "";
                            info.period = oldRecord.has("period") ? oldRecord.get("period").getAsString() : "";
                        }

                        records.add(info);
                    }
                });
            }
        } catch (Exception e) {
            // Return empty list if parsing fails
        }

        return records;
    }

    /**
     * Inner class to hold attendance record information for display
     */
    public static class AttendanceRecordInfo {
        public String date;
        public String newCheckIn;
        public String newCheckOut;
        public String newStatus;
        public String oldCheckIn;
        public String oldCheckOut;
        public String oldStatus;
        public String source;
        public String period;
        public String recordType; // "EDIT" or "NEW"

        public String getDate() { return date; }
        public String getNewCheckIn() { return newCheckIn; }
        public String getNewCheckOut() { return newCheckOut; }
        public String getNewStatus() { return newStatus; }
        public String getOldCheckIn() { return oldCheckIn; }
        public String getOldCheckOut() { return oldCheckOut; }
        public String getOldStatus() { return oldStatus; }
        public String getSource() { return source; }
        public String getPeriod() { return period; }
        public String getRecordType() { return recordType; }
        
        // Helper methods
        public boolean isNewRecord() { return "NEW".equals(recordType); }
        public boolean isEditRecord() { return "EDIT".equals(recordType); }
    }

    @Override
    public String toString() {
        return "AppealRequestDetail{" +
                "attendanceDates=" + attendanceDates +
                ", reason='" + reason + '\'' +
                ", attachmentPath='" + attachmentPath + '\'' +
                ", hrNotes='" + hrNotes + '\'' +
                ", hrmNotes='" + hrmNotes + '\'' +
                ", resolutionAction='" + resolutionAction + '\'' +
                ", submittedDate='" + submittedDate + '\'' +
                ", appealStatus='" + appealStatus + '\'' +
                '}';
    }
}
