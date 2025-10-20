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


@WebServlet("/settings/edit")
public class SettingEditController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingEditController.class);
    private final SettingDao settingDao = new SettingDao();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // E3: Check session timeout
        if (request.getSession(false) == null) {
            logger.warn("Session expired");
            response.sendRedirect(request.getContextPath() + "/login?message=Session expired. Please login again");
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
        if (request.getSession(false) == null) {
            logger.warn("Session expired");
            response.sendRedirect(request.getContextPath() + "/login?message=Session expired. Please login again");
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
            
            // Prevent type change 
            if (!currentSetting.getType().equals(newType)) {
                logger.warn("Attempt to change setting type from {} to {}", currentSetting.getType(), newType);
                request.setAttribute("errorMessage", "Setting type cannot be changed");
                request.setAttribute("setting", currentSetting);
                request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                return;
            }
            
            // A3: Check if no changes were made
            boolean hasChanges = false;
            
            if (!currentSetting.getName().equals(name)) {
                hasChanges = true;
            }
            
            // Check value changes (handle null cases)
            String currentValue = currentSetting.getValue();
            if ((currentValue == null && value != null && !value.isEmpty()) ||
                (currentValue != null && !currentValue.equals(value))) {
                hasChanges = true;
            }
            
            // Check priority changes (handle null cases)
            Integer currentPriority = currentSetting.getPriority();
            if ((currentPriority == null && priority != null) ||
                (currentPriority != null && !currentPriority.equals(priority))) {
                hasChanges = true;
            }
            
            // A3: No changes made
            if (!hasChanges) {
                logger.info("No changes detected for Setting ID: {}", id);
                response.sendRedirect(request.getContextPath() + "/settings?info=" + 
                    java.net.URLEncoder.encode("No changes have been made", "UTF-8"));
                return;
            }
            
            // Normal update (type unchanged, has changes)
            Setting setting = new Setting();
            setting.setId(id);
            setting.setName(name);
            setting.setType(newType);
            setting.setValue(value);
            setting.setPriority(priority);
            
            settingDao.update(setting);
            
            logger.info("Successfully updated Setting ID: {}", id);
            
            // Step 7-8: Update setting and timestamp
            response.sendRedirect(request.getContextPath() + "/settings?success=" + 
                java.net.URLEncoder.encode("Setting updated successfully", "UTF-8"));
            
        }catch (NumberFormatException e) {
            logger.warn("Invalid ID format: {}", idParam);
            response.sendRedirect(request.getContextPath() + "/settings?error=" + 
                java.net.URLEncoder.encode("Invalid ID", "UTF-8"));
        }
        // E2: Database connection error
         catch (Exception e) {
            logger.error("Error updating Setting", e);
            response.sendRedirect(request.getContextPath() + "/settings?error=" + 
                java.net.URLEncoder.encode("An error occurred: " + e.getMessage(), "UTF-8"));
        }
    }
}
