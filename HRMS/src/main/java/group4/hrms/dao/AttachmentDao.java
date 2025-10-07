package group4.hrms.dao;

import group4.hrms.model.Attachment;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO class để xử lý các thao tác với bảng attachments
 * Mapping theo database schema mới
 * 
 * @author Group4
 */
public class AttachmentDao extends BaseDao<Attachment, Long> {
    
    @Override
    protected String getTableName() {
        return "attachments";
    }
    
    @Override
    protected Attachment mapResultSetToEntity(ResultSet rs) throws SQLException {
        Attachment attachment = new Attachment();
        attachment.setId(rs.getLong("id"));
        attachment.setOwnerType(rs.getString("owner_type"));
        attachment.setOwnerId(rs.getLong("owner_id"));
        attachment.setPath(rs.getString("path"));
        attachment.setOriginalName(rs.getString("original_name"));
        attachment.setContentType(rs.getString("content_type"));
        attachment.setSizeBytes(rs.getLong("size_bytes"));
        attachment.setChecksumSha256(rs.getString("checksum_sha256"));
        attachment.setUploadedByAccountId(rs.getLong("uploaded_by_account_id"));
        attachment.setCreatedAt(getLocalDateTime(rs, "created_at"));
        
        return attachment;
    }
    
    @Override
    protected void setEntityId(Attachment attachment, Long id) {
        attachment.setId(id);
    }
    
