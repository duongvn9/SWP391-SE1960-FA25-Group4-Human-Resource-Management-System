package group4.hrms.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import group4.hrms.dao.PayslipDao;
import group4.hrms.dto.ExportResult;
import group4.hrms.dto.PayslipDto;
import group4.hrms.dto.PayslipFilter;

/**
 * Service for exporting payslip data to various formats
 * Requirements: 9.2, 9.3
 */
public class PayslipExportService {

    private static final Logger logger = Logger.getLogger(PayslipExportService.class.getName());

    private final PayslipDao payslipDao;

    public PayslipExportService() {
        this.payslipDao = new PayslipDao();
    }

    /**
     * Export payslips to Excel format
     * Requirements: 9.2, 9.3
     *
     * @param filter Filter criteria for payslips to export
     * @return Excel file as byte array
     */
    public ExportResult exportToExcel(PayslipFilter filter) throws SQLException, IOException {
        logger.info("Starting Excel export with filter: " + filter);

        // Get payslips data
        List<PayslipDto> payslips = payslipDao.findWithFilters(filter, null); // No pagination for export

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Payslips");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Employee ID", "Employee Name", "Department", "Position",
                "Period Start", "Period End", "Base Salary", "OT Amount",
                "Lateness Deduction", "Under Hours Deduction", "Tax Amount",
                "Gross Amount", "Net Amount", "Currency", "Status",
                "Generated At", "Is Dirty", "Dirty Reason"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (PayslipDto payslip : payslips) {
                Row row = sheet.createRow(rowNum++);

                // Employee ID
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(payslip.getUserEmployeeId() != null ? payslip.getUserEmployeeId() : "");
                cell0.setCellStyle(centerStyle);

                // Employee Name
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(payslip.getUserFullName() != null ? payslip.getUserFullName() : "");

                // Department
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(payslip.getDepartmentName() != null ? payslip.getDepartmentName() : "");

                // Position
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(payslip.getPositionName() != null ? payslip.getPositionName() : "");

                // Period Start
                Cell cell4 = row.createCell(4);
                if (payslip.getPeriodStart() != null) {
                    cell4.setCellValue(java.sql.Date.valueOf(payslip.getPeriodStart()));
                    cell4.setCellStyle(dateStyle);
                }

                // Period End
                Cell cell5 = row.createCell(5);
                if (payslip.getPeriodEnd() != null) {
                    cell5.setCellValue(java.sql.Date.valueOf(payslip.getPeriodEnd()));
                    cell5.setCellStyle(dateStyle);
                }

                // Base Salary
                Cell cell6 = row.createCell(6);
                if (payslip.getBaseSalary() != null) {
                    cell6.setCellValue(payslip.getBaseSalary().doubleValue());
                    cell6.setCellStyle(currencyStyle);
                }

                // OT Amount
                Cell cell7 = row.createCell(7);
                if (payslip.getOtAmount() != null) {
                    cell7.setCellValue(payslip.getOtAmount().doubleValue());
                    cell7.setCellStyle(currencyStyle);
                }

                // Lateness Deduction
                Cell cell8 = row.createCell(8);
                if (payslip.getLatenessDeduction() != null) {
                    cell8.setCellValue(payslip.getLatenessDeduction().doubleValue());
                    cell8.setCellStyle(currencyStyle);
                }

                // Under Hours Deduction
                Cell cell9 = row.createCell(9);
                if (payslip.getUnderHoursDeduction() != null) {
                    cell9.setCellValue(payslip.getUnderHoursDeduction().doubleValue());
                    cell9.setCellStyle(currencyStyle);
                }

                // Tax Amount
                Cell cell10 = row.createCell(10);
                if (payslip.getTaxAmount() != null) {
                    cell10.setCellValue(payslip.getTaxAmount().doubleValue());
                    cell10.setCellStyle(currencyStyle);
                }

                // Gross Amount
                Cell cell11 = row.createCell(11);
                if (payslip.getGrossAmount() != null) {
                    cell11.setCellValue(payslip.getGrossAmount().doubleValue());
                    cell11.setCellStyle(currencyStyle);
                }

                // Net Amount
                Cell cell12 = row.createCell(12);
                if (payslip.getNetAmount() != null) {
                    cell12.setCellValue(payslip.getNetAmount().doubleValue());
                    cell12.setCellStyle(currencyStyle);
                }

                // Currency
                Cell cell13 = row.createCell(13);
                cell13.setCellValue(payslip.getCurrency() != null ? payslip.getCurrency() : "");
                cell13.setCellStyle(centerStyle);

                // Status
                Cell cell14 = row.createCell(14);
                cell14.setCellValue(payslip.getStatus() != null ? payslip.getStatus() : "");
                cell14.setCellStyle(centerStyle);

                // Generated At
                Cell cell15 = row.createCell(15);
                if (payslip.getGeneratedAt() != null) {
                    cell15.setCellValue(java.sql.Timestamp.valueOf(payslip.getGeneratedAt()));
                    cell15.setCellStyle(dateStyle);
                }

                // Is Dirty
                Cell cell16 = row.createCell(16);
                cell16.setCellValue(Boolean.TRUE.equals(payslip.getIsDirty()) ? "Yes" : "No");
                cell16.setCellStyle(centerStyle);

                // Dirty Reason
                Cell cell17 = row.createCell(17);
                cell17.setCellValue(payslip.getDirtyReason() != null ? payslip.getDirtyReason() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Set minimum width
                if (sheet.getColumnWidth(i) < 2000) {
                    sheet.setColumnWidth(i, 2000);
                }
                // Set maximum width
                if (sheet.getColumnWidth(i) > 8000) {
                    sheet.setColumnWidth(i, 8000);
                }
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] result = outputStream.toByteArray();

            logger.info(String.format("Excel export completed: %d payslips exported", payslips.size()));

            // Generate filename based on filter criteria
            String filename = generateExportFilename(filter, "excel");

            ExportResult exportResult = new ExportResult(true, "Excel export completed successfully");
            exportResult.setData(result);
            exportResult.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            exportResult.setFileName(filename);
            exportResult.setRecordCount(payslips.size());
            return exportResult;
        }
    }

