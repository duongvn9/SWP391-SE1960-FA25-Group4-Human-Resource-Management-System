package group4.hrms.dao;

import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing account-role relationships
 */
public class AccountRoleDao {
    private static final Logger logger = LoggerFactory.getLogger(AccountRoleDao.class);

    /**
     * Assign a role to an account
     * 
     * @param accountId Account ID
     * @param roleId    Role ID
     * @return true if successful
     */
    public boolean assignRole(Long accountId, Long roleId) {
        if (accountId == null || roleId == null) {
            logger.warn("Account ID or Role ID is null");
            return false;
        }

        logger.info("Assigning role {} to account {}", roleId, accountId);

        // Check if already exists
        if (hasRole(accountId, roleId)) {
            logger.info("Account {} already has role {}", accountId, roleId);
            return true;
        }

        String sql = "INSERT INTO account_roles (account_id, role_id, assigned_at) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);
            stmt.setLong(2, roleId);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Successfully assigned role {} to account {}", roleId, accountId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error assigning role {} to account {}: {}", roleId, accountId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Remove a role from an account
     * 
     * @param accountId Account ID
     * @param roleId    Role ID
     * @return true if successful
     */
    public boolean removeRole(Long accountId, Long roleId) {
        if (accountId == null || roleId == null) {
            logger.warn("Account ID or Role ID is null");
            return false;
        }

        logger.info("Removing role {} from account {}", roleId, accountId);

        String sql = "DELETE FROM account_roles WHERE account_id = ? AND role_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);
            stmt.setLong(2, roleId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Successfully removed role {} from account {}", roleId, accountId);
                return true;
            }

            logger.warn("No role {} found for account {}", roleId, accountId);
            return false;

        } catch (SQLException e) {
            logger.error("Error removing role {} from account {}: {}", roleId, accountId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Remove all roles from an account
     * 
     * @param accountId Account ID
     * @return true if successful
     */
    public boolean removeAllRoles(Long accountId) {
        if (accountId == null) {
            logger.warn("Account ID is null");
            return false;
        }

        logger.info("Removing all roles from account {}", accountId);

        String sql = "DELETE FROM account_roles WHERE account_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);

            int affectedRows = stmt.executeUpdate();
            logger.info("Removed {} roles from account {}", affectedRows, accountId);
            return true;

        } catch (SQLException e) {
            logger.error("Error removing all roles from account {}: {}", accountId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if account has a specific role
     * 
     * @param accountId Account ID
     * @param roleId    Role ID
     * @return true if account has the role
     */
    public boolean hasRole(Long accountId, Long roleId) {
        if (accountId == null || roleId == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM account_roles WHERE account_id = ? AND role_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);
            stmt.setLong(2, roleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            logger.error("Error checking role {} for account {}: {}", roleId, accountId, e.getMessage(), e);
        }

        return false;
    }

    /**
     * Get all role IDs for an account
     * 
     * @param accountId Account ID
     * @return List of role IDs
     */
    public List<Long> getRoleIds(Long accountId) {
        List<Long> roleIds = new ArrayList<>();

        if (accountId == null) {
            return roleIds;
        }

        String sql = "SELECT role_id FROM account_roles WHERE account_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roleIds.add(rs.getLong("role_id"));
                }
            }

            logger.debug("Found {} roles for account {}", roleIds.size(), accountId);

        } catch (SQLException e) {
            logger.error("Error getting roles for account {}: {}", accountId, e.getMessage(), e);
        }

        return roleIds;
    }

    /**
     * Update account role (remove old, add new)
     * 
     * @param accountId Account ID
     * @param newRoleId New role ID
     * @return true if successful
     */
    public boolean updateRole(Long accountId, Long newRoleId) {
        if (accountId == null || newRoleId == null) {
            logger.warn("Account ID or Role ID is null");
            return false;
        }

        logger.info("Updating role for account {} to role {}", accountId, newRoleId);

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Remove all existing roles
            String deleteSql = "DELETE FROM account_roles WHERE account_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setLong(1, accountId);
                deleteStmt.executeUpdate();
            }

            // Add new role
            String insertSql = "INSERT INTO account_roles (account_id, role_id, assigned_at) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setLong(1, accountId);
                insertStmt.setLong(2, newRoleId);
                insertStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                insertStmt.executeUpdate();
            }

            conn.commit();
            logger.info("Successfully updated role for account {} to role {}", accountId, newRoleId);
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction: {}", ex.getMessage());
                }
            }
            logger.error("Error updating role for account {}: {}", accountId, e.getMessage(), e);
            return false;
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
}
