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
import group4.hrms.dto.LeaveRequestDetail;
import group4.hrms.model.Request;
import group4.hrms.model.RequestType;
import group4.hrms.model.LeaveType;
import group4.hrms.util.RequestTypeInitializer;
import group4.hrms.exception.LeaveValidationException;
import group4.hrms.exception.ValidationErrorMessage;

/**
 * LeaveRequestService - Pure Database Approach
 * No JSON configuration - all business rules from database
 */
public class LeaveRequestService {
    private static final Logger logger = Logger.getLogger(LeaveRequestService.class.getName());

    private final RequestDao requestDao;
    private final RequestTypeDao requestTypeDao;
    private final LeaveTypeDao leaveTypeDao;

    public LeaveRequestService(RequestDao requestDao, RequestTypeDao requestTypeDao, LeaveTypeDao leaveTypeDao) {
        this.requestDao = requestDao;
        this.requestTypeDao = requestTypeDao;
        this.leaveTypeDao = leaveTypeDao;
    }

    /**
     * Create leave request - JSON-based approach
     */
    public Long createLeaveRequest(Long accountId, Long userId, Long departmentId,
            String leaveTypeCode, LocalDateTime startDate, LocalDateTime endDate, String reason) throws SQLException {

        logger.info("Creating leave request for user " + userId + " with leave type " + leaveTypeCode);

        try {
            // 1. Get leave type from database
            Optional<LeaveType> leaveTypeOpt = leaveTypeDao.findByCode(leaveTypeCode);
            if (!leaveTypeOpt.isPresent()) {
                throw new IllegalArgumentException("Invalid leave type: " + leaveTypeCode);
            }
            LeaveType leaveType = leaveTypeOpt.get();

            // 2. Get LEAVE_REQUEST type from request_types (ensure it exists)
            RequestTypeInitializer initializer = new RequestTypeInitializer(requestTypeDao);
            RequestType leaveRequestType = initializer.ensureLeaveRequestTypeExists();

            if (leaveRequestType == null) {
                throw new IllegalArgumentException("LEAVE_REQUEST type not configured in system");
            }

            // 3. Calculate working days
            int dayCount = calculateWorkingDays(startDate, endDate);

            // 4. Validate using database rules
            validateLeaveRequest(userId, leaveType, startDate, endDate, dayCount, reason);

            // 4.5. Check for pending requests and log warning (Requirements: 3)
            List<Request> pendingRequests = findPendingLeaveInRange(userId, startDate, endDate);
            if (!pendingRequests.isEmpty()) {
                // Log warning but still allow creating new request
                for (Request pendingRequest : pendingRequests) {
                    LeaveRequestDetail pendingDetail = pendingRequest.getLeaveDetail();
                    if (pendingDetail != null) {
                        logger.warning(String.format(
                            "Warning: User %d already has a pending leave request: %s (%s) from %s to %s. " +
                            "Creating new request anyway.",
                            userId,
                            pendingDetail.getLeaveTypeName(),
                            pendingRequest.getStatus(),
                            pendingDetail.getStartDate(),
                            pendingDetail.getEndDate()
                        ));
                    } else {
                        logger.warning(String.format(
                            "Warning: User %d already has a pending request: %s (%s). Creating new request anyway.",
                            userId,
                            pendingRequest.getTitle(),
                            pendingRequest.getStatus()
                        ));
                    }
                }
            }

            // 5. Create LeaveRequestDetail object with form data
            LeaveRequestDetail detail = new LeaveRequestDetail();
            detail.setLeaveTypeCode(leaveType.getCode());
            detail.setLeaveTypeName(leaveType.getName());
            detail.setStartDate(startDate.toString());
            detail.setEndDate(endDate.toString());
            detail.setDayCount(dayCount);
            detail.setReason(reason);
            detail.setCertificateRequired(leaveType.isRequiresCertificate());
            detail.setAttachmentPath(null);
            detail.setManagerNotes(null);

            // 6. Create request object
            Request leaveRequest = new Request();
            leaveRequest.setRequestTypeId(leaveRequestType.getId());
            leaveRequest.setTitle("Leave Request - " + leaveType.getName());
            leaveRequest.setLeaveDetail(detail);  // Set JSON data
            leaveRequest.setCreatedByAccountId(accountId);
            leaveRequest.setCreatedByUserId(userId);
            leaveRequest.setDepartmentId(departmentId);
            leaveRequest.setStatus("PENDING");
            leaveRequest.setCreatedAt(LocalDateTime.now());
            leaveRequest.setUpdatedAt(LocalDateTime.now());

            // 7. Save to database
            Request savedRequest = requestDao.save(leaveRequest);

            logger.info("Created leave request with ID " + savedRequest.getId());
            return savedRequest.getId();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating leave request", e);
            throw e;
        }
    }

