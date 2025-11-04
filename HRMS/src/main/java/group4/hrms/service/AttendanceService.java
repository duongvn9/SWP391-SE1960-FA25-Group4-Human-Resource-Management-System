package group4.hrms.service;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import group4.hrms.model.Request;

import group4.hrms.util.ExcelUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
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
        if (dto == null || dto.getDate() == null) {
            return "Invalid";
        }

        LocalTime checkIn = dto.getCheckIn();
        LocalTime checkOut = dto.getCheckOut();
        if (checkIn == null || checkOut == null) {
            return "Invalid";
        }

        // Giờ làm việc chuẩn
        LocalTime standardMorningStart = LocalTime.of(8, 0);   // 08:00
        LocalTime standardMorningEnd = LocalTime.of(12, 0);    // 12:00
        LocalTime standardAfternoonStart = LocalTime.of(13, 0); // 13:00
        LocalTime standardAfternoonEnd = LocalTime.of(17, 0);   // 17:00

        int graceMinutes = 10; // Ân hạn 10 phút

        // Biến tạm cho leave & OT (sẽ thay sau)
        boolean hasAMLeave = false;
        boolean hasPMLeave = false;
        boolean hasApprovedOT = false;

        if (hasApprovedOT) {
            return "Over Time";
        }

        boolean isLate = false;
        boolean isEarlyLeave = false;

        // ====== Xác định làm ca nào ======
        // Giả định nếu checkOut <= 13h thì là ca sáng, nếu checkIn >= 12h thì là ca chiều
        boolean isMorningShift = checkOut.isBefore(standardAfternoonStart);
        boolean isAfternoonShift = checkIn.isAfter(standardMorningEnd);

        // ====== Kiểm tra đi muộn / về sớm theo từng ca ======
        if (isMorningShift) {
            // Ca sáng
            LocalTime lateThreshold = standardMorningStart.plusMinutes(graceMinutes); // 08:10
            LocalTime earlyThreshold = standardMorningEnd.minusMinutes(graceMinutes); // 11:50

            if (!hasAMLeave && checkIn.isAfter(lateThreshold)) {
                isLate = true;
            }
            if (!hasAMLeave && checkOut.isBefore(earlyThreshold)) {
                isEarlyLeave = true;
            }
        } else if (isAfternoonShift) {
            // Ca chiều
            LocalTime lateThreshold = standardAfternoonStart.plusMinutes(graceMinutes); // 13:10
            LocalTime earlyThreshold = standardAfternoonEnd.minusMinutes(graceMinutes); // 16:50

            if (!hasPMLeave && checkIn.isAfter(lateThreshold)) {
                isLate = true;
            }
            if (!hasPMLeave && checkOut.isBefore(earlyThreshold)) {
                isEarlyLeave = true;
            }
        } else {
            // Cả ngày
            LocalTime morningLateThreshold = standardMorningStart.plusMinutes(graceMinutes);
            LocalTime afternoonEarlyThreshold = standardAfternoonEnd.minusMinutes(graceMinutes);

            if (!hasAMLeave && checkIn.isAfter(morningLateThreshold)) {
                isLate = true;
            }
            if (!hasPMLeave && checkOut.isBefore(afternoonEarlyThreshold)) {
                isEarlyLeave = true;
            }
        }

        // ====== Kết quả cuối ======
        if (isLate && isEarlyLeave) {
            return "Late & Early Leave";
        } else if (isLate) {
            return "Late";
        } else if (isEarlyLeave) {
            return "Early Leave";
        } else {
            return "On Time";
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

    public static Map<String, Object> calculateAttendanceSummary(Long userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        Map<String, Object> summary = new HashMap<>();

        AttendanceLogDao attendanceDao = new AttendanceLogDao();
        RequestDao requestDao = new RequestDao();

        // Lấy tất cả attendance records trong khoảng thời gian
        List<AttendanceLogDto> attendanceList = attendanceDao.findByFilter(
                userId, null, null, startDate, endDate, null, null, null,
                Integer.MAX_VALUE, 0, false
        );

        // Khởi tạo các biến đếm
        int totalWorkingDays = 0;
        int daysOnTime = 0;
        int daysLate = 0;
        int daysEarlyLeaving = 0;
        int daysLateAndEarlyLeaving = 0;
        int daysAbsent = 0;
        double totalHoursWorked = 0.0;
        double overtimeHours = 0.0;

        // Tạo map để tra cứu attendance theo ngày
        Map<LocalDate, AttendanceLogDto> attendanceMap = new HashMap<>();
        for (AttendanceLogDto record : attendanceList) {
            attendanceMap.put(record.getDate(), record);
        }

        // Lấy danh sách approved leave requests trong khoảng thời gian
        List<Request> approvedLeaveRequests = requestDao.findByUserIdAndDateRange(
                userId,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59),
                List.of("APPROVED"),
                null
        );

        // Lấy danh sách approved OT requests trong khoảng thời gian
        List<Request> approvedOTRequests = requestDao.findOTRequestsByUserIdAndDateRange(
                userId,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // Tạo set các ngày có leave được duyệt
        Map<LocalDate, Boolean> leaveDates = new HashMap<>();
        for (Request leaveRequest : approvedLeaveRequests) {
            try {
                // Parse JSON để lấy startDate và endDate
                String detailJson = leaveRequest.getDetailJson();
                if (detailJson != null && leaveRequest.getLeaveDetail() != null) {
                    LocalDate leaveStart = LocalDate.parse(leaveRequest.getLeaveDetail().getStartDate());
                    LocalDate leaveEnd = LocalDate.parse(leaveRequest.getLeaveDetail().getEndDate());

                    // Đánh dấu tất cả ngày trong khoảng leave
                    LocalDate current = leaveStart;
                    while (!current.isAfter(leaveEnd)) {
                        if (!current.isBefore(startDate) && !current.isAfter(endDate)) {
                            leaveDates.put(current, true);
                        }
                        current = current.plusDays(1);
                    }
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Tạo set các ngày có OT được duyệt
        Map<LocalDate, Boolean> otDates = new HashMap<>();
        for (Request otRequest : approvedOTRequests) {
            try {
                if (otRequest.getDetailJson() != null && otRequest.getOtDetail() != null) {
                    LocalDate otDate = LocalDate.parse(otRequest.getOtDetail().getOtDate());
                    if (!otDate.isBefore(startDate) && !otDate.isAfter(endDate)) {
                        otDates.put(otDate, true);
                    }
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }

        // Chỉ tính toán dựa trên attendance records thực tế có
        for (AttendanceLogDto record : attendanceList) {
            totalWorkingDays++;

            // Tính số giờ làm việc
            if (record.getCheckIn() != null && record.getCheckOut() != null) {
                long minutes = ChronoUnit.MINUTES.between(record.getCheckIn(), record.getCheckOut());
                double hours = minutes / 60.0;

                // Trừ giờ nghỉ trưa (1 tiếng)
                if (hours > 4) { // Chỉ trừ nếu làm việc hơn 4 tiếng
                    hours -= 1.0;
                }

                totalHoursWorked += Math.max(0, hours);

                // Tính overtime nếu có OT được duyệt
                if (otDates.containsKey(record.getDate())) {
                    // Giờ làm việc chuẩn là 8 tiếng
                    double standardHours = 8.0;
                    if (hours > standardHours) {
                        overtimeHours += (hours - standardHours);
                    }
                }
            }

            // Tính lại status để đảm bảo chính xác
            String originalStatus = record.getStatus();
            String calculatedStatus = calculateAttendanceStatus(record);

            // Debug log để kiểm tra
            System.out.println("DEBUG Summary - Date: " + record.getDate()
                    + ", CheckIn: " + record.getCheckIn()
                    + ", CheckOut: " + record.getCheckOut()
                    + ", Original Status: " + originalStatus
                    + ", Calculated Status: " + calculatedStatus);

            // Phân loại theo status đã tính lại
            if (calculatedStatus != null) {
                switch (calculatedStatus) {
                    case "On Time" ->
                        daysOnTime++;
                    case "Late" ->
                        daysLate++;
                    case "Early Leave" ->
                        daysEarlyLeaving++;
                    case "Late & Early Leave" ->
                        daysLateAndEarlyLeaving++;
                    case "Over Time" ->
                        daysOnTime++; // OT vẫn tính là on time
                    default -> {
                        // Invalid hoặc status khác không tính vào
                        System.out.println("DEBUG Summary - Unhandled status: " + calculatedStatus);
                    }
                }
            }
        }

        // Days absent = 0 (không tự động tính absent)
        daysAbsent = 0;

        // Đưa kết quả vào map (bỏ averageWorkingHours và workdayRatio)
        summary.put("totalWorkingDays", totalWorkingDays);
        summary.put("daysOnTime", daysOnTime);
        summary.put("daysLate", daysLate);
        summary.put("daysEarlyLeaving", daysEarlyLeaving);
        summary.put("daysLateAndEarlyLeaving", daysLateAndEarlyLeaving);
        summary.put("daysAbsent", daysAbsent);
        summary.put("totalHoursWorked", Math.round(totalHoursWorked * 100.0) / 100.0);
        summary.put("overtimeHours", Math.round(overtimeHours * 100.0) / 100.0);

        return summary;
    }
}
