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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

        // ===== 1. Lấy danh sách chấm công =====
        List<AttendanceLogDto> attendanceList = attendanceDao.findByFilter(
                userId, null, null, startDate, endDate, null, null, null,
                Integer.MAX_VALUE, 0, false
        );

        // ===== 2. Khởi tạo biến thống kê =====
        int daysOnTime = 0;
        int daysLate = 0;
        int daysEarlyLeaving = 0;
        int daysLateAndEarlyLeaving = 0;
        int daysAbsent = 0;
        double totalHoursWorked = 0.0;
        double overtimeHours = 0.0;

        // ===== 3. Lấy đơn nghỉ phép & OT đã duyệt =====
        List<Request> approvedLeaveRequests = requestDao.findByUserIdAndDateRange(
                userId,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59),
                List.of("APPROVED"),
                null
        );

        List<Request> approvedOTRequests = requestDao.findOTRequestsByUserIdAndDateRange(
                userId,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // ===== 4. Map ngày có phép =====
        Map<LocalDate, Boolean> leaveDates = new HashMap<>();
        for (Request leaveRequest : approvedLeaveRequests) {
            try {
                if (leaveRequest.getLeaveDetail() != null) {
                    LocalDate leaveStart = LocalDate.parse(leaveRequest.getLeaveDetail().getStartDate());
                    LocalDate leaveEnd = LocalDate.parse(leaveRequest.getLeaveDetail().getEndDate());
                    LocalDate date = leaveStart;
                    while (!date.isAfter(leaveEnd)) {
                        if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                            leaveDates.put(date, true);
                        }
                        date = date.plusDays(1);
                    }
                }
            } catch (Exception e) {
                // Ignore parse errors
            }
        }

        // ===== 5. Map ngày có OT =====
        Map<LocalDate, Boolean> otDates = new HashMap<>();
        for (Request otRequest : approvedOTRequests) {
            try {
                if (otRequest.getOtDetail() != null) {
                    LocalDate otDate = LocalDate.parse(otRequest.getOtDetail().getOtDate());
                    if (!otDate.isBefore(startDate) && !otDate.isAfter(endDate)) {
                        otDates.put(otDate, true);
                    }
                }
            } catch (Exception e) {
                // Ignore parse errors
            }
        }

        // ===== 6. Lọc bỏ các bản ghi chỉ có in hoặc chỉ có out =====
        List<AttendanceLogDto> validAttendanceList = attendanceList.stream()
                .filter(r -> r != null && r.getDate() != null && r.getCheckIn() != null && r.getCheckOut() != null)
                .collect(Collectors.toList());

        // ===== 7. Tính số ngày có bản ghi hợp lệ =====
        int totalWorkingDays = 0;
        if (!validAttendanceList.isEmpty()) {
            Set<LocalDate> recordedDates = validAttendanceList.stream()
                    .map(AttendanceLogDto::getDate)
                    .collect(Collectors.toSet());
            totalWorkingDays = recordedDates.size();
        }

        // ===== 8. Gom nhóm bản ghi theo ngày =====
        Map<LocalDate, List<AttendanceLogDto>> groupedByDate = validAttendanceList.stream()
                .collect(Collectors.groupingBy(AttendanceLogDto::getDate));

        Set<LocalDate> attendedDates = new HashSet<>();

        // ===== 9. Tính toán theo ngày =====
        for (Map.Entry<LocalDate, List<AttendanceLogDto>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<AttendanceLogDto> logsOfDay = entry.getValue();
            attendedDates.add(date);

            // --- Tổng thời gian ngày ---
            long totalMinutesOfDay = 0;
            for (AttendanceLogDto log : logsOfDay) {
                long minutes = ChronoUnit.MINUTES.between(log.getCheckIn(), log.getCheckOut());
                if (minutes > 0) {
                    totalMinutesOfDay += minutes;
                }
            }

            double dailyHours = totalMinutesOfDay / 60.0;

            // Nếu chỉ có 1 ca > 4h => trừ 1h nghỉ trưa
            if (logsOfDay.size() == 1 && dailyHours > 4.0) {
                dailyHours -= 1.0;
            }

            if (dailyHours < 0) {
                dailyHours = 0;
            }

            totalHoursWorked += dailyHours;

            // --- Tính OT ---
            if (otDates.containsKey(date)) {
                double standardHours = 8.0;
                if (dailyHours > standardHours) {
                    overtimeHours += (dailyHours - standardHours);
                }
            }

            // --- Xác định status ngày ---
            boolean lateFlag = false;
            boolean earlyFlag = false;

            for (AttendanceLogDto log : logsOfDay) {
                String s = calculateAttendanceStatus(log);
                if (s == null) {
                    continue;
                }

                switch (s) {
                    case "Late" ->
                        lateFlag = true;
                    case "Early Leave" ->
                        earlyFlag = true;
                    case "Late & Early Leave" -> {
                        lateFlag = true;
                        earlyFlag = true;
                    }
                }
            }

            if (lateFlag && earlyFlag) {
                daysLateAndEarlyLeaving++;
            } else if (lateFlag) {
                daysLate++;
            } else if (earlyFlag) {
                daysEarlyLeaving++;
            } else {
                daysOnTime++;
            }
        }

        // ===== 10. Tính ngày vắng =====
        daysAbsent = Math.max(0, totalWorkingDays - attendedDates.size());

        // ===== 11. Tổng hợp kết quả =====
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
