package group4.hrms.util;

import jakarta.servlet.http.Part;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Utility class để xử lý file uploads
 * 
 * @author Group4
 */
public class FileUploadUtil {
    
    private static final Logger logger = Logger.getLogger(FileUploadUtil.class.getName());
    
    // Base upload directory (relative to webapp root)
    private static final String UPLOAD_BASE_DIR = "uploads";
    
    // Maximum file size (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    // Allowed file extensions
    private static final String[] ALLOWED_RESUME_EXTENSIONS = {".pdf", ".doc", ".docx"};
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    
    /**
     * Save uploaded file to server
     * 
     * @param filePart The uploaded file part
     * @param subDirectory Sub-directory under uploads (e.g., "resumes", "cccd")
     * @return Relative path to saved file, or null if failed
     */
    public static String saveUploadedFile(Part filePart, String subDirectory) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        
        // Validate file size
        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds maximum allowed size of 5MB");
        }
        
        // Get original filename
        String originalFilename = getOriginalFilename(filePart);
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IOException("Invalid filename");
        }
        
        // Validate file extension
        String fileExtension = getFileExtension(originalFilename);
        if (!isValidFileExtension(fileExtension, subDirectory)) {
            throw new IOException("Invalid file type. Allowed types: " + getAllowedExtensions(subDirectory));
        }
        
        // Create upload directory structure
        Path uploadDir = createUploadDirectory(subDirectory);
        
        // Generate unique filename
        String uniqueFilename = generateUniqueFilename(originalFilename);
        Path filePath = uploadDir.resolve(uniqueFilename);
        
        // Save file
        try {
            Files.copy(filePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return relative path from webapp root
            String relativePath = UPLOAD_BASE_DIR + "/" + subDirectory + "/" + uniqueFilename;
            logger.info("File saved successfully: " + relativePath);
            return relativePath;
            
        } catch (IOException e) {
            logger.severe("Failed to save file: " + e.getMessage());
            throw new IOException("Failed to save file: " + e.getMessage());
        }
    }
    
    /**
     * Get original filename from Part
     */
    private static String getOriginalFilename(Part part) {
        String contentDisposition = part.getHeader("Content-Disposition");
        if (contentDisposition == null) {
            return null;
        }
        
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                String filename = token.substring(token.indexOf('=') + 1).trim();
                return filename.replace("\"", "");
            }
        }
        return null;
    }
    
    /**
     * Get file extension from filename
     */
    private static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex).toLowerCase();
    }
    
    /**
     * Check if file extension is valid for the given subdirectory
     */
    private static boolean isValidFileExtension(String extension, String subDirectory) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        
        String[] allowedExtensions;
        if ("resumes".equals(subDirectory)) {
            allowedExtensions = ALLOWED_RESUME_EXTENSIONS;
        } else if ("cccd".equals(subDirectory)) {
            allowedExtensions = ALLOWED_IMAGE_EXTENSIONS;
        } else {
            return false;
        }
        
        for (String allowed : allowedExtensions) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get allowed extensions string for error messages
     */
    private static String getAllowedExtensions(String subDirectory) {
        if ("resumes".equals(subDirectory)) {
            return String.join(", ", ALLOWED_RESUME_EXTENSIONS);
        } else if ("cccd".equals(subDirectory)) {
            return String.join(", ", ALLOWED_IMAGE_EXTENSIONS);
        }
        return "";
    }
    
    /**
     * Create upload directory if it doesn't exist
     */
    private static Path createUploadDirectory(String subDirectory) throws IOException {
        // Try to get webapp root from system property first
        String webappRoot = System.getProperty("webapp.root");
        
        if (webappRoot == null) {
            // Fallback to catalina.base
            String catalinaBase = System.getProperty("catalina.base");
            if (catalinaBase != null) {
                webappRoot = catalinaBase + File.separator + "webapps" + File.separator + "HRMS";
            } else {
                // Final fallback to current directory
                webappRoot = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "webapp";
            }
        }
        
        Path uploadPath = Paths.get(webappRoot, UPLOAD_BASE_DIR, subDirectory);
        
        // Create directory if it doesn't exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("Created upload directory: " + uploadPath);
        }
        
        return uploadPath;
    }
    
    /**
     * Generate unique filename to avoid conflicts
     */
    private static String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        
        // Clean base name (remove special characters)
        baseName = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // Generate timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        // Generate short UUID
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("%s_%s_%s%s", baseName, timestamp, uuid, extension);
    }
    
    /**
     * Delete uploaded file
     */
    public static boolean deleteFile(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            String webappRoot = getWebappRoot();
            Path filePath = Paths.get(webappRoot, relativePath);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("File deleted successfully: " + relativePath);
                return true;
            }
            
        } catch (IOException e) {
            logger.severe("Failed to delete file " + relativePath + ": " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get file size in bytes
     */
    public static long getFileSize(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return 0;
        }
        
        try {
            String webappRoot = getWebappRoot();
            Path filePath = Paths.get(webappRoot, relativePath);
            
            if (Files.exists(filePath)) {
                return Files.size(filePath);
            }
            
        } catch (IOException e) {
            logger.severe("Failed to get file size " + relativePath + ": " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Check if file exists
     */
    public static boolean fileExists(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            String webappRoot = getWebappRoot();
            Path filePath = Paths.get(webappRoot, relativePath);
            return Files.exists(filePath);
            
        } catch (Exception e) {
            logger.severe("Failed to check file existence " + relativePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get webapp root directory
     */
    private static String getWebappRoot() {
        String webappRoot = System.getProperty("webapp.root");
        
        if (webappRoot == null) {
            String catalinaBase = System.getProperty("catalina.base");
            if (catalinaBase != null) {
                webappRoot = catalinaBase + File.separator + "webapps" + File.separator + "HRMS";
            } else {
                webappRoot = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "webapp";
            }
        }
        
        return webappRoot;
    }
    
    // ========== OCR-specific methods ==========
    
    /**
     * Validates if the uploaded file is valid for OCR processing
     * 
     * @param filePart The uploaded file part
     * @return true if valid, false otherwise
     */
    public static boolean isValidFile(Part filePart) {
        if (filePart == null || filePart.getSize() == 0) {
            return false;
        }

        // Check file size (10MB limit for OCR)
        if (filePart.getSize() > 10 * 1024 * 1024) {
            return false;
        }

        // Check content type
        String contentType = filePart.getContentType();
        if (contentType == null) {
            return false;
        }

        String[] allowedTypes = {"image/jpeg", "image/jpg", "image/png"};
        for (String allowedType : allowedTypes) {
            if (contentType.toLowerCase().contains(allowedType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Converts uploaded file to Base64 encoded string for OCR processing
     * 
     * @param filePart The uploaded file part
     * @return Base64 encoded string of the file
     * @throws IOException if file reading fails
     */
    public static String convertToBase64(Part filePart) throws IOException {
        if (filePart == null) {
            throw new IllegalArgumentException("File part cannot be null");
        }

        try (InputStream inputStream = filePart.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] fileBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(fileBytes);
        }
    }

    /**
     * Gets MIME type based on file extension for OCR processing
     * 
     * @param filename The filename
     * @return MIME type string
     */
    public static String getMimeTypeForOCR(String filename) {
        String extension = getFileExtension(filename);

        switch (extension) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }
}