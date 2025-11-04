package group4.hrms.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import group4.hrms.dao.SystemParametersDao;

/**
 * Utility class for loading and caching payroll configuration from SystemParameters
 * Provides methods to parse JSON configuration for working days, hours, policies, tax settings
 * Includes caching mechanism to avoid repeated database queries
 */
public class PayrollConfig {
    private static final Logger logger = LoggerFactory.getLogger(PayrollConfig.class);
    private static final String PAYROLL_NAMESPACE = "payroll";

    // Cache for configuration values
    private static final Map<String, Object> configCache = new ConcurrentHashMap<>();
    private static final long CACHE_REFRESH_INTERVAL_MINUTES = 30;

    // JSON mapper for parsing configuration
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // DAO for database access
    private static final SystemParametersDao systemParametersDao = new SystemParametersDao();

    // Scheduled executor for cache refresh
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "PayrollConfig-CacheRefresh");
        t.setDaemon(true);
        return t;
    });

    // Configuration keys
    public static final String WORKING_DAYS_PER_MONTH = "working_days_per_month";
    public static final String WORKING_HOURS_PER_DAY = "working_hours_per_day";
    public static final String LATE_POLICY = "late_policy";
    public static final String UNDER_HOURS_POLICY = "under_hours_policy";
    public static final String TAX_CONFIG = "tax_config";
    public static final String GENERATE_CUTOFF_DAYS = "generate_cutoff_days";
    public static final String TIMEZONE = "timezone";
    public static final String CURRENCY = "currency";

    static {
        // Initialize cache and start refresh scheduler
        refreshCache();
        scheduler.scheduleAtFixedRate(PayrollConfig::refreshCache,
                                    CACHE_REFRESH_INTERVAL_MINUTES,
                                    CACHE_REFRESH_INTERVAL_MINUTES,
                                    TimeUnit.MINUTES);

        // Shutdown hook to clean up scheduler
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));
    }

    /**
     * Get working days per month
     *
     * @return Working days per month (default: 22)
     */
    public static int getWorkingDaysPerMonth() {
        return getIntegerConfig(WORKING_DAYS_PER_MONTH, 22);
    }

    /**
     * Get working hours per day
     *
     * @return Working hours per day (default: 8)
     */
    public static int getWorkingHoursPerDay() {
        return getIntegerConfig(WORKING_HOURS_PER_DAY, 8);
    }

    /**
     * Get late policy configuration
     *
     * @return LatePolicyConfig object
     */
    public static LatePolicyConfig getLatePolicyConfig() {
        return getObjectConfig(LATE_POLICY, LatePolicyConfig.class, createDefaultLatePolicyConfig());
    }

    /**
     * Get under-hours policy configuration
     *
     * @return UnderHoursPolicyConfig object
     */
    public static UnderHoursPolicyConfig getUnderHoursPolicyConfig() {
        return getObjectConfig(UNDER_HOURS_POLICY, UnderHoursPolicyConfig.class, createDefaultUnderHoursPolicyConfig());
    }

    /**
     * Get tax configuration
     *
     * @return TaxConfig object
     */
    public static TaxConfig getTaxConfig() {
        return getObjectConfig(TAX_CONFIG, TaxConfig.class, createDefaultTaxConfig());
    }

    /**
     * Get generate cutoff days
     *
     * @return Generate cutoff days (default: 7)
     */
    public static int getGenerateCutoffDays() {
        return getIntegerConfig(GENERATE_CUTOFF_DAYS, 7);
    }

    /**
     * Get timezone
     *
     * @return Timezone string (default: "Asia/Ho_Chi_Minh")
     */
    public static String getTimezone() {
        return getStringConfig(TIMEZONE, "Asia/Ho_Chi_Minh");
    }

    /**
     * Get currency
     *
     * @return Currency code (default: "VND")
     */
    public static String getCurrency() {
        return getStringConfig(CURRENCY, "VND");
    }

    /**
     * Refresh configuration cache from database
     */
    public static void refreshCache() {
        try {
            logger.debug("Refreshing payroll configuration cache");
            Map<String, String> parameters = systemParametersDao.getParametersByNamespace(PAYROLL_NAMESPACE);

            // Clear existing cache
            configCache.clear();

            // Load new values into cache
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                configCache.put(entry.getKey(), entry.getValue());
            }

            logger.info("Refreshed payroll configuration cache with {} parameters", parameters.size());

        } catch (Exception e) {
            logger.error("Error refreshing payroll configuration cache: {}", e.getMessage(), e);
        }
    }

    /**
     * Clear configuration cache (useful for testing)
     */
    public static void clearCache() {
        configCache.clear();
        logger.debug("Cleared payroll configuration cache");
    }

    // Private helper methods

    private static String getStringConfig(String key, String defaultValue) {
        try {
            Object cached = configCache.get(key);
            if (cached != null) {
                String value = cached.toString();
                // Remove quotes if present (JSON string values are quoted)
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                return value;
            }

            // Fallback to database if not in cache
            return systemParametersDao.getParameter(PAYROLL_NAMESPACE, key)
                    .map(value -> {
                        // Remove quotes if present
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            return value.substring(1, value.length() - 1);
                        }
                        return value;
                    })
                    .orElse(defaultValue);

        } catch (Exception e) {
            logger.error("Error getting string config {}: {}", key, e.getMessage(), e);
            return defaultValue;
        }
    }

    private static int getIntegerConfig(String key, int defaultValue) {
        try {
            Object cached = configCache.get(key);
            if (cached != null) {
                return Integer.parseInt(cached.toString());
            }

            // Fallback to database if not in cache
            return systemParametersDao.getParameter(PAYROLL_NAMESPACE, key)
                    .map(Integer::parseInt)
                    .orElse(defaultValue);

        } catch (Exception e) {
            logger.error("Error getting integer config {}: {}", key, e.getMessage(), e);
            return defaultValue;
        }
    }

    private static <T> T getObjectConfig(String key, Class<T> clazz, T defaultValue) {
        try {
            Object cached = configCache.get(key);
            if (cached != null) {
                return objectMapper.readValue(cached.toString(), clazz);
            }

            // Fallback to database if not in cache
            return systemParametersDao.getParameter(PAYROLL_NAMESPACE, key)
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, clazz);
                        } catch (Exception e) {
                            logger.error("Error parsing JSON config {}: {}", key, e.getMessage(), e);
                            return defaultValue;
                        }
                    })
                    .orElse(defaultValue);

        } catch (Exception e) {
            logger.error("Error getting object config {}: {}", key, e.getMessage(), e);
            return defaultValue;
        }
    }

    // Default configuration creators

    private static LatePolicyConfig createDefaultLatePolicyConfig() {
        LatePolicyConfig config = new LatePolicyConfig();
        config.setMode("ladder");
        config.setGraceMinutesPerEvent(15);

        // Create default ladder bands
        java.util.List<LatePolicyConfig.LadderBand> bands = new java.util.ArrayList<>();
        bands.add(new LatePolicyConfig.LadderBand(0, 14, 0.0, null));
        bands.add(new LatePolicyConfig.LadderBand(15, 59, 0.5, null));
        bands.add(new LatePolicyConfig.LadderBand(60, 119, 1.0, null));
        bands.add(new LatePolicyConfig.LadderBand(120, 999, null, "ceil(minutes/60)"));

        config.setLadderBands(bands);
        return config;
    }

    private static UnderHoursPolicyConfig createDefaultUnderHoursPolicyConfig() {
        return new UnderHoursPolicyConfig(true, false);
    }

    private static TaxConfig createDefaultTaxConfig() {
        return new TaxConfig(true, "flat", 0.05);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private PayrollConfig() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}