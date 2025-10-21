package group4.hrms.util;

import group4.hrms.model.Position;
import group4.hrms.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class để quản lý permissions dựa trên position
 */
public class PermissionUtil {

    private static final Logger logger = LoggerFactory.getLogger(PermissionUtil.class);

    // Position codes
    public static final String POSITION_ADMIN = "ADMIN";
    public static final String POSITION_HR = "HR";
    public static final String POSITION_HRM = "HRM";
    public static final String POSITION_DEPT_MANAGER = "DEPT_MANAGER";

    // Permissions
    public static final String PERM_VIEW_ALL_USERS = "VIEW_ALL_USERS";
    public static final String PERM_VIEW_DEPT_USERS = "VIEW_DEPT_USERS";
    public static final String PERM_VIEW_ACCOUNTS = "VIEW_ACCOUNTS";
    public static final String PERM_CREATE_USER = "CREATE_USER";
    public static final String PERM_CREATE_ACCOUNT = "CREATE_ACCOUNT";
    public static final String PERM_RESET_PASSWORD = "RESET_PASSWORD";

    private PermissionUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Lấy position code từ position name
     */
    public static String getPositionCode(String positionName) {
        if (positionName == null) {
            return null;
        }

        String normalized = positionName.trim().toLowerCase();

        // Check for Admin or Administrator
        if (normalized.equals("admin") || normalized.equals("administrator")) {
            return POSITION_ADMIN;
        } else if (normalized.equals("hr manager") || normalized.equals("hr staff")) {
            return POSITION_HR;
        } else if (normalized.equals("hrm")) {
            return POSITION_HRM;
        } else if (normalized.contains("manager") || normalized.contains("dept manager")) {
            return POSITION_DEPT_MANAGER;
        }

        return null;
    }

    /**
     * Lấy position code của user hiện tại
     */
    public static String getCurrentUserPositionCode(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        // Check if user is admin by username (fallback for admin account without
        // position)
        Boolean isAdmin = (Boolean) session.getAttribute(SessionUtil.IS_ADMIN_KEY);
        if (Boolean.TRUE.equals(isAdmin)) {
            logger.debug("User is admin by session flag");
            return POSITION_ADMIN;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return null;
        }

        // If no position, check username
        if (user.getPositionId() == null) {
            String username = SessionUtil.getCurrentUsername(request);
            if ("admin".equalsIgnoreCase(username)) {
                logger.debug("User is admin by username");
                return POSITION_ADMIN;
            }
            return null;
        }

        // Get position from cache
        try {
            var positions = DropdownCacheUtil.getCachedPositions(request.getServletContext());
            var position = positions.stream()
                    .filter(p -> p.getId().equals(user.getPositionId()))
                    .findFirst();

            if (position.isPresent()) {
                String positionCode = getPositionCode(position.get().getName());
                logger.debug("User position: {} -> code: {}", position.get().getName(), positionCode);
                return positionCode;
            }
        } catch (Exception e) {
            logger.error("Error getting position code", e);
        }

        return null;
    }

    /**
     * Lấy department ID của user hiện tại
     */
    public static Long getCurrentUserDepartmentId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        User user = (User) session.getAttribute("user");
        return user != null ? user.getDepartmentId() : null;
    }

    /**
     * Kiểm tra user có permission không
     */
    public static boolean hasPermission(HttpServletRequest request, String permission) {
        String positionCode = getCurrentUserPositionCode(request);
        logger.debug("Checking permission: {} for position code: {}", permission, positionCode);

        if (positionCode == null) {
            logger.warn("Position code is null, denying permission: {}", permission);
            return false;
        }

        Set<String> permissions = getPermissionsForPosition(positionCode);
        boolean hasPermission = permissions.contains(permission);
        logger.debug("Position {} has permissions: {}, checking {}: {}",
                positionCode, permissions, permission, hasPermission);

        return hasPermission;
    }

    /**
     * Lấy danh sách permissions cho position
     */
    private static Set<String> getPermissionsForPosition(String positionCode) {
        Set<String> permissions = new HashSet<>();

        switch (positionCode) {
            case POSITION_ADMIN:
                // Admin có tất cả quyền
                permissions.addAll(Arrays.asList(
                        PERM_VIEW_ALL_USERS,
                        PERM_VIEW_ACCOUNTS,
                        PERM_CREATE_USER,
                        PERM_CREATE_ACCOUNT,
                        PERM_RESET_PASSWORD));
                break;

            case POSITION_HR:
            case POSITION_HRM:
                // HR và HRM xem được tất cả users nhưng không quản lý accounts
                permissions.add(PERM_VIEW_ALL_USERS);
                break;

            case POSITION_DEPT_MANAGER:
                // Department Manager chỉ xem được users trong phòng mình
                permissions.add(PERM_VIEW_DEPT_USERS);
                break;

            default:
                // Không có quyền gì
                break;
        }

        return permissions;
    }

    /**
     * Kiểm tra user có quyền xem user list không
     */
    public static boolean canViewUserList(HttpServletRequest request) {
        return hasPermission(request, PERM_VIEW_ALL_USERS) ||
                hasPermission(request, PERM_VIEW_DEPT_USERS);
    }

    /**
     * Kiểm tra user có quyền xem account list không
     */
    public static boolean canViewAccountList(HttpServletRequest request) {
        return hasPermission(request, PERM_VIEW_ACCOUNTS);
    }

    /**
     * Kiểm tra user có quyền tạo user không
     */
    public static boolean canCreateUser(HttpServletRequest request) {
        return hasPermission(request, PERM_CREATE_USER);
    }

    /**
     * Kiểm tra user có quyền tạo account không
     */
    public static boolean canCreateAccount(HttpServletRequest request) {
        return hasPermission(request, PERM_CREATE_ACCOUNT);
    }

    /**
     * Kiểm tra user có quyền reset password không
     */
    public static boolean canResetPassword(HttpServletRequest request) {
        return hasPermission(request, PERM_RESET_PASSWORD);
    }

    /**
     * Kiểm tra user có quyền xem tất cả users hay chỉ department của mình
     */
    public static boolean canViewAllUsers(HttpServletRequest request) {
        return hasPermission(request, PERM_VIEW_ALL_USERS);
    }

    /**
     * Kiểm tra user có quyền xem users trong department cụ thể không
     */
    public static boolean canViewDepartmentUsers(HttpServletRequest request, Long departmentId) {
        // Nếu có quyền xem tất cả users
        if (canViewAllUsers(request)) {
            return true;
        }

        // Nếu có quyền xem department users và đúng department của mình
        if (hasPermission(request, PERM_VIEW_DEPT_USERS)) {
            Long userDeptId = getCurrentUserDepartmentId(request);
            return userDeptId != null && userDeptId.equals(departmentId);
        }

        return false;
    }
}
