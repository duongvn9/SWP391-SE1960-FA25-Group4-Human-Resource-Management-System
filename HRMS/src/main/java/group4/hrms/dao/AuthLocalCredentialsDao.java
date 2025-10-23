package group4.hrms.dao;

import group4.hrms.model.AuthLocalCredentials;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng auth_local_credentials
 * 
 * @author Group4
 */
public class AuthLocalCredentialsDao extends BaseDao<AuthLocalCredentials, Long> {
    
    @Override
    protected String getTableName() {
        return "auth_local_credentials";
    }
    
    @Override
    protected AuthLocalCredentials mapResultSetToEntity(ResultSet rs) throws SQLException {
        AuthLocalCredentials credentials = new AuthLocalCredentials();
        credentials.setIdentityId(rs.getLong("identity_id"));
        credentials.setPasswordHash(rs.getString("password_hash"));
        credentials.setPasswordUpdatedAt(getLocalDateTime(rs, "password_updated_at"));
        
        return credentials;
    }
    
    @Override
    protected void setEntityId(AuthLocalCredentials credentials, Long id) {
        credentials.setIdentityId(id);
    }
    
    @Override
    protected Long getEntityId(AuthLocalCredentials credentials) {
        return credentials.getIdentityId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO auth_local_credentials (identity_id, password_hash, password_updated_at) VALUES (?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE auth_local_credentials SET password_hash = ?, password_updated_at = ? WHERE identity_id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, AuthLocalCredentials credentials) throws SQLException {
        stmt.setLong(1, credentials.getIdentityId());
        stmt.setString(2, credentials.getPasswordHash());
        setTimestamp(stmt, 3, credentials.getPasswordUpdatedAt() != null ? credentials.getPasswordUpdatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, AuthLocalCredentials credentials) throws SQLException {
        stmt.setString(1, credentials.getPasswordHash());
        setTimestamp(stmt, 2, LocalDateTime.now());
        stmt.setLong(3, credentials.getIdentityId());
    }
    
    // Custom CRUD methods since identity_id is PK
    
    @Override
    public Optional<AuthLocalCredentials> findById(Long identityId) throws SQLException {
        if (identityId == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM auth_local_credentials WHERE identity_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, identityId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding credentials by identity ID {}: {}", identityId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Alias cho findById
     */
    public Optional<AuthLocalCredentials> findByIdentityId(Long identityId) throws SQLException {
        return findById(identityId);
    }
    
    @Override
    public boolean deleteById(Long identityId) throws SQLException {
        if (identityId == null) {
            return false;
        }
        
        String sql = "DELETE FROM auth_local_credentials WHERE identity_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, identityId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error deleting credentials by identity ID {}: {}", identityId, e.getMessage(), e);
            throw e;
        }
    }
    
    // Business methods
    
    /**
     * Cập nhật password hash
     */
    public boolean updatePassword(Long identityId, String newPasswordHash) throws SQLException {
        if (identityId == null || newPasswordHash == null) {
            return false;
        }
        
        String sql = "UPDATE auth_local_credentials SET password_hash = ?, password_updated_at = ? WHERE identity_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPasswordHash);
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, identityId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating password for identity {}: {}", identityId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Kiểm tra password có tồn tại không
     */
    public boolean hasPassword(Long identityId) throws SQLException {
        return findById(identityId).isPresent();
    }
    
    /**
     * Lấy thời gian cập nhật password cuối cùng
     */
    public Optional<LocalDateTime> getPasswordUpdatedAt(Long identityId) throws SQLException {
        Optional<AuthLocalCredentials> credentials = findById(identityId);
        return credentials.map(AuthLocalCredentials::getPasswordUpdatedAt);
    }
    
    /**
     * Kiểm tra password có hết hạn không
     */
    public boolean isPasswordExpired(Long identityId, int maxDaysValid) throws SQLException {
        if (maxDaysValid <= 0) {
            return false;
        }
        
        Optional<AuthLocalCredentials> credentials = findById(identityId);
        return credentials.map(c -> c.isPasswordExpired(maxDaysValid)).orElse(false);
    }

    /**
     * Alias method for compatibility with AuthService
     */
    public AuthLocalCredentials create(AuthLocalCredentials credentials) throws SQLException {
        return save(credentials);
    }
    
    /**
     * Find credentials by account ID (via auth_identities join)
     * @param accountId Account ID
     * @return Optional of AuthLocalCredentials
     */
    public Optional<AuthLocalCredentials> findByAccountId(Long accountId) throws SQLException {
        if (accountId == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT alc.* FROM auth_local_credentials alc " +
                    "INNER JOIN auth_identities ai ON alc.identity_id = ai.id " +
                    "WHERE ai.account_id = ? AND ai.provider = 'local'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding credentials by account ID {}: {}", accountId, e.getMessage(), e);
            throw e;
        }
    }
}