package group4.hrms.email.service;

import group4.hrms.email.dao.EmailQueueDao;
import group4.hrms.email.model.ContactRequest;
import group4.hrms.email.model.EmailEventType;
import group4.hrms.email.model.EmailQueue;
import group4.hrms.email.model.EmailTemplate;
import group4.hrms.model.Application;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Service chính để xử lý gửi email
 * Bao gồm các methods cho contact response, company notification và CV
 * confirmation
 * 
 * @author Group4
 */
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final EmailTemplateService templateService;
    private final EmailQueueDao queueDao;

    // Company email configuration
    private static final String COMPANY_EMAIL = "hrms8386@gmail.com";
    private static final String COMPANY_NAME = "HRMS System";

    // Date formatters
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EmailService() {
        this.templateService = new EmailTemplateService();
        this.queueDao = new EmailQueueDao();
    }

    public EmailService(EmailTemplateService templateService, EmailQueueDao queueDao) {
        this.templateService = templateService;
        this.queueDao = queueDao;
    }

    /**
     * Gửi email phản hồi cho người liên hệ
     * Requirements: 2.1, 2.2, 2.3
     * 
     * @param contact ContactRequest từ người dùng
     */
    public void sendContactResponse(ContactRequest contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact request không được null");
        }

        if (contact.getEmail() == null || contact.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được rỗng");
        }

        try {
            logger.info("Chuẩn bị gửi contact response email cho: {}", contact.getEmail());

            // Lấy template
            EmailTemplate template = templateService.getTemplate(EmailEventType.CONTACT_RESPONSE);

            // Chuẩn bị data cho template
            Map<String, Object> data = buildContactResponseData(contact);

            // Process template
            String content = templateService.processTemplate(template, data);

            // Tạo email queue
            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setRecipientEmail(contact.getEmail());
            emailQueue.setSubject(template.getSubject());
            emailQueue.setContent(content);
            emailQueue.setReferenceId(contact.getId());

            // Thêm vào queue
            EmailQueue queued = queueDao.save(emailQueue);

            logger.info("Đã thêm contact response email vào queue với ID: {}", queued.getId());

        } catch (SQLException e) {
            logger.error("Lỗi database khi gửi contact response: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi contact response email", e);
        } catch (Exception e) {
            logger.error("Lỗi khi gửi contact response: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi contact response email", e);
        }
    }

    /**
     * Gửi email thông báo cho công ty khi có contact request mới
     * Requirements: 4.1, 4.2, 4.3, 4.4
     * 
     * @param contact ContactRequest từ người dùng
     */
    public void sendCompanyNotification(ContactRequest contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact request không được null");
        }

        try {
            logger.info("Chuẩn bị gửi company notification cho contact ID: {}", contact.getId());

            // Lấy template
            EmailTemplate template = templateService.getTemplate(EmailEventType.COMPANY_NOTIFICATION);

            // Chuẩn bị data cho template
            Map<String, Object> data = buildCompanyNotificationData(contact);

            // Process template
            String content = templateService.processTemplate(template, data);

            // Tạo email queue
            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setRecipientEmail(COMPANY_EMAIL);
            emailQueue.setSubject("Liên hệ mới: " + contact.getSubject());
            emailQueue.setContent(content);
            emailQueue.setReferenceId(contact.getId());

            // Thêm vào queue
            EmailQueue queued = queueDao.save(emailQueue);

            logger.info("Đã thêm company notification vào queue với ID: {}", queued.getId());

        } catch (SQLException e) {
            logger.error("Lỗi database khi gửi company notification: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi company notification", e);
        } catch (Exception e) {
            logger.error("Lỗi khi gửi company notification: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi company notification", e);
        }
    }

    /**
     * Gửi email thông báo cho công ty khi có ứng viên nộp CV mới
     * 
     * @param application Application từ ứng viên
     */
    public void sendCvNotification(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("Application không được null");
        }

        // Kiểm tra nếu email ứng viên trùng với email công ty thì không gửi
        if (application.getEmail() != null
                && application.getEmail().trim().equalsIgnoreCase(COMPANY_EMAIL.trim())) {
            logger.info("Bỏ qua CV notification vì email ứng viên trùng với email công ty: {}",
                    application.getEmail());
            return;
        }

        try {
            logger.info("Chuẩn bị gửi CV notification cho công ty, application ID: {}", application.getId());

            // Lấy template
            EmailTemplate template = templateService.getTemplate(EmailEventType.CV_NOTIFICATION);

            // Chuẩn bị data cho template
            Map<String, Object> data = buildCvNotificationData(application);

            // Process template
            String content = templateService.processTemplate(template, data);

            // Tạo email queue
            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setRecipientEmail(COMPANY_EMAIL);
            emailQueue.setSubject("Ứng viên mới: " + application.getFullName() + " - "
                    + (application.getJobId() != null ? application.getJobId().toString() : "N/A"));
            emailQueue.setContent(content);
            emailQueue.setReferenceId(application.getId() != null ? application.getId().toString() : null);

            // Thêm vào queue
            EmailQueue queued = queueDao.save(emailQueue);

            logger.info("Đã thêm CV notification vào queue với ID: {}", queued.getId());

        } catch (SQLException e) {
            logger.error("Lỗi database khi gửi CV notification: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi CV notification", e);
        } catch (Exception e) {
            logger.error("Lỗi khi gửi CV notification: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi CV notification", e);
        }
    }

    /**
     * Gửi email xác nhận cho ứng viên khi nộp CV
     * Requirements: 1.1, 1.2, 1.3
     * 
     * @param application Application từ ứng viên
     */
    public void sendApplicationConfirmation(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("Application không được null");
        }

        if (application.getEmail() == null || application.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được rỗng");
        }

        try {
            logger.info("Chuẩn bị gửi CV confirmation email cho: {}", application.getEmail());

            // Lấy template
            EmailTemplate template = templateService.getTemplate(EmailEventType.CV_CONFIRMATION);

            // Chuẩn bị data cho template
            Map<String, Object> data = buildApplicationConfirmationData(application);

            // Process template
            String content = templateService.processTemplate(template, data);

            // Tạo email queue
            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setRecipientEmail(application.getEmail());
            emailQueue.setSubject(template.getSubject());
            emailQueue.setContent(content);
            emailQueue.setReferenceId(application.getId() != null ? application.getId().toString() : null);

            // Thêm vào queue
            EmailQueue queued = queueDao.save(emailQueue);

            logger.info("Đã thêm CV confirmation email vào queue với ID: {}", queued.getId());

        } catch (SQLException e) {
            logger.error("Lỗi database khi gửi CV confirmation: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi CV confirmation email", e);
        } catch (Exception e) {
            logger.error("Lỗi khi gửi CV confirmation: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi CV confirmation email", e);
        }
    }

    /**
     * Gửi email xác nhận cho ứng viên (overload cho test purposes)
     * 
     * @param testData Map chứa test data
     */
    public void sendApplicationConfirmation(Map<String, Object> testData) {
        if (testData == null) {
            throw new IllegalArgumentException("Test data không được null");
        }

        String recipientEmail = (String) testData.get("applicantEmail");
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được rỗng");
        }

        try {
            logger.info("Chuẩn bị gửi test CV confirmation email cho: {}", recipientEmail);

            // Lấy template
            EmailTemplate template = templateService.getTemplate(EmailEventType.CV_CONFIRMATION);

            // Chuẩn bị data cho template
            Map<String, Object> data = new HashMap<>();
            data.put("fullName", testData.getOrDefault("applicantName", "Test Applicant"));
            data.put("applicationId", testData.getOrDefault("applicationId", "TEST-APP"));
            data.put("jobId", testData.getOrDefault("jobId", "TEST-JOB"));
            data.put("email", recipientEmail);
            data.put("phone", testData.getOrDefault("phone", "Không cung cấp"));
            data.put("submittedDate", LocalDateTime.now().format(DATETIME_FORMATTER));
            data.put("estimatedTimeline", "7-14 ngày làm việc");
            data.put("nextSteps", "Chúng tôi sẽ xem xét hồ sơ của bạn và liên hệ nếu phù hợp");
            data.put("companyName", COMPANY_NAME);
            data.put("companyEmail", COMPANY_EMAIL);
            data.put("currentYear", String.valueOf(LocalDateTime.now().getYear()));

            // Process template
            String content = templateService.processTemplate(template, data);

            // Tạo email queue
            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setRecipientEmail(recipientEmail);
            emailQueue.setSubject(template.getSubject());
            emailQueue.setContent(content);
            emailQueue.setReferenceId((String) testData.get("applicationId"));

            // Thêm vào queue
            EmailQueue queued = queueDao.save(emailQueue);

            logger.info("Đã thêm test CV confirmation email vào queue với ID: {}", queued.getId());

        } catch (SQLException e) {
            logger.error("Lỗi database khi gửi test CV confirmation: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi test CV confirmation email", e);
        } catch (Exception e) {
            logger.error("Lỗi khi gửi test CV confirmation: {}", e.getMessage(), e);
            throw new EmailServiceException("Lỗi khi gửi test CV confirmation email", e);
        }
    }

    /**
     * Build data cho contact response email
     * 
     * @param contact ContactRequest
     * @return Map chứa data cho template
     */
    private Map<String, Object> buildContactResponseData(ContactRequest contact) {
        Map<String, Object> data = new HashMap<>();

        data.put("fullName", contact.getFullName());
        data.put("contactId", contact.getId());
        data.put("referenceId", contact.getId()); // Mã tham chiếu
        data.put("subject", contact.getSubject());
        data.put("contactType", contact.getContactType().name());
        data.put("contactTypeDisplay", getContactTypeDisplay(contact.getContactType().name()));
        data.put("submittedDate", contact.getCreatedAt().format(DATETIME_FORMATTER));
        data.put("expectedResponseTime", getExpectedResponseTime(contact.getContactType().name()));
        data.put("companyName", COMPANY_NAME);
        data.put("companyEmail", COMPANY_EMAIL);
        data.put("currentYear", String.valueOf(LocalDateTime.now().getYear()));

        return data;
    }

    /**
     * Build data cho company notification email
     * 
     * @param contact ContactRequest
     * @return Map chứa data cho template
     */
    private Map<String, Object> buildCompanyNotificationData(ContactRequest contact) {
        Map<String, Object> data = new HashMap<>();

        data.put("contactId", contact.getId());
        data.put("referenceId", contact.getId()); // Mã tham chiếu
        data.put("fullName", contact.getFullName());
        data.put("email", contact.getEmail());
        data.put("phone", contact.hasPhone() ? contact.getPhone() : "Không cung cấp");
        data.put("contactType", contact.getContactType().name());
        data.put("contactTypeDisplay", getContactTypeDisplay(contact.getContactType().name()));
        data.put("subject", contact.getSubject());
        data.put("message", contact.getMessage());
        data.put("submittedDate", contact.getCreatedAt().format(DATETIME_FORMATTER));
        data.put("isUrgent", contact.isUrgent() ? "CÓ" : "KHÔNG");
        data.put("urgentClass", contact.isUrgent() ? "urgent" : "normal");

        return data;
    }

    /**
     * Build data cho application confirmation email
     * 
     * @param application Application
     * @return Map chứa data cho template
     */
    private Map<String, Object> buildApplicationConfirmationData(Application application) {
        Map<String, Object> data = new HashMap<>();

        // Lấy job title từ database
        String jobTitle = getJobTitle(application.getJobId());

        data.put("applicantName", application.getFullName());
        data.put("applicationId", application.getId() != null ? application.getId().toString() : "N/A");
        data.put("jobPosition", jobTitle);
        data.put("applicantEmail", application.getEmail());
        data.put("applicantPhone", application.getPhone() != null ? application.getPhone() : "Không cung cấp");
        data.put("submittedDate", application.getCreatedAt().format(DATETIME_FORMATTER));
        data.put("companyName", COMPANY_NAME);
        data.put("companyEmail", COMPANY_EMAIL);
        data.put("currentYear", String.valueOf(LocalDateTime.now().getYear()));

        return data;
    }

    /**
     * Build data cho CV notification email (gửi cho công ty)
     * 
     * @param application Application
     * @return Map chứa data cho template
     */
    private Map<String, Object> buildCvNotificationData(Application application) {
        Map<String, Object> data = new HashMap<>();

        // Lấy job title từ database
        String jobTitle = getJobTitle(application.getJobId());

        data.put("applicationId", application.getId() != null ? application.getId().toString() : "N/A");
        data.put("applicantName", application.getFullName());
        data.put("applicantEmail", application.getEmail());
        data.put("applicantPhone", application.getPhone() != null ? application.getPhone() : "Không cung cấp");
        data.put("jobPosition", jobTitle);
        data.put("jobId", application.getJobId() != null ? application.getJobId().toString() : "N/A");
        data.put("submittedDate", application.getCreatedAt().format(DATETIME_FORMATTER));

        return data;
    }

    /**
     * Lấy job title từ database theo jobId
     * 
     * @param jobId Job ID
     * @return Job title hoặc "N/A" nếu không tìm thấy
     */
    private String getJobTitle(Long jobId) {
        if (jobId == null) {
            return "N/A";
        }

        String sql = "SELECT title FROM job_postings WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, jobId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("title");
                }
            }

        } catch (SQLException e) {
            logger.error("Lỗi khi lấy job title cho jobId {}: {}", jobId, e.getMessage(), e);
        }

        return "Job ID: " + jobId;
    }

    /**
     * Lấy display name cho contact type
     * 
     * @param contactType Contact type
     * @return Display name
     */
    private String getContactTypeDisplay(String contactType) {
        switch (contactType) {
            case "RECRUITMENT":
                return "Tuyển dụng";
            case "PARTNERSHIP":
                return "Hợp tác";
            case "COMPLAINT":
                return "Khiếu nại";
            case "OTHER":
                return "Khác";
            default:
                return contactType;
        }
    }

    /**
     * Lấy expected response time dựa trên contact type
     * 
     * @param contactType Contact type
     * @return Expected response time
     */
    private String getExpectedResponseTime(String contactType) {
        switch (contactType) {
            case "COMPLAINT":
                return "24 giờ";
            case "RECRUITMENT":
                return "2-3 ngày làm việc";
            case "PARTNERSHIP":
                return "3-5 ngày làm việc";
            default:
                return "3-5 ngày làm việc";
        }
    }

    /**
     * Custom exception cho EmailService
     */
    public static class EmailServiceException extends RuntimeException {
        public EmailServiceException(String message) {
            super(message);
        }

        public EmailServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
