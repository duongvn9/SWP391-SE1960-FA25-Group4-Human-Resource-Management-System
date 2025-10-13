package group4.hrms.util;

import group4.hrms.model.Account;
import group4.hrms.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class để quản lý HTTP Session
 */
public class SessionUtil {

    private static final Logger logger = LoggerFactory.getLogger(SessionUtil.class);

    // Session attribute keys
    public static final String ACCOUNT_ID_KEY = "accountId";
    public static final String USER_ID_KEY = "userId";
    public static final String USERNAME_KEY = "username";
    public static final String USER_FULL_NAME_KEY = "userFullName";
    public static final String USER_EMAIL_KEY = "userEmail";
    public static final String USER_ROLES_KEY = "userRoles";
    public static final String IS_ADMIN_KEY = "isAdmin";
    public static final String LAST_LOGIN_TIME_KEY = "lastLoginTime";

    /**
     * Private constructor
     */
    private SessionUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Tạo session cho user sau khi đăng nhập thành công
     */
    public static void createUserSession(HttpServletRequest request, Account account, User user) {
        logger.info("=== START: Creating session for user: {} (Account ID: {}, User ID: {}) ===",
            account.getUsername(), account.getId(), user.getId());

        try {
            // Get or create session
            HttpSession session = request.getSession(true);
            logger.info("Session ID: {}", session.getId());
            logger.debug("Session is new: {}", session.isNew());

            // Configure session timeout
            int timeout = ConfigUtil.getSessionTimeout();
            session.setMaxInactiveInterval(timeout);
            logger.debug("Session timeout set to: {} seconds", timeout);

            // Set user information with detailed logging
            logger.debug("Setting ACCOUNT_ID_KEY...");
            session.setAttribute(ACCOUNT_ID_KEY, account.getId());
            logger.info("✓ Set ACCOUNT_ID_KEY = {}", account.getId());

            logger.debug("Setting USER_ID_KEY...");
            session.setAttribute(USER_ID_KEY, user.getId());
            logger.info("✓ Set USER_ID_KEY = {}", user.getId());

            logger.debug("Setting USERNAME_KEY...");
            session.setAttribute(USERNAME_KEY, account.getUsername());
            logger.info("✓ Set USERNAME_KEY = {}", account.getUsername());

            logger.debug("Setting USER_FULL_NAME_KEY...");
            session.setAttribute(USER_FULL_NAME_KEY, user.getFullName());
            logger.info("✓ Set USER_FULL_NAME_KEY = {}", user.getFullName());

            logger.debug("Setting USER_EMAIL_KEY...");
            session.setAttribute(USER_EMAIL_KEY, account.getEmailLogin());
            logger.info("✓ Set USER_EMAIL_KEY = {}", account.getEmailLogin());

            logger.debug("Setting LAST_LOGIN_TIME_KEY...");
            long loginTime = System.currentTimeMillis();
            session.setAttribute(LAST_LOGIN_TIME_KEY, loginTime);
            logger.info("✓ Set LAST_LOGIN_TIME_KEY = {}", loginTime);

            // Verify session attributes were set correctly
            logger.debug("Verifying session attributes...");
            Long verifyAccountId = (Long) session.getAttribute(ACCOUNT_ID_KEY);
            Long verifyUserId = (Long) session.getAttribute(USER_ID_KEY);
            String verifyUsername = (String) session.getAttribute(USERNAME_KEY);

            if (verifyAccountId == null || verifyUserId == null || verifyUsername == null) {
                logger.error("VERIFICATION FAILED! Some attributes are null after setting:");
                logger.error("  accountId: {}", verifyAccountId);
                logger.error("  userId: {}", verifyUserId);
                logger.error("  username: {}", verifyUsername);
                throw new RuntimeException("Session attributes verification failed");
            }

            logger.info("✓ Session attributes verified successfully");
            logger.info("=== SUCCESS: Session created for user: {} (Session ID: {}) ===",
                account.getUsername(), session.getId());

        } catch (Exception e) {
            logger.error("=== FAILED: Error creating session for user: {} ===", account.getUsername(), e);
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            throw new RuntimeException("Failed to create user session: " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra user có đăng nhập không
     */
    public static boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(ACCOUNT_ID_KEY) != null;
    }

    /**
     * Lấy Account ID từ session
     */
    public static Long getCurrentAccountId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Long) session.getAttribute(ACCOUNT_ID_KEY);
        }
        return null;
    }

    /**
     * Lấy User ID từ session
     */
    public static Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Long) session.getAttribute(USER_ID_KEY);
        }
        return null;
    }

    /**
     * Lấy username từ session
     */
    public static String getCurrentUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(USERNAME_KEY);
        }
        return null;
    }

    /**
     * Lấy full name từ session
     */
    public static String getCurrentUserFullName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(USER_FULL_NAME_KEY);
        }
        return null;
    }

    /**
     * Kiểm tra user có phải admin không
     */
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Boolean isAdmin = (Boolean) session.getAttribute(IS_ADMIN_KEY);
            return Boolean.TRUE.equals(isAdmin);
        }
        return false;
    }

    /**
     * Set admin flag trong session
     */
    public static void setAdminFlag(HttpServletRequest request, boolean isAdmin) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(IS_ADMIN_KEY, isAdmin);
        }
    }

    /**
     * Set user roles trong session
     */
    public static void setUserRoles(HttpServletRequest request, String roles) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(USER_ROLES_KEY, roles);
        }
    }

    /**
     * Lấy user roles từ session
     */
    public static String getUserRoles(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(USER_ROLES_KEY);
        }
        return null;
    }

    /**
     * Hủy session (logout)
     */
    public static void destroySession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = getCurrentUsername(request);
            session.invalidate();
            logger.info("Destroyed session for user: {}", username);
        }
    }

    /**
     * Kiểm tra session còn hiệu lực không
     */
    public static boolean isSessionValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        try {
            // Thử truy cập attribute để trigger exception nếu session invalid
            session.getAttribute(ACCOUNT_ID_KEY);
            return true;
        } catch (IllegalStateException e) {
            logger.debug("Session is invalid: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Refresh session timeout
     */
    public static void refreshSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(ConfigUtil.getSessionTimeout());
        }
    }
}