package group4.hrms.dao;

import group4.hrms.dto.AccountListDto;
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
    private static final String INSERT_ACCOUNT = "INSERT INTO accounts (user_id, username, email_login, status, failed_attempts, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_ACCOUNT = "UPDATE accounts SET username = ?, email_login = ?, status = ?, last_login_at = ?, updated_at = ? WHERE id = ?";

    private static final String DELETE_ACCOUNT = "DELETE FROM accounts WHERE id = ?";

    private static final String SELECT_ACCOUNT_BY_ID = "SELECT id, user_id, username, email_login, status, failed_attempts, last_login_at, created_at, updated_at FROM accounts WHERE id = ?";

    private static final String SELECT_ACCOUNT_BY_USERNAME = "SELECT id, user_id, username, email_login, status, failed_attempts, last_login_at, created_at, updated_at FROM accounts WHERE username = ?";

    private static final String SELECT_ACCOUNT_BY_EMAIL = "SELECT id, user_id, username, email_login, status, failed_attempts, last_login_at, created_at, updated_at FROM accounts WHERE email_login = ?";

    private static final String SELECT_ALL_ACCOUNTS = "SELECT id, user_id, username, email_login, status, failed_attempts, last_login_at, created_at, updated_at FROM accounts ORDER BY username";

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

            // Set last_login_at (can be null)
            if (account.getLastLoginAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(account.getLastLoginAt()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setTimestamp(5, Timestamp.valueOf(now));
            stmt.setLong(6, account.getId());

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

    /**
     * Tìm accounts với filters và pagination
     * 
     * @param search       Search keyword (username, email_login, hoặc user
     *                     full_name)
     * @param status       Status filter
     * @param departmentId Department ID filter
     * @param positionId   Position ID filter
     * @param offset       Offset for pagination
     * @param limit        Limit for pagination
     * @param sortBy       Column to sort by
     * @param sortOrder    Sort order (asc/desc)
     * @return List of AccountListDto
     */
    public List<AccountListDto> findWithFilters(String search, String status, Long departmentId,
            Long positionId, int offset, int limit,
            String sortBy, String sortOrder) {
        logger.debug("Tìm accounts với filters - search: {}, status: {}, dept: {}, pos: {}, offset: {}, limit: {}",
                search, status, departmentId, positionId, offset, limit);

        List<AccountListDto> accounts = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT a.id, a.username, a.email_login, a.user_id, a.status, a.last_login_at, ");
        sql.append("u.full_name as user_full_name, ");
        sql.append("d.name as department_name, ");
        sql.append("p.name as position_name, ");
        sql.append("r.name as role_name, r.code as role_code, r.priority as role_priority ");
        sql.append("FROM accounts a ");
        sql.append("INNER JOIN users u ON a.user_id = u.id ");
        sql.append("LEFT JOIN departments d ON u.department_id = d.id ");
        sql.append("LEFT JOIN positions p ON u.position_id = p.id ");
        // Get highest priority role for this account
        sql.append("LEFT JOIN (");
        sql.append("  SELECT ar.account_id, r.id, r.name, r.code, r.priority ");
        sql.append("  FROM account_roles ar ");
        sql.append("  INNER JOIN roles r ON ar.role_id = r.id ");
        sql.append("  WHERE ar.account_id IN (SELECT id FROM accounts) ");
        sql.append("  ORDER BY r.priority DESC ");
        sql.append(") r ON r.account_id = a.id ");
        sql.append("WHERE 1=1 ");

        // Build dynamic WHERE clauses
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (a.username LIKE ? OR a.email_login LIKE ? OR u.full_name LIKE ?) ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND a.status = ? ");
        }
        if (departmentId != null) {
            sql.append("AND u.department_id = ? ");
        }
        if (positionId != null) {
            sql.append("AND u.position_id = ? ");
        }

        // Add sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            sql.append("ORDER BY ");
            switch (sortBy) {
                case "username":
                    sql.append("a.username ");
                    break;
                case "email_login":
                    sql.append("a.email_login ");
                    break;
                case "user_full_name":
                    sql.append("u.full_name ");
                    break;
                case "status":
                    sql.append("a.status ");
                    break;
                case "last_login_at":
                    sql.append("a.last_login_at ");
                    break;
                default:
                    sql.append("a.created_at ");
            }
            sql.append(("asc".equalsIgnoreCase(sortOrder)) ? "ASC " : "DESC ");
        } else {
            sql.append("ORDER BY a.created_at DESC ");
        }

        // MySQL pagination syntax
        sql.append("LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameters
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }

            // Set status parameter
            if (status != null && !status.trim().isEmpty()) {
                stmt.setString(paramIndex++, status);
            }

            // Set department parameter
            if (departmentId != null) {
                stmt.setLong(paramIndex++, departmentId);
            }

            // Set position parameter
            if (positionId != null) {
                stmt.setLong(paramIndex++, positionId);
            }

            // Set pagination parameters (limit first, then offset for MySQL)
            stmt.setInt(paramIndex++, limit);
            stmt.setInt(paramIndex++, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccountListDto(rs));
                }
            }

            logger.debug("Tìm thấy {} accounts", accounts.size());
            return accounts;

        } catch (SQLException e) {
            logger.error("Lỗi khi tìm accounts với filters: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm accounts với filters", e);
        }
    }

    /**
     * Đếm tổng số accounts với filters
     * 
     * @param search       Search keyword
     * @param status       Status filter
     * @param departmentId Department ID filter
     * @param positionId   Position ID filter
     * @return Total count
     */
    public int countWithFilters(String search, String status, Long departmentId, Long positionId) {
        logger.debug("Đếm accounts với filters - search: {}, status: {}, dept: {}, pos: {}",
                search, status, departmentId, positionId);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM accounts a ");
        sql.append("INNER JOIN users u ON a.user_id = u.id ");
        sql.append("WHERE 1=1 ");

        // Build dynamic WHERE clauses
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (a.username LIKE ? OR a.email_login LIKE ? OR u.full_name LIKE ?) ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND a.status = ? ");
        }
        if (departmentId != null) {
            sql.append("AND u.department_id = ? ");
        }
        if (positionId != null) {
            sql.append("AND u.position_id = ? ");
        }

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameters
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }

            // Set status parameter
            if (status != null && !status.trim().isEmpty()) {
                stmt.setString(paramIndex++, status);
            }

            // Set department parameter
            if (departmentId != null) {
                stmt.setLong(paramIndex++, departmentId);
            }

            // Set position parameter
            if (positionId != null) {
                stmt.setLong(paramIndex++, positionId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.debug("Tổng số accounts: {}", count);
                    return count;
                }
                return 0;
            }

        } catch (SQLException e) {
            logger.error("Lỗi khi đếm accounts với filters: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi đếm accounts với filters", e);
        }
    }

    /**
     * Tìm accounts theo user ID
     * 
     * @param userId User ID
     * @return List of accounts
     */
    public List<Account> findByUserId(Long userId) {
        logger.debug("Tìm accounts theo user ID: {}", userId);

        String sql = "SELECT id, user_id, username, email_login, status, failed_attempts, " +
                "last_login_at, created_at, updated_at " +
                "FROM accounts WHERE user_id = ? ORDER BY created_at DESC";

        List<Account> accounts = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }

            logger.debug("Tìm thấy {} accounts cho user ID: {}", accounts.size(), userId);
            return accounts;

        } catch (SQLException e) {
            logger.error("Lỗi khi tìm accounts theo user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm accounts theo user ID", e);
        }
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
     * Map ResultSet thành AccountListDto object
     */
    private AccountListDto mapResultSetToAccountListDto(ResultSet rs) throws SQLException {
        AccountListDto dto = new AccountListDto();

        dto.setId(rs.getLong("id"));
        dto.setUsername(rs.getString("username"));
        dto.setEmailLogin(rs.getString("email_login"));
        dto.setUserId(rs.getLong("user_id"));
        dto.setUserFullName(rs.getString("user_full_name"));
        dto.setDepartmentName(rs.getString("department_name"));
        dto.setPositionName(rs.getString("position_name"));
        dto.setStatus(rs.getString("status"));

        // Last login có thể null
        Timestamp lastLogin = rs.getTimestamp("last_login_at");
        if (lastLogin != null) {
            dto.setLastLoginAt(lastLogin.toLocalDateTime());
        }

        // Role information (highest priority role)
        dto.setRoleName(rs.getString("role_name"));
        dto.setRoleCode(rs.getString("role_code"));
        int rolePriority = rs.getInt("role_priority");
        if (!rs.wasNull()) {
            dto.setRolePriority(rolePriority);
        }

        return dto;
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

    /**
     * Create account with password (local authentication)
     * Creates account, auth_identity, and auth_local_credentials in a transaction
     * 
     * @param userId        User ID
     * @param username      Username
     * @param emailLogin    Email for login
     * @param plainPassword Plain text password (will be hashed)
     * @return Created Account
     */
    public Account createWithPassword(Long userId, String username, String emailLogin, String plainPassword) {
        logger.info("Creating account with password for user ID: {}", userId);

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Create account
            Account account = new Account();
            account.setUserId(userId);
            account.setUsername(username);
            account.setEmailLogin(emailLogin);
            account.setStatus("active");
            account.setFailedAttempts(0);

            LocalDateTime now = LocalDateTime.now();

            String insertAccount = "INSERT INTO accounts (user_id, username, email_login, status, failed_attempts, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
            long accountId;

            try (PreparedStatement stmt = conn.prepareStatement(insertAccount, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, userId);
                stmt.setString(2, username);
                stmt.setString(3, emailLogin);
                stmt.setString(4, "active");
                stmt.setInt(5, 0);
                stmt.setTimestamp(6, Timestamp.valueOf(now));
                stmt.setTimestamp(7, Timestamp.valueOf(now));

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        accountId = rs.getLong(1);
                        account.setId(accountId);
                    } else {
                        throw new SQLException("Failed to create account, no ID obtained");
                    }
                }
            }

            // 2. Create auth_identity for local provider
            String insertIdentity = "INSERT INTO auth_identities (account_id, provider, provider_user_id, email, email_verified, created_at) VALUES (?, ?, ?, ?, ?, ?)";
            long identityId;

            try (PreparedStatement stmt = conn.prepareStatement(insertIdentity, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, accountId);
                stmt.setString(2, "local");
                stmt.setString(3, username); // Use username as provider_user_id for local
                stmt.setString(4, emailLogin);
                stmt.setBoolean(5, false);
                stmt.setTimestamp(6, Timestamp.valueOf(now));

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        identityId = rs.getLong(1);
                    } else {
                        throw new SQLException("Failed to create auth_identity, no ID obtained");
                    }
                }
            }

            // 3. Hash password and create auth_local_credentials
            String passwordHash = group4.hrms.util.PasswordUtil.hashPassword(plainPassword);
            String insertCredentials = "INSERT INTO auth_local_credentials (identity_id, password_hash, password_updated_at) VALUES (?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(insertCredentials)) {
                stmt.setLong(1, identityId);
                stmt.setString(2, passwordHash);
                stmt.setTimestamp(3, Timestamp.valueOf(now));

                stmt.executeUpdate();
            }

            conn.commit();
            logger.info("Successfully created account with password: {} (ID: {})", username, accountId);

            account.setCreatedAt(now);
            account.setUpdatedAt(now);
            return account;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warn("Transaction rolled back due to error");
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction: {}", ex.getMessage());
                }
            }
            logger.error("Error creating account with password: {}", e.getMessage(), e);

            // Provide more specific error message
            String errorMsg = "Error creating account with password: " + e.getMessage();
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Duplicate entry")) {
                    errorMsg = "Duplicate entry: " + e.getMessage();
                } else if (e.getMessage().contains("foreign key constraint")) {
                    errorMsg = "Invalid user ID or foreign key constraint violation";
                }
            }
            throw new RuntimeException(errorMsg, e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Save account (alias for create method)
     * 
     * @param account Account to save
     * @return Created account
     */
    public Account save(Account account) {
        return create(account);
    }
}
