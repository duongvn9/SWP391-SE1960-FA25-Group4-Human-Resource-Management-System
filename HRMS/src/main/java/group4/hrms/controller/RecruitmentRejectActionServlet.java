package group4.hrms.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Request;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet xử lý HR hoặc HRM từ chối yêu cầu
 */
@WebServlet(name = "RecruitmentRejectActionServlet", urlPatterns = {"/recruitment/reject"})
public class RecruitmentRejectActionServlet extends HttpServlet {

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
        Long userId = (Long) session.getAttribute("userId");

        if (role == null || (!role.equalsIgnoreCase("HR") && !role.equalsIgnoreCase("HRM"))) {
            res.sendRedirect(req.getContextPath() + "/access-denied.jsp");
            return;
        }

        Long requestId = Long.parseLong(req.getParameter("id"));
        Optional<Request> opt = requestDao.findById(requestId);
        if (opt.isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/error.jsp?msg=Request not found");
            return;
        }

        Request request = opt.get();

        // HR từ chối => HR_REJECTED
        // HRM từ chối => REJECTED
        if (role.equalsIgnoreCase("HR")) {
            request.setStatus("HR_REJECTED");
        } else if (role.equalsIgnoreCase("HRM")) {
            request.setStatus("REJECTED");
        }

        request.setApprovedBy(userId);
        request.setApprovedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        requestDao.update(request);

        // Chuyển hướng lại danh sách
        if (role.equalsIgnoreCase("HR")) {
            res.sendRedirect(req.getContextPath() + "/recruitment/hr?success=rejected");
        } else {
            res.sendRedirect(req.getContextPath() + "/recruitment/hrm?success=rejected");
        }
    }
}
