package group4.hrms.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@WebServlet("/attendance/import")
@MultipartConfig
public class ImportAttendanceServlet extends HttpServlet {

    private final AttendanceLogDao attendanceLogDAO = new AttendanceLogDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        Part filePart = req.getPart("file");

        try {
            Path tempFilePath = null;

            // Nếu user upload file mới, lưu tạm vào server
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = filePart.getSubmittedFileName();
                if (fileName == null || !(fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls"))) {
                    req.setAttribute("error", "Please upload an Excel file (.xlsx or .xls).");
                    req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
                    return;
                }

                tempFilePath = Files.createTempFile("attendance_", "_" + fileName);
                try (InputStream inputStream = filePart.getInputStream()) {
                    Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // Lưu đường dẫn tạm vào session
                req.getSession().setAttribute("uploadedFile", tempFilePath.toString());
            } else {
                // Nếu không upload file mới, lấy file tạm trong session
                String path = (String) req.getSession().getAttribute("uploadedFile");
                if (path != null) {
                    tempFilePath = Paths.get(path);
                }
            }

            // Nếu không có file nào (cả mới lẫn tạm), báo lỗi
            if (tempFilePath == null || !Files.exists(tempFilePath)) {
                req.setAttribute("error", "Please select a file before " + action.toLowerCase() + ".");
                req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
                return;
            }

            // Đọc file từ đường dẫn tạm
            List<AttendanceLogDto> logsDto;
            try (InputStream inputStream = Files.newInputStream(tempFilePath)) {
                logsDto = readExcelFile(inputStream);
            }

            if ("Preview".equalsIgnoreCase(action)) {
                req.setAttribute("previewLogs", logsDto);

            } else if ("Import".equalsIgnoreCase(action)) {
                List<AttendanceLog> logs = convertDtoToEntity(logsDto);
                saveAttendanceLogs(logs);
                req.setAttribute("success", "Imported " + logs.size() + " attendance logs successfully.");

                // Xóa file tạm sau khi import xong
                Files.deleteIfExists(tempFilePath);
                req.getSession().removeAttribute("uploadedFile");

            } else {
                req.setAttribute("error", "Invalid action.");
            }

            req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);

        } catch (ServletException | IOException e) {
            req.setAttribute("error", "Error processing file: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(ImportAttendanceServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/attendance/import-attendance.jsp").forward(req, resp);
    }

