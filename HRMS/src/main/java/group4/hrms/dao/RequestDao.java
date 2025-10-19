package group4.hrms.dao;

import group4.hrms.model.Request;
import group4.hrms.dto.RequestDto;
import group4.hrms.dto.RequestListFilter;
import group4.hrms.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Request entity. Handles database operations for leave
 * requests and OT requests. Supports JSON detail storage and parsing for
 * LeaveRequestDetail and OTRequestDetail.
 *
 * @author HRMS Development Team
 * @version 1.0
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
        return "INSERT INTO requests (request_type_id, title, detail, "
                + "created_by_account_id, created_by_user_id, department_id, status, "
                + "created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE requests SET request_type_id = ?, title = ?, detail = ?, "
                + "department_id = ?, status = ?, current_approver_account_id = ?, "
                + "updated_at = ? WHERE id = ?";
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

    private static final String SELECT_ALL
            = "SELECT id, request_type_id, title, detail, created_by_account_id, "
            + "created_by_user_id, department_id, status, current_approver_account_id, "
            + "created_at, updated_at FROM " + TABLE_NAME;

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";

    private static final String SELECT_WITH_DETAILS
            = "SELECT r.id, r.request_type_id, r.title, r.detail, r.created_by_account_id, "
            + "r.created_by_user_id, r.department_id, r.status, r.current_approver_account_id, "
            + "r.created_at, r.updated_at, "
            + "u.username, u.full_name, u.employee_code, "
            + "rt.name as request_type_name, rt.code as request_type_code, "
            + "d.name as department_name, "
            + "approver.full_name as approver_name "
            + "FROM " + TABLE_NAME + " r "
            + "LEFT JOIN users u ON r.created_by_user_id = u.id "
            + "LEFT JOIN request_types rt ON r.request_type_id = rt.id "
            + "LEFT JOIN departments d ON u.department_id = d.id "
            + "LEFT JOIN users approver ON r.current_approver_account_id = approver.id ";

    private static final String INSERT
            = "INSERT INTO " + TABLE_NAME + " (request_type_id, title, detail, created_by_account_id, "
            + "created_by_user_id, department_id, status, created_at, updated_at) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE
            = "UPDATE " + TABLE_NAME + " SET request_type_id = ?, title = ?, detail = ?, "
            + "department_id = ?, status = ?, current_approver_account_id = ?, updated_at = ? WHERE id = ?";

    private static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

    private static final String COUNT_ALL = "SELECT COUNT(*) FROM " + TABLE_NAME;

    @Override
    public Optional<Request> findById(Long id) {
        logger.debug("Finding request by id: {}", id);

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Request request = mapResultSetToEntity(rs);
                    logger.debug("Found request with id: {}", id);
                    return Optional.of(request);
                }
            }

        } catch (SQLException e) {
            logger.error("Database error finding request by id: {}. SQL State: {}, Error Code: {}",
                    id, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error finding request by id: " + id, e);
        }

        logger.debug("No request found with id: {}", id);
        return Optional.empty();
    }

    @Override
    public List<Request> findAll() {
        return findAll(0, Integer.MAX_VALUE);
    }

    public List<Request> findAll(int offset, int limit) {
        logger.debug("Finding all requests with offset: {}, limit: {}", offset, limit);
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

            logger.debug("Found {} requests", requests.size());

        } catch (SQLException e) {
            logger.error("Database error finding all requests. Offset: {}, Limit: {}. SQL State: {}, Error Code: {}",
                    offset, limit, e.getSQLState(), e.getErrorCode(), e);
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
        logger.debug("Inserting new request: userId={}, typeId={}, status={}",
                request.getCreatedByUserId(), request.getRequestTypeId(), request.getStatus());

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

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

            logger.debug("Executing insert with parameters: typeId={}, userId={}, accountId={}, status={}",
                    request.getRequestTypeId(), request.getCreatedByUserId(),
                    request.getCreatedByAccountId(), request.getStatus());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        request.setId(generatedKeys.getLong(1));
                        logger.info("Request created successfully: id={}, userId={}, typeId={}, status={}",
                                request.getId(), request.getCreatedByUserId(),
                                request.getRequestTypeId(), request.getStatus());
                        return request;
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Database error creating request. UserId: {}, TypeId: {}, Status: {}. SQL State: {}, Error Code: {}",
                    request.getCreatedByUserId(), request.getRequestTypeId(), request.getStatus(),
                    e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error creating request", e);
        }

        logger.error("Failed to create request - no rows affected or no generated key. UserId: {}, TypeId: {}",
                request.getCreatedByUserId(), request.getRequestTypeId());
        throw new RuntimeException("Failed to create request");
    }

    @Override
    public Request update(Request request) {
        logger.debug("Updating request: id={}, status={}", request.getId(), request.getStatus());

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

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

            logger.debug("Executing update with parameters: id={}, typeId={}, status={}",
                    request.getId(), request.getRequestTypeId(), request.getStatus());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Request updated successfully: id={}, status={}",
                        request.getId(), request.getStatus());
                return request;
            }

        } catch (SQLException e) {
            logger.error("Database error updating request. Id: {}, Status: {}. SQL State: {}, Error Code: {}",
                    request.getId(), request.getStatus(), e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error updating request: " + request.getId(), e);
        }

        logger.error("Failed to update request - no rows affected. Id: {}", request.getId());
        throw new RuntimeException("Failed to update request: " + request.getId());
    }

    @Override
    public boolean deleteById(Long id) {
        logger.debug("Deleting request by id: {}", id);

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Request deleted successfully: id={}", id);
                return true;
            }

            logger.warn("No request found to delete with id: {}", id);

        } catch (SQLException e) {
            logger.error("Database error deleting request. Id: {}. SQL State: {}, Error Code: {}",
                    id, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error deleting request: " + id, e);
        }

        return false;
    }

    @Override
    public long count() {
        logger.debug("Counting all requests");

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(COUNT_ALL); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long count = rs.getLong(1);
                logger.debug("Total request count: {}", count);
                return count;
            }

        } catch (SQLException e) {
            logger.error("Database error counting requests. SQL State: {}, Error Code: {}",
                    e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error counting requests", e);
        }

        return 0;
    }

    /**
     * Find all requests created by a specific user. Returns requests with
     * parsed JSON details (LeaveRequestDetail or OTRequestDetail).
     *
     * @param userId the ID of the user who created the requests
     * @return list of requests ordered by creation date (newest first)
     * @throws RuntimeException if database error occurs
     */
    public List<Request> findByUserId(Long userId) {
        logger.debug("Finding requests by userId: {}", userId);
        List<Request> requests = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE created_by_user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} requests for userId: {}", requests.size(), userId);

        } catch (SQLException e) {
            logger.error("Database error finding requests by userId: {}. SQL State: {}, Error Code: {}",
                    userId, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error finding requests by user id: " + userId, e);
        }

        return requests;
    }

    /**
     * Find all requests with a specific status.
     *
     * @param status the status to filter by (e.g., "PENDING", "APPROVED",
     * "REJECTED")
     * @return list of requests with the specified status, ordered by creation
     * date (newest first)
     * @throws RuntimeException if database error occurs
     */
    public List<Request> findByStatus(String status) {
        logger.debug("Finding requests by status: {}", status);
        List<Request> requests = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} requests with status: {}", requests.size(), status);

        } catch (SQLException e) {
            logger.error("Database error finding requests by status: {}. SQL State: {}, Error Code: {}",
                    status, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error finding requests by status: " + status, e);
        }

        return requests;
    }

    /**
     * Find all requests with detailed information (joined with user and
     * request_type tables). Supports pagination for large result sets.
     *
     * @param offset the starting position for pagination
     * @param limit the maximum number of records to return
     * @return list of RequestDto objects with detailed information
     * @throws RuntimeException if database error occurs
     */
    public List<RequestDto> findAllWithDetails(int offset, int limit) {
        logger.debug("Finding all requests with details: offset={}, limit={}", offset, limit);
        List<RequestDto> requests = new ArrayList<>();
        String sql = SELECT_WITH_DETAILS + " ORDER BY r.created_at DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToDto(rs));
                }
            }

            logger.debug("Found {} requests with details", requests.size());

        } catch (SQLException e) {
            logger.error("Database error finding requests with details. Offset: {}, Limit: {}. SQL State: {}, Error Code: {}",
                    offset, limit, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error finding requests with details", e);
        }

        return requests;
    }

    /**
     * Find requests for a specific user with detailed information. Joins with
     * user and request_type tables for complete information. Supports
     * pagination for large result sets.
     *
     * @param userId the ID of the user who created the requests
     * @param offset the starting position for pagination
     * @param limit the maximum number of records to return
     * @return list of RequestDto objects with detailed information
     * @throws RuntimeException if database error occurs
     */
    public List<RequestDto> findByUserIdWithDetails(Long userId, int offset, int limit) {
        logger.debug("Finding requests by userId with details: userId={}, offset={}, limit={}",
                userId, offset, limit);
        List<RequestDto> requests = new ArrayList<>();
        String sql = SELECT_WITH_DETAILS + " WHERE r.created_by_user_id = ? ORDER BY r.created_at DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToDto(rs));
                }
            }

            logger.debug("Found {} requests with details for userId: {}", requests.size(), userId);

        } catch (SQLException e) {
            logger.error("Database error finding requests by userId with details. UserId: {}, Offset: {}, Limit: {}. SQL State: {}, Error Code: {}",
                    userId, offset, limit, e.getSQLState(), e.getErrorCode(), e);
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

        // Read detail column as JSON string and handle parsing errors gracefully
        String detailJson = rs.getString("detail");
        if (detailJson != null && !detailJson.trim().isEmpty()) {
            try {
                request.setDetailJson(detailJson);
                // Pre-parse JSON to validate it (will be cached in Request object)
                // This triggers lazy loading and validates JSON format
                if (detailJson.contains("leaveTypeCode")) {
                    request.getLeaveDetail(); // Trigger parsing for leave requests
                } else if (detailJson.contains("otDate")) {
                    request.getOtDetail(); // Trigger parsing for OT requests
                }
            } catch (Exception e) {
                logger.warn("JSON parsing error for request id={}: {}. Detail JSON: {}. Setting detail to null.",
                        request.getId(), e.getMessage(),
                        detailJson.length() > 100 ? detailJson.substring(0, 100) + "..." : detailJson);
                request.setDetailJson(null);
            }
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
     * Map ResultSet to RequestDto with joined information from related tables.
     * Includes user information, request type details, and approver
     * information.
     *
     * @param rs the ResultSet containing joined data
     * @return RequestDto object with complete information
     * @throws SQLException if error occurs reading from ResultSet
     */
    private RequestDto mapResultSetToDto(ResultSet rs) throws SQLException {
        RequestDto dto = new RequestDto(mapResultSetToEntity(rs));

        // Thông tin user
        dto.setUserName(rs.getString("username"));
        dto.setUserFullName(rs.getString("full_name"));
        dto.setEmployeeCode(rs.getString("employee_code"));

        // Thông tin request type
        dto.setRequestTypeName(rs.getString("request_type_name"));
        dto.setRequestTypeCode(rs.getString("request_type_code"));

        // Thông tin department
        dto.setDepartmentName(rs.getString("department_name"));

        // Thông tin approver
        dto.setApproverName(rs.getString("approver_name"));

        return dto;
    }

    /**
     * Find leave requests by user ID within a specific date range. Used for
     * overlap detection and pending request checks. Parses JSON detail to
     * extract startDate and endDate for comparison.
     *
     * @param userId the ID of the user who created the requests
     * @param startDate the start date of the range to check
     * @param endDate the end date of the range to check
     * @param statuses list of statuses to filter by (e.g., "PENDING",
     * "APPROVED")
     * @param excludeRequestId optional request ID to exclude (for update
     * scenarios to avoid self-conflict)
     * @return list of requests that overlap with the specified date range
     * @throws RuntimeException if database error occurs
     */
    public List<Request> findByUserIdAndDateRange(Long userId, LocalDateTime startDate,
            LocalDateTime endDate, List<String> statuses,
            Long excludeRequestId) {
        logger.debug("Finding requests by userId and date range: userId={}, startDate={}, endDate={}, statuses={}, excludeId={}",
                userId, startDate, endDate, statuses, excludeRequestId);
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
        // Use DATE() function to extract date part only (handles both 'YYYY-MM-DD' and 'YYYY-MM-DDTHH:MM:SS' formats)
        sql.append("AND DATE(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.startDate'))) <= ? ");
        sql.append("AND DATE(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.endDate'))) >= ? ");
        sql.append("ORDER BY created_at DESC");

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

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

            logger.info("=== EXECUTING OVERLAP QUERY ===");
            logger.info("SQL: " + sql.toString());
            logger.info("Parameters: userId={}, statuses={}, excludeId={}, endDate={}, startDate={}",
                    userId, statuses, excludeRequestId, endDate.toLocalDate(), startDate.toLocalDate());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Request req = mapResultSetToEntity(rs);
                    requests.add(req);
                    logger.info("Found overlapping request: id={}, status={}, detail={}",
                            req.getId(), req.getStatus(), req.getDetailJson());
                }
            }

            logger.info("=== QUERY RESULT: Found {} requests ===", requests.size());

        } catch (SQLException e) {
            logger.error("Database error finding requests by userId and date range. UserId: {}, StartDate: {}, EndDate: {}, Statuses: {}, ExcludeId: {}. SQL State: {}, Error Code: {}",
                    userId, startDate, endDate, statuses, excludeRequestId, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error finding requests by user id and date range", e);
        }

        return requests;
    }

    /**
     * Count total requests matching filter criteria.
     * Used for pagination metadata calculation.
     *
     * @param filter RequestListFilter containing all filter criteria
     * @return Total count of matching requests
     * @throws RuntimeException if database error occurs
     */
    public long countWithFilters(RequestListFilter filter) {
        logger.debug("Counting requests with filters: filter={}", filter);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ").append(TABLE_NAME).append(" r ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Status filter (excluding CANCELLED by default)
        if (filter.isShowCancelled()) {
            if (filter.hasStatusFilter()) {
                sql.append("AND r.status = ? ");
                params.add(filter.getStatus());
            }
        } else {
            if (filter.hasStatusFilter()) {
                sql.append("AND r.status = ? ");
                params.add(filter.getStatus());
            } else {
                sql.append("AND r.status != 'CANCELLED' ");
            }
        }

        // Type filter
        if (filter.hasTypeFilter()) {
            sql.append("AND r.request_type_id = ? ");
            params.add(filter.getRequestTypeId());
        }

        // Date range filter
        if (filter.getFromDate() != null) {
            sql.append("AND DATE(r.created_at) >= ? ");
            params.add(filter.getFromDate());
        }
        if (filter.getToDate() != null) {
            sql.append("AND DATE(r.created_at) <= ? ");
            params.add(filter.getToDate());
        }

        // Employee filter
        if (filter.hasEmployeeFilter()) {
            sql.append("AND r.created_by_user_id = ? ");
            params.add(filter.getEmployeeId());
        }

        // Search filter (title or detail)
        if (filter.hasSearch()) {
            sql.append("AND (r.title LIKE ? OR JSON_UNQUOTE(JSON_EXTRACT(r.detail, '$.reason')) LIKE ?) ");
            String searchPattern = "%" + filter.getSearchKeyword() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Long) {
                    stmt.setLong(i + 1, (Long) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof LocalDate) {
                    stmt.setDate(i + 1, java.sql.Date.valueOf((LocalDate) param));
                } else {
                    stmt.setString(i + 1, param.toString());
                }
            }

            logger.debug("Executing count query: {}", sql.toString());
            logger.debug("Parameters: {}", params);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long count = rs.getLong(1);
                    logger.debug("Total request count with filters: {}", count);
                    return count;
                }
            }

        } catch (SQLException e) {
            logger.error("Database error counting requests with filters. Filter: {}. SQL State: {}, Error Code: {}",
                    filter, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error counting requests with filters", e);
        }

        return 0;
    }

    /**
     * Soft delete request by changing status to CANCELLED.
     * Does not physically delete the record from database.
     *
     * @param requestId Request ID to delete
     * @return true if successful, false otherwise
     * @throws RuntimeException if database error occurs
     */
    public boolean softDelete(Long requestId) {
        logger.debug("Soft deleting request: id={}", requestId);

        String sql = "UPDATE " + TABLE_NAME + " SET status = 'CANCELLED', updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, requestId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Request soft deleted successfully: id={}", requestId);
                return true;
            }

            logger.warn("No request found to soft delete with id: {}", requestId);

        } catch (SQLException e) {
            logger.error("Database error soft deleting request. Id: {}. SQL State: {}, Error Code: {}",
                    requestId, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error soft deleting request: " + requestId, e);
        }

        return false;
    }

    /**
     * Map ResultSet to RequestDto with extended information including employee code and department name.
     * Used by findWithFilters method.
     *
     * @param rs the ResultSet containing joined data
     * @return RequestDto object with complete information
     * @throws SQLException if error occurs reading from ResultSet
     */
    private RequestDto mapResultSetToDtoExtended(ResultSet rs) throws SQLException {
        RequestDto dto = new RequestDto(mapResultSetToEntity(rs));

        // Employee information
        dto.setEmployeeCode(rs.getString("employee_code"));
        dto.setUserFullName(rs.getString("full_name"));

        // Department information
        dto.setDepartmentName(rs.getString("department_name"));

        // Request type information
        dto.setRequestTypeName(rs.getString("request_type_name"));
        dto.setRequestTypeCode(rs.getString("request_type_code"));

        return dto;
    }

    /**
     * Find approved OT requests by user ID within a specific date range. Used
     * for conflict detection between leave requests and OT requests. Parses
     * JSON detail to extract otDate for comparison. Only returns requests with
     * status = APPROVED.
     *
     * @param userId the ID of the user who created the requests
     * @param startDate the start date of the range to check
     * @param endDate the end date of the range to check
     * @return list of approved OT requests within the specified date range
     * @throws RuntimeException if database error occurs
     */
    public List<Request> findOTRequestsByUserIdAndDateRange(Long userId, LocalDateTime startDate,
            LocalDateTime endDate) {
        logger.debug("Finding OT requests by userId and date range: userId={}, startDate={}, endDate={}",
                userId, startDate, endDate);
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

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setLong(1, userId);
            // Format: yyyy-MM-dd (matching the format in OTRequestDetail)
            stmt.setString(2, startDate.toLocalDate().toString());
            stmt.setString(3, endDate.toLocalDate().toString());

            logger.debug("Executing OT query with parameters: userId={}, startDate={}, endDate={}",
                    userId, startDate.toLocalDate(), endDate.toLocalDate());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} OT requests in date range for userId: {}", requests.size(), userId);

        } catch (SQLException e) {
            logger.error("Database error finding OT requests by userId and date range. UserId: {}, StartDate: {}, EndDate: {}. SQL State: {}, Error Code: {}",
                    userId, startDate, endDate, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error finding OT requests by user id and date range", e);
        }

        return requests;
    }

    /**
     * Lấy danh sách yêu cầu tuyển dụng nháp (DRAFT) của 1 user
     */
    public List<Request> findDraftsByUserId(Long userId) {
        List<Request> drafts = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName()
                + " WHERE created_by_user_id = ? AND status = 'DRAFT' ORDER BY updated_at DESC";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = SELECT_WITH_DETAILS + " WHERE r.created_by_user_id = ? AND r.status = 'DRAFT' ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }

        } catch (Exception e) {
            logger.error("Error finding pending recruitment requests", e);
        }
        return list;
    }

    /**
     * Tìm requests theo loại và trạng thái (ví dụ: Recruitment + PENDING)
     */
    public List<Request> findByTypeAndStatus(Long requestTypeId, String status) {
        logger.debug("Finding requests by type and status: typeId={}, status={}", requestTypeId, status);
        List<Request> requests = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE request_type_id = ? AND status = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, requestTypeId);
            stmt.setString(2, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }

            logger.debug("Found {} requests with typeId={}, status={}", requests.size(), requestTypeId, status);

        } catch (SQLException e) {
            logger.error("Database error finding requests by type and status. TypeId: {}, Status: {}. SQL State: {}, Error Code: {}",
                    requestTypeId, status, e.getSQLState(), e.getErrorCode(), e);
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

    // ==================== Performance Optimized Methods ====================
    /**
     * Count approved leave days by user, leave type and year (optimized) Uses
     * SQL aggregation instead of loading all requests into memory
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param year Year to count
     * @return Total days used
     */
    public double countApprovedLeaveDaysByUserAndTypeAndYear(Long userId, String leaveTypeCode, int year) {
        logger.debug("Counting approved leave days: userId={}, leaveType={}, year={}", userId, leaveTypeCode, year);

        String sql
                = "SELECT COALESCE(SUM(CAST(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.durationDays')) AS DECIMAL(10,2))), 0) as total_days "
                + "FROM requests "
                + "WHERE created_by_user_id = ? "
                + "AND status = 'APPROVED' "
                + "AND JSON_UNQUOTE(JSON_EXTRACT(detail, '$.leaveTypeCode')) = ? "
                + "AND YEAR(STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.startDate')), '%Y-%m-%dT%H:%i:%s')) = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, leaveTypeCode);
            stmt.setInt(3, year);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double totalDays = rs.getDouble("total_days");
                    logger.debug("Counted {} approved leave days for userId={}, leaveType={}, year={}",
                            totalDays, userId, leaveTypeCode, year);
                    return totalDays;
                }
            }

        } catch (SQLException e) {
            logger.error("Database error counting approved leave days. UserId: {}, LeaveType: {}, Year: {}. SQL State: {}, Error Code: {}",
                    userId, leaveTypeCode, year, e.getSQLState(), e.getErrorCode(), e);
            // Return 0 on error to be safe
            return 0;
        }

        return 0;
    }

    /**
     * Count pending leave days by user, leave type and year (optimized) Uses
     * SQL aggregation instead of loading all requests into memory
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param year Year to count
     * @return Total pending days
     */
    public double countPendingLeaveDaysByUserAndTypeAndYear(Long userId, String leaveTypeCode, int year) {
        logger.debug("Counting pending leave days: userId={}, leaveType={}, year={}", userId, leaveTypeCode, year);

        String sql
                = "SELECT COALESCE(SUM(CAST(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.durationDays')) AS DECIMAL(10,2))), 0) as total_days "
                + "FROM requests "
                + "WHERE created_by_user_id = ? "
                + "AND status = 'PENDING' "
                + "AND JSON_UNQUOTE(JSON_EXTRACT(detail, '$.leaveTypeCode')) = ? "
                + "AND YEAR(STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.startDate')), '%Y-%m-%dT%H:%i:%s')) = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, leaveTypeCode);
            stmt.setInt(3, year);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double totalDays = rs.getDouble("total_days");
                    logger.debug("Counted {} pending leave days for userId={}, leaveType={}, year={}",
                            totalDays, userId, leaveTypeCode, year);
                    return totalDays;
                }
            }

        } catch (SQLException e) {
            logger.error("Database error counting pending leave days. UserId: {}, LeaveType: {}, Year: {}. SQL State: {}, Error Code: {}",
                    userId, leaveTypeCode, year, e.getSQLState(), e.getErrorCode(), e);
            // Return 0 on error to be safe
            return 0;
        }

        return 0;
    }

    /**
     * Check if user has any approved leave on specific date (optimized) Uses
     * SQL query instead of loading all requests
     *
     * @param userId User ID
     * @param date Date to check
     * @return true if user has approved leave on this date
     */
    public boolean hasApprovedLeaveOnDate(Long userId, java.time.LocalDate date) {
        logger.debug("Checking approved leave on date: userId={}, date={}", userId, date);

        String sql
                = "SELECT COUNT(*) as count "
                + "FROM requests "
                + "WHERE created_by_user_id = ? "
                + "AND status = 'APPROVED' "
                + "AND STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.startDate')), '%Y-%m-%dT%H:%i:%s') <= ? "
                + "AND STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(detail, '$.endDate')), '%Y-%m-%dT%H:%i:%s') >= ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, date.toString());
            stmt.setString(3, date.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    boolean hasLeave = count > 0;
                    logger.debug("User {} {} approved leave on {}", userId, hasLeave ? "has" : "does not have", date);
                    return hasLeave;
                }
            }

        } catch (SQLException e) {
            logger.error("Database error checking approved leave. UserId: {}, Date: {}. SQL State: {}, Error Code: {}",
                    userId, date, e.getSQLState(), e.getErrorCode(), e);
            // Return false on error to be safe
            return false;
        }

        return false;
    }

    /**
     * Find requests with advanced filtering and pagination.
     * Supports filtering by scope, type, status, date range, employee, and search.
     *
     * Requirements: 1, 4, 5, 6, 6.1, 7, 8, 9
     *
     * @param filter RequestListFilter containing all filter criteria
     * @param targetUserIds List of user IDs to filter by (empty for "all" scope)
     * @param offset Starting position for pagination
     * @param limit Number of records to return
     * @return List of RequestDto with joined user and department info
     * @throws RuntimeException if database error occurs
     */
    public List<RequestDto> findWithFilters(RequestListFilter filter, List<Long> targetUserIds,
                                           int offset, int limit) {
        logger.debug("Finding requests with filters: filter={}, usffset={}, limit={}",
                    filter, targetUserIds, offset, limit);
        List<RequestDto> requests = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.id, r.request_type_id, r.title, r.detail, r.created_by_account_id, ");
        sql.append("r.created_by_user_id, r.department_id, r.status, r.current_approver_account_id, ");
        sql.append("r.created_at, r.updated_at, ");
        sql.append("u.employee_code, u.full_name, ");
        sql.append("d.name as department_name, ");
        sql.append("rt.name as request_type_name, rt.code as request_type_code ");
        sql.append("FROM ").append(TABLE_NAME).append(" r ");
        sql.append("LEFT JOIN users u ON r.created_by_user_id = u.id ");
        sql.append("LEFT JOIN departments d ON u.department_id = d.id ");
        sql.append("LEFT JOIN request_types rt ON r.request_type_id = rt.id ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // User scope filter
        if (targetUserIds != null && !targetUserIds.isEmpty()) {
            sql.append("AND r.created_by_user_id IN (");
            for (int i = 0; i < targetUserIds.size(); i++) {
                sql.append("?");
                if (i < targetUserIds.size() - 1) {
                    sql.append(", ");
                }
                params.add(targetUserIds.get(i));
            }
            sql.append(") ");
        }

        // Status filter
        if (filter.hasStatusFilter()) {
            sql.append("AND r.status = ? ");
            params.add(filter.getStatus());
        }

        // Show cancelled toggle
        if (!filter.isShowCancelled()) {
            sql.append("AND r.status != 'CANCELLED' ");
        }

        // Type filter
        if (filter.hasTypeFilter()) {
            sql.append("AND r.request_type_id = ? ");
            params.add(filter.getRequestTypeId());
        }

        // Date range filter
        if (filter.getFromDate() != null) {
            sql.append("AND DATE(r.created_at) >= ? ");
            params.add(filter.getFromDate().toString());
        }
        if (filter.getToDate() != null) {
            sql.append("AND DATE(r.created_at) <= ? ");
            params.add(filter.getToDate().toString());
        }

        // Employee filter
        if (filter.hasEmployeeFilter()) {
            sql.append("AND r.created_by_user_id = ? ");
            params.add(filter.getEmployeeId());
        }

        // Search filter
        if (filter.hasSearch()) {
            sql.append("AND (r.title LIKE ? OR r.detail LIKE ?) ");
            String searchPattern = "%" + filter.getSearchKeyword() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Order by
        if ("all".equals(filter.getScope())) {
            sql.append("ORDER BY d.name ASC, r.created_at DESC ");
        } else {
            sql.append("ORDER BY r.created_at DESC ");
        }

        // Pagination
        sql.append("LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Long) {
                    stmt.setLong(i + 1, (Long) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RequestDto dto = mapResultSetToDtoExtended(rs);
                    requests.add(dto);
                }
            }

            logger.debug("Found {} requests with filters", requests.size());

        } catch (SQLException e) {
            logger.error("Database error finding requests with filters. Filter: {}. SQL State: {}, Error Code: {}",
                        filter, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error finding requests with filters", e);
        }

        return requests;
    }

    /**
     * Count total requests matching filter criteria.
     *
     * Requirements: 1, 4, 5, 6, 6.1, 7, 8, 9
     *
     * @param filter RequestListFilter containing all filter criteria
     * @param targetUserIds List of user IDs to filter by (empty for "all" scope)
     * @return Total count of matching requests
     * @throws RuntimeException if database error occurs
     */
    public long countWithFilters(RequestListFilter filter, List<Long> targetUserIds) {
        logger.debug("Counting requests with filters: filter={}, userIds={}", filter, targetUserIds);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ").append(TABLE_NAME).append(" r ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // User scope filter
        if (targetUserIds != null && !targetUserIds.isEmpty()) {
            sql.append("AND r.created_by_user_id IN (");
            for (int i = 0; i < targetUserIds.size(); i++) {
                sql.append("?");
                if (i < targetUserIds.size() - 1) {
                    sql.append(", ");
                }
                params.add(targetUserIds.get(i));
            }
            sql.append(") ");
        }

        // Status filter
        if (filter.hasStatusFilter()) {
            sql.append("AND r.status = ? ");
            params.add(filter.getStatus());
        }

        // Show cancelled toggle
        if (!filter.isShowCancelled()) {
            sql.append("AND r.status != 'CANCELLED' ");
        }

        // Type filter
        if (filter.hasTypeFilter()) {
            sql.append("AND r.request_type_id = ? ");
            params.add(filter.getRequestTypeId());
        }

        // Date range filter
        if (filter.getFromDate() != null) {
            sql.append("AND DATE(r.created_at) >= ? ");
            params.add(filter.getFromDate().toString());
        }
        if (filter.getToDate() != null) {
            sql.append("AND DATE(r.created_at) <= ? ");
            params.add(filter.getToDate().toString());
        }

        // Employee filter
        if (filter.hasEmployeeFilter()) {
            sql.append("AND r.created_by_user_id = ? ");
            params.add(filter.getEmployeeId());
        }

        // Search filter
        if (filter.hasSearch()) {
            sql.append("AND (r.title LIKE ? OR r.detail LIKE ?) ");
            String searchPattern = "%" + filter.getSearchKeyword() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Long) {
                    stmt.setLong(i + 1, (Long) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long count = rs.getLong(1);
                    logger.debug("Total request count with filters: {}", count);
                    return count;
                }
            }

        } catch (SQLException e) {
            logger.error("Database error counting requests with filters. Filter: {}. SQL State: {}, Error Code: {}",
                        filter, e.getSQLState(), e.getErrorCode(), e);
            throw new RuntimeException("Error counting requests with filters", e);
        }

        return 0;
    }

}
