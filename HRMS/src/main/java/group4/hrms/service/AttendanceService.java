package group4.hrms.service;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;

import group4.hrms.util.ExcelUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AttendanceService {

    public static List<AttendanceLogDto> readAttendanceExcelFile(InputStream inputStream) throws IOException {
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

                boolean isEmpty = true;
                for (int c = 0; c <= 8; c++) {
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
                    continue;
                }
                AttendanceLogDto dto = new AttendanceLogDto();

                dto.setUserId(ExcelUtil.parseLong(row.getCell(0)));
                dto.setEmployeeName(ExcelUtil.parseString(row.getCell(1)));
                dto.setDepartment(ExcelUtil.parseString(row.getCell(2)));
                LocalDate date = ExcelUtil.parseDate(row.getCell(3));
                dto.setDate(date);

                LocalTime checkIn = ExcelUtil.parseTime(row.getCell(4));
                LocalTime checkOut = ExcelUtil.parseTime(row.getCell(5));
                if (date != null) {
                    if (checkIn != null) {
                        dto.setCheckIn(ExcelUtil.parseTime(row.getCell(4)));
                    }
                    if (checkOut != null) {
                        dto.setCheckOut(ExcelUtil.parseTime(row.getCell(5)));
                    }
                }

                // Tự động tính status thay vì đọc từ Excel
                String calculatedStatus = calculateAttendanceStatus(dto);
                dto.setStatus(calculatedStatus);
                dto.setSource("Excel");
                dto.setPeriod(ExcelUtil.parseString(row.getCell(7)));

                list.add(dto);
            }
        }
        return list;
    }

    public static List<AttendanceLogDto> readExcel(Path filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return AttendanceService.readAttendanceExcelFile(inputStream);
        }
    }

    public static String calculateAttendanceStatus(AttendanceLogDto dto) {
        // Kiểm tra dữ liệu đầu vào
        if (dto == null || dto.getDate() == null) {
            return "Invalid";
        }
        
        LocalTime checkIn = dto.getCheckIn();
        LocalTime checkOut = dto.getCheckOut();
        
        // Nếu chỉ có in hoặc chỉ có out thì invalid
        if (checkIn == null || checkOut == null) {
            return "Invalid";
        }
        
        // Giờ làm việc chuẩn
        LocalTime standardMorningStart = LocalTime.of(8, 0);   // 08:00
        LocalTime standardMorningEnd = LocalTime.of(12, 0);    // 12:00
        LocalTime standardAfternoonStart = LocalTime.of(13, 0); // 13:00
        LocalTime standardAfternoonEnd = LocalTime.of(17, 0);   // 17:00
        
        // Ân hạn (10 phút)
        int graceMinutes = 10;
        LocalTime lateThreshold = standardMorningStart.plusMinutes(graceMinutes); // 08:10
        LocalTime earlyThreshold = standardAfternoonEnd.minusMinutes(graceMinutes); // 16:50
        
        // TODO: Kiểm tra đơn nghỉ phép và OT (cần implement sau khi có DAO tương ứng)
        // boolean hasAMLeave = checkLeaveRequest(dto.getUserId(), dto.getDate(), "AM");
        // boolean hasPMLeave = checkLeaveRequest(dto.getUserId(), dto.getDate(), "PM");
        // boolean hasApprovedOT = checkOTRequest(dto.getUserId(), dto.getDate());
        
        // Tạm thời set false cho các trường hợp này
        boolean hasAMLeave = false;
        boolean hasPMLeave = false;
        boolean hasApprovedOT = false;
        
        // Nếu có OT được duyệt
        if (hasApprovedOT) {
            return "Over Time";
        }
        
        boolean isLate = false;
        boolean isEarlyLeave = false;
        
        // Kiểm tra đi muộn
        if (!hasAMLeave && checkIn.isAfter(lateThreshold)) {
            isLate = true;
        }
        
        // Kiểm tra về sớm
        if (!hasPMLeave && checkOut.isBefore(earlyThreshold)) {
            isEarlyLeave = true;
        }
        
        // Xác định status
        if (isLate && isEarlyLeave) {
            return "Late & Early Leave";
        } else if (isLate) {
            return "Late";
        } else if (isEarlyLeave) {
            return "Early Leave";
        } else {
            return "On time";
        }
    }
    
    public static List<AttendanceLogDto> calculateStatusForList(List<AttendanceLogDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        
        for (AttendanceLogDto dto : dtos) {
            String calculatedStatus = calculateAttendanceStatus(dto);
            dto.setStatus(calculatedStatus);
        }
        
        return dtos;
    }

    public static void processImport(List<AttendanceLogDto> dtos, String action, Path tempFilePath, HttpServletRequest req) throws SQLException, IOException {
        if ("Preview".equalsIgnoreCase(action)) {
            req.setAttribute("previewLogs", dtos);

        } else if ("Import".equalsIgnoreCase(action)) {
            AttendanceLogDao attendanceLogDAO = new AttendanceLogDao();

            // Bước 1: Kiểm tra mâu thuẫn nội bộ trong Excel
            Map<String, List<AttendanceLogDto>> excelValidation = attendanceLogDAO.validateExcelInternalConsistency(dtos);
            List<AttendanceLogDto> excelValidLogs = excelValidation.get("valid");
            List<AttendanceLogDto> invalidLogsDto = excelValidation.get("invalid"); // lưu tạm danh sách invalid từ Excel

            // Bước 2: Tính lại status cho các bản ghi hợp lệ
            calculateStatusForList(excelValidLogs);
            
            // Bước 3: Kiểm tra mâu thuẫn với DB
            Map<String, List<AttendanceLogDto>> dbValidation = attendanceLogDAO.validateAndImportExcelLogs(excelValidLogs);
            List<AttendanceLogDto> dbValidLogs = dbValidation.get("valid");
            List<AttendanceLogDto> dbInvalidLogs = dbValidation.get("invalid");

            // Gộp tất cả invalid từ Excel và DB
            if (dbInvalidLogs != null && !dbInvalidLogs.isEmpty()) {
                invalidLogsDto.addAll(dbInvalidLogs);
            }

            // Lưu lại danh sách valid để import
            List<AttendanceLog> logs = AttendanceMapper.convertDtoToEntity(dbValidLogs);
            attendanceLogDAO.saveAttendanceLogs(logs);

            req.setAttribute("success", "Imported " + dbValidLogs.size() + " valid attendance logs successfully.");

            // Hiển thị invalid logs
            if (!invalidLogsDto.isEmpty()) {
                int page = 1;
                String pageParam = req.getParameter("invalidPage");
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
                int totalInvalid = invalidLogsDto.size();
                int totalPages = (int) Math.ceil((double) totalInvalid / recordsPerPage);

                int fromIndex = (page - 1) * recordsPerPage;
                int toIndex = Math.min(fromIndex + recordsPerPage, totalInvalid);

                List<AttendanceLogDto> pageInvalidLogs = new ArrayList<>();
                if (fromIndex < totalInvalid) {
                    pageInvalidLogs = invalidLogsDto.subList(fromIndex, toIndex);
                }

                req.setAttribute("warning", invalidLogsDto.size() + " records were invalid and not imported.");
                req.setAttribute("invalidLogsExcel", pageInvalidLogs);
                req.setAttribute("invalidCurrentPage", page);
                req.setAttribute("invalidTotalPages", totalPages);
                req.getSession().setAttribute("invalidLogsAll", invalidLogsDto);               
            }

            Files.deleteIfExists(tempFilePath);
            req.getSession().removeAttribute("uploadedFile");
        } else {
            req.setAttribute("error", "Invalid action.");
        }
    }
}
