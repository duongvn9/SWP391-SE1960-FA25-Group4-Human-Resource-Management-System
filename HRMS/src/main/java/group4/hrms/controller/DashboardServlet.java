package group4.hrms.controller;

import java.io.IOException;

import group4.hrms.dao.UserDao;
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
    private final UserDao userDao = new UserDao();

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

        try {
            // Get total employees count
            int totalEmployees = userDao.countAll();
            request.setAttribute("totalEmployees", totalEmployees);
            logger.info("Total employees: {}", totalEmployees);
        } catch (Exception e) {
            logger.error("Error getting total employees count", e);
            request.setAttribute("totalEmployees", 0);
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