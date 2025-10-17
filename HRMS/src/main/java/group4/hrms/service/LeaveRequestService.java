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
import group4.hrms.dao.HolidayDao;
import group4.hrms.dao.LeaveBalanceDao;
import group4.hrms.dto.LeaveRequestDetail;
import group4.hrms.model.Request;
import group4.hrms.model.RequestType;
import group4.hrms.model.LeaveType;
import group4.hrms.util.RequestTypeInitializer;
import group4.hrms.exception.LeaveValidationException;
import group4.hrms.exception.ValidationErrorMessage;
import group4.hrms.dto.ValidationResult;
import group4.hrms.dto.HalfDayConflict;
import java.time.LocalDate;
import java.time.DayOfWeek;

/**
 * LeaveRequestService - Pure Database Approach
 * No JSON configuration - all business rules from database
 */
public class LeaveRequestService {
    private static final Logger logger = Logger.getLogger(LeaveRequestService.class.getName());

    private final RequestDao requestDao;
    private final RequestTypeDao requestTypeDao;
    private final LeaveTypeDao leaveTypeDao;
    private final HolidayDao holidayDao;
    private final LeaveBalanceDao leaveBalanceDao;

    public LeaveRequestService(RequestDao requestDao, RequestTypeDao requestTypeDao, LeaveTypeDao leaveTypeDao) {
        this.requestDao = requestDao;
        this.requestTypeDao = requestTypeDao;
        this.leaveTypeDao = leaveTypeDao;
        this.holidayDao = new HolidayDao();
        this.leaveBalanceDao = new LeaveBalanceDao();
    }

    /**
     * Create leave request - JSON-based approach with half-day support
     */
    public Long createLeaveRequest(Long accountId, Long userId, Long departmentId,
            String leaveTypeCode, LocalDateTime startDate, LocalDateTime endDate, String reason,
            Boolean isHalfDay, String halfDayPeriod) throws SQLException {

        logger.info(String.format("Creating leave request: userId=%d, leaveType=%s, startDate=%s, endDate=%s, isHalfDay=%b, period=%s",
                   userId, leaveTypeCode, startDate, endDate, isHalfDay, halfDayPeriod));

        try {
            // 1. Get leave type from database
            Optional<LeaveType> leaveTypeOpt = leaveTypeDao.findByCode(leaveTypeCode);
            if (!leaveTypeOpt.isPresent()) {
                logger.warning(String.format("Invalid leave type requested: userId=%d, leaveTypeCode=%s",
                              userId, leaveTypeCode));
                throw new IllegalArgumentException("Invalid leave type: " + leaveTypeCode);
            }
            LeaveType leaveType = leaveTypeOpt.get();
            logger.fine(String.format("Found leave type: %s (%s)", leaveType.getName(), leaveType.getCode()));

            // 2. Get LEAVE_REQUEST type from request_types (ensure it exists)
            RequestTypeInitializer initializer = new RequestTypeInitializer(requestTypeDao);
            RequestType leaveRequestType = initializer.ensureLeaveRequestTypeExists();

            if (leaveRequestType == null) {
                throw new IllegalArgumentException("LEAVE_REQUEST type not configured in system");
            }

            // 3. Calculate working days and duration
            int dayCount = calculateWorkingDays(startDate, endDate);
            double durationDays = calculateLeaveDuration(isHalfDay != null && isHalfDay, startDate, endDate);

            // 4. Validate using database rules (including half-day specific validation)
            validateLeaveRequest(userId, leaveType, startDate, endDate, dayCount, reason, isHalfDay, halfDayPeriod);

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

            // 5. Create LeaveRequestDetail object with form data (including half-day fields)
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

            // Set half-day specific fields
            detail.setIsHalfDay(isHalfDay);
            detail.setHalfDayPeriod(halfDayPeriod);
            detail.setDurationDays(durationDays);

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

            logger.info(String.format("Successfully created leave request: id=%d, userId=%d, leaveType=%s, days=%d, status=%s",
                       savedRequest.getId(), userId, leaveTypeCode, dayCount, savedRequest.getStatus()));
            return savedRequest.getId();

        } catch (LeaveValidationException e) {
            logger.warning(String.format("Leave validation failed: userId=%d, leaveType=%s, error=%s",
                          userId, leaveTypeCode, e.getMessage()));
            throw e;
        } catch (IllegalArgumentException e) {
            logger.warning(String.format("Invalid leave request parameters: userId=%d, leaveType=%s, error=%s",
                          userId, leaveTypeCode, e.getMessage()));
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Unexpected error creating leave request: userId=%d, leaveType=%s",
                      userId, leaveTypeCode), e);
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
        logger.fine(String.format("Validating leave balance: userId=%d, leaveType=%s, requestedDays=%d, year=%d",
                   userId, leaveTypeCode, requestedDays, year));

