package group4.hrms.util;

import group4.hrms.dao.RequestTypeDao;
import group4.hrms.model.RequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Utility class to verify request types setup
 * Can be used for health checks and diagnostics
 */
public class RequestTypeVerifier {

    private static final Logger logger = LoggerFactory.getLogger(RequestTypeVerifier.class);

    private final RequestTypeDao requestTypeDao;

    public RequestTypeVerifier(RequestTypeDao requestTypeDao) {
        this.requestTypeDao = requestTypeDao;
    }

    /**
     * Verify that LEAVE_REQUEST type exists
     *
     * @return true if LEAVE_REQUEST exists, false otherwise
     */
    public boolean verifyLeaveRequestExists() {
        try {
            RequestType leaveRequestType = requestTypeDao.findByCode("LEAVE_REQUEST");

            if (leaveRequestType == null) {
                logger.warn("LEAVE_REQUEST type does not exist in database");
                return false;
            }

            logger.info("LEAVE_REQUEST type found: id={}, name={}, active={}",
                    leaveRequestType.getId(),
                    leaveRequestType.getName(),
                    leaveRequestType.isActive());

            return true;

        } catch (Exception e) {
            logger.error("Error verifying LEAVE_REQUEST type", e);
            return false;
        }
    }

    /**
     * Verify all request types and print summary
     *
     * @return VerificationResult with details
     */
    public VerificationResult verifyAllRequestTypes() {
        VerificationResult result = new VerificationResult();

        try {
            List<RequestType> allTypes = requestTypeDao.findAll();
            result.totalTypes = allTypes.size();

            for (RequestType type : allTypes) {
                if (type.isActive()) {
                    result.activeTypes++;
                }

                if ("LEAVE_REQUEST".equals(type.getCode())) {
                    result.hasLeaveRequest = true;
                    result.leaveRequestId = type.getId();
                }

                if ("OVERTIME_REQUEST".equals(type.getCode())) {
                    result.hasOvertimeRequest = true;
                }

                if ("PERSONAL_INFO_UPDATE".equals(type.getCode())) {
                    result.hasPersonalInfoUpdate = true;
                }
            }

            result.success = true;

            logger.info("Request types verification: total={}, active={}, hasLeaveRequest={}",
                    result.totalTypes, result.activeTypes, result.hasLeaveRequest);

        } catch (Exception e) {
            logger.error("Error verifying request types", e);
            result.success = false;
            result.errorMessage = e.getMessage();
        }

        return result;
    }

    /**
     * Print verification report to console
     */
    public void printVerificationReport() {
        System.out.println("========================================");
        System.out.println("Request Types Verification Report");
        System.out.println("========================================");

        VerificationResult result = verifyAllRequestTypes();

        if (!result.success) {
            System.out.println("ERROR: Verification failed");
            System.out.println("Error: " + result.errorMessage);
            return;
        }

        System.out.println("Total request types: " + result.totalTypes);
        System.out.println("Active request types: " + result.activeTypes);
        System.out.println();

        System.out.println("Required Types:");
        System.out.println("  LEAVE_REQUEST: " + (result.hasLeaveRequest ? "✓ EXISTS (id=" + result.leaveRequestId + ")" : "✗ MISSING"));
        System.out.println();

        System.out.println("Optional Types:");
        System.out.println("  OVERTIME_REQUEST: " + (result.hasOvertimeRequest ? "✓ EXISTS" : "✗ MISSING"));
        System.out.println("  PERSONAL_INFO_UPDATE: " + (result.hasPersonalInfoUpdate ? "✓ EXISTS" : "✗ MISSING"));
        System.out.println();

        if (!result.hasLeaveRequest) {
            System.out.println("WARNING: LEAVE_REQUEST is missing!");
            System.out.println("Action required:");
            System.out.println("  1. Run: mysql -u root -p hrms < db-script/request_types_seed.sql");
            System.out.println("  2. Or use RequestTypeInitializer.ensureLeaveRequestTypeExists()");
        } else {
            System.out.println("✓ All required request types are present");
        }

        System.out.println("========================================");
    }

    /**
     * Result of verification
     */
    public static class VerificationResult {
        public boolean success = false;
        public String errorMessage = null;
        public int totalTypes = 0;
        public int activeTypes = 0;
        public boolean hasLeaveRequest = false;
        public Long leaveRequestId = null;
        public boolean hasOvertimeRequest = false;
        public boolean hasPersonalInfoUpdate = false;

        public boolean isHealthy() {
            return success && hasLeaveRequest;
        }
    }

    /**
     * Main method for standalone verification
     */
    public static void main(String[] args) {
        try {
            RequestTypeDao requestTypeDao = new RequestTypeDao();
            RequestTypeVerifier verifier = new RequestTypeVerifier(requestTypeDao);

            verifier.printVerificationReport();

            // Exit with appropriate code
            VerificationResult result = verifier.verifyAllRequestTypes();
            System.exit(result.isHealthy() ? 0 : 1);

        } catch (Exception e) {
            System.err.println("ERROR: Failed to verify request types");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