    /**
     * Export payslips to CSV format
     * Requirements: 9.2, 9.3
     *
     * @param filter Filter criteria for payslips to export
     * @return CSV content as string
     */
    public ExportResult exportToCSV(PayslipFilter filter) throws SQLException {
        logger.info("Starting CSV export with filter: " + filter);

        // Get payslips data
        List<PayslipDto> payslips = payslipDao.findWithFilters(filter, null); // No pagination for export

        StringBuilder csv = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Add header
        csv.append("Employee ID,Employee Name,Department,Position,")
           .append("Period Start,Period End,Base Salary,OT Amount,")
           .append("Lateness Deduction,Under Hours Deduction,Tax Amount,")
           .append("Gross Amount,Net Amount,Currency,Status,")
           .append("Generated At,Is Dirty,Dirty Reason\n");

        // Add data rows
        for (PayslipDto payslip : payslips) {
            csv.append(escapeCsvValue(payslip.getUserEmployeeId())).append(",")
               .append(escapeCsvValue(payslip.getUserFullName())).append(",")
               .append(escapeCsvValue(payslip.getDepartmentName())).append(",")
               .append(escapeCsvValue(payslip.getPositionName())).append(",")
               .append(payslip.getPeriodStart() != null ? payslip.getPeriodStart().format(dateFormatter) : "").append(",")
               .append(payslip.getPeriodEnd() != null ? payslip.getPeriodEnd().format(dateFormatter) : "").append(",")
               .append(payslip.getBaseSalary() != null ? payslip.getBaseSalary().toString() : "0").append(",")
               .append(payslip.getOtAmount() != null ? payslip.getOtAmount().toString() : "0").append(",")
               .append(payslip.getLatenessDeduction() != null ? payslip.getLatenessDeduction().toString() : "0").append(",")
               .append(payslip.getUnderHoursDeduction() != null ? payslip.getUnderHoursDeduction().toString() : "0").append(",")
               .append(payslip.getTaxAmount() != null ? payslip.getTaxAmount().toString() : "0").append(",")
               .append(payslip.getGrossAmount() != null ? payslip.getGrossAmount().toString() : "0").append(",")
               .append(payslip.getNetAmount() != null ? payslip.getNetAmount().toString() : "0").append(",")
               .append(escapeCsvValue(payslip.getCurrency())).append(",")
               .append(escapeCsvValue(payslip.getStatus())).append(",")
               .append(payslip.getGeneratedAt() != null ? payslip.getGeneratedAt().format(dateTimeFormatter) : "").append(",")
               .append(Boolean.TRUE.equals(payslip.getIsDirty()) ? "Yes" : "No").append(",")
               .append(escapeCsvValue(payslip.getDirtyReason())).append("\n");
        }

        logger.info(String.format("CSV export completed: %d payslips exported", payslips.size()));

        // Generate filename based on filter criteria
        String filename = generateExportFilename(filter, "csv");

        ExportResult exportResult = new ExportResult(true, "CSV export completed successfully");
        exportResult.setData(csv.toString().getBytes());
        exportResult.setContentType("text/csv");
        exportResult.setFileName(filename);
        exportResult.setRecordCount(payslips.size());
        return exportResult;
    }

