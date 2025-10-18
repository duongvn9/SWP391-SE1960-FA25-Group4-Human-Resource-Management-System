package group4.hrms.controller.setting;

import group4.hrms.dao.SettingDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller xóa Setting
 */
@WebServlet("/settings/delete")
public class SettingDeleteController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingDeleteController.class);
    private final SettingDao settingDao = new SettingDao();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String type = request.getParameter("type");
        
        if (idParam == null || idParam.trim().isEmpty() || type == null || type.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/settings?error=invalid_params");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            
            boolean deleted = settingDao.delete(id, type);
            
            if (deleted) {
                logger.info("Xóa thành công Setting ID: {} - Type: {}", id, type);
                response.sendRedirect(request.getContextPath() + "/settings?success=delete");
            } else {
                response.sendRedirect(request.getContextPath() + "/settings?error=not_found");
            }
            
        } catch (RuntimeException e) {
            logger.error("Lỗi khi xóa Setting", e);
            response.sendRedirect(request.getContextPath() + "/settings?error=" + 
                java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi xóa Setting", e);
            response.sendRedirect(request.getContextPath() + "/settings?error=unknown");
        }
    }
}
