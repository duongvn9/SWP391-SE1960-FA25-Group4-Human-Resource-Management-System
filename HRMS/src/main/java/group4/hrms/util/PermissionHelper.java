package group4.hrms.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * Helper class for JSP to check permissions
 * Usage in JSP: ${PermissionHelper.canViewUserList(pageContext.request)}
 */
public class PermissionHelper {

    private PermissionHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Check if user can view user list
     */
    public static boolean canViewUserList(HttpServletRequest request) {
        return PermissionUtil.canViewUserList(request);
    }

    /**
     * Check if user can view account list
     */
    public static boolean canViewAccountList(HttpServletRequest request) {
        return PermissionUtil.canViewAccountList(request);
    }

    /**
     * Check if user can create user
     */
    public static boolean canCreateUser(HttpServletRequest request) {
        return PermissionUtil.canCreateUser(request);
    }

    /**
     * Check if user can create account
     */
    public static boolean canCreateAccount(HttpServletRequest request) {
        return PermissionUtil.canCreateAccount(request);
    }

    /**
     * Check if user can reset password
     */
    public static boolean canResetPassword(HttpServletRequest request) {
        return PermissionUtil.canResetPassword(request);
    }

    /**
     * Check if user can view all users
     */
    public static boolean canViewAllUsers(HttpServletRequest request) {
        return PermissionUtil.canViewAllUsers(request);
    }
}
