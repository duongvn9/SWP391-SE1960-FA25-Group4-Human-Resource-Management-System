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
import group4.hrms.model.Department;
import group4.hrms.model.TimesheetPeriod;
import group4.hrms.model.User;
import group4.hrms.service.AttendanceMapper;
import group4.hrms.service.AttendanceService;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@WebServlet("/attendance/import")
@MultipartConfig
public class ImportAttendanceServlet extends HttpServlet {

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

        req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if ("ManualImport".equalsIgnoreCase(action)) {
                String manualDataJson = req.getParameter("manualData");
                List<AttendanceLogDto> manualLogs = new ArrayList<>();

                if (manualDataJson != null && !manualDataJson.trim().isEmpty()) {
                    manualDataJson = manualDataJson.trim();
                    if (manualDataJson.startsWith("[") && manualDataJson.endsWith("]")) {
                        manualDataJson = manualDataJson.substring(1, manualDataJson.length() - 1); 
                        String[] records = manualDataJson.split("\\},\\{");
                        for (String r : records) {
                            r = r.replace("{", "").replace("}", "");
                            String[] fields = r.split(",");
                            AttendanceLogDto dto = new AttendanceLogDto();

                            for (String f : fields) {
                                String[] kv = f.split(":", 2);
                                if (kv.length == 2) {
                                    String key = kv[0].trim().replaceAll("\"", "");
                                    String value = kv[1].trim().replaceAll("\"", "");

                                    switch (key) {
                                        case "employeeId" -> {
                                            dto.setUserId(value.isEmpty() ? null : Long.valueOf(value));
                                        }
                                        case "date" -> {
                                            if (!value.isEmpty()) {
                                                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                                dto.setDate(LocalDate.parse(value, dateFormatter));
                                            }
                                        }
                                        case "checkIn" -> {
                                            if (!value.isEmpty()) {
                                                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
                                                dto.setCheckIn(LocalTime.parse(value, timeFormatter));
                                            }
                                        }
                                        case "checkOut" -> {
                                            if (!value.isEmpty()) {
                                                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
                                                dto.setCheckOut(LocalTime.parse(value, timeFormatter));
                                            }
                                        }
                                        case "status" -> {
                                            dto.setStatus(value);
                                        }
                                    }
                                }
                            }

                            manualLogs.add(dto);
                        }
                    }
                }

                UserDao userDao = new UserDao();
                DepartmentDao dDao = new DepartmentDao();
                TimesheetPeriodDao tDao = new TimesheetPeriodDao();
                AttendanceLogDao dao = new AttendanceLogDao();
                for (AttendanceLogDto dto : manualLogs) {
                    try {
                        if (dto.getUserId() != null) {
                            Optional<User> userOpt = userDao.findById(dto.getUserId());
                            userOpt.ifPresent(user -> {
                                dto.setEmployeeName(user.getFullName());
                                Optional<Department> depOpt = dDao.findById(user.getDepartmentId());
                                depOpt.ifPresent(dep -> dto.setDepartment(dep.getName()));
                            });
                        }

                        dto.setSource("manual");

                        if (dto.getDate() != null) {
                            TimesheetPeriod period = tDao.findPeriodByDate(dto.getDate());
                            dto.setPeriod(period != null ? period.getName() : "N/A");
                        } else {
                            dto.setPeriod("N/A");
                        }
                    } catch (SQLException e) {
                    }
                }

                List<AttendanceLog> record = AttendanceMapper.convertDtoToEntity(manualLogs);
                dao.saveAttendanceLogs(record);
                req.setAttribute("manualSuccess", "Import successfully");

                req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
                return;
            }
        } catch (ServletException | IOException | NumberFormatException ex) {
            req.setAttribute("manualError", "Lỗi server: " + ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
            return;
        } catch (SQLException ex) {
            Logger.getLogger(ImportAttendanceServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Path tempFilePath = handleFileUpload(req);
            List<AttendanceLogDto> logsDto = AttendanceService.readExcel(tempFilePath);
            System.out.println(logsDto);
            req.getSession().setAttribute("previewLogsAll", logsDto);

            if ("Preview".equalsIgnoreCase(action)) {
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
            }

            if ("Import".equalsIgnoreCase(action)) {
                AttendanceService.processImport(logsDto, action, tempFilePath, req);
            }

            req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            req.setAttribute("error", "Lỗi: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(ImportAttendanceServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
}
