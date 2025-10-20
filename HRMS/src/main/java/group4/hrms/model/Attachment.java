package group4.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity Attachment - File đính kèm
 * Mapping từ bảng attachments trong database
 */
public class Attachment {

    // Các field khớp với database schema
    private Long id;
    private String ownerType;               // owner_type (NVARCHAR(64))
    private Long ownerId;                   // owner_id
    private String path;                    // path (NVARCHAR(1024))
    private String originalName;            // original_name (NVARCHAR(255))
    private String contentType;             // content_type (NVARCHAR(128))
    private Long sizeBytes;                 // size_bytes
    private String checksumSha256;          // checksum_sha256 (CHAR(64))
    private Long uploadedByAccountId;       // uploaded_by_account_id
    private LocalDateTime createdAt;        // created_at
    private String attachmentType;          // attachment_type (VARCHAR(10)) - 'FILE' or 'LINK'
    private String externalUrl;             // external_url (VARCHAR(500)) - for Google Drive links

    // Constructors
    public Attachment() {}

    public Attachment(String ownerType, Long ownerId, String path, String originalName,
                     String contentType, Long sizeBytes, Long uploadedByAccountId) {
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.path = path;
        this.originalName = originalName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.uploadedByAccountId = uploadedByAccountId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getChecksumSha256() {
        return checksumSha256;
    }

    public void setChecksumSha256(String checksumSha256) {
        this.checksumSha256 = checksumSha256;
    }

    public Long getUploadedByAccountId() {
        return uploadedByAccountId;
    }

    public void setUploadedByAccountId(Long uploadedByAccountId) {
        this.uploadedByAccountId = uploadedByAccountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    // Business methods

    /**
     * Check if this is a file upload (vs external link)
     */
    public boolean isFileUpload() {
        return "FILE".equals(attachmentType) || attachmentType == null;
    }

    /**
     * Check if this is an external link (e.g., Google Drive)
     */
    public boolean isExternalLink() {
        return "LINK".equals(attachmentType);
    }

    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }

    public boolean isDocument() {
        return contentType != null && (
            contentType.equals("application/pdf") ||
            contentType.startsWith("application/vnd.openxmlformats-officedocument") ||
            contentType.startsWith("application/msword") ||
            contentType.equals("text/plain")
        );
    }

    public boolean isVideo() {
        return contentType != null && contentType.startsWith("video/");
    }

    public boolean isAudio() {
        return contentType != null && contentType.startsWith("audio/");
    }

    /**
     * Lấy phần mở rộng của file
     */
    public String getFileExtension() {
        if (originalName != null && originalName.contains(".")) {
            return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Định dạng kích thước file để hiển thị
     */
    public String getFormattedFileSize() {
        if (sizeBytes == null) {
            return "0 B";
        }

        if (sizeBytes < 1024) {
            return sizeBytes + " B";
        } else if (sizeBytes < 1024 * 1024) {
            return String.format("%.1f KB", sizeBytes / 1024.0);
        } else if (sizeBytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", sizeBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", sizeBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * Lấy icon CSS class dựa trên loại file
     */
    public String getFileIcon() {
        String extension = getFileExtension();

        switch (extension) {
            case "pdf":
                return "fas fa-file-pdf text-danger";
            case "doc":
            case "docx":
                return "fas fa-file-word text-primary";
            case "xls":
            case "xlsx":
                return "fas fa-file-excel text-success";
            case "ppt":
            case "pptx":
                return "fas fa-file-powerpoint text-warning";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return "fas fa-file-image text-info";
            case "mp4":
            case "avi":
            case "mov":
            case "wmv":
                return "fas fa-file-video text-purple";
            case "mp3":
            case "wav":
            case "wma":
                return "fas fa-file-audio text-info";
            case "zip":
            case "rar":
            case "7z":
                return "fas fa-file-archive text-warning";
            case "txt":
                return "fas fa-file-alt text-secondary";
            default:
                return "fas fa-file text-secondary";
        }
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", ownerType='" + ownerType + '\'' +
                ", ownerId=" + ownerId +
                ", originalName='" + originalName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", sizeBytes=" + sizeBytes +
                '}';
    }
}