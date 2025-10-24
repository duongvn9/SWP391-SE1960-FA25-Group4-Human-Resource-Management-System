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
import java.util.ArrayList;
import java.util.List;

/**
 * Controller hiển thị danh sách tất cả settings (Department, Position)
 */
@WebServlet("/settings")
public class SettingListController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingListController.class);
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
        
        logger.info("Display Setting List");
        
        // Get filter parameters
        String typeFilter = request.getParameter("type");
        String searchKeyword = request.getParameter("search");
        String sortBy = request.getParameter("sortBy");
        String pageParam = request.getParameter("page");
        
        int currentPage = 1;
        int pageSize = 5;
        
        try {
            if (pageParam != null && !pageParam.isEmpty()) {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            }
        } catch (NumberFormatException e) {
            currentPage = 1;
        }
        
        try {
            logger.info("Type filter: {}, Search: {}, Sort: {}, Page: {}", 
                       typeFilter, searchKeyword, sortBy, currentPage);
            
            // Get data from database
            List<Setting> allSettings;
            
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                if (typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equals("all")) {
                    allSettings = settingDao.searchByType(typeFilter, searchKeyword.trim());
                } else {
                    allSettings = settingDao.search(searchKeyword.trim());
                }
            } else if (typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equals("all")) {
                allSettings = settingDao.findByType(typeFilter);
            } else {
                allSettings = settingDao.findAll();
            }
            
            // Sort
            if (sortBy != null && !sortBy.isEmpty()) {
                if ("id".equals(sortBy)) {
                    allSettings.sort((s1, s2) -> s1.getId().compareTo(s2.getId()));
                } else if ("name".equals(sortBy)) {
                    allSettings.sort((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
                }
            }
            
            // Pagination
            int totalItems = allSettings.size();
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }
            
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);
            
            List<Setting> settingList = allSettings.subList(startIndex, endIndex);
            
            logger.info("Found {} settings, showing page {}/{}", totalItems, currentPage, totalPages);
            
            // Set attributes
            request.setAttribute("settingList", settingList);
            request.setAttribute("typeFilter", typeFilter != null ? typeFilter : "all");
            request.setAttribute("searchKeyword", searchKeyword != null ? searchKeyword : "");
            request.setAttribute("sortBy", sortBy != null ? sortBy : "");
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalItems", totalItems);
            
            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-list.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error displaying Setting List", e);
            e.printStackTrace();
            
            request.setAttribute("settingList", new ArrayList<>());
            request.setAttribute("typeFilter", typeFilter != null ? typeFilter : "all");
            request.setAttribute("searchKeyword", searchKeyword != null ? searchKeyword : "");
            request.setAttribute("sortBy", sortBy != null ? sortBy : "");
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 0);
            request.setAttribute("totalItems", 0);
            request.setAttribute("errorMessage", "Error loading settings: " + e.getMessage());
            
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-list.jsp").forward(request, response);
        }
    }
}
