package group4.hrms.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet điều khiển trang FAQs của hệ thống HRMS
 */
@WebServlet("/faqs")
public class FaqsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set page title
        request.setAttribute("pageTitle", "Câu hỏi thường gặp - HRMS");
        request.setAttribute("pageName", "faqs");

        // Forward to faqs page
        request.getRequestDispatcher("/WEB-INF/views/faqs.jsp").forward(request, response);
    }
}
