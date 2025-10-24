package group4.hrms.controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import com.google.gson.Gson;

import group4.hrms.dao.HolidayCalendarDao;
import group4.hrms.dao.HolidayDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dao.RequestTypeDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.OTBalance;
import group4.hrms.service.OTRequestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * REST API Controller for OT Balance queries
 * Provides endpoints to fetch OT balance data with week/month offsets
 */
@WebServlet("/api/ot-balance")
public class OTBalanceApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    /**
     * GET /api/ot-balance?userId=X&weekOffset=Y&monthOffset=Z
     * Returns OT balance data in JSON format
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Not authenticated\"}");
                return;
            }

            Long userId = (Long) session.getAttribute("userId");

            // Initialize services
            OTRequestService otRequestService = new OTRequestService(
                new RequestDao(),
                new RequestTypeDao(),
                new HolidayDao(),
                new HolidayCalendarDao(),
                new UserDao()
            );

            // Get offset parameters (0 = current period)
            String weekOffsetStr = request.getParameter("weekOffset");
            String monthOffsetStr = request.getParameter("monthOffset");

            OTBalanceResponse responseData = new OTBalanceResponse();

            // Calculate weekly balance if weekOffset provided
            if (weekOffsetStr != null) {
                int weekOffset = Integer.parseInt(weekOffsetStr);
                responseData.setWeekly(calculateWeeklyBalance(otRequestService, userId, weekOffset));
            }

            // Calculate monthly balance if monthOffset provided
            if (monthOffsetStr != null) {
                int monthOffset = Integer.parseInt(monthOffsetStr);
                responseData.setMonthly(calculateMonthlyBalance(otRequestService, userId, monthOffset));
            }

            // Send JSON response
            String jsonResponse = gson.toJson(responseData);
            response.getWriter().write(jsonResponse);

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid offset parameter\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Calculate OT balance for a specific week (offset from current week)
     */
    private OTBalance calculateWeeklyBalance(OTRequestService service, Long userId, int weekOffset) {
        // For now, we only have getOTBalance() which returns current week
        // We'll need to enhance OTRequestService later to support custom date ranges
        // Temporary implementation: always return current week but modify display dates

        OTBalance balance = service.getOTBalance(userId);

        // Adjust week dates based on offset
        LocalDate targetDate = LocalDate.now().plusWeeks(weekOffset);
        LocalDate weekStart = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        balance.setWeekStartDate(weekStart.format(formatter));
        balance.setWeekEndDate(weekEnd.format(formatter));

        return balance;
    }

    /**
     * Calculate OT balance for a specific month (offset from current month)
     */
    private OTBalance calculateMonthlyBalance(OTRequestService service, Long userId, int monthOffset) {
        // For now, we only have getOTBalance() which returns current month
        // Temporary implementation: always return current month but modify display

        OTBalance balance = service.getOTBalance(userId);

        // Adjust month name based on offset
        LocalDate targetDate = LocalDate.now().plusMonths(monthOffset);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

        balance.setMonthName(targetDate.format(formatter));

        return balance;
    }

    /**
     * Response wrapper for OT Balance API
     */
    private static class OTBalanceResponse {
        private OTBalance weekly;
        private OTBalance monthly;

        public OTBalance getWeekly() {
            return weekly;
        }

        public void setWeekly(OTBalance weekly) {
            this.weekly = weekly;
        }

        public OTBalance getMonthly() {
            return monthly;
        }

        public void setMonthly(OTBalance monthly) {
            this.monthly = monthly;
        }
    }
}
