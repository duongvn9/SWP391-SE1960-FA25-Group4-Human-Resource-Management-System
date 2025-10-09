package group4.hrms.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet điều khiển trang About của hệ thống HRMS
 */
@WebServlet("/about")
public class AboutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set page title
        request.setAttribute("pageTitle", "Về chúng tôi - HRMS");
        request.setAttribute("pageName", "about");

        // Forward to about page
        request.getRequestDispatcher("/WEB-INF/views/about.jsp").forward(request, response);
    }
}