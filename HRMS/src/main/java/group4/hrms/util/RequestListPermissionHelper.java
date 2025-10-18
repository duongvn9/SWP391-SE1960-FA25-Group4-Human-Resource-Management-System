package group4.hrms.util;

import group4.hrms.model.Position;
import group4.hrms.model.Request;
import group4.hrms.model.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Permission helper utility for Request List functionality.
 * Determines user permissions based on job_level from positions table.
 *
 * Job Level Mapping:
 * - 1: ADMIN
 * - 2: HR_MANAGER
 * - 3: HR_STAFF
 * - 4: DEPT_MANAGER
 * - 5: STAFF
 */
public class RequestListPermissionHelper {

    // Job level constants
    private static final int JOB_LEVEL_ADMIN = 1;
    private static final int JOB_LEVEL_HR_MANAGER = 2;
    private static final int JOB_LEVEL_HR_STAFF = 3;
    private static final int JOB_LEVEL_DEPT_MANAGER = 4;
    private static final int JOB_LEVEL_STAFF = 5;

    /**
     * Get available scopes for a user based on their job level.
     *
     * @param position User's position (contains job_level)
     * @return Set of available scope strings: "my", "subordinate", "all"
     */
    public static Set<String> getAvailableScopes(Position position) {
        Set<String> scopes = new HashSet<>();

        // Everyone can see their own requests
        scopes.add("my");

        if (position == null || position.getJobLevel() == null) {
            return scopes;
        }

        int jobLevel = position.getJobLevel();

        // ADMIN, HR_MANAGER, HR_STAFF, DEPT_MANAGER can see subordinate requests
        if (jobLevel <= JOB_LEVEL_DEPT_MANAGER) {
            scopes.add("subordinate");
        }

        // ADMIN, HR_MANAGER, HR_STAFF can see all requests
        if (jobLevel <= JOB_LEVEL_HR_STAFF) {
            scopes.add("all");
        }

        return scopes;
    }

    /**
     * Get the default scope for a user based on their job level.
     *
     * @param position User's position (contains job_level)
     * @return Default scope string: "my", "subordinate", or "all"
     */
    public static String getDefaultScope(Position position) {
        if (position == null || position.getJobLevel() == null) {
            return "my";
        }

        int jobLevel = position.getJobLevel();

        // ADMIN and HR_MANAGER default to "all"
        if (jobLevel <= JOB_LEVEL_HR_MANAGER) {
            return "all";
        }

        // HR_STAFF and DEPT_MANAGER default to "subordinate"
        if (jobLevel <= JOB_LEVEL_DEPT_MANAGER) {
            return "subordinate";
        }

        // STAFF defaults to "my"
        return "my";
    }

    /**
     * Check if a user can export requests based on their job level.
     * Only HR staff and above can export.
     *
     * @param position User's position (contains job_level)
     * @return true if user can export, false otherwise
     */
    public static boolean canExport(Position position) {
        if (position == null || position.getJobLevel() == null) {
            return false;
        }

        // ADMIN, HR_MANAGER, HR_STAFF can export
        return position.getJobLevel() <= JOB_LEVEL_HR_STAFF;
    }

    /**
     * Check if a user can delete a specific request.
     * Rules:
     * - Must be the owner of the request
     * - Request must be APPROVED or REJECTED (not PENDING or CANCELLED)
     *
     * @param user Current user
     * @param request Request to check
     * @return true if user can delete the request, false otherwise
     */
    public static boolean canDeleteRequest(User user, Request request) {
        if (user == null || request == null) {
            return false;
        }

        // Must be the owner of the request
        if (!user.getId().equals(request.getCreatedByUserId())) {
            return false;
        }

        // Can only delete APPROVED or REJECTED requests
        return request.isApproved() || request.isRejected();
    }

    /**
     * Check if a user can update a specific request.
     * Rules:
     * - Must be the owner of the request
     * - Request must be PENDING
     *
     * @param user Current user
     * @param request Request to check
     * @return true if user can update the request, false otherwise
     */
    public static boolean canUpdateRequest(User user, Request request) {
        if (user == null || request == null) {
            return false;
        }

        // Must be the owner of the request
        if (!user.getId().equals(request.getCreatedByUserId())) {
            return false;
        }

        // Can only update PENDING requests
        return request.isPending();
    }

    /**
     * Check if a user can approve/reject a specific request.
     * Rules:
     * - Must NOT be the owner of the request
     * - Request must be PENDING OR APPROVED (HR can re-approve)
     * - User must be a manager (DEPT_MANAGER or above)
     * - If request is APPROVED, only HR_STAFF or above can re-approve
     *
     * @param user Current user
     * @param request Request to check
     * @param position User's position
     * @return true if user can approve/reject the request, false otherwise
     */
    public static boolean canApproveRequest(User user, Request request, Position position) {
        if (user == null || request == null) {
            return false;
        }

        // Cannot approve own request
        if (user.getId().equals(request.getCreatedByUserId())) {
            return false;
        }

        // Must be a manager (DEPT_MANAGER or above)
        if (position == null || position.getJobLevel() == null) {
            return false;
        }

        int jobLevel = position.getJobLevel();
        if (jobLevel > JOB_LEVEL_DEPT_MANAGER) {
            return false;
        }

        // Can approve PENDING requests
        if (request.isPending()) {
            return true;
        }

        // Can re-approve APPROVED requests if user is HR_STAFF or above
        if (request.isApproved() && jobLevel <= JOB_LEVEL_HR_STAFF) {
            return true;
        }

        return false;
    }
}
