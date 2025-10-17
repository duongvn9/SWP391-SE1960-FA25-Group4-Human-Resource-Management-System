package group4.hrms.controller;

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
import group4.hrms.service.AttendanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/attendance/import")
@MultipartConfig
public class ImportAttendanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            // 1. Xử lý upload file (nếu có)
            Path tempFilePath = handleFileUpload(req);
            List<AttendanceLogDto> logsDto = AttendanceService.readExcel(tempFilePath);

            // 2. Lưu toàn bộ previewLogs vào session để phân trang
            req.getSession().setAttribute("previewLogsAll", logsDto);

            // 3. Xử lý Preview với phân trang
            if ("Preview".equalsIgnoreCase(action)) {
                int page = 1; // trang mặc định
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

                int recordsPerPage = 10; // số record / trang, có thể thay đổi
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

            // 4. Nếu action là Import, gọi processImport để xử lý import
            if ("Import".equalsIgnoreCase(action)) {
                AttendanceService.processImport(logsDto, action, tempFilePath, req);
            }

        } catch (ServletException | IOException e) {
            req.setAttribute("error", "Error: " + e.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(ImportAttendanceServlet.class.getName()).log(Level.SEVERE, null, ex);
            req.setAttribute("error", "Database error.");
        }

        req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
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
