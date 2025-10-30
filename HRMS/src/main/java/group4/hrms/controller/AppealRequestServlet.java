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
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/requests/appeal/create")
@MultipartConfig
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
            String userAttachmentLink = req.getParameter("attachmentLink");

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

            Request request = new Request();
            request.setRequestTypeId(requestTypeId);
            request.setTitle(title);
            request.setCreatedByAccountId(accountId);
            request.setCreatedByUserId(userId);

            User u = uDao.findById(userId).orElse(new User());
            request.setDepartmentId(u.getDepartmentId());
            request.setStatus("PENDING");
            request.setCreatedAt(LocalDateTime.now());

            String uploadedFileLink = null;
            Part filePart = req.getPart("attachment");
            if (filePart != null && filePart.getSize() > 0) {
                String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uuid = java.util.UUID.randomUUID().toString();
                String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
                String serverFileName = uuid + ext;

                Path uploadDir = Paths.get("C:/HRMS_uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path filePath = uploadDir.resolve(serverFileName);
                try (InputStream in = filePart.getInputStream()) {
                    Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                uploadedFileLink = "/downloads/" + serverFileName;
            }

            // Build JSON in AppealRequestDetail format
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

            // Add attachmentPath field (maps to attachment_link)
            if (uploadedFileLink != null) {
                detailJsonBuilder.append(",\"attachmentPath\":\"").append(escapeJson(uploadedFileLink)).append("\"");
                // Keep attachment_link for backward compatibility
                detailJsonBuilder.append(",\"attachment_link\":\"").append(escapeJson(uploadedFileLink)).append("\"");
            }
            if (userAttachmentLink != null && !userAttachmentLink.isEmpty()) {
                if (uploadedFileLink == null) {
                    detailJsonBuilder.append(",\"attachmentPath\":\"").append(escapeJson(userAttachmentLink)).append("\"");
                }
                detailJsonBuilder.append(",\"user_attachment_link\":\"").append(escapeJson(userAttachmentLink)).append("\"");
            }

            detailJsonBuilder.append("}");
            request.setDetailJson(detailJsonBuilder.toString());

            System.out.println("--------------------------------------");
            System.out.println(request.getDetailJson());
            requestDao.save(request);

            if (userAttachmentLink != null && !userAttachmentLink.isEmpty()) {
                Attachment attachment = new Attachment();
                attachment.setOwnerType("REQUEST");
                attachment.setOwnerId(request.getId());
                attachment.setOriginalName("Google drive link");
                attachment.setAttachmentType("LINK");
                attachment.setExternalUrl(userAttachmentLink);
                attachment.setPath("");
                attachment.setSizeBytes(0L);
                attachment.setUploadedByAccountId(accountId);
                attachment.setCreatedAt(LocalDateTime.now());
                attachment.setContentType("External/link");
                attachment.setChecksumSha256(null);

                attachmentDao.save(attachment);
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

        } catch (ServletException | IOException | NumberFormatException ex) {
            throw new ServletException(ex);
        } catch (SQLException ex) {
            Logger.getLogger(AppealRequestServlet.class.getName()).log(Level.SEVERE, null, ex);
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
