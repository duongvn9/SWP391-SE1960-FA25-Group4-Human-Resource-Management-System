package group4.hrms.util;

/**
 * Permission helper utility for Job Posting functionality.
 * Determines user permissions based on positionId.
 */
public class JobPostingPermissionHelper {
    // Position IDs for HR roles
    private static final long POSITION_HR_MANAGER = 7;
    private static final long POSITION_HR = 8;
    
    /**
     * Check if user can create/edit job postings based on their position.
     * Only HR (8) and HR Manager (7) can create/edit job postings.
     *
     * @param positionId User's position ID
     * @return true if user can create/edit job postings
     */
    public static boolean canManageJobPosting(Long positionId) {
        if (positionId == null) {
            return false;
        }
        return positionId == POSITION_HR_MANAGER || positionId == POSITION_HR;
    }
    
    /**
     * Check if user can approve job postings based on their position.
     * Only HR Manager (7) can approve job postings.
     *
     * @param positionId User's position ID
     * @return true if user can approve job postings
     */
    public static boolean canApproveJobPosting(Long positionId) {
        if (positionId == null) {
            return false;
        }
        return positionId == POSITION_HR_MANAGER;
    }
}