    /**
     * Generate export filename based on filter criteria
     * Requirements: 9.2, 9.3
     *
     * @param filter Filter criteria
     * @param format Export format (excel, csv, pdf)
     * @return Generated filename
     */
    public String generateExportFilename(PayslipFilter filter, String format) {
        StringBuilder filename = new StringBuilder("payslips");

        // Add period to filename if specified, otherwise use "all"
        if (filter != null && filter.hasPeriodFilter()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            filename.append("_").append(filter.getPeriodStart().format(formatter));

            // If period has both start and end, and they're different months, show range
            if (filter.getPeriodEnd() != null &&
                !filter.getPeriodStart().getMonth().equals(filter.getPeriodEnd().getMonth())) {
                filename.append("_to_").append(filter.getPeriodEnd().format(formatter));
            }
        } else {
            // No period filter - export all periods
            filename.append("_all");
        }

        // Add department to filename if specified
        if (filter != null && filter.hasDepartmentFilter()) {
            filename.append("_dept").append(filter.getDepartmentId());
        }

        // Add user to filename if specified
        if (filter != null && filter.hasUserFilter()) {
            filename.append("_user").append(filter.getUserId());
        }

        // Add special filters
        if (filter != null) {
            if (filter.hasDirtyFilter()) {
                filename.append("_dirty");
            }
            if (filter.hasNotGeneratedFilter()) {
                filename.append("_not-generated");
            }
        }

        // Add timestamp
        filename.append("_").append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        // Add extension
        switch (format.toLowerCase()) {
            case "excel":
                filename.append(".xlsx");
                break;
            case "csv":
                filename.append(".csv");
                break;
            case "pdf":
                filename.append(".pdf");
                break;
            default:
                filename.append(".").append(format);
        }

        return filename.toString();
    }

    /**
     * Get export statistics
     * Requirements: 9.4
     *
     * @param filter Filter criteria
     * @return Export statistics
     */
    public ExportStatistics getExportStatistics(PayslipFilter filter) throws SQLException {
        long totalCount = payslipDao.countWithFilters(filter);

        ExportStatistics stats = new ExportStatistics();
        stats.setTotalRecords(totalCount);
        stats.setFilterCriteria(filter != null ? filter.toString() : "No filter");
        stats.setEstimatedFileSize(estimateFileSize(totalCount, "excel"));

        return stats;
    }

    // Private helper methods

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd/mm/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createCenterStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }

        // Escape quotes and wrap in quotes if contains comma, quote, or newline
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    private long estimateFileSize(long recordCount, String format) {
        // Rough estimates based on average record size
        switch (format.toLowerCase()) {
            case "excel":
                return recordCount * 200; // ~200 bytes per record in Excel
            case "csv":
                return recordCount * 150; // ~150 bytes per record in CSV
            case "pdf":
                return recordCount * 300; // ~300 bytes per record in PDF
            default:
                return recordCount * 200;
        }
    }

    /**
     * Inner class for export statistics
     */
    public static class ExportStatistics {
        private long totalRecords;
        private String filterCriteria;
        private long estimatedFileSize;

        // Getters and setters
        public long getTotalRecords() { return totalRecords; }
        public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }

        public String getFilterCriteria() { return filterCriteria; }
        public void setFilterCriteria(String filterCriteria) { this.filterCriteria = filterCriteria; }

        public long getEstimatedFileSize() { return estimatedFileSize; }
        public void setEstimatedFileSize(long estimatedFileSize) { this.estimatedFileSize = estimatedFileSize; }

        public String getFormattedFileSize() {
            if (estimatedFileSize < 1024) {
                return estimatedFileSize + " B";
            } else if (estimatedFileSize < 1024 * 1024) {
                return String.format("%.1f KB", estimatedFileSize / 1024.0);
            } else {
                return String.format("%.1f MB", estimatedFileSize / (1024.0 * 1024.0));
            }
        }
    }
}