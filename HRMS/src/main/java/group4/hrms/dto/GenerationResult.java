package group4.hrms.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for payslip generation results
 * Requirements: 9.4, 9.5
 */
public class GenerationResult {

    private boolean success;
    private int createdCount;
    private int updatedCount;
    private int skippedCount;
    private int errorCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<GenerationError> errors;
    private String message;

    // Default constructor
    public GenerationResult() {
        this.success = true;
        this.createdCount = 0;
        this.updatedCount = 0;
        this.skippedCount = 0;
        this.errorCount = 0;
        this.errors = new ArrayList<>();
        this.startTime = LocalDateTime.now();
    }

    // Constructor with basic info
    public GenerationResult(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    // Mark generation as completed
    public void markCompleted() {
        this.endTime = LocalDateTime.now();
    }

    // Add error
    public void addError(Long userId, String error, Exception exception) {
        this.errors.add(new GenerationError(userId, error, exception));
        this.errorCount++;
        this.success = false;
    }

    // Add error without exception
    public void addError(Long userId, String error) {
        addError(userId, error, null);
    }

    // Increment counters
    public void incrementCreated() {
        this.createdCount++;
    }

    public void incrementUpdated() {
        this.updatedCount++;
    }

    public void incrementSkipped() {
        this.skippedCount++;
    }

    // Calculate total processed
    public int getTotalProcessed() {
        return createdCount + updatedCount + skippedCount + errorCount;
    }

    // Calculate duration in milliseconds
    public long getDurationMs() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, endTime).toMillis();
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(int createdCount) {
        this.createdCount = createdCount;
    }

    public int getUpdatedCount() {
        return updatedCount;
    }

    public void setUpdatedCount(int updatedCount) {
        this.updatedCount = updatedCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<GenerationError> getErrors() {
        return errors;
    }

    public void setErrors(List<GenerationError> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Additional methods needed by PayslipListController
    public int getTotalRequested() {
        return createdCount + updatedCount + skippedCount + errorCount;
    }

    public long getDurationMillis() {
        if (startTime == null) return 0;
        LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
        return java.time.Duration.between(startTime, end).toMillis();
    }

    public boolean hasErrors() {
        return errorCount > 0 || !errors.isEmpty();
    }

    /**
     * Inner class for generation errors
     */
    public static class GenerationError {
        private Long userId;
        private String error;
        private String exceptionMessage;
        private LocalDateTime timestamp;

        public GenerationError(Long userId, String error, Exception exception) {
            this.userId = userId;
            this.error = error;
            this.exceptionMessage = exception != null ? exception.getMessage() : null;
            this.timestamp = LocalDateTime.now();
        }

        // Getters and setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getExceptionMessage() {
            return exceptionMessage;
        }

        public void setExceptionMessage(String exceptionMessage) {
            this.exceptionMessage = exceptionMessage;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("GenerationError{userId=%d, error='%s', exception='%s', timestamp=%s}",
                               userId, error, exceptionMessage, timestamp);
        }
    }

    @Override
    public String toString() {
        return String.format("GenerationResult{success=%s, created=%d, updated=%d, skipped=%d, errors=%d, duration=%dms}",
                           success, createdCount, updatedCount, skippedCount, errorCount, getDurationMs());
    }
}