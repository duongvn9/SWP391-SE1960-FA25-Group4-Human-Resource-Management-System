package group4.hrms.controller;

import java.io.IOException;

import group4.hrms.dto.DashboardKpiDto;
import group4.hrms.service.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServlet.class);
    private final DashboardService dashboardService = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            // Chưa đăng nhập, redirect về login
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Kiểm tra quyền xem dashboard - chỉ HR (8) và HRM (7) mới thấy KPI và charts
        group4.hrms.model.User user = (group4.hrms.model.User) session.getAttribute("user");
        boolean canViewDashboardData = false;
        
        if (user != null && user.getPositionId() != null) {
            canViewDashboardData = (user.getPositionId() == 7 || user.getPositionId() == 8);
        }
        
        request.setAttribute("canViewDashboardData", canViewDashboardData);
        logger.info("User position: {}, canViewDashboardData: {}", 
                user != null ? user.getPositionId() : "null", canViewDashboardData);

        // Chỉ load KPI data nếu user có quyền xem
        if (canViewDashboardData) {
            try {
                // Get all dashboard KPIs
                DashboardKpiDto kpis = dashboardService.getDashboardKpis();
                request.setAttribute("kpis", kpis);
                logger.info("Dashboard KPIs loaded successfully");
            } catch (Exception e) {
                logger.error("Error loading dashboard KPIs", e);
                // Set empty KPI object to prevent JSP errors
                request.setAttribute("kpis", new DashboardKpiDto());
            }
        }

        // Set permission flags for sidebar
        request.setAttribute("canViewUserList", group4.hrms.util.PermissionUtil.canViewUserList(request));
        request.setAttribute("canViewAccountList", group4.hrms.util.PermissionUtil.canViewAccountList(request));
        request.setAttribute("canCreateUser", group4.hrms.util.PermissionUtil.canCreateUser(request));
        request.setAttribute("canCreateAccount", group4.hrms.util.PermissionUtil.canCreateAccount(request));
        request.setAttribute("isAdminPosition",
                "ADMIN".equals(group4.hrms.util.PermissionUtil.getCurrentUserPositionCode(request)));

        // Forward đến dashboard page
        request.getRequestDispatcher("/WEB-INF/views/dashboard/dashboard.jsp").forward(request, response);
    }
}