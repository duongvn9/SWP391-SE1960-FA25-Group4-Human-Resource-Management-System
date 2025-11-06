package group4.hrms.email.service;

import group4.hrms.email.dao.EmailQueueDao;
import group4.hrms.email.model.EmailQueue;
import group4.hrms.email.model.EmailStatus;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * Service để gửi email thật qua SMTP
 * 
 * @author Group4
 */
public class EmailSenderService {
    private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class);

    private final EmailQueueDao queueDao;

    // SMTP Configuration - Đọc từ application.properties
    private static String SMTP_HOST;
    private static String SMTP_PORT;
    private static String SMTP_USERNAME;
    private static String SMTP_PASSWORD;
    private static String FROM_EMAIL;
    private static String FROM_NAME;

    // Email Settings - Đọc từ application.properties
    private static int MAX_RETRY_ATTEMPTS;
    private static int CONNECTION_TIMEOUT;
    private static int READ_TIMEOUT;

    static {
        try {
            java.util.Properties props = new java.util.Properties();
            props.load(EmailSenderService.class.getClassLoader()
                    .getResourceAsStream("application.properties"));

            // SMTP Configuration
            SMTP_HOST = props.getProperty("mail.smtp.host", "smtp.gmail.com").trim();
            SMTP_PORT = props.getProperty("mail.smtp.port", "587").trim();
            SMTP_USERNAME = props.getProperty("mail.username", "hrms8386@gmail.com").trim();
            SMTP_PASSWORD = props.getProperty("mail.password", "").trim();
            FROM_EMAIL = props.getProperty("mail.from.address", "hrms8386@gmail.com").trim();
            FROM_NAME = props.getProperty("mail.from.name", "HRMS System").trim();

            // Email Settings
            MAX_RETRY_ATTEMPTS = Integer.parseInt(props.getProperty("email.retry.max-attempts", "3"));
            CONNECTION_TIMEOUT = Integer.parseInt(props.getProperty("email.timeout.connection", "10000"));
            READ_TIMEOUT = Integer.parseInt(props.getProperty("email.timeout.read", "10000"));

            LoggerFactory.getLogger(EmailSenderService.class)
                    .info("Email configuration loaded: host={}, port={}, maxRetry={}, connTimeout={}ms, readTimeout={}ms",
                            SMTP_HOST, SMTP_PORT, MAX_RETRY_ATTEMPTS, CONNECTION_TIMEOUT, READ_TIMEOUT);

        } catch (Exception e) {
            LoggerFactory.getLogger(EmailSenderService.class)
                    .error("Failed to load email configuration from properties file", e);
            // Fallback values
            SMTP_HOST = "smtp.gmail.com";
            SMTP_PORT = "587";
            SMTP_USERNAME = "hrms8386@gmail.com";
            SMTP_PASSWORD = "";
            FROM_EMAIL = "hrms8386@gmail.com";
            FROM_NAME = "HRMS System";
            MAX_RETRY_ATTEMPTS = 3;
            CONNECTION_TIMEOUT = 30000; // 30 seconds - match với application.properties
            READ_TIMEOUT = 30000; // 30 seconds - match với application.properties
        }
    }

    private final Session mailSession;

    public EmailSenderService() {
        this.queueDao = new EmailQueueDao();
        this.mailSession = createMailSession();
    }

    public EmailSenderService(EmailQueueDao queueDao) {
        this.queueDao = queueDao;
        this.mailSession = createMailSession();
    }

    /**
     * Tạo mail session với SMTP configuration
     */
    private Session createMailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        // Timeout configuration từ application.properties
        props.put("mail.smtp.connectiontimeout", String.valueOf(CONNECTION_TIMEOUT));
        props.put("mail.smtp.timeout", String.valueOf(READ_TIMEOUT));
        props.put("mail.smtp.writetimeout", String.valueOf(READ_TIMEOUT));

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
    }

    /**
     * Gửi một email từ queue
     * 
     * @param emailQueue Email cần gửi
     * @return true nếu gửi thành công
     */
    public boolean sendEmail(EmailQueue emailQueue) {
        if (emailQueue == null) {
            logger.error("EmailQueue is null");
            return false;
        }

        try {
            logger.info("Đang gửi email đến: {}", emailQueue.getRecipientEmail());

            // Tạo message
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME, "UTF-8"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailQueue.getRecipientEmail()));
            message.setSubject(emailQueue.getSubject(), "UTF-8");
            message.setContent(emailQueue.getContent(), "text/html; charset=UTF-8");

            // Gửi email
            Transport.send(message);
            logger.info("✅ Email đã gửi thành công qua SMTP đến: {}", emailQueue.getRecipientEmail());

            // Cập nhật status thành SENT bằng raw SQL với retry logic
            boolean dbUpdateSuccess = false;
            int retryCount = 0;
            int maxRetries = MAX_RETRY_ATTEMPTS; // Đọc từ application.properties

            while (!dbUpdateSuccess && retryCount < maxRetries) {
                try {
                    logger.info("Chuẩn bị update status SENT cho email ID: {} (attempt {}/{})",
                            emailQueue.getId(), retryCount + 1, maxRetries);

                    String updateSql = "UPDATE email_queue SET status = 'SENT', sent_at = NOW() WHERE id = ?";

                    try (Connection conn = DatabaseUtil.getConnection()) {
                        // Đảm bảo autoCommit = true để commit ngay lập tức
                        conn.setAutoCommit(true);

                        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                            stmt.setLong(1, emailQueue.getId());
                            int rows = stmt.executeUpdate();

                            if (rows > 0) {
                                logger.info("✅ Đã cập nhật status SENT cho email ID: {} - {} rows affected",
                                        emailQueue.getId(), rows);

                                // Update local object
                                emailQueue.setStatus(EmailStatus.SENT);
                                emailQueue.setSentAt(LocalDateTime.now());
                                dbUpdateSuccess = true;
                            } else {
                                logger.warn("⚠️ Update không ảnh hưởng row nào cho email ID: {}", emailQueue.getId());
                            }
                        }
                    }

                } catch (SQLException ex) {
                    retryCount++;
                    logger.error("❌ Lỗi SQLException khi cập nhật status SENT cho email {} (attempt {}/{}): {}",
                            emailQueue.getId(), retryCount, maxRetries, ex.getMessage());

                    if (retryCount >= maxRetries) {
                        logger.error("❌ ĐÃ HẾT SỐ LẦN RETRY! Email đã gửi nhưng DB không update được cho ID: {}",
                                emailQueue.getId());
                        ex.printStackTrace();
                    } else {
                        // Đợi một chút trước khi retry
                        try {
                            Thread.sleep(100 * retryCount); // 100ms, 200ms, 300ms
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } catch (Exception ex) {
                    retryCount++;
                    logger.error("❌ Lỗi Exception khi cập nhật status SENT cho email {} (attempt {}/{}): {}",
                            emailQueue.getId(), retryCount, maxRetries, ex.getMessage());
                    ex.printStackTrace();

                    if (retryCount >= maxRetries) {
                        logger.error("❌ ĐÃ HẾT SỐ LẦN RETRY! Email đã gửi nhưng DB không update được cho ID: {}",
                                emailQueue.getId());
                    }
                }
            }

            if (!dbUpdateSuccess) {
                logger.error(
                        "❌ CRITICAL: Email ID {} đã gửi thành công nhưng DB status KHÔNG được update sau {} lần thử!",
                        emailQueue.getId(), maxRetries);
            }

            logger.info("✅ Email đã gửi thành công đến: {}", emailQueue.getRecipientEmail());
            return true;

        } catch (MessagingException e) {
            logger.error("❌ Lỗi khi gửi email đến {}: {}",
                    emailQueue.getRecipientEmail(), e.getMessage(), e);

            // Cập nhật status thành FAILED bằng raw SQL với retry logic
            boolean dbUpdateSuccess = false;
            int retryCount = 0;
            int maxRetries = MAX_RETRY_ATTEMPTS; // Đọc từ application.properties

            while (!dbUpdateSuccess && retryCount < maxRetries) {
                try {
                    String updateSql = "UPDATE email_queue SET status = 'FAILED', retry_count = retry_count + 1, error_message = ? WHERE id = ?";

                    try (Connection conn = DatabaseUtil.getConnection()) {
                        // Đảm bảo autoCommit = true
                        conn.setAutoCommit(true);

                        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                            stmt.setString(1, e.getMessage());
                            stmt.setLong(2, emailQueue.getId());
                            int rows = stmt.executeUpdate();

                            if (rows > 0) {
                                logger.info("✅ Đã cập nhật status FAILED cho email ID: {} - {} rows affected",
                                        emailQueue.getId(), rows);

                                // Update local object
                                emailQueue.setStatus(EmailStatus.FAILED);
                                emailQueue.setRetryCount(emailQueue.getRetryCount() + 1);
                                emailQueue.setErrorMessage(e.getMessage());
                                dbUpdateSuccess = true;
                            }
                        }
                    }

                } catch (SQLException ex) {
                    retryCount++;
                    logger.error("❌ Lỗi khi cập nhật status FAILED cho email {} (attempt {}/{}): {}",
                            emailQueue.getId(), retryCount, maxRetries, ex.getMessage());

                    if (retryCount >= maxRetries) {
                        logger.error("❌ CRITICAL: Không thể update status FAILED cho email ID {} sau {} lần thử!",
                                emailQueue.getId(), maxRetries);
                        ex.printStackTrace();
                    } else {
                        try {
                            Thread.sleep(100 * retryCount);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            return false;

        } catch (Exception e) {
            logger.error("❌ Lỗi không xác định khi gửi email: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gửi email test
     * 
     * @param toEmail Email người nhận
     * @param subject Tiêu đề
     * @param content Nội dung HTML
     * @return true nếu gửi thành công
     */
    public boolean sendTestEmail(String toEmail, String subject, String content) {
        try {
            logger.info("Đang gửi test email đến: {}", toEmail);

            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME, "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject, "UTF-8");
            message.setContent(content, "text/html; charset=UTF-8");

            Transport.send(message);

            logger.info("✅ Test email đã gửi thành công đến: {}", toEmail);
            return true;

        } catch (Exception e) {
            logger.error("❌ Lỗi khi gửi test email: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Kiểm tra SMTP configuration có hoạt động không
     * 
     * @return true nếu kết nối thành công
     */
    public boolean testConnection() {
        try {
            logger.info("Đang test SMTP connection...");

            Transport transport = mailSession.getTransport("smtp");
            transport.connect(SMTP_HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.close();

            logger.info("✅ SMTP connection thành công!");
            return true;

        } catch (Exception e) {
            logger.error("❌ SMTP connection thất bại: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verify status của email trong database
     * Method này dùng để debug và kiểm tra xem status có được update đúng không
     * 
     * @param emailId ID của email cần kiểm tra
     * @return Status hiện tại trong DB, hoặc null nếu không tìm thấy
     */
    public String verifyEmailStatus(Long emailId) {
        if (emailId == null) {
            return null;
        }

        try {
            String sql = "SELECT status FROM email_queue WHERE id = ?";

            try (Connection conn = DatabaseUtil.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setLong(1, emailId);

                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("status");
                        logger.debug("Email ID {} has status in DB: {}", emailId, status);
                        return status;
                    }
                }
            }

            logger.warn("Email ID {} not found in database", emailId);
            return null;

        } catch (SQLException e) {
            logger.error("Error verifying email status for ID {}: {}", emailId, e.getMessage(), e);
            return null;
        }
    }
}
