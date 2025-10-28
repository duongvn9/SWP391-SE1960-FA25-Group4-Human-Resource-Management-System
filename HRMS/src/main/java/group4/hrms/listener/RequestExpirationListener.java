package group4.hrms.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import group4.hrms.dao.RequestDao;
import group4.hrms.service.RequestExpirationService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Listener that handles request expiration on application lifecycle events.
 *
 * On Application Startup:
 * - Runs once immediately to auto-reject any expired PENDING requests
 * - Starts a scheduled task to run daily at midnight
 *
 * On Application Shutdown:
 * - Stops the scheduled task gracefully
 *
 * This ensures that expired requests (OT/Leave past their effective date)
 * are automatically rejected and won't remain in PENDING status indefinitely.
 */
@WebListener
public class RequestExpirationListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(RequestExpirationListener.class.getName());
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("=== Request Expiration Listener: Application Starting ===");

        try {
            // Immediately check and auto-reject expired requests on startup
            logger.info("Running initial check for expired requests...");
            RequestExpirationService service = new RequestExpirationService(new RequestDao());
            int rejectedCount = service.processExpiredRequests();
            logger.info(String.format("Initial check complete. Auto-rejected %d expired requests.", rejectedCount));

            // Start scheduled task to run daily at midnight
            startScheduledTask();
            logger.info("Scheduled task started: Will check for expired requests daily at midnight.");

        } catch (Exception e) {
            logger.severe("Error initializing Request Expiration Listener: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== Request Expiration Listener: Application Shutting Down ===");

        if (scheduler != null && !scheduler.isShutdown()) {
            logger.info("Stopping scheduled expiration check task...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                logger.info("Scheduled task stopped successfully.");
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Start a scheduled task that runs daily to check for expired requests.
     * Uses ScheduledExecutorService to run at fixed rate.
     */
    private void startScheduledTask() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "RequestExpirationChecker");
            thread.setDaemon(true); // Daemon thread won't prevent JVM shutdown
            return thread;
        });

        // Calculate initial delay to run at next midnight
        long initialDelay = getMillisUntilMidnight();
        long period = TimeUnit.DAYS.toMillis(1); // Run every 24 hours

        scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.info("=== Scheduled Request Expiration Check Starting ===");
                RequestExpirationService service = new RequestExpirationService(new RequestDao());
                int rejectedCount = service.processExpiredRequests();
                logger.info(String.format("=== Scheduled Check Complete. Auto-rejected %d expired requests. ===",
                           rejectedCount));
            } catch (Exception e) {
                logger.severe("Error in scheduled expiration check: " + e.getMessage());
                e.printStackTrace();
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);

        logger.info(String.format("Scheduled task configured: initial delay = %.2f hours, period = 24 hours",
                   initialDelay / 3600000.0));
    }

    /**
     * Calculate milliseconds until next midnight (00:00:00).
     *
     * @return Milliseconds from now until next midnight
     */
    private long getMillisUntilMidnight() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return java.time.Duration.between(now, nextMidnight).toMillis();
    }
}

