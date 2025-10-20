package group4.hrms.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Arrays;

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
     * Handles both modern format (attendanceDates as array) and legacy format (attendance_dates as comma-separated string).
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
            
            // Check if attendance_dates is a string (comma-separated format: "2025-10-20,2025-10-19" or empty "")
            if (jsonObject.has("attendance_dates") && jsonObject.get("attendance_dates").isJsonPrimitive()) {
                String datesString = jsonObject.get("attendance_dates").getAsString();
                List<String> datesList;
                
                if (datesString != null && !datesString.trim().isEmpty()) {
                    // Support both comma and dot as separators: "2025-10-20,2025-10-19" or "2025-10-20.2025-10-19"
                    String[] datesArray = datesString.split("[,.]");
                    datesList = Arrays.asList(datesArray);
                    datesList = datesList.stream()
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(java.util.stream.Collectors.toList());
                } else {
                    // Empty string → empty list
                    datesList = new java.util.ArrayList<>();
                }
                
                // Remove attendance_dates from JSON to avoid Gson parsing error
                jsonObject.remove("attendance_dates");
                
                // Parse object and set dates manually
                AppealRequestDetail detail = gson.fromJson(jsonObject.toString(), AppealRequestDetail.class);
                detail.setAttendanceDates(datesList);
                return detail;
            }
            
            // Standard deserialization for modern format or if attendance_dates is already an array
            return gson.fromJson(json, AppealRequestDetail.class);
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
                '}';
    }
}
