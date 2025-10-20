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
import java.util.List;
import java.util.Optional;

/**
 * Controller for editing Settings
 * Use Case: Edit Setting
 * 
 * Flow:
 * 1. Admin clicks Edit button on Setting List
 * 2. System validates session and authorization
 * 3. System retrieves setting data by ID and Type
 * 4. System displays Edit Form pre-filled with data
 * 5. Admin modifies data and submits
 * 6. System validates input (required fields, duplicate name, priority format)
 * 7. System updates setting and timestamp
 * 8. System redirects to list with success message
 * 
 * Error Scenarios:
 * E1: Validation Error (missing required fields, duplicate name, invalid priority)
 * E2: Database connection error
 * E3: Session timeout
 * E4: Unauthorized access (non-admin)
 * E7: Invalid priority format
 * 
 * Alternative Flows:
 * A2: Duplicate name check (if name changed)
 * A4: Setting not found
 */
@WebServlet("/settings/edit")
public class SettingEditController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingEditController.class);
    private final SettingDao settingDao = new SettingDao();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // E3: Check session timeout
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("Session expired");
            response.sendRedirect(request.getContextPath() + "/login?message=Session expired. Please login again");
            return;
        }
        
        // E4: Check authorization (Admin only)
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !userRole.equalsIgnoreCase("ADMIN")) {
            logger.warn("Unauthorized access attempt by role: {}", userRole);
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        String idParam = request.getParameter("id");
        String type = request.getParameter("type");
        
        if (idParam == null || idParam.trim().isEmpty() || type == null || type.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/settings?error=Missing parameters");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            
            // Step 2: Retrieve setting data by ID and Type
            Optional<Setting> settingOpt = settingDao.findById(id, type);
            
            if (settingOpt.isEmpty()) {
                // A4: Setting Not Found
                logger.warn("Setting not found: id={}, type={}", id, type);
                response.sendRedirect(request.getContextPath() + "/settings?error=Setting not found");
                return;
            }
            
            // Step 3: Display Edit Form pre-filled with data
            request.setAttribute("setting", settingOpt.get());
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            logger.warn("Invalid ID format: {}", idParam);
            response.sendRedirect(request.getContextPath() + "/settings?error=Invalid ID");
        } catch (Exception e) {
            // E2: Database connection error
            logger.error("Error loading Setting for edit", e);
            response.sendRedirect(request.getContextPath() + "/settings?error=Database error. Please try again later");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // E3: Check session timeout
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("Session expired");
            response.sendRedirect(request.getContextPath() + "/login?message=Session expired. Please login again");
            return;
        }
        
        // E4: Check authorization (Admin only)
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !userRole.equalsIgnoreCase("ADMIN")) {
            logger.warn("Unauthorized access attempt by role: {}", userRole);
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        String newType = request.getParameter("type");
        String value = request.getParameter("value");
        String priorityStr = request.getParameter("priority");
        String oldType = request.getParameter("oldType");
        
        try {
            Long id = Long.parseLong(idParam);
            
            // E1: Validate required fields
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Setting name is required");
                Optional<Setting> settingOpt = settingDao.findById(id, oldType != null ? oldType : newType);
                if (settingOpt.isPresent()) {
                    request.setAttribute("setting", settingOpt.get());
                }
                request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                return;
            }
            
            if (newType == null || newType.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/settings?error=Setting type is required");
                return;
            }
            
            // Validate type value
            if (!newType.equals("Department") && !newType.equals("Position") && !newType.equals("Role")) {
                response.sendRedirect(request.getContextPath() + "/settings?error=Invalid setting type");
                return;
            }
            
            // E7: Validate priority if provided
            Integer priority = null;
            if (priorityStr != null && !priorityStr.trim().isEmpty()) {
                try {
                    priority = Integer.parseInt(priorityStr.trim());
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "Priority must be a number");
                    Optional<Setting> settingOpt = settingDao.findById(id, oldType != null ? oldType : newType);
                    if (settingOpt.isPresent()) {
                        request.setAttribute("setting", settingOpt.get());
                    }
                    request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                    return;
                }
            }
            
            // Get current setting using oldType from hidden field
            String typeToFind = oldType != null ? oldType : newType;
            Optional<Setting> currentSettingOpt = settingDao.findById(id, typeToFind);
            if (currentSettingOpt.isEmpty()) {
                // A4: Setting not found
                response.sendRedirect(request.getContextPath() + "/settings?error=Setting not found");
                return;
            }
            
            Setting currentSetting = currentSettingOpt.get();
            
            // Sanitize input
            name = name.trim();
            newType = newType.trim();
            if (value != null) {
                value = value.trim();
            }
            
            // A2: Check duplicate name (if name changed)
            if (!currentSetting.getName().equals(name)) {
                // Name changed, check for duplicates in the target type
                List<Setting> existingSettings = settingDao.findByType(newType);
                for (Setting existing : existingSettings) {
                    if (existing.getId() != id && existing.getName().equalsIgnoreCase(name)) {
                        request.setAttribute("errorMessage", "Setting with this name already exists in " + newType);
                        request.setAttribute("setting", currentSetting);
                        request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                        return;
                    }
                }
            }
            
            // Prevent type change (as per requirement)
            if (!currentSetting.getType().equals(newType)) {
                logger.warn("Attempt to change setting type from {} to {}", currentSetting.getType(), newType);
                request.setAttribute("errorMessage", "Setting type cannot be changed");
                request.setAttribute("setting", currentSetting);
                request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                return;
            }
            
            // Normal update (type unchanged)
            Setting setting = new Setting();
            setting.setId(id);
            setting.setName(name);
            setting.setType(newType);
            setting.setValue(value);
            setting.setPriority(priority);
            
            settingDao.update(setting);
            
            logger.info("Successfully updated Setting ID: {}", id);
            
            // Step 7-8: Update setting and timestamp
            response.sendRedirect(request.getContextPath() + "/settings?success=Setting updated successfully");
            
        } catch (java.sql.SQLException e) {
            // E2: Database connection error
            logger.error("Database error while updating Setting", e);
            response.sendRedirect(request.getContextPath() + "/settings?error=Database error. Please try again later");
        } catch (NumberFormatException e) {
            logger.warn("Invalid ID format: {}", idParam);
            response.sendRedirect(request.getContextPath() + "/settings?error=Invalid ID");
        } catch (Exception e) {
            logger.error("Error updating Setting", e);
            response.sendRedirect(request.getContextPath() + "/settings?error=An error occurred: " + e.getMessage());
        }
    }
}
