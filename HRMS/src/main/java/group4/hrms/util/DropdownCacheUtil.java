package group4.hrms.util;

import group4.hrms.dao.DepartmentDao;
import group4.hrms.dao.PositionDao;
import group4.hrms.model.Department;
import group4.hrms.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Utility class for caching dropdown data (departments and positions) in
 * application scope with 5-minute expiration and cache statistics tracking
 * Requirements: 8.5 - Cache Department and Position data with 5-minute
 * expiration
 */
public class DropdownCacheUtil {

    private static final Logger logger = LoggerFactory.getLogger(DropdownCacheUtil.class);

    // Cache keys
    private static final String DEPARTMENTS_CACHE_KEY = "cached_departments";
    private static final String POSITIONS_CACHE_KEY = "cached_positions";
    private static final String DEPARTMENTS_CACHE_TIME_KEY = "cached_departments_time";
    private static final String POSITIONS_CACHE_TIME_KEY = "cached_positions_time";

    // Cache statistics keys
    private static final String DEPARTMENTS_HIT_COUNT_KEY = "departments_cache_hits";
    private static final String POSITIONS_HIT_COUNT_KEY = "positions_cache_hits";
    private static final String DEPARTMENTS_MISS_COUNT_KEY = "departments_cache_misses";
    private static final String POSITIONS_MISS_COUNT_KEY = "positions_cache_misses";

    // Cache expiration time in minutes (5 minutes as per requirements 8.5)
    private static final long CACHE_EXPIRATION_MINUTES = 5;

    /**
     * Get cached departments list (convenience method)
     * Alias for getCachedDepartments() for backward compatibility
     *
     * @param context ServletContext for application scope
     * @return List of departments
     */
    public static List<Department> getDepartments(ServletContext context) {
        return getCachedDepartments(context);
    }

    /**
     * Get cached positions list (convenience method)
     * Alias for getCachedPositions() for backward compatibility
     *
     * @param context ServletContext for application scope
     * @return List of positions
     */
    public static List<Position> getPositions(ServletContext context) {
        return getCachedPositions(context);
    }

    /**
     * Get cached departments list from application scope
     * If cache is expired or doesn't exist, refresh from database
     *
     * @param context ServletContext for application scope
     * @return List of departments
     */
    @SuppressWarnings("unchecked")
    public static List<Department> getCachedDepartments(ServletContext context) {
        logger.debug("Getting cached departments");

        // Check if cache exists and is valid
        List<Department> cachedDepartments = (List<Department>) context.getAttribute(DEPARTMENTS_CACHE_KEY);
        LocalDateTime cacheTime = (LocalDateTime) context.getAttribute(DEPARTMENTS_CACHE_TIME_KEY);

        if (cachedDepartments != null && cacheTime != null && !isCacheExpired(cacheTime)) {
            // Cache hit - increment counter
            incrementCacheHits(context, DEPARTMENTS_HIT_COUNT_KEY);
            logger.debug("Cache HIT: Returning cached departments (size: {})", cachedDepartments.size());
            return cachedDepartments;
        }

        // Cache miss - increment counter and refresh from database
        incrementCacheMisses(context, DEPARTMENTS_MISS_COUNT_KEY);
        logger.info("Cache MISS: Departments cache expired or not found, refreshing from database");
        return refreshDepartmentsCache(context);
    }

    /**
     * Get cached positions list from application scope
     * If cache is expired or doesn't exist, refresh from database
     *
     * @param context ServletContext for application scope
     * @return List of positions
     */
    @SuppressWarnings("unchecked")
    public static List<Position> getCachedPositions(ServletContext context) {
        logger.debug("Getting cached positions");

        // Check if cache exists and is valid
        List<Position> cachedPositions = (List<Position>) context.getAttribute(POSITIONS_CACHE_KEY);
        LocalDateTime cacheTime = (LocalDateTime) context.getAttribute(POSITIONS_CACHE_TIME_KEY);

        if (cachedPositions != null && cacheTime != null && !isCacheExpired(cacheTime)) {
            // Cache hit - increment counter
            incrementCacheHits(context, POSITIONS_HIT_COUNT_KEY);
            logger.debug("Cache HIT: Returning cached positions (size: {})", cachedPositions.size());
            return cachedPositions;
        }

        // Cache miss - increment counter and refresh from database
        incrementCacheMisses(context, POSITIONS_MISS_COUNT_KEY);
        logger.info("Cache MISS: Positions cache expired or not found, refreshing from database");
        return refreshPositionsCache(context);
    }

