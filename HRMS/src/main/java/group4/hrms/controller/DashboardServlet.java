package group4.hrms.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

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

        // Forward đến dashboard page
        request.getRequestDispatcher("/WEB-INF/views/dashboard/dashboard.jsp").forward(request, response);
    }
}