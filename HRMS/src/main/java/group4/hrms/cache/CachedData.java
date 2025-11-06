package group4.hrms.cache;

import java.time.LocalDateTime;

/**
 * Generic wrapper for cached data with expiration time.
 * Used by various cache implementations to store data with TTL.
 *
 * @param <T> the type of data being cached
 */
public class CachedData<T> {
    private final T data;
    private final LocalDateTime expiresAt;

    /**
     * Create cached data with TTL in minutes.
     *
     * @param data the data to cache
     * @param ttlMinutes time-to-live in minutes
     */
    public CachedData(T data, int ttlMinutes) {
        this.data = data;
        this.expiresAt = LocalDateTime.now().plusMinutes(ttlMinutes);
    }

    /**
     * Create cached data with custom expiration time.
     *
     * @param data the data to cache
     * @param expiresAt the expiration timestamp
     */
    public CachedData(T data, LocalDateTime expiresAt) {
        this.data = data;
        this.expiresAt = expiresAt;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Check if cached data has expired.
     *
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Get remaining TTL in seconds.
     *
     * @return remaining seconds until expiration, 0 if expired
     */
    public long getRemainingSeconds() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
    }
}
