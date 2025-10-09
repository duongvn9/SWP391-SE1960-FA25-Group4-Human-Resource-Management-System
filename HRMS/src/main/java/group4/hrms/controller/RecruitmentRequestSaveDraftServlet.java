/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package group4.hrms.controller;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Request;
import group4.hrms.util.FileUploadUtil;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "RecruitmentRequestSaveDraftServlet", urlPatterns = {"/requests/save-draft"})
public class RecruitmentRequestSaveDraftServlet extends HttpServlet {

    private final RequestDao requestDao = new RequestDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Lấy user đang đăng nhập (Manager)
        HttpSession session = req.getSession(false);
        Long userId = (Long) session.getAttribute("userId");

        // Tạo mới hoặc lấy request hiện tại (nếu đang chỉnh sửa nháp)
        Request request = new Request();
        request.setUserId(userId);
        request.setRequestTypeId(2L);
        request.setTitle(req.getParameter("jobTitle"));
        request.setDescription(req.getParameter("description"));
        
        // CHỈNH SỬA QUAN TRỌNG: Trạng thái là DRAFT
        request.setStatus("DRAFT"); 
        
        request.setPriority("NORMAL");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        String attachmentPath = FileUploadUtil.uploadFile(req, "attachment", "uploads/recruitments");
        request.setAttachmentPath(attachmentPath);

        // Lưu DB (cần có logic để update nếu đã là nháp cũ, hoặc save mới)
        requestDao.save(request); 

        
        
        // Chuyển hướng về trang danh sách nháp 
        res.sendRedirect(req.getContextPath() + "/requests/drafts?success=draft-saved");
    }

}
