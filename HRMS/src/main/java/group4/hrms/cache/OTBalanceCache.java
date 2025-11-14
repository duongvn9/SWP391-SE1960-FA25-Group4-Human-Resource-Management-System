package group4.hrms.cache;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import group4.hrms.dao.RequestDao;
import group4.hrms.dto.OTBalance;
import group4.hrms.dto.OTRequestDetail;
import group4.hrms.model.Request;
import jakarta.servlet.http.HttpSession;

/**
 * Application-scope cache for OT Balance.
 * Caches calculated OT balance to avoid repeated database queries.
 *
 * IMPORTANT: Cache is now shared across all sessions (application-level)
 * so when one user's request is approved, all users see the updated balance immediately.
 *
 * OPTIMIZATION: Instead of querying database 7 times for each balance calculation,
 * we query ONCE and calculate all balances in memory.
 */
public class OTBalanceCache {
    private static final Logger logger = Logger.getLogger(OTBalanceCache.class.getName());
    private static final int CACHE_TTL_MINUTES = 5;

    // Application-level cache shared across all sessions
    private static final Map<String, CachedData<OTBalance>> APPLICATION_CACHE = new ConcurrentHashMap<>();

    // Constants for OT limits
    private static final int WEEKLY_LIMIT = 48;
    private static final int MONTHLY_LIMIT = 40;
    private static final int ANNUAL_LIMIT = 300;
    private static final int REGULAR_WEEKLY_HOURS = 40;

    /**
     * Get OT balance from cache or calculate if not cached/expired.
     *
     * IMPORTANT: Cache is now application-level (shared across all sessions)
     * instead of session-level, so all users see the same cached data.
     *
     * @param session HTTP session (kept for API compatibility, but not used for caching)
     * @param userId user ID
     * @param weekOffset weeks from current week (0 = current, -1 = last week, +1 = next week)
     * @param monthOffset from current month
     * @param yearOffset years from current year
     * @param requestDao DAO to load requests if not cached
     * @return OT balance
     */
    public OTBalance getOrCalculate(HttpSession session, Long userId,
                                    int weekOffset, int monthOffset, int yearOffset,
                                    RequestDao requestDao) {
        String cacheKey = String.format("otBalance_%d_%d_%d_%d",
                                       userId, weekOffset, monthOffset, yearOffset);

        // Check application-level cache
        CachedData<OTBalance> cached = APPLICATION_CACHE.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            logger.fine(String.format("OT Balance cache HIT: userId=%d, offsets=(%d,%d,%d), remaining=%ds",
                       userId, weekOffset, monthOffset, yearOffset, cached.getRemainingSeconds()));
            return cached.getData();
        }

        logger.info(String.format("OT Balance cache MISS: userId=%d, offsets=(%d,%d,%d), calculating...",
                   userId, weekOffset, monthOffset, yearOffset));

        // Calculate and cache at application level
        OTBalance balance = calculateAllBalances(userId, weekOffset, monthOffset, yearOffset, requestDao);
        APPLICATION_CACHE.put(cacheKey, new CachedData<>(balance, CACHE_TTL_MINUTES));

        logger.info(String.format("Cached OT Balance: userId=%d, weekly=%.2f, monthly=%.2f, annual=%.2f",
                   userId, balance.getCurrentWeekHours(), balance.getMonthlyHours(), balance.getAnnualHours()));

