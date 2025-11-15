package group4.hrms.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import group4.hrms.dao.AttachmentDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dto.RecruitmentDetailsDto;
import group4.hrms.model.Attachment;
import group4.hrms.model.Request;
import group4.hrms.model.RequestType;
import group4.hrms.util.RecruitmentPermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet xử lý việc tạo mới Recruitment Request (chỉ cho MANAGER)
 */
@WebServlet(name = "RecruitmentRequestCreateServlet", urlPatterns = {"/requests/recruitment/submit"})
public class RecruitmentRequestCreateServlet extends HttpServlet {

    private final RequestDao requestDao = new RequestDao();
    private final RequestTypeDao requestTypeDao = new RequestTypeDao();
    private final AttachmentDao attachmentDao = new AttachmentDao();
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
            // 1. HANDLE ATTACHMENT: Google Drive link only (optional)
            String attachmentPath = null;
            String driveLink = req.getParameter("driveLink");
            if (driveLink != null && !driveLink.trim().isEmpty()) {
                driveLink = driveLink.trim();
                // Validate Google Drive link format if provided
                if (!isValidGoogleDriveLink(driveLink)) {
                    preserveFormData(req);
                    setFieldError(req, "driveLink", "Invalid Google Drive link format");
                    req.setAttribute("error", "Invalid Google Drive link format. Please provide a valid shareable Google Drive link.");
                    req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                    return;
                }
                attachmentPath = driveLink;
            }

            // 2. GÁN DỮ LIỆU VÀO OBJECT CHI TIẾT (RecruitmentDetailsDto)
            RecruitmentDetailsDto details = new RecruitmentDetailsDto();
            details.setPositionCode(req.getParameter("positionCode"));
            details.setPositionName(req.getParameter("positionName"));

