package group4.hrms.email.scheduler;

import group4.hrms.email.dao.EmailQueueDao;
import group4.hrms.email.model.EmailQueue;
import group4.hrms.email.model.EmailStatus;
import group4.hrms.email.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Background scheduler để tự động gửi email từ queue
 * Chạy mỗi 30 giây
 * 
 * @author Group4
 */
@WebListener
public class EmailScheduler implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(EmailScheduler.class);

    private ScheduledExecutorService scheduler;
    private EmailSenderService emailSender;
    private EmailQueueDao queueDao;

    // Cấu hình
    private static final int INITIAL_DELAY = 10; // Delay 10 giây khi start
    private static final int PERIOD = 30; // Chạy mỗi 30 giây
    private static final int MAX_ATTEMPTS = 3; // Số lần thử tối đa
    private static final int BATCH_SIZE = 10; // Số email gửi mỗi lần

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("🚀 Starting Email Scheduler...");

        emailSender = new EmailSenderService();
        queueDao = new EmailQueueDao();

        // Test SMTP connection
        if (!emailSender.testConnection()) {
            logger.warn("⚠️ SMTP connection failed! Email scheduler will still run but emails may fail.");
        }

        // Tạo scheduler
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "EmailScheduler");
            thread.setDaemon(true);
            return thread;
        });

        // Schedule task
        scheduler.scheduleAtFixedRate(
                this::processEmailQueue,
                INITIAL_DELAY,
                PERIOD,
                TimeUnit.SECONDS);

        logger.info("✅ Email Scheduler started successfully! Running every {} seconds", PERIOD);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("🛑 Stopping Email Scheduler...");

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                logger.info("✅ Email Scheduler stopped successfully");
            } catch (InterruptedException e) {
                logger.error("Error stopping scheduler", e);
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Xử lý email queue - gửi các email đang pending
     */
    private void processEmailQueue() {
        try {
            // Lấy các email pending
            List<EmailQueue> pendingEmails = queueDao.findByStatus(EmailStatus.PENDING, BATCH_SIZE);

            if (pendingEmails.isEmpty()) {
                logger.debug("📭 No pending emails in queue");
                return;
            }

            logger.info("📧 Processing {} pending emails...", pendingEmails.size());

            int successCount = 0;
            int failCount = 0;

            for (EmailQueue email : pendingEmails) {
                // Kiểm tra số lần thử
                if (email.getAttempts() >= MAX_ATTEMPTS) {
                    logger.warn("⚠️ Email {} exceeded max attempts ({}), marking as FAILED",
                            email.getId(), MAX_ATTEMPTS);

                    email.setStatus(EmailStatus.FAILED);
                    email.setErrorMessage("Exceeded maximum retry attempts");
                    queueDao.update(email);
                    failCount++;
                    continue;
                }

                // Tăng số lần thử
                email.setAttempts(email.getAttempts() + 1);
                queueDao.update(email);

                // Gửi email
                boolean success = emailSender.sendEmail(email);

                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }

                // Delay nhỏ giữa các email để tránh spam
                Thread.sleep(1000);
            }

            logger.info("✅ Email processing completed: {} success, {} failed", successCount, failCount);

        } catch (SQLException e) {
            logger.error("❌ Database error while processing email queue: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("❌ Email processing interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("❌ Unexpected error while processing email queue: {}", e.getMessage(), e);
        }
    }

    /**
     * Retry các email failed (có thể gọi manually)
     */
    public void retryFailedEmails() {
        try {
            logger.info("🔄 Retrying failed emails...");

            List<EmailQueue> failedEmails = queueDao.findByStatus(EmailStatus.FAILED, BATCH_SIZE);

            for (EmailQueue email : failedEmails) {
                if (email.getAttempts() < MAX_ATTEMPTS) {
                    // Reset về PENDING để thử lại
                    email.setStatus(EmailStatus.PENDING);
                    email.setErrorMessage(null);
                    queueDao.update(email);
                    logger.info("Reset email {} to PENDING for retry", email.getId());
                }
            }

            logger.info("✅ Retry process completed for {} emails", failedEmails.size());

        } catch (SQLException e) {
            logger.error("❌ Error retrying failed emails: {}", e.getMessage(), e);
        }
    }
}
