package group4.hrms.dao;

import group4.hrms.model.Position;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class cho Position entity
 * Xử lý các thao tác CRUD với bảng positions
 */
public class PositionDao {
    
    private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);
    
    // SQL queries
    private static final String INSERT_POSITION = 
        "INSERT INTO positions (code, name, job_level, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_POSITION = 
        "UPDATE positions SET code = ?, name = ?, job_level = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE_POSITION = "DELETE FROM positions WHERE id = ?";
    
    private static final String SELECT_POSITION_BY_ID = 
        "SELECT id, code, name, job_level, created_at, updated_at FROM positions WHERE id = ?";
    
    private static final String SELECT_ALL_POSITIONS = 
        "SELECT id, code, name, job_level, created_at, updated_at FROM positions ORDER BY name";
    
    private static final String SELECT_POSITIONS_BY_LEVEL = 
        "SELECT id, code, name, job_level, created_at, updated_at FROM positions WHERE job_level = ? ORDER BY name";
    
    private static final String SELECT_POSITION_BY_CODE = 
        "SELECT id, code, name, job_level, created_at, updated_at FROM positions WHERE code = ?";
    
    private static final String SELECT_POSITION_BY_NAME = 
        "SELECT id, code, name, job_level, created_at, updated_at FROM positions WHERE name = ?";
    
    private static final String COUNT_USERS_IN_POSITION = 
        "SELECT COUNT(*) FROM users WHERE position_id = ?";
    
    /**
     * Tạo mới position
     */
    public Position create(Position position) {
        logger.info("Tạo mới position: {}", position.getName());
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_POSITION, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, position.getCode());
            stmt.setString(2, position.getName());
            setNullableInteger(stmt, 3, position.getJobLevel());
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.setTimestamp(5, Timestamp.valueOf(now));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Tạo position thất bại, không có row nào được insert");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    position.setId(generatedKeys.getLong(1));
                    position.setCreatedAt(now);
                    position.setUpdatedAt(now);
                    
                    logger.info("Tạo position thành công với ID: {}", position.getId());
                    return position;
                } else {
                    throw new SQLException("Tạo position thất bại, không lấy được generated ID");
                }
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tạo position: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo position", e);
        }
    }
    
    /**
     * Cập nhật position
     */
    public Position update(Position position) {
        logger.info("Cập nhật position ID: {}", position.getId());
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_POSITION)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, position.getCode());
            stmt.setString(2, position.getName());
            setNullableInteger(stmt, 3, position.getJobLevel());
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.setLong(5, position.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                logger.warn("Cập nhật thất bại, không tìm thấy position với ID: {}", position.getId());
                return null;
            }
            
            position.setUpdatedAt(now);
            logger.info("Cập nhật position thành công: {}", position.getId());
            return position;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi cập nhật position ID {}: {}", position.getId(), e.getMessage(), e);
            throw new RuntimeException("Lỗi khi cập nhật position", e);
        }
    }
    
    /**
     * Xóa position theo ID
     */
    public boolean delete(Long positionId) {
        logger.info("Xóa position ID: {}", positionId);
        
        // Kiểm tra xem có users nào trong position không
        if (hasUsers(positionId)) {
            logger.warn("Không thể xóa position ID {} vì còn có users", positionId);
            throw new RuntimeException("Không thể xóa position vì còn có nhân viên đang giữ vị trí này");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_POSITION)) {
            
            stmt.setLong(1, positionId);
            int affectedRows = stmt.executeUpdate();
            
            boolean deleted = affectedRows > 0;
            if (deleted) {
                logger.info("Xóa position thành công ID: {}", positionId);
            } else {
                logger.warn("Không tìm thấy position để xóa ID: {}", positionId);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi xóa position ID {}: {}", positionId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi xóa position", e);
        }
    }
    
    /**
     * Tìm position theo ID
     */
    public Optional<Position> findById(Long positionId) {
        logger.debug("Tìm position theo ID: {}", positionId);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_POSITION_BY_ID)) {
            
            stmt.setLong(1, positionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Position position = mapResultSetToPosition(rs);
                    logger.debug("Tìm thấy position: {}", position.getName());
                    return Optional.of(position);
                }
                
                logger.debug("Không tìm thấy position với ID: {}", positionId);
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm position ID {}: {}", positionId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm position", e);
        }
    }
    
    /**
     * Tìm position theo code
     */
    public Optional<Position> findByCode(String code) {
        logger.debug("Tìm position theo code: {}", code);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_POSITION_BY_CODE)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Position position = mapResultSetToPosition(rs);
                    logger.debug("Tìm thấy position: {}", position.getName());
                    return Optional.of(position);
                }
                
                logger.debug("Không tìm thấy position với code: {}", code);
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm position theo code {}: {}", code, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm position", e);
        }
    }
    
    /**
     * Tìm position theo name
     */
    public Optional<Position> findByName(String name) {
        logger.debug("Tìm position theo name: {}", name);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_POSITION_BY_NAME)) {
            
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Position position = mapResultSetToPosition(rs);
                    logger.debug("Tìm thấy position: {}", position.getName());
                    return Optional.of(position);
                }
                
                logger.debug("Không tìm thấy position với name: {}", name);
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm position theo name {}: {}", name, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm position", e);
        }
    }
    
    /**
     * Lấy tất cả positions
     */
    public List<Position> findAll() {
        logger.debug("Lấy tất cả positions");
        
        List<Position> positions = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_POSITIONS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                positions.add(mapResultSetToPosition(rs));
            }
            
            logger.debug("Tìm thấy {} positions", positions.size());
            return positions;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy danh sách positions: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách positions", e);
        }
    }
    
    /**
     * Lấy positions theo job level
     */
    public List<Position> findByJobLevel(Integer jobLevel) {
        logger.debug("Tìm positions theo job level: {}", jobLevel);
        
        List<Position> positions = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_POSITIONS_BY_LEVEL)) {
            
            stmt.setInt(1, jobLevel);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    positions.add(mapResultSetToPosition(rs));
                }
            }
            
            logger.debug("Tìm thấy {} positions cho job level: {}", positions.size(), jobLevel);
            return positions;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm positions theo job level {}: {}", jobLevel, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm positions theo job level", e);
        }
    }
    
    /**
     * Đếm số users trong position
     */
    public int countUsers(Long positionId) {
        logger.debug("Đếm users trong position ID: {}", positionId);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_USERS_IN_POSITION)) {
            
            stmt.setLong(1, positionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.debug("Position ID {} có {} users", positionId, count);
                    return count;
                }
                return 0;
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi đếm users trong position ID {}: {}", positionId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi đếm users", e);
        }
    }
    
    /**
     * Kiểm tra position có users không
     */
    public boolean hasUsers(Long positionId) {
        return countUsers(positionId) > 0;
    }
    
    /**
     * Kiểm tra code position đã tồn tại chưa
     */
    public boolean existsByCode(String code) {
        return findByCode(code).isPresent();
    }
    
    /**
     * Kiểm tra code position đã tồn tại (trừ ID hiện tại)
     */
    public boolean existsByCodeAndNotId(String code, Long excludeId) {
        Optional<Position> position = findByCode(code);
        return position.isPresent() && !position.get().getId().equals(excludeId);
    }
    
    /**
     * Kiểm tra name position đã tồn tại chưa
     */
    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }
    
    /**
     * Kiểm tra name position đã tồn tại (trừ ID hiện tại)
     */
    public boolean existsByNameAndNotId(String name, Long excludeId) {
        Optional<Position> position = findByName(name);
        return position.isPresent() && !position.get().getId().equals(excludeId);
    }
    
    // Helper methods
    
    /**
     * Map ResultSet thành Position object
     */
    private Position mapResultSetToPosition(ResultSet rs) throws SQLException {
        Position position = new Position();
        
        position.setId(rs.getLong("id"));
        position.setCode(rs.getString("code"));
        position.setName(rs.getString("name"));
        
        // Job Level có thể null
        Integer jobLevel = rs.getInt("job_level");
        if (!rs.wasNull()) {
            position.setJobLevel(jobLevel);
        }
        
        // Timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            position.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            position.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return position;
    }
    
    /**
     * Set nullable Integer parameter
     */
    private void setNullableInteger(PreparedStatement stmt, int parameterIndex, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(parameterIndex, value);
        } else {
            stmt.setNull(parameterIndex, Types.INTEGER);
        }
    }
    
    /**
     * Đếm số users có position này
     * @param positionId ID của position
     * @return Số lượng users
     */
    public int countUsers(Long positionId) {
        logger.debug("Counting users with position ID: {}", positionId);
        
        String sql = "SELECT COUNT(*) FROM users WHERE position_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, positionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.debug("Position ID {} has {} users", positionId, count);
                    return count;
                }
                return 0;
            }
            
        } catch (SQLException e) {
            logger.error("Error counting users with position ID {}: {}", positionId, e.getMessage(), e);
            throw new RuntimeException("Error counting users", e);
        }
    }
}