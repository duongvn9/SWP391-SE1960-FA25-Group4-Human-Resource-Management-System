package group4.hrms.dao;

import group4.hrms.model.AuthIdentity;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng auth_identities
 * 
 * @author Group4
 */
public class AuthIdentityDao extends BaseDao<AuthIdentity, Long> {
    
    @Override
    protected String getTableName() {
        return "auth_identities";
    }
    
    @Override
    protected AuthIdentity mapResultSetToEntity(ResultSet rs) throws SQLException {
        AuthIdentity identity = new AuthIdentity();
        identity.setId(rs.getLong("id"));
        identity.setAccountId(rs.getLong("account_id"));
        identity.setProvider(rs.getString("provider"));
        identity.setProviderUserId(rs.getString("provider_user_id"));
        identity.setEmail(rs.getString("email"));
        identity.setEmailVerified(rs.getBoolean("email_verified"));
        identity.setCreatedAt(getLocalDateTime(rs, "created_at"));
        
        return identity;
    }
    
    @Override
    protected void setEntityId(AuthIdentity identity, Long id) {
        identity.setId(id);
    }
    
    @Override
    protected Long getEntityId(AuthIdentity identity) {
        return identity.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO auth_identities (account_id, provider, provider_user_id, " +
               "email, email_verified, created_at) VALUES (?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE auth_identities SET account_id = ?, provider = ?, provider_user_id = ?, " +
               "email = ?, email_verified = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, AuthIdentity identity) throws SQLException {
        stmt.setLong(1, identity.getAccountId());
        stmt.setString(2, identity.getProvider());
        stmt.setString(3, identity.getProviderUserId());
        stmt.setString(4, identity.getEmail());
        Boolean emailVerified = identity.getEmailVerified();
        stmt.setBoolean(5, emailVerified != null && emailVerified);
        setTimestamp(stmt, 6, identity.getCreatedAt() != null ? identity.getCreatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, AuthIdentity identity) throws SQLException {
        stmt.setLong(1, identity.getAccountId());
        stmt.setString(2, identity.getProvider());
        stmt.setString(3, identity.getProviderUserId());
        stmt.setString(4, identity.getEmail());
        Boolean emailVerified = identity.getEmailVerified();
        stmt.setBoolean(5, emailVerified != null && emailVerified);
        stmt.setLong(6, identity.getId());
    }
    
    // Business methods
    
    /**
     * Tìm identity theo account ID và provider
     */
    public Optional<AuthIdentity> findByAccountAndProvider(Long accountId, String provider) throws SQLException {
        if (accountId == null || provider == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM auth_identities WHERE account_id = ? AND provider = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            stmt.setString(2, provider);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding identity by account {} and provider {}: {}", accountId, provider, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm identity theo provider và provider_user_id
     */
    public Optional<AuthIdentity> findByProviderAndUserId(String provider, String providerUserId) throws SQLException {
        if (provider == null || providerUserId == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM auth_identities WHERE provider = ? AND provider_user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, provider);
            stmt.setString(2, providerUserId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding identity by provider {} and user ID {}: {}", provider, providerUserId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm tất cả identities của một account
     */
    public List<AuthIdentity> findByAccountId(Long accountId) throws SQLException {
        if (accountId == null) {
            return new ArrayList<>();
        }
        
        List<AuthIdentity> identities = new ArrayList<>();
        String sql = "SELECT * FROM auth_identities WHERE account_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    identities.add(mapResultSetToEntity(rs));
                }
            }
            
            return identities;
            
        } catch (SQLException e) {
            logger.error("Error finding identities by account ID {}: {}", accountId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm local identity của account
     */
    public Optional<AuthIdentity> findLocalIdentity(Long accountId) throws SQLException {
        return findByAccountAndProvider(accountId, "local");
    }
    
    /**
     * Kiểm tra email đã verify chưa
     */
    public boolean isEmailVerified(Long accountId, String provider) throws SQLException {
        Optional<AuthIdentity> identity = findByAccountAndProvider(accountId, provider);
        return identity.map(AuthIdentity::isEmailVerified).orElse(false);
    }
    
    /**
     * Cập nhật email verified status
     */
    public boolean updateEmailVerified(Long identityId, boolean verified) throws SQLException {
        String sql = "UPDATE auth_identities SET email_verified = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, verified);
            stmt.setLong(2, identityId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating email verified status for identity {}: {}", identityId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm identities theo provider
     */
    public List<AuthIdentity> findByProvider(String provider) throws SQLException {
        if (provider == null || provider.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<AuthIdentity> identities = new ArrayList<>();
        String sql = "SELECT * FROM auth_identities WHERE provider = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, provider);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    identities.add(mapResultSetToEntity(rs));
                }
            }
            
            return identities;
            
        } catch (SQLException e) {
            logger.error("Error finding identities by provider {}: {}", provider, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm số identities theo provider
     */
    public long countByProvider(String provider) throws SQLException {
        if (provider == null || provider.trim().isEmpty()) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM auth_identities WHERE provider = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, provider);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting identities by provider {}: {}", provider, e.getMessage(), e);
            throw e;
        }
    }
}