    /**
     * Refresh departments cache from database
     *
     * @param context ServletContext for application scope
     * @return List of departments
     */
    public static List<Department> refreshDepartmentsCache(ServletContext context) {
        logger.info("Refreshing departments cache from database");

        try {
            DepartmentDao departmentDao = new DepartmentDao();
            List<Department> departments = departmentDao.findAll();

            // Store in application scope
            context.setAttribute(DEPARTMENTS_CACHE_KEY, departments);
            context.setAttribute(DEPARTMENTS_CACHE_TIME_KEY, LocalDateTime.now());

            logger.info("Departments cache refreshed successfully (size: {})", departments.size());
            return departments;

        } catch (Exception e) {
            logger.error("Error refreshing departments cache: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refresh departments cache", e);
        }
    }

    /**
     * Refresh positions cache from database
     *
     * @param context ServletContext for application scope
     * @return List of positions
     */
    public static List<Position> refreshPositionsCache(ServletContext context) {
        logger.info("Refreshing positions cache from database");

        try {
            PositionDao positionDao = new PositionDao();
            List<Position> positions = positionDao.findAll();

            // Store in application scope
            context.setAttribute(POSITIONS_CACHE_KEY, positions);
            context.setAttribute(POSITIONS_CACHE_TIME_KEY, LocalDateTime.now());

            logger.info("Positions cache refreshed successfully (size: {})", positions.size());
            return positions;

        } catch (Exception e) {
            logger.error("Error refreshing positions cache: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refresh positions cache", e);
        }
    }

    /**
     * Clear all dropdown caches
     *
     * @param context ServletContext for application scope
     */
    public static void clearAllCaches(ServletContext context) {
        logger.info("Clearing all dropdown caches");

        context.removeAttribute(DEPARTMENTS_CACHE_KEY);
        context.removeAttribute(DEPARTMENTS_CACHE_TIME_KEY);
        context.removeAttribute(POSITIONS_CACHE_KEY);
        context.removeAttribute(POSITIONS_CACHE_TIME_KEY);

        logger.info("All dropdown caches cleared");
    }

    /**
     * Clear departments cache only
     *
     * @param context ServletContext for application scope
     */
    public static void clearDepartmentsCache(ServletContext context) {
        logger.info("Clearing departments cache");

        context.removeAttribute(DEPARTMENTS_CACHE_KEY);
        context.removeAttribute(DEPARTMENTS_CACHE_TIME_KEY);

        logger.info("Departments cache cleared");
    }

    /**
     * Clear positions cache only
     *
     * @param context ServletContext for application scope
     */
    public static void clearPositionsCache(ServletContext context) {
        logger.info("Clearing positions cache");

        context.removeAttribute(POSITIONS_CACHE_KEY);
        context.removeAttribute(POSITIONS_CACHE_TIME_KEY);

        logger.info("Positions cache cleared");
    }

    /**
     * Check if cache is expired based on expiration time
     *
     * @param cacheTime Time when cache was created
     * @return true if cache is expired, false otherwise
     */
    private static boolean isCacheExpired(LocalDateTime cacheTime) {
        LocalDateTime expirationTime = cacheTime.plusMinutes(CACHE_EXPIRATION_MINUTES);
        boolean expired = LocalDateTime.now().isAfter(expirationTime);

        if (expired) {
            logger.debug("Cache expired (cache time: {}, expiration: {})", cacheTime, expirationTime);
        }

        return expired;
    }

    /**
     * Get cache expiration time in minutes
     *
     * @return Cache expiration time in minutes
     */
    public static long getCacheExpirationMinutes() {
        return CACHE_EXPIRATION_MINUTES;
    }

