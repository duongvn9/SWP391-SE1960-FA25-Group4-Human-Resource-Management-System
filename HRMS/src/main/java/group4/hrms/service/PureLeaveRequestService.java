package group4.hrms.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dao.LeaveTypeDao;
import group4.hrms.model.Request;
import group4.hrms.model.RequestType;
import group4.hrms.model.LeaveType;

/**
 * PureLeaveRequestService - Pure Database Approach
 * No JSON configuration - all business rules from database
 */
public class PureLeaveRequestService {
    private static final Logger logger = Logger.getLogger(PureLeaveRequestService.class.getName());

    private final RequestDao requestDao;
    private final RequestTypeDao requestTypeDao;
    private final LeaveTypeDao leaveTypeDao;

    public PureLeaveRequestService(RequestDao requestDao, RequestTypeDao requestTypeDao, LeaveTypeDao leaveTypeDao) {
        this.requestDao = requestDao;
        this.requestTypeDao = requestTypeDao;
        this.leaveTypeDao = leaveTypeDao;
    }

    /**
     * Create leave request - Pure database approach
     */
    public Long createLeaveRequest(Long userId, String leaveTypeCode,
            LocalDateTime startDate, LocalDateTime endDate, String reason) throws SQLException {

        logger.info("Creating leave request for user " + userId + " with leave type " + leaveTypeCode);

        try {
            // 1. Get leave type from database
            Optional<LeaveType> leaveTypeOpt = leaveTypeDao.findByCode(leaveTypeCode);
            if (!leaveTypeOpt.isPresent()) {
                throw new IllegalArgumentException("Invalid leave type: " + leaveTypeCode);
            }
            LeaveType leaveType = leaveTypeOpt.get();

            // 2. Get LEAVE_REQUEST type from request_types
            RequestType leaveRequestType = requestTypeDao.findByCode("LEAVE_REQUEST");
            if (leaveRequestType == null) {
                throw new IllegalArgumentException("LEAVE_REQUEST type not configured in system");
            }

            // 3. Calculate working days
            int dayCount = calculateWorkingDays(startDate, endDate);

            // 4. Validate using database rules
            validateLeaveRequest(userId, leaveType, startDate, endDate, dayCount, reason);

            // 5. Create request object
            Request leaveRequest = new Request();
            leaveRequest.setUserId(userId);
            leaveRequest.setRequestTypeId(leaveRequestType.getId());
            leaveRequest.setLeaveTypeId(leaveType.getId()); // Direct foreign key reference
            leaveRequest.setTitle("Leave Request - " + leaveType.getName());
            leaveRequest.setDescription(reason);
            leaveRequest.setStartDate(startDate);
            leaveRequest.setEndDate(endDate);
            leaveRequest.setDayCount(dayCount);
            leaveRequest.setStatus("PENDING");
            leaveRequest.setPriority("MEDIUM");
            leaveRequest.setCreatedAt(LocalDateTime.now());
            leaveRequest.setUpdatedAt(LocalDateTime.now());

            // 6. Save to database
            Request savedRequest = requestDao.save(leaveRequest);

            logger.info("Created leave request with ID " + savedRequest.getId());
            return savedRequest.getId();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating leave request", e);
            throw e;
        }
    }

    /**
     * Validate leave request using database rules
     */
    private void validateLeaveRequest(Long userId, LeaveType leaveType,
            LocalDateTime startDate, LocalDateTime endDate, int dayCount, String reason) {

        // Basic validation
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }

