package group4.hrms.controller;

import group4.hrms.service.AuthService;
import group4.hrms.util.GoogleOAuthUtil;
import group4.hrms.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final AuthService authService;

    public LoginServlet() {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra nếu user đã đăng nhập
        if (SessionUtil.isUserLoggedIn(request)) {
            String redirectUrl = determineRedirectUrl(request);
            response.sendRedirect(redirectUrl);
            return;
        }

        // Generate CSRF token
        String csrfToken = generateCsrfToken();
        request.getSession(true).setAttribute("_csrf_token", csrfToken);
        request.setAttribute("csrfToken", csrfToken);

        // Generate Google OAuth URL
        try {
            String state = GoogleOAuthUtil.generateState();
            request.getSession().setAttribute("oauth_state", state);
            String googleAuthUrl = GoogleOAuthUtil.createAuthorizationUrl(state);
            request.setAttribute("googleAuthUrl", googleAuthUrl);
            logger.debug("Generated Google Auth URL: {}", googleAuthUrl);
        } catch (Exception e) {
            logger.error("Failed to generate Google Auth URL", e);
            // Continue without Google OAuth option
        }

        // Forward đến trang login
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Validate CSRF token
            if (!validateCsrfToken(request)) {
                logger.warn("Invalid CSRF token in login request");
                request.setAttribute("error", "Invalid request. Please try again.");
                forwardToLoginPage(request, response);
                return;
            }

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            // Validate input
            if (username == null || username.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {
                request.setAttribute("error", "Username and password are required");
                request.setAttribute("username", username);
                forwardToLoginPage(request, response);
                return;
            }

            username = username.trim();

            // Authenticate user
            AuthService.AuthResult result = authService.authenticateLocal(username, password);

            if (result.isSuccess()) {
                // Create session
                SessionUtil.createUserSession(request, result.getAccount(), result.getUser());

                logger.info("User {} logged in successfully", username);

                // Check for original URL to redirect after login
                String originalUrl = (String) request.getSession().getAttribute("originalUrl");
                if (originalUrl != null) {
                    request.getSession().removeAttribute("originalUrl");
                    response.sendRedirect(originalUrl);
                } else {
                    // Default redirect based on user role/permissions
                    String redirectUrl = determineRedirectUrl(request);
                    response.sendRedirect(redirectUrl);
                }

            } else {
                // Authentication failed
                logger.warn("Login failed for username: {} - {}", username, result.getMessage());
                request.setAttribute("error", result.getMessage());
                request.setAttribute("username", username);
                forwardToLoginPage(request, response);
            }

        } catch (Exception e) {
            logger.error("Error during login process", e);
            request.setAttribute("error", "An error occurred. Please try again.");
            forwardToLoginPage(request, response);
        }
    }

    /**
     * Generate CSRF token
     */
    private String generateCsrfToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Validate CSRF token
     */
    private boolean validateCsrfToken(HttpServletRequest request) {
        String sessionToken = (String) request.getSession().getAttribute("_csrf_token");
        String requestToken = request.getParameter("_csrf_token");

        return sessionToken != null && sessionToken.equals(requestToken);
    }

    /**
     * Forward to login page with CSRF token
     */
    private void forwardToLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Generate new CSRF token
        String csrfToken = generateCsrfToken();
        request.getSession().setAttribute("_csrf_token", csrfToken);
        request.setAttribute("csrfToken", csrfToken);

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    /**
     * Determine redirect URL based on user role
     */
    private String determineRedirectUrl(HttpServletRequest request) {
        // TODO: Implement role-based redirection logic
        // For now, redirect to dashboard
        return request.getContextPath() + "/dashboard";
    }
}