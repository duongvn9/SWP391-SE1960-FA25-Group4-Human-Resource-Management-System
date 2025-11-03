package group4.hrms.controller;

import java.io.IOException;
import group4.hrms.util.RecruitmentPermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RecruitmentRequestFormServlet", urlPatterns = {"/requests/recruitment/create"})
public class RecruitmentRequestFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        
        // Kiểm tra session đăng nhập
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra quyền tạo recruitment request (chỉ Department Manager - positionId = 9)
        Long positionId = (Long) session.getAttribute("userPositionId");
        
        // Nếu vẫn null, thử lấy từ user object
        if (positionId == null) {
            Object userObj = session.getAttribute("user");
            if (userObj instanceof group4.hrms.model.User) {
                group4.hrms.model.User user = (group4.hrms.model.User) userObj;
                positionId = user.getPositionId();
            }
        }
        
        if (!RecruitmentPermissionHelper.canCreateRecruitmentRequest(positionId)) {
            // Redirect về trang dashboard nếu không có quyền
            res.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        
        req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp")
                .forward(req, res);
    }
}