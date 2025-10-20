package group4.hrms.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.AttachmentDao;
import group4.hrms.model.Attachment;
import jakarta.servlet.http.Part;

/**
 * Service class for handling file attachment operations
 * Provides file validation, storage, and retrieval functionality
 *
 * @author Group4
 */
public class AttachmentService {

    private static final Logger logger = Logger.getLogger(AttachmentService.class.getName());

    // File validation constants
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "application/pdf",
        "image/jpeg",
        "image/jpg",
        "image/png",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".pdf", ".jpg", ".jpeg", ".png", ".doc", ".docx"
    );

    private final AttachmentDao attachmentDao;

    public AttachmentService() {
        this.attachmentDao = new AttachmentDao();
    }

    public AttachmentService(AttachmentDao attachmentDao) {
        this.attachmentDao = attachmentDao;
    }

    /**
     * Save external link (e.g., Google Drive) to database
     *
     * @param externalUrl External URL (Google Drive link)
     * @param ownerId ID of the owner entity (e.g., request ID)
     * @param ownerType Type of owner entity (e.g., "REQUEST")
     * @param uploadedByAccountId Account ID of the uploader
     * @param originalName Optional original name/description
     * @return Saved Attachment object
     * @throws SQLException if database operations fail
     */
    public Attachment saveExternalLink(
            String externalUrl,
            Long ownerId,
            String ownerType,
            Long uploadedByAccountId,
            String originalName) throws SQLException {

        logger.info(String.format("Saving external link for owner %s:%d - URL: %s",
            ownerType, ownerId, externalUrl));

        // Validate URL
        if (externalUrl == null || externalUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("External URL cannot be empty");
        }

        // Basic Google Drive URL validation
        if (!externalUrl.contains("drive.google.com") && !externalUrl.contains("docs.google.com")) {
            throw new IllegalArgumentException("Only Google Drive links are supported");
        }

        // Create attachment metadata for external link
        Attachment attachment = new Attachment();
        attachment.setOwnerType(ownerType);
        attachment.setOwnerId(ownerId);
        attachment.setAttachmentType("LINK");
        attachment.setExternalUrl(externalUrl);
        attachment.setPath(""); // Empty string instead of null (DB constraint NOT NULL)
        attachment.setOriginalName(originalName != null ? originalName : "Google Drive Link");
        attachment.setContentType("external/link");
        attachment.setSizeBytes(0L);
        attachment.setChecksumSha256(null);
        attachment.setUploadedByAccountId(uploadedByAccountId);

        // Save to database
        Attachment saved = attachmentDao.save(attachment);

        logger.info(String.format("Successfully saved external link attachment: id=%d, url=%s",
            saved.getId(), externalUrl));

        return saved;
    }

    /**
     * Save multiple files to filesystem and database
     *
     * @param parts Collection of file parts from multipart request
     * @param ownerId ID of the owner entity (e.g., request ID)
     * @param ownerType Type of owner entity (e.g., "REQUEST")
     * @param uploadedByAccountId Account ID of the uploader
     * @param uploadBasePath Base path for file storage
     * @return List of saved Attachment objects
     * @throws IOException if file operations fail
     * @throws SQLException if database operations fail
     */
    public List<Attachment> saveFiles(
            Collection<Part> parts,
            Long ownerId,
            String ownerType,
            Long uploadedByAccountId,
            String uploadBasePath) throws IOException, SQLException {

        logger.info(String.format("Saving %d files for owner %s:%d",
            parts.size(), ownerType, ownerId));

        List<Attachment> savedAttachments = new ArrayList<>();

        for (Part part : parts) {
            try {
                // 1. Validate file
                validateFile(part);

                // 2. Get original filename
                String originalFilename = getFilename(part);
                logger.fine(String.format("Processing file: %s (size: %d bytes)",
                    originalFilename, part.getSize()));

                // 3. Generate unique file path
                String relativePath = generateFilePath(ownerType, originalFilename);
                String absolutePath = uploadBasePath + File.separator + relativePath;

                logger.info("Upload base path: " + uploadBasePath);
                logger.info("Relative path: " + relativePath);
                logger.info("Absolute path: " + absolutePath);

                // 4. Create directories if they don't exist
                File file = new File(absolutePath);
                File parentDir = file.getParentFile();
                if (!parentDir.exists()) {
                    boolean created = parentDir.mkdirs();
                    if (!created) {
                        throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
                    }
                    logger.info("Created directory: " + parentDir.getAbsolutePath());
                }

                // 5. Save file to filesystem
                try (InputStream input = part.getInputStream();
                     OutputStream output = new FileOutputStream(file)) {

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }

                logger.info("File saved successfully to: " + absolutePath);

                // 6. Calculate checksum
                String checksum = calculateChecksum(new FileInputStream(file));

                // 7. Create attachment metadata
                // Store web-accessible path in database
                String webPath = "assets/img/Request/" + relativePath.replace(File.separator, "/");

                Attachment attachment = new Attachment();
                attachment.setOwnerType(ownerType);
                attachment.setOwnerId(ownerId);
                attachment.setAttachmentType("FILE");  // Mark as file upload
                attachment.setPath(webPath);  // Store web-accessible path
                attachment.setExternalUrl(null);  // No external URL for file uploads
                attachment.setOriginalName(originalFilename);
                attachment.setContentType(part.getContentType());
                attachment.setSizeBytes(part.getSize());
                attachment.setChecksumSha256(checksum);
                attachment.setUploadedByAccountId(uploadedByAccountId);

                // 8. Save metadata to database
                Attachment saved = attachmentDao.save(attachment);
                savedAttachments.add(saved);

                logger.info(String.format("Successfully saved attachment: id=%d, file=%s, size=%d bytes",
                    saved.getId(), originalFilename, part.getSize()));

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error saving file: " + getFilename(part), e);
                // Clean up any files that were saved before the error
                for (Attachment saved : savedAttachments) {
                    try {
                        File fileToDelete = new File(uploadBasePath + File.separator + saved.getPath());
                        if (fileToDelete.exists()) {
                            fileToDelete.delete();
                        }
                        attachmentDao.deleteById(saved.getId());
                    } catch (Exception cleanupEx) {
                        logger.log(Level.WARNING, "Error during cleanup", cleanupEx);
                    }
                }
                throw e;
            }
        }

        return savedAttachments;
    }

    /**
     * Validate file size and type
     *
     * @param part File part to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateFile(Part part) {
        if (part == null || part.getSize() == 0) {
            throw new IllegalArgumentException("File is empty or null");
        }

        // Validate file size
        if (part.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                String.format("File size exceeds maximum allowed size of 5MB (actual: %.2f MB)",
                    part.getSize() / (1024.0 * 1024.0)));
        }

        // Validate content type
        String contentType = part.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                String.format("File type '%s' is not allowed. Allowed types: PDF, JPG, PNG, DOC, DOCX",
                    contentType));
        }

        // Validate file extension
        String filename = getFilename(part);
        String extension = getFileExtension(filename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                String.format("File extension '%s' is not allowed. Allowed extensions: %s",
                    extension, String.join(", ", ALLOWED_EXTENSIONS)));
        }

        // Sanitize filename to prevent path traversal
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename: contains illegal characters");
        }
    }

    /**
     * Generate unique file path with UUID
     * Format: {year}/{month}/{uuid}.{extension}
     *
     * @param ownerType Type of owner entity (not used in path, kept for compatibility)
     * @param originalFilename Original filename
     * @return Relative file path
     */
    public String generateFilePath(String ownerType, String originalFilename) {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());

        // Generate UUID for unique filename
        String uuid = UUID.randomUUID().toString();

        // Get file extension
        String extension = getFileExtension(originalFilename);

        // Build path: {year}/{month}/{uuid}{extension}
        // Base path (assets/img/Request) already provided by controller
        return year
            + File.separator + month
            + File.separator + uuid + extension;
    }

    /**
     * Calculate SHA-256 checksum of file
     *
     * @param inputStream Input stream of the file
     * @return SHA-256 checksum as hex string
     * @throws IOException if reading fails
     */
    public String calculateChecksum(InputStream inputStream) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            byte[] hashBytes = digest.digest();

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "SHA-256 algorithm not available", e);
            throw new IOException("Failed to calculate checksum", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error closing input stream", e);
                }
            }
        }
    }

    /**
     * Get file from filesystem
     *
     * @param path Relative path of the file
     * @param uploadBasePath Base path for file storage
     * @return File object
     * @throws IOException if file not found or access denied
     */
    public File getFile(String path, String uploadBasePath) throws IOException {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        // Prevent path traversal attacks
        if (path.contains("..")) {
            throw new SecurityException("Invalid file path: contains '..'");
        }

        String absolutePath = uploadBasePath + File.separator + path;
        File file = new File(absolutePath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + path);
        }

        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + path);
        }

        return file;
    }

    /**
     * Delete attachment and its file
     *
     * @param attachmentId Attachment ID
     * @param uploadBasePath Base path for file storage
     * @return true if deleted successfully
     * @throws SQLException if database operation fails
     * @throws IOException if file deletion fails
     */
    public boolean deleteAttachment(Long attachmentId, String uploadBasePath)
            throws SQLException, IOException {

        // Get attachment metadata
        Optional<Attachment> attachmentOpt = attachmentDao.findById(attachmentId);
        if (!attachmentOpt.isPresent()) {
            logger.warning("Attachment not found: " + attachmentId);
            return false;
        }

        Attachment attachment = attachmentOpt.get();

        // Delete file from filesystem
        try {
            File file = getFile(attachment.getPath(), uploadBasePath);
            boolean deleted = file.delete();
            if (!deleted) {
                logger.warning("Failed to delete file: " + attachment.getPath());
            }
        } catch (FileNotFoundException e) {
            logger.warning("File not found during deletion: " + attachment.getPath());
            // Continue to delete database record even if file doesn't exist
        }

        // Delete database record
        boolean dbDeleted = attachmentDao.deleteById(attachmentId);

        logger.info(String.format("Deleted attachment: id=%d, file=%s",
            attachmentId, attachment.getOriginalName()));

        return dbDeleted;
    }

    // Helper methods

    /**
     * Extract filename from Part header
     */
    private String getFilename(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            for (String token : contentDisposition.split(";")) {
                if (token.trim().startsWith("filename")) {
                    String filename = token.substring(token.indexOf('=') + 1).trim()
                        .replace("\"", "");
                    // Sanitize filename
                    return sanitizeFilename(filename);
                }
            }
        }
        return "unknown";
    }

    /**
     * Sanitize filename to prevent security issues
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unknown";
        }
        // Remove path separators and null bytes
        filename = filename.replaceAll("[/\\\\]", "");
        filename = filename.replace("\0", "");
        // Remove any non-printable characters
        filename = filename.replaceAll("[^\\x20-\\x7E]", "");
        return filename;
    }

    /**
     * Get file extension including the dot
     */
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".")).toLowerCase();
        }
        return "";
    }
}
