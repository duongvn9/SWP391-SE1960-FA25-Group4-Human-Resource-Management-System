package group4.hrms.cache;

import java.util.List;
import java.util.logging.Logger;

import group4.hrms.dao.UserDao;
import group4.hrms.model.User;
import jakarta.servlet.http.HttpSession;

/**
 * Session-scope cache for subordinates list.
 * Caches subordinates to avoid repeated database queries.
 * TTL: 10 minutes (subordinates don't change frequently)
 */
public class SubordinateCache {
    private static final Logger logger = Logger.getLogger(SubordinateCache.class.getName());
    private static final int CACHE_TTL_MINUTES = 10;

    /**
     * Get subordinates from cache or load if not cached/expired.
     *
     * @param session HTTP session to store cache
     * @param managerId manager user ID
     * @param userDao DAO to load subordinates if not cached
     * @return list of subordinates
     */
    public List<User> getOrLoad(HttpSession session, Long managerId, UserDao userDao) {
        String cacheKey = "subordinates_" + managerId;

        // Check cache
        @SuppressWarnings("unchecked")
        CachedData<List<User>> cached = (CachedData<List<User>>) session.getAttribute(cacheKey);
        if (cached != null && !cached.isExpired()) {
            logger.fine(String.format("Subordinate cache HIT: managerId=%d, count=%d, remaining=%ds",
                       managerId, cached.getData().size(), cached.getRemainingSeconds()));
            return cached.getData();
        }

        logger.info(String.format("Subordinate cache MISS: managerId=%d, loading from database", managerId));

        // Load and cache
        try {
            List<User> subordinates = userDao.getSubordinates(managerId);
            session.setAttribute(cacheKey, new CachedData<>(subordinates, CACHE_TTL_MINUTES));

            logger.info(String.format("Cached %d subordinates for managerId=%d",
                       subordinates.size(), managerId));

            return subordinates;
        } catch (Exception e) {
            logger.severe(String.format("Error loading subordinates for managerId=%d: %s",
                         managerId, e.getMessage()));
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Invalidate subordinate cache for a manager.
     * Call this when organization structure changes.
     *
     * @param session HTTP session
     * @param managerId manager user ID
     */
    public void invalidate(HttpSession session, Long managerId) {
        String cacheKey = "subordinates_" + managerId;
        session.removeAttribute(cacheKey);
        logger.info(String.format("Invalidated subordinate cache for managerId=%d", managerId));
    }

    /**
     * Invalidate all subordinate cache in session.
     *
     * @param session HTTP session
     */
    public void invalidateAll(HttpSession session) {
        session.getAttributeNames().asIterator().forEachRemaining(name -> {
            if (name.startsWith("subordinates_")) {
                session.removeAttribute(name);
            }
        });
        logger.info("Invalidated all subordinate cache in session");
    }
}
