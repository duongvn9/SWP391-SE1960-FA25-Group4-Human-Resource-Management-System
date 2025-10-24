package group4.hrms.controller;

import java.io.IOException;

import group4.hrms.model.User;
import group4.hrms.service.JobPostingService;
import group4.hrms.util.JobPostingPermissionHelper;
import group4.hrms.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/job-posting/reject")
public class JobPostingRejectServlet extends HttpServlet {
    private JobPostingService jobPostingService;

    @Override
    public void init() throws ServletException {
        // Sử dụng dependency injection (hoặc service locator)
        jobPostingService = (JobPostingService) getServletContext().getAttribute("jobPostingService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Kiểm tra Quyền (theo positionId)
        Long approverId = SecurityUtil.getLoggedInUserId(request.getSession()); // Lấy ID người dùng
        User logged = (User) request.getSession().getAttribute("user");
        Long positionId = logged != null ? logged.getPositionId() : null;

        if (approverId == null || !JobPostingPermissionHelper.canApproveJobPosting(positionId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Must be logged in as HRM.");
            return;
        }
        
        // 2. Kiểm tra CSRF
        String csrfToken = request.getParameter("csrfToken");
        if (!SecurityUtil.verifyCsrfToken(request.getSession(), csrfToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }
        
        // 3. Lấy và Xác thực tham số
        String idStr = request.getParameter("id");
        String reason = request.getParameter("reason");
        Long jobId = null;
        
        try {
            jobId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            // ID không hợp lệ
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Invalid job ID.");
            return;
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Rejection reason is required.");
            return;
        }
        
        // 4. Gọi Service và Xử lý Ngoại lệ
        String errorMessage = null;
        try {
            // GỌI HÀM SERVICE ĐÃ SỬA: reject(id, approverId, reason)
            jobPostingService.reject(jobId, approverId, reason.trim());
            
        } catch (IllegalArgumentException e) {
            // Lỗi không tìm thấy Job Posting
            errorMessage = e.getMessage();
        } catch (IllegalStateException e) {
            // Lỗi trạng thái không hợp lệ (Ví dụ: không phải PENDING)
            errorMessage = e.getMessage();
        } catch (Exception e) {
            // Lỗi chung (SQL/IO)
            errorMessage = "A database error occurred: " + e.getMessage();
        }

        // 5. Điều hướng
        if (errorMessage == null) {
            response.sendRedirect(request.getContextPath() + "/job-postings?success=Job posting rejected successfully.");
        } else {
            response.sendRedirect(request.getContextPath() + "/job-postings?error=Failed to reject job posting: " + errorMessage);
        }
    }
}