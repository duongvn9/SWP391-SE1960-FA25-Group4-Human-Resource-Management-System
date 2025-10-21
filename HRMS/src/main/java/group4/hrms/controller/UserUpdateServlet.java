package group4.hrms.controller;

import com.google.gson.Gson;
import group4.hrms.dao.AccountDao;
import group4.hrms.dao.AccountRoleDao;
import group4.hrms.dao.RoleDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Account;
import group4.hrms.model.Role;
import group4.hrms.model.User;
import group4.hrms.util.DropdownCacheUtil;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servlet xử lý cập nhật thông tin user
 * URL: /employees/users/update
 */
@WebServlet("/employees/users/update")
public class UserUpdateServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserUpdateServlet.class);
    private final UserDao userDao = new UserDao();
    private final AccountDao accountDao = new AccountDao();
    private final RoleDao roleDao = new RoleDao();
    private final AccountRoleDao accountRoleDao = new AccountRoleDao();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            logger.info("UserUpdateServlet.doPost() called");

            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.put("success", false);
                result.put("message", "Unauthorized");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Extract user ID from form parameter
            String userIdParam = request.getParameter("userId");
            logger.info("Received userId parameter: {}", userIdParam);
            if (userIdParam == null || userIdParam.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "User ID is required");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Parse user ID
            Long userId;
            try {
                userId = Long.parseLong(userIdParam.trim());
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Invalid user ID");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            logger.info("Updating user ID: {}", userId);

            // Fetch existing user
            User user = userDao.findById(userId).orElse(null);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "User not found");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Get form parameters
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String emailCompany = request.getParameter("emailCompany");
            String departmentIdStr = request.getParameter("departmentId");
            String positionIdStr = request.getParameter("positionId");
            String dateJoinedStr = request.getParameter("dateJoined");
            String startWorkDateStr = request.getParameter("startWorkDate");
            String status = request.getParameter("status");

            // Validate required fields
            if (fullName == null || fullName.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Full name is required");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            if (emailCompany == null || emailCompany.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Company email is required");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Update user fields
            user.setFullName(fullName.trim());
            user.setPhone(phone != null ? phone.trim() : null);
            user.setEmailCompany(emailCompany.trim());
            user.setStatus(status != null ? status.trim() : "active");

            // Parse and set department ID
            if (departmentIdStr != null && !departmentIdStr.trim().isEmpty()) {
                try {
                    user.setDepartmentId(Long.parseLong(departmentIdStr.trim()));
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    result.put("success", false);
                    result.put("message", "Invalid department ID");
                    response.getWriter().write(gson.toJson(result));
                    return;
                }
            }

            // Parse and set position ID with validation
            Long newPositionId = null;
            if (positionIdStr != null && !positionIdStr.trim().isEmpty()) {
                try {
                    newPositionId = Long.parseLong(positionIdStr.trim());
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    result.put("success", false);
                    result.put("message", "Invalid position ID");
                    response.getWriter().write(gson.toJson(result));
                    return;
                }
            }

            // Validate manager uniqueness if position is changing
            if (newPositionId != null && !newPositionId.equals(user.getPositionId())) {
                final Long finalNewPositionId = newPositionId;
                // Get position name from cache
                var positions = group4.hrms.util.DropdownCacheUtil.getCachedPositions(getServletContext());
                var newPosition = positions.stream()
                        .filter(p -> p.getId().equals(finalNewPositionId))
                        .findFirst();

                if (newPosition.isPresent()) {
                    String positionName = newPosition.get().getName();
                    Long newDepartmentId = (departmentIdStr != null && !departmentIdStr.trim().isEmpty())
                            ? Long.parseLong(departmentIdStr.trim())
                            : user.getDepartmentId();

                    // Check if HR-specific positions are assigned to Human Resource department
                    if (("HR Manager".equalsIgnoreCase(positionName) || "HR Staff".equalsIgnoreCase(positionName))
                            && newDepartmentId != null) {
                        var departments = DropdownCacheUtil.getCachedDepartments(getServletContext());
                        var department = departments.stream()
                                .filter(d -> d.getId().equals(newDepartmentId))
                                .findFirst();

                        if (department.isPresent()
                                && !"Human Resource".equalsIgnoreCase(department.get().getName())) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            result.put("success", false);
                            result.put("message", "Position '" + positionName
                                    + "' can only be assigned to employees in the Human Resource department.");
                            response.getWriter().write(gson.toJson(result));
                            return;
                        }
                    }

                    // Check for Department Manager uniqueness
                    if ("Department Manager".equalsIgnoreCase(positionName)) {
                        if (newDepartmentId != null) {
                            java.util.Optional<User> existingManager = userDao.findDepartmentManager(newDepartmentId);
                            if (existingManager.isPresent() && !existingManager.get().getId().equals(userId)) {
                                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                result.put("success", false);
                                result.put("message", "This department already has a manager: " +
                                        existingManager.get().getFullName() +
                                        ". Please remove the current manager first.");
                                response.getWriter().write(gson.toJson(result));
                                return;
                            }
                        }
                    }

                    // Check for HR Manager uniqueness
                    if ("HR Manager".equalsIgnoreCase(positionName)) {
                        java.util.List<User> existingHRManagers = userDao.findByPositionId(finalNewPositionId);
                        // Filter out current user
                        final Long finalUserId = userId;
                        existingHRManagers = existingHRManagers.stream()
                                .filter(u -> !u.getId().equals(finalUserId))
                                .collect(java.util.stream.Collectors.toList());

                        if (!existingHRManagers.isEmpty()) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            result.put("success", false);
                            result.put("message", "The company already has an HR Manager: " +
                                    existingHRManagers.get(0).getFullName() +
                                    ". Only one HR Manager is allowed.");
                            response.getWriter().write(gson.toJson(result));
                            return;
                        }
                    }
                }

                user.setPositionId(newPositionId);
            } else if (newPositionId != null) {
                user.setPositionId(newPositionId);
            }

            // Parse and set dates
            try {
                if (dateJoinedStr != null && !dateJoinedStr.trim().isEmpty()) {
                    user.setDateJoined(LocalDate.parse(dateJoinedStr.trim()));
                }
                if (startWorkDateStr != null && !startWorkDateStr.trim().isEmpty()) {
                    user.setStartWorkDate(LocalDate.parse(startWorkDateStr.trim()));
                }
            } catch (DateTimeParseException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Invalid date format");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Update user in database
            boolean updated = userDao.update(user);

            if (updated) {
                logger.info("User updated successfully: {}", userId);

                // Update role if position or department changed
                if (newPositionId != null && !newPositionId.equals(user.getPositionId())) {
                    try {
                        updateUserRole(user);
                    } catch (Exception e) {
                        logger.error("Error updating user role", e);
                        // Continue anyway - user is updated, role update failed
                    }
                }

                result.put("success", true);
                result.put("message", "User updated successfully");
            } else {
                logger.error("Failed to update user: {}", userId);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.put("success", false);
                result.put("message", "Failed to update user");
            }

            response.getWriter().write(gson.toJson(result));

        } catch (Exception e) {
            logger.error("Error updating user", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "Internal server error: " + e.getMessage());
            response.getWriter().write(gson.toJson(result));
        }
    }

    /**
     * Update user's account role based on position and department
     * Role assignment logic:
     * - HR Manager position → HR_MANAGER role
     * - Department Manager in Human Resource → HR_MANAGER role
     * - Department Manager in other departments → DEPARTMENT_MANAGER role
     * - Other positions → no role change
     */
    private void updateUserRole(User user) {
        // Check if user has an account
        List<Account> accounts = accountDao.findByUserId(user.getId());
        if (accounts.isEmpty()) {
            logger.info("User {} has no account, skipping role update", user.getId());
            return;
        }

        Account account = accounts.get(0);

        // Determine new role based on position and department
        String newRoleName = null;

        var positions = DropdownCacheUtil.getCachedPositions(getServletContext());
        var position = positions.stream()
                .filter(p -> p.getId().equals(user.getPositionId()))
                .findFirst();

        if (position.isPresent()) {
            String positionName = position.get().getName();

            // If position is HR Manager, assign HR_MANAGER role
            if ("HR Manager".equalsIgnoreCase(positionName)) {
                newRoleName = "HR_MANAGER";
            }
            // If Department Manager, check department
            else if ("Department Manager".equalsIgnoreCase(positionName) && user.getDepartmentId() != null) {
                var departments = DropdownCacheUtil.getCachedDepartments(getServletContext());
                var department = departments.stream()
                        .filter(d -> d.getId().equals(user.getDepartmentId()))
                        .findFirst();

                if (department.isPresent() && "Human Resource".equalsIgnoreCase(department.get().getName())) {
                    newRoleName = "HR_MANAGER";
                } else {
                    newRoleName = "DEPARTMENT_MANAGER";
                }
            }
        }

        // Update role if needed
        if (newRoleName != null) {
            Optional<Role> newRole = roleDao.findByName(newRoleName);
            if (newRole.isPresent()) {
                // Assign new role
                // Note: This may create duplicate roles if user already has it
                // In production, you should check existing roles and remove old manager roles
                // first
                try {
                    accountRoleDao.assignRole(account.getId(), newRole.get().getId());
                    logger.info("Assigned {} role to account: {}", newRoleName, account.getUsername());
                } catch (Exception e) {
                    // Role might already exist (duplicate key), log and continue
                    logger.warn("Could not assign {} role to account {}: {}",
                            newRoleName, account.getUsername(), e.getMessage());
                }
            } else {
                logger.warn("{} role not found in database", newRoleName);
            }
        }
    }
}
