package group4.hrms.util;

/**
 * Permission helper utility for Application Management functionality.
 * Determines user permissions based on positionId for job application management.
 * 
 * @author Group4
 */
public class ApplicationPermissionHelper {
    // Position IDs for HR roles
    private static final long POSITION_HR_MANAGER = 7;  // HRM
    private static final long POSITION_HR = 8;          // HR Staff
    private static final long POSITION_DEPARTMENT_MANAGER = 9;
    
    /**
     * Check if user can view applications based on their position.
     * HR Manager (7) and HR Staff (8) can view applications.
     * Department Manager (9) cannot view applications (they only handle recruitment requests).
     *
     * @param positionId User's position ID
     * @return true if user can view applications
     */
    public static boolean canViewApplications(Long positionId) {
        if (positionId == null) {
            return false;
        }
        return positionId == POSITION_HR_MANAGER || positionId == POSITION_HR;
    }
    
    /**
     * Check if user can manage (approve/reject) applications based on their position.
     * Both HR Manager (7) and HR Staff (8) can manage applications but with different levels.
     *
     * @param positionId User's position ID
     * @return true if user can manage applications
     */
    public static boolean canManageApplications(Long positionId) {
        if (positionId == null) {
            return false;
        }
        return positionId == POSITION_HR_MANAGER || positionId == POSITION_HR;
    }
    
    /**
     * Check if user can perform HR-level approval (first stage).
     * Only HR Staff (8) can perform HR-level approval.
     * This moves applications from "new" to "reviewing" status.
     *
     * @param positionId User's position ID
     * @return true if user can perform HR approval
     */
    public static boolean canPerformHrApproval(Long positionId) {
        if (positionId == null) {
            return false;
        }
        return positionId == POSITION_HR;
    }
    
    /**
     * Check if user can perform HRM-level approval (final stage).
     * Only HR Manager (7) can perform HRM-level approval.
     * This moves applications from "reviewing" to "approved" or "rejected" status.
     *
     * @param positionId User's position ID
     * @return true if user can perform HRM approval
     */
    public static boolean canPerformHrmApproval(Long positionId) {
        if (positionId == null) {
            return false;
        }
        return positionId == POSITION_HR_MANAGER;
    }
    
    /**
     * Check if user can view application details.
     * Same as canViewApplications - HR Manager (7) and HR Staff (8).
     *
     * @param positionId User's position ID
     * @return true if user can view application details
     */
    public static boolean canViewApplicationDetails(Long positionId) {
        return canViewApplications(positionId);
    }
    
    /**
     * Check if user can download application attachments (CV, CCCD).
     * Same as canViewApplications - HR Manager (7) and HR Staff (8).
     *
     * @param positionId User's position ID
     * @return true if user can download attachments
     */
    public static boolean canDownloadApplicationAttachments(Long positionId) {
        return canViewApplications(positionId);
    }
    
    /**
     * Get the approval role for the user based on position.
     * Used to determine which type of approval the user can perform.
     *
     * @param positionId User's position ID
     * @return "HR" for HR Staff, "HRM" for HR Manager, null for others
     */
    public static String getApprovalRole(Long positionId) {
        if (positionId == null) {
            return null;
        }
        
        if (positionId == POSITION_HR) {
            return "HR";
        } else if (positionId == POSITION_HR_MANAGER) {
            return "HRM";
        }
        
        return null;
    }
    
    /**
     * Check if user can approve application at current status.
     * Considers both user role and application current status.
     *
     * @param positionId User's position ID
     * @param applicationStatus Current status of the application
     * @return true if user can approve this application
     */
    public static boolean canApproveApplication(Long positionId, String applicationStatus) {
        if (positionId == null || applicationStatus == null) {
            return false;
        }
        
        // HR can approve applications with "new" status
        if (positionId == POSITION_HR && "new".equals(applicationStatus)) {
            return true;
        }
        
        // HRM can approve applications with "reviewing" or "new" status
        if (positionId == POSITION_HR_MANAGER && 
            ("reviewing".equals(applicationStatus) || "new".equals(applicationStatus))) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if user can reject application at current status.
     * Both HR and HRM can reject applications at appropriate stages.
     *
     * @param positionId User's position ID
     * @param applicationStatus Current status of the application
     * @return true if user can reject this application
     */
    public static boolean canRejectApplication(Long positionId, String applicationStatus) {
        if (positionId == null || applicationStatus == null) {
            return false;
        }
        
        // HR can reject applications with "new" status
        if (positionId == POSITION_HR && "new".equals(applicationStatus)) {
            return true;
        }
        
        // HRM can reject applications with "reviewing" or "new" status
        if (positionId == POSITION_HR_MANAGER && 
            ("reviewing".equals(applicationStatus) || "new".equals(applicationStatus))) {
            return true;
        }
        
        return false;
    }
}