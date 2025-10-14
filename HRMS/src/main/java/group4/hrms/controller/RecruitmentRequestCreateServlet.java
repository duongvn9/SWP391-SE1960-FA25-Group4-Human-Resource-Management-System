package group4.hrms.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Request;
import group4.hrms.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet xử lý việc tạo mới Recruitment Request (chỉ cho MANAGER)
 */
@WebServlet(name = "RecruitmentRequestCreateServlet", urlPatterns = {"/recruitment/create"})
public class RecruitmentRequestCreateServlet extends HttpServlet {

    private final RequestDao requestDao = new RequestDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Kiểm tra session đăng nhập
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Kiểm tra quyền (chỉ MANAGER được tạo request)
        String role = (String) session.getAttribute("userRole");
        if (role == null || !role.equalsIgnoreCase("MANAGER")) {
            res.sendRedirect(req.getContextPath() + "/access-denied.jsp");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");

        // Tạo mới đối tượng Request
        Request request = new Request();
        request.setUserId(userId);
        request.setRequestTypeId(2L); // 2 = Recruitment Request
        request.setTitle(req.getParameter("jobTitle"));
        request.setDescription(req.getParameter("description"));
        request.setStatus("PENDING"); // Gửi HR duyệt
        request.setPriority("NORMAL");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        // 🔹 Upload file nếu có
        String attachmentPath = FileUploadUtil.uploadFile(req, "attachment", "uploads/recruitments");
        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            request.setAttachmentPath(attachmentPath);
        }

        requestDao.save(request);

        sendNotificationToHRAndHRM(req, request);
        res.sendRedirect(req.getContextPath() + "/recruitment?success=submitted");
    }

    private void sendNotificationToHRAndHRM(HttpServletRequest req, Request request) {
        try {
            System.out.println("📩 Notification: Recruitment request #" + request.getId()
                    + " from userId=" + request.getUserId()
                    + " has been sent to HR & HRM.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
