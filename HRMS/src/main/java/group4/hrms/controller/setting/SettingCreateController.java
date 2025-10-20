package group4.hrms.controller.setting;

import group4.hrms.dao.SettingDao;
import group4.hrms.model.Setting;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Controller tạo mới Setting
 */
@WebServlet("/settings/new")
public class SettingCreateController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingCreateController.class);
    private final SettingDao settingDao = new SettingDao();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.info("Hiển thị form tạo Setting");
        request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
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
        
        // E4: Check authorization (Admin only)
        String userRole = (String) request.getSession().getAttribute("userRole");
        if (userRole == null || !userRole.equalsIgnoreCase("ADMIN")) {
            logger.warn("Unauthorized access attempt by role: {}", userRole);
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }
        
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String value = request.getParameter("value");
        String priorityStr = request.getParameter("priority");
        
        try {
            // E1: Validate required fields
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Setting name is required");
                request.setAttribute("name", name);
                request.setAttribute("type", type);
                request.setAttribute("value", value);
                request.setAttribute("priority", priorityStr);
                request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                return;
            }
            
            if (type == null || type.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Setting type is required");
                request.setAttribute("name", name);
                request.setAttribute("type", type);
                request.setAttribute("value", value);
                request.setAttribute("priority", priorityStr);
                request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                return;
            }
            
            // Validate type value
            if (!type.equals("Department") && !type.equals("Position") && !type.equals("Role")) {
                request.setAttribute("errorMessage", "Invalid setting type");
                request.setAttribute("name", name);
                request.setAttribute("type", type);
                request.setAttribute("value", value);
                request.setAttribute("priority", priorityStr);
                request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                return;
            }
            
            // E5: Sanitize input (trim and basic validation)
            name = name.trim();
            type = type.trim();
            if (value != null) {
                value = value.trim();
            }
            
            // A2: Check duplicate setting key (name + type combination)
            List<Setting> existingSettings = settingDao.findByType(type);
            for (Setting existing : existingSettings) {
                if (existing.getName().equalsIgnoreCase(name)) {
                    request.setAttribute("errorMessage", "Setting with this name already exists in " + type);
                    request.setAttribute("name", name);
                    request.setAttribute("type", type);
                    request.setAttribute("value", value);
                    request.setAttribute("priority", priorityStr);
                    request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                    return;
                }
            }
            
            // Parse priority
            Integer priority = null;
            if (priorityStr != null && !priorityStr.trim().isEmpty()) {
                try {
                    priority = Integer.parseInt(priorityStr.trim());
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "Priority phải là số");
                    request.setAttribute("name", name);
                    request.setAttribute("type", type);
                    request.setAttribute("value", value);
                    request.setAttribute("priority", priorityStr);
                    request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                    return;
                }
            }
            
            // Tạo mới
            Setting setting = new Setting(name.trim(), type.trim(), 
                value != null ? value.trim() : null, priority);
            settingDao.create(setting);
            
            logger.info("Setting created successfully: {} - {}", type, name);
            response.sendRedirect(request.getContextPath() + "/settings?success=Setting created successfully");
            
        } catch (java.sql.SQLException e) {
            // E2: Database connection error
            logger.error("Database error while creating Setting", e);
            request.setAttribute("errorMessage", "Database error. Please try again later");
            request.setAttribute("name", name);
            request.setAttribute("type", type);
            request.setAttribute("value", value);
            request.setAttribute("priority", priorityStr);
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error creating Setting", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.setAttribute("name", name);
            request.setAttribute("type", type);
            request.setAttribute("value", value);
            request.setAttribute("priority", priorityStr);
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
        }
    }
}
