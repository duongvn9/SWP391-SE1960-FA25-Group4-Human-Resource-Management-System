/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
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
import java.util.List;

/**
 * Servlet hiển thị danh sách các bản nháp (Recruitment Requests - DRAFT)
 * Dành cho Manager đang đăng nhập
 */
@WebServlet(name = "RecruitmentDraftListServlet", urlPatterns = {"/recruitment/drafts"})
public class RecruitmentDraftListServlet extends HttpServlet {

    private final RequestDao requestDao = new RequestDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // 1️⃣ Kiểm tra session đăng nhập
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        List<Request> draftList = requestDao.findDraftsByUserId(userId);

        req.setAttribute("draftList", draftList);

        req.getRequestDispatcher("/WEB-INF/views/recruitment/drafts.jsp").forward(req, res);
    }

}
