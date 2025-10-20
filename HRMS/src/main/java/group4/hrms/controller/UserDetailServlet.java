package group4.hrms.controller;

import com.google.gson.Gson;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.UserDetailDto;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet xử lý API lấy thông tin chi tiết user
 * URL: /employees/users/{id}/details
 */
@WebServlet("/employees/users/details")
public class UserDetailServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailServlet.class);
    private final UserDao userDao = new UserDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.put("success", false);
                result.put("message", "Unauthorized");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Extract user ID from query parameter
            String userIdParam = request.getParameter("id");
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

            logger.info("Fetching details for user ID: {}", userId);

            // Fetch user details
            UserDetailDto user = userDao.findByIdAsDto(userId);

            if (user == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "User not found");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Return success response
            result.put("success", true);
            result.put("user", user);
            response.getWriter().write(gson.toJson(result));

        } catch (Exception e) {
            logger.error("Error fetching user details", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "Internal server error");
            response.getWriter().write(gson.toJson(result));
        }
    }
}
