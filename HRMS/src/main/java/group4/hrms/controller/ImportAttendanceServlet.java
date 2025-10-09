package group4.hrms.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.model.AttendanceLog;
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

    private final AttendanceLogDao attendanceLogDAO = new AttendanceLogDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part filePart = req.getPart("file"); 
        if (filePart == null || filePart.getSize() == 0) {
            resp.getWriter().write("File is empty");
            return;
        }

        String submittedName = filePart.getSubmittedFileName();
        boolean isCsv = submittedName != null && submittedName.toLowerCase().endsWith(".csv");

        try (InputStream inputStream = filePart.getInputStream()) {
            List<AttendanceLog> logs;
            if (isCsv) {
                logs = parseCsvFile(inputStream);
            } else {
                logs = parseExcelFile(inputStream);
            }

            // Lưu tất cả bản ghi vào DB
            saveAttendanceLogs(logs);

            resp.getWriter().write("Imported " + logs.size() + " attendance logs successfully.");
        } catch (Exception e) {
            // Trả về thông tin lỗi rõ ràng hơn cho debug
            resp.getWriter().write("Error importing file: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
    }

    // ----------- Hàm đọc file Excel và parse thành AttendanceLog -----------
    private List<AttendanceLog> parseExcelFile(InputStream inputStream) throws IOException {
        List<AttendanceLog> logs = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // chỉ đọc sheet đầu tiên

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // bỏ qua header
                }
                AttendanceLog log = parseRowToAttendanceLog(row);
                if (log != null) {
                    logs.add(log);
                }
            }
        }

        return logs;
    }

    // Parse CSV (simple, supports comma or tab separated values)
    private List<AttendanceLog> parseCsvFile(InputStream inputStream) throws IOException {
        List<AttendanceLog> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                if (line.trim().isEmpty()) continue;
                String[] parts;
                if (line.contains("\t")) parts = line.split("\t"); else parts = line.split(",");
                // Trim all
                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
                AttendanceLog log = parseValuesToAttendanceLog(parts);
                if (log != null) logs.add(log);
            }
        }
        return logs;
    }

    // ----------- Hàm parse một row Excel thành AttendanceLog -----------
    private AttendanceLog parseRowToAttendanceLog(Row row) {
        try {
            // Extract cell values as strings then delegate to value parser to handle types/formats
            String[] values = new String[6];
            for (int i = 0; i < 6; i++) {
                Cell c = row.getCell(i);
                if (c == null) { values[i] = null; continue; }
                switch (c.getCellType()) {
                    case STRING:
                        values[i] = c.getStringCellValue();
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(c)) {
                            // convert excel date to yyyy-MM-dd HH:mm:ss
                            LocalDateTime dt = c.getLocalDateTimeCellValue();
                            values[i] = dt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
                        } else {
                            // numeric -> avoid scientific notation
                            double d = c.getNumericCellValue();
                            if (d == Math.rint(d)) {
                                values[i] = String.valueOf((long) d);
                            } else {
                                values[i] = String.valueOf(d);
                            }
                        }
                        break;
                    case BOOLEAN:
                        values[i] = String.valueOf(c.getBooleanCellValue());
                        break;
                    case FORMULA:
                        try {
                            values[i] = c.getStringCellValue();
                        } catch (Exception ex) {
                            double dv = c.getNumericCellValue();
                            values[i] = String.valueOf(dv);
                        }
                        break;
                    default:
                        values[i] = null;
                }
            }
            return parseValuesToAttendanceLog(values);
        } catch (Exception e) {
            // Log or ignore malformed row; return null to skip
            return null;
        }
    }

    // Create AttendanceLog from parsed string values array
    private AttendanceLog parseValuesToAttendanceLog(String[] values) {
        try {
            // values expected: userId, checkType, checkedAt, source, note, periodId
            if (values == null || values.length < 3) return null;
            String userIdStr = values[0];
            String checkType = values[1];
            String checkedAtStr = values[2];

            if (userIdStr == null || userIdStr.isBlank() || checkType == null || checkType.isBlank() || checkedAtStr == null || checkedAtStr.isBlank()) {
                return null;
            }

            AttendanceLog log = new AttendanceLog();
            try {
                log.setUserId(Long.valueOf(userIdStr));
            } catch (NumberFormatException nfe) {
                // If user ID is non-numeric, skip this row
                return null;
            }

            log.setCheckType(checkType);

            // Try multiple date formats
            DateTimeFormatter[] formats = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
            };
            LocalDateTime parsed = null;
            for (DateTimeFormatter f : formats) {
                try {
                    if (f == formats[2]) {
                        // date only -> set time to start of day
                        parsed = LocalDateTime.parse(checkedAtStr + " 00:00:00", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
                    } else {
                        parsed = LocalDateTime.parse(checkedAtStr, f);
                    }
                    break;
                } catch (DateTimeParseException ignore) {}
            }
            if (parsed == null) {
                // as a last resort, try to parse epoch or skip
                return null;
            }
            log.setCheckedAt(parsed);

            if (values.length > 3) log.setSource(values[3]);
            if (values.length > 4) log.setNote(values[4]);
            if (values.length > 5 && values[5] != null && !values[5].isBlank()) {
                try { log.setPeriodId(Long.valueOf(values[5])); } catch (NumberFormatException ignore) {}
            }

            return log;
        } catch (Exception e) {
            return null;
        }
    }

    // ----------- Hàm lưu danh sách AttendanceLog vào DB -----------
    private void saveAttendanceLogs(List<AttendanceLog> logs) {
        for (AttendanceLog log : logs) {
            try {
                attendanceLogDAO.save(log);
            } catch (SQLException e) {
            }
        }
    }
}
