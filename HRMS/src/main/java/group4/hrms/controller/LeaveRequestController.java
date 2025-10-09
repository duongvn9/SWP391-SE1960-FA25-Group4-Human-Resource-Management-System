
package group4.hrms.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/request/leave")
public class LeaveRequestController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chỉ xử lý action=create, các action khác bỏ qua
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/views/requests/leave-form.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chỉ xử lý submit form create
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            // Chỉ hiển thị lại form, không xử lý DB
            request.setAttribute("success", "(Demo) Leave request submitted successfully.");
            request.getRequestDispatcher("/WEB-INF/views/requests/leave-form.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
