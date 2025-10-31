package group4.hrms.dao;

import group4.hrms.model.LeaveBalance;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng LeaveBalance
 *
 * @author Group4
 */
public class LeaveBalanceDao extends BaseDao<LeaveBalance, Long> {

    @Override
    protected String getTableName() {
        return "leave_balances";
    }

    @Override
    protected LeaveBalance mapResultSetToEntity(ResultSet rs) throws SQLException {
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setId(rs.getLong("id"));
        leaveBalance.setUserId(rs.getLong("user_id"));
        leaveBalance.setLeaveTypeId(rs.getLong("leave_type_id"));
        leaveBalance.setYear(rs.getInt("year"));

        // Handle nullable fields
        int totalDays = rs.getInt("total_days");
        leaveBalance.setTotalDays(rs.wasNull() ? null : totalDays);

        int used = rs.getInt("used_days");
        leaveBalance.setUsedDays(rs.wasNull() ? null : used);

        int carriedForward = rs.getInt("carried_forward_days");
        leaveBalance.setCarriedForwardDays(rs.wasNull() ? null : carriedForward);

        int remaining = rs.getInt("remaining_days");
        leaveBalance.setRemainingDays(rs.wasNull() ? null : remaining);

        leaveBalance.setCreatedAt(getLocalDateTime(rs, "created_at"));
        leaveBalance.setUpdatedAt(getLocalDateTime(rs, "updated_at"));

        return leaveBalance;
    }

    @Override
    protected void setEntityId(LeaveBalance leaveBalance, Long id) {
        leaveBalance.setId(id);
    }

    @Override
    protected Long getEntityId(LeaveBalance leaveBalance) {
        return leaveBalance.getId();
    }

    @Override
    protected String createInsertSql() {
        return "INSERT INTO leave_balances (user_id, leave_type_id, year, total_days, " +
               "used_days, carried_forward_days, remaining_days, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE leave_balances SET user_id = ?, leave_type_id = ?, year = ?, " +
               "total_days = ?, used_days = ?, carried_forward_days = ?, " +
               "remaining_days = ?, updated_at = ? WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, LeaveBalance leaveBalance) throws SQLException {
        stmt.setLong(1, leaveBalance.getUserId());
        stmt.setLong(2, leaveBalance.getLeaveTypeId());
        stmt.setInt(3, leaveBalance.getYear());

        if (leaveBalance.getTotalDays() != null) {
            stmt.setInt(4, leaveBalance.getTotalDays());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }

        if (leaveBalance.getUsedDays() != null) {
            stmt.setInt(5, leaveBalance.getUsedDays());
        } else {
            stmt.setNull(5, Types.INTEGER);
        }

        if (leaveBalance.getCarriedForwardDays() != null) {
            stmt.setInt(6, leaveBalance.getCarriedForwardDays());
        } else {
            stmt.setNull(6, Types.INTEGER);
        }

        if (leaveBalance.getRemainingDays() != null) {
            stmt.setInt(7, leaveBalance.getRemainingDays());
        } else {
            stmt.setNull(7, Types.INTEGER);
        }

        LocalDateTime now = LocalDateTime.now();
        setTimestamp(stmt, 8, now);
        setTimestamp(stmt, 9, now);
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, LeaveBalance leaveBalance) throws SQLException {
        stmt.setLong(1, leaveBalance.getUserId());
        stmt.setLong(2, leaveBalance.getLeaveTypeId());
        stmt.setInt(3, leaveBalance.getYear());

        if (leaveBalance.getTotalDays() != null) {
            stmt.setInt(4, leaveBalance.getTotalDays());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }

        if (leaveBalance.getUsedDays() != null) {
            stmt.setInt(5, leaveBalance.getUsedDays());
        } else {
            stmt.setNull(5, Types.INTEGER);
        }

        if (leaveBalance.getCarriedForwardDays() != null) {
            stmt.setInt(6, leaveBalance.getCarriedForwardDays());
        } else {
            stmt.setNull(6, Types.INTEGER);
        }

        if (leaveBalance.getRemainingDays() != null) {
            stmt.setInt(7, leaveBalance.getRemainingDays());
        } else {
            stmt.setNull(7, Types.INTEGER);
        }

        setTimestamp(stmt, 8, LocalDateTime.now());
        stmt.setLong(9, leaveBalance.getId());
    }

    // Business methods

