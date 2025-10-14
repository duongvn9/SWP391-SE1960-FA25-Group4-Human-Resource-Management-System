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

        try {
            // Kiểm tra nếu user đã đăng nhập
            if (SessionUtil.isUserLoggedIn(request)) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }

            String code = request.getParameter("code");
            String error = request.getParameter("error");
            String state = request.getParameter("state");

            // Kiểm tra lỗi từ Google
            if (error != null) {
                logger.warn("Google OAuth error: {}", error);
                request.setAttribute("error", "Google authentication was cancelled or failed");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }

            // Kiểm tra authorization code
            if (code == null || code.trim().isEmpty()) {
                // Redirect to Google OAuth for authorization
                String authUrl = GoogleOAuthUtil.createAuthorizationUrl(GoogleOAuthUtil.generateState());
                response.sendRedirect(authUrl);
                return;
            }

            // Exchange code for access token
            String accessToken = GoogleOAuthUtil.exchangeCodeForAccessToken(code);
            if (accessToken == null) {
                logger.error("Failed to get access token from Google");
                request.setAttribute("error", "Failed to authenticate with Google");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }

            // Get user info from Google
            GoogleOAuthUtil.GoogleUserInfo googleUserInfo = GoogleOAuthUtil.getUserInfo(accessToken);
            if (googleUserInfo == null || googleUserInfo.getEmail() == null) {
                logger.error("Failed to get user info from Google");
                request.setAttribute("error", "Failed to get user information from Google");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }

            logger.info("Google OAuth user info: {}", googleUserInfo);

            // Authenticate with system
            AuthService.AuthResult result = authService.authenticateGoogle(
                    googleUserInfo.getEmail(), googleUserInfo);

            if (result.isSuccess()) {
                // Create session
                SessionUtil.createUserSession(request, result.getAccount(), result.getUser());
                
                logger.info("User {} logged in successfully via Google", googleUserInfo.getEmail());

                // Redirect to dashboard
                response.sendRedirect(request.getContextPath() + "/dashboard");

            } else {
                // Authentication failed
                logger.warn("Google authentication failed for email: {} - {}", 
                        googleUserInfo.getEmail(), result.getMessage());
                request.setAttribute("error", result.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }

        } catch (ParseException e) {
            logger.error("Error parsing Google OAuth response", e);
            request.setAttribute("error", "Error processing Google authentication");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            
        } catch (IOException e) {
            logger.error("IO error during Google OAuth", e);
            request.setAttribute("error", "Error communicating with Google");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Unexpected error during Google OAuth", e);
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