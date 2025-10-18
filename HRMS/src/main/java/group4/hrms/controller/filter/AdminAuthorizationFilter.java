package group4.hrms.controller.filter;

import group4.hrms.model.User;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Filter to check if user has admin role (position_id = 6) for admin-only pages
 * This filter runs AFTER AuthenticationFilter
 */
@WebFilter(
    urlPatterns = {"/settings", "/settings/*"},
    filterName = "AdminAuthorizationFilter"
)
public class AdminAuthorizationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AdminAuthorizationFilter.class);
    private static final Long ADMIN_POSITION_ID = 6L;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("========================================");
        logger.info("AdminAuthorizationFilter INITIALIZED");
        logger.info("Protecting /settings/* for admin only (position_id = 6)");
        logger.info("========================================");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        logger.info("========================================");
        logger.info("!!! AdminAuthorizationFilter TRIGGERED !!!");
        logger.info("Request URI: {}", requestURI);
        logger.info("========================================");

        // Check if user is logged in
        if (!SessionUtil.isUserLoggedIn(httpRequest)) {
            logger.warn("User not logged in, redirecting to login");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Get user from session
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            logger.warn("No session found, redirecting to login");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            logger.error("!!! User object not found in session, redirecting to login");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Check if user is admin (position_id = 6)
        Long positionId = user.getPositionId();
        logger.info("!!! ADMIN CHECK: User={}, PositionId={}, RequiredPositionId={}", 
                   user.getFullName(), positionId, ADMIN_POSITION_ID);
        
        if (positionId == null || !positionId.equals(ADMIN_POSITION_ID)) {
            logger.error("!!! ACCESS DENIED for user {} (position_id: {}) - Admin only (position_id: 6)", 
                       user.getFullName(), positionId);
            
            // Send 403 Forbidden with error page
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpRequest.setAttribute("errorTitle", "Access Denied");
            httpRequest.setAttribute("errorMessage", "You do not have permission to access this page. Only administrators can access Settings.");
            httpRequest.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(httpRequest, httpResponse);
            return;
        }

        // User is admin, allow access
        logger.debug("Admin access granted for user: {} (position_id: {})", user.getFullName(), positionId);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AdminAuthorizationFilter destroyed");
    }
}
