package group4.hrms.dto;

/**
 * Represents conflict information for half-day leave requests
 */
public class HalfDayConflict {
    private boolean hasConflict;
    private String conflictType; // "FULL_DAY", "SAME_PERIOD", "NONE"
    private String conflictDate;
    private String conflictPeriod; // "AM", "PM", or null
    private String conflictLeaveType;
    private String conflictStatus;
    private String message;

    public HalfDayConflict() {
        this.hasConflict = false;
        this.conflictType = "NONE";
    }

    public HalfDayConflict(boolean hasConflict, String conflictType, String message) {
        this.hasConflict = hasConflict;
        this.conflictType = conflictType;
        this.message = message;
    }

    // Getters and Setters
    public boolean isHasConflict() {
        return hasConflict;
    }

    public void setHasConflict(boolean hasConflict) {
        this.hasConflict = hasConflict;
    }

    public String getConflictType() {
        return conflictType;
    }

    public void setConflictType(String conflictType) {
        this.conflictType = conflictType;
    }

    public String getConflictDate() {
        return conflictDate;
    }

    public void setConflictDate(String conflictDate) {
        this.conflictDate = conflictDate;
    }

    public String getConflictPeriod() {
        return conflictPeriod;
    }

    public void setConflictPeriod(String conflictPeriod) {
        this.conflictPeriod = conflictPeriod;
    }

    public String getConflictLeaveType() {
        return conflictLeaveType;
    }

    public void setConflictLeaveType(String conflictLeaveType) {
        this.conflictLeaveType = conflictLeaveType;
    }

    public String getConflictStatus() {
        return conflictStatus;
    }

    public void setConflictStatus(String conflictStatus) {
        this.conflictStatus = conflictStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean hasConflict() {
        return hasConflict;
    }

    public String getErrorMessage() {
        return message != null ? message : "Unknown conflict";
    }

    public static HalfDayConflict noConflict() {
        return new HalfDayConflict(false, "NONE", "No conflict found");
    }

    public static HalfDayConflict fullDayConflict(String date, String leaveType, String status) {
        HalfDayConflict conflict = new HalfDayConflict(true, "FULL_DAY",
            "Cannot request half-day: Full-day " + leaveType + " leave already exists on " + date + " (Status: " + status + ")");
        conflict.setConflictDate(date);
        conflict.setConflictLeaveType(leaveType);
        conflict.setConflictStatus(status);
        return conflict;
    }

    public static HalfDayConflict samePeriodConflict(String date, String period, String leaveType, String status) {
        HalfDayConflict conflict = new HalfDayConflict(true, "SAME_PERIOD",
            "Cannot request half-day: " + period + " half-day " + leaveType + " leave already exists on " + date + " (Status: " + status + ")");
        conflict.setConflictDate(date);
        conflict.setConflictPeriod(period);
        conflict.setConflictLeaveType(leaveType);
        conflict.setConflictStatus(status);
        return conflict;
    }
}
