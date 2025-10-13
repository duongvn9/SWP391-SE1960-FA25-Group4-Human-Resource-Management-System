package group4.hrms.dao;

import group4.hrms.model.Request;
import group4.hrms.dto.RequestDto;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
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
        return "INSERT INTO requests (request_type_id, title, detail, " +
               "created_by_account_id, created_by_user_id, department_id, status, " +
               "created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE requests SET request_type_id = ?, title = ?, detail = ?, " +
               "department_id = ?, status = ?, current_approver_account_id = ?, " +
               "updated_at = ? WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Request request) throws SQLException {
        stmt.setLong(1, request.getRequestTypeId());
        stmt.setString(2, request.getTitle());
        stmt.setString(3, request.getDetailJson());
        stmt.setLong(4, request.getCreatedByAccountId());
        stmt.setLong(5, request.getCreatedByUserId());

        // Handle nullable department_id
        if (request.getDepartmentId() != null) {
            stmt.setLong(6, request.getDepartmentId());
        } else {
            stmt.setNull(6, java.sql.Types.BIGINT);
        }

        stmt.setString(7, request.getStatus());

        LocalDateTime now = LocalDateTime.now();
        setTimestamp(stmt, 8, now);
        setTimestamp(stmt, 9, now);
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Request request) throws SQLException {
        stmt.setLong(1, request.getRequestTypeId());
        stmt.setString(2, request.getTitle());
        stmt.setString(3, request.getDetailJson());

        // Handle nullable department_id
        if (request.getDepartmentId() != null) {
            stmt.setLong(4, request.getDepartmentId());
        } else {
            stmt.setNull(4, java.sql.Types.BIGINT);
        }

        stmt.setString(5, request.getStatus());

        // Handle nullable current_approver_account_id
        if (request.getCurrentApproverAccountId() != null) {
            stmt.setLong(6, request.getCurrentApproverAccountId());
        } else {
            stmt.setNull(6, java.sql.Types.BIGINT);
        }

        setTimestamp(stmt, 7, LocalDateTime.now());
        stmt.setLong(8, request.getId());
    }

    private static final String SELECT_ALL =
        "SELECT id, request_type_id, title, detail, created_by_account_id, " +
        "created_by_user_id, department_id, status, current_approver_account_id, " +
        "created_at, updated_at FROM " + TABLE_NAME;

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";

    private static final String SELECT_WITH_DETAILS =
        "SELECT r.id, r.request_type_id, r.title, r.detail, r.created_by_account_id, " +
        "r.created_by_user_id, r.department_id, r.status, r.current_approver_account_id, " +
        "r.created_at, r.updated_at, " +
        "u.username, u.full_name, " +
        "rt.name as request_type_name, rt.code as request_type_code, " +
        "approver.full_name as approver_name " +
        "FROM " + TABLE_NAME + " r " +
        "LEFT JOIN users u ON r.created_by_user_id = u.id " +
        "LEFT JOIN request_types rt ON r.request_type_id = rt.id " +
        "LEFT JOIN users approver ON r.current_approver_account_id = approver.id ";

    private static final String INSERT =
        "INSERT INTO " + TABLE_NAME + " (request_type_id, title, detail, created_by_account_id, " +
        "created_by_user_id, department_id, status, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
        "UPDATE " + TABLE_NAME + " SET request_type_id = ?, title = ?, detail = ?, " +
        "department_id = ?, status = ?, current_approver_account_id = ?, updated_at = ? WHERE id = ?";

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

            stmt.setLong(1, request.getRequestTypeId());
            stmt.setString(2, request.getTitle());
            stmt.setString(3, request.getDetailJson());
            stmt.setLong(4, request.getCreatedByAccountId());
            stmt.setLong(5, request.getCreatedByUserId());

            // Handle nullable department_id
            if (request.getDepartmentId() != null) {
                stmt.setLong(6, request.getDepartmentId());
            } else {
                stmt.setNull(6, java.sql.Types.BIGINT);
            }

            stmt.setString(7, request.getStatus());
            stmt.setTimestamp(8, Timestamp.valueOf(request.getCreatedAt()));
            stmt.setTimestamp(9, Timestamp.valueOf(request.getUpdatedAt()));

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

            stmt.setLong(1, request.getRequestTypeId());
            stmt.setString(2, request.getTitle());
            stmt.setString(3, request.getDetailJson());

            // Handle nullable department_id
            if (request.getDepartmentId() != null) {
                stmt.setLong(4, request.getDepartmentId());
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }

            stmt.setString(5, request.getStatus());

            // Handle nullable current_approver_account_id
            if (request.getCurrentApproverAccountId() != null) {
                stmt.setLong(6, request.getCurrentApproverAccountId());
            } else {
                stmt.setNull(6, java.sql.Types.BIGINT);
            }

            stmt.setTimestamp(7, Timestamp.valueOf(request.getUpdatedAt()));
            stmt.setLong(8, request.getId());

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
        String sql = SELECT_ALL + " WHERE created_by_user_id = ? ORDER BY created_at DESC";

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
        String sql = SELECT_WITH_DETAILS + " WHERE r.created_by_user_id = ? ORDER BY r.created_at DESC LIMIT ? OFFSET ?";

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
        request.setRequestTypeId(rs.getLong("request_type_id"));
        request.setTitle(rs.getString("title"));

        // Read detail column as JSON string
        String detailJson = rs.getString("detail");
        if (detailJson != null && !detailJson.trim().isEmpty()) {
            request.setDetailJson(detailJson);
        }

        request.setCreatedByAccountId(rs.getLong("created_by_account_id"));
        request.setCreatedByUserId(rs.getLong("created_by_user_id"));

        // Handle nullable department_id
        Long departmentId = (Long) rs.getObject("department_id");
        if (departmentId != null) {
            request.setDepartmentId(departmentId);
        }

        request.setStatus(rs.getString("status"));

        // Handle nullable current_approver_account_id
        Long currentApproverId = (Long) rs.getObject("current_approver_account_id");
        if (currentApproverId != null) {
            request.setCurrentApproverAccountId(currentApproverId);
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

    /**
     * Tìm requests theo user ID và khoảng thời gian
     * Dùng để kiểm tra overlap và pending requests
     *
     * @param userId User ID
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param statuses Danh sách status cần filter (PENDING, APPROVED, etc.)
     * @param excludeRequestId Request ID cần loại trừ (để update không conflict với chính nó)
     * @return List of requests trong khoảng thời gian
     */
    public List<Request> findByUserIdAndDateRange(Long userId, LocalDateTime startDate,
                                                   LocalDateTime endDate, List<String> statuses,
                                                   Long excludeRequestId) {
        List<Request> requests = new ArrayList<>();

        // Build SQL with JSON extraction for date comparison
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, request_type_id, title, detail, created_by_account_id, ");
        sql.append("created_by_user_id, department_id, status, current_approver_account_id, ");
        sql.append("created_at, updated_at FROM ").append(TABLE_NAME).append(" ");
        sql.append("WHERE created_by_user_id = ? ");

        // Add status filter
        if (statuses != null && !statuses.isEmpty()) {
            sql.append("AND status IN (");
            for (int i = 0; i < statuses.size(); i++) {
                sql.append("?");
                if (i < statuses.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(") ");
        }

        // Exclude specific request ID (for update scenarios)
        if (excludeRequestId != null) {
            sql.append("AND id != ? ");
        }

        // Date range filter using JSON extraction
        // Check if request date range overlaps with given date range
        sql.append("AND JSON_UNQUOTE(JSON_EXTRACT(detail, '$.startDate')) <= ? ");
        sql.append("AND JSON_UNQUOTE(JSON_EXTRACT(detail, '$.endDate')) >= ? ");
        sql.append("ORDER BY created_at DESC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setLong(paramIndex++, userId);

            // Set status parameters
            if (statuses != null && !statuses.isEmpty()) {
                for (String status : statuses) {
                    stmt.setString(paramIndex++, status);
                }
            }

            // Set exclude request ID
            if (excludeRequestId != null) {
                stmt.setLong(paramIndex++, excludeRequestId);
            }

            // Set date range parameters
            // Format: yyyy-MM-dd (matching the format in LeaveRequestDetail)
            stmt.setString(paramIndex++, endDate.toLocalDate().toString());
            stmt.setString(paramIndex++, startDate.toLocalDate().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding requests by user id and date range: userId={}, startDate={}, endDate={}",
                        userId, startDate, endDate, e);
            throw new RuntimeException("Error finding requests by user id and date range", e);
        }

        return requests;
    }

    /**
     * Tìm OT requests theo user ID và khoảng thời gian
     * Dùng để kiểm tra conflict giữa leave request và OT request
     *
     * @param userId User ID
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return List of OT requests trong khoảng thời gian với status = APPROVED
     */
    public List<Request> findOTRequestsByUserIdAndDateRange(Long userId, LocalDateTime startDate,
                                                             LocalDateTime endDate) {
        List<Request> requests = new ArrayList<>();

        // Build SQL to find OT requests
        // Filter by request_type_id for OVERTIME_REQUEST and status = APPROVED
        // Extract otDate from JSON detail and check if it falls within date range
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.id, r.request_type_id, r.title, r.detail, r.created_by_account_id, ");
        sql.append("r.created_by_user_id, r.department_id, r.status, r.current_approver_account_id, ");
        sql.append("r.created_at, r.updated_at ");
        sql.append("FROM ").append(TABLE_NAME).append(" r ");
        sql.append("INNER JOIN request_types rt ON r.request_type_id = rt.id ");
        sql.append("WHERE r.created_by_user_id = ? ");
        sql.append("AND rt.code = 'OVERTIME_REQUEST' ");
        sql.append("AND r.status = 'APPROVED' ");
        sql.append("AND JSON_UNQUOTE(JSON_EXTRACT(r.detail, '$.otDate')) >= ? ");
        sql.append("AND JSON_UNQUOTE(JSON_EXTRACT(r.detail, '$.otDate')) <= ? ");
        sql.append("ORDER BY r.created_at DESC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setLong(1, userId);
            // Format: yyyy-MM-dd (matching the format in OTRequestDetail)
            stmt.setString(2, startDate.toLocalDate().toString());
            stmt.setString(3, endDate.toLocalDate().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding OT requests by user id and date range: userId={}, startDate={}, endDate={}",
                        userId, startDate, endDate, e);
            throw new RuntimeException("Error finding OT requests by user id and date range", e);
        }

        return requests;
    }
}