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
@WebServlet(name = "RecruitmentRequestSaveDraftServlet", urlPatterns = {"/recruitment/save-draft"})
public class RecruitmentRequestSaveDraftServlet extends HttpServlet {

    private final RequestDao requestDao = new RequestDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Kiểm tra role
        String role = (String) session.getAttribute("userRole");
        if (role == null || !role.equalsIgnoreCase("MANAGER")) {
            res.sendRedirect(req.getContextPath() + "/access-denied.jsp");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");

        Request request = new Request();
        request.setUserId(userId);
        request.setRequestTypeId(2L);
        request.setTitle(req.getParameter("jobTitle"));
        request.setDescription(req.getParameter("description"));
        request.setStatus("DRAFT");
        request.setPriority("NORMAL");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        String attachmentPath = FileUploadUtil.uploadFile(req, "attachment", "uploads/recruitments");
        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            request.setAttachmentPath(attachmentPath);
        }

        requestDao.save(request);

        res.sendRedirect(req.getContextPath() + "/recruitment/drafts?success=draft-saved");
    }

}
