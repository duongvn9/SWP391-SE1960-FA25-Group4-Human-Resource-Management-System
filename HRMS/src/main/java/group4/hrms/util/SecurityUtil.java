package group4.hrms.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for security-related operations
 */
public class SecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Check if the current user has a specific role
     */
    public static boolean hasRole(HttpServletRequest request, String role) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        String userRole = (String) session.getAttribute("userRole");
        return role != null && role.equals(userRole);
    }
    
    /**
     * Get logged in account ID from session
     */
    public static long getAccountId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalStateException("No active session");
        }
        
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) {
            throw new IllegalStateException("No account ID in session");
        }
        
        return accountId;
    }
    
    /**
     * Get logged in user ID from session
     */
    public static Long getLoggedInUserId(HttpSession session) {
        if (session == null) return null;
        return (Long) session.getAttribute("accountId");
    }
    
    /**
     * Generate CSRF token
     */
    public static String generateCsrfToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    /**
     * Generate CSRF token and store in session
     */
    public static String generateCsrfToken(HttpSession session) {
        String token = generateCsrfToken();
        if (session != null) {
            session.setAttribute("csrfToken", token);
        }
        return token;
    }
    
    /**
     * Get CSRF token from session
     */
    public static String getCsrfToken(HttpSession session) {
        if (session == null) return null;
        String token = (String) session.getAttribute("csrfToken");
        if (token == null) {
            token = generateCsrfToken(session);
        }
        return token;
    }
    
    /**
     * Validate CSRF token from request against session
     */
    public static boolean verifyCsrfToken(HttpSession session, String requestToken) {
        if (session == null || requestToken == null) return false;
        String sessionToken = (String) session.getAttribute("csrfToken");
        return sessionToken != null && sessionToken.equals(requestToken);
    }
    
    /**
     * Validate CSRF token from request parameters
     */
    public static boolean isValidCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");
        
        if (sessionToken == null || requestToken == null) {
            return false;
        }
        
        return sessionToken.equals(requestToken);
    }
    
    /**
     * Generate and set CSRF token in session
     */
    public static String initCsrfToken(HttpServletRequest request) {
        String token = generateCsrfToken();
        request.getSession().setAttribute("csrfToken", token);
        return token;
    }
    
    private SecurityUtil() {
        // Prevent instantiation
    }
}