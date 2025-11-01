package group4.hrms.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import java.io.File;
import java.util.logging.Logger;

/**
 * Servlet để khởi tạo các thư mục cần thiết khi ứng dụng start up
 * 
 * @author Group4
 */
@WebServlet(name = "InitServlet", loadOnStartup = 1)
public class InitServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(InitServlet.class.getName());
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Create upload directories
        createUploadDirectories();
        
        logger.info("Application initialization completed successfully");
    }
    
    private void createUploadDirectories() {
        try {
            // Get webapp root directory
            String webappRoot = getServletContext().getRealPath("/");
            
            // Create upload directories
            String[] uploadDirs = {
                "uploads",
                "uploads/resumes", 
                "uploads/cccd",
                "uploads/temp"
            };
            
            for (String dir : uploadDirs) {
                File uploadDir = new File(webappRoot, dir);
                if (!uploadDir.exists()) {
                    boolean created = uploadDir.mkdirs();
                    if (created) {
                        logger.info("Created upload directory: " + uploadDir.getAbsolutePath());
                    } else {
                        logger.warning("Failed to create upload directory: " + uploadDir.getAbsolutePath());
                    }
                } else {
                    logger.info("Upload directory already exists: " + uploadDir.getAbsolutePath());
                }
            }
            
        } catch (Exception e) {
            logger.severe("Error creating upload directories: " + e.getMessage());
        }
    }
}