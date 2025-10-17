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
     * Validate all fields according to requirements
     */
    public boolean validate() {
        errors.clear();
        
        // 1. Validate Full Name (Optional, max 100 chars, only letters and spaces - no numbers)
        if (fullName != null && !fullName.trim().isEmpty()) {
            if (fullName.trim().length() > 100) {
                errors.add("Full name must not exceed 100 characters");
            } else if (!fullName.matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
                errors.add("Full name can only contain letters and spaces (no numbers or special characters)");
            }
        }
        
        // 2. Validate Phone Number (Optional, 10-11 digits if provided)
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("^[0-9]{10,11}$")) {
                errors.add("Phone number must be 10-11 digits");
            }
        }
        
        // 3. Validate Date of Birth (Optional, must be <= current date and >= 01/01/1900 if provided)
        if (dob != null) {
            LocalDate minDate = LocalDate.of(1900, 1, 1);
            LocalDate today = LocalDate.now();
            if (dob.isAfter(today)) {
                errors.add("Date of birth must not be in the future");
            } else if (dob.isBefore(minDate)) {
                errors.add("Date of birth must be after 01/01/1900");
            }
        }
        
        // 4. Validate Gender (Optional, must be valid value if provided)
        if (gender != null && !gender.trim().isEmpty()) {
            if (!gender.equals("male") && !gender.equals("female") && !gender.equals("other")) {
                errors.add("Invalid gender value");
            }
        }
        
        // 5. Validate Hometown (Optional, max 50 chars)
        if (hometown != null && hometown.length() > 50) {
            errors.add("Hometown must not exceed 50 characters");
        }
        
        // 6. Validate CCCD (Optional, 12 digits if provided)
        if (cccd != null && !cccd.trim().isEmpty()) {
            if (!cccd.matches("^[0-9]{12}$")) {
                errors.add("Citizen ID (CCCD) must be exactly 12 digits");
            }
        }
        
        // 7. Validate CCCD Issued Date (Optional, must be <= current date if provided)
        if (cccdIssuedDate != null) {
            if (cccdIssuedDate.isAfter(LocalDate.now())) {
                errors.add("CCCD issued date must not be in the future");
            }
        }
        
        // 8. Validate CCCD Issued Place (Optional, max 100 chars if provided)
        if (cccdIssuedPlace != null && !cccdIssuedPlace.trim().isEmpty()) {
            if (cccdIssuedPlace.length() > 100) {
                errors.add("CCCD issued place must not exceed 100 characters");
            }
        }
        
        // 9. Validate Country (Optional)
        // No validation needed - can be empty
        
        // 10. Validate Address Line 1 (Optional, max 100 chars if provided)
        if (addressLine1 != null && !addressLine1.trim().isEmpty()) {
            if (addressLine1.length() > 100) {
                errors.add("Address line 1 must not exceed 100 characters");
            }
        }
        
        // 11. Validate Address Line 2 (Optional, max 100 chars)
        if (addressLine2 != null && addressLine2.length() > 100) {
            errors.add("Address line 2 must not exceed 100 characters");
        }
        
        // 12. Validate City (Optional, max 50 chars if provided)
        if (city != null && !city.trim().isEmpty()) {
            if (city.length() > 50) {
                errors.add("City must not exceed 50 characters");
            }
        }
        
        // 13. Validate State (Optional, max 50 chars)
        if (state != null && state.length() > 50) {
            errors.add("State must not exceed 50 characters");
        }
        
        // 14. Validate Postal Code (Optional, 5-10 digits)
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            if (!postalCode.matches("^[0-9]{5,10}$")) {
                errors.add("Postal code must be 5-10 digits");
            }
        }
        
        // 15. Validate Email (Required for system, but not editable by user)
        if (emailCompany == null || emailCompany.trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!isValidEmail(emailCompany)) {
            errors.add("Invalid email format");
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
