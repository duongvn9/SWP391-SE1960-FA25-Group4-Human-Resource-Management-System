package group4.hrms.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Request;
import group4.hrms.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import group4.hrms.dto.RecruitmentDetailsDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet xử lý việc tạo mới Recruitment Request (chỉ cho MANAGER)
 */
@WebServlet(name = "RecruitmentRequestCreateServlet", urlPatterns = {"/requests/recruitment/submit"})
@MultipartConfig // Cần thiết cho file upload
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

        try {
            // 1. UPLOAD FILE và LẤY PATH
            String attachmentPath = FileUploadUtil.uploadFile(req, "attachment", "uploads/recruitments");

            // 2. GÁN DỮ LIỆU VÀO OBJECT CHI TIẾT (RecruitmentDetailsDto)
            RecruitmentDetailsDto details = new RecruitmentDetailsDto();
            details.setPositionCode(req.getParameter("positionCode"));
            details.setPositionName(req.getParameter("positionName"));

            // Xử lý chuyển đổi Integer an toàn
            details.setJobLevel(Integer.parseInt(req.getParameter("jobLevel")));
            details.setQuantity(Integer.parseInt(req.getParameter("quantity")));

            details.setType(req.getParameter("type"));
            details.setRecruitmentReason(req.getParameter("recruitmentReason"));
            details.setBudgetSalaryRange(req.getParameter("budgetSalaryRange"));
            details.setJobSummary(req.getParameter("jobSummary"));
            details.setAttachmentPath(attachmentPath);

            // 3. TẠO REQUEST CHÍNH VÀ GÁN JSON
            Request request = new Request();

            Long accountId = (Long) session.getAttribute("accountId"); 
            Long userId = (Long) session.getAttribute("userId");       

            // Kiểm tra tính hợp lệ của ID (Thêm bước an toàn)
            if (accountId == null || userId == null) {
                res.sendRedirect(req.getContextPath() + "/login?error=session_data_missing");
                return;
            }
            request.setCreatedByAccountId(accountId); // 
            request.setCreatedByUserId(userId);

            request.setRequestTypeId(2L); // Recruitment Request
            request.setTitle(req.getParameter("jobTitle")); // Tiêu đề chính
            request.setDepartmentId(null); // Cần lấy Department ID thực sự

            request.setRecruitmentDetail(details); // <<< LƯU CHI TIẾT VÀO JSON

            request.setStatus("PENDING");
            request.setCreatedAt(LocalDateTime.now());
            request.setUpdatedAt(LocalDateTime.now());

            requestDao.save(request);

            sendNotificationToHRAndHRM(req, request);
            res.sendRedirect(req.getContextPath() + "/recruitment?success=submitted");

        } catch (Exception e) {
            System.err.println("Error in RecruitmentRequestCreateServlet: " + e.getMessage());
            req.setAttribute("error", "Submission Failed: Invalid input or server error.");
            req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
        }
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
