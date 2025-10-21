package group4.hrms.listener;

import group4.hrms.service.JobPostingService;
import group4.hrms.service.DepartmentService;
import group4.hrms.service.impl.JobPostingServiceImpl;
import group4.hrms.service.impl.DepartmentServiceImpl;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener for initializing services
 */
@WebListener
public class ApplicationListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize services
        JobPostingService jobPostingService = new JobPostingServiceImpl();
        DepartmentService departmentService = new DepartmentServiceImpl();
        
        // Store services in ServletContext
        sce.getServletContext().setAttribute("jobPostingService", jobPostingService);
        sce.getServletContext().setAttribute("departmentService", departmentService);
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Clean up resources if needed
    }
}