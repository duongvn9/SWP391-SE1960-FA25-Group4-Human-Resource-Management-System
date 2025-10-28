package group4.hrms.service;

import group4.hrms.dao.AccountDao;
import group4.hrms.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for change password business logic and validation
 */
public class ChangePasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordService.class);
    private final AccountDao accountDao;

    public ChangePasswordService() {
        this.accountDao = new AccountDao();
    }

    public ChangePasswordService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * Validate if password is not empty
     */
    public boolean isPasswordNotEmpty(String password) {
        return password != null && !password.trim().isEmpty();
    }

    /**
     * Validate if new password matches confirm password
     */
    public boolean isPasswordMatching(String newPassword, String confirmPassword) {
        if (newPassword == null || confirmPassword == null) {
            return false;
        }
        return newPassword.equals(confirmPassword);
    }

    /**
     * Validate password length - must be longer than 6 characters
     */
    public boolean isPasswordLongerThan6(String password) {
        if (password == null) {
            return false;
        }
        return password.length() > 6;
    }

    /**
     * Validate password contains at least 1 uppercase letter
     */
    public boolean hasUppercaseLetter(String password) {
        if (password == null) {
            return false;
        }
        return password.matches(".*[A-Z].*");
    }

    /**
     * Validate password contains at least 1 number
     */
    public boolean hasNumber(String password) {
        if (password == null) {
            return false;
        }
        return password.matches(".*[0-9].*");
    }

    /**
     * Validate password contains at least 1 special character
     */
    public boolean hasSpecialCharacter(String password) {
        if (password == null) {
            return false;
        }
        return password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    /**
     * Verify if current password is correct
     */
    public boolean isCurrentPasswordCorrect(String currentPassword, Long accountId) {
        String currentPasswordHash = accountDao.getPasswordHash(accountId);
        if (currentPasswordHash == null) {
            logger.error("Password hash not found for account_id: {}", accountId);
            return false;
        }
        return PasswordUtil.verifyPassword(currentPassword, currentPasswordHash);
    }

    /**
     * Check if new password is same as current password
     */
    public boolean isNewPasswordSameAsCurrent(String newPassword, Long accountId) {
        String currentPasswordHash = accountDao.getPasswordHash(accountId);
        if (currentPasswordHash == null) {
            return false;
        }
        return PasswordUtil.verifyPassword(newPassword, currentPasswordHash);
    }

    /**
     * Validate all password change requirements
     * Returns error message if validation fails, null if all validations pass
     */
    public String validatePasswordChange(String currentPassword, String newPassword, String confirmPassword, Long accountId) {
        // Validate current password not empty
        if (!isPasswordNotEmpty(currentPassword)) {
            logger.warn("Current password is empty");
            return "Current password is required";
        }

        // Validate new password not empty
        if (!isPasswordNotEmpty(newPassword)) {
            logger.warn("New password is empty");
            return "New password is required";
        }

        // Validate confirm password not empty
        if (!isPasswordNotEmpty(confirmPassword)) {
            logger.warn("Confirm password is empty");
            return "Confirm password is required";
        }

        // Check if new password matches confirm password
        if (!isPasswordMatching(newPassword, confirmPassword)) {
            logger.warn("New password and confirm password do not match");
            return "New password and confirm password do not match";
        }

        // Validate password length
        if (!isPasswordLongerThan6(newPassword)) {
            logger.warn("Password is not longer than 6 characters");
            return "Password must be longer than 6 characters";
        }

        // Validate uppercase letter
        if (!hasUppercaseLetter(newPassword)) {
            logger.warn("Password does not contain uppercase letter");
            return "Password must contain at least 1 uppercase letter";
        }

        // Validate number
        if (!hasNumber(newPassword)) {
            logger.warn("Password does not contain number");
            return "Password must contain at least 1 number";
        }

        // Validate special character
        if (!hasSpecialCharacter(newPassword)) {
            logger.warn("Password does not contain special character");
            return "Password must contain at least 1 special character";
        }

        // Verify current password
        if (!isCurrentPasswordCorrect(currentPassword, accountId)) {
            logger.warn("Current password is incorrect for account_id: {}", accountId);
            return "Current password is incorrect";
        }

        // Check if new password is same as current password
        if (isNewPasswordSameAsCurrent(newPassword, accountId)) {
            logger.warn("New password is same as current password for account_id: {}", accountId);
            return "New password must be different from current password";
        }

        // All validations passed
        logger.info("All password validations passed for account_id: {}", accountId);
        return null;
    }

    /**
     * Change password for account
     */
    public boolean changePassword(Long accountId, String newPassword) {
        String newPasswordHash = PasswordUtil.hashPassword(newPassword);
        boolean success = accountDao.updatePassword(accountId, newPasswordHash);
        
        if (success) {
            logger.info("Password changed successfully for account_id: {}", accountId);
        } else {
            logger.error("Failed to change password for account_id: {}", accountId);
        }
        
        return success;
    }
}
