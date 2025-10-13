package group4.hrms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.dto.RequestDto;
import group4.hrms.model.Request;
import group4.hrms.util.DatabaseUtil;

/**
 * DAO cho Request - Yêu cầu/Đơn từ
 */
public class RequestDao extends BaseDao<Request, Long> {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestDao.class);
    
    private static final String TABLE_NAME = "requests";
    
    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }
    
    @Override
    protected void setEntityId(Request request, Long id) {
        request.setId(id);
    }
    
    @Override
    protected Long getEntityId(Request request) {
        return request.getId();
    }
    
    @Override
    protected String createInsertSql() {
        return "INSERT INTO requests (user_id, request_type_id, title, description, " +
               "status, priority, start_date, end_date, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String createUpdateSql() {
        return "UPDATE requests SET user_id = ?, request_type_id = ?, title = ?, " +
               "description = ?, status = ?, priority = ?, start_date = ?, end_date = ?, " +
               "updated_at = ? WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Request request) throws SQLException {
        stmt.setLong(1, request.getUserId());
        stmt.setLong(2, request.getRequestTypeId());
        stmt.setString(3, request.getTitle());
        stmt.setString(4, request.getDescription());
        stmt.setString(5, request.getStatus());
        stmt.setString(6, request.getPriority());
        setTimestamp(stmt, 7, request.getStartDate());
        setTimestamp(stmt, 8, request.getEndDate());
        
        LocalDateTime now = LocalDateTime.now();
        setTimestamp(stmt, 9, now);
        setTimestamp(stmt, 10, now);
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Request request) throws SQLException {
        stmt.setLong(1, request.getUserId());
        stmt.setLong(2, request.getRequestTypeId());
        stmt.setString(3, request.getTitle());
        stmt.setString(4, request.getDescription());
        stmt.setString(5, request.getStatus());
        stmt.setString(6, request.getPriority());
        setTimestamp(stmt, 7, request.getStartDate());
        setTimestamp(stmt, 8, request.getEndDate());
        setTimestamp(stmt, 9, LocalDateTime.now());
        stmt.setLong(10, request.getId());
    }
    
    private static final String SELECT_ALL = 
        "SELECT id, user_id, request_type_id, title, description, status, priority, " +
        "start_date, end_date, day_count, attachment_path, reject_reason, " +
        "approved_by, approved_at, created_at, updated_at FROM " + TABLE_NAME;
    
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";
    
    private static final String SELECT_WITH_DETAILS = 
        "SELECT r.id, r.user_id, r.request_type_id, r.title, r.description, r.status, r.priority, " +
        "r.start_date, r.end_date, r.day_count, r.attachment_path, r.reject_reason, " +
        "r.approved_by, r.approved_at, r.created_at, r.updated_at, " +
        "u.username, u.full_name, " +
        "rt.name as request_type_name, rt.code as request_type_code, " +
        "approver.full_name as approver_name " +
        "FROM " + TABLE_NAME + " r " +
        "LEFT JOIN users u ON r.user_id = u.id " +
        "LEFT JOIN request_types rt ON r.request_type_id = rt.id " +
        "LEFT JOIN users approver ON r.approved_by = approver.id ";
    
    private static final String INSERT = 
        "INSERT INTO " + TABLE_NAME + " (user_id, request_type_id, title, description, status, priority, " +
        "start_date, end_date, day_count, attachment_path, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE = 
        "UPDATE " + TABLE_NAME + " SET user_id = ?, request_type_id = ?, title = ?, description = ?, " +
        "status = ?, priority = ?, start_date = ?, end_date = ?, day_count = ?, attachment_path = ?, " +
        "reject_reason = ?, approved_by = ?, approved_at = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
    
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM " + TABLE_NAME;
    
    @Override
    public Optional<Request> findById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding request by id: {}", id, e);
            throw new RuntimeException("Error finding request by id: " + id, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Request> findAll() {
        return findAll(0, Integer.MAX_VALUE);
    }
    
    public List<Request> findAll(int offset, int limit) {
        List<Request> requests = new ArrayList<>();
        String sql = SELECT_ALL + " ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding all requests", e);
            throw new RuntimeException("Error finding all requests", e);
        }
        
        return requests;
    }
    
    @Override
    public Request save(Request request) {
        if (request.getId() != null) {
            return update(request);
        } else {
            return insert(request);
        }
    }
    
    private Request insert(Request request) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            request.setCreatedAt(now);
            request.setUpdatedAt(now);
            
            stmt.setLong(1, request.getUserId());
            stmt.setLong(2, request.getRequestTypeId());
            stmt.setString(3, request.getTitle());
            stmt.setString(4, request.getDescription());
            stmt.setString(5, request.getStatus());
            stmt.setString(6, request.getPriority());
            stmt.setTimestamp(7, request.getStartDate() != null ? Timestamp.valueOf(request.getStartDate()) : null);
            stmt.setTimestamp(8, request.getEndDate() != null ? Timestamp.valueOf(request.getEndDate()) : null);
            stmt.setObject(9, request.getDayCount());
            stmt.setString(10, request.getAttachmentPath());
            stmt.setTimestamp(11, Timestamp.valueOf(request.getCreatedAt()));
            stmt.setTimestamp(12, Timestamp.valueOf(request.getUpdatedAt()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        request.setId(generatedKeys.getLong(1));
                        logger.info("Request created successfully with id: {}", request.getId());
                        return request;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error creating request", e);
            throw new RuntimeException("Error creating request", e);
        }
        
        throw new RuntimeException("Failed to create request");
    }
    
    public Request update(Request request) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            
            request.setUpdatedAt(LocalDateTime.now());
            
            stmt.setLong(1, request.getUserId());
            stmt.setLong(2, request.getRequestTypeId());
            stmt.setString(3, request.getTitle());
            stmt.setString(4, request.getDescription());
            stmt.setString(5, request.getStatus());
            stmt.setString(6, request.getPriority());
            stmt.setTimestamp(7, request.getStartDate() != null ? Timestamp.valueOf(request.getStartDate()) : null);
            stmt.setTimestamp(8, request.getEndDate() != null ? Timestamp.valueOf(request.getEndDate()) : null);
            stmt.setObject(9, request.getDayCount());
            stmt.setString(10, request.getAttachmentPath());
            stmt.setString(11, request.getRejectReason());
            stmt.setObject(12, request.getApprovedBy());
            stmt.setTimestamp(13, request.getApprovedAt() != null ? Timestamp.valueOf(request.getApprovedAt()) : null);
            stmt.setTimestamp(14, Timestamp.valueOf(request.getUpdatedAt()));
            stmt.setLong(15, request.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Request updated successfully: {}", request.getId());
                return request;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating request: {}", request.getId(), e);
            throw new RuntimeException("Error updating request: " + request.getId(), e);
        }
        
        throw new RuntimeException("Failed to update request: " + request.getId());
    }
    
    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {
            
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Request deleted successfully: {}", id);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error deleting request: {}", id, e);
            throw new RuntimeException("Error deleting request: " + id, e);
        }
        
        return false;
    }
    
    @Override
    public long count() {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
        } catch (SQLException e) {
            logger.error("Error counting requests", e);
            throw new RuntimeException("Error counting requests", e);
        }
        
        return 0;
    }
    
    /**
     * Tìm requests theo user ID
     */
    public List<Request> findByUserId(Long userId) {
        List<Request> requests = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding requests by user id: {}", userId, e);
            throw new RuntimeException("Error finding requests by user id: " + userId, e);
        }
        
        return requests;
    }
    /**
 * Lấy danh sách yêu cầu tuyển dụng nháp (DRAFT) của 1 user
 */
