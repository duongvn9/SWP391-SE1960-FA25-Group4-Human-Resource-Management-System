package group4.hrms.email.service;

import group4.hrms.email.dao.EmailQueueDao;
import group4.hrms.email.model.EmailQueue;
import group4.hrms.email.model.EmailStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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

    static {
        try {
            java.util.Properties props = new java.util.Properties();
            props.load(EmailSenderService.class.getClassLoader()
                    .getResourceAsStream("application.properties"));

            SMTP_HOST = props.getProperty("mail.smtp.host", "smtp.gmail.com").trim();
            SMTP_PORT = props.getProperty("mail.smtp.port", "587").trim();
            SMTP_USERNAME = props.getProperty("mail.username", "hrms8386@gmail.com").trim();
            SMTP_PASSWORD = props.getProperty("mail.password", "").trim();
            FROM_EMAIL = props.getProperty("mail.from.address", "hrms8386@gmail.com").trim();
            FROM_NAME = props.getProperty("mail.from.name", "HRMS System").trim();

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

            // Cập nhật status thành SENT
            emailQueue.setStatus(EmailStatus.SENT);
            emailQueue.setSentAt(LocalDateTime.now());
            queueDao.update(emailQueue);

            logger.info("✅ Email đã gửi thành công đến: {}", emailQueue.getRecipientEmail());
            return true;

        } catch (MessagingException e) {
            logger.error("❌ Lỗi khi gửi email đến {}: {}",
                    emailQueue.getRecipientEmail(), e.getMessage(), e);

            // Cập nhật status thành FAILED
            try {
                emailQueue.setStatus(EmailStatus.FAILED);
                emailQueue.setAttempts(emailQueue.getAttempts() + 1);
                emailQueue.setErrorMessage(e.getMessage());
                queueDao.update(emailQueue);
            } catch (SQLException ex) {
                logger.error("Lỗi khi cập nhật email queue: {}", ex.getMessage());
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
}
