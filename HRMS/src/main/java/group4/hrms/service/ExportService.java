package group4.hrms.service;

import group4.hrms.controller.AttendanceRecordEmpServlet;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.util.AttendanceExportUtil;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportService {

    public static void AttendanceRecordExport(HttpServletResponse resp, String exportType, List<AttendanceLogDto> attendanceList) {
        if (attendanceList == null) {
            attendanceList = Collections.emptyList();
        }

        switch (exportType) {
            case "csv" -> {
                try {
                    resp.setContentType("text/csv; charset=UTF-8");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"attendance.csv\"");

                    OutputStream out = resp.getOutputStream();
                    out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

                    AttendanceExportUtil.exportAttendanceCSV(out, attendanceList);
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            case "xls" -> {
                try {
                    resp.setContentType("application/vnd.ms-excel");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"attendance.xls\"");
                    AttendanceExportUtil.exportAttendanceXLS(resp.getOutputStream(), attendanceList);
                } catch (IOException ex) {
                    Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            case "pdf" -> {
                try {
                    resp.setContentType("application/pdf");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"attendance.pdf\"");
                    AttendanceExportUtil.exportAttendancePDF(resp.getOutputStream(), attendanceList);
                } catch (IOException ex) {
                    Logger.getLogger(AttendanceRecordEmpServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
