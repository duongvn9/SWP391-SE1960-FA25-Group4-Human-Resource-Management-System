package group4.hrms.controller;

import group4.hrms.dao.AccountDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Account;
import group4.hrms.model.User;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servlet xử lý tạo Account mới
 * URL: /employees/accounts/create
 * Chỉ ADMIN mới có quyền tạo account
 */
@WebServlet("/employees/accounts/create")
public class AccountCreateServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AccountCreateServlet.class);
    private final AccountDao accountDao = new AccountDao();
    private final UserDao userDao = new UserDao();

    // Password validation patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*(),.?\":{}|<>].*");

    /**
     * Validate password strength according to requirements
     * 
     * @param password The password to validate
     * @return List of specific error messages, empty if password is valid
     */
    private List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.length() < 6) {
            errors.add("Password must be at least 6 characters long");
        }

        if (password != null && !UPPERCASE_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (password != null && !SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one special character");
        }

        return errors;
    }

    /**
     * GET - Hiển thị form tạo account mới
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check authorization - Chỉ ADMIN mới được tạo account
        if (!group4.hrms.util.PermissionUtil.canCreateAccount(request)) {
            logger.warn("Unauthorized access attempt to create account page");
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", "You don't have permission to create accounts");
            }
            response.sendRedirect(request.getContextPath() + "/employees/accounts");
            return;
        }

        try {
            // Check if userId is provided in query parameter (for pre-fill from user card)
            String userIdParam = request.getParameter("userId");
            List<User> users;

            if (userIdParam != null && !userIdParam.trim().isEmpty()) {
                // Khi click từ user card → chỉ hiển thị users chưa có account
                users = userDao.findUsersWithoutAccount();
                try {
                    Long preSelectedUserId = Long.parseLong(userIdParam);
                    request.setAttribute("preSelectedUserId", preSelectedUserId);
                    logger.info("Pre-selecting user ID: {} for account creation", preSelectedUserId);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid userId parameter: {}", userIdParam);
                }
            } else {
                // Khi click "Add New Account" → hiển thị TẤT CẢ users (active)
                users = userDao.findByStatus("active");
                logger.info("Loading all active users for account creation");
            }

            // Set attributes
            request.setAttribute("users", users);
            request.setAttribute("currentPage", "account-create");

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/employees/account-create.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading account create page: {}", e.getMessage(), e);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", "Error loading page. Please try again.");
            }
            response.sendRedirect(request.getContextPath() + "/employees/accounts");
        }
    }

    /**
     * POST - Xử lý tạo account mới
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Check authorization
        if (!group4.hrms.util.PermissionUtil.canCreateAccount(request)) {
            logger.warn("Unauthorized attempt to create account");
            session.setAttribute("errorMessage", "You don't have permission to create accounts");
            response.sendRedirect(request.getContextPath() + "/employees/accounts");
            return;
        }

        try {
            // Get form parameters
            String userIdStr = request.getParameter("userId");
            String username = request.getParameter("username");
            String emailLogin = request.getParameter("emailLogin");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            // Validate required fields
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Please select a user");
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            if (username == null || username.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Username is required");
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Password is required");
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            if (confirmPassword == null || !password.equals(confirmPassword)) {
                session.setAttribute("errorMessage", "Passwords do not match");
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            // Validate password strength
            List<String> passwordErrors = validatePassword(password);
            if (!passwordErrors.isEmpty()) {
                // Combine all password validation errors into a single message
                String errorMessage = "Password validation failed: " + String.join("; ", passwordErrors);
                session.setAttribute("errorMessage", errorMessage);
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            // Parse userId
            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                logger.error("Invalid user ID format: {}", userIdStr);
                session.setAttribute("errorMessage", "Invalid user ID");
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            // Check if user exists
            User user = userDao.findById(userId).orElse(null);
            if (user == null) {
                logger.warn("User not found with ID: {}", userId);
                session.setAttribute("errorMessage", "Selected user not found");
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            // Note: A user can have multiple accounts with different roles
            // No need to check for existing accounts

            // Determine email login - use company email if not provided
            String finalEmailLogin = (emailLogin != null && !emailLogin.trim().isEmpty())
                    ? emailLogin.trim()
                    : user.getEmailCompany();

            if (finalEmailLogin == null || finalEmailLogin.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Email login is required. User has no company email.");
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            // Check if username already exists
            if (accountDao.existsByUsername(username.trim())) {
                logger.warn("Username already exists: {}", username);
                session.setAttribute("errorMessage", "Username already exists. Please choose another one.");
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            // Check if email login already exists
            if (accountDao.findByEmailLogin(finalEmailLogin).isPresent()) {
                logger.warn("Email login already exists: {}", finalEmailLogin);
                session.setAttribute("errorMessage", "Email login already exists. Please use another email.");
                response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
                return;
            }

            // Create new account with hashed password
            // Note: Permissions will be determined by the user's position, not by role
            Account createdAccount = accountDao.createWithPassword(
                    userId,
                    username.trim(),
                    finalEmailLogin,
                    password);

            logger.info("Account created successfully: {} by user: {}",
                    createdAccount.getUsername(),
                    SessionUtil.getCurrentUsername(request));

            // Set success message
            session.setAttribute("successMessage",
                    "Account created successfully for " + user.getFullName());

            // Redirect to account list
            response.sendRedirect(request.getContextPath() + "/employees/accounts");

        } catch (Exception e) {
            logger.error("Error creating account: {}", e.getMessage(), e);

            // Provide more specific error message
            String errorMessage = "An error occurred while creating account. Please try again.";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Duplicate entry")) {
                    if (e.getMessage().contains("username")) {
                        errorMessage = "Username already exists. Please choose another one.";
                    } else if (e.getMessage().contains("email")) {
                        errorMessage = "Email already exists. Please use another email.";
                    } else {
                        errorMessage = "This account information already exists in the system.";
                    }
                } else if (e.getMessage().contains("foreign key constraint")) {
                    errorMessage = "Invalid user selection. Please select a valid user.";
                } else if (e.getMessage().contains("password")) {
                    errorMessage = "Error processing password. Please try again.";
                }
            }

            session.setAttribute("errorMessage", errorMessage);
            response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
        }
    }
}
