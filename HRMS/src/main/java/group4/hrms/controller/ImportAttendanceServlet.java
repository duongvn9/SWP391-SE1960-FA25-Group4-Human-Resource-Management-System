package group4.hrms.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
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
import java.util.List;

@WebServlet("/attendance/import")
@MultipartConfig
public class ImportAttendanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            Path tempFilePath = handleFileUpload(req);
            List<AttendanceLogDto> logsDto = AttendanceService.readExcel(tempFilePath);
            AttendanceService.processImport(logsDto, action, tempFilePath, req);

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
