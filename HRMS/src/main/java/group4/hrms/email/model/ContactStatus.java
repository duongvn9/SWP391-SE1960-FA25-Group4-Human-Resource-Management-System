package group4.hrms.email.model;

/**
 * Enum representing the status of contact requests.
 * Used to track the processing status of contact form submissions.
 */
public enum ContactStatus {
    /**
     * Contact request has been received but not yet processed
     */
    NEW("Mới", "new"),

    /**
     * Contact request is being processed by staff
     */
    IN_PROGRESS("Đang xử lý", "in_progress"),

    /**
     * Contact request has been resolved and completed
     */
    RESOLVED("Đã giải quyết", "resolved");

    private final String displayName;
    private final String code;

    ContactStatus(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get ContactStatus by code
     */
    public static ContactStatus fromCode(String code) {
        for (ContactStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No ContactStatus found for code: " + code);
    }

    /**
     * Get ContactStatus by display name
     */
    public static ContactStatus fromDisplayName(String displayName) {
        for (ContactStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No ContactStatus found for display name: " + displayName);
    }

    /**
     * Check if the status indicates the contact is still active
     */
    public boolean isActive() {
        return this == NEW || this == IN_PROGRESS;
    }

    /**
     * Check if the status indicates the contact is completed
     */
    public boolean isCompleted() {
        return this == RESOLVED;
    }
}