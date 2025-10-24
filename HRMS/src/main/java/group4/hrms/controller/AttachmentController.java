package group4.hrms.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.dao.AttachmentDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.model.Attachment;
import group4.hrms.model.Request;
import group4.hrms.service.AttachmentService;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller for serving attachment files
 * Handles download and preview of attachments with access control
 *
 * URL patterns:
 * - GET /attachments/{id}/download - Download file with Content-Disposition: attachment
 * - GET /attachments/{id}/view - View file inline for preview
 *
 * @author Group4
 */
@WebServlet("/attachments/*")
public class AttachmentController extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    private AttachmentDao attachmentDao;
    private RequestDao requestDao;
    private AttachmentService attachmentService;
    private String uploadBasePath;

    @Override
    public void init() throws ServletException {
        super.init();
        this.attachmentDao = new AttachmentDao();
        this.requestDao = new RequestDao();
        this.attachmentService = new AttachmentService(attachmentDao);

        // Set upload base path to match LeaveRequestController and OTRequestController
        // Always use absolute path to assets/img/Request directory inside webapp
        this.uploadBasePath = getServletContext().getRealPath("/assets/img/Request");

        if (uploadBasePath == null) {
            // Fallback: if realPath returns null (e.g., running from JAR/WAR without extraction)
            // use temp directory as last resort
            uploadBasePath = System.getProperty("java.io.tmpdir") + File.separator + "hrms-uploads";
            logger.warn("Cannot resolve /assets/img/Request path, using temp directory: {}", uploadBasePath);
        }

        logger.info("AttachmentController initialized with upload base path: {}", uploadBasePath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Attachment ID is required");
            return;
        }

        // Parse path: /{id}/download or /{id}/view
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid attachment URL");
            return;
        }

        // Extract attachment ID
        Long attachmentId;
        try {
            attachmentId = Long.parseLong(pathParts[1]);
        } catch (NumberFormatException e) {
            logger.warn("Invalid attachment ID format: {}", pathParts[1]);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid attachment ID");
            return;
        }

        // Determine action: download or view
        boolean isDownload = pathParts.length > 2 && "download".equals(pathParts[2]);
        boolean isView = pathParts.length > 2 && "view".equals(pathParts[2]);

        if (!isDownload && !isView) {
            // Default to download if no action specified
            isDownload = true;
        }

        try {
            if (isDownload) {
                handleDownload(request, response, attachmentId);
            } else {
                handleView(request, response, attachmentId);
            }
        } catch (Exception e) {
            logger.error("Error serving attachment {}: {}", attachmentId, e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error serving attachment");
        }
    }

    /**
     * Handle file download with Content-Disposition: attachment
     */
    private void handleDownload(HttpServletRequest request, HttpServletResponse response,
                               Long attachmentId) throws IOException, java.sql.SQLException {

        logger.debug("Handling download request for attachment: {}", attachmentId);

        // Get attachment metadata
        Optional<Attachment> attachmentOpt = attachmentDao.findById(attachmentId);
        if (!attachmentOpt.isPresent()) {
            logger.warn("Attachment not found: {}", attachmentId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Attachment not found");
            return;
        }

        Attachment attachment = attachmentOpt.get();

        // Verify access control
        if (!canAccessAttachment(request, attachment)) {
            logger.warn("Access denied to attachment {} for user {}",
                attachmentId, SessionUtil.getCurrentAccountId(request));
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "You do not have permission to access this attachment");
            return;
        }

        // Get file from filesystem
        File file;
        try {
            file = attachmentService.getFile(attachment.getPath(), uploadBasePath);
        } catch (FileNotFoundException e) {
            logger.error("File not found on filesystem: {}", attachment.getPath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                "File not found on server");
            return;
        } catch (SecurityException e) {
            logger.error("Security violation accessing file: {}", attachment.getPath());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        // Set response headers for download
        response.setContentType(attachment.getContentType());
        response.setContentLengthLong(attachment.getSizeBytes());
        response.setHeader("Content-Disposition",
            "attachment; filename=\"" + sanitizeFilename(attachment.getOriginalName()) + "\"");

        // Stream file to response
        serveFile(file, response);

        logger.info("Successfully served attachment {} (download) to user {}",
            attachmentId, SessionUtil.getCurrentAccountId(request));
    }

    /**
     * Handle file view with Content-Disposition: inline
     */
    private void handleView(HttpServletRequest request, HttpServletResponse response,
                           Long attachmentId) throws IOException, java.sql.SQLException {

        logger.debug("Handling view request for attachment: {}", attachmentId);

        // Get attachment metadata
        Optional<Attachment> attachmentOpt = attachmentDao.findById(attachmentId);
        if (!attachmentOpt.isPresent()) {
            logger.warn("Attachment not found: {}", attachmentId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Attachment not found");
            return;
        }

        Attachment attachment = attachmentOpt.get();

        // Verify access control
        if (!canAccessAttachment(request, attachment)) {
            logger.warn("Access denied to attachment {} for user {}",
                attachmentId, SessionUtil.getCurrentAccountId(request));
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "You do not have permission to access this attachment");
            return;
        }

        // Get file from filesystem
        File file;
        try {
            file = attachmentService.getFile(attachment.getPath(), uploadBasePath);
        } catch (FileNotFoundException e) {
            logger.error("File not found on filesystem: {}", attachment.getPath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                "File not found on server");
            return;
        } catch (SecurityException e) {
            logger.error("Security violation accessing file: {}", attachment.getPath());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        // Set response headers for inline viewing
        response.setContentType(attachment.getContentType());
        response.setContentLengthLong(attachment.getSizeBytes());
        response.setHeader("Content-Disposition",
            "inline; filename=\"" + sanitizeFilename(attachment.getOriginalName()) + "\"");

        // Stream file to response
        serveFile(file, response);

        logger.info("Successfully served attachment {} (view) to user {}",
            attachmentId, SessionUtil.getCurrentAccountId(request));
    }

    /**
     * Check if current user can access the attachment
     * User can access if:
     * - They are the requester (created_by_account_id)
     * - They are the current approver (current_approver_account_id)
     * - They have ADMIN or HR role
     */
    private boolean canAccessAttachment(HttpServletRequest request, Attachment attachment) {
        Long currentAccountId = SessionUtil.getCurrentAccountId(request);

        if (currentAccountId == null) {
            logger.warn("No account ID in session");
            return false;
        }

        // Check if owner type is REQUEST
        if (!"REQUEST".equals(attachment.getOwnerType())) {
            logger.warn("Unsupported owner type: {}", attachment.getOwnerType());
            return false;
        }

        // Get the associated request
        Optional<Request> requestOpt = requestDao.findById(attachment.getOwnerId());
        if (!requestOpt.isPresent()) {
            logger.warn("Request not found for attachment: requestId={}", attachment.getOwnerId());
            return false;
        }

        Request req = requestOpt.get();

        // Check if user is the requester
        if (currentAccountId.equals(req.getCreatedByAccountId())) {
            logger.debug("Access granted: user is the requester");
            return true;
        }

        // Check if user is the current approver
        if (req.getCurrentApproverAccountId() != null &&
            currentAccountId.equals(req.getCurrentApproverAccountId())) {
            logger.debug("Access granted: user is the current approver");
            return true;
        }

        // Check if user has ADMIN or HR role
        if (SessionUtil.hasAnyRole(request, "ADMIN", "HR")) {
            logger.debug("Access granted: user has ADMIN or HR role");
            return true;
        }

        logger.debug("Access denied: user {} does not have permission for request {}",
            currentAccountId, req.getId());
        return false;
    }

    /**
     * Stream file content to response output stream
     */
    private void serveFile(File file, HttpServletResponse response) throws IOException {
        try (InputStream input = new FileInputStream(file);
             OutputStream output = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            output.flush();
        }
    }

    /**
     * Sanitize filename for Content-Disposition header
     * Removes or escapes characters that could cause issues
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "download";
        }

        // Remove or replace problematic characters
        filename = filename.replaceAll("[\\\\/:*?\"<>|]", "_");

        // Remove any control characters
        filename = filename.replaceAll("[\\x00-\\x1F\\x7F]", "");

        return filename;
    }
}
