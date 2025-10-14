package group4.hrms.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO cho việc cập nhật user profile
 * Chỉ chứa các trường có thể được user cập nhật
 */
public class UserProfileDto {
    
    // Editable fields
    private String cccd;
    private LocalDate cccdIssuedDate;
    private String cccdIssuedPlace;
    private String fullName;
    private String gender;
    private String emailCompany;
    private String phone;
    private String hometown;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String bankInfo;
    private LocalDate dob;
    
    // Validation errors
    private List<String> errors;
    
    public UserProfileDto() {
        this.errors = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getCccd() {
        return cccd;
    }
    
    public void setCccd(String cccd) {
        this.cccd = cccd;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getEmailCompany() {
        return emailCompany;
    }
    
    public void setEmailCompany(String emailCompany) {
        this.emailCompany = emailCompany;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
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
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getBankInfo() {
        return bankInfo;
    }
    
    public void setBankInfo(String bankInfo) {
        this.bankInfo = bankInfo;
    }
    
    public LocalDate getDob() {
        return dob;
    }
    
    public void setDob(LocalDate dob) {
        this.dob = dob;
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
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
    
    /**
     * Validate all fields
     */
    public boolean validate() {
        errors.clear();
        
        // Validate full name
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.add("Full name is required");
        } else if (fullName.trim().length() < 2) {
            errors.add("Full name must be at least 2 characters");
        } else if (fullName.length() > 255) {
            errors.add("Full name must not exceed 255 characters");
        }
        
        // Validate email
        if (emailCompany == null || emailCompany.trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!isValidEmail(emailCompany)) {
            errors.add("Invalid email format");
        }
        
        // Validate CCCD (optional)
        if (cccd != null && !cccd.trim().isEmpty()) {
            if (!cccd.matches("\\d+")) {
                errors.add("CCCD must contain only numbers");
            } else if (cccd.length() != 9 && cccd.length() != 12) {
                errors.add("CCCD must be 9 or 12 digits");
            }
        }
        
        // Validate phone (optional)
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("\\d+")) {
                errors.add("Phone must contain only numbers");
            } else if (phone.length() < 10) {
                errors.add("Phone must be at least 10 digits");
            } else if (phone.length() > 11) {
                errors.add("Phone must not exceed 11 digits");
            }
        }
        
        // Validate gender (optional)
        if (gender != null && !gender.trim().isEmpty()) {
            if (!gender.equals("male") && !gender.equals("female") && !gender.equals("others")) {
                errors.add("Invalid gender value");
            }
        }
        
        // Validate address fields length
        if (addressLine1 != null && addressLine1.length() > 255) {
            errors.add("Address line 1 must not exceed 255 characters");
        }
        if (addressLine2 != null && addressLine2.length() > 255) {
            errors.add("Address line 2 must not exceed 255 characters");
        }
        if (city != null && city.length() > 100) {
            errors.add("City must not exceed 100 characters");
        }
        if (country != null && country.length() > 100) {
            errors.add("Country must not exceed 100 characters");
        }
        if (hometown != null && hometown.length() > 255) {
            errors.add("Hometown must not exceed 255 characters");
        }
        
        return errors.isEmpty();
    }
    
    /**
     * Simple email validation
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    @Override
    public String toString() {
        return "UserProfileDto{" +
                "fullName='" + fullName + '\'' +
                ", emailCompany='" + emailCompany + '\'' +
                ", phone='" + phone + '\'' +
                ", errors=" + errors.size() +
                '}';
    }
}
