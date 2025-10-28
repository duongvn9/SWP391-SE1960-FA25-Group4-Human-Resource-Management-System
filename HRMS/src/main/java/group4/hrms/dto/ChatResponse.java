package group4.hrms.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO cho chatbot response
 * Chứa kết quả trả về từ Gemini AI
 */
public class ChatResponse {
    private boolean success;
    private String answer;
    private String error;
    private String timestamp;

    // Constructors
    public ChatResponse() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ChatResponse(boolean success, String answer, String error) {
        this.success = success;
        this.answer = answer;
        this.error = error;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ChatResponse(boolean success, String answer, String error, String timestamp) {
        this.success = success;
        this.answer = answer;
        this.error = error;
        this.timestamp = timestamp;
    }

    // Static factory methods
    public static ChatResponse success(String answer) {
        return new ChatResponse(true, answer, null);
    }

    public static ChatResponse error(String errorMessage) {
        return new ChatResponse(false, null, errorMessage);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "success=" + success +
                ", answer='" + (answer != null ? answer.substring(0, Math.min(50, answer.length())) + "..." : "null")
                + '\'' +
                ", error='" + error + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
