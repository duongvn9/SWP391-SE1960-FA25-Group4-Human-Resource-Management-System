package group4.hrms.dao;

import group4.hrms.model.LeaveType;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng LeaveType
 *
 * @author Group4
 */
public class LeaveTypeDao extends BaseDao<LeaveType, Long> {

    @Override
    protected String getTableName() {
        return "leave_types";
    }

    @Override
    protected LeaveType mapResultSetToEntity(ResultSet rs) throws SQLException {
        LeaveType leaveType = new LeaveType();
        leaveType.setId(rs.getLong("id"));
        leaveType.setName(rs.getString("name"));
        leaveType.setCode(rs.getString("code"));

        // Only read columns that exist in the database schema
        // Based on hrms_mysql.sql: id, code, name, default_days, is_paid, created_at

        // Try to read optional columns if they exist
        try {
            leaveType.setDescription(rs.getString("description"));
        } catch (SQLException e) {
            // Column doesn't exist, use default
            leaveType.setDescription(null);
        }

        Integer defaultDays = rs.getInt("default_days");
        if (!rs.wasNull()) {
            leaveType.setDefaultDays(defaultDays);
            leaveType.setMaxDays(defaultDays); // Use default_days as max_days if max_days doesn't exist
        }

        try {
            Integer maxDays = rs.getInt("max_days");
            if (!rs.wasNull()) {
                leaveType.setMaxDays(maxDays);
            }
        } catch (SQLException e) {
            // Column doesn't exist, already set to default_days above
        }

        leaveType.setPaid(rs.getBoolean("is_paid"));

        // Set defaults for columns that don't exist in database
        try {
            leaveType.setRequiresApproval(rs.getBoolean("requires_approval"));
        } catch (SQLException e) {
            leaveType.setRequiresApproval(true); // Default to true
        }

        try {
            leaveType.setRequiresCertificate(rs.getBoolean("requires_certificate"));
        } catch (SQLException e) {
            leaveType.setRequiresCertificate(false); // Default to false
        }

        try {
            Integer minAdvanceNotice = rs.getInt("min_advance_notice");
            if (!rs.wasNull()) {
                leaveType.setMinAdvanceNotice(minAdvanceNotice);
            }
        } catch (SQLException e) {
            leaveType.setMinAdvanceNotice(1); // Default to 1 day
        }

        try {
            leaveType.setCanCarryForward(rs.getBoolean("can_carry_forward"));
        } catch (SQLException e) {
            leaveType.setCanCarryForward(false); // Default to false
        }

        try {
            Integer maxCarryForward = rs.getInt("max_carry_forward");
            if (!rs.wasNull()) {
                leaveType.setMaxCarryForward(maxCarryForward);
            }
        } catch (SQLException e) {
            leaveType.setMaxCarryForward(null);
        }

        try {
            leaveType.setGender(rs.getString("gender"));
        } catch (SQLException e) {
            leaveType.setGender("ALL"); // Default to ALL
        }

        try {
            leaveType.setActive(rs.getBoolean("is_active"));
        } catch (SQLException e) {
            leaveType.setActive(true); // Default to active
        }

        leaveType.setCreatedAt(getLocalDateTime(rs, "created_at"));

        try {
            leaveType.setUpdatedAt(getLocalDateTime(rs, "updated_at"));
        } catch (SQLException e) {
            leaveType.setUpdatedAt(leaveType.getCreatedAt()); // Use created_at if updated_at doesn't exist
        }

        return leaveType;
    }

