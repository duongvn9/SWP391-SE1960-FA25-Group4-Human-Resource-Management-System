package group4.hrms.controller;

import group4.hrms.dao.UserProfileDao;
import group4.hrms.model.UserProfile;
import group4.hrms.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/my-account")
public class AccountController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final UserProfileDao userProfileDao;
    
    public AccountController() {
        this.userProfileDao = new UserProfileDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        logger.debug("GET /my-account - Loading account information page");
        
        // Check authentication
        if (!SessionUtil.isUserLoggedIn(req)) {
            logger.warn("User not logged in, redirecting to login page");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            // Get account ID from session
            Long accountId = SessionUtil.getCurrentAccountId(req);
            if (accountId == null) {
                logger.error("Account ID not found in session");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            
            String username = SessionUtil.getCurrentUsername(req);
            logger.info("Loading account info for account_id: {}, username: {}", accountId, username);
            
            // Get user profile from database
            Optional<UserProfile> profileOpt = userProfileDao.findByAccountId(accountId);
            
            if (!profileOpt.isPresent()) {
                logger.error("User profile not found for account_id: {}", accountId);
                req.setAttribute("error", "Account information not found");
                req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
                return;
            }
            
            UserProfile profile = profileOpt.get();
            logger.debug("Account info loaded successfully: {}", profile);
            
            // Set attributes for JSP
            req.setAttribute("profile", profile);
            
            // Forward to my-account page
            req.getRequestDispatcher("/WEB-INF/views/account/my-account.jsp").forward(req, resp);
            
        } catch (Exception e) {
            logger.error("Error loading account information", e);
            req.setAttribute("error", "An error occurred while loading your account information");
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }
}
