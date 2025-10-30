package group4.hrms.dto;

import java.time.LocalDate;

/**
 * DTO cho việc cập nhật user profile
 * Chỉ chứa các trường có thể được user cập nhật
 * Không chứa logic validation
 */
public class UserProfileDto {
    
    // Editable fields
    private String cccd;
    private LocalDate cccdIssuedDate;
    private String cccdIssuedPlace;
    private LocalDate cccdExpireDate;
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
    
    public UserProfileDto() {
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
    
    public LocalDate getCccdExpireDate() {
        return cccdExpireDate;
    }
    
    public void setCccdExpireDate(LocalDate cccdExpireDate) {
        this.cccdExpireDate = cccdExpireDate;
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
    
    @Override
    public String toString() {
        return "UserProfileDto{" +
                "fullName='" + fullName + '\'' +
                ", emailCompany='" + emailCompany + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
