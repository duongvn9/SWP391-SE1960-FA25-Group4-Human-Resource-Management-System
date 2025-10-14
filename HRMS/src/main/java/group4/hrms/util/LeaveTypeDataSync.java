package group4.hrms.util;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.RequestTypeDao;
import group4.hrms.model.RequestType;

/**
 * Utility class to sync request types and leave types from JSON config to
 * database
 * - request_types: 4 main request categories
 * - leave_types: specific leave type configurations
 */
public class LeaveTypeDataSync {
    private static final Logger logger = Logger.getLogger(LeaveTypeDataSync.class.getName());

    /**
     * Sync leave types from JSON config to database
     * This method should be called during application startup or migration
     */
    public static void syncLeaveTypesToDatabase(RequestTypeDao requestTypeDao) {
        logger.info("Starting leave types data sync to database...");

        try {
            // Define leave types based on leave-rules.json
            LeaveTypeData[] leaveTypes = {
                    new LeaveTypeData("ANNUAL", "Annual Leave", "LEAVE", true, false, 21, "MANAGER_THEN_HR", true),
                    new LeaveTypeData("SICK", "Sick Leave", "LEAVE", true, false, 30, "MANAGER_THEN_HR", true),
                    new LeaveTypeData("MATERNITY", "Maternity Leave", "LEAVE", true, true, 180, "MANAGER_THEN_HR",
                            true),
                    new LeaveTypeData("EMERGENCY", "Emergency Leave", "LEAVE", true, false, 5, "MANAGER_THEN_HR", true),
                    new LeaveTypeData("PERSONAL", "Personal Leave", "LEAVE", true, false, 10, "MANAGER_THEN_HR", true)
            };

            int syncedCount = 0;
            int updatedCount = 0;

            for (LeaveTypeData leaveTypeData : leaveTypes) {
                RequestType existingType = requestTypeDao.findByCode(leaveTypeData.code);

                if (existingType == null) {
                    // Create new leave type
                    RequestType newType = createRequestType(leaveTypeData);
                    requestTypeDao.save(newType);
                    syncedCount++;
                    logger.info("Created new leave type: " + leaveTypeData.code);
                } else {
                    // Update existing leave type if needed
                    boolean needsUpdate = false;

                    if (!leaveTypeData.name.equals(existingType.getName())) {
                        existingType.setName(leaveTypeData.name);
                        needsUpdate = true;
                    }

                    if (!leaveTypeData.category.equals(existingType.getCategory())) {
                        existingType.setCategory(leaveTypeData.category);
                        needsUpdate = true;
                    }

                    if (leaveTypeData.requiresApproval != existingType.isRequiresApproval()) {
                        existingType.setRequiresApproval(leaveTypeData.requiresApproval);
                        needsUpdate = true;
                    }

                    if (leaveTypeData.requiresAttachment != existingType.isRequiresAttachment()) {
                        existingType.setRequiresAttachment(leaveTypeData.requiresAttachment);
                        needsUpdate = true;
                    }

                    if (!leaveTypeData.maxDays.equals(existingType.getMaxDays())) {
                        existingType.setMaxDays(leaveTypeData.maxDays);
                        needsUpdate = true;
                    }

                    if (!leaveTypeData.approvalWorkflow.equals(existingType.getApprovalWorkflow())) {
                        existingType.setApprovalWorkflow(leaveTypeData.approvalWorkflow);
                        needsUpdate = true;
                    }

                    if (leaveTypeData.isActive != existingType.isActive()) {
                        existingType.setActive(leaveTypeData.isActive);
                        needsUpdate = true;
                    }

                    if (needsUpdate) {
                        requestTypeDao.save(existingType);
                        updatedCount++;
                        logger.info("Updated leave type: " + leaveTypeData.code);
                    }
                }
            }

            logger.info("Leave types sync completed. Created: " + syncedCount + ", Updated: " + updatedCount);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error syncing leave types to database", e);
            throw new RuntimeException("Failed to sync leave types to database", e);
        }
    }

    private static RequestType createRequestType(LeaveTypeData data) {
        RequestType requestType = new RequestType();
        requestType.setCode(data.code);
        requestType.setName(data.name);
        requestType.setDescription("Auto-generated leave type: " + data.name);
        requestType.setCategory(data.category);
        requestType.setRequiresApproval(data.requiresApproval);
        requestType.setRequiresAttachment(data.requiresAttachment);
        requestType.setMaxDays(data.maxDays);
        requestType.setApprovalWorkflow(data.approvalWorkflow);
        requestType.setActive(data.isActive);

        LocalDateTime now = LocalDateTime.now();
        requestType.setCreatedAt(now);
        requestType.setUpdatedAt(now);

        return requestType;
    }

    /**
     * Data class to hold leave type information from JSON config
     */
    private static class LeaveTypeData {
        final String code;
        final String name;
        final String category;
        final boolean requiresApproval;
        final boolean requiresAttachment;
        final Integer maxDays;
        final String approvalWorkflow;
        final boolean isActive;

        LeaveTypeData(String code, String name, String category, boolean requiresApproval,
                boolean requiresAttachment, Integer maxDays, String approvalWorkflow, boolean isActive) {
            this.code = code;
            this.name = name;
            this.category = category;
            this.requiresApproval = requiresApproval;
            this.requiresAttachment = requiresAttachment;
            this.maxDays = maxDays;
            this.approvalWorkflow = approvalWorkflow;
            this.isActive = isActive;
        }
    }

    /**
     * Method to verify leave types exist in database
     */
    public static boolean verifyLeaveTypesExist(RequestTypeDao requestTypeDao) {
        try {
            String[] requiredCodes = { "ANNUAL", "SICK", "MATERNITY", "EMERGENCY", "PERSONAL" };

            for (String code : requiredCodes) {
                RequestType type = requestTypeDao.findByCode(code);
                if (type == null) {
                    logger.warning("Leave type not found in database: " + code);
                    return false;
                }
            }

            logger.info("All required leave types exist in database");
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error verifying leave types in database", e);
            return false;
        }
    }

    /**
     * Method to get leave type configuration from database for validation
     */
    public static void printLeaveTypeConfiguration(RequestTypeDao requestTypeDao) {
        try {
            System.out.println("=== LEAVE TYPES IN DATABASE ===");

            String[] codes = { "ANNUAL", "SICK", "MATERNITY", "EMERGENCY", "PERSONAL" };

            for (String code : codes) {
                RequestType type = requestTypeDao.findByCode(code);
                if (type != null) {
                    System.out.println(code + ":");
                    System.out.println("  Name: " + type.getName());
                    System.out.println("  Category: " + type.getCategory());
                    System.out.println("  Max Days: " + type.getMaxDays());
                    System.out.println("  Requires Approval: " + type.isRequiresApproval());
                    System.out.println("  Requires Attachment: " + type.isRequiresAttachment());
                    System.out.println("  Approval Workflow: " + type.getApprovalWorkflow());
                    System.out.println("  Active: " + type.isActive());
                    System.out.println();
                } else {
                    System.out.println(code + ": NOT FOUND");
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error printing leave type configuration", e);
        }
    }
}