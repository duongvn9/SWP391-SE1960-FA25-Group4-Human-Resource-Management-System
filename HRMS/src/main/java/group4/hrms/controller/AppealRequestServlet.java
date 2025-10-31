package group4.hrms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import group4.hrms.dao.AttachmentDao;
import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.Attachment;
import group4.hrms.model.Request;
import group4.hrms.model.TimesheetPeriod;
import group4.hrms.model.User;
import group4.hrms.service.AttachmentService;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/requests/appeal/create")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, // 5MB per file
        maxRequestSize = 25 * 1024 * 1024 // 25MB total request size
)
public class AppealRequestServlet extends HttpServlet {

    private final AttendanceLogDao dao = new AttendanceLogDao();
    private final TimesheetPeriodDao tDAO = new TimesheetPeriodDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long userId = (Long) req.getSession().getAttribute("userId");
            String action = req.getParameter("action");
            if ("submitSelectedRecords".equals(action)) {
                String jsonStr = req.getParameter("records"); // Lấy từ input hidden
                List<AttendanceLogDto> records = new ArrayList<>();

                if (jsonStr != null && !jsonStr.isEmpty()) {
                    // Loại bỏ dấu [ ] ở đầu cuối
                    String recsPart = jsonStr.trim();
                    if (recsPart.startsWith("[")) {
                        recsPart = recsPart.substring(1);
                    }
                    if (recsPart.endsWith("]")) {
                        recsPart = recsPart.substring(0, recsPart.length() - 1);
                    }

                    // Tách các record bằng "},{"
                    String[] recArray = recsPart.split("\\},\\{");

                    for (int i = 0; i < recArray.length; i++) {
                        String r = recArray[i];
                        if (!r.startsWith("{")) {
                            r = "{" + r;
                        }
                        if (!r.endsWith("}")) {
                            r = r + "}";
                        }

                        // Loại bỏ dấu { } và "
                        r = r.replace("{", "").replace("}", "").replace("\"", "");
                        String[] fields = r.split(",");

                        AttendanceLogDto dto = new AttendanceLogDto();
                        for (String f : fields) {
                            String[] kv = f.split(":", 2);
                            if (kv.length != 2) {
                                continue;
                            }
                            String key = kv[0].trim();
                            String value = kv[1].trim();

                            try {
                                switch (key) {
                                    case "date" ->
                                        dto.setDate(LocalDate.parse(value)); // yyyy-MM-dd
                                    case "checkIn" ->
                                        dto.setCheckIn(LocalTime.parse(value)); // HH:mm
                                    case "checkOut" ->
                                        dto.setCheckOut(LocalTime.parse(value));
                                    case "status" ->
                                        dto.setStatus(value);
                                    case "source" ->
                                        dto.setSource(value);
                                    case "period" ->
                                        dto.setPeriod(value);
                                    case "isLocked" ->
                                        dto.setIsLocked(Boolean.parseBoolean(value));
                                    case "employeeName" ->
                                        dto.setEmployeeName(value);
                                    case "department" ->
                                        dto.setDepartment(value);
                                    case "userId" ->
                                        dto.setUserId(Long.valueOf(value));
                                    default ->
                                        dto.setError("Unknown field: " + key);
                                }
                            } catch (NumberFormatException e) {
                                dto.setError("Failed to parse field " + key + ": " + e.getMessage());
                            }
                        }
                        records.add(dto);
                    }
                }
                // --- Lấy danh sách các kỳ công (periods) ---
                List<TimesheetPeriod> periodList = tDAO.findAll();
                req.setAttribute("periodList", periodList);

                // --- Xác định kỳ công hiện tại ---
                TimesheetPeriod currentPeriod = tDAO.findCurrentPeriod();
                Long currentPeriodId = currentPeriod != null ? currentPeriod.getId() : null;
                req.setAttribute("currentPeriod", currentPeriod);
                req.setAttribute("currentPeriodId", currentPeriodId);

                // --- Lấy toàn bộ attendance logs của user trong kỳ công hiện tại ---
                List<AttendanceLogDto> attendanceList = new ArrayList<>();
                if (currentPeriod != null) {
                    LocalDate startDate = currentPeriod.getStartDate();
                    LocalDate endDate = currentPeriod.getEndDate();

                    // Lấy tất cả bản ghi của user trong kỳ công hiện tại, không giới hạn số lượng
                    attendanceList = dao.findByFilter(
                            userId,
                            null, // departmentId
                            null, // employeeId (all)
                            startDate,
                            endDate,
                            null,
                            null,
                            currentPeriodId,
                            Integer.MAX_VALUE,
                            0,
                            true
                    );
                }

                req.setAttribute("attendanceList", attendanceList);

                req.setAttribute("records", records);
                req.getRequestDispatcher("/WEB-INF/views/requests/appeal-form.jsp").forward(req, resp);
                return;
            }