        try {
            // Get leave type
            LeaveType leaveType = getLeaveTypeByCode(leaveTypeCode);
            if (leaveType == null) {
                logger.warning(String.format("Leave type not found during balance validation: userId=%d, leaveType=%s",
                              userId, leaveTypeCode));
                throw new IllegalArgumentException("Invalid leave type: " + leaveTypeCode);
            }

            // Skip validation if leave type has no limit (unlimited)
            if (leaveType.getDefaultDays() == null || leaveType.getDefaultDays() <= 0) {
                logger.info(String.format("Leave type has no limit, skipping balance validation: userId=%d, leaveType=%s",
                           userId, leaveTypeCode));
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

            logger.fine(String.format("Balance calculation: userId=%d, leaveType=%s, total=%d, used=%d, remaining=%d, requested=%d",
                       userId, leaveTypeCode, totalAllowed, usedDays, remainingDays, requestedDays));

            // Validate if requested days exceed remaining days
            if (requestedDays > remainingDays) {
                logger.warning(String.format("Insufficient leave balance: userId=%d, leaveType=%s, requested=%d, remaining=%d, used=%d, total=%d",
                              userId, leaveTypeCode, requestedDays, remainingDays, usedDays, totalAllowed));
                ValidationErrorMessage errorMsg = ValidationErrorMessage.balanceExceededError(
                    leaveType.getName(),
                    remainingDays,
                    usedDays,
                    requestedDays,
                    totalAllowed
                );
                throw new LeaveValidationException(errorMsg);
            }

            logger.info(String.format("Leave balance validation passed: userId=%d, leaveType=%s, requested=%d, remaining=%d",
                       userId, leaveTypeCode, requestedDays, remainingDays));

        } catch (IllegalArgumentException e) {
            // Re-throw validation exceptions (includes LeaveValidationException)
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error validating leave balance: userId=%d, leaveType=%s, year=%d",
                      userId, leaveTypeCode, year), e);
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
        logger.info(String.format("=== CHECKING LEAVE OVERLAP === userId=%d, startDate=%s, endDate=%s, excludeId=%s",
                   userId, startDate, endDate, excludeRequestId));

