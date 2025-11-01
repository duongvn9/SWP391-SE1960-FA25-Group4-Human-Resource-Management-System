package group4.hrms.email.controller;

import group4.hrms.email.service.EmailSenderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Test controller để kiểm tra email configuration
 */
@WebServlet("/test-email")
public class EmailTestController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EmailTestController.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Email Test</title></head><body>");
        out.println("<h1>Email Configuration Test</h1>");

        try {
            EmailSenderService emailService = new EmailSenderService();

            // Test connection
            out.println("<h2>Testing SMTP Connection...</h2>");
            boolean connected = emailService.testConnection();

            if (connected) {
                out.println("<p style='color: green;'>✅ SMTP Connection: SUCCESS</p>");

                // Test sending email
                String testEmail = request.getParameter("email");
                if (testEmail != null && !testEmail.trim().isEmpty()) {
                    out.println("<h2>Sending Test Email to: " + testEmail + "</h2>");

                    String subject = "Test Email from HRMS";
                    String content = "<h1>Test Email</h1><p>This is a test email from HRMS system.</p>";

                    boolean sent = emailService.sendTestEmail(testEmail, subject, content);

                    if (sent) {
                        out.println("<p style='color: green;'>✅ Email Sent: SUCCESS</p>");
                    } else {
                        out.println("<p style='color: red;'>❌ Email Sent: FAILED</p>");
                    }
                }
            } else {
                out.println("<p style='color: red;'>❌ SMTP Connection: FAILED</p>");
                out.println("<p>Please check:</p>");
                out.println("<ul>");
                out.println("<li>Gmail app password is correct</li>");
                out.println("<li>2-Step Verification is enabled</li>");
                out.println("<li>App password hasn't been revoked</li>");
                out.println("</ul>");
            }

        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Error: " + e.getMessage() + "</p>");
            logger.error("Email test failed", e);
        }

        out.println("<hr>");
        out.println("<form method='get'>");
        out.println("<label>Test Email Address:</label><br>");
        out.println("<input type='email' name='email' required><br><br>");
        out.println("<button type='submit'>Send Test Email</button>");
        out.println("</form>");

        out.println("</body></html>");
    }
}
