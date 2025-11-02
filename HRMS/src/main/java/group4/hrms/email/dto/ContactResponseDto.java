package group4.hrms.email.dto;

/**
 * DTO for contact form response
 */
public class ContactResponseDto {
    private String referenceId;
    private String message;
    private boolean success;

    public ContactResponseDto(String referenceId, String message, boolean success) {
        this.referenceId = referenceId;
        this.message = message;
        this.success = success;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
