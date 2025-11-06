package group4.hrms.cache;

import java.util.List;
import java.util.logging.Logger;

import group4.hrms.dto.LeaveBalance;
import group4.hrms.service.LeaveRequestService;
import jakarta.servlet.http.HttpSession;

/**
 * Session-scope cache for Leave Balances.
 * Caches calculated leave balances to avoid repeated database queries.
 *
 * OPTIMIZATION: Instead of querying database 42 times (7 leave types × 6 queries each),
 * we cache the result and reuse it.
 *
 * TTL: 5 minutes (balances change when leave requests are created/approved)
 */
public class LeaveBalanceCache {
    private static final Logger logger = Logger.getLogger(LeaveBalanceCache.class.getName());
    private static final int CACHE_TTL_MINUTES = 5;

    /**
     * Get leave balances from cache or calculate if not cached/expired.
     *
     * @param session HTTP session to store cache
     * @param userId user ID
     * @param year year to get balances for
     * @param service LeaveRequestService to calculate balances if not cached
     * @return list of leave balances
     */
    public List<LeaveBalance> getOrCalculate(
            HttpSession session, Long userId, int year, LeaveRequestService service) {

        String cacheKey = String.format("leaveBalances_%d_%d", userId, year);

        // Check cache
        @SuppressWarnings("unchecked")
        CachedData<List<LeaveBalance>> cached =
            (CachedData<List<LeaveBalance>>) session.getAttribute(cacheKey);

        if (cached != null && !cached.isExpired()) {
            logger.fine(String.format("Leave Balance cache HIT: userId=%d, year=%d, remaining=%ds",
                       userId, year, cached.getRemainingSeconds()));
            return cached.getData();
        }

        logger.info(String.format("Leave Balance cache MISS: userId=%d, year=%d, calculating...",
                   userId, year));

        long startTime = System.currentTimeMillis();

        // Calculate balances (this will do the 42 queries)
        List<LeaveBalance> balances = service.getAllLeaveBalances(userId, year);

        long duration = System.currentTimeMillis() - startTime;
        logger.info(String.format("Calculated %d leave balances in %dms: userId=%d, year=%d",
                   balances.size(), duration, userId, year));

        // Cache the result
        session.setAttribute(cacheKey, new CachedData<>(balances, CACHE_TTL_MINUTES));

        return balances;
    }

    /**
     * Invalidate leave balance cache for a user.
     * Call this when user creates/updates/deletes leave request.
     *
     * @param session HTTP session
     * @param userId user ID
     */
    public void invalidate(HttpSession session, Long userId) {
        // Remove all cache entries for this user (all years)
        session.getAttributeNames().asIterator().forEachRemaining(name -> {
            if (name.startsWith("leaveBalances_" + userId + "_")) {
                session.removeAttribute(name);
            }
        });
        logger.info(String.format("Invalidated Leave Balance cache for userId=%d", userId));
    }

    /**
     * Invalidate all leave balance cache in session.
     *
     * @param session HTTP session
     */
    public void invalidateAll(HttpSession session) {
        session.getAttributeNames().asIterator().forEachRemaining(name -> {
            if (name.startsWith("leaveBalances_")) {
                session.removeAttribute(name);
            }
        });
        logger.info("Invalidated all Leave Balance cache in session");
    }
}
