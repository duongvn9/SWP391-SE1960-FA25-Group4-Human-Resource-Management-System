package group4.hrms.dto;

import group4.hrms.model.Application;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO cho Application entity
 * 
 * @author Group4
 */
public class ApplicationDto {
    private Long id;
    private Long jobId;
    private String jobTitle;           // Join từ job_postings
    private String status;
    private String note;
    
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
    private String fullAddress;        // Computed field
    
    private String resumePath;
    private boolean hasResume;         // Computed field
    
    // CCCD
    private String cccd;
    private LocalDate cccdIssuedDate;
    private String cccdIssuedPlace;
    private String cccdFrontPath;
    private String cccdBackPath;
    private boolean hasCccdImages;     // Computed field
    
    private LocalDateTime createdAt;
    
    // Constructors
    public ApplicationDto() {}
    
    public ApplicationDto(Application application) {
        if (application != null) {
            this.id = application.getId();
            this.jobId = application.getJobId();
            this.status = application.getStatus();
            this.note = application.getNote();
            this.fullName = application.getFullName();
            this.email = application.getEmail();
            this.phone = application.getPhone();
            this.dob = application.getDob();
            this.gender = application.getGender();
            this.hometown = application.getHometown();
            this.addressLine1 = application.getAddressLine1();
            this.addressLine2 = application.getAddressLine2();
            this.city = application.getCity();
            this.state = application.getState();
            this.postalCode = application.getPostalCode();
            this.country = application.getCountry();
            this.resumePath = application.getResumePath();
            this.cccd = application.getCccd();
            this.cccdIssuedDate = application.getCccdIssuedDate();
            this.cccdIssuedPlace = application.getCccdIssuedPlace();
            this.cccdFrontPath = application.getCccdFrontPath();
            this.cccdBackPath = application.getCccdBackPath();
            this.createdAt = application.getCreatedAt();
            
            // Computed fields
            this.fullAddress = application.getFullAddress();
            this.hasResume = application.hasResume();
            this.hasCccdImages = application.hasCccdImages();
        }
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
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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
    
    public String getFullAddress() {
        return fullAddress;
    }
    
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
    
    public String getResumePath() {
        return resumePath;
    }
    
    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }
    
    public boolean isHasResume() {
        return hasResume;
    }
    
    public void setHasResume(boolean hasResume) {
        this.hasResume = hasResume;
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
    
    public boolean isHasCccdImages() {
        return hasCccdImages;
    }
    
    public void setHasCccdImages(boolean hasCccdImages) {
        this.hasCccdImages = hasCccdImages;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Convenience methods
    public boolean isNew() {
        return "new".equalsIgnoreCase(this.status);
    }
    
    public boolean isHired() {
        return "hired".equalsIgnoreCase(this.status);
    }
    
    public boolean isRejected() {
        return "rejected".equalsIgnoreCase(this.status);
    }
    
    /**
     * Convert DTO back to entity
     */
    public Application toEntity() {
        Application application = new Application();
        application.setId(this.id);
        application.setJobId(this.jobId);
        application.setStatus(this.status);
        application.setNote(this.note);
        application.setFullName(this.fullName);
        application.setEmail(this.email);
        application.setPhone(this.phone);
        application.setDob(this.dob);
        application.setGender(this.gender);
        application.setHometown(this.hometown);
        application.setAddressLine1(this.addressLine1);
        application.setAddressLine2(this.addressLine2);
        application.setCity(this.city);
        application.setState(this.state);
        application.setPostalCode(this.postalCode);
        application.setCountry(this.country);
        application.setResumePath(this.resumePath);
        application.setCccd(this.cccd);
        application.setCccdIssuedDate(this.cccdIssuedDate);
        application.setCccdIssuedPlace(this.cccdIssuedPlace);
        application.setCccdFrontPath(this.cccdFrontPath);
        application.setCccdBackPath(this.cccdBackPath);
        application.setCreatedAt(this.createdAt);
        
        return application;
    }
}