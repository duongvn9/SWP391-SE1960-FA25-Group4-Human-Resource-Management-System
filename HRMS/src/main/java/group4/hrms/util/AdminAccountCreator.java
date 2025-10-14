package group4.hrms.util;

import group4.hrms.dao.AccountDao;
import group4.hrms.dao.AuthIdentityDao;
import group4.hrms.dao.AuthLocalCredentialsDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Account;
import group4.hrms.model.AuthIdentity;
import group4.hrms.model.AuthLocalCredentials;
import group4.hrms.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Utility class to create admin account
 * Run this once to create the default admin account
 */
public class AdminAccountCreator {

    private static final Logger logger = LoggerFactory.getLogger(AdminAccountCreator.class);

    public static void main(String[] args) {
        try {
            createAdminAccount();
        } catch (Exception e) {
            logger.error("Failed to create admin account", e);
            System.err.println("Failed to create admin account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createAdminAccount() {
        UserDao userDao = new UserDao();
        AccountDao accountDao = new AccountDao();
        AuthIdentityDao authIdentityDao = new AuthIdentityDao();
        AuthLocalCredentialsDao authLocalCredentialsDao = new AuthLocalCredentialsDao();

        try {
            // Check if admin account already exists
            Optional<Account> existingAccount = accountDao.findByUsername("admin");
            if (existingAccount.isPresent()) {
                logger.info("Admin account already exists");
                System.out.println("Admin account already exists!");
                return;
            }

            // Step 1: Create User
            User user = new User();
            user.setEmployeeCode("ADMIN001");
            user.setFullName("System Administrator");
            user.setEmailCompany("admin@hrms.local");
            user.setStatus("active");
            user.setDateJoined(LocalDate.now());

            Optional<User> createdUserOpt = userDao.create(user);
            if (!createdUserOpt.isPresent()) {
                throw new RuntimeException("Failed to create user");
            }
            User createdUser = createdUserOpt.get();
            logger.info("Created user with ID: {}", createdUser.getId());

            // Step 2: Create Account
            Account account = new Account();
            account.setUserId(createdUser.getId());
            account.setUsername("admin");
            account.setEmailLogin("admin@hrms.local");
            account.setStatus(Account.Status.ACTIVE.getValue());
            account.setFailedAttempts(0);

            Account createdAccount = accountDao.create(account);
            if (createdAccount == null || createdAccount.getId() == null) {
                throw new RuntimeException("Failed to create account");
            }
            logger.info("Created account with ID: {}", createdAccount.getId());

            // Step 3: Create Auth Identity (local provider)
            AuthIdentity identity = new AuthIdentity();
            identity.setAccountId(createdAccount.getId());
            identity.setProvider("local");
            identity.setProviderUserId(createdAccount.getId().toString());
            identity.setEmail("admin@hrms.local");
            identity.setEmailVerified(true);

            AuthIdentity createdIdentity = authIdentityDao.create(identity);
            logger.info("Created auth identity with ID: {}", createdIdentity.getId());

            // Step 4: Create Auth Local Credentials
            // Password: "admin"
            String passwordHash = PasswordUtil.hashPassword("admin");

            AuthLocalCredentials credentials = new AuthLocalCredentials();
            credentials.setIdentityId(createdIdentity.getId());
            credentials.setPasswordHash(passwordHash);
            credentials.setPasswordUpdatedAt(LocalDateTime.now());

            authLocalCredentialsDao.create(credentials);
            logger.info("Created local credentials for identity: {}", createdIdentity.getId());

            System.out.println("========================================");
            System.out.println("Admin account created successfully!");
            System.out.println("========================================");
            System.out.println("Username: admin");
            System.out.println("Password: admin");
            System.out.println("========================================");
            System.out.println("Please change the password after first login!");

        } catch (Exception e) {
            logger.error("Error creating admin account", e);
            throw new RuntimeException("Failed to create admin account", e);
        }
    }
}
