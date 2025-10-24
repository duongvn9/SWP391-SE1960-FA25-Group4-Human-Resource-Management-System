package group4.hrms.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*(),.?\":{}|<>].*");
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_AGE = 18;

    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validate if a date of birth indicates the person is at least the minimum age
     * 
     * @param dateOfBirth The date of birth to validate
     * @return true if age is valid (>= 18 years), false otherwise
     */
    public static boolean isAgeValid(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        int age = Period.between(dateOfBirth, today).getYears();
        return age >= MIN_AGE;
    }

    /**
     * Validate if a phone number has the correct format (10 digits starting with 0)
     * 
     * @param phone The phone number to validate
     * @return true if phone is valid (10 digits starting with 0), false otherwise
     */
    public static boolean isPhoneValid(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate if a password meets strength requirements:
     * - At least 6 characters long
     * - Contains at least one uppercase letter
     * - Contains at least one special character
     * 
     * @param password The password to validate
     * @return true if password meets all requirements, false otherwise
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        return UPPERCASE_PATTERN.matcher(password).matches()
                && SPECIAL_CHAR_PATTERN.matcher(password).matches();
    }

    /**
     * Validate if a gender value is valid (male or female)
     * 
     * @param gender The gender value to validate
     * @return true if gender is "male" or "female" (case-insensitive), false
     *         otherwise
     */
    public static boolean isGenderValid(String gender) {
        return gender != null &&
                ("male".equalsIgnoreCase(gender) || "female".equalsIgnoreCase(gender));
    }
}
