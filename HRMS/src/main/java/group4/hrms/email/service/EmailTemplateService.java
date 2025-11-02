package group4.hrms.email.service;

import group4.hrms.email.model.EmailEventType;
import group4.hrms.email.model.EmailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service class để xử lý email templates
 * Đọc templates từ file HTML trong resources/email-templates/
 * 
 * @author Group4
 */
public class EmailTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(EmailTemplateService.class);

    // Pattern để tìm placeholders dạng {{key}}
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    // Template path
    private static final String TEMPLATE_PATH = "/email-templates/";

    public EmailTemplateService() {
    }

    /**
     * Lấy template theo event type
     * Đọc từ file HTML trong resources/email-templates/
     * 
     * @param eventType loại sự kiện email
     * @return EmailTemplate
     * @throws TemplateNotFoundException nếu không tìm thấy template
     */
    public EmailTemplate getTemplate(EmailEventType eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type không được null");
        }

        try {
            String fileName = getTemplateFileName(eventType);
            String htmlContent = loadTemplateFromFile(fileName);

            EmailTemplate template = new EmailTemplate();
            template.setEventType(eventType);
            template.setSubject(getDefaultSubject(eventType));
            template.setHtmlContent(htmlContent);
            template.setActive(true);

            return template;

        } catch (Exception e) {
            logger.error("Lỗi khi lấy template cho {}: {}", eventType, e.getMessage(), e);
            throw new TemplateProcessingException("Lỗi khi lấy template: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy tên file template theo event type
     */
    private String getTemplateFileName(EmailEventType eventType) {
        switch (eventType) {
            case CONTACT_RESPONSE:
                return "contact-response.html";
            case COMPANY_NOTIFICATION:
                return "company-notification.html";
            case CV_CONFIRMATION:
                return "cv-confirmation.html";
            case CV_NOTIFICATION:
                return "cv-notification.html";
            default:
                throw new TemplateNotFoundException("Không có template cho event type: " + eventType);
        }
    }

    /**
     * Lấy subject mặc định theo event type
     */
    private String getDefaultSubject(EmailEventType eventType) {
        switch (eventType) {
            case CONTACT_RESPONSE:
                return "Xác nhận liên hệ - HRMS";
            case COMPANY_NOTIFICATION:
                return "Yêu cầu liên hệ mới - HRMS";
            case CV_CONFIRMATION:
                return "Xác nhận đơn ứng tuyển - HRMS";
            case CV_NOTIFICATION:
                return "Ứng viên mới nộp CV - HRMS";
            default:
                return "HRMS Notification";
        }
    }

    /**
     * Đọc template từ file
     */
    private String loadTemplateFromFile(String fileName) {
        try {
            String path = TEMPLATE_PATH + fileName;
            InputStream inputStream = getClass().getResourceAsStream(path);

            if (inputStream == null) {
                throw new TemplateNotFoundException("Không tìm thấy file template: " + path);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }

        } catch (Exception e) {
            logger.error("Lỗi khi đọc template file {}: {}", fileName, e.getMessage(), e);
            throw new TemplateProcessingException("Lỗi khi đọc template file: " + e.getMessage(), e);
        }
    }

    /**
     * Process template với data được cung cấp
     * Thay thế tất cả placeholders {{key}} bằng giá trị tương ứng
     * 
     * @param template EmailTemplate cần process
     * @param data     Map chứa key-value để thay thế placeholders
     * @return Nội dung HTML đã được process
     */
    public String processTemplate(EmailTemplate template, Map<String, Object> data) {
        if (template == null) {
            throw new IllegalArgumentException("Template không được null");
        }

        if (data == null) {
            data = new HashMap<>();
        }

        try {
            validateTemplate(template);

            String content = template.getHtmlContent();

            if (content == null || content.trim().isEmpty()) {
                throw new TemplateProcessingException("Template content rỗng");
            }

            // Thay thế placeholders
            content = replacePlaceholders(content, data);

            // Validate kết quả
            validateProcessedContent(content);

            return content;

        } catch (Exception e) {
            logger.error("Lỗi khi process template {}: {}", template.getId(), e.getMessage(), e);
            throw new TemplateProcessingException("Lỗi khi xử lý template: " + e.getMessage(), e);
        }
    }

    /**
     * Thay thế tất cả placeholders trong content
     * 
     * @param content Nội dung template
     * @param data    Map chứa data để thay thế
     * @return Nội dung đã được thay thế placeholders
     */
    private String replacePlaceholders(String content, Map<String, Object> data) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1).trim();
            Object value = data.get(placeholder);

            // Nếu không có value, giữ nguyên placeholder hoặc thay bằng empty string
            String replacement = value != null ? escapeHtml(value.toString()) : "";

            // Log warning nếu placeholder không có data
            if (value == null) {
                logger.warn("Placeholder '{}' không có giá trị trong data", placeholder);
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Escape HTML special characters để tránh XSS
     * 
     * @param text Text cần escape
     * @return Text đã được escape
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * Validate template trước khi process
     * 
     * @param template Template cần validate
     * @throws TemplateValidationException nếu template không hợp lệ
     */
    public void validateTemplate(EmailTemplate template) {
        if (template == null) {
            throw new TemplateValidationException("Template không được null");
        }

        if (template.getEventType() == null) {
            throw new TemplateValidationException("Event type không được null");
        }

        if (template.getSubject() == null || template.getSubject().trim().isEmpty()) {
            throw new TemplateValidationException("Subject không được rỗng");
        }

        if (template.getHtmlContent() == null || template.getHtmlContent().trim().isEmpty()) {
            throw new TemplateValidationException("HTML content không được rỗng");
        }

        if (!template.isActive()) {
            throw new TemplateValidationException("Template không active");
        }

        // Validate HTML structure cơ bản
        String content = template.getHtmlContent();
        if (!content.contains("<html") && !content.contains("<body")) {
            logger.warn("Template {} thiếu HTML structure cơ bản", template.getId());
        }
    }

    /**
     * Validate nội dung đã được process
     * 
     * @param content Nội dung cần validate
     * @throws TemplateValidationException nếu nội dung không hợp lệ
     */
    private void validateProcessedContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new TemplateValidationException("Processed content rỗng");
        }

        // Kiểm tra còn placeholders chưa được thay thế
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(content);
        if (matcher.find()) {
            logger.warn("Còn placeholders chưa được thay thế trong content");
        }
    }

    /**
     * Lấy tất cả placeholders trong template
     * 
     * @param template Template cần extract placeholders
     * @return Map chứa placeholder names
     */
    public Map<String, String> extractPlaceholders(EmailTemplate template) {
        Map<String, String> placeholders = new HashMap<>();

        if (template == null || template.getHtmlContent() == null) {
            return placeholders;
        }

        String content = template.getHtmlContent();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(content);

        while (matcher.find()) {
            String placeholder = matcher.group(1).trim();
            placeholders.put(placeholder, "");
        }

        return placeholders;
    }

    // Custom Exceptions

    public static class TemplateNotFoundException extends RuntimeException {
        public TemplateNotFoundException(String message) {
            super(message);
        }
    }

    public static class TemplateProcessingException extends RuntimeException {
        public TemplateProcessingException(String message) {
            super(message);
        }

        public TemplateProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class TemplateValidationException extends RuntimeException {
        public TemplateValidationException(String message) {
            super(message);
        }
    }
}
