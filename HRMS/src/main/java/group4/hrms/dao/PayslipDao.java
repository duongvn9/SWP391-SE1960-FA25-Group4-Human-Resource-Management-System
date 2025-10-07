package group4.hrms.dao;

import group4.hrms.model.Payslip;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng payslips
 * Mapping theo database schema mới
 * 
 * @author Group4
 */
public class PayslipDao extends BaseDao<Payslip, Long> {
    
    @Override
    protected String getTableName() {
        return "payslips";
    }
    
    @Override
    protected Payslip mapResultSetToEntity(ResultSet rs) throws SQLException {
        Payslip payslip = new Payslip();
        payslip.setId(rs.getLong("id"));
        payslip.setUserId(rs.getLong("user_id"));
        payslip.setPeriodStart(rs.getDate("period_start") != null ? rs.getDate("period_start").toLocalDate() : null);
        payslip.setPeriodEnd(rs.getDate("period_end") != null ? rs.getDate("period_end").toLocalDate() : null);
        payslip.setCurrency(rs.getString("currency"));
        payslip.setGrossAmount(rs.getBigDecimal("gross_amount"));
        payslip.setNetAmount(rs.getBigDecimal("net_amount"));
        payslip.setDetailsJson(rs.getString("details_json"));
        payslip.setFilePath(rs.getString("file_path"));
        payslip.setStatus(rs.getString("status"));
        payslip.setCreatedAt(getLocalDateTime(rs, "created_at"));
        
        return payslip;
    }
    
    @Override
    protected void setEntityId(Payslip payslip, Long id) {
        payslip.setId(id);
    }
    
    @Override
    protected Long getEntityId(Payslip payslip) {
        return payslip.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO payslips (user_id, period_start, period_end, currency, " +
               "gross_amount, net_amount, details_json, file_path, status, created_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE payslips SET user_id = ?, period_start = ?, period_end = ?, currency = ?, " +
               "gross_amount = ?, net_amount = ?, details_json = ?, file_path = ?, status = ? " +
               "WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Payslip payslip) throws SQLException {
        stmt.setLong(1, payslip.getUserId());
        stmt.setObject(2, payslip.getPeriodStart() != null ? Date.valueOf(payslip.getPeriodStart()) : null, Types.DATE);
        stmt.setObject(3, payslip.getPeriodEnd() != null ? Date.valueOf(payslip.getPeriodEnd()) : null, Types.DATE);
        stmt.setString(4, payslip.getCurrency());
        stmt.setBigDecimal(5, payslip.getGrossAmount());
        stmt.setBigDecimal(6, payslip.getNetAmount());
        stmt.setString(7, payslip.getDetailsJson());
        stmt.setString(8, payslip.getFilePath());
        stmt.setString(9, payslip.getStatus());
        setTimestamp(stmt, 10, payslip.getCreatedAt() != null ? payslip.getCreatedAt() : LocalDateTime.now());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Payslip payslip) throws SQLException {
        stmt.setLong(1, payslip.getUserId());
        stmt.setObject(2, payslip.getPeriodStart() != null ? Date.valueOf(payslip.getPeriodStart()) : null, Types.DATE);
        stmt.setObject(3, payslip.getPeriodEnd() != null ? Date.valueOf(payslip.getPeriodEnd()) : null, Types.DATE);
        stmt.setString(4, payslip.getCurrency());
        stmt.setBigDecimal(5, payslip.getGrossAmount());
        stmt.setBigDecimal(6, payslip.getNetAmount());
        stmt.setString(7, payslip.getDetailsJson());
        stmt.setString(8, payslip.getFilePath());
        stmt.setString(9, payslip.getStatus());
        stmt.setLong(10, payslip.getId());
    }
    
    // Business methods
    
    /**
     * Tìm payslip theo userId và kỳ lương
     */
    public Optional<Payslip> findByUserAndPeriod(Long userId, java.time.LocalDate periodStart, java.time.LocalDate periodEnd) throws SQLException {
        if (userId == null || periodStart == null || periodEnd == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM payslips WHERE user_id = ? AND period_start = ? AND period_end = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(periodStart));
            stmt.setDate(3, Date.valueOf(periodEnd));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Tìm tất cả payslip của một user
     */
    public List<Payslip> findByUserId(Long userId) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();
        
        if (userId == null) {
            return payslips;
        }
        
        String sql = "SELECT * FROM payslips WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }
        
        return payslips;
    }
    
    /**
     * Tìm payslip theo status
     */
    public List<Payslip> findByStatus(String status) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();
        
        if (status == null || status.trim().isEmpty()) {
            return payslips;
        }
        
        String sql = "SELECT * FROM payslips WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }
        
        return payslips;
    }
    
    /**
     * Tìm payslip theo khoảng thời gian
     */
    public List<Payslip> findByPeriodRange(java.time.LocalDate startDate, java.time.LocalDate endDate) throws SQLException {
        List<Payslip> payslips = new ArrayList<>();
        
        if (startDate == null || endDate == null) {
            return payslips;
        }
        
        String sql = "SELECT * FROM payslips WHERE period_start >= ? AND period_end <= ? ORDER BY period_start DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payslips.add(mapResultSetToEntity(rs));
                }
            }
        }
        
        return payslips;
    }
    
    /**
     * Cập nhật status của payslip
     */
    public boolean updateStatus(Long payslipId, String newStatus) throws SQLException {
        if (payslipId == null || newStatus == null || newStatus.trim().isEmpty()) {
            return false;
        }
        
        String sql = "UPDATE payslips SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setLong(2, payslipId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Đếm số payslip theo status
     */
    public long countByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM payslips WHERE status = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Đếm số payslip của một user
     */
    public long countByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM payslips WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Kiểm tra xem payslip đã tồn tại cho user và kỳ lương chưa
     */
    public boolean existsByUserAndPeriod(Long userId, java.time.LocalDate periodStart, java.time.LocalDate periodEnd) throws SQLException {
        return findByUserAndPeriod(userId, periodStart, periodEnd).isPresent();
    }
    
    /**
     * Lấy payslip với thông tin user (JOIN query)
     */
    public List<Payslip> findWithUserInfo() throws SQLException {
        List<Payslip> payslips = new ArrayList<>();
        
        String sql = "SELECT p.*, u.username, u.full_name, u.employee_id " +
                    "FROM payslips p " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "ORDER BY p.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Payslip payslip = mapResultSetToEntity(rs);
                // Note: User info có thể được map vào PayslipDto thay vì entity
                payslips.add(payslip);
            }
        }
        
        return payslips;
    }
}