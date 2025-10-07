package group4.hrms.dao;

import group4.hrms.model.Department;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class cho Department entity
 * Xử lý các thao tác CRUD với bảng departments
 */
public class DepartmentDao {
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentDao.class);
    
    // SQL queries
    private static final String INSERT_DEPARTMENT = 
        "INSERT INTO departments (name, head_account_id, created_at, updated_at) VALUES (?, ?, ?, ?)";
    
    private static final String UPDATE_DEPARTMENT = 
        "UPDATE departments SET name = ?, head_account_id = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE_DEPARTMENT = "DELETE FROM departments WHERE id = ?";
    
    private static final String SELECT_DEPARTMENT_BY_ID = 
        "SELECT d.id, d.name, d.head_account_id, d.created_at, d.updated_at, " +
        "u.first_name, u.last_name " +
        "FROM departments d " +
        "LEFT JOIN accounts a ON d.head_account_id = a.id " +
        "LEFT JOIN users u ON a.user_id = u.id " +
        "WHERE d.id = ?";
    
    private static final String SELECT_ALL_DEPARTMENTS = 
        "SELECT d.id, d.name, d.head_account_id, d.created_at, d.updated_at, " +
        "u.first_name, u.last_name " +
        "FROM departments d " +
        "LEFT JOIN accounts a ON d.head_account_id = a.id " +
        "LEFT JOIN users u ON a.user_id = u.id " +
        "ORDER BY d.name";
    
    private static final String SELECT_DEPARTMENTS_BY_HEAD = 
        "SELECT d.id, d.name, d.head_account_id, d.created_at, d.updated_at, " +
        "u.first_name, u.last_name " +
        "FROM departments d " +
        "LEFT JOIN accounts a ON d.head_account_id = a.id " +
        "LEFT JOIN users u ON a.user_id = u.id " +
        "WHERE d.head_account_id = ? " +
        "ORDER BY d.name";
    
    private static final String COUNT_EMPLOYEES_IN_DEPARTMENT = 
        "SELECT COUNT(*) FROM users WHERE department_id = ?";
    
    private static final String SELECT_DEPARTMENT_BY_NAME = 
        "SELECT d.id, d.name, d.head_account_id, d.created_at, d.updated_at, " +
        "u.first_name, u.last_name " +
        "FROM departments d " +
        "LEFT JOIN accounts a ON d.head_account_id = a.id " +
        "LEFT JOIN users u ON a.user_id = u.id " +
        "WHERE d.name = ?";
    
    /**
     * Tạo mới department
     */
    public Department create(Department department) {
        logger.info("Tạo mới department: {}", department.getName());
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_DEPARTMENT, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, department.getName());
            setNullableLong(stmt, 2, department.getHeadAccountId());
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Tạo department thất bại, không có row nào được insert");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    department.setId(generatedKeys.getLong(1));
                    department.setCreatedAt(now);
                    department.setUpdatedAt(now);
                    
                    logger.info("Tạo department thành công với ID: {}", department.getId());
                    return department;
                } else {
                    throw new SQLException("Tạo department thất bại, không lấy được generated ID");
                }
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tạo department: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo department", e);
        }
    }
    
    /**
     * Cập nhật department
     */
    public Department update(Department department) {
        logger.info("Cập nhật department ID: {}", department.getId());
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_DEPARTMENT)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, department.getName());
            setNullableLong(stmt, 2, department.getHeadAccountId());
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.setLong(4, department.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                logger.warn("Cập nhật thất bại, không tìm thấy department với ID: {}", department.getId());
                return null;
            }
            
            department.setUpdatedAt(now);
            logger.info("Cập nhật department thành công: {}", department.getId());
            return department;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi cập nhật department ID {}: {}", department.getId(), e.getMessage(), e);
            throw new RuntimeException("Lỗi khi cập nhật department", e);
        }
    }
    
    /**
     * Xóa department theo ID
     */
    public boolean delete(Long departmentId) {
        logger.info("Xóa department ID: {}", departmentId);
        
        // Kiểm tra xem có employees nào trong department không
        if (hasEmployees(departmentId)) {
            logger.warn("Không thể xóa department ID {} vì còn có employees", departmentId);
            throw new RuntimeException("Không thể xóa department vì còn có nhân viên trong phòng ban");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_DEPARTMENT)) {
            
            stmt.setLong(1, departmentId);
            int affectedRows = stmt.executeUpdate();
            
            boolean deleted = affectedRows > 0;
            if (deleted) {
                logger.info("Xóa department thành công ID: {}", departmentId);
            } else {
                logger.warn("Không tìm thấy department để xóa ID: {}", departmentId);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi xóa department ID {}: {}", departmentId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi xóa department", e);
        }
    }
    
    /**
     * Tìm department theo ID
     */
    public Optional<Department> findById(Long departmentId) {
        logger.debug("Tìm department theo ID: {}", departmentId);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_DEPARTMENT_BY_ID)) {
            
            stmt.setLong(1, departmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Department dept = mapResultSetToDepartment(rs);
                    logger.debug("Tìm thấy department: {}", dept.getName());
                    return Optional.of(dept);
                }
                
                logger.debug("Không tìm thấy department với ID: {}", departmentId);
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm department ID {}: {}", departmentId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm department", e);
        }
    }
    
    /**
     * Tìm department theo tên
     */
    public Optional<Department> findByName(String name) {
        logger.debug("Tìm department theo tên: {}", name);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_DEPARTMENT_BY_NAME)) {
            
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Department dept = mapResultSetToDepartment(rs);
                    logger.debug("Tìm thấy department: {}", dept.getName());
                    return Optional.of(dept);
                }
                
                logger.debug("Không tìm thấy department với tên: {}", name);
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm department theo tên {}: {}", name, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm department", e);
        }
    }
    
    /**
     * Lấy tất cả departments
     */
    public List<Department> findAll() {
        logger.debug("Lấy tất cả departments");
        
        List<Department> departments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_DEPARTMENTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
            
            logger.debug("Tìm thấy {} departments", departments.size());
            return departments;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy danh sách departments: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách departments", e);
        }
    }
    
    /**
     * Lấy departments theo head account ID
     */
    public List<Department> findByHeadAccountId(Long headAccountId) {
        logger.debug("Tìm departments theo head account ID: {}", headAccountId);
        
        List<Department> departments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_DEPARTMENTS_BY_HEAD)) {
            
            stmt.setLong(1, headAccountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    departments.add(mapResultSetToDepartment(rs));
                }
            }
            
            logger.debug("Tìm thấy {} departments cho head account ID: {}", departments.size(), headAccountId);
            return departments;
            
        } catch (SQLException e) {
            logger.error("Lỗi khi tìm departments theo head account ID {}: {}", headAccountId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm departments theo head account", e);
        }
    }
    
    /**
     * Đếm số employees trong department
     */
    public int countEmployees(Long departmentId) {
        logger.debug("Đếm employees trong department ID: {}", departmentId);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_EMPLOYEES_IN_DEPARTMENT)) {
            
            stmt.setLong(1, departmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.debug("Department ID {} có {} employees", departmentId, count);
                    return count;
                }
                return 0;
            }
            
        } catch (SQLException e) {
            logger.error("Lỗi khi đếm employees trong department ID {}: {}", departmentId, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi đếm employees", e);
        }
    }
    
    /**
     * Kiểm tra department có employees không
     */
    public boolean hasEmployees(Long departmentId) {
        return countEmployees(departmentId) > 0;
    }
    
    /**
     * Kiểm tra tên department đã tồn tại chưa
     */
    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }
    
    /**
     * Kiểm tra tên department đã tồn tại (trừ ID hiện tại)
     */
    public boolean existsByNameAndNotId(String name, Long excludeId) {
        Optional<Department> dept = findByName(name);
        return dept.isPresent() && !dept.get().getId().equals(excludeId);
    }
    
    // Helper methods
    
    /**
     * Map ResultSet thành Department object
     */
    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department dept = new Department();
        
        dept.setId(rs.getLong("id"));
        dept.setName(rs.getString("name"));
        
        // Head Account ID có thể null
        Long headAccountId = rs.getLong("head_account_id");
        if (!rs.wasNull()) {
            dept.setHeadAccountId(headAccountId);
        }
        
        // Timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            dept.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            dept.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return dept;
    }
    
    /**
     * Set nullable Long parameter
     */
    private void setNullableLong(PreparedStatement stmt, int parameterIndex, Long value) throws SQLException {
        if (value != null) {
            stmt.setLong(parameterIndex, value);
        } else {
            stmt.setNull(parameterIndex, Types.BIGINT);
        }
    }
}