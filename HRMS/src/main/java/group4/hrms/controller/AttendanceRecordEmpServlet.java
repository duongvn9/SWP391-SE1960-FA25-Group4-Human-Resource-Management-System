package group4.hrms.controller;

import group4.hrms.dao.AttendanceLogDao;
import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dto.AttendanceLogDto;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

@WebServlet("/attendance/record/emp")
public class AttendanceRecordEmpServlet extends HttpServlet {

    private final AttendanceLogDao dao = new AttendanceLogDao();
    private final TimesheetPeriodDao tDAO = new TimesheetPeriodDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
//            // 🔹 Lấy session hiện tại, không tạo mới nếu chưa tồn tại
//            HttpSession session = req.getSession(false);
//
//            Long userId = null;
//            if (session != null) {
//                Object userIdObj = session.getAttribute("userId");
//                if (userIdObj instanceof Long) {
//                    userId = (Long) userIdObj;
//                } else if (userIdObj != null) {
//                    try {
//                        userId = Long.valueOf(userIdObj.toString());
//                    } catch (NumberFormatException e) {
//                        userId = null; 
//                    }
//                }
//            }
//
//            // Nếu userId vẫn null => chưa đăng nhập, có thể redirect về login
//            if (userId == null) {
//                resp.sendRedirect(req.getContextPath() + "/login");
//                return; // dừng xử lý tiếp
//            }

            // 🔹 Lấy thông tin người dùng hiện tại (tạm thời hardcode, sau này lấy từ session)
            Long userId = 45L;
            String exportType = req.getParameter("exportType");
            String employeeKeyword = req.getParameter("employeeKeyword");
            String department = req.getParameter("department");
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String status = req.getParameter("status");
            String source = req.getParameter("source");
            String periodIdStr = req.getParameter("periodId");

            LocalDate startDate = null;
            LocalDate endDate = null;
            Long periodId = null;
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr);
            }
            if (periodIdStr != null && !periodIdStr.isEmpty()) {
                try {
                    periodId = Long.valueOf(periodIdStr);
                } catch (NumberFormatException e) {
                    periodId = null;
                }
            }

            if (exportType != null) {
                handleExport(req, resp, exportType);
                return;
            }

            List<AttendanceLogDto> attendanceList = dao.findByFilter(
                    userId,
                    employeeKeyword,
                    department,
                    startDate,
                    endDate,
                    status,
                    source,
                    periodId
            );

            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.setAttribute("employeeKeyword", employeeKeyword);
            req.setAttribute("department", department);
            req.setAttribute("startDate", startDateStr);
            req.setAttribute("endDate", endDateStr);
            req.setAttribute("status", status);
            req.setAttribute("source", source);
            req.setAttribute("periodId", periodIdStr);

            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
