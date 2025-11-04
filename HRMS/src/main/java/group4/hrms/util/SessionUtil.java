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
    public static final String USER_POSITION_ID_KEY = "userPositionId";
    public static final String USER_DEPARTMENT_ID_KEY = "userDepartmentId";
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
        HttpSession session = request.getSession(true);

        // Configure session
        int timeout = ConfigUtil.getSessionTimeout();
        session.setMaxInactiveInterval(timeout);

        logger.info("Creating session with timeout: {} seconds ({} minutes)", timeout, timeout / 60);

        // Set user information (primitive values for backward compatibility)
        session.setAttribute(ACCOUNT_ID_KEY, account.getId());
        session.setAttribute(USER_ID_KEY, user.getId());
        session.setAttribute(USERNAME_KEY, account.getUsername());
        session.setAttribute(USER_FULL_NAME_KEY, user.getFullName());
        session.setAttribute(USER_EMAIL_KEY, account.getEmailLogin());
        session.setAttribute(USER_POSITION_ID_KEY, user.getPositionId());
        session.setAttribute(USER_DEPARTMENT_ID_KEY, user.getDepartmentId());
        session.setAttribute(LAST_LOGIN_TIME_KEY, System.currentTimeMillis());

        // Set full objects (needed by controllers)
        session.setAttribute("account", account);
        session.setAttribute("user", user);

        // Set admin flag based on position (check if user has Administrator position)
        boolean isAdmin = false;
        if (user.getPositionId() != null) {
            try {
                // Get position from cache to check if it's Administrator
                var positions = DropdownCacheUtil.getCachedPositions(request.getServletContext());
                var position = positions.stream()
                        .filter(p -> p.getId().equals(user.getPositionId()))
                        .findFirst();

                if (position.isPresent()) {
                    String positionName = position.get().getName();
                    // Check if position is Administrator or Admin
                    isAdmin = "Administrator".equalsIgnoreCase(positionName) ||
                            "Admin".equalsIgnoreCase(positionName);
                    logger.debug("User position: {}, isAdmin: {}", positionName, isAdmin);
                }
            } catch (Exception e) {
                logger.error("Error checking admin position", e);
                // Fallback to username check for backward compatibility
                isAdmin = "admin".equalsIgnoreCase(account.getUsername());
            }
        } else {
            // Fallback to username check if no position
            isAdmin = "admin".equalsIgnoreCase(account.getUsername());
        }

        session.setAttribute(IS_ADMIN_KEY, isAdmin);

        // Set roles string (temporary solution)
        if (isAdmin) {
            session.setAttribute(USER_ROLES_KEY, "ADMIN");
        }

        logger.info("Created session for user: {} (ID: {}), Session ID: {}, isAdmin: {}",
                account.getUsername(), account.getId(), session.getId(), isAdmin);
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
     * Lấy User object từ session (tạo từ session attributes)
     */
    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute(USER_ID_KEY);
            String fullName = (String) session.getAttribute(USER_FULL_NAME_KEY);
            String email = (String) session.getAttribute(USER_EMAIL_KEY);
            Long positionId = (Long) session.getAttribute(USER_POSITION_ID_KEY);
            Long departmentId = (Long) session.getAttribute(USER_DEPARTMENT_ID_KEY);

            if (userId != null) {
                User user = new User();
                user.setId(userId);
                user.setFullName(fullName);
                user.setEmailCompany(email);
                user.setPositionId(positionId);
                user.setDepartmentId(departmentId);
                return user;
            }
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

    /**
     * Kiểm tra user có role cụ thể không
     *
     * @param request HttpServletRequest
     * @param role    Role cần kiểm tra (ADMIN, HRM, HR, MANAGER, EMPLOYEE)
     * @return true nếu user có role đó
     */
    public static boolean hasRole(HttpServletRequest request, String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }

        // Check admin flag first
        if ("ADMIN".equalsIgnoreCase(role) && isAdmin(request)) {
            return true;
        }

        // Check roles string
        String roles = getUserRoles(request);
        if (roles == null || roles.trim().isEmpty()) {
            return false;
        }

        // Check if role exists in roles string (comma-separated)
        String[] roleArray = roles.split(",");
        for (String r : roleArray) {
            if (role.equalsIgnoreCase(r.trim())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Kiểm tra user có bất kỳ role nào trong danh sách không
     *
     * @param request HttpServletRequest
     * @param roles   Danh sách roles cần kiểm tra
     * @return true nếu user có ít nhất một role trong danh sách
     */
    public static boolean hasAnyRole(HttpServletRequest request, String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        for (String role : roles) {
            if (hasRole(request, role)) {
                return true;
            }
        }

        return false;
    }
}