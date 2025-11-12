package group4.hrms.service;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.HolidayDao;
import group4.hrms.dao.LeaveBalanceDao;
import group4.hrms.dao.LeaveTypeDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dto.HalfDayConflict;
import group4.hrms.dto.LeaveRequestDetail;
import group4.hrms.dto.ValidationResult;
import group4.hrms.exception.LeaveValidationException;
import group4.hrms.exception.ValidationErrorMessage;
import group4.hrms.model.LeaveType;
import group4.hrms.model.Request;
import group4.hrms.model.RequestType;
import group4.hrms.util.RequestTypeInitializer;

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
    private final group4.hrms.dao.UserDao userDao;

    public LeaveRequestService(RequestDao requestDao, RequestTypeDao requestTypeDao, LeaveTypeDao leaveTypeDao) {
        this.requestDao = requestDao;
        this.requestTypeDao = requestTypeDao;
        this.leaveTypeDao = leaveTypeDao;
        this.holidayDao = new HolidayDao();
        this.leaveBalanceDao = new LeaveBalanceDao();
        this.userDao = new group4.hrms.dao.UserDao();
    }

    /**
     * Create leave request - JSON-based approach with half-day support
     */
    public Long createLeaveRequest(Long accountId, Long userId, Long departmentId,
            String requestTitle, String leaveTypeCode, LocalDateTime startDate, LocalDateTime endDate, String reason,
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
            leaveRequest.setTitle(requestTitle != null && !requestTitle.trim().isEmpty()
                ? requestTitle.trim()
                : "Leave Request - " + leaveType.getName());
            leaveRequest.setLeaveDetail(detail);  // Set JSON data
            leaveRequest.setCreatedByAccountId(accountId);
            leaveRequest.setCreatedByUserId(userId);
            leaveRequest.setDepartmentId(departmentId);
            leaveRequest.setStatus("PENDING");
            leaveRequest.setCreatedAt(LocalDateTime.now());
            leaveRequest.setUpdatedAt(LocalDateTime.now());

            // 7. Save to database
            Request savedRequest = requestDao.save(leaveRequest);

            // 8. Determine approver and auto-approve if top-level
            try {
                group4.hrms.dao.UserDao userDao = new group4.hrms.dao.UserDao();
                group4.hrms.dao.AccountDao accountDao = new group4.hrms.dao.AccountDao();
                ApproverService approverService = new ApproverService(userDao, accountDao);

                Long approverId = approverService.findApprover(userId);

                if (approverId != null) {
                    // Found approver - set and keep status PENDING
                    savedRequest.setCurrentApproverAccountId(approverId);
                    requestDao.update(savedRequest);
                    logger.info(String.format("Approver set for request: requestId=%d, approverId=%d",
                               savedRequest.getId(), approverId));
                } else {
                    // No approver found - user is top-level (HRM/Department Head)
                    // Auto-approve immediately
                    savedRequest.setStatus("APPROVED");
                    savedRequest.setCurrentApproverAccountId(accountId);
                    savedRequest.setApproveReason("Auto-approved (top-level manager)");
                    requestDao.update(savedRequest);

                    logger.info(String.format("Auto-approved leave request for top-level user: requestId=%d, userId=%d",
                               savedRequest.getId(), userId));

                    // Process leave balance for paid leave types
                    if (leaveType.isPaid()) {
                        processLeaveApproval(savedRequest.getId());
                        logger.info(String.format("Leave balance updated for auto-approved request: requestId=%d",
                                   savedRequest.getId()));
                    }
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, String.format("Error setting approver for request: requestId=%d",
                          savedRequest.getId()), e);
                // Continue anyway - request is created, approver can be set manually
            }

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
     * Supports half-day leave (0.5 days)
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code
     * @param requestedDays Number of days requested (supports decimal for half-day: 0.5, 1.0, 1.5, etc.)
     * @param year Year to check balance
     * @throws IllegalArgumentException if balance is insufficient
     */
    public void validateLeaveBalance(Long userId, String leaveTypeCode,
                                      double requestedDays, int year) {
        logger.fine(String.format("Validating leave balance: userId=%d, leaveType=%s, requestedDays=%.1f, year=%d",
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

            // Calculate seniority bonus based on user's join date
            int seniorityBonus = calculateSeniorityBonus(userId, leaveTypeCode);

            // Calculate total allowed days
            int totalAllowed = defaultDays + seniorityBonus;

            // Calculate used days from APPROVED requests in the year (supports half-day decimal)
            double usedDays = calculateUsedDays(userId, leaveTypeCode, year);

            // Calculate remaining days (supports decimal)
            double remainingDays = totalAllowed - usedDays;

            logger.fine(String.format("Balance calculation: userId=%d, leaveType=%s, total=%d, used=%.1f, remaining=%.1f, requested=%.1f",
                       userId, leaveTypeCode, totalAllowed, usedDays, remainingDays, requestedDays));

            // Validate if requested days exceed remaining days (supports decimal comparison for half-day)
            if (requestedDays > remainingDays) {
                logger.warning(String.format("Insufficient leave balance: userId=%d, leaveType=%s, requested=%.1f, remaining=%.1f, used=%.1f, total=%d",
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

            logger.info(String.format("Leave balance validation passed: userId=%d, leaveType=%s, requested=%.1f, remaining=%.1f",
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
     * Validate leave request for conflicts when approving
     * This method is called during the approval process to ensure no conflicts exist
     * Checks conflicts with both PENDING and APPROVED requests to prevent conflicts early
     *
     * @param userId User ID
     * @param startDateTime Start date/time of leave request
     * @param endDateTime End date/time of leave request
     * @param excludeRequestId Request ID to exclude from checks (the request being approved)
     * @param isHalfDay Whether this is a half-day leave
     * @param halfDayPeriod Period for half-day leave ("AM" or "PM")
     * @throws IllegalArgumentException if conflicts are detected
     * @throws LeaveValidationException if validation fails
     */
    public void validateLeaveConflictsForApproval(Long userId, LocalDateTime startDateTime,
                                                   LocalDateTime endDateTime, Long excludeRequestId,
                                                   Boolean isHalfDay, String halfDayPeriod) {
        logger.info(String.format("Validating leave conflicts for approval: userId=%d, requestId=%d, isHalfDay=%s, period=%s",
                   userId, excludeRequestId, isHalfDay, halfDayPeriod));

        // Check for overlapping leave requests
        // This already checks PENDING and APPROVED leaves
        checkLeaveOverlap(userId, startDateTime, endDateTime, excludeRequestId, isHalfDay, halfDayPeriod);

        // Check for conflicts with OT requests (PENDING + APPROVED)
        // The main method now checks both PENDING and APPROVED
        checkConflictWithOT(userId, startDateTime, endDateTime, isHalfDay, halfDayPeriod);

        logger.info(String.format("Leave conflict validation passed for approval: userId=%d, requestId=%d",
                   userId, excludeRequestId));
    }

    /**
     * Check for overlapping leave requests (with half-day support)
     * Requirements: 1
     *
     * Half-day overlap rules:
     * - Half-day AM + Half-day PM (same date) = ALLOWED (different periods)
     * - Half-day AM + Half-day AM (same date) = BLOCKED (same period)
     * - Full-day + Any half-day (same date) = BLOCKED
     *
     * @param userId User ID
     * @param startDate Start date of leave request
     * @param endDate End date of leave request
     * @param excludeRequestId Request ID to exclude (for update scenarios)
     * @param isHalfDay Whether the new request is a half-day leave
     * @param halfDayPeriod Period of the new half-day leave ("AM" or "PM")
     * @throws IllegalArgumentException if overlap is detected
     */
    private void checkLeaveOverlap(Long userId, LocalDateTime startDate,
                                   LocalDateTime endDate, Long excludeRequestId,
                                   Boolean isHalfDay, String halfDayPeriod) {
        logger.info(String.format("=== CHECKING LEAVE OVERLAP === userId=%d, startDate=%s, endDate=%s, excludeId=%s, isHalfDay=%s, period=%s",
                   userId, startDate, endDate, excludeRequestId, isHalfDay, halfDayPeriod));

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

            // Check each overlapping request
            for (Request existingRequest : overlappingRequests) {
                LeaveRequestDetail existingDetail = existingRequest.getLeaveDetail();

                if (existingDetail == null) {
                    // No detail available, assume overlap
                    logger.warning(String.format("Leave overlap detected (no detail): userId=%d, existingRequestId=%d, status=%s",
                                  userId, existingRequest.getId(), existingRequest.getStatus()));

                    ValidationErrorMessage errorMsg = ValidationErrorMessage.overlapError(
                        existingRequest.getTitle(),
                        existingRequest.getStatus(),
                        "N/A",
                        "N/A"
                    );
                    throw new LeaveValidationException(errorMsg);
                }

                    String existingStartDate = existingDetail.getStartDate();
                    String existingEndDate = existingDetail.getEndDate();
                    String existingLeaveType = existingDetail.getLeaveTypeName();
                Boolean existingIsHalfDay = existingDetail.getIsHalfDay();
                String existingPeriod = existingDetail.getHalfDayPeriod();

                // CASE 1: Both are half-day leaves on the same date
                if (isHalfDay != null && isHalfDay && existingIsHalfDay != null && existingIsHalfDay) {
                    // Check if they're on the same date
                    java.time.LocalDate newLeaveDate = startDate.toLocalDate();
                    java.time.LocalDate existingLeaveDate = java.time.LocalDateTime.parse(existingStartDate).toLocalDate();

                    if (newLeaveDate.equals(existingLeaveDate)) {
                        // Same date: check if different periods
                        if (halfDayPeriod != null && existingPeriod != null && !halfDayPeriod.equals(existingPeriod)) {
                            // Different periods (AM vs PM) → ALLOWED
                            logger.info(String.format("✓ Half-day leaves on same date but different periods: new=%s, existing=%s. ALLOWED.",
                                       halfDayPeriod, existingPeriod));
                            continue; // Skip this overlap, it's allowed
                        } else {
                            // Same period (AM+AM or PM+PM) → BLOCKED
                            logger.warning(String.format("Half-day overlap - same period: userId=%d, date=%s, period=%s",
                                          userId, newLeaveDate, halfDayPeriod));

                    ValidationErrorMessage errorMsg = ValidationErrorMessage.overlapError(
                                existingLeaveType + " (" + existingPeriod + " half-day)",
                        existingRequest.getStatus(),
                        existingStartDate,
                        existingEndDate
                    );
                    throw new LeaveValidationException(errorMsg);
                        }
                    }
                }

                // CASE 2: At least one is full-day, or different dates → normal overlap check
                logger.warning(String.format("Leave overlap detected: userId=%d, existingRequestId=%d, existingType=%s, existingDates=%s to %s, status=%s",
                              userId, existingRequest.getId(), existingLeaveType, existingStartDate, existingEndDate, existingRequest.getStatus()));

                    ValidationErrorMessage errorMsg = ValidationErrorMessage.overlapError(
                    existingLeaveType,
                        existingRequest.getStatus(),
                    existingStartDate,
                    existingEndDate
                    );
                    throw new LeaveValidationException(errorMsg);
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
     * Check for conflicts with OT requests (PENDING + APPROVED, with half-day support)
     * This checks both PENDING and APPROVED OT to prevent conflicts early
     * Used in both creation and approval flows
     * Requirements: 4, BR-22, BR-LV-11
     *
     * Rules:
     * - Full-day leave: Block all OT on that date
     * - Half-day AM (8:00-12:00): Block OT overlapping with morning, allow after 12:00
     * - Half-day PM (13:00-17:00): Block OT overlapping with afternoon, allow after 17:00
     * - Two half-days (AM+PM): Treat as full-day, block all OT
     *
     * @param userId User ID
     * @param startDate Start date of leave request
     * @param endDate End date of leave request
     * @param isHalfDay Whether this is a half-day leave
     * @param halfDayPeriod Period of half-day ("AM" or "PM")
     * @throws IllegalArgumentException if conflict with OT is detected
     */
    private void checkConflictWithOT(Long userId, LocalDateTime startDate,
                                     LocalDateTime endDate, Boolean isHalfDay, String halfDayPeriod) {
        logger.fine(String.format("Checking OT conflict (PENDING + APPROVED): userId=%d, startDate=%s, endDate=%s, isHalfDay=%s, period=%s",
                   userId, startDate, endDate, isHalfDay, halfDayPeriod));

        try {
            // Query BOTH PENDING and APPROVED OT requests in the date range
            List<Request> allOTRequests = requestDao.findByUserId(userId);

            List<Request> otRequests = allOTRequests.stream()
                .filter(r -> r.getRequestTypeId() != null && r.getRequestTypeId() == 7L) // OT requests
                .filter(r -> "PENDING".equals(r.getStatus()) || "APPROVED".equals(r.getStatus()))
                .filter(r -> {
                    group4.hrms.dto.OTRequestDetail otDetail = r.getOtDetail();
                    if (otDetail == null || otDetail.getOtDate() == null) return false;

                    java.time.LocalDate otDate = java.time.LocalDate.parse(otDetail.getOtDate());
                    java.time.LocalDate leaveStart = startDate.toLocalDate();
                    java.time.LocalDate leaveEnd = endDate.toLocalDate();

                    return !otDate.isBefore(leaveStart) && !otDate.isAfter(leaveEnd);
                })
                .collect(java.util.stream.Collectors.toList());

            if (otRequests.isEmpty()) {
                logger.fine("No OT requests found in date range");
                return; // No conflict
            }

            // If creating full-day leave → block all OT in date range
            if (isHalfDay == null || !isHalfDay) {
                Request otRequest = otRequests.get(0);
                group4.hrms.dto.OTRequestDetail otDetail = otRequest.getOtDetail();
                String otStatus = otRequest.getStatus();

                if (otDetail != null) {
                    logger.warning(String.format("Full-day leave conflicts with %s OT: userId=%d, otDate=%s, time=%s-%s",
                                  otStatus, userId, otDetail.getOtDate(), otDetail.getStartTime(), otDetail.getEndTime()));

                    throw new IllegalArgumentException(
                        String.format("Cannot create leave request! There is a %s OT request on %s (%s-%s, %.1f hours). " +
                                     "Please cancel or wait for the OT request to be resolved first.",
                                     otStatus, otDetail.getOtDate(), otDetail.getStartTime(), otDetail.getEndTime(), otDetail.getOtHours())
                    );
                } else {
                    throw new IllegalArgumentException(
                        "Cannot create leave request! There is a " + otStatus + " OT request on the same date."
                    );
                }
            }

            // Half-day leave: Check if combined with existing APPROVED half-day creates full-day
            // that would conflict with OT
            java.time.LocalDate leaveDate = startDate.toLocalDate();

            // Check if there's already another APPROVED half-day leave on the same date
            List<Request> allRequests = requestDao.findByUserId(userId);
            boolean hasOtherApprovedHalfDay = false;
            String otherPeriod = null;

            for (Request request : allRequests) {
                // Check both APPROVED and PENDING leaves to prevent conflicts
                if (!"APPROVED".equals(request.getStatus()) && !"PENDING".equals(request.getStatus())) {
                    continue;
                }

                group4.hrms.dto.LeaveRequestDetail existingLeave = request.getLeaveDetail();
                if (existingLeave == null) {
                    continue;
                }

                // Check if this leave is on the same date
                String existingStartStr = existingLeave.getStartDate();
                if (existingStartStr == null || existingStartStr.length() < 10) {
                    continue;
                }

                java.time.LocalDate existingDate = java.time.LocalDate.parse(existingStartStr.substring(0, 10));
                if (!existingDate.equals(leaveDate)) {
                    continue; // Different date
                }

                // Check if it's a half-day
                Boolean existingIsHalfDay = existingLeave.getIsHalfDay();
                String existingPeriod = existingLeave.getHalfDayPeriod();

                if (existingIsHalfDay != null && existingIsHalfDay && existingPeriod != null) {
                    // Found another APPROVED half-day on same date
                    if (!existingPeriod.equals(halfDayPeriod)) {
                        // Different period (AM vs PM) → would create full-day when approved
                        hasOtherApprovedHalfDay = true;
                        otherPeriod = existingPeriod;
                        break;
                    }
                }
            }

            // If creating second half-day that would combine with APPROVED one to make full-day
            // → check if this would conflict with OT
            if (hasOtherApprovedHalfDay && !otRequests.isEmpty()) {
                Request otRequest = otRequests.get(0);
                group4.hrms.dto.OTRequestDetail otDetail = otRequest.getOtDetail();
                String otStatus = otRequest.getStatus();

                logger.warning(String.format("Two half-days (AM+PM) would conflict with %s OT: userId=%d, date=%s, existing=%s, new=%s",
                              otStatus, userId, leaveDate, otherPeriod, halfDayPeriod));

                String errorMsg = String.format(
                    "Cannot create %s half-day leave on %s! " +
                    "You already have an APPROVED %s half-day leave on this date. " +
                    "When both are approved, this equals a full-day leave and conflicts with " +
                    "a %s OT request at %s-%s (%.1f hours). " +
                    "Please cancel or wait for the OT request to be resolved first, or choose a different date.",
                    halfDayPeriod, leaveDate, otherPeriod, otStatus,
                    otDetail != null ? otDetail.getStartTime() : "?",
                    otDetail != null ? otDetail.getEndTime() : "?",
                    otDetail != null ? otDetail.getOtHours() : 0.0
                );

                throw new IllegalArgumentException(errorMsg);
            }

            // Half-day leave: Check OT time overlap
            for (Request otRequest : otRequests) {
                group4.hrms.dto.OTRequestDetail otDetail = otRequest.getOtDetail();
                if (otDetail == null) {
                    continue;
                }

                String otStartTime = otDetail.getStartTime();
                String otEndTime = otDetail.getEndTime();

                if (otStartTime == null || otEndTime == null) {
                    continue;
                }

                java.time.LocalTime otStart = java.time.LocalTime.parse(otStartTime);
                java.time.LocalTime otEnd = java.time.LocalTime.parse(otEndTime);

                // Check overlap based on half-day period
                boolean hasOverlap = false;

                if ("AM".equals(halfDayPeriod)) {
                    // Morning: 8:00-12:00
                    java.time.LocalTime amStart = java.time.LocalTime.of(8, 0);
                    java.time.LocalTime amEnd = java.time.LocalTime.of(12, 0);

                    // Check if OT overlaps with AM period
                    if (otStart.isBefore(amEnd) && amStart.isBefore(otEnd)) {
                        hasOverlap = true;
                    }
                } else if ("PM".equals(halfDayPeriod)) {
                    // Afternoon: 13:00-17:00
                    java.time.LocalTime pmStart = java.time.LocalTime.of(13, 0);
                    java.time.LocalTime pmEnd = java.time.LocalTime.of(17, 0);

                    // Check if OT overlaps with PM period
                    if (otStart.isBefore(pmEnd) && pmStart.isBefore(otEnd)) {
                        hasOverlap = true;
                    }
                }

                if (hasOverlap) {
                    String otStatus = otRequest.getStatus();
                    logger.warning(String.format("Half-day leave conflicts with %s OT: userId=%d, period=%s, otTime=%s-%s",
                                  otStatus, userId, halfDayPeriod, otStartTime, otEndTime));

                    throw new IllegalArgumentException(
                        String.format("Cannot create %s half-day leave on %s! " +
                                     "There is a %s OT request at %s-%s (%.1f hours) that overlaps with the %s leave period. " +
                                     "Please cancel or wait for the OT request to be resolved first.",
                                     halfDayPeriod, otDetail.getOtDate(), otStatus,
                                     otStartTime, otEndTime, otDetail.getOtHours(), halfDayPeriod)
                    );
                }
            }

            logger.fine(String.format("No OT conflict for half-day leave: userId=%d, period=%s",
                       userId, halfDayPeriod));

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation exceptions
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error checking OT conflict: userId=%d, startDate=%s",
                      userId, startDate), e);
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

        // Validate day count is positive (after excluding weekends and holidays)
        if (dayCount <= 0) {
            String errorMessage = "The selected date range contains no working days. ";
            if (isHalfDay != null && isHalfDay) {
                errorMessage += "Half-day leave can only be requested on working days (Monday-Friday, excluding holidays).";
            } else {
                errorMessage += String.format(
                    "All dates from %s to %s fall on weekends or holidays. " +
                    "Please select a date range that includes at least one working day.",
                    startDate.toLocalDate(), endDate.toLocalDate()
                );
            }
            throw new IllegalArgumentException(errorMessage);
        }

        // Requirement 4.7: Validate duration does not exceed max_days limit (for all leave types)
        // Both paid and unpaid leave types can have max_days limits per request
        if (leaveType.getMaxDays() != null) {
            int effectiveMaxDays = leaveType.getMaxDays();

            // Special handling for Annual Leave: max_days should include seniority bonus
            if ("ANNUAL".equals(leaveType.getCode())) {
                int seniorityBonus = calculateSeniorityBonus(userId, leaveType.getCode());
                effectiveMaxDays = leaveType.getDefaultDays() + seniorityBonus;

                logger.fine(String.format("Annual Leave max_days calculation: userId=%d, base=%d, seniority=%d, total=%d",
                           userId, leaveType.getDefaultDays(), seniorityBonus, effectiveMaxDays));
            }

            if (dayCount > effectiveMaxDays) {
                throw new IllegalArgumentException(
                        "Cannot request more than " + effectiveMaxDays + " days for " + leaveType.getName()
                        + " (requested: " + dayCount + " days)");
            }
        }

        // Requirement BR-LV-13: Validate monthly limit for unpaid leave (13 days per month)
        if (!leaveType.isPaid() && "UNPAID".equals(leaveType.getCode())) {
            validateUnpaidLeaveMonthlyLimit(userId, startDate, dayCount);
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

        // Requirement 1: Check for overlapping leave requests (with half-day support)
        // Call after date logic validation
        checkLeaveOverlap(userId, startDate, endDate, null, isHalfDay, halfDayPeriod);

        // Requirement 4: Check for conflict with approved OT requests (with half-day support)
        // Call after overlap check
        checkConflictWithOT(userId, startDate, endDate, isHalfDay, halfDayPeriod);
    }

    /**
     * Calculate working days (excluding weekends and holidays)
     * Requirement: Count only actual working days for leave duration
     */
    private int calculateWorkingDays(LocalDateTime start, LocalDateTime end) {
        int workingDays = 0;
        LocalDateTime current = start.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endDate = end.truncatedTo(ChronoUnit.DAYS);

        while (!current.isAfter(endDate)) {
            LocalDate currentDate = current.toLocalDate();

            // Monday = 1, Sunday = 7
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            // Check if it's a weekday (Monday to Friday)
            if (dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5) {
                // Check if it's NOT a holiday
                try {
                    boolean isHoliday = holidayDao.isHoliday(currentDate);
                    if (!isHoliday) {
                        workingDays++;
                    } else {
                        logger.fine(String.format("Skipping holiday: %s", currentDate));
                    }
                } catch (Exception e) {
                    // If can't check holiday, count as working day (fail-safe)
                    logger.log(Level.WARNING, String.format("Error checking holiday for %s, counting as working day", currentDate), e);
                    workingDays++;
                }
            }

            current = current.plusDays(1);
        }

        logger.fine(String.format("Calculated %d working days between %s and %s (excluding weekends and holidays)",
                    workingDays, start.toLocalDate(), end.toLocalDate()));
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

            // Calculate seniority bonus based on user's join date
            int seniorityBonus = calculateSeniorityBonus(userId, leaveTypeCode);

            // Calculate used days from approved requests in the year (supports decimal for half-day)
            double usedDays = calculateUsedDays(userId, leaveTypeCode, year);

            // Calculate pending days from PENDING requests in the year (supports decimal for half-day)
            double pendingDays = calculatePendingDays(userId, leaveTypeCode, year);

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
    private double calculateUsedDays(Long userId, String leaveTypeCode, int year) {
        logger.fine(String.format("Calculating used days: userId=%d, leaveType=%s, year=%d",
                   userId, leaveTypeCode, year));

        try {
            // Use optimized DAO method that aggregates in SQL
            double totalUsedDays = requestDao.countApprovedLeaveDaysByUserAndTypeAndYear(userId, leaveTypeCode, year);

            logger.info(String.format("Calculated used days (optimized): userId=%d, leaveType=%s, year=%d, usedDays=%.1f",
                       userId, leaveTypeCode, year, totalUsedDays));

            // Return exact value (supports half-day: 0.5, 1.5, etc.)
            return totalUsedDays;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating used days: userId=%d, leaveType=%s, year=%d",
                      userId, leaveTypeCode, year), e);
            return 0.0;
        }
    }

    /**
     * Calculate pending days for user, leave type and year (OPTIMIZED)
     * Requirements: 7
     *
     * Uses optimized DAO method with SQL aggregation instead of loading all requests
     */
    private double calculatePendingDays(Long userId, String leaveTypeCode, int year) {
        logger.fine(String.format("Calculating pending days: userId=%d, leaveType=%s, year=%d",
                   userId, leaveTypeCode, year));

        try {
            // Use optimized DAO method that aggregates in SQL
            double totalPendingDays = requestDao.countPendingLeaveDaysByUserAndTypeAndYear(userId, leaveTypeCode, year);

            logger.info(String.format("Calculated pending days (optimized): userId=%d, leaveType=%s, year=%d, pendingDays=%.1f",
                       userId, leaveTypeCode, year, totalPendingDays));

            // Return exact value (supports half-day: 0.5, 1.5, etc.)
            return totalPendingDays;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating pending days: userId=%d, leaveType=%s, year=%d",
                      userId, leaveTypeCode, year), e);
            return 0.0;
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
                double usedDays = calculateUsedDays(userId, leaveTypeCode, year);
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

    /**
     * Calculate seniority bonus based on user's join date and company policy
     *
     * Company Policy for Annual Leave:
     * - 0-4 years: 0 extra days
     * - 5+ years: +1 day for every 5 years of service
     *
     * Examples:
     * - 4 years: 0 bonus days
     * - 5 years: 1 bonus day
     * - 9 years: 1 bonus day
     * - 10 years: 2 bonus days
     * - 15 years: 3 bonus days
     *
     * @param userId User ID
     * @param leaveTypeCode Leave type code (only applies to ANNUAL)
     * @return Seniority bonus days
     */
    public int calculateSeniorityBonus(Long userId, String leaveTypeCode) {
        logger.fine(String.format("Calculating seniority bonus: userId=%d, leaveType=%s", userId, leaveTypeCode));

        try {
            // Only apply seniority bonus to Annual Leave
            if (!"ANNUAL".equals(leaveTypeCode)) {
                logger.fine(String.format("Seniority bonus not applicable for leave type: %s", leaveTypeCode));
                return 0;
            }

            // Get user information
            java.util.Optional<group4.hrms.model.User> userOpt = userDao.findById(userId);
            if (!userOpt.isPresent()) {
                logger.warning(String.format("User not found for seniority calculation: userId=%d", userId));
                return 0;
            }

            group4.hrms.model.User user = userOpt.get();
            java.time.LocalDate joinDate = user.getDateJoined();

            logger.info(String.format("DEBUG: User %d dateJoined=%s", userId, joinDate));

            // Fallback to startWorkDate if dateJoined is null
            if (joinDate == null) {
                joinDate = user.getStartWorkDate();
                logger.info(String.format("DEBUG: User %d using startWorkDate=%s", userId, joinDate));
            }

            if (joinDate == null) {
                logger.warning(String.format("No join date found for user: userId=%d", userId));
                return 0;
            }

            // Calculate years of service
            java.time.LocalDate currentDate = java.time.LocalDate.now();
            long yearsOfService = java.time.temporal.ChronoUnit.YEARS.between(joinDate, currentDate);

            logger.fine(String.format("User service calculation: userId=%d, joinDate=%s, yearsOfService=%d",
                       userId, joinDate, yearsOfService));

            // Apply company policy: 1 bonus day for every 5 years of service
            int seniorityBonus = 0;
            if (yearsOfService >= 5) {
                seniorityBonus = (int) (yearsOfService / 5); // Integer division: 5-9 years = 1 day, 10-14 years = 2 days, etc.
            }

            logger.info(String.format("Seniority bonus calculated: userId=%d, yearsOfService=%d, bonus=%d days",
                       userId, yearsOfService, seniorityBonus));

            return seniorityBonus;

        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Error calculating seniority bonus: userId=%d, leaveType=%s",
                      userId, leaveTypeCode), e);
            return 0; // Return 0 on error to be safe
        }
    }

    /**
     * Validate monthly limit for unpaid leave (BR-LV-13)
     *
     * Business Rule: Unpaid leave is limited to 13 working days per month
     * to prevent abuse and ensure business continuity.
     *
     * @param userId User ID
     * @param startDate Start date of the new request
     * @param requestedDays Number of days being requested
     * @throws IllegalArgumentException if monthly limit would be exceeded
     */
    private void validateUnpaidLeaveMonthlyLimit(Long userId, LocalDateTime startDate, int requestedDays) {
        logger.info(String.format("Validating unpaid leave monthly limit: userId=%d, month=%d-%d, requestedDays=%d",
                   userId, startDate.getYear(), startDate.getMonthValue(), requestedDays));

        try {
            int year = startDate.getYear();
            int month = startDate.getMonthValue();

            // Calculate used unpaid leave days in the current month (APPROVED only)
            double usedDaysInMonth = calculateUnpaidLeaveDaysInMonth(userId, year, month);

            // Monthly limit for unpaid leave
            final int MONTHLY_LIMIT = 13;

            // Check if new request would exceed monthly limit
            double totalAfterRequest = usedDaysInMonth + requestedDays;

            logger.fine(String.format("Monthly limit check: userId=%d, month=%d-%d, used=%.1f, requested=%d, total=%.1f, limit=%d",
                       userId, year, month, usedDaysInMonth, requestedDays, totalAfterRequest, MONTHLY_LIMIT));

            if (totalAfterRequest > MONTHLY_LIMIT) {
                double remainingDays = MONTHLY_LIMIT - usedDaysInMonth;

                logger.warning(String.format("Monthly unpaid leave limit exceeded: userId=%d, month=%d-%d, used=%.1f, requested=%d, limit=%d, remaining=%.1f",
                              userId, year, month, usedDaysInMonth, requestedDays, MONTHLY_LIMIT, remainingDays));

                throw new IllegalArgumentException(
                    String.format("Monthly unpaid leave limit exceeded! " +
                                 "You have already used %.1f days of unpaid leave in %d-%02d. " +
                                 "Monthly limit: %d days. Remaining: %.1f days. " +
                                 "Requested: %d days. " +
                                 "Please reduce your request or wait for next month.",
                                 usedDaysInMonth, year, month, MONTHLY_LIMIT, remainingDays, requestedDays)
                );
            }

            logger.info(String.format("Monthly limit validation passed: userId=%d, month=%d-%d, used=%.1f, requested=%d, remaining=%.1f",
                       userId, year, month, usedDaysInMonth, requestedDays, MONTHLY_LIMIT - totalAfterRequest));

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation exceptions
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error validating monthly unpaid leave limit: userId=%d, month=%d-%d",
                      userId, startDate.getYear(), startDate.getMonthValue()), e);
            throw new RuntimeException("Error validating monthly unpaid leave limit", e);
        }
    }

    /**
     * Calculate unpaid leave days used in a specific month
     * Counts both APPROVED and PENDING requests to prevent monthly limit abuse
     *
     * @param userId User ID
     * @param year Year
     * @param month Month (1-12)
     * @return Number of unpaid leave days used in the month (supports half-day: 0.5)
     */
    private double calculateUnpaidLeaveDaysInMonth(Long userId, int year, int month) {
        logger.fine(String.format("Calculating unpaid leave days in month: userId=%d, year=%d, month=%d",
                   userId, year, month));

        try {
            // Get all requests for the user
            List<Request> allRequests = requestDao.findByUserId(userId);

            double totalDays = 0.0;

            for (Request request : allRequests) {
                // Count both APPROVED and PENDING unpaid leave requests
                // This prevents creating multiple requests that would exceed monthly limit
                if (!"APPROVED".equals(request.getStatus()) && !"PENDING".equals(request.getStatus())) {
                    continue;
                }

                LeaveRequestDetail detail = request.getLeaveDetail();
                if (detail == null) {
                    continue;
                }

                // Only count unpaid leave
                if (!"UNPAID".equals(detail.getLeaveTypeCode())) {
                    continue;
                }

                // Parse start date to check if it's in the target month
                String startDateStr = detail.getStartDate();
                if (startDateStr == null || startDateStr.length() < 10) {
                    continue;
                }

                try {
                    LocalDateTime requestStartDate = LocalDateTime.parse(startDateStr);

                    // Check if request is in the target month
                    if (requestStartDate.getYear() == year && requestStartDate.getMonthValue() == month) {
                        // Add days to total (supports half-day)
                        if (detail.getIsHalfDay() != null && detail.getIsHalfDay()) {
                            totalDays += 0.5;
                            logger.fine(String.format("Added half-day unpaid leave: requestId=%d, date=%s, period=%s",
                                       request.getId(), startDateStr, detail.getHalfDayPeriod()));
                        } else {
                            int dayCount = detail.getDayCount() != null ? detail.getDayCount() : 1;
                            totalDays += dayCount;
                            logger.fine(String.format("Added full-day unpaid leave: requestId=%d, days=%d",
                                       request.getId(), dayCount));
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, String.format("Error parsing date for request %d: %s",
                              request.getId(), startDateStr), e);
                    // Continue with other requests
                }
            }

            logger.info(String.format("Calculated unpaid leave days in month (APPROVED + PENDING): userId=%d, year=%d, month=%d, totalDays=%.1f",
                       userId, year, month, totalDays));

            return totalDays;

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error calculating unpaid leave days in month: userId=%d, year=%d, month=%d",
                      userId, year, month), e);
            return 0.0; // Return 0 on error to be safe
        }
    }
}
