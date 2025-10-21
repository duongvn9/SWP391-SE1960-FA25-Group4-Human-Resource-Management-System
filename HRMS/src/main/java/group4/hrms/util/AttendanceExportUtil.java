package group4.hrms.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import group4.hrms.dto.AttendanceLogDto;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class AttendanceExportUtil {

    public static void exportAttendanceCSV(OutputStream out, List<AttendanceLogDto> list) throws IOException {
        out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true)) {
            writer.println("Employee ID,Employee Name,Department,Date,Check-in,Check-out,Status,Source,Period");

            for (AttendanceLogDto dto : list) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        dto.getUserId(),
                        escapeCsv(safe(dto.getEmployeeName())),
                        escapeCsv(safe(dto.getDepartment())),
                        safe(dto.getDate()),
                        safe(dto.getCheckIn()),
                        safe(dto.getCheckOut()),
                        escapeCsv(safe(dto.getStatus())),
                        escapeCsv(safe(dto.getSource())),
                        escapeCsv(safe(dto.getPeriod()))
                );
            }
        }
    }

    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\""); // double quotes
            return "\"" + value + "\"";
        }
        return value;
    }

    private static String safe(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static void exportAttendanceXLS(OutputStream out, List<AttendanceLogDto> list) throws IOException {
        try (Workbook workbook = new HSSFWorkbook()) {
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
                row.createCell(0).setCellValue(dto.getUserId() != null ? dto.getUserId() : 0);
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

    public static void exportAttendancePDF(OutputStream out, List<AttendanceLogDto> list) throws IOException {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);

            document.add(new Paragraph("Attendance Report", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)));
            document.add(new Paragraph("Generated on: " + LocalDate.now(), cellFont));
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
                table.addCell(new Phrase(safe(dto.getUserId()), cellFont));
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
            throw new IOException(e);
        } finally {
            document.close();
        }
    }
}
