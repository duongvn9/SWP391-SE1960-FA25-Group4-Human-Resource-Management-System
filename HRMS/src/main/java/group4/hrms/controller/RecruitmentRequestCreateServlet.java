package group4.hrms.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dto.RecruitmentDetailsDto;
import group4.hrms.model.Request;
import group4.hrms.model.RequestType;
import group4.hrms.util.FileUploadUtil;
import group4.hrms.util.RecruitmentPermissionHelper;
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

        // Kiểm tra quyền tạo recruitment request (chỉ Department Manager - positionId = 9)
        Long positionId = (Long) session.getAttribute("userPositionId");
        
        // Nếu vẫn null, thử lấy từ user object
        if (positionId == null) {
            Object userObj = session.getAttribute("user");
            if (userObj instanceof group4.hrms.model.User) {
                group4.hrms.model.User user = (group4.hrms.model.User) userObj;
                positionId = user.getPositionId();
            }
        }
        
        if (!RecruitmentPermissionHelper.canCreateRecruitmentRequest(positionId)) {
            // Redirect về trang dashboard nếu không có quyền
            res.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        try {
            // 1. HANDLE ATTACHMENT: could be multiple files or a drive link (both optional)
            String attachmentType = req.getParameter("attachmentType"); // 'file' or 'link'
            String attachmentPath = null;
            java.util.List<String> attachmentsList = null;
            if ("link".equals(attachmentType)) {
                String driveLink = req.getParameter("driveLink");
                if (driveLink != null && !driveLink.trim().isEmpty()) {
                    driveLink = driveLink.trim();
                    // Validate Google Drive link format if provided
                    if (!isValidGoogleDriveLink(driveLink)) {
                        req.setAttribute("error", "Invalid Google Drive link format. Please provide a valid shareable Google Drive link.");
                        req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                        return;
                    }
                    attachmentPath = driveLink;
                }
                // If driveLink is empty, attachmentPath remains null (which is fine since it's optional)
            } else {
                attachmentsList = new java.util.ArrayList<>();
                try {
                    for (jakarta.servlet.http.Part p : req.getParts()) {
                        if (p.getName().equals("attachments") && p.getSize() > 0) {
                            String stored = FileUploadUtil.saveUploadedFile(p, "recruitments");
                            if (stored != null) {
                                attachmentsList.add(stored);
                            }
                        }
                    }
                } catch (Exception ex) {
                    throw new ServletException("Failed to upload attachments: " + ex.getMessage(), ex);
                }
                if (!attachmentsList.isEmpty()) {
                    // attachmentPath lưu JSON array
                    attachmentPath = new com.google.gson.Gson().toJson(attachmentsList);
                } else {
                    attachmentPath = null;
                }
            }

            // 2. GÁN DỮ LIỆU VÀO OBJECT CHI TIẾT (RecruitmentDetailsDto)
            RecruitmentDetailsDto details = new RecruitmentDetailsDto();
            details.setPositionCode(req.getParameter("positionCode"));
            details.setPositionName(req.getParameter("positionName"));

            // Parse jobLevel as String (DB stores as String: SENIOR, JUNIOR, etc.)
            String jobLevel = req.getParameter("jobLevel");
            details.setJobLevel(jobLevel);

            // Parse quantity
            String quantityStr = req.getParameter("quantity");
            if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                try {
                    details.setQuantity(Integer.parseInt(quantityStr.trim()));
                } catch (NumberFormatException nfe) {
                    req.setAttribute("error", "Invalid quantity: must be a number");
                    req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                    return;
                }
            }

            // Set jobType (FULL_TIME, PART_TIME, CONTRACT, etc.)
            details.setJobType(req.getParameter("jobType"));
            details.setRecruitmentReason(req.getParameter("recruitmentReason"));

            // Parse salary fields separately (stored as individual fields in DB)
            String minSalaryRaw = req.getParameter("minSalary");
            String maxSalaryRaw = req.getParameter("maxSalary");
            String salaryType = req.getParameter("salaryType");

            if (minSalaryRaw != null && !minSalaryRaw.trim().isEmpty()) {
                try {
                    details.setMinSalary(Double.parseDouble(minSalaryRaw.trim()));
                } catch (NumberFormatException nfe) {
                    req.setAttribute("error", "Invalid minimum salary: must be a number");
                    req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                    return;
                }
            }

            if (maxSalaryRaw != null && !maxSalaryRaw.trim().isEmpty()) {
                try {
                    details.setMaxSalary(Double.parseDouble(maxSalaryRaw.trim()));
                } catch (NumberFormatException nfe) {
                    req.setAttribute("error", "Invalid maximum salary: must be a number");
                    req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                    return;
                }
            }

            details.setSalaryType(salaryType);

            // Set job summary and working location as separate fields
            details.setJobSummary(req.getParameter("jobSummary"));
            details.setWorkingLocation(req.getParameter("workingLocation"));
            details.setAttachmentPath(attachmentPath);
            details.setAttachments(attachmentsList);

            // Basic validation (since validate() method is removed)
            if (details.getPositionName() == null || details.getPositionName().trim().isEmpty()) {
                req.setAttribute("error", "Position name is required");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            if (details.getQuantity() == null || details.getQuantity() <= 0) {
                req.setAttribute("error", "Quantity must be greater than 0");
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
            // Truyền thông báo thành công và forward về trang create
            String successText = "Recruitment request submitted successfully! Request ID: " + request.getId();
            // Keep backward-compatible attribute name and also set 'success' which the JSP now expects
            req.setAttribute("successMessage", successText);
            req.setAttribute("success", successText);
            req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);

        } catch (Exception e) {
            System.err.println("Error in RecruitmentRequestCreateServlet: " + e.getMessage());
            req.setAttribute("error", "Submission Failed: Invalid input or server error.");
            req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
        }
    }

    /**
     * Validate Google Drive link format
     */
    private boolean isValidGoogleDriveLink(String link) {
        if (link == null || link.trim().isEmpty()) {
            return false;
        }
        
        // Check if it's a valid URL format
        try {
            java.net.URL url = new java.net.URL(link);
            String host = url.getHost().toLowerCase();
            
            // Must be from Google Drive domains
            if (!host.equals("drive.google.com") && !host.equals("docs.google.com")) {
                return false;
            }
            
            // Check for common Google Drive URL patterns
            String path = url.getPath();
            return path.contains("/file/d/") || path.contains("/document/d/") || 
                   path.contains("/spreadsheets/d/") || path.contains("/presentation/d/") ||
                   path.contains("/folders/") || path.contains("/drive/folders/");
                   
        } catch (java.net.MalformedURLException e) {
            return false;
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
