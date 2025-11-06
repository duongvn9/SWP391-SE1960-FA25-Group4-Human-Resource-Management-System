package group4.hrms.service;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import group4.hrms.model.Department;
import group4.hrms.model.Request;
import group4.hrms.model.TimesheetPeriod;
import group4.hrms.model.User;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AttendanceService {

    public static String validateExcelStructure(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Kiểm tra file rỗng
            if (sheet.getLastRowNum() < 0) {
                return "Excel file is empty. Please add data to the file.";
            }
            
            // Kiểm tra có header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return "Excel file must have header line.";
            }
            
            // Kiểm tra số cột trong header
            int numColumns = headerRow.getLastCellNum();
            if (numColumns < 4) {
                return "The Excel file must have 4 fields: Employee ID, date, check_in, check_out";
            }
            
            if (numColumns > 5) {
                return "The Excel file has too many fields. The file must have exactly 4 fields in order: Employee ID, date, check_in, check_out";
            }
            
            // Kiểm tra header names (optional - có thể bỏ nếu không cần strict)
            String[] expectedHeaders = {"Employee ID", "date", "check_in", "check_out"};
            for (int i = 0; i < 4; i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null || cell.toString().trim().isEmpty()) {
                    return "Missing column header " + (i + 1) + ". Expectation: " + expectedHeaders[i];
                }
            }
            
            // Kiểm tra có dữ liệu sau header không
            if (sheet.getLastRowNum() <= 0) {
                return "Excel file has only header but no data. Please add at least one record.";
            }
            
            // Validation thành công
            return null;
            
        } catch (IOException e) {
            return "Error reading Excel file: " + e.getMessage();
        }
    }

    public static List<AttendanceLogDto> readAttendanceExcelFile(InputStream inputStream) throws IOException {
        List<AttendanceLogDto> list = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            int firstRow = sheet.getFirstRowNum() + 1; // Skip header row 
            int lastRow = sheet.getLastRowNum();

            for (int i = firstRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // Kiểm tra xem dòng có dữ liệu thực sự không
                boolean isEmpty = true;
                for (int c = 0; c <= 3; c++) { 
                    Cell cell = row.getCell(c);
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        String val = cell.toString().trim();
                        if (!val.isEmpty() && !val.equals("null")) {
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
                dto.setDate(ExcelUtil.parseDate(row.getCell(1))); 
                dto.setCheckIn(ExcelUtil.parseTime(row.getCell(2))); 
                dto.setCheckOut(ExcelUtil.parseTime(row.getCell(3))); 
                dto.setSource("Excel");
                
                if (dto.getUserId() == null && dto.getDate() == null && 
                    dto.getCheckIn() == null && dto.getCheckOut() == null) {
                    continue; 
                }
                
                list.add(dto);
            }
        }
        
        return list;
    }

    public static List<AttendanceLogDto> readExcelForPreview(Path filePath) throws IOException, SQLException {
        // Validate cấu trúc file trước khi đọc
        try (InputStream validationStream = Files.newInputStream(filePath)) {
            String structureError = validateExcelStructure(validationStream);
            if (structureError != null) {
                throw new IOException(structureError);
            }
        }
        
        // Đọc dữ liệu từ file
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            List<AttendanceLogDto> basicLogs = AttendanceService.readAttendanceExcelFile(inputStream);
            // Enrichment thông tin từ database cho TẤT CẢ bản ghi (cả valid và invalid)
            return enrichAttendanceLogsForPreview(basicLogs);
        }
    }
    
    public static List<AttendanceLogDto> readExcel(Path filePath) throws IOException, SQLException {
        // Validate cấu trúc file trước khi đọc
        try (InputStream validationStream = Files.newInputStream(filePath)) {
            String structureError = validateExcelStructure(validationStream);
            if (structureError != null) {
                throw new IOException(structureError);
            }
        }
        
        // Đọc dữ liệu từ file
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            List<AttendanceLogDto> basicLogs = AttendanceService.readAttendanceExcelFile(inputStream);
            // Enrichment thông tin từ database (chỉ cho các bản ghi không có lỗi)
            return enrichAttendanceLogsFromDatabase(basicLogs);
        }
    }
    
    public static List<AttendanceLogDto> enrichAttendanceLogsFromDatabase(List<AttendanceLogDto> basicLogs) throws SQLException {
        if (basicLogs == null || basicLogs.isEmpty()) {
            return basicLogs;
        }
        
        // Sử dụng các DAO có sẵn thay vì raw SQL
        UserDao userDao = new UserDao();
        DepartmentDao departmentDao = new DepartmentDao();
        TimesheetPeriodDao timesheetPeriodDao = new TimesheetPeriodDao();
        
        for (AttendanceLogDto dto : basicLogs) {
            // Chỉ enrichment cho các bản ghi không có lỗi format
            if (dto.getError() != null) {
                continue; // Skip bản ghi có lỗi format
            }
            
            // Lấy thông tin employee và department
            if (dto.getUserId() != null) {
                Optional<User> userOpt = userDao.findById(dto.getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    dto.setEmployeeName(user.getFullName());
                    
                    // Lấy thông tin department
                    if (user.getDepartmentId() != null) {
                        Optional<Department> deptOpt = departmentDao.findById(user.getDepartmentId());
                        if (deptOpt.isPresent()) {
                            dto.setDepartment(deptOpt.get().getName());
                        } else {
                            dto.setDepartment("Unknown Department");
                        }
                    } else {
                        dto.setDepartment("No Department");
                    }
                } else {
                    // Employee không tồn tại - đánh dấu để validation sau này bắt lỗi
                    dto.setEmployeeName("Unknown Employee (ID: " + dto.getUserId() + ")");
                    dto.setDepartment("Unknown Department");
                }
            }
            
            // Lấy thông tin period dựa trên date
            if (dto.getDate() != null) {
                try {
                    TimesheetPeriod period = timesheetPeriodDao.findPeriodByDate(dto.getDate());
                    if (period != null) {
                        dto.setPeriod(period.getName());
                        dto.setIsLocked(period.getIsLocked());
                    } else {
                        dto.setPeriod("No Period");
                        dto.setIsLocked(false);
                    }
                } catch (SQLException e) {
                    dto.setPeriod("Error loading period");
                    dto.setIsLocked(false);
                }
            }
        }
        
        // Tính status cho các bản ghi không có lỗi sau khi enrichment
        for (AttendanceLogDto dto : basicLogs) {
            if (dto.getError() == null && (dto.getCheckIn() != null || dto.getCheckOut() != null)) {
                String calculatedStatus = calculateAttendanceStatus(dto);
                dto.setStatus(calculatedStatus);
            } else if (dto.getError() != null) {
                dto.setStatus("Invalid");
            }
        }
        
        return basicLogs;
    }
    
    public static List<AttendanceLogDto> enrichAttendanceLogsForPreview(List<AttendanceLogDto> basicLogs) throws SQLException {
        if (basicLogs == null || basicLogs.isEmpty()) {
            return basicLogs;
        }
        
        // Sử dụng các DAO có sẵn thay vì raw SQL
        UserDao userDao = new UserDao();
        DepartmentDao departmentDao = new DepartmentDao();
        TimesheetPeriodDao timesheetPeriodDao = new TimesheetPeriodDao();
        
        for (AttendanceLogDto dto : basicLogs) {
            // Enrichment cho TẤT CẢ bản ghi, kể cả có lỗi format
            
            // Lấy thông tin employee và department
            if (dto.getUserId() != null) {
                Optional<User> userOpt = userDao.findById(dto.getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    dto.setEmployeeName(user.getFullName());
                    
                    // Lấy thông tin department
                    if (user.getDepartmentId() != null) {
                        Optional<Department> deptOpt = departmentDao.findById(user.getDepartmentId());
                        if (deptOpt.isPresent()) {
                            dto.setDepartment(deptOpt.get().getName());
                        } else {
                            dto.setDepartment("Unknown Department");
                        }
                    } else {
                        dto.setDepartment("No Department");
                    }
                } else {
                    // Employee không tồn tại - đánh dấu để validation sau này bắt lỗi
                    dto.setEmployeeName("Unknown Employee (ID: " + dto.getUserId() + ")");
                    dto.setDepartment("Unknown Department");
                }
            } else {
                // Nếu userId null, vẫn set thông tin mặc định để hiển thị
                dto.setEmployeeName("Invalid Employee ID");
                dto.setDepartment("Unknown Department");
            }
            // Lấy thông tin period dựa trên date
            if (dto.getDate() != null) {
                try {
                    TimesheetPeriod period = timesheetPeriodDao.findPeriodByDate(dto.getDate());
                    if (period != null) {
                        dto.setPeriod(period.getName());
                        dto.setIsLocked(period.getIsLocked());
                    } else {
                        dto.setPeriod("No Period");
                        dto.setIsLocked(false);
                    }
                } catch (SQLException e) {
                    dto.setPeriod("Error loading period");
                    dto.setIsLocked(false);
                }
            } else {
                dto.setPeriod("Invalid Date");
                dto.setIsLocked(false);
            }
        }
        
        // Tính status cho TẤT CẢ bản ghi
        for (AttendanceLogDto dto : basicLogs) {
            if (dto.getError() != null) {
                dto.setStatus("Invalid");
            } else if (dto.getCheckIn() != null || dto.getCheckOut() != null) {
                String calculatedStatus = calculateAttendanceStatus(dto);
                dto.setStatus(calculatedStatus);
            } else {
                dto.setStatus("Invalid");
            }
        }
        
        return basicLogs;
    }
    
    public static Map<String, List<AttendanceLogDto>> separateValidAndInvalidRecords(List<AttendanceLogDto> allRecords) {
        Map<String, List<AttendanceLogDto>> result = new HashMap<>();
        List<AttendanceLogDto> validRecords = new ArrayList<>();
        List<AttendanceLogDto> invalidRecords = new ArrayList<>();
        
        for (AttendanceLogDto dto : allRecords) {
            if (dto.getError() != null) {
                invalidRecords.add(dto);
            } else {
                validRecords.add(dto);
            }
        }
        
        result.put("valid", validRecords);
        result.put("invalid", invalidRecords);
        return result;
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

        LocalTime standardMorningStart = LocalTime.of(8, 0);   
        LocalTime standardMorningEnd = LocalTime.of(12, 0);  
        LocalTime standardAfternoonStart = LocalTime.of(13, 0); 
        LocalTime standardAfternoonEnd = LocalTime.of(17, 0);  

        int graceMinutes = 10;
        
        // Kiểm tra ngoài giờ làm việc
        // 1. Check-in sau 17h (ca tối/ngoài giờ)
        if (checkIn.isAfter(standardAfternoonEnd)) {
            return "Outside Working Hours";
        }
        
        // 2. Check-in trước 6h sáng (ca đêm/sớm bất thường)
        LocalTime earlyMorningLimit = LocalTime.of(6, 0);
        if (checkIn.isBefore(earlyMorningLimit)) {
            return "Outside Working Hours";
        }
        
        // 3. Cả check-in và check-out đều ngoài khung giờ hành chính
        boolean checkInOutsideNormal = checkIn.isBefore(standardMorningStart.minusMinutes(30)) || 
                                      checkIn.isAfter(standardAfternoonEnd);
        boolean checkOutOutsideNormal = checkOut.isBefore(standardMorningStart) || 
                                       checkOut.isAfter(LocalTime.of(22, 0)); // sau 22h
        
        if (checkInOutsideNormal && checkOutOutsideNormal) {
            return "Outside Working Hours";
        } 

        // Biến tạm cho leave & OT
        boolean hasAMLeave = false;
        boolean hasPMLeave = false;
        boolean hasApprovedOT = checkForApprovedOT(dto);

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
            LocalTime lateThreshold = standardMorningStart.plusMinutes(graceMinutes); // 08:10
            LocalTime earlyThreshold = standardMorningEnd.minusMinutes(graceMinutes); // 11:50

            if (!hasAMLeave && checkIn.isAfter(lateThreshold)) {
                isLate = true;
            }
            if (!hasAMLeave && checkOut.isBefore(earlyThreshold)) {
                isEarlyLeave = true;
            }
        } else if (isAfternoonShift) {
            LocalTime lateThreshold = standardAfternoonStart.plusMinutes(graceMinutes); // 13:10
            LocalTime earlyThreshold = standardAfternoonEnd.minusMinutes(graceMinutes); // 16:50

            if (!hasPMLeave && checkIn.isAfter(lateThreshold)) {
                isLate = true;
            }
            if (!hasPMLeave && checkOut.isBefore(earlyThreshold)) {
                isEarlyLeave = true;
            }
        } else {
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

    private static boolean checkForApprovedOT(AttendanceLogDto dto) {
        if (dto == null || dto.getDate() == null || dto.getUserId() == null) {
            return false;
        }

        try {
            RequestDao requestDao = new RequestDao();
            
            // Tìm đơn OT đã duyệt cho ngày đó
            List<Request> otRequests = requestDao.findOTRequestsByUserIdAndDateRange(
                dto.getUserId(),
                dto.getDate().atStartOfDay(),
                dto.getDate().atTime(23, 59, 59)
            );

            if (otRequests.isEmpty()) {
                return false;
            }

            LocalTime checkIn = dto.getCheckIn();
            LocalTime checkOut = dto.getCheckOut();
            
            // Giờ hành chính chuẩn
            LocalTime standardMorningStart = LocalTime.of(8, 0);   
            LocalTime standardMorningEnd = LocalTime.of(12, 0);  
            LocalTime standardAfternoonStart = LocalTime.of(13, 0); 
            LocalTime standardAfternoonEnd = LocalTime.of(17, 0);

            for (Request otRequest : otRequests) {
                if (otRequest.getOtDetail() != null) {
                    try {
                        // Lấy thông tin từ đơn OT
                        LocalTime otStartTime = LocalTime.parse(otRequest.getOtDetail().getStartTime());
                        LocalTime otEndTime = LocalTime.parse(otRequest.getOtDetail().getEndTime());
                        
                        // Kiểm tra xem bản ghi attendance có nằm trong khung giờ OT không
                        boolean isWithinOTTimeframe = 
                            (checkIn.equals(otStartTime) || checkIn.isAfter(otStartTime)) &&
                            (checkOut.equals(otEndTime) || checkOut.isBefore(otEndTime));
                        
                        if (!isWithinOTTimeframe) {
                            continue;
                        }
                        
                        // Kiểm tra xem có dính dáng tới ca làm sáng chiều không
                        // Nếu OT nằm hoàn toàn ngoài giờ hành chính thì mới tính là OT
                        boolean isOutsideWorkingHours = 
                            // OT trước giờ làm sáng
                            (otEndTime.isBefore(standardMorningStart) || otEndTime.equals(standardMorningStart)) ||
                            // OT trong giờ nghỉ trưa
                            (otStartTime.equals(standardMorningEnd) && otEndTime.equals(standardAfternoonStart)) ||
                            // OT sau giờ làm chiều
                            (otStartTime.equals(standardAfternoonEnd) || otStartTime.isAfter(standardAfternoonEnd));
                        
                        if (isOutsideWorkingHours) {
                            return true;
                        }
                        
                    } catch (Exception e) {
                    }
                }
            }
            
        } catch (Exception e) {
            return false;
        }
        return false;
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

            Map<String, List<AttendanceLogDto>> excelValidation = attendanceLogDAO.validateExcelInternalConsistency(dtos);
            List<AttendanceLogDto> excelValidLogs = excelValidation.get("valid");
            List<AttendanceLogDto> invalidLogsDto = excelValidation.get("invalid"); // lưu tạm danh sách invalid từ Excel

            Map<String, List<AttendanceLogDto>> dbValidation = attendanceLogDAO.validateAndImportExcelLogs(excelValidLogs);
            List<AttendanceLogDto> dbValidLogs = dbValidation.get("valid");
            List<AttendanceLogDto> dbInvalidLogs = dbValidation.get("invalid");

            if (dbInvalidLogs != null && !dbInvalidLogs.isEmpty()) {
                invalidLogsDto.addAll(dbInvalidLogs);
            }

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

    public static List<AttendanceLogDto> filterSpamAndCleanLogs(List<AttendanceLogDto> logs) {
        List<AttendanceLogDto> cleanLogs = new ArrayList<>();

        if (logs == null || logs.isEmpty()) {
            return cleanLogs;
        }

        // Bước 1: Gom các bản ghi theo userId + date
        Map<String, List<AttendanceLogDto>> logsByUserDate = new HashMap<>();
        for (AttendanceLogDto log : logs) {
            if (log.getUserId() == null || log.getDate() == null) {
                continue; // Skip invalid records
            }
            String key = log.getUserId() + "_" + log.getDate();
            logsByUserDate.computeIfAbsent(key, k -> new ArrayList<>()).add(log);
        }

        // Xử lý từng nhóm user-date
        for (Map.Entry<String, List<AttendanceLogDto>> entry : logsByUserDate.entrySet()) {
            List<AttendanceLogDto> groupLogs = entry.getValue();
            
            // Bước 2: Tách các cặp IN/OUT thành bản ghi riêng lẻ
            List<IndividualRecord> individualRecords = new ArrayList<>();
            
            for (AttendanceLogDto log : groupLogs) {
                // Tạo bản ghi IN nếu có checkIn
                if (log.getCheckIn() != null) {
                    IndividualRecord inRecord = new IndividualRecord();
                    inRecord.originalLog = log;
                    inRecord.timestamp = log.getDate().atTime(log.getCheckIn());
                    inRecord.isCheckIn = true;
                    individualRecords.add(inRecord);
                }
                
                // Tạo bản ghi OUT nếu có checkOut
                if (log.getCheckOut() != null) {
                    IndividualRecord outRecord = new IndividualRecord();
                    outRecord.originalLog = log;
                    // Xử lý trường hợp checkOut qua ngày
                    if (log.getCheckIn() != null && log.getCheckOut().isBefore(log.getCheckIn())) {
                        outRecord.timestamp = log.getDate().plusDays(1).atTime(log.getCheckOut());
                    } else {
                        outRecord.timestamp = log.getDate().atTime(log.getCheckOut());
                    }
                    outRecord.isCheckIn = false;
                    individualRecords.add(outRecord);
                }
            }
            
            // Sắp xếp theo thời gian
            individualRecords.sort(Comparator.comparing(r -> r.timestamp));
            
            // Bước 3: Lọc spam (các bản ghi cách nhau <= 2 phút) - chỉ giữ bản ghi sớm nhất
            List<IndividualRecord> filteredRecords = filterSpamRecords(individualRecords);
            
            // Bước 4: Ghép lại theo thứ tự IN-OUT-IN-OUT...
            List<AttendanceLogDto> pairedLogs = pairRecordsInOrder(filteredRecords);
            
            cleanLogs.addAll(pairedLogs);
        }

        // Bước 5: Tự động tính status cho tất cả bản ghi đã clean
        for (AttendanceLogDto log : cleanLogs) {
            String calculatedStatus = calculateAttendanceStatus(log);
            log.setStatus(calculatedStatus);
        }

        return cleanLogs;
    }

    private static List<IndividualRecord> filterSpamRecords(List<IndividualRecord> records) {
        if (records.size() <= 1) {
            return records;
        }

        List<IndividualRecord> filtered = new ArrayList<>();
        
        // Tìm các nhóm spam
        List<IndividualRecord> currentGroup = new ArrayList<>();
        currentGroup.add(records.get(0));
        
        for (int i = 1; i < records.size(); i++) {
            IndividualRecord current = records.get(i);
            IndividualRecord previous = records.get(i - 1);
            
            // Kiểm tra khoảng cách thời gian <= 2 phút
            long minutesDiff = java.time.Duration.between(previous.timestamp, current.timestamp).toMinutes();
            
            if (minutesDiff <= 2) {
                // Cùng nhóm spam
                if (currentGroup.size() == 1) {
                    // Nhóm mới, thêm cả previous và current
                    currentGroup.add(current);
                } else {
                    // Nhóm đã có, chỉ thêm current
                    currentGroup.add(current);
                }
            } else {
                // Kết thúc nhóm hiện tại
                if (currentGroup.size() > 1) {
                    // Là nhóm spam - chỉ giữ bản ghi sớm nhất
                    currentGroup.sort(Comparator.comparing(r -> r.timestamp));
                    filtered.add(currentGroup.get(0)); // Chỉ lấy bản ghi sớm nhất
                } else {
                    // Không phải spam, thêm vào filtered
                    filtered.add(currentGroup.get(0));
                }
                
                // Bắt đầu nhóm mới
                currentGroup.clear();
                currentGroup.add(current);
            }
        }
        
        // Xử lý nhóm cuối cùng
        if (currentGroup.size() > 1) {
            // Là nhóm spam - chỉ giữ bản ghi sớm nhất
            currentGroup.sort(Comparator.comparing(r -> r.timestamp));
            filtered.add(currentGroup.get(0));
        } else if (currentGroup.size() == 1) {
            filtered.add(currentGroup.get(0));
        }
        
        // Sắp xếp lại filtered theo thời gian
        filtered.sort(Comparator.comparing(r -> r.timestamp));
        
        return filtered;
    }

    private static List<AttendanceLogDto> pairRecordsInOrder(List<IndividualRecord> records) {
        List<AttendanceLogDto> result = new ArrayList<>();
        
        if (records.isEmpty()) {
            return result;
        }
        
        // Ghép theo thứ tự: bản ghi đầu là IN, sau đó OUT, sau đó IN, OUT...
        for (int i = 0; i < records.size(); i += 2) {
            AttendanceLogDto dto = new AttendanceLogDto();
            
            // Bản ghi IN (chỉ số chẵn)
            IndividualRecord inRecord = records.get(i);
            dto.setUserId(inRecord.originalLog.getUserId());
            dto.setEmployeeName(inRecord.originalLog.getEmployeeName());
            dto.setDepartment(inRecord.originalLog.getDepartment());
            dto.setDate(inRecord.originalLog.getDate());
            dto.setCheckIn(inRecord.timestamp.toLocalTime());
            dto.setSource(inRecord.originalLog.getSource());
            dto.setPeriod(inRecord.originalLog.getPeriod());
            
            // Bản ghi OUT (chỉ số lẻ) nếu có
            if (i + 1 < records.size()) {
                IndividualRecord outRecord = records.get(i + 1);
                LocalTime outTime = outRecord.timestamp.toLocalTime();
                
                // Xử lý trường hợp OUT qua ngày
                if (outRecord.timestamp.toLocalDate().isAfter(inRecord.timestamp.toLocalDate())) {
                    // Giữ nguyên thời gian OUT nhưng đánh dấu là qua ngày
                    dto.setCheckOut(outTime);
                } else {
                    dto.setCheckOut(outTime);
                }
            }
            
            result.add(dto);
        }
        
        return result;
    }

    private static class IndividualRecord {
        AttendanceLogDto originalLog;
        LocalDateTime timestamp;
        boolean isCheckIn;
    }
}
