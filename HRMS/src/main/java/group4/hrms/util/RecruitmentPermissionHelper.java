package group4.hrms.util;

/**
 * Permission helper utility for Recruitment Request functionality.
 * Determines user permissions based on positionId.
 */
public class RecruitmentPermissionHelper {
    // Position IDs for management roles
    private static final long POSITION_DEPARTMENT_MANAGER = 9;
    
    /**
     * Check if user can create recruitment requests based on their position.
     * Only Department Manager (9) can create recruitment requests.
     *
     * @param positionId User's position ID
     * @return true if user can create recruitment requests
     */
    public static boolean canCreateRecruitmentRequest(Long positionId) {
        if (positionId == null) {
            return false;
        }
        return positionId == POSITION_DEPARTMENT_MANAGER;
    }
    
    /**
     * Check if user can view recruitment requests based on their position.
     * Department Manager (9), HR Staff (8), and HR Manager (7) can view recruitment requests.
     *
     * @param positionId User's position ID
     * @return true if user can view recruitment requests
     */
    public static boolean canViewRecruitmentRequest(Long positionId) {
        if (positionId == null) {
            return false;
        }
        return positionId == POSITION_DEPARTMENT_MANAGER 
            || positionId == 8  // HR Staff
            || positionId == 7; // HR Manager
    }
    
    /**
     * Check if user can approve recruitment requests based on their position.
     * Only HR Manager (7) can approve recruitment requests.
     *
     * @param positionId User's position ID
     * @return true if user can approve recruitment requests
     */
    public static boolean canApproveRecruitmentRequest(Long positionId) {
        if (positionId == null) {
            return false;
        }
        return positionId == 7; // HR Manager
    }
}