            RequestDao requestDao = new RequestDao();
            UserDao uDao = new UserDao();
            AttachmentDao attachmentDao = new AttachmentDao();

            Long accountId = (Long) req.getSession().getAttribute("accountId");
            String title = req.getParameter("title");
            String detailText = req.getParameter("detail");
            Long requestTypeId = 8L;

            // --- Lấy danh sách bản ghi cũ + mới từ JS ---
            String selectedLogsData = req.getParameter("selected_logs_data");

            System.out.println("----------------------------");
            System.out.println(selectedLogsData);
            System.out.println("--------------------------------");

            List<Map<String, Map<String, String>>> recordsList = new ArrayList<>();
            if (selectedLogsData != null && !selectedLogsData.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    recordsList = mapper.readValue(selectedLogsData,
                            new TypeReference<List<Map<String, Map<String, String>>>>() {
                    });
                } catch (JsonProcessingException e) {
                }
            }

            System.out.println("recordsList size: " + recordsList.size());
            System.out.println(recordsList);

            // === VALIDATION: Check edit fields completeness and conflicts ===
            List<AttendanceLogDto> editedLogs = new ArrayList<>();
            boolean hasValidationErrors = false;
            StringBuilder validationErrors = new StringBuilder();

            for (Map<String, Map<String, String>> record : recordsList) {
                Map<String, String> newRecord = record.get("newRecord");
                if (newRecord == null) {
                    continue;
                }

                // Validate required fields
                String date = newRecord.get("date");
                String checkIn = newRecord.get("checkIn");
                String checkOut = newRecord.get("checkOut");
                String status = newRecord.get("status");

                if (date == null || date.trim().isEmpty()) {
                    hasValidationErrors = true;
                    validationErrors.append("Date is required for all edited records. ");
                    continue;
                }

                if (checkIn == null || checkIn.trim().isEmpty()) {
                    hasValidationErrors = true;
                    validationErrors.append("Check-in time is required for all edited records. ");
                    continue;
                }

                if (checkOut == null || checkOut.trim().isEmpty()) {
                    hasValidationErrors = true;
                    validationErrors.append("Check-out time is required for all edited records. ");
                    continue;
                }

                if (status == null || status.trim().isEmpty()) {
                    hasValidationErrors = true;
                    validationErrors.append("Status is required for all edited records. ");
                    continue;
                }

                // Create DTO for conflict checking
                try {
                    AttendanceLogDto dto = new AttendanceLogDto();
                    dto.setUserId(userId);
                    dto.setDate(LocalDate.parse(date));
                    dto.setCheckIn(LocalTime.parse(checkIn));
                    dto.setCheckOut(LocalTime.parse(checkOut));
                    dto.setStatus(status);
                    dto.setSource("appeal");
                    editedLogs.add(dto);
                } catch (Exception e) {
                    hasValidationErrors = true;
                    validationErrors.append("Invalid date/time format in edited records. ");
                }
            }

            // Check for conflicts using appeal-specific validation method
            if (!hasValidationErrors && !editedLogs.isEmpty()) {
                // For appeal requests, we need to validate edited records against existing records
                // but exclude the original records being edited from conflict checking
                List<Map<String, Map<String, String>>> originalRecords = recordsList;

                System.out.println("=== APPEAL VALIDATION DEBUG ===");
                System.out.println("Edited logs count: " + editedLogs.size());
                System.out.println("Original records count: " + originalRecords.size());
                for (AttendanceLogDto editedLog : editedLogs) {
                    System.out.println("Edited log: " + editedLog.getDate() + " "
                            + editedLog.getCheckIn() + "-" + editedLog.getCheckOut());
                }
                for (Map<String, Map<String, String>> originalRecord : originalRecords) {
                    Map<String, String> oldRec = originalRecord.get("oldRecord");
                    if (oldRec != null) {
                        System.out.println("Original record: " + oldRec.get("date") + " "
                                + oldRec.get("checkIn") + "-" + oldRec.get("checkOut"));
                    }
                }

                Map<String, List<AttendanceLogDto>> validationResult = dao.validateAppealLogs(editedLogs, originalRecords);
                List<AttendanceLogDto> invalidLogs = validationResult.get("invalid");

                if (!invalidLogs.isEmpty()) {
                    hasValidationErrors = true;
                    for (AttendanceLogDto invalidLog : invalidLogs) {
                        if (invalidLog.getError() != null) {
                            System.out.println("Validation error: " + invalidLog.getError());
                            validationErrors.append(invalidLog.getError()).append(" ");
                        }
                    }
                } else {
                    System.out.println("All edited logs are valid!");
                }
                System.out.println("=== END APPEAL VALIDATION DEBUG ===");
            }

            // If validation fails, preserve form data and show error
            if (hasValidationErrors) {
                // Preserve form data
                req.setAttribute("title", title);
                req.setAttribute("detail", detailText);

                // Preserve attachment data
                String attachmentType = req.getParameter("attachmentType");
                String driveLink = req.getParameter("driveLink");
                req.setAttribute("attachmentType", attachmentType);
                req.setAttribute("driveLink", driveLink);

                // Convert recordsList back to records for display
                List<AttendanceLogDto> preservedRecords = new ArrayList<>();
                for (Map<String, Map<String, String>> record : recordsList) {
                    Map<String, String> oldRec = record.get("oldRecord");
                    Map<String, String> newRec = record.get("newRecord");

                    if (oldRec != null && newRec != null) {
                        AttendanceLogDto dto = new AttendanceLogDto();
                        try {
                            dto.setDate(LocalDate.parse(oldRec.get("date")));
                            dto.setCheckIn(oldRec.get("checkIn") != null ? LocalTime.parse(oldRec.get("checkIn")) : null);
                            dto.setCheckOut(oldRec.get("checkOut") != null ? LocalTime.parse(oldRec.get("checkOut")) : null);
                            dto.setStatus(oldRec.get("status"));
                            dto.setSource(oldRec.get("source"));
                            dto.setPeriod(oldRec.get("period"));
                            preservedRecords.add(dto);
                        } catch (Exception e) {
                            // Skip invalid records
                        }
                    }
                }

                req.setAttribute("records", preservedRecords);
                req.setAttribute("message", "Validation Error: " + validationErrors.toString().trim());

                // Get required data for form display
                List<TimesheetPeriod> periodList = tDAO.findAll();
                req.setAttribute("periodList", periodList);
                TimesheetPeriod currentPeriod = tDAO.findCurrentPeriod();
                Long currentPeriodId = currentPeriod != null ? currentPeriod.getId() : null;
                req.setAttribute("currentPeriod", currentPeriod);
                req.setAttribute("currentPeriodId", currentPeriodId);

                List<AttendanceLogDto> attendanceList = new ArrayList<>();
                if (currentPeriod != null) {
                    LocalDate startDate = currentPeriod.getStartDate();
                    LocalDate endDate = currentPeriod.getEndDate();
                    attendanceList = dao.findByFilter(userId, null, null, startDate, endDate, null, null, currentPeriodId, Integer.MAX_VALUE, 0, true);
                }
                req.setAttribute("attendanceList", attendanceList);

                req.getRequestDispatcher("/WEB-INF/views/requests/appeal-form.jsp").forward(req, resp);
                return;
            }

            // Save the request first to get the ID for attachments
            Request request = new Request();
            request.setRequestTypeId(requestTypeId);
            request.setTitle(title);
            request.setCreatedByAccountId(accountId);
            request.setCreatedByUserId(userId);

            User u = uDao.findById(userId).orElse(new User());
            request.setDepartmentId(u.getDepartmentId());
            request.setStatus("PENDING");
            request.setCreatedAt(LocalDateTime.now());

            // Build JSON first (without attachment info)
            StringBuilder detailJsonBuilder = new StringBuilder();
            detailJsonBuilder.append("{");

            // Extract attendance dates from records
            List<String> attendanceDates = new ArrayList<>();
            if (!recordsList.isEmpty()) {
                // Add records section for backward compatibility
                detailJsonBuilder.append("\"records\":[");
                for (int i = 0; i < recordsList.size(); i++) {
                    Map<String, Map<String, String>> pair = recordsList.get(i);
                    detailJsonBuilder.append("{");

                    // oldRecord
                    detailJsonBuilder.append("\"oldRecord\":{");
                    Map<String, String> oldRec = pair.get("oldRecord");
                    int j = 0;
                    for (Map.Entry<String, String> entry : oldRec.entrySet()) {
                        detailJsonBuilder.append("\"").append(entry.getKey()).append("\":\"")
                                .append(entry.getValue()).append("\"");
                        if (j++ < oldRec.size() - 1) {
                            detailJsonBuilder.append(",");
                        }
                    }
                    detailJsonBuilder.append("},");

                    // newRecord
                    detailJsonBuilder.append("\"newRecord\":{");
                    Map<String, String> newRec = pair.get("newRecord");
                    j = 0;
                    for (Map.Entry<String, String> entry : newRec.entrySet()) {
                        detailJsonBuilder.append("\"").append(entry.getKey()).append("\":\"")
                                .append(entry.getValue()).append("\"");
                        if (j++ < newRec.size() - 1) {
                            detailJsonBuilder.append(",");
                        }
                    }
                    detailJsonBuilder.append("}");

                    detailJsonBuilder.append("}");
                    if (i < recordsList.size() - 1) {
                        detailJsonBuilder.append(",");
                    }

                    // Extract date for attendanceDates array
                    if (oldRec.containsKey("date") && !attendanceDates.contains(oldRec.get("date"))) {
                        attendanceDates.add(oldRec.get("date"));
                    }
                }
                detailJsonBuilder.append("],");

                // Add attendanceDates array for AppealRequestDetail compatibility
                detailJsonBuilder.append("\"attendanceDates\":[");
                for (int i = 0; i < attendanceDates.size(); i++) {
                    detailJsonBuilder.append("\"").append(attendanceDates.get(i)).append("\"");
                    if (i < attendanceDates.size() - 1) {
                        detailJsonBuilder.append(",");
                    }
                }
                detailJsonBuilder.append("]");
            }

            // Add reason field (maps to detail_text)
            if (detailText != null && !detailText.isEmpty()) {
                if (!recordsList.isEmpty()) {
                    detailJsonBuilder.append(",");
                }
                detailJsonBuilder.append("\"reason\":\"").append(escapeJson(detailText)).append("\"");
                // Keep detail_text for backward compatibility
                detailJsonBuilder.append(",\"detail_text\":\"").append(escapeJson(detailText)).append("\"");
            }

            detailJsonBuilder.append("}");
            request.setDetailJson(detailJsonBuilder.toString());

            // Save request to get ID
            requestDao.save(request);
            Long requestId = request.getId();

            // Handle attachments - both file uploads and external links
            try {
                AttachmentService attachmentService = new AttachmentService();

                // Check attachment type: "file" or "link"
                String attachmentType = req.getParameter("attachmentType");

                if ("link".equals(attachmentType)) {
                    // Handle Google Drive link
                    String driveLink = req.getParameter("driveLink");

                    if (driveLink != null && !driveLink.trim().isEmpty()) {
                        Logger.getLogger(AppealRequestServlet.class.getName()).info(
                                String.format("Processing Google Drive link for appeal request ID: %d - URL: %s",
                                        requestId, driveLink));

                        // Save external link to database
                        Attachment linkAttachment = attachmentService.saveExternalLink(
                                driveLink.trim(),
                                requestId,
                                "REQUEST",
                                accountId,
                                "Google Drive Link");

                        Logger.getLogger(AppealRequestServlet.class.getName()).info(
                                String.format("Successfully saved external link attachment: id=%d",
                                        linkAttachment.getId()));
                    }

                } else {
                    // Handle file uploads (default)
                    Collection<Part> fileParts = req.getParts().stream()
                            .filter(part -> "attachments".equals(part.getName()) && part.getSize() > 0)
                            .collect(Collectors.toList());

                    if (!fileParts.isEmpty()) {
                        Logger.getLogger(AppealRequestServlet.class.getName()).info(
                                String.format("Processing %d file attachment(s) for appeal request ID: %d",
                                        fileParts.size(), requestId));

                        // Get upload base path - save to webapp/assets/img/Request/
                        String uploadBasePath = req.getServletContext().getRealPath("/assets/img/Request");
                        if (uploadBasePath == null) {
                            // Fallback to system temp directory if realPath is not available
                            uploadBasePath = System.getProperty("java.io.tmpdir");
                            Logger.getLogger(AppealRequestServlet.class.getName()).log(Level.WARNING, "Using temp directory for uploads: {0}", uploadBasePath);
                        } else {
                            // Create directory if it doesn't exist
                            java.io.File uploadDir = new java.io.File(uploadBasePath);
                            if (!uploadDir.exists()) {
                                boolean created = uploadDir.mkdirs();
                                if (created) {
                                    Logger.getLogger(AppealRequestServlet.class.getName()).log(Level.INFO, "Created upload directory: {0}", uploadBasePath);
                                } else {
                                    Logger.getLogger(AppealRequestServlet.class.getName()).log(Level.WARNING, "Failed to create upload directory: {0}", uploadBasePath);
                                }
                            }
                        }

                        // Save files to filesystem and database
                        List<Attachment> attachments = attachmentService.saveFiles(
                                fileParts,
                                requestId,
                                "REQUEST",
                                accountId,
                                uploadBasePath);

                        Logger.getLogger(AppealRequestServlet.class.getName()).info(
                                String.format("Successfully saved %d file attachment(s) for appeal request ID: %d",
                                        attachments.size(), requestId));
                    }
                }

            } catch (ServletException | IOException | SQLException fileError) {
                // Attachment handling failed - log error and rollback the request creation
                Logger.getLogger(AppealRequestServlet.class.getName()).log(Level.SEVERE,
                        String.format("Attachment handling failed for appeal request ID: %d, error: %s",
                                requestId, fileError.getMessage()), fileError);

                // TODO: Implement transaction rollback - delete the created request
                // For now, we'll throw an exception to inform the user
                throw new Exception("Appeal request was created but attachment handling failed. "
                        + "Please contact IT support with request ID: " + requestId, fileError);
            }

            // --- Lấy danh sách các kỳ công (periods) ---
            List<TimesheetPeriod> periodList = tDAO.findAll();
            req.setAttribute("periodList", periodList);

            // --- Xác định kỳ công hiện tại ---
            TimesheetPeriod currentPeriod = tDAO.findCurrentPeriod();
            Long currentPeriodId = currentPeriod != null ? currentPeriod.getId() : null;
            req.setAttribute("currentPeriod", currentPeriod);
            req.setAttribute("currentPeriodId", currentPeriodId);

            // --- Lấy toàn bộ attendance logs của user trong kỳ công hiện tại ---
            List<AttendanceLogDto> attendanceList = new ArrayList<>();
            if (currentPeriod != null) {
                LocalDate startDate = currentPeriod.getStartDate();
                LocalDate endDate = currentPeriod.getEndDate();

                // Lấy tất cả bản ghi của user trong kỳ công hiện tại, không giới hạn số lượng
                attendanceList = dao.findByFilter(
                        userId,
                        null, // departmentId
                        null, // employeeId (all)
                        startDate,
                        endDate,
                        null,
                        null,
                        currentPeriodId,
                        Integer.MAX_VALUE,
                        0,
                        true
                );
            }

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("success", "Create appeal attendance request successfully!");
            req.getRequestDispatcher("/WEB-INF/views/requests/appeal-form.jsp").forward(req, resp);

        } catch (Exception ex) {
            Logger.getLogger(AppealRequestServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\"", "\\\"");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long userId = (Long) req.getSession().getAttribute(SessionUtil.USER_ID_KEY);

            if (userId == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            // Request type cho form kháng nghị
            Long requestTypeId = 8L;
            req.setAttribute("requestTypeId", requestTypeId);

            // --- Lấy danh sách các kỳ công (periods) ---
            List<TimesheetPeriod> periodList = tDAO.findAll();
            req.setAttribute("periodList", periodList);

            // --- Xác định kỳ công hiện tại ---
            TimesheetPeriod currentPeriod = tDAO.findCurrentPeriod();
            Long currentPeriodId = currentPeriod != null ? currentPeriod.getId() : null;
            req.setAttribute("currentPeriod", currentPeriod);
            req.setAttribute("currentPeriodId", currentPeriodId);

            // --- Lấy toàn bộ attendance logs của user trong kỳ công hiện tại ---
            List<AttendanceLogDto> attendanceList = new ArrayList<>();
            if (currentPeriod != null) {
                LocalDate startDate = currentPeriod.getStartDate();
                LocalDate endDate = currentPeriod.getEndDate();

                // Lấy tất cả bản ghi của user trong kỳ công hiện tại, không giới hạn số lượng
                attendanceList = dao.findByFilter(
                        userId,
                        null, // departmentId
                        null, // employeeId (all)
                        startDate,
                        endDate,
                        null, // status
                        null, // source
                        currentPeriodId,
                        Integer.MAX_VALUE, // số lượng bản ghi cực lớn
                        0,
                        true
                );
            }

            req.setAttribute("attendanceList", attendanceList);

            // --- Forward tới form ---
            req.getRequestDispatcher("/WEB-INF/views/requests/appeal-form.jsp").forward(req, resp);

        } catch (SQLException ex) {
            Logger.getLogger(AppealRequestServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException(ex);
        }
    }
}
