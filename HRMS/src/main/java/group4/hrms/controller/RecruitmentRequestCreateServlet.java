package group4.hrms.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dto.RecruitmentDetailsDto;
import group4.hrms.model.Request;
import group4.hrms.model.RequestType;
import group4.hrms.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
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
    private final RequestTypeDao requestTypeDao = new RequestTypeDao();
    private final group4.hrms.dao.UserDao userDao = new group4.hrms.dao.UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Kiểm tra session đăng nhập
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        /*
         // Kiểm tra quyền (chỉ Department Manager được tạo request)
        String positionName = (String) session.getAttribute("positionName");
        System.out.println("[DEBUG] positionName in session: " + positionName);
            if (positionName == null || !positionName.equals("Department Manager")) {
                // access-denied.jsp does not exist in this project; forward to login with an error message
                req.setAttribute("error", "Access denied: you do not have permission to create recruitment requests.");
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, res);
                return;
            }
        */

        try {
            // 1. UPLOAD FILE và LẤY PATH
            String attachmentPath = FileUploadUtil.uploadFile(req, "attachment", "uploads/recruitments");

            // 2. GÁN DỮ LIỆU VÀO OBJECT CHI TIẾT (RecruitmentDetailsDto)
            RecruitmentDetailsDto details = new RecruitmentDetailsDto();
            details.setPositionCode(req.getParameter("positionCode"));
            details.setPositionName(req.getParameter("positionName"));

            // Lấy job level trực tiếp từ select box
            details.setJobLevel(req.getParameter("jobLevel"));
            details.setQuantity(Integer.parseInt(req.getParameter("quantity")));
            details.setJobType(req.getParameter("jobType"));
            details.setRecruitmentReason(req.getParameter("recruitmentReason"));
            String minSalaryRaw = req.getParameter("minSalary");
            String maxSalaryRaw = req.getParameter("maxSalary");
            // If user provided salary inputs, ensure they are numeric
            if (minSalaryRaw != null && !minSalaryRaw.trim().isEmpty()) {
                try {
                    details.setMinSalary(Double.parseDouble(minSalaryRaw.trim()));
                } catch (NumberFormatException nfe) {
                    req.setAttribute("error", "Invalid minimum salary: must be a number");
                    req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                    return;
                }
            } else {
                details.setMinSalary(null);
            }

            if (maxSalaryRaw != null && !maxSalaryRaw.trim().isEmpty()) {
                try {
                    details.setMaxSalary(Double.parseDouble(maxSalaryRaw.trim()));
                } catch (NumberFormatException nfe) {
                    req.setAttribute("error", "Invalid maximum salary: must be a number");
                    req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                    return;
                }
            } else {
                details.setMaxSalary(null);
            }
            details.setSalaryType(req.getParameter("salaryType"));
            details.setJobSummary(req.getParameter("jobSummary"));
            details.setAttachmentPath(attachmentPath);
            details.setWorkingLocation(req.getParameter("workingLocation"));
    
            
                // VALIDATION chi tiết
                try {
                    details.validate();
                } catch (IllegalArgumentException ve) {
                    req.setAttribute("error", "Invalid recruitment details: " + ve.getMessage());
                    req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                    return;
                }

            // 3. TẠO REQUEST CHÍNH VÀ GÁN JSON
            Request request = new Request();

            Long accountId = (Long) session.getAttribute("accountId"); 
            Long userId = (Long) session.getAttribute("userId");       

            // Kiểm tra tính hợp lệ của ID (Thêm bước an toàn)
            if (accountId == null || userId == null) {
                res.sendRedirect(req.getContextPath() + "/login?error=session_data_missing");
                return;
            }
            request.setCreatedByAccountId(accountId);
            request.setCreatedByUserId(userId);

            // Tìm hoặc tạo RequestType cho RECRUITMENT_REQUEST
            Long requestTypeId;
            try {
                RequestType requestType = requestTypeDao.findByCode("RECRUITMENT_REQUEST");
                if (requestType == null) {
                    try {
                        RequestType newType = new RequestType();
                        newType.setCode("RECRUITMENT_REQUEST");
                        newType.setName("Recruitment Request");
                        requestType = requestTypeDao.save(newType);
                        if (requestType == null) {
                            throw new ServletException("Failed to create RequestType: save returned null");
                        }
                    } catch (Exception e) {
                        throw new ServletException("Failed to create RequestType: " + e.getMessage(), e);
                    }
                }
                requestTypeId = requestType.getId();
            } catch (Exception e) {
                throw new ServletException("Error handling RequestType: " + e.getMessage(), e);
            }
            
            request.setRequestTypeId(requestTypeId);
            request.setTitle(req.getParameter("jobTitle")); // Tiêu đề chính

            // Lấy departmentId từ user đang đăng nhập
            Long departmentId = null;
            try {
                departmentId = userDao.findById(userId)
                    .map(u -> u.getDepartmentId())
                    .orElseThrow(() -> new ServletException("Department not found for user " + userId));
            } catch (Exception ex) {
                throw new ServletException("Failed to get department: " + ex.getMessage(), ex);
            }
            request.setDepartmentId(departmentId); // Gán đúng phòng ban

            request.setRecruitmentDetail(details); // <<< LƯU CHI TIẾT VÀO JSON

            request.setStatus("PENDING");
            request.setCreatedAt(LocalDateTime.now());
            request.setUpdatedAt(LocalDateTime.now());

            requestDao.save(request);

            sendNotificationToHRAndHRM(req, request);
            // Redirect back to dashboard after successful submission
            res.sendRedirect(req.getContextPath() + "/dashboard?success=submitted");

        } catch (Exception e) {
            System.err.println("Error in RecruitmentRequestCreateServlet: " + e.getMessage());
            req.setAttribute("error", "Submission Failed: Invalid input or server error.");
            req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
        }
    }

    private void sendNotificationToHRAndHRM(HttpServletRequest req, Request request) {
        try {
            System.out.println("📩 Notification: Recruitment request #" + request.getId()
                    + " from user " + request.getCreatedByUserId()
                    + " has been sent to HR & HRM.");
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }

}
