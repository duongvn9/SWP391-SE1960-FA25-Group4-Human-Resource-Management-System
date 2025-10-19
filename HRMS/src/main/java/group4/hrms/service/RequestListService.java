package group4.hrms.service;

import group4.hrms.dao.RequestDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dto.RequestDto;
import group4.hrms.dto.RequestListFilter;
import group4.hrms.dto.RequestListResult;
import group4.hrms.dto.PaginationMetadata;
import group4.hrms.model.User;
import group4.hrms.model.Position;
import group4.hrms.model.Request;
import group4.hrms.model.Department;
import group4.hrms.util.RequestListPermissionHelper;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service for Request List functionality
 * Handles filtering, pagination, and permission-based data access
 *
 * Requirements: 1, 2, 3, 4, 10
 */
public class RequestListService {

    private static final Logger logger = Logger.getLogger(RequestListService.class.getName());

    private final RequestDao requestDao;
    private final UserDao userDao;
    private final DepartmentDao departmentDao;

    // Constructors
    public RequestListService(RequestDao requestDao, UserDao userDao, DepartmentDao departmentDao) {
        this.requestDao = requestDao;
        this.userDao = userDao;
        this.departmentDao = departmentDao;
    }

    /**
     * Get filtered and paginated request list based on user permissions
     *
     * Requirements: 1, 2, 3, 4
     *
     * @param filter Filter criteria
     * @param currentUser Current logged-in user
     * @param position User's position (for permission checks)
     * @param contextPath Context path for building URLs
     * @return RequestListResult with filtered requests and pagination
     */
    public RequestListResult getRequestList(RequestListFilter filter, User currentUser,
                                           Position position, String contextPath) {
        logger.info(String.format("Getting request list: userId=%d, scope=%s, filter=%s",
                   currentUser.getId(), filter.getScope(), filter));

        try {
            // 1. Determine actual scope based on user permissions
            String actualScope = determineActualScope(filter.getScope(), position);
            filter.setScope(actualScope);

            // 2. Get user IDs based on scope
            List<Long> targetUserIds = getTargetUserIds(actualScope, currentUser);

            // 3. Calculate offset for pagination
            int offset = (filter.getPage() - 1) * filter.getPageSize();

            // 4. Fetch filtered requests from DAO
            List<RequestDto> requests = requestDao.findWithFilters(filter, targetUserIds,
                                                                   offset, filter.getPageSize());

            // 5. Get total count for pagination
            long totalCount = requestDao.countWithFilters(filter, targetUserIds);

            // 6. Set UI helper fields for each request
            for (RequestDto request : requests) {
                enrichRequestDto(request, currentUser, position, contextPath);
            }

            // 7. Create pagination metadata
            PaginationMetadata pagination = PaginationMetadata.create(
                filter.getPage(),
                filter.getPageSize(),
                totalCount
            );

            // 8. Build result based on scope
            RequestListResult result;
            if ("all".equals(actualScope)) {
                // Group by department for "all" scope (Requirement 3)
                Map<String, List<RequestDto>> requestsByDepartment = groupByDepartment(requests);
                result = RequestListResult.createGroupedByDepartment(requestsByDepartment, pagination);
            } else {
                // Flat list for "my" and "subordinate" scopes (Requirements 1, 2)
                result = RequestListResult.createFlatList(requests, pagination);
            }

            result.setAppliedFilters(filter);

            logger.info(String.format("Successfully retrieved %d requests (total: %d, pages: %d)",
                       requests.size(), totalCount, pagination.getTotalPages()));

            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error getting request list: userId=%d, scope=%s",
                      currentUser.getId(), filter.getScope()), e);
            throw new RuntimeException("Error retrieving request list", e);
        }
    }

    /**
     * Soft delete a request by changing status to CANCELLED
     *
     * Requirement: 10
     *
     * @param requestId Request ID to delete
     * @param currentUser Current logged-in user
     * @return true if successful, false otherwise
     */
    public boolean softDeleteRequest(Long requestId, User currentUser) {
        logger.info(String.format("Soft deleting request: requestId=%d, userId=%d",
                   requestId, currentUser.getId()));

        try {
            // 1. Fetch the request
            Optional<Request> requestOpt = requestDao.findById(requestId);
            if (!requestOpt.isPresent()) {
                logger.warning(String.format("Request not found: requestId=%d", requestId));
                return false;
            }

            Request request = requestOpt.get();

            // 2. Check if user can delete this request
            if (!RequestListPermissionHelper.canDeleteRequest(currentUser, request)) {
                logger.warning(String.format("User not authorized to delete request: userId=%d, requestId=%d",
                              currentUser.getId(), requestId));
                return false;
            }

            // 3. Perform soft delete
            boolean success = requestDao.softDelete(requestId);

            if (success) {
                logger.info(String.format("Successfully soft deleted request: requestId=%d, userId=%d",
                           requestId, currentUser.getId()));
            } else {
                logger.warning(String.format("Failed to soft delete request: requestId=%d", requestId));
            }

            return success;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error soft deleting request: requestId=%d, userId=%d",
                      requestId, currentUser.getId()), e);
            throw new RuntimeException("Error deleting request", e);
        }
    }

    // ==================== Private Helper Methods ====================

    /**
     * Determine actual scope based on user permissions
     * If user requests a scope they don't have access to, default to their highest available scope
     *
     * Requirement: 4
     */
    private String determineActualScope(String requestedScope, Position position) {
        Set<String> availableScopes = RequestListPermissionHelper.getAvailableScopes(position);

        // If requested scope is available, use it
        if (availableScopes.contains(requestedScope)) {
            return requestedScope;
        }

        // Otherwise, use default scope for user's position
        String defaultScope = RequestListPermissionHelper.getDefaultScope(position);
        logger.info(String.format("Requested scope '%s' not available, using default '%s'",
                   requestedScope, defaultScope));

        return defaultScope;
    }

    /**
     * Get target user IDs based on scope
     *
     * Requirements: 1, 2, 3
     */
    private List<Long> getTargetUserIds(String scope, User currentUser) {
        List<Long> userIds = new ArrayList<>();

        switch (scope) {
            case "my":
                // Only current user's requests (Requirement 1)
                userIds.add(currentUser.getId());
                break;

            case "subordinate":
                // Subordinates' requests (Requirement 2)
                try {
                    List<Long> subordinateIds = userDao.findSubordinateUserIds(currentUser.getId());
                    userIds.addAll(subordinateIds);
                    logger.info(String.format("Found %d subordinates for user %d",
                               subordinateIds.size(), currentUser.getId()));
                } catch (Exception e) {
                    logger.log(Level.WARNING, String.format("Error finding subordinates for user %d",
                              currentUser.getId()), e);
                }
                break;

            case "all":
                // All users - no filter needed (Requirement 3)
                // Empty list means no user filter in DAO query
                break;

            default:
                logger.warning(String.format("Unknown scope '%s', defaulting to 'my'", scope));
                userIds.add(currentUser.getId());
        }

        return userIds;
    }

    /**
     * Enrich RequestDto with UI helper fields
     *
     * Requirement: 10
     */
    private void enrichRequestDto(RequestDto dto, User currentUser, Position position, String contextPath) {
        // Set status badge CSS class
        dto.calculateStatusBadgeClass();

        // Build URLs
        dto.buildDetailUrl(contextPath);
        dto.buildUpdateUrl(contextPath);

        // Set permission flags
        Request request = dto.toEntity();
        dto.setCanUpdate(RequestListPermissionHelper.canUpdateRequest(currentUser, request));
        dto.setCanDelete(RequestListPermissionHelper.canDeleteRequest(currentUser, request));
        dto.setCanApprove(RequestListPermissionHelper.canApproveRequest(currentUser, request, position));
    }

    /**
     * Group requests by department name
     *
     * Requirement: 3
     */
    private Map<String, List<RequestDto>> groupByDepartment(List<RequestDto> requests) {
        // Use LinkedHashMap to preserve insertion order
        Map<String, List<RequestDto>> grouped = new LinkedHashMap<>();

        // Group by department name
        for (RequestDto request : requests) {
            String deptName = request.getDepartmentName();
            if (deptName == null || deptName.trim().isEmpty()) {
                deptName = "No Department";
            }

            grouped.computeIfAbsent(deptName, k -> new ArrayList<>()).add(request);
        }

        // Sort departments alphabetically
        Map<String, List<RequestDto>> sortedGrouped = new TreeMap<>(grouped);

        logger.info(String.format("Grouped %d requests into %d departments",
                   requests.size(), sortedGrouped.size()));

        return sortedGrouped;
    }
}
