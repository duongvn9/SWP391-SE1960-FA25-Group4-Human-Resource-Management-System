package group4.hrms.controller;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dao.UserDao;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import group4.hrms.model.User;
import group4.hrms.service.AttendanceMapper;
import group4.hrms.service.AttendanceService;
import group4.hrms.service.PayslipDirtyFlagService;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@WebServlet("/attendance/import")
@MultipartConfig
public class ImportAttendanceServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ImportAttendanceServlet.class.getName());
    private final PayslipDirtyFlagService dirtyFlagService = new PayslipDirtyFlagService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute(SessionUtil.USER_ID_KEY);

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if ("Preview".equalsIgnoreCase(action)) {
            int page = 1;
            String pageParam = req.getParameter("page");
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            List<AttendanceLogDto> logsDto = (List<AttendanceLogDto>) req.getSession().getAttribute("previewLogsAll");
            if (logsDto != null) {
                int recordsPerPage = 10;
                int totalLogs = logsDto.size();
                int totalPages = (int) Math.ceil((double) totalLogs / recordsPerPage);
                int fromIndex = (page - 1) * recordsPerPage;
                int toIndex = Math.min(fromIndex + recordsPerPage, totalLogs);

                List<AttendanceLogDto> pageLogs = logsDto.subList(fromIndex, toIndex);

                req.setAttribute("previewLogs", pageLogs);
                req.setAttribute("currentPage", page);
                req.setAttribute("totalPages", totalPages);
            }
        }

        // --- Phân trang cho invalid logs ---
        List<AttendanceLogDto> invalidLogsDto = (List<AttendanceLogDto>) req.getSession().getAttribute("invalidLogsAll");
        System.out.println("---------------Invalid Log doGet-------------------------");
        System.out.println(invalidLogsDto);
        if (invalidLogsDto != null && !invalidLogsDto.isEmpty()) {
            int invalidPage = 1;
            String invalidPageParam = req.getParameter("invalidPage");
            if (invalidPageParam != null) {
                try {
                    invalidPage = Integer.parseInt(invalidPageParam);
                } catch (NumberFormatException e) {
                    invalidPage = 1;
                }
            }

            int recordsPerPage = 10;
            int totalInvalid = invalidLogsDto.size();
            int totalInvalidPages = (int) Math.ceil((double) totalInvalid / recordsPerPage);
            int fromIndex = (invalidPage - 1) * recordsPerPage;
            int toIndex = Math.min(fromIndex + recordsPerPage, totalInvalid);

            List<AttendanceLogDto> pageInvalidLogs = invalidLogsDto.subList(fromIndex, toIndex);

            req.setAttribute("invalidLogsExcel", pageInvalidLogs);
            req.setAttribute("invalidCurrentPage", invalidPage);
            req.setAttribute("invalidTotalPages", totalInvalidPages);
        }

        UserDao uDao = new UserDao();
        List<User> uList = uDao.findAll();
        req.setAttribute("uList", uList);
        String activeTab = req.getParameter("activeTab");
        if (activeTab == null) {
            activeTab = "upload";
        }
        req.setAttribute("activeTab", activeTab);
        req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        UserDao userDao = new UserDao();
        AttendanceLogDao dao = new AttendanceLogDao();
        try {
            if ("ManualImport".equalsIgnoreCase(action)) {
                String manualDataJson = req.getParameter("manualData");
                List<AttendanceLogDto> manualLogs = new ArrayList<>();

                if (manualDataJson != null && !manualDataJson.trim().isEmpty()) {
                    manualDataJson = manualDataJson.trim();
                    if (manualDataJson.startsWith("[")) {
                        manualDataJson = manualDataJson.substring(1);
                    }
                    if (manualDataJson.endsWith("]")) {
                        manualDataJson = manualDataJson.substring(0, manualDataJson.length() - 1);
                    }

                    // Tách các object bằng "},{"
                    String[] objects = manualDataJson.split("\\},\\{");

                    for (String obj : objects) {
                        if (!obj.startsWith("{")) {
                            obj = "{" + obj;
                        }
                        if (!obj.endsWith("}")) {
                            obj = obj + "}";
                        }

                        AttendanceLogDto dto = new AttendanceLogDto();
                        dto.setUserId(extractLong(obj, "userId"));
                        dto.setDate(extractDate(obj, "date"));
                        dto.setCheckIn(extractTime(obj, "checkIn"));
                        dto.setCheckOut(extractTime(obj, "checkOut"));
                        // Status sẽ được tính tự động sau khi lọc spam

                        manualLogs.add(dto);
                    }
                }

                // --- Bổ sung thông tin user, phòng ban, kỳ công ---
                for (AttendanceLogDto dto : manualLogs) {
                    dto.setSource("manual");
                }

                // Sử dụng hàm enrichment mới
                try {
                    manualLogs = AttendanceService.enrichAttendanceLogsFromDatabase(manualLogs);
                } catch (SQLException e) {
                    req.setAttribute("manualError", "Error enriching manual data: " + e.getMessage());
                    req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
                    return;
                }

                // --- ✅ Validate dữ liệu ---
                Map<String, List<AttendanceLogDto>> validatedMap = dao.validateManualLogs(manualLogs);
                System.out.println(validatedMap);
                List<AttendanceLogDto> validLogs = validatedMap.get("valid");
                List<AttendanceLogDto> invalidLogs = validatedMap.get("invalid");

                // --- ✅ Lưu các bản ghi hợp lệ ---
                if (!validLogs.isEmpty()) {
                    List<AttendanceLog> record = AttendanceMapper.convertDtoToEntity(validLogs);
                    dao.saveAttendanceLogs(record);

                    // --- ✅ Mark affected payslips as dirty ---
                    markPayslipsDirtyForAttendanceChanges(validLogs, "Manual attendance import");
                }

                // --- ✅ Đưa thông tin ra FE ---
                if (!invalidLogs.isEmpty()) {
                    req.setAttribute("invalidLogs", invalidLogs);
                    req.setAttribute("manualError", "Some records are duplicate or invalid.");
                } else {
                    req.setAttribute("manualSuccess", "Import successfully");
                }

                List<User> uList = userDao.findAll();
                req.setAttribute("uList", uList);
                String activeTab = req.getParameter("activeTab");
                if (activeTab == null || activeTab.isEmpty()) {
                    activeTab = "upload";
                }
                req.setAttribute("activeTab", activeTab);
                req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
                return;
            } else if ("Delete".equalsIgnoreCase(action)) {
                // Xóa session chứa danh sách invalid logs
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.removeAttribute("invalidLogsAll");
                }
                req.setAttribute("message", "Records have been cleared.");
                List<User> uList = userDao.findAll();
                req.setAttribute("uList", uList);
                String activeTab = req.getParameter("activeTab");
                if (activeTab == null || activeTab.isEmpty()) {
                    activeTab = "upload";
                }
                req.setAttribute("activeTab", activeTab);
                req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
                return;
            }
        } catch (ServletException | IOException | NumberFormatException ex) {
            req.setAttribute("manualError", "Server error: " + ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
            return;
        } catch (SQLException ex) {
            Logger.getLogger(ImportAttendanceServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Path tempFilePath = handleFileUpload(req);
            List<AttendanceLogDto> logsDto = null;

            if ("Preview".equalsIgnoreCase(action)) {
                logsDto = AttendanceService.readExcelForPreview(tempFilePath);
                req.setAttribute("previewLogsAll", logsDto);
                System.out.println(logsDto);

                // Xử lý phân trang cho Preview
                int page = 1;
                String pageParam = req.getParameter("page");
                if (pageParam != null) {
                    try {
                        page = Integer.parseInt(pageParam);
                        if (page < 1) {
                            page = 1;
                        }
                    } catch (NumberFormatException e) {
                        page = 1;
                    }
                }

                int recordsPerPage = 10;
                int totalLogs = logsDto.size();
                int totalPages = (int) Math.ceil((double) totalLogs / recordsPerPage);

                int fromIndex = (page - 1) * recordsPerPage;
                int toIndex = Math.min(fromIndex + recordsPerPage, totalLogs);

                List<AttendanceLogDto> pageLogs = new ArrayList<>();
                if (fromIndex < totalLogs) {
                    pageLogs = logsDto.subList(fromIndex, toIndex);
                }

                req.setAttribute("previewLogs", pageLogs);
                req.setAttribute("currentPage", page);
                req.setAttribute("totalPages", totalPages);

            } else if ("Import".equalsIgnoreCase(action)) {
                // Đọc tất cả bản ghi để tách biệt valid/invalid
                logsDto = AttendanceService.readExcelForPreview(tempFilePath);
                Map<String, List<AttendanceLogDto>> separatedLogs = AttendanceService.separateValidAndInvalidRecords(logsDto);

                List<AttendanceLogDto> validLogs = separatedLogs.get("valid");
                List<AttendanceLogDto> formatInvalidLogs = separatedLogs.get("invalid");

                // Process import với chỉ valid logs
                AttendanceService.processImport(validLogs, action, tempFilePath, req);

                // --- ✅ Mark affected payslips as dirty ---
                if (!validLogs.isEmpty()) {
                    markPayslipsDirtyForAttendanceChanges(validLogs, "Excel attendance import");
                }

                // Hiển thị format invalid logs nếu có
                if (!formatInvalidLogs.isEmpty()) {
                    req.getSession().setAttribute("formatInvalidLogsAll", formatInvalidLogs);

                    // Phân trang cho format invalid logs
                    int invalidPage = 1;
                    String invalidPageParam = req.getParameter("formatInvalidPage");
                    if (invalidPageParam != null) {
                        try {
                            invalidPage = Integer.parseInt(invalidPageParam);
                        } catch (NumberFormatException e) {
                            invalidPage = 1;
                        }
                    }

                    int recordsPerPage = 10;
                    int totalInvalid = formatInvalidLogs.size();
                    int totalInvalidPages = (int) Math.ceil((double) totalInvalid / recordsPerPage);
                    int fromIndex = (invalidPage - 1) * recordsPerPage;
                    int toIndex = Math.min(fromIndex + recordsPerPage, totalInvalid);

                    List<AttendanceLogDto> pageInvalidLogs = formatInvalidLogs.subList(fromIndex, toIndex);

                    req.setAttribute("formatInvalidLogs", pageInvalidLogs);
                    req.setAttribute("formatInvalidCurrentPage", invalidPage);
                    req.setAttribute("formatInvalidTotalPages", totalInvalidPages);
                    req.setAttribute("formatInvalidMessage", formatInvalidLogs.size() + " bản ghi có lỗi định dạng và không được import.");
                }
            }

            // Đảm bảo dữ liệu luôn có trong session để có thể truy cập từ doGet
            if (logsDto != null) {
                req.getSession().setAttribute("previewLogsAll", logsDto);
            }

            List<User> uList = userDao.findAll();
            req.setAttribute("uList", uList);
            req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
        } catch (ServletException | IOException | SQLException e) {
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
        }
    }

    private Long extractLong(String json, String key) {
        try {
            String pattern = "\"" + key + "\":(\\d+)";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (m.find()) {
                return Long.valueOf(m.group(1));
            }
        } catch (NumberFormatException e) {
        }
        return null;
    }

    private String extractString(String json, String key) {
        try {
            String pattern = "\"" + key + "\":\"(.*?)\"";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private LocalDate extractDate(String json, String key) {
        String val = extractString(json, key);
        if (val != null) {
            return LocalDate.parse(val);
        }
        return null;
    }

    private LocalTime extractTime(String json, String key) {
        String val = extractString(json, key);
        if (val != null) {
            return LocalTime.parse(val);
        }
        return null;
    }

    private Path handleFileUpload(HttpServletRequest req) throws IOException, ServletException {
        Part filePart = req.getPart("file");
        Path tempFilePath = null;

        if (filePart != null && filePart.getSize() > 0) {
            String fileName = filePart.getSubmittedFileName();
            if (fileName == null || !(fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls"))) {
                throw new ServletException("Please upload an Excel file (.xlsx or .xls).");
            }

            tempFilePath = Files.createTempFile("attendance_", "_" + fileName);
            try (InputStream inputStream = filePart.getInputStream()) {
                Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            req.getSession().setAttribute("uploadedFile", tempFilePath.toString());
        } else {
            String path = (String) req.getSession().getAttribute("uploadedFile");
            if (path != null) {
                tempFilePath = Paths.get(path);
            }
        }

        if (tempFilePath == null || !Files.exists(tempFilePath)) {
            throw new ServletException("Please select a file before processing.");
        }

        return tempFilePath;
    }

    /**
     * Mark payslips as dirty for attendance changes
     * This ensures payslips are recalculated when attendance data is updated
     */
    private void markPayslipsDirtyForAttendanceChanges(List<AttendanceLogDto> attendanceLogs, String source) {
        try {
            logger.info(String.format("Marking payslips dirty for %d attendance changes from %s",
                                    attendanceLogs.size(), source));

            int markedCount = 0;
            for (AttendanceLogDto log : attendanceLogs) {
                if (log.getUserId() != null && log.getDate() != null) {
                    try {
                        String reason = String.format("Attendance updated via %s", source);
                        dirtyFlagService.markDirtyForAttendanceChange(
                            log.getUserId(),
                            log.getDate(),
                            reason
                        );
                        markedCount++;
                    } catch (Exception e) {
                        logger.log(Level.WARNING,
                                 String.format("Failed to mark payslip dirty for user %d on %s: %s",
                                             log.getUserId(), log.getDate(), e.getMessage()), e);
                        // Continue with other records
                    }
                }
            }

            logger.info(String.format("Successfully marked %d payslips as dirty", markedCount));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error marking payslips dirty for attendance changes", e);
            // Don't throw exception to avoid breaking the import operation
        }
    }
}