    /**
     * Validate leave balance
     * Requirements: 2
     *
     * Kiểm tra số ngày nghỉ phép còn lại trước khi tạo đơn
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param requestedDays Number of days requested
     * @param year Year to check balance
     * @throws IllegalArgumentException if balance is insufficient
     */
    private void validateLeaveBalance(Long userId, String leaveTypeCode,
                                      int requestedDays, int year) {
        try {
            // Get leave type
            LeaveType leaveType = getLeaveTypeByCode(leaveTypeCode);
            if (leaveType == null) {
                throw new IllegalArgumentException("Invalid leave type: " + leaveTypeCode);
            }

            // Skip validation if leave type has no limit (unlimited)
            if (leaveType.getDefaultDays() == null || leaveType.getDefaultDays() <= 0) {
                logger.info("Leave type " + leaveTypeCode + " has no limit, skipping balance validation");
                return;
            }

            // Get default days for leave type
            int defaultDays = leaveType.getDefaultDays();

            // Calculate seniority bonus (simplified - can be enhanced later)
            // TODO: Calculate based on user's join date and company policy
            int seniorityBonus = 0;

            // Calculate total allowed days
            int totalAllowed = defaultDays + seniorityBonus;

            // Calculate used days from APPROVED requests in the year
            int usedDays = calculateUsedDays(userId, leaveTypeCode, year);

            // Calculate remaining days
            int remainingDays = totalAllowed - usedDays;

            // Validate if requested days exceed remaining days
            if (requestedDays > remainingDays) {
                ValidationErrorMessage errorMsg = ValidationErrorMessage.balanceExceededError(
                    leaveType.getName(),
                    remainingDays,
                    usedDays,
                    requestedDays,
                    totalAllowed
                );
                throw new LeaveValidationException(errorMsg);
            }

            logger.info("Leave balance validation passed for user " + userId + " leave type " + leaveTypeCode + ": " +
                       "requested=" + requestedDays + ", remaining=" + remainingDays + ", used=" + usedDays + ", total=" + totalAllowed);

        } catch (IllegalArgumentException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error validating leave balance", e);
            throw new RuntimeException("Error validating leave balance", e);
        }
    }

    /**
     * Check for overlapping leave requests
     * Requirements: 1
     *
     * @param userId User ID
     * @param startDate Start date of leave request
     * @param endDate End date of leave request
     * @param excludeRequestId Request ID to exclude (for update scenarios)
     * @throws IllegalArgumentException if overlap is detected
     */
    private void checkLeaveOverlap(Long userId, LocalDateTime startDate,
                                   LocalDateTime endDate, Long excludeRequestId) {
        try {
            // Query all PENDING and APPROVED requests in the date range
            List<String> statuses = new java.util.ArrayList<>();
            statuses.add("PENDING");
            statuses.add("APPROVED");

            List<Request> overlappingRequests = requestDao.findByUserIdAndDateRange(
                userId, startDate, endDate, statuses, excludeRequestId
            );

            // Check if any overlapping requests exist
            if (!overlappingRequests.isEmpty()) {
                Request existingRequest = overlappingRequests.get(0);

                // Get leave detail from existing request
                LeaveRequestDetail existingDetail = existingRequest.getLeaveDetail();

                if (existingDetail != null) {
                    String existingStartDate = existingDetail.getStartDate();
                    String existingEndDate = existingDetail.getEndDate();
                    String existingLeaveType = existingDetail.getLeaveTypeName();

                    ValidationErrorMessage errorMsg = ValidationErrorMessage.overlapError(
                        existingLeaveType,
                        existingRequest.getStatus(),
                        existingStartDate,
                        existingEndDate
                    );
                    throw new LeaveValidationException(errorMsg);
                } else {
                    // Fallback if detail is not available
                    ValidationErrorMessage errorMsg = ValidationErrorMessage.overlapError(
                        existingRequest.getTitle(),
                        existingRequest.getStatus(),
                        "N/A",
                        "N/A"
                    );
                    throw new LeaveValidationException(errorMsg);
                }
            }

        } catch (IllegalArgumentException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking leave overlap", e);
            throw new RuntimeException("Error checking leave overlap", e);
        }
    }

