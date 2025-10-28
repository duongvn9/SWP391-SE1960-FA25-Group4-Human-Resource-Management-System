package group4.hrms.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import group4.hrms.dao.AccountDao;
import group4.hrms.dao.PositionDao;
import group4.hrms.dao.UserDao;
import group4.hrms.model.Account;
import group4.hrms.model.Position;
import group4.hrms.model.User;

/**
 * Service for determining request approvers
 * Handles logic for finding appropriate approver based on organizational hierarchy
 */
public class ApproverService {
    private static final Logger logger = Logger.getLogger(ApproverService.class.getName());

    private final UserDao userDao;
    private final AccountDao accountDao;
    private final PositionDao positionDao;

    public ApproverService(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.positionDao = new PositionDao();
    }

    /**
     * Find approver for a request
     * Returns null if user is top-level (no higher manager exists)
     *
     * @param userId User creating the request
     * @return Account ID of approver, or null if top-level
     */
    public Long findApprover(Long userId) {
        logger.info(String.format("Finding approver for userId=%d", userId));

        try {
            // Get user information
            Optional<User> userOpt = userDao.findById(userId);
            if (!userOpt.isPresent()) {
                logger.warning(String.format("User not found: userId=%d", userId));
                return null;
            }

            User user = userOpt.get();
            Long departmentId = user.getDepartmentId();
            Long positionId = user.getPositionId();

            // Get user's job level from position
            Integer userJobLevel = null;
            if (positionId != null) {
                Optional<Position> posOpt = positionDao.findById(positionId);
                if (posOpt.isPresent()) {
                    userJobLevel = posOpt.get().getJobLevel();
                }
            }

            if (userJobLevel == null) {
                logger.warning(String.format("User has no job level: userId=%d", userId));
                return null;
            }

            logger.fine(String.format("User info: userId=%d, jobLevel=%d, departmentId=%d",
                       userId, userJobLevel, departmentId));

            // STEP 1: Find users in same department with HIGHER job level
            // Higher job level = lower number (1 is HRM, 2 is HR, 3 is Manager, 4 is Staff, etc.)
            List<User> sameDepUsers = userDao.findByDepartmentId(departmentId);

            User approver = null;
            int lowestJobLevel = Integer.MAX_VALUE; // We want the lowest number (highest position)

            for (User potentialApprover : sameDepUsers) {
                // Skip self
                if (potentialApprover.getId().equals(userId)) {
                    continue;
                }

                // Get approver's job level from position
                Integer approverJobLevel = null;
                if (potentialApprover.getPositionId() != null) {
                    Optional<Position> appPosOpt = positionDao.findById(potentialApprover.getPositionId());
                    if (appPosOpt.isPresent()) {
                        approverJobLevel = appPosOpt.get().getJobLevel();
                    }
                }

                if (approverJobLevel == null) {
                    continue;
                }

                // Check if this user has higher position (lower job level number)
                if (approverJobLevel < userJobLevel && approverJobLevel < lowestJobLevel) {
                    approver = potentialApprover;
                    lowestJobLevel = approverJobLevel;
                }
            }

            if (approver != null) {
                // Get approver's account ID
                Long approverAccountId = getAccountIdForUser(approver.getId());
                if (approverAccountId != null) {
                    logger.info(String.format("Found approver in same department: userId=%d, name=%s, jobLevel=%d, accountId=%d for requestor userId=%d",
                               approver.getId(), approver.getFullName(), lowestJobLevel, approverAccountId, userId));
                    return approverAccountId;
                }
            }

            // STEP 2: No approver in same department
            // If user has job_level <= 2 (ADMIN or HR_MANAGER - highest in workflow), return null for auto-approve
            // Note: ADMIN (job_level=1) doesn't participate in Leave/OT workflow
            // HR_MANAGER (job_level=2) is the highest in Leave/OT workflow
            if (userJobLevel != null && userJobLevel <= 2) {
                logger.info(String.format("User is top-level (job_level=%d) - auto-approve: userId=%d", userJobLevel, userId));
                return null;
            }

            // STEP 3: User is Department Manager/Head but not HRM
            // Find HR/HRM across all departments to approve
            logger.info(String.format("Looking for HR/HRM approver for department manager: userId=%d", userId));

            try {
                List<User> allUsers = userDao.findAll();
                User hrApprover = null;
                int hrLowestJobLevel = Integer.MAX_VALUE;

                for (User potentialHR : allUsers) {
                    // Skip self
                    if (potentialHR.getId().equals(userId)) {
                        continue;
                    }

                    // Get HR's job level from position
                    Integer hrJobLevel = null;
                    if (potentialHR.getPositionId() != null) {
                        Optional<Position> hrPosOpt = positionDao.findById(potentialHR.getPositionId());
                        if (hrPosOpt.isPresent()) {
                            hrJobLevel = hrPosOpt.get().getJobLevel();
                        }
                    }

                    if (hrJobLevel == null) {
                        continue;
                    }

                    // Find HR/HRM (job_level 1 or 2) who can approve cross-department
                    if (hrJobLevel <= 2 && hrJobLevel < hrLowestJobLevel) {
                        hrApprover = potentialHR;
                        hrLowestJobLevel = hrJobLevel;
                    }
                }

                if (hrApprover != null) {
                    Long hrAccountId = getAccountIdForUser(hrApprover.getId());
                    if (hrAccountId != null) {
                        logger.info(String.format("Found HR/HRM approver: userId=%d, name=%s, jobLevel=%d, accountId=%d for requestor userId=%d",
                                   hrApprover.getId(), hrApprover.getFullName(), hrLowestJobLevel, hrAccountId, userId));
                        return hrAccountId;
                    }
                }
            } catch (Exception e) {
                logger.warning(String.format("Error finding HR/HRM approver: %s", e.getMessage()));
            }

            // STEP 4: Fallback - no approver found at all (shouldn't happen in normal setup)
            logger.warning(String.format("No approver found for userId=%d - this may indicate setup issue", userId));
            return null;

        } catch (Exception e) {
            logger.severe(String.format("Error finding approver for userId=%d: %s", userId, e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if user is top-level (has no higher manager)
     *
     * @param userId User ID to check
     * @return true if top-level, false otherwise
     */
    public boolean isTopLevel(Long userId) {
        return findApprover(userId) == null;
    }

    /**
     * Get account ID for a given user ID
     *
     * @param userId User ID
     * @return Account ID, or null if not found
     */
    private Long getAccountIdForUser(Long userId) {
        try {
            List<Account> accounts = accountDao.findAll();
            for (Account account : accounts) {
                if (account.getUserId() != null && account.getUserId().equals(userId)) {
                    return account.getId();
                }
            }
            logger.warning(String.format("No account found for userId=%d", userId));
            return null;
        } catch (Exception e) {
            logger.severe(String.format("Error finding account for userId=%d: %s", userId, e.getMessage()));
            return null;
        }
    }
}

