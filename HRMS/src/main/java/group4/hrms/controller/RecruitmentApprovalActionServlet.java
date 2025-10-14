package group4.hrms.controller;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Request;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@WebServlet(name = "RecruitmentApprovalActionServlet", urlPatterns = {"/recruitment/approve"})
public class RecruitmentApprovalActionServlet extends HttpServlet {

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

        // HR duyệt => sang HR_APPROVED
        // HRM duyệt => sang APPROVED
        if (role.equalsIgnoreCase("HR")) {
            request.setStatus("HR_APPROVED");
        } else if (role.equalsIgnoreCase("HRM")) {
            request.setStatus("APPROVED");
        }

        request.setApprovedBy(userId);
        request.setApprovedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        requestDao.update(request);

        // Chuyển hướng lại danh sách
        if (role.equalsIgnoreCase("HR")) {
            res.sendRedirect(req.getContextPath() + "/recruitment/hr?success=approved");
        } else {
            res.sendRedirect(req.getContextPath() + "/recruitment/hrm?success=approved");
        }
    }
}

