package group4.hrms.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RecruitmentRequestFormServlet", urlPatterns = {"/requests/recruitment/create"})
public class RecruitmentRequestFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Có thể thêm logic kiểm tra quyền MANAGER ở đây nếu cần
        
        req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp")
                .forward(req, res);
    }
}