    @Override
    protected String createInsertSql() {
        return "INSERT INTO leave_types (name, code, description, default_days, max_days, " +
               "is_paid, requires_approval, requires_certificate, min_advance_notice, " +
               "can_carry_forward, max_carry_forward, gender, is_active, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE leave_types SET name = ?, code = ?, description = ?, default_days = ?, " +
               "max_days = ?, is_paid = ?, requires_approval = ?, requires_certificate = ?, " +
               "min_advance_notice = ?, can_carry_forward = ?, max_carry_forward = ?, " +
               "gender = ?, is_active = ?, updated_at = ? WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, LeaveType leaveType) throws SQLException {
        stmt.setString(1, leaveType.getName());
        stmt.setString(2, leaveType.getCode());
        stmt.setString(3, leaveType.getDescription());

        if (leaveType.getDefaultDays() != null) {
            stmt.setInt(4, leaveType.getDefaultDays());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }

        if (leaveType.getMaxDays() != null) {
            stmt.setInt(5, leaveType.getMaxDays());
        } else {
            stmt.setNull(5, Types.INTEGER);
        }

        stmt.setBoolean(6, leaveType.isPaid());
        stmt.setBoolean(7, leaveType.isRequiresApproval());
        stmt.setBoolean(8, leaveType.isRequiresCertificate());

        if (leaveType.getMinAdvanceNotice() != null) {
            stmt.setInt(9, leaveType.getMinAdvanceNotice());
        } else {
            stmt.setNull(9, Types.INTEGER);
        }

        stmt.setBoolean(10, leaveType.isCanCarryForward());

        if (leaveType.getMaxCarryForward() != null) {
            stmt.setInt(11, leaveType.getMaxCarryForward());
        } else {
            stmt.setNull(11, Types.INTEGER);
        }

        stmt.setString(12, leaveType.getGender());
        stmt.setBoolean(13, leaveType.isActive());

        LocalDateTime now = LocalDateTime.now();
        setTimestamp(stmt, 14, now);
        setTimestamp(stmt, 15, now);
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, LeaveType leaveType) throws SQLException {
        stmt.setString(1, leaveType.getName());
        stmt.setString(2, leaveType.getCode());
        stmt.setString(3, leaveType.getDescription());

        if (leaveType.getDefaultDays() != null) {
            stmt.setInt(4, leaveType.getDefaultDays());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }

        if (leaveType.getMaxDays() != null) {
            stmt.setInt(5, leaveType.getMaxDays());
        } else {
            stmt.setNull(5, Types.INTEGER);
        }

        stmt.setBoolean(6, leaveType.isPaid());
        stmt.setBoolean(7, leaveType.isRequiresApproval());
        stmt.setBoolean(8, leaveType.isRequiresCertificate());

        if (leaveType.getMinAdvanceNotice() != null) {
            stmt.setInt(9, leaveType.getMinAdvanceNotice());
        } else {
            stmt.setNull(9, Types.INTEGER);
        }

        stmt.setBoolean(10, leaveType.isCanCarryForward());

        if (leaveType.getMaxCarryForward() != null) {
            stmt.setInt(11, leaveType.getMaxCarryForward());
        } else {
            stmt.setNull(11, Types.INTEGER);
        }

        stmt.setString(12, leaveType.getGender());
        stmt.setBoolean(13, leaveType.isActive());
        setTimestamp(stmt, 14, LocalDateTime.now());
        stmt.setLong(15, leaveType.getId());
    }

    @Override
    protected void setEntityId(LeaveType leaveType, Long id) {
        leaveType.setId(id);
    }

    @Override
    protected Long getEntityId(LeaveType leaveType) {
        return leaveType.getId();
    }

    /**
     * Override save method để xử lý insert/update
     */
    @Override
    public LeaveType save(LeaveType entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        if (getEntityId(entity) != null) {
            return update(entity);
        } else {
            String sql = createInsertSql();

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                setInsertParameters(stmt, entity);

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating leave type failed, no rows affected");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long generatedId = generatedKeys.getLong(1);
                        setEntityId(entity, generatedId);
                    } else {
                        throw new SQLException("Creating leave type failed, no ID obtained");
                    }
                }

