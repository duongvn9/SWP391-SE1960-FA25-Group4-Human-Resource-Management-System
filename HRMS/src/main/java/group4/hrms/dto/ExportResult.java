package group4.hrms.dto;

import java.time.LocalDateTime;

/**
 * DTO for payslip export results
 */
public class ExportResult {
    private boolean success;
    private String filePath;
    private String fileName;
    private int exportedCount;
    private String message;
    private LocalDateTime exportTime;
    private String errorMessage;
    private String contentType;
    private byte[] data;
    private int recordCount;

    public ExportResult() {
        this.success = true;
        this.exportTime = LocalDateTime.now();
    }

    public ExportResult(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getExportedCount() {
        return exportedCount;
    }

    public void setExportedCount(int exportedCount) {
        this.exportedCount = exportedCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getExportTime() {
        return exportTime;
    }

    public void setExportTime(LocalDateTime exportTime) {
        this.exportTime = exportTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Additional methods needed by PayslipListController
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return fileName; // Alias for getFileName
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
}