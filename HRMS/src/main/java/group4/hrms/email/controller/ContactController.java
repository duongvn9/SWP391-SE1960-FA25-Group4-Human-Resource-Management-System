package group4.hrms.email.controller;

import com.google.gson.Gson;
import group4.hrms.email.dto.ContactRequestDto;
import group4.hrms.email.dto.ContactResponseDto;
import group4.hrms.email.model.ContactRequest;
import group4.hrms.email.model.ContactType;
import group4.hrms.email.service.ContactService;
import group4.hrms.email.service.EmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Controller xử lý contact form
 * Bao gồm GET endpoint để hiển thị form và POST endpoint để xử lý submission
 * 
 * Requirements: 5.1, 5.3
 * 
 * @author Group4
 */
@WebServlet(urlPatterns = { "/contact", "/contact/submit" })
public class ContactController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private ContactService contactService;
    private EmailService emailService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.contactService = new ContactService();
        this.emailService = new EmailService();
        this.gson = new Gson();
        logger.info("ContactController initialized");
    }

    /**
     * GET endpoint để hiển thị contact form
     * Requirements: 5.1
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("GET /contact - Displaying contact form");

        // Kiểm tra message thành công từ redirect
        String sent = request.getParameter("sent");
        if ("true".equals(sent)) {
            request.setAttribute("successMessage",
                    "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất có thể.");
        }

        // Thêm contact types vào request để hiển thị trong dropdown
        request.setAttribute("contactTypes", ContactType.values());

        // Forward đến trang contact
        request.getRequestDispatcher("/WEB-INF/views/contact.jsp").forward(request, response);
    }

    /**
     * POST endpoint để xử lý form submission
     * Requirements: 5.1, 5.3
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set character encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getServletPath();

        // Handle AJAX submission to /contact/submit
        if ("/contact/submit".equals(pathInfo)) {
            handleAjaxSubmission(request, response);
        } else {
            // Handle traditional form submission to /contact
            handleFormSubmission(request, response);
        }
    }

    /**
     * Xử lý AJAX form submission (JSON request/response)
     */
    private void handleAjaxSubmission(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Parse JSON request
            BufferedReader reader = request.getReader();
            ContactRequestDto dto = gson.fromJson(reader, ContactRequestDto.class);

            logger.info("Received AJAX contact submission from: {}", dto.getEmail());

            // Convert DTO to entity
            ContactRequest contact = convertDtoToEntity(dto);

            // Save contact request
            ContactRequest savedContact = contactService.saveContact(contact);

            // Send emails asynchronously
            try {
                emailService.sendContactResponse(savedContact);
                emailService.sendCompanyNotification(savedContact);
                logger.info("Emails queued successfully for contact: {}", savedContact.getId());
            } catch (Exception e) {
                logger.error("Error queueing emails for contact {}: {}",
                        savedContact.getId(), e.getMessage(), e);
                // Don't fail the request if email queueing fails
            }

            // Return success response
            ContactResponseDto responseDto = new ContactResponseDto(
                    savedContact.getId(),
                    "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất có thể.",
                    true);

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(responseDto));

        } catch (ContactService.ContactValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());

            ContactResponseDto errorResponse = new ContactResponseDto(
                    null,
                    e.getMessage(),
                    false);

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(errorResponse));

        } catch (Exception e) {
            logger.error("Error processing contact submission: {}", e.getMessage(), e);

            ContactResponseDto errorResponse = new ContactResponseDto(
                    null,
                    "Có lỗi xảy ra. Vui lòng thử lại sau.",
                    false);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(errorResponse));
        } finally {
            out.flush();
        }
    }

    /**
     * Xử lý traditional form submission (form-encoded request)
     */
    private void handleFormSubmission(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy thông tin từ form
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String contactTypeStr = request.getParameter("contactType");
            String subject = request.getParameter("subject");
            String message = request.getParameter("message");

            logger.info("Received form contact submission from: {}", email);

            // Create ContactRequest entity
            ContactRequest contact = new ContactRequest();
            contact.setFullName(fullName);
            contact.setEmail(email);
            contact.setPhone(phone);

            // Parse contact type
            if (contactTypeStr != null && !contactTypeStr.trim().isEmpty()) {
                try {
                    contact.setContactType(ContactType.valueOf(contactTypeStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    contact.setContactType(ContactType.OTHER);
                }
            } else {
                contact.setContactType(ContactType.OTHER);
            }

            contact.setSubject(subject);
            contact.setMessage(message);

            // Save contact request
            ContactRequest savedContact = contactService.saveContact(contact);

            // Send emails asynchronously
            try {
                emailService.sendContactResponse(savedContact);
                emailService.sendCompanyNotification(savedContact);
                logger.info("Emails queued successfully for contact: {}", savedContact.getId());
            } catch (Exception e) {
                logger.error("Error queueing emails for contact {}: {}",
                        savedContact.getId(), e.getMessage(), e);
                // Don't fail the request if email queueing fails
            }

            // Redirect với success message
            response.sendRedirect(request.getContextPath() + "/contact?sent=true");

        } catch (ContactService.ContactValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("contactTypes", ContactType.values());
            request.getRequestDispatcher("/WEB-INF/views/contact.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error processing contact form: {}", e.getMessage(), e);
            request.setAttribute("errorMessage",
                    "Có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại sau.");
            request.setAttribute("contactTypes", ContactType.values());
            request.getRequestDispatcher("/WEB-INF/views/contact.jsp").forward(request, response);
        }
    }

    /**
     * Convert DTO to entity
     */
    private ContactRequest convertDtoToEntity(ContactRequestDto dto) {
        ContactRequest contact = new ContactRequest();
        contact.setFullName(dto.getFullName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());

        // Parse contact type
        if (dto.getContactType() != null && !dto.getContactType().trim().isEmpty()) {
            try {
                contact.setContactType(ContactType.valueOf(dto.getContactType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                contact.setContactType(ContactType.OTHER);
            }
        } else {
            contact.setContactType(ContactType.OTHER);
        }

        contact.setSubject(dto.getSubject());
        contact.setMessage(dto.getMessage());

        return contact;
    }
}