        if (startDate.isBefore(LocalDateTime.now().minusDays(1))) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }

        if (reason.length() > 1000) {
            throw new IllegalArgumentException("Reason cannot exceed 1000 characters");
        }

        if (dayCount <= 0) {
            throw new IllegalArgumentException("Number of leave days must be greater than 0");
        }

        // Database-driven validation
        if (!leaveType.isActive()) {
            throw new IllegalArgumentException("Leave type " + leaveType.getName() + " is not active");
        }

        // Check max days per request from database
        if (leaveType.getMaxDays() != null && dayCount > leaveType.getMaxDays()) {
            throw new IllegalArgumentException(
                "Cannot request more than " + leaveType.getMaxDays() + " days for " + leaveType.getName());
        }

        // Check advance notice requirement from database
        if (leaveType.getMinAdvanceNotice() != null && leaveType.getMinAdvanceNotice() > 0) {
            long daysUntilStart = ChronoUnit.DAYS.between(LocalDateTime.now(), startDate);
            if (daysUntilStart < leaveType.getMinAdvanceNotice()) {
                throw new IllegalArgumentException(
                    leaveType.getName() + " requires at least " + leaveType.getMinAdvanceNotice() + " days advance notice");
            }
        }

        // Check if requires certificate for long duration
        if (leaveType.isRequiresCertificate() && dayCount > 3) {
            // This validation can be enhanced to check if certificate is provided
            logger.info("Leave type " + leaveType.getName() + " with " + dayCount + " days may require certificate");
        }
    }

    /**
     * Calculate working days (excluding weekends)
     */
    private int calculateWorkingDays(LocalDateTime start, LocalDateTime end) {
        int workingDays = 0;
        LocalDateTime current = start.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endDate = end.truncatedTo(ChronoUnit.DAYS);

        while (!current.isAfter(endDate)) {
            // Monday = 1, Sunday = 7
            int dayOfWeek = current.getDayOfWeek().getValue();
            if (dayOfWeek >= 1 && dayOfWeek <= 5) { // Monday to Friday
                workingDays++;
            }
            current = current.plusDays(1);
        }

        return workingDays;
    }

    /**
     * Get available leave types from database
     */
    public Map<String, String> getAvailableLeaveTypes() {
        Map<String, String> leaveTypes = new HashMap<>();

        try {
            List<LeaveType> dbLeaveTypes = leaveTypeDao.findActiveLeaveTypes();

            for (LeaveType leaveType : dbLeaveTypes) {
                leaveTypes.put(leaveType.getCode(), leaveType.getName());
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading leave types from database", e);
            // Return empty map rather than fallback values
        }

        return leaveTypes;
    }

    /**
     * Get leave type details by code
     */
    public LeaveType getLeaveTypeByCode(String code) {
        try {
            Optional<LeaveType> leaveTypeOpt = leaveTypeDao.findByCode(code);
            return leaveTypeOpt.orElse(null);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding leave type by code: " + code, e);
            return null;
        }
    }

    /**
     * Get user's leave requests with full details
     */
    public List<Request> getUserLeaveRequests(Long userId) {
        try {
            // Get all requests for user, can filter by leave request type if needed
            return requestDao.findByUserId(userId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding leave requests for user: " + userId, e);
            throw new RuntimeException("Error finding leave requests", e);
        }
    }

    /**
     * Check if leave type requires approval
     */
    public boolean doesLeaveTypeRequireApproval(String leaveTypeCode) {
        LeaveType leaveType = getLeaveTypeByCode(leaveTypeCode);
        return leaveType != null && leaveType.isRequiresApproval();
    }

    /**
     * Get default days for leave type
     */
    public int getDefaultDaysForLeaveType(String leaveTypeCode) {
        LeaveType leaveType = getLeaveTypeByCode(leaveTypeCode);
        if (leaveType != null && leaveType.getDefaultDays() != null) {
            return leaveType.getDefaultDays();
        }
        return 0;
    }

    /**
     * Check if leave type is paid
     */
    public boolean isLeaveTypePaid(String leaveTypeCode) {
        LeaveType leaveType = getLeaveTypeByCode(leaveTypeCode);
        return leaveType != null && leaveType.isPaid();
    }

    /**
     * Get leave type rules summary for UI
     */
    public LeaveTypeRules getLeaveTypeRules(String leaveTypeCode) {
        LeaveType leaveType = getLeaveTypeByCode(leaveTypeCode);
        if (leaveType == null) {
            return null;
        }

        LeaveTypeRules rules = new LeaveTypeRules();
        rules.code = leaveType.getCode();
        rules.name = leaveType.getName();
        rules.defaultDays = leaveType.getDefaultDays() != null ? leaveType.getDefaultDays() : 0;
        rules.maxDays = leaveType.getMaxDays() != null ? leaveType.getMaxDays() : 365;
        rules.isPaid = leaveType.isPaid();
        rules.requiresApproval = leaveType.isRequiresApproval();
        rules.requiresCertificate = leaveType.isRequiresCertificate();
        rules.minAdvanceNotice = leaveType.getMinAdvanceNotice() != null ? leaveType.getMinAdvanceNotice() : 0;
        rules.canCarryForward = leaveType.isCanCarryForward();
        rules.maxCarryForward = leaveType.getMaxCarryForward() != null ? leaveType.getMaxCarryForward() : 0;

        return rules;
    }

    /**
     * Data class for leave type rules (from database)
     */
    public static class LeaveTypeRules {
        public String code;
        public String name;
        public int defaultDays;
        public int maxDays;
        public boolean isPaid;
        public boolean requiresApproval;
        public boolean requiresCertificate;
        public int minAdvanceNotice;
        public boolean canCarryForward;
        public int maxCarryForward;
    }

    /**
     * Get all leave types with their rules
     */
    public List<LeaveTypeRules> getAllLeaveTypeRules() {
        List<LeaveTypeRules> rulesList = new java.util.ArrayList<>();

        try {
            List<LeaveType> leaveTypes = leaveTypeDao.findActiveLeaveTypes();

            for (LeaveType leaveType : leaveTypes) {
                LeaveTypeRules rules = getLeaveTypeRules(leaveType.getCode());
                if (rules != null) {
                    rulesList.add(rules);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading all leave type rules", e);
        }

        return rulesList;
    }

    /**
     * Update leave request status
     */
    public boolean updateLeaveRequestStatus(Long requestId, String newStatus, String note) {
        try {
            Optional<Request> requestOpt = requestDao.findById(requestId);
            if (!requestOpt.isPresent()) {
                return false;
            }

            Request request = requestOpt.get();
            request.setStatus(newStatus);
            if (note != null && !note.trim().isEmpty()) {
                request.setRejectReason(note);
            }
            request.setUpdatedAt(LocalDateTime.now());

            requestDao.save(request);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating leave request status", e);
            return false;
        }
    }

    /**
     * Calculate remaining leave days for user and leave type
     */
    public int calculateRemainingLeaveDays(Long userId, String leaveTypeCode, int year) {
        try {
            LeaveType leaveType = getLeaveTypeByCode(leaveTypeCode);
            if (leaveType == null || leaveType.getDefaultDays() == null) {
                return 0;
            }

            int defaultDays = leaveType.getDefaultDays();

            // Calculate used days from approved requests in the year
            // This would require a more complex query to sum approved leave days
            // For now, return default days (can be enhanced later)

            return defaultDays;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating remaining leave days", e);
            return 0;
        }
    }
}