package group4.hrms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dao.RequestDao;
import group4.hrms.model.Account;
import group4.hrms.model.Request;
import group4.hrms.model.User;
import group4.hrms.util.RequestListPermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for approving/rejecting requests
 * Can be used from request list page or request detail page
 */
@WebServlet("/requests/approve")
public class ApproveRequestController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ApproveRequestController.class.getName());
    private RequestDao requestDao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.requestDao = new RequestDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Get session and user
            HttpSession session = request.getSession(false);
            if (session == null) {
                out.print("{\"success\": false, \"message\": \"Session expired\"}");
                return;
            }

            User currentUser = (User) session.getAttribute("user");
            Account currentAccount = (Account) session.getAttribute("account");
            if (currentUser == null || currentAccount == null) {
                out.print("{\"success\": false, \"message\": \"User not logged in\"}");
                return;
            }

            // Get parameters
            String action = request.getParameter("action");
            String requestIdStr = request.getParameter("requestId");

            if (action == null || requestIdStr == null) {
                out.print("{\"success\": false, \"message\": \"Missing parameters\"}");
                return;
            }

            Long requestId;
            try {
                requestId = Long.parseLong(requestIdStr);
            } catch (NumberFormatException e) {
                out.print("{\"success\": false, \"message\": \"Invalid request ID\"}");
                return;
            }

            // Fetch the request
            Optional<Request> requestOpt = requestDao.findById(requestId);
            if (!requestOpt.isPresent()) {
                out.print("{\"success\": false, \"message\": \"Request not found\"}");
                return;
            }

            Request req = requestOpt.get();

            // Parse request detail based on request_type_id (needed for permission check)
            if (req.getDetailJson() != null && !req.getDetailJson().trim().isEmpty()) {
                try {
                    Long requestTypeId = req.getRequestTypeId();
                    if (requestTypeId != null) {
                        if (requestTypeId == 7L) {
                            req.getOtDetail(); // Parse OT detail for OVERTIME_REQUEST
                        } else if (requestTypeId == 6L) {
                            req.getLeaveDetail(); // Parse Leave detail for LEAVE_REQUEST
                        } else if (requestTypeId == 8L) {
                            req.getAppealDetail(); // Parse Appeal detail for ADJUSTMENT_REQUEST
                        }
                    }
                } catch (Exception e) {
                    logger.warning("Error parsing request detail for request " + requestId + ": " + e.getMessage());
                }
            }

            // Get user's position for permission check
            group4.hrms.dao.PositionDao positionDao = new group4.hrms.dao.PositionDao();
            group4.hrms.model.Position position = null;
            if (currentUser.getPositionId() != null) {
                java.util.Optional<group4.hrms.model.Position> positionOpt = positionDao.findById(currentUser.getPositionId());
                if (positionOpt.isPresent()) {
                    position = positionOpt.get();
                }
            }

            // Check permission
            if (!RequestListPermissionHelper.canApproveRequest(currentUser, req, position, currentAccount.getId())) {
                // Get employee name for better error message
                String employeeName = "this employee";
                try {
                    group4.hrms.dao.UserDao userDao = new group4.hrms.dao.UserDao();
                    java.util.Optional<group4.hrms.model.User> employeeOpt = userDao.findById(req.getCreatedByUserId());
                    if (employeeOpt.isPresent()) {
                        employeeName = employeeOpt.get().getFullName();
                    }
                } catch (Exception e) {
                    logger.warning("Error getting employee name for error message: " + e.getMessage());
                }

                out.print("{\"success\": false, \"message\": \"You do not have permission to approve/reject " + employeeName + "'s request\"}");
                return;
            }

            // Perform action
            boolean success;
            String message;
            String reason = request.getParameter("reason");

            if ("approve".equals(action)) {
                // Approval reason is required for all approvals
                if (reason == null || reason.trim().isEmpty()) {
                    out.print("{\"success\": false, \"message\": \"Approval reason is required\"}");
                    return;
                }

                // Allow approving PENDING and REJECTED requests (HR override)
                // Permission helper already checks if user can override REJECTED
                if (!"PENDING".equals(req.getStatus()) && !"REJECTED".equals(req.getStatus())) {
                    out.print("{\"success\": false, \"message\": \"Can only approve PENDING or REJECTED requests\"}");
                    return;
                }

                // Check if this is an override of REJECTED request
                boolean isOverride = "REJECTED".equals(req.getStatus());
                if (isOverride) {
                    logger.info(String.format("HR override detected: User %d overriding REJECTED request %d",
                               currentUser.getId(), requestId));

                    // IMPORTANT: When overriding REJECTED, need to re-validate for conflicts
                    // because the situation may have changed since rejection
                    // This validation will happen in the type-specific validation below
                }

                // Validate OT balance before approving OT requests (includes conflict checks)
                if (req.getRequestTypeId() != null && req.getRequestTypeId() == 7L) {
                    try {
                        // Initialize OTRequestService with required DAOs
                        group4.hrms.dao.RequestTypeDao requestTypeDao = new group4.hrms.dao.RequestTypeDao();
                        group4.hrms.dao.HolidayDao holidayDao = new group4.hrms.dao.HolidayDao();
                        group4.hrms.dao.HolidayCalendarDao holidayCalendarDao = new group4.hrms.dao.HolidayCalendarDao();
                        group4.hrms.dao.UserDao userDao = new group4.hrms.dao.UserDao();

                        group4.hrms.service.OTRequestService otService = new group4.hrms.service.OTRequestService(
                            requestDao, requestTypeDao, holidayDao, holidayCalendarDao, userDao
                        );

                        group4.hrms.dto.OTRequestDetail otDetail = req.getOtDetail();
                        if (otDetail != null) {
                            // Validate weekly, monthly, and annual limits
                            otService.validateOTBalance(
                                req.getUserId(),
                                otDetail.getOtDate(),
                                otDetail.getOtHours()
                            );

                            // Check conflict with existing leave requests
                            // This is critical when approving, especially for override cases
                            otService.checkConflictWithLeave(
                                req.getUserId(),
                                otDetail.getOtDate(),
                                otDetail.getStartTime(),
                                otDetail.getEndTime()
                            );
                        }
                    } catch (IllegalArgumentException e) {
                        out.print("{\"success\": false, \"message\": \"Cannot approve: " + e.getMessage() + "\"}");
                        return;
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error validating OT request during approval", e);
                        out.print("{\"success\": false, \"message\": \"Cannot approve: Unable to validate OT request\"}");
                        return;
                    }
                }

                // Validate leave balance for LEAVE requests (request_type_id = 6)
                if (req.getRequestTypeId() != null && req.getRequestTypeId() == 6L) {
                    try {
                        group4.hrms.dao.LeaveTypeDao leaveTypeDao = new group4.hrms.dao.LeaveTypeDao();
                        group4.hrms.dao.RequestTypeDao requestTypeDao = new group4.hrms.dao.RequestTypeDao();

                        group4.hrms.service.LeaveRequestService leaveService = new group4.hrms.service.LeaveRequestService(
                            requestDao, requestTypeDao, leaveTypeDao
                        );

                        group4.hrms.dto.LeaveRequestDetail leaveDetail = req.getLeaveDetail();
                        if (leaveDetail != null && leaveDetail.getLeaveTypeCode() != null) {
                            // Parse dates to calculate requested days and year
                            try {
                                // Parse as LocalDateTime first since the format includes time component
                                java.time.LocalDateTime startDateTime = java.time.LocalDateTime.parse(leaveDetail.getStartDate());
                                java.time.LocalDateTime endDateTime = java.time.LocalDateTime.parse(leaveDetail.getEndDate());
                                java.time.LocalDate startDate = startDateTime.toLocalDate();
                                java.time.LocalDate endDate = endDateTime.toLocalDate();

                                // Calculate requested days (inclusive)
                                int requestedDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
                                int year = startDate.getYear();

                                // Validate leave balance before approval
                                leaveService.validateLeaveBalance(
                                    req.getUserId(),
                                    leaveDetail.getLeaveTypeCode(),
                                    requestedDays,
                                    year
                                );

                                // Check conflicts with OT and other leave requests
                                // This is critical when approving, especially for half-day leaves
                                Boolean isHalfDay = leaveDetail.getIsHalfDay();
                                String halfDayPeriod = leaveDetail.getHalfDayPeriod();

                                // Validate all conflicts (OT + overlapping leaves)
                                leaveService.validateLeaveConflictsForApproval(
                                    req.getUserId(),
                                    startDateTime,
                                    endDateTime,
                                    req.getId(), // exclude this request from overlap check
                                    isHalfDay,
                                    halfDayPeriod
                                );
                            } catch (java.time.format.DateTimeParseException e) {
                                logger.log(Level.WARNING, "Invalid date format in leave detail", e);
                                out.print("{\"success\": false, \"message\": \"Cannot approve: Invalid date format\"}");
                                return;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        // This catches both IllegalArgumentException and LeaveValidationException
                        // (LeaveValidationException extends IllegalArgumentException)
                        out.print("{\"success\": false, \"message\": \"Cannot approve: " + e.getMessage() + "\"}");
                        return;
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error validating leave request during approval", e);
                        out.print("{\"success\": false, \"message\": \"Cannot approve: Unable to validate leave request\"}");
                        return;
                    }
                }

                // Handle APPEAL/ADJUSTMENT requests (request_type_id = 8)
                if (req.getRequestTypeId() != null && req.getRequestTypeId() == 8L) {
                    try {
                        // Validate appeal request for time conflicts before approval
                        String validationError = validateAppealTimeConflicts(req);
                        if (validationError != null) {
                            out.print("{\"success\": false, \"message\": \"Cannot approve: " + validationError + "\"}");
                            return;
                        }

                        logger.info("Starting appeal approval process for request: " + req.getId());
                        boolean appealProcessed = processAppealApproval(req, currentAccount.getId(), reason);
                        logger.info("Appeal approval process result: " + appealProcessed + " for request: " + req.getId());
                        if (!appealProcessed) {
                            out.print("{\"success\": false, \"message\": \"Failed to process appeal request\"}");
                            return;
                        }

                        // Update appealStatus in JSON detail
                        updateAppealStatusInJson(req, "APPROVED");
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error processing appeal request during approval", e);
                        out.print("{\"success\": false, \"message\": \"Cannot approve: " + e.getMessage() + "\"}");
                        return;
                    }
                }

                req.setStatus("APPROVED");
                req.setCurrentApproverAccountId(currentAccount.getId());
                req.setUpdatedAt(LocalDateTime.now());
                // Store approval reason (already validated as required above)
                req.setApproveReason(reason);
                Request updated = requestDao.update(req);
                success = (updated != null);

                // Dynamic message based on request type
                String requestTypeName = getRequestTypeName(req.getRequestTypeId());
                if (req.getRequestTypeId() != null && req.getRequestTypeId() == 8L) {
                    // Appeal request specific message
                    message = success ? requestTypeName + " approved successfully! Attendance records have been updated." : "Failed to approve " + requestTypeName.toLowerCase();
                } else {
                    // General message for other request types
                    message = success ? requestTypeName + " approved successfully!" : "Failed to approve " + requestTypeName.toLowerCase();
                }

            } else if ("reject".equals(action)) {
                // Rejection reason is always required
                if (reason == null || reason.trim().isEmpty()) {
                    out.print("{\"success\": false, \"message\": \"Rejection reason is required\"}");
                    return;
                }

                // Can reject PENDING or APPROVED requests (for manager override)
                if (!"PENDING".equals(req.getStatus()) && !"APPROVED".equals(req.getStatus())) {
                    out.print("{\"success\": false, \"message\": \"Can only reject PENDING or APPROVED requests\"}");
                    return;
                }

                // Handle APPEAL/ADJUSTMENT requests rejection (request_type_id = 8)
                if (req.getRequestTypeId() != null && req.getRequestTypeId() == 8L) {
                    try {
                        // If rejecting an APPROVED request, validate conflicts for restoring old times
                        if ("APPROVED".equals(req.getStatus())) {
                            String validationError = validateAppealRejectConflicts(req);
                            if (validationError != null) {
                                out.print("{\"success\": false, \"message\": \"Cannot reject: " + validationError + "\"}");
                                return;
                            }
                        }

                        boolean appealProcessed = processAppealRejection(req, currentAccount.getId(), reason);
                        if (!appealProcessed) {
                            out.print("{\"success\": false, \"message\": \"Failed to process appeal rejection\"}");
                            return;
                        }

                        // Update appealStatus in JSON detail
                        updateAppealStatusInJson(req, "REJECTED");
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error processing appeal request during rejection", e);
                        out.print("{\"success\": false, \"message\": \"Cannot reject: " + e.getMessage() + "\"}");
                        return;
                    }
                }

                req.setStatus("REJECTED");
                req.setCurrentApproverAccountId(currentAccount.getId());
                req.setUpdatedAt(LocalDateTime.now());
                req.setApproveReason(reason);
                Request updated = requestDao.update(req);
                success = (updated != null);

                // Dynamic message based on request type
                String requestTypeName = getRequestTypeName(req.getRequestTypeId());
                if (req.getRequestTypeId() != null && req.getRequestTypeId() == 8L) {
                    // Appeal request specific message
                    message = success ? requestTypeName + " rejected successfully! Original attendance records remain unchanged." : "Failed to reject " + requestTypeName.toLowerCase();
                } else {
                    // General message for other request types
                    message = success ? requestTypeName + " rejected successfully!" : "Failed to reject " + requestTypeName.toLowerCase();
                }

            } else {
                out.print("{\"success\": false, \"message\": \"Invalid action\"}");
                return;
            }

            // Return response
            if (success) {
                logger.info(String.format("User %d %s request %d", currentUser.getId(), action, requestId));
                out.print("{\"success\": true, \"message\": \"" + message + "\"}");
            } else {
                logger.warning(String.format("Failed to %s request %d by user %d", action, requestId, currentUser.getId()));
                out.print("{\"success\": false, \"message\": \"" + message + "\"}");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing approval", e);
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Xử lý duyệt đơn appeal
     * Luồng: PENDING -> APPROVED hoặc REJECTED -> APPROVED (ghi đè)
     */
    private boolean processAppealApproval(Request request, Long approverAccountId, String reason) {
        logger.info("Processing appeal approval for request: " + request.getId());

        String oldStatus = request.getStatus();

        try {
            if ("PENDING".equals(oldStatus)) {
                // Đơn chưa được duyệt -> Approve: Áp dụng giờ mới
                return applyNewAttendanceRecords(request);
            } else if ("REJECTED".equals(oldStatus)) {
                // Ghi đè từ REJECTED sang APPROVED: Áp dụng lại giờ mới
                return applyNewAttendanceRecords(request);
            }

            return true; // Các trường hợp khác không cần xử lý attendance

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing appeal approval", e);
            return false;
        }
    }

    /**
     * Xử lý từ chối đơn appeal
     * Luồng: PENDING -> REJECTED hoặc APPROVED -> REJECTED (ghi đè)
     */
    private boolean processAppealRejection(Request request, Long approverAccountId, String reason) {
        logger.info("Processing appeal rejection for request: " + request.getId());

        String oldStatus = request.getStatus();

        try {
            if ("PENDING".equals(oldStatus)) {
                // Đơn chưa được duyệt -> Reject: Không cần cập nhật attendance
                return true;
            } else if ("APPROVED".equals(oldStatus)) {
                // Ghi đè từ APPROVED sang REJECTED: Khôi phục giờ cũ
                return restoreOldAttendanceRecords(request);
            }

            return true; // Các trường hợp khác không cần xử lý attendance

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing appeal rejection", e);
            return false;
        }
    }

    /**
     * Áp dụng giờ mới từ records trong JSON detail
     */
    private boolean applyNewAttendanceRecords(Request request) {
        logger.info("Applying new attendance records for request: " + request.getId());

        try {
            java.util.List<AttendanceRecord> records = parseRecordsFromDetail(request.getDetailJson());
            logger.info("Parsed " + records.size() + " records from detail JSON for request: " + request.getId());

            if (records.isEmpty()) {
                logger.warning("No records found in detail JSON for request: " + request.getId());
                logger.warning("Detail JSON content: " + request.getDetailJson());
                return true; // Không có records để cập nhật
            }

            group4.hrms.dao.AttendanceLogDao attendanceLogDao = new group4.hrms.dao.AttendanceLogDao();

            for (AttendanceRecord record : records) {
                // Set userId từ request
                record.userId = request.getCreatedByUserId();
                logger.info("Processing record for userId: " + record.userId + ", date: " + record.date);

                // Cập nhật attendance log với giờ mới
                boolean updated = updateAttendanceLog(attendanceLogDao, record, true);
                logger.info("Update result for record " + record.date + ": " + updated);

                if (!updated) {
                    logger.severe("Failed to update attendance log for record: " + record);
                    return false;
                }
            }

            logger.info("Successfully applied new attendance records for request: " + request.getId());
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error applying new attendance records for request: " + request.getId(), e);
            return false;
        }
    }

    /**
     * Khôi phục giờ cũ từ records trong JSON detail
     */
    private boolean restoreOldAttendanceRecords(Request request) {
        logger.info("Restoring old attendance records for request: " + request.getId());

        try {
            java.util.List<AttendanceRecord> records = parseRecordsFromDetail(request.getDetailJson());
            if (records.isEmpty()) {
                logger.info("No records found in detail JSON for request: " + request.getId());
                return true; // Không có records để khôi phục
            }

            group4.hrms.dao.AttendanceLogDao attendanceLogDao = new group4.hrms.dao.AttendanceLogDao();

            for (AttendanceRecord record : records) {
                // Set userId từ request
                record.userId = request.getCreatedByUserId();

                // Cập nhật attendance log với giờ cũ
                boolean updated = updateAttendanceLog(attendanceLogDao, record, false);
                if (!updated) {
                    logger.warning("Failed to restore attendance log for record: " + record);
                    return false;
                }
            }

            logger.info("Successfully restored old attendance records for request: " + request.getId());
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error restoring old attendance records", e);
            return false;
        }
    }

    /**
     * Parse records từ JSON detail
     * Xử lý cấu trúc JSON thực tế: records[].newRecord và records[].oldRecord
     */
    private java.util.List<AttendanceRecord> parseRecordsFromDetail(String detailJson) {
        java.util.List<AttendanceRecord> records = new java.util.ArrayList<>();

        try {
            logger.info("Parsing detail JSON: " + detailJson);
            com.google.gson.JsonObject jsonObject = com.google.gson.JsonParser.parseString(detailJson).getAsJsonObject();

            if (jsonObject.has("records") && jsonObject.get("records").isJsonArray()) {
                com.google.gson.JsonArray recordsArray = jsonObject.getAsJsonArray("records");
                logger.info("Found " + recordsArray.size() + " records in JSON array");

                for (com.google.gson.JsonElement element : recordsArray) {
                    com.google.gson.JsonObject recordObj = element.getAsJsonObject();

                    // Parse newRecord và oldRecord
                    if (recordObj.has("newRecord") && recordObj.has("oldRecord")) {
                        com.google.gson.JsonObject newRecord = recordObj.getAsJsonObject("newRecord");
                        com.google.gson.JsonObject oldRecord = recordObj.getAsJsonObject("oldRecord");

                        AttendanceRecord record = new AttendanceRecord();

                        // Lấy thông tin từ newRecord
                        if (newRecord.has("date")) {
                            record.date = newRecord.get("date").getAsString();
                        }
                        if (newRecord.has("checkIn")) {
                            record.checkIn = newRecord.get("checkIn").getAsString();
                        }
                        if (newRecord.has("checkOut")) {
                            record.checkOut = newRecord.get("checkOut").getAsString();
                        }
                        if (newRecord.has("status")) {
                            record.status = newRecord.get("status").getAsString();
                        }

                        // Lấy thông tin từ oldRecord
                        if (oldRecord.has("checkIn")) {
                            record.oldCheckIn = oldRecord.get("checkIn").getAsString();
                        }
                        if (oldRecord.has("checkOut")) {
                            record.oldCheckOut = oldRecord.get("checkOut").getAsString();
                        }
                        if (oldRecord.has("source")) {
                            record.source = oldRecord.get("source").getAsString();
                        }
                        if (oldRecord.has("period")) {
                            record.period = oldRecord.get("period").getAsString();
                        }

                        records.add(record);

                        logger.info("Parsed attendance record: date=" + record.date +
                                   ", newCheckIn=" + record.checkIn +
                                   ", newCheckOut=" + record.checkOut +
                                   ", oldCheckIn=" + record.oldCheckIn +
                                   ", oldCheckOut=" + record.oldCheckOut +
                                   ", source=" + record.source);
                    } else {
                        logger.warning("Record missing newRecord or oldRecord: " + recordObj.toString());
                    }
                }
            } else {
                logger.warning("No 'records' array found in JSON or it's not an array");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error parsing records from detail JSON: " + detailJson, e);
        }

        logger.info("Total parsed records: " + records.size());
        return records;
    }

    /**
     * Cập nhật attendance log
     * @param attendanceLogDao DAO để cập nhật
     * @param record Record chứa thông tin
     * @param useNewTime true = dùng giờ mới, false = dùng giờ cũ
     */
    private boolean updateAttendanceLog(group4.hrms.dao.AttendanceLogDao attendanceLogDao,
                                       AttendanceRecord record, boolean useNewTime) {
        try {
            // Tìm attendance log hiện tại theo userId và date
            java.time.LocalDate date = java.time.LocalDate.parse(record.date);
            java.util.List<group4.hrms.model.AttendanceLog> existingLogs =
                attendanceLogDao.findByUserIdAndDate(record.userId, date);

            logger.info("Found " + existingLogs.size() + " existing logs for userId=" + record.userId + ", date=" + date);

            // Cập nhật cả check-in và check-out vì appeal thường sửa cả hai
            // Kiểm tra có dữ liệu check-in không
            if (record.checkIn != null && !record.checkIn.trim().isEmpty() &&
                record.oldCheckIn != null && !record.oldCheckIn.trim().isEmpty()) {
                updateCheckInLog(attendanceLogDao, existingLogs, record, useNewTime);
            }

            // Kiểm tra có dữ liệu check-out không
            if (record.checkOut != null && !record.checkOut.trim().isEmpty() &&
                record.oldCheckOut != null && !record.oldCheckOut.trim().isEmpty()) {
                updateCheckOutLog(attendanceLogDao, existingLogs, record, useNewTime);
            }

            return true;

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating attendance log", e);
            return false;
        }
    }

    /**
     * Cập nhật check-in log
     */
    private void updateCheckInLog(group4.hrms.dao.AttendanceLogDao attendanceLogDao,
                                 java.util.List<group4.hrms.model.AttendanceLog> existingLogs,
                                 AttendanceRecord record, boolean useNewTime) {
        try {
            // Tìm log check-in hiện tại
            group4.hrms.model.AttendanceLog checkInLog = existingLogs.stream()
                .filter(log -> "IN".equals(log.getCheckType()))
                .findFirst()
                .orElse(null);

            String timeToUse = useNewTime ? record.checkIn : record.oldCheckIn;
            if (timeToUse == null || timeToUse.trim().isEmpty()) {
                return; // Không có thời gian để cập nhật
            }

            // Parse time format "09:30" và combine với date
            java.time.LocalDateTime newCheckTime = parseTimeWithDate(record.date, timeToUse);
            if (newCheckTime == null) {
                logger.warning("Failed to parse check-in time: " + timeToUse + " for date: " + record.date);
                return;
            }

            if (checkInLog != null) {
                // Cập nhật log hiện tại - CHỈ thay đổi thời gian, giữ nguyên source và note
                java.time.LocalDateTime oldTime = checkInLog.getCheckedAt(); // Lưu thời gian cũ
                checkInLog.setCheckedAtNew(newCheckTime); // Set thời gian mới
                // KHÔNG thay đổi source và note để giữ nguyên status như "on time", "late"...
                attendanceLogDao.update(checkInLog);
                logger.info("Updated check-in log: userId=" + record.userId + ", oldTime=" + oldTime + ", newTime=" + newCheckTime + ", kept original source and note");
            } else {
                // Tạo log mới - sử dụng source và note từ record cũ nếu có
                group4.hrms.model.AttendanceLog newLog = new group4.hrms.model.AttendanceLog();
                newLog.setUserId(record.userId);
                newLog.setCheckType("IN");
                newLog.setCheckedAt(newCheckTime);
                // Sử dụng source từ record nếu có, nếu không thì để mặc định
                newLog.setSource(record.source != null ? record.source : "Manual");
                // Note sẽ được tính toán lại dựa trên thời gian mới (on time, late, etc.)
                newLog.setNote(""); // Để trống, sẽ được tính toán lại bởi hệ thống
                attendanceLogDao.save(newLog);
                logger.info("Created new check-in log: userId=" + record.userId + ", time=" + newCheckTime + ", source=" + newLog.getSource());
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating check-in log", e);
        }
    }

    /**
     * Cập nhật check-out log
     */
    private void updateCheckOutLog(group4.hrms.dao.AttendanceLogDao attendanceLogDao,
                                  java.util.List<group4.hrms.model.AttendanceLog> existingLogs,
                                  AttendanceRecord record, boolean useNewTime) {
        try {
            // Tìm log check-out hiện tại
            group4.hrms.model.AttendanceLog checkOutLog = existingLogs.stream()
                .filter(log -> "OUT".equals(log.getCheckType()))
                .findFirst()
                .orElse(null);

            String timeToUse = useNewTime ? record.checkOut : record.oldCheckOut;
            if (timeToUse == null || timeToUse.trim().isEmpty()) {
                return; // Không có thời gian để cập nhật
            }

            // Parse time format "10:58" và combine với date
            java.time.LocalDateTime newCheckTime = parseTimeWithDate(record.date, timeToUse);
            if (newCheckTime == null) {
                logger.warning("Failed to parse check-out time: " + timeToUse + " for date: " + record.date);
                return;
            }

            if (checkOutLog != null) {
                // Cập nhật log hiện tại - CHỈ thay đổi thời gian, giữ nguyên source và note
                java.time.LocalDateTime oldTime = checkOutLog.getCheckedAt(); // Lưu thời gian cũ
                checkOutLog.setCheckedAtNew(newCheckTime); // Set thời gian mới
                // KHÔNG thay đổi source và note để giữ nguyên status như "on time", "late"...
                attendanceLogDao.update(checkOutLog);
                logger.info("Updated check-out log: userId=" + record.userId + ", oldTime=" + oldTime + ", newTime=" + newCheckTime + ", kept original source and note");
            } else {
                // Tạo log mới - sử dụng source và note từ record cũ nếu có
                group4.hrms.model.AttendanceLog newLog = new group4.hrms.model.AttendanceLog();
                newLog.setUserId(record.userId);
                newLog.setCheckType("OUT");
                newLog.setCheckedAt(newCheckTime);
                // Sử dụng source từ record nếu có, nếu không thì để mặc định
                newLog.setSource(record.source != null ? record.source : "Manual");
                // Note sẽ được tính toán lại dựa trên thời gian mới (on time, late, etc.)
                newLog.setNote(""); // Để trống, sẽ được tính toán lại bởi hệ thống
                attendanceLogDao.save(newLog);
                logger.info("Created new check-out log: userId=" + record.userId + ", time=" + newCheckTime + ", source=" + newLog.getSource());
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating check-out log", e);
        }
    }

    /**
     * Parse time format "09:30" với date "2025-10-28" thành LocalDateTime
     */
    private java.time.LocalDateTime parseTimeWithDate(String dateStr, String timeStr) {
        try {
            // Parse date
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);

            // Parse time - xử lý format "09:30" hoặc "00:58" (có thể là lỗi format)
            java.time.LocalTime time;
            if (timeStr.contains(":")) {
                String[] timeParts = timeStr.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                // Xử lý trường hợp "00:58" có thể là "17:58" (check-out time)
                if (hour == 0 && minute > 0) {
                    // Giả định đây là check-out time vào buổi chiều
                    hour = 17; // Default to 5 PM
                    logger.info("Adjusted time from " + timeStr + " to " + hour + ":" + minute + " (assumed PM)");
                }

                time = java.time.LocalTime.of(hour, minute);
            } else {
                // Fallback parsing
                time = java.time.LocalTime.parse(timeStr);
            }

            return java.time.LocalDateTime.of(date, time);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing time: " + timeStr + " with date: " + dateStr, e);
            return null;
        }
    }

    /**
     * Inner class để represent attendance record từ JSON
     */
    private static class AttendanceRecord {
        public Long userId;
        public String date;
        public String status; // IN/OUT/BOTH
        public String checkIn;
        public String checkOut;
        public String source;
        public String oldCheckIn;
        public String oldCheckOut;
        public String period;

        @Override
        public String toString() {
            return "AttendanceRecord{" +
                    "userId=" + userId +
                    ", date='" + date + '\'' +
                    ", status='" + status + '\'' +
                    ", checkIn='" + checkIn + '\'' +
                    ", checkOut='" + checkOut + '\'' +
                    ", oldCheckIn='" + oldCheckIn + '\'' +
                    ", oldCheckOut='" + oldCheckOut + '\'' +
                    '}';
        }
    }

    /**
     * Validate appeal request for time conflicts before approval
     * Kiểm tra xung đột thời gian giữa giờ mới trong appeal với attendance logs hiện tại
     */
    private String validateAppealTimeConflicts(Request request) {
        logger.info("Validating appeal time conflicts for request: " + request.getId());

        try {
            java.util.List<AttendanceRecord> records = parseRecordsFromDetail(request.getDetailJson());
            if (records.isEmpty()) {
                return null; // Không có records để validate
            }

            group4.hrms.dao.AttendanceLogDao attendanceLogDao = new group4.hrms.dao.AttendanceLogDao();

            for (AttendanceRecord record : records) {
                record.userId = request.getCreatedByUserId();

                // Validate từng record
                String error = validateSingleRecordTimeConflict(attendanceLogDao, record);
                if (error != null) {
                    return error;
                }
            }

            return null; // Không có conflict

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error validating appeal time conflicts", e);
            return "Unable to validate time conflicts: " + e.getMessage();
        }
    }

    /**
     * Validate một record cho time conflicts
     */
    private String validateSingleRecordTimeConflict(group4.hrms.dao.AttendanceLogDao attendanceLogDao,
                                                   AttendanceRecord record) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(record.date);

            // Lấy tất cả attendance logs trong ngày
            java.util.List<group4.hrms.model.AttendanceLog> existingLogs =
                attendanceLogDao.findByUserIdAndDate(record.userId, date);

            // Parse thời gian mới từ appeal
            java.time.LocalDateTime newCheckIn = null;
            java.time.LocalDateTime newCheckOut = null;

            if (record.checkIn != null && !record.checkIn.trim().isEmpty()) {
                newCheckIn = parseTimeWithDate(record.date, record.checkIn);
            }

            if (record.checkOut != null && !record.checkOut.trim().isEmpty()) {
                newCheckOut = parseTimeWithDate(record.date, record.checkOut);
            }

            // Kiểm tra xung đột với từng log hiện tại
            for (group4.hrms.model.AttendanceLog existingLog : existingLogs) {
                java.time.LocalDateTime existingTime = existingLog.getCheckedAt();
                String existingType = existingLog.getCheckType();

                // Skip nếu đây là log sẽ được update (cùng type)
                if (("IN".equals(existingType) && newCheckIn != null) ||
                    ("OUT".equals(existingType) && newCheckOut != null)) {
                    continue; // Sẽ được replace, không cần check conflict
                }

                // Kiểm tra xung đột thời gian
                if (newCheckIn != null && isTimeConflict(newCheckIn, existingTime)) {
                    return "New check-in time " + newCheckIn.toLocalTime() +
                           " conflicts with existing " + existingType.toLowerCase() +
                           " at " + existingTime.toLocalTime() + " on " + record.date;
                }

                if (newCheckOut != null && isTimeConflict(newCheckOut, existingTime)) {
                    return "New check-out time " + newCheckOut.toLocalTime() +
                           " conflicts with existing " + existingType.toLowerCase() +
                           " at " + existingTime.toLocalTime() + " on " + record.date;
                }
            }

            // Kiểm tra logic thời gian: check-in phải trước check-out
            if (newCheckIn != null && newCheckOut != null) {
                if (!newCheckIn.isBefore(newCheckOut)) {
                    return "Check-in time (" + newCheckIn.toLocalTime() +
                           ") must be before check-out time (" + newCheckOut.toLocalTime() +
                           ") on " + record.date;
                }
            }

            return null; // Không có conflict

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error validating single record time conflict", e);
            return "Invalid time format in appeal record for date " + record.date;
        }
    }

    /**
     * Validate appeal reject conflicts - kiểm tra xung đột khi khôi phục giờ cũ
     * Dùng khi reject đơn đã APPROVED để khôi phục về thời gian cũ
     */
    private String validateAppealRejectConflicts(Request request) {
        logger.info("Validating appeal reject conflicts for request: " + request.getId());

        try {
            java.util.List<AttendanceRecord> records = parseRecordsFromDetail(request.getDetailJson());
            if (records.isEmpty()) {
                return null; // Không có records để validate
            }

            group4.hrms.dao.AttendanceLogDao attendanceLogDao = new group4.hrms.dao.AttendanceLogDao();

            for (AttendanceRecord record : records) {
                record.userId = request.getCreatedByUserId();

                // Validate việc khôi phục về thời gian cũ
                String error = validateSingleRecordRejectConflict(attendanceLogDao, record);
                if (error != null) {
                    return error;
                }
            }

            return null; // Không có conflict

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error validating appeal reject conflicts", e);
            return "Unable to validate restore conflicts: " + e.getMessage();
        }
    }

    /**
     * Validate một record cho reject conflicts (khôi phục thời gian cũ)
     */
    private String validateSingleRecordRejectConflict(group4.hrms.dao.AttendanceLogDao attendanceLogDao,
                                                     AttendanceRecord record) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(record.date);

            // Lấy tất cả attendance logs trong ngày
            java.util.List<group4.hrms.model.AttendanceLog> existingLogs =
                attendanceLogDao.findByUserIdAndDate(record.userId, date);

            // Parse thời gian CŨ từ appeal (sẽ được khôi phục)
            java.time.LocalDateTime oldCheckIn = null;
            java.time.LocalDateTime oldCheckOut = null;

            if (record.oldCheckIn != null && !record.oldCheckIn.trim().isEmpty()) {
                oldCheckIn = parseTimeWithDate(record.date, record.oldCheckIn);
            }

            if (record.oldCheckOut != null && !record.oldCheckOut.trim().isEmpty()) {
                oldCheckOut = parseTimeWithDate(record.date, record.oldCheckOut);
            }

            // Kiểm tra xung đột với từng log hiện tại
            for (group4.hrms.model.AttendanceLog existingLog : existingLogs) {
                java.time.LocalDateTime existingTime = existingLog.getCheckedAt();
                String existingType = existingLog.getCheckType();

                // Skip nếu đ là log sẽ được restore (cùng type)
                if (("IN".equals(existingType) && oldCheckIn != null) ||
                    ("OUT".equals(existingType) && oldCheckOut != null)) {
                    continue; // Sẽ được restore, không cần check conflict
                }

                // Kiểm tra xung đột thời gian khi khôi phục
                if (oldCheckIn != null && isTimeConflict(oldCheckIn, existingTime)) {
                    return "Cannot restore old check-in time " + oldCheckIn.toLocalTime() +
                           " - conflicts with existing " + existingType.toLowerCase() +
                           " at " + existingTime.toLocalTime() + " on " + record.date;
                }

                if (oldCheckOut != null && isTimeConflict(oldCheckOut, existingTime)) {
                    return "Cannot restore old check-out time " + oldCheckOut.toLocalTime() +
                           " - conflicts with existing " + existingType.toLowerCase() +
                           " at " + existingTime.toLocalTime() + " on " + record.date;
                }
            }

            // Kiểm tra logic thời gian cũ: check-in phải trước check-out
            if (oldCheckIn != null && oldCheckOut != null) {
                if (!oldCheckIn.isBefore(oldCheckOut)) {
                    return "Cannot restore - old check-in time (" + oldCheckIn.toLocalTime() +
                           ") must be before old check-out time (" + oldCheckOut.toLocalTime() +
                           ") on " + record.date;
                }
            }

            return null; // Không có conflict

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error validating single record reject conflict", e);
            return "Invalid old time format in appeal record for date " + record.date;
        }
    }

    /**
     * Lấy tên loại đơn theo request type ID
     */
    private String getRequestTypeName(Long requestTypeId) {
        if (requestTypeId == null) {
            return "Request";
        }

        switch (requestTypeId.intValue()) {
            case 6:
                return "Leave request";
            case 7:
                return "Overtime request";
            case 8:
                return "Appeal request";
            case 9:
                return "Recruitment request";
            default:
                return "Request";
        }
    }

    /**
     * Kiểm tra xung đột thời gian (trong vòng 15 phút)
     */
    private boolean isTimeConflict(java.time.LocalDateTime time1, java.time.LocalDateTime time2) {
        if (time1 == null || time2 == null) {
            return false;
        }

        // Xung đột nếu 2 thời gian cách nhau ít hơn 15 phút
        long minutesDiff = Math.abs(java.time.Duration.between(time1, time2).toMinutes());
        return minutesDiff < 15;
    }

    /**
     * Update appealStatus trong JSON detail của request
     * @param request Request cần update
     * @param newStatus Status mới (APPROVED, REJECTED, PENDING)
     */
    private void updateAppealStatusInJson(Request request, String newStatus) {
        try {
            String detailJson = request.getDetailJson();
            if (detailJson == null || detailJson.trim().isEmpty()) {
                logger.warning("No detail JSON found for appeal request: " + request.getId());
                return;
            }

            // Parse JSON và update appealStatus
            com.google.gson.JsonObject jsonObject = com.google.gson.JsonParser.parseString(detailJson).getAsJsonObject();
            jsonObject.addProperty("appealStatus", newStatus);

            // Update lại detail JSON trong request
            String updatedJson = jsonObject.toString();
            request.setDetailJson(updatedJson);

            logger.info("Updated appealStatus to " + newStatus + " for request: " + request.getId());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating appealStatus in JSON for request: " + request.getId(), e);
        }
    }
}