    /**
     * Find pending leave requests in date range
     * Requirements: 3
     *
     * Query PENDING requests trong khoảng thời gian để cảnh báo user
     *
     * @param userId User ID
     * @param startDate Start date of leave request
     * @param endDate End date of leave request
     * @return List of pending requests in the date range
     */
    private List<Request> findPendingLeaveInRange(Long userId, LocalDateTime startDate,
                                                   LocalDateTime endDate) {
        try {
            // Query only PENDING requests in the date range
            List<String> statuses = new java.util.ArrayList<>();
            statuses.add("PENDING");

            List<Request> pendingRequests = requestDao.findByUserIdAndDateRange(
                userId, startDate, endDate, statuses, null
            );

            logger.info("Found " + pendingRequests.size() + " pending leave requests for user " + userId +
                       " in date range " + startDate + " to " + endDate);

            return pendingRequests;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding pending leave requests", e);
            throw new RuntimeException("Error finding pending leave requests", e);
        }
    }

    /**
     * Check for conflicts with approved OT requests
     * Requirements: 4
     *
     * Kiểm tra xem có đơn OT APPROVED nào trùng với khoảng thời gian nghỉ phép không
     *
     * @param userId User ID
     * @param startDate Start date of leave request
     * @param endDate End date of leave request
     * @throws IllegalArgumentException if conflict with OT is detected
     */
    private void checkConflictWithOT(Long userId, LocalDateTime startDate,
                                     LocalDateTime endDate) {
        try {
            // Query APPROVED OT requests in the date range
            List<Request> otRequests = requestDao.findOTRequestsByUserIdAndDateRange(
                userId, startDate, endDate
            );

            // Check if any OT requests exist in the date range
            if (!otRequests.isEmpty()) {
                Request otRequest = otRequests.get(0);

                // Get OT detail from request
                group4.hrms.dto.OTRequestDetail otDetail = otRequest.getOtDetail();

                if (otDetail != null) {
                    String otDate = otDetail.getOtDate();
                    Double otHours = otDetail.getOtHours();
                    String startTime = otDetail.getStartTime();
                    String endTime = otDetail.getEndTime();

                    ValidationErrorMessage errorMsg = ValidationErrorMessage.otConflictError(
                        otDate,
                        otHours,
                        startTime,
                        endTime
                    );
                    throw new LeaveValidationException(errorMsg);
                } else {
                    // Fallback if detail is not available
                    ValidationErrorMessage errorMsg = ValidationErrorMessage.genericError(
                        "Không thể xin nghỉ phép trong ngày đã có đơn OT được duyệt"
                    );
                    throw new LeaveValidationException(errorMsg);
                }
            }

            logger.info("No OT conflict detected for user " + userId + " in date range " + startDate + " to " + endDate);

        } catch (IllegalArgumentException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking conflict with OT", e);
            throw new RuntimeException("Error checking conflict with OT", e);
        }
    }

