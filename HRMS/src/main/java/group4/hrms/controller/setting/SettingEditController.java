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
import java.util.Optional;

/**
 * Controller chỉnh sửa Setting
 */
@WebServlet("/settings/edit")
public class SettingEditController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingEditController.class);
    private final SettingDao settingDao = new SettingDao();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String type = request.getParameter("type");
        
        if (idParam == null || idParam.trim().isEmpty() || type == null || type.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/settings");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Optional<Setting> settingOpt = settingDao.findById(id, type);
            
            if (settingOpt.isEmpty()) {
                request.setAttribute("errorMessage", "Không tìm thấy setting");
                response.sendRedirect(request.getContextPath() + "/settings");
                return;
            }
            
            request.setAttribute("setting", settingOpt.get());
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            logger.error("ID không hợp lệ: {}", idParam);
            response.sendRedirect(request.getContextPath() + "/settings");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        String newType = request.getParameter("type");
        String value = request.getParameter("value");
        String priorityStr = request.getParameter("priority");
        String oldType = request.getParameter("oldType");
        
        try {
            Long id = Long.parseLong(idParam);
            
            if (newType == null || newType.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/settings?error=invalid_type");
                return;
            }
            
            // Get current setting using oldType from hidden field
            String typeToFind = oldType != null ? oldType : newType;
            Optional<Setting> currentSettingOpt = settingDao.findById(id, typeToFind);
            if (currentSettingOpt.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/settings?error=not_found");
                return;
            }
            
            Setting currentSetting = currentSettingOpt.get();
            
            // Validate
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Name cannot be empty");
                request.setAttribute("setting", currentSetting);
                request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                return;
            }
            
            // Parse priority
            Integer priority = null;
            if (priorityStr != null && !priorityStr.trim().isEmpty()) {
                try {
                    priority = Integer.parseInt(priorityStr.trim());
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "Priority must be a number");
                    request.setAttribute("setting", currentSetting);
                    request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                    return;
                }
            }
            
            // Check if type changed
            if (!currentSetting.getType().equals(newType)) {
                logger.info("Type changed from {} to {}, will delete old and create new", 
                           currentSetting.getType(), newType);
                
                // Delete old setting
                settingDao.delete(id, currentSetting.getType());
                
                // Create new setting with new type
                Setting newSetting = new Setting();
                newSetting.setName(name.trim());
                newSetting.setType(newType);
                newSetting.setValue(value != null ? value.trim() : null);
                newSetting.setPriority(priority);
                
                settingDao.create(newSetting);
                
                logger.info("Successfully changed type and recreated setting");
            } else {
                // Normal update
                Setting setting = new Setting();
                setting.setId(id);
                setting.setName(name.trim());
                setting.setType(newType);
                setting.setValue(value != null ? value.trim() : null);
                setting.setPriority(priority);
                
                settingDao.update(setting);
                
                logger.info("Successfully updated Setting ID: {}", id);
            }
            
            response.sendRedirect(request.getContextPath() + "/settings?success=update");
            
        } catch (Exception e) {
            logger.error("Error updating Setting", e);
            request.setAttribute("errorMessage", "Error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
        }
    }
}
