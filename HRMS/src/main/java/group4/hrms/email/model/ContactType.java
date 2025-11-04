package group4.hrms.email.model;

/**
 * Enum representing different types of contact requests.
 * Used to categorize contact form submissions.
 */
public enum ContactType {
    /**
     * Contact related to job recruitment and applications
     */
    RECRUITMENT("Recruitment", "recruitment"),

    /**
     * Contact related to business partnerships
     */
    PARTNERSHIP("Business Partnership", "partnership"),

    /**
     * Contact related to complaints or issues
     */
    COMPLAINT("Complaint", "complaint"),

    /**
     * General inquiries or other types of contact
     */
    OTHER("Other", "other");

    private final String displayName;
    private final String code;

    ContactType(String displayName, String code) {
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
     * Get ContactType by code
     */
    public static ContactType fromCode(String code) {
        for (ContactType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ContactType found for code: " + code);
    }

    /**
     * Get ContactType by display name
     */
    public static ContactType fromDisplayName(String displayName) {
        for (ContactType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ContactType found for display name: " + displayName);
    }
}