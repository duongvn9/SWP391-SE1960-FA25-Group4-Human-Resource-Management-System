package group4.hrms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.util.DatabaseUtil;

/**
 * Base DAO class với common CRUD operations
 * Các DAO khác có thể kế thừa để sử dụng các method cơ bản
 */
public abstract class BaseDao<T, ID> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Lấy tên bảng
     */
    protected abstract String getTableName();

    /**
     * Lấy tên cột ID
     */
    protected String getIdColumnName() {
        return "id";
    }

    /**
     * Map ResultSet to Entity
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;

    /**
     * Tạo SQL INSERT statement
     */
    protected abstract String createInsertSql();

    /**
     * Tạo SQL UPDATE statement
     */
    protected abstract String createUpdateSql();

    /**
     * Set parameters cho INSERT statement
     */
    protected abstract void setInsertParameters(PreparedStatement stmt, T entity) throws SQLException;

    /**
     * Set parameters cho UPDATE statement
     */
    protected abstract void setUpdateParameters(PreparedStatement stmt, T entity) throws SQLException;

    /**
     * Set ID cho entity sau khi insert
     */
    protected abstract void setEntityId(T entity, ID id);

    /**
     * Lấy ID của entity
     */
    protected abstract ID getEntityId(T entity);

    /**
     * Lấy tất cả records
     */
    public List<T> findAll() throws SQLException {
        List<T> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }

            logger.debug("Found {} records from {}", entities.size(), getTableName());
            return entities;

        } catch (SQLException e) {
            logger.error("Error finding all records from {}: {}", getTableName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy record theo ID
     */
    public Optional<T> findById(ID id) throws SQLException {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    T entity = mapResultSetToEntity(rs);
                    logger.debug("Found record with id {} from {}", id, getTableName());
                    return Optional.of(entity);
                }
            }

            logger.debug("Record not found with id {} from {}", id, getTableName());
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding record by id {} from {}: {}", id, getTableName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lưu entity mới
     */
    public T save(T entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        String sql = createInsertSql();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParameters(stmt, entity);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating record failed, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Handle MySQL returning BigInteger for AUTO_INCREMENT
                    Object keyObject = generatedKeys.getObject(1);
                    @SuppressWarnings("unchecked")
                    ID generatedId = (ID) convertToLong(keyObject);
                    setEntityId(entity, generatedId);
                } else {
                    throw new SQLException("Creating record failed, no ID obtained");
                }
            }

            logger.info("Created new record in {}", getTableName());
            return entity;

        } catch (SQLException e) {
            logger.error("Error saving record to {}: {}", getTableName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật entity
     */
    public T update(T entity) throws SQLException {
        if (entity == null || getEntityId(entity) == null) {
            throw new IllegalArgumentException("Entity and entity ID cannot be null");
        }

        String sql = createUpdateSql();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setUpdateParameters(stmt, entity);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating record failed, no rows affected");
            }

            logger.info("Updated record in {}", getTableName());
            return entity;

        } catch (SQLException e) {
            logger.error("Error updating record in {}: {}", getTableName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Xóa record theo ID
     */
    public boolean deleteById(ID id) throws SQLException {
        if (id == null) {
            return false;
        }

        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;

            if (deleted) {
                logger.info("Deleted record with id {} from {}", id, getTableName());
            } else {
                logger.warn("No record found to delete with id {} from {}", id, getTableName());
            }

            return deleted;

        } catch (SQLException e) {
            logger.error("Error deleting record {} from {}: {}", id, getTableName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Đếm tổng số records
     */
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + getTableName();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

            return 0;

        } catch (SQLException e) {
            logger.error("Error counting records from {}: {}", getTableName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Kiểm tra record có tồn tại không
     */
    public boolean existsById(ID id) throws SQLException {
        if (id == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error checking exists by id {} from {}: {}", id, getTableName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Helper method để set Timestamp từ LocalDateTime
     */
    protected void setTimestamp(PreparedStatement stmt, int parameterIndex, LocalDateTime dateTime) throws SQLException {
        if (dateTime != null) {
            stmt.setTimestamp(parameterIndex, Timestamp.valueOf(dateTime));
        } else {
            stmt.setNull(parameterIndex, Types.TIMESTAMP);
        }
    }

    /**
     * Helper method để get LocalDateTime từ Timestamp
     */
    protected LocalDateTime getLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    /**
     * Helper method để set Date từ LocalDate
     */
    protected void setDate(PreparedStatement stmt, int parameterIndex, java.time.LocalDate date) throws SQLException {
        if (date != null) {
            stmt.setDate(parameterIndex, java.sql.Date.valueOf(date));
        } else {
            stmt.setNull(parameterIndex, Types.DATE);
        }
    }

    /**
     * Helper method để get LocalDate từ Date
     */
    protected java.time.LocalDate getLocalDate(ResultSet rs, String columnName) throws SQLException {
        java.sql.Date date = rs.getDate(columnName);
        return date != null ? date.toLocalDate() : null;
    }

    /**
     * Helper method to convert various number types to Long
     * MySQL returns BigInteger for AUTO_INCREMENT columns
     */
    protected Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to Long");
    }
}