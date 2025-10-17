package group4.hrms.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a validation operation
 */
public class ValidationResult {
    private boolean valid;
    private List<String> errorMessages;

    public ValidationResult() {
        this.valid = true;
        this.errorMessages = new ArrayList<>();
    }

    public ValidationResult(boolean valid) {
        this.valid = valid;
        this.errorMessages = new ArrayList<>();
    }

    public ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessages = new ArrayList<>();
        if (errorMessage != null && !errorMessage.isEmpty()) {
            this.errorMessages.add(errorMessage);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void addErrorMessage(String message) {
        this.errorMessages.add(message);
        this.valid = false;
    }

    public String getFirstErrorMessage() {
        return errorMessages.isEmpty() ? null : errorMessages.get(0);
    }

    public String getAllErrorMessages() {
        return String.join("; ", errorMessages);
    }

    public static ValidationResult success() {
        return new ValidationResult(true);
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }
}