//            Long userId = (Long) req.getSession().getAttribute("userId");    
            Long userId = 45l;
            List<AttendanceLogDto> attendanceList = dao.findByUserId(userId);
            System.out.println(tDAO.findAll());
            req.setAttribute("attendanceList", attendanceList);
            req.setAttribute("periodList", tDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<String> attendanceList = new ArrayList<>();
        req.setAttribute("attendanceList", attendanceList);
        req.getRequestDispatcher("/WEB-INF/views/attendance/attendance-record-emp.jsp").forward(req, resp);

    }

    private void handleExport(HttpServletRequest req, HttpServletResponse resp, String exportType) throws SQLException {
        Long userId = 45L; // tạm thời hardcode
//        List<AttendanceLogDto> attendanceList = dao.findByFilter(
//                userId,
//                req.getParameter("employeeKeyword"),
//                req.getParameter("department"),
//                null, null,
//                req.getParameter("status"),
//                req.getParameter("source"),
//                null
//        );
        List<AttendanceLogDto> attendanceList = dao.findByUserId(userId);

        switch (exportType) {
            case "csv" -> {
                try {
                    resp.setContentType("text/csv");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"attendance.csv\"");
                    writeCSV(resp.getWriter(), attendanceList);
                } catch (IOException ex) {
                    Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            case "xls" -> {
                try {
                    resp.setContentType("application/vnd.ms-excel");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"attendance.xls\"");
                    writeXLS(resp.getOutputStream(), attendanceList);
                } catch (IOException ex) {
                    Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            case "pdf" -> {
                try {
                    resp.setContentType("application/pdf");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"attendance.pdf\"");
                    writePDF(resp.getOutputStream(), attendanceList);
                } catch (IOException ex) {
                    Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    private void writeCSV(PrintWriter writer, List<AttendanceLogDto> list) {
        writer.println("Employee ID,Employee Name,Department,Date,Check-in,Check-out,Status,Source,Period");
        for (AttendanceLogDto dto : list) {
            writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    dto.getEmployeeId(),
                    safe(dto.getEmployeeName()),
                    safe(dto.getDepartment()),
                    safe(dto.getDate()),
                    safe(dto.getCheckIn()),
                    safe(dto.getCheckOut()),
                    safe(dto.getStatus()),
                    safe(dto.getSource()),
                    safe(dto.getPeriod())
            );
        }
    }

    // tránh null gây lỗi
    private String safe(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private void writeXLS(OutputStream out, List<AttendanceLogDto> list) throws IOException {
        try (Workbook workbook = new HSSFWorkbook()) { // hoặc XSSFWorkbook() cho .xlsx
            Sheet sheet = workbook.createSheet("Attendance Records");

            Row header = sheet.createRow(0);
            String[] columns = {"Employee ID", "Employee Name", "Department", "Date", "Check-in", "Check-out", "Status", "Source", "Period"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (AttendanceLogDto dto : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getEmployeeId() != null ? dto.getEmployeeId() : 0);
                row.createCell(1).setCellValue(safe(dto.getEmployeeName()));
                row.createCell(2).setCellValue(safe(dto.getDepartment()));
                row.createCell(3).setCellValue(safe(dto.getDate()));
                row.createCell(4).setCellValue(safe(dto.getCheckIn()));
                row.createCell(5).setCellValue(safe(dto.getCheckOut()));
                row.createCell(6).setCellValue(safe(dto.getStatus()));
                row.createCell(7).setCellValue(safe(dto.getSource()));
                row.createCell(8).setCellValue(safe(dto.getPeriod()));
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        }
    }

    private void writePDF(OutputStream out, List<AttendanceLogDto> list) throws IOException {
        Document document = new Document(PageSize.A4.rotate()); // ngang để vừa bảng
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);

            document.add(new Paragraph("Attendance Report", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)));
            document.add(new Paragraph("Generated on: " + LocalDate.now()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{2f, 3f, 3f, 3f, 2f, 2f, 2f, 2f, 2f});

            String[] headers = {"Employee ID", "Employee Name", "Department", "Date", "Check-in", "Check-out", "Status", "Source", "Period"};

            for (String h : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(h, headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(headerCell);
            }

            for (AttendanceLogDto dto : list) {
                table.addCell(new Phrase(safe(dto.getEmployeeId()), cellFont));
                table.addCell(new Phrase(safe(dto.getEmployeeName()), cellFont));
                table.addCell(new Phrase(safe(dto.getDepartment()), cellFont));
                table.addCell(new Phrase(safe(dto.getDate()), cellFont));
                table.addCell(new Phrase(safe(dto.getCheckIn()), cellFont));
                table.addCell(new Phrase(safe(dto.getCheckOut()), cellFont));
                table.addCell(new Phrase(safe(dto.getStatus()), cellFont));
                table.addCell(new Phrase(safe(dto.getSource()), cellFont));
                table.addCell(new Phrase(safe(dto.getPeriod()), cellFont));
            }

            document.add(table);
        } catch (DocumentException e) {
        } finally {
            document.close();
        }
    }
}
