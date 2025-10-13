package group4.hrms.dao;

import group4.hrms.model.Account;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class cho Account entity - Phiên bản đơn giản
 * Xử lý các thao tác CRUD với bảng accounts
 */
public class AccountDao {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);
    
    // SQL queries
    private static final String INSERT_ACCOUNT = 
        "INSERT INTO accounts (user_id, username, email_login, status, failed_attempts, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_ACCOUNT = 
        "UPDATE accounts SET username = ?, email_login = ?, status = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE_ACCOUNT = "DELETE FROM accounts WHERE id = ?";
    
    private static final String SELECT_ACCOUNT_BY_ID = 
        "SELECT id, user_id, username, email_login, status, failed_attempts, last_login_at, created_at, updated_at FROM accounts WHERE id = ?";
    
    private static final String SELECT_ACCOUNT_BY_USERNAME = 
        "SELECT id, user_id, username, email_login, status, failed_attempts, last_login_at, created_at, updated_at FROM accounts WHERE username = ?";
    
    private static final String SELECT_ACCOUNT_BY_EMAIL = 
        "SELECT id, user_id, username, email_login, status, failed_attempts, last_login_at, created_at, updated_at FROM accounts WHERE email_login = ?";
    
    private static final String SELECT_ALL_ACCOUNTS = 
        "SELECT id, user_id, username, email_login, status, failed_attempts, last_login_at, created_at, updated_at FROM accounts ORDER BY username";
    
    /**
     * Tạo mới account
     */
    public Account create(Account account) {
        logger.info("Tạo mới account: {}", account.getUsername());
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setLong(1, account.getUserId());
            stmt.setString(2, account.getUsername());
            stmt.setString(3, account.getEmailLogin());
            stmt.setString(4, account.getStatus());
            stmt.setInt(5, account.getFailedAttempts() != null ? account.getFailedAttempts().intValue() : 0);
            stmt.setTimestamp(6, Timestamp.valueOf(now));
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Tạo account thất bại, không có row nào được insert");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getLong(1));
                    account.setCreatedAt(now);
                    account.setUpdatedAt(now);
                    
                    logger.info("Tạo account thành công với ID: {}", account.getId());
                    return account;
                } else {
                    throw new SQLException("Tạo account thất bại, không lấy được generated ID");
                }
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tạo account: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo account", e);
        }
    }
    
    /**
     * Cập nhật account
     */
    public Account update(Account account) {
        logger.info("Cập nhật account ID: {}", account.getId());
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ACCOUNT)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getEmailLogin());
            stmt.setString(3, account.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.setLong(5, account.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                logger.warn("Cập nhật thất bại, không tìm thấy account với ID: {}", account.getId());
                return null;
            }
            
            account.setUpdatedAt(now);
            logger.info("Cập nhật account thành công: {}", account.getId());
            return account;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi cập nhật account ID {}: {}", account.getId(), e.getMessage(), e);
            throw new RuntimeException("Lỗi khi cập nhật account", e);
        }
    }
    
    /**
     * Xóa account theo ID
     */
    public boolean delete(Long accountId) {
        logger.info("Xóa account ID: {}", accountId);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ACCOUNT)) {
            
            stmt.setLong(1, accountId);
            int affectedRows = stmt.executeUpdate();
            
            boolean deleted = affectedRows > 0;
            if (deleted) {
                logger.info("Xóa account thành công ID: {}", accountId);
            } else {
                logger.warn("Không tìm thấy account để xóa ID: {}", accountId);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi xóa account ID {}: {}", accountId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi xóa account", e);
        }
    }
    
    /**
     * Tìm account theo ID
     */
    public Optional<Account> findById(Long accountId) {
        logger.debug("Tìm account theo ID: {}", accountId);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACCOUNT_BY_ID)) {
            
            stmt.setLong(1, accountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = mapResultSetToAccount(rs);
                    logger.debug("Tìm thấy account: {}", account.getUsername());
                    return Optional.of(account);
                }
                
                logger.debug("Không tìm thấy account với ID: {}", accountId);
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm account ID {}: {}", accountId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm account", e);
        }
    }
    
    /**
     * Tìm account theo username
     */
    public Optional<Account> findByUsername(String username) {
        logger.debug("Tìm account theo username: {}", username);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACCOUNT_BY_USERNAME)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = mapResultSetToAccount(rs);
                    logger.debug("Tìm thấy account: {}", account.getUsername());
                    return Optional.of(account);
                }
                
                logger.debug("Không tìm thấy account với username: {}", username);
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm account theo username {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm account", e);
        }
    }
    
    /**
     * Lấy tất cả accounts
     */
    public List<Account> findAll() {
        logger.debug("Lấy tất cả accounts");
        
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ACCOUNTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
            
            logger.debug("Tìm thấy {} accounts", accounts.size());
            return accounts;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy danh sách accounts: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách accounts", e);
        }
    }
    
    /**
     * Tìm account theo email login
     */
    public Optional<Account> findByEmailLogin(String emailLogin) {
        logger.debug("Tìm account theo email login: {}", emailLogin);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACCOUNT_BY_EMAIL)) {
            
            stmt.setString(1, emailLogin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = mapResultSetToAccount(rs);
                    logger.debug("Tìm thấy account: {}", account.getUsername());
                    return Optional.of(account);
                }
                
                logger.debug("Không tìm thấy account với email login: {}", emailLogin);
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm account theo email login {}: {}", emailLogin, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm account", e);
        }
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
    
    // Helper methods
    
    /**
     * Map ResultSet thành Account object
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        
        account.setId(rs.getLong("id"));
        account.setUserId(rs.getLong("user_id"));
        account.setUsername(rs.getString("username"));
        account.setEmailLogin(rs.getString("email_login"));
        account.setStatus(rs.getString("status"));
        account.setFailedAttempts(rs.getInt("failed_attempts"));
        
        // Last login có thể null
        Timestamp lastLogin = rs.getTimestamp("last_login_at");
        if (lastLogin != null) {
            account.setLastLoginAt(lastLogin.toLocalDateTime());
        }
        
        // Timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            account.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            account.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return account;
    }
    
    /**
     * Set nullable Long parameter
     */
    private void setNullableLong(PreparedStatement stmt, int parameterIndex, Long value) throws SQLException {
        if (value != null) {
            stmt.setLong(parameterIndex, value);
        } else {
            stmt.setNull(parameterIndex, Types.BIGINT);
        }
    }
}