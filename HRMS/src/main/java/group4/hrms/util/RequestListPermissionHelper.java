package group4.hrms.util;

import java.util.HashSet;
import java.util.Set;

import group4.hrms.model.Position;
import group4.hrms.model.Request;
import group4.hrms.model.User;

/**
 * Permission helper utility for Request List functionality.
 * Determines user permissions based on job_level from positions table.
 *
 * Job Level Mapping:
 * - 1: ADMIN (NO ACCESS to request system)
 * - 2: HR_MANAGER (Full access)
 * - 3: HR_STAFF (Full access)
 * - 4: DEPT_MANAGER (Subordinate + own requests)
 * - 5: STAFF (Own requests only)
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
     * ADMIN (level 1) has NO ACCESS to request system.
     *
     * @param position User's position (contains job_level)
     * @return Set of available scope strings: "my", "subordinate", "all"
     */
    public static Set<String> getAvailableScopes(Position position) {
        Set<String> scopes = new HashSet<>();

        if (position == null || position.getJobLevel() == null) {
            // Everyone can see their own requests
            scopes.add("my");
            return scopes;
        }

        int jobLevel = position.getJobLevel();

        // ADMIN has NO ACCESS to request system
        if (jobLevel == JOB_LEVEL_ADMIN) {
            return scopes; // Return empty set
        }

        // Everyone else can see their own requests
        scopes.add("my");

        // HR_MANAGER, HR_STAFF, DEPT_MANAGER can see subordinate requests
        if (jobLevel >= JOB_LEVEL_HR_MANAGER && jobLevel <= JOB_LEVEL_DEPT_MANAGER) {
            scopes.add("subordinate");
        }

        // HR_MANAGER, HR_STAFF can see all requests
        if (jobLevel >= JOB_LEVEL_HR_MANAGER && jobLevel <= JOB_LEVEL_HR_STAFF) {
            scopes.add("all");
        }

        return scopes;
    }

    /**
     * Get the default scope for a user based on their job level.
     * ADMIN (level 1) has NO ACCESS to request system.
     *
     * @param position User's position (contains job_level)
     * @return Default scope string: "my", "subordinate", or "all"
     */
    public static String getDefaultScope(Position position) {
        if (position == null || position.getJobLevel() == null) {
            return "my";
        }

        int jobLevel = position.getJobLevel();

        // ADMIN has NO ACCESS to request system
        if (jobLevel == JOB_LEVEL_ADMIN) {
            return null; // No default scope for admin
        }

        // HR_MANAGER defaults to "all"
        if (jobLevel == JOB_LEVEL_HR_MANAGER) {
            return "all";
        }

        // HR_STAFF and DEPT_MANAGER default to "subordinate"
        if (jobLevel >= JOB_LEVEL_HR_STAFF && jobLevel <= JOB_LEVEL_DEPT_MANAGER) {
            return "subordinate";
        }

        // STAFF defaults to "my"
        return "my";
    }

    /**
     * Check if a user can export requests based on their job level.
     * Only HR staff and above can export (ADMIN excluded).
     *
     * @param position User's position (contains job_level)
     * @return true if user can export, false otherwise
     */
    public static boolean canExport(Position position) {
        if (position == null || position.getJobLevel() == null) {
            return false;
        }

        int jobLevel = position.getJobLevel();

        // ADMIN has NO ACCESS
        if (jobLevel == JOB_LEVEL_ADMIN) {
            return false;
        }

        // HR_MANAGER, HR_STAFF can export
        return jobLevel >= JOB_LEVEL_HR_MANAGER && jobLevel <= JOB_LEVEL_HR_STAFF;
    }

    /**
     * Check if a user can delete a specific request.
     * Rules:
     * - Must be the owner of the request
     * - Request must be APPROVED or REJECTED (not PENDING or CANCELLED)
     *
     * @param user    Current user
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
     * @param user    Current user
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
     * Check if a user can view a specific request.
     * Rules:
     * - ADMIN has NO ACCESS to request system
     * - User can view their own requests
     * - Managers (DEPT_MANAGER or above) can view subordinate requests
     * - HR staff (HR_STAFF or above) can view all requests
     *
     * @param user     Current user
     * @param request  Request to check
     * @param position User's position
     * @return true if user can view the request, false otherwise
     */
    public static boolean canViewRequest(User user, Request request, Position position) {
        if (user == null || request == null) {
            return false;
        }

        // Check if user has permission
        if (position == null || position.getJobLevel() == null) {
            return false;
        }

        int jobLevel = position.getJobLevel();

        // ADMIN has NO ACCESS to request system
        if (jobLevel == JOB_LEVEL_ADMIN) {
            return false;
        }

        // User can always view their own requests
        if (user.getId().equals(request.getCreatedByUserId())) {
            return true;
        }

        // HR staff and above can view all requests
        if (jobLevel >= JOB_LEVEL_HR_MANAGER && jobLevel <= JOB_LEVEL_HR_STAFF) {
            return true;
        }

        // Department managers can view subordinate requests
        // This would require checking if the request creator is a subordinate
        // For now, we allow DEPT_MANAGER to view (subordinate check should be done in
        // service layer)
        if (jobLevel == JOB_LEVEL_DEPT_MANAGER) {
            return true;
        }

        return false;
    }

    /**
     * Check if a user can approve/reject a specific request.
     * Rules:
     * - Must NOT be the creator of the request (cannot approve own request)
     * - SPECIAL CASE: If manager created OT request for employee, employee CAN
     * approve
     * - Request must be PENDING OR APPROVED (HR can re-approve, manager can
     * override reject)
     * - User must be a manager (DEPT_MANAGER or above) OR be the employee for whom
     * request was created
     * - If request is APPROVED, only HR_STAFF or above can re-approve (OR manager
     * who created it can reject)
     * - Request must NOT be in effect yet (effective date > today)
     *
     * @param user     Current user
     * @param request  Request to check
     * @param position User's position
     * @return true if user can approve/reject the request, false otherwise
     */
    public static boolean canApproveRequest(User user, Request request, Position position) {
        return canApproveRequest(user, request, position, null);
    }

    /**
     * Check if a user can approve/reject a specific request (with accountId).
     * ADMIN (level 1) has NO ACCESS to request system.
     *
     * @param user             Current user
     * @param request          Request to check
     * @param position         User's position
     * @param currentAccountId Current user's account ID (optional, for checking
     *                         creator)
     * @return true if user can approve/reject the request, false otherwise
     */
    public static boolean canApproveRequest(User user, Request request, Position position, Long currentAccountId) {
        if (user == null || request == null) {
            return false;
        }

        // Check if user has permission
        if (position == null || position.getJobLevel() == null) {
            return false;
        }

        int jobLevel = position.getJobLevel();

        // ADMIN has NO ACCESS to request system
        if (jobLevel == JOB_LEVEL_ADMIN) {
            return false;
        }

        // Check if request is still valid for approval (not yet in effect)
        if (!isRequestValidForApproval(request)) {
            return false;
        }

        // SPECIAL RULE: ADJUSTMENT_REQUEST (Appeal) - Only HR/HRM can approve
        // Department Manager CANNOT approve appeal requests
        if (request.getRequestTypeId() != null && request.getRequestTypeId() == 8L) {
            // Only HR_MANAGER (level 2) and HR_STAFF (level 3) can approve
            // DEPT_MANAGER (level 4) and below CANNOT approve
            if (jobLevel < JOB_LEVEL_HR_MANAGER || jobLevel > JOB_LEVEL_HR_STAFF) {
                return false;
            }

            // Cannot approve own request
            if (user.getId().equals(request.getCreatedByUserId())) {
                return false;
            }

            // Can approve PENDING requests
            return request.isPending();
        }

        // SPECIAL RULE: RECRUITMENT_REQUEST - Only HR/HRM can approve
        // Department Manager CANNOT approve recruitment requests
        if (request.getRequestTypeId() != null && request.getRequestTypeId() == 9L) {
            // Only HR_MANAGER (level 2) and HR_STAFF (level 3) can approve
            // DEPT_MANAGER (level 4) and below CANNOT approve
            if (jobLevel < JOB_LEVEL_HR_MANAGER || jobLevel > JOB_LEVEL_HR_STAFF) {
                return false;
            }

            // Cannot approve own request
            if (user.getId().equals(request.getCreatedByUserId())) {
                return false;
            }

            // Can approve PENDING requests
            return request.isPending();
        }

        // SPECIAL CASE: OT Request created by manager for employee
        // Check if this is an OT request with createdByManager flag
        // Only check OT detail if this is actually an OVERTIME_REQUEST (type_id=7)
        boolean isOTCreatedByManager = false;
        if (request.getRequestTypeId() != null && request.getRequestTypeId() == 7L) {
            if (request.getOtDetail() != null
                    && request.getOtDetail().getCreatedByManager() != null
                    && request.getOtDetail().getCreatedByManager()) {
                isOTCreatedByManager = true;
                System.out.println("[DEBUG] OT created by manager detected. RequestId=" + request.getId()
                        + ", createdByUserId=" + request.getCreatedByUserId()
                        + ", createdByAccountId=" + request.getCreatedByAccountId()
                        + ", currentUserId=" + user.getId());
            }
        }

        // If OT created by manager for employee, employee can approve it
        if (isOTCreatedByManager && user.getId().equals(request.getCreatedByUserId())) {
            // Employee can approve PENDING request created for them
            return request.isPending();
        }

        // Cannot approve own self-created request
        if (user.getId().equals(request.getCreatedByUserId()) && !isOTCreatedByManager) {
            return false;
        }

        // Must be a manager (DEPT_MANAGER or above, excluding ADMIN)
        if (jobLevel > JOB_LEVEL_DEPT_MANAGER) {
            return false;
        }

        // SPECIAL RULE for manager-created OT: Manager CANNOT approve when PENDING
        // Manager must wait for employee to approve first
        if (isOTCreatedByManager && request.isPending()) {
            // Only employee can approve PENDING status
            return false;
        }

        // Can approve PENDING requests (normal flow for non-manager-created requests)
        if (request.isPending()) {
            return true;
        }

        // SPECIAL: Manager-created OT override rules
        // After employee approves, managers can override based on hierarchy:
        // - Only managers at same level as creator OR HIGHER can override
        // - Once someone overrides, only HIGHER level managers can override again
        // - Cannot override your own decision
        if (request.isApproved() && isOTCreatedByManager) {
            Long currentApprover = request.getCurrentApproverAccountId();
            Long creatorAccountId = request.getCreatedByAccountId();

            System.out.println("[DEBUG] Manager-created OT check: requestId=" + request.getId()
                    + ", currentApprover=" + currentApprover
                    + ", creatorAccountId=" + creatorAccountId
                    + ", currentAccountId=" + currentAccountId
                    + ", currentUserJobLevel=" + jobLevel);

            // Rule 1: Cannot override your own decision
            if (currentAccountId != null && currentApprover != null
                    && currentAccountId.equals(currentApprover)) {
                System.out.println("[DEBUG] User is the last approver (accountId=" + currentAccountId
                        + "), cannot override own decision");
                return false;
            }

            // Rule 2: Must be at manager level (DEPT_MANAGER or above, excluding ADMIN)
            if (jobLevel > JOB_LEVEL_DEPT_MANAGER) {
                System.out.println("[DEBUG] User is not a manager, cannot override");
                return false;
            }

            // Rule 3: Check if current approver is a manager (not employee)
            // If currentApprover != creatorAccountId and != employee, it means a manager
            // has already reviewed
            // We need to query the approver's job level to compare
            if (currentApprover != null && !currentApprover.equals(creatorAccountId)) {
                // Try to get approver's job level
                Integer approverJobLevel = getJobLevelFromAccountId(currentApprover);

                System.out.println("[DEBUG] Previous approver jobLevel=" + approverJobLevel
                        + ", current user jobLevel=" + jobLevel);

                if (approverJobLevel != null) {
                    // Only allow override if current user has HIGHER authority (lower job level
                    // number)
                    if (jobLevel >= approverJobLevel) {
                        System.out.println(
                                "[DEBUG] User job level is not higher than previous approver, cannot override");
                        return false;
                    }
                }
            }

            // Rule 4: Allow override if user is creator (manager who made the request) or
            // superior
            // Creator can override employee's approval (first override ONLY)
            if (currentAccountId != null && currentAccountId.equals(creatorAccountId)) {
                // Double check: Creator has NOT already approved
                if (currentApprover != null && currentApprover.equals(creatorAccountId)) {
                    System.out.println("[DEBUG] Creator has already approved, cannot override again");
                    return false;
                }
                System.out.println("[DEBUG] User is creator manager, can override employee's approval");
                return true;
            }

            // Superior manager can override (already passed hierarchy check in Rule 3)
            System.out.println("[DEBUG] User is superior manager, can override");
            return true;
        }

        // For non-OT APPROVED requests (Leave, etc.), only allow override if:
        // 1. User is HR_STAFF or above (excluding ADMIN), AND
        // 2. User has higher authority than the current approver
        if (request.isApproved() && jobLevel >= JOB_LEVEL_HR_MANAGER && jobLevel <= JOB_LEVEL_HR_STAFF) {
            Long currentApprover = request.getCurrentApproverAccountId();

            // Cannot override your own decision
            if (currentAccountId != null && currentApprover != null
                    && currentAccountId.equals(currentApprover)) {
                return false;
            }

            // If there's a current approver, check hierarchy
            if (currentApprover != null) {
                Integer approverJobLevel = getJobLevelFromAccountId(currentApprover);
                if (approverJobLevel != null) {
                    // Only allow if current user has higher authority (lower job level)
                    return jobLevel < approverJobLevel;
                }
            }

            // No approver yet, or couldn't determine level - allow HR to approve
            return true;
        }

        return false;
    }

    /**
     * Check if request is still valid for approval (not yet in effect).
     * For OT and Leave requests: effective date must be in the future (> today).
     * For other request types: always allow approval.
     *
     * @param request Request to check
     * @return true if request can still be approved, false if already in effect
     */
    private static boolean isRequestValidForApproval(Request request) {
        if (request == null) {
            return false; // No request, cannot approve
        }

        try {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate effectiveDate = null;

            // Check OT request (type_id=7)
            if (request.getRequestTypeId() != null && request.getRequestTypeId() == 7L
                    && request.getOtDetail() != null) {
                String otDateStr = request.getOtDetail().getOtDate();
                if (otDateStr != null && !otDateStr.isEmpty()) {
                    effectiveDate = java.time.LocalDate.parse(otDateStr);
                }
            }

            // Check Leave request (type_id=6)
            if (request.getRequestTypeId() != null && request.getRequestTypeId() == 6L
                    && request.getLeaveDetail() != null) {
                String startDateStr = request.getLeaveDetail().getStartDate();
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    // Extract date part (yyyy-MM-dd) if datetime format
                    if (startDateStr.length() >= 10) {
                        effectiveDate = java.time.LocalDate.parse(startDateStr.substring(0, 10));
                    }
                }
            }

            // Check Appeal request (type_id=8)
            if (request.getRequestTypeId() != null && request.getRequestTypeId() == 8L
                    && request.getAppealDetail() != null) {
                java.util.List<String> attendanceDates = request.getAppealDetail().getAttendanceDates();
                if (attendanceDates != null && !attendanceDates.isEmpty()) {
                    // Use the first attendance date as effective date
                    effectiveDate = java.time.LocalDate.parse(attendanceDates.get(0));
                }
            }

            // If effective date found, check if it's in the future
            if (effectiveDate != null) {
                return effectiveDate.isAfter(today);
            }

            // For other request types or if no effective date, allow approval
            return true;

        } catch (Exception e) {
            // If date parsing fails, allow approval (fail-safe)
            return true;
        }
    }

    /**
     * Get job level from account ID by querying user and position
     * Returns null if not found or error occurs
     *
     * @param accountId Account ID to lookup
     * @return Job level (1-5) or null if not found
     */
    private static Integer getJobLevelFromAccountId(Long accountId) {
        if (accountId == null) {
            return null;
        }

        try {
            // Query account to get user_id
            group4.hrms.dao.AccountDao accountDao = new group4.hrms.dao.AccountDao();
            java.util.Optional<group4.hrms.model.Account> accountOpt = accountDao.findById(accountId);

            if (!accountOpt.isPresent()) {
                return null;
            }

            Long userId = accountOpt.get().getUserId();
            if (userId == null) {
                return null;
            }

            // Query user to get position_id
            group4.hrms.dao.UserDao userDao = new group4.hrms.dao.UserDao();
            java.util.Optional<User> userOpt = userDao.findById(userId);

            if (!userOpt.isPresent() || userOpt.get().getPositionId() == null) {
                return null;
            }

            // Query position to get job_level
            group4.hrms.dao.PositionDao positionDao = new group4.hrms.dao.PositionDao();
            java.util.Optional<Position> positionOpt = positionDao.findById(userOpt.get().getPositionId());

            if (positionOpt.isPresent() && positionOpt.get().getJobLevel() != null) {
                return positionOpt.get().getJobLevel();
            }

            return null;

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to get job level for accountId " + accountId + ": " + e.getMessage());
            return null;
        }
    }
}