                logger.info("Created new leave type: {}", entity.getName());
                return entity;

            } catch (SQLException e) {
                logger.error("Error saving leave type: {}", e.getMessage(), e);
                throw e;
            }
        }
    }

    /**
     * Tìm leave type theo code
     */
    public Optional<LeaveType> findByCode(String code) throws SQLException {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM leave_types WHERE code = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding leave type by code {}: {}", code, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm leave type theo name
     */
    public Optional<LeaveType> findByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM leave_types WHERE name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding leave type by name {}: {}", name, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm tất cả leave types active
     */
    public List<LeaveType> findActiveLeaveTypes() throws SQLException {
        List<LeaveType> leaveTypes = new ArrayList<>();
        String sql = "SELECT * FROM leave_types WHERE is_active = true ORDER BY name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                leaveTypes.add(mapResultSetToEntity(rs));
            }

            logger.debug("Found {} active leave types", leaveTypes.size());
            return leaveTypes;

        } catch (SQLException e) {
            logger.error("Error finding active leave types: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm leave types theo gender restriction
     */
    public List<LeaveType> findByGenderRestriction(String gender) throws SQLException {
        List<LeaveType> leaveTypes = new ArrayList<>();
        String sql = "SELECT * FROM leave_types WHERE (gender_restriction IS NULL OR gender_restriction = '' OR gender_restriction = ?) " +
                    "AND is_active = true ORDER BY sort_order, name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gender);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    leaveTypes.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} leave types for gender {}", leaveTypes.size(), gender);
            return leaveTypes;

        } catch (SQLException e) {
            logger.error("Error finding leave types by gender {}: {}", gender, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm leave types có yêu cầu approval
     */
    public List<LeaveType> findRequireApprovalTypes() throws SQLException {
        List<LeaveType> leaveTypes = new ArrayList<>();
        String sql = "SELECT * FROM leave_types WHERE require_approval = true AND is_active = true ORDER BY sort_order, name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                leaveTypes.add(mapResultSetToEntity(rs));
            }

            logger.debug("Found {} leave types requiring approval", leaveTypes.size());
            return leaveTypes;

        } catch (SQLException e) {
            logger.error("Error finding leave types requiring approval: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm leave types có thể carry forward
     */
    public List<LeaveType> findCarryForwardTypes() throws SQLException {
        List<LeaveType> leaveTypes = new ArrayList<>();
        String sql = "SELECT * FROM leave_types WHERE is_carry_forward = true AND is_active = true ORDER BY sort_order, name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                leaveTypes.add(mapResultSetToEntity(rs));
            }

            logger.debug("Found {} leave types with carry forward", leaveTypes.size());
            return leaveTypes;

        } catch (SQLException e) {
            logger.error("Error finding carry forward leave types: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Kiểm tra code đã tồn tại
     */
    public boolean existsByCode(String code) throws SQLException {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM leave_types WHERE code = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking exists by code {}: {}", code, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Kiểm tra name đã tồn tại (trừ leave type hiện tại)
     */
    public boolean existsByNameExcludingId(String name, Long excludeId) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM leave_types WHERE name = ? AND id != ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.trim());
            stmt.setLong(2, excludeId != null ? excludeId : -1);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking name exists {}: {}", name, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật trạng thái active
     */
    public boolean updateActiveStatus(Long leaveTypeId, boolean isActive) throws SQLException {
        if (leaveTypeId == null) {
            return false;
        }

        String sql = "UPDATE leave_types SET is_active = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isActive);
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, leaveTypeId);

            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;

            if (updated) {
                logger.info("Updated active status for leave type {} to {}", leaveTypeId, isActive);
            }

            return updated;

        } catch (SQLException e) {
            logger.error("Error updating active status for leave type {}: {}", leaveTypeId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật mô tả của leave type
     */
    public boolean updateDescription(Long leaveTypeId, String description) throws SQLException {
        if (leaveTypeId == null) {
            return false;
        }

        String sql = "UPDATE leave_types SET description = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, description);
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setLong(3, leaveTypeId);

            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;

            if (updated) {
                logger.info("Updated description for leave type {}", leaveTypeId);
            }

            return updated;

        } catch (SQLException e) {
            logger.error("Error updating description for leave type {}: {}", leaveTypeId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Đếm leave types theo active status
     */
    public long countByActiveStatus(boolean isActive) throws SQLException {
        String sql = "SELECT COUNT(*) FROM leave_types WHERE is_active = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isActive);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

            return 0;

        } catch (SQLException e) {
            logger.error("Error counting leave types by active status {}: {}", isActive, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm kiếm leave types theo từ khóa
     */
    public List<LeaveType> searchLeaveTypes(String keyword, int offset, int limit) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        List<LeaveType> leaveTypes = new ArrayList<>();
        String sql = "SELECT * FROM leave_types WHERE " +
                    "name LIKE ? OR code LIKE ? OR description LIKE ? " +
                    "ORDER BY name LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword.trim() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setInt(4, limit);
            stmt.setInt(5, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    leaveTypes.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} leave types matching keyword '{}'", leaveTypes.size(), keyword);
            return leaveTypes;

        } catch (SQLException e) {
            logger.error("Error searching leave types with keyword {}: {}", keyword, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Kiểm tra leave type có đang được sử dụng không
     */
    public boolean isLeaveTypeInUse(Long leaveTypeId) throws SQLException {
        if (leaveTypeId == null) {
            return false;
        }

        // Kiểm tra trong bảng requests và leave_balances
        String sql = "SELECT COUNT(*) FROM requests WHERE request_type_id = ? " +
                    "UNION ALL SELECT COUNT(*) FROM leave_balances WHERE leave_type_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, leaveTypeId);
            stmt.setLong(2, leaveTypeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking if leave type {} is in use: {}", leaveTypeId, e.getMessage(), e);
            throw e;
        }
    }
}