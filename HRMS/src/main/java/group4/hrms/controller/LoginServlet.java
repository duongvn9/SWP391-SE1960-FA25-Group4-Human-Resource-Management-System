package group4.hrms.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet({ "/login", "/auth/login" })
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra nếu user đã đăng nhập
        if (request.getSession(false) != null &&
                request.getSession().getAttribute("userId") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // Forward đến trang login
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // TODO: Implement actual authentication logic
        // For now, just redirect to dashboard for demo
        if ("admin".equals(username) && "1234".equals(password)) {
            // Set session attributes
            request.getSession().setAttribute("userId", 1);
            request.getSession().setAttribute("userFullName", "Administrator");
            request.getSession().setAttribute("userRole", "ADMIN");

            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            // Authentication failed
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}