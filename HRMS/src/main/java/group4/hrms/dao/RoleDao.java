package group4.hrms.dao;

import group4.hrms.model.Role;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO cho Role entity
 * Xử lý các thao tác CRUD với bảng roles
 */
public class RoleDao {
    private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);

    // SQL queries
    private static final String SELECT_ALL = 
        "SELECT id, code, name, priority, is_system, created_at, updated_at FROM roles ORDER BY priority DESC";
    
    private static final String SELECT_BY_ID = 
        "SELECT id, code, name, priority, is_system, created_at, updated_at FROM roles WHERE id = ?";
    
    private static final String SELECT_BY_CODE = 
        "SELECT id, code, name, priority, is_system, created_at, updated_at FROM roles WHERE code = ?";
    
    private static final String INSERT_ROLE = 
        "INSERT INTO roles (code, name, priority, is_system) VALUES (?, ?, ?, ?)";
    
    private static final String UPDATE_ROLE = 
        "UPDATE roles SET name = ?, priority = ?, updated_at = GETUTCDATE() WHERE id = ?";
    
    private static final String DELETE_ROLE = 
        "DELETE FROM roles WHERE id = ? AND is_system = 0";
    
    private static final String SELECT_SYSTEM_ROLES = 
        "SELECT id, code, name, priority, is_system, created_at, updated_at FROM roles WHERE is_system = 1 ORDER BY priority DESC";

    /**
     * Lấy tất cả roles
     */
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
            
            logger.info("Tìm thấy {} roles", roles.size());
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy danh sách roles", e);
        }
        
        return roles;
    }

    /**
     * Tìm role theo ID
     */
    public Optional<Role> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = mapResultSetToRole(rs);
                    logger.debug("Tìm thấy role với ID {}: {}", id, role.getCode());
                    return Optional.of(role);
                }
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm role với ID: " + id, e);
        }
        
        return Optional.empty();
    }

    /**
     * Tìm role theo code
     */
    public Optional<Role> findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_CODE)) {
            
            ps.setString(1, code.trim().toUpperCase());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = mapResultSetToRole(rs);
                    logger.debug("Tìm thấy role với code {}: {}", code, role.getName());
                    return Optional.of(role);
                }
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm role với code: " + code, e);
        }
        
        return Optional.empty();
    }

    /**
     * Lấy tất cả system roles
     */
    public List<Role> findSystemRoles() {
        List<Role> roles = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_SYSTEM_ROLES);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
            
            logger.info("Tìm thấy {} system roles", roles.size());
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy system roles", e);
        }
        
        return roles;
    }

    /**
     * Tạo role mới
     */
    public Optional<Role> create(Role role) {
        if (role == null || role.getCode() == null || role.getName() == null) {
            logger.warn("Dữ liệu role không hợp lệ để tạo mới");
            return Optional.empty();
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_ROLE, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, role.getCode().trim().toUpperCase());
            ps.setString(2, role.getName().trim());
            ps.setInt(3, role.getPriority() != null ? role.getPriority().intValue() : 0);
            ps.setBoolean(4, role.getIsSystem() != null ? role.getIsSystem().booleanValue() : false);
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        logger.info("Tạo thành công role với ID: {}, code: {}", id, role.getCode());
                        return findById(id);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi tạo role: " + role.getCode(), e);
        }
        
        return Optional.empty();
    }

    /**
     * Cập nhật role
     */
    public boolean update(Role role) {
        if (role == null || role.getId() == null) {
            logger.warn("Dữ liệu role không hợp lệ để cập nhật");
            return false;
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ROLE)) {
            
            ps.setString(1, role.getName().trim());
            ps.setInt(2, role.getPriority() != null ? role.getPriority().intValue() : 0);
            ps.setLong(3, role.getId());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Cập nhật thành công role ID: {}", role.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi cập nhật role ID: " + role.getId(), e);
        }
        
        return false;
    }

    /**
     * Xóa role (chỉ non-system roles)
     */
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ROLE)) {
            
            ps.setLong(1, id);
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Xóa thành công role ID: {}", id);
                return true;
            } else {
                logger.warn("Không thể xóa role ID: {} (có thể là system role hoặc không tồn tại)", id);
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi xóa role ID: " + id, e);
        }
        
        return false;
    }

    /**
     * Kiểm tra role code đã tồn tại
     */
    public boolean existsByCode(String code) {
        return findByCode(code).isPresent();
    }

    /**
     * Map ResultSet to Role object
     */
    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setCode(rs.getString("code"));
        role.setName(rs.getString("name"));
        role.setPriority(rs.getInt("priority"));
        role.setIsSystem(rs.getBoolean("is_system"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            role.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            role.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return role;
    }
}