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
        
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String value = request.getParameter("value");
        String priorityStr = request.getParameter("priority");
        
        try {
            // Validate
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tên không được để trống");
                request.setAttribute("name", name);
                request.setAttribute("type", type);
                request.setAttribute("value", value);
                request.setAttribute("priority", priorityStr);
                request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                return;
            }
            
            if (type == null || type.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Type không được để trống");
                request.setAttribute("name", name);
                request.setAttribute("type", type);
                request.setAttribute("value", value);
                request.setAttribute("priority", priorityStr);
                request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
                return;
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
            
            logger.info("Tạo thành công Setting: {} - {}", type, name);
            response.sendRedirect(request.getContextPath() + "/settings?success=create");
            
        } catch (Exception e) {
            logger.error("Lỗi khi tạo Setting", e);
            request.setAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            request.setAttribute("name", name);
            request.setAttribute("type", type);
            request.setAttribute("value", value);
            request.setAttribute("priority", priorityStr);
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp").forward(request, response);
        }
    }
}