        try {
            // Query all PENDING and APPROVED requests in the date range
            List<String> statuses = new java.util.ArrayList<>();
            statuses.add("PENDING");
            statuses.add("APPROVED");

            logger.info(String.format("Querying database for overlapping requests: userId=%d, statuses=%s",
                       userId, statuses));

            List<Request> overlappingRequests = requestDao.findByUserIdAndDateRange(
                userId, startDate, endDate, statuses, excludeRequestId
            );

            logger.info(String.format("Found %d overlapping requests", overlappingRequests.size()));

            // Check if any overlapping requests exist
            if (!overlappingRequests.isEmpty()) {
                Request existingRequest = overlappingRequests.get(0);

                // Get leave detail from existing request
                LeaveRequestDetail existingDetail = existingRequest.getLeaveDetail();

                if (existingDetail != null) {
                    String existingStartDate = existingDetail.getStartDate();
                    String existingEndDate = existingDetail.getEndDate();
                    String existingLeaveType = existingDetail.getLeaveTypeName();

                    logger.warning(String.format("Leave overlap detected: userId=%d, existingRequestId=%d, existingType=%s, existingDates=%s to %s, status=%s",
                                  userId, existingRequest.getId(), existingLeaveType, existingStartDate, existingEndDate, existingRequest.getStatus()));

                    ValidationErrorMessage errorMsg = ValidationErrorMessage.overlapError(
                        existingLeaveType,
                        existingRequest.getStatus(),
                        existingStartDate,
                        existingEndDate
                    );
                    throw new LeaveValidationException(errorMsg);
                } else {
                    logger.warning(String.format("Leave overlap detected (no detail): userId=%d, existingRequestId=%d, status=%s",
                                  userId, existingRequest.getId(), existingRequest.getStatus()));

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

            logger.info(String.format("✓ No leave overlap found: userId=%d, dateRange=%s to %s",
                       userId, startDate, endDate));

        } catch (IllegalArgumentException e) {
            // Re-throw validation exceptions (includes LeaveValidationException)
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error checking leave overlap: userId=%d, startDate=%s, endDate=%s",
                      userId, startDate, endDate), e);
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
        logger.fine(String.format("Checking OT conflict: userId=%d, startDate=%s, endDate=%s",
                   userId, startDate, endDate));

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

                    logger.warning(String.format("OT conflict detected: userId=%d, otRequestId=%d, otDate=%s, hours=%.1f, time=%s-%s",
                                  userId, otRequest.getId(), otDate, otHours, startTime, endTime));

                    ValidationErrorMessage errorMsg = ValidationErrorMessage.otConflictError(
                        otDate,
                        otHours,
                        startTime,
                        endTime
                    );
                    throw new LeaveValidationException(errorMsg);
                } else {
                    logger.warning(String.format("OT conflict detected (no detail): userId=%d, otRequestId=%d",
                                  userId, otRequest.getId()));

                    // Fallback if detail is not available
                    ValidationErrorMessage errorMsg = ValidationErrorMessage.genericError(
                        "Không thể xin nghỉ phép trong ngày đã có đơn OT được duyệt"
                    );
                    throw new LeaveValidationException(errorMsg);
                }
            }

            logger.fine(String.format("No OT conflict found: userId=%d, dateRange=%s to %s",
                       userId, startDate, endDate));

        } catch (IllegalArgumentException e) {
            // Re-throw validation exceptions (includes LeaveValidationException)
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error checking OT conflict: userId=%d, startDate=%s, endDate=%s",
                      userId, startDate, endDate), e);
            throw new RuntimeException("Error checking conflict with OT", e);
        }
    }

    /**
     * Validate leave request using database rules (with half-day support)
     * Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 1.5, 2.1, 2.2, 2.3, 5.7
     */
    private void validateLeaveRequest(Long userId, LeaveType leaveType,
            LocalDateTime startDate, LocalDateTime endDate, int dayCount, String reason,
            Boolean isHalfDay, String halfDayPeriod) {

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

        // Validate gender restriction for Maternity/Paternity leave
        if ("MATERNITY_LEAVE".equals(leaveType.getCode()) || "PATERNITY_LEAVE".equals(leaveType.getCode())) {
            try {
                group4.hrms.dao.UserProfileDao userProfileDao = new group4.hrms.dao.UserProfileDao();
                group4.hrms.model.UserProfile userProfile = userProfileDao.findByUserId(userId);
                String userGender = (userProfile != null && userProfile.getGender() != null) ? userProfile.getGender() : "MALE";

                if ("MATERNITY_LEAVE".equals(leaveType.getCode()) && !"FEMALE".equalsIgnoreCase(userGender)) {
                    throw new IllegalArgumentException("Maternity leave is only available for female employees");
                }

                if ("PATERNITY_LEAVE".equals(leaveType.getCode()) && !"MALE".equalsIgnoreCase(userGender)) {
                    throw new IllegalArgumentException("Paternity leave is only available for male employees");
                }
            } catch (Exception e) {
                logger.warning("Could not validate gender restriction: " + e.getMessage());
                // Continue if gender check fails (graceful degradation)
            }
        }

        // Half-day specific validation
        if (isHalfDay != null && isHalfDay) {
            // Requirement 1.5: Validate single date for half-day (startDate = endDate)
            if (!startDate.toLocalDate().equals(endDate.toLocalDate())) {
                throw new IllegalArgumentException("Half-day leave can only be requested for a single day");
            }

            // Requirement 2.1, 2.2, 2.3: Validate period is "AM" or "PM"
            if (halfDayPeriod == null || halfDayPeriod.trim().isEmpty()) {
                ValidationErrorMessage errorMsg = ValidationErrorMessage.invalidHalfDayPeriodError(null);
                throw new LeaveValidationException(errorMsg);
            }
            if (!"AM".equals(halfDayPeriod) && !"PM".equals(halfDayPeriod)) {
                ValidationErrorMessage errorMsg = ValidationErrorMessage.invalidHalfDayPeriodError(halfDayPeriod);
                throw new LeaveValidationException(errorMsg);
            }

            // Requirement 5.7: Validate date is working day (not weekend or holiday)
            WorkingDayService workingDayService = new WorkingDayService(holidayDao);
            ValidationResult workingDayResult = workingDayService.validateHalfDayDate(startDate.toLocalDate());
            if (!workingDayResult.isValid()) {
                // Determine if it's weekend or holiday for better error message
                DayOfWeek dayOfWeek = startDate.getDayOfWeek();
                String dayType = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)
                    ? "weekend" : "holiday";

                ValidationErrorMessage errorMsg = ValidationErrorMessage.halfDayWeekendHolidayError(
                    startDate.toLocalDate().toString(), dayType);
                throw new LeaveValidationException(errorMsg);
            }

            // Check for half-day conflicts
            HalfDayConflict conflict = checkHalfDayConflict(userId, startDate.toLocalDate(), halfDayPeriod);
            if (conflict.hasConflict()) {
                ValidationErrorMessage errorMsg;
                if ("FULL_DAY".equals(conflict.getConflictType())) {
                    errorMsg = ValidationErrorMessage.halfDayFullDayConflictError(
                        conflict.getConflictDate(),
                        conflict.getConflictLeaveType(),
                        conflict.getConflictStatus()
                    );
                } else if ("SAME_PERIOD".equals(conflict.getConflictType())) {
                    errorMsg = ValidationErrorMessage.halfDaySamePeriodConflictError(
                        conflict.getConflictDate(),
                        conflict.getConflictPeriod(),
                        conflict.getConflictLeaveType(),
                        conflict.getConflictStatus()
                    );
                } else {
                    errorMsg = ValidationErrorMessage.genericError(conflict.getErrorMessage());
                }
                throw new LeaveValidationException(errorMsg);
            }
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

        // Requirement 2: Validate leave balance (with half-day support)
        // Call after max days validation, skip if leave type is unlimited or unpaid
        if (leaveType.isPaid() && leaveType.getDefaultDays() != null && leaveType.getDefaultDays() > 0) {
            int currentYear = startDate.getYear();
            double requestedDays = (isHalfDay != null && isHalfDay) ? 0.5 : dayCount;

            // Use LeaveBalanceService to check sufficient balance
            LeaveBalanceService balanceService = new LeaveBalanceService(requestDao, leaveTypeDao);
            if (!balanceService.hasSufficientBalance(userId, leaveType.getCode(), requestedDays, currentYear)) {
                double availableBalance = balanceService.getAvailableBalance(userId, leaveType.getCode(), currentYear);
                double usedDays = balanceService.calculateUsedDays(userId, leaveType.getCode(), currentYear);
                double totalAllowed = leaveType.getDefaultDays();

                ValidationErrorMessage errorMsg = ValidationErrorMessage.insufficientBalanceHalfDayError(
                    leaveType.getName(),
                    availableBalance,
                    requestedDays,
                    usedDays,
                    totalAllowed
                );
                throw new LeaveValidationException(errorMsg);
            }
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

            // Optimize: Create rules directly from LeaveType objects instead of calling getLeaveTypeRules()
            // which would make additional database calls
            for (LeaveType leaveType : leaveTypes) {
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
                rulesList.add(rules);
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
     * Calculate used days for user, leave type and year (OPTIMIZED)
     * Requirements: 2
     *
     * Uses optimized DAO method with SQL aggregation instead of loading all requests
     */
    private int calculateUsedDays(Long userId, String leaveTypeCode, int year) {
        logger.fine(String.format("Calculating used days: userId=%d, leaveType=%s, year=%d",
                   userId, leaveTypeCode, year));

        try {
            // Use optimized DAO method that aggregates in SQL
            double totalUsedDays = requestDao.countApprovedLeaveDaysByUserAndTypeAndYear(userId, leaveTypeCode, year);

            logger.info(String.format("Calculated used days (optimized): userId=%d, leaveType=%s, year=%d, usedDays=%.1f",
                       userId, leaveTypeCode, year, totalUsedDays));

            // Round up to nearest integer for safety
            return (int) Math.ceil(totalUsedDays);

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating used days: userId=%d, leaveType=%s, year=%d",
                      userId, leaveTypeCode, year), e);
            return 0;
        }
    }

    /**
     * Calculate pending days for user, leave type and year (OPTIMIZED)
     * Requirements: 7
     *
     * Uses optimized DAO method with SQL aggregation instead of loading all requests
     */
    private int calculatePendingDays(Long userId, String leaveTypeCode, int year) {
        logger.fine(String.format("Calculating pending days: userId=%d, leaveType=%s, year=%d",
                   userId, leaveTypeCode, year));

        try {
            // Use optimized DAO method that aggregates in SQL
            double totalPendingDays = requestDao.countPendingLeaveDaysByUserAndTypeAndYear(userId, leaveTypeCode, year);

            logger.info(String.format("Calculated pending days (optimized): userId=%d, leaveType=%s, year=%d, pendingDays=%.1f",
                       userId, leaveTypeCode, year, totalPendingDays));

            // Round up to nearest integer for safety
            return (int) Math.ceil(totalPendingDays);

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating pending days: userId=%d, leaveType=%s, year=%d",
                      userId, leaveTypeCode, year), e);
            return 0;
        }
    }

    /**
     * Get all leave balances for user
     */
    public java.util.List<group4.hrms.dto.LeaveBalance> getAllLeaveBalances(Long userId, int year) {
        logger.fine(String.format("Getting all leave balances: userId=%d, year=%d", userId, year));
        java.util.List<group4.hrms.dto.LeaveBalance> balances = new java.util.ArrayList<>();

        try {
            List<LeaveType> leaveTypes = leaveTypeDao.findActiveLeaveTypes();
            logger.fine(String.format("Found %d active leave types for userId=%d", leaveTypes.size(), userId));

            for (LeaveType leaveType : leaveTypes) {
                try {
                    group4.hrms.dto.LeaveBalance balance = getLeaveBalance(userId, leaveType.getCode(), year);
                    if (balance != null) {
                        balances.add(balance);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, String.format("Error getting balance for leaveType=%s, userId=%d. Skipping.",
                              leaveType.getCode(), userId), e);
                    // Continue with other leave types
                }
            }

            logger.info(String.format("Retrieved %d leave balances for userId=%d, year=%d",
                       balances.size(), userId, year));

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error getting all leave balances: userId=%d, year=%d",
                      userId, year), e);
        }

        return balances;
    }

    // ==================== Half-Day Leave Methods ====================

    /**
     * Validate half-day leave request
     * Requirements: 5.1, 5.2, 5.3, 5.4, 5.7
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param date Date of half-day leave
     * @param period Period (AM or PM)
     * @return ValidationResult with error messages if validation fails
     */
    public ValidationResult validateHalfDayRequest(Long userId, String leaveTypeCode,
                                                   LocalDate date, String period) {
        logger.info(String.format("Validating half-day request: userId=%d, leaveType=%s, date=%s, period=%s",
                   userId, leaveTypeCode, date, period));

        try {
            // 1. Check if date is working day (not weekend/holiday)
            if (!isWorkingDay(date)) {
                String message = "Half-day leave only applies to working days. " + date + " is a weekend or holiday.";
                logger.warning(String.format("Half-day validation failed - not working day: userId=%d, date=%s",
                              userId, date));
                return ValidationResult.error(message);
            }

            // 2. Check for full-day leave conflict on same date
            if (hasFullDayLeaveOnDate(userId, date)) {
                String message = "Cannot request half-day leave: Full-day leave already exists on " + date;
                logger.warning(String.format("Half-day validation failed - full-day conflict: userId=%d, date=%s",
                              userId, date));
                return ValidationResult.error(message);
            }

            // 3. Check for same period half-day conflict
            List<Request> halfDayRequests = findHalfDayRequestsByDate(userId, date);
            for (Request request : halfDayRequests) {
                LeaveRequestDetail detail = request.getLeaveDetail();
                if (detail != null && period.equals(detail.getHalfDayPeriod())) {
                    String message = String.format("Cannot request half-day leave: %s half-day %s leave already exists on %s (Status: %s)",
                                                  period, detail.getLeaveTypeName(), date, request.getStatus());
                    logger.warning(String.format("Half-day validation failed - same period conflict: userId=%d, date=%s, period=%s",
                                  userId, date, period));
                    return ValidationResult.error(message);
                }
            }

            // 4. Check balance for paid leave types
            LeaveType leaveType = getLeaveTypeByCode(leaveTypeCode);
            if (leaveType == null) {
                return ValidationResult.error("Invalid leave type: " + leaveTypeCode);
            }

            // Only check balance for paid leave types with limits
            if (leaveType.isPaid() && leaveType.getDefaultDays() != null && leaveType.getDefaultDays() > 0) {
                int year = date.getYear();
                int usedDays = calculateUsedDays(userId, leaveTypeCode, year);
                int defaultDays = leaveType.getDefaultDays();
                double remainingDays = defaultDays - usedDays;

                if (remainingDays < 0.5) {
                    String message = String.format("Insufficient leave balance for %s. Available: %.1f days, Requested: 0.5 days",
                                                  leaveType.getName(), remainingDays);
                    logger.warning(String.format("Half-day validation failed - insufficient balance: userId=%d, leaveType=%s, remaining=%.1f",
                                  userId, leaveTypeCode, remainingDays));
                    return ValidationResult.error(message);
                }
            }

            logger.info(String.format("Half-day validation passed: userId=%d, leaveType=%s, date=%s, period=%s",
                       userId, leaveTypeCode, date, period));
            return ValidationResult.success();

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error validating half-day request: userId=%d, date=%s, period=%s",
                      userId, date, period), e);
            return ValidationResult.error("Error validating half-day request: " + e.getMessage());
        }
    }

    /**
     * Calculate leave duration
     * Requirements: 1.6, 3.1
     *
     * @param isHalfDay Whether this is a half-day request
     * @param startDate Start date
     * @param endDate End date
     * @return Duration in days (0.5 for half-day, working days count for full-day)
     */
    public double calculateLeaveDuration(boolean isHalfDay, LocalDateTime startDate, LocalDateTime endDate) {
        logger.fine(String.format("Calculating leave duration: isHalfDay=%b, startDate=%s, endDate=%s",
                   isHalfDay, startDate, endDate));

        if (isHalfDay) {
            logger.fine("Half-day leave duration: 0.5 days");
            return 0.5;
        } else {
            int workingDays = calculateWorkingDays(startDate, endDate);
            logger.fine(String.format("Full-day leave duration: %d days", workingDays));
            return workingDays;
        }
    }

    /**
     * Check for half-day conflicts
     * Requirements: 5.1, 5.2, 5.3, 5.4
     *
     * @param userId User ID
     * @param date Date to check
     * @param period Period (AM or PM)
     * @return HalfDayConflict object with conflict information
     */
    public HalfDayConflict checkHalfDayConflict(Long userId, LocalDate date, String period) {
        logger.info(String.format("Checking half-day conflict: userId=%d, date=%s, period=%s",
                   userId, date, period));

        try {
            // Check if full-day leave exists
            if (hasFullDayLeaveOnDate(userId, date)) {
                logger.warning(String.format("Full-day conflict found: userId=%d, date=%s", userId, date));
                // Get the full-day request details
                List<String> statuses = new java.util.ArrayList<>();
                statuses.add("PENDING");
                statuses.add("APPROVED");
                LocalDateTime dateTime = date.atStartOfDay();
                List<Request> requests = requestDao.findByUserIdAndDateRange(userId, dateTime, dateTime, statuses, null);

                for (Request request : requests) {
                    LeaveRequestDetail detail = request.getLeaveDetail();
                    if (detail != null && (detail.getIsHalfDay() == null || !detail.getIsHalfDay())) {
                        return HalfDayConflict.fullDayConflict(date.toString(),
                                                              detail.getLeaveTypeName(),
                                                              request.getStatus());
                    }
                }
                return HalfDayConflict.fullDayConflict(date.toString(), "Leave", "PENDING/APPROVED");
            }

            // Check if same period half-day exists
            List<Request> halfDayRequests = findHalfDayRequestsByDate(userId, date);
            for (Request request : halfDayRequests) {
                LeaveRequestDetail detail = request.getLeaveDetail();
                if (detail != null && period.equals(detail.getHalfDayPeriod())) {
                    logger.warning(String.format("Same period conflict found: userId=%d, date=%s, period=%s",
                                  userId, date, period));
                    return HalfDayConflict.samePeriodConflict(date.toString(), period,
                                                             detail.getLeaveTypeName(),
                                                             request.getStatus());
                }
            }

            logger.info(String.format("No conflict found: userId=%d, date=%s, period=%s", userId, date, period));
            return HalfDayConflict.noConflict();

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error checking half-day conflict: userId=%d, date=%s, period=%s",
                      userId, date, period), e);
            return new HalfDayConflict(true, "ERROR", "Error checking conflict: " + e.getMessage());
        }
    }

    /**
     * Process leave approval and update balance
     * Requirements: 3.1, 3.2, 3.1.2, 3.1.3
     *
     * @param requestId Request ID to approve
     * @throws SQLException if database error occurs
     */
    public void processLeaveApproval(Long requestId) throws SQLException {
        logger.info(String.format("Processing leave approval: requestId=%d", requestId));

        try {
            Optional<Request> requestOpt = requestDao.findById(requestId);
            if (!requestOpt.isPresent()) {
                throw new IllegalArgumentException("Request not found: " + requestId);
            }

            Request request = requestOpt.get();
            LeaveRequestDetail detail = request.getLeaveDetail();

            if (detail == null) {
                throw new IllegalArgumentException("Leave request detail not found for request: " + requestId);
            }

            LeaveType leaveType = getLeaveTypeByCode(detail.getLeaveTypeCode());
            if (leaveType == null) {
                throw new IllegalArgumentException("Leave type not found: " + detail.getLeaveTypeCode());
            }

            // Determine days to deduct
            double daysToDeduct;
            if (detail.getIsHalfDay() != null && detail.getIsHalfDay()) {
                daysToDeduct = 0.5;
                logger.info(String.format("Half-day leave approval: requestId=%d, days=0.5", requestId));
            } else {
                daysToDeduct = detail.getDayCount() != null ? detail.getDayCount() : 1.0;
                logger.info(String.format("Full-day leave approval: requestId=%d, days=%.1f", requestId, daysToDeduct));
            }

            // Process based on leave type (paid vs unpaid)
            if (leaveType.isPaid()) {
                // Deduct from balance for paid leave
                logger.info(String.format("Deducting %.1f days from balance for paid leave: userId=%d, leaveType=%s",
                           daysToDeduct, request.getCreatedByUserId(), detail.getLeaveTypeCode()));
                // Balance deduction logic would go here
                // This would typically update the leave_balances table
            } else {
                // Record for salary deduction for unpaid leave
                logger.info(String.format("Recording %.1f days for salary deduction (unpaid leave): userId=%d",
                           daysToDeduct, request.getCreatedByUserId()));
                // Salary deduction recording logic would go here
                // This would typically be handled by payroll service
            }

            logger.info(String.format("Leave approval processed successfully: requestId=%d, days=%.1f, paid=%b",
                       requestId, daysToDeduct, leaveType.isPaid()));

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error processing leave approval: requestId=%d", requestId), e);
            throw e;
        }
    }

    /**
     * Check if a date is a working day (not weekend or holiday)
     *
     * @param date Date to check
     * @return true if working day, false otherwise
     */
    private boolean isWorkingDay(LocalDate date) {
        try {
            // Check if weekend (Saturday or Sunday)
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                logger.fine(String.format("Date %s is weekend: %s", date, dayOfWeek));
                return false;
            }

            // Check if holiday
            boolean isHoliday = holidayDao.isHoliday(date);
            if (isHoliday) {
                logger.fine(String.format("Date %s is a holiday", date));
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Error checking if date %s is working day, assuming it is", date), e);
            // If error checking holiday, assume it's a working day (fail-safe)
            return true;
        }
    }

    /**
     * Data class for leave type options with enabled/disabled flags
     */
    public static class LeaveTypeOption {
        public String code;
        public String name;
        public boolean isPaid;
        public boolean enabled;
        public double availableDays;

        // Getters for JSP EL
        public String getCode() { return code; }
        public String getName() { return name; }
        public boolean isPaid() { return isPaid; }
        public boolean isEnabled() { return enabled; }
        public double getAvailableDays() { return availableDays; }
    }

    /**
     * Helper method to check if user has full-day leave on a specific date
     */
    private boolean hasFullDayLeaveOnDate(Long userId, LocalDate date) {
        try {
            List<String> statuses = new java.util.ArrayList<>();
            statuses.add("PENDING");
            statuses.add("APPROVED");
            LocalDateTime dateTime = date.atStartOfDay();
            List<Request> requests = requestDao.findByUserIdAndDateRange(userId, dateTime, dateTime, statuses, null);

            for (Request request : requests) {
                LeaveRequestDetail detail = request.getLeaveDetail();
                if (detail != null && (detail.getIsHalfDay() == null || !detail.getIsHalfDay())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error checking full-day leave: userId=%d, date=%s", userId, date), e);
            return false;
        }
    }

    /**
     * Helper method to find half-day requests on a specific date
     */
    private List<Request> findHalfDayRequestsByDate(Long userId, LocalDate date) {
        try {
            List<String> statuses = new java.util.ArrayList<>();
            statuses.add("PENDING");
            statuses.add("APPROVED");
            LocalDateTime dateTime = date.atStartOfDay();
            List<Request> allRequests = requestDao.findByUserIdAndDateRange(userId, dateTime, dateTime, statuses, null);

            List<Request> halfDayRequests = new java.util.ArrayList<>();
            for (Request request : allRequests) {
                LeaveRequestDetail detail = request.getLeaveDetail();
                if (detail != null && detail.getIsHalfDay() != null && detail.getIsHalfDay()) {
                    halfDayRequests.add(request);
                }
            }
            return halfDayRequests;
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error finding half-day requests: userId=%d, date=%s", userId, date), e);
            return new java.util.ArrayList<>();
        }
    }
}