    // ----------- Hàm đọc file Excel và trả về List<AttendanceLogDto> -----------
    public List<AttendanceLogDto> readExcelFile(InputStream inputStream) throws IOException {
        List<AttendanceLogDto> list = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            int firstRow = sheet.getFirstRowNum() + 1;
            int lastRow = sheet.getLastRowNum();

            for (int i = firstRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // Kiểm tra nếu toàn bộ row trống, bỏ qua
                boolean isEmpty = true;
                for (int c = 0; c <= 8; c++) { // 9 cột từ 0 đến 8
                    Cell cell = row.getCell(c);
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        String val = cell.toString().trim();
                        if (!val.isEmpty()) {
                            isEmpty = false;
                            break;
                        }
                    }
                }
                if (isEmpty) {
                    continue; // bỏ qua row trống
                }
                AttendanceLogDto dto = new AttendanceLogDto();

                dto.setEmployeeId(parseLong(row.getCell(0)));
                dto.setEmployeeName(parseString(row.getCell(1)));
                dto.setDepartment(parseString(row.getCell(2)));
                LocalDate date = parseDate(row.getCell(3));
                dto.setDate(date);

                LocalTime checkIn = parseTime(row.getCell(4));
                LocalTime checkOut = parseTime(row.getCell(5));
                if (date != null) {
                    if (checkIn != null) {
                        dto.setCheckIn(parseTime(row.getCell(4)));
                    }
                    if (checkOut != null) {
                        dto.setCheckOut(parseTime(row.getCell(5)));
                    }
                }

                dto.setStatus(parseString(row.getCell(6)));
                dto.setSource(parseString(row.getCell(7)));
                dto.setPeriod(parseString(row.getCell(8)));

                list.add(dto);
            }
        }
        return list;
    }

    // ----------- Các hàm xử lý linh hoạt kiểu dữ liệu -----------
    private String parseString(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            return switch (cell.getCellType()) {
                case STRING ->
                    cell.getStringCellValue().trim();
                case NUMERIC -> {
                    double val = cell.getNumericCellValue();
                    // Nếu là số nguyên (vd: 8.0 → "8"), cắt .0 đi
                    if (val == Math.floor(val)) {
                        yield String.valueOf((long) val);
                    } else {
                        yield String.valueOf(val);
                    }
                }
                case BOOLEAN ->
                    String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> {
                    // Nếu công thức trả về kiểu chuỗi
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    CellValue evaluatedValue = evaluator.evaluate(cell);
                    if (evaluatedValue == null) {
                        yield null;
                    }
                    yield switch (evaluatedValue.getCellType()) {
                        case STRING ->
                            evaluatedValue.getStringValue().trim();
                        case NUMERIC -> {
                            double val = evaluatedValue.getNumberValue();
                            if (val == Math.floor(val)) {
                                yield String.valueOf((long) val);
                            } else {
                                yield String.valueOf(val);
                            }
                        }
                        case BOOLEAN ->
                            String.valueOf(evaluatedValue.getBooleanValue());
                        default ->
                            null;
                    };
                }
                default ->
                    null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseLong(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (long) cell.getNumericCellValue();
            }
            String val = cell.getStringCellValue().trim();
            return val.isEmpty() ? null : Long.valueOf(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC -> {
                    // Nếu là kiểu ngày thực sự hoặc số serial Excel
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                    } else {
                        // Nếu là số serial ngày (General format)
                        double numericValue = cell.getNumericCellValue();
                        return LocalDate.ofEpochDay((long) numericValue - 25569); // Excel epoch 1900
                    }
                }
                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    if (val.isEmpty()) {
                        return null;
                    }

                    // Thử nhiều pattern khác nhau
                    String[] patterns = {
                        "M/d/yyyy", "MM/dd/yyyy", "d/M/yyyy",
                        "yyyy-MM-dd", "dd-MM-yyyy", "dd/MM/yyyy",
                        "M/d/yy", "dd/MM/yy"
                    };

                    for (String p : patterns) {
                        try {
                            return LocalDate.parse(val,
                                    java.time.format.DateTimeFormatter.ofPattern(p)
                                            .withResolverStyle(java.time.format.ResolverStyle.LENIENT));
                        } catch (Exception ignore) {
                        }
                    }

                    // Cuối cùng thử ISO format mặc định (ví dụ "2025-10-11")
                    try {
                        return LocalDate.parse(val);
                    } catch (Exception ignore) {
                    }
                }
                case FORMULA -> {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    CellValue evaluated = evaluator.evaluate(cell);
                    if (evaluated == null) {
                        return null;
                    }
                    if (evaluated.getCellType() == CellType.NUMERIC) {
                        return evaluated.getNumberValue() > 25569
                                ? LocalDate.ofEpochDay((long) evaluated.getNumberValue() - 25569)
                                : null;
                    } else if (evaluated.getCellType() == CellType.STRING) {
                        cell.setCellValue(evaluated.getStringValue());
                        return parseDate(cell);
                    }
                }
                default -> {
                    return null;
                }
            }
        } catch (Exception e) {
        }

        return null;
    }

    private LocalTime parseTime(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC -> {
                    double numericValue = cell.getNumericCellValue();

                    // Nếu là kiểu time thật trong Excel (0 <= value < 1) hoặc có kèm ngày
                    if (DateUtil.isCellDateFormatted(cell) || numericValue > 0) {
                        // Nếu lớn hơn 1 (nghĩa là có cả phần ngày), lấy phần thập phân (phần thời gian)
                        double timePortion = numericValue % 1;
                        long totalSeconds = Math.round(timePortion * 24 * 60 * 60);
                        return LocalTime.ofSecondOfDay(totalSeconds);
                    }
                }

                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    if (val.isEmpty()) {
                        return null;
                    }

                    String[] patterns = {
                        "H:mm", "HH:mm", "H:mm:ss", "HH:mm:ss",
                        "h:mm a", "hh:mm a" // 12h format
                    };

                    for (String p : patterns) {
                        try {
                            return LocalTime.parse(val,
                                    java.time.format.DateTimeFormatter.ofPattern(p)
                                            .withResolverStyle(java.time.format.ResolverStyle.LENIENT));
                        } catch (Exception ignore) {
                        }
                    }
                }

                case FORMULA -> {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    CellValue evaluated = evaluator.evaluate(cell);
                    if (evaluated == null) {
                        return null;
                    }

                    if (evaluated.getCellType() == CellType.NUMERIC) {
                        double numericValue = evaluated.getNumberValue();
                        double timePortion = numericValue % 1;
                        long totalSeconds = Math.round(timePortion * 24 * 60 * 60);
                        return LocalTime.ofSecondOfDay(totalSeconds);
                    } else if (evaluated.getCellType() == CellType.STRING) {
                        return parseTime(cell);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    // ----------- Chuyển từ DTO sang Entity (AttendanceLog) ----------- 
    private List<AttendanceLog> convertDtoToEntity(
            List<AttendanceLogDto> dtos) throws SQLException {
        TimesheetPeriodDao periodDao = new TimesheetPeriodDao();
        List<AttendanceLog> entities = new ArrayList<>();

        for (AttendanceLogDto dto : dtos) {
            if (dto == null) {
                continue;
            }

            // Tra periodId bằng DAO
            Optional<Long> periodIdOpt = Optional.empty();

            if (dto.getPeriod() != null) {
                periodIdOpt = periodDao.findIdByName(dto.getPeriod());
            }
            
            Long periodId = periodIdOpt.orElse(null);
            System.out.println(periodId);
            // Check-in
            if (dto.getCheckIn() != null && dto.getDate() != null) {
                AttendanceLog checkInLog = new AttendanceLog();
                checkInLog.setUserId(dto.getEmployeeId());
                checkInLog.setCheckType("IN");
                checkInLog.setCheckedAt(LocalDateTime.of(dto.getDate(), dto.getCheckIn()));
                checkInLog.setSource(dto.getSource());
                checkInLog.setNote(dto.getStatus());
                checkInLog.setPeriodId(periodId);
                entities.add(checkInLog);
            }

            // Check-out
            if (dto.getCheckOut() != null && dto.getDate() != null) {
                AttendanceLog checkOutLog = new AttendanceLog();
                checkOutLog.setUserId(dto.getEmployeeId());
                checkOutLog.setCheckType("OUT");
                checkOutLog.setCheckedAt(LocalDateTime.of(dto.getDate(), dto.getCheckOut()));
                checkOutLog.setSource(dto.getSource());
                checkOutLog.setNote(dto.getStatus());
                checkOutLog.setPeriodId(periodId);
                entities.add(checkOutLog);
            }
        }

        return entities;
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
