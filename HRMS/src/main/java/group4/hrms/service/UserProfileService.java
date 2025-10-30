package group4.hrms.service;

import group4.hrms.dao.UserProfileDao;
import group4.hrms.dto.UserProfileDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Service class for user profile business logic and validation
 */
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);
    private final UserProfileDao userProfileDao;

    public UserProfileService() {
        this.userProfileDao = new UserProfileDao();
    }

    public UserProfileService(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    /**
     * Validate full name
     * Rules: Required, max 100 chars, only letters and spaces - no numbers
     */
    public boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false; // Required field
        }
        return fullName.trim().length() <= 100 && fullName.matches("^[a-zA-ZÀ-ỹ\\s]+$");
    }

    /**
     * Validate phone number format
     * Rules: Required, must be exactly 10 digits
     */
    public boolean isPhone10Digits(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false; // Required field
        }
        return phone.matches("^\\d{10}$");
    }

    /**
     * Check if phone exists for other user
     */
    public boolean isPhoneExistsForOtherUser(String phone, Long currentUserId) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return userProfileDao.isPhoneExistsForOtherUser(phone, currentUserId);
    }

    /**
     * Validate date of birth
     * Rules: Required, must be between 01/01/1900 and today
     */
    public boolean isValidDob(LocalDate dob) {
        if (dob == null) {
            return false; // Required field
        }
        LocalDate minDate = LocalDate.of(1900, 1, 1);
        LocalDate today = LocalDate.now();
        return !dob.isAfter(today) && !dob.isBefore(minDate);
    }

    /**
     * Validate age from date of birth
     * Rules: Required, age must be between 18 and 60 years old
     */
    public boolean isAgeBetween18And60(LocalDate dob) {
        if (dob == null) {
            return false; // Required field
        }
        LocalDate today = LocalDate.now();
        int age = today.getYear() - dob.getYear();

        // Adjust age if birthday hasn't occurred this year yet
        if (today.getMonthValue() < dob.getMonthValue() ||
                (today.getMonthValue() == dob.getMonthValue() && today.getDayOfMonth() < dob.getDayOfMonth())) {
            age--;
        }

        return age >= 18 && age <= 60;
    }

    /**
     * Validate gender
     * Rules: Required, must be "male", "female", or "other" (case insensitive)
     */
    public boolean isValidGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            return false; // Required field
        }
        String genderLower = gender.trim().toLowerCase();
        return genderLower.equals("male") || genderLower.equals("female") || genderLower.equals("other");
    }

    /**
     * Validate hometown - no special characters
     * Rules: Optional, max 50 chars, no special characters
     */
    public boolean isValidHometown(String hometown) {
        if (hometown == null || hometown.trim().isEmpty()) {
            return true; // Optional field
        }
        if (hometown.length() > 50) {
            return false;
        }
        return !hometown.matches(".*[^a-zA-Z0-9À-ỹ\\s,.-].*");
    }

    /**
     * Validate CCCD format
     * Rules: Required, must be exactly 12 digits
     */
    public boolean isCCCD12Digits(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return false; // Required field
        }
        return cccd.matches("^[0-9]{12}$");
    }

    /**
     * Check if CCCD exists for other user
     */
    public boolean isCCCDExistsForOtherUser(String cccd, Long currentUserId) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return false;
        }
        return userProfileDao.isCccdExistsForOtherUser(cccd, currentUserId);
    }

    /**
     * Validate CCCD issued date
     * Rules: Required, must be from 01/01/2021 onwards and not in the future
     */
    public boolean isCCCDIssuedDateFrom2021(LocalDate cccdIssuedDate) {
        if (cccdIssuedDate == null) {
            return false; // Required field
        }
        LocalDate minIssuedDate = LocalDate.of(2021, 1, 1);
        LocalDate today = LocalDate.now();
        return !cccdIssuedDate.isBefore(minIssuedDate) && !cccdIssuedDate.isAfter(today);
    }

    /**
     * Validate CCCD issued place - no special characters
     * Rules: Required, max 100 chars, no special characters
     */
    public boolean isValidCCCDIssuedPlace(String cccdIssuedPlace) {
        if (cccdIssuedPlace == null || cccdIssuedPlace.trim().isEmpty()) {
            return false; // Required field
        }
        if (cccdIssuedPlace.length() > 100) {
            return false;
        }
        return !cccdIssuedPlace.matches(".*[^a-zA-Z0-9À-ỹ\\s,.-].*");
    }

    /**
     * Validate CCCD expire date
     * Rules: Required, must be in the future
     */
    public boolean isCCCDExpireDateInFuture(LocalDate cccdExpireDate) {
        if (cccdExpireDate == null) {
            return false; // Required field
        }
        LocalDate today = LocalDate.now();
        return cccdExpireDate.isAfter(today);
    }

    /**
     * Validate address line 1 - no special characters
     * Rules: Optional, max 100 chars, no special characters
     */
    public boolean isValidAddressLine1(String addressLine1) {
        if (addressLine1 == null || addressLine1.trim().isEmpty()) {
            return true; // Optional field
        }
        if (addressLine1.length() > 100) {
            return false;
        }
        return !addressLine1.matches(".*[^a-zA-Z0-9À-ỹ\\s,.-].*");
    }

    /**
     * Validate address line 2 - no special characters
     * Rules: Optional, max 100 chars, no special characters
     */
    public boolean isValidAddressLine2(String addressLine2) {
        if (addressLine2 == null || addressLine2.trim().isEmpty()) {
            return true; // Optional field
        }
        if (addressLine2.length() > 100) {
            return false;
        }
        return !addressLine2.matches(".*[^a-zA-Z0-9À-ỹ\\s,.-].*");
    }

    /**
     * Validate city
     * Rules: Optional, max 50 chars
     */
    public boolean isValidCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return true; // Optional field
        }
        return city.length() <= 50;
    }

    /**
     * Validate postal code
     * Rules: Optional, 5-10 digits
     */
    public boolean isValidPostalCode(String postalCode) {
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return true; // Optional field
        }
        return postalCode.matches("^[0-9]{5,10}$");
    }

    /**
     * Validate entire profile DTO
     * Returns error message if validation fails, null if all validations pass
     */
    public String validateProfile(UserProfileDto dto, Long currentUserId) {
        // Validate full name
        if (!isValidFullName(dto.getFullName())) {
            logger.warn("Full name validation failed: {}", dto.getFullName());
            return "Full name must not exceed 100 characters and cannot contain digits or special characters";
        }

        // Validate phone format
        if (!isPhone10Digits(dto.getPhone())) {
            logger.warn("Phone validation failed: {}", dto.getPhone());
            return "Phone number must be exactly 10 digits";
        }

        // Check phone uniqueness
        if (isPhoneExistsForOtherUser(dto.getPhone(), currentUserId)) {
            logger.warn("Phone already exists: {}", dto.getPhone());
            return "Phone number already exists";
        }

        // Validate date of birth
        if (!isValidDob(dto.getDob())) {
            logger.warn("DOB validation failed: {}", dto.getDob());
            return "Date of birth must be between 01/01/1900 and today";
        }

        // Validate age (18-60)
        if (!isAgeBetween18And60(dto.getDob())) {
            logger.warn("Age validation failed: {}", dto.getDob());
            return "Age must be between 18 and 60 years old";
        }

        // Validate gender
        if (!isValidGender(dto.getGender())) {
            logger.warn("Gender validation failed: {}", dto.getGender());
            return "Invalid gender value";
        }

        // Validate hometown
        if (!isValidHometown(dto.getHometown())) {
            logger.warn("Hometown validation failed: {}", dto.getHometown());
            return "Hometown must not exceed 50 characters and cannot contain special characters";
        }

        // Validate CCCD format
        if (!isCCCD12Digits(dto.getCccd())) {
            logger.warn("CCCD validation failed: {}", dto.getCccd());
            return "Citizen ID must be exactly 12 digits";
        }

        // Check CCCD uniqueness
        if (isCCCDExistsForOtherUser(dto.getCccd(), currentUserId)) {
            logger.warn("CCCD already exists: {}", dto.getCccd());
            return "CCCD already exists";
        }

        // Validate CCCD issued date
        if (!isCCCDIssuedDateFrom2021(dto.getCccdIssuedDate())) {
            logger.warn("CCCD issued date validation failed: {}", dto.getCccdIssuedDate());
            return "CCCD issued date must be from 01/01/2021 to now";
        }

        // Validate CCCD issued place
        if (!isValidCCCDIssuedPlace(dto.getCccdIssuedPlace())) {
            logger.warn("CCCD issued place validation failed: {}", dto.getCccdIssuedPlace());
            return "CCCD issued place must not exceed 100 characters and cannot contain special characters";
        }

        // Validate CCCD expire date
        if (!isCCCDExpireDateInFuture(dto.getCccdExpireDate())) {
            logger.warn("CCCD expire date validation failed: {}", dto.getCccdExpireDate());
            return "CCCD expire date must be in the future";
        }

        // Validate address line 1
        if (!isValidAddressLine1(dto.getAddressLine1())) {
            logger.warn("Address line 1 validation failed: {}", dto.getAddressLine1());
            return "Address line 1 must not exceed 100 characters and cannot contain special characters";
        }

        // Validate address line 2
        if (!isValidAddressLine2(dto.getAddressLine2())) {
            logger.warn("Address line 2 validation failed: {}", dto.getAddressLine2());
            return "Address line 2 must not exceed 100 characters and cannot contain special characters";
        }

        // Validate city
        if (!isValidCity(dto.getCity())) {
            logger.warn("City validation failed: {}", dto.getCity());
            return "City must not exceed 50 characters";
        }

        // Validate postal code
        if (!isValidPostalCode(dto.getPostalCode())) {
            logger.warn("Postal code validation failed: {}", dto.getPostalCode());
            return "Postal code must be 5-10 digits";
        }

        // All validations passed
        logger.info("All validations passed for user_id: {}", currentUserId);
        return null;
    }
}
