package group4.hrms.dao;

import group4.hrms.model.Request;
import group4.hrms.dto.RequestDto;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return "INSERT INTO requests ("
                + "request_type_id, title, detail, "
                + "created_by_account_id, created_by_user_id, department_id, "
                + "status, current_approver_account_id"
                + ") VALUES (?, ?, CAST(? AS JSON), ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE requests SET "
                + "request_type_id = ?, "
                + "title = ?, "
                + "detail = CAST(? AS JSON), "
                + "created_by_account_id = ?, "
                + "created_by_user_id = ?, "
                + "department_id = ?, "
                + "status = ?, "
                + "current_approver_account_id = ?, "
                + "updated_at = UTC_TIMESTAMP() "
                + "WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Request request) throws SQLException {
        stmt.setLong(1, request.getRequestTypeId());
        stmt.setString(2, request.getTitle());
        stmt.setString(3, request.getDetail());
        stmt.setLong(4, request.getCreatedByAccountId());
        stmt.setLong(5, request.getCreatedByUserId());
        if (request.getDepartmentId() != null) {
            stmt.setLong(6, request.getDepartmentId());
        } else {
            stmt.setNull(6, java.sql.Types.BIGINT);
        }
        stmt.setString(7, request.getStatus());
        if (request.getCurrentApproverAccountId() != null) {
            stmt.setLong(8, request.getCurrentApproverAccountId());
        } else {
            stmt.setNull(8, java.sql.Types.BIGINT);
        }
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Request request) throws SQLException {
        stmt.setLong(1, request.getRequestTypeId());
        stmt.setString(2, request.getTitle());
        stmt.setString(3, request.getDetail());
        stmt.setLong(4, request.getCreatedByAccountId());
        stmt.setLong(5, request.getCreatedByUserId());
        if (request.getDepartmentId() != null) {
            stmt.setLong(6, request.getDepartmentId());
        } else {
            stmt.setNull(6, java.sql.Types.BIGINT);
        }
        stmt.setString(7, request.getStatus());
        if (request.getCurrentApproverAccountId() != null) {
            stmt.setLong(8, request.getCurrentApproverAccountId());
        } else {
            stmt.setNull(8, java.sql.Types.BIGINT);
        }
        stmt.setLong(9, request.getId());
    }

    private static final String SELECT_ALL
            = "SELECT id, user_id, request_type_id, title, description, status, priority, "
            + "start_date, end_date, day_count, attachment_path, reject_reason, "
            + "approved_by, approved_at, created_at, updated_at FROM " + TABLE_NAME;

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";

    private static final String SELECT_WITH_DETAILS
            = "SELECT r.id, r.user_id, r.request_type_id, r.title, r.description, r.status, r.priority, "
            + "r.start_date, r.end_date, r.day_count, r.attachment_path, r.reject_reason, "
            + "r.approved_by, r.approved_at, r.created_at, r.updated_at, "
            + "u.username, u.full_name, "
            + "rt.name as request_type_name, rt.code as request_type_code, "
            + "approver.full_name as approver_name "
            + "FROM " + TABLE_NAME + " r "
            + "LEFT JOIN users u ON r.user_id = u.id "
            + "LEFT JOIN request_types rt ON r.request_type_id = rt.id "
            + "LEFT JOIN users approver ON r.approved_by = approver.id ";

    private static final String INSERT
            = "INSERT INTO requests "
            + "(request_type_id, title, detail, created_by_account_id, created_by_user_id, department_id, status) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE
            = "UPDATE " + TABLE_NAME + " SET user_id = ?, request_type_id = ?, title = ?, description = ?, "
            + "status = ?, priority = ?, start_date = ?, end_date = ?, day_count = ?, attachment_path = ?, "
            + "reject_reason = ?, approved_by = ?, approved_at = ?, updated_at = ? WHERE id = ?";

    private static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

    private static final String COUNT_ALL = "SELECT COUNT(*) FROM " + TABLE_NAME;

    @Override
    public Optional<Request> findById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, request.getRequestTypeId());
            stmt.setString(2, request.getTitle());
            stmt.setString(3, request.getDetail());
            stmt.setLong(4, request.getCreatedByAccountId());
            stmt.setLong(5, request.getCreatedByUserId());

            if (request.getDepartmentId() != null) {
                stmt.setLong(6, request.getDepartmentId());
            } else {
                stmt.setNull(6, java.sql.Types.BIGINT);
            }

            stmt.setString(7, request.getStatus());

