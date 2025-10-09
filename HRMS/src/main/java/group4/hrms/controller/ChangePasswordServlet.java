package group4.hrms.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet xử lý yêu cầu đổi mật khẩu của người dùng
 * URL mapping: /profile/change-password
 * 
 * @author Group4 - SWP391
 */
@WebServlet("/profile/change-password")
public class ChangePasswordServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Tạo CSRF token
        String csrfToken = generateCSRFToken();
        session.setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken);
        
        // Chuyển tiếp đến trang đổi mật khẩu
        request.getRequestDispatcher("/WEB-INF/views/profile/change-password.jsp")
               .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Kiểm tra CSRF token
            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = request.getParameter("csrfToken");
            
            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                request.setAttribute("errorMessage", "Yêu cầu không hợp lệ. Vui lòng thử lại.");
                doGet(request, response);
                return;
            }
            
            // Lấy thông tin từ form
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            // Validation cơ bản
            if (isEmpty(currentPassword) || isEmpty(newPassword) || isEmpty(confirmPassword)) {
                request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin.");
                doGet(request, response);
                return;
            }
            
            // Kiểm tra mật khẩu mới và xác nhận khớp nhau
            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
                doGet(request, response);
                return;
            }
            
            // Kiểm tra độ mạnh mật khẩu
            if (!isPasswordStrong(newPassword)) {
                request.setAttribute("errorMessage", 
                    "Mật khẩu mới không đủ mạnh. Vui lòng đảm bảo có ít nhất 8 ký tự, " +
                    "bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
                doGet(request, response);
                return;
            }
            
            // Lấy userId từ session
            Long userId = (Long) session.getAttribute("userId");
            
            // TODO: Implement password change logic
            // 1. Verify current password
            // 2. Hash new password
            // 3. Update database
            // 4. Log the action
            
            // Tạm thời giả lập thành công
            boolean changeSuccess = processPasswordChange(userId, currentPassword, newPassword);
            
            if (changeSuccess) {
                request.setAttribute("successMessage", "Đổi mật khẩu thành công!");
                
                // Tạo token mới cho lần request tiếp theo
                String newCsrfToken = generateCSRFToken();
                session.setAttribute("csrfToken", newCsrfToken);
                request.setAttribute("csrfToken", newCsrfToken);
                
            } else {
                request.setAttribute("errorMessage", "Mật khẩu hiện tại không đúng.");
            }
            
        } catch (Exception e) {
            // Log error
            getServletContext().log("Error in ChangePasswordServlet", e);
            request.setAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại sau.");
        }
        
        // Hiển thị lại form với thông báo
        doGet(request, response);
    }
    
    /**
     * Kiểm tra chuỗi có rỗng hay không
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Kiểm tra độ mạnh của mật khẩu
     */
    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*(),.?\":{}|<>".indexOf(ch) >= 0);
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Tạo CSRF token
     */
    private String generateCSRFToken() {
        return java.util.UUID.randomUUID().toString();
    }
    
    /**
     * Xử lý đổi mật khẩu
     * TODO: Implement với database và BCrypt
     */
    private boolean processPasswordChange(Long userId, String currentPassword, String newPassword) {
        // Tạm thời return true để test UI
        // Trong thực tế sẽ:
        // 1. Lấy hash password hiện tại từ DB
        // 2. Verify current password với BCrypt
        // 3. Hash new password với BCrypt
        // 4. Update database
        // 5. Log action
        
        return true; // Giả lập thành công
    }
}