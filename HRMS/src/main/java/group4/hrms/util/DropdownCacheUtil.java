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
 * application scope
 * Requirements: 9.5 - Cache Department and Position data to avoid repeated
 * queries
 */
public class DropdownCacheUtil {

    private static final Logger logger = LoggerFactory.getLogger(DropdownCacheUtil.class);

    // Cache keys
    private static final String DEPARTMENTS_CACHE_KEY = "cached_departments";
    private static final String POSITIONS_CACHE_KEY = "cached_positions";
    private static final String DEPARTMENTS_CACHE_TIME_KEY = "cached_departments_time";
    private static final String POSITIONS_CACHE_TIME_KEY = "cached_positions_time";

    // Cache expiration time in minutes (default: 60 minutes)
    private static final long CACHE_EXPIRATION_MINUTES = 60;

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
            logger.debug("Returning cached departments (size: {})", cachedDepartments.size());
            return cachedDepartments;
        }

        // Cache is expired or doesn't exist, refresh from database
        logger.info("Departments cache expired or not found, refreshing from database");
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
            logger.debug("Returning cached positions (size: {})", cachedPositions.size());
            return cachedPositions;
        }

        // Cache is expired or doesn't exist, refresh from database
        logger.info("Positions cache expired or not found, refreshing from database");
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
}
