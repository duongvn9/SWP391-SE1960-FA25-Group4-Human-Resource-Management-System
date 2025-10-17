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
import java.util.List;

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

    /**
     * GET - Hiển thị form tạo account mới
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check authorization - Chỉ ADMIN mới được tạo account
        if (!SessionUtil.hasRole(request, "ADMIN")) {
            logger.warn("Unauthorized access attempt to create account page");
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", "You don't have permission to create accounts");
            }
            response.sendRedirect(request.getContextPath() + "/employees/accounts");
            return;
        }

        try {
            // Lấy danh sách users để chọn (chỉ users đang active)
            List<User> users = userDao.findByStatus("active");

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
        if (!SessionUtil.hasRole(request, "ADMIN")) {
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
            session.setAttribute("errorMessage", "Error creating account. Please try again.");
            response.sendRedirect(request.getContextPath() + "/employees/accounts/create");
        }
    }
}
