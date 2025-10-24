package group4.hrms.listener;

import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.PositionDao;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.PositionService;
import group4.hrms.service.impl.DepartmentServiceImpl;
import group4.hrms.service.impl.JobPostingServiceImpl;
import group4.hrms.service.impl.PositionServiceImpl;
import group4.hrms.service.JobPostingService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ContextListener implements ServletContextListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ContextListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing application services...");
        
        try {
            // Initialize Services with their DAOs
            DepartmentDao departmentDao = new DepartmentDao();
            DepartmentService departmentService = new DepartmentServiceImpl(departmentDao);
            
            PositionDao positionDao = new PositionDao();
            PositionService positionService = new PositionServiceImpl(positionDao);
            
            // Create JobPostingService with proper dependency chain
            JobPostingService jobPostingService = new JobPostingServiceImpl(departmentService, positionService);
            
            // Set services in ServletContext
            sce.getServletContext().setAttribute("departmentService", departmentService);
            sce.getServletContext().setAttribute("jobPostingService", jobPostingService);
            sce.getServletContext().setAttribute("positionService", positionService);
            
            logger.info("Successfully initialized and registered all services");
        } catch (Exception e) {
            logger.error("Error initializing services", e);
            throw new RuntimeException("Failed to initialize application services", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application context being destroyed, cleaning up resources...");
    }
}
