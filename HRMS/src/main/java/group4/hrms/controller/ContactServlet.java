package group4.hrms.controller;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ContactServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra message thành công từ redirect
        String sent = request.getParameter("sent");
        if ("true".equals(sent)) {
            request.setAttribute("successMessage",
                    "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi trong vòng 24 giờ.");
        }

        // Forward đến trang contact
        request.getRequestDispatcher("/WEB-INF/views/contact.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy thông tin từ form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        // Validate required fields
        if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                message == null || message.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ các thông tin bắt buộc!");
            request.getRequestDispatcher("/WEB-INF/views/contact.jsp").forward(request, response);
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            request.setAttribute("errorMessage", "Địa chỉ email không hợp lệ!");
            request.getRequestDispatcher("/WEB-INF/views/contact.jsp").forward(request, response);
            return;
        }

        try {
            // TODO: Implement actual email sending logic
            // For now, just log the contact information
            logger.info(String.format("New contact message from %s (%s): %s", name, email, message));

            // In a real application, you would:
            // 1. Save to database
            // 2. Send email notification
            // 3. Send auto-reply to customer

            // Simulate processing time
            Thread.sleep(1000);

            // Set success message
            request.setAttribute("successMessage",
                    "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi trong vòng 24 giờ.");

            // Clear form data by redirecting
            response.sendRedirect(request.getContextPath() + "/contact?sent=true");

        } catch (Exception e) {
            logger.severe("Error processing contact form: " + e.getMessage());
            request.setAttribute("errorMessage",
                    "Có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/WEB-INF/views/contact.jsp").forward(request, response);
        }
    }

    /**
     * Simple email validation
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Basic email pattern
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }
}