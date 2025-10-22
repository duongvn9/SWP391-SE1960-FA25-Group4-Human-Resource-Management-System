package group4.hrms.controller;

import group4.hrms.model.User;
import group4.hrms.util.PermissionUtil;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Debug servlet để kiểm tra permissions
 * URL: /debug/permissions
 */
@WebServlet("/debug/permissions")
public class DebugPermissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Permission Debug</title>");
        out.println(
                "<style>body{font-family:monospace;padding:20px;} table{border-collapse:collapse;} td,th{border:1px solid #ccc;padding:8px;text-align:left;} .true{color:green;font-weight:bold;} .false{color:red;} .null{color:orange;}</style>");
        out.println("</head><body>");
        out.println("<h1>Permission Debug Information</h1>");

        HttpSession session = request.getSession(false);
        if (session == null) {
            out.println("<p style='color:red;'>No session found!</p>");
            out.println("</body></html>");
            return;
        }

        // Session info
        out.println("<h2>Session Information</h2>");
        out.println("<table>");
        out.println("<tr><th>Key</th><th>Value</th></tr>");

        out.println("<tr><td>Session ID</td><td>" + session.getId() + "</td></tr>");
        out.println("<tr><td>accountId</td><td>" + session.getAttribute(SessionUtil.ACCOUNT_ID_KEY) + "</td></tr>");
        out.println("<tr><td>userId</td><td>" + session.getAttribute(SessionUtil.USER_ID_KEY) + "</td></tr>");
        out.println("<tr><td>username</td><td>" + session.getAttribute(SessionUtil.USERNAME_KEY) + "</td></tr>");
        out.println(
                "<tr><td>userFullName</td><td>" + session.getAttribute(SessionUtil.USER_FULL_NAME_KEY) + "</td></tr>");
        out.println("<tr><td>isAdmin</td><td class='" + session.getAttribute(SessionUtil.IS_ADMIN_KEY) + "'>"
                + session.getAttribute(SessionUtil.IS_ADMIN_KEY) + "</td></tr>");
        out.println("<tr><td>userRoles</td><td>" + session.getAttribute(SessionUtil.USER_ROLES_KEY) + "</td></tr>");
        out.println("<tr><td>userPositionId</td><td>" + session.getAttribute(SessionUtil.USER_POSITION_ID_KEY)
                + "</td></tr>");
        out.println("<tr><td>userDepartmentId</td><td>" + session.getAttribute(SessionUtil.USER_DEPARTMENT_ID_KEY)
                + "</td></tr>");

        User user = (User) session.getAttribute("user");
        if (user != null) {
            out.println("<tr><td>user.positionId</td><td>" + user.getPositionId() + "</td></tr>");
            out.println("<tr><td>user.departmentId</td><td>" + user.getDepartmentId() + "</td></tr>");
        } else {
            out.println("<tr><td colspan='2' style='color:red;'>User object not found in session!</td></tr>");
        }

        out.println("</table>");

        // Position info
        out.println("<h2>Position Information</h2>");
        out.println("<table>");
        out.println("<tr><th>Property</th><th>Value</th></tr>");

        String positionCode = PermissionUtil.getCurrentUserPositionCode(request);
        out.println("<tr><td>Position Code</td><td class='" + (positionCode != null ? "true" : "null") + "'>"
                + positionCode + "</td></tr>");

        Long deptId = PermissionUtil.getCurrentUserDepartmentId(request);
        out.println("<tr><td>Department ID</td><td>" + deptId + "</td></tr>");

        out.println("</table>");

        // Permissions
        out.println("<h2>Permissions Check</h2>");
        out.println("<table>");
        out.println("<tr><th>Permission</th><th>Has Permission</th></tr>");

        boolean canViewUserList = PermissionUtil.canViewUserList(request);
        out.println(
                "<tr><td>canViewUserList</td><td class='" + canViewUserList + "'>" + canViewUserList + "</td></tr>");

        boolean canViewAccountList = PermissionUtil.canViewAccountList(request);
        out.println("<tr><td>canViewAccountList</td><td class='" + canViewAccountList + "'>" + canViewAccountList
                + "</td></tr>");

        boolean canCreateUser = PermissionUtil.canCreateUser(request);
        out.println("<tr><td>canCreateUser</td><td class='" + canCreateUser + "'>" + canCreateUser + "</td></tr>");

        boolean canCreateAccount = PermissionUtil.canCreateAccount(request);
        out.println(
                "<tr><td>canCreateAccount</td><td class='" + canCreateAccount + "'>" + canCreateAccount + "</td></tr>");

        boolean canResetPassword = PermissionUtil.canResetPassword(request);
        out.println(
                "<tr><td>canResetPassword</td><td class='" + canResetPassword + "'>" + canResetPassword + "</td></tr>");

        boolean canViewAllUsers = PermissionUtil.canViewAllUsers(request);
        out.println(
                "<tr><td>canViewAllUsers</td><td class='" + canViewAllUsers + "'>" + canViewAllUsers + "</td></tr>");

        out.println("</table>");

        // Raw permissions
        out.println("<h2>Raw Permission Checks</h2>");
        out.println("<table>");
        out.println("<tr><th>Permission Constant</th><th>Has Permission</th></tr>");

        boolean hasViewAll = PermissionUtil.hasPermission(request, PermissionUtil.PERM_VIEW_ALL_USERS);
        out.println("<tr><td>PERM_VIEW_ALL_USERS</td><td class='" + hasViewAll + "'>" + hasViewAll + "</td></tr>");

        boolean hasViewDept = PermissionUtil.hasPermission(request, PermissionUtil.PERM_VIEW_DEPT_USERS);
        out.println("<tr><td>PERM_VIEW_DEPT_USERS</td><td class='" + hasViewDept + "'>" + hasViewDept + "</td></tr>");

        boolean hasViewAccounts = PermissionUtil.hasPermission(request, PermissionUtil.PERM_VIEW_ACCOUNTS);
        out.println(
                "<tr><td>PERM_VIEW_ACCOUNTS</td><td class='" + hasViewAccounts + "'>" + hasViewAccounts + "</td></tr>");

        boolean hasCreateUser = PermissionUtil.hasPermission(request, PermissionUtil.PERM_CREATE_USER);
        out.println("<tr><td>PERM_CREATE_USER</td><td class='" + hasCreateUser + "'>" + hasCreateUser + "</td></tr>");

        boolean hasCreateAccount = PermissionUtil.hasPermission(request, PermissionUtil.PERM_CREATE_ACCOUNT);
        out.println("<tr><td>PERM_CREATE_ACCOUNT</td><td class='" + hasCreateAccount + "'>" + hasCreateAccount
                + "</td></tr>");

        boolean hasResetPassword = PermissionUtil.hasPermission(request, PermissionUtil.PERM_RESET_PASSWORD);
        out.println("<tr><td>PERM_RESET_PASSWORD</td><td class='" + hasResetPassword + "'>" + hasResetPassword
                + "</td></tr>");

        out.println("</table>");

        out.println("<p><a href='" + request.getContextPath() + "/dashboard'>Back to Dashboard</a></p>");
        out.println("</body></html>");
    }
}
