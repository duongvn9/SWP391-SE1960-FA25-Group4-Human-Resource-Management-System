package group4.hrms.listener;

import group4.hrms.dao.HolidayCalendarDao;
import group4.hrms.dao.HolidayDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.util.ChatbotContextLoader;
import group4.hrms.util.HolidayGenerator;
import group4.hrms.util.RequestTypeInitializer;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application startup listener
 * Initializes required data when the application starts
 */
@WebListener
public class AppStartupListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppStartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Application starting up...");

        try {
            // Initialize request types
            RequestTypeDao requestTypeDao = new RequestTypeDao();
            RequestTypeInitializer initializer = new RequestTypeInitializer(requestTypeDao);
            initializer.initializeCommonRequestTypes();

            // Generate holidays for 2025-2030 (if not already generated)
            logger.info("Checking holidays for years 2025-2030...");
            HolidayCalendarDao calendarDao = new HolidayCalendarDao();
            HolidayDao holidayDao = new HolidayDao();
            HolidayGenerator generator = new HolidayGenerator(calendarDao, holidayDao);
            generator.generateHolidaysForYears(2025, 2030);
            logger.info("Holiday check completed");

            // Load chatbot context from chatbot-qa.json
            logger.info("Loading chatbot context...");
            ChatbotContextLoader.getContext();
            logger.info("Chatbot context loaded successfully");

            logger.info("Application startup completed successfully");

        } catch (Exception e) {
            logger.error("Error during application startup", e);
            // Don't prevent app from starting, but log the error
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down...");

        try {
            // Shutdown HikariCP connection pool
            group4.hrms.util.DatabaseUtil.shutdown();
            logger.info("Database connection pool shutdown completed");
        } catch (Exception e) {
            logger.error("Error during application shutdown", e);
        }
    }
}
