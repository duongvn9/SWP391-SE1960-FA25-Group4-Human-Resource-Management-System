package group4.hrms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity mapping bảng applications
 * Đơn ứng tuyển của candidates cho job postings
 * 
 * @author Group4
 */
public class Application {
    private Long id;
    private Long jobId;                // job posting
    private String status;             // new, reviewing, interviewed, hired, rejected
    private String note;               // ghi chú từ HR
    
    // Personal info
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String hometown;
    
    // Address
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    
    private String resumePath;         // CV file path
    
    // CCCD
    private String cccd;
    private LocalDate cccdIssuedDate;
    private String cccdIssuedPlace;
    private String cccdFrontPath;
    private String cccdBackPath;
    
    private LocalDateTime createdAt;
    
    // Constructors
    public Application() {
        this.status = "new";
        this.createdAt = LocalDateTime.now();
    }
    
    public Application(Long jobId, String fullName, String email) {
        this();
        this.jobId = jobId;
        this.fullName = fullName;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
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
    
    public LocalDate getDob() {
        return dob;
    }
    
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getHometown() {
        return hometown;
    }
    
    public void setHometown(String hometown) {
        this.hometown = hometown;
    }
    
    public String getAddressLine1() {
        return addressLine1;
    }
    
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getResumePath() {
        return resumePath;
    }
    
    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }
    
    public String getCccd() {
        return cccd;
    }
    
    public void setCccd(String cccd) {
        this.cccd = cccd;
    }
    
    public LocalDate getCccdIssuedDate() {
        return cccdIssuedDate;
    }
    
    public void setCccdIssuedDate(LocalDate cccdIssuedDate) {
        this.cccdIssuedDate = cccdIssuedDate;
    }
    
    public String getCccdIssuedPlace() {
        return cccdIssuedPlace;
    }
    
    public void setCccdIssuedPlace(String cccdIssuedPlace) {
        this.cccdIssuedPlace = cccdIssuedPlace;
    }
    
    public String getCccdFrontPath() {
        return cccdFrontPath;
    }
    
    public void setCccdFrontPath(String cccdFrontPath) {
        this.cccdFrontPath = cccdFrontPath;
    }
    
    public String getCccdBackPath() {
        return cccdBackPath;
    }
    
    public void setCccdBackPath(String cccdBackPath) {
        this.cccdBackPath = cccdBackPath;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    public boolean isNew() {
        return "new".equalsIgnoreCase(this.status);
    }
    
    public boolean isHired() {
        return "hired".equalsIgnoreCase(this.status);
    }
    
    public boolean isRejected() {
        return "rejected".equalsIgnoreCase(this.status);
    }
    
    public boolean hasResume() {
        return resumePath != null && !resumePath.trim().isEmpty();
    }
    
    public boolean hasCccdImages() {
        return cccdFrontPath != null && cccdBackPath != null;
    }
    
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null) address.append(addressLine1);
        if (addressLine2 != null) {
            if (address.length() > 0) address.append(", ");
            address.append(addressLine2);
        }
        if (city != null) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        if (state != null) {
            if (address.length() > 0) address.append(", ");
            address.append(state);
        }
        if (country != null) {
            if (address.length() > 0) address.append(", ");
            address.append(country);
        }
        return address.toString();
    }
    
    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", status='" + status + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}