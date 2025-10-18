package group4.hrms.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.PositionDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.RequestDto;
import group4.hrms.dto.RequestListFilter;
import group4.hrms.model.Account;
import group4.hrms.model.Position;
import group4.hrms.model.User;
import group4.hrms.util.RequestListPermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for exporting request list to Excel or CSV
 *
 * Supported URLs:
 * - GET /requests/export - Export filtered requests to Excel or CSV
 *
 * Requirements: 11
 *
 * @author HRMS Development Team
 * @version 1.0
 */
@WebServlet("/requests/export")
public class RequestExportController extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RequestExportController.class.getName());

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int MAX_EXPORT_RECORDS = 10000; // Safety limit

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("RequestExportController.doGet() called");

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated");
            return;
        }

        Account account = (Account) session.getAttribute("account");
        User user = (User) session.getAttribute("user");

        try {
            // Initialize DAOs
            RequestDao requestDao = new RequestDao();
            UserDao userDao = new UserDao();
            DepartmentDao departmentDao = new DepartmentDao();
            PositionDao positionDao = new PositionDao();

            // Get user's position for permission checks
            Position position = null;
            if (user.getPositionId() != null) {
                position = positionDao.findById(user.getPositionId()).orElse(null);
            }

            if (position == null) {
                logger.warning("User position not found. Using default permissions.");
                position = new Position();
                position.setJobLevel(5); // Default to STAFF level
            }

            // Check export permission (Requirement 11)
            if (!RequestListPermissionHelper.canExport(position)) {
                logger.warning(String.format("User %d does not have export permission", user.getId()));
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to export requests");
                return;
            }

            // Parse filter parameters (same as RequestListController)
            RequestListFilter filter = parseFilterParameters(request);

            // Get available scopes for user
            Set<String> availableScopes = RequestListPermissionHelper.getAvailableScopes(position);

            // If no scope specified or invalid scope, use default
            if (filter.getScope() == null || filter.getScope().trim().isEmpty() ||
                !availableScopes.contains(filter.getScope())) {
                String defaultScope = RequestListPermissionHelper.getDefaultScope(position);
                filter.setScope(defaultScope);
            }

            // Get target user IDs based on scope
            List<Long> targetUserIds = getTargetUserIds(filter.getScope(), user, userDao);

            // Fetch ALL filtered requests (no pagination for export)
            // Use a high limit but cap at MAX_EXPORT_RECORDS for safety
            List<RequestDto> requests = requestDao.findWithFilters(filter, targetUserIds,
                                                                   0, MAX_EXPORT_RECORDS);

            logger.info(String.format("Exporting %d requests for user %d", requests.size(), user.getId()));

            // Get export format
            String format = request.getParameter("format");
            if (format == null || format.trim().isEmpty()) {
                format = "excel"; // Default to Excel
            }

            // Export based on format
            if ("csv".equalsIgnoreCase(format)) {
                exportToCSV(requests, response);
            } else {
                exportToExcel(requests, response);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error exporting requests: userId=%d, error=%s",
                         user.getId(), e.getMessage()), e);

            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "System error occurred while exporting requests");
            }
        }
    }

    /**
     * Export requests to Excel format using Apache POI
     * Requirement: 11
     */
    private void exportToExcel(List<RequestDto> requests, HttpServletResponse response) throws IOException {
        logger.info("Exporting to Excel format");

        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Requests");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Create data style
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "Request ID", "Type", "Title", "Employee Code", "Employee Name",
            "Department", "Status", "Created Date", "Updated Date", "Reason"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        int rowNum = 1;
        for (RequestDto dto : requests) {
            Row row = sheet.createRow(rowNum++);

            // Request ID
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(dto.getId());
            cell0.setCellStyle(dataStyle);

            // Type
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(dto.getRequestTypeName() != null ? dto.getRequestTypeName() : "");
            cell1.setCellStyle(dataStyle);

            // Title
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(dto.getTitle() != null ? dto.getTitle() : "");
            cell2.setCellStyle(dataStyle);

            // Employee Code
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(dto.getEmployeeCode() != null ? dto.getEmployeeCode() : "");
            cell3.setCellStyle(dataStyle);

            // Employee Name
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(dto.getUserFullName() != null ? dto.getUserFullName() : "");
            cell4.setCellStyle(dataStyle);

            // Department
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(dto.getDepartmentName() != null ? dto.getDepartmentName() : "");
            cell5.setCellStyle(dataStyle);

            // Status
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(dto.getStatus() != null ? dto.getStatus() : "");
            cell6.setCellStyle(dataStyle);

            // Created Date
            Cell cell7 = row.createCell(7);
            if (dto.getCreatedAt() != null) {
                cell7.setCellValue(dto.getCreatedAt().format(DATE_FORMATTER));
            }
            cell7.setCellStyle(dataStyle);

            // Updated Date
            Cell cell8 = row.createCell(8);
            if (dto.getUpdatedAt() != null) {
                cell8.setCellValue(dto.getUpdatedAt().format(DATE_FORMATTER));
            }
            cell8.setCellStyle(dataStyle);

            // Reason (from detail JSON)
            Cell cell9 = row.createCell(9);
            String reason = dto.getReasonFromDetail();
            cell9.setCellValue(reason != null ? reason : "");
            cell9.setCellStyle(dataStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"requests_export_" + LocalDate.now() + ".xlsx\"");

        // Write to output stream
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush();
        } finally {
            workbook.close();
        }

        logger.info("Excel export completed successfully");
    }

    /**
     * Export requests to CSV format
     * Requirement: 11
     */
    private void exportToCSV(List<RequestDto> requests, HttpServletResponse response) throws IOException {
        logger.info("Exporting to CSV format");

        // Set response headers
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"requests_export_" + LocalDate.now() + ".csv\"");

        // Write CSV content
        StringBuilder csv = new StringBuilder();

        // Header row
        csv.append("Request ID,Type,Title,Employee Code,Employee Name,Department,Status,Created Date,Updated Date,Reason\n");

        // Data rows
        for (RequestDto dto : requests) {
            csv.append(escapeCsv(String.valueOf(dto.getId()))).append(",");
            csv.append(escapeCsv(dto.getRequestTypeName())).append(",");
            csv.append(escapeCsv(dto.getTitle())).append(",");
            csv.append(escapeCsv(dto.getEmployeeCode())).append(",");
            csv.append(escapeCsv(dto.getUserFullName())).append(",");
            csv.append(escapeCsv(dto.getDepartmentName())).append(",");
            csv.append(escapeCsv(dto.getStatus())).append(",");
            csv.append(escapeCsv(dto.getCreatedAt() != null ? dto.getCreatedAt().format(DATE_FORMATTER) : "")).append(",");
            csv.append(escapeCsv(dto.getUpdatedAt() != null ? dto.getUpdatedAt().format(DATE_FORMATTER) : "")).append(",");
            csv.append(escapeCsv(dto.getReasonFromDetail())).append("\n");
        }

        // Write to response
        response.getWriter().write(csv.toString());
        response.getWriter().flush();

        logger.info("CSV export completed successfully");
    }

    /**
     * Escape CSV field value
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        // If value contains comma, quote, or newline, wrap in quotes and escape quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    /**
     * Parse filter parameters from HTTP request
     * (Same logic as RequestListController)
     */
    private RequestListFilter parseFilterParameters(HttpServletRequest request) {
        RequestListFilter filter = new RequestListFilter();

        // Scope filter
        String scope = request.getParameter("scope");
        if (scope != null && !scope.trim().isEmpty()) {
            filter.setScope(scope.trim());
        }

        // Request type filter
        String typeStr = request.getParameter("type");
        if (typeStr != null && !typeStr.trim().isEmpty() && !"all".equalsIgnoreCase(typeStr)) {
            try {
                filter.setRequestTypeId(Long.parseLong(typeStr));
            } catch (NumberFormatException e) {
                logger.warning("Invalid request type ID: " + typeStr);
            }
        }

        // Status filter
        String status = request.getParameter("status");
        if (status != null && !status.trim().isEmpty()) {
            filter.setStatus(status.trim());
        }

        // Show cancelled toggle
        String showCancelledStr = request.getParameter("showCancelled");
        filter.setShowCancelled("true".equalsIgnoreCase(showCancelledStr) ||
                               "on".equalsIgnoreCase(showCancelledStr));

        // Date range filter
        String fromDateStr = request.getParameter("fromDate");
        if (fromDateStr != null && !fromDateStr.trim().isEmpty()) {
            try {
                filter.setFromDate(LocalDate.parse(fromDateStr.trim()));
            } catch (DateTimeParseException e) {
                logger.warning("Invalid from date format: " + fromDateStr);
            }
        }

        String toDateStr = request.getParameter("toDate");
        if (toDateStr != null && !toDateStr.trim().isEmpty()) {
            try {
                filter.setToDate(LocalDate.parse(toDateStr.trim()));
            } catch (DateTimeParseException e) {
                logger.warning("Invalid to date format: " + toDateStr);
            }
        }

        // Employee filter
        String employeeIdStr = request.getParameter("employeeId");
        if (employeeIdStr != null && !employeeIdStr.trim().isEmpty()) {
            try {
                filter.setEmployeeId(Long.parseLong(employeeIdStr));
            } catch (NumberFormatException e) {
                logger.warning("Invalid employee ID: " + employeeIdStr);
            }
        }

        // Search filter
        String search = request.getParameter("search");
        if (search != null && !search.trim().isEmpty()) {
            filter.setSearchKeyword(search.trim());
        }

        return filter;
    }

    /**
     * Get target user IDs based on scope
     * (Same logic as RequestListService)
     */
    private List<Long> getTargetUserIds(String scope, User currentUser, UserDao userDao) {
        List<Long> userIds = new java.util.ArrayList<>();

        switch (scope) {
            case "my":
                userIds.add(currentUser.getId());
                break;

            case "subordinate":
                try {
                    List<Long> subordinateIds = userDao.findSubordinateUserIds(currentUser.getId());
                    userIds.addAll(subordinateIds);
                } catch (Exception e) {
                    logger.log(Level.WARNING, String.format("Error finding subordinates for user %d",
                              currentUser.getId()), e);
                }
                break;

            case "all":
                // Empty list means no user filter
                break;

            default:
                logger.warning(String.format("Unknown scope '%s', defaulting to 'my'", scope));
                userIds.add(currentUser.getId());
        }

        return userIds;
    }
}
