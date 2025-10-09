/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package group4.hrms.controller;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Request;
import group4.hrms.util.FileUploadUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 *
 * @author ADMIN
 */
@WebServlet(name="RecruitmentRequestCreateServlet", urlPatterns={"/requests/create"})
public class RecruitmentRequestCreateServlet extends HttpServlet {
   
    private final RequestDao requestDao = new RequestDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Lấy user đang đăng nhập (Manager)
        HttpSession session = req.getSession(false);
        Long userId = (Long) session.getAttribute("userId");

        // Tạo mới đối tượng Request
        Request request = new Request();
        request.setUserId(userId);
        request.setRequestTypeId(2L); // ví dụ: 2 = Recruitment Request
        request.setTitle(req.getParameter("jobTitle"));
        request.setDescription(req.getParameter("description"));
        request.setStatus("PENDING"); // gửi HR duyệt
        request.setPriority("NORMAL");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        // Upload file nếu có
        String attachmentPath = FileUploadUtil.uploadFile(req, "attachment", "uploads/recruitments");
        request.setAttachmentPath(attachmentPath);

        // Lưu DB
        requestDao.save(request);

        sendNotificationToHRAndHRM(req, request);

        // Chuyển hướng hoặc hiển thị thành công
        res.sendRedirect(req.getContextPath() + "/requests/success");
    }

    private void sendNotificationToHRAndHRM(HttpServletRequest req, Request request) {
        try {
            // Có thể dùng NotificationDao / EmailUtil tùy hệ thống.
            // Ví dụ: tạo record notification hoặc gửi email.

            System.out.println("Send notification: Recruitment request #" + request.getId() +
                                              " from userId=" + request.getUserId() +
                                              " has been sent to HR & HRM");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}