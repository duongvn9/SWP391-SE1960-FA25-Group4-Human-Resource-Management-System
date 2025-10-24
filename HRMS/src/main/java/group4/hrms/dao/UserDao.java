package group4.hrms.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.model.User;
import group4.hrms.util.DatabaseUtil;

/**
 * DAO cho User entity
 * Xử lý các thao tác CRUD với bảng users
 */
public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    // SQL queries
    private static final String SELECT_ALL = """
            SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                   u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                   u.start_work_date, u.created_at, u.updated_at,
                   d.name as department_name, p.name as position_name
            FROM users u
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN positions p ON u.position_id = p.id
            ORDER BY u.created_at DESC
            """;

    private static final String SELECT_BY_ID = """
            SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                   u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                   u.start_work_date, u.created_at, u.updated_at,
                   d.name as department_name, p.name as position_name
            FROM users u
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN positions p ON u.position_id = p.id
            WHERE u.id = ?
            """;

    private static final String SELECT_BY_EMPLOYEE_CODE = """
            SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                   u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                   u.start_work_date, u.created_at, u.updated_at,
                   d.name as department_name, p.name as position_name
            FROM users u
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN positions p ON u.position_id = p.id
            WHERE u.employee_code = ?
            """;

    private static final String SELECT_BY_EMAIL = """
            SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                   u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                   u.start_work_date, u.created_at, u.updated_at,
                   d.name as department_name, p.name as position_name
            FROM users u
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN positions p ON u.position_id = p.id
            WHERE u.email_company = ?
            """;

    private static final String SELECT_BY_DEPARTMENT = """
            SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                   u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
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
                              gender, department_id, position_id, status, date_joined, start_work_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_USER = """
            UPDATE users SET
                employee_code = ?, full_name = ?, cccd = ?, email_company = ?, phone = ?,
                gender = ?, department_id = ?, position_id = ?, status = ?, date_joined = ?, start_work_date = ?,
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
            ps.setString(6, user.getGender());
            setLongOrNull(ps, 7, user.getDepartmentId());
            setLongOrNull(ps, 8, user.getPositionId());
            ps.setString(9, user.getStatus() != null ? user.getStatus() : "active");
            setDateOrNull(ps, 10, user.getDateJoined());
            setDateOrNull(ps, 11, user.getStartWorkDate());

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
            ps.setString(6, user.getGender());
            setLongOrNull(ps, 7, user.getDepartmentId());
            setLongOrNull(ps, 8, user.getPositionId());
            ps.setString(9, user.getStatus());
            setDateOrNull(ps, 10, user.getDateJoined());
            setDateOrNull(ps, 11, user.getStartWorkDate());
            ps.setLong(12, user.getId());

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
     * Tìm users với filters và pagination
     *
     * @param search       Search keyword (employee code, full name, email)
     * @param departmentId Department filter
     * @param positionId   Position filter
     * @param status       Status filter
     * @param gender       Gender filter
     * @param offset       Offset for pagination
     * @param limit        Limit for pagination
     * @param sortBy       Column to sort by
     * @param sortOrder    Sort order (asc/desc)
     * @return List of users matching filters
     */
    public List<User> findWithFilters(String search, Long departmentId, Long positionId,
            String status, String gender, int offset, int limit,
            String sortBy, String sortOrder) {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                       u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                       u.start_work_date, u.created_at, u.updated_at,
                       d.name as department_name, p.name as position_name
                FROM users u
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE 1=1
                """);

        // Build dynamic WHERE clauses
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (u.employee_code LIKE ? OR u.full_name LIKE ? OR u.email_company LIKE ?)");
        }
        if (departmentId != null) {
            sql.append(" AND u.department_id = ?");
        }
        if (positionId != null) {
            sql.append(" AND u.position_id = ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND u.status = ?");
        }
        if (gender != null && !gender.trim().isEmpty()) {
            sql.append(" AND u.gender = ?");
        }

        // Add ORDER BY
        String validSortBy = validateSortColumn(sortBy);
        String validSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
        sql.append(" ORDER BY ").append(validSortBy).append(" ").append(validSortOrder);

        // Add LIMIT and OFFSET (MySQL syntax)
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameters
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            // Set filter parameters
            if (departmentId != null) {
                ps.setLong(paramIndex++, departmentId);
            }
            if (positionId != null) {
                ps.setLong(paramIndex++, positionId);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status.trim());
            }
            if (gender != null && !gender.trim().isEmpty()) {
                ps.setString(paramIndex++, gender.trim());
            }

            // Set pagination parameters (limit first, then offset for MySQL)
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

            logger.info("Tìm thấy {} users với filters", users.size());
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm users với filters", e);
        }

        return users;
    }

    /**
     * Đếm tổng số users với filters
     *
     * @param search       Search keyword
     * @param departmentId Department filter
     * @param positionId   Position filter
     * @param status       Status filter
     * @param gender       Gender filter
     * @return Total count of users matching filters
     */
    public int countWithFilters(String search, Long departmentId, Long positionId, String status, String gender) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users u WHERE 1=1");

        // Build dynamic WHERE clauses
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (u.employee_code LIKE ? OR u.full_name LIKE ? OR u.email_company LIKE ?)");
        }
        if (departmentId != null) {
            sql.append(" AND u.department_id = ?");
        }
        if (positionId != null) {
            sql.append(" AND u.position_id = ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND u.status = ?");
        }
        if (gender != null && !gender.trim().isEmpty()) {
            sql.append(" AND u.gender = ?");
        }

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameters
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            // Set filter parameters
            if (departmentId != null) {
                ps.setLong(paramIndex++, departmentId);
            }
            if (positionId != null) {
                ps.setLong(paramIndex++, positionId);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status.trim());
            }
            if (gender != null && !gender.trim().isEmpty()) {
                ps.setString(paramIndex++, gender.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi đếm users với filters", e);
        }

        return 0;
    }

    /**
     * Kiểm tra user có accounts active không
     *
     * @param userId User ID
     * @return true nếu user có ít nhất 1 account active
     */
    public boolean hasActiveAccounts(Long userId) {
        if (userId == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM accounts WHERE user_id = ? AND status = 'active'";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.debug("User ID {} có {} active accounts", userId, count);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi kiểm tra active accounts cho user ID: " + userId, e);
        }

        return false;
    }

    /**
     * Tìm users với filters và pagination, trả về UserListDto
     *
     * @param search       Search keyword (employee code, full name, email)
     * @param departmentId Department filter
     * @param positionId   Position filter
     * @param status       Status filter
     * @param gender       Gender filter
     * @param offset       Offset for pagination
     * @param limit        Limit for pagination
     * @param sortBy       Column to sort by
     * @param sortOrder    Sort order (asc/desc)
     * @return List of UserListDto matching filters
     */
    public List<group4.hrms.dto.UserListDto> findWithFiltersAsDto(String search, Long departmentId, Long positionId,
            String status, String gender, int offset, int limit,
            String sortBy, String sortOrder) {
        List<group4.hrms.dto.UserListDto> userDtos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT u.id, u.employee_code, u.full_name, u.email_company, u.gender,
                       d.name as department_name, p.name as position_name,
                       u.status, u.date_joined,
                       (SELECT COUNT(*) FROM accounts a
                        WHERE a.user_id = u.id AND a.status = 'active') as active_account_count
                FROM users u
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE 1=1
                """);

        // Build dynamic WHERE clauses
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (u.employee_code LIKE ? OR u.full_name LIKE ? OR u.email_company LIKE ?)");
        }
        if (departmentId != null) {
            sql.append(" AND u.department_id = ?");
        }
        if (positionId != null) {
            sql.append(" AND u.position_id = ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND u.status = ?");
        }
        if (gender != null && !gender.trim().isEmpty()) {
            sql.append(" AND u.gender = ?");
        }

        // Add ORDER BY
        String validSortBy = validateSortColumn(sortBy);
        String validSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
        sql.append(" ORDER BY ").append(validSortBy).append(" ").append(validSortOrder);

        // Add LIMIT and OFFSET (MySQL syntax)
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameters
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            // Set filter parameters
            if (departmentId != null) {
                ps.setLong(paramIndex++, departmentId);
            }
            if (positionId != null) {
                ps.setLong(paramIndex++, positionId);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status.trim());
            }
            if (gender != null && !gender.trim().isEmpty()) {
                ps.setString(paramIndex++, gender.trim());
            }

            // Set pagination parameters (limit first, then offset for MySQL)
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userDtos.add(new group4.hrms.dto.UserListDto(rs));
                }
            }

            logger.info("Tìm thấy {} user DTOs với filters", userDtos.size());
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm user DTOs với filters", e);
        }

        return userDtos;
    }

    /**
     * Validate và sanitize sort column để tránh SQL injection
     */
    private String validateSortColumn(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "u.created_at";
        }

        // Whitelist các columns được phép sort
        return switch (sortBy.toLowerCase()) {
            case "employee_code" -> "u.employee_code";
            case "full_name" -> "u.full_name";
            case "email_company" -> "u.email_company";
            case "department_name" -> "d.name";
            case "position_name" -> "p.name";
            case "status" -> "u.status";
            case "date_joined" -> "u.date_joined";
            case "created_at" -> "u.created_at";
            default -> "u.created_at";
        };
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
        user.setGender(rs.getString("gender"));

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

    /**
     * Get subordinates (employees with lower job_level) for a manager
     * - If user is DEPT_MANAGER: only get employees in same department with lower
     * job_level
     * - If user is HR_MANAGER/HR_STAFF/ADMIN: get all employees with lower
     * job_level across all departments
     *
     * @param userId The manager's user ID
     * @return List of subordinate users
     */
    public List<User> getSubordinates(Long userId) throws SQLException {
        List<User> subordinates = new ArrayList<>();

        String query = """
                SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                       u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                       u.start_work_date, u.created_at, u.updated_at,
                       d.name as department_name, p.name as position_name, p.job_level
                FROM users u
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE u.id != ?
                  AND u.status = 'ACTIVE'
                  AND p.job_level > (
                      SELECT p2.job_level
                      FROM users u2
                      JOIN positions p2 ON u2.position_id = p2.id
                      WHERE u2.id = ?
                  )
                  AND (
                      -- If manager is DEPT_MANAGER, only show same department
                      (SELECT p2.code FROM users u2 JOIN positions p2 ON u2.position_id = p2.id WHERE u2.id = ?) = 'DEPT_MANAGER'
                      AND u.department_id = (SELECT department_id FROM users WHERE id = ?)
                      OR
                      -- If manager is HR/ADMIN, show all departments
                      (SELECT p2.code FROM users u2 JOIN positions p2 ON u2.position_id = p2.id WHERE u2.id = ?) IN ('ADMIN', 'HR_MANAGER', 'HR_STAFF')
                  )
                ORDER BY d.name, p.job_level, u.full_name
                """;

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, userId);
            ps.setLong(4, userId);
            ps.setLong(5, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    subordinates.add(user);
                }
            }

            logger.info("Found {} subordinates for user ID: {}", subordinates.size(), userId);
        } catch (SQLException e) {
            logger.error("Error getting subordinates for user ID: " + userId, e);
            throw e;
        }

        return subordinates;
    }

    /**
     * Find subordinate user IDs for a given manager based on department hierarchy
     * and job level.
     * This method is optimized for filtering requests by subordinate scope.
     *
     * Logic:
     * - For DEPT_MANAGER (job_level 4): Returns users in same department with
     * higher job_level (5 = STAFF)
     * - For HR_STAFF/HR_MANAGER/ADMIN (job_level 1-3): Returns all users with
     * higher job_level across all departments
     * - Excludes the manager themselves
     * - Only includes active users
     *
     * @param managerId User ID of the manager
     * @return List of subordinate user IDs
     */
    public List<Long> findSubordinateUserIds(Long managerId) {
        List<Long> subordinateIds = new ArrayList<>();

        if (managerId == null) {
            logger.warn("Manager ID is null, returning empty subordinate list");
            return subordinateIds;
        }

        String query = """
                SELECT u.id
                FROM users u
                JOIN positions p ON u.position_id = p.id
                WHERE u.id != ?
                  AND u.status = 'active'
                  AND p.job_level > (
                      SELECT p2.job_level
                      FROM users u2
                      JOIN positions p2 ON u2.position_id = p2.id
                      WHERE u2.id = ?
                  )
                  AND (
                      -- If manager is DEPT_MANAGER (job_level 4), only show same department
                      (
                          (SELECT p2.job_level FROM users u2 JOIN positions p2 ON u2.position_id = p2.id WHERE u2.id = ?) = 4
                          AND u.department_id = (SELECT department_id FROM users WHERE id = ?)
                      )
                      OR
                      -- If manager is HR/ADMIN (job_level 1-3), show all departments
                      (SELECT p2.job_level FROM users u2 JOIN positions p2 ON u2.position_id = p2.id WHERE u2.id = ?) < 4
                  )
                """;

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, managerId);
            ps.setLong(2, managerId);
            ps.setLong(3, managerId);
            ps.setLong(4, managerId);
            ps.setLong(5, managerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subordinateIds.add(rs.getLong("id"));
                }
            }

            logger.info("Found {} subordinates for manager ID: {}", subordinateIds.size(), managerId);
        } catch (SQLException e) {
            logger.error("Error finding subordinate user IDs for manager ID: " + managerId, e);
        }

        return subordinateIds;
    }

    /**
     * Tìm users theo status
     *
     * @param status Status của user (active, inactive, terminated)
     * @return List of users với status được chỉ định
     */
    public List<User> findByStatus(String status) {
        logger.debug("Tìm users theo status: {}", status);

        List<User> users = new ArrayList<>();
        String sql = """
                SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                       u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                       u.start_work_date, u.created_at, u.updated_at
                FROM users u
                WHERE u.status = ?
                ORDER BY u.full_name
                """;

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setEmployeeCode(rs.getString("employee_code"));
                    user.setFullName(rs.getString("full_name"));
                    user.setCccd(rs.getString("cccd"));
                    user.setEmailCompany(rs.getString("email_company"));
                    user.setPhone(rs.getString("phone"));
                    user.setGender(rs.getString("gender"));

                    Long deptId = rs.getLong("department_id");
                    if (!rs.wasNull()) {
                        user.setDepartmentId(deptId);
                    }

                    Long posId = rs.getLong("position_id");
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

                    users.add(user);
                }
            }

            logger.debug("Tìm thấy {} users với status: {}", users.size(), status);
            return users;

        } catch (SQLException e) {
            logger.error("Lỗi khi tìm users theo status {}: {}", status, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm users theo status", e);
        }
    }

    /**
     * Tìm employee code lớn nhất trong hệ thống
     * Dùng để auto-generate employee code mới
     *
     * @return Employee code lớn nhất (format: HExxxx) hoặc null nếu chưa có user
     *         nào
     */
    public String findMaxEmployeeCode() {
        logger.debug("Tìm employee code lớn nhất");

        String sql = "SELECT employee_code FROM users WHERE employee_code LIKE 'HE%' ORDER BY employee_code DESC LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String maxCode = rs.getString("employee_code");
                logger.debug("Employee code lớn nhất: {}", maxCode);
                return maxCode;
            }

            logger.debug("Chưa có employee code nào trong hệ thống");
            return null;

        } catch (SQLException e) {
            logger.error("Lỗi khi tìm max employee code: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm max employee code", e);
        }
    }

    /**
     * Kiểm tra employee code đã tồn tại chưa
     *
     * @param employeeCode Employee code cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    public boolean isEmployeeCodeExists(String employeeCode) {
        if (employeeCode == null || employeeCode.trim().isEmpty()) {
            return false;
        }

        logger.debug("Kiểm tra employee code tồn tại: {}", employeeCode);

        String sql = "SELECT COUNT(*) FROM users WHERE employee_code = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeCode.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt(1) > 0;
                    logger.debug("Employee code {} {} tồn tại", employeeCode, exists ? "đã" : "chưa");
                    return exists;
                }
            }

        } catch (SQLException e) {
            logger.error("Lỗi khi kiểm tra employee code tồn tại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi kiểm tra employee code tồn tại", e);
        }

        return false;
    }

    /**
     * Kiểm tra company email đã tồn tại chưa
     *
     * @param emailCompany Company email cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    public boolean isCompanyEmailExists(String emailCompany) {
        if (emailCompany == null || emailCompany.trim().isEmpty()) {
            return false;
        }

        logger.debug("Kiểm tra company email tồn tại: {}", emailCompany);

        String sql = "SELECT COUNT(*) FROM users WHERE email_company = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emailCompany.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt(1) > 0;
                    logger.debug("Company email {} {} tồn tại", emailCompany, exists ? "đã" : "chưa");
                    return exists;
                }
            }

        } catch (SQLException e) {
            logger.error("Lỗi khi kiểm tra company email tồn tại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi kiểm tra company email tồn tại", e);
        }

        return false;
    }

    /**
     * Kiểm tra phone đã tồn tại chưa
     *
     * @param phone Phone number cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    public boolean isPhoneExists(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        logger.debug("Kiểm tra phone tồn tại: {}", phone);

        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt(1) > 0;
                    logger.debug("Phone {} {} tồn tại", phone, exists ? "đã" : "chưa");
                    return exists;
                }
            }

        } catch (SQLException e) {
            logger.error("Lỗi khi kiểm tra phone tồn tại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi kiểm tra phone tồn tại", e);
        }

        return false;
    }

    /**
     * Tìm department manager hiện tại của một department
     * Note: Simplified version - checks if any user in department has position with
     * code 'DEPT_MANAGER'
     *
     * @param departmentId Department ID
     * @return User là department manager hoặc null nếu không có
     */
    public Optional<User> findDepartmentManager(Long departmentId) {
        if (departmentId == null) {
            return Optional.empty();
        }

        logger.debug("Tìm department manager cho department ID: {}", departmentId);

        // Find user with "Department Manager" position in the specified department
        String sql = """
                SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                       u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                       u.start_work_date, u.created_at, u.updated_at,
                       d.name as department_name, p.name as position_name
                FROM users u
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE u.department_id = ?
                  AND u.status = 'active'
                  AND p.name = 'Department Manager'
                LIMIT 1
                """;

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, departmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User manager = mapResultSetToUser(rs);
                    logger.debug("Tìm thấy department manager: {} cho department ID: {}",
                            manager.getFullName(), departmentId);
                    return Optional.of(manager);
                }
            }

            logger.debug("Không tìm thấy department manager cho department ID: {}", departmentId);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Lỗi khi tìm department manager: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm department manager", e);
        }
    }

    /**
     * Lưu user mới vào database
     * Alias method for create() to match design document naming
     *
     * @param user User object cần lưu
     * @return User đã được lưu với ID, hoặc empty nếu thất bại
     */
    public Optional<User> save(User user) {
        return create(user);
    }

    /**
     * Tìm user theo ID và trả về UserDetailDto
     *
     * @param id User ID
     * @return UserDetailDto hoặc null nếu không tìm thấy
     */
    public group4.hrms.dto.UserDetailDto findByIdAsDto(Long id) {
        if (id == null) {
            return null;
        }

        String sql = """
                SELECT u.id, u.employee_code, u.full_name, u.phone, u.email_company,
                       u.gender, u.department_id, u.position_id, u.status,
                       u.date_joined, u.start_work_date, u.created_at, u.updated_at,
                       d.name as department_name, p.name as position_name
                FROM users u
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE u.id = ?
                """;

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new group4.hrms.dto.UserDetailDto(rs);
                }
            }

            logger.debug("Không tìm thấy user với ID: {}", id);
            return null;

        } catch (SQLException e) {
            logger.error("Lỗi khi tìm user detail với ID: {}", id, e);
            return null;
        }
    }

    /**
     * Tìm users theo position ID
     *
     * @param positionId Position ID
     * @return List of users with the specified position
     */
    public List<User> findByPositionId(Long positionId) {
        logger.debug("Tìm users theo position ID: {}", positionId);

        String sql = """
                SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                       u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                       u.start_work_date, u.created_at, u.updated_at,
                       d.name as department_name, p.name as position_name
                FROM users u
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE u.position_id = ? AND u.status = 'active'
                ORDER BY u.created_at DESC
                """;

        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, positionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

            logger.debug("Tìm thấy {} users với position ID: {}", users.size(), positionId);
            return users;

        } catch (SQLException e) {
            logger.error("Lỗi khi tìm users theo position ID: {}", positionId, e);
            return users;
        }
    }

    /**
     * Tìm users chưa có account (active users without any account)
     *
     * @return List of users without accounts
     */
    public List<User> findUsersWithoutAccount() {
        logger.debug("Tìm users chưa có account");

        String sql = """
                SELECT u.id, u.employee_code, u.full_name, u.cccd, u.email_company, u.phone,
                       u.gender, u.department_id, u.position_id, u.status, u.date_joined, u.date_left,
                       u.start_work_date, u.created_at, u.updated_at,
                       d.name as department_name, p.name as position_name
                FROM users u
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE u.status = 'active'
                  AND u.id NOT IN (SELECT user_id FROM accounts)
                ORDER BY u.created_at DESC
                """;

        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            logger.debug("Tìm thấy {} users chưa có account", users.size());
            return users;

        } catch (SQLException e) {
            logger.error("Lỗi khi tìm users chưa có account: {}", e.getMessage(), e);
            return users;
        }
    }

    /**
     * Tìm users chưa có account với giới hạn số lượng (for lazy loading)
     * Optimized query that only fetches necessary fields and limits results
     *
     * @param limit Maximum number of users to return
     * @return List of users without accounts (limited)
     */
    public List<User> findUsersWithoutAccountLimited(int limit) {
        logger.debug("Tìm users chưa có account với limit: {}", limit);

        // Validate limit
        if (limit <= 0) {
            limit = 10;
        }
        if (limit > 50) {
            limit = 50;
        }

        String sql = """
                SELECT u.id, u.employee_code, u.full_name, u.email_company, u.phone,
                       u.department_id, u.position_id, u.status, u.date_joined,
                       d.name as department_name, p.name as position_name
                FROM users u
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN positions p ON u.position_id = p.id
                WHERE u.status = 'active'
                  AND u.id NOT IN (SELECT user_id FROM accounts)
                ORDER BY u.created_at DESC
                LIMIT ?
                """;

        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Create user with only essential fields for performance
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setEmployeeCode(rs.getString("employee_code"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmailCompany(rs.getString("email_company"));
                    user.setPhone(rs.getString("phone"));
                    user.setStatus(rs.getString("status"));

                    long deptId = rs.getLong("department_id");
                    if (!rs.wasNull()) {
                        user.setDepartmentId(deptId);
                    }

                    long posId = rs.getLong("position_id");
                    if (!rs.wasNull()) {
                        user.setPositionId(posId);
                    }

                    Date dateJoined = rs.getDate("date_joined");
                    if (dateJoined != null) {
                        user.setDateJoined(dateJoined.toLocalDate());
                    }

                    users.add(user);
                }
            }

            logger.debug("Tìm thấy {} users chưa có account (limited)", users.size());
            return users;

        } catch (SQLException e) {
            logger.error("Lỗi khi tìm users chưa có account (limited): {}", e.getMessage(), e);
            return users;
        }
    }



    /**
     * Count total number of users
     * 
     * @return Total count of users
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            logger.error("Error counting all users: {}", e.getMessage(), e);
            return 0;
        }
    }

}
