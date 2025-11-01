package group4.hrms.email.model;

/**
 * Enum representing different types of email events in the system.
 * Used to categorize and template different email types.
 */
public enum EmailEventType {
    /**
     * Email sent to users who submit contact requests
     */
    CONTACT_RESPONSE("Contact Response", "contact-response.html"),

    /**
     * Email sent to company staff when new contact requests are received
     */
    COMPANY_NOTIFICATION("Company Notification", "company-notification.html"),

    /**
     * Email sent to applicants when they submit CV applications
     */
    CV_CONFIRMATION("CV Application Confirmation", "cv-confirmation.html"),

    /**
     * Email sent to company staff when new CV applications are received
     */
    CV_NOTIFICATION("CV Application Notification", "cv-notification.html");

    private final String displayName;
    private final String templateFileName;

    EmailEventType(String displayName, String templateFileName) {
        this.displayName = displayName;
        this.templateFileName = templateFileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    /**
     * Get EmailEventType by template file name
     */
    public static EmailEventType fromTemplateFileName(String fileName) {
        for (EmailEventType type : values()) {
            if (type.templateFileName.equals(fileName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No EmailEventType found for template file: " + fileName);
    }
}