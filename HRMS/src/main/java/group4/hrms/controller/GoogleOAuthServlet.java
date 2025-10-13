package group4.hrms.controller;

import group4.hrms.service.AuthService;
import group4.hrms.util.GoogleOAuthUtil;
import group4.hrms.util.SessionUtil;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "GoogleOAuthServlet", urlPatterns = { "/login-google" })
public class GoogleOAuthServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(GoogleOAuthServlet.class);
    private final AuthService authService;

    public GoogleOAuthServlet() {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("=== START: Google OAuth Callback ===");

        try {
            // Log all request parameters
            String code = request.getParameter("code");
            String error = request.getParameter("error");
            String state = request.getParameter("state");

            logger.info("OAuth callback parameters:");
            logger.info("  code: {}", code != null ? "[PRESENT]" : "[NULL]");
            logger.info("  error: {}", error);
            logger.info("  state: {}", state);

            // Kiểm tra nếu user đã đăng nhập
            if (SessionUtil.isUserLoggedIn(request)) {
                logger.info("User already logged in, redirecting to dashboard");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }

            // Kiểm tra lỗi từ Google
            if (error != null) {
                logger.warn("Google OAuth error received: {}", error);
                request.setAttribute("error", "Google authentication was cancelled or failed");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }

            // Kiểm tra authorization code
            if (code == null || code.trim().isEmpty()) {
                logger.warn("No authorization code received, redirecting to Google OAuth");
                String authUrl = GoogleOAuthUtil.createAuthorizationUrl(GoogleOAuthUtil.generateState());
                response.sendRedirect(authUrl);
                return;
            }

            // Validate state (optional but recommended)
            logger.debug("Validating OAuth state...");
            String sessionState = (String) request.getSession().getAttribute("oauth_state");
            if (sessionState != null && !sessionState.equals(state)) {
                logger.error("State mismatch! Session state: {}, Received state: {}", sessionState, state);
                request.setAttribute("error", "Invalid OAuth state. Please try again.");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }
            logger.debug("State validation passed");

            // Exchange code for access token
            logger.info("Step 1: Exchanging authorization code for access token...");
            String accessToken = GoogleOAuthUtil.exchangeCodeForAccessToken(code);
            if (accessToken == null || accessToken.trim().isEmpty()) {
                logger.error("Failed to get access token from Google - token is null or empty");
                request.setAttribute("error", "Failed to authenticate with Google");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }
            logger.info("✓ Access token received successfully");

            // Get user info from Google
            logger.info("Step 2: Getting user info from Google...");
            GoogleOAuthUtil.GoogleUserInfo googleUserInfo = GoogleOAuthUtil.getUserInfo(accessToken);
            if (googleUserInfo == null) {
                logger.error("Failed to get user info from Google - userInfo is null");
                request.setAttribute("error", "Failed to get user information from Google");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }
            if (googleUserInfo.getEmail() == null || googleUserInfo.getEmail().trim().isEmpty()) {
                logger.error("User info received but email is null or empty");
                request.setAttribute("error", "Failed to get email from Google");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }
            logger.info("✓ User info received: {}", googleUserInfo);

            // Authenticate with system
            logger.info("Step 3: Authenticating with system for email: {}", googleUserInfo.getEmail());
            AuthService.AuthResult result = authService.authenticateGoogle(
                    googleUserInfo.getEmail(), googleUserInfo);

            if (result.isSuccess()) {
                logger.info("✓ Authentication successful");

                // Verify account and user objects
                if (result.getAccount() == null) {
                    logger.error("Authentication succeeded but account is null!");
                    request.setAttribute("error", "Internal error: account data missing");
                    request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                    return;
                }
                if (result.getUser() == null) {
                    logger.error("Authentication succeeded but user is null!");
                    request.setAttribute("error", "Internal error: user data missing");
                    request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                    return;
                }

                logger.info("Account: ID={}, Username={}", result.getAccount().getId(), result.getAccount().getUsername());
                logger.info("User: ID={}, FullName={}", result.getUser().getId(), result.getUser().getFullName());

                // Create session
                logger.info("Step 4: Creating user session...");
                SessionUtil.createUserSession(request, result.getAccount(), result.getUser());
                logger.info("✓ Session created successfully");

                logger.info("User {} logged in successfully via Google", googleUserInfo.getEmail());
                logger.info("=== SUCCESS: Redirecting to dashboard ===");

                // Redirect to dashboard
                response.sendRedirect(request.getContextPath() + "/dashboard");

            } else {
                // Authentication failed
                logger.warn("Authentication failed for email: {} - {}",
                        googleUserInfo.getEmail(), result.getMessage());
                request.setAttribute("error", result.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }

        } catch (ParseException e) {
            logger.error("=== FAILED: Parse error during Google OAuth ===", e);
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            request.setAttribute("error", "Error processing Google authentication");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);

        } catch (IOException e) {
            logger.error("=== FAILED: IO error during Google OAuth ===", e);
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            request.setAttribute("error", "Error communicating with Google");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("=== FAILED: Unexpected error during Google OAuth ===", e);
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            logger.error("Stack trace:", e);
            request.setAttribute("error", "An unexpected error occurred");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // POST not supported for OAuth callback
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method not supported");
    }
}