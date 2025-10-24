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

                dto.setStatus(ExcelUtil.parseString(row.getCell(6)));
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

    public static void processImport(List<AttendanceLogDto> dtos, String action, Path tempFilePath, HttpServletRequest req) throws SQLException, IOException {
        if ("Preview".equalsIgnoreCase(action)) {
            req.setAttribute("previewLogs", dtos);

        } else if ("Import".equalsIgnoreCase(action)) {
            AttendanceLogDao attendanceLogDAO = new AttendanceLogDao();
            List<AttendanceLog> logs = AttendanceMapper.convertDtoToEntity(dtos);
            attendanceLogDAO.saveAttendanceLogs(logs);
            req.setAttribute("success", "Imported " + logs.size() + " attendance logs successfully.");

            Files.deleteIfExists(tempFilePath);
            req.getSession().removeAttribute("uploadedFile");
        } else {
            req.setAttribute("error", "Invalid action.");
        }
    }
}
