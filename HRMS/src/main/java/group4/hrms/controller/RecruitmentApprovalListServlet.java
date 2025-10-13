package group4.hrms.controller;

import java.io.IOException;
import java.util.List;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Request;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Hiển thị danh sách yêu cầu tuyển dụng chờ HR duyệt
 */
@WebServlet(name = "RecruitmentApprovalListServlet", urlPatterns = {"/recruitment/hr"})
public class RecruitmentApprovalListServlet extends HttpServlet {

    private final RequestDao requestDao = new RequestDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("userRole");
        if (role == null || !role.equalsIgnoreCase("HR")) {
            res.sendRedirect(req.getContextPath() + "/access-denied.jsp");
            return;
        }

        // Lấy danh sách recruitment request chờ HR duyệt
        List<Request> pendingRequests = requestDao.findPendingForHR();

        req.setAttribute("pendingRequests", pendingRequests);
        req.getRequestDispatcher("/WEB-INF/views/recruitment/hr_approval_list.jsp").forward(req, res);
    }
}
