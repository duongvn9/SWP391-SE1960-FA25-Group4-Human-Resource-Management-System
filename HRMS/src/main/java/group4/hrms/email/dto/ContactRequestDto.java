package group4.hrms.email.dto;

/**
 * DTO cho contact form submission
 * 
 * @author Group4
 */
public class ContactRequestDto {
    private String fullName;
    private String email;
    private String phone;
    private String contactType;
    private String subject;
    private String message;

    // Constructors
    public ContactRequestDto() {
    }

    public ContactRequestDto(String fullName, String email, String phone, String contactType,
            String subject, String message) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.contactType = contactType;
        this.subject = subject;
        this.message = message;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ContactRequestDto{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", contactType='" + contactType + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
