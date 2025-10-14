package group4.hrms.exception;

/**
 * LeaveValidationException - Custom exception for leave request validation errors
 * Requirements: 6
 *
 * Wraps ValidationErrorMessage to provide structured error information
 * that can be caught and formatted in the controller
 */
public class LeaveValidationException extends IllegalArgumentException {

    private final ValidationErrorMessage errorMessage;

    public LeaveValidationException(ValidationErrorMessage errorMessage) {
        super(errorMessage.getFormattedError());
        this.errorMessage = errorMessage;
    }

    /**
     * Get the structured error message
     */
    public ValidationErrorMessage getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get error type
     */
    public String getErrorType() {
        return errorMessage.getType();
    }

    /**
     * Get short error message
     */
    public String getShortMessage() {
        return errorMessage.getMessage();
    }

    /**
     * Get detailed error message
     */
    public String getDetailedMessage() {
        return errorMessage.getDetails();
    }

    /**
     * Get HTML formatted error for JSP display
     */
    public String getHtmlFormattedError() {
        return errorMessage.getHtmlFormattedError();
    }
}