public List<Request> findDraftsByUserId(Long userId) {
    List<Request> drafts = new ArrayList<>();
    String sql = "SELECT * FROM " + getTableName() +
                 " WHERE user_id = ? AND status = 'DRAFT' ORDER BY updated_at DESC";
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setLong(1, userId);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                drafts.add(mapResultSetToEntity(rs));
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error finding draft requests for userId " + userId, e);
    }
    return drafts;
}

public List<RequestDto> findDraftsByUserIdWithDetails(Long userId) {
    List<RequestDto> drafts = new ArrayList<>();
    String sql = SELECT_WITH_DETAILS + " WHERE r.user_id = ? AND r.status = 'DRAFT' ORDER BY r.created_at DESC";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setLong(1, userId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                drafts.add(mapResultSetToDto(rs));
            }
        }
        
    } catch (SQLException e) {
        logger.error("Error finding draft requests with details for user id: {}", userId, e);
        throw new RuntimeException("Error finding draft requests with details for user id: " + userId, e);
    }
    
    return drafts;
}
  public List<Request> findPendingRecruitmentRequests() {
        List<Request> list = new ArrayList<>();
        String sql = """
            SELECT * FROM requests 
            WHERE request_type_id = 2 
              AND status IN ('PENDING', 'HR_APPROVED', 'HR_REJECTED')
            ORDER BY created_at DESC
        """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Request req = new Request();
                req.setId(rs.getLong("id"));
                req.setUserId(rs.getLong("user_id"));
                req.setTitle(rs.getString("title"));
                req.setDescription(rs.getString("description"));
                req.setStatus(rs.getString("status"));
                req.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                req.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                list.add(req);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
 * Tìm requests theo loại và trạng thái (ví dụ: Recruitment + PENDING)
 */
public List<Request> findByTypeAndStatus(Long requestTypeId, String status) {
    List<Request> requests = new ArrayList<>();
    String sql = SELECT_ALL + " WHERE request_type_id = ? AND status = ? ORDER BY created_at DESC";

    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setLong(1, requestTypeId);
        stmt.setString(2, status);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                requests.add(mapResultSetToEntity(rs));
            }
        }

    } catch (SQLException e) {
        logger.error("Error finding requests by type and status: typeId={}, status={}", requestTypeId, status, e);
        throw new RuntimeException("Error finding requests by type and status", e);
    }

    return requests;
}
/**
 * Lấy danh sách yêu cầu tuyển dụng đang chờ HR duyệt
 */
