package group4.hrms.controller;

import group4.hrms.model.CitizenIDCard;
import group4.hrms.service.OCRService;
import group4.hrms.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Servlet for handling OCR requests for Vietnamese Citizen ID cards
 */
@WebServlet("/api/ocr")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10,  // 10MB
    maxRequestSize = 1024 * 1024 * 15 // 15MB
)
public class OCRServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(OCRServlet.class.getName());
    private OCRService ocrService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ocrService = new OCRService();
        LOGGER.info("OCR Servlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Enable CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            // Get uploaded file
            Part filePart = request.getPart("file");
            
            if (filePart == null || filePart.getSize() == 0) {
                sendErrorResponse(response, "No file uploaded", 400);
                return;
            }

            // Validate file
            if (!FileUploadUtil.isValidFile(filePart)) {
                sendErrorResponse(response, "Invalid file type or size. Please upload a valid image file (JPG, PNG) under 10MB.", 400);
                return;
            }

            LOGGER.log(Level.INFO, "Processing file: {0}, Size: {1} bytes, Type: {2}", 
                new Object[]{filePart.getSubmittedFileName(), filePart.getSize(), filePart.getContentType()});

            // Convert to base64
            String base64Image = FileUploadUtil.convertToBase64(filePart);
            String mimeType = filePart.getContentType();

            // Process with OCR
            CitizenIDCard result = ocrService.processImage(base64Image, mimeType);

            // Validate that expiry date was extracted
            if (!result.hasExpiryDate()) {
                sendErrorResponse(response, "Unable to read expiry date from CCCD. Please ensure the image is clear and shows the complete front side of the Citizen ID card.", 400);
                return;
            }

            // Check if CCCD is expired
            if (result.isExpired()) {
                sendErrorResponse(response, "This CCCD has expired on " + result.getNgayHetHan() + ". Please renew your Citizen ID card before applying.", 400);
                return;
            }

            // Send response
            sendSuccessResponse(response, result);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing OCR request", e);
            sendErrorResponse(response, "Error processing image: " + e.getMessage(), 500);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle CORS preflight
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Sends a success response with OCR results
     */
    private void sendSuccessResponse(HttpServletResponse response, CitizenIDCard result) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        
        try (PrintWriter out = response.getWriter()) {
            out.print(result.toJson());
            out.flush();
        }
    }

    /**
     * Sends an error response
     */
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"error\": true, \"message\": \"" + message + "\", \"statusCode\": " + statusCode + "}");
            out.flush();
        }
    }
}