    /**
     * Tìm leave balance theo userId và leaveTypeId cho năm cụ thể
     */
    public Optional<LeaveBalance> findByUserAndLeaveTypeAndYear(Long userId, Long leaveTypeId, int year) throws SQLException {
        if (userId == null || leaveTypeId == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM leave_balances WHERE user_id = ? AND leave_type_id = ? AND year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, leaveTypeId);
            stmt.setInt(3, year);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding leave balance by user {} leave type {} year {}: {}",
                        userId, leaveTypeId, year, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm tất cả leave balances của user trong năm
     */
    public List<LeaveBalance> findByUserAndYear(Long userId, int year) throws SQLException {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<LeaveBalance> balances = new ArrayList<>();
        String sql = "SELECT * FROM leave_balances WHERE user_id = ? AND year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setInt(2, year);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    balances.add(mapResultSetToEntity(rs));
                }
            }

            return balances;

        } catch (SQLException e) {
            logger.error("Error finding leave balances by user {} year {}: {}", userId, year, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm tất cả leave balances của user
     */
    public List<LeaveBalance> findByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<LeaveBalance> balances = new ArrayList<>();
        String sql = "SELECT * FROM leave_balances WHERE user_id = ? ORDER BY year DESC, leave_type_id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    balances.add(mapResultSetToEntity(rs));
                }
            }

            return balances;

        } catch (SQLException e) {
            logger.error("Error finding leave balances by user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm tất cả leave balances theo leave type trong năm
     */
    public List<LeaveBalance> findByLeaveTypeAndYear(Long leaveTypeId, int year) throws SQLException {
        if (leaveTypeId == null) {
            return new ArrayList<>();
        }

        List<LeaveBalance> balances = new ArrayList<>();
        String sql = "SELECT * FROM leave_balances WHERE leave_type_id = ? AND year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, leaveTypeId);
            stmt.setInt(2, year);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    balances.add(mapResultSetToEntity(rs));
                }
            }

            return balances;

        } catch (SQLException e) {
            logger.error("Error finding leave balances by leave type {} year {}: {}",
                        leaveTypeId, year, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật used days khi có request được approve
     */
    public boolean updateUsedDays(Long leaveBalanceId, int usedDays) throws SQLException {
        if (leaveBalanceId == null) {
            return false;
        }

        String sql = "UPDATE leave_balances SET used_days = ?, " +
                    "remaining_days = (total_days + COALESCE(carried_forward_days, 0) - ?), " +
                    "updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usedDays);
            stmt.setInt(2, usedDays);
            setTimestamp(stmt, 3, LocalDateTime.now());
            stmt.setLong(4, leaveBalanceId);

            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;

            if (updated) {
                logger.info("Updated used days for leave balance {} to {}", leaveBalanceId, usedDays);
            }

            return updated;

        } catch (SQLException e) {
            logger.error("Error updating used days for leave balance {}: {}", leaveBalanceId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật carried forward days cho năm mới
     */
    public boolean updateCarriedForwardDays(Long leaveBalanceId, int carriedForwardDays) throws SQLException {
        if (leaveBalanceId == null) {
            return false;
        }

        String sql = "UPDATE leave_balances SET carried_forward_days = ?, " +
                    "remaining_days = (total_days + ? - COALESCE(used_days, 0)), " +
                    "updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, carriedForwardDays);
            stmt.setInt(2, carriedForwardDays);
            setTimestamp(stmt, 3, LocalDateTime.now());
            stmt.setLong(4, leaveBalanceId);

            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;

            if (updated) {
                logger.info("Updated carried forward days for leave balance {} to {}",
                           leaveBalanceId, carriedForwardDays);
            }

            return updated;

        } catch (SQLException e) {
            logger.error("Error updating carried forward days for leave balance {}: {}",
                        leaveBalanceId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tạo leave balance cho user và leave type mới
     */
    public LeaveBalance createLeaveBalance(Long userId, Long leaveTypeId, int year,
                                         int allocatedDays) throws SQLException {
        if (userId == null || leaveTypeId == null) {
            throw new IllegalArgumentException("UserId and LeaveTypeId cannot be null");
        }

        // Kiểm tra đã tồn tại chưa
        Optional<LeaveBalance> existing = findByUserAndLeaveTypeAndYear(userId, leaveTypeId, year);
        if (existing.isPresent()) {
            throw new SQLException("Leave balance already exists for user " + userId +
                                 " leave type " + leaveTypeId + " year " + year);
        }

        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setUserId(userId);
        leaveBalance.setLeaveTypeId(leaveTypeId);
        leaveBalance.setYear(year);
        leaveBalance.setTotalDays(allocatedDays);
        leaveBalance.setUsedDays(0);
        leaveBalance.setCarriedForwardDays(0);
        leaveBalance.setRemainingDays(allocatedDays);

        return save(leaveBalance);
    }

    /**
     * Tính số ngày annual leave dựa trên thâm niên
     * Quy tắc: 12 ngày cơ bản + 1 ngày cho mỗi 5 năm thâm niên
     *
     * @param userId ID của user
     * @param year Năm tính toán
     * @return Số ngày annual leave
     */
    public int calculateAnnualLeaveAllocation(Long userId, int year) throws SQLException {
        if (userId == null) {
            return 12; // Default 12 days
        }

        String sql = "SELECT date_joined FROM users WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dateJoined = rs.getDate("date_joined");
                    if (dateJoined != null) {
                        // Tính số năm thâm niên tính đến cuối năm tính toán
                        java.time.LocalDate joinDate = dateJoined.toLocalDate();
                        java.time.LocalDate endOfYear = java.time.LocalDate.of(year, 12, 31);

                        long yearsOfService = java.time.temporal.ChronoUnit.YEARS.between(joinDate, endOfYear);

                        // 12 ngày cơ bản + 1 ngày cho mỗi 5 năm thâm niên
                        int baseDays = 12;
                        int bonusDays = (int) (yearsOfService / 5);
                        int totalDays = baseDays + bonusDays;

                        logger.info("Calculated annual leave for user " + userId + ": " + yearsOfService + " years of service, " + totalDays + " total days");

                        return totalDays;
                    }
                }
            }

            // Fallback nếu không tìm thấy date_joined
            logger.warn("Could not find date_joined for user " + userId + ", using default 12 days");
            return 12;

        } catch (SQLException e) {
            logger.error("Error calculating annual leave allocation for user " + userId + ": " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tạo annual leave balance với tính toán thâm niên
     */
    public LeaveBalance createAnnualLeaveBalance(Long userId, Long annualLeaveTypeId, int year) throws SQLException {
        int allocatedDays = calculateAnnualLeaveAllocation(userId, year);
        return createLeaveBalance(userId, annualLeaveTypeId, year, allocatedDays);
    }

    /**
     * Tính tổng remaining days của user trong năm
     */
    public int getTotalRemainingDays(Long userId, int year) throws SQLException {
        if (userId == null) {
            return 0;
        }

        String sql = "SELECT SUM(COALESCE(remaining_days, 0)) FROM leave_balances " +
                    "WHERE user_id = ? AND year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setInt(2, year);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 0;

        } catch (SQLException e) {
            logger.error("Error getting total remaining days for user {} year {}: {}",
                        userId, year, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tính tổng used days của user trong năm
     */
    public int getTotalUsedDays(Long userId, int year) throws SQLException {
        if (userId == null) {
            return 0;
        }

        String sql = "SELECT SUM(COALESCE(used_days, 0)) FROM leave_balances " +
                    "WHERE user_id = ? AND year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setInt(2, year);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 0;

        } catch (SQLException e) {
            logger.error("Error getting total used days for user {} year {}: {}",
                        userId, year, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Xoá tất cả leave balances của user
     */
    public boolean deleteByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return false;
        }

        String sql = "DELETE FROM leave_balances WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;

            if (deleted) {
                logger.info("Deleted {} leave balances for user {}", affectedRows, userId);
            }

            return deleted;

        } catch (SQLException e) {
            logger.error("Error deleting leave balances for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Xoá tất cả leave balances của leave type
     */
    public boolean deleteByLeaveTypeId(Long leaveTypeId) throws SQLException {
        if (leaveTypeId == null) {
            return false;
        }

        String sql = "DELETE FROM leave_balances WHERE leave_type_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, leaveTypeId);

            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;

            if (deleted) {
                logger.info("Deleted {} leave balances for leave type {}", affectedRows, leaveTypeId);
            }

            return deleted;

        } catch (SQLException e) {
            logger.error("Error deleting leave balances for leave type {}: {}", leaveTypeId, e.getMessage(), e);
            throw e;
        }
    }
}