public List<Request> findPendingForHR() {
    return findByTypeAndStatus(2L, "PENDING");
}

/**
 * Lấy danh sách yêu cầu tuyển dụng đã duyệt qua HR, chờ HRM xác nhận
 */
public List<Request> findPendingForHRM() {
    return findByTypeAndStatus(2L, "HR_APPROVED");
}

    /**
     * Tìm requests theo trạng thái
     */
    public List<Request> findByStatus(String status) {
        List<Request> requests = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding requests by status: {}", status, e);
            throw new RuntimeException("Error finding requests by status: " + status, e);
        }
        
        return requests;
    }
    
    /**
     * Tìm requests với thông tin chi tiết (join với user, request_type)
     */
    public List<RequestDto> findAllWithDetails(int offset, int limit) {
        List<RequestDto> requests = new ArrayList<>();
        String sql = SELECT_WITH_DETAILS + " ORDER BY r.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToDto(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding requests with details", e);
            throw new RuntimeException("Error finding requests with details", e);
        }
        
        return requests;
    }
    
    /**
     * Tìm requests của user với thông tin chi tiết
     */
    public List<RequestDto> findByUserIdWithDetails(Long userId, int offset, int limit) {
        List<RequestDto> requests = new ArrayList<>();
        String sql = SELECT_WITH_DETAILS + " WHERE r.user_id = ? ORDER BY r.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToDto(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding requests by user id with details: {}", userId, e);
            throw new RuntimeException("Error finding requests by user id with details: " + userId, e);
        }
        
        return requests;
    }
    
    @Override
    protected Request mapResultSetToEntity(ResultSet rs) throws SQLException {
        Request request = new Request();
        
        request.setId(rs.getLong("id"));
        request.setUserId(rs.getLong("user_id"));
        request.setRequestTypeId(rs.getLong("request_type_id"));
        request.setTitle(rs.getString("title"));
        request.setDescription(rs.getString("description"));
        request.setStatus(rs.getString("status"));
        request.setPriority(rs.getString("priority"));
        
        Timestamp startDate = rs.getTimestamp("start_date");
        if (startDate != null) {
            request.setStartDate(startDate.toLocalDateTime());
        }
        
        Timestamp endDate = rs.getTimestamp("end_date");
        if (endDate != null) {
            request.setEndDate(endDate.toLocalDateTime());
        }
        
        request.setDayCount((Integer) rs.getObject("day_count"));
        request.setAttachmentPath(rs.getString("attachment_path"));
        request.setRejectReason(rs.getString("reject_reason"));
        request.setApprovedBy((Long) rs.getObject("approved_by"));
        
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) {
            request.setApprovedAt(approvedAt.toLocalDateTime());
        }
        
        request.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        request.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        return request;
    }
    
    /**
     * Map ResultSet to RequestDto (với thông tin join)
     */
    private RequestDto mapResultSetToDto(ResultSet rs) throws SQLException {
        RequestDto dto = new RequestDto(mapResultSetToEntity(rs));
        
        // Thông tin user
        dto.setUserName(rs.getString("username"));
        dto.setUserFullName(rs.getString("full_name"));
        
        // Thông tin request type
        dto.setRequestTypeName(rs.getString("request_type_name"));
        dto.setRequestTypeCode(rs.getString("request_type_code"));
        
        // Thông tin approver
        dto.setApproverName(rs.getString("approver_name"));
        
        return dto;
    }
}