    /**
     * Validate leave request using database rules
     * Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7
     */
    private void validateLeaveRequest(Long userId, LeaveType leaveType,
            LocalDateTime startDate, LocalDateTime endDate, int dayCount, String reason) {

        // Requirement 4.1: Validate all required fields
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (leaveType == null) {
            throw new IllegalArgumentException("Leave type cannot be null");
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required");
        }

        // Requirement 4.5: Validate leave type exists and is active
        if (!leaveType.isActive()) {
            throw new IllegalArgumentException("Leave type '" + leaveType.getName() + "' is not active");
        }

        // Requirement 4.2: Validate start_date is not after end_date
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        // Requirement 4.3: Validate start_date is not in the past
        // Allow some flexibility for emergency leave or same-day requests
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();

        if (startDate.isBefore(startOfToday)) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        // Requirement 4.4: Validate reason length (max 1000 characters)
        if (reason.length() > 1000) {
            throw new IllegalArgumentException("Reason cannot exceed 1000 characters (current: " + reason.length() + ")");
        }

        // Validate day count is positive
        if (dayCount <= 0) {
            throw new IllegalArgumentException("Number of leave days must be greater than 0");
        }

        // Requirement 4.7: Validate duration does not exceed max_days limit
        if (leaveType.getMaxDays() != null && dayCount > leaveType.getMaxDays()) {
            throw new IllegalArgumentException(
                    "Cannot request more than " + leaveType.getMaxDays() + " days for " + leaveType.getName()
                    + " (requested: " + dayCount + " days)");
        }

        // Requirement 2: Validate leave balance
        // Call after max days validation, skip if leave type is unlimited
        if (leaveType.getDefaultDays() != null && leaveType.getDefaultDays() > 0) {
            int currentYear = startDate.getYear();
            validateLeaveBalance(userId, leaveType.getCode(), dayCount, currentYear);
        }

        // Requirement 4.6: Validate advance notice requirement
        if (leaveType.getMinAdvanceNotice() != null && leaveType.getMinAdvanceNotice() > 0) {
            long daysUntilStart = ChronoUnit.DAYS.between(now.toLocalDate().atStartOfDay(),
                                                          startDate.toLocalDate().atStartOfDay());

            if (daysUntilStart < leaveType.getMinAdvanceNotice()) {
                throw new IllegalArgumentException(
                        leaveType.getName() + " requires at least " + leaveType.getMinAdvanceNotice()
                                + " days advance notice (current notice: " + daysUntilStart + " days)");
            }
        }

        // Additional validation: Check if requires certificate for long duration
        if (leaveType.isRequiresCertificate() && dayCount > 3) {
            // Log for information - certificate validation can be enhanced later
            logger.info("Leave type '" + leaveType.getName() + "' with " + dayCount
                       + " days may require certificate documentation");
        }

        // Requirement 1: Check for overlapping leave requests
        // Call after date logic validation
        checkLeaveOverlap(userId, startDate, endDate, null);

        // Requirement 4: Check for conflict with approved OT requests
        // Call after overlap check
        checkConflictWithOT(userId, startDate, endDate);
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
        rules.paid = leaveType.isPaid();
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
        public boolean paid;
        public boolean requiresApproval;
        public boolean requiresCertificate;
        public int minAdvanceNotice;
        public boolean canCarryForward;
        public int maxCarryForward;

        // Getters for JSP EL
        public String getCode() { return code; }
        public String getName() { return name; }
        public int getDefaultDays() { return defaultDays; }
        public int getMaxDays() { return maxDays; }
        public boolean isPaid() { return paid; }
        public boolean isRequiresApproval() { return requiresApproval; }
        public boolean isRequiresCertificate() { return requiresCertificate; }
        public int getMinAdvanceNotice() { return minAdvanceNotice; }
        public boolean isCanCarryForward() { return canCarryForward; }
        public int getMaxCarryForward() { return maxCarryForward; }
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

    /**
     * Get leave balance for user and leave type
     * Requirements: 7
     *
     * Enhanced to include pending days and available days
     */
    public group4.hrms.dto.LeaveBalance getLeaveBalance(Long userId, String leaveTypeCode, int year) {
        try {
            LeaveType leaveType = getLeaveTypeByCode(leaveTypeCode);
            if (leaveType == null || leaveType.getDefaultDays() == null) {
                return null;
            }

            int defaultDays = leaveType.getDefaultDays();

            // Calculate seniority bonus (simplified - can be enhanced)
            // For now, assume 1 extra day per 2 years of service
            int seniorityBonus = 0; // TODO: Calculate based on user's join date

            // Calculate used days from approved requests in the year
            int usedDays = calculateUsedDays(userId, leaveTypeCode, year);

            // Calculate pending days from PENDING requests in the year
            int pendingDays = calculatePendingDays(userId, leaveTypeCode, year);

            return new group4.hrms.dto.LeaveBalance(
                leaveType.getCode(),
                leaveType.getName(),
                defaultDays,
                seniorityBonus,
                usedDays,
                pendingDays,
                year
            );

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting leave balance", e);
            return null;
        }
    }

    /**
     * Calculate used days for user, leave type and year
     * Requirements: 2
     *
     * Tính chính xác số ngày đã sử dụng từ các đơn APPROVED trong năm
     */
    private int calculateUsedDays(Long userId, String leaveTypeCode, int year) {
        try {
            // Get all requests for user
            List<Request> requests = requestDao.findByUserId(userId);

            int totalUsedDays = 0;
            for (Request request : requests) {
                // Only count APPROVED requests
                if (!"APPROVED".equals(request.getStatus())) {
                    continue;
                }

                // Check if request has leave detail
                group4.hrms.dto.LeaveRequestDetail detail = request.getLeaveDetail();
                if (detail == null || !leaveTypeCode.equals(detail.getLeaveTypeCode())) {
                    continue;
                }

                // Check if request's start date is in the specified year
                // Use startDate from detail instead of createdAt for accuracy
                String startDateStr = detail.getStartDate();
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    try {
                        LocalDateTime startDate = LocalDateTime.parse(startDateStr);
                        if (startDate.getYear() == year) {
                            totalUsedDays += detail.getDayCount();
                        }
                    } catch (Exception e) {
                        // If parsing fails, fallback to createdAt
                        if (request.getCreatedAt() != null &&
                            request.getCreatedAt().getYear() == year) {
                            totalUsedDays += detail.getDayCount();
                        }
                    }
                } else if (request.getCreatedAt() != null &&
                           request.getCreatedAt().getYear() == year) {
                    // Fallback to createdAt if startDate is not available
                    totalUsedDays += detail.getDayCount();
                }
            }

            logger.info("Calculated used days for user " + userId + " leave type " + leaveTypeCode +
                       " year " + year + ": " + totalUsedDays + " days");
            return totalUsedDays;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating used days", e);
            return 0;
        }
    }

