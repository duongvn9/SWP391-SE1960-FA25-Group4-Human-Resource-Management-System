package group4.hrms.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HomeServlet: Điều hướng root (guest) tới landing page.
 */
@WebServlet(name = "HomeServlet", urlPatterns = { "/", "/home" })
public class HomeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("HomeServlet forwarding to landing page");

        // Handle logout message
        String logoutMessage = req.getParameter("logoutMessage");
        if (logoutMessage != null && !logoutMessage.trim().isEmpty()) {
            req.setAttribute("logoutMessage", logoutMessage);
        }

        req.getRequestDispatcher("/WEB-INF/views/home/landing.jsp").forward(req, resp);
    }
}
