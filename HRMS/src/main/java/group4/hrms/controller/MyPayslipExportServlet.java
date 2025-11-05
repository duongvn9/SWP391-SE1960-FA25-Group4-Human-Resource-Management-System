package group4.hrms.controller;

import group4.hrms.dto.ExportResult;
import group4.hrms.dto.PayslipFilter;
import group4.hrms.model.User;
import group4.hrms.service.PayslipExportService;
import group4.hrms.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Servlet for exporting employee's own payslips to Excel
 * Allows employees to download their payslip history
 */
@WebServlet("/my-payslips/export")
public class MyPayslipExportServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(MyPayslipExportServlet.class);
    private PayslipExportService exportService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.exportService = new PayslipExportService();
        logger.info("MyPayslipExportServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("MyPayslipExportServlet.doGet() called");

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || !SessionUtil.isUserLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User session invalid");
            return;
        }

        try {
            // Build filter - restrict to current user only
            PayslipFilter filter = new PayslipFilter();
            filter.setUserId(currentUser.getId());

            // Parse optional period filter
            String periodStartStr = request.getParameter("periodStart");
            String periodEndStr = request.getParameter("periodEnd");

            if (periodStartStr != null && periodEndStr != null) {
                try {
                    LocalDate periodStart = LocalDate.parse(periodStartStr);
                    LocalDate periodEnd = LocalDate.parse(periodEndStr);
                    filter.setPeriodStart(periodStart);
                    filter.setPeriodEnd(periodEnd);
                } catch (Exception e) {
                    logger.warn("Invalid date format: start={}, end={}", periodStartStr, periodEndStr);
                }
            }

            logger.info("Employee export request: userId={}, filter={}", currentUser.getId(), filter);

            // Export to Excel
            ExportResult exportResult = exportService.exportToExcel(filter);

            // Set response headers for file download
            response.setContentType(exportResult.getContentType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + exportResult.getFilename() + "\"");
            response.setContentLength(exportResult.getData().length);

            // Write file data to response
            response.getOutputStream().write(exportResult.getData());
            response.getOutputStream().flush();

            logger.info("Employee export completed: userId={}, {} records exported",
                       currentUser.getId(), exportResult.getRecordCount());

        } catch (Exception e) {
            logger.error("Error in employee payslip export", e);

            // Reset response if not committed
            if (!response.isCommitted()) {
                response.reset();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                 "Export failed: " + e.getMessage());
            }
        }
    }
}
