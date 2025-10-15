package group4.hrms.dao;

import group4.hrms.model.User;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO cho User entity
 * Xử lý các thao tác CRUD với bảng users
 */
public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    // SQL queries
    private static final String SELECT_ALL = """
        SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
               u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
               u.start_work_date, u.created_at, u.updated_at,
               d.name as department_name, p.name as position_name
        FROM users u
        LEFT JOIN departments d ON u.department_id = d.id
        LEFT JOIN positions p ON u.position_id = p.id
        ORDER BY u.created_at DESC
        """;

    private static final String SELECT_BY_ID = """
        SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
               u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
               u.start_work_date, u.created_at, u.updated_at,
               d.name as department_name, p.name as position_name
        FROM users u
        LEFT JOIN departments d ON u.department_id = d.id
        LEFT JOIN positions p ON u.position_id = p.id
        WHERE u.id = ?
        """;

    private static final String SELECT_BY_EMPLOYEE_CODE = """
        SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
               u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
               u.start_work_date, u.created_at, u.updated_at,
               d.name as department_name, p.name as position_name
        FROM users u
        LEFT JOIN departments d ON u.department_id = d.id
        LEFT JOIN positions p ON u.position_id = p.id
        WHERE u.employee_code = ?
        """;

    private static final String SELECT_BY_EMAIL = """
        SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
               u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
               u.start_work_date, u.created_at, u.updated_at,
               d.name as department_name, p.name as position_name
        FROM users u
        LEFT JOIN departments d ON u.department_id = d.id
        LEFT JOIN positions p ON u.position_id = p.id
        WHERE u.email_company = ?
        """;

    private static final String SELECT_BY_DEPARTMENT = """
        SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
               u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
               u.start_work_date, u.created_at, u.updated_at,
               d.name as department_name, p.name as position_name
        FROM users u
        LEFT JOIN departments d ON u.department_id = d.id
        LEFT JOIN positions p ON u.position_id = p.id
        WHERE u.department_id = ? AND u.status = 'active'
        ORDER BY u.full_name
        """;

    private static final String INSERT_USER = """
        INSERT INTO users (employee_code, full_name, cccd, email_company, phone,
                          department_id, position_id, status, date_joined, start_work_date)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_USER = """
        UPDATE users SET
            employee_code = ?, full_name = ?, cccd = ?, email_company = ?, phone = ?,
            department_id = ?, position_id = ?, status = ?, date_joined = ?, start_work_date = ?,
            updated_at = NOW()
        WHERE id = ?
        """;

    private static final String UPDATE_STATUS = """
        UPDATE users SET status = ?, updated_at = GETUTCDATE() WHERE id = ?
        """;

    private static final String DELETE_USER = """
        DELETE FROM users WHERE id = ?
        """;

    /**
     * Lấy tất cả users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            logger.info("Tìm thấy {} users", users.size());
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy danh sách users", e);
        }

        return users;
    }

    /**
     * Tìm user theo ID
     */
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    logger.debug("Tìm thấy user với ID {}: {}", id, user.getFullName());
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm user với ID: " + id, e);
        }

        return Optional.empty();
    }

    /**
     * Tìm user theo employee code
     */
    public Optional<User> findByEmployeeCode(String employeeCode) {
        if (employeeCode == null || employeeCode.trim().isEmpty()) {
            return Optional.empty();
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_EMPLOYEE_CODE)) {

            ps.setString(1, employeeCode.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    logger.debug("Tìm thấy user với employee code {}: {}", employeeCode, user.getFullName());
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm user với employee code: " + employeeCode, e);
        }

        return Optional.empty();
    }

    /**
     * Tìm user theo email
     */
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_EMAIL)) {

            ps.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    logger.debug("Tìm thấy user với email {}: {}", email, user.getFullName());
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm user với email: " + email, e);
        }

        return Optional.empty();
    }

    /**
     * Lấy users theo department
     */
    public List<User> findByDepartmentId(Long departmentId) {
        List<User> users = new ArrayList<>();

        if (departmentId == null) {
            return users;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_DEPARTMENT)) {

            ps.setLong(1, departmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

            logger.info("Tìm thấy {} users trong department ID: {}", users.size(), departmentId);
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy users theo department ID: " + departmentId, e);
        }

        return users;
    }

    /**
     * Lấy danh sách nhân viên trong phòng ban (chỉ active users)
     * Alias method cho OT Request feature
     */
    public List<User> getDepartmentEmployees(Long departmentId) {
        return findByDepartmentId(departmentId);
    }

    /**
     * Tạo user mới
     */
    public Optional<User> create(User user) {
        if (user == null || user.getFullName() == null) {
            logger.warn("Dữ liệu user không hợp lệ để tạo mới");
            return Optional.empty();
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getEmployeeCode());
            ps.setString(2, user.getFullName().trim());
            ps.setString(3, user.getCccd());
            ps.setString(4, user.getEmailCompany());
            ps.setString(5, user.getPhone());
            setLongOrNull(ps, 6, user.getDepartmentId());
            setLongOrNull(ps, 7, user.getPositionId());
            ps.setString(8, user.getStatus() != null ? user.getStatus() : "active");
            setDateOrNull(ps, 9, user.getDateJoined());
            setDateOrNull(ps, 10, user.getStartWorkDate());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Tạo thành công user với ID: {}, name: {}", id, user.getFullName());
                        return findById(id);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi tạo user: " + user.getFullName(), e);
        }

        return Optional.empty();
    }

    /**
     * Cập nhật user
     */
    public boolean update(User user) {
        if (user == null || user.getId() == null) {
            logger.warn("Dữ liệu user không hợp lệ để cập nhật");
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER)) {

            ps.setString(1, user.getEmployeeCode());
            ps.setString(2, user.getFullName().trim());
            ps.setString(3, user.getCccd());
            ps.setString(4, user.getEmailCompany());
            ps.setString(5, user.getPhone());
            setLongOrNull(ps, 6, user.getDepartmentId());
            setLongOrNull(ps, 7, user.getPositionId());
            ps.setString(8, user.getStatus());
            setDateOrNull(ps, 9, user.getDateJoined());
            setDateOrNull(ps, 10, user.getStartWorkDate());
            ps.setLong(11, user.getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Cập nhật thành công user ID: {}", user.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi cập nhật user ID: " + user.getId(), e);
        }

        return false;
    }

    /**
     * Cập nhật status của user
     */
    public boolean updateStatus(Long id, String status) {
        if (id == null || status == null) {
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {

            ps.setString(1, status);
            ps.setLong(2, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Cập nhật status thành công cho user ID: {} -> {}", id, status);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi cập nhật status user ID: " + id, e);
        }

        return false;
    }

    /**
     * Xóa user
     */
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_USER)) {

            ps.setLong(1, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Xóa thành công user ID: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi xóa user ID: " + id, e);
        }

        return false;
    }

    /**
     * Helper methods for null handling
     */
    private void setLongOrNull(PreparedStatement ps, int parameterIndex, Long value) throws SQLException {
        if (value != null) {
            ps.setLong(parameterIndex, value);
        } else {
            ps.setNull(parameterIndex, Types.BIGINT);
        }
    }

    private void setDateOrNull(PreparedStatement ps, int parameterIndex, LocalDate value) throws SQLException {
        if (value != null) {
            ps.setDate(parameterIndex, Date.valueOf(value));
        } else {
            ps.setNull(parameterIndex, Types.DATE);
        }
    }

    private void setBigDecimalOrNull(PreparedStatement ps, int parameterIndex, BigDecimal value) throws SQLException {
        if (value != null) {
            ps.setBigDecimal(parameterIndex, value);
        } else {
            ps.setNull(parameterIndex, Types.DECIMAL);
        }
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmployeeCode(rs.getString("employee_code"));
        user.setFullName(rs.getString("full_name"));
        user.setCccd(rs.getString("cccd"));
        user.setEmailCompany(rs.getString("email_company"));
        user.setPhone(rs.getString("phone"));

        long deptId = rs.getLong("department_id");
        if (!rs.wasNull()) {
            user.setDepartmentId(deptId);
        }

        long posId = rs.getLong("position_id");
        if (!rs.wasNull()) {
            user.setPositionId(posId);
        }

        user.setStatus(rs.getString("status"));

        Date dateJoined = rs.getDate("date_joined");
        if (dateJoined != null) {
            user.setDateJoined(dateJoined.toLocalDate());
        }

        Date dateLeft = rs.getDate("date_left");
        if (dateLeft != null) {
            user.setDateLeft(dateLeft.toLocalDate());
        }

        Date startWorkDate = rs.getDate("start_work_date");
        if (startWorkDate != null) {
            user.setStartWorkDate(startWorkDate.toLocalDate());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }
}