//            if (request.getCurrentApproverAccountId() != null) {
//                stmt.setLong(8, request.getCurrentApproverAccountId());
//            } else {
//                stmt.setNull(8, java.sql.Types.BIGINT);
//            }

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

    @Override
    public Request update(Request request) {
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            // Set các parameter theo SQL mới (cập nhật 8 cột, updated_at = UTC_TIMESTAMP() trong SQL)
            stmt.setLong(1, request.getRequestTypeId());
            stmt.setString(2, request.getTitle());
            stmt.setString(3, request.getDetail()); // detail JSON
            stmt.setLong(4, request.getCreatedByAccountId());
            stmt.setLong(5, request.getCreatedByUserId());

            if (request.getDepartmentId() != null) {
                stmt.setLong(6, request.getDepartmentId());
            } else {
                stmt.setNull(6, java.sql.Types.BIGINT);
            }

            stmt.setString(7, request.getStatus());

            if (request.getCurrentApproverAccountId() != null) {
                stmt.setLong(8, request.getCurrentApproverAccountId());
            } else {
                stmt.setNull(8, java.sql.Types.BIGINT);
            }

            stmt.setLong(9, request.getId()); // WHERE id = ?

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
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE)) {

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
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(COUNT_ALL); ResultSet rs = stmt.executeQuery()) {

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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
     * Tìm requests theo trạng thái
     */
    public List<Request> findByStatus(String status) {
        List<Request> requests = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
//    public List<RequestDto> findAllWithDetails(int offset, int limit) {
//        List<RequestDto> requests = new ArrayList<>();
//        String sql = SELECT_WITH_DETAILS + " ORDER BY r.created_at DESC LIMIT ? OFFSET ?";
//
//        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, limit);
//            stmt.setInt(2, offset);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    requests.add(mapResultSetToDto(rs));
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.error("Error finding requests with details", e);
//            throw new RuntimeException("Error finding requests with details", e);
//        }
//
//        return requests;
//    }
    /**
     * Tìm requests của user với thông tin chi tiết
     */
//    public List<RequestDto> findByUserIdWithDetails(Long userId, int offset, int limit) {
//        List<RequestDto> requests = new ArrayList<>();
//        String sql = SELECT_WITH_DETAILS + " WHERE r.user_id = ? ORDER BY r.created_at DESC LIMIT ? OFFSET ?";
//
//        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setLong(1, userId);
//            stmt.setInt(2, limit);
//            stmt.setInt(3, offset);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    requests.add(mapResultSetToDto(rs));
//                }
//            }
//
//        } catch (SQLException e) {
//            logger.error("Error finding requests by user id with details: {}", userId, e);
//            throw new RuntimeException("Error finding requests by user id with details: " + userId, e);
//        }
//
//        return requests;
//    }
    @Override
    protected Request mapResultSetToEntity(ResultSet rs) throws SQLException {
        Request request = new Request();

        request.setId(rs.getLong("id"));
        request.setRequestTypeId(rs.getLong("request_type_id"));
        request.setTitle(rs.getString("title"));
        request.setDetail(rs.getString("detail")); // detail JSON
        request.setCreatedByAccountId(rs.getLong("created_by_account_id"));
        request.setCreatedByUserId(rs.getLong("created_by_user_id"));

        long deptId = rs.getLong("department_id");
        if (!rs.wasNull()) {
            request.setDepartmentId(deptId);
        }

        request.setStatus(rs.getString("status"));

        long approverId = rs.getLong("current_approver_account_id");
        if (!rs.wasNull()) {
            request.setCurrentApproverAccountId(approverId);
        }

        request.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        request.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return request;
    }

    /**
     * Map ResultSet to RequestDto (với thông tin join)
     */
//    private RequestDto mapResultSetToDto(ResultSet rs) throws SQLException {
//        RequestDto dto = new RequestDto(mapResultSetToEntity(rs));
//
//        // Thông tin user
//        dto.setUserName(rs.getString("username"));
//        dto.setUserFullName(rs.getString("full_name"));
//
//        // Thông tin request type
//        dto.setRequestTypeName(rs.getString("request_type_name"));
//        dto.setRequestTypeCode(rs.getString("request_type_code"));
//
//        // Thông tin approver
//        dto.setApproverName(rs.getString("approver_name"));
//
//        return dto;
//    }
}