    @Override
    protected Long getEntityId(Attachment attachment) {
        return attachment.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO attachments (owner_type, owner_id, path, original_name, " +
               "content_type, size_bytes, checksum_sha256, uploaded_by_account_id, created_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE attachments SET owner_type = ?, owner_id = ?, path = ?, " +
               "original_name = ?, content_type = ?, size_bytes = ?, checksum_sha256 = ?, " +
               "uploaded_by_account_id = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Attachment attachment) throws SQLException {
        stmt.setString(1, attachment.getOwnerType());
        stmt.setLong(2, attachment.getOwnerId());
        stmt.setString(3, attachment.getPath());
        stmt.setString(4, attachment.getOriginalName());
        stmt.setString(5, attachment.getContentType());
        stmt.setObject(6, attachment.getSizeBytes(), Types.BIGINT);
        stmt.setString(7, attachment.getChecksumSha256());
        stmt.setObject(8, attachment.getUploadedByAccountId(), Types.BIGINT);
        setTimestamp(stmt, 9, attachment.getCreatedAt() != null ? attachment.getCreatedAt() : LocalDateTime.now());
    }
    
    @Override  
    protected void setUpdateParameters(PreparedStatement stmt, Attachment attachment) throws SQLException {
        stmt.setString(1, attachment.getOwnerType());
        stmt.setLong(2, attachment.getOwnerId());
        stmt.setString(3, attachment.getPath());
        stmt.setString(4, attachment.getOriginalName());
        stmt.setString(5, attachment.getContentType());
        stmt.setObject(6, attachment.getSizeBytes(), Types.BIGINT);
        stmt.setString(7, attachment.getChecksumSha256());
        stmt.setObject(8, attachment.getUploadedByAccountId(), Types.BIGINT);
        stmt.setLong(9, attachment.getId());
    }
    
    // Business methods
    
    /**
     * Tìm attachments theo owner type và owner id
     */
    public List<Attachment> findByOwner(String ownerType, Long ownerId) throws SQLException {
        if (ownerType == null || ownerType.trim().isEmpty() || ownerId == null) {
            return new ArrayList<>();
        }
        
        List<Attachment> attachments = new ArrayList<>();
        String sql = "SELECT * FROM attachments WHERE owner_type = ? AND owner_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ownerType);
            stmt.setLong(2, ownerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attachments.add(mapResultSetToEntity(rs));
                }
            }
            
            return attachments;
            
        } catch (SQLException e) {
            logger.error("Error finding attachments by owner {}:{}: {}", ownerType, ownerId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attachment theo original name
     */
    public List<Attachment> findByOriginalName(String originalName) throws SQLException {
        if (originalName == null || originalName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Attachment> attachments = new ArrayList<>();
        String sql = "SELECT * FROM attachments WHERE original_name = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, originalName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attachments.add(mapResultSetToEntity(rs));
                }
            }
            
            return attachments;
            
        } catch (SQLException e) {
            logger.error("Error finding attachments by original name {}: {}", originalName, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attachment theo path
     */
    public Optional<Attachment> findByPath(String path) throws SQLException {
        if (path == null || path.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM attachments WHERE path = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, path);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding attachment by path {}: {}", path, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attachments theo content type
     */
    public List<Attachment> findByContentType(String contentType) throws SQLException {
        if (contentType == null || contentType.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Attachment> attachments = new ArrayList<>();
        String sql = "SELECT * FROM attachments WHERE content_type = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, contentType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attachments.add(mapResultSetToEntity(rs));
                }
            }
            
            return attachments;
            
        } catch (SQLException e) {
            logger.error("Error finding attachments by content type {}: {}", contentType, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attachments theo người upload
     */
    public List<Attachment> findByUploader(Long uploadedByAccountId) throws SQLException {
        if (uploadedByAccountId == null) {
            return new ArrayList<>();
        }
        
        List<Attachment> attachments = new ArrayList<>();
        String sql = "SELECT * FROM attachments WHERE uploaded_by_account_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, uploadedByAccountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attachments.add(mapResultSetToEntity(rs));
                }
            }
            
            return attachments;
            
        } catch (SQLException e) {
            logger.error("Error finding attachments by uploader {}: {}", uploadedByAccountId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tìm attachments theo khoảng thời gian
     */
    public List<Attachment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        
        List<Attachment> attachments = new ArrayList<>();
        String sql = "SELECT * FROM attachments WHERE created_at >= ? AND created_at <= ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setTimestamp(stmt, 1, startDate);
            setTimestamp(stmt, 2, endDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attachments.add(mapResultSetToEntity(rs));
                }
            }
            
            return attachments;
            
        } catch (SQLException e) {
            logger.error("Error finding attachments by date range {}-{}: {}", startDate, endDate, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Đếm số attachments theo owner
     */
    public long countByOwner(String ownerType, Long ownerId) throws SQLException {
        if (ownerType == null || ownerType.trim().isEmpty() || ownerId == null) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM attachments WHERE owner_type = ? AND owner_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ownerType);
            stmt.setLong(2, ownerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting attachments by owner {}:{}: {}", ownerType, ownerId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Tính tổng dung lượng theo owner
     */
    public long getTotalSizeByOwner(String ownerType, Long ownerId) throws SQLException {
        if (ownerType == null || ownerType.trim().isEmpty() || ownerId == null) {
            return 0;
        }
        
        String sql = "SELECT COALESCE(SUM(size_bytes), 0) FROM attachments WHERE owner_type = ? AND owner_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ownerType);
            stmt.setLong(2, ownerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error calculating total size by owner {}:{}: {}", ownerType, ownerId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Xóa attachments theo owner
     */
    public int deleteByOwner(String ownerType, Long ownerId) throws SQLException {
        if (ownerType == null || ownerType.trim().isEmpty() || ownerId == null) {
            return 0;
        }
        
        String sql = "DELETE FROM attachments WHERE owner_type = ? AND owner_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ownerType);
            stmt.setLong(2, ownerId);
            
            int deleted = stmt.executeUpdate();
            logger.info("Deleted {} attachments for owner {}:{}", deleted, ownerType, ownerId);
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Error deleting attachments by owner {}:{}: {}", ownerType, ownerId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Kiểm tra attachment có tồn tại theo checksum không
     */
    public Optional<Attachment> findByChecksum(String checksumSha256) throws SQLException {
        if (checksumSha256 == null || checksumSha256.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = "SELECT * FROM attachments WHERE checksum_sha256 = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, checksumSha256);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding attachment by checksum {}: {}", checksumSha256, e.getMessage(), e);
            throw e;
        }
    }
}