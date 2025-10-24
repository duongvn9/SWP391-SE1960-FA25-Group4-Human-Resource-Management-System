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
        
        // Get filter parameters for Department
        String deptSearch = request.getParameter("deptSearch");
        String deptSort = request.getParameter("deptSort");
        String deptPageParam = request.getParameter("deptPage");
        
        // Get filter parameters for Position
        String posSearch = request.getParameter("posSearch");
        String posSort = request.getParameter("posSort");
        String posPageParam = request.getParameter("posPage");
        
        int deptPage = 1;
        int posPage = 1;
        int pageSize = 5;
        
        try {
            if (deptPageParam != null && !deptPageParam.isEmpty()) {
                deptPage = Integer.parseInt(deptPageParam);
                if (deptPage < 1) deptPage = 1;
            }
            if (posPageParam != null && !posPageParam.isEmpty()) {
                posPage = Integer.parseInt(posPageParam);
                if (posPage < 1) posPage = 1;
            }
        } catch (NumberFormatException e) {
            deptPage = 1;
            posPage = 1;
        }
        
        try {
            logger.info("Dept: search={}, sort={}, page={}", deptSearch, deptSort, deptPage);
            logger.info("Pos: search={}, sort={}, page={}", posSearch, posSort, posPage);
            
            // Get all settings
            List<Setting> allSettings = settingDao.findAll();
            
            // Separate into Department and Position
            List<Setting> allDepartments = new ArrayList<>();
            List<Setting> allPositions = new ArrayList<>();
            
            for (Setting s : allSettings) {
                if ("Department".equals(s.getType())) {
                    allDepartments.add(s);
                } else if ("Position".equals(s.getType())) {
                    allPositions.add(s);
                }
            }
            
            logger.info("After separation: {} departments, {} positions", allDepartments.size(), allPositions.size());
            
            // Filter and sort Departments
            if (deptSearch != null && !deptSearch.trim().isEmpty()) {
                String keyword = deptSearch.trim().toLowerCase();
                List<Setting> filteredDepts = new ArrayList<>();
                for (Setting d : allDepartments) {
                    if (d.getName().toLowerCase().contains(keyword) ||
                        (d.getDescription() != null && d.getDescription().toLowerCase().contains(keyword))) {
                        filteredDepts.add(d);
                    }
                }
                allDepartments = filteredDepts;
            }
            if ("id".equals(deptSort)) {
                allDepartments.sort((s1, s2) -> s1.getId().compareTo(s2.getId()));
            } else if ("name".equals(deptSort)) {
                allDepartments.sort((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
            }
            
            // Filter and sort Positions
            if (posSearch != null && !posSearch.trim().isEmpty()) {
                String keyword = posSearch.trim().toLowerCase();
                List<Setting> filteredPos = new ArrayList<>();
                for (Setting p : allPositions) {
                    if (p.getName().toLowerCase().contains(keyword) ||
                        (p.getValue() != null && p.getValue().toLowerCase().contains(keyword)) ||
                        (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword))) {
                        filteredPos.add(p);
                    }
                }
                allPositions = filteredPos;
            }
            if ("id".equals(posSort)) {
                allPositions.sort((s1, s2) -> s1.getId().compareTo(s2.getId()));
            } else if ("name".equals(posSort)) {
                allPositions.sort((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
            }
            
            // Pagination for Departments
            int deptTotal = allDepartments.size();
            int deptTotalPages = (int) Math.ceil((double) deptTotal / pageSize);
            if (deptPage > deptTotalPages && deptTotalPages > 0) deptPage = deptTotalPages;
            int deptStart = (deptPage - 1) * pageSize;
            int deptEnd = Math.min(deptStart + pageSize, deptTotal);
            List<Setting> departmentList = deptTotal > 0 ? allDepartments.subList(deptStart, deptEnd) : new ArrayList<>();
            
            // Pagination for Positions
            int posTotal = allPositions.size();
            int posTotalPages = (int) Math.ceil((double) posTotal / pageSize);
            if (posPage > posTotalPages && posTotalPages > 0) posPage = posTotalPages;
            int posStart = (posPage - 1) * pageSize;
            int posEnd = Math.min(posStart + pageSize, posTotal);
            List<Setting> positionList = posTotal > 0 ? allPositions.subList(posStart, posEnd) : new ArrayList<>();
            
            logger.info("Departments: {} total, page {}/{}, list size: {}", deptTotal, deptPage, deptTotalPages, departmentList.size());
            logger.info("Positions: {} total, page {}/{}, list size: {}", posTotal, posPage, posTotalPages, positionList.size());
            
            // Set attributes for Departments
            request.setAttribute("departmentList", departmentList);
            logger.info("Set departmentList attribute with {} items", departmentList.size());
            request.setAttribute("deptSearch", deptSearch != null ? deptSearch : "");
            request.setAttribute("deptSort", deptSort != null ? deptSort : "");
            request.setAttribute("deptPage", deptPage);
            request.setAttribute("deptTotalPages", deptTotalPages);
            request.setAttribute("deptTotal", deptTotal);
            
            // Set attributes for Positions
            request.setAttribute("positionList", positionList);
            logger.info("Set positionList attribute with {} items", positionList.size());
            request.setAttribute("posSearch", posSearch != null ? posSearch : "");
            request.setAttribute("posSort", posSort != null ? posSort : "");
            request.setAttribute("posPage", posPage);
            request.setAttribute("posTotalPages", posTotalPages);
            request.setAttribute("posTotal", posTotal);
            
            // Keep settingList for backward compatibility
            List<Setting> settingList = new ArrayList<>();
            settingList.addAll(allDepartments);
            settingList.addAll(allPositions);
            request.setAttribute("settingList", settingList);
            
            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-list.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error displaying Setting List", e);
            e.printStackTrace();
            
            request.setAttribute("departmentList", new ArrayList<>());
            request.setAttribute("positionList", new ArrayList<>());
            request.setAttribute("settingList", new ArrayList<>());
            request.setAttribute("deptSearch", deptSearch != null ? deptSearch : "");
            request.setAttribute("deptSort", deptSort != null ? deptSort : "");
            request.setAttribute("deptPage", 1);
            request.setAttribute("deptTotalPages", 0);
            request.setAttribute("deptTotal", 0);
            request.setAttribute("posSearch", posSearch != null ? posSearch : "");
            request.setAttribute("posSort", posSort != null ? posSort : "");
            request.setAttribute("posPage", 1);
            request.setAttribute("posTotalPages", 0);
            request.setAttribute("posTotal", 0);
            request.setAttribute("errorMessage", "Error loading settings: " + e.getMessage());
            
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-list.jsp").forward(request, response);
        }
    }
}
