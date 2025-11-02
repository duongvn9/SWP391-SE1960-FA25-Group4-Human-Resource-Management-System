package group4.hrms.email.service;

import group4.hrms.email.dao.ContactRequestDao;
import group4.hrms.email.model.ContactRequest;
import group4.hrms.email.model.ContactStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service để xử lý business logic cho contact requests
 * Bao gồm validation, ID generation và data processing
 * 
 * Requirements: 5.1, 5.4
 * 
 * @author Group4
 */
public class ContactService {
    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    private final ContactRequestDao contactDao;

    public ContactService() {
        this.contactDao = new ContactRequestDao();
    }

    public ContactService(ContactRequestDao contactDao) {
        this.contactDao = contactDao;
    }

    /**
     * Lưu contact request mới
     * Tự động generate ID và validate data
     * 
     * @param contact ContactRequest cần lưu
     * @return ContactRequest đã được lưu với ID
     * @throws ContactValidationException nếu validation thất bại
     * @throws ContactServiceException    nếu có lỗi khi lưu
     */
    public ContactRequest saveContact(ContactRequest contact) {
        if (contact == null) {
            throw new ContactValidationException("Contact request không được null");
        }

        // Validate contact data
        validateContact(contact);

        // Generate ID nếu chưa có
        if (contact.getId() == null || contact.getId().trim().isEmpty()) {
            contact.setId(generateContactId());
        }

        // Set timestamps
        if (contact.getCreatedAt() == null) {
            contact.setCreatedAt(LocalDateTime.now());
        }
        contact.setUpdatedAt(LocalDateTime.now());

        // Set default status
        if (contact.getStatus() == null) {
            contact.setStatus(ContactStatus.NEW);
        }

        try {
            logger.info("Đang lưu contact request: {}", contact.getId());
            ContactRequest saved = contactDao.save(contact);
            logger.info("Đã lưu contact request thành công: {}", saved.getId());
            return saved;

        } catch (SQLException e) {
            logger.error("Lỗi database khi lưu contact request: {}", e.getMessage(), e);
            throw new ContactServiceException("Không thể lưu contact request", e);
        }
    }

    /**
     * Validate contact request data
     * 
     * @param contact ContactRequest cần validate
     * @throws ContactValidationException nếu validation thất bại
     */
    private void validateContact(ContactRequest contact) {
        // Validate full name
        if (contact.getFullName() == null || contact.getFullName().trim().isEmpty()) {
            throw new ContactValidationException("Họ tên không được để trống");
        }

        if (contact.getFullName().trim().length() < 2) {
            throw new ContactValidationException("Họ tên phải có ít nhất 2 ký tự");
        }

        if (contact.getFullName().length() > 100) {
            throw new ContactValidationException("Họ tên không được vượt quá 100 ký tự");
        }

        // Validate email
        if (contact.getEmail() == null || contact.getEmail().trim().isEmpty()) {
            throw new ContactValidationException("Email không được để trống");
        }

        if (!isValidEmail(contact.getEmail())) {
            throw new ContactValidationException("Email không hợp lệ");
        }

        if (contact.getEmail().length() > 255) {
            throw new ContactValidationException("Email không được vượt quá 255 ký tự");
        }

        // Validate phone (optional)
        if (contact.getPhone() != null && !contact.getPhone().trim().isEmpty()) {
            if (!isValidPhone(contact.getPhone())) {
                throw new ContactValidationException("Số điện thoại không hợp lệ");
            }
        }

        // Validate contact type
        if (contact.getContactType() == null) {
            throw new ContactValidationException("Loại liên hệ không được để trống");
        }

        // Validate subject
        if (contact.getSubject() == null || contact.getSubject().trim().isEmpty()) {
            throw new ContactValidationException("Tiêu đề không được để trống");
        }

        if (contact.getSubject().trim().length() < 5) {
            throw new ContactValidationException("Tiêu đề phải có ít nhất 5 ký tự");
        }

        if (contact.getSubject().length() > 255) {
            throw new ContactValidationException("Tiêu đề không được vượt quá 255 ký tự");
        }

        // Validate message
        if (contact.getMessage() == null || contact.getMessage().trim().isEmpty()) {
            throw new ContactValidationException("Nội dung không được để trống");
        }

        if (contact.getMessage().trim().length() < 10) {
            throw new ContactValidationException("Nội dung phải có ít nhất 10 ký tự");
        }

        if (contact.getMessage().length() > 5000) {
            throw new ContactValidationException("Nội dung không được vượt quá 5000 ký tự");
        }
    }

    /**
     * Generate unique contact ID
     * Format: CR-{UUID}
     * 
     * @return Contact ID
     */
    private String generateContactId() {
        return "CR-" + UUID.randomUUID().toString();
    }

    /**
     * Validate email format
     * 
     * @param email Email cần validate
     * @return true nếu email hợp lệ
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Basic email pattern
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }

    /**
     * Validate phone number format
     * Accepts Vietnamese phone numbers with or without country code
     * 
     * @param phone Phone number cần validate
     * @return true nếu phone hợp lệ
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        // Remove spaces and special characters
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");

        // Vietnamese phone patterns:
        // - 10 digits starting with 0: 0xxxxxxxxx
        // - 11 digits starting with 84: 84xxxxxxxxx
        // - 12 digits starting with +84: +84xxxxxxxxx
        String phonePattern = "^(\\+?84|0)[0-9]{9}$";
        return cleanPhone.matches(phonePattern);
    }

    /**
     * Lấy contact request theo ID
     * 
     * @param contactId Contact ID
     * @return ContactRequest hoặc null nếu không tìm thấy
     */
    public ContactRequest getContactById(String contactId) {
        if (contactId == null || contactId.trim().isEmpty()) {
            return null;
        }

        try {
            return contactDao.findById(contactId).orElse(null);
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm contact {}: {}", contactId, e.getMessage(), e);
            throw new ContactServiceException("Không thể tìm contact request", e);
        }
    }

    /**
     * Cập nhật status của contact request
     * 
     * @param contactId Contact ID
     * @param newStatus Status mới
     * @return true nếu cập nhật thành công
     */
    public boolean updateContactStatus(String contactId, ContactStatus newStatus) {
        if (contactId == null || newStatus == null) {
            return false;
        }

        try {
            return contactDao.updateStatus(contactId, newStatus);
        } catch (SQLException e) {
            logger.error("Lỗi khi cập nhật status contact {}: {}", contactId, e.getMessage(), e);
            throw new ContactServiceException("Không thể cập nhật status", e);
        }
    }

    /**
     * Exception cho validation errors
     */
    public static class ContactValidationException extends RuntimeException {
        public ContactValidationException(String message) {
            super(message);
        }
    }

    /**
     * Exception cho service errors
     */
    public static class ContactServiceException extends RuntimeException {
        public ContactServiceException(String message) {
            super(message);
        }

        public ContactServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
