package group4.hrms.controller.filter;

import group4.hrms.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter để kiểm tra authentication cho các trang protected
 */
@WebFilter(urlPatterns = {
    "/dashboard/*",
    "/profile/*",
    "/request/*",
    "/recruitment/*",
    "/attendance/*",
    "/payroll/*",
    "/payslips",
    "/settings/*",
    "/admin/*"
})
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        logger.debug("Checking authentication for: {}", requestURI);

        // Skip authentication for login-related URLs
        if (isLoginRelatedUrl(requestURI, contextPath)) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is logged in
        if (!SessionUtil.isUserLoggedIn(httpRequest)) {
            logger.debug("User not logged in, redirecting to login page");

            // Store original URL for redirect after login
            String originalUrl = requestURI;
            if (httpRequest.getQueryString() != null) {
                originalUrl += "?" + httpRequest.getQueryString();
            }
            httpRequest.getSession(true).setAttribute("originalUrl", originalUrl);

            // Redirect to login page
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // Check if session is still valid
        if (!SessionUtil.isSessionValid(httpRequest)) {
            logger.debug("Session is invalid, redirecting to login page");
            SessionUtil.destroySession(httpRequest);
            httpResponse.sendRedirect(contextPath + "/login?sessionExpired=true");
            return;
        }

        // Refresh session timeout
        SessionUtil.refreshSession(httpRequest);

        // User is authenticated, continue with the request
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed");
    }

    /**
     * Check if URL is login-related and should skip authentication
     */
    private boolean isLoginRelatedUrl(String requestURI, String contextPath) {
        String[] loginUrls = {
            "/login",
            "/auth/login",
            "/login-google",
            "/auth/logout",
            "/",
            "/index.jsp",
            "/assets/",
            "/favicon.ico"
        };

        for (String url : loginUrls) {
            if (requestURI.equals(contextPath + url) ||
                requestURI.startsWith(contextPath + url)) {
                return true;
            }
        }

        return false;
    }
}