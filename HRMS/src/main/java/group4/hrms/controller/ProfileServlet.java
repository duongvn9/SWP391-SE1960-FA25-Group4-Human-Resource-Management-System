package group4.hrms.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet xử lý trang thông tin cá nhân
 * URL mapping: /profile
 * 
 * @author Group4 - SWP391
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // TODO: Load user profile data from database
        // Long userId = (Long) session.getAttribute("userId");
        // UserService userService = new UserService();
        // User user = userService.getUserById(userId);
        // request.setAttribute("user", user);
        
        // Chuyển tiếp đến trang profile
        request.getRequestDispatcher("/WEB-INF/views/profile/index.jsp")
               .forward(request, response);
    }
}