            // Parse jobLevel as String (DB stores as String: SENIOR, JUNIOR, etc.)
            String jobLevel = req.getParameter("jobLevel");
            if (jobLevel == null || jobLevel.trim().isEmpty()) {
                preserveFormData(req);
                setFieldError(req, "jobLevel", "Job level is required");
                req.setAttribute("error", "Job level is required");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            // Validate job level value
            if (!jobLevel.equals("JUNIOR") && !jobLevel.equals("MIDDLE") && !jobLevel.equals("SENIOR")) {
                preserveFormData(req);
                setFieldError(req, "jobLevel", "Invalid job level. Must be JUNIOR, MIDDLE, or SENIOR");
                req.setAttribute("error", "Invalid job level selected");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            details.setJobLevel(jobLevel);

            // Parse quantity
            String quantityStr = req.getParameter("quantity");
            if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                try {
                    details.setQuantity(Integer.parseInt(quantityStr.trim()));
                } catch (NumberFormatException nfe) {
                    preserveFormData(req);
                    setFieldError(req, "quantity", "Invalid quantity: must be a number");
                    req.setAttribute("error", "Invalid quantity: must be a number");
                    req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                    return;
                }
            }

            // Set jobType (FULL_TIME, PART_TIME, CONTRACT, etc.)
            String jobType = req.getParameter("jobType");
            if (jobType == null || jobType.trim().isEmpty()) {
                preserveFormData(req);
                setFieldError(req, "jobType", "Job type is required");
                req.setAttribute("error", "Job type is required");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            // Validate job type value
            if (!jobType.equals("Full-time") && !jobType.equals("Part-time") && !jobType.equals("Internship")) {
                preserveFormData(req);
                setFieldError(req, "jobType", "Invalid job type. Must be Full-time, Part-time, or Internship");
                req.setAttribute("error", "Invalid job type selected");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            details.setJobType(jobType);
            details.setRecruitmentReason(req.getParameter("recruitmentReason"));

            // Parse salary fields separately (stored as individual fields in DB)
            String minSalaryRaw = req.getParameter("minSalary");
            String maxSalaryRaw = req.getParameter("maxSalary");
            String salaryType = req.getParameter("salaryType");

            if (minSalaryRaw != null && !minSalaryRaw.trim().isEmpty()) {
                try {
                    // Remove commas before parsing
                    String cleanedMinSalary = minSalaryRaw.trim().replace(",", "");
                    details.setMinSalary(Double.parseDouble(cleanedMinSalary));
                } catch (NumberFormatException nfe) {
                    preserveFormData(req);
                    setFieldError(req, "minSalary", "Invalid minimum salary: must be a number");
                    req.setAttribute("error", "Invalid minimum salary: must be a number");
                    req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                    return;
                }
            }

            if (maxSalaryRaw != null && !maxSalaryRaw.trim().isEmpty()) {
                try {
                    // Remove commas before parsing
                    String cleanedMaxSalary = maxSalaryRaw.trim().replace(",", "");
                    details.setMaxSalary(Double.parseDouble(cleanedMaxSalary));
                } catch (NumberFormatException nfe) {
                    preserveFormData(req);
                    setFieldError(req, "maxSalary", "Invalid maximum salary: must be a number");
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

            // Basic validation (since validate() method is removed)
            String jobTitle = req.getParameter("jobTitle");
            if (jobTitle == null || jobTitle.trim().isEmpty()) {
                preserveFormData(req);
                setFieldError(req, "jobTitle", "Job title is required");
                req.setAttribute("error", "Job title is required");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            if (jobTitle.trim().length() < 3 || jobTitle.trim().length() > 100) {
                preserveFormData(req);
                setFieldError(req, "jobTitle", "Job title must be between 3 and 100 characters");
                req.setAttribute("error", "Job title must be between 3 and 100 characters");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            
            if (details.getPositionName() == null || details.getPositionName().trim().isEmpty()) {
                preserveFormData(req);
                setFieldError(req, "positionName", "Position name is required");
                req.setAttribute("error", "Position name is required");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            if (details.getPositionName().trim().length() < 3 || details.getPositionName().trim().length() > 100) {
                preserveFormData(req);
                setFieldError(req, "positionName", "Position name must be between 3 and 100 characters");
                req.setAttribute("error", "Position name must be between 3 and 100 characters");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            
            if (details.getJobSummary() == null || details.getJobSummary().trim().isEmpty()) {
                preserveFormData(req);
                setFieldError(req, "jobSummary", "Job summary is required");
                req.setAttribute("error", "Job summary is required");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            if (details.getJobSummary().trim().length() < 10 || details.getJobSummary().trim().length() > 1000) {
                preserveFormData(req);
                setFieldError(req, "jobSummary", "Job summary must be between 10 and 1000 characters");
                req.setAttribute("error", "Job summary must be between 10 and 1000 characters");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            
            if (details.getQuantity() == null || details.getQuantity() <= 0) {
                preserveFormData(req);
                setFieldError(req, "quantity", "Quantity must be greater than 0");
                req.setAttribute("error", "Quantity must be greater than 0");
                req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
                return;
            }
            if (details.getQuantity() > 100) {
                preserveFormData(req);
                setFieldError(req, "quantity", "Quantity cannot exceed 100");
                req.setAttribute("error", "Quantity cannot exceed 100");
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

            // 4. LƯU GOOGLE DRIVE LINK VÀO BẢNG ATTACHMENTS (nếu có)
            if (attachmentPath != null && !attachmentPath.trim().isEmpty()) {
                try {
                    Attachment attachment = new Attachment();
                    attachment.setOwnerType("REQUEST");
                    attachment.setOwnerId(request.getId());
                    attachment.setAttachmentType("LINK");
                    attachment.setExternalUrl(attachmentPath);
                    attachment.setOriginalName("Recruitment Supporting Document");
                    attachment.setContentType("application/link");
                    attachment.setSizeBytes(0L);
                    attachment.setUploadedByAccountId(accountId);
                    attachment.setCreatedAt(LocalDateTime.now());
                    
                    attachmentDao.save(attachment);
                    System.out.println("✓ Saved Google Drive link to attachments table: " + attachmentPath);
                } catch (Exception e) {
                    System.err.println("Warning: Failed to save attachment to database: " + e.getMessage());
                    // Don't fail the whole request if attachment save fails
                }
            }

            sendNotificationToHRAndHRM(req, request);
            // Truyền thông báo thành công và forward về trang create
            String successText = "Recruitment request submitted successfully! Request ID: " + request.getId();
            // Keep backward-compatible attribute name and also set 'success' which the JSP now expects
            req.setAttribute("successMessage", successText);
            req.setAttribute("success", successText);
            req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);

        } catch (Exception e) {
            System.err.println("Error in RecruitmentRequestCreateServlet: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
            preserveFormData(req);
            // Show more detailed error message
            String errorMsg = "Submission Failed: " + (e.getMessage() != null ? e.getMessage() : "Unknown error");
            req.setAttribute("error", errorMsg);
            req.getRequestDispatcher("/WEB-INF/views/recruitment/recruitment_request.jsp").forward(req, res);
        }
    }

    /**
     * Preserve form data to display back to user when there's an error
     */
    private void preserveFormData(HttpServletRequest req) {
        req.setAttribute("formData_jobTitle", req.getParameter("jobTitle"));
        req.setAttribute("formData_positionName", req.getParameter("positionName"));
        req.setAttribute("formData_jobSummary", req.getParameter("jobSummary"));
        req.setAttribute("formData_quantity", req.getParameter("quantity"));
        req.setAttribute("formData_jobLevel", req.getParameter("jobLevel"));
        req.setAttribute("formData_jobType", req.getParameter("jobType"));
        req.setAttribute("formData_recruitmentReason", req.getParameter("recruitmentReason"));
        req.setAttribute("formData_minSalary", req.getParameter("minSalary"));
        req.setAttribute("formData_maxSalary", req.getParameter("maxSalary"));
        req.setAttribute("formData_salaryType", req.getParameter("salaryType"));
        req.setAttribute("formData_workingLocation", req.getParameter("workingLocation"));
        req.setAttribute("formData_driveLink", req.getParameter("driveLink"));
        req.setAttribute("formData_attachmentType", req.getParameter("attachmentType"));
    }
    
    /**
     * Set field-specific error for display
     */
    private void setFieldError(HttpServletRequest req, String fieldName, String errorMessage) {
        java.util.Map<String, String> errors = (java.util.Map<String, String>) req.getAttribute("errors");
        if (errors == null) {
            errors = new java.util.HashMap<>();
            req.setAttribute("errors", errors);
        }
        errors.put(fieldName, errorMessage);
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