    /**
     * Calculate pending days for user, leave type and year
     * Requirements: 7
     *
     * Tính số ngày đang chờ duyệt từ các đơn PENDING trong năm
     */
    private int calculatePendingDays(Long userId, String leaveTypeCode, int year) {
        try {
            // Get all requests for user
            List<Request> requests = requestDao.findByUserId(userId);

            int totalPendingDays = 0;
            for (Request request : requests) {
                // Only count PENDING requests
                if (!"PENDING".equals(request.getStatus())) {
                    continue;
                }

                // Check if request has leave detail
                group4.hrms.dto.LeaveRequestDetail detail = request.getLeaveDetail();
                if (detail == null || !leaveTypeCode.equals(detail.getLeaveTypeCode())) {
                    continue;
                }

                // Check if request's start date is in the specified year
                String startDateStr = detail.getStartDate();
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    try {
                        LocalDateTime startDate = LocalDateTime.parse(startDateStr);
                        if (startDate.getYear() == year) {
                            totalPendingDays += detail.getDayCount();
                        }
                    } catch (Exception e) {
                        // If parsing fails, fallback to createdAt
                        if (request.getCreatedAt() != null &&
                            request.getCreatedAt().getYear() == year) {
                            totalPendingDays += detail.getDayCount();
                        }
                    }
                } else if (request.getCreatedAt() != null &&
                           request.getCreatedAt().getYear() == year) {
                    // Fallback to createdAt if startDate is not available
                    totalPendingDays += detail.getDayCount();
                }
            }

            logger.info("Calculated pending days for user " + userId + " leave type " + leaveTypeCode +
                       " year " + year + ": " + totalPendingDays + " days");
            return totalPendingDays;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating pending days", e);
            return 0;
        }
    }

    /**
     * Get all leave balances for user
     */
    public java.util.List<group4.hrms.dto.LeaveBalance> getAllLeaveBalances(Long userId, int year) {
        java.util.List<group4.hrms.dto.LeaveBalance> balances = new java.util.ArrayList<>();

        try {
            List<LeaveType> leaveTypes = leaveTypeDao.findActiveLeaveTypes();

            for (LeaveType leaveType : leaveTypes) {
                group4.hrms.dto.LeaveBalance balance = getLeaveBalance(userId, leaveType.getCode(), year);
                if (balance != null) {
                    balances.add(balance);
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting all leave balances", e);
        }

        return balances;
    }
}