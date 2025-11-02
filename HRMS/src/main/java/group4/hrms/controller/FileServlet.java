package group4.hrms.controller;

import group4.hrms.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Servlet để serve uploaded files
 * 
 * @author Group4
 */
@WebServlet("/uploads/*")
public class FileServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(FileServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Remove leading slash
        String relativePath = "uploads" + pathInfo;
        
        // Security check - prevent directory traversal
        if (relativePath.contains("..") || relativePath.contains("//")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Check if file exists
        if (!FileUploadUtil.fileExists(relativePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        try {
            // Get webapp root
            String webappRoot = getServletContext().getRealPath("/");
            Path filePath = Paths.get(webappRoot, relativePath);
            
            // Set content type
            String contentType = getServletContext().getMimeType(filePath.toString());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            response.setContentType(contentType);
            
            // Set content length
            long fileSize = Files.size(filePath);
            response.setContentLengthLong(fileSize);
            
            // Set cache headers
            response.setHeader("Cache-Control", "private, max-age=3600");
            
            // Copy file to response
            Files.copy(filePath, response.getOutputStream());
            
        } catch (IOException e) {
            logger.severe("Error serving file " + relativePath + ": " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}