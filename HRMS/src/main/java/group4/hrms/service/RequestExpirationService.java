package group4.hrms.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.RequestDao;
import group4.hrms.dto.LeaveRequestDetail;
import group4.hrms.dto.OTRequestDetail;
import group4.hrms.model.Request;

/**
 * Service for handling request expiration logic.
 * Auto-rejects PENDING requests that have passed their effective date.
 *
 * Business Rules:
 * - OT requests: If otDate has passed, auto-reject
 * - Leave requests: If startDate has passed, auto-reject
 * - Appeal requests: If attendance date has passed, auto-reject
 * - Auto-rejection reason: "Request expired (effective date passed)"
 */
public class RequestExpirationService {

    private static final Logger logger = Logger.getLogger(RequestExpirationService.class.getName());
    private static final String AUTO_REJECT_REASON = "Request expired (effective date passed). Auto-rejected by system.";

    private final RequestDao requestDao;

    public RequestExpirationService(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    /**
     * Check and auto-reject a single request if expired.
     *
     * @param request Request to check
     * @return true if request was auto-rejected, false otherwise
     */
    public boolean checkAndAutoReject(Request request) {
        if (request == null || !"PENDING".equals(request.getStatus())) {
            return false; // Only auto-reject PENDING requests
        }

        LocalDate today = LocalDate.now();
        LocalDate effectiveDate = getEffectiveDate(request);

        if (effectiveDate != null && !effectiveDate.isAfter(today)) {
            // Effective date has passed, auto-reject
            logger.info(String.format("Auto-rejecting expired request: id=%d, type=%d, effectiveDate=%s",
                       request.getId(), request.getRequestTypeId(), effectiveDate));

            request.setStatus("REJECTED");
            request.setApproveReason(AUTO_REJECT_REASON);
            request.setUpdatedAt(LocalDateTime.now());
            request.setCurrentApproverAccountId(null); // System rejection, no approver

            try {
                requestDao.update(request);
                return true;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to auto-reject expired request: " + request.getId(), e);
                return false;
            }
        }

        return false; // Not expired
    }

    /**
     * Batch process: Check and auto-reject all expired PENDING requests.
     * This can be called:
     * - On system startup
     * - By a scheduled job (cron)
     * - Manually by admin
     *
     * @return Number of requests auto-rejected
     */
    public int processExpiredRequests() {
        logger.info("Starting batch processing of expired requests...");

        try {
            // Find all PENDING requests
            List<Request> pendingRequests = requestDao.findByStatus("PENDING");
            logger.info(String.format("Found %d PENDING requests to check", pendingRequests.size()));

            int rejectedCount = 0;
            for (Request request : pendingRequests) {
                if (checkAndAutoReject(request)) {
                    rejectedCount++;
                }
            }

            logger.info(String.format("Batch processing complete. Auto-rejected %d expired requests.", rejectedCount));
            return rejectedCount;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in batch processing of expired requests", e);
            return 0;
        }
    }

    /**
     * Get the effective date of a request based on its type.
     *
     * @param request Request to get effective date from
     * @return Effective date, or null if cannot determine
     */
    private LocalDate getEffectiveDate(Request request) {
        if (request == null || request.getRequestTypeId() == null) {
            return null;
        }

        try {
            Long typeId = request.getRequestTypeId();

            // OT Request (type_id=7)
            if (typeId == 7L) {
                OTRequestDetail otDetail = request.getOtDetail();
                if (otDetail != null && otDetail.getOtDate() != null) {
                    return LocalDate.parse(otDetail.getOtDate());
                }
            }

            // Leave Request (type_id=6)
            if (typeId == 6L) {
                LeaveRequestDetail leaveDetail = request.getLeaveDetail();
                if (leaveDetail != null && leaveDetail.getStartDate() != null) {
                    String startDateStr = leaveDetail.getStartDate();
                    // Handle both date and datetime formats
                    if (startDateStr.length() >= 10) {
                        return LocalDate.parse(startDateStr.substring(0, 10));
                    }
                }
            }

            // Appeal Request (type_id=8)
            if (typeId == 8L) {
                var appealDetail = request.getAppealDetail();
                if (appealDetail != null && appealDetail.getAttendanceDates() != null
                    && !appealDetail.getAttendanceDates().isEmpty()) {
                    return LocalDate.parse(appealDetail.getAttendanceDates().get(0));
                }
            }

            // Recruitment requests (type_id=9) don't have effective date - never expire
            // Other request types: no expiration logic

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing effective date for request " + request.getId(), e);
        }

        return null; // No effective date or error parsing
    }

    /**
     * Check if a request is expired (effective date has passed).
     * Does NOT auto-reject, just returns true/false.
     *
     * @param request Request to check
     * @return true if request is expired, false otherwise
     */
    public boolean isExpired(Request request) {
        if (request == null || !"PENDING".equals(request.getStatus())) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate effectiveDate = getEffectiveDate(request);

        return effectiveDate != null && !effectiveDate.isAfter(today);
    }

    /**
     * Get human-readable expiration status message for a request.
     *
     * @param request Request to check
     * @return Status message (e.g., "Expires in 2 days", "Expired", "No expiration")
     */
    public String getExpirationStatus(Request request) {
        if (request == null) {
            return "Unknown";
        }

        if (!"PENDING".equals(request.getStatus())) {
            return "Not applicable"; // Only pending requests can expire
        }

        LocalDate effectiveDate = getEffectiveDate(request);
        if (effectiveDate == null) {
            return "No expiration"; // No effective date
        }

        LocalDate today = LocalDate.now();
        long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, effectiveDate);

        if (daysUntil < 0) {
            return String.format("Expired (%d days ago)", Math.abs(daysUntil));
        } else if (daysUntil == 0) {
            return "Expires today";
        } else if (daysUntil == 1) {
            return "Expires tomorrow";
        } else {
            return String.format("Expires in %d days", daysUntil);
        }
    }
}

