package group4.hrms.email.model;

/**
 * Enum representing the status of email processing.
 * Used to track email queue and delivery status.
 */
public enum EmailStatus {
    /**
     * Email is queued and waiting to be sent
     */
    PENDING("Pending"),

    /**
     * Email has been successfully sent
     */
    SENT("Sent"),

    /**
     * Email sending failed and will be retried
     */
    RETRY("Retry"),

    /**
     * Email sending failed permanently after all retry attempts
     */
    FAILED("Failed");

    private final String displayName;

    EmailStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the status indicates the email is still being processed
     */
    public boolean isProcessing() {
        return this == PENDING || this == RETRY;
    }

    /**
     * Check if the status indicates the email processing is complete
     */
    public boolean isComplete() {
        return this == SENT || this == FAILED;
    }

    /**
     * Check if the status indicates success
     */
    public boolean isSuccess() {
        return this == SENT;
    }

    /**
     * Check if the status indicates failure
     */
    public boolean isFailure() {
        return this == FAILED;
    }
}