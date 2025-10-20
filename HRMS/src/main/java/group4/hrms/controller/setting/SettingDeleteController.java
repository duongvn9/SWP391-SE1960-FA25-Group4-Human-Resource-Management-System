package group4.hrms.controller.setting;

import group4.hrms.dao.SettingDao;
import group4.hrms.model.Setting;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for deleting Settings
 * Use Case: Delete Setting
 * 
 * Flow:
 * 1. Admin clicks Delete button on Setting List
 * 2. System displays confirmation dialog (handled by frontend)
 * 3. Admin confirms deletion
 * 4. System validates session and authorization
 * 5. System checks if setting exists
 * 6. System checks if setting is in use (has dependencies)
 * 7. System deletes setting from appropriate table
 * 8. System displays success message and refreshes list
 * 
 * Error Scenarios:
 * E1: Setting is in use (has dependencies) - Cannot delete
 * E2: Database connection error
 * E3: Session timeout
 * E4: Unauthorized access (non-admin)
 * E5: Foreign key constraint violation
 * E6: Concurrent deletion
 * 
 * Alternative Flows:
 * A1: Cancel deletion (handled by frontend)
 * A2: Setting not found
 */
@WebServlet("/settings/delete")
public class SettingDeleteController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingDeleteController.class);
    private final SettingDao settingDao = new SettingDao();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // E3: Check session timeout
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("Session expired during delete attempt");
            response.sendRedirect(request.getContextPath() + "/login?message=Session expired. Please login again");
            return;
        }
        
        // E4: Check authorization (Admin only)
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !userRole.equalsIgnoreCase("ADMIN")) {
            logger.warn("Unauthorized delete attempt by role: {}", userRole);
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        // Step 4: Get parameters
        String idParam = request.getParameter("id");
        String type = request.getParameter("type");
        
        // Validate required parameters
        if (idParam == null || idParam.trim().isEmpty() || type == null || type.trim().isEmpty()) {
            logger.warn("Missing required parameters: id={}, type={}", idParam, type);
            response.sendRedirect(request.getContextPath() + "/settings?error=Missing required parameters");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            
            // Step 5: Check if setting exists
            Optional<Setting> settingOpt = settingDao.findById(id, type);
            if (settingOpt.isEmpty()) {
                // A2: Setting not found (E6: might be concurrent deletion)
                logger.warn("Setting not found for deletion: id={}, type={}", id, type);
                response.sendRedirect(request.getContextPath() + "/settings?error=Setting not found or already deleted");
                return;
            }
            
            Setting setting = settingOpt.get();
            String settingName = setting.getName();
            
            // Step 6: Check if setting is in use (has dependencies)
            // E1: Setting is in use - Cannot delete
            try {
                int usageCount = settingDao.countUsage(id, type);
                if (usageCount > 0) {
                    logger.warn("Cannot delete setting in use: id={}, type={}, name={}, usageCount={}", 
                               id, type, settingName, usageCount);
                    String errorMsg = String.format(
                        "Cannot delete setting '%s' because it is currently in use by %d user(s)", 
                        settingName, usageCount);
                    response.sendRedirect(request.getContextPath() + "/settings?error=" + 
                        java.net.URLEncoder.encode(errorMsg, "UTF-8"));
                    return;
                }
            } catch (Exception e) {
                // If countUsage fails, log but continue (better to fail safe)
                logger.error("Error checking usage for setting id={}, type={}", id, type, e);
            }
            
            // Step 7: Delete setting
            boolean deleted = settingDao.delete(id, type);
            
            if (deleted) {
                // Step 8: Success - redirect with success message
                logger.info("Setting deleted successfully: id={}, type={}, name={}", id, type, settingName);
                response.sendRedirect(request.getContextPath() + "/settings?success=Setting deleted successfully");
            } else {
                // Unexpected failure (setting might have been deleted concurrently)
                logger.warn("Failed to delete setting (might be concurrent deletion): id={}, type={}", id, type);
                response.sendRedirect(request.getContextPath() + "/settings?error=Failed to delete setting");
            }
            
        } catch (NumberFormatException e) {
            logger.warn("Invalid ID format: {}", idParam);
            response.sendRedirect(request.getContextPath() + "/settings?error=Invalid ID format");
        } catch (java.sql.SQLException e) {
            // E2: Database connection error
            logger.error("Database error while deleting setting: id={}, type={}", idParam, type, e);
            response.sendRedirect(request.getContextPath() + "/settings?error=Database error. Please try again later");
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // E5: Foreign key constraint violation
            logger.error("Foreign key constraint violation while deleting setting: id={}, type={}", idParam, type, e);
            response.sendRedirect(request.getContextPath() + "/settings?error=" + 
                java.net.URLEncoder.encode("Cannot delete setting because it is referenced by other records", "UTF-8"));
        } catch (RuntimeException e) {
            // Handle specific runtime exceptions (e.g., setting in use from DAO)
            logger.error("Runtime error deleting setting: id={}, type={}", idParam, type, e);
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("employees") || 
                errorMessage != null && errorMessage.contains("users")) {
                // E1: Setting in use error from DAO
                response.sendRedirect(request.getContextPath() + "/settings?error=" + 
                    java.net.URLEncoder.encode(errorMessage, "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/settings?error=An error occurred while deleting");
            }
        } catch (Exception e) {
            // Catch-all for unexpected errors
            logger.error("Unexpected error deleting setting: id={}, type={}", idParam, type, e);
            response.sendRedirect(request.getContextPath() + "/settings?error=An unexpected error occurred");
        }
    }
}