        return balance;
    }

    /**
     * OPTIMIZATION: Calculate ALL balances in ONE query.
     * Instead of querying database 7 times, we query ONCE and filter in memory.
     *
     * @param userId user ID
     * @param weekOffset weeks offset
     * @param monthOffset months offset
     * @param yearOffset years offset
     * @param requestDao DAO to load requests
     * @return calculated OT balance
     */
    private OTBalance calculateAllBalances(Long userId, int weekOffset,
                                          int monthOffset, int yearOffset,
                                          RequestDao requestDao) {
        long startTime = System.currentTimeMillis();

        // 1 QUERY DUY NHẤT - lấy TẤT CẢ OT requests của user
        List<Request> allRequests = requestDao.findByUserId(userId);

        // Filter only OT requests (type_id = 7)
        List<Request> allOTRequests = allRequests.stream()
            .filter(r -> r.getRequestTypeId() != null && r.getRequestTypeId() == 7L)
            .filter(r -> "APPROVED".equals(r.getStatus()) || "PENDING".equals(r.getStatus()))
            .collect(Collectors.toList());

        logger.fine(String.format("Loaded %d OT requests for userId=%d in %dms",
                   allOTRequests.size(), userId, System.currentTimeMillis() - startTime));

        // Calculate target dates
        LocalDate now = LocalDate.now();
        LocalDate targetWeekDate = now.plusWeeks(weekOffset);
        LocalDate targetMonthDate = now.plusMonths(monthOffset);
        int targetYear = now.getYear() + yearOffset;

        // Calculate week range
        LocalDate weekStart = targetWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = targetWeekDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Calculate month range
        LocalDate monthStart = targetMonthDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = targetMonthDate.with(TemporalAdjusters.lastDayOfMonth());

        // Calculate year range
        LocalDate yearStart = LocalDate.of(targetYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(targetYear, 12, 31);

        // Filter và tính toán trong memory (FAST!)
        double weekOT = calculateOTHoursInRange(allOTRequests, weekStart, weekEnd);
        int weekApprovedCount = countApprovedInRange(allOTRequests, weekStart, weekEnd);

        double monthOT = calculateOTHoursInRange(allOTRequests, monthStart, monthEnd);
        int monthApprovedCount = countApprovedInRange(allOTRequests, monthStart, monthEnd);

        double yearOT = calculateOTHoursInRange(allOTRequests, yearStart, yearEnd);
        int yearApprovedCount = countApprovedInRange(allOTRequests, yearStart, yearEnd);

        // Build OT Balance
        OTBalance balance = new OTBalance();
        balance.setCurrentWeekHours(weekOT);
        balance.setRegularHoursThisWeek((double) REGULAR_WEEKLY_HOURS); // Simplified, can be enhanced
        balance.setWeeklyLimit((double) WEEKLY_LIMIT);
        balance.setWeeklyApprovedCount(weekApprovedCount);

        // Set week date range
        java.time.format.DateTimeFormatter weekFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
        balance.setWeekStartDate(weekStart.format(weekFormatter));
        balance.setWeekEndDate(weekEnd.format(weekFormatter));

        // Set month info
        java.time.format.DateTimeFormatter monthFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.ENGLISH);
        balance.setMonthName(targetMonthDate.format(monthFormatter));
        balance.setMonthlyHours(monthOT);
        balance.setMonthlyLimit((double) MONTHLY_LIMIT);
        balance.setMonthlyApprovedCount(monthApprovedCount);

        // Set year info
        balance.setAnnualHours(yearOT);
        balance.setAnnualLimit((double) ANNUAL_LIMIT);
        balance.setAnnualApprovedCount(yearApprovedCount);

        logger.info(String.format("Calculated OT Balance in %dms: userId=%d, week=%.2f, month=%.2f, year=%.2f",
                   System.currentTimeMillis() - startTime, userId, weekOT, monthOT, yearOT));

        return balance;
    }

    /**
     * Calculate OT hours in a date range by filtering in memory.
     *
     * @param otRequests all OT requests
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return total OT hours
     */
    private double calculateOTHoursInRange(List<Request> otRequests, LocalDate startDate, LocalDate endDate) {
        return otRequests.stream()
            .map(Request::getOtDetail)
            .filter(detail -> detail != null && detail.getOtDate() != null)
            .filter(detail -> {
                LocalDate otDate = LocalDate.parse(detail.getOtDate());
                return !otDate.isBefore(startDate) && !otDate.isAfter(endDate);
            })
            .mapToDouble(OTRequestDetail::getOtHours)
            .sum();
    }

    /**
     * Count approved OT requests in a date range.
     *
     * @param otRequests all OT requests
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return count of approved requests
     */
    private int countApprovedInRange(List<Request> otRequests, LocalDate startDate, LocalDate endDate) {
        return (int) otRequests.stream()
            .filter(r -> "APPROVED".equals(r.getStatus()))
            .map(Request::getOtDetail)
            .filter(detail -> detail != null && detail.getOtDate() != null)
            .filter(detail -> {
                LocalDate otDate = LocalDate.parse(detail.getOtDate());
                return !otDate.isBefore(startDate) && !otDate.isAfter(endDate);
            })
            .count();
    }

    /**
     * Invalidate OT balance cache for a user.
     * Call this when user creates/updates/deletes OT request.
     *
     * IMPORTANT: This now invalidates application-level cache,
     * affecting ALL users/sessions immediately.
     *
     * @param session HTTP session (kept for API compatibility, but not used)
     * @param userId user ID
     */
    public void invalidate(HttpSession session, Long userId) {
        // Remove all cache entries for this user (all offsets) from application cache
        String prefix = "otBalance_" + userId + "_";
        APPLICATION_CACHE.keySet().removeIf(key -> key.startsWith(prefix));
        logger.info(String.format("Invalidated OT Balance cache for userId=%d (application-wide)", userId));
    }

    /**
     * Invalidate all OT balance cache.
     *
     * IMPORTANT: This clears the entire application-level cache.
     *
     * @param session HTTP session (kept for API compatibility, but not used)
     */
    public void invalidateAll(HttpSession session) {
        APPLICATION_CACHE.clear();
        logger.info("Invalidated all OT Balance cache (application-wide)");
    }
}
