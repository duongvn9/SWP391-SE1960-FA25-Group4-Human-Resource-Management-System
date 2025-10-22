package group4.hrms.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.model.UserProfile;
import group4.hrms.util.DatabaseUtil;

/**
 * DAO class để xử lý user profile data
 */
public class UserProfileDao {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileDao.class);

    /**
     * Lấy full profile của user bằng account_id
     */
    public Optional<UserProfile> findByAccountId(Long accountId) {
        String sql = "SELECT " +
                "u.id as user_id, u.employee_code, u.full_name, u.dob, u.gender, u.hometown, " +
                "u.cccd, u.cccd_issued_date, u.cccd_issued_place, " +
                "u.email_company, u.phone, " +
                "u.department_id, d.name as department_name, " +
                "u.position_id, p.name as position_name, " +
                "u.status, u.date_joined, u.date_left, u.start_work_date, " +
                "u.address_line1, u.address_line2, u.city, u.state, u.postal_code, u.country, " +
                "a.id as account_id, a.username, a.email_login, a.status as account_status, " +
                "a.last_login_at, u.created_at, u.updated_at " +
                "FROM accounts a " +
                "INNER JOIN users u ON a.user_id = u.id " +
                "LEFT JOIN departments d ON u.department_id = d.id " +
                "LEFT JOIN positions p ON u.position_id = p.id " +
                "WHERE a.id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserProfile profile = mapResultSetToUserProfile(rs);
                    logger.debug("Found user profile for account_id: {}", accountId);
                    return Optional.of(profile);
                }
            }

            logger.warn("User profile not found for account_id: {}", accountId);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding user profile by account_id: " + accountId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    /**
     * Lấy full profile của user bằng user_id
     */
    public UserProfile findByUserId(Long userId) {
        String sql = "SELECT " +
                "u.id as user_id, u.employee_code, u.full_name, u.dob, u.gender, u.hometown, " +
                "u.cccd, u.cccd_issued_date, u.cccd_issued_place, " +
                "u.email_company, u.phone, " +
                "u.department_id, d.name as department_name, " +
                "u.position_id, p.name as position_name, " +
                "u.status, u.date_joined, u.date_left, u.start_work_date, " +
                "u.address_line1, u.address_line2, u.city, u.state, u.postal_code, u.country, " +
                "COALESCE(a.id, 0) as account_id, a.username, a.email_login, a.status as account_status, " +
                "a.last_login_at, u.created_at, u.updated_at " +
                "FROM users u " +
                "LEFT JOIN accounts a ON a.user_id = u.id " +
                "LEFT JOIN departments d ON u.department_id = d.id " +
                "LEFT JOIN positions p ON u.position_id = p.id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserProfile profile = mapResultSetToUserProfile(rs);
                    logger.debug("Found user profile for user_id: {}", userId);
                    return profile;
                }
            }

            logger.warn("User profile not found for user_id: {}", userId);
            return null;

        } catch (SQLException e) {
            logger.error("Error finding user profile by user_id: " + userId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    /**
     * Cập nhật user profile
     */
    public boolean updateProfile(Long userId, UserProfile profile) {
        String sql = "UPDATE users SET " +
                "full_name = ?, " +
                "dob = ?, " +
                "gender = ?, " +
                "hometown = ?, " +
                "cccd = ?, " +
                "cccd_issued_date = ?, " +
                "cccd_issued_place = ?, " +
                "email_company = ?, " +
                "phone = ?, " +
                "address_line1 = ?, " +
                "address_line2 = ?, " +
                "city = ?, " +
                "state = ?, " +
                "postal_code = ?, " +
                "country = ?, " +
                "updated_at = UTC_TIMESTAMP() " +
                "WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profile.getFullName());
            stmt.setObject(2, profile.getDob());
            stmt.setString(3, profile.getGender());
            stmt.setString(4, profile.getHometown());
            stmt.setString(5, profile.getCccd());
            stmt.setObject(6, profile.getCccdIssuedDate());
            stmt.setString(7, profile.getCccdIssuedPlace());
            stmt.setString(8, profile.getEmailCompany());
            stmt.setString(9, profile.getPhone());
            stmt.setString(10, profile.getAddressLine1());
            stmt.setString(11, profile.getAddressLine2());
            stmt.setString(12, profile.getCity());
            stmt.setString(13, profile.getState());
            stmt.setString(14, profile.getPostalCode());
            stmt.setString(15, profile.getCountry());
            stmt.setLong(16, userId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Updated user profile for user_id: {}", userId);
                return true;
            }

            logger.warn("No rows updated for user_id: {}", userId);
            return false;

        } catch (SQLException e) {
            logger.error("Error updating user profile for user_id: " + userId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    /**
     * Kiểm tra CCCD có tồn tại cho user khác không
     */
    public boolean isCccdExistsForOtherUser(String cccd, Long currentUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE cccd = ? AND id != ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cccd);
            stmt.setLong(2, currentUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking CCCD existence", e);
            throw new RuntimeException("Database error", e);
        }
    }

    /**
     * Kiểm tra email có tồn tại cho user khác không
     */
    public boolean isEmailExistsForOtherUser(String email, Long currentUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE email_company = ? AND id != ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setLong(2, currentUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking email existence", e);
            throw new RuntimeException("Database error", e);
        }
    }

    /**
     * Kiểm tra phone có tồn tại cho user khác không
     */
    public boolean isPhoneExistsForOtherUser(String phone, Long currentUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ? AND id != ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);
            stmt.setLong(2, currentUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking phone existence", e);
            throw new RuntimeException("Database error", e);
        }
    }

    /**
     * Map ResultSet to UserProfile object
     */
    private UserProfile mapResultSetToUserProfile(ResultSet rs) throws SQLException {
        UserProfile profile = new UserProfile();

        // User info
        profile.setUserId(rs.getLong("user_id"));
        profile.setEmployeeCode(rs.getString("employee_code"));
        profile.setFullName(rs.getString("full_name"));

        Date dob = rs.getDate("dob");
        if (dob != null) {
            profile.setDob(dob.toLocalDate());
        }

        profile.setGender(rs.getString("gender"));
        profile.setHometown(rs.getString("hometown"));

        // CCCD info
        profile.setCccd(rs.getString("cccd"));
        Date cccdIssuedDate = rs.getDate("cccd_issued_date");
        if (cccdIssuedDate != null) {
            profile.setCccdIssuedDate(cccdIssuedDate.toLocalDate());
        }
        profile.setCccdIssuedPlace(rs.getString("cccd_issued_place"));

        // Contact info
        profile.setEmailCompany(rs.getString("email_company"));
        profile.setPhone(rs.getString("phone"));

        // Organization info
        profile.setDepartmentId(rs.getLong("department_id"));
        profile.setDepartmentName(rs.getString("department_name"));
        profile.setPositionId(rs.getLong("position_id"));
        profile.setPositionName(rs.getString("position_name"));
        profile.setStatus(rs.getString("status"));

        // Employment info
        Date dateJoined = rs.getDate("date_joined");
        if (dateJoined != null) {
            profile.setDateJoined(dateJoined.toLocalDate());
        }

        Date dateLeft = rs.getDate("date_left");
        if (dateLeft != null) {
            profile.setDateLeft(dateLeft.toLocalDate());
        }

        Date startWorkDate = rs.getDate("start_work_date");
        if (startWorkDate != null) {
            profile.setStartWorkDate(startWorkDate.toLocalDate());
        }

        // Address info
        profile.setAddressLine1(rs.getString("address_line1"));
        profile.setAddressLine2(rs.getString("address_line2"));
        profile.setCity(rs.getString("city"));
        profile.setState(rs.getString("state"));
        profile.setPostalCode(rs.getString("postal_code"));
        profile.setCountry(rs.getString("country"));

        // Account info
        profile.setAccountId(rs.getLong("account_id"));
        profile.setUsername(rs.getString("username"));
        profile.setEmailLogin(rs.getString("email_login"));
        profile.setAccountStatus(rs.getString("account_status"));

        Timestamp lastLoginAt = rs.getTimestamp("last_login_at");
        if (lastLoginAt != null) {
            profile.setLastLoginAt(lastLoginAt.toLocalDateTime());
        }

        // Timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            profile.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            profile.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return profile;
    }
}
