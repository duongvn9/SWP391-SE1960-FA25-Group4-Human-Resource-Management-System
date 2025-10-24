package group4.hrms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.dao.RequestTypeDao;
import group4.hrms.model.RequestType;

/**
 * Utility class to initialize request types in the database
 * Ensures that required request types like LEAVE_REQUEST exist
 */
public class RequestTypeInitializer {

    private static final Logger logger = LoggerFactory.getLogger(RequestTypeInitializer.class);

    private final RequestTypeDao requestTypeDao;

    public RequestTypeInitializer(RequestTypeDao requestTypeDao) {
        this.requestTypeDao = requestTypeDao;
    }


    public RequestType ensureLeaveRequestTypeExists() {
        try {
            // Try to find existing LEAVE_REQUEST
            RequestType existingType = requestTypeDao.findByCode("LEAVE_REQUEST");

            if (existingType != null) {
                logger.info("LEAVE_REQUEST type already exists with id: {}", existingType.getId());
                return existingType;
            }

            // Create new LEAVE_REQUEST type
            logger.warn("LEAVE_REQUEST type not found. Creating it now...");

            RequestType leaveRequestType = new RequestType();
            leaveRequestType.setCode("LEAVE_REQUEST");
            leaveRequestType.setName("Leave Request");
            leaveRequestType.setDescription("Employee leave request for various types of leave");
            leaveRequestType.setCategory("LEAVE");
            leaveRequestType.setRequiresApproval(true);
            leaveRequestType.setRequiresAttachment(false);
            leaveRequestType.setMaxDays(null); // No max days limit at request type level
            leaveRequestType.setApprovalWorkflow("SINGLE");
            leaveRequestType.setActive(true);

            RequestType savedType = requestTypeDao.save(leaveRequestType);
            logger.info("LEAVE_REQUEST type created successfully with id: {}", savedType.getId());

            return savedType;

        } catch (Exception e) {
            logger.error("Error ensuring LEAVE_REQUEST type exists", e);
            throw new RuntimeException("Failed to ensure LEAVE_REQUEST type exists. " +
                    "Please check database schema and ensure request_types table has all required columns.", e);
        }
    }

    /**
     * Initializes all common request types
     * This can be called during application startup
     */
    public void initializeCommonRequestTypes() {
        logger.info("Initializing common request types...");

        // Ensure LEAVE_REQUEST exists
        ensureLeaveRequestTypeExists();

        // You can add other request types here
        ensureRequestTypeExists(
            "OVERTIME_REQUEST",
            "Overtime Request",
            "Request for overtime work",
            "OVERTIME"
        );

        // PERSONAL_INFO_UPDATE removed - not used in current system
        // If needed in future, uncomment below:
        // ensureRequestTypeExists(
        //     "PERSONAL_INFO_UPDATE",
        //     "Personal Information Update",
        //     "Request to update personal information",
        //     "PERSONAL"
        // );

        logger.info("Common request types initialization completed");
    }

    /**
     * Helper method to ensure a request type exists
     */
    private void ensureRequestTypeExists(String code, String name, String description, String category) {
        try {
            RequestType existingType = requestTypeDao.findByCode(code);

            if (existingType != null) {
                logger.debug("{} type already exists", code);
                return;
            }

            RequestType requestType = new RequestType();
            requestType.setCode(code);
            requestType.setName(name);
            requestType.setDescription(description);
            requestType.setCategory(category);
            requestType.setRequiresApproval(true);
            requestType.setRequiresAttachment(false);
            requestType.setApprovalWorkflow("SINGLE");
            requestType.setActive(true);

            requestTypeDao.save(requestType);
            logger.info("{} type created successfully", code);

        } catch (Exception e) {
            logger.error("Error creating {} type", code, e);
            // Don't throw exception for optional types
        }
    }
}
