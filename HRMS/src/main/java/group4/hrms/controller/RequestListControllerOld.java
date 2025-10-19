package group4.hrms.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import group4.hrms.dao.RequestDao;
import group4.hrms.dto.RequestDto;
import group4.hrms.model.Account;
import group4.hrms.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * OLD Controller for displaying request lists (BACKUP).
 * This is the old implementation - kept for reference.
 * The new implementation will be in RequestListController.java
 *
 * @author HRMS Development Team
 * @version 1.0
 * @deprecated Use RequestListController instead
 */
@WebServlet("/requests/list/old")
public class RequestListControllerOld extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RequestListControllerOld.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("RequestListControllerOld.doGet() called");

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Account account = (Account) session.getAttribute("account");
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            RequestDao requestDao = new RequestDao();

            // Get filter parameters
            String statusFilter = request.getParameter("status");
            String typeFilter = request.getParameter("type");
            String durationFilter = request.getParameter("duration"); // full-day, half-day
            String periodFilter = request.getParameter("period"); // AM, PM

            // Get all requests for the user with details
            List<RequestDto> allRequests = requestDao.findByUserIdWithDetails(user.getId(), 0, 100);

            // Apply filters
            List<RequestDto> filteredRequests = new java.util.ArrayList<>();
            for (RequestDto req : allRequests) {
                // Apply status filter
                if (statusFilter != null && !statusFilter.isEmpty()) {
                    if (!req.getStatus().equals(statusFilter)) {
                        continue;
                    }
                }

                // Apply type filter
                if (typeFilter != null && !typeFilter.isEmpty()) {
                    if (!req.getRequestTypeCode().equals(typeFilter)) {
                        continue;
                    }
                }

                // Apply duration and period filters (only for leave requests)
                if (req.getRequestTypeCode() != null && req.getRequestTypeCode().startsWith("LEAVE_")) {
                    // Skip if no detail JSON
                    if (req.getDetailJson() == null || req.getDetailJson().trim().isEmpty()) {
                        continue;
                    }

                    // Parse leave detail from the request
                    group4.hrms.dto.LeaveRequestDetail leaveDetail = group4.hrms.dto.LeaveRequestDetail.fromJson(req.getDetailJson());

                    // Apply duration filter
                    if (durationFilter != null && !durationFilter.isEmpty()) {
                        boolean isHalfDay = leaveDetail.getIsHalfDay() != null && leaveDetail.getIsHalfDay();

                        if ("half-day".equals(durationFilter) && !isHalfDay) {
                            continue;
                        }
                        if ("full-day".equals(durationFilter) && isHalfDay) {
                            continue;
                        }
                    }

                    // Apply period filter (only for half-day requests)
                    if (periodFilter != null && !periodFilter.isEmpty()) {
                        boolean isHalfDay = leaveDetail.getIsHalfDay() != null && leaveDetail.getIsHalfDay();

                        if (isHalfDay) {
                            String halfDayPeriod = leaveDetail.getHalfDayPeriod();
                            if (halfDayPeriod == null || !halfDayPeriod.equals(periodFilter)) {
                                continue;
                            }
                        } else {
                            // Skip full-day requests when period filter is applied
                            continue;
                        }
                    }
                }

                // If all filters pass, add to filtered list
                filteredRequests.add(req);
            }

            // Set attributes for JSP
            request.setAttribute("requests", filteredRequests);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("typeFilter", typeFilter);
            request.setAttribute("durationFilter", durationFilter);
            request.setAttribute("periodFilter", periodFilter);

            // Forward to request list view
            request.getRequestDispatcher("/WEB-INF/views/requests/request-list-old.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            logger.severe("Error loading request list: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading requests. Please try again later.");
            request.getRequestDispatcher("/dashboard").forward(request, response);
        }
    }
}
