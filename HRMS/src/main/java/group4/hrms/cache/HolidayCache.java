package group4.hrms.cache;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import group4.hrms.dao.HolidayDao;
import group4.hrms.model.Holiday;

/**
 * Application-scope cache for holidays.
 * Holidays rarely change, so we cache them for 24 hours.
 * Thread-safe implementation using ConcurrentHashMap.
 */
public class HolidayCache {
    private static final Logger logger = Logger.getLogger(HolidayCache.class.getName());

    private static final Map<Integer, List<Holiday>> cache = new ConcurrentHashMap<>();
    private static final Map<Integer, LocalDateTime> cacheTime = new ConcurrentHashMap<>();
    private static final int CACHE_TTL_HOURS = 24; // Holidays ít thay đổi

    /**
     * Get holidays for a specific year.
     * Returns cached data if available and not expired.
     *
     * @param year the year to get holidays for
     * @param holidayDao DAO to load holidays if not cached
     * @return list of holidays for the year
     */
    public static List<Holiday> getHolidays(int year, HolidayDao holidayDao) {
        // Check cache
        if (cache.containsKey(year) && !isCacheExpired(year)) {
            logger.fine(String.format("Holiday cache HIT for year %d", year));
            return new ArrayList<>(cache.get(year)); // Return copy to prevent modification
        }

        logger.info(String.format("Holiday cache MISS for year %d, loading from database", year));

        // Load and cache
        try {
            List<Holiday> holidays = holidayDao.findByYear(year);
            cache.put(year, holidays);
            cacheTime.put(year, LocalDateTime.now());
            logger.info(String.format("Cached %d holidays for year %d", holidays.size(), year));
            return new ArrayList<>(holidays);
        } catch (Exception e) {
            logger.severe(String.format("Error loading holidays for year %d: %s", year, e.getMessage()));
            return Collections.emptyList();
        }
    }

    /**
     * Batch load holidays for multiple years in ONE query.
     * This is the main optimization - reduces N queries to 1 query.
     *
     * @param startYear start year (inclusive)
     * @param endYear end year (inclusive)
     * @param holidayDao DAO to load holidays
     * @return map of year to holidays
     */
    public static Map<Integer, List<Holiday>> getHolidaysForYears(
            int startYear, int endYear, HolidayDao holidayDao) {

        logger.fine(String.format("Loading holidays for years %d-%d", startYear, endYear));

        // Check which years need loading
        List<Integer> yearsToLoad = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            if (!cache.containsKey(year) || isCacheExpired(year)) {
                yearsToLoad.add(year);
            }
        }

        // Batch load missing years in ONE query
        if (!yearsToLoad.isEmpty()) {
            logger.info(String.format("Cache MISS for years %s, batch loading from database", yearsToLoad));

            try {
                List<Holiday> holidays = holidayDao.findByYearRange(
                    yearsToLoad.get(0),
                    yearsToLoad.get(yearsToLoad.size() - 1)
                );

                logger.info(String.format("Loaded %d holidays for years %d-%d in ONE query",
                           holidays.size(), yearsToLoad.get(0), yearsToLoad.get(yearsToLoad.size() - 1)));

                // Group by year and cache
                Map<Integer, List<Holiday>> grouped = holidays.stream()
                    .collect(Collectors.groupingBy(h -> h.getDateHoliday().getYear()));

                for (int year : yearsToLoad) {
                    List<Holiday> yearHolidays = grouped.getOrDefault(year, Collections.emptyList());
                    cache.put(year, yearHolidays);
                    cacheTime.put(year, LocalDateTime.now());
                    logger.fine(String.format("Cached %d holidays for year %d", yearHolidays.size(), year));
                }
            } catch (Exception e) {
                logger.severe(String.format("Error batch loading holidays: %s", e.getMessage()));
                // Cache empty lists for failed years to prevent repeated failures
                for (int year : yearsToLoad) {
                    cache.put(year, Collections.emptyList());
                    cacheTime.put(year, LocalDateTime.now());
                }
            }
        } else {
            logger.fine(String.format("Cache HIT for all years %d-%d", startYear, endYear));
        }

        // Return from cache
        Map<Integer, List<Holiday>> result = new HashMap<>();
        for (int year = startYear; year <= endYear; year++) {
            result.put(year, new ArrayList<>(cache.getOrDefault(year, Collections.emptyList())));
        }
        return result;
    }

    /**
     * Check if cache for a year has expired.
     *
     * @param year the year to check
     * @return true if expired or not cached, false otherwise
     */
    private static boolean isCacheExpired(int year) {
        LocalDateTime cached = cacheTime.get(year);
        if (cached == null) {
            return true;
        }
        return cached.plusHours(CACHE_TTL_HOURS).isBefore(LocalDateTime.now());
    }

    /**
     * Invalidate cache for a specific year.
     * Call this when holidays are added/updated/deleted.
     *
     * @param year the year to invalidate
     */
    public static void invalidate(int year) {
        cache.remove(year);
        cacheTime.remove(year);
        logger.info(String.format("Invalidated holiday cache for year %d", year));
    }

    /**
     * Invalidate all cached holidays.
     * Call this when doing bulk holiday updates.
     */
    public static void invalidateAll() {
        int size = cache.size();
        cache.clear();
        cacheTime.clear();
        logger.info(String.format("Invalidated all holiday cache (%d years)", size));
    }

    /**
     * Get cache statistics for monitoring.
     *
     * @return map of statistics
     */
    public static Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cachedYears", cache.size());
        stats.put("totalHolidays", cache.values().stream().mapToInt(List::size).sum());
        stats.put("oldestCache", cacheTime.values().stream()
            .min(LocalDateTime::compareTo)
            .orElse(null));
        return stats;
    }
}
