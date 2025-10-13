package group4.hrms.service;

import group4.hrms.dao.AccountDao;
import group4.hrms.dao.AuthIdentityDao;
import group4.hrms.dao.AuthLocalCredentialsDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Account;
import group4.hrms.model.AuthIdentity;
import group4.hrms.model.AuthLocalCredentials;
import group4.hrms.model.User;
import group4.hrms.util.GoogleOAuthUtil;
import group4.hrms.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class để xử lý authentication logic
 */
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AccountDao accountDao;
    private final UserDao userDao;
    private final AuthIdentityDao authIdentityDao;
    private final AuthLocalCredentialsDao authLocalCredentialsDao;

    public AuthService() {
        this.accountDao = new AccountDao();
        this.userDao = new UserDao();
        this.authIdentityDao = new AuthIdentityDao();
        this.authLocalCredentialsDao = new AuthLocalCredentialsDao();
    }

    /**
     * Result class cho authentication
     */
    public static class AuthResult {
        private boolean success;
        private String message;
        private Account account;
        private User user;

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public AuthResult(boolean success, String message, Account account, User user) {
            this.success = success;
            this.message = message;
            this.account = account;
            this.user = user;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Account getAccount() { return account; }
        public User getUser() { return user; }

        public void setAccount(Account account) { this.account = account; }
        public void setUser(User user) { this.user = user; }
    }

    /**
     * Authenticate user bằng username/password
     */
    public AuthResult authenticateLocal(String username, String password) {
        logger.info("=== START: Local Authentication for username: {} ===", username);

        try {
            // Tìm account bằng username
            logger.debug("Looking up account by username: {}", username);
            Optional<Account> accountOpt = accountDao.findByUsername(username);
            if (!accountOpt.isPresent()) {
                logger.warn("❌ Account not found for username: {}", username);
                return new AuthResult(false, "Invalid username or password");
            }

            Account account = accountOpt.get();
            logger.info("✓ Account found: ID={}, Username={}, Status={}, UserID={}",
                account.getId(), account.getUsername(), account.getStatus(), account.getUserId());

            // Kiểm tra account status
            if (!Account.Status.ACTIVE.getValue().equals(account.getStatus())) {
                logger.warn("❌ Account is not active: {} - Status: {}", username, account.getStatus());
                return new AuthResult(false, "Account is not active");
            }
            logger.debug("✓ Account status is ACTIVE");

            // Tìm local credentials
            logger.debug("Looking up local identity for account: {}", account.getId());
            Optional<AuthIdentity> identityOpt = authIdentityDao.findByAccountIdAndProvider(
                    account.getId(), "local");

            if (!identityOpt.isPresent()) {
                logger.warn("❌ Local identity not found for account: {}", account.getId());
                return new AuthResult(false, "Invalid username or password");
            }

            AuthIdentity identity = identityOpt.get();
            logger.debug("✓ Found local identity: ID={}", identity.getId());

            logger.debug("Looking up local credentials for identity: {}", identity.getId());
            Optional<AuthLocalCredentials> credentialsOpt =
                    authLocalCredentialsDao.findByIdentityId(identity.getId());

            if (!credentialsOpt.isPresent()) {
                logger.warn("❌ Local credentials not found for identity: {}", identity.getId());
                return new AuthResult(false, "Invalid username or password");
            }

            AuthLocalCredentials credentials = credentialsOpt.get();
            logger.debug("✓ Found local credentials");

            // Verify password
            logger.debug("Verifying password...");
            if (!PasswordUtil.verifyPassword(password, credentials.getPasswordHash())) {
                logger.warn("❌ Invalid password for username: {}", username);
                return new AuthResult(false, "Invalid username or password");
            }
            logger.debug("✓ Password verified");

            // Lấy thông tin User
            logger.debug("Looking up user by ID: {}", account.getUserId());
            if (account.getUserId() == null) {
                logger.error("❌ Account has NULL user_id! Account ID: {}", account.getId());
                return new AuthResult(false, "User information not found");
            }

            Optional<User> userOpt = userDao.findById(account.getUserId());
            if (!userOpt.isPresent()) {
                logger.error("❌ User not found for account: {} (User ID: {})",
                    account.getId(), account.getUserId());
                return new AuthResult(false, "User information not found");
            }

            User user = userOpt.get();
            logger.info("✓ User found: ID={}, FullName={}, EmailCompany={}, Status={}",
                user.getId(), user.getFullName(), user.getEmailCompany(), user.getStatus());

            // Update last login time
            logger.debug("Updating last login time for account: {}", account.getId());
            account.setLastLoginAt(LocalDateTime.now());
            accountDao.update(account);
            logger.debug("✓ Last login time updated");

            logger.info("=== SUCCESS: Local authentication successful for username: {} ===", username);
            return new AuthResult(true, "Login successful", account, user);

        } catch (Exception e) {
            logger.error("=== FAILED: Error during local authentication for username: {} ===", username, e);
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            return new AuthResult(false, "Authentication error occurred");
        }
    }

    /**
     * Authenticate user bằng Google OAuth
     */
    public AuthResult authenticateGoogle(String email, GoogleOAuthUtil.GoogleUserInfo googleUserInfo) {
        logger.info("=== START: Google Authentication for email: {} ===", email);

        try {
            // Tìm account bằng email_login
            logger.debug("Looking up account by email: {}", email);
            Optional<Account> accountOpt = accountDao.findByEmailLogin(email);
            if (!accountOpt.isPresent()) {
                logger.warn("❌ Account not found for email: {}", email);
                return new AuthResult(false, "Email is not registered in the system");
            }

            Account account = accountOpt.get();
            logger.info("✓ Account found: ID={}, Username={}, Status={}",
                account.getId(), account.getUsername(), account.getStatus());

            // Kiểm tra account status
            if (!Account.Status.ACTIVE.getValue().equals(account.getStatus())) {
                logger.warn("❌ Account is not active: {} - Status: {}", email, account.getStatus());
                return new AuthResult(false, "Account is not active");
            }
            logger.debug("✓ Account status is ACTIVE");

            // Tìm hoặc tạo Google identity
            logger.debug("Looking up Google identity for account: {}", account.getId());
            Optional<AuthIdentity> identityOpt = authIdentityDao.findByAccountIdAndProvider(
                    account.getId(), "google");

            AuthIdentity identity;
            if (!identityOpt.isPresent()) {
                // Tạo mới Google identity
                logger.info("Creating new Google identity for account: {}", account.getId());
                identity = new AuthIdentity();
                identity.setAccountId(account.getId());
                identity.setProvider("google");
                identity.setProviderUserId(googleUserInfo.getId());
                identity.setEmail(googleUserInfo.getEmail());
                identity.setEmailVerified(googleUserInfo.isEmailVerified());
                identity = authIdentityDao.create(identity);
                logger.info("✓ Created new Google identity: ID={}", identity.getId());
            } else {
                identity = identityOpt.get();
                logger.debug("✓ Found existing Google identity: ID={}", identity.getId());
                // Update Google user info
                identity.setProviderUserId(googleUserInfo.getId());
                identity.setEmail(googleUserInfo.getEmail());
                identity.setEmailVerified(googleUserInfo.isEmailVerified());
                authIdentityDao.update(identity);
                logger.debug("✓ Updated Google identity");
            }

            // Lấy thông tin User
            logger.debug("Looking up user by ID: {}", account.getUserId());
            Optional<User> userOpt = userDao.findById(account.getUserId());
            if (!userOpt.isPresent()) {
                logger.error("❌ User not found for account: {} (User ID: {})",
                    account.getId(), account.getUserId());
                return new AuthResult(false, "User information not found");
            }

            User user = userOpt.get();
            logger.info("✓ User found: ID={}, FullName={}, EmailCompany={}",
                user.getId(), user.getFullName(), user.getEmailCompany());

            // Update last login time
            logger.debug("Updating last login time for account: {}", account.getId());
            account.setLastLoginAt(LocalDateTime.now());
            accountDao.update(account);
            logger.debug("✓ Last login time updated");

            logger.info("=== SUCCESS: Google authentication successful for email: {} ===", email);
            return new AuthResult(true, "Google login successful", account, user);

        } catch (Exception e) {
            logger.error("=== FAILED: Error during Google authentication for email: {} ===", email, e);
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            return new AuthResult(false, "Authentication error occurred");
        }
    }

    /**
     * Tạo local credentials cho account (khi admin tạo account)
     */
    public boolean createLocalCredentials(Long accountId, String password) {
        try {
            logger.info("Creating local credentials for account: {}", accountId);

            // Tạo AuthIdentity cho local provider
            AuthIdentity identity = new AuthIdentity();
            identity.setAccountId(accountId);
            identity.setProvider("local");
            identity.setProviderUserId(accountId.toString()); // Use account ID as provider user ID
            identity.setEmailVerified(false);
            identity = authIdentityDao.create(identity);

            // Tạo AuthLocalCredentials
            AuthLocalCredentials credentials = new AuthLocalCredentials();
            credentials.setIdentityId(identity.getId());
            credentials.setPasswordHash(PasswordUtil.hashPassword(password));
            credentials.setPasswordUpdatedAt(LocalDateTime.now());
            authLocalCredentialsDao.create(credentials);

            logger.info("Local credentials created successfully for account: {}", accountId);
            return true;

        } catch (Exception e) {
            logger.error("Error creating local credentials for account: " + accountId, e);
            return false;
        }
    }

    /**
     * Kiểm tra email có tồn tại trong hệ thống không
     */
    public boolean isEmailRegistered(String email) {
        try {
            return accountDao.findByEmailLogin(email).isPresent();
        } catch (Exception e) {
            logger.error("Error checking email registration: " + email, e);
            return false;
        }
    }

    /**
     * Kiểm tra username có tồn tại trong hệ thống không
     */
    public boolean isUsernameExists(String username) {
        try {
            return accountDao.findByUsername(username).isPresent();
        } catch (Exception e) {
            logger.error("Error checking username existence: " + username, e);
            return false;
        }
    }
}