package group4.hrms.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class cho password hashing và verification
 * Sử dụng BCrypt để hash password an toàn
 */
public class PasswordUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    
    // BCrypt work factor (độ phức tạp)
    private static final int WORK_FACTOR = 12;
    
    /**
     * Private constructor để ngăn tạo instance
     */
    private PasswordUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Hash password sử dụng BCrypt
     * 
     * @param plainPassword password gốc
     * @return hashed password
     * @throws IllegalArgumentException nếu password null hoặc rỗng
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
            logger.debug("Password hashed successfully");
            return hashedPassword;
            
        } catch (Exception e) {
            logger.error("Error hashing password: {}", e.getMessage(), e);
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Kiểm tra password với hash
     * 
     * @param plainPassword password gốc
     * @param hashedPassword password đã hash
     * @return true nếu password khớp
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            logger.warn("Password or hash is null");
            return false;
        }
        
        if (plainPassword.trim().isEmpty() || hashedPassword.trim().isEmpty()) {
            logger.warn("Password or hash is empty");
            return false;
        }
        
        try {
            boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
            logger.debug("Password check result: {}", matches);
            return matches;
            
        } catch (Exception e) {
            logger.error("Error checking password: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Kiểm tra password có đủ mạnh không
     * 
     * @param password password cần kiểm tra
     * @return true nếu password đủ mạnh
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Kiểm tra có ít nhất 1 chữ hoa
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        
        // Kiểm tra có ít nhất 1 chữ thường
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        
        // Kiểm tra có ít nhất 1 số
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        
        // Kiểm tra có ít nhất 1 ký tự đặc biệt
        boolean hasSpecialChar = password.chars().anyMatch(ch -> 
            "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0
        );
        
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
    
    /**
     * Tạo password ngẫu nhiên
     * 
     * @param length độ dài password
     * @return password ngẫu nhiên
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8");
        }
        
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String allChars = upperCase + lowerCase + digits + specialChars;
        
        StringBuilder password = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        
        // Đảm bảo có ít nhất 1 ký tự từ mỗi loại
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Thêm các ký tự ngẫu nhiên còn lại
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle password để tránh pattern cố định
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
    
    /**
     * Validate password theo quy tắc
     * 
     * @param password password cần validate
     * @throws IllegalArgumentException nếu password không hợp lệ
     */
    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được rỗng");
        }
        
        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        
        if (password.length() > 50) {
            throw new IllegalArgumentException("Mật khẩu không được quá 50 ký tự");
        }
        
        // Kiểm tra có chứa ký tự không hợp lệ
        if (password.contains(" ")) {
            throw new IllegalArgumentException("Mật khẩu không được chứa khoảng trắng");
        }
        
        // Kiểm tra password phổ biến
        String[] commonPasswords = {
            "123456", "password", "123456789", "12345678", "12345",
            "1234567", "1234567890", "qwerty", "abc123", "Password",
            "password123", "admin", "administrator"
        };
        
        for (String common : commonPasswords) {
            if (password.equalsIgnoreCase(common)) {
                throw new IllegalArgumentException("Mật khẩu quá đơn giản, vui lòng chọn mật khẩu khác");
            }
        }
    }
}