    /**
     * Force invalidate all caches regardless of expiration time
     * This method clears all cached data and forces fresh database queries
     *
     * @param context ServletContext for application scope
     */
    public static void invalidateAllCaches(ServletContext context) {
        logger.info("Force invalidating all dropdown caches");
        clearAllCaches(context);

        // Also clear statistics
        context.removeAttribute(DEPARTMENTS_HIT_COUNT_KEY);
        context.removeAttribute(POSITIONS_HIT_COUNT_KEY);
        context.removeAttribute(DEPARTMENTS_MISS_COUNT_KEY);
        context.removeAttribute(POSITIONS_MISS_COUNT_KEY);

        logger.info("All dropdown caches and statistics invalidated");
    }

    /**
     * Get cache statistics for departments
     *
     * @param context ServletContext for application scope
     * @return String with cache hit/miss statistics
     */
    public static String getDepartmentsCacheStats(ServletContext context) {
        Integer hits = (Integer) context.getAttribute(DEPARTMENTS_HIT_COUNT_KEY);
        Integer misses = (Integer) context.getAttribute(DEPARTMENTS_MISS_COUNT_KEY);

        hits = (hits != null) ? hits : 0;
        misses = (misses != null) ? misses : 0;

        int total = hits + misses;
        double hitRate = total > 0 ? (double) hits / total * 100 : 0;

        return String.format("Departments Cache - Hits: %d, Misses: %d, Hit Rate: %.1f%%",
                hits, misses, hitRate);
    }

    /**
     * Get cache statistics for positions
     *
     * @param context ServletContext for application scope
     * @return String with cache hit/miss statistics
     */
    public static String getPositionsCacheStats(ServletContext context) {
        Integer hits = (Integer) context.getAttribute(POSITIONS_HIT_COUNT_KEY);
        Integer misses = (Integer) context.getAttribute(POSITIONS_MISS_COUNT_KEY);

        hits = (hits != null) ? hits : 0;
        misses = (misses != null) ? misses : 0;

        int total = hits + misses;
        double hitRate = total > 0 ? (double) hits / total * 100 : 0;

        return String.format("Positions Cache - Hits: %d, Misses: %d, Hit Rate: %.1f%%",
                hits, misses, hitRate);
    }

    /**
     * Check if departments cache is currently valid (not expired)
     *
     * @param context ServletContext for application scope
     * @return true if cache exists and is not expired, false otherwise
     */
    public static boolean isDepartmentsCacheValid(ServletContext context) {
        List<Department> cachedDepartments = (List<Department>) context.getAttribute(DEPARTMENTS_CACHE_KEY);
        LocalDateTime cacheTime = (LocalDateTime) context.getAttribute(DEPARTMENTS_CACHE_TIME_KEY);

        return cachedDepartments != null && cacheTime != null && !isCacheExpired(cacheTime);
    }

    /**
     * Check if positions cache is currently valid (not expired)
     *
     * @param context ServletContext for application scope
     * @return true if cache exists and is not expired, false otherwise
     */
    public static boolean isPositionsCacheValid(ServletContext context) {
        List<Position> cachedPositions = (List<Position>) context.getAttribute(POSITIONS_CACHE_KEY);
        LocalDateTime cacheTime = (LocalDateTime) context.getAttribute(POSITIONS_CACHE_TIME_KEY);

        return cachedPositions != null && cacheTime != null && !isCacheExpired(cacheTime);
    }

    /**
     * Increment cache hit counter
     *
     * @param context ServletContext for application scope
     * @param key     Cache hit counter key
     */
    private static void incrementCacheHits(ServletContext context, String key) {
        Integer currentCount = (Integer) context.getAttribute(key);
        currentCount = (currentCount != null) ? currentCount + 1 : 1;
        context.setAttribute(key, currentCount);
    }

    /**
     * Increment cache miss counter
     *
     * @param context ServletContext for application scope
     * @param key     Cache miss counter key
     */
    private static void incrementCacheMisses(ServletContext context, String key) {
        Integer currentCount = (Integer) context.getAttribute(key);
        currentCount = (currentCount != null) ? currentCount + 1 : 1;
        context.setAttribute(key, currentCount);
    }
}
