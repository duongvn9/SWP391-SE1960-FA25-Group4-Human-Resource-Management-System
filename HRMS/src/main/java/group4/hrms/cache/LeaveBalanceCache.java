package group4.hrms.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import group4.hrms.dto.LeaveBalance;
import group4.hrms.service.LeaveRequestService;
import jakarta.servlet.http.HttpSession;

/**
 * Application-scope cache for Leave Balances.
 * Caches calculated leave balances to avoid repeated database queries.
 *
 * IMPORTANT: Cache is now shared across all sessions (application-level)
 * so when one user's request is approved, all users see the updated balance immediately.
 *
 * OPTIMIZATION: Instead of querying database 42 times (7 leave types × 6 queries each),
 * we cache the result and reuse it.
 *
 * TTL: 5 minutes (balances change when leave requests are created/approved)
 */
public class LeaveBalanceCache {
    private static final Logger logger = Logger.getLogger(LeaveBalanceCache.class.getName());
    private static final int CACHE_TTL_MINUTES = 5;

    // Application-level cache shared across all sessions
    private static final Map<String, CachedData<List<LeaveBalance>>> APPLICATION_CACHE = new ConcurrentHashMap<>();

    /**
     * Get leave balances from cache or calculate if not cached/expired.
     *
     * IMPORTANT: Cache is now application-level (shared across all sessions)
     * instead of session-level, so all users see the same cached data.
     *
     * @param session HTTP session (kept for API compatibility, but not used for caching)
     * @param userId user ID
     * @param year year to get balances for
     * @param service LeaveRequestService to calculate balances if not cached
     * @return list of leave balances
     */
    public List<LeaveBalance> getOrCalculate(
            HttpSession session, Long userId, int year, LeaveRequestService service) {

        String cacheKey = String.format("leaveBalances_%d_%d", userId, year);

        // Check application-level cache
        CachedData<List<LeaveBalance>> cached = APPLICATION_CACHE.get(cacheKey);

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

        // Cache the result at application level
        APPLICATION_CACHE.put(cacheKey, new CachedData<>(balances, CACHE_TTL_MINUTES));

        return balances;
    }

    /**
     * Invalidate leave balance cache for a user.
     * Call this when user creates/updates/deletes leave request.
     *
     * IMPORTANT: This now invalidates application-level cache,
     * affecting ALL users/sessions immediately.
     *
     * @param session HTTP session (kept for API compatibility, but not used)
     * @param userId user ID
     */
    public void invalidate(HttpSession session, Long userId) {
        // Remove all cache entries for this user (all years) from application cache
        String prefix = "leaveBalances_" + userId + "_";
        APPLICATION_CACHE.keySet().removeIf(key -> key.startsWith(prefix));
        logger.info(String.format("Invalidated Leave Balance cache for userId=%d (application-wide)", userId));
    }

    /**
     * Invalidate all leave balance cache.
     *
     * IMPORTANT: This clears the entire application-level cache.
     *
     * @param session HTTP session (kept for API compatibility, but not used)
     */
    public void invalidateAll(HttpSession session) {
        APPLICATION_CACHE.clear();
        logger.info("Invalidated all Leave Balance cache (application-wide)");
    }
}
