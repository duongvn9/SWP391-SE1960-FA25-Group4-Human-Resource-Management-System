package group4.hrms.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import group4.hrms.dao.RequestDao; // <<< Cần thư viện Gson/Jackson
import group4.hrms.dto.RecruitmentDetailsDto;
import group4.hrms.model.Request;
import group4.hrms.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet xử lý lưu bản nháp Recruitment Request (chỉ cho MANAGER)
 */
@WebServlet(name = "RecruitmentRequestSaveDraftServlet", urlPatterns = {"/requests/recruitment/save-draft"})
@MultipartConfig // Bắt buộc nếu form có enctype="multipart/form-data"
public class RecruitmentRequestSaveDraftServlet extends HttpServlet {

    private final RequestDao requestDao = new RequestDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        // Lấy ID Account và User ID từ Session (GIẢ ĐỊNH CHÚNG ĐƯỢC LƯU NHƯ THẾ NÀY)
        Long accountId = (Long) session.getAttribute("accountId");
        Long userId = (Long) session.getAttribute("userId");

        if (accountId == null || userId == null || session.getAttribute("userRole") == null ||
            !session.getAttribute("userRole").toString().equalsIgnoreCase("MANAGER")) {
            res.sendRedirect(req.getContextPath() + "/access-denied.jsp");
            return;
        }

        try {
            // 1. UPLOAD FILE
            String attachmentPath = FileUploadUtil.uploadFile(req, "attachment", "uploads/recruitments");

            // 2. TẠO VÀ GÁN DỮ LIỆU VÀO OBJECT CHI TIẾT (RecruitmentDetailsDto)
            RecruitmentDetailsDto details = new RecruitmentDetailsDto();
            details.setPositionName(req.getParameter("positionName"));
            details.setQuantity(Integer.parseInt(req.getParameter("quantity")));
            details.setJobSummary(req.getParameter("jobSummary")); // Dùng cho trường Description
            details.setAttachmentPath(attachmentPath);
            // Gán các trường JSON khác: positionCode, jobLevel (String), type, recruitmentReason...
            details.setPositionCode(req.getParameter("positionCode"));
            details.setJobLevel(req.getParameter("jobLevel")); // jobLevel is String (SENIOR, JUNIOR, etc.)

            // 3. TẠO REQUEST CHÍNH (Sử dụng trường Active)
            Request request = new Request();
            request.setCreatedByAccountId(accountId);
            request.setCreatedByUserId(userId);
            request.setRequestTypeId(2L); // Recruitment Request
            request.setTitle(req.getParameter("jobTitle"));

            // LƯU CHI TIẾT VÀO JSON
            request.setRecruitmentDetail(details);

            request.setStatus("DRAFT");
            request.setCreatedAt(LocalDateTime.now());
            request.setUpdatedAt(LocalDateTime.now());

            requestDao.save(request);

            res.sendRedirect(req.getContextPath() + "/recruitment/drafts?success=draft-saved");

        } catch (Exception e) {
            System.err.println("Error saving draft: " + e.getMessage());
            res.sendRedirect(req.getContextPath() + "/recruitment/drafts?error=draft-failed");
        }
    }
}