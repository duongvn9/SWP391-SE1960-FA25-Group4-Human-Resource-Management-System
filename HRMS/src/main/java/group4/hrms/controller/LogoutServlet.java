package group4.hrms.controller;

import group4.hrms.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "LogoutServlet", urlPatterns = { "/auth/logout" })
public class LogoutServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Destroy session
            SessionUtil.destroySession(request);
            logger.info("User logged out successfully");

            // Redirect về trang chủ với thông báo logout thành công
            response.sendRedirect(request.getContextPath() + "/?logoutMessage=" +
                    java.net.URLEncoder.encode("Logged out successfully!", "UTF-8"));

        } catch (Exception e) {
            logger.error("Error during logout", e);
            // Still redirect to home even if there's an error
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}