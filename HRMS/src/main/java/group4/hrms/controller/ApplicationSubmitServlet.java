package group4.hrms.controller;

import group4.hrms.dao.ApplicationDao;
import group4.hrms.email.service.EmailService;
import group4.hrms.model.Application;
import group4.hrms.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet xử lý submit application từ job application modal
 * 
 * @author Group4
 */
@WebServlet("/submit-application")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 5, // 5MB
        maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class ApplicationSubmitServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ApplicationSubmitServlet.class.getName());
    private ApplicationDao applicationDao;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.applicationDao = new ApplicationDao();
        this.emailService = new EmailService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            logger.info("Processing application submission request");

            // Parse form data
            Application application = parseApplicationFromRequest(request);
            logger.info("Parsed application: JobID=" + application.getJobId() +
                    ", Email=" + application.getEmail() +
                    ", FullName=" + application.getFullName());

            // Check if application already exists for this job and email
            Optional<Application> existingApp = applicationDao.findByJobAndEmail(
                    application.getJobId(), application.getEmail());

            if (existingApp.isPresent()) {
                logger.warning("Duplicate application attempt: JobID=" + application.getJobId() +
                        ", Email=" + application.getEmail());
                sendErrorResponse(response, "You have already applied for this position with this email address. " +
                        "Each email can only submit one application per job.");
                return;
            }

            // Handle file uploads
            handleFileUploads(request, application);

            // Save application to database
            Application savedApp = applicationDao.save(application);

            if (savedApp != null && savedApp.getId() != null) {
                // Send confirmation emails asynchronously
                try {
                    emailService.sendApplicationConfirmation(savedApp);
                    emailService.sendCvNotification(savedApp);
                    logger.info("Emails queued successfully for application: " + savedApp.getId());
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error queueing emails for application " + savedApp.getId(), e);
                    // Don't fail the request if email queueing fails
                }

                sendSuccessResponse(response, savedApp.getId());
                logger.info("Application submitted successfully: ID=" + savedApp.getId() +
                        ", Job=" + application.getJobId() + ", Email=" + application.getEmail());
            } else {
                sendErrorResponse(response, "Failed to save application. Please try again.");
            }

        } catch (ServletException e) {
            logger.log(Level.WARNING, "Validation error while processing application: " + e.getMessage(), e);
            sendErrorResponse(response, e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while saving application", e);
            sendErrorResponse(response, "Database error occurred. Please try again later.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error while processing application", e);
            sendErrorResponse(response, "An unexpected error occurred: " + e.getMessage());
        }
    }

    private Application parseApplicationFromRequest(HttpServletRequest request) throws ServletException {
        Application app = new Application();

        // Job ID (required)
        String jobIdStr = request.getParameter("jobId");
        if (jobIdStr == null || jobIdStr.trim().isEmpty()) {
            throw new ServletException("Job ID is required");
        }

        try {
            app.setJobId(Long.parseLong(jobIdStr));
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid job ID format");
        }

        // Contact information (required)
        app.setEmail(getRequiredParameter(request, "email", "Email is required"));
        app.setPhone(getRequiredParameter(request, "phone", "Phone number is required"));

        // Personal info from CCCD OCR (required)
        app.setFullName(getRequiredParameter(request, "cccdName", "Full name from CCCD is required"));
        app.setGender(getRequiredParameter(request, "cccdGender", "Gender from CCCD is required"));
        app.setHometown(getRequiredParameter(request, "cccdHometown", "Hometown from CCCD is required"));

        // Date of birth from CCCD (required)
        String dobStr = getRequiredParameter(request, "cccdDob", "Date of birth from CCCD is required");
        try {
            // Parse Vietnamese date format DD/MM/YYYY
            if (dobStr.contains("/")) {
                String[] parts = dobStr.split("/");
                if (parts.length == 3) {
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);
                    app.setDob(LocalDate.of(year, month, day));
                }
            } else {
                // Try ISO format as fallback
                app.setDob(LocalDate.parse(dobStr));
            }
        } catch (Exception e) {
            logger.warning("Invalid date of birth format: " + dobStr);
            throw new ServletException("Invalid date of birth format. Expected DD/MM/YYYY");
        }

        // Address information
        app.setAddressLine1(request.getParameter("addressLine1"));
        app.setAddressLine2(request.getParameter("addressLine2"));
        app.setCity(request.getParameter("city"));
        app.setState(request.getParameter("state"));
        app.setPostalCode(request.getParameter("postalCode"));
        app.setCountry(request.getParameter("country"));

        // Resume path (from URL if provided)
        String resumeUrl = request.getParameter("resumeUrl");
        if (resumeUrl != null && !resumeUrl.trim().isEmpty()) {
            app.setResumePath(resumeUrl);
        }

        // CCCD information (required)
        app.setCccd(getRequiredParameter(request, "cccd", "CCCD number is required"));
        app.setCccdIssuedPlace(request.getParameter("cccdIssuedPlace"));

        String cccdIssuedDateStr = request.getParameter("cccdIssuedDate");
        if (cccdIssuedDateStr != null && !cccdIssuedDateStr.trim().isEmpty()) {
            try {
                app.setCccdIssuedDate(LocalDate.parse(cccdIssuedDateStr));
            } catch (DateTimeParseException e) {
                logger.warning("Invalid CCCD issued date format: " + cccdIssuedDateStr);
            }
        }

        // All CCCD information is now required, so no additional validation needed

        return app;
    }

    private String getRequiredParameter(HttpServletRequest request, String paramName, String errorMessage)
            throws ServletException {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            throw new ServletException(errorMessage);
        }
        return value.trim();
    }

    private void handleFileUploads(HttpServletRequest request, Application application)
            throws IOException, ServletException {

        // Handle resume file upload
        Part resumeFilePart = request.getPart("resumeFile");
        if (resumeFilePart != null && resumeFilePart.getSize() > 0) {
            String resumePath = FileUploadUtil.saveUploadedFile(resumeFilePart, "resumes");
            if (resumePath != null) {
                application.setResumePath(resumePath);
            }
        }

        // Handle CCCD front image
        Part cccdFrontPart = request.getPart("cccdFront");
        if (cccdFrontPart != null && cccdFrontPart.getSize() > 0) {
            String cccdFrontPath = FileUploadUtil.saveUploadedFile(cccdFrontPart, "cccd");
            if (cccdFrontPath != null) {
                application.setCccdFrontPath(cccdFrontPath);
            }
        }

        // Handle CCCD back image
        Part cccdBackPart = request.getPart("cccdBack");
        if (cccdBackPart != null && cccdBackPart.getSize() > 0) {
            String cccdBackPath = FileUploadUtil.saveUploadedFile(cccdBackPart, "cccd");
            if (cccdBackPath != null) {
                application.setCccdBackPath(cccdBackPath);
            }
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, Long applicationId) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(String.format(
                "{\"success\": true, \"message\": \"Application submitted successfully!\", \"applicationId\": %d}",
                applicationId));
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(String.format(
                "{\"success\": false, \"message\": \"%s\"}",
                message.replace("\"", "\